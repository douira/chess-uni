package schach.common;

import java.util.concurrent.ThreadFactory;

/**
 * Produces threads with the given name and a counter suffix.
 */
public class NamedThreadFactory implements ThreadFactory {
  private final String baseName;
  private int counter = 0;

  /**
   * Creates a new named thread factory that names threads with the base name.
   * 
   * @param baseName Base name to prepend to all thread names
   */
  public NamedThreadFactory(String baseName) {
    this.baseName = baseName;
  }

  @Override
  public Thread newThread(Runnable r) {
    return new Thread(r, baseName + "-" + counter++);
  }
}
