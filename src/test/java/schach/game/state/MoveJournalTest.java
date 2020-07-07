package schach.game.state;

import org.junit.jupiter.api.Test;
import schach.common.Color;
import schach.common.Position;
import schach.game.moves.Move;
import schach.game.moves.Movement;
import schach.game.pieces.PieceType;

//NOPMD is required here to suppress false positives
import static schach.game.GameTestUtils.*; //NOPMD

/**
 * Tests the behavior of MoveJournal.
 */
public class MoveJournalTest {
  private GameState game = gameFromMoves("a2-a3", "a7-a6", "d2-d3");
  private MoveJournal journal = game.getJournal();
  private Move lastMove = new Movement(new Position(3, 6), new Position(3, 5));

  /**
   * Tests that the moves are redone until the given move.
    */
  @Test
  public void testRedoUntil() {
    journal.undoMove();
    journal.undoMove();
    journal.redoUntil(lastMove);
    assertPiece(game.getPieceAt(0, 5), Color.WHITE, PieceType.PAWN);
    assertPiece(game.getPieceAt(0, 2), Color.BLACK, PieceType.PAWN);
    assertPiece(game.getPieceAt(3, 5), Color.WHITE, PieceType.PAWN);
  }
}
