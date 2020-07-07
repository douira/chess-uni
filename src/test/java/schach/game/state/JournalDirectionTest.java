package schach.game.state;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test that the journal direction enum works correctly.
 */
public class JournalDirectionTest {
  /**
   * Tests that the construction works.
   */
  @Test
  public void testConstruction() {
    assertEquals("REDO", JournalDirection.REDO.name());
    assertEquals("UNDO", JournalDirection.UNDO.name());
    assertEquals(2, JournalDirection.values().length);
  }
}
