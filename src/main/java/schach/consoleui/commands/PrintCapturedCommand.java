package schach.consoleui.commands;

import schach.consoleui.ConsolePlayer;
import schach.consoleui.ConsoleSession;
import schach.interaction.commands.SessionCommand;

/**
 * Command that prints the capturd pieces of the given board.
 */
public class PrintCapturedCommand implements ConsoleCommand {
  /**
   * The static instance of this command since it has no own state.
   */
  public static final PrintCapturedCommand INSTANCE = new PrintCapturedCommand();

  /**
   * Prevent public construction since we do it ourselves.
   */
  private PrintCapturedCommand() {
  }

  @Override
  public SessionCommand applyToPlayer(ConsolePlayer player, ConsoleSession session) {
    session.printCapturedPieces();
    return null;
  }
}
