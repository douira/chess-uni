package schach.game.state;

import java.util.Set;
import java.util.HashSet;
import java.util.List;

import schach.common.Color;
import schach.common.Position;
import schach.game.accumulators.AcceptAccumulator;
import schach.game.accumulators.CollectionAccumulator;
import schach.game.moves.CapturingMove;
import schach.game.moves.Move;
import schach.game.moves.MoveType;
import schach.game.moves.Movement;
import schach.game.pieces.RookPiece;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
//NOPMD is required here to suppress false positives
import static schach.game.GameTestUtils.*; //NOPMD

/**
 * Tests for the GameState class.
 */
public class GameStateTest {
  /**
   * Tests if getActiveColor returns the correct color.
   */
  @Test
  public void testGetActiveColor() {
    GameState game = new GameState();
    assertEquals(Color.WHITE, game.getActiveColor());
  }

  /**
   * Tests that getPieceAt returns the correct piece for a pair of coordinates.
   */
  @Test
  public void testGetPieceAt() {
    GameState game = new GameState();
    assertEqualPieces(new RookPiece(Color.BLACK), game.getPieceAt(0, 0));
  }

  private void assertGameStatus(GameState game, GameStatus status) {
    assertEquals(status, game.getStatus());
    assertEquals(status.gameIsStopped(), game.gameIsStopped());
  }

  /**
   * Tests if getStatus returns the correct game status, ie. NONE, IN_CHECK,
   * IN_CHECKMATE or DRAW
   */
  @Test
  public void testGetStatus() {
    GameState game1 = new GameState();
    GameState game2 = new GameState();

    // test 1: nothing happened yet
    assertGameStatus(game1, GameStatus.NONE);

    // test 2: put game into draw
    intoDraw(game2);
    assertGameStatus(game2, GameStatus.DRAW);

    // test 3: put game1 into checkmate
    intoCheckmate(game1);
    assertGameStatus(game1, GameStatus.IN_CHECKMATE);
  }

  /**
   * Tests if validateMove validates movements properly.
   */
  @Test
  public void testValidateMove() {
    // set up game so that a capturing move can be generated
    GameState game = new GameState();
    game.doMove(new Movement(new Position(1, 6), new Position(1, 4)));
    game.doMove(new Movement(new Position(0, 1), new Position(0, 3)));

    // generate capturing move
    Move capturingMove = new CapturingMove(new Position(1, 4), new Position(0, 3));
    capturingMove.setMoveType(MoveType.PAWN_SIMPLE);

    // also, validate the same type of move that was just generated
    Move gameMove = game.validateMove(new Movement(new Position(1, 4), new Position(0, 3)));

    // test that the validated move is the same as the generated move
    assertEquals(capturingMove, gameMove);
  }

  /**
   * Tests that accumulateMovesFor accumulates the moves to a position and doesn't
   * accumulate any if there is no piece at a position
   */
  @Test
  public void testAccumulateMovesFor() {
    GameState game = new GameState();

    // test 1: expect moves to be accumulated for a position correctly
    // what moves we expect this white pawn to be able to make
    Movement singleStep = new Movement(0, 6, 0, 5);
    singleStep.setMoveType(MoveType.PAWN_SIMPLE);
    Movement doubleStep = new Movement(0, 6, 0, 4);
    doubleStep.setMoveType(MoveType.PAWN_DOUBLE);
    Set<Move> expectedMoves = Set.of(singleStep, doubleStep);

    // generate moves and expect them to match
    CollectionAccumulator<HashSet<Move>> accumulator = CollectionAccumulator.withHashSet();
    game.accumulateMovesFor(accumulator, new Position(0, 6));
    assertEquals(accumulator.getMoves(), expectedMoves);

    // test 2: expect no moves to be accumulated for an empty position
    AcceptAccumulator testAccumulator = new AcceptAccumulator();
    game.accumulateMovesFor(testAccumulator, new Position(0, 5));
    assertFalse(testAccumulator.conditionIsSatisfied());

    // test 3: expect another obstructed piece to not have any moves
    AcceptAccumulator testAccumulator2 = new AcceptAccumulator();
    game.accumulateMovesFor(testAccumulator2, new Position(0, 7));
    assertFalse(testAccumulator2.conditionIsSatisfied());
  }

  /**
   * Tests that the accumulated moves for a specific position are legal, ie. do
   * not endanger the king.
   */
  @Test
  public void testAccumulateLegalMovesFor() {
    GameState game = new GameState();

    // test 1: generate set of possible moves for position, test that they are legal
    Movement singleStep = new Movement(0, 6, 0, 5);
    singleStep.setMoveType(MoveType.PAWN_SIMPLE);
    Movement doubleStep = new Movement(0, 6, 0, 4);
    doubleStep.setMoveType(MoveType.PAWN_DOUBLE);
    Set<Move> expectedMoves = Set.of(singleStep, doubleStep);

    CollectionAccumulator<HashSet<Move>> testAccumulator = CollectionAccumulator.withHashSet();
    game.accumulateLegalMovesFor(testAccumulator, new Position(0, 6));
    assertEquals(testAccumulator.getMoves(), expectedMoves);

    // test 2: put king in checkmate, test that no moves are legal from a given
    // position
    intoCheckmate(game);
    AcceptAccumulator testAccumulator2 = new AcceptAccumulator();
    game.accumulateLegalMovesFor(testAccumulator2, new Position(1, 6));
    assertFalse(testAccumulator2.conditionIsSatisfied());
  }

  /**
   * Tests that for a certain position, getLegalMovesFor returns the correct list
   * of legal moves.
   */
  @Test
  public void testGetLegalMovesFor() {
    GameState game = new GameState();

    // test 1: generate set of possible moves for position
    // and test that the returned list contains them
    Movement singleStep = new Movement(0, 6, 0, 5);
    singleStep.setMoveType(MoveType.PAWN_SIMPLE);
    Movement doubleStep = new Movement(0, 6, 0, 4);
    doubleStep.setMoveType(MoveType.PAWN_DOUBLE);
    List<Move> expectedMoves = List.of(singleStep, doubleStep);
    List<Move> testAccumulator = game.getLegalMovesFor(new Position(0, 6));

    assertEquals(testAccumulator, expectedMoves);

    // test 2: put king in checkmate, test that returned list contains no moves
    intoCheckmate(game);
    List<Move> noMoves = List.of();
    List<Move> testAccumulator2 = game.getLegalMovesFor(new Position(1, 6));
    assertEquals(testAccumulator2, noMoves);
  }
}
