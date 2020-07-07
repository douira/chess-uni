package schach.common;

/**
 * Holds global constants about the game that aren't meant to be changed.
 */
public class Constants {
  /**
   * Size of the square chess board in squares
   */
  public static final int BOARD_SIZE = 8;

  /**
   * The alphabet, this is used for command parsing and move display
   */
  public static final String ALPHABET = "abcdefghijklmnopqrstuvwxyz";

  /**
   * How many instances of a single piece type/color combination there can be
   * through promotions or any other means.
   */
  public static final int PIECE_DUPLICATES = 10;

  /**
   * Disallow instantiation
   */
  private Constants() {
  }
}
