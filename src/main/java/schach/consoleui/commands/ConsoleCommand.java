package schach.consoleui.commands;

import schach.consoleui.ConsolePlayer;
import schach.consoleui.ConsoleSession;
import schach.interaction.commands.SessionCommand;

/**
 * A command that can be produced by the command reader.
 */
public interface ConsoleCommand {
  /**
   * Applies the console command to the given player. Context is given by the
   * player.
   * 
   * @param player  Player to apply the command to
   * @param session ConsoleSession to use for applying the command as well
   * @return Returns a session command if this command is actionable on the
   *         session, if null the player parses commands until a session command
   *         is produced
   */
  SessionCommand applyToPlayer(ConsolePlayer player, ConsoleSession session);
}
