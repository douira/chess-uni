package schach.game.moves;

import schach.common.Position;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the CapturingMove class.
 */
public class CapturingMoveTest {
  /**
   * Test that the hash code of identical objects is the same
   */
  @Test
  public void testHashCode() {
    assertEquals(new CapturingMove(new Position(0, 1), new Position(0, 2)).hashCode(),
        new CapturingMove(new Position(0, 1), new Position(0, 2)).hashCode());
    assertEquals(new CapturingMove(new Position(5, 6), new Position(3, 1)).hashCode(),
        new CapturingMove(new Position(5, 6), new Position(3, 1)).hashCode());
  }

  /**
   * Tests that 'equals' correctly identifies the same move and returns false if
   * the move is not the same.
   */
  @Test
  public void testEquals() {
    CapturingMove capturingMove = new CapturingMove(new Position(0, 1), new Position(0, 2));
    CapturingMove otherMove = new CapturingMove(new Position(0, 2), new Position(0, 3));

    assertEquals(capturingMove, capturingMove);
    assertNotEquals(capturingMove, otherMove);
    assertNotEquals(capturingMove, null);
  }
}
