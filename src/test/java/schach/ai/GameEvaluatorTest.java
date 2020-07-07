package schach.ai;

import schach.common.Color;
import schach.common.Position;
import schach.game.state.Board;
import schach.game.state.GameState;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
//NOPMD is required here to suppress false positives
import static schach.game.GameTestUtils.*; //NOPMD

/**
 * Tests the behavior of the game evaluator. No concrete state valuations are
 * asserted because they can easily change but rather the general behavior is
 * tested.
 */
public class GameEvaluatorTest {
  GameEvaluator evaluator = new GameEvaluator();

  /**
   * Tests that the ai color is set correctly indirectly.
   */
  @Test
  public void testSetAiColor() {
    evaluator.setAiColor(Color.WHITE);

    // test that it's 0 to begin with
    GameState game = new GameState();
    Board board = game.getBoard();
    assertTrue(evaluator.calculateBoardValue(game) < 1E-3);

    // Test that a modified board as the correct value
    board.capturePiece(new Position(0, 0));
    assertTrue(evaluator.calculateBoardValue(game) > 0);

    // test also in the other direction
    board.capturePiece(new Position(0, 7));
    board.capturePiece(new Position(0, 6));
    assertTrue(evaluator.calculateBoardValue(game) < 0);
  }

  /**
   * Tests that the board value is correct in end situations.
   */
  @Test
  public void testCalculateBoardValue() {
    evaluator.setAiColor(Color.WHITE);

    // Test that it returns the min/max value on end-states
    GameState game = new GameState();
    intoCheckmate(game);
    assertEquals(Double.NEGATIVE_INFINITY, evaluator.calculateBoardValue(game));
    game = new GameState();
    intoDraw(game);
    assertEquals(0, evaluator.calculateBoardValue(game));
  }
}
