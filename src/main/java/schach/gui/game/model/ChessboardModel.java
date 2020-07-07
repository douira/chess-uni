package schach.gui.game.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import schach.common.Constants;
import schach.common.Position;
import schach.game.moves.Move;
import schach.game.moves.MoveType;
import schach.game.moves.Movement;
import schach.game.pieces.Piece;
import schach.gui.game.view.GameView;
import schach.gui.settings.SettingsModel;
import schach.gui.settings.SettingsModel.Settings;
import schach.gui.settings.SettingsView;

/**
 * The chessboard model reacts to the tiles that have been clicked and updates
 * the observable lists when the gameState has changed
 */
public class ChessboardModel {
  private ObservableList<Piece> piecesOnBoard = FXCollections.observableArrayList();
  private ObservableList<Boolean> markersOnBoard = FXCollections.observableArrayList();
  private ObservableList<Boolean> indexHighlighted = FXCollections.observableArrayList();
  private int lastIndexClicked = -1;
  private boolean lastClickedMyPiece = false;
  private GameModel gameModel;
  private SettingsModel settingsModel = SettingsModel.getInstance();

  /**
   * Constructor for the chessboard model, it initializes the pieces with false
   * and null respectively and sets the gameModel
   * 
   * @param gameModel Game model to use as the state source
   */
  public ChessboardModel(GameModel gameModel) {
    this.gameModel = gameModel;
    for (int i = 0; i < Constants.BOARD_SIZE * Constants.BOARD_SIZE; i++) {
      piecesOnBoard.add(null);
      markersOnBoard.add(false);
      indexHighlighted.add(false);
    }
  }

  public ObservableList<Piece> getPiecesOnBoard() {
    return piecesOnBoard;
  }

  public ObservableList<Boolean> getMarkersOnBoard() {
    return markersOnBoard;
  }

  public ObservableList<Boolean> getIndexHighlighted() {
    return indexHighlighted;
  }

  public void setlastClickedMyPiece(boolean lastClickedMyPiece) {
    this.lastClickedMyPiece = lastClickedMyPiece;
  }

  /**
   * Resets what chess tile the player has clicked last
   */
  public void resetLastClicked() {
    lastIndexClicked = -1;
    lastClickedMyPiece = false;
  }

  /**
   * Recalculates all pieces on the board with the help of the gameState and
   * changes a piece if a difference is found
   */
  public void updatePiecesOnBoard() {
    for (int column = 0; column < Constants.BOARD_SIZE; column++) {
      for (int row = 0; row < Constants.BOARD_SIZE; row++) {
        if (piecesOnBoard.get(row + column * Constants.BOARD_SIZE) != gameModel.getGameState().getBoard()
            .getPieceAt(row, column)) {
          piecesOnBoard.set(row + column * Constants.BOARD_SIZE,
              gameModel.getGameState().getBoard().getPieceAt(row, column));
        }
      }
    }
  }

  /**
   * Recalculates the markers that show where a possible move can be made
   */
  public void updateMarkers() {
    for (int column = 0; column < Constants.BOARD_SIZE; column++) {
      for (int row = 0; row < Constants.BOARD_SIZE; row++) {
        Position start = new Position(lastIndexClicked % Constants.BOARD_SIZE, lastIndexClicked / Constants.BOARD_SIZE);
        Position target = new Position(row, column);
        Movement possibleMove = new Movement(start, target);
        possibleMove.setMoveType(MoveType.PROMOTION_QUEEN);
        if (gameModel.getGameState().validateMove(possibleMove) != null) {
          markersOnBoard.set(row + column * Constants.BOARD_SIZE, true);
        } else {
          markersOnBoard.set(row + column * Constants.BOARD_SIZE, false);
        }
      }
    }
  }

  /**
   * Removes all highlighting and markers
   */
  public void removeHelp() {
    for (int i = 0; i < markersOnBoard.size(); i++) {
      markersOnBoard.set(i, false);
      indexHighlighted.set(i, false);
    }
  }

  /**
   * Gets called when a tile is clicked by the player
   * 
   * @param index        the index that is clicked
   * @param settingsView the settingsView to access some of the settings
   * @param gameView     the gameView that may need to show a promotion popUp
   */
  public void clickedTile(int index, SettingsView settingsView, GameView gameView) {
    if (!gameModel.isGuiTurn()) {
      return;
    }

    if (canRechoose()) {
      removeHelp();
    }

    if (piecesOnBoard.get(index) != null
        && piecesOnBoard.get(index).getColor() == gameModel.getGameState().getActiveColor()
        && canRechoose()) {
      clickedMyPiece(index);
    } else if (lastClickedMyPiece) {
      clickedNotMyPiece(index, gameView);
    }
  }

  /**
   * Tests if the a new piece can be chosen based on the one touch setting and if the chosen piece has possible moves
   * 
   * @return true or false depending if a new piece can be chosen
   */
  public boolean canRechoose() {
    return !((boolean) settingsModel.getSetting(Settings.ONE_TOUCH) && lastClickedMyPiece) ||
        lastClickedMyPiece && gameModel.getGameState().getLegalMovesFor(new Position(lastIndexClicked % Constants.BOARD_SIZE,lastIndexClicked / Constants.BOARD_SIZE)).size() == 0;
  }
  /**
   * Gets called when a piece of the active color is clicked updates highlighting
   * and markers
   * 
   * @param index the index of the clicked piece
   */
  public void clickedMyPiece(int index) {
    lastClickedMyPiece = true;
    lastIndexClicked = index;
    updateMarkers();
    indexHighlighted.set(index, true);
  }

  /**
   * Gets called when a tile is clicked that does'nt have a piece of the active
   * color
   * 
   * @param index    the index of the clicked tile
   * @param gameView the gameView that may have to show a promotion popUp
   */
  public void clickedNotMyPiece(int index, GameView gameView) {
    Position start = new Position(lastIndexClicked % Constants.BOARD_SIZE, lastIndexClicked / Constants.BOARD_SIZE);
    Position target = new Position(index % Constants.BOARD_SIZE, index / Constants.BOARD_SIZE);
    Movement movement = new Movement(start, target);
    if (testPromotion(movement)) {
      MoveType promotion = gameView.promotionPopUp();
      movement.setMoveType(promotion);
    }
    Move validMove = gameModel.getGameState().validateMove(movement);
    if (validMove != null) {
      gameModel.supplyPlayerMove(validMove);
    }
  }

  /**
   * Tests if a promotion popUp has to be shown
   * 
   * @param movement the movement that might need a promotion type
   * @return true if a promotion popUp is needed false otherwise
   */
  public boolean testPromotion(Movement movement) {
    movement.setMoveType(MoveType.UNSPECIFIED);
    if (gameModel.getGameState().validateMove(movement) == null) {
      movement.setMoveType(MoveType.PROMOTION_QUEEN);
      if (gameModel.getGameState().validateMove(movement) != null) {
        return true;
      }
    }
    return false;
  }

  /**
   * forces a redraw by setting the 0 index of all arrays again, so the observers
   * of the lists see a change
   */
  public void forceRedraw() {
    piecesOnBoard.set(0, piecesOnBoard.get(0));
    markersOnBoard.set(0, markersOnBoard.get(0));
    indexHighlighted.set(0, indexHighlighted.get(0));
  }
}
