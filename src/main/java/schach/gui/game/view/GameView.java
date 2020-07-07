package schach.gui.game.view;

import java.util.Optional;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.shape.Rectangle;
import schach.common.Color;
import schach.common.Constants;
import schach.game.moves.MoveType;
import schach.gui.game.viewmodel.GameViewModel;
import schach.gui.menu.MainMenuView;
import schach.gui.settings.SettingsView;

/**
 * Creates and manages the view that the player sees while playing, it mainly
 * displays the other view classes
 *
 */
public class GameView extends BorderPane {
  private ChessboardView chessboard;
  private InfoPanelView infoPanel;
  private CapturedPiecesView capturedPiecesWhite;
  private CapturedPiecesView capturedPiecesBlack;
  private GameViewModel gameViewModel;
  private MoveJournalView moveHistoryView;
  private double windowSizeY;
  private boolean blackCapturedBottom = true;
  private String style = "-fx-background:#3b3c3d;";

  /**
   * Constructor that creates the a new ViewModel and initializes all images etc.
   * that need to be displayed in the game view
   * 
   * @param mainMenuView The view of the main menu which is needed because the
   *                     gameViewModel may need to switch back to it
   * @param settingsView The view of the settings which is needed because the
   *                     gameViewModel may need to switch to it
   * @param scene        the scene is used to get the current height and width of
   *                     the display and also to use it in the gameViewModel
   *                     constructor
   */
  public GameView(MainMenuView mainMenuView, SettingsView settingsView, Scene scene) {
    this.setStyle(style);
    windowSizeY = scene.getHeight();
    moveHistoryView = new MoveJournalView(scene.getWidth() - (windowSizeY - (windowSizeY / Constants.BOARD_SIZE)));
    gameViewModel = new GameViewModel(this, mainMenuView, settingsView, scene);
    chessboard = new ChessboardView(gameViewModel.getChessboardViewModel());
    infoPanel = new InfoPanelView(gameViewModel.getinfoPanelViewModel());
    infoPanel.setMoveHistory(moveHistoryView);
    capturedPiecesWhite = new CapturedPiecesView(Color.WHITE, gameViewModel.getCapturedPiecesViewModel());
    capturedPiecesBlack = new CapturedPiecesView(Color.BLACK, gameViewModel.getCapturedPiecesViewModel());
    setCenter(infoPanel);
    setLeft(chessboard);
    setTop(capturedPiecesWhite);
    setBottom(capturedPiecesBlack);
    gameViewModel.initClickEvents();
  }

  /**
   * Getter for the homeButton
   * 
   * @return Current homeButton which can be found in the infoPanel
   */
  public ImageView getHomeButton() {
    return infoPanel.getHomeButton();
  }

  /**
   * Getter for the settingsButton
   * 
   * @return Current settingsButton which can be found in the infoPanel
   */
  public ImageView getSettingsButton() {
    return infoPanel.getSettingsButton();
  }

  /**
   * Returns the info panel view
   * 
   * @return the current info panel view
   */
  public InfoPanelView getInfoPanelView() {
    return infoPanel;
  }

  /**
   * The gameViewModel that was created in the constructor
   * 
   * @return Current gameViewModel
   */
  public GameViewModel getGameViewModel() {
    return gameViewModel;
  }

  public MoveJournalView getMoveHistoryView() {
    return moveHistoryView;
  }

  /**
   * The tiles that are needed to see if a user clicked in a certain field
   * 
   * @param index the board index between 0 and (BoardSize*BoardSize-1)
   * @return Tile at the specified index
   */
  public Rectangle getTile(int index) {
    return chessboard.getTile(index);
  }

  /**
   * Switches the the captured Pieces so that they are always displayed at the
   * opposite color
   * 
   * @param blackCapturedBottom boolean that tells the method if the captured
   *                            black pieces should be displayed at the bottom
   */
  public void switchCapturedPieces(boolean blackCapturedBottom) {
    if (this.blackCapturedBottom != blackCapturedBottom) {
      this.blackCapturedBottom = blackCapturedBottom;
      Node top = getTop();
      Node bottom = getBottom();
      setTop(null);
      setBottom(top);
      setTop(bottom);
    }
  }

  /**
   * Creates a popUp that asks the user to which piece he would like to promote
   * 
   * @return Chosen promotion Piece null if the user cancels the popUp
   */
  public MoveType promotionPopUp() {
    Alert alert = new Alert(AlertType.CONFIRMATION);
    alert.setTitle("Promotion");
    alert.setHeaderText("To wich piece would you like to Promote?");

    ButtonType buttonTypeQueen = new ButtonType("Queen");
    ButtonType buttonTypeRook = new ButtonType("Rook");
    ButtonType buttonTypeBishop = new ButtonType("Bishop");
    ButtonType buttonTypeKnight = new ButtonType("Knight");
    ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);
    alert.getButtonTypes().setAll(buttonTypeQueen, buttonTypeRook, buttonTypeBishop, buttonTypeKnight,
        buttonTypeCancel);
    Optional<ButtonType> result = alert.showAndWait();
    if (result.get() == buttonTypeQueen) {
      return MoveType.PROMOTION_QUEEN;
    } else if (result.get() == buttonTypeRook) {
      return MoveType.PROMOTION_ROOK;
    } else if (result.get() == buttonTypeBishop) {
      return MoveType.PROMOTION_BISHOP;
    } else if (result.get() == buttonTypeKnight) {
      return MoveType.PROMOTION_KNIGHT;
    } else {
      return MoveType.UNSPECIFIED;
    }
  }

  /**
   * Creates a confirmation popUp to make sure the user wants to return to the
   * main menu
   * 
   * @return True if the user selects the confirming action
   */
  public boolean homeConfirmationPopUp() {
    Alert alert = new Alert(AlertType.CONFIRMATION);
    alert.setTitle("Home Button Confirmation");
    alert.setHeaderText("Are you sure you want to Return to the Main menu?\nYou will loose all your progress!");
    alert.setContentText("Are you ok with this?");

    Optional<ButtonType> result = alert.showAndWait();
    return result.get() == ButtonType.OK;
  }
}
