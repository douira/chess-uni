package schach.common;

/**
 * Represents a position on the board. The origin of the coordinate system is
 * the top left corner where a black rook stands on a white square in the
 * default configuration.
 */
public class Position extends Vector {
  /**
   * Constructs a new Position from two coordinates.
   * 
   * @param x X coordinate
   * @param y Y coordinate
   */
  public Position(int x, int y) {
    super(x, y);
  }

  /**
   * Constructs a new position using an origin position and an offset vector which
   * is added to the origin to get the new position.
   * 
   * @param origin Origin position
   * @param offset Offset vector which is added to the position
   * @return New offset position
   */
  public static Position fromOffset(Position origin, Vector offset) {
    return new Position(origin.getX() + offset.getX(), origin.getY() + offset.getY());
  }

  /**
   * Does the reverse calculation of computeBoardIndex. Returns a position object
   * for a given board index.
   * 
   * @param index Board index to make a position from
   * @return Position corresponding to the given board index
   */
  public static Position fromBoardIndex(int index) {
    return new Position(index % Constants.BOARD_SIZE, index / Constants.BOARD_SIZE);
  }

  /**
   * Calculates the board index of a position specified as a coordinate pair. The
   * board index is used to identify each square with a single number.
   * 
   * @param x X coordinate of the position
   * @param y Y coordinate of the position
   * @return Board index of the given position
   */
  public static int getBoardIndex(int x, int y) {
    return x + y * Constants.BOARD_SIZE;
  }

  /**
   * Returns the board index for a position instance.
   * 
   * @return Board index for this position
   */
  public int getBoardIndex() {
    return getBoardIndex(x, y);
  }

  /**
   * Checks if this position is still inside the board.
   * 
   * @return Whether this position is inside the board, false otherwise
   */
  public boolean outOfBounds() {
    return x < 0 || y < 0 || x >= Constants.BOARD_SIZE || y >= Constants.BOARD_SIZE;
  }

  /**
   * Returns a hash code for this position by mixing the coordinates.
   */
  @Override
  public int hashCode() {
    return x * 7 + y * 13;
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) {
      return true;
    }
    if (!(other instanceof Position)) {
      return false;
    }
    Position otherPosition = (Position) other;
    return otherPosition.x == x && otherPosition.y == y;
  }

  @Override
  public String toString() {
    return String.format("(%d,%d)", x, y);
  }

  /**
   * Converts this position to a command string coordinate representation.
   * 
   * @return This position as a command coordinate
   */
  public String toCommandString() {
    return Constants.ALPHABET.charAt(x) + Integer.toString(Constants.BOARD_SIZE - y);
  }
}
