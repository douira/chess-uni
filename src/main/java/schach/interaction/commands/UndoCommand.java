package schach.interaction.commands;

import java.util.List;

import schach.game.moves.Move;
import schach.game.state.MoveJournal;

/**
 * Command that undoes a move. See JournalStepCommand for details.
 */
public class UndoCommand extends JournalStepCommand {
  /**
   * Singleton instance of the undo command.
   */
  public static final UndoCommand INSTANCE = new UndoCommand();

  @Override
  void apply(MoveJournal journal) {
    journal.undoMove();
  }

  @Override
  List<Move> getDirectionList(MoveJournal journal) {
    return journal.getHistory();
  }
}
