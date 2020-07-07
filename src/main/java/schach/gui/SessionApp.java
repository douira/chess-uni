package schach.gui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import schach.gui.menu.MainMenuController;
import schach.gui.menu.MainMenuView;
import schach.gui.settings.SettingsView;

/**
 * The session app exists to be the JavaFX entrypoint and to remove it from the
 * Main class and the main thread since we don't always want to create a GUI.
 */
public class SessionApp extends Application {
  @Override
  public void start(Stage stage) {

    // get the global gui session instance
    GUISession session = GUISession.getInstance();

    // setup the window and connect all the parts together
    int windowSizeX = 1000;
    int windowSizeY = 720;
    MainMenuView mainMenuView = new MainMenuView(windowSizeY);
    Scene scene = new Scene(mainMenuView, windowSizeX, windowSizeY);

    SettingsView settingsView = new SettingsView(mainMenuView, scene);
    new MainMenuController(session, mainMenuView, settingsView, scene);
    stage.setScene(scene);
    stage.setMinHeight(500);
    stage.setMinWidth(750);
    stage.show();
  }

  /**
   * Exit the program on the JavaFX window closing.
   */
  @Override
  public void stop() {
    System.exit(0);
  }
}
