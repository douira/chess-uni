package schach.gui.menu;

import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import schach.ai.AIPlayer;
import schach.common.Color;
import schach.gui.GUIPlayer;
import schach.gui.game.model.GameModel;
import schach.gui.game.view.GameView;
import schach.gui.settings.SettingsView;
import schach.interaction.GameMode;
import schach.interaction.Session;

/**
 * The MainMenuController class handles the inputs of the user in the main menu.
 * It can switch between the settings and the gameView. It also creates a new
 * game mode when needed and starts new sessions.
 *
 */
public class MainMenuController {
  private Scene scene;
  private GameView gameView;

  /**
   * The Constructor of the mainMenuController class
   * 
   * @param session      the current session
   * @param mainMenuView the view that the user sees while interacting with the
   *                     controller
   * @param settingsView the view that gets switched to when the user clicks the
   *                     settings button
   * @param scene        the current scene
   */
  public MainMenuController(Session session, MainMenuView mainMenuView, SettingsView settingsView, Scene scene) {
    this.scene = scene;
    gameView = new GameView(mainMenuView, settingsView, scene);
    mainMenuView.getImagePvP().setOnMouseClicked((MouseEvent e) -> {
      GameMode mode = GameMode.HUMANS;

      // tell the session that we're ready to play the game by starting it
      startNewGame(session, mode, mainMenuView, settingsView);
    });

    mainMenuView.getImagePvAi().setOnMouseClicked((MouseEvent e) -> {
      Color chosenColor = mainMenuView.playerColorPopUp();
      GameMode mode;
      if (chosenColor == Color.WHITE) {
        mode = GameMode.WHITE_V_AI;
      } else {
        mode = GameMode.BLACK_V_AI;
      }
      if (chosenColor != null) {
        // tell the session that we're ready to play the game by starting it
        startNewGame(session, mode, mainMenuView, settingsView);
      }
    });

    mainMenuView.getSettingsButton().setOnMouseClicked((MouseEvent e) -> {
      settingsView.getSettingsController().setLastView(mainMenuView);
      scene.setRoot(settingsView);
    });
  }

  /**
   * Starts a new Game with the specified parameters
   * 
   * @param session      the current session
   * @param mode         the chosen gameMode
   * @param mainMenuView the view that the user sees while interacting with the
   *                     controller
   * @param settingsView the view that gets switched to when the user clicks the
   *                     settings button
   */
  public void startNewGame(Session session, GameMode mode, MainMenuView mainMenuView, SettingsView settingsView) {
    GameModel gameModel = new GameModel();
    final int maxDepth = (int) mainMenuView.getAiStrength().getValue();
    mode.startSession(session, () -> new AIPlayer(maxDepth), () -> new GUIPlayer(gameModel));
    gameView.getGameViewModel().setModel(gameModel);
    gameView.getGameViewModel().resetGame();
    scene.setRoot(gameView);
  }
}
