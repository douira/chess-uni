package schach.common;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests that the named thread factory works correctly.
 */
public class NamedThreadFactoryTest {
  /**
   * Tests that threads are creates with the correct name.
   */
  @Test
  public void testNewThread() {
    NamedThreadFactory factory1 = new NamedThreadFactory("test name");
    NamedThreadFactory factory2 = new NamedThreadFactory("");
    Runnable noop = () -> {
    };
    assertEquals("test name-0", factory1.newThread(noop).getName());
    assertEquals("test name-1", factory1.newThread(noop).getName());
    assertEquals("-0", factory2.newThread(noop).getName());
    assertEquals("-1", factory2.newThread(noop).getName());
  }
}
