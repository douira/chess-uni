package schach.game.accumulators;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for AcceptAccumulator class
 */
public class AcceptAccumulatorTest {
  /**
   * Test that the AcceptAccumulator accepts the generated moves.
   */
  @Test
  public void testGenerateNonAttacking() {
    AcceptAccumulator accumulator = new AcceptAccumulator();
    assertTrue(accumulator.generateNonAttacking());
  }
}
