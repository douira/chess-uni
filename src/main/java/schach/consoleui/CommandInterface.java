package schach.consoleui;

import java.io.InputStream;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

import schach.common.Constants;
import schach.game.moves.Move;
import schach.game.moves.MoveType;
import schach.game.moves.Movement;
import schach.interaction.GameMode;
import schach.common.Position;
import schach.consoleui.commands.PrintCapturedCommand;
import schach.consoleui.commands.ConsoleCommand;
import schach.consoleui.commands.ConsoleJournalCommand;
import schach.consoleui.commands.MovementCommand;
import schach.consoleui.commands.PrintingCommand;

/**
 * The CommandReader exists to manage all inputs given by the user and it parses
 * them into easy-to-use arguments.
 */
public class CommandInterface {
  private static final Map<Character, MoveType> PROMOTIONS = Map.of('Q', MoveType.PROMOTION_QUEEN, ' ',
      MoveType.PROMOTION_QUEEN, 'R', MoveType.PROMOTION_ROOK, 'B', MoveType.PROMOTION_BISHOP, 'N',
      MoveType.PROMOTION_KNIGHT);

  /**
   * Reverse the promotions map in order to be able to produce the names of
   * promotions for given promotion types
   */
  private static final Map<MoveType, Character> PROMOTION_NAMES = PROMOTIONS.entrySet().stream()
      .filter(entry -> entry.getKey() != ' ')
      .collect(Collectors.<Map.Entry<Character, MoveType>, MoveType, Character>toMap(Map.Entry::getValue,
          Map.Entry::getKey));

  private final Scanner scanner;

  /**
   * Constructs a new command interface that scans the given input stream.
   * 
   * @param inputStream Input stream to scan for commands
   */
  public CommandInterface(InputStream inputStream) {
    scanner = new Scanner(inputStream);
  }

  /**
   * Reads the next command from the console and parses it.
   * 
   * @return Command parsed from user input that the user entered
   */
  public ConsoleCommand readCommand() {
    return parseInput(scanner.nextLine());
  }

  /**
   * Reads an init fromm and from the console.
   * 
   * @return Init command with the chosen game mode
   */
  public GameMode readInitCommand() {
    return parseInitInput(scanner.nextLine());
  }

  /**
   * Parses the given input as a init command for choosing the game mode.
   * 
   * @param input String to parse as a command
   * @return Init command that the user entered
   */
  public static GameMode parseInitInput(String input) {
    return GameMode.getWithName(input);
  }

  /**
   * Parses the given input and returns the resulting command.
   * 
   * @param input String to parse as a command
   * @return The move that the user entered
   */
  public static ConsoleCommand parseInput(String input) {
    switch (input) {
      case "beaten":
        return PrintCapturedCommand.INSTANCE;
      case "undo":
        return ConsoleJournalCommand.UNDO;
      case "redo":
        return ConsoleJournalCommand.REDO;
      default:
        // otherwise try to parse a movement and validate the constructed move
        Movement movement = parseMovement(input);
        return movement == null ? PrintingCommand.INVALID_INPUT : new MovementCommand(movement, input);
    }
  }

  /**
   * Converts a char from the alphabet to an integer number, starting at 1.
   * 
   * @param letter Character to convert
   * @return Value of the character, 1-based index in the alphabet
   */
  private static int alphabetToInteger(char letter) {
    int index = Constants.ALPHABET.indexOf(letter);
    return index == -1 ? 0 : index + 1;
  }

  /**
   * This method converts an input String into a move, if any position is
   * not/wrongly specified a 0 will be written to that position.
   * 
   * @param inputString The string given by the user
   * @return A newly constructed move
   */
  public static Movement parseMovement(String inputString) {
    // split the user input into parts to parse as coordinates
    String[] input = inputString.split("-");
    int xFrom = -1;
    int yFrom = -1;
    int xTo = -1;
    int yTo = -1;
    char promotionChar = ' ';

    // stop if string is not long enough or the parts are not long enough
    if (input.length != 2 || input[0].length() < 2 || input[1].length() < 2) {
      return null;
    }

    // parse the parts as coordinates
    if (Character.isDigit(input[1].charAt(input[1].length() - 1))) {
      input[1] += " ";
    } else {
      promotionChar = input[1].charAt(input[1].length() - 1);
    }

    xFrom = alphabetToInteger(input[0].charAt(0));
    xTo = alphabetToInteger(input[1].charAt(0));

    // if the string can't be parsed as an int signal invalid
    try {
      yFrom = Integer.parseInt(input[0].substring(1, input[0].length()));
      yTo = Integer.parseInt(input[1].substring(1, input[1].length() - 1));
    } catch (NumberFormatException e) {
      return null;
    }

    // add the promotion type if possible and stop if invalid
    MoveType promotion = PROMOTIONS.get(promotionChar);
    if (promotion == null) {
      return null;
    }

    // construct a move with the parsed position data,
    // transform the coordinate space to use the correct offsets and origin
    Position from = new Position(xFrom - 1, Constants.BOARD_SIZE - yFrom);
    Position to = new Position(xTo - 1, Constants.BOARD_SIZE - yTo);
    if (from.outOfBounds() || to.outOfBounds()) {
      return null;
    }

    Movement movement = new Movement(from, to);
    movement.setMoveType(promotion);
    return movement;
  }

  /**
   * Converts a given move into a command string to print to the console for
   * displaying the move as text.
   * 
   * @param move Move to return as a command string
   * @return Command string for this move
   */
  public static String moveToCommandString(Move move) {
    Position origin = move.getOriginPosition();
    Position target = move.getTargetPosition();
    Character promotion = PROMOTION_NAMES.get(move.getMoveType());
    return origin.toCommandString() + "-" + target.toCommandString() + (promotion == null ? "" : promotion.toString());
  }
}
