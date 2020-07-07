package schach.interaction.commands;

import java.util.List;

import schach.common.Color;
import schach.game.moves.Move;
import schach.game.pieces.PieceType;
import schach.game.state.GameState;
import schach.game.state.MoveJournal;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
//NOPMD is required here to suppress false positives
import static schach.game.GameTestUtils.*; //NOPMD

/**
 * Tests that UndoCommand works correctly.
 */
public class UndoCommandTest {
  private GameState game = gameFromMoves("a2-a3");
  private MoveJournal journal = game.getJournal();

  /**
   * Tests that apply undoes the last move.
   */
  @Test
  public void testApply() {
    UndoCommand.INSTANCE.apply(journal);
    assertPiece(game.getPieceAt(0, 6), Color.WHITE, PieceType.PAWN);
  }

  /**
   * Tests that getDirectionList returns the list of the move journal.
   */
  @Test
  public void testGetDirectionList() {
    List<Move> expectedList = journal.getHistory();
    List<Move> returnedList = UndoCommand.INSTANCE.getDirectionList(journal);
    assertEquals(expectedList.get(0), returnedList.get(0));
  }
}
