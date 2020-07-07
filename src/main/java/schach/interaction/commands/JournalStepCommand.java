package schach.interaction.commands;

import java.util.List;

import schach.common.Color;
import schach.game.moves.Move;
import schach.game.state.MoveJournal;
import schach.interaction.Session;

/**
 * A journal command either undoes or redoes a single move on the game state.
 * Two moves are automatically applied if the other player is not accessible.
 */
public abstract class JournalStepCommand extends JournalCommand {
  /**
   * Applies a single journal step in the direction of the subclass.
   * 
   * @param journal Journal to apply the step to
   */
  abstract void apply(MoveJournal journal);

  /**
   * Returns the list of the move journal that holds possible undo/redo moves for
   * the direction of the subclass.
   * 
   * @param journal Journal to get the list from
   * @return List of moves for this direction
   */
  abstract List<Move> getDirectionList(MoveJournal journal);

  /**
   * Checks if the necessary number of steps can be done and applies them if
   * possible.
   */
  @Override
  public void applyTo(Session session) {
    if (canApplyTo(session)) {
      MoveJournal journal = session.getGame().getJournal();
      int steps = getStepAmount(session);
      while (steps-- > 0) {
        apply(journal);
      }
    }
  }

  /**
   * Determines how many journal steps need to be made for this step command. If
   * there is an inaccessible player and the last active player was this
   * inaccessible player, the undo/redo step needs to be two steps big.
   * 
   * @param session Session to calculate the number of moves on
   * @return Number of journal steps that have to be made.
   */
  int getStepAmount(Session session) {
    return (!session.getPlayerFor(Color.WHITE).isAccessible() || !session.getPlayerFor(Color.BLACK).isAccessible())
        && session.lastActivePlayerAccessible() ? 2 : 1;
  }

  /**
   * Returns the size of the journal list for this type of step.
   * 
   * @param session Session to get the game and journal from
   * @return Size of the journal list for this direction
   */
  int getDirectionListSize(Session session) {
    return getDirectionList(session.getGame().getJournal()).size();
  }

  /**
   * Checks if this command can be applied to the given session. Checks that two
   * moves can be undone if there is only one accessible player.
   * 
   * @param session Session to check
   * @return True if this command can be applied to the given session
   */
  public boolean canApplyTo(Session session) {
    return getDirectionListSize(session) >= getStepAmount(session);
  }
}
