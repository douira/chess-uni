package schach.gui;

import java.util.function.Supplier;

import javafx.beans.property.SimpleBooleanProperty;
import schach.common.Color;
import schach.game.pieces.Piece;
import schach.game.pieces.PieceType;

/**
 * Provides utilities for the GUI that deal with JavaFX specific problems.
 */
public class GUIUtils {
  /**
   * This static class doesn't allow instances.
   */
  private GUIUtils() {
  }

  /**
   * Initializes a 2D nested Property array in the form of a grid
   * 
   * @param <PropertyType> Type of the properties in the array
   * @param array          Array to initialize
   * @param generator      Generator to provide value instances
   */
  public static <PropertyType> void makePropGrid(PropertyType[][] array, Supplier<PropertyType> generator) {
    for (int x = 0; x < array.length; x++) {
      for (int y = 0; y < array[x].length; y++) {
        array[x][y] = generator.get();
      }
    }
  }

  /**
   * Initializes a simple Property array
   * 
   * @param <PropertyType> Type of the properties in the array
   * @param array          Array to initialize
   * @param generator      Generator to provide value instances
   */
  public static <PropertyType> void makePropArray(PropertyType[] array, Supplier<PropertyType> generator) {
    for (int x = 0; x < array.length; x++) {
      array[x] = generator.get();
    }
  }

  /**
   * Generates simple boolean props that have the value false.
   * 
   * @return Boolean prop set to false
   */
  public static SimpleBooleanProperty generateFalse() {
    return new SimpleBooleanProperty(false);
  }

  /**
   * Calculates the piece type id based on the components of the a piece type
   * identity.
   * 
   * @param type  Type of the piece
   * @param color Color of the piece
   * @return Piece type id for this type and color
   */
  public static int getPieceTypeId(PieceType type, Color color) {
    return type.ordinal() * 2 + color.ordinal();
  }

  /**
   * Calculates the piece type id based on the name and color of a piece
   * 
   * @param piece Piece to get the number for
   * @return Piece type id for this type and color
   */
  public static int getPieceTypeId(Piece piece) {
    return getPieceTypeId(piece.getType(), piece.getColor());
  }

  /**
   * Calculates the piece type id for use in lists of captured pieces based on the
   * components of the a piece type identity.
   * 
   * @param type  Type of the piece
   * @param color Color of the piece
   * @return Piece type id for capture lists for this type and color
   */
  public static int getCapturePieceTypeId(PieceType type, Color color) {
    return getPieceTypeId(type, color) - 2 * (PieceType.amount - PieceType.captureAmount);
  }
}
