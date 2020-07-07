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

/**
 * ViewModel of the captured Pieces it transforms the model data into easy to
 * display data for the capturedPiecesView
 * 
 */
public class CapturedPiecesViewModel {
  private BooleanProperty[][] piecesCaptured = new SimpleBooleanProperty[PieceType.captureAmount
      * 2][Constants.PIECE_DUPLICATES];
  private DoubleProperty capturedPiecesHeightProperty = new SimpleDoubleProperty();

  private ListChangeListener<Piece> captureListener = change -> {
    var pieces = change.getList();
    resetCapturedPieces();
    for (Piece piece : pieces) {
      for (BooleanProperty entry : getPieceCaptureList(piece)) {
        if (!entry.get()) {
          entry.set(true);
          break;
        }
      }
    }
  };

  /**
   * Constructor for the captured Pieces viewModel
   * 
   */
  public CapturedPiecesViewModel() {
    GUIUtils.makePropGrid(piecesCaptured, GUIUtils::generateFalse);
  }

  /**
   * Returns the list of capture props for this piece type and color.
   * 
   * @param type  Type of the piece
   * @param color Color of the piece
   * @return List of boolean props that show the duplicates
   */
  private BooleanProperty[] getPieceCaptureList(PieceType type, Color color) {
    return piecesCaptured[GUIUtils.getCapturePieceTypeId(type, color)];
  }

  /**
   * Returns the list of capture props for this piece instance.
   * 
   * @param piece Piece to get the list for
   * @return List of boolean props that show the duplicates
   */
  private BooleanProperty[] getPieceCaptureList(Piece piece) {
    return getPieceCaptureList(piece.getType(), piece.getColor());
  }

  /**
   * Returns the booleanProperty that is true if a piece at a certain index has
   * been captured
   * 
   * @param type        Type of the piece
   * @param color       Color of the piece
   * @param rowPosition Index of the piece duplicate
   * @return true if piece has been captured false otherwise
   */
  public ReadOnlyBooleanProperty getPieceCaptured(PieceType type, Color color, int rowPosition) {
    return getPieceCaptureList(type, color)[rowPosition];
  }

  public ReadOnlyDoubleProperty getCapturedPiecesHeightProperty() {
    return capturedPiecesHeightProperty;
  }

  public ListChangeListener<Piece> getCaptureListener() {
    return captureListener;
  }

  /**
   * Updates the size of the captured Pieces
   * 
   * @param sceneHeight the height of the scene
   * @param sceneWidth  the width of the scene
   */
  public void updateSize(double sceneHeight, double sceneWidth) {
    capturedPiecesHeightProperty.set((Math.min(sceneHeight, sceneWidth) / Constants.BOARD_SIZE) / 2);
  }

  /**
   * Resets the captured Pieces
   */
  public void resetCapturedPieces() {
    for (BooleanProperty[] array : piecesCaptured) {
      for (BooleanProperty property : array) {
        property.set(false);
      }
    }
  }

}
