package schach.interaction.commands;

import schach.game.moves.Move;
import schach.interaction.Session;

/**
 * A move command applies a move.
 */
public class MoveCommand implements SessionCommand {
  private final Move move;

  /**
   * Creates a new move command with the given move to apply.
   * 
   * @param move Applies this move when the command is applied
   */
  public MoveCommand(Move move) {
    this.move = move;
  }

  public Move getMove() {
    return move;
  }

  @Override
  public void applyTo(Session session) {
    session.doMove(move);
  }

  @Override
  public boolean allowedFromInactivePlayer() {
    return false;
  }
}
