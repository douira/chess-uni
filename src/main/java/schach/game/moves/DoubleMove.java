package schach.game.moves;

import schach.common.Position;
import schach.game.state.Board;

/**
 * A DoubleMove is the combination of two separate moves that are applied one
 * after the other. This is meant for situations where the two moves are
 * distinct and the behavior of the double move doesn't need to depend primarily
 * on one or the other partial move.
 * 
 * The origin and target positions need to be explicitly set on a double move
 * since they can't be directly inferred as in a regular movement. The origin
 * and target positions should form the movement that this move primarily
 * represents and that is entered by a player to specify this move.
 */
public class DoubleMove extends Move {
  private final Move first;
  private final Move second;

  private final Position origin;
  private final Position target;

  /**
   * Constructs a new double move using two already constructed moves.
   * 
   * @param first  First move to apply when the double move is applied
   * @param second Second move to apply when the double move is applied
   * @param origin Origin position of this double move
   * @param target Target position of this double move
   */
  public DoubleMove(Move first, Move second, Position origin, Position target) {
    this.first = first;
    this.second = second;

    this.origin = origin;
    this.target = target;
  }

  /**
   * Constructs a double move using a movement and a move. The origin and
   * destination positions are taken from the first move (a movement).
   * 
   * @param first  First move to apply, this gives the origin and destination
   *               positions
   * @param second Second move to apply
   */
  public DoubleMove(Movement first, Move second) {
    this(first, second, first.getFromPosition(), first.getToPosition());
  }

  @Override
  public Position getOriginPosition() {
    return origin;
  }

  @Override
  public Position getTargetPosition() {
    return target;
  }

  @Override
  public boolean isAttacking(Position checkPosition) {
    return first.isAttacking(checkPosition) || second.isAttacking(checkPosition);
  }

  /**
   * Applies both moves to the game state in order.
   */
  @Override
  public void applyTo(Board board) {
    first.applyTo(board);
    second.applyTo(board);
  }

  @Override
  public void reverseOn(Board board) {
    second.reverseOn(board);
    first.reverseOn(board);
  }

  @Override
  public String toString() {
    return first.toString() + "+" + second.toString() + " [" + new Movement(origin, target).toString() + "]";
  }

  @Override
  public int hashCode() {
    return first.hashCode() * 23 + second.hashCode();
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) {
      return true;
    }
    if (!(other instanceof DoubleMove)) {
      return false;
    }
    DoubleMove otherDoubleMove = (DoubleMove) other;
    return otherDoubleMove.getOriginPosition().equals(origin) && otherDoubleMove.getTargetPosition().equals(target)
        && otherDoubleMove.first.equals(first) && otherDoubleMove.second.equals(second);
  }
}
