package schach.game.pieces;

import schach.common.Color;
import schach.game.accumulators.MoveAccumulator;

/**
 * The knight can jump to and capture pieces at eight different squares around
 * it without being affected by obstructions in the line of movement.
 */
public class KnightPiece extends Piece {
  /**
   * Constructs a knight piece with the given color.
   * 
   * @param color Color of the new piece
   */
  public KnightPiece(Color color) {
    super(color);
    type = PieceType.KNIGHT;
  }

  @Override
  boolean accumulateMoves(MoveAccumulator accumulator) {
    return processJumpPatterns(accumulator, OffsetPatterns.KNIGHT);
  }
}
