package schach.ai.strategies;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import schach.ai.GameEvaluator;
import schach.common.Color;
import schach.common.Position;
import schach.game.moves.Movement;
import schach.game.state.Board;
import schach.game.state.GameState;

/**
 * Tests the behavior of the fixedAlphaBeta search. Because in most situation the best move is impossible to calculate, only very
 * clear best moves are tested.
 *
 */
public class FixedAlphaBetaTest {
  GameEvaluator evaluator = new GameEvaluator();
  
  /**
   * Tests that the ai color is set correctly indirectly.
   */
  @Test
  public void testFindBestMove() {
    FakeThreadedPlayer fakePlayer = new FakeThreadedPlayer();
    FixedAlphaBeta fixedAlphaBeta = new FixedAlphaBeta(2);
    evaluator.setAiColor(Color.WHITE);
    fixedAlphaBeta.setEvaluator(evaluator);
    fixedAlphaBeta.setPlayer(fakePlayer);
    
    assertThrows(IllegalArgumentException.class, () -> {
      new FixedAlphaBeta(0);
    });

    
    // Constructs a gameState where capturing the rook is the definite best move
    GameState game = new GameState();
    Board board = game.getBoard();
    board.capturePiece(new Position(0, 6));
    board.capturePiece(new Position(0, 1));
    
    //Rook capture is expected
    Position origin = fixedAlphaBeta.findBestMove(game).getOriginPosition();
    Position target = fixedAlphaBeta.findBestMove(game).getTargetPosition();
    Position origin2 = new Position(0,7);
    Position target2 = new Position(0,0);
    testMoveEquality(origin,target,origin2,target2);
    
    //Rook capture is still expected at deeper search
    fixedAlphaBeta.setMaxDepth(3);
    origin = fixedAlphaBeta.findBestMove(game).getOriginPosition();
    target = fixedAlphaBeta.findBestMove(game).getTargetPosition();
    origin2 = new Position(0,7);
    target2 = new Position(0,0);
    testMoveEquality(origin,target,origin2,target2);
    
    //Tests if null is returned when player is aborted
    fakePlayer.setAborted(true);
    assertEquals(fixedAlphaBeta.findBestMove(game),null);
    
    //Tests if exception is thrown when no move is possible
    fakePlayer.setAborted(false);
    board.capturePiece(new Position(5,1));
    board.capturePiece(new Position(6,1));
    board.capturePiece(new Position(4,6));
    game.doMove(new Movement(new Position(3,7),new Position(7,3)));
    evaluator.setAiColor(Color.BLACK);
    
    assertThrows(IllegalStateException.class, () -> {
      fixedAlphaBeta.findBestMove(game);
    });
    
  }
  
  /**
   * Tests if 2 moves describe the same move
   * @param origin the origin of the first move
   * @param target the target of the first move
   * @param origin2 the origin of the second move
   * @param target2 the target of the second move
   */
  public void testMoveEquality(Position origin,Position target,Position origin2,Position target2) {
    assertEquals(origin,origin2);
    assertEquals(target,target2);
  }
}
