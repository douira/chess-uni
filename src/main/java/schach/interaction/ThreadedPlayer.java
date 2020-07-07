package schach.interaction;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import schach.common.NamedThreadFactory;

/**
 * A player that runs the implementing class in a separate task. Doesn't deal
 * with asynchronous updates since we don't have a usecase for that.
 */
public abstract class ThreadedPlayer extends Player {
  private static ExecutorService executorService = Executors.newCachedThreadPool(new NamedThreadFactory("Player-PT"));

  protected volatile boolean aborted = false;
  private Future<?> commandFuture;

  public boolean isAborted() {
    return aborted;
  }

  /**
   * Implemented by subclasses with the long-running work method or a method that
   * waits for user input for a while. If isAborted returns true at some point the
   * subclass should try and return as soon as possible and not call
   * supplyCommand.
   */
  protected abstract void requestCommandAsync();

  /**
   * When the session requests a command this player creates a task to get the
   * command with the method implemented by the subclass.
   */
  @Override
  public void requestCommand() {
    aborted = false;
    commandFuture = executorService.submit(() -> {
      try {
        this.requestCommandAsync();
      } catch (Exception e) {
        e.printStackTrace();
      }
    });
  }

  /**
   * Tells the running task to not supply a command. Waits for the command future
   * to be done to ensure the session only proceeds once the command computation
   * has actually been interrupted.
   */
  @Override
  public void abortCommandRequest() {
    aborted = true;
    if (commandFuture != null) {
      try {
        commandFuture.get();
      } catch (ExecutionException e) {
        e.printStackTrace();
        return;
      } catch (InterruptedException e) {
        return;
      } finally {
        commandFuture = null;
      }
    }
  }
}
