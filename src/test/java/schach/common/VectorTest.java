package schach.common;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests that vectors behave correctly
 */
public class VectorTest {
  Vector vector = new Vector(3, 4);

  /**
   * Tests that the correct x coordinate is returned
   */
  @Test
  public void testGetX() {
    assertEquals(3, vector.getX());
  }

  /**
   * Tests that the correct y coordinate is returned
   */
  @Test
  public void testGetY() {
    assertEquals(4, vector.getY());
  }

  /**
   * Tests that vector is correctly flipped vertically
   */
  @Test
  public void testFlipVertically() {
    assertEquals(3, vector.flipVertically().getX());
    assertEquals(-4, vector.flipVertically().getY());
  }

  /**
   * Tests that vector is correctly flipped horizontally
   */
  @Test
  public void testFlipHorizontally() {
    assertEquals(-3, vector.flipHorizontally().getX());
    assertEquals(4, vector.flipHorizontally().getY());
  }
}
