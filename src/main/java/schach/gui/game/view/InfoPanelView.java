package schach.gui.game.view;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import schach.gui.ImageHandler;
import schach.gui.game.viewmodel.InfoPanelViewModel;

/**
 * Creates the settings and home button as well as the game status and the move
 * history
 *
 */
public class InfoPanelView extends BorderPane {
  private GridPane menuBar = new GridPane();
  private GridPane moveController = new GridPane();
  private ImageView homeButton = new ImageView();
  private ImageView settingsButton = new ImageView();
  private ImageView leftArrowButton = new ImageView();
  private ImageView rightArrowButton = new ImageView();
  private Label notifications = new Label();
  Font font = new Font("Courier", 20);
  InfoPanelViewModel infoPanelViewModel;

  /**
   * Constructor for the infoPanel used to initialize a new information panel
   * 
   * @param infoPanelViewModel The infoPanelViewModel for binding certain sizes
   */
  public InfoPanelView(InfoPanelViewModel infoPanelViewModel) {
    notifications.setFont(font);
    notifications.setStyle("-fx-font-weight: bold");
    this.infoPanelViewModel = infoPanelViewModel;
    moveController.setAlignment(Pos.BOTTOM_CENTER);
    setUpMenuBar();
    setStyle("-fx-padding: 10;");
    setTop(menuBar);
    setUpMoveController();
    setBottom(moveController);

  }

  /**
   * Sets the move History in the view
   * 
   * @param moveHistoryView the move history to set
   */
  public void setMoveHistory(MoveJournalView moveHistoryView) {
    setCenter(moveHistoryView);
  }

  public ImageView getHomeButton() {
    return homeButton;
  }

  public ImageView getSettingsButton() {
    return settingsButton;
  }

  public ImageView getRightArrowButton() {
    return rightArrowButton;
  }

  public ImageView getLeftArrowButton() {
    return leftArrowButton;
  }

  /**
   * Sets up the menu bar which displays the settings button, home button and the
   * current status
   */
  public void setUpMenuBar() {
    setUpSettingsButton();
    setUpHomeButton();
    notifications.textProperty().bind(infoPanelViewModel.getNotificationsProperty());
    GridPane.setMargin(settingsButton, new Insets(5));
    GridPane.setMargin(homeButton, new Insets(5));
    GridPane.setMargin(notifications, new Insets(5));
    menuBar.add(settingsButton, 0, 0);
    menuBar.add(homeButton, 0, 1);
    menuBar.add(notifications, 1, 1);
  }

  /**
   * Sets up the undo and redo buttons
   */
  public void setUpMoveController() {
    leftArrowButton.setImage(ImageHandler.getIconImage(ImageHandler.Icon.LEFT_ARROW));
    rightArrowButton.setImage(ImageHandler.getIconImage(ImageHandler.Icon.RIGHT_ARROW));
    setUpColoring(leftArrowButton);
    setUpColoring(rightArrowButton);

    GridPane.setMargin(leftArrowButton, new Insets(15));
    GridPane.setMargin(rightArrowButton, new Insets(15));
    GridPane.setHalignment(leftArrowButton, HPos.CENTER);
    GridPane.setHalignment(rightArrowButton, HPos.CENTER);
    leftArrowButton.fitHeightProperty().bind(infoPanelViewModel.getButtonSizeProperty());
    leftArrowButton.fitWidthProperty().bind(infoPanelViewModel.getButtonSizeProperty().divide(1.8));
    rightArrowButton.fitHeightProperty().bind(infoPanelViewModel.getButtonSizeProperty());
    rightArrowButton.fitWidthProperty().bind(infoPanelViewModel.getButtonSizeProperty().divide(1.8));
    leftArrowButton.setPickOnBounds(true);
    rightArrowButton.setPickOnBounds(true);
    moveController.add(leftArrowButton, 0, 0);
    moveController.add(rightArrowButton, 1, 0);
  }

  /**
   * Sets up the home button
   */
  public void setUpHomeButton() {
    homeButton.setImage(ImageHandler.getIconImage(ImageHandler.Icon.HOME));
    setUpColoring(homeButton);
    homeButton.fitHeightProperty().bind(infoPanelViewModel.getButtonSizeProperty());
    homeButton.fitWidthProperty().bind(infoPanelViewModel.getButtonSizeProperty());
    homeButton.setPickOnBounds(true);
  }

  /**
   * Sets up the settings button
   */
  public void setUpSettingsButton() {
    settingsButton.setImage(ImageHandler.getIconImage(ImageHandler.Icon.SETTINGS));
    setUpColoring(settingsButton);
    settingsButton.setPickOnBounds(true);
    settingsButton.fitHeightProperty().bind(infoPanelViewModel.getButtonSizeProperty());
    settingsButton.fitWidthProperty().bind(infoPanelViewModel.getButtonSizeProperty());
  }

  /**
   * Color adjusts the desired image
   * 
   * @param image the image to adjust
   */
  public void setUpColoring(ImageView image) {
    ColorAdjust adjust = new ColorAdjust();
    adjust.setBrightness(-1);
    image.setEffect(adjust);
  }
}
