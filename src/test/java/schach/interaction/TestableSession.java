package schach.interaction;

import schach.game.state.GameState;

/**
 * Session that has public methods for testing
 */
public class TestableSession extends Session {
  @Override
  public void start(GameState game, Player whitePlayer, Player blackPlayer) {
    super.start(game, whitePlayer, blackPlayer);
  }

  /**
   * Starts a new session but with fake players
   */
  public void startFake() {
    start(new GameState(), new FakePlayer(), new FakePlayer());
  }
}
