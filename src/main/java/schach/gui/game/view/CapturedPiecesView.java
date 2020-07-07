package schach.gui.game.view;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import schach.common.Color;
import schach.common.Constants;
import schach.game.pieces.PieceType;
import schach.gui.ImageHandler;
import schach.gui.game.viewmodel.CapturedPiecesViewModel;

/**
 * A row of captured pieces can be generated with this class which are then made
 * visible as needed
 *
 */
public class CapturedPiecesView extends GridPane {
  /**
   * Creates a new row of captured piece views
   * 
   * @param captureColor            the color of the captured pieces
   * @param capturedPiecesViewModel the capturedPiecesViewModel which is used for
   *                                binding the visibility
   */
  public CapturedPiecesView(Color captureColor, CapturedPiecesViewModel capturedPiecesViewModel) {
    // iterate all types and generate views for each copy
    for (PieceType pieceType : PieceType.capturableValues) {
      Image pieceTypeImage = ImageHandler.getPieceImage(pieceType, captureColor);
      for (int duplicateIndex = 0; duplicateIndex < Constants.PIECE_DUPLICATES; duplicateIndex++) {
        ImageView pieceView = new ImageView(pieceTypeImage);
        minHeightProperty().bind(capturedPiecesViewModel.getCapturedPiecesHeightProperty());
        ReadOnlyBooleanProperty capturePieceProperty = capturedPiecesViewModel.getPieceCaptured(pieceType, captureColor,
            duplicateIndex);
        pieceView.managedProperty().bind(capturePieceProperty);
        pieceView.visibleProperty().bind(capturePieceProperty);
        ReadOnlyDoubleProperty capturePieceHeightProperty = capturedPiecesViewModel.getCapturedPiecesHeightProperty();
        pieceView.fitWidthProperty().bind(capturePieceHeightProperty);
        pieceView.fitHeightProperty().bind(capturePieceHeightProperty);
        GridPane.setValignment(pieceView, VPos.TOP);
        GridPane.setHalignment(pieceView, HPos.CENTER);
        add(pieceView, duplicateIndex + pieceType.ordinal() * Constants.PIECE_DUPLICATES, 0);
      }
    }
  }
}
