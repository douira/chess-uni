package schach.interaction.commands;

import java.util.List;

import schach.game.moves.Move;
import schach.game.state.MoveJournal;
import schach.interaction.Session;

/**
 * Command that redoes a move. See JournalStepCommand for details.
 */
public class RedoCommand extends JournalStepCommand {
  /**
   * Singleton instance of the redo command.
   */
  public static final RedoCommand INSTANCE = new RedoCommand();

  @Override
  void apply(MoveJournal journal) {
    journal.redoMove();
  }

  @Override
  List<Move> getDirectionList(MoveJournal journal) {
    return journal.getFuture();
  }

  /**
   * Handle the edge case where we want to allow redoing the last move of the
   * player even though it's the inaccessible player's turn afterwards (the ai's
   * turn). This is to allow redoing our last move if we undid our last move while
   * the inaccessible player was already working on its move.
   */
  @Override
  int getStepAmount(Session session) {
    return getDirectionListSize(session) == 1 ? 1 : super.getStepAmount(session);
  }
}
