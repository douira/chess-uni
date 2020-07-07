package schach.gui.game.viewmodel;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.ListChangeListener;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import schach.common.Color;
import schach.consoleui.CommandInterface;
import schach.game.moves.Move;
import schach.game.state.JournalDirection;
import schach.gui.game.view.MoveJournalView;
import schach.gui.settings.SettingsModel;
import schach.gui.settings.SettingsModel.Settings;

/**
 * ViewModel of the infoPanel it transforms the model data into easy to display
 * data for the infoPanelView
 * 
 */
public class InfoPanelViewModel {
  private List<Label> moveHistoryLabels = new ArrayList<Label>();
  private List<Label> moveFutureLabels = new ArrayList<Label>();
  private DoubleProperty buttonSizeProperty = new SimpleDoubleProperty();
  private GameViewModel gameViewModel;
  private StringProperty notificationsProperty = new SimpleStringProperty("");
  private MoveJournalView moveJournalView;
  private ListChangeListener<Move> moveHistoryListener = makeJournalListener(moveHistoryLabels, JournalDirection.UNDO);
  private ListChangeListener<Move> moveFutureListener = makeJournalListener(moveFutureLabels, JournalDirection.REDO);
  private ChangeListener<String> statusListener = (observable, oldValue, newValue) -> {
    setStatus(newValue);
  };

  /**
   * Constructor for the infoPanel viewModel
   * 
   * @param moveJournalView MoveJournalView that handles the display of the move
   *                        journal
   * @param gameViewModel   GameViewModel that handles the infoPanel viewModel
   */
  public InfoPanelViewModel(MoveJournalView moveJournalView, GameViewModel gameViewModel) {
    this.gameViewModel = gameViewModel;
    this.moveJournalView = moveJournalView;
  }

  public ListChangeListener<Move> getMoveHistoryListener() {
    return moveHistoryListener;
  }

  public ListChangeListener<Move> getMoveFutureListener() {
    return moveFutureListener;
  }

  public ReadOnlyDoubleProperty getButtonSizeProperty() {
    return buttonSizeProperty;
  }

  /**
   * Generates a move journal listener that updates the journal view according to
   * the changes to the given move list.
   * 
   * @param moveLabels List of labels to generate
   * @return Listener for the given lists
   */
  private ListChangeListener<Move> makeJournalListener(List<Label> moveLabels, JournalDirection direction) {
    return change -> {
      moveLabels.clear();
      for (Move move : change.getList()) {
        moveLabels.add(new Label(CommandInterface.moveToCommandString(move)));
        final Move moveToReturn = move;
        moveLabels.get(moveLabels.size() - 1).setOnMouseClicked((MouseEvent e) -> {
          gameViewModel.getGameModel().clickMoveInJournalDisplay(moveToReturn, direction);
        });
      }
      if (direction == JournalDirection.UNDO) {
        moveHistoryLabels.add(new Label(""));
        moveHistoryLabels.add(new Label("Start"));
        moveHistoryLabels.get(moveHistoryLabels.size() - 1).setOnMouseClicked((MouseEvent e) -> {
          gameViewModel.getGameModel().clickMoveInJournalDisplay(null, JournalDirection.UNDO);
        });

      }
      moveJournalView.clearOldHistory();
      moveJournalView.displayMoveHistory(moveHistoryLabels);
      moveJournalView.displayMoveFuture(moveFutureLabels, moveHistoryLabels.size());
    };
  }

  public ChangeListener<String> getStatusListener() {
    return statusListener;
  }

  /**
   * Sets the given status so its displayed in the infoPanel
   * 
   * @param status Status to be displayed
   */
  public void setStatus(String status) {
    Color activeColor = gameViewModel.getGameModel().getActiveColor();
    String activeColorString = activeColor == Color.WHITE ? "white" : "black";
    String nonActiveColorString = activeColor == Color.WHITE ? "Black" : "White";
    if (status.equals("IN_CHECK") && (boolean) SettingsModel.getInstance().getSetting(Settings.SHOW_CHECK)) {
      notificationsProperty.set("The " + activeColorString + " King is in check");
    } else if (status.equals("IN_CHECKMATE")) {
      notificationsProperty.set(nonActiveColorString + " has won!");
    } else if (status.equals("DRAW")) {
      notificationsProperty.set("The game has ended in a draw.");
    } else {
      notificationsProperty.set("");
    }
  }

  public StringProperty getNotificationsProperty() {
    return notificationsProperty;
  }

  /**
   * Updates the size of the buttons in the information panel
   * 
   * @param sceneHeight the height of the scene
   * @param sceneWidth  the width of the scene
   */
  public void updateSize(double sceneHeight, double sceneWidth) {
    buttonSizeProperty.set(Math.min(sceneHeight, sceneWidth) / 20);
  }

  /**
   * Resets the move history and notifications
   */
  public void resetInfoPanel() {
    notificationsProperty.set("");
    moveHistoryLabels.clear();
    moveHistoryLabels.add(new Label(""));
    moveHistoryLabels.add(new Label("Start"));
    moveFutureLabels.clear();
    moveJournalView.clearOldHistory();
    moveJournalView.displayMoveHistory(moveHistoryLabels);
    moveJournalView.displayMoveFuture(moveFutureLabels, moveHistoryLabels.size());
  }
}
