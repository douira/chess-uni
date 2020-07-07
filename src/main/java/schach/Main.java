package schach;

import schach.common.Environment;
import schach.consoleui.BoardPrinter;
import schach.consoleui.ConsoleSession;
import schach.gui.GUISession;

/**
 * The main class is started as a program and decides whether to start the GUI
 * or the command line interface.
 */
public class Main {
  private static Environment env;

  /**
   * The main method that starts the program.
   *
   * @param args Command line arguments.
   */
  public static void main(String[] args) {
    // catch argument exceptions and print them
    try {
      env = new Environment(args);
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
    }

    // construct sessions depending on the selected interface
    if (env.flagActive(Environment.Flag.USE_GUI)) {
      GUISession guiSession = GUISession.ensureInstance(args);

      // activate the session to make it loop and wait for notifications
      // this is the blocking main operation
      guiSession.activate();

      // the session is started when the GUI threads is ready
    } else {
      ConsoleSession session = new ConsoleSession(env, new BoardPrinter(env));

      // run the session until it's done, this is the blocking main operation
      session.loopTurns();
    }
  }
}
