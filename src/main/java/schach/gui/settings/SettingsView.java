package schach.gui.settings;

import java.util.LinkedHashMap;
import java.util.Map;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.text.Font;
import schach.gui.ImageHandler;
import schach.gui.menu.MainMenuView;

/**
 * This class displays the setting view
 *
 */
public class SettingsView extends BorderPane {
  private Map<String, SwitchButton> switches = new LinkedHashMap<String, SwitchButton>();
  private ImageView homeButton = new ImageView();
  private GridPane settings = new GridPane();
  private Font labelFont = new Font("Arial", 20);
  private Scene scene;
  private String[] labels = { "Show that the King is in check:", "Enforce one touch rule:", "Show possible moves:",
      "Show active player at the bottom:" };
  private Label showBlackAtBottomLabel = new Label("Show white at the bottom:");
  private SettingsController settingsController;
  private String style = "-fx-background:#3b3c3d;";

  /**
   * The constructor of the class, this constructor also Initializes the
   * settingsController when a new settingsView is created all settings will be
   * lost unless the old instance is saved
   * 
   * @param mainMenuView the mainMenuView for the settings Controller
   * @param scene        the current scene
   */
  public SettingsView(MainMenuView mainMenuView, Scene scene) {
    this.setStyle(style);
    this.scene = scene;
    addSwitches();
    addLabels();
    setUpHomeButton();
    initColumnConstraints();
    getButton("showInCheckButton").buttonSwitched();
    getButton("showMovesButton").buttonSwitched();
    getButton("bottomColorButton").buttonSwitched();
    settings.setAlignment(Pos.TOP_CENTER);
    BorderPane.setAlignment(homeButton, Pos.TOP_RIGHT);
    setCenter(settings);
    setTop(homeButton);
    settingsController = new SettingsController(this, mainMenuView, scene);
  }

  public SettingsController getSettingsController() {
    return settingsController;
  }

  /**
   * Returns the specified button
   * 
   * @param key the name of the button
   * @return Specified button
   */
  public SwitchButton getButton(String key) {
    return switches.get(key);
  }

  public Label getShowBlackAtBottomLabel() {
    return showBlackAtBottomLabel;
  }

  public ImageView getHomeButton() {
    return homeButton;
  }

  /**
   * Sets up the home button, it also color corrects it to black
   */
  public void setUpHomeButton() {
    ColorAdjust adjust = new ColorAdjust();
    adjust.setBrightness(-1);
    homeButton.setEffect(adjust);
    homeButton.setImage(ImageHandler.getIconImage(ImageHandler.Icon.HOME));
    homeButton.setPickOnBounds(true);
    homeButton.setId("HomeButton");
    homeButton.setFitHeight(scene.getHeight() / 12);
    homeButton.setFitWidth(scene.getHeight() / 12);
  }

  /**
   * Initializes the column constraints so the options are properly spaced
   */
  public void initColumnConstraints() {
    for (int i = 0; i < 4; i++) {
      RowConstraints row = new RowConstraints();
      row.setMinHeight(scene.getHeight() / 10);
      settings.getRowConstraints().add(row);
    }
    ColumnConstraints column = new ColumnConstraints();
    column.setMinWidth(scene.getHeight() / 3);
    settings.getColumnConstraints().add(column);
  }

  /**
   * Adds all switches in the order they were created in
   */
  public void addSwitches() {
    switches.put("showInCheckButton", new SwitchButton());
    switches.put("allowRechoosingButton", new SwitchButton());
    switches.put("showMovesButton", new SwitchButton());
    switches.put("turnBoardButton", new SwitchButton());
    switches.put("bottomColorButton", new SwitchButton());
    int index = 0;
    for (String key : switches.keySet()) {
      settings.add(switches.get(key), 1, index);
      index++;
    }
  }

  /**
   * Adds all labels, showBlackAtBottomLabel is special because it must be made
   * invisible when the player chose to show the active player at the bottom
   */
  public void addLabels() {
    for (int i = 0; i < labels.length; i++) {
      Label tmpLabel = new Label(labels[i]);
      tmpLabel.setFont(labelFont);
      settings.add(tmpLabel, 0, i);
    }
    showBlackAtBottomLabel.setFont(labelFont);
    settings.add(showBlackAtBottomLabel, 0, labels.length);
  }

}
