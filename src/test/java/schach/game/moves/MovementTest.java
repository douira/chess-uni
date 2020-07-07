package schach.game.moves;

import schach.common.Color;
import schach.common.Position;
import schach.game.state.GameState;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the CapturingMove class.
 */
public class MovementTest {
  Position from1 = new Position(0, 1);
  Position to1 = new Position(0, 2);
  Movement movement1 = new Movement(from1, to1);
  Movement movement1Copy = new Movement(0, 1, 0, 2);
  Movement movement2 = new Movement(0, 2, 0, 3);

  /**
   * Tests that the authoring color detection works.
   */
  @Test
  public void testGetByColor() {
    Movement movement = new Movement(1, 1, 1, 1);
    movement.setMoveIndex(11);
    assertEquals(Color.WHITE, movement.getByColor());

    movement.setMoveIndex(20);
    assertEquals(Color.BLACK, movement.getByColor());

    movement.setMoveIndex(0);
    assertNull(movement.getByColor());
    movement.setMoveIndex(-6);
    assertNull(movement.getByColor());
  }

  /**
   * Tests that the fulfilling movement is generated correctly.
   */
  @Test
  public void testGetFulfillingMovement() {
    Movement fulfilled = movement1.getFulfillingMovement();
    assertTrue(movement1.fulfillsMovement(fulfilled));
    assertEquals(movement1, fulfilled);
  }

  /**
   * Test that the from position is correct
   */
  @Test
  public void testGetFromPosition() {
    assertEquals(movement1.getFromPosition(), from1);
  }

  /**
   * Test that the to position is correct
   */
  @Test
  public void testGetToPosition() {
    assertEquals(movement1.getToPosition(), to1);
  }

  /**
   * Tests that equal moves are seen as such
   */
  @Test
  public void testMovesEqual() {
    assertTrue(movement1.movesEqual(movement1Copy));
    assertFalse(movement1.movesEqual(movement2));
    assertFalse(new Movement(0, 1, 0, 2).movesEqual(new Movement(0, 1, 1, 2)));
    Movement almostMovement1 = new Movement(0, 1, 0, 2);
    almostMovement1.setMoveType(MoveType.PROMOTION_BISHOP);
    assertFalse(movement1.movesEqual(almostMovement1));
  }

  /**
   * Tests that the correct movements are fulfilled
   */
  @Test
  public void testFulfillsMovement() {
    assertTrue(movement1.fulfillsMovement(movement1));
    assertTrue(movement1.fulfillsMovement(movement1Copy));
    assertFalse(movement1.fulfillsMovement(movement2));
  }

  /**
   * Tests that a simple movement is not attacking
   */
  @Test
  public void testIsAttacking() {
    assertFalse(movement1.isAttacking(from1));
    assertFalse(movement1.isAttacking(to1));
  }

  /**
   * Tests that applying this movement to a game state has the desired change
   */
  @Test
  public void testApplyTo() {
    GameState game = new GameState();
    Movement movement = new Movement(0, 0, 4, 4);
    movement.applyTo(game.getBoard());
    assertNull(game.getPieceAt(0, 0));
    assertNotNull(game.getPieceAt(4, 4));
  }

  /**
   * Tests if the toString method converts movements into Strings.
   */
  @Test
  public void testToString() {
    assertEquals("(0,1)->(0,2)", movement1.toString());
    Movement movementKnight = new Movement(4, 5, 2, 0);
    movementKnight.setMoveType(MoveType.PROMOTION_KNIGHT);
    assertEquals("(4,5)->(2,0)PROMOTION_KNIGHT", movementKnight.toString());
  }

  /**
   * Tests that the hash code of identical movement is the same.
   */
  @Test
  public void testHashCode() {
    assertEquals(movement1.hashCode(), movement1Copy.hashCode());
  }

  /**
   * Tests if the equals method correctly identifies two equal movements and
   * returns false if the movements are not the same.
   */
  @Test
  public void testEquals() {
    assertEquals(movement1, movement1);
    assertNotEquals(movement1, movement2);
    assertNotEquals(movement1, null);
  }
}
