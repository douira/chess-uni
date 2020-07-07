package schach.game.accumulators;

import schach.common.Position;
import schach.game.accumulators.filters.MoveFilter;
import schach.game.moves.Move;
import schach.game.state.Board;
import schach.game.state.GameState;

/**
 * A move accumulator is given a game state and is then passed into the pieces
 * to have them generate moves and add them to this accumulator. How each
 * accumulator type deals with a new move depends on its purpose.
 */
public abstract class MoveAccumulator {
  protected GameState gameState;
  private Position position;

  /**
   * Sets the game state on this accumulator after construction. The game state
   * isn't set during construction since accumulators are passed to the game state
   * without having been constructed with a game state.
   * 
   * @param gameState Game state to set on this accumulator
   */
  public void setGameState(GameState gameState) {
    this.gameState = gameState;
  }

  /**
   * Gets the game state saved in this accumulator. This is called by pieces when
   * they need to check the position of other pieces in the game state when
   * generating moves.
   * 
   * @return Game state we are accumulating moves for
   */
  public GameState getGameState() {
    return gameState;
  }

  /**
   * Gets the board from the contained game state.
   * 
   * @return Board of the game state
   */
  public Board getBoard() {
    return gameState.getBoard();
  }

  /**
   * Sets the piece position for the piece to use during accumulation. When a
   * piece starts accumulation it sets this position so its deeper methods can
   * access the combined game state and position information.
   * 
   * @param position Position to set for the piece
   */
  public void setPosition(Position position) {
    this.position = position;
  }

  public Position getPosition() {
    return position;
  }

  /**
   * Constructs a filter chain with the given array of filters. The first filter
   * is given the second filter as its inner accumulator and the second is given
   * the third and so on.
   * 
   * @param filters Array of filters to chain with this accumulator at the end.
   * @return Move accumulator that has all the filters
   */
  public MoveAccumulator withFilters(MoveFilter... filters) {
    if (filters.length == 0) {
      return this;
    }

    // chain the filters in the order they were given
    MoveFilter outer = filters[filters.length - 1];
    outer.setAccumulator(this);

    // don't do complicated chaining if there's only one filter
    if (filters.length > 1) {
      for (int i = filters.length - 2; i >= 0; i--) {
        // get the new outer filter and put it around the last one
        MoveFilter newOuter = filters[i];
        newOuter.setAccumulator(outer);
        outer = newOuter;
      }
    }
    return outer;
  }

  /**
   * Adds a move to this accumulator. The implementation determines what happens
   * to moves when they're added. This method can also be used to stop
   * accumulation of moves prematurely if no more moves are necessary or wanted.
   * 
   * @param move Move to add to the accumulator
   * @return If false, stop adding moves to the accumulator if possible
   */
  public abstract boolean addMove(Move move);

  /**
   * Returns if more moves should be generated. This can be used when the return
   * value from addMove can't be passed out of the enclosing method but this
   * accumulator is available further up the call chain.
   * 
   * @return If more moves should be generated and added to this accumulator
   */
  public abstract boolean generateMore();

  /**
   * Returns if non-attacking moves should be generated. If false, moves that
   * aren't capturing moves don't need to be generated since they aren't
   * interesting to the accumulator.
   * 
   * @return If non-attacking moves should even be considered
   */
  public abstract boolean generateNonAttacking();
}
