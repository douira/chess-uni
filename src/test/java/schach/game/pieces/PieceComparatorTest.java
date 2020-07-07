package schach.game.pieces;

import org.junit.jupiter.api.Test;

import schach.common.Color;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests that the piece comparator works correctly.
 */
public class PieceComparatorTest {
  PieceComparator pieceComparator = new PieceComparator();

  /**
   * Tests if the compare function works as expected
   */
  @Test
  public void testCompare() {
    Piece piece = new KnightPiece(Color.BLACK);
    assertEquals(0, pieceComparator.compare(piece, piece));
    assertEquals(1, pieceComparator.compare(new KnightPiece(Color.BLACK), new KnightPiece(Color.WHITE)));
    assertEquals(-1, pieceComparator.compare(new KnightPiece(Color.WHITE), new KnightPiece(Color.BLACK)));
    assertEquals(-1, pieceComparator.compare(new QueenPiece(Color.WHITE), new KnightPiece(Color.WHITE)));
    assertEquals(1, pieceComparator.compare(new KnightPiece(Color.WHITE), new QueenPiece(Color.WHITE)));
  }
}
