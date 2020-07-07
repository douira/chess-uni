package schach.ai.strategies;

import schach.interaction.ThreadedPlayer;
import schach.interaction.TurnStatus;

/**
 * A fake Threaded player for testing classes that check if the player has aborted
 *
 */
public class FakeThreadedPlayer extends ThreadedPlayer{
  private boolean aborted = false;
  
  @Override
  protected void requestCommandAsync() {
    //does nothing
    
  }

  @Override
  public void notifyStatus(TurnStatus status) {
    //does nothing
    
  }
  
  @Override
  public boolean isAborted() {
    return aborted;
  }
  
  public void setAborted(boolean aborted) {
    this.aborted = aborted;
  }
}
