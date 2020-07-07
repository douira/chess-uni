package schach.game.accumulators.filters;

import schach.game.accumulators.MoveAccumulator;
import schach.game.moves.Move;
import schach.game.state.GameState;

/**
 * A move filter is an accumulator that wraps another accumulator and only adds
 * moves to the inner accumulator if they fulfill a certain condition. This
 * condition is given by the implenting subclass. Filters don't set the
 * terminating condition of a
 */
abstract public class MoveFilter extends MoveAccumulator {
  private MoveAccumulator accumulator;

  /**
   * Sets the game state for both this accumulator and the inner accumulator in
   * case the inner accumulator also needs the game state at some point.
   */
  @Override
  public void setGameState(GameState gameState) {
    super.setGameState(gameState);
    accumulator.setGameState(gameState);
  }

  /**
   * Sets the inner accumulator for this move filter.
   * 
   * @param accumulator Accumulator to set as the inner accumulator
   */
  public void setAccumulator(MoveAccumulator accumulator) {
    this.accumulator = accumulator;
  }

  /**
   * Method in which implementing filters determine if a given move should be
   * passed along.
   * 
   * @param move Move to check for passing on
   * @return If the given move should be passed on
   */
  protected abstract boolean checkMove(Move move);

  /**
   * Only adds given moves to the inner accumulator if they fulfill the condition.
   */
  @Override
  public boolean addMove(Move move) {
    if (checkMove(move)) {
      return accumulator.addMove(move);
    }
    return accumulator.generateMore();
  }

  @Override
  public final boolean generateMore() {
    return accumulator.generateMore();
  }

  @Override
  public final boolean generateNonAttacking() {
    return accumulator.generateNonAttacking();
  }
}
