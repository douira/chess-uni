package schach.game.pieces;

import schach.common.Color;
import schach.game.state.GameState;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
//NOPMD is required here to suppress false positives
import static schach.game.GameTestUtils.*; //NOPMD

/**
 * Tests the correct behavior of the pawn piece.
 */
public class PawnPieceTest {
  PawnPiece pawn = new PawnPiece(Color.WHITE);

  /**
   * Tests if the pawn value is set correctly
   */
  @Test
  public void testGetOrdinal() {
    assertEquals(5, pawn.getOrdinal());
  }

  /**
   * Tests if enPassant is implemented correctly
   */
  @Test
  public void testAccumulateEnPassant() {
    GameState game = gameFromMoves("d2-d4", "e7-e5", "d4-d5", "c7-c5");

    // Test if EnPassant is allowed at the correct time
    assertValidMove(game, "d5-c6");

    // Test if EnPassant is forbidden after a move has passed since the opponent
    // appeared
    assertInvalidMove(game, "d5-e6");
  }

  /**
   * Tests if promotions are implemented correctly
   */
  @Test
  public void testAccumulatePromotions() {
    GameState game = gameFromMoves("d2-d4", "e7-e5", "d4-d5", "c7-c5", "d5-c6", "b7-b5", "c6-c7", "c8-a6");

    // Tests Capturing Promotion to the Right
    assertValidMove(game, "c7-d8");

    // Tests Capturing Promotion to the Left
    assertValidMove(game, "c7-b8");

    // Tests Normal Promotions
    assertValidMove(game, "c7-c8Q", "c7-c8R", "c7-c8B", "c7-c8N");

    // test black promotion
    applyMoves(game, "d1-d4", "e5-d4", "e2-e4", "d4-d3", "e4-e5", "d3-c2", "c1-h6");
    assertValidMove(game, "c2-c1");
  }

  /**
   * Tests if moves that don't capture are implemented correctly
   */
  @Test
  public void testAccumulateNonCapturing() {
    // Test if doubleMove is blocked by bishop
    assertInvalidMove(gameFromMoves("d2-d4", "e7-e5", "c1-h6"), "h7-h5");
  }

  /**
   * Tests if shortName is returned correctly
   */
  @Test
  public void testGetShortName() {
    assertEquals(pawn.getShortName(), "p");
  }

  /**
   * Tests if color corrected sign is returned correctly
   */
  @Test
  public void testGetPieceSign() {
    assertEquals(pawn.getPieceSign(), "P");
  }

  /**
   * Tests if the Symbol is returned correctly
   */
  @Test
  public void testGetSymbol() {
    assertEquals(pawn.getSymbol(), "♙");
  }

  /**
   * Tests if the piece can castle
   */
  @Test
  public void testCanCastle() {
    assertFalse(pawn.canCastle(null));
  }
}
