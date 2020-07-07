package schach.consoleui;

import java.io.InputStream;
import java.io.PrintStream;

import schach.ai.AIPlayer;
import schach.common.Environment;
import schach.interaction.GameMode;
import schach.interaction.Session;

/**
 * A session on the console also inits the game mode using commands from the
 * console
 */
public class ConsoleSession extends Session {
  private final CommandInterface input;
  private final PrintStream output;
  private final BoardPrinter printer;

  /**
   * Constructs a new console session with the given board printer.
   * 
   * @param env         Environment to configure the game mode prompt behavior
   * @param printer     Board printer to use for printing
   * @param inputStream Input stream to pass to the command interface
   * @param output      Print stream to use for printing strings
   */
  public ConsoleSession(Environment env, BoardPrinter printer, InputStream inputStream, PrintStream output) {
    this.printer = printer;
    this.output = output;

    // use the game mode to start the session with players
    // only prompt if enabled, otherwise use human vs human game by default
    input = new CommandInterface(inputStream);
    if (env.flagActive(Environment.Flag.MODE_PRESELECTED)) {
      startInMode(GameMode.HUMANS);
    } else {
      promptGameMode();
    }
  }

  /**
   * Constructs a console session with the default print and input stream using
   * System.in/out.
   * 
   * @param env     Environment for game mode prompt config
   * @param printer Board printer to use for printing
   */
  public ConsoleSession(Environment env, BoardPrinter printer) {
    this(env, printer, System.in, System.out);
  }

  /**
   * Starts this session with the given mode and passes the correct player
   * generators.
   * 
   * @param mode Mode to start the session in
   */
  public void startInMode(GameMode mode) {
    mode.startSession(this, () -> new AIPlayer(), () -> new ConsolePlayer(printer, input, output));
  }

  /**
   * Prints instructions for the selection of the game state and then reads them
   * from the console.
   * 
   * @return Game mode entered by the user
   */
  private void promptGameMode() {
    // add a start message and all the lines for the game modes
    StringBuilder builder = new StringBuilder();
    builder.append("Select a game mode by entering a game mode name like 'X-Y':\n");
    for (GameMode mode : GameMode.values()) {
      builder.append("  ");
      builder.append(mode.toListEntry());
      builder.append('\n');
    }
    output.print(builder.toString());

    // read the game mode from the command reader, loop until valid
    GameMode mode;
    while (true) {
      mode = input.readInitCommand();
      if (mode == null) {
        output.print("Invalid game mode, try again. Enter strings of the form 'X-Y'.\n");
      } else {
        break;
      }
    }

    // print confirmation
    output.print("Selected: " + mode.toListEntry()
        + "\nEnter moves in the form 'x9-y8'. Print captured pieces with the 'beaten' command.\nWhite begins.\n\n");
    startInMode(mode);
  }

  /**
   * Prints the captured pieces of the game. This is called by a command.
   */
  public void printCapturedPieces() {
    printer.printCapturedPieces(getGame().getBoard());
  }
}
