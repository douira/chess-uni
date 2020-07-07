package schach.game.accumulators;

import schach.game.moves.Move;

/**
 * This accumulator checks if there are any moves possible. This can also be
 * used as the last element in a filter chain.
 */
public class AcceptAccumulator extends SearchAccumulator {
  @Override
  protected boolean checkMove(Move move) {
    return true;
  }

  @Override
  public boolean generateNonAttacking() {
    return true;
  }
}
