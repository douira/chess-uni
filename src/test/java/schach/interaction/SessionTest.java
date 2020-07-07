package schach.interaction;

import java.util.Map;

import schach.common.Color;
import schach.game.moves.Move;
import schach.game.moves.Movement;
import schach.game.state.GameState;
import schach.interaction.Session.SessionStatus;
import schach.interaction.commands.MoveCommand;
import schach.interaction.commands.UndoCommand;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests that the session works correctly.
 */
public class SessionTest {
  private GameState gameState;
  private Player whitePlayer;
  private Player blackPlayer;
  private Session session;

  /**
   * Inits a session with fake players for use in the tests
   */
  @BeforeEach
  public void initSession() {
    gameState = new GameState();
    whitePlayer = new FakePlayer(true);
    blackPlayer = new FakePlayer(false);
    session = new Session();
    session.start(gameState, whitePlayer, blackPlayer);
  }

  /**
   * Tests the getter of Session
   */
  @Test
  public void testGetters() {
    assertEquals(gameState, session.getGame());
    assertEquals(Map.of(Color.WHITE, whitePlayer, Color.BLACK, blackPlayer), session.getPlayers());
    assertEquals(SessionStatus.RUNNING, session.getStatus());
    assertEquals(whitePlayer, session.getActivePlayer());
  }

  /**
   * Tests that the last command is returned correctly
   */
  @Test
  public void testGetLastCommand() {
    assertNull(session.getLastCommand());
    session.executeTurn();
    assertTrue(session.getLastCommand() instanceof MoveCommand);
  }

  /**
   * Tests that aborting the game works.
   */
  @Test
  public void testAbortGame() {
    assertEquals(SessionStatus.RUNNING, session.getStatus());
    session.stopGame();
    assertEquals(SessionStatus.STOPPED, session.getStatus());
    assertFalse(session.executeTurn());
  }

  /**
   * Tests that moves that determines to be accessible correctly.
   */
  @Test
  public void testCheckMoveAccessible() {
    Move move = new Movement(0, 0, 0, 0);
    move.setMoveIndex(1);
    assertTrue(session.checkMoveAccessible(move));

    move.setMoveIndex(2);
    assertFalse(session.checkMoveAccessible(move));
  }

  /**
   * Tests that commands are supplied correctly.
   */
  @Test
  public void testSupplyCommand() {
    session.supplyCommand(whitePlayer, UndoCommand.INSTANCE);
    session.executeTurn();
    assertEquals(session.getLastCommand(), UndoCommand.INSTANCE);
  }

  /**
   * Tests the behavior of execute turn.
   */
  @Test
  public void testExecuteTurn() {
    session.stopGame();
    assertFalse(session.executeTurn());

    Session localSession = new Session();
    assertThrows(IllegalStateException.class, () -> localSession.executeTurn());
  }
}
