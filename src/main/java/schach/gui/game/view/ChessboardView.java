package schach.gui.game.view;

import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import schach.common.Constants;
import schach.game.pieces.PieceType;
import schach.gui.ImageHandler;
import schach.gui.game.viewmodel.ChessboardViewModel;

/**
 * Creates a new chessboard and adds every possible pieces to every position and
 * binds the visibility of the piece to the chessboardViewModel. Also creates
 * the chessboard tiles which are later used to see where the user clicks.
 *
 */
public class ChessboardView extends GridPane {
  private Rectangle[] tiles = new Rectangle[Constants.BOARD_SIZE * Constants.BOARD_SIZE];
  ChessboardViewModel chessboardViewModel;

  /**
   * Constructor that is used to create a new ChessboardView needs the
   * gameViewModel for bindings
   * 
   * @param chessboardViewModel the chessboardViewModel that is used to bind
   *                            alignment and visibility
   */
  public ChessboardView(ChessboardViewModel chessboardViewModel) {
    this.chessboardViewModel = chessboardViewModel;
    createChessBoard();
  }

  /**
   * Return the tile at the specified index
   * 
   * @param index the board index between 0 and (BoardSize*BoardSize-1)
   * @return Tile at the specified index
   */
  public Rectangle getTile(int index) {
    return tiles[index];
  }

  /**
   * creates a marker,tile and every chess pieces at every possible position
   */
  public void createChessBoard() {
    for (int row = 0; row < Constants.BOARD_SIZE; row++) {
      for (int column = 0; column < Constants.BOARD_SIZE; column++) {
        createChessTile(row, column);
        createAllPieces(row, column);
        createMarker(row, column);
      }
    }
  }

  /**
   * Creates a chess tile in a specified row and column, chooses the color based
   * on the row and column also binds the size of the tile to the gameViewModel
   * 
   * @param row    Row where the tile will be created
   * @param column Column where the tile will be created
   */
  public void createChessTile(int row, int column) {
    Rectangle rectangle = new Rectangle();
    rectangle.widthProperty().bind(chessboardViewModel.getFieldSizeProperty());
    rectangle.heightProperty().bind(chessboardViewModel.getFieldSizeProperty());
    rectangle.setFill((row + column) % 2 != 0 ? Color.FORESTGREEN : Color.LIGHTGOLDENRODYELLOW);
    tiles[row + column * Constants.BOARD_SIZE] = rectangle;
    add(rectangle, row, column);
  }

  /**
   * Creates every type of piece at a specified position and binds the visibility
   * to the gameViewModel.
   * 
   * @param row    Row where the pieces will be created
   * @param column Column where the pieces will be created
   */
  public void createAllPieces(int row, int column) {
    for (PieceType pieceType : PieceType.values()) {
      for (schach.common.Color color : schach.common.Color.values()) {
        ImageView pieceView = new ImageView(ImageHandler.getPieceImage(pieceType, color));
        pieceView.setPickOnBounds(true);
        pieceView.visibleProperty()
            .bind(chessboardViewModel.getPieceOnBoard(pieceType, color, row + column * Constants.BOARD_SIZE));
        pieceView.opacityProperty().bind(chessboardViewModel.getPieceHighlighted(row + column * Constants.BOARD_SIZE));
        GridPane.setValignment(pieceView, VPos.TOP);
        allignPiece(pieceView);
        add(pieceView, row, column);
      }
    }
  }

  /**
   * Creates a marker at a specified position also binds the visibility and size
   * to the gameViewModel
   * 
   * @param row    the row where the marker will be created
   * @param column the column where the marker will be created
   */
  public void createMarker(int row, int column) {
    Circle circle = new Circle();
    circle.radiusProperty().bind(chessboardViewModel.getMarkersSizeProperty());
    circle.setMouseTransparent(true);
    GridPane.setHalignment(circle, HPos.CENTER);
    circle.setOpacity(0.2);
    add(circle, row, column);
    circle.visibleProperty().bind(chessboardViewModel.getMarkerOnBoard(row + column * Constants.BOARD_SIZE));
  }

  /**
   * Aligns the specified piece
   * 
   * @param piece the pieces which should be aligned
   */
  public void allignPiece(ImageView piece) {
    piece.fitHeightProperty().bind(chessboardViewModel.getFieldSizeProperty());
    piece.fitWidthProperty().bind(chessboardViewModel.getFieldSizeProperty());
    piece.setMouseTransparent(true);
    GridPane.setHalignment(piece, HPos.CENTER);
  }
}
