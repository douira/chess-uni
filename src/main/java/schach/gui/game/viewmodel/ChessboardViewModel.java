package schach.gui.game.viewmodel;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.ListChangeListener;
import schach.common.Color;
import schach.common.Constants;
import schach.game.pieces.Piece;
import schach.game.pieces.PieceType;
import schach.gui.GUIUtils;
import schach.gui.settings.SettingsModel;
import schach.gui.settings.SettingsModel.Settings;

/**
 * ViewModel of the chessboard it transforms the model data into easy to display
 * data for the chessboardView
 *
 */
public class ChessboardViewModel {
  private DoubleProperty[] piecesHighlighted = new SimpleDoubleProperty[Constants.BOARD_SIZE * Constants.BOARD_SIZE];
  private BooleanProperty[] markersOnBoard = new SimpleBooleanProperty[Constants.BOARD_SIZE * Constants.BOARD_SIZE];
  private BooleanProperty[][] piecesOnBoard = new SimpleBooleanProperty[PieceType.amount * 2][Constants.BOARD_SIZE
      * Constants.BOARD_SIZE];
  private DoubleProperty fieldSizeProperty = new SimpleDoubleProperty();
  private DoubleProperty markersSizeProperty = new SimpleDoubleProperty();
  private GameViewModel gameViewModel;
  private SettingsModel settingsModel = SettingsModel.getInstance();
  private ListChangeListener<Boolean> highlightListener = change -> {
    var newList = change.getList();
    for (int i = 0; i < piecesHighlighted.length; i++) {
      if (newList.get(i)) {
        if (gameViewModel.showWhiteAtBottom()) {
          piecesHighlighted[i].set(0.5);
        } else {
          piecesHighlighted[Constants.BOARD_SIZE * Constants.BOARD_SIZE - 1 - i].set(0.5);
        }
      } else {
        if (gameViewModel.showWhiteAtBottom()) {
          piecesHighlighted[i].set(1);
        } else {
          piecesHighlighted[Constants.BOARD_SIZE * Constants.BOARD_SIZE - 1 - i].set(1);
        }
      }
    }
  };

  private ListChangeListener<Boolean> markersListener = change -> {
    var newList = change.getList();
    for (int i = 0; i < Constants.BOARD_SIZE * Constants.BOARD_SIZE; i++) {
      if ((boolean) settingsModel.getSetting(Settings.SHOW_MOVES)) {
        if (gameViewModel.showWhiteAtBottom()) {
          markersOnBoard[i].set(newList.get(i));
        } else {
          markersOnBoard[Constants.BOARD_SIZE * Constants.BOARD_SIZE - 1 - i].set(newList.get(i));
        }
      } else {
        markersOnBoard[i].set(false);
      }
    }
  };

  private ListChangeListener<Piece> boardListener = change -> {
    gameViewModel.switchCapturedPieces();
    var newBoard = change.getList();
    if (gameViewModel.showWhiteAtBottom()) {
      for (int i = 0; i < Constants.BOARD_SIZE * Constants.BOARD_SIZE; i++) {
        updateBoardIndex(newBoard.get(i), i);
      }
    } else {
      for (int i = 0; i < Constants.BOARD_SIZE * Constants.BOARD_SIZE; i++) {
        updateBoardIndex(newBoard.get(i), Constants.BOARD_SIZE * Constants.BOARD_SIZE - 1 - i);
      }
    }
  };

  /**
   * Constructor for the chessboard viewModel
   * 
   * @param gameViewModel the gameViewModel that handles the chessboard viewModel
   */
  public ChessboardViewModel(GameViewModel gameViewModel) {
    this.gameViewModel = gameViewModel;
    GUIUtils.makePropGrid(piecesOnBoard, GUIUtils::generateFalse);
    GUIUtils.makePropArray(markersOnBoard, GUIUtils::generateFalse);
    GUIUtils.makePropArray(piecesHighlighted, () -> new SimpleDoubleProperty(1));
  }

  public ListChangeListener<Boolean> getMarkersListener() {
    return markersListener;
  }

  public ListChangeListener<Boolean> getHighlightListener() {
    return highlightListener;
  }

  public ListChangeListener<Piece> getBoardListener() {
    return boardListener;
  }

  /**
   * Getter for the boolean property for the visibility of a piece on the board
   * 
   * @param pieceType       Piece type to get the entry for
   * @param color           Piece color to get the entry for
   * @param chessboardIndex the index of the piece
   * @return BooleanProperty in the specified place in the array
   */
  public ReadOnlyBooleanProperty getPieceOnBoard(PieceType pieceType, Color color, int chessboardIndex) {
    return piecesOnBoard[GUIUtils.getPieceTypeId(pieceType, color)][chessboardIndex];
  }

  /**
   * Getter for the piece highlighting
   * 
   * @param chessboardIndex the index that could be highlighted
   * @return DoubleProperty with the value 1 if the piece isn't highlighted
   *         otherwise a value less than 1
   */
  public ReadOnlyDoubleProperty getPieceHighlighted(int chessboardIndex) {
    return piecesHighlighted[chessboardIndex];
  }

  /**
   * Getter for the visibility of the marker
   * 
   * @param chessboardIndex the index of the marker
   * @return booleanProperty true if marker should be visible false otherwise
   */
  public ReadOnlyBooleanProperty getMarkerOnBoard(int chessboardIndex) {
    return markersOnBoard[chessboardIndex];
  }

  public ReadOnlyDoubleProperty getFieldSizeProperty() {
    return fieldSizeProperty;
  }

  public ReadOnlyDoubleProperty getMarkersSizeProperty() {
    return markersSizeProperty;
  }

  /**
   * Sets the piece on the board as visible
   * 
   * @param pieceTypeId Id of the piece type and color
   * @param boardIndex  the index on the board
   */
  public void setPieceOnBoard(int pieceTypeId, int boardIndex) {
    piecesOnBoard[pieceTypeId][boardIndex].set(true);
  }

  /**
   * Hides all pieces at an index
   * 
   * @param boardIndex the index where the piece should be hidden
   */
  public void removePieceOnBoard(int boardIndex) {
    for (BooleanProperty[] pieces : piecesOnBoard) {
      pieces[boardIndex].set(false);
    }
  }

  /**
   * Updates the board at an index by removing all pieces at that index and then
   * setting the new piece
   * 
   * @param piece the piece that should be set if null the index will just be
   *              cleared
   * @param index the index where the piece should be set
   */
  public void updateBoardIndex(Piece piece, int index) {
    removePieceOnBoard(index);
    if (piece != null) {
      setPieceOnBoard(GUIUtils.getPieceTypeId(piece), index);
    }
  }

  /**
   * Updates the size of the displayed elements on the chessboard
   * 
   * @param sceneHeight the height of the scene
   * @param sceneWidth  the width of the scene
   */
  public void updateSize(double sceneHeight, double sceneWidth) {
    fieldSizeProperty
        .set((Math.min(sceneHeight, sceneWidth) - (Math.min(sceneHeight, sceneWidth) / Constants.BOARD_SIZE))
            / Constants.BOARD_SIZE);
    markersSizeProperty.set(fieldSizeProperty.get() / 5);
  }
}
