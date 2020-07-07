package schach.consoleui.commands;

import schach.consoleui.ConsolePlayer;
import schach.consoleui.ConsoleSession;
import schach.interaction.commands.JournalStepCommand;
import schach.interaction.commands.RedoCommand;
import schach.interaction.commands.SessionCommand;
import schach.interaction.commands.UndoCommand;

/**
 * A journal console command is always a journal step command. This command
 * produces a journal command if doing so is possible.
 */
public class ConsoleJournalCommand implements ConsoleCommand {
  /**
   * Static instance of the journal command that does an undo step.
   */
  public static final ConsoleJournalCommand UNDO = new ConsoleJournalCommand(true);

  /**
   * Static instance of the journal command that does a redo step.
   */
  public static final ConsoleJournalCommand REDO = new ConsoleJournalCommand(false);

  private final boolean undo;

  /**
   * Creates a new journal command with the given step direction.
   * 
   * @param undo True if the command should undo
   */
  private ConsoleJournalCommand(boolean undo) {
    this.undo = undo;
  }

  /**
   * Returns a session command if possible but applies an error printing command
   * if the operation cannot be executed.
   */
  @Override
  public SessionCommand applyToPlayer(ConsolePlayer player, ConsoleSession session) {
    JournalStepCommand stepCommand = undo ? UndoCommand.INSTANCE : RedoCommand.INSTANCE;
    if (stepCommand.canApplyTo(session)) {
      return stepCommand;
    } else {
      return PrintingCommand.IMPOSSIBLE_JOURNAL_COMMAND.applyToPlayer(player, session);
    }
  }
}
