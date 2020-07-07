package schach.interaction.commands;

/**
 * Journal commands are allowed from inactive players.
 */
public abstract class JournalCommand implements SessionCommand {
  @Override
  public boolean allowedFromInactivePlayer() {
    return true;
  }
}
