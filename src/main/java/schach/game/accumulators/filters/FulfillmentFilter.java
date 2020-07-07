package schach.game.accumulators.filters;

import schach.game.moves.Move;
import schach.game.moves.Movement;

/**
 * A validation accumulator checks if moves fulfill a certain movement and is
 * then used to return the proper move corresponding to the given movement.
 */
public class FulfillmentFilter extends MoveFilter {
  private final Movement testMovement;

  /**
   * Constructs a test accumulator for the given movement.
   * 
   * @param testMovement Movement check moves against
   */
  public FulfillmentFilter(Movement testMovement) {
    this.testMovement = testMovement;
  }

  /**
   * The validation is fulfilled when a move fulfilling the test movement is
   * found.
   */
  @Override
  protected boolean checkMove(Move move) {
    return move.fulfillsMovement(testMovement);
  }
}
