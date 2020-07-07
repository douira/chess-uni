package schach.game.moves;

import java.util.EnumSet;
import java.util.Set;

/**
 * Holds all the different types of special moves that can be made. This enum is
 * only used when it's required by some game logic to check the type of the
 * move.
 */
public enum MoveType {
  UNSPECIFIED,

  PAWN_DOUBLE, PAWN_SIMPLE,

  PROMOTION_QUEEN, PROMOTION_ROOK, PROMOTION_BISHOP, PROMOTION_KNIGHT;

  /**
   * A set of the promotion moves so we can iterate over all possible promotion
   * moves.
   */
  public static final Set<MoveType> PROMOTION_TYPES = EnumSet.of(PROMOTION_QUEEN, PROMOTION_ROOK, PROMOTION_BISHOP,
      PROMOTION_KNIGHT);

  /**
   * Returns if this move type is a pawn promotion type. Pawn promotion types can
   * be used in PromotionMove.
   * 
   * @return If this move type is a pawn promotion type
   */
  public boolean isPromotion() {
    return PROMOTION_TYPES.contains(this);
  }
}
