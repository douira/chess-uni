package schach.common;

import java.time.Duration;
import java.util.List;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the Util class.
 */
public class UtilsTest {
  /**
   * Tests if two collections are concatenated properly.
   */
  @Test
  public void testConcatCollections() {
    assertEquals(Utils.concatCollections(List.of("1", "2", "3"), List.of("4", "5", "6")),
        List.of("1", "2", "3", "4", "5", "6"));
  }

  private void waitOne() {
    try {
      Thread.sleep(1);
    } catch (InterruptedException e) {
    }
  }

  /**
   * Tests that waitUntil works as expected by syncing on an object, interrupting
   * and then also fulfilling the condition.
   */
  @Test
  public void testWaitUntil() {
    assertTimeout(Duration.ofSeconds(1), () -> {
      // make a thread with a state holder object (this array)
      boolean[] state = { false, false };
      Thread thread = new Thread(() -> {
        Utils.waitUntil(state, (sync) -> sync[0]);
        synchronized (state) {
          state[1] = true;
        }
      });
      thread.start();

      // check that it waits
      waitOne();
      synchronized (state) {
        assertFalse(state[1]);
      }

      // test that interrupting it without fulfilling the condition doesn't stop it
      thread.interrupt();
      waitOne();
      synchronized (state) {
        assertFalse(state[1]);
        state[0] = true;
        state.notify();
      }

      // wait for it to get out of the waiting state
      try {
        thread.join();
      } catch (InterruptedException e) {
      }
      assertTrue(state[1]);
    });
  }

  /**
   * Tests that double zeros are correctly normalized.
   */
  @Test
  public void testNormalizeZero() {
    assertEquals(0d, Utils.normalizeZero(-0d));
    assertEquals(0d, Utils.normalizeZero(0d));
    assertEquals(40d, Utils.normalizeZero(40d));
    assertEquals(-42d, Utils.normalizeZero(-42d));
  }
}
