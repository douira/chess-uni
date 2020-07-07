package schach.game;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Stream;

import schach.common.Color;
import schach.common.Environment;
import schach.common.Position;
import schach.consoleui.BoardPrinter;
import schach.consoleui.CommandInterface;
import schach.game.moves.CapturingMove;
import schach.game.moves.Movement;
import schach.game.pieces.Piece;
import schach.game.pieces.PieceType;
import schach.game.state.GameState;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Class of general utility methods that make tests more concise. See conversion
 * function in README.md for more info.
 */
public class GameTestUtils {
  /**
   * Disallow construction of this static utility class.
   */
  private GameTestUtils() {
  }

  /**
   * Constructs a new movement list from a list of movement descriptions
   * 
   * @param movements List of movements in string format to parse as movements
   * @return Stream of converted movements
   */
  private static Stream<Movement> parseMovements(String[] movements) {
    return Arrays.stream(movements).map(CommandInterface::parseMovement);
  }

  /**
   * Applies all given movements to a game state.
   * 
   * @param game      Game state to apply movements to
   * @param movements List of movements to a game state
   */
  public static void applyMoves(GameState game, String... movements) {
    parseMovements(movements).forEach(movement -> game.doMove(game.validateMove(movement)));
  }

  /**
   * Constructs a game state and then applies all given movements
   * 
   * @param movements List of movements to apply to a new game state
   * @return Game state with all movements applied
   */
  public static GameState gameFromMoves(String... movements) {
    GameState game = new GameState();
    applyMoves(game, movements);
    return game;
  }

  /**
   * Makes sure a given movement specified by a string is valid in the given game
   * state.
   * 
   * @param game      Game state to test the movement in
   * @param movements Movements to test, all are asserted the same way
   */
  public static void assertValidMove(GameState game, String... movements) {
    parseMovements(movements).forEach(movement -> assertNotNull(game.validateMove(movement)));
  }

  /**
   * Does the opposite of assertValidMove and checks that a move is not valid.
   * 
   * @param game      Game state to test the movement in
   * @param movements Movements to test, all are asserted the same way
   */
  public static void assertInvalidMove(GameState game, String... movements) {
    parseMovements(movements).forEach(movement -> assertNull(game.validateMove(movement)));
  }

  /**
   * Asserts that the two given pieces are the same color and type of piece.
   * 
   * @param piece First piece to compare
   * @param other Second piece to compare
   */
  public static void assertEqualPieces(Piece piece, Piece other) {
    assertEquals(piece.getColor(), other.getColor());
    assertEquals(piece.getType(), other.getType());
  }

  /**
   * Asserts that a piece has the desired color and type.
   * 
   * @param piece Piece to check
   * @param color Color to assert
   * @param type  Type to assert
   */
  public static void assertPiece(Piece piece, Color color, PieceType type) {
    assertNotNull(piece);
    assertEquals(color, piece.getColor());
    assertEquals(type, piece.getType());
  }

  /**
   * Applies moves to a given game that put it into checkmate. White is in
   * checkmate.
   * 
   * @param game Game to apply the moves to
   */
  public static void intoCheckmate(GameState game) {
    // TODO: refactor these to use applyMoves from this class
    game.doMove(new Movement(new Position(4, 1), new Position(4, 3)));
    game.doMove(new Movement(new Position(6, 6), new Position(6, 4)));
    game.doMove(new Movement(new Position(5, 6), new Position(5, 5)));
    game.doMove(new Movement(new Position(3, 0), new Position(7, 4)));
  }

  /**
   * Applies moves to a given game that put it in a draw. It's black's turn but
   * the game is in draw.
   * 
   * @param game Game to apply the moves to
   */
  public static void intoDraw(GameState game) {
    game.doMove(new Movement(new Position(4, 6), new Position(4, 5)));
    game.doMove(new Movement(new Position(0, 1), new Position(0, 3)));
    game.doMove(new Movement(new Position(3, 7), new Position(7, 3)));
    game.doMove(new Movement(new Position(0, 0), new Position(0, 2)));
    game.doMove(new CapturingMove(new Position(7, 3), new Position(0, 3)));
    game.doMove(new Movement(new Position(7, 1), new Position(7, 3)));
    game.doMove(new Movement(new Position(7, 6), new Position(7, 4)));
    game.doMove(new Movement(new Position(0, 2), new Position(7, 2)));
    game.doMove(new CapturingMove(new Position(0, 3), new Position(2, 1)));
    game.doMove(new Movement(new Position(5, 1), new Position(5, 2)));
    game.doMove(new CapturingMove(new Position(2, 1), new Position(3, 1)));
    game.doMove(new Movement(new Position(4, 0), new Position(5, 1)));
    game.doMove(new CapturingMove(new Position(3, 1), new Position(1, 1)));
    game.doMove(new Movement(new Position(3, 0), new Position(3, 5)));
    game.doMove(new CapturingMove(new Position(1, 1), new Position(1, 0)));
    game.doMove(new Movement(new Position(3, 5), new Position(7, 1)));
    game.doMove(new CapturingMove(new Position(1, 0), new Position(2, 0)));
    game.doMove(new Movement(new Position(5, 1), new Position(6, 2)));
    game.doMove(new Movement(new Position(2, 0), new Position(4, 2)));
  }

  /**
   * Method for debugging tests, this is not efficient because it doesn't need to
   * be since this is only used when debugging and writing tests.
   */
  public static void debugPrintBoard(GameState game) {
    new BoardPrinter(new Environment(Set.of(Environment.Flag.PRINT_BACKGROUND, Environment.Flag.USE_SYMBOLS)))
        .printChessboard(game.getBoard());
  }
}
