package schach.game.state;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import schach.common.Color;
import schach.common.Position;
import schach.game.moves.Movement;
import schach.game.pieces.Piece;
import schach.game.pieces.PieceComparator;
import schach.game.pieces.PieceType;
import schach.game.pieces.HistoryPiece;
import schach.game.pieces.BishopPiece;
import schach.game.pieces.KingPiece;
import schach.game.pieces.KnightPiece;
import schach.game.pieces.PawnPiece;
import schach.game.pieces.QueenPiece;
import schach.game.pieces.RookPiece;

/**
 * Models the pieces on the board and the positions of the pieces. This also
 * stores the captured pieces. The board does not know about the state of the
 * game or what moves can be made.
 */
public class Board {
  /**
   * How many pieces there are expected to be. This is used for initializing
   * hashmaps so it being accurate only affects performance.
   */
  private static final int INITIAL_PIECE_AMOUNT = 32;

  private final GameState gameState;

  private final Map<Integer, Piece> pieces = new HashMap<>((int) (INITIAL_PIECE_AMOUNT / 0.75 + 1));
  private final Deque<Piece> capturedPieces = new LinkedList<>();

  /**
   * This stores the move history for the history pieces individually so their
   * move indexes can be looked up when needed. Roughly estimate the number of
   * history pieces initially on the board.
   */
  private final Map<HistoryPiece, Deque<Movement>> pieceMoveHistory = new HashMap<>(
      (int) ((INITIAL_PIECE_AMOUNT / 2 + 6) / 0.75 + 1));

  private final Map<Color, Position> kingPositions = new EnumMap<>(Color.class);

  /**
   * Constructs a new board with the starting positions. Simple initialization is
   * done beforehand.
   * 
   * @param gameState Game state to set as the parent
   */
  public Board(GameState gameState) {
    this.gameState = gameState;

    placeNewPiece(0, 0, new RookPiece(Color.BLACK));
    placeNewPiece(1, 0, new KnightPiece(Color.BLACK));
    placeNewPiece(2, 0, new BishopPiece(Color.BLACK));
    placeNewPiece(3, 0, new QueenPiece(Color.BLACK));
    placeNewPiece(4, 0, new KingPiece(Color.BLACK));
    placeNewPiece(5, 0, new BishopPiece(Color.BLACK));
    placeNewPiece(6, 0, new KnightPiece(Color.BLACK));
    placeNewPiece(7, 0, new RookPiece(Color.BLACK));

    placeNewPiece(0, 7, new RookPiece(Color.WHITE));
    placeNewPiece(1, 7, new KnightPiece(Color.WHITE));
    placeNewPiece(2, 7, new BishopPiece(Color.WHITE));
    placeNewPiece(3, 7, new QueenPiece(Color.WHITE));
    placeNewPiece(4, 7, new KingPiece(Color.WHITE));
    placeNewPiece(5, 7, new BishopPiece(Color.WHITE));
    placeNewPiece(6, 7, new KnightPiece(Color.WHITE));
    placeNewPiece(7, 7, new RookPiece(Color.WHITE));

    for (int x = 0; x < 8; x++) {
      placeNewPiece(x, 1, new PawnPiece(Color.BLACK));
      placeNewPiece(x, 6, new PawnPiece(Color.WHITE));
    }
  }

  /**
   * Returns an unmodifiable map view of the pieces on this board for iterating
   * over them but not modification.
   * 
   * @return Unmodifiable map of the contained pieces
   */
  public Map<Integer, Piece> getPieces() {
    return Collections.unmodifiableMap(pieces);
  }

  /**
   * Checks if the piece at the given position is being attacked. Which pieces do
   * the attacking depends on the color of the piece at the position.
   * 
   * @param position Position of the piece to check
   * @return True if the piece at the given position is being attacked, false if
   *         not or there is no piece there
   */
  public boolean isAttackedAt(Position position) {
    // get the piece at the position and make sure it's not null
    Piece target = getPieceAt(position);
    if (target == null) {
      return false;
    }

    return target.isAttackedLocally(gameState, position);
  }

  /**
   * Checks if the king with the given color is being attacked.
   * 
   * @param color Color of the king to check for being attacked
   * @return If the king with the given color is being attacked
   */
  public boolean kingAttacked(Color color) {
    return isAttackedAt(getKingPositionFor(color));
  }

  /**
   * Checks if there is too little material on the board for a checkmate.
   * 
   * @return If there is too little material on the board
   */
  public boolean hasInsufficientMaterial() {
    Map<PieceType, Integer> remaining = new EnumMap<>(PieceType.class);
    BishopPiece whiteBishop = null;
    BishopPiece blackBishop = null;

    // init with zero to prevent null pointer exceptions
    for (PieceType type : PieceType.values()) {
      remaining.put(type, Integer.valueOf(0));
    }

    // generate a list of the remaining types of pieces
    for (Piece piece : pieces.values()) {
      PieceType type = piece.getType();
      Integer count = remaining.get(type);
      remaining.put(type, count + 1);

      // register bishop pieces since they contribute to a special condition
      if (piece instanceof BishopPiece) {
        BishopPiece bishop = (BishopPiece) piece;
        if (bishop.getColor() == Color.WHITE) {
          whiteBishop = bishop;
        } else {
          blackBishop = bishop;
        }
      }
    }

    // if there are too few kings or
    int totalAmount = pieces.size();
    return totalAmount <= 2
        // only one bishop or only one knight
        || totalAmount == 3 && (remaining.get(PieceType.BISHOP) == 1 || remaining.get(PieceType.KNIGHT) == 1)
        // or two bishops of the same color
        || totalAmount == 4 && whiteBishop != null && blackBishop != null
            && whiteBishop.getSquareColor() == blackBishop.getSquareColor();
  }

  /**
   * Returns a list of the captured pieces as a sorted list. The pieces are sorted
   * by color, value and creation index.
   * 
   * @return Sorted list of captured pieces
   */
  public List<Piece> getSortedCapturedPieces() {
    // copy and sort the set of captured pieces
    ArrayList<Piece> sortList = new ArrayList<Piece>(capturedPieces);
    PieceComparator comparator = new PieceComparator();
    Collections.sort(sortList, comparator);
    return sortList;
  }

  /**
   * Returns the position of the king for a given color. There are only ever two
   * kings and they are never captured since that would end the game.
   * 
   * @param color What color to return the king for
   * @return The position of the king for the given color
   */
  public Position getKingPositionFor(Color color) {
    return kingPositions.get(color);
  }

  /**
   * Returns the piece at a position specified by a coordinate pair on the board.
   * Null is returned if the position doesn't exist or the square is empty.
   * 
   * @param x X coordinate of the square to query
   * @param y Y coordinate of the square to query
   * @return Piece found at the given position
   */
  public Piece getPieceAt(int x, int y) {
    return pieces.get(Position.getBoardIndex(x, y));
  }

  /**
   * Returns the piece at a position specified by a position object. Returns null
   * if the position is invalid or there is no piece at that position.
   * 
   * @param position position of the square to query
   * @return piece found at the given position
   */
  public Piece getPieceAt(Position position) {
    return pieces.get(position.getBoardIndex());
  }

  /**
   * Returns the last move a history piece made. Null is it has not moved yet.
   * 
   * @param piece Piece to check the last move of
   * @return Last move this piece made
   */
  public Movement getPieceLastMove(HistoryPiece piece) {
    Deque<Movement> pieceHistory = pieceMoveHistory.get(piece);
    return pieceHistory == null ? null : pieceHistory.peek();
  }

  /**
   * Captures the piece at the given position by removing it from the board and
   * putting it in the list of captured pieces.
   * 
   * @param position position of the piece to capture
   */
  public void capturePiece(Position position) {
    Piece removedPiece = pieces.remove(position.getBoardIndex());
    if (removedPiece == null) {
      throw new IllegalStateException(
          "The capturing of position " + position + " is illegal since there is no piece at that position.");
    } else {
      capturedPieces.push(removedPiece);

      // reset the draw move index for the limited move rule
      gameState.notifyCapture();
    }
  }

  /**
   * Puts the last captured piece back to the given position. This is used for
   * reversing a capturing move.
   * 
   * @param putBackTo Position to put the piece back to
   */
  public void uncapturePiece(Position putBackTo) {
    // we expect reversing moves to work since they it's not a user initiated action
    pieces.put(putBackTo.getBoardIndex(), capturedPieces.pop());
  }

  /**
   * Updates the position of king pieces if the given piece is a king piece.
   * 
   * @param piece       Piece to update the position for if it's a king
   * @param newPosition New position of the given piece
   */
  private void updateKingPositions(Piece piece, Position newPosition) {
    if (piece instanceof KingPiece) {
      kingPositions.put(piece.getColor(), newPosition);
    }
  }

  /**
   * Places or replaces a piece on the board at a given position. This separated
   * coordinate version is only called within the initial game state construction.
   * 
   * @param x     X coordinate of the piece position
   * @param y     Y coordinate of the piece position
   * @param piece New Piece to put at the position
   */
  private void placeNewPiece(int x, int y, Piece piece) {
    placeNewPiece(new Position(x, y), piece);
  }

  /**
   * Places or replaces a piece on the board. This method is called when a new
   * piece is created during game state setup or during pawn promotion. The piece
   * already present at the given position is removed from this game state but
   * given to the new piece for later reference.
   * 
   * @param position Position to put the piece at
   * @param piece    Piece to place at the given position
   */
  public void placeNewPiece(Position position, Piece piece) {
    int boardIndex = position.getBoardIndex();
    Piece presentPiece = pieces.get(boardIndex);
    if (presentPiece != null) {
      piece.setReplacedPiece(presentPiece);
    }
    pieces.put(boardIndex, piece);
    if (piece instanceof BishopPiece) {
      ((BishopPiece) piece).notifyPosition(position);
    }

    // we don't need to keep track of king positions when reversing promotions since
    // kings are never the result of promotions
    updateKingPositions(piece, position);
  }

  /**
   * Removes a piece at the given position. This is used when reversing a
   * promotion move. Instead of directly overwriting this piece with another piece
   * while reversing a movement, this method should be called for clarity and so
   * the already present piece isn't set as the replaced piece on the original
   * piece (which would be wrong).
   * 
   * @param position Position to remove the piece at
   */
  public void removePiece(Position position) {
    pieces.remove(position.getBoardIndex());
  }

  /**
   * Applies a given movement of two positions to the pieces. The piece at the
   * "from" position is moved to the "to" position. This method expects the caller
   * to have made sure a piece at the starting position exists and the piece at
   * the target position has been captured or otherwise removed beforehand.
   * 
   * @param movement Movement to apply to the state
   */
  public void applyMovement(Movement movement) {
    Piece piece = pieces.remove(movement.getFromPosition().getBoardIndex());
    if (piece == null) {
      // throw when illegal movement is applied
      throw new IllegalArgumentException("The movement " + movement
          + " is not legal in this game state since there is no piece at the starting position.");
    }
    Position toPosition = movement.getToPosition();
    pieces.put(toPosition.getBoardIndex(), piece);

    // track the history for a history piece
    if (piece instanceof HistoryPiece) {
      HistoryPiece historyPiece = (HistoryPiece) piece;

      // create a new piece history for this piece if it doesn't have one yet
      Deque<Movement> pieceHistory = pieceMoveHistory.get(historyPiece);
      if (pieceHistory == null) {
        pieceHistory = new LinkedList<>(Set.of(movement));
        pieceMoveHistory.put(historyPiece, pieceHistory);
      } else {
        pieceHistory.push(movement);
      }
    }

    // reset the draw move index for the limited move rule when a pawn moves
    if (piece instanceof PawnPiece) {
      gameState.notifyCapture();
    }

    // track movement of the kings
    updateKingPositions(piece, toPosition);
  }

  /**
   * Reverses the effects of the given movement on the board. The piece at the to
   * position is moved back to the from position and the history piece entry is
   * popped.
   * 
   * @param movement Movement to reverse on the board state
   */
  public void reverseMovement(Movement movement) {
    Piece piece = pieces.remove(movement.getToPosition().getBoardIndex());
    Position targetPosition = movement.getFromPosition();
    pieces.put(targetPosition.getBoardIndex(), piece);

    // pop the latest entry on the history for this piece
    if (piece instanceof HistoryPiece) {
      // we know it has a move history stack
      // since this movement was added to this stack when it was applied
      pieceMoveHistory.get((HistoryPiece) piece).pop();
    }

    // track movement of the kings
    updateKingPositions(piece, targetPosition);
  }
}
