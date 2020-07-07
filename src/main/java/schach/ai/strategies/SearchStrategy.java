package schach.ai.strategies;

import java.util.function.Function;

import schach.ai.GameEvaluator;
import schach.game.moves.Move;
import schach.game.state.GameState;
import schach.interaction.ThreadedPlayer;

/**
 * Describes how a search strategy looks. A search strategy is given a game
 * evaluator and parameters upon construction. When it should calculate a good
 * move, it's given a game state to search from.
 */
public abstract class SearchStrategy {
  /**
   * Maximum search depth to use. Branches deeper than this are never evaluated.
   * If this is set to 0, the search depth can be dynamically chosen if possible
   * or isn't set at all.
   */
  int maxDepth;

  GameEvaluator evaluator;
  private ThreadedPlayer player;

  /**
   * Constructs a new search strategy instance with the depth setting.
   * 
   * @param maxDepth Maximum search depth
   */
  public SearchStrategy(int maxDepth) {
    this.maxDepth = maxDepth;
  }

  public void setMaxDepth(int maxDepth) {
    this.maxDepth = maxDepth;
  }

  public void setEvaluator(GameEvaluator evaluator) {
    this.evaluator = evaluator;
  }

  public void setPlayer(ThreadedPlayer player) {
    this.player = player;
  }

  /**
   * Checks if the search has been aborted.
   * 
   * @return True if aborted
   */
  boolean isAborted() {
    return player.isAborted();
  }

  /**
   * Finds the best (or at least the best one it can find) move to make (for the
   * active color) in this game state. How this is done depends on the
   * implementing subclass.
   * 
   * @param game Game to find the best move for
   * @return Best found move for this game state, it's expected that this move is
   *         legal on this game state
   */
  public abstract Move findBestMove(GameState game);

  /**
   * Does a simple search of the top-level legal moves and returns the move that
   * the mapper produces the highest value for.
   * 
   * @param game   Game to search in
   * @param mapper Mapper that produces the value to choose moves with
   * @return Move with the highest mapped value
   */
  Move maximizeFirstLevel(GameState game, Function<GameState, Double> mapper) {
    Move bestMove = null;
    double bestMoveValue = Double.NEGATIVE_INFINITY;

    // find the best legal move
    for (Move move : game.getAllLegalMoves()) {
      if (isAborted()) {
        return null;
      }

      // if the value of this move is higher than the current best move,
      // take it as the new best move
      double value = game.runWithMove(move, () -> mapper.apply(game));
      if (value >= bestMoveValue) {
        bestMoveValue = value;
        bestMove = move;
      }
    }

    // if no move was found that means no move was valid
    if (bestMove == null) {
      throw new IllegalStateException("No move could be found for search!");
    }
    if (isAborted()) {
      return null;
    }
    return bestMove;
  }
}
