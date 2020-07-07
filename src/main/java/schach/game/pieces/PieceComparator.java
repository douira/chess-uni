package schach.game.pieces;

import java.util.Comparator;

import schach.common.Color;

/**
 * Compares pieces for sorting them by color and rank.
 */
public class PieceComparator implements Comparator<Piece> {
  /**
   * Compares two pieces and returns -1, 0 or 1 for ordering them in a list.
   */
  @Override
  public int compare(Piece first, Piece second) {
    // default case for the same piece
    if (first == second) {
      return 0;
    }

    // sort by ascending by color (first white, then black) if different
    Color firstColor = first.getColor();
    Color secondColor = second.getColor();
    if (firstColor != secondColor) {
      return firstColor == Color.WHITE ? -1 : 1;
    }

    // sort by descending by rank if different
    int firstValue = first.getOrdinal();
    int secondValue = second.getOrdinal();
    if (firstValue != secondValue) {
      return firstValue > secondValue ? 1 : -1;
    }

    // otherwise it doesn't matter
    return 0;
  }
}
