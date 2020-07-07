package schach.gui;

import schach.common.Utils;
import schach.game.state.GameState;
import schach.interaction.Player;
import schach.interaction.Session;

/**
 * A gui session manages the interaction of the session, which represents the
 * game and player interaction, and the gui. The gui is started in a separate
 * thread in order to not block it when the main thread is busy calculting
 * something. (like a move for the AI)
 * 
 * This class is a singleton and instances are retrieved using getInstance while
 * instances are created using ensureInstance.
 */
public class GUISession extends Session {
  public static GUISession INSTANCE;

  /**
   * Creates a new gui session instance with an args array for JavaFX to consume.
   * The gui is started in another thread.
   * 
   * @param args Original program args to pass to JavaFX
   */
  private GUISession(String[] args) {
    Thread thread = new Thread(() -> SessionApp.launch(SessionApp.class, args));
    thread.setName("guiThread");
    thread.setDaemon(false);
    thread.start();
  }

  /**
   * The main gui session loop is not loopTurns because it needs to be able to
   * restart itself when notified by the gui.
   */
  public void activate() {
    // loop games forever
    while (true) {
      // wait util we are started again
      Utils.waitUntil(this, (sync) -> sync.status == SessionStatus.RUNNING);

      // do a game by looping through turns until it's finished
      loopTurns();
    }
  }

  /**
   * Starts the session and then notifies itself so that the looping of the turns
   * can start.
   */
  @Override
  synchronized public void start(GameState game, Player whitePlayer, Player blackPlayer) {
    super.start(game, whitePlayer, blackPlayer);

    // notify self to get out of the start waiting loop
    notify();
  }

  /**
   * Returns the global instance for this class.
   * 
   * @return Instance of the gui session
   */
  public static GUISession getInstance() {
    return INSTANCE;
  }

  /**
   * Saves a new instance, constructed using the given args, globally as the
   * singleton instance.
   * 
   * @param args Arguments to pass to JavaFX for creating the application
   * @return Gui session instance
   */
  public static GUISession ensureInstance(String[] args) {
    INSTANCE = new GUISession(args);
    return INSTANCE;
  }
}
