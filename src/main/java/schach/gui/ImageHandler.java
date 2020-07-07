package schach.gui;

import java.util.HashMap;
import java.util.Map;

import javafx.scene.image.Image;
import schach.common.Color;
import schach.game.pieces.PieceType;

/**
 * The image handler manages all images that are displayed, it prevents loading
 * an image more than once. This is a singleton and stores it's image
 *
 */
public class ImageHandler {
  private static Map<String, Image> images = new HashMap<String, Image>();

  /**
   * Enum that holds all possible icons.
   */
  public enum Icon {
    COMPUTER, HOME, PLAYER, SETTINGS, LEFT_ARROW, RIGHT_ARROW
  }

  /**
   * This is a static class and instances are forbidden.
   */
  private ImageHandler() {
  }

  /**
   * Preloads all images to make fetching an image faster later.
   */
  public static void preloadAll() {
    // load all icons
    for (Icon icon : Icon.values()) {
      getIconImage(icon);
    }

    // load all piece icons
    for (PieceType type : PieceType.values()) {
      getPieceImage(type, Color.WHITE);
      getPieceImage(type, Color.BLACK);
    }
  }

  /**
   * Loads a new Image and puts it into an array
   * 
   * @param name the name of the image
   */
  private static void loadImage(String name) {
    Image image = new Image(ImageHandler.class.getResource(name + ".png").toExternalForm(), true);
    images.put(name, image);
  }

  /**
   * This method loads a new image if there isn't one yet, otherwise it just
   * returns the existing one
   * 
   * @param key the name of the image
   * @return Requested image
   */
  private static Image getImage(String key) {
    if (images.get(key) == null) {
      loadImage(key);
    }
    return images.get(key);
  }

  /**
   * Returns the image for the given icon.
   * 
   * @param icon Icon to load the image for
   * @return Image for this icon
   */
  public static Image getIconImage(Icon icon) {
    return getImage(icon.name().toLowerCase() + "_icon");
  }

  /**
   * Returns the piece image for a piece type specified by the combination of a
   * color and a piece type.
   * 
   * @param type  Type of the piece
   * @param color Color of the piece
   * @return Loaded image instance for this piece
   */
  public static Image getPieceImage(PieceType type, Color color) {
    return getImage(type.getFullName().toLowerCase() + "_" + color.name().toLowerCase());
  }
}
