package schach.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Function;

/**
 * A collections of generic utility methods used throughout the project. Deep
 * copy refers to a copy of the references, not the referenced objects
 * themselves. This means changing the contents of the original map doesn't
 * affect the new map but changing the contained objects will since they're
 * still the same instances.
 */
public final class Utils {
  // prevent instances
  private Utils() {
  }

  /**
   * Combines two collections into one larger collection. The order of the
   * collections is preserved since an ArrayList with just enough fields to hold
   * all items is created and then filled with the items from the first and second
   * given collection.
   *
   * @param <E>    Type of the elements of the collections
   * @param first  First collection to combine
   * @param second Second collection to combine
   * @return A new collection that encompasses the items from the two given
   *         collections
   */
  public static <E> Collection<E> concatCollections(Collection<E> first, Collection<E> second) {
    Collection<E> accumulator = new ArrayList<E>(first.size() + second.size());
    accumulator.addAll(first);
    accumulator.addAll(second);
    return accumulator;
  }

  /**
   * Synchronizes and waits on an object until the object is notified and the
   * condition is met.
   * 
   * @param <SyncObject> Type of the object to synchronize on
   * @param sync         Object to sync
   * @param condition    Condition to wait for
   */
  public static <SyncObject> void waitUntil(SyncObject sync, Function<SyncObject, Boolean> condition) {
    // wait until the condition is reached, sync on the sync object
    synchronized (sync) {
      while (!condition.apply(sync)) {
        try {
          sync.wait();
        } catch (InterruptedException e) {
          // we don't care if we're interrupted
        }
      }
    }
  }

  /**
   * Normalizes a double value to be the normal zero and not negative zero.
   * 
   * @param value Value to normalize
   * @return Same value but -0 is now 0
   */
  public static double normalizeZero(double value) {
    return value == -0d ? 0d : value;
  }
}
