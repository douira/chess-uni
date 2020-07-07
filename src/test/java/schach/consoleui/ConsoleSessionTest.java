package schach.consoleui;

import java.lang.reflect.Field;
import java.util.Set;

import schach.ai.AIPlayer;
import schach.common.Color;
import schach.common.Environment;
import schach.common.Position;
import schach.game.state.GameState;
import schach.interaction.GameMode;
import schach.interaction.Session;

import org.junit.jupiter.api.Test;
//prevent pmd false positives with static util imports
import static schach.consoleui.IOTestUtils.*; //NOPMD
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests that the console session works correctly.
 */
public class ConsoleSessionTest {
  private Environment regularEnv = new Environment();
  Environment preselectEnv = new Environment(Set.of(Environment.Flag.MODE_PRESELECTED));

  /**
   * Tests that the game mode is prompted correctly.
   */
  @Test
  public void testPromptGameMode() {
    assertPrinted(
        "Select a game mode by entering a game mode name like 'X-Y':\n  [W-C] Human playing white vs. Computer\n  [B-C] Human playing black vs. Computer\n  [H-H] Human vs. Human\nInvalid game mode, try again. Enter strings of the form 'X-Y'.\nSelected: [H-H] Human vs. Human\nEnter moves in the form 'x9-y8'. Print captured pieces with the 'beaten' command.\nWhite begins.\n\n",
        (printStream) -> new ConsoleSession(regularEnv, new BoardPrinter(regularEnv), mockInputStream("hh\nh-h\n"),
            printStream));
  }

  /**
   * Test printing the captured pieces. The bulk of this is tested in the board
   * printer test. This is just to ensure it actually appears if the console
   * session is requested to print it.
   */
  @Test
  public void testPrintCapturedPieces() {
    assertPrinted("The following pieces have been captured: \nr \n", (printStream) -> {
      ConsoleSession session = new ConsoleSession(preselectEnv, new BoardPrinter(preselectEnv, printStream));
      session.stopGame();
      GameState preparedGame = new GameState();
      preparedGame.getBoard().capturePiece(new Position(0, 0));
      assertDoesNotThrow(() -> {
        Field gameField = Session.class.getDeclaredField("game");
        gameField.setAccessible(true);
        gameField.set(session, preparedGame);
        session.printCapturedPieces();
      });
    });
  }

  /**
   * Tests starting the console session in a specific mode. Game mode testing is
   * done in the test for game mode.
   */
  @Test
  public void testStartInMode() {
    ConsoleSession session = new ConsoleSession(preselectEnv, new BoardPrinter(preselectEnv));
    session.stopGame();
    session.startInMode(GameMode.WHITE_V_AI);
    assertTrue(session.getPlayerFor(Color.WHITE) instanceof ConsolePlayer);
    assertTrue(session.getPlayerFor(Color.BLACK) instanceof AIPlayer);
  }
}
