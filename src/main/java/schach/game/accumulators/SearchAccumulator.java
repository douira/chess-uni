package schach.game.accumulators;

import schach.game.moves.Move;

/**
 * A search accumulator is used to look for a move that fulfills a certain
 * condition which the implementing subclass provides. The search accumulator
 * stops the generation of more moves once the condition has been fulfilled.
 */
public abstract class SearchAccumulator extends MoveAccumulator {
  private Move satisfyingMove;

  /**
   * Returns the move that satisfies the condition provided by the subclass.
   * 
   * @return Move that satisfies the condition
   */
  public Move getSatisfyingMove() {
    return satisfyingMove;
  }

  /**
   * Checks if a move was added that satisfied the condition. This value can
   * change as more moves are generated.
   * 
   * @return If a satisfying move was added yet
   */
  public boolean conditionIsSatisfied() {
    return satisfyingMove != null;
  }

  /**
   * Checks if the given move satisfies the condition implemented by the subclass.
   * 
   * @param move Move to check the condition for
   * @return If the given move fulfills the condition
   */
  protected abstract boolean checkMove(Move move);

  /**
   * Checks if the given move fulfills the condition provided by the subclass.
   * Accumulation is halted if the condition is satisfied by the given move.
   */
  @Override
  public boolean addMove(Move move) {
    // signal no more moves are wanted when move has already been found
    if (conditionIsSatisfied()) {
      return false;
    }

    // when a satisfying move is found, save it and also signal to stop
    if (checkMove(move)) {
      satisfyingMove = move;
      return false;
    } else {
      return true;
    }
  }

  /**
   * Generate more moves as long as the satisfying move has not been found yet.
   */
  @Override
  public boolean generateMore() {
    return !conditionIsSatisfied();
  }
}
