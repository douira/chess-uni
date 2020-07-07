module schach {
  requires javafx.controls;
  requires javafx.base;
  requires transitive javafx.graphics;

  exports schach;

  opens schach;
  opens schach.gui to javafx.graphics;
}
