package schach.consoleui;

import java.io.PrintStream;

import schach.common.Color;
import schach.common.Constants;
import schach.common.Environment;
import schach.game.pieces.Piece;
import schach.game.state.Board;

/**
 * Prints different views of given game states to the console using graphical
 * symbols or piece names. It also colors the squares of the board and the
 * pieces in their respective colors as defined in PrintColor.
 */
public class BoardPrinter {
  public boolean useSymbols;
  public boolean printBackground;

  /**
   * The full display row that shows the coordinate system to the user. This
   * string is longer than it needs to be to accommodate the board size.
   */
  private static final String DISPLAY_ROW = " a b c d e f g h i j k l m o p q r s t u v w x y z";

  private final PrintStream output;

  /**
   * Represents the current board printing color state.
   */
  private class PrintColor {
    private static final String BACKGROUND_WHITE = "\u001B[46m";
    private static final String BACKGROUND_BLACK = "\u001B[100m";
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String PIECE_WHITE = "\u001B[97m";
    private static final String PIECE_BLACK = "\u001B[30m";

    private boolean currentIsWhite;

    /**
     * Constructs a new print color state using the given starting state.
     * 
     * @param startWhite Starting color is white if the passed starting state is
     *                   true
     */
    private PrintColor(boolean startWhite) {
      currentIsWhite = startWhite;
    }

    /**
     * Constructs a new print color state using the default starting color white.
     */
    private PrintColor() {
      this(true);
    }

    /**
     * Returns the ANSI coloring string to prepend before the characters of the
     * current square to be printed.
     * 
     * @return A console color control string with the current color
     */
    private String getCurrentColor() {
      return currentIsWhite ? BACKGROUND_WHITE : BACKGROUND_BLACK;
    }

    /**
     * Returns a control string to reset the console text display to the default.
     * 
     * @return Reset control string
     */
    private String getResetColor() {
      return ANSI_RESET;
    }

    /**
     * Returns a constrol string to color the pieces.
     * 
     * @param piceColor Piece color of the piece we're printing
     * @return Coloring control string
     */
    private String getPieceColor(Color piceColor) {
      return piceColor == Color.WHITE ? PIECE_WHITE : PIECE_BLACK;
    }

    /**
     * Applies the current background color to the console by printing it.
     * 
     * @param builder String builder to add the background control string to
     */
    private void applyBackground(StringBuilder builder) {
      builder.append(getCurrentColor());
    }

    /**
     * Resets the current background color of the console by printing the reset
     * string.
     * 
     * @param builder String builder to add the background reset to
     */
    private void resetBackground(StringBuilder builder) {
      builder.append(getResetColor());
    }

    /**
     * Toggles the current color to the color of the opposite party. This is called
     * when a new square is started and the color of the adjacent square needs to be
     * the opposite color.
     */
    private void toggleColor() {
      currentIsWhite = !currentIsWhite;
    }
  }

  /**
   * Constructs a new board printer with an config environment and an output
   * stream. The output stream is used to print the rendered string
   * 
   * @param env    Environment with flags to configure print behavior
   * @param output Printing stream for output
   */
  public BoardPrinter(Environment env, PrintStream output) {
    this.useSymbols = env.flagActive(Environment.Flag.USE_SYMBOLS);
    this.printBackground = env.flagActive(Environment.Flag.PRINT_BACKGROUND);
    this.output = output;
  }

  /**
   * Constructs a board printer with only a config environment and uses System.out
   * for printing.
   * 
   * @param env Environment with flags to configure print behavior
   */
  public BoardPrinter(Environment env) {
    this(env, System.out);
  }

  /**
   * Renders a game state to a string using the symbols or names o the pieces. A
   * coordinate system is also printed to allow the user to easily read the
   * position of the pieces in algebraic notation.
   * 
   * @param board Board to render as a graphical grid of squares and figures
   * @return String rendering of the game state's board and pieces
   */
  public String renderChessboard(Board board) {
    StringBuilder builder = new StringBuilder();
    PrintColor printColor = new PrintColor();

    for (int row = 0; row < Constants.BOARD_SIZE; row++) {
      builder.append(Constants.BOARD_SIZE - row);
      for (int column = 0; column < Constants.BOARD_SIZE; column++) {
        Piece piece = board.getPieceAt(column, row);
        if (printBackground) {
          printColor.applyBackground(builder);
        }

        // print the symbol and a space before it if present, otherwise just spaces
        builder.append(" " + (piece == null ? " "
            : useSymbols ? printColor.getPieceColor(piece.getColor()) + piece.getSymbol() : piece.getPieceSign()));

        if (printBackground) {
          printColor.resetBackground(builder);
          printColor.toggleColor();
        }
      }
      if (printBackground) {
        printColor.toggleColor();
      }
      builder.append('\n');
    }

    // finalize and return the built string
    builder.append(' ');
    builder.append(DISPLAY_ROW.substring(0, Constants.BOARD_SIZE * 2));
    return builder.toString();
  }

  /**
   * Actually prints the rendered chess board as a string to the console.
   * 
   * @param board Board to print the chessboard of
   */
  public void printChessboard(Board board) {
    output.print(renderChessboard(board) + "\n");
  }

  /**
   * Generates a string that displays the captured pieces of a given game state.
   * 
   * @param board Board to display the captured pieces for
   * @return String rendering of the game state's captured pieces
   */
  public String renderCapturedPieces(Board board) {
    StringBuilder builder = new StringBuilder();
    builder.append("The following pieces have been captured: \n");
    for (Piece piece : board.getSortedCapturedPieces()) {
      builder.append((useSymbols ? piece.getSymbol() : piece.getPieceSign()) + " ");
    }
    return builder.toString();
  }

  /**
   * Prints the list of captured pieces to the console using the piece symbols or
   * names.
   * 
   * @param board Board to display the captured pieces of
   */
  public void printCapturedPieces(Board board) {
    output.print(renderCapturedPieces(board) + "\n");
  }
}
