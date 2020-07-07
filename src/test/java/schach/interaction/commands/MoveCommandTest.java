package schach.interaction.commands;

import schach.common.Color;
import schach.common.Position;
import schach.game.moves.Move;
import schach.game.moves.Movement;
import schach.game.pieces.PieceType;
import schach.interaction.TestableSession;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
//NOPMD is required here to suppress false positives
import static schach.game.GameTestUtils.*; //NOPMD

/**
 * Tests the behavior of MoveCommand.
 */
public class MoveCommandTest {
  private Move move = new Movement(new Position(0, 6), new Position(0, 5));
  private MoveCommand command = new MoveCommand(move);

  /**
   * Tests that the correct move is returned.
   */
  @Test
  public void getMove() {
    assertTrue(command.getMove().equals(move));
  }

  /**
   * Tests that the move is correctly applied to the session.
   */
  @Test
  public void applyTo() {
    TestableSession session = new TestableSession();
    session.startFake();
    command.applyTo(session);
    assertPiece(session.getGame().getPieceAt(0, 5), Color.WHITE, PieceType.PAWN);
  }

  /**
   * Tests that inactive players are not allowed to generate commands.
   */
  @Test
  public void allowedFromInactivePlayer() {
    assertFalse(command.allowedFromInactivePlayer());
  }
}
