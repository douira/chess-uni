package schach.ai.strategies;

import schach.game.moves.Move;
import schach.game.state.GameState;

/**
 * Generates a move using only shallow evaluation of all possible moves.
 */
public class ShallowEvaluation extends SearchStrategy {
  /**
   * Constructs a new shallow evaluation strategy. The maxDepth value is ignored
   * since this is always depth 1.
   */
  public ShallowEvaluation() {
    super(0);
  }

  /**
   * Simply look for the move with that produces the state with the highest value.
   */
  @Override
  public Move findBestMove(GameState game) {
    return maximizeFirstLevel(game, evaluator::calculateBoardValue);
  }
}
