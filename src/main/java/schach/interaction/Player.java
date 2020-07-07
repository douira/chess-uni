package schach.interaction;

import schach.common.Color;
import schach.interaction.commands.SessionCommand;

/**
 * A player is an abstract agent that reacts to the game state and returns the
 * move it chooses to make. A player can be implemented by the console
 * interface, the GUI or an AI module.
 */
public abstract class Player {
  protected Color color;
  protected Session session;
  private boolean accessible;

  public Color getColor() {
    return color;
  }

  /**
   * Sets up the player for use in the session by giving it a reference to the
   * session that the player can supply commands to and the color of the player in
   * the session.
   * 
   * @param session Session to supply commands to
   * @param color   Color of the player in the enclosing session
   */
  public void setupPlayer(Session session, Color color) {
    this.session = session;
    this.color = color;
  }

  /**
   * Sets the accessible flag on this player. Accessible players are controlled by
   * the user while inaccessible players are controlled by the computer, or at
   * least not the user using this program.
   * 
   * @param accessible If the player should be set as accessible
   */
  public void setAccessible(boolean accessible) {
    this.accessible = accessible;
  }

  /**
   * Returns if this player is accessible.
   * 
   * @return True if accessible
   */
  public boolean isAccessible() {
    return accessible;
  }

  /**
   * Determines if this player is the active player in the session.
   * 
   * @return True if the player is the active player
   */
  protected boolean isActivePlayer() {
    return session.isActivePlayer(this);
  }

  /**
   * This is called before each move (of any player) and after the game is over.
   * This doesn't mean it's this player's turn.
   * 
   * @param status Output status to display
   */
  public abstract void notifyStatus(TurnStatus status);

  /**
   * Requests the player to produce a command for the session. It's not
   * necessarily the player's turn when this method is called. Commands are
   * returned to the session through supplyCommand.
   */
  public abstract void requestCommand();

  /**
   * Instructs the player to not supply a command until annother command is
   * requested.
   */
  public abstract void abortCommandRequest();

  /**
   * Supplies a command to the parent session
   * 
   * @param command Command to pass to the parent
   */
  public void supplyCommand(SessionCommand command) {
    session.supplyCommand(this, command);
  }
}
