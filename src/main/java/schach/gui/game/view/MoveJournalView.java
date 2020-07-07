package schach.gui.game.view;

import java.util.List;

import javafx.geometry.VPos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;

/**
 * Creates the move history view. This class extends a scrollPane as the move
 * history might get very long
 *
 */
public class MoveJournalView extends ScrollPane {
  private static final String FONT_FAMILY = "Courier";
  private static final int FONT_SIZE = 18;
  private String style = "-fx-font-weight: bold; -fx-font-family: '" + FONT_FAMILY + "'; -fx-font-size: " + FONT_SIZE
      + "px;";
  private String currentMoveStyle = "-fx-font-weight: bold; -fx-font-family: '" + FONT_FAMILY + "'; -fx-font-size: "
      + FONT_SIZE + "px;" + "-fx-background-color: grey;";
  private String futureMoveStyle = "-fx-font-weight: bold; -fx-font-family: '" + FONT_FAMILY + "'; -fx-font-size: "
      + FONT_SIZE + "px;" + "-fx-opacity: 0.5;";
  private GridPane gridPane = new GridPane();
  private double infoPanelWidth;

  /**
   * Constructor for creating a new move history view
   * 
   * @param infoPanelWidth the width the information panel should be
   */
  public MoveJournalView(double infoPanelWidth) {
    setStyle("-fx-background-color: transparent;");
    setHbarPolicy(ScrollBarPolicy.NEVER);
    this.infoPanelWidth = infoPanelWidth;
    setColumnConstraints();
    setContent(gridPane);
  }

  /**
   * Clears the old labels from the history
   */
  public void clearOldHistory() {
    gridPane.getChildren().clear();
  }

  /**
   * Displays all history moves, highlights the last move
   * 
   * @param moveHistory the moves already made
   */
  public void displayMoveHistory(List<Label> moveHistory) {
    for (int i = 0; i < moveHistory.size(); i++) {
      if (i % 2 == 0) {
        addNumeration(i / 2, style);
      }
      moveHistory.get(moveHistory.size() - i - 1).setStyle(style);
      GridPane.setValignment(moveHistory.get(moveHistory.size() - i - 1), VPos.TOP);
      if (i == moveHistory.size() - 1 || moveHistory.size() == 2) {
        moveHistory.get(moveHistory.size() - i - 1).setStyle(currentMoveStyle);
      }
      gridPane.add(moveHistory.get(moveHistory.size() - i - 1), i % 2 == 0 ? 1 : 2, i / 2);
    }
  }

  /**
   * Displays all future moves
   * 
   * @param moveFuture      the moves in the future
   * @param moveHistorySize the size of the history
   */
  public void displayMoveFuture(List<Label> moveFuture, int moveHistorySize) {
    if (moveFuture != null) {
      for (int i = 0; i < moveFuture.size(); i++) {
        if ((i + moveHistorySize) % 2 == 0) {
          addNumeration((i + moveHistorySize) / 2, futureMoveStyle);
        }
        moveFuture.get(i).setStyle(futureMoveStyle);
        GridPane.setValignment(moveFuture.get(i), VPos.TOP);
        gridPane.add(moveFuture.get(i), (i + moveHistorySize) % 2 == 0 ? 1 : 2, (i + moveHistorySize) / 2);
      }
    }
  }

  /**
   * Adds a number label at the specified index with the specified style
   * 
   * @param number the number to add
   * @param style  Style the style
   */
  public void addNumeration(int number, String style) {
    Label numberLabel = new Label(number + "");
    numberLabel.setStyle(style);
    GridPane.setValignment(numberLabel, VPos.TOP);
    gridPane.add(numberLabel, 0, number);
  }

  /**
   * Sets the column constraints so the columns are spaced properly
   */
  public void setColumnConstraints() {
    ColumnConstraints column = new ColumnConstraints();
    column.setMinWidth(infoPanelWidth / 10);
    gridPane.getColumnConstraints().add(column);
    column = new ColumnConstraints();
    column.setMinWidth(infoPanelWidth / 4);
    gridPane.getColumnConstraints().add(column);
    gridPane.getColumnConstraints().add(column);
  }
}
