package schach.game.state;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import schach.game.moves.Move;

/**
 * Models the move history of a game state and handles advances reversing and
 * re-doing operations including future moves.
 * 
 * PMD: We need to use the specific type LinkedList because it's impossible to
 * define a type that implements more than one interface. We could hold two
 * separate references to the same list but with different interface-types but
 * that would not be an elegant solution either.
 */
@SuppressWarnings("PMD.LooseCoupling")
public class MoveJournal {
  private final GameState gameState;

  /**
   * Store a history of the moves to allow reverting moves.
   */
  private LinkedList<Move> history = new LinkedList<>();

  /**
   * A stack of reversed moves that is built if the user reverses a move. This
   * allows reversed moves to be re-applied.
   */
  private LinkedList<Move> future = new LinkedList<>();

  /**
   * Constructs a new move history with the given parent game state.
   * 
   * @param gameState Game state to manipulate in response to do/undo requests
   */
  public MoveJournal(GameState gameState) {
    this.gameState = gameState;
  }

  /**
   * Returns the history list as an unmodifiable list
   * 
   * @return History list of moves
   */
  public List<Move> getHistory() {
    return Collections.unmodifiableList(history);
  }

  /**
   * Returns the future list as an unmodifiable list
   * 
   * @return Future list of moves
   */
  public List<Move> getFuture() {
    return Collections.unmodifiableList(future);
  }

  /**
   * Returns the move index of the last move on the stack. Move index 1 is the
   * first move while move index 0 means no move has been made yet.
   * 
   * @return Move index of the latest move
   */
  private int getMoveIndex() {
    return history.size();
  }

  /**
   * Returns the move index for the next move that's applied. This needs to be
   * used in the game state since the move index is relevant for accumulating
   * moves even if they're not added to the journal history (but are added to the
   * board's piece history).
   * 
   * @return Next move index to set on a move
   */
  int getNextMoveIndex() {
    return getMoveIndex() + 1;
  }

  /**
   * Checks if the given move index corresponds to the current move index. Returns
   * true if the move with the given index is the last applied move.
   * 
   * @param index Move index to check
   * @return True if the index is the last move index
   */
  public boolean isLatestMoveIndex(int index) {
    return getMoveIndex() == index;
  }

  /**
   * Applies the given move to the game state and adds it to the history. This
   * expects the future to be handled externally.
   * 
   * @param move Move to apply
   */
  private void doKnownMove(Move move) {
    gameState.applyMove(move);
    history.push(move);
  }

  /**
   * Regularly applies a given move. This clears the move future because it's
   * invalidated by applying a foreign move.
   * 
   * @param move Move to apply
   */
  public void doMove(Move move) {
    doKnownMove(move);
    future.clear();
  }

  /**
   * Undoes the last applied move and adds it to the move future. This allows it
   * to be redone at a later time.
   */
  public void undoMove() {
    Move historyMove = history.pop();
    future.push(historyMove);
    gameState.reverseMove(historyMove);
  }

  /**
   * Redoes the last undone move. Does nothing if no move to redo exists.
   */
  public void redoMove() {
    if (!future.isEmpty()) {
      doKnownMove(future.pop());
    }
  }

  /**
   * Undoes moves until the last applied move on the stack is the given move.
   * Undoes all moves if the given move never appears in the stack or is null.
   * 
   * @param newLatestMove Move to be the new last applied move
   */
  public void undoUntil(Move newLatestMove) {
    while (history.peek() != newLatestMove) {
      undoMove();
    }
  }

  /**
   * Redoes moves until the last applied move is the given move. The given move
   * will be applied last if it's in the future stack. Does nothing if the future
   * is empty. If the given move is not in the future or is null, the entire
   * future will be redone.
   * 
   * @param newLatestMove Move to be the new last applied move
   */
  public void redoUntil(Move newLatestMove) {
    while (!future.isEmpty() && history.peek() != newLatestMove) {
      redoMove();
    }
  }
}
