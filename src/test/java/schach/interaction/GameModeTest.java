package schach.interaction;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import schach.common.Color;

/**
 * Tests the behavior of the game mode enum
 */
public class GameModeTest {
  /**
   * Test that list entries are produced
   * correctly.EmptyMethodInAbstractClassShouldBeAbstract
   */
  @Test
  public void testToListEntry() {
    assertEquals("[W-C] Human playing white vs. Computer", GameMode.WHITE_V_AI.toListEntry());
    assertEquals("[B-C] Human playing black vs. Computer", GameMode.BLACK_V_AI.toListEntry());
    assertEquals("[H-H] Human vs. Human", GameMode.HUMANS.toListEntry());
  }

  /**
   * Used for implementing mock player classes. This is left empty intentionally
   * because it's a mock class but we still need to be able to make an instance.
   * This one mocks an ai player.
   */
  private static class MockAIPlayer extends Player {
    @Override
    public void notifyStatus(TurnStatus status) {
      // does nothing
    }

    @Override
    public void requestCommand() {
      // does nothing
    }

    @Override
    public void abortCommandRequest() {
      // does nothing
    }
  }

  /**
   * The second mock plyer class used for identifying the game mode using human
   * players.
   */
  private static class MockHumanPlayer extends Player {
    @Override
    public void notifyStatus(TurnStatus status) {
      // does nothing
    }

    @Override
    public void requestCommand() {
      // does nothing
    }

    @Override
    public void abortCommandRequest() {
      // does nothing
    }
  }

  /**
   * Tests that the game mode starts the session with the correct players.
   * 
   * @param mode        Game mode to test
   * @param whitePlayer Class of the white player to assert
   * @param blackPlayer Class of the black player to assert
   */
  private static void assertStartedWith(GameMode mode, Class<? extends Player> whitePlayer,
      Class<? extends Player> blackPlayer) {
    Session session = new Session();
    mode.startSession(session, () -> new MockAIPlayer(), () -> new MockHumanPlayer());
    assertEquals(whitePlayer, session.getPlayerFor(Color.WHITE).getClass());
    assertEquals(blackPlayer, session.getPlayerFor(Color.BLACK).getClass());
  }

  /**
   * Tests that sessions are started with the right players.
   */
  @Test
  public void testStartSession() {
    assertStartedWith(GameMode.WHITE_V_AI, MockHumanPlayer.class, MockAIPlayer.class);
    assertStartedWith(GameMode.BLACK_V_AI, MockAIPlayer.class, MockHumanPlayer.class);
    assertStartedWith(GameMode.HUMANS, MockHumanPlayer.class, MockHumanPlayer.class);
  }
}
