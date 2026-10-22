package impl.org.controlsfx.skin;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.animation.Animation.Status;
import javafx.beans.InvalidationListener;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;
import javafx.event.EventHandler;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.SkinBase;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import org.controlsfx.control.HiddenSidesPane;

public class HiddenSidesPaneSkin extends SkinBase<HiddenSidesPane> {
   private static final String SHOW = "showPane";
   private final StackPane stackPane;
   private final EventHandler<MouseEvent> exitedHandler;
   private boolean mousePressed;
   private final MapChangeListener<Object, Object> propertiesMapListener = c -> {
      if (c.wasAdded() && "showPane".equals(c.getKey())) {
         Object value = c.getValueAdded();
         if (value == null) {
            this.hide();
         } else if (value instanceof Side) {
            this.show((Side)value);
         }

         ((HiddenSidesPane)this.getSkinnable()).getProperties().remove("showPane");
      }
   };
   private DoubleProperty[] visibility = new SimpleDoubleProperty[Side.values().length];
   private Timeline showTimeline;
   private Timeline hideTimeline;

   public HiddenSidesPaneSkin(HiddenSidesPane pane) {
      super(pane);
      this.exitedHandler = event -> {
         if (this.isMouseEnabled() && ((HiddenSidesPane)this.getSkinnable()).getPinnedSide() == null && !this.mousePressed) {
            this.hide();
         }
      };
      this.stackPane = new StackPane();
      this.getChildren().add(this.stackPane);
      this.updateStackPane();
      InvalidationListener rebuildListener = observable -> this.updateStackPane();
      pane.contentProperty().addListener(rebuildListener);
      pane.topProperty().addListener(rebuildListener);
      pane.rightProperty().addListener(rebuildListener);
      pane.bottomProperty().addListener(rebuildListener);
      pane.leftProperty().addListener(rebuildListener);
      pane.addEventFilter(MouseEvent.MOUSE_MOVED, event -> {
         if (this.isMouseEnabled() && ((HiddenSidesPane)this.getSkinnable()).getPinnedSide() == null) {
            Side sidex = this.getSide(event);
            if (sidex != null) {
               this.show(sidex);
            } else if (this.isMouseMovedOutsideSides(event)) {
               this.hide();
            }
         }
      });
      pane.addEventFilter(MouseEvent.MOUSE_EXITED, this.exitedHandler);
      pane.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> this.mousePressed = true);
      pane.addEventFilter(MouseEvent.MOUSE_RELEASED, event -> {
         this.mousePressed = false;
         if (this.isMouseEnabled() && ((HiddenSidesPane)this.getSkinnable()).getPinnedSide() == null) {
            Side sidex = this.getSide(event);
            if (sidex != null) {
               this.show(sidex);
            } else {
               this.hide();
            }
         }
      });

      for (Side side : Side.values()) {
         this.visibility[side.ordinal()] = new SimpleDoubleProperty(0.0);
         this.visibility[side.ordinal()].addListener(observable -> ((HiddenSidesPane)this.getSkinnable()).requestLayout());
      }

      Side pinnedSide = ((HiddenSidesPane)this.getSkinnable()).getPinnedSide();
      if (pinnedSide != null) {
         this.show(pinnedSide);
      }

      pane.pinnedSideProperty().addListener(observable -> this.show(((HiddenSidesPane)this.getSkinnable()).getPinnedSide()));
      ObservableMap<Object, Object> properties = pane.getProperties();
      properties.remove("showPane");
      properties.addListener(this.propertiesMapListener);
      Rectangle clip = new Rectangle();
      clip.setX(0.0);
      clip.setY(0.0);
      clip.widthProperty().bind(((HiddenSidesPane)this.getSkinnable()).widthProperty());
      clip.heightProperty().bind(((HiddenSidesPane)this.getSkinnable()).heightProperty());
      ((HiddenSidesPane)this.getSkinnable()).setClip(clip);
   }

   private boolean isMouseMovedOutsideSides(MouseEvent event) {
      if (((HiddenSidesPane)this.getSkinnable()).getLeft() != null
         && ((HiddenSidesPane)this.getSkinnable()).getLeft().getBoundsInParent().contains(event.getX(), event.getY())) {
         return false;
      } else if (((HiddenSidesPane)this.getSkinnable()).getTop() != null
         && ((HiddenSidesPane)this.getSkinnable()).getTop().getBoundsInParent().contains(event.getX(), event.getY())) {
         return false;
      } else {
         return ((HiddenSidesPane)this.getSkinnable()).getRight() != null
               && ((HiddenSidesPane)this.getSkinnable()).getRight().getBoundsInParent().contains(event.getX(), event.getY())
            ? false
            : ((HiddenSidesPane)this.getSkinnable()).getBottom() == null
               || !((HiddenSidesPane)this.getSkinnable()).getBottom().getBoundsInParent().contains(event.getX(), event.getY());
      }
   }

   private boolean isMouseEnabled() {
      return ((HiddenSidesPane)this.getSkinnable()).getTriggerDistance() > 0.0;
   }

   private Side getSide(MouseEvent evt) {
      if (this.stackPane.getBoundsInLocal().contains(evt.getX(), evt.getY())) {
         double trigger = ((HiddenSidesPane)this.getSkinnable()).getTriggerDistance();
         if (evt.getX() <= trigger) {
            return Side.LEFT;
         }

         if (evt.getX() > ((HiddenSidesPane)this.getSkinnable()).getWidth() - trigger) {
            return Side.RIGHT;
         }

         if (evt.getY() <= trigger) {
            return Side.TOP;
         }

         if (evt.getY() > ((HiddenSidesPane)this.getSkinnable()).getHeight() - trigger) {
            return Side.BOTTOM;
         }
      }

      return null;
   }

   private void show(Side side) {
      if (this.hideTimeline != null) {
         this.hideTimeline.stop();
      }

      if (this.showTimeline == null || this.showTimeline.getStatus() != Status.RUNNING) {
         KeyValue[] keyValues = new KeyValue[Side.values().length];

         for (Side s : Side.values()) {
            keyValues[s.ordinal()] = new KeyValue(this.visibility[s.ordinal()], s.equals(side) ? 1 : 0);
         }

         Duration delay = ((HiddenSidesPane)this.getSkinnable()).getAnimationDelay() != null
            ? ((HiddenSidesPane)this.getSkinnable()).getAnimationDelay()
            : Duration.millis(300.0);
         Duration duration = ((HiddenSidesPane)this.getSkinnable()).getAnimationDuration() != null
            ? ((HiddenSidesPane)this.getSkinnable()).getAnimationDuration()
            : Duration.millis(200.0);
         KeyFrame keyFrame = new KeyFrame(duration, keyValues);
         this.showTimeline = new Timeline(new KeyFrame[]{keyFrame});
         this.showTimeline.setDelay(delay);
         this.showTimeline.play();
      }
   }

   private void hide() {
      if (this.showTimeline != null) {
         this.showTimeline.stop();
      }

      if (this.hideTimeline == null || this.hideTimeline.getStatus() != Status.RUNNING) {
         boolean sideVisible = false;

         for (Side side : Side.values()) {
            if (this.visibility[side.ordinal()].get() > 0.0) {
               sideVisible = true;
               break;
            }
         }

         if (sideVisible) {
            KeyValue[] keyValues = new KeyValue[Side.values().length];

            for (Side sidex : Side.values()) {
               keyValues[sidex.ordinal()] = new KeyValue(this.visibility[sidex.ordinal()], 0);
            }

            Duration delay = ((HiddenSidesPane)this.getSkinnable()).getAnimationDelay() != null
               ? ((HiddenSidesPane)this.getSkinnable()).getAnimationDelay()
               : Duration.millis(300.0);
            Duration duration = ((HiddenSidesPane)this.getSkinnable()).getAnimationDuration() != null
               ? ((HiddenSidesPane)this.getSkinnable()).getAnimationDuration()
               : Duration.millis(200.0);
            KeyFrame keyFrame = new KeyFrame(duration, keyValues);
            this.hideTimeline = new Timeline(new KeyFrame[]{keyFrame});
            this.hideTimeline.setDelay(delay);
            this.hideTimeline.play();
         }
      }
   }

   private void updateStackPane() {
      this.stackPane.getChildren().clear();
      if (((HiddenSidesPane)this.getSkinnable()).getContent() != null) {
         this.stackPane.getChildren().add(((HiddenSidesPane)this.getSkinnable()).getContent());
      }

      if (((HiddenSidesPane)this.getSkinnable()).getTop() != null) {
         this.stackPane.getChildren().add(((HiddenSidesPane)this.getSkinnable()).getTop());
         ((HiddenSidesPane)this.getSkinnable()).getTop().setManaged(false);
         ((HiddenSidesPane)this.getSkinnable()).getTop().removeEventFilter(MouseEvent.MOUSE_EXITED, this.exitedHandler);
         ((HiddenSidesPane)this.getSkinnable()).getTop().addEventFilter(MouseEvent.MOUSE_EXITED, this.exitedHandler);
      }

      if (((HiddenSidesPane)this.getSkinnable()).getRight() != null) {
         this.stackPane.getChildren().add(((HiddenSidesPane)this.getSkinnable()).getRight());
         ((HiddenSidesPane)this.getSkinnable()).getRight().setManaged(false);
         ((HiddenSidesPane)this.getSkinnable()).getRight().removeEventFilter(MouseEvent.MOUSE_EXITED, this.exitedHandler);
         ((HiddenSidesPane)this.getSkinnable()).getRight().addEventFilter(MouseEvent.MOUSE_EXITED, this.exitedHandler);
      }

      if (((HiddenSidesPane)this.getSkinnable()).getBottom() != null) {
         this.stackPane.getChildren().add(((HiddenSidesPane)this.getSkinnable()).getBottom());
         ((HiddenSidesPane)this.getSkinnable()).getBottom().setManaged(false);
         ((HiddenSidesPane)this.getSkinnable()).getBottom().removeEventFilter(MouseEvent.MOUSE_EXITED, this.exitedHandler);
         ((HiddenSidesPane)this.getSkinnable()).getBottom().addEventFilter(MouseEvent.MOUSE_EXITED, this.exitedHandler);
      }

      if (((HiddenSidesPane)this.getSkinnable()).getLeft() != null) {
         this.stackPane.getChildren().add(((HiddenSidesPane)this.getSkinnable()).getLeft());
         ((HiddenSidesPane)this.getSkinnable()).getLeft().setManaged(false);
         ((HiddenSidesPane)this.getSkinnable()).getLeft().removeEventFilter(MouseEvent.MOUSE_EXITED, this.exitedHandler);
         ((HiddenSidesPane)this.getSkinnable()).getLeft().addEventFilter(MouseEvent.MOUSE_EXITED, this.exitedHandler);
      }
   }

   protected void layoutChildren(double contentX, double contentY, double contentWidth, double contentHeight) {
      super.layoutChildren(contentX, contentY, contentWidth, contentHeight);
      Node bottom = ((HiddenSidesPane)this.getSkinnable()).getBottom();
      if (bottom != null) {
         double prefHeight = bottom.prefHeight(-1.0);
         double offset = prefHeight * this.visibility[Side.BOTTOM.ordinal()].get();
         bottom.resizeRelocate(contentX, contentY + contentHeight - offset, contentWidth, prefHeight);
         bottom.setVisible(this.visibility[Side.BOTTOM.ordinal()].get() > 0.0);
      }

      Node left = ((HiddenSidesPane)this.getSkinnable()).getLeft();
      if (left != null) {
         double prefWidth = left.prefWidth(-1.0);
         double offset = prefWidth * this.visibility[Side.LEFT.ordinal()].get();
         left.resizeRelocate(contentX - (prefWidth - offset), contentY, prefWidth, contentHeight);
         left.setVisible(this.visibility[Side.LEFT.ordinal()].get() > 0.0);
      }

      Node right = ((HiddenSidesPane)this.getSkinnable()).getRight();
      if (right != null) {
         double prefWidth = right.prefWidth(-1.0);
         double offset = prefWidth * this.visibility[Side.RIGHT.ordinal()].get();
         right.resizeRelocate(contentX + contentWidth - offset, contentY, prefWidth, contentHeight);
         right.setVisible(this.visibility[Side.RIGHT.ordinal()].get() > 0.0);
      }

      Node top = ((HiddenSidesPane)this.getSkinnable()).getTop();
      if (top != null) {
         double prefHeight = top.prefHeight(-1.0);
         double offset = prefHeight * this.visibility[Side.TOP.ordinal()].get();
         top.resizeRelocate(contentX, contentY - (prefHeight - offset), contentWidth, prefHeight);
         top.setVisible(this.visibility[Side.TOP.ordinal()].get() > 0.0);
      }
   }
}
