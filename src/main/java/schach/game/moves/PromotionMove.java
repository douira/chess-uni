package schach.game.moves;

import schach.common.Color;
import schach.common.Position;
import schach.game.pieces.BishopPiece;
import schach.game.pieces.KnightPiece;
import schach.game.pieces.PawnPiece;
import schach.game.pieces.Piece;
import schach.game.pieces.QueenPiece;
import schach.game.pieces.RookPiece;
import schach.game.state.Board;

/**
 * A promotion move encapsulates another move because there are different types
 * of promotions moves (both capturing and non-capturing). This is not being
 * implemented as a double move since there aren't two distinct and
 * non-hierarchical moves.
 */
public class PromotionMove extends Move {
  private final Movement baseMove;

  /**
   * As opposed to a regular move, a promotion move needs to be given a move type
   * so we know to what piece the pawn is being promoted. The base move can be any
   * kind of movement which also includes capturing movements.
   * 
   * @param baseMove Underlying move of this promotion
   * @param moveType Move type that determines the type of promotion
   */
  public PromotionMove(Movement baseMove, MoveType moveType) {
    // make sure this is a promotion move type, this should never be violated though
    // since we control the construction of instances
    if (!moveType.isPromotion()) {
      throw new IllegalArgumentException("Cannot construct a pawn promotion move with the move type " + moveType
          + " since it's not a pawn promotion move type.");
    }
    this.moveType = moveType;
    this.baseMove = baseMove;
  }

  @Override
  public boolean fulfillsMovement(Movement movement) {
    // require the promotion type to match
    return super.fulfillsMovement(movement) && movement.hasMoveType(moveType);
  }

  @Override
  public Position getOriginPosition() {
    return baseMove.getOriginPosition();
  }

  @Override
  public Position getTargetPosition() {
    return baseMove.getTargetPosition();
  }

  @Override
  public boolean isAttacking(Position checkPosition) {
    return baseMove.isAttacking(checkPosition);
  }

  /**
   * Applies this method to the game state by replacing the position of the
   * original piece with a new piece instance of the desired type.
   */
  @Override
  public void applyTo(Board board) {
    baseMove.applyTo(board);

    // make sure the targeted piece is actually a pawn we can promote
    Position targetPosition = baseMove.getToPosition();
    Piece targetPiece = board.getPieceAt(targetPosition);
    if (!(targetPiece instanceof PawnPiece)) {
      throw new IllegalStateException("The promotion of the piece at " + targetPosition
          + " is illegal since there is no piece there or the present piece is not a pawn.");
    }

    // generate a new piece instance of the specified type with the same color
    Color color = board.getPieceAt(targetPosition).getColor();
    Piece newPiece;
    switch (moveType) {
      case PROMOTION_QUEEN:
        newPiece = new QueenPiece(color);
        break;
      case PROMOTION_ROOK:
        newPiece = new RookPiece(color);
        break;
      case PROMOTION_BISHOP:
        newPiece = new BishopPiece(color);
        break;
      case PROMOTION_KNIGHT:
        newPiece = new KnightPiece(color);
        break;
      default:
        // this never happens
        return;
    }

    board.placeNewPiece(targetPosition, newPiece);
  }

  @Override
  public void reverseOn(Board board) {
    // reverse the promotion using the stored replaced piece in the promoted piece
    Position promotionPosition = baseMove.getToPosition();
    Piece promotedPiece = board.getPieceAt(promotionPosition);
    Piece originalPiece = promotedPiece.getReplacedPiece();
    promotedPiece.setReplacedPiece(null);
    board.removePiece(promotionPosition);
    board.placeNewPiece(promotionPosition, originalPiece);

    baseMove.reverseOn(board);
  }
}
