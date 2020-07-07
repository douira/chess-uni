package schach.interaction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

import schach.game.state.GameStatus;
import schach.interaction.Session.SessionStatus;

/**
 * Tests if the TurnStatus works properly
 *
 */
public class TurnStatusTest {
  
  /**
   * Tests if doesAffectBoth returns the right value
   */
  @Test
  public void testDoesAffectBoth() {
    assertFalse(TurnStatus.IN_CHECK.doesAffectBoth());
  }
  
  /**
   * Tests if the Status is correctly calculated
   */
  @Test
  public void testFromSessionData() {
    assertEquals(TurnStatus.fromSessionData(SessionStatus.FINISHED, GameStatus.IN_CHECKMATE, true),TurnStatus.LOST);
    assertEquals(TurnStatus.fromSessionData(SessionStatus.FINISHED, GameStatus.IN_CHECKMATE, false),TurnStatus.WON);
    assertEquals(TurnStatus.fromSessionData(SessionStatus.FINISHED, GameStatus.DRAW, true),TurnStatus.DRAW);
    assertEquals(TurnStatus.fromSessionData(SessionStatus.RUNNING, GameStatus.IN_CHECK, false),TurnStatus.GIVING_CHECK);
    assertEquals(TurnStatus.fromSessionData(SessionStatus.RUNNING, GameStatus.IN_CHECK, true),TurnStatus.IN_CHECK);  
  }
}
