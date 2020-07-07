package schach.game.pieces;

import java.util.Set;

import schach.common.Color;
import schach.common.Position;
import schach.common.Vector;
import schach.game.accumulators.MoveAccumulator;
import schach.game.moves.DoubleMove;
import schach.game.moves.Movement;

/**
 * The king piece can move a single square in any direction as long as it can't
 * be immediately captured at the new position.
 */
public class KingPiece extends CastlingPiece {
  /**
   * Castlings a king and rook pair can make in either direction. Both colors make
   * the same castling moves since the kings are at the same x coordinate in their
   * starting positions.
   */
  private static final Set<CastlingPattern> CASTLING_PATTERNS = Set.of(new CastlingPattern(2, -2, 3),
      new CastlingPattern(-2, 3, -4));

  /**
   * Describes a combination of vectors needed to construct a castling move.
   */
  private static class CastlingPattern {
    private final Vector kingOffset;
    private final Vector rookOffset;
    private final Vector rookStartOffset;
    private final Vector stepOffset;

    /**
     * Constructs a pair of castling movement vectors from only the x offsets
     * involved since castling only happens horizontally.
     * 
     * @param kingOffset X coordinate offset in the movement of the king
     * @param rookOffset X coordinate offset in the movement of the rook
     * @param kingToRook Offset from the king to the rook for determining the
     *                   position where the rook needs to start
     */
    public CastlingPattern(int kingOffset, int rookOffset, int kingToRook) {
      this.kingOffset = new Vector(kingOffset, 0);
      this.rookOffset = new Vector(rookOffset, 0);

      // compute the preliminary checking step offset from the king offset
      stepOffset = new Vector(kingOffset > 0 ? 1 : -1, 0);

      // also make a vector for where the rook starts relative to the king
      rookStartOffset = new Vector(kingToRook, 0);
    }

    /**
     * Checks if the given king can castle to the specified target position. If
     * possible, adds the resulting double move to the accumulator. It is expected
     * that the position of the king has already been checked for not being under
     * attack. Since we can expect rhe piece to be at the given position we don't
     * need to check the game state for the piece being at "from".
     *
     * @param accumulator Accumulator to add generated moves to
     * @param onPiece     Piece to accumulate a castling move for
     * @return If more moves should be generated
     */
    private boolean accumulateCastling(MoveAccumulator accumulator, Piece onPiece) {
      // check that the rook is present and has not moved
      Position from = accumulator.getPosition();
      Position rookStart = Position.fromOffset(from, rookStartOffset);
      Piece rook = accumulator.getGameState().getPieceAt(rookStart);
      if (rook == null || !rook.canCastle(accumulator)) {
        return true;
      }

      // calculate where the king is going to be so we can check all fields in between
      Position kingTarget = Position.fromOffset(from, kingOffset);

      // keep track of if we are still checking for non-attacked or only free fields
      boolean checkingNonAttacked = true;

      // check the fields in the line to movement to be non-attacked
      // later only free of obstacles
      Position checkPosition = Position.fromOffset(from, stepOffset);
      while (!checkPosition.equals(rookStart)) {
        Piece checkPiece = accumulator.getGameState().getPieceAt(checkPosition);
        if (checkPiece != null) {
          return true;
        }

        // if still within the king movement range, also check for non-attacked
        if (checkingNonAttacked) {
          if (onPiece.isAttackedAt(accumulator, checkPosition)) {
            return true;
          }
          if (checkPosition.equals(kingTarget)) {
            checkingNonAttacked = false;
          }
        }

        checkPosition = Position.fromOffset(checkPosition, stepOffset);
      }

      // generate and accumulate the castling move now that we know it's legal
      return accumulator.addMove(new DoubleMove(new Movement(from, kingTarget),
          new Movement(rookStart, Position.fromOffset(rookStart, rookOffset))));
    }
  }

  /**
   * Constructs a king piece with the given color.
   * 
   * @param color Color of the new piece
   */
  public KingPiece(Color color) {
    super(color);
    type = PieceType.KING;
  }

  /**
   * Calculates the moves the king piece can make at the given position. The
   * possible moves consist of the regular 8 possible moves in all directions and
   * two additional castling moves.
   */
  @Override
  boolean accumulateMoves(MoveAccumulator accumulator) {
    // accumulate regular moves in all directions
    processJumpPatterns(accumulator, OffsetPatterns.AROUND);

    // check the two castling moves if not moved yet and not being attacked
    if (accumulator.generateNonAttacking() && canCastle(accumulator)
        && !isAttackedLocally(accumulator.getGameState(), accumulator.getPosition())) {
      for (CastlingPattern pattern : CASTLING_PATTERNS) {
        if (!pattern.accumulateCastling(accumulator, this)) {
          return false;
        }
      }
    }
    return true;
  }
}
