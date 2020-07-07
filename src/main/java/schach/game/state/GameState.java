package schach.game.state;

import java.util.Map.Entry;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

import schach.common.Color;
import schach.common.Position;
import schach.game.accumulators.filters.LosingMoveFilter;
import schach.game.accumulators.filters.FulfillmentFilter;
import schach.game.accumulators.CollectionAccumulator;
import schach.game.accumulators.MoveAccumulator;
import schach.game.accumulators.AcceptAccumulator;
import schach.game.moves.Move;
import schach.game.moves.Movement;
import schach.game.pieces.Piece;

/**
 * The game state holds the state of the game (including a board instance) at
 * the beginning of a turn. The physical positions of the pieces are stored in a
 * board and not directly in the game state. This class represents the game as a
 * concept and less the physical state.
 */
public class GameState {
  private MoveJournal journal = new MoveJournal(this);
  private Board board = new Board(this);
  private GameStatus status = GameStatus.NONE;
  private Color activeColor = Color.WHITE;

  /**
   * Move index that is incremented like the move index but reset when a pawn
   * moves or a piece is captured. This is used for determining when the 75 or
   * 50-move-rule comes into effect and causes a draw. A draw is only enforced if
   * the 75 move threshold is reached.
   * 
   * We're using a stack here to make sure we can revert changes to this value.
   */
  private Deque<Integer> drawMoveIndexes = new LinkedList<>();

  /**
   * Constructs a game state. Some parts of the state that don't require a complex
   * initialization are already initialized.
   */
  public GameState() {
    drawMoveIndexes.push(0);
  }

  public Color getActiveColor() {
    return activeColor;
  }

  public Board getBoard() {
    return board;
  }

  public MoveJournal getJournal() {
    return journal;
  }

  /**
   * Returns the latest draw move index. This is the move index of the last time a
   * move happened that resets the n-move rules.
   * 
   * @return Current draw move index
   */
  private int getDrawMoveIndex() {
    return drawMoveIndexes.peek();
  }

  /**
   * Returns the piece at the given position.
   * 
   * @param position Position of the square to query
   * @return Piece found at the given position
   */
  public Piece getPieceAt(Position position) {
    return board.getPieceAt(position);
  }

  /**
   * Returns the piece at the given position using a coordinate pair.
   * 
   * @param x X coordinate of the square to query
   * @param y Y coordinate of the square to query
   * @return Piece found at the given position
   */
  public Piece getPieceAt(int x, int y) {
    return board.getPieceAt(x, y);
  }

  /**
   * Called by the board to notify the game state that a piece has been captured
   * and the draw move index needs to be reset.
   */
  public void notifyCapture() {
    // set the top of the stack to 0 if not set like this already
    if (drawMoveIndexes.peek() != 0) {
      drawMoveIndexes.pop();
      drawMoveIndexes.push(0);
    }
  }

  /**
   * Checks if the current game state allows continuing to play the game. If true,
   * no further moves may be applied.
   * 
   * @return If the game has stopped
   */
  public boolean gameIsStopped() {
    return getStatus().gameIsStopped();
  }

  /**
   * Calculates the current game status. This is not done automatically when
   * applying a move since it involves fetching all legal moves. (which is
   * recursive)
   * 
   * @return Computed or cached game status
   */
  public GameStatus getStatus() {
    // return status if already calculated
    if (status != null) {
      return status;
    }

    // check if the current player can move and if the player is in check
    boolean inCheck = board.kingAttacked(activeColor);
    AcceptAccumulator acceptAccumulator = new AcceptAccumulator();
    accumulateAllLegalMoves(acceptAccumulator);
    boolean canMove = acceptAccumulator.conditionIsSatisfied();

    // determine the game status using the combination of these states
    status = inCheck ? canMove ? GameStatus.IN_CHECK : GameStatus.IN_CHECKMATE
        : canMove ? GameStatus.NONE : GameStatus.DRAW;

    // apply the 75 move rule (a move is two steps of the move index)
    // also check if there is too little material for a checkmate left on the board
    if (getDrawMoveIndex() == 150 && !status.gameIsStopped() || board.hasInsufficientMaterial()) {
      status = GameStatus.DRAW;
    }

    return status;
  }

  /**
   * This method is only meant to be called by the move journal. Applies a legal
   * move to the game state. This is a mutation of the game state and the state is
   * different afterwards.
   * 
   * This method does *not* check that the move is actually legal. The legality of
   * the move has to be ensured before applying it. Applying an illegal move might
   * throw an exception. (An IllegalStateException or IllegalArgumentException is
   * most likely)
   * 
   * @param move Move to apply to this game state
   */
  void applyMove(Move move) {
    // advance the player state and apply the move
    drawMoveIndexes.push(getDrawMoveIndex() + 1);
    move.setMoveIndex(journal.getNextMoveIndex());
    move.applyTo(board);

    // activate the opposing color to prepare for the turn of the other player
    activeColor = activeColor.getOpposing();

    // the game status needs to be reset since the state has changed
    status = null;
  }

  /**
   * This method is only meant to be called by the move journal. Reverses the
   * given move. The game state is identical to the game state before applying
   * this move.
   * 
   * @param move Move to reverse on this game state
   */
  void reverseMove(Move move) {
    // switch the color again and also reset the status
    activeColor = activeColor.getOpposing();
    status = null;

    // reverse the last applied move and remove the move index from it
    move.reverseOn(board);
    drawMoveIndexes.pop();
  }

  /**
   * Shortcut for applying a given move with history support. This uses the
   * journal to keep track of the history.
   * 
   * @param move Move to do with journal tracking
   */
  public void doMove(Move move) {
    journal.doMove(move);
  }

  /**
   * Runs the given function after applying the given move and then reverts the
   * move after the function is done. This allows the game state to be cleanly
   * changed and then reverted for testing a move without committing to using it.
   * 
   * @param <ReturnType> Type of the object supplied by the passed function
   * @param move         Move to apply and then reverse
   * @param duringMove   Function to run after the move has been applied. The
   *                     value returned from this is passed through.
   * @return Value produced by duringMove
   */
  public <ReturnType> ReturnType runWithMove(Move move, Supplier<ReturnType> duringMove) {
    applyMove(move);
    ReturnType result = duringMove.get();
    reverseMove(move);
    return result;
  }

  /**
   * Checks if a given movement constitutes a valid move. If it does, the proper
   * full-fledged move is constructed and returned. This method checks if a move
   * is valid by generating possible moves until it finds a move that fulfills the
   * movement we're trying to validate.
   * 
   * @param movement Movement to validate as a legal move
   * @return Proper newly constructed move that fulfills the given movement, null
   *         if no fulfilling move exists and the movement is invalid
   */
  public Move validateMove(Movement movement) {
    // construct a chain of the fulfillment filter for validation, then the loosing
    // move filter for making sure the found move is legal and the accept
    // accumulator that actually takes the output of the filters and saves it
    AcceptAccumulator acceptor = new AcceptAccumulator();
    accumulateAllMoves(acceptor.withFilters(new FulfillmentFilter(movement), new LosingMoveFilter()));
    return acceptor.getSatisfyingMove();
  }

  /**
   * Accumulate all possible moves for the given color to the given accumulator.
   * This can be used for the opposing color to detect check for the current
   * color.
   * 
   * @param <AccumulatorType> Type of the given and returned accumulator
   * @param accumulator       Accumulator to add the moves to
   * @param forColor          The color to generate moves for
   * @return Given move accumulator
   */
  public <AccumulatorType extends MoveAccumulator> AccumulatorType accumulateAllMoves(AccumulatorType accumulator,
      Color forColor) {
    accumulator.setGameState(this);

    // we need to get the positions all at once to avoid a concurrent modification
    for (Entry<Integer, Piece> entry : board.getPieces().entrySet().stream().collect(Collectors.toList())) {
      Piece piece = entry.getValue();
      // stop when move accumulation is stopped by the accumulator
      if (piece.getColor() == forColor
          && !piece.accumulateMoves(accumulator, Position.fromBoardIndex(entry.getKey()))) {
        return accumulator;
      }
    }
    return accumulator;
  }

  /**
   * Accumulate all possible moves for all pieces that belong to the currently
   * active color. This is the list of all currently legal moves.
   * 
   * @param <AccumulatorType> Type of the given and returned accumulator
   * @param accumulator       Accumulator to add moves to
   * @return Given move accumulator
   */
  public <AccumulatorType extends MoveAccumulator> AccumulatorType accumulateAllMoves(AccumulatorType accumulator) {
    return accumulateAllMoves(accumulator, activeColor);
  }

  /**
   * Finds and accumulates moves that the piece at the given position can make.
   * 
   * @param <AccumulatorType> Type of the given and returned accumulator
   * @param accumulator       Move accumulator to add the moves to
   * @param position          Position of the piece to generate moves for
   * @return Given move accumulator
   */
  public <AccumulatorType extends MoveAccumulator> AccumulatorType accumulateMovesFor(AccumulatorType accumulator,
      Position position) {
    accumulator.setGameState(this);
    Piece piece = board.getPieceAt(position);
    if (piece != null) {
      piece.accumulateMoves(accumulator, position);
    }
    return accumulator;
  }

  /**
   * Accumulates legal moves (non-loosing) into the given accumulator.
   * 
   * @param <AccumulatorType> Type of the given and returned accumulator
   * @param accumulator       Inner accumulator to add validated moves to
   * @return Given accumulator
   */
  public <AccumulatorType extends MoveAccumulator> AccumulatorType accumulateAllLegalMoves(
      AccumulatorType accumulator) {
    accumulateAllMoves(accumulator.withFilters(new LosingMoveFilter()));
    return accumulator;
  }

  /**
   * Accumulates legal moves (non-loosing) for a specific position into the given
   * accumulator.
   * 
   * @param <AccumulatorType> Type of the given and returned accumulator
   * @param accumulator       Inner accumulator to add validated moves to
   * @param position          Position to accumulate legal moves for
   * @return Given accumulator
   */
  public <AccumulatorType extends MoveAccumulator> AccumulatorType accumulateLegalMovesFor(AccumulatorType accumulator,
      Position position) {
    accumulateMovesFor(accumulator.withFilters(new LosingMoveFilter()), position);
    return accumulator;
  }

  /**
   * Returns a linked list of all non losing (not moving the king into check)
   * moves possible in this game state.
   * 
   * @return LinkedList collection accumulator with all possible moves
   */
  public List<Move> getAllLegalMoves() {
    return accumulateAllLegalMoves(CollectionAccumulator.withLinkedList()).getMoves();
  }

  /**
   * Accumulates all possible moves for the piece on a given square into a linked
   * list accumulator for convenience.
   * 
   * @param position position of the piece to find moves for
   * @return LinkedList collection accumulator with moves for this piece
   */
  public List<Move> getLegalMovesFor(Position position) {
    return accumulateLegalMovesFor(CollectionAccumulator.withLinkedList(), position).getMoves();
  }
}
