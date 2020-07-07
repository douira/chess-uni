package schach.game.pieces;

import schach.common.Color;
import schach.common.Vector;

/**
 * A flip vector is a pair of two vectors where each vector is given to a
 * differently colored piece. This makes generating moves for pawns and castling
 * more elegant as it abstracts the direction and color logic away from the move
 * generating logic.
 */
public class FlipVector {
  /**
   * Passed to the constructor to make the vector flip horizontally.
   */
  public static final boolean HORIZONTAL = true;

  /**
   * Passed to the constructor to make the vector flip vertically.
   */
  public static final boolean VERTICAL = false;

  /**
   * The vector given to white pieces
   */
  private final Vector whiteDirection;

  /**
   * The vector given to black pieces
   */
  private final Vector blackDirection;

  /**
   * Constructs a new flip vector with a given vector that represents the default
   * down direction.
   *
   * @param blackDirection Vector for the down direction that is given to black
   *                       pieces
   * @param flipDirection  in which direction the vector will be flipped
   */
  FlipVector(Vector blackDirection, boolean flipDirection) {
    this.blackDirection = blackDirection;
    this.whiteDirection = flipDirection == HORIZONTAL ? blackDirection.flipHorizontally()
        : blackDirection.flipVertically();
  }

  /**
   * Constructs a flip vector using coordinates instead of an existing vector
   * object.
   * 
   * @param x             X coordinate
   * @param y             Y coordinate
   * @param flipDirection in which direction the vector will be flipped
   */
  FlipVector(int x, int y, boolean flipDirection) {
    this(new Vector(x, y), flipDirection);
  }

  /**
   * Returns a vector flipped up or down depending on the color of the piece. This
   * is primarily meant for pawns which move in different directions depending on
   * their color.
   *
   * @param color Color of the piece
   * @return Vector that goes in the vertical direction for the given color
   */
  public Vector getDirectional(Color color) {
    return color == Color.WHITE ? whiteDirection : blackDirection;
  }
}
