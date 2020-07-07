package schach.game.pieces;

import schach.common.Color;
import schach.game.accumulators.MoveAccumulator;

/**
 * Pieces that can castle extend this class in order to get the correct return
 * value from canCastle. (king and rook)
 */
public abstract class CastlingPiece extends HistoryPiece {
  /**
   * Constructs a castling piece with the given color.
   * 
   * @param color Color of the piece
   */
  public CastlingPiece(Color color) {
    super(color);
  }

  /**
   * Castling pieces can castle if they have not moved yet.
   */
  @Override
  public boolean canCastle(MoveAccumulator accumulator) {
    return !hasMoved(accumulator);
  }
}
