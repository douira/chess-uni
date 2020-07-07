package schach.ai;

import java.util.Map;

import schach.common.Color;
import schach.common.Utils;
import schach.game.pieces.Piece;
import schach.game.pieces.PieceType;
import schach.game.state.GameState;
import schach.game.state.GameStatus;

/**
 * Evaluates the positions and values of the pieces in a given game state to
 * determine the value of the board configuration for a given player.
 */
public class GameEvaluator {
  //@formatter:off
  private static final int[] pawnMap = {
     0,  0,  0,  0,  0,  0,  0,  0,
    10, 10, 10, 10, 10, 10, 10, 10,
     2,  2,  4,  6,  6,  4,  2,  2,
     1,  1,  2,  5,  5,  2,  1,  1,
     0,  0,  0,  4,  4,  0,  0,  0,
     1, -1, -2,  0,  0, -2, -1,  1,
     1,  2,  2, -4, -4,  2,  2,  1,
     0,  0,  0,  0,  0,  0,  0,  0
  };
  private static final int[] knightMap = {
    -10, -8, -6, -6, -6, -6, -8, -10,
     -8, -4,  0,  0,  0,  0, -4,  -8,
     -6,  0,  2,  3,  3,  2,  0,  -6,
     -6,  1,  3,  4,  4,  3,  1,  -6,
     -6,  0,  3,  4,  4,  3,  0,  -6,
     -6,  1,  1,  3,  3,  2,  1,  -6,
     -8, -4,  0,  1,  1,  0, -4,  -8,
    -10, -8, -6, -6, -6, -6, -8, -10
  };
  private static final int[] bishopMap = {
    -4, -2, -2, -2, -2, -2, -2, -4,
    -2,  0,  0,  0,  0,  0,  0, -2,
    -2,  0,  1,  2,  2,  1,  0, -2,
    -2,  1,  1,  2,  2,  1,  1, -2,
    -2,  0,  2,  2,  2,  2,  0, -2,
    -2,  2,  2,  2,  2,  2,  2, -2,
    -2,  1,  0,  0,  0,  0,  1, -2,
    -4, -2, -2, -2, -2, -2, -2, -4
  };
  private static final int[] rookMap = {
     0,  0,  0,  0,  0,  0,  0,  0,
     1,  2,  2,  2,  2,  2,  2,  1,
    -1,  0,  0,  0,  0,  0,  0, -1,
    -1,  0,  0,  0,  0,  0,  0, -1,
    -1,  0,  0,  0,  0,  0,  0, -1,
    -1,  0,  0,  0,  0,  0,  0, -1,
    -1,  0,  0,  0,  0,  0,  0, -1,
     0,  0,  0,  1,  1,  0,  0,  0
  };
  private static final int[] queenMap = {
    -4, -2, -2, -1, -1, -2, -2, -4,
    -2,  0,  0,  0,  0,  0,  0, -2,
    -2,  0,  1,  1,  1,  1,  0, -2,
    -1,  0,  1,  1,  1,  1,  0, -1,
    -1,  0,  1,  1,  1,  1,  0, -1,
    -2,  1,  1,  1,  1,  1,  1, -2,
    -2,  0,  1,  0,  0,  0,  0, -2,
    -4, -2, -2, -1, -1, -2, -2, -4
  };
  private static final int[] kingMap = {
    -6, -8, -8, -10, -10, -8, -8, -6,
    -6, -8, -8, -10, -10, -8, -8, -6,
    -6, -8, -8, -10, -10, -8, -8, -6,
    -6, -8, -8, -10, -10, -8, -8, -6,
    -4, -6, -6,  -8,  -8, -6, -6, -4,
    -2, -4, -4,  -4,  -4, -4, -4, -2,
     4,  4,  0,   0,   0,  0,  4,  4,
     4,  6,  2,   0,   0,  2,  6,  4
  };
  //@formatter:on

  /**
   * A map of all the position weights for quick lookup of a piece weight. These
   * maps are oriented so that they should be seen from the white player's
   * perspective. Invert them for the black player's perspective
   */
  private static final Map<PieceType, int[]> positionWeights = Map.of(PieceType.KING, kingMap, PieceType.QUEEN,
      queenMap, PieceType.ROOK, rookMap, PieceType.BISHOP, bishopMap, PieceType.KNIGHT, knightMap, PieceType.PAWN,
      pawnMap);

  /**
   * A map of the piece values used for determining how much the existence of a
   * piece is worth to us. Kings are valued 0 in this map since their loss is
   * never an option and the game status CHECKMATE will happen before a king is
   * ever captured.
   */
  private static final Map<PieceType, Integer> pieceWeights = Map.of(PieceType.KING, 0, PieceType.QUEEN, 18,
      PieceType.ROOK, 10, PieceType.BISHOP, 7, PieceType.KNIGHT, 6, PieceType.PAWN, 2);

  /**
   * The weight determines how strongly the position of a piece is valued over the
   * value of the piece itself.
   */
  private static final double POSITION_MAP_WEIGHT = 0.05;

  /**
   * How much end game states are valued.
   */
  private static final Map<GameStatus, Double> END_STATUS_VALUES = Map.of(GameStatus.DRAW, 0D, GameStatus.IN_CHECKMATE,
      Double.NEGATIVE_INFINITY);

  /**
   * From what color's perspective we're evaluating game states from. Values will
   * be positive for moves that benefit this color.
   */
  private Color aiColor;

  public void setAiColor(Color color) {
    aiColor = color;
  }

  /**
   * Calculates the value of the board based on the pieces that are still alive
   * 
   * @param gameState the current gameState of the board
   * @return the value of the board
   */
  public double calculateBoardValue(GameState gameState) {
    // return values for end-states or throw
    GameStatus status = gameState.getStatus();
    double value = 0;
    if (status.gameIsStopped()) {
      // this will never run on non-end game statuses

      // flip the end state value to be reversed for the opposite color
      value = END_STATUS_VALUES.get(status) * (aiColor == gameState.getActiveColor() ? 1 : -1);
    } else {
      // accumulate the total value of the board
      for (Map.Entry<Integer, Piece> entry : gameState.getBoard().getPieces().entrySet()) {
        value += getValuation(entry.getValue(), entry.getKey());
      }
    }

    return Utils.normalizeZero(value);
  }

  /**
   * Calculates the value of a piece at a certain position for this player.
   * 
   * @param piece         Piece to calculate the value of
   * @param positionIndex Position of the piece on the board in index form
   * @return Value of the piece at this position for this player (negative for
   *         pieces of the opposing color)
   */
  private double getValuation(Piece piece, int positionIndex) {
    Color pieceColor = piece.getColor();
    PieceType type = piece.getType();
    int[] positionMap = positionWeights.get(type);

    // invert the position index if this piece is of the other color
    int mappedPositionIndex = pieceColor == Color.BLACK ? positionMap.length - positionIndex - 1 : positionIndex;

    // calculate the value of this piece at this position and weight the mapping
    // with a strength
    return (POSITION_MAP_WEIGHT * positionMap[mappedPositionIndex] + pieceWeights.get(type))
        * (pieceColor == aiColor ? 1 : -1);
  }
}
