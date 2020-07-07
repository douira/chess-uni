package schach.common;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the behavior of positions
 */
public class PositionTest {
  Position offTheBoardYPlus = new Position(3, 11);
  Position offTheBoardXPlus = new Position(9, 5);
  Position offTheBoardYMinus = new Position(2, -3);
  Position offTheBoardXMinus = new Position(-800, 11);
  Position origin = new Position(1, 1);
  Position originSame = new Position(1, 1);
  Position notOrigin = new Position(2, 3);
  Vector similarVector = new Vector(1, 1);
  Vector offset = new Vector(3, 4);
  int index = 46;

  /**
   * Test that the correct positions are generated from an offsetted position
   */
  @Test
  public void testFromOffset() {
    Position position = Position.fromOffset(origin, offset);
    assertEquals(4, position.getX());
    assertEquals(5, position.getY());
  }

  /**
   * Test that the correct positions are generated from board indexes
   */
  @Test
  public void testFromBoardIndex() {
    Position position = Position.fromBoardIndex(index);
    assertEquals(6, position.getX());
    assertEquals(5, position.getY());
  }

  /**
   * Test the calculation of the board index
   */
  @Test
  public void testGetBoardIndex() {
    assertEquals(35, Position.getBoardIndex(3, 4));
    assertEquals(9, origin.getBoardIndex());
  }

  /**
   * Test that positions are seen as out of bounds.
   */
  @Test
  public void testOutOfBounds() {
    assertTrue(offTheBoardYPlus.outOfBounds());
    assertTrue(offTheBoardYMinus.outOfBounds());
    assertTrue(offTheBoardXPlus.outOfBounds());
    assertTrue(offTheBoardXMinus.outOfBounds());
    assertFalse(origin.outOfBounds());
  }

  /**
   * Test that identical or equal positions are seen as equal.
   */
  @Test
  public void testEquals() {
    assertEquals(origin, origin);
    assertEquals(origin, originSame);
    assertEquals(originSame, origin);
    assertNotEquals(origin, notOrigin);
    assertNotEquals(origin, similarVector);
  }

  /**
   * Test that the hash code of identical objects is the same
   */
  @Test
  public void testHashCode() {
    assertEquals(origin.hashCode(), originSame.hashCode());
  }

  /**
   * Test that the correct strings are generated
   */
  @Test
  public void testToString() {
    assertEquals("(1,1)", origin.toString());
  }
}
