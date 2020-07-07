package schach.game.state;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import schach.common.Color;
import schach.common.Constants;
import schach.common.Position;
import schach.game.moves.Movement;
import schach.game.pieces.BishopPiece;
import schach.game.pieces.HistoryPiece;
import schach.game.pieces.KnightPiece;
import schach.game.pieces.PawnPiece;
import schach.game.pieces.Piece;
import schach.game.pieces.QueenPiece;
import schach.game.pieces.RookPiece;

//a NOPMD is required here because PMD has false positives when importing GameTestUtils
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
//NOPMD is required here to suppress false positives
import static schach.game.GameTestUtils.*; //NOPMD

/**
 * Tests the behavior of the board.
 */
public class BoardTest {
  /**
   * Tests if getPieces returns the correct HashMap.
   */
  @Test
  public void testGetPieces() {
    Board board = new Board(new GameState());
    Position position = new Position(0, 3);
    int index = position.getBoardIndex();

    Piece piece = new RookPiece(Color.BLACK);
    board.placeNewPiece(position, piece);
    assertEquals(piece, board.getPieces().get(index));
  }

  /**
   * Tests that the attack detection works correctly.
   */
  @Test
  public void testIsAttackedAt() {
    GameState game = gameFromMoves("a2-a4", "b7-b5");
    Board board = game.getBoard();
    assertTrue(board.isAttackedAt(new Position(1, 3)));
    assertTrue(board.isAttackedAt(new Position(0, 4)));
    assertFalse(board.isAttackedAt(new Position(1, 4)));
  }

  /**
   * Tests if the captured pieces are properly sorted into the list of captured
   * pieces. For this, the captured pieces from game2 (intoDraw) are used.
   */
  @Test
  public void testGetSortedCapturedPieces() {
    GameState game = new GameState();

    // setup of the pieces that are captured by intoDraw() in sorted order
    List<Piece> sortedList = List.of(new BishopPiece(Color.BLACK), new KnightPiece(Color.BLACK),
        new PawnPiece(Color.BLACK), new PawnPiece(Color.BLACK), new PawnPiece(Color.BLACK), new PawnPiece(Color.BLACK));

    // move game into draw and get the captured pieces as a sorted list
    intoDraw(game);
    List<Piece> gameList = game.getBoard().getSortedCapturedPieces();

    assertEquals(sortedList.size(), gameList.size());
    int count = 0;
    for (Piece expected : sortedList) {
      assertEqualPieces(gameList.get(count++), expected);
    }
  }

  /**
   * Tests that getKingPosition returns the correct position for each king.
   */
  @Test
  public void testGetKingPositionFor() {
    Board board = new Board(new GameState());
    Position whiteKing = new Position(4, 7);
    Position blackKing = new Position(4, 0);

    assertEquals(whiteKing, board.getKingPositionFor(Color.WHITE));
    assertEquals(blackKing, board.getKingPositionFor(Color.BLACK));
  }

  /**
   * Tests that capturing a pice gets rid of it.
   */
  @Test
  public void testCapturePiece() {
    GameState game = new GameState();
    Board board = game.getBoard();
    board.capturePiece(new Position(0, 0));
    assertNull(board.getPieceAt(0, 0));

    assertThrows(IllegalStateException.class, () -> board.capturePiece(new Position(5, 5)));
    assertThrows(IllegalStateException.class, () -> board.capturePiece(new Position(-1, 5)));
  }

  /**
   * Tests that getPieceAt returns the correct piece for a pair of coordinates.
   */
  @Test
  public void testGetPieceAt() {
    GameState game = new GameState();
    Piece blackRook = new RookPiece(Color.BLACK);
    assertEquals(blackRook.getColor(), game.getPieceAt(0, 0).getColor());
    assertEquals(blackRook.getFullName(), game.getPieceAt(0, 0).getFullName());
  }

  /**
   * Test that placing a new piece has the desired effect.
   */
  @Test
  public void testPlaceNewPiece() {
    Board board = new Board(new GameState());
    Position position = new Position(0, 0);
    HistoryPiece piece = new RookPiece(Color.WHITE);
    board.placeNewPiece(position, piece);

    // test 2: that 'pieces' (HashMap) is updated with the new piece that was placed
    int boardIndex = position.getBoardIndex();
    assertTrue(board.getPieces().containsKey(boardIndex));
    assertEquals(piece, board.getPieces().get(boardIndex));

    // test 3: that replacedPiece is updated if the target position is already
    // occupied
    Piece newPiece = new RookPiece(Color.BLACK);
    board.placeNewPiece(position, newPiece);
    assertEquals(piece, newPiece.getReplacedPiece());

    // test 5: put a piece in a position where no piece has yet been put, e.g.
    // presentPiece == null
    Piece anotherPiece = new QueenPiece(Color.WHITE);
    Position empty = new Position(0, 4);
    board.placeNewPiece(empty, anotherPiece);
    assertEquals(anotherPiece, board.getPieceAt(empty));
  }

  /**
   * Tests that apply movement actually applies the movement.
   * 
   * TODO: test more branches
   */
  @Test
  public void testApplyMovement() {
    GameState game = new GameState();
    Board board = game.getBoard();
    assertThrows(IllegalArgumentException.class, () -> board.applyMovement(new Movement(5, 5, 5, 5)));
  }

  /**
   * Asserts that if all pieces except the ones at the given positions are removed
   * there is or isn't sufficient material. The kings are never removed.
   * 
   * @param sufficient      If the material should be sufficient or not
   * @param retainPositions Array of positions to retain
   */
  private void assertSufficientMaterial(boolean sufficient, Position... retainPositions) {
    GameState game = new GameState();
    Board board = game.getBoard();

    // remove all if not in set of positions to retain
    Set<Position> retainSet = new HashSet<>(Arrays.asList(retainPositions));
    for (Color color : Color.values()) {
      retainSet.add(board.getKingPositionFor(color));
    }
    for (int x = 0; x < Constants.BOARD_SIZE; x++) {
      for (int y = 0; y < Constants.BOARD_SIZE; y++) {
        Position toRemove = new Position(x, y);
        if (!retainSet.contains(toRemove) && board.getPieceAt(toRemove) != null) {
          board.capturePiece(toRemove);
        }
      }
    }

    // assert that there is or isn't sufficient material
    assertEquals(sufficient, !board.hasInsufficientMaterial());
  }

  /**
   * Tests that the empty board does not have sufficient material.
   */
  @Test
  public void testEmptyBoardMaterial() {
    assertSufficientMaterial(false);
  }

  /**
   * Tests that the detection works for only one bishop or one knight.
   * 
   * TODO: cover remaining branch
   */
  @Test
  public void testHasInsufficientMaterial() {
    assertSufficientMaterial(true, new Position(0, 0));
    assertSufficientMaterial(false, new Position(1, 0));
    assertSufficientMaterial(false, new Position(2, 7));
  }

  /**
   * Tests that boards with only kinds and two bishops are sufficient in certain
   * cases.
   */
  @Test
  public void testBishopBoardMaterial() {
    assertSufficientMaterial(true, new Position(0, 0), new Position(0, 1));
    assertSufficientMaterial(true, new Position(2, 0), new Position(0, 1));
    assertSufficientMaterial(true, new Position(2, 0), new Position(2, 7));
    assertSufficientMaterial(false, new Position(2, 0), new Position(5, 7));
  }
}
