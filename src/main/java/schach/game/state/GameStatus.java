package schach.game.state;

/**
 * The different game statuses a game can have (for each color). Checkmate or a
 * draw in any player's turn cause the game to stop.
 */
public enum GameStatus {
  NONE(false), IN_CHECK(false), IN_CHECKMATE(true), DRAW(true);

  /**
   * If this game status causes the game to end.
   */
  private final boolean stopGame;

  /**
   * Constructs a game status with a flag determining if the game ends when this
   * game status is reached.
   * 
   * @param stop If the game has ended on reaching this status
   */
  GameStatus(boolean stop) {
    stopGame = stop;
  }

  /**
   * Returns if this game state means the game is stopped.
   * 
   * @return If the game is stopped
   */
  public boolean gameIsStopped() {
    return stopGame;
  }
}
