package schach.gui.settings;

import java.util.EnumMap;
import java.util.Map;

/**
 * The SettingsModel class handles all settings by saving them in a EnumMap, the
 * singleton pattern is used as the settings class is needed only once while
 * using the program
 *
 */
public class SettingsModel {
  private static SettingsModel instance;
  private static Map<Settings, Object> settings = new EnumMap<Settings, Object>(Settings.class);

  /**
   * All settings that are used
   *
   */
  public enum Settings {
    SHOW_CHECK, ONE_TOUCH, SHOW_MOVES, DARK_MODE, SHOW_ACTIVE_PLAYER, SHOW_WHITE;
  }

  /**
   * This is a static class and instances are forbidden. This constructor is only
   * called when there has not been an instance created yet and sets the values in
   * the EnumMap.
   */
  private SettingsModel() {
    settings.put(Settings.SHOW_CHECK, true);
    settings.put(Settings.ONE_TOUCH, false);
    settings.put(Settings.SHOW_MOVES, true);
    settings.put(Settings.SHOW_ACTIVE_PLAYER, false);
    settings.put(Settings.SHOW_WHITE, true);
  }

  /**
   * This method returns an instance of the class, when no instance has been
   * created yet it creates a new instance
   * 
   * @return the instance of the class
   */
  public static SettingsModel getInstance() {
    if (instance == null) {
      instance = new SettingsModel();
    }
    return instance;
  }

  /**
   * Returns a specified setting
   * 
   * @param setting the settings that should be return
   * @return the value that has been saved in the map
   */
  public Object getSetting(Settings setting) {
    if (settings.get(setting) == null) {
      throw new IllegalArgumentException("Setting has not been spezified");
    } else {
      return settings.get(setting);
    }
  }

  /**
   * Sets a specified setting
   * 
   * @param setting  the settings that should be set
   * @param newValue the new value of the setting
   */
  public void setSetting(Settings setting, Object newValue) {
    settings.put(setting, newValue);
  }
}
