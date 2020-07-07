package schach.game.accumulators;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;

import schach.game.moves.Move;

/**
 * Accumulates moves into a given type of collection. This is used for producing
 * a list of moves.
 * 
 * @param <CollectionType> Type of the collection to deal with
 */
public class CollectionAccumulator<CollectionType extends Collection<Move>> extends MoveAccumulator {
  private CollectionType moves;

  /**
   * Constructs a new accumulator with a given collection instance of the correct
   * type. Moves are added to the given collection.
   * 
   * @param collection Collection instance to add moves to
   */
  public CollectionAccumulator(CollectionType collection) {
    super();
    moves = collection;
  }

  /**
   * Convenience method that creates a collection accumulator using a linked list.
   * 
   * @return Collection accumulator with a linked list
   */
  public static CollectionAccumulator<LinkedList<Move>> withLinkedList() {
    return new CollectionAccumulator<LinkedList<Move>>(new LinkedList<Move>());
  }

  /**
   * Convenience method that creates a collection accumulator using an array list.
   * 
   * @return Collection accumulator with a array list
   */
  public static CollectionAccumulator<ArrayList<Move>> withArrayList() {
    return new CollectionAccumulator<ArrayList<Move>>(new ArrayList<Move>());
  }

  /**
   * Convenience method that creates a collection accumulator using a hash set.
   * 
   * @return Collection accumulator with a hash set
   */
  public static CollectionAccumulator<HashSet<Move>> withHashSet() {
    return new CollectionAccumulator<HashSet<Move>>(new HashSet<Move>());
  }

  /**
   * Returns the collection used for storing the moves. This should be called when
   * all the pieces are finished accumulating moves into this accumulator and the
   * collection contains all legal moves any piece can make.
   * 
   * @return Collection containing all accumulated moves
   */
  public CollectionType getMoves() {
    return moves;
  }

  /**
   * A collection accumulator wants to get all possible moves and therefore
   * doesn't stop accumulation of moves prematurely.
   */
  @Override
  public boolean addMove(Move move) {
    moves.add(move);
    return true;
  }

  /**
   * Since the collection accumulator wants to collect all moves it always signals
   * to generate more moves.
   */
  @Override
  public boolean generateMore() {
    return true;
  }

  @Override
  public boolean generateNonAttacking() {
    return true;
  }
}
