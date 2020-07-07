package schach.consoleui.commands;

import schach.consoleui.ConsolePlayer;
import schach.consoleui.ConsoleSession;
import schach.interaction.commands.SessionCommand;

/**
 * Command that prints something as it's result. Instances are not created from
 * the outside but the given static instances are used.
 */
public class PrintingCommand implements ConsoleCommand {
  /**
   * The instance of the invalid command.
   */
  public static final PrintingCommand INVALID_INPUT = new PrintingCommand("!Invalid move");

  /**
   * The instance of the illegal command.
   */
  public static final PrintingCommand MOVE_ILLEGAL = new PrintingCommand("!Move not allowed");

  /**
   * The instance of the command that prints the error message for impossible
   * undo/redo commands.
   */
  public static final PrintingCommand IMPOSSIBLE_JOURNAL_COMMAND = new PrintingCommand("Impossible journal operation!");

  /**
   * The message to be printed when this command is applied.
   */
  private final String message;

  /**
   * Constructs a new command that prints the given string when applied.
   * 
   * @param message
   */
  private PrintingCommand(String message) {
    this.message = message;
  }

  @Override
  public SessionCommand applyToPlayer(ConsolePlayer player, ConsoleSession session) {
    player.printMessage(message);
    return null;
  }
}
