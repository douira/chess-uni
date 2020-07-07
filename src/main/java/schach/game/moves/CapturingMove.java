package schach.game.moves;

import schach.common.Position;
import schach.game.state.Board;

/**
 * A CapturingMove is a move of a piece capturing another piece at a given
 * position. The captured piece is removed from the board and marked as captured
 * while the capturing piece takes the position of the captured piece.
 */
public class CapturingMove extends Movement {
  /**
   * Constructs a capturing move using the given start and end position for the
   * movement.
   * 
   * @param from Starting position of the piece doing the capture
   * @param to   Target position of the piece, this is where the captured piece is
   *             taken away
   */
  public CapturingMove(Position from, Position to) {
    super(from, to);
  }

  @Override
  public boolean isAttacking(Position checkPosition) {
    return checkPosition.equals(to);
  }

  /**
   * Captures the piece at the starting position and applies the movement to the
   * game state.
   */
  @Override
  public void applyTo(Board board) {
    board.capturePiece(to);
    super.applyTo(board);
  }

  @Override
  public void reverseOn(Board board) {
    super.reverseOn(board);
    board.uncapturePiece(to);
  }

  @Override
  public int hashCode() {
    return super.hashCode() + 1;
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) {
      return true;
    }
    if (!(other instanceof CapturingMove)) {
      return false;
    }
    return movesEqual((Movement) other);
  }
}
