package schach.game.moves;

import java.lang.reflect.Field;

import schach.common.Color;
import schach.common.Position;
import schach.game.pieces.Piece;
import schach.game.pieces.PieceType;
import schach.game.state.Board;
import schach.game.state.GameState;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
//NOPMD is required here to suppress false positives
import static schach.game.GameTestUtils.*; //NOPMD

/**
 * Tests for the DoubleMove class.
 */
public class PromotionMoveTest {

  /**
   * Tests that the promotion move is a correct movement.
   */
  @Test
  public void testFulfillsMovement() {
    Movement baseMove = new Movement(new Position(0, 1), new Position(0, 2));
    PromotionMove promotion = new PromotionMove(baseMove, MoveType.PROMOTION_KNIGHT);

    // test 1: expect false, since the movement type is unspecified
    assertFalse(promotion.fulfillsMovement(baseMove));

    // test 2: expect true, since the movement type is a promotion type
    baseMove.setMoveType(MoveType.PROMOTION_KNIGHT);
    assertTrue(promotion.fulfillsMovement(baseMove));
  }

  /**
   * Tests that if the promotion move is attacking a position, then isAttacking
   * returns true; otherwise false.
   */
  @Test
  public void testIsAttacking() {
    CapturingMove movement = new CapturingMove(new Position(0, 1), new Position(0, 2));
    PromotionMove promotion = new PromotionMove(movement, MoveType.PROMOTION_KNIGHT);

    // test 1: expects the move to be attacking
    assertTrue(promotion.isAttacking(new Position(0, 2)));

    // test 2: expects the move to not be attacking
    assertFalse(promotion.isAttacking(new Position(5, 6)));
  }

  /**
   * Asserts the type of the promotion and the fact that the promotion worked
   * correctly.
   * 
   * @param from      Origin position
   * @param to        Target position for the promotion
   * @param moveType  Type of the move to make
   * @param pieceType Type of the piece to expect after the promotion
   */
  private static void testPromotionMove(Position from, Position to, MoveType moveType, PieceType pieceType) {
    GameState game = new GameState();
    Board board = game.getBoard();
    new PromotionMove(new Movement(from, to), moveType).applyTo(board);
    Piece promoted = game.getPieceAt(to);
    assertPiece(promoted, Color.WHITE, pieceType);
  }

  /**
   * Test that applying the promotion to the game state results in a correct
   * promotion.
   */
  @Test
  public void testApplyTo() {
    // test applying normal promotions
    testPromotionMove(new Position(0, 6), new Position(0, 7), MoveType.PROMOTION_QUEEN, PieceType.QUEEN);
    testPromotionMove(new Position(1, 6), new Position(1, 7), MoveType.PROMOTION_ROOK, PieceType.ROOK);
    testPromotionMove(new Position(2, 6), new Position(2, 7), MoveType.PROMOTION_BISHOP, PieceType.BISHOP);
    testPromotionMove(new Position(3, 6), new Position(3, 7), MoveType.PROMOTION_KNIGHT, PieceType.KNIGHT);

    // test invaid move type
    GameState game = new GameState();
    Board board = game.getBoard();
    PromotionMove broken = new PromotionMove(new Movement(0, 6, 0, 7), MoveType.PROMOTION_QUEEN);
    assertDoesNotThrow(() -> {
      Field moveTypeField = Move.class.getDeclaredField("moveType");
      moveTypeField.setAccessible(true);
      moveTypeField.set(broken, MoveType.UNSPECIFIED);
    });
    broken.applyTo(board);
    Piece notMovedPiece = game.getPieceAt(0, 7);
    assertPiece(notMovedPiece, Color.WHITE, PieceType.PAWN);

    // test throw on not a pawn
    GameState game2 = new GameState();
    Board board2 = game2.getBoard();
    assertThrows(IllegalStateException.class,
        () -> new PromotionMove(new Movement(0, 7, 0, 7), MoveType.PROMOTION_BISHOP).applyTo(board2));

    // test throw on bad move type
    assertThrows(IllegalArgumentException.class,
        () -> new PromotionMove(new Movement(4, 6, 4, 7), MoveType.UNSPECIFIED));
  }
}
