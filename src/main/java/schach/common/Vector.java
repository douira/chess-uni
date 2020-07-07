package schach.common;

/**
 * A vector consists of two coordinate components for the two spatial
 * dimensions.
 */
public class Vector {
  protected final int x;
  protected final int y;

  /**
   * Creates a new vector with two given coordinate values.
   * 
   * @param x X coordinate of the new vector
   * @param y Y coordinate of the new vector
   */
  public Vector(int x, int y) {
    this.x = x;
    this.y = y;
  }

  public int getX() {
    return x;
  }

  public int getY() {
    return y;
  }

  /**
   * Returns a new vector in which the value of the y coodinate is negated. This
   * flips the vector vertically.
   * 
   * @return A new vector with the same values as this vector but with the y
   *         direction flipped.
   */
  public Vector flipVertically() {
    return new Vector(x, -y);
  }

  /**
   * Returns a new vector in which the value of the x coordinate is negated.
   * 
   * @return A vector with the x direction flipped
   */
  public Vector flipHorizontally() {
    return new Vector(-x, y);
  }
}
