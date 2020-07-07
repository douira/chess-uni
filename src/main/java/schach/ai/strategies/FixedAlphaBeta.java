package schach.ai.strategies;

import schach.game.accumulators.SearchAccumulator;
import schach.game.moves.Move;
import schach.game.state.GameState;

/**
 * Implements the move search with a simple alpha/beta search that searches all
 * branches (as long as they're not cut off by pruning) until the given
 * maxDepth. Throws if maxDepth is not set (is 0). Setting max-depth to 1 will
 * cause it to choose the best move using only the evaluator and no branches.
 */
public class FixedAlphaBeta extends SearchStrategy {
  /**
   * Constructs a alpha/beta search strategy with the given maximum search depth
   * 
   * @param maxDepth Maximum search depth
   */
  public FixedAlphaBeta(int maxDepth) {
    super(maxDepth);

    // throw when maxDepth is disabled
    if (maxDepth == 0) {
      throw new IllegalArgumentException(
          "MaxDepth is 0: Cannot disable the maximum depth setting in the fixed alpha/beta pruning search.");
    }
  }

  /**
   * Looks for a good move using alpha/beta pruning search. All top-level moves
   * need to be searched since we can never exclude one of them because we don't
   * know if one of them might be better than what we already have.
   */
  @Override
  public Move findBestMove(GameState game) {
    return maximizeFirstLevel(game, this::minMax);
  }

  /**
   * Is used for searching for moves until branch pruning happens. We don't just
   * look for all legal moves since we can be faster if we only look for legal
   * moves until we stop because of pruning.
   */
  private class MinMaxAccumulator extends SearchAccumulator {
    private final GameState game;
    private final int depth;
    private final boolean maximizing;

    private double alpha;
    private double beta;
    private double bestValue;

    /**
     * Creates a new min max accumulator with the given search parameters. This will
     * accept moves until the branch is cut off.
     * 
     * @param alpha Alpha value for pruning
     * @param beta  Beta value for pruning
     * @param game  Game state to search
     * @param depth Current search depth, the root node is 0
     */
    public MinMaxAccumulator(double alpha, double beta, GameState game, int depth) {
      this.alpha = alpha;
      this.beta = beta;
      this.game = game;
      this.depth = depth;

      // we start with depth 1 (the root is depth 0) so that we minimize on the first
      // minMax step (the first minMax step is for the opponent minimizing our value)
      maximizing = depth % 2 == 0;
      bestValue = maximizing ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY;
    }

    public double getBestValue() {
      return bestValue;
    }

    @Override
    public boolean generateNonAttacking() {
      return true;
    }

    /**
     * "checks" a move by inspecting it in the tree further. Returns true if a cut
     * off happens.
     */
    @Override
    public boolean checkMove(Move move) {
      double moveValue = game.runWithMove(move, () -> minMax(alpha, beta, game, depth + 1));

      if (maximizing) {
        bestValue = Math.max(bestValue, moveValue);
        alpha = Math.max(alpha, bestValue);
      } else {
        bestValue = Math.min(bestValue, moveValue);
        beta = Math.min(beta, bestValue);
      }

      // return true to cut off when the alpha/beta condition is reached
      return alpha >= beta;
    }
  }

  /**
   * Does the first step of the search and places default parameters
   * 
   * @param game Game state to search on
   * @return Value of the game state reachable through optimal play
   */
  private double minMax(GameState game) {
    return minMax(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, game, 1);
  }

  /**
   * Does a step in the search using a min/max search with alpha/beta pruning.
   * Returns infinitely good values if the thread was aborted which causes the
   * search to stop because of the alpha/beta cutoff.
   * 
   * @param alpha Alpha value for pruning, best value for this player
   * @param beta  Beta value for pruning, best value for the opponent
   * @param game  Game state to search
   * @param depth Current search depth, the root node is 0
   * @return Value of the game state reachable through optimal play
   */
  private double minMax(double alpha, double beta, GameState game, int depth) {
    if (isAborted()) {
      return depth % 2 == 0 ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY;
    }

    // stop and calculate the concrete game state value
    // if the maximum depth has been reached or the game is in a stopped state
    // also stop on end game statuses
    if (depth == maxDepth || game.getStatus().gameIsStopped()) {
      return evaluator.calculateBoardValue(game);
    }

    // accumulate moves with a min max accumulator
    // in order to stop after cut off happens
    MinMaxAccumulator minMaxAccumulator = new MinMaxAccumulator(alpha, beta, game, depth);
    game.accumulateAllLegalMoves(minMaxAccumulator);
    return minMaxAccumulator.getBestValue();
  }
}
