package schach.interaction;

import java.util.Map;

import schach.common.Color;
import schach.common.Utils;
import schach.game.moves.Move;
import schach.game.state.GameState;
import schach.game.state.GameStatus;
import schach.interaction.commands.SessionCommand;

/**
 * A session manages the interaction between two players and the game status.
 */
public class Session {
  private GameState game;
  private Map<Color, Player> players;
  private Player activePlayer = null;
  private SessionCommand lastCommand = null;
  private volatile SessionCommand collectedCommand;
  private volatile Player commandAuthor;
  protected volatile SessionStatus status = SessionStatus.INIT;

  /**
   * The states a game session can have.
   */
  public enum SessionStatus {
    /**
     * INIT means the session is not started yet.
     */
    INIT,

    /**
     * RUNNING means the session is active and there is an active palyer.
     */
    RUNNING,

    /**
     * FINISHED means the session is still active but there is no active player
     * (game has ended but undo/redo possible).
     */
    FINISHED,

    /**
     * STOPPED means that loopTurns has finished and no commands are being requested
     * from the players at all anymore.
     */
    STOPPED;
  }

  /**
   * Sets the players of this session and starts the session's playing phase. This
   * can also be used for resetting this session for a new game.
   * 
   * @param game        Game state to play on
   * @param whitePlayer Player with the white pieces, goes first
   * @param blackPlayer Player with the black pieces
   */
  protected void start(GameState game, Player whitePlayer, Player blackPlayer) {
    // if we're already running, stop first
    if (status == SessionStatus.RUNNING) {
      stop();
    }

    this.game = game;
    whitePlayer.setupPlayer(this, Color.WHITE);
    blackPlayer.setupPlayer(this, Color.BLACK);
    players = Map.of(Color.WHITE, whitePlayer, Color.BLACK, blackPlayer);
    status = SessionStatus.RUNNING;
    updateActivePlayer();
  }

  /**
   * Sets the players of this session without passing a game state. A game state
   * is generated automatically.
   * 
   * @param whitePlayer Player with the white pieces, goes first
   * @param blackPlayer Player with the black pieces
   */
  protected void start(Player whitePlayer, Player blackPlayer) {
    start(new GameState(), whitePlayer, blackPlayer);
  }

  /**
   * Stops the session and aborts all the players who might still be supplying
   * commands.
   */
  synchronized private void stop() {
    status = SessionStatus.INIT;
    for (Player player : players.values()) {
      player.abortCommandRequest();
    }

    // notify to stop waiting for commands from players
    notify();
    Utils.waitUntil(this, (sync) -> status == SessionStatus.STOPPED);
  }

  public GameState getGame() {
    return game;
  }

  public SessionCommand getLastCommand() {
    return lastCommand;
  }

  public Map<Color, Player> getPlayers() {
    return players;
  }

  public SessionStatus getStatus() {
    return status;
  }

  /**
   * Applies the given move to the session and saves the last move.
   * 
   * @param move Move to apply
   */
  public void doMove(Move move) {
    game.doMove(move);
  }

  /**
   * Stops the current game and halts all calculations.
   */
  public void stopGame() {
    status = SessionStatus.STOPPED;
  }

  /**
   * Checks if this move was made by a player that is accessible.
   * 
   * @param move Move to check
   * @return If the given move was made by an accessible player
   */
  public boolean checkMoveAccessible(Move move) {
    return players.get(move.getByColor()).isAccessible();
  }

  /**
   * Returns the player for a specific color.
   * 
   * @param color Color of the player to return
   * @return Player with the given color
   */
  public Player getPlayerFor(Color color) {
    return players.get(color);
  }

  /**
   * Returns the current active player in the game. This is a cached value of the
   * active player since the active color of the game mode may change during
   * computation by a player. This value is updated before the players are
   * notified.
   * 
   * @return Player that is making the next move
   */
  public Player getActivePlayer() {
    return status == SessionStatus.STOPPED ? null : activePlayer;
  }

  /**
   * Checks if the player who was active last is accessible.
   * 
   * @return True if the last active player was accessible
   */
  public boolean lastActivePlayerAccessible() {
    return activePlayer != null && activePlayer.isAccessible();
  }

  /**
   * Checks if the given player is the currently active player.
   * 
   * @param player Player to check
   * @return True if the given player is the currently active player
   */
  public boolean isActivePlayer(Player player) {
    return player != null && player == getActivePlayer();
  }

  /**
   * Updates the session status if the game is still running using the game
   * outcome. Also reactives the session to running if the game is running again
   * (because of undo).
   */
  private void updateSessionStatus() {
    // if the game is still running
    GameStatus gameStatus = game.getStatus();
    if (status == SessionStatus.RUNNING && (gameStatus == GameStatus.IN_CHECKMATE || gameStatus == GameStatus.DRAW)) {
      status = SessionStatus.FINISHED;
    } else if (status == SessionStatus.FINISHED && !gameStatus.gameIsStopped()) {
      status = SessionStatus.RUNNING;
    }
  }

  /**
   * Updates the active player using the active game color. The active player may
   * be null if the session is not running.
   */
  private void updateActivePlayer() {
    activePlayer = getPlayerFor(game.getActiveColor());
  }

  /**
   * Notifies all players with a turns status and this session as the context.
   */
  private void notifyAllPlayers() {
    GameStatus statusBefore = game.getStatus();
    for (Map.Entry<Color, Player> entry : players.entrySet()) {
      Player player = entry.getValue();

      // also pass the opposing player for correct negotiation of what needs to be
      // displayed
      player.notifyStatus(TurnStatus.fromSessionData(status, statusBefore, player == activePlayer));
    }
  }

  /**
   * This is called by players to supply the session with a command to execute.
   * 
   * @param fromPlayer Player supplying the command
   * @param command    Command to supply to the session
   */
  synchronized public void supplyCommand(Player fromPlayer, SessionCommand command) {
    // don't allow the collected command to be overwritten by a spurious method call
    if (collectedCommand == null) {
      commandAuthor = fromPlayer;
      collectedCommand = command;
      notify();
    }
  }

  /**
   * Gathers a command from the players by requesting a command and then waiting
   * for an answer.
   */
  private void gatherCommand() {
    // request commands from the players and then wait
    for (Player player : players.values()) {
      player.requestCommand();
    }

    // don't wait if condition already fulfilled by non-async player
    if (!(collectedCommand != null || status == SessionStatus.INIT)) {
      Utils.waitUntil(this, (sync) -> {
        return sync.collectedCommand != null || sync.status == SessionStatus.INIT;
      });
    }

    // abort the other player to stop them from generating a command now
    if (commandAuthor != null) {
      getPlayerFor(commandAuthor.getColor().getOpposing()).abortCommandRequest();
    }
  }

  /**
   * Executes a single turn for the current active player. Both players are
   * notified with the current turn state and then the active player returns a
   * move that's executed. This method must be called one last time after a
   * winning move is made to set the session status to finished.
   * 
   * @return If the game is still running
   */
  public boolean executeTurn() {
    // stop if in invalid state for turns
    if (status == SessionStatus.INIT) {
      throw new IllegalStateException("This session needs to be initialized before turns can be executed!");
    }

    updateActivePlayer();
    updateSessionStatus();
    notifyAllPlayers();

    if (status == SessionStatus.STOPPED) {
      return false;
    }

    gatherCommand();

    // if the session status is init, the session is being restarted
    // tell the other thread that we've stopped
    if (status == SessionStatus.INIT) {
      synchronized (this) {
        status = SessionStatus.STOPPED;
        notify();
      }
      return false;
    }

    if (!isActivePlayer(commandAuthor) && !collectedCommand.allowedFromInactivePlayer()) {
      throw new IllegalArgumentException(
          "The session command supplied by the inactive player is not allowed for inactive players.");
    }

    collectedCommand.applyTo(this);
    lastCommand = collectedCommand;
    collectedCommand = null;
    commandAuthor = null;
    return true;
  }

  /**
   * Executes turns until the game is finished.
   */
  public void loopTurns() {
    while (true) {
      if (!executeTurn()) {
        break;
      }
    }
  }
}
