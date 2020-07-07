package schach.game.pieces;

import org.junit.jupiter.api.Test;

import schach.common.Color;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the behavior of the queen piece
 */
public class QueenPieceTest {
  QueenPiece queen = new QueenPiece(Color.WHITE);

  /**
   * Test that the ordinal of the queen is correct
   */
  @Test
  public void testGetOrdinal() {
    assertEquals(1, queen.getOrdinal());
  }

  /**
   * Tests if shortName is returned correctly
   */
  @Test
  public void testGetShortName() {
    assertEquals(queen.getShortName(), "q");
  }

  /**
   * Tests if color corrected sign is returned correctly
   */
  @Test
  public void testGetPieceSign() {
    assertEquals(queen.getPieceSign(), "Q");
  }

  /**
   * Tests if the Symbol is returned correctly
   */
  @Test
  public void testGetSymbol() {
    assertEquals(queen.getSymbol(), "♕");
  }

  /**
   * Tests if the piece can castle
   */
  @Test
  public void testCanCastle() {
    assertFalse(queen.canCastle(null));
  }
}
