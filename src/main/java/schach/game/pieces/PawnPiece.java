package schach.game.pieces;

import schach.common.Color;
import schach.common.Position;
import schach.common.Vector;
import schach.game.accumulators.MoveAccumulator;
import schach.game.moves.CapturingMove;
import schach.game.moves.DoubleMove;
import schach.game.moves.Move;
import schach.game.moves.MoveType;
import schach.game.moves.Movement;
import schach.game.moves.PromotionMove;
import schach.game.state.GameState;

/**
 * The pawn piece can move one or initially two squares vertically up or down
 * depending on its color without obstruction. It can only capture by moving
 * diagonally in it's designated direction.
 */
public class PawnPiece extends HistoryPiece {
  /**
   * Constructs a pawn piece with the given color. The color of a pawn affects in
   * which direction it can move.
   * 
   * @param color Color of the new piece
   */
  public PawnPiece(Color color) {
    super(color);
    type = PieceType.PAWN;
  }

  /**
   * Checks if the given en passant move specified by the capturing offset (left
   * or right) can be performed. The piece at the capturing offset needs to have
   * just moved there. If all conditions are fulfilled, this pawn accumulates a
   * double move consisting of the capturing move and a simple step forwards.
   * 
   * @param accumulator     Accumulator to add generated moves to
   * @param capturingOffset Offset for targeting the piece to be captured
   */
  private boolean accumulateEnPassant(MoveAccumulator accumulator, Vector capturingOffset) {
    // get the piece at the capture position and make sure it's a pawn we can
    // capture
    Position from = accumulator.getPosition();
    GameState gameState = accumulator.getGameState();
    Position capturePosition = Position.fromOffset(from, capturingOffset);
    Piece capturePiece = gameState.getPieceAt(capturePosition);
    if (!(canReplacePiece(capturePiece) && capturePiece instanceof PawnPiece)) {
      return true;
    }
    PawnPiece capturePawn = (PawnPiece) capturePiece;

    // check that this pawn just moved there with a double move
    Move capturePawnLastMove = capturePawn.lastMove(accumulator);
    if (capturePawnLastMove != null && capturePawnLastMove.isLatestMoveOf(gameState)
        && capturePawnLastMove.hasMoveType(MoveType.PAWN_DOUBLE)) {
      // construct an en passe capturing move,
      // we can expect the square behind the captured piece to be free since it just
      // left that square in the last move
      Position afterCaptureStep = Position.fromOffset(capturePosition,
          OffsetPatterns.FORWARD_STEP.getDirectional(color));
      return accumulator.addMove(new DoubleMove(new CapturingMove(from, capturePosition),
          new Movement(capturePosition, afterCaptureStep), from, afterCaptureStep));
    }
    return true;
  }

  /**
   * Accumulates all possible promotion moves if at the right position. The check
   * for a promotion being necessary needs to have happened beforehand.
   * 
   * @param accumulator Accumulator to add found moves to
   * @param baseMove    Move to encapsulate in the promotion move
   * @return If more moves should be generated
   */
  private boolean accumulatePromotions(MoveAccumulator accumulator, Movement baseMove) {
    for (MoveType promotionType : MoveType.PROMOTION_TYPES) {
      if (!accumulator.addMove(new PromotionMove(baseMove, promotionType))) {
        return false;
      }
    }
    return true;
  }

  /**
   * Checks if the target location can be moved to. If the move there is possible
   * it's added to the moves collection. If there is a piece at the target square,
   * it can only be captured if a capturing offset is being checked (enabled
   * through the isCapturing flag).
   * 
   * @param accumulator Accumulator to add generated moves to
   * @param offset      Relative movement to the target field as a flip vector
   * @param moveType    Type of the move to create, used for later identification
   *                    of double moves
   * @param isCapturing If we are accumulating a capturing more or not
   * @return true if a move was added and more moves can be produced
   */
  private boolean accumulatePawnStep(MoveAccumulator accumulator, FlipVector offset, MoveType moveType,
      boolean isCapturing) {
    Position from = accumulator.getPosition();
    Position target = Position.fromOffset(from, offset.getDirectional(color));
    if (target.outOfBounds()) {
      return false;
    }

    // only allow capturing and non-capturing moves with their respective offsets
    Piece targetPiece = accumulator.getGameState().getPieceAt(target);
    if (isCapturing ? targetPiece != null && targetPiece.getColor() != color : targetPiece == null) {
      Movement move = constructMovement(from, target, targetPiece);

      // add as promotion if necessary or as a normal otherwise
      if (OffsetPatterns.PROMOTION_RANKS.get(color) == target.getY()) {
        return accumulatePromotions(accumulator, move);
      } else {
        move.setMoveType(moveType);
        accumulator.addMove(move);
        return true;
      }
    }
    return false;
  }

  /**
   * Finds and accumulates capturing moves for this pawn and adds them to the
   * given accumulator.
   * 
   * @param accumulator Accumulator to add found moves to
   * @return If more moves should be generated
   */
  private boolean accumulateCapturing(MoveAccumulator accumulator) {
    // accumulate the diagonal capturing moves
    for (FlipVector captureVector : OffsetPatterns.FORWARD_CAPTURE) {
      accumulatePawnStep(accumulator, captureVector, MoveType.PAWN_SIMPLE, true);
      if (!accumulator.generateMore()) {
        return false;
      }
    }

    // if at the right y position (rank), accumulate en passant moves
    if (accumulator.getPosition().getY() == (color == Color.WHITE ? 3 : 4)) {
      for (Vector capturingOffset : OffsetPatterns.EN_PASSANT) {
        if (!accumulateEnPassant(accumulator, capturingOffset)) {
          return false;
        }
      }
    }
    return true;
  }

  /**
   * Finds and accumulating normal non-capturing moves for this pawn and adds them
   * to the accumulator.
   * 
   * @param accumulator Accumulator to add found moves to
   * @return If more moves should be generated
   */
  private boolean accumulateNonCapturing(MoveAccumulator accumulator) {
    // accumulate the two vertical non-capturing moves
    boolean canMoveForward = accumulatePawnStep(accumulator, OffsetPatterns.FORWARD_STEP, MoveType.PAWN_SIMPLE, false);
    if (!accumulator.generateMore()) {
      return false;
    }

    // only check the double step move if the single step move is not obstructed
    if (canMoveForward && !hasMoved(accumulator)) {
      accumulatePawnStep(accumulator, OffsetPatterns.DOUBLE_FORWARD, MoveType.PAWN_DOUBLE, false);
      if (!accumulator.generateMore()) {
        return false;
      }
    }
    return true;
  }

  @Override
  boolean accumulateMoves(MoveAccumulator accumulator) {
    // accumulate non capturing moves if necessary
    if (accumulator.generateNonAttacking() && !accumulateNonCapturing(accumulator)) {
      return false;
    }

    // also accumulate the different types of capturing moves
    return accumulateCapturing(accumulator);
  }
}
