package schach.consoleui;

import java.io.PrintStream;
import java.util.Map;

import schach.common.Color;
import schach.consoleui.commands.ConsoleCommand;
import schach.interaction.Session;
import schach.interaction.ThreadedPlayer;
import schach.interaction.TurnStatus;
import schach.interaction.commands.MoveCommand;
import schach.interaction.commands.SessionCommand;

/**
 * A console player is a human player using the console to enter commands and
 * receive data about the game state.
 * 
 * This player can't actually be aborted but that's ok since we just don't
 * accept input from this player if it's in the aborted state. If it does
 * produce a command after multiple abort/request cycles that's ok because we
 * can still print although the input stream in being waited on.
 */
public class ConsolePlayer extends ThreadedPlayer {
  private static final Map<TurnStatus, String> STATUS_MESSAGES = Map.of(TurnStatus.IN_CHECK, "%s is in check.",
      TurnStatus.GIVING_CHECK, "", TurnStatus.WON, "%s has won.", TurnStatus.LOST, "", TurnStatus.DRAW,
      "The game has ended in a draw.");

  private final BoardPrinter printer;
  private final CommandInterface input;
  private final PrintStream output;
  private volatile boolean scannerBlocked = false;

  /**
   * Constructs a new console player with a board printer.
   * 
   * @param printer Board printer for this console player to use
   * @param input   Command interface for reading commands with
   * @param output  Output print stream for messages
   */
  public ConsolePlayer(BoardPrinter printer, CommandInterface input, PrintStream output) {
    this.printer = printer;
    this.input = input;
    this.output = output;
  }

  /**
   * Constructs a new console player using the system output stream as the output.
   * 
   * @param printer Board printer for this console player to use
   */
  public ConsolePlayer(BoardPrinter printer) {
    this(printer, new CommandInterface(System.in), System.out);
  }

  /**
   * Ensures the session is a console session because the console player only
   * works with a console session.
   */
  @Override
  public void setupPlayer(Session session, Color color) {
    if (!(session instanceof ConsoleSession)) {
      throw new IllegalArgumentException("ConsolePlayer only works with ConsoleSession or subclass of it!");
    }
    super.setupPlayer(session, color);
  }

  /**
   * Checks if the opposing player is accessible (prints the output on their own).
   * If false, this player will also print updates for the game state after the
   * other player makes a move and not just for our own moves.
   * 
   * @return True if the other player is accessible
   */
  private boolean otherIsAccessible() {
    return session.getPlayerFor(color.getOpposing()).isAccessible();
  }

  /**
   * Shows the game state to the user and prints status messages.
   */
  @Override
  public void notifyStatus(TurnStatus status) {
    boolean otherIsAccessible = otherIsAccessible();
    boolean ownTurn = session.isActivePlayer(this);

    // don't print if it's the opponents turn now (to prevent echoing our own turn)
    if (ownTurn && !otherIsAccessible) {
      // print the last move if there is one
      SessionCommand lastCommand = session.getLastCommand();
      if (lastCommand instanceof MoveCommand) {
        output.print("!" + CommandInterface.moveToCommandString(((MoveCommand) lastCommand).getMove()) + "\n");
      }
    }

    // print the board before our own turn or if the other player doesn't print
    boolean printingForBoth = ownTurn || !otherIsAccessible;
    if (printingForBoth) {
      printer.printChessboard(session.getGame().getBoard());
    }

    // notify depending on turn status with optional own color in message
    String statusMessage = STATUS_MESSAGES.get(status);
    if (statusMessage != null && statusMessage.length() > 0 && (!status.doesAffectBoth() || printingForBoth)) {
      output.print(String.format(statusMessage, color.getPrettyName()) + "\n");
    }
  }

  /**
   * Used by console commands to print their messages.
   * 
   * @param message Message to print to the output
   */
  public void printMessage(String message) {
    output.print(message + "\n");
  }

  @Override
  public void requestCommand() {
    aborted = false;
    if (!isActivePlayer() && otherIsAccessible() || scannerBlocked) {
      return;
    }

    // run in fully synchronous mode if both are console players
    if (session.getPlayerFor(color.getOpposing()) instanceof ConsolePlayer) {
      requestCommandAsync();
    } else {
      super.requestCommand();
    }
  }

  /**
   * Reads a command from the command line and interprets it. Also prints feedback
   * to the console.
   */
  @Override
  public void requestCommandAsync() {
    // read commands from the console until a valid move has been found
    SessionCommand result;
    scannerBlocked = true;
    do {
      ConsoleCommand consoleCommand = input.readCommand();

      // stop if aborted in case of session stopping
      if (isAborted()) {
        return;
      }
      result = consoleCommand.applyToPlayer(this, (ConsoleSession) session);
    } while (result == null || !(isActivePlayer() || result.allowedFromInactivePlayer()));
    scannerBlocked = false;
    supplyCommand(result);
  }

  /**
   * Don't wait for the abort to complete because it's ok if it never completes.
   */
  @Override
  public void abortCommandRequest() {
    aborted = true;
  }
}
