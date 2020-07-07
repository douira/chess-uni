package schach.consoleui;

import schach.common.Color;
import schach.common.Environment;
import schach.interaction.Session;

import org.junit.jupiter.api.Test;
//prevent pmd false positives with static util imports
import static schach.consoleui.IOTestUtils.*; //NOPMD

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the behavior of the console player.
 */
public class ConsolePlayerTest {
  private Environment env = new Environment(Set.of(Environment.Flag.MODE_PRESELECTED));
  private BoardPrinter printer = new BoardPrinter(env);

  /**
   * Test that the player is setup correctly.
   */
  @Test
  public void testSetupPlayer() {
    assertThrows(IllegalArgumentException.class,
        () -> new ConsolePlayer(printer).setupPlayer(new Session(), Color.WHITE));

    ConsolePlayer player = new ConsolePlayer(printer);
    ConsoleSession session = new ConsoleSession(env, printer);
    player.setupPlayer(session, Color.BLACK);
    assertEquals(Color.BLACK, player.getColor());
  }

  /**
   * Test that messages are printed.
   */
  @Test
  public void testPrintMessage() {
    assertPrinted("foobar\nfoo\n",
        (printStream) -> new ConsolePlayer(printer, new CommandInterface(System.in), printStream)
            .printMessage("foobar\nfoo"));
  }

  /**
   * Test that it sets the abort flag.
   */
  @Test
  public void testAbortCommandRequest() {
    ConsolePlayer player = new ConsolePlayer(printer);
    assertFalse(player.isAborted());
    player.abortCommandRequest();
    assertTrue(player.isAborted());
  }
}
