package schach.interaction;

import schach.game.state.GameStatus;
import schach.interaction.Session.SessionStatus;

/**
 * Status of the game given as a notification type to the players.
 */
public enum TurnStatus {
  NONE(true), IN_CHECK, GIVING_CHECK, WON, LOST, DRAW(true);

  private boolean affectsBoth;

  /**
   * Construct a new turn status with the given affectsBoth flag. This flag
   * determines if this turns status is always given to both players and it's
   * necessary to make sure, that only one of the players displays this status if
   * they get it.
   * 
   * @param affectsBoth If this is always given to both players
   */
  TurnStatus(boolean affectsBoth) {
    this.affectsBoth = affectsBoth;
  }

  /**
   * Default to not affecting both
   */
  TurnStatus() {
    this(false);
  }

  /**
   * Checks if this turn status affects both.
   * 
   * @return True if it affects both
   */
  public boolean doesAffectBoth() {
    return affectsBoth;
  }

  /**
   * Calculates the turn status for a given session and player context. The
   * resulting turn status is given to the player in the notification.
   * 
   * @param sessionStatus  Status of the session
   * @param gameStatus     Status of the game state
   * @param isActivePlayer If the player being notified is the active player
   * @return Turn status calculated for the given turn context data
   */
  public static TurnStatus fromSessionData(SessionStatus sessionStatus, GameStatus gameStatus, boolean isActivePlayer) {
    return sessionStatus == SessionStatus.FINISHED
        // if game is finished the player can be in draw, have won or have lost
        ? gameStatus == GameStatus.IN_CHECKMATE ? isActivePlayer ? TurnStatus.LOST : TurnStatus.WON : TurnStatus.DRAW

        // if the game is not finished, the player can be giving check, in check or none
        : gameStatus == GameStatus.IN_CHECK ? isActivePlayer ? TurnStatus.IN_CHECK : TurnStatus.GIVING_CHECK
            : TurnStatus.NONE;
  }
}
