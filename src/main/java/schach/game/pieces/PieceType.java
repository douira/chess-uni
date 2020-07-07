package schach.game.pieces;

import java.util.ArrayList;

/**
 * The different piece types and various display names. No value is stored
 * because the declaration order determines the sort order through the use of
 * .ordinal(). The value of these type of pieces for certain game positions is
 * stored in GameEvaluator.
 */
public enum PieceType {
  KING("k", "King", "♔", false), QUEEN("q", "Queen", "♕"), ROOK("r", "Rook", "♖"), BISHOP("b", "Bishop", "♗"),
  KNIGHT("n", "Knight", "♘"), PAWN("p", "Pawn", "♙");

  private final String shortName;
  private final String fullName;
  private final String symbol;

  /**
   * If this piece type should be considered when making a list of captured
   * pieces.
   */
  private final boolean capturable;

  /**
   * Piece types that can appear in lists of captured pieces.
   */
  public static final PieceType[] capturableValues;

  /**
   * How many different piece types there are.
   */
  public static final int amount = values().length;

  /**
   * How many of these pieces can appear to have been captured.
   */
  public static final int captureAmount;

  // init the list of capturable types
  static {
    ArrayList<PieceType> list = new ArrayList<>(amount);
    for (PieceType type : values()) {
      if (type.capturable) {
        list.add(type);
      }
    }
    capturableValues = new PieceType[list.size()];
    list.toArray(capturableValues);
    captureAmount = capturableValues.length;
  }

  PieceType(String shortName, String fullName, String symbol, boolean capturable) {
    this.shortName = shortName;
    this.fullName = fullName;
    this.symbol = symbol;
    this.capturable = capturable;
  }

  PieceType(String shortName, String fullName, String symbol) {
    this(shortName, fullName, symbol, true);
  }

  public String getShortName() {
    return shortName;
  }

  public String getFullName() {
    return fullName;
  }

  public String getSymbol() {
    return symbol;
  }
}
