package schach.ai.strategies;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import schach.ai.GameEvaluator;
import schach.common.Color;
import schach.common.Position;
import schach.game.state.Board;
import schach.game.state.GameState;

/**
 * Tests the ShallowEvaluation of the game.
 *
 */
public class ShallowEvaluationTest {
  GameEvaluator evaluator = new GameEvaluator();
  
  /**
   * Tests that the ai color is set correctly indirectly.
   */
  @Test
  public void testFindBestMove() {
    ShallowEvaluation shallowEvaluation = new ShallowEvaluation();
    evaluator.setAiColor(Color.WHITE);
    shallowEvaluation.setEvaluator(evaluator);
    shallowEvaluation.setPlayer(new FakeThreadedPlayer());
    
    // Constructs a gameState where capturing the rook is the definite best move
    GameState game = new GameState();
    Board board = game.getBoard();
    board.capturePiece(new Position(0, 6));
    board.capturePiece(new Position(0, 1));

    assertEquals(shallowEvaluation.findBestMove(game).getOriginPosition(), new Position(0,7));
    assertEquals(shallowEvaluation.findBestMove(game).getTargetPosition(), new Position(0,0));
  }
}
