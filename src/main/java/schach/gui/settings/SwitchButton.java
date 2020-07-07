package schach.gui.settings;

import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

/**
 * Because javafx did not have switch buttons as a button type this class was
 * created
 *
 */
public class SwitchButton extends StackPane {
  private double buttonWidth = 35;
  private double buttonHeight = 15;
  private final Rectangle backGroundOn = new Rectangle(buttonWidth, buttonHeight);
  private final Rectangle backGroundOff = new Rectangle(buttonWidth, buttonHeight);
  private final Circle buttonOn = new Circle(buttonHeight / 1.6);
  private final Circle buttonOff = new Circle(buttonHeight / 1.6);
  private BooleanProperty switchedOn = new SimpleBooleanProperty(false);

  /**
   * Creates a new switchButton instance
   */
  public SwitchButton() {
    buttonSwitchedOn();
    buttonSwitchedOff();
    backGroundOn.managedProperty().bind(switchedOn);
    backGroundOn.visibleProperty().bind(switchedOn);
    backGroundOff.managedProperty().bind(switchedOn.not());
    backGroundOff.visibleProperty().bind(switchedOn.not());
    buttonOn.managedProperty().bind(switchedOn);
    buttonOn.visibleProperty().bind(switchedOn);
    buttonOff.managedProperty().bind(switchedOn.not());
    buttonOff.visibleProperty().bind(switchedOn.not());
    setMaxSize(buttonWidth + 5, buttonHeight / 1.25);
    setMinSize(buttonWidth + 5, buttonHeight / 1.25);
    getChildren().addAll(backGroundOn, backGroundOff, buttonOn, buttonOff);
  }

  /**
   * Switches the button on or off
   */
  public void buttonSwitched() {
    if (switchedOn.get()) {
      switchedOn.set(false);
    } else {
      switchedOn.set(true);
    }
  }

  /**
   * creates the elements of the on button
   */
  private void buttonSwitchedOn() {
    backGroundOn.maxWidth(buttonWidth);
    backGroundOn.minWidth(buttonWidth);
    backGroundOn.maxHeight(buttonHeight);
    backGroundOn.minHeight(buttonHeight);
    backGroundOn.setArcHeight(backGroundOn.getHeight());
    backGroundOn.setArcWidth(backGroundOn.getHeight());
    setAlignment(backGroundOn, Pos.CENTER);
    buttonOn.setFill(Color.DARKGREEN);
    backGroundOn.setFill(Color.valueOf("#84dbaa"));
    setAlignment(buttonOn, Pos.CENTER_RIGHT);
  }

  /**
   * creates the elements of the off button
   */
  private void buttonSwitchedOff() {
    backGroundOff.maxWidth(buttonWidth);
    backGroundOff.minWidth(buttonWidth);
    backGroundOff.maxHeight(buttonHeight);
    backGroundOff.minHeight(buttonHeight);
    backGroundOff.setArcHeight(backGroundOff.getHeight());
    backGroundOff.setArcWidth(backGroundOff.getHeight());
    setAlignment(backGroundOff, Pos.CENTER);
    buttonOff.setFill(Color.WHITE);
    backGroundOff.setFill(Color.valueOf("#b5b5b5"));
    setAlignment(buttonOff, Pos.CENTER_LEFT);
  }

  public boolean isSwitchedOn() {
    return switchedOn.get();
  }
}
