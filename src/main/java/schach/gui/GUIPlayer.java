package schach.gui;

import javafx.application.Platform;
import schach.common.Utils;
import schach.gui.game.model.GameModel;
import schach.interaction.Player;
import schach.interaction.TurnStatus;

/**
 * A gui player interfaces with the threaded GUI to notify it of game state
 * changes when moves happen and receives moves from it when the player makes a
 * move.
 */
public class GUIPlayer extends Player {
  private final GameModel gameModel;
  private volatile boolean statusUpdateDone;

  /**
   * Construct a gui player using a game model to notify on changes.
   * 
   * @param gameModel Game model to notify
   */
  public GUIPlayer(GameModel gameModel) {
    this.gameModel = gameModel;
  }

  /**
   * This is called before each move (of any player) and after the game is over.
   * This doesn't mean it's this player's turn.
   * 
   * @param status Output status to display
   */
  @Override
  public void notifyStatus(TurnStatus status) {
    // if both are gui players, only have the active player update the gui
    if (isActivePlayer() || !(session.getActivePlayer() instanceof GUIPlayer)) {
      // run the update on the GUI and wait for it to complete
      statusUpdateDone = false;
      Platform.runLater(() -> {
        gameModel.updateGameState(this, session, status);
        statusUpdateDone = true;
        synchronized (this) {
          notify();
        }
      });
      Utils.waitUntil(this, (sync) -> sync.statusUpdateDone);
    }
  }

  @Override
  public void requestCommand() {
    // only the active player needs to talk to the gui
    if (isActivePlayer()) {
      Platform.runLater(() -> gameModel.requestActiveCommand(this));
    }
  }

  /**
   * Aborts waiting for the GUI to post a move when the Session wants to restart.
   */
  @Override
  public void abortCommandRequest() {
    if (isActivePlayer()) {
      Platform.runLater(() -> gameModel.finishGettingAction());
    }
  }
}
