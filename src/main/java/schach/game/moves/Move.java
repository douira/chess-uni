package schach.game.moves;

import schach.common.Color;
import schach.common.Position;
import schach.game.state.Board;
import schach.game.state.GameState;

/**
 * A move is usually a combination of a piece and movement information that can
 * be applied to a game state. A move can be thought of as a description of the
 * state transition of the game state. No reference to the piece being moved is
 * stored because the moving piece is identified by it's current position.
 */
public abstract class Move {
  private int moveIndex;

  /**
   * By default, moves start off as unspecified and are then set to a specific
   * type if necessary.
   */
  protected MoveType moveType = MoveType.UNSPECIFIED;

  /**
   * Checks if this move is the latest move in the given game state.
   * 
   * @param game Game to check
   * @return True if this move is the latest move
   */
  public boolean isLatestMoveOf(GameState game) {
    return game.getJournal().isLatestMoveIndex(moveIndex);
  }

  /**
   * Checks if the given move type matches this move.
   * 
   * @param type Move type to match
   * @return True if this move has the same move type.
   */
  public boolean hasMoveType(MoveType type) {
    return moveType == type;
  }

  public MoveType getMoveType() {
    return moveType;
  }

  public void setMoveType(MoveType type) {
    moveType = type;
  }

  public void setMoveIndex(int moveIndex) {
    this.moveIndex = moveIndex;
  }

  /**
   * Returns which color this move was made by determined by it's move index.
   * 
   * @return Color of the player that made this move
   */
  public Color getByColor() {
    return moveIndex <= 0 ? null : moveIndex % 2 == 0 ? Color.BLACK : Color.WHITE;
  }

  /**
   * Checks if this move fulfills the given movement. This is used for checking if
   * a user input corresponds to a complex constructed (and possibly nested) move.
   * 
   * @param movement Movement to check for fulfillment
   * @return Targeted square of this move
   */
  public boolean fulfillsMovement(Movement movement) {
    return movement.getFromPosition().equals(getOriginPosition())
        && movement.getToPosition().equals(getTargetPosition());
  }

  /**
   * Returns the movement that fulfills this move and also sets the correct move
   * type.
   * 
   * @return A movement that fulfills this move and has the same move type.
   */
  public Movement getFulfillingMovement() {
    Movement movement = new Movement(getOriginPosition(), getTargetPosition());
    movement.setMoveType(moveType);
    return movement;
  }

  /**
   * Returns the origin position of this move. This is only really significant for
   * double moves where the origin might not be clear. The value of this will
   * cooincide with the answer that fulfillsMovement gives you.
   * 
   * @return Origin position of this move
   */
  public abstract Position getOriginPosition();

  /**
   * Returns the target position of this move.
   * 
   * @return Target position of this move
   */
  public abstract Position getTargetPosition();

  /**
   * Checks if this move is attacking the given position.
   * 
   * @param checkPosition What position to test for being attacked by this move
   * @return If the given position is being attacked
   */
  public abstract boolean isAttacking(Position checkPosition);

  /**
   * Applies this move to the given game state. The implementation will call
   * methods on the game state to modify it according to the type of the move and
   * the concrete piece and movement the move contains. Since the game state is
   * modified directly, care should be taken to copy or otherwise preserve the
   * previous game state if reversibility of this state transition is required.
   * 
   * @param board Board state this move should be applied to
   */
  public abstract void applyTo(Board board);

  /**
   * Reverses this move on the given game state. The game state should be in the
   * exact state it was in before this move was applied. This typically involves
   * calling methods for reversing actions in the opposite order as they're called
   * in applyTo.
   * 
   * @param board Board state to reverse this move on
   */
  public abstract void reverseOn(Board board);
}
