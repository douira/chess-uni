package schach.consoleui;

import java.util.EnumSet;
import java.util.Set;

import schach.common.Environment;
import schach.common.Position;
import schach.game.state.Board;
import schach.game.state.GameState;

//a NOPMD is required here because PMD has false positives when importing GameTestUtils
import org.junit.jupiter.api.Test;
import static schach.consoleui.IOTestUtils.*; //NOPMD
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests behavior of printer
 */
public class BoardPrinterTest {
  private static final Board board = new GameState().getBoard();

  /**
   * Asserts that the board is as expected both from the render function and from
   * the print stream.
   * 
   * @param flags    Flags to render the board with
   * @param expected Expected board
   */
  private static void assertChessboardRender(Set<Environment.Flag> flags, String expected) {
    Environment env = new Environment(flags);

    // assert simple expected
    assertEquals(expected, new BoardPrinter(env).renderChessboard(board));

    // assert with print stream
    // assertPrinted(expected + "\n", (printStream) -> new BoardPrinter(env,
    // printStream).printChessboard(board));
  }

  /**
   * Tests if the chessboard is rendered correctly
   */
  @Test
  public void testRenderChessboard() {
    String expectedBoard = "8 r n b q k b n r\n";
    expectedBoard += "7 p p p p p p p p\n";
    expectedBoard += "6                \n";
    expectedBoard += "5                \n";
    expectedBoard += "4                \n";
    expectedBoard += "3                \n";
    expectedBoard += "2 P P P P P P P P\n";
    expectedBoard += "1 R N B Q K B N R\n";
    expectedBoard += "  a b c d e f g h";
    assertChessboardRender(Set.of(), expectedBoard);

    expectedBoard = "8[46m r[0m[100m n[0m[46m b[0m[100m q[0m[46m k[0m[100m b[0m[46m n[0m[100m r[0m\n";
    expectedBoard += "7[100m p[0m[46m p[0m[100m p[0m[46m p[0m[100m p[0m[46m p[0m[100m p[0m[46m p[0m\n";
    expectedBoard += "6[46m  [0m[100m  [0m[46m  [0m[100m  [0m[46m  [0m[100m  [0m[46m  [0m[100m  [0m\n";
    expectedBoard += "5[100m  [0m[46m  [0m[100m  [0m[46m  [0m[100m  [0m[46m  [0m[100m  [0m[46m  [0m\n";
    expectedBoard += "4[46m  [0m[100m  [0m[46m  [0m[100m  [0m[46m  [0m[100m  [0m[46m  [0m[100m  [0m\n";
    expectedBoard += "3[100m  [0m[46m  [0m[100m  [0m[46m  [0m[100m  [0m[46m  [0m[100m  [0m[46m  [0m\n";
    expectedBoard += "2[46m P[0m[100m P[0m[46m P[0m[100m P[0m[46m P[0m[100m P[0m[46m P[0m[100m P[0m\n";
    expectedBoard += "1[100m R[0m[46m N[0m[100m B[0m[46m Q[0m[100m K[0m[46m B[0m[100m N[0m[46m R[0m\n";
    expectedBoard += "  a b c d e f g h";
    assertChessboardRender(EnumSet.of(Environment.Flag.PRINT_BACKGROUND), expectedBoard);

    expectedBoard = "8 [30m♖ [30m♘ [30m♗ [30m♕ [30m♔ [30m♗ [30m♘ [30m♖\n";
    expectedBoard += "7 [30m♙ [30m♙ [30m♙ [30m♙ [30m♙ [30m♙ [30m♙ [30m♙\n";
    expectedBoard += "6                \n";
    expectedBoard += "5                \n";
    expectedBoard += "4                \n";
    expectedBoard += "3                \n";
    expectedBoard += "2 [97m♙ [97m♙ [97m♙ [97m♙ [97m♙ [97m♙ [97m♙ [97m♙\n";
    expectedBoard += "1 [97m♖ [97m♘ [97m♗ [97m♕ [97m♔ [97m♗ [97m♘ [97m♖\n";
    expectedBoard += "  a b c d e f g h";
    assertChessboardRender(EnumSet.of(Environment.Flag.USE_SYMBOLS), expectedBoard);
  }

  /**
   * Asserts that the captured pieces are rendered and printed as expected.
   * 
   * @param board    Board to print the captured pieces for
   * @param flags    Flags to apply to the environment for the board
   * @param expected Expected string for the display of the captured pieces
   */
  private static void assertCapturedPiecesRender(Board board, Set<Environment.Flag> flags, String expected) {
    Environment env = new Environment(flags);

    // assert the normal rendering
    assertEquals(expected, new BoardPrinter(env).renderCapturedPieces(board));

    // assert the print stream rendering
    assertPrinted(expected + "\n", (printStream) -> new BoardPrinter(env, printStream).printCapturedPieces(board));
  }

  /**
   * Tests if beaten Pieces are rendered correctly
   */
  @Test
  public void testRenderCapturedPieces() {
    Board board = new GameState().getBoard();
    board.capturePiece(new Position(0, 0));
    assertCapturedPiecesRender(board, EnumSet.of(Environment.Flag.USE_SYMBOLS),
        "The following pieces have been captured: \n♖ ");
    assertCapturedPiecesRender(board, Set.of(), "The following pieces have been captured: \nr ");
  }
}
