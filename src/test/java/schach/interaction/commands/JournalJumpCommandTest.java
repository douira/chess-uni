package schach.interaction.commands;

import schach.common.Color;
import schach.game.moves.Move;
import schach.game.moves.Movement;
import schach.game.pieces.PieceType;
import schach.game.state.JournalDirection;
import schach.interaction.TestableSession;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
//NOPMD is required here to suppress false positives
import static schach.game.GameTestUtils.*; //NOPMD

/**
 * Tests that JournalJumpCommand works correctly.
 */
public class JournalJumpCommandTest {
  private Move move = new Movement(1, 6, 1, 5);
  private JournalJumpCommand jumpCommand = new JournalJumpCommand(null, JournalDirection.UNDO);

  /**
   * Tests that applyTo applies the move correctly. Tests that the previous
   * move(s) were undone such that the position of the moved pawn from above is
   * now the same as it was before gameState.doMove was applied
   */
  @Test
  public void testApplyTo() {
    TestableSession session = new TestableSession();
    session.startFake();
    session.getGame().doMove(move);
    jumpCommand.applyTo(session);
    assertPiece(session.getGame().getPieceAt(1, 6), Color.WHITE, PieceType.PAWN);
  }

  /**
   * Tests that moves are allowed from inactive players.
   */
  @Test
  public void testAllowedFromInactivePlayer() {
    assertTrue(jumpCommand.allowedFromInactivePlayer());
  }
}
