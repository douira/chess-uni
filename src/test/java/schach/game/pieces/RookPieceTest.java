package schach.game.pieces;

import org.junit.jupiter.api.Test;

import schach.common.Color;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the behavior of the rook piece
 */
public class RookPieceTest {

  RookPiece rook = new RookPiece(Color.WHITE);

  /**
   * Test that the value of the rook is correct
   */
  @Test
  public void testGetOrdinal() {
    assertEquals(2, rook.getOrdinal());
  }

  /**
   * Tests if shortName is returned correctly
   */
  @Test
  public void testGetShortName() {
    assertEquals(rook.getShortName(), "r");
  }

  /**
   * Tests if color corrected sign is returned correctly
   */
  @Test
  public void testGetPieceSign() {
    assertEquals(rook.getPieceSign(), "R");
  }

  /**
   * Tests if the Symbol is returned correctly
   */
  @Test
  public void testGetSymbol() {
    assertEquals(rook.getSymbol(), "♖");
  }
}
