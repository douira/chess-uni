package schach.ai;

import schach.ai.strategies.FixedAlphaBeta;
import schach.ai.strategies.SearchStrategy;
import schach.ai.strategies.ShallowEvaluation;
import schach.game.moves.Move;
import schach.game.state.GameState;
import schach.interaction.ThreadedPlayer;

/**
 * Calculates moves for an ai player using a given search strategy and a game
 * evaluator. It also provides static factory functions for creating move
 * calculators with certain strategy settings.
 */
public class MoveCalculator {
  private SearchStrategy strategy;
  private GameEvaluator evaluator;

  /**
   * Constructs a move calculator with a search strategy. This constructor is not
   * publicly used but rather one of the utility methods that construct a strategy
   * for you.
   * 
   * @param strategy Strategy for calculating the best move
   */
  private MoveCalculator(SearchStrategy strategy) {
    evaluator = new GameEvaluator();
    this.strategy = strategy;
    strategy.setEvaluator(evaluator);
  }

  /**
   * Sets the player on the strategy for detecting when the search should be
   * aborted.
   * 
   * @param player Player to set as the abort source
   */
  public void setPlayer(ThreadedPlayer player) {
    strategy.setPlayer(player);
  }

  /**
   * Sets the maximum search depth on the search strategy.
   * 
   * @param maxDepth New maximum search depth
   */
  public void setMaxSearchDepth(int maxDepth) {
    strategy.setMaxDepth(maxDepth);
  }

  /**
   * Calculates the best found move for the given game state with the chosen
   * search strategy.
   * 
   * @param game Game state to search in
   * @return Best found move in this game state, this move can be expected to be
   *         legal in this game state
   */
  public Move findBestMove(GameState game) {
    evaluator.setAiColor(game.getActiveColor());
    return strategy.findBestMove(game);
  }

  /**
   * Returns a new move calculator using the shallow evaluation strategy.
   * 
   * @return Move calculator with the shallow evaluation strategy
   */
  public static MoveCalculator withShallowEvaluation() {
    return new MoveCalculator(new ShallowEvaluation());
  }

  /**
   * Returns a new move calculator using the fixed alpha/beta pruning search
   * strategy.
   * 
   * @param maxDepth Search depth to set on the alpha/beta search strategy
   * @return Move calculator with the fixed alpha/beta search strategy.
   */
  public static MoveCalculator withFixedAlphaBeta(int maxDepth) {
    return new MoveCalculator(new FixedAlphaBeta(maxDepth));
  }
}
