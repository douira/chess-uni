package schach.game.accumulators;

import java.util.ArrayList;

import schach.common.Position;
import schach.game.moves.Move;
import schach.game.moves.MoveType;
import schach.game.moves.Movement;
import schach.game.state.GameState;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test the behavior of the collection accumulator
 */
public class CollectionAccumulatorTest {
  /**
   * Test that the CollectionAccumulator constructs the collection as an array
   * list.
   */
  @Test
  public void testWithArrayList() {
    GameState game = new GameState();
    Movement singleStep = new Movement(0, 6, 0, 5);
    singleStep.setMoveType(MoveType.PAWN_SIMPLE);
    Movement doubleStep = new Movement(0, 6, 0, 4);
    doubleStep.setMoveType(MoveType.PAWN_DOUBLE);

    ArrayList<Move> expectedMoves = new ArrayList<Move>();
    expectedMoves.add(singleStep);
    expectedMoves.add(doubleStep);
    CollectionAccumulator<ArrayList<Move>> accumulator = CollectionAccumulator.withArrayList();

    game.accumulateMovesFor(accumulator, new Position(0, 6));
    assertEquals(accumulator.getMoves(), expectedMoves);
  }
}
