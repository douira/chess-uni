package schach.game.pieces;

import schach.common.Color;
import schach.game.accumulators.MoveAccumulator;

/**
 * The queen piece can move any number of squares in any direction without
 * obstruction.
 */
public class QueenPiece extends Piece {
  /**
   * Constructs a queen piece with the given color.
   * 
   * @param color Color of the new piece
   */
  public QueenPiece(Color color) {
    super(color);
    type = PieceType.QUEEN;
  }

  @Override
  boolean accumulateMoves(MoveAccumulator accumulator) {
    return processRayPatterns(accumulator, OffsetPatterns.AROUND);
  }
}
