package schach.game.pieces;

import schach.common.Color;
import schach.game.accumulators.MoveAccumulator;

/**
 * The rook piece can move any number of squares vertically or horizontally
 * without obstruction.
 */
public class RookPiece extends CastlingPiece {
  /**
   * Constructs a rook piece with the given color.
   * 
   * @param color Color of the new piece
   */
  public RookPiece(Color color) {
    super(color);
    type = PieceType.ROOK;
  }

  @Override
  boolean accumulateMoves(MoveAccumulator accumulator) {
    return processRayPatterns(accumulator, OffsetPatterns.ORTHOGONAL);
  }
}
