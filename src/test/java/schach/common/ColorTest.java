package schach.common;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test the behavior of the color enum
 */
public class ColorTest {
  /**
   * Tests the pretty printing
   */
  @Test
  public void testGetPrettyName() {
    assertEquals("White", Color.WHITE.getPrettyName());
    assertEquals("Black", Color.BLACK.getPrettyName());
  }

  /**
   * Tests the index of the colors
   */
  @Test
  public void testGetIndex() {
    int index = 0;
    for (Color color : Color.values()) {
      assertEquals(index++, color.getIndex());
    }
  }

  /**
   * Test getting the opposing color
   */
  @Test
  public void testGetOpposing() {
    assertEquals(Color.WHITE, Color.BLACK.getOpposing());
    assertEquals(Color.BLACK, Color.WHITE.getOpposing());
  }

  /**
   * Tests that the case conversion happens correctly for the different colors.
   */
  @Test
  public void testToColorCase() {
    assertEquals("TEST", Color.WHITE.toColorCase("teST"));
    assertEquals("test", Color.BLACK.toColorCase("teST"));
  }
}
