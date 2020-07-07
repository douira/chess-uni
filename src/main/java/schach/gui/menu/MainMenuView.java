package schach.gui.menu;

import java.util.Optional;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.text.Font;
import schach.common.Color;
import schach.gui.ImageHandler;
import schach.gui.settings.SwitchButton;

/**
 * The MainMenuView class gets displayed when the user needs to see the main
 * menu, it extends the borderPane
 *
 */
public class MainMenuView extends BorderPane {

  private ImageView imagePvAI = new ImageView();
  private ImageView imagePvP = new ImageView();
  private ImageView settingsButton = new ImageView();
  private SwitchButton aiColorWhite = new SwitchButton();
  private GridPane chooseGameMode = new GridPane();
  private Slider aiStrength = new Slider(1, 10, 4);
  private double windowSizeY;
  private String style = "-fx-background:#3b3c3d;";

  /**
   * Constructor for the main menu view, initializes the view
   * 
   * @param windowSizeY the windowSize of the scene needed for calculating the
   *                    sizes of the buttons
   */
  public MainMenuView(double windowSizeY) {
    this.setStyle(style);
    this.windowSizeY = windowSizeY;
    setUpSettingsButton();
    setUpChooseGameMode();
    setTop(settingsButton);
    setCenter(chooseGameMode);
    setAlignment(chooseGameMode, Pos.CENTER);
  }

  public ImageView getImagePvAi() {
    return imagePvAI;
  }

  public ImageView getSettingsButton() {
    return settingsButton;
  }

  public ImageView getImagePvP() {
    return imagePvP;
  }

  public SwitchButton getAiColorWhite() {
    return aiColorWhite;
  }

  public Slider getAiStrength() {
    return aiStrength;
  }

  /**
   * Sets up the size and color of the settings button
   */
  public void setUpSettingsButton() {
    ColorAdjust adjust = new ColorAdjust();
    adjust.setBrightness(-1);
    settingsButton.setEffect(adjust);
    settingsButton.setImage(ImageHandler.getIconImage(ImageHandler.Icon.SETTINGS));
    settingsButton.setPickOnBounds(true);
    settingsButton.setId("HomeButton");
    settingsButton.setFitHeight(windowSizeY / 12);
    settingsButton.setFitWidth(windowSizeY / 12);
    setAlignment(settingsButton, Pos.TOP_RIGHT);
  }

  /**
   * Sets up the two images that give the player the choice between playing
   * against a computer or another human
   */
  public void setUpChooseGameMode() {
    RowConstraints row0 = new RowConstraints();
    row0.setMinHeight(30);
    chooseGameMode.getRowConstraints().add(row0);
    ColorAdjust adjust = new ColorAdjust();
    adjust.setBrightness(-1);
    imagePvAI.setEffect(adjust);
    imagePvP.setEffect(adjust);
    imagePvAI.setImage(ImageHandler.getIconImage(ImageHandler.Icon.COMPUTER));
    imagePvP.setImage(ImageHandler.getIconImage(ImageHandler.Icon.PLAYER));
    imagePvAI.setPickOnBounds(true);
    imagePvP.setPickOnBounds(true);
    imagePvAI.setFitHeight(windowSizeY / 2.5);
    imagePvAI.setFitWidth(windowSizeY / 2.5);
    imagePvP.setFitHeight(windowSizeY / 2.5);
    imagePvP.setFitWidth(windowSizeY / 2.5);
    GridPane.setConstraints(imagePvP, 0, 0, 1, 1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);
    chooseGameMode.add(imagePvP, 0, 1);
    GridPane.setConstraints(imagePvAI, 1, 0, 1, 1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);
    chooseGameMode.add(imagePvAI, 1, 1);
    setUpAiStrength();
  }

  /**
   * Gets called from the MainMenuController to prompt the user with choice
   * between playing white or black against the computer
   * 
   * @return Chosen color
   */
  public Color playerColorPopUp() {
    Alert alert = new Alert(AlertType.CONFIRMATION);
    alert.setTitle("Choose Color");
    alert.setHeaderText("With which color would you like to play?");

    ButtonType buttonWhite = new ButtonType("White");
    ButtonType buttonBlack = new ButtonType("Black");
    ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);
    alert.getButtonTypes().setAll(buttonWhite, buttonBlack, buttonTypeCancel);
    Optional<ButtonType> result = alert.showAndWait();
    if (result.get() == buttonWhite) {
      return Color.WHITE;
    } else if (result.get() == buttonBlack) {
      return Color.BLACK;
    } else {
      return null;
    }
  }

  /**
   * Sets up the slider to determine the strength of the Ai
   */
  private void setUpAiStrength() {
    Label aiStrengthLabel = new Label("Ai Strength: ");
    aiStrengthLabel.setFont(new Font("Arial", 20));
    GridPane.setConstraints(aiStrengthLabel, 1, 1, 1, 1, HPos.CENTER, VPos.TOP, Priority.ALWAYS, Priority.SOMETIMES);
    chooseGameMode.add(aiStrengthLabel, 1, 2);
    aiStrength.setMajorTickUnit(1);
    aiStrength.setMinorTickCount(0);
    aiStrength.setShowTickLabels(true);
    aiStrength.setShowTickMarks(true);
    aiStrength.setSnapToTicks(true);
    aiStrength.setMaxWidth(windowSizeY / 2);
    GridPane.setConstraints(aiStrength, 1, 2, 1, 1, HPos.CENTER, VPos.TOP, Priority.ALWAYS, Priority.ALWAYS,
        new Insets(10));
    chooseGameMode.add(aiStrength, 1, 3);
  }
}
