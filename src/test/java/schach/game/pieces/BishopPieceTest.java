package schach.game.pieces;

import schach.common.Color;
import schach.common.Position;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the behavior of the bishop piece
 */
public class BishopPieceTest {
  BishopPiece bishop = new BishopPiece(Color.WHITE);

  /**
   * Tests the value of a bishop
   */
  @Test
  public void testGetOrdinal() {
    assertEquals(3, bishop.getOrdinal());
  }

  /**
   * Tests if the bishop color is calculated correctly
   */
  @Test
  public void testsnotifyPosition() {
    bishop.notifyPosition(new Position(2, 7));
    assertEquals(bishop.getSquareColor(), Color.BLACK);
    bishop.notifyPosition(new Position(5, 7));
    assertEquals(bishop.getSquareColor(), Color.WHITE);
  }

  /**
   * Tests if shortName is returned correctly
   */
  @Test
  public void testGetShortName() {
    assertEquals(bishop.getShortName(), "b");
  }

  /**
   * Tests if color corrected sign is returned correctly
   */
  @Test
  public void testGetPieceSign() {
    assertEquals(bishop.getPieceSign(), "B");
  }

  /**
   * Tests if the Symbol is returned correctly
   */
  @Test
  public void testGetSymbol() {
    assertEquals(bishop.getSymbol(), "♗");
  }

  /**
   * Tests if the piece can castle
   */
  @Test
  public void testCanCastle() {
    assertFalse(bishop.canCastle(null));
  }
}
