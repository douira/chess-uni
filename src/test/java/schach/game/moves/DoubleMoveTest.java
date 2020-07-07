package schach.game.moves;

import java.util.Set;

import schach.common.Color;
import schach.common.Position;
import schach.game.state.GameState;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the DoubleMove class.
 */
public class DoubleMoveTest {
  Movement movement1 = new Movement(new Position(0, 1), new Position(0, 2));
  Movement movement2 = new Movement(new Position(0, 2), new Position(0, 3));
  Movement movement3 = new Movement(new Position(0, 1), new Position(0, 3));
  DoubleMove doubleMove = new DoubleMove(movement1, movement2, movement3.getFromPosition(), movement3.getToPosition());
  DoubleMove doubleMove2 = new DoubleMove(movement1, movement2, movement3.getFromPosition(), movement3.getToPosition());

  /**
   * Tests that the given movement is a correct double move.
   */
  @Test
  public void testFulfillsMovement() {
    assertTrue(doubleMove.fulfillsMovement(movement3));
  }

  /**
   * Tests if the given fulfilling movement is valid.
   */
  @Test
  public void testGetFulfillingMovement() {
    Movement fulfilled = doubleMove.getFulfillingMovement();
    assertTrue(doubleMove.fulfillsMovement(fulfilled));
    assertNotEquals(doubleMove, fulfilled);
  }

  /**
   * Tests that if the double move is attacking a position, then isAttacking
   * returns true; otherwise false.
   */
  @Test
  public void testIsAttacking() {
    CapturingMove capturingMove = new CapturingMove(new Position(7, 3), new Position(0, 3));
    DoubleMove attackMove = new DoubleMove(movement1, capturingMove);

    // test 1: expects the move to be attacking (either second or first)
    assertTrue(attackMove.isAttacking(new Position(0, 3)));
    assertTrue(new DoubleMove(capturingMove, movement1).isAttacking(new Position(0, 3)));

    // test 2: expects the move to not be attacking
    assertFalse(attackMove.isAttacking(new Position(5, 6)));
  }

  /**
   * Tests that applyTo applies both moves to the game state correctly.
   */
  @Test
  public void testApplyTo() {
    GameState game = new GameState();

    doubleMove.applyTo(game.getBoard());
    assertEquals("Pawn", game.getPieceAt(0, 3).getFullName());
    assertEquals(Color.BLACK, game.getPieceAt(0, 3).getColor());
  }

  /**
   * Tests the correct conversion of a double move to a string.
   */
  @Test
  public void testToString() {
    assertEquals("(0,1)->(0,2)+(0,2)->(0,3) [(0,1)->(0,2)]",
        new DoubleMove(new Movement(new Position(0, 1), new Position(0, 2)),
            new Movement(new Position(0, 2), new Position(0, 3))).toString());
  }

  /**
   * Test that identical objects have the same hash code.
   */
  @Test
  public void testHashCode() {
    assertEquals(doubleMove.hashCode(), doubleMove2.hashCode());
  }

  /**
   * Tests that 'equals' correctly identifies the same double move and returns
   * false if the double move is not the same.
   */
  @Test
  public void testEquals() {
    Movement move1 = new Movement(new Position(0, 1), new Position(0, 2));
    Movement move2 = new Movement(new Position(0, 2), new Position(0, 3));
    DoubleMove doubleMove = new DoubleMove(move1, move2);

    assertEquals(doubleMove, doubleMove);
    assertEquals(doubleMove, new DoubleMove(move1, move2));

    // test that it doesn't equal null, this is different than assertNotNull
    assertNotEquals(doubleMove, null);

    // Test different invalid moves
    Set<DoubleMove> notEqualMoves = Set.of(new DoubleMove(move1, move2, new Position(0, 3), new Position(0, 3)),
        new DoubleMove(move1, move2, new Position(0, 1), new Position(0, 4)),
        new DoubleMove(move2, move2, new Position(0, 1), new Position(0, 2)),
        new DoubleMove(move1, move1, new Position(0, 1), new Position(0, 2)));
    for (DoubleMove move : notEqualMoves) {
      assertNotEquals(move, doubleMove);
    }
  }
}
