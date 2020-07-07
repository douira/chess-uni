package schach.game.moves;

import schach.common.Position;
import schach.game.state.Board;

/**
 * A movement is a basic move by a single piece to a new empty square. Collision
 * and capturing detection needs to have happened before construction since this
 * move only directly applies the movement of the active piece. It is expected
 * that the piece in question is at the given starting position when the game
 * state is applied.
 * 
 * A simple movement is not an attacking move unless the subclass CapturingMove
 * is used.
 */
public class Movement extends Move {
  protected final Position from;
  protected final Position to;

  /**
   * Constructs a new movement from a to and from position.
   * 
   * @param from Starting position of the piece being moved
   * @param to   Final position of the moving piece
   */
  public Movement(Position from, Position to) {
    this.from = from;
    this.to = to;
  }

  /**
   * Creates a movement from four coordinates for convenience.
   * 
   * 
   * @param x1 X coordinate of the first position
   * @param y1 Y coordinate of the first position
   * @param x2 X coordinate of the second position
   * @param y2 Y coordinate of the second position
   */
  public Movement(int x1, int y1, int x2, int y2) {
    this(new Position(x1, y1), new Position(x2, y2));
  }

  /**
   * Returns the origin position of this movement.
   * 
   * @return Where this movement starts, the moving piece must be at this
   *         position.
   */
  public Position getFromPosition() {
    return from;
  }

  /**
   * Returns the target position of this movement.
   * 
   * @return Where this movement goes to
   */
  public Position getToPosition() {
    return to;
  }

  /**
   * A movement doesn't need to do anything special to get a movement.
   */
  @Override
  public Movement getFulfillingMovement() {
    return this;
  }

  /**
   * Checks if two movements are equal. This also checks that the move type is the
   * same. The move type is not checked in other subclasses of Move since the move
   * type is irrelevant there (for now).
   * 
   * @param other Other movement to check equality to
   * @return If this movement is equal to the other movement
   */
  public boolean movesEqual(Movement other) {
    return other.from.equals(from) && other.to.equals(to) && other.hasMoveType(moveType);
  }

  @Override
  public Position getOriginPosition() {
    return from;
  }

  @Override
  public Position getTargetPosition() {
    return to;
  }

  @Override
  public boolean isAttacking(Position checkPosition) {
    return false;
  }

  /**
   * Applies this movement to the game state by calling the respective method on
   * the game state which causes it to move the reference to the piece found at
   * the starting position to the final position.
   */
  @Override
  public void applyTo(Board board) {
    board.applyMovement(this);
  }

  @Override
  public void reverseOn(Board board) {
    board.reverseMovement(this);
  }

  @Override
  public String toString() {
    return from + "->" + to + (moveType == MoveType.UNSPECIFIED ? "" : moveType);
  }

  @Override
  public int hashCode() {
    return from.hashCode() * 13 + to.hashCode();
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) {
      return true;
    }
    if (!(other instanceof Movement)) {
      return false;
    }
    return movesEqual((Movement) other);
  }
}
