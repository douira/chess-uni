package schach.game.accumulators;

import schach.common.Position;
import schach.game.moves.Move;

/**
 * A move accumulator that looks for a move that attacks a certain position and
 * then stops accumulation since it has found an attacking move already.
 */
public class CaptureTestAccumulator extends SearchAccumulator {
  private final Position testPosition;

  /**
   * Constructs a test accumulator for the given position that is scanned for.
   * 
   * @param testPosition Position to test a move targeting
   */
  public CaptureTestAccumulator(Position testPosition) {
    this.testPosition = testPosition;
  }

  /**
   * The capture test is fulfilled when a move attacking the test position is
   * found.
   */
  @Override
  protected boolean checkMove(Move move) {
    return move.isAttacking(testPosition);
  }

  /**
   * Since we are only interesting in capturing moves, non-attacking moves don't
   * need to be generated.
   */
  @Override
  public boolean generateNonAttacking() {
    return false;
  }
}
