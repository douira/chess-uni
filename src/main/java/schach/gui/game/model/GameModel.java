package schach.gui.game.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import schach.common.Color;
import schach.game.moves.Move;
import schach.game.pieces.Piece;
import schach.game.state.GameState;
import schach.game.state.JournalDirection;
import schach.game.state.MoveJournal;
import schach.gui.GUIPlayer;
import schach.interaction.Session;
import schach.interaction.TurnStatus;
import schach.interaction.commands.AbortGameCommand;
import schach.interaction.commands.JournalJumpCommand;
import schach.interaction.commands.JournalStepCommand;
import schach.interaction.commands.MoveCommand;
import schach.interaction.commands.RedoCommand;
import schach.interaction.commands.UndoCommand;

/**
 * The gameModel of the gui is the class that represents the model in the mvvm
 * structure, it manages the chessboardModel and also is the interface to the
 * gui session
 *
 */
public class GameModel {
  private ObservableList<Piece> capturedPieces = FXCollections.observableArrayList();
  private ObservableList<Move> moveHistory = FXCollections.observableArrayList();
  private ObservableList<Move> moveFuture = FXCollections.observableArrayList();
  private StringProperty status = new SimpleStringProperty("");
  private ChessboardModel chessboardModel;
  private GUIPlayer activeGuiPlayer;
  private Session session;
  private boolean guiTurn = false;
  private Color activeColor = Color.WHITE;

  /**
   * The constructor of class, it Initializes the chessboardModel
   */
  public GameModel() {
    chessboardModel = new ChessboardModel(this);
  }

  public boolean isGuiTurn() {
    return guiTurn;
  }

  /**
   * Returns the current GameState, throws an exception when it is an illegal time
   * to access the gui
   * 
   * @return Current gameState
   */
  public GameState getGameState() {
    if (!guiTurn) {
      throw new IllegalArgumentException(
          "Illegal access to gameState, the gui is not allowed to access the gameState at this point");
    }
    return session == null ? new GameState() : session.getGame();
  }

  /**
   * This method does not need to check if its the guis turn, as the move journal
   * will only be read
   * 
   * @return the current move journal
   */
  public MoveJournal getJournal() {
    return session == null ? new GameState().getJournal() : session.getGame().getJournal();
  }

  public Color getActiveColor() {
    return activeColor;
  }

  public StringProperty getStatus() {
    return status;
  }

  public ObservableList<Piece> getCapturedPieces() {
    return capturedPieces;
  }

  public ChessboardModel getChessboardModel() {
    return chessboardModel;
  }

  public ObservableList<Move> getMoveHistory() {
    return moveHistory;
  }

  public ObservableList<Move> getMoveFuture() {
    return moveFuture;
  }

  /**
   * Supplys the move and direction to the session to jump to a different position
   * in the move journal
   * 
   * @param move      the move to jump to
   * @param direction the wanted journal direction
   */
  public void clickMoveInJournalDisplay(Move move, JournalDirection direction) {
    if (move == null && session.getPlayerFor(Color.WHITE).isAccessible()
        || session.getPlayerFor(Color.BLACK).isAccessible() && session.getPlayerFor(Color.WHITE).isAccessible()
        || move != null && !session.getPlayerFor(move.getByColor()).isAccessible()) {
      supplyMoveHistoryJump(move, direction);
    }
  }

  /**
   * Tells the session to undo the last move
   */
  public void undoMove() {
    supplyMoveHistoryStep(true);
  }

  /**
   * Tells the session to redo the last move
   */
  public void redoMove() {
    supplyMoveHistoryStep(false);
  }

  /**
   * Updates the captured Pieces
   */
  public void updateCapturedPieces() {
    capturedPieces.clear();
    capturedPieces.addAll(getGameState().getBoard().getSortedCapturedPieces());
  }

  /**
   * Updates everything that is displayed on the gameView
   */
  public void updateAll() {
    activeColor = getGameState().getActiveColor();
    status.set(getGameState().getStatus().name());
    chessboardModel.removeHelp();
    chessboardModel.updateMarkers();
    chessboardModel.updatePiecesOnBoard();
    updateMoveHistory();
    updateCapturedPieces();
  }

  /**
   * Rewrites the move history with the current journal from the gameState
   */
  public void updateMoveHistory() {
    moveHistory.clear();
    moveFuture.clear();
    MoveJournal journal = getGameState().getJournal();
    moveHistory.addAll(journal.getHistory());
    moveFuture.addAll(journal.getFuture());
  }

  /**
   * Notifies the game model that the game state given here should be displayed
   * since it has been modified by a player in the last turn (or at the beginning
   * of the game). This method is called by the GUIPlayer at the beginning of
   * every turn and once after the end of the game.
   * 
   * @param guiPlayer Reference to the gui player that is updating the gui
   * @param session   Session this game is being played on, provides the game
   *                  state and the currently active player.
   * @param status    Status of this player in the turn
   */
  public void updateGameState(GUIPlayer guiPlayer, Session session, TurnStatus status) {
    if (activeGuiPlayer == null) {
      activeGuiPlayer = guiPlayer;
    }

    guiTurn = true;
    this.session = session;
    chessboardModel.setlastClickedMyPiece(false);
    updateAll();
    guiTurn = false;
  }

  /**
   * Called by a gui player to notify this model of the fact that a command for
   * the gui player should be sent when the user enters a move. Making a command
   * should only be possible after this method has been called and only until the
   * made command is sent back to the gui player using player.supplyMove(move)
   * 
   * @param player Player to send the move back to at some point, note that this
   *               reference may change between calls if a human vs human game is
   *               happening.
   */
  public void requestActiveCommand(GUIPlayer player) {
    guiTurn = true;
    activeGuiPlayer = player;
  }

  /**
   * Stops getting an action from the GUI. If this is called, the GUI will never
   * supply a command to the GUIPlayer
   */
  public void finishGettingAction() {
    guiTurn = false;
  }

  /**
   * Called by another part of the GUI with the Move that should be made. The
   * command will be passed to the saved gui player instance allowing it to
   * continue in its thread.
   * 
   * @param move Move to give the gui player in the main thread
   */
  public void supplyPlayerMove(Move move) {
    finishGettingAction();
    chessboardModel.resetLastClicked();
    activeGuiPlayer.supplyCommand(new MoveCommand(move));
  }

  /**
   * Called by another part of the GUI with the move and direction to revert or
   * fast forward to The command will be passed to the saved gui player instance
   * allowing it to continue in its thread.
   * 
   * @param move      Move to revert back or jump forward to
   * @param direction Direction to indicate which way to go in the move History
   */
  public void supplyMoveHistoryJump(Move move, JournalDirection direction) {
    finishGettingAction();
    chessboardModel.resetLastClicked();
    activeGuiPlayer.supplyCommand(new JournalJumpCommand(move, direction));
  }

  /**
   * Called by the gui to either redo or undo a move. The command will be passed
   * to the saved gui player instance allowing it to continue in its thread.
   * 
   * @param undo true if move should be undone
   */
  public void supplyMoveHistoryStep(boolean undo) {
    JournalStepCommand stepCommand = undo ? UndoCommand.INSTANCE : RedoCommand.INSTANCE;
    if (stepCommand.canApplyTo(session)) {
      finishGettingAction();
      chessboardModel.resetLastClicked();
      activeGuiPlayer.supplyCommand(stepCommand);
    }
  }

  /**
   * Called by the gui to tell the session that the game has been aborted
   */
  public void supplyAbbortGameCommand() {
    finishGettingAction();
    activeGuiPlayer.supplyCommand(new AbortGameCommand());
  }
}
