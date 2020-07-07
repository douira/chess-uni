package schach.game.pieces;

import schach.common.Color;
import schach.common.Position;
import schach.game.accumulators.MoveAccumulator;

/**
 * The bishop piece can move any number of squares diagonally in any direction
 * without obstruction. Combined with the placement of the white and black
 * squares this means it always stays on color color of squares.
 */
public class BishopPiece extends Piece {
  private Color squareColor;

  /**
   * Constructs a bishop piece with the given color.
   * 
   * @param color Color of the new piece
   */
  public BishopPiece(Color color) {
    super(color);
    type = PieceType.BISHOP;
  }

  /**
   * Uses extra information about the position to init the board color of this
   * bishop. Bishops can only move along one color.
   * 
   * @param position Position this piece was started on
   */
  public void notifyPosition(Position position) {
    squareColor = (position.getX() + position.getY()) % 2 == 0 ? Color.WHITE : Color.BLACK;
  }

  public Color getSquareColor() {
    return squareColor;
  }

  @Override
  boolean accumulateMoves(MoveAccumulator accumulator) {
    return processRayPatterns(accumulator, OffsetPatterns.DIAGONAL);
  }
}
