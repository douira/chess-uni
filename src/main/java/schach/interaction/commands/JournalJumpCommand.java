package schach.interaction.commands;

import schach.game.moves.Move;
import schach.game.state.JournalDirection;
import schach.game.state.MoveJournal;
import schach.interaction.Session;

/**
 * A journal jump command undoes or redoes until a given move is reached and not
 * just one step like JournalStepCommand.
 */
public class JournalJumpCommand extends JournalCommand {
  private final Move move;
  private final JournalDirection direction;

  /**
   * Creates a new journal jump command that will move the journal such that the
   * given move is the latest applied move after applying this command. For now,
   * the direction still needs to be explicitly set.
   * 
   * @param move      Move to use for undoing/redoing the journal
   * @param direction In what direction the command should jump
   */
  public JournalJumpCommand(Move move, JournalDirection direction) {
    this.move = move;
    this.direction = direction;
  }

  @Override
  public void applyTo(Session session) {
    MoveJournal journal = session.getGame().getJournal();
    if (direction == JournalDirection.UNDO) {
      journal.undoUntil(move);
    } else {
      journal.redoUntil(move);
    }
  }
}
