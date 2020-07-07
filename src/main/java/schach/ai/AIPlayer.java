package schach.ai;

import schach.game.moves.Move;
import schach.interaction.ThreadedPlayer;
import schach.interaction.TurnStatus;
import schach.interaction.Session.SessionStatus;
import schach.interaction.commands.MoveCommand;

/**
 * The computer player calculates moves using an algorithmic strategy to search
 * through possible moves and evaluate their favorability for this player.
 */
public class AIPlayer extends ThreadedPlayer {
  private final MoveCalculator moveCalculator;

  /**
   * Constructs a new computer player and explicitly sets the move calculator to
   * use.
   * 
   * @param moveCalculator Move calculator for calculating moves
   */
  public AIPlayer(MoveCalculator moveCalculator) {
    this.moveCalculator = moveCalculator;
    moveCalculator.setPlayer(this);
  }

  /**
   * Constructs a computer player with the default move calculator settings but a
   * variable search depth.
   * 
   * @param maxDepth Maximum search depth to use
   */
  public AIPlayer(int maxDepth) {
    this(MoveCalculator.withFixedAlphaBeta(maxDepth));
  }

  /**
   * Constructs an ai player with the default search depth of 5.
   */
  public AIPlayer() {
    this(4);
  }

  /**
   * The computer player do anything upon notification. There's nothing to notify
   * since move calculation is entirely dependent on the concrete game state.
   */
  @Override
  public void notifyStatus(TurnStatus status) {
  }

  /**
   * The computer player calculates a move using the move calculator.
   */
  @Override
  public void requestCommandAsync() {
    if (isActivePlayer() && session.getStatus() == SessionStatus.RUNNING) {
      Move result = moveCalculator.findBestMove(session.getGame());
      if (!isAborted() && result != null) {
        supplyCommand(new MoveCommand(result));
      }
    }
  }
}
