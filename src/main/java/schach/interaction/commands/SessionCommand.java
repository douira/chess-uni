package schach.interaction.commands;

import schach.interaction.Session;

/**
 * A session command contains the behavior a session should execute in a turn.
 */
public interface SessionCommand {
  /**
   * Applies the session command to the given game state.
   * 
   * @param session Session to apply the session command to
   */
  void applyTo(Session session);

  /**
   * If this command is allowed to be generated by an inactive player.
   * 
   * @return True if this command is allowed to be generated by an inactive player
   */
  boolean allowedFromInactivePlayer();
}
