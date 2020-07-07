package schach.interaction.commands;

import schach.common.Color;
import schach.game.moves.Move;
import schach.game.pieces.PieceType;
import schach.game.state.GameState;
import schach.game.state.MoveJournal;
import java.util.List;

import org.junit.jupiter.api.Test;
import schach.interaction.FakePlayer;
import schach.interaction.TestableSession;

import static org.junit.jupiter.api.Assertions.*;
//NOPMD is required here to suppress false positives
import static schach.game.GameTestUtils.*; //NOPMD

/**
 * Tests that RedoCommand works correctly.
 */
public class RedoCommandTest {
  private GameState game = gameFromMoves("a2-a3");
  private MoveJournal journal = game.getJournal();

  /**
   * Tests that the apply method redoes the last command.
   */
  @Test
  public void testApply() {
    journal.undoMove();
    RedoCommand.INSTANCE.apply(journal);
    assertPiece(game.getPieceAt(0, 5), Color.WHITE, PieceType.PAWN);
  }

  /**
   * Tests that the correct list is returned with getDirectionList
   */
  @Test
  public void testGetDirectionList() {
    journal.undoMove();
    List<Move> expectedList = journal.getFuture();
    List<Move> returnedList = RedoCommand.INSTANCE.getDirectionList(journal);
    assertEquals(expectedList.get(0), returnedList.get(0));
  }

  /**
   * Tests that the method returns the correct amount of steps that should be redone
   * either if the last player is accessible or not.
   */
  @Test
  public void testGetStepAmount() {
    TestableSession session = new TestableSession();
    GameState game = new GameState();
    FakePlayer whitePlayer = new FakePlayer();
    FakePlayer blackPlayer = new FakePlayer(false);
    session.start(game, whitePlayer, blackPlayer);

    // white is active player & accessible, black is not accessible
    assertEquals(2, RedoCommand.INSTANCE.getStepAmount(session));

    // white is active player, but no longer accessible
    whitePlayer.setAccessible(false);
    assertEquals(1, RedoCommand.INSTANCE.getStepAmount(session));

    // white and black are both accessible
    whitePlayer.setAccessible(true);
    blackPlayer.setAccessible(true);
    assertEquals(1, RedoCommand.INSTANCE.getStepAmount(session));
  }
}
