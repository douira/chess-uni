package schach.interaction.commands;

import schach.interaction.Session;

/**
 * The abort game command tells the session to stop all calculations.
 */
public class AbortGameCommand implements SessionCommand {
  @Override
  public void applyTo(Session session) {
    session.stopGame();
  }

  @Override
  public boolean allowedFromInactivePlayer() {
    return true;
  }
}
