package schach.interaction;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * The possible game modes that can be selected.
 */
public enum GameMode {
  WHITE_V_AI("W-C", "Human playing white vs. Computer") {
    @Override
    public void startSession(Session session, Supplier<Player> AIPlayerGenerator,
        Supplier<Player> humanPlayerGenerator) {
      session.start(setupHumanPlayer(humanPlayerGenerator.get()), setupAIPlayer(AIPlayerGenerator.get()));
    }
  },
  BLACK_V_AI("B-C", "Human playing black vs. Computer") {
    @Override
    public void startSession(Session session, Supplier<Player> AIPlayerGenerator,
        Supplier<Player> humanPlayerGenerator) {
      session.start(setupAIPlayer(AIPlayerGenerator.get()), setupHumanPlayer(humanPlayerGenerator.get()));
    }
  },
  HUMANS("H-H", "Human vs. Human") {
    @Override
    public void startSession(Session session, Supplier<Player> AIPlayerGenerator,
        Supplier<Player> humanPlayerGenerator) {
      session.start(setupHumanPlayer(humanPlayerGenerator.get()), setupHumanPlayer(humanPlayerGenerator.get()));
    }
  };

  private static final Map<String, GameMode> SHORT_NAME_MAP = new HashMap<>(6);

  private String shortName;
  private String description;

  // init the short name map with the short names
  static {
    for (GameMode mode : values()) {
      // convert to lower case to allow being case insensitive when searching
      SHORT_NAME_MAP.put(mode.shortName.toLowerCase(), mode);
    }
  };

  /**
   * Constructs a game mode from a short name and a description.
   * 
   * @param shortName   Short name for this game mode, identifies the game mode
   * @param description Description for this game mode, explains the game mode to
   *                    the user
   */
  GameMode(String shortName, String description) {
    this.shortName = shortName;
    this.description = description;
  }

  /**
   * Starts a session with two players that are generated by a given functions if
   * required.
   * 
   * @param session              Session to start with the players
   * @param AIPlayerGenerator    Returns players to use as the computer players
   * @param humanPlayerGenerator Returns players to use as the human players
   */
  public abstract void startSession(Session session, Supplier<Player> AIPlayerGenerator,
      Supplier<Player> humanPlayerGenerator);

  private static Player setupHumanPlayer(Player player) {
    player.setAccessible(true);
    return player;
  }

  private static Player setupAIPlayer(Player player) {
    player.setAccessible(false);
    return player;
  }

  /**
   * Returns a list entry string for this game mode. The string includes the short
   * name in brackets and the description.
   * 
   * @return List entry string for this game mode
   */
  public String toListEntry() {
    return "[" + shortName + "] " + description;
  }

  /**
   * Returns the game mode that has a short name that matches the given string.
   * (Not case sensitive)
   * 
   * @param likeShortName String to search with for game modes with this short
   *                      name
   * @return Game mode with this short name, null if such a game mode doesn't
   *         exist
   */
  public static GameMode getWithName(String likeShortName) {
    return SHORT_NAME_MAP.get(likeShortName.toLowerCase().trim());
  }
}
