package schach.common;

/**
 * Represents the color of a piece. A piece is either black or white and it's
 * color is set during construction using this enum. Both values of this enum
 * can transform a given string for text-based display of the piece symbol for
 * the color of the piece.
 */
public enum Color {
  BLACK {
    @Override
    public String toColorCase(String symbol) {
      return symbol.toLowerCase();
    }
  },
  WHITE {
    @Override
    public String toColorCase(String symbol) {
      return symbol.toUpperCase();
    }
  };

  /**
   * How many different colors there are.
   */
  public static final int AMOUNT = 2;

  /**
   * The color opposing this instance is the other color.
   */
  private Color opposing;

  static {
    BLACK.opposing = WHITE;
    WHITE.opposing = BLACK;
  }

  /**
   * Returns the index of this color using the ordinal value. This is used for
   * mapping from this enum.
   * 
   * @return Index of this color.
   */
  public int getIndex() {
    return ordinal();
  }

  /**
   * Returns the color that is opposing this color. For two colors this is always
   * the other color.
   * 
   * @return Color opposing this color
   */
  public Color getOpposing() {
    return opposing;
  }

  /**
   * Returns the name of this enum instance in a printable format with only the
   * first letter capitalized.
   * 
   * @return Name of the enum in a printable format.
   */
  public String getPrettyName() {
    String name = name();
    return name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
  }

  /**
   * Converts the given string to upper or lower case depending on the color.
   * White transforms to upper case while black transforms to lower case.
   * 
   * @param symbol String of the piece symbol to transform
   * @return Transformed piece symbol
   */
  public abstract String toColorCase(String symbol);
}
