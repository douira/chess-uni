package schach.gui.game.viewmodel;

import schach.common.Color;
import schach.common.Constants;
import schach.gui.game.model.GameModel;
import schach.gui.game.view.GameView;
import schach.gui.menu.MainMenuView;
import schach.gui.settings.SettingsModel;
import schach.gui.settings.SettingsView;
import schach.gui.settings.SettingsModel.Settings;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;

/**
 * The GameViewModel exists as a layer between the model and the view that
 * translates the model into easy to display elements for the view. It also
 * manages the other viewModel classes.
 *
 */
public class GameViewModel {
  private MainMenuView mainMenuView;
  private GameView gameView;
  private SettingsView settingsView;
  private Scene scene;
  private GameModel gameModel;
  private CapturedPiecesViewModel capturedPiecesViewModel;
  private ChessboardViewModel chessboardViewModel;
  private InfoPanelViewModel infoPanelViewModel;
  private SettingsModel settingsModel = SettingsModel.getInstance();

  /**
   * Constructor for the gameViewModel it initializes all other viewModels for the
   * game
   * 
   * @param gameView     the gameView that the gameViewModel interacts with
   * @param mainMenuView the mainMenuView that the gameViewModel might need to
   *                     switch to
   * @param settingsView the settingsView that the gameViewModel might need to
   *                     switch to
   * @param scene        the current scene
   */
  public GameViewModel(GameView gameView, MainMenuView mainMenuView, SettingsView settingsView, Scene scene) {
    this.mainMenuView = mainMenuView;
    this.gameView = gameView;
    this.settingsView = settingsView;
    this.scene = scene;
    chessboardViewModel = new ChessboardViewModel(this);
    infoPanelViewModel = new InfoPanelViewModel(gameView.getMoveHistoryView(), this);
    capturedPiecesViewModel = new CapturedPiecesViewModel();
    updateSize();
  }

  public ChessboardViewModel getChessboardViewModel() {
    return chessboardViewModel;
  }

  public InfoPanelViewModel getinfoPanelViewModel() {
    return infoPanelViewModel;
  }

  public CapturedPiecesViewModel getCapturedPiecesViewModel() {
    return capturedPiecesViewModel;
  }

  public SettingsView getSettingsView() {
    return settingsView;
  }

  public GameModel getGameModel() {
    return gameModel;
  }

  /**
   * Calculates if white should be shown at the bottom based on the settings and
   * active players
   * 
   * @return true if white should be shown at the bottom
   */
  public boolean showWhiteAtBottom() {
    return (boolean) settingsModel.getSetting(Settings.SHOW_ACTIVE_PLAYER) && gameModel.getActiveColor() == Color.WHITE
        || !(boolean) settingsModel.getSetting(Settings.SHOW_ACTIVE_PLAYER)
            && (boolean) settingsModel.getSetting(Settings.SHOW_WHITE);
  }

  /**
   * Calls all viewModels to update the all element sizes with the given scene
   * height and width
   */
  public void updateSize() {
    chessboardViewModel.updateSize(scene.getHeight(), scene.getWidth());
    infoPanelViewModel.updateSize(scene.getHeight(), scene.getWidth());
    capturedPiecesViewModel.updateSize(scene.getHeight(), scene.getWidth());
  }

  /**
   * Sets the gameModel and adds the listeners, if the own model is not null it
   * first removes the listeners from the own model.
   * 
   * @param gameModel the new gameModel
   */
  public void setModel(GameModel gameModel) {
    this.gameModel = gameModel;
    gameModel.getStatus().addListener(infoPanelViewModel.getStatusListener());
    gameModel.getChessboardModel().getPiecesOnBoard().addListener(chessboardViewModel.getBoardListener());
    gameModel.getChessboardModel().getMarkersOnBoard().addListener(chessboardViewModel.getMarkersListener());
    gameModel.getChessboardModel().getIndexHighlighted().addListener(chessboardViewModel.getHighlightListener());
    gameModel.getCapturedPieces().addListener(capturedPiecesViewModel.getCaptureListener());
    gameModel.getMoveHistory().addListener(infoPanelViewModel.getMoveHistoryListener());
    gameModel.getMoveFuture().addListener(infoPanelViewModel.getMoveFutureListener());
    scene.widthProperty().addListener((obs, oldVal, newVal) -> {
      updateSize();
    });
    scene.heightProperty().addListener((obs, oldVal, newVal) -> {
      updateSize();
    });
  }

  /**
   * Initializes all events that happen when certain elements in the gui are
   * clicked like for example the chessboard tiles.
   */
  public void initClickEvents() {

    gameView.getInfoPanelView().getRightArrowButton().setOnMouseClicked((MouseEvent e) -> {
      gameModel.redoMove();
    });

    gameView.getInfoPanelView().getLeftArrowButton().setOnMouseClicked((MouseEvent e) -> {
      gameModel.undoMove();
    });

    gameView.getHomeButton().setOnMouseClicked((MouseEvent e) -> {
      if (gameView.homeConfirmationPopUp()) {
        gameModel.supplyAbbortGameCommand();
        scene.setRoot(mainMenuView);
      }
    });

    gameView.getSettingsButton().setOnMouseClicked((MouseEvent e) -> {
      settingsView.getSettingsController().setLastView(gameView);
      scene.setRoot(settingsView);
    });

    for (int index = 0; index < Constants.BOARD_SIZE * Constants.BOARD_SIZE; index++) {
      final Integer innerIndex = Integer.valueOf(index);
      gameView.getTile(innerIndex).setOnMouseClicked((MouseEvent e) -> {
        clickedTile(innerIndex);
      });
    }
  }

  /**
   * Calls the gameModel with the clicked tile, inverts the tile that is clicked
   * if the black pieces are shown at the bottom.
   * 
   * @param index Board index of the clicked tile
   */
  public void clickedTile(int index) {
    if (showWhiteAtBottom()) {
      gameModel.getChessboardModel().clickedTile(index, settingsView, gameView);
    } else {
      gameModel.getChessboardModel().clickedTile(Constants.BOARD_SIZE * Constants.BOARD_SIZE - 1 - index, settingsView,
          gameView);
    }
  }

  /**
   * Forces a redraw of the status and all elements on the chessboard by
   * recalculating the status, calling the gameModel and recalculating the
   * orientation of the captured pieces
   */
  public void update() {
    gameModel.getChessboardModel().forceRedraw();
    switchCapturedPieces();
  }

  /**
   * Resets all elements to the beginning of the game, the chessboard does not
   * need to be reset as it will update automatically when updateGameState is
   * called in the game model
   */
  public void resetGame() {
    capturedPiecesViewModel.resetCapturedPieces();
    infoPanelViewModel.resetInfoPanel();
    update();
  }

  /**
   * Updates the game view with the new captured Piece orientation
   */
  public void switchCapturedPieces() {
    gameView.switchCapturedPieces(showWhiteAtBottom());
  }
}
