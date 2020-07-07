package schach.game.pieces;

import schach.common.Color;
import schach.game.accumulators.MoveAccumulator;
import schach.game.moves.Move;

/**
 * Models pieces that keep track of their last move index to make sure they only
 * produce time or motion sensitive moves when allowed.
 */
public abstract class HistoryPiece extends Piece {
  /**
   * Constructs a new history piece with the given color.
   * 
   * @param color color of the piece
   */
  HistoryPiece(Color color) {
    super(color);
  }

  /**
   * Returns the last move this piece made using the game state given in the
   * accumulator.
   * 
   * @param accumulator accumulator to get the game state from
   * @return Last move this piece did, null if it has not moved yet
   */
  Move lastMove(MoveAccumulator accumulator) {
    return accumulator.getBoard().getPieceLastMove(this);
  }

  /**
   * Checks if this piece has moved yet using the last move index.
   * 
   * @param accumulator accumulator to get the game state from
   * @return If this piece has moved in this game yet
   */
  boolean hasMoved(MoveAccumulator accumulator) {
    return lastMove(accumulator) != null;
  }
}
