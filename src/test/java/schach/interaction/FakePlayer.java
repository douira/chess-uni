package schach.interaction;

import schach.interaction.commands.MoveCommand;

/**
 * A Fake player for testing the session class
 */
public class FakePlayer extends Player {
  /**
   * Constructs a new fake player with the given accessible flag.
   * 
   * @param accessible True if this player should be accessible
   */
  public FakePlayer(boolean accessible) {
    setAccessible(accessible);
  }

  /**
   * By default the fake player is accessible.
   */
  public FakePlayer() {
    this(true);
  }

  @Override
  public void notifyStatus(TurnStatus status) {
  }

  @Override
  public void requestCommand() {
    if (isActivePlayer()) {
      supplyCommand(new MoveCommand(session.getGame().getAllLegalMoves().get(0)));
    }
  }

  @Override
  public void abortCommandRequest() {
    // does nothing
  }
}
