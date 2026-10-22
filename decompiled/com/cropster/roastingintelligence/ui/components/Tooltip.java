package com.cropster.roastingintelligence.ui.components;

import com.cropster.roastingintelligence.ui.JavaFxUtil;
import com.cropster.roastingintelligence.ui.util.KeyboardShortcut;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.geometry.Rectangle2D;
import javafx.scene.layout.Region;
import javafx.stage.Screen;
import javafx.stage.Window;

public class Tooltip extends javafx.scene.control.Tooltip {
   public static final double VERTICAL_OFFSET = 6.0;
   private final ObjectProperty<KeyboardShortcut> shortcut = new SimpleObjectProperty<KeyboardShortcut>(this, "shortcut", null) {
      protected void invalidated() {
         Tooltip.this.setGraphic(this.get() == null ? null : new KeyCodeCombinationLabel(((KeyboardShortcut)this.get()).getKeyCodeCombination()));
      }
   };

   public Tooltip() {
      this(null, null);
   }

   public Tooltip(String text) {
      this(text, null);
   }

   public Tooltip(String text, KeyboardShortcut shortcut) {
      super(text);
      this.init(shortcut);
   }

   public KeyboardShortcut getShortcut() {
      return (KeyboardShortcut)this.shortcut.get();
   }

   public void setShortcut(KeyboardShortcut keyboardShortcut) {
      this.shortcut.set(keyboardShortcut);
   }

   public ObjectProperty<KeyboardShortcut> shortcutProperty() {
      return this.shortcut;
   }

   public void show(Window ownerWindow, double anchorX, double anchorY) {
      super.show(ownerWindow, anchorX, anchorY);
      JavaFxUtil.newRecursiveChildrenStream(ownerWindow.getScene().getRoot())
         .filter(n -> n.getProperties().get("javafx.scene.control.Tooltip") == this)
         .findFirst()
         .ifPresent(
            ownerNode -> {
               Bounds ownerBounds = ownerNode.localToScreen(ownerNode.getBoundsInLocal());
               ObservableList<Screen> screens = Screen.getScreensForRectangle(
                  ownerBounds.getMinX(), ownerBounds.getMinY(), ownerBounds.getWidth(), ownerBounds.getHeight()
               );
               if (!screens.isEmpty()) {
                  Screen screen = (Screen)screens.get(0);
                  Rectangle2D screenBounds = screen.getVisualBounds();
                  Region region = (Region)this.getSkin().getNode();
                  double y = ownerBounds.getMaxY() + 6.0;
                  if (y + region.getHeight() > screenBounds.getMaxY()) {
                     y = ownerBounds.getMinY() - region.getHeight() - 2.0 - 6.0;
                  }

                  double x = ownerBounds.getMinX() + (ownerBounds.getWidth() - region.getWidth()) / 2.0;
                  if (x < screenBounds.getMinX()) {
                     x = ownerBounds.getMinX();
                  } else if (x > screenBounds.getMaxX()) {
                     x = ownerBounds.getMaxX() - region.getWidth();
                  }

                  super.show(ownerWindow, x, y);
               }
            }
         );
   }

   private void init(KeyboardShortcut keyboardShortcut) {
      this.setShortcut(keyboardShortcut);
   }
}
