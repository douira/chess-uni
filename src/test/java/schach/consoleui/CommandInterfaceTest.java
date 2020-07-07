package schach.consoleui;

import java.lang.reflect.Field;

import schach.common.Position;
import schach.consoleui.commands.ConsoleCommand;
import schach.consoleui.commands.ConsoleJournalCommand;
import schach.consoleui.commands.PrintCapturedCommand;
import schach.consoleui.commands.PrintingCommand;
import schach.game.moves.MoveType;
import schach.game.moves.Movement;
import schach.game.moves.PromotionMove;
import schach.interaction.GameMode;

import org.junit.jupiter.api.Test;
//prevent pmd false positives with static util imports
import static schach.consoleui.IOTestUtils.*; //NOPMD
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the behavior of the input handler
 */
public class CommandInterfaceTest {
  /**
   * Asserts that the given command results in the given move.
   * 
   * @param expected Movement to expect from this command
   * @param command  String command to parse
   */
  private void testMoveCommand(Movement expected, String input) {
    // Test by making a fake input and reading it
    ConsoleCommand command = new CommandInterface(mockInputStream(input + "\n")).readCommand();
    assertDoesNotThrow(() -> {
      Field movementField = command.getClass().getDeclaredField("movement");
      movementField.setAccessible(true);
      assertEquals(expected, movementField.get(command));
    });
  }

  /**
   * Tests if the user input is converted into console commands correctly
   */
  @Test
  public void testParseInput() {
    Movement move = new Movement(new Position(0, 7), new Position(0, 6));

    // Tests beaten command
    assertEquals(PrintCapturedCommand.INSTANCE, CommandInterface.parseInput("beaten"));

    // Test movement and promotion commands
    move.setMoveType(MoveType.PROMOTION_QUEEN);
    testMoveCommand(move, "a1-a2");
    testMoveCommand(move, "a1-a2Q");
    move.setMoveType(MoveType.PROMOTION_ROOK);
    testMoveCommand(move, "a1-a2R");
    move.setMoveType(MoveType.PROMOTION_BISHOP);
    testMoveCommand(move, "a1-a2B");
    move.setMoveType(MoveType.PROMOTION_KNIGHT);
    testMoveCommand(move, "a1-a2N");

    // Test that out of bounds and wrong inputs return null
    String[] invalidInputs = { "a1-a2X", "a200-a2", "a1-a200", "+1-a2", "a200-a2", "", "-a2", "a1-a", "aa-a2",
        "a1-aa" };
    for (String input : invalidInputs) {
      assertEquals(PrintingCommand.INVALID_INPUT, CommandInterface.parseInput(input));
    }

    // test generation of journal commands
    assertEquals(ConsoleJournalCommand.UNDO, CommandInterface.parseInput("undo"));
    assertEquals(ConsoleJournalCommand.REDO, CommandInterface.parseInput("redo"));
  }

  /**
   * Test that moves are converted to command strings correctly
   */
  @Test
  public void testMoveToCommandString() {
    assertEquals("a8-b7", CommandInterface.moveToCommandString(new Movement(0, 0, 1, 1)));
    assertEquals("h3-d1", CommandInterface.moveToCommandString(new Movement(7, 5, 3, 7)));
    assertEquals("a8-a8Q",
        CommandInterface.moveToCommandString(new PromotionMove(new Movement(0, 0, 0, 0), MoveType.PROMOTION_QUEEN)));
    assertEquals("a8-a8B",
        CommandInterface.moveToCommandString(new PromotionMove(new Movement(0, 0, 0, 0), MoveType.PROMOTION_BISHOP)));
  }

  /**
   * Tests that init commands are handled correctly.
   */
  @Test
  public void testParseInitInput() {
    assertNull(CommandInterface.parseInitInput("543543"));
    assertEquals(GameMode.HUMANS, CommandInterface.parseInitInput("H-H"));
  }

  /**
   * Tests that the init commands are read correctly from a stream.
   */
  @Test
  public void testReadInitCommand() {
    assertNull(new CommandInterface(mockInputStream("43543534\n")).readInitCommand());
    assertEquals(GameMode.WHITE_V_AI, new CommandInterface(mockInputStream("W-C\n")).readInitCommand());
  }
}
