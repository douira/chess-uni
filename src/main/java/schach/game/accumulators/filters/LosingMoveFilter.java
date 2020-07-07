package schach.game.accumulators.filters;

import schach.common.Color;
import schach.game.moves.Move;

/**
 * A Losing move filter is an accumulator that wraps another accumulator and
 * only adds moves to the inner accumulator when they don't cause the current
 * active king to move into check or be put into check.
 */
public class LosingMoveFilter extends MoveFilter {
  @Override
  protected boolean checkMove(Move move) {
    // check if the currently active king can be attacked after making this move
    Color prevActiveColor = gameState.getActiveColor();
    return !gameState.runWithMove(move, () -> gameState.getBoard().kingAttacked(prevActiveColor));
  }
}
