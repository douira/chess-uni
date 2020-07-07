package schach.game.pieces;

import schach.common.Color;
import schach.common.Position;
import schach.game.state.Board;
import schach.game.state.GameState;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
//NOPMD is required here to suppress false positives
import static schach.game.GameTestUtils.*; //NOPMD

/**
 * Tests the behavior of the king piece
 */
public class KingPieceTest {
  KingPiece king = new KingPiece(Color.WHITE);

  /**
   * Tests if the king value is correct
   */
  @Test
  public void testGetOrdinal() {
    assertEquals(0, king.getOrdinal());
  }

  /**
   * Tests if castling is implemented correctly
   */
  @Test
  public void testAccumulateCastling() {
    // test queenside castling
    GameState game = gameFromMoves("d2-d4", "e7-e5", "d1-d3", "e5-e4", "c1-h6", "e8-e7", "b1-c3", "e7-e6");
    assertValidMove(game, "e1-c1");

    // Test castle is illegal when a piece in the way can be captured
    applyMoves(game, "c3-b5", "d8-g5", "d3-a3", "g5-g2", "a3-a7", "g2-g1", "e2-e3", "g1-g2", "f1-d3", "g7-h6");
    Position piecePosition = new Position(4, 7);
    Position pieceTarget = new Position(5, 7);
    assertTrue(game.getPieceAt(piecePosition).isAttackedAt(game, piecePosition, pieceTarget));
    assertInvalidMove(game, "e1-g1");

    // Tests that no castling is possible when rook has moved
    game = new GameState();
    Board board = game.getBoard();
    board.capturePiece(new Position(6, 7));
    board.capturePiece(new Position(5, 7));
    board.capturePiece(new Position(7, 6));
    applyMoves(game, "h1-h2", "a7-a6", "h2-h1", "a6-a5");
    assertInvalidMove(game, "e1-c1");

    // Tests that no castling is possible when rook has been captured
    board.capturePiece(new Position(7, 7));
    assertInvalidMove(game, "e1-g1");
  }

  /**
   * Tests if shortName is returned correctly
   */
  @Test
  public void testGetShortName() {
    assertEquals(king.getShortName(), "k");
  }

  /**
   * Tests if color corrected sign is returned correctly
   */
  @Test
  public void testGetPieceSign() {
    assertEquals(king.getPieceSign(), "K");
  }

  /**
   * Tests if the Symbol is returned correctly
   */
  @Test
  public void testGetSymbol() {
    assertEquals(king.getSymbol(), "♔");
  }
}
