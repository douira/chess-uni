package schach.game.pieces;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import schach.common.Color;
import schach.common.Constants;
import schach.common.Position;
import schach.common.Utils;
import schach.common.Vector;
import schach.game.accumulators.MoveAccumulator;
import schach.game.accumulators.CaptureTestAccumulator;
import schach.game.moves.CapturingMove;
import schach.game.moves.Movement;
import schach.game.state.Board;
import schach.game.state.GameState;

/**
 * Piece represents the identity of a piece in the game state over the span of
 * the entire game. A piece does not hold mutable state. Such state is instead
 * held exclusively in the game state, hence the name. First of all, this makes
 * the rest of the game core simpler since the state is all in one place.
 * Another benefit of this architecture is that pieces don't ever need to be
 * created or copied after their first initialization.
 * 
 * The movement generation of each pice is implemented in the respective
 * subclasses. No piece may generate a move that leaves the king in check.
 */
public abstract class Piece {
  final Color color;

  private Piece replacedPiece;
  public PieceType type;

  /**
   * A common collection of the different offsets used to search for possible
   * moves a piece can make. These collections of vectors are iterated by the
   * different piece subclasses and passed to the methods for finding moves in a
   * straight line or as jumps.
   */
  static final class OffsetPatterns {
    /**
     * The movements a knight can make in any direction
     */
    public static final Collection<Vector> KNIGHT = Set.of(new Vector(1, 2), new Vector(-1, -2), new Vector(-1, 2),
        new Vector(1, -2), new Vector(2, 1), new Vector(-2, -1), new Vector(-2, 1), new Vector(2, -1));

    /**
     * Orthogonal movements (up/down and right/left) that a rook can make
     */
    public static final Collection<Vector> ORTHOGONAL = Set.of(new Vector(0, 1), new Vector(0, -1), new Vector(1, 0),
        new Vector(-1, 0));

    /**
     * Diagonal movements that a bishop can make
     */
    public static final Collection<Vector> DIAGONAL = Set.of(new Vector(1, 1), new Vector(-1, 1), new Vector(1, -1),
        new Vector(-1, -1));

    /**
     * Orthogonal and diagonal movements combined result in movements in any
     * direction. The king and the queen can make these movements.
     */
    public static final Collection<Vector> AROUND = Utils.concatCollections(ORTHOGONAL, DIAGONAL);

    /**
     * The position of the captured piece relative to the moving piece in an en
     * passant capture. This movement is the first half of a double move that
     * results in the moving pawn not on the square of the piece it captured.
     */
    public static final Collection<Vector> EN_PASSANT = Set.of(new Vector(1, 0), new Vector(-1, 0));

    /**
     * The standard movement of a pawn. Each pawn will resolve this and the
     * following FlipVectors the regular vectors using its color since pawn's
     * movement direction depends on which color they are.
     */
    public static final FlipVector FORWARD_STEP = new FlipVector(0, 1, FlipVector.VERTICAL);

    /**
     * The initial double step movement of a pawn
     */
    public static final FlipVector DOUBLE_FORWARD = new FlipVector(0, 2, FlipVector.VERTICAL);

    /**
     * The diagonal capturing movement of a pawn to both directions
     */
    public static final Collection<FlipVector> FORWARD_CAPTURE = Set.of(new FlipVector(-1, 1, FlipVector.VERTICAL),
        new FlipVector(1, 1, FlipVector.VERTICAL));

    /**
     * Where pawn promotions need to happen for the two colors
     */
    public static final Map<Color, Integer> PROMOTION_RANKS = Map.of(Color.BLACK, Constants.BOARD_SIZE - 1, Color.WHITE,
        0);

    /**
     * Prevent instances of this class
     */
    private OffsetPatterns() {
    }
  }

  /**
   * Constructs a new piece with the given color. The color can't be changed after
   * construction since pieces are are stateless and only represent a piece
   * identity.
   * 
   * @param color Color of the new piece
   */
  Piece(Color color) {
    this.color = color;
  }

  /**
   * Returns the symbol for this piece using the short name (single letter) from
   * the type.
   * 
   * @return Short name of the piece
   */
  public String getShortName() {
    return this.type.getShortName();
  }

  /**
   * Returns the symbol for this piece using the full name (word) from the type.
   * 
   * @return Full name of the piece
   */
  public String getFullName() {
    return this.type.getFullName();
  }

  /**
   * Returns the symbol for this piece using the symbol from the type.
   * 
   * @return Unicode symbol of the piece
   */
  public String getSymbol() {
    return this.type.getSymbol();
  }

  /**
   * Returns the value of this piece. This affects the sorting order of pieces.
   * 
   * @return Value of this piece depending on the type of piece it is
   */
  public int getOrdinal() {
    return type.ordinal();
  }

  public Color getColor() {
    return color;
  }

  public Piece getReplacedPiece() {
    return replacedPiece;
  }

  public PieceType getType() {
    return type;
  }

  public void setReplacedPiece(Piece replacedPiece) {
    this.replacedPiece = replacedPiece;
  }

  /**
   * Gets the sign for this piece for pureley text-based console display of
   * pieces. The single letter symbol is capitalized for white pieces.
   * 
   * @return Single letter symbol for this piece and piece color
   */
  public String getPieceSign() {
    return color.toColorCase(getShortName());
  }

  /**
   * Checks if this piece can be involved in a castling
   * 
   * @param accumulator Accumulator to use as a source of the game state
   * @return if this piece can castle, is false by default
   */
  boolean canCastle(MoveAccumulator accumulator) {
    return false;
  }

  /**
   * Checks if this piece can replace the given piece through a move (even a
   * capturing move). Only null pieces (no piece at a square) or pieces of the
   * opposing color can be replaced.
   * 
   * @param piece Piece to check for replacement
   * @return Whether given piece can be replaced by this piece
   */
  boolean canReplacePiece(Piece piece) {
    return piece == null || piece.getColor() != color;
  }

  /**
   * Constructs a move to make this piece move to the given target position. The
   * targetPiece is captured if not null. The targetPiece is expected to be of the
   * opposite color and this should be made sure of before calling this method.
   * 
   * @param from        Where this piece is moving from
   * @param target      Target position for the new move
   * @param targetPiece Piece on the target square if there is any
   * @return A new move that moves this piece to the target and captures the
   *         residing piece if necessary
   */
  Movement constructMovement(Position from, Position target, Piece targetPiece) {
    if (targetPiece == null) {
      return new Movement(from, target);
    } else {
      return new CapturingMove(from, target);
    }
  }

  /**
   * Checks if a move from the target with the given offset is possible and adds
   * it to the given accumulator.
   * 
   * @param accumulator Accumulator to add generated moves to
   * @param offset      Relative movement to the target field
   * @return If more moves should be generated
   */
  boolean accumulateJumpMove(MoveAccumulator accumulator, Vector offset) {
    // make sure that this move doesn't go out of bounds
    Position from = accumulator.getPosition();
    Position target = Position.fromOffset(from, offset);
    if (target.outOfBounds()) {
      return true;
    }

    // if the position can be taken, construct a move to do so if possible
    Piece targetPiece = accumulator.getGameState().getPieceAt(target);
    if (canReplacePiece(targetPiece)) {
      return accumulator.addMove(constructMovement(from, target, targetPiece));
    }
    return true;
  }

  /**
   * Accumulates simple jump moves with a given pattern collection.
   * 
   * @param accumulator Accumulator to add the moves to
   * @param patterns    Collection of patterns to generate moves with
   * @return If more moves should be generated
   */
  boolean processJumpPatterns(MoveAccumulator accumulator, Collection<Vector> patterns) {
    for (Vector offset : patterns) {
      if (!accumulateJumpMove(accumulator, offset)) {
        return false;
      }
    }
    return true;
  }

  /**
   * Checks if this piece can move to the squares starting at the given position
   * and adds all resulting moves to the provided accumulator. The first move
   * check starts with one step increment. All squares in the ray are checked for
   * obstacles and accumulation of moves stops when a obstacle is found.
   * 
   * @param accumulator Accumulator to add generated moves to
   * @param step        Movement of each step
   * @return If more moves should be generated
   */
  boolean accumulateRayMoves(MoveAccumulator accumulator, Vector step) {
    // iterate until we find an obstacle or the edge# of the board
    Position from = accumulator.getPosition();
    Position target = from;
    while (true) {
      // check the move of this piece to the current position in the ray
      target = Position.fromOffset(target, step);
      if (target.outOfBounds()) {
        return true;
      }
      Piece targetPiece = accumulator.getGameState().getPieceAt(target);
      if (canReplacePiece(targetPiece) && !accumulator.addMove(constructMovement(from, target, targetPiece))) {
        return false;
      }

      // stop advancing the ray if we found any piece (obstacle)
      if (targetPiece != null) {
        return true;
      }
    }
  }

  /**
   * Accumulates simple ray moves with a given pattern collection
   * 
   * @param accumulator Accumulator to add the moves to
   * @param patterns    Collection of patterns to generate moves with
   * @return If more moves should be generated
   */
  boolean processRayPatterns(MoveAccumulator accumulator, Collection<Vector> patterns) {
    for (Vector stepOffset : patterns) {
      if (!accumulateRayMoves(accumulator, stepOffset)) {
        return false;
      }
    }
    return true;
  }

  /**
   * Prepares a given accumulator with the position for use in deeper move
   * accumulation logic.
   * 
   * @param accumulator Accumulator to set the position on
   * @param position    Position to set on the accumulator for the processing
   *                    inside this piece
   * @return Accumulator with the piece position registered
   */
  public static MoveAccumulator prepareAccumulator(MoveAccumulator accumulator, Position position) {
    accumulator.setPosition(position);
    return accumulator;
  }

  /**
   * Prepares the given accumulator to receive moves from the implementing
   * subclass methods. The given position is attached to the accumulator since
   * otherwise we'd always have to pass both the accumulator and the position
   * around together.
   * 
   * @param accumulator Accumulator to add generated moves to
   * @param position    Position of the piece generating the moves
   * @return If more moves should be generated
   */
  public final boolean accumulateMoves(MoveAccumulator accumulator, Position position) {
    prepareAccumulator(accumulator, position);
    return accumulateMoves(accumulator);
  }

  /**
   * Adds moves for the implementing piece to the accumulator. This will vary
   * depending on other pieces on the board, the type of the piece, if the piece
   * has moved yet and other factors. The accumulator is expected to already
   * contain the current piece position.
   * 
   * @param accumulator Prepared accumulator to add generated moves to
   * @return If more moves should be generated
   */
  abstract boolean accumulateMoves(MoveAccumulator accumulator);

  /**
   * Checks if this piece at the given position can be captured by any opposing
   * piece on the board.
   * 
   * @param accumulator Accumulator to use as a source of the game state and the
   *                    position of the current piece
   * @param target      Position that the piece should be checked at
   * @return Result of the query if this piece can be captured there
   */
  public boolean isAttackedAt(MoveAccumulator accumulator, Position target) {
    return isAttackedAt(accumulator.getGameState(), accumulator.getPosition(), target);
  }

  /**
   * Checks if a piece can be attacked at a given position. The piece is moved to
   * this position temporarily.
   * 
   * @param gameState     Game state to check the piece in
   * @param piecePosition Position of the piece currently
   * @param target        Position to check the piece at
   * @return If the piece can be attacked at the target position
   */
  public boolean isAttackedAt(GameState gameState, Position piecePosition, Position target) {
    // if the piece is not at the given position, move it there temporarily so that
    // capturing moves can be generated if they attack the position
    Board board = gameState.getBoard();
    Movement tempMovement = new Movement(piecePosition, target);
    board.applyMovement(tempMovement);
    boolean isAttacked = isAttackedLocally(gameState, target);
    board.reverseMovement(tempMovement);
    return isAttacked;
  }

  /**
   * Checks if this piece at the given position can be captured by any opposing
   * piece on the board at the position it's at currently
   * 
   * @param gameState Game state to check the attack with
   * @param current   Position of this piece that is being tested
   * @return Result of the query if this piece can be captured there
   */
  public boolean isAttackedLocally(GameState gameState, Position current) {
    // accumulate moves using a test accumulator that lazily looks for moves
    // attacking the position of this piece
    CaptureTestAccumulator testAccumulator = gameState.accumulateAllMoves(new CaptureTestAccumulator(current),
        color.getOpposing());
    return testAccumulator.conditionIsSatisfied();
  }
}
