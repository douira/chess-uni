package schach.consoleui.commands;

import schach.consoleui.ConsolePlayer;
import schach.consoleui.ConsoleSession;
import schach.game.moves.Move;
import schach.game.moves.Movement;
import schach.interaction.commands.MoveCommand;
import schach.interaction.commands.SessionCommand;

/**
 * A command that contains a movement.
 */
public class MovementCommand implements ConsoleCommand {
  private final Movement movement;
  private final String source;

  /**
   * Creates a new movement command with the given movement.
   * 
   * @param movement Movement to perform
   * @param source   String this movement was parsed from
   */
  public MovementCommand(Movement movement, String source) {
    this.movement = movement;
    this.source = source;
  }

  /**
   * Apply the movement command
   */
  @Override
  public SessionCommand applyToPlayer(ConsolePlayer player, ConsoleSession session) {
    Move move = session.getGame().validateMove(movement);
    if (move == null) {
      return PrintingCommand.MOVE_ILLEGAL.applyToPlayer(player, session);
    } else {
      // echo the move source and return the move as the result
      player.printMessage("!" + source);
      return new MoveCommand(move);
    }
  }
}
