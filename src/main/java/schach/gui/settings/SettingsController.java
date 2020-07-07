package schach.gui.settings;

import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import schach.gui.game.view.GameView;
import schach.gui.menu.MainMenuView;
import schach.gui.settings.SettingsModel.Settings;

/**
 * Controls the settings of the game. This class manages all the click events
 * that happen when a switch in the settings is clicked
 *
 */
public class SettingsController {
  private SettingsModel settingsModel = SettingsModel.getInstance();
  private GameView gameView;
  private MainMenuView mainMenuView;

  /**
   * Constructor for the controller
   * 
   * @param settingsView settingsView that shows the switches
   * @param mainMenuView mainMenuView from which the settings Controller is first
   *                     created
   * @param scene        the current scene
   */
  public SettingsController(SettingsView settingsView, MainMenuView mainMenuView, Scene scene) {
    this.mainMenuView = mainMenuView;
    settingsView.getHomeButton().setOnMouseClicked((MouseEvent e) -> {
      if (gameView != null) {
        gameView.getGameViewModel().update();
        scene.setRoot(gameView);
      } else {
        scene.setRoot(this.mainMenuView);
      }
    });

    initButtons(settingsView);

  }

  /**
   * Sets the mainMenuView as the view to which the controller should switch back
   * to when the back button is clicked
   * 
   * @param mainMenuView the view that is set as the new root
   */
  public void setLastView(MainMenuView mainMenuView) {
    this.mainMenuView = mainMenuView;
    gameView = null;
  }

  /**
   * Sets the gameView as the view to which the controller should switch back to
   * when the back button is clicked
   * 
   * @param gameView the view that is set as the new root
   */
  public void setLastView(GameView gameView) {
    this.gameView = gameView;
    mainMenuView = null;
  }

  /**
   * Initializes all the click events
   * 
   * @param settingsView the current settingsView
   */
  public void initButtons(SettingsView settingsView) {
    SwitchButton showCheckButton = settingsView.getButton("showInCheckButton");
    SwitchButton showMovesButton = settingsView.getButton("showMovesButton");
    SwitchButton whiteBottomButton = settingsView.getButton("bottomColorButton");
    SwitchButton turnBoardButton = settingsView.getButton("turnBoardButton");
    SwitchButton oneTouchButton = settingsView.getButton("allowRechoosingButton");

    showCheckButton.setOnMouseClicked((MouseEvent e) -> {
      showCheckButton.buttonSwitched();
      settingsModel.setSetting(Settings.SHOW_CHECK, showCheckButton.isSwitchedOn());
    });

    showMovesButton.setOnMouseClicked((MouseEvent e) -> {
      showMovesButton.buttonSwitched();
      settingsModel.setSetting(Settings.SHOW_MOVES, showMovesButton.isSwitchedOn());
    });

    whiteBottomButton.setOnMouseClicked((MouseEvent e) -> {
      whiteBottomButton.buttonSwitched();
      settingsModel.setSetting(Settings.SHOW_WHITE, whiteBottomButton.isSwitchedOn());
    });

    oneTouchButton.setOnMouseClicked((MouseEvent e) -> {
      oneTouchButton.buttonSwitched();
      settingsModel.setSetting(Settings.ONE_TOUCH, oneTouchButton.isSwitchedOn());
    });

    turnBoardButton.setOnMouseClicked((MouseEvent e) -> {
      turnBoardButton.buttonSwitched();
      settingsModel.setSetting(Settings.SHOW_ACTIVE_PLAYER, turnBoardButton.isSwitchedOn());
      whiteBottomButton.setVisible(!(boolean) settingsModel.getSetting(Settings.SHOW_ACTIVE_PLAYER));
      settingsView.getShowBlackAtBottomLabel()
          .setVisible(!(boolean) settingsModel.getSetting(Settings.SHOW_ACTIVE_PLAYER));
    });
  }
}
