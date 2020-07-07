package schach.game.pieces;

import org.junit.jupiter.api.Test;

import schach.common.Color;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for KnightPiece class.
 */
public class KnightPieceTest {
  KnightPiece knight = new KnightPiece(Color.WHITE);

  /**
   * Tests the correct value.
   */
  @Test
  public void testGetOrdinal() {
    assertEquals(4, knight.getOrdinal());
  }
}
