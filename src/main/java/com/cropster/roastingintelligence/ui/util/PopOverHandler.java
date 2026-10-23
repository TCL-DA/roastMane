package com.cropster.roastingintelligence.ui.util;

import com.cropster.roastingintelligence.ui.components.CommentPopupContent;
import com.cropster.roastingintelligence.ui.components.PopOverBubble;
import java.util.function.BiConsumer;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.TextInputControl;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import org.apache.commons.lang3.SystemUtils;

public class PopOverHandler<T extends CommentPopupContent> implements EventHandler<MouseEvent> {
   private final T content;
   private final BiConsumer<PopOverHandler<T>, Point2D> handler;
   private final Stage imeAwarePopOver;
   private volatile boolean closed;
   private volatile double pressedScreenX;
   private volatile double pressedScreenY;

   public PopOverHandler(Node header, T content, BiConsumer<PopOverHandler<T>, Point2D> handler) {
      this.content = content;
      this.handler = handler;
      Button closeButton = new Button(org.cropster.roastingintelligence.util.Messages.m_("Close"));
      closeButton.getStyleClass().add("button--plain");
      closeButton.setMaxHeight(Double.POSITIVE_INFINITY);
      closeButton.setMaxWidth(Double.POSITIVE_INFINITY);
      this.imeAwarePopOver = this.initImeAwarePopOver(
         PopOverBubble.builder().withHeader(header, true).withContent(content).withFooter(closeButton).closable(false).withDetached().build()
      );
      closeButton.setOnAction(e -> this.hide());
   }

   private Stage initImeAwarePopOver(PopOverBubble popOver) {
      Stage customPopOver = new Stage();
      customPopOver.initModality(Modality.NONE);
      customPopOver.initStyle(SystemUtils.IS_OS_MAC_OSX ? StageStyle.UTILITY : StageStyle.UNDECORATED);
      customPopOver.setResizable(false);
      Scene scene = popOver.getScene();
      Parent root = scene.getRoot();
      scene.setRoot(new BorderPane());
      Scene customScene = new Scene(root);
      customPopOver.setScene(customScene);
      customScene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
         if (event.getCode() == KeyCode.ESCAPE) {
            customPopOver.hide();
            event.consume();
         }
      });
      double[] dragOffset = new double[2];
      boolean[] isDragging = new boolean[1];
      customScene.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
         if (!this.isOutsideImeAwarePopOver(event) && !this.isTargetInteractive((Node)event.getTarget())) {
            isDragging[0] = true;
            dragOffset[0] = event.getScreenX() - customPopOver.getX();
            dragOffset[1] = event.getScreenY() - customPopOver.getY();
         } else {
            isDragging[0] = false;
         }
      });
      customScene.addEventFilter(MouseEvent.MOUSE_DRAGGED, event -> {
         if (isDragging[0]) {
            customPopOver.setX(event.getScreenX() - dragOffset[0]);
            customPopOver.setY(event.getScreenY() - dragOffset[1]);
         }
      });
      customScene.addEventFilter(MouseEvent.MOUSE_RELEASED, event -> isDragging[0] = false);
      customPopOver.addEventHandler(WindowEvent.WINDOW_SHOWN, e -> this.clampToScreen(customPopOver));
      return customPopOver;
   }

   private void clampToScreen(Stage stage) {
      Rectangle2D screenBounds = WindowUtil.getScreenBounds(stage);
      double x = stage.getX();
      double y = stage.getY();
      double w = stage.getWidth();
      double h = stage.getHeight();
      double clampedX = Math.max(screenBounds.getMinX(), Math.min(x, screenBounds.getMaxX() - w));
      double clampedY = Math.max(screenBounds.getMinY(), Math.min(y, screenBounds.getMaxY() - h));
      if (clampedX != x) {
         stage.setX(clampedX);
      }

      if (clampedY != y) {
         stage.setY(clampedY);
      }
   }

   private boolean isOutsideImeAwarePopOver(MouseEvent e) {
      if (this.imeAwarePopOver == null) {
         return false;
      } else {
         double x = e.getScreenX();
         double y = e.getScreenY();
         return x < this.imeAwarePopOver.getX()
            || x > this.imeAwarePopOver.getX() + this.imeAwarePopOver.getWidth()
            || y < this.imeAwarePopOver.getY()
            || y > this.imeAwarePopOver.getY() + this.imeAwarePopOver.getHeight();
      }
   }

   private boolean isTargetInteractive(Node target) {
      if (target == null) {
         return false;
      } else if (!(target instanceof ButtonBase) && !(target instanceof TextInputControl)) {
         Parent parent = target.getParent();
         return parent != null && parent != target ? this.isTargetInteractive(parent) : false;
      } else {
         return true;
      }
   }

   public T getContentNode() {
      return this.content;
   }

   public void show(Node node, Point2D location) {
      if (this.imeAwarePopOver.getOwner() != null && node != null && node.getScene() != null) {
         this.imeAwarePopOver.initOwner(node.getScene().getWindow());
      }

      this.imeAwarePopOver.setX(location.getX() - 140.0);
      this.imeAwarePopOver.setY(location.getY() - 12.0);
      this.imeAwarePopOver.show();
      if (this.content.getGasEditor().getParent().isVisible()) {
         this.content.getGasEditor().requestFocus();
         this.content.getGasEditor().selectAll();
      } else if (this.content.getAirEditor().getParent().isVisible()) {
         this.content.getAirEditor().requestFocus();
         this.content.getAirEditor().selectAll();
      } else {
         this.content.getNoteInput().requestFocus();
      }
   }

   public void hide() {
      this.imeAwarePopOver.hide();
   }

   public void handle(MouseEvent e) {
      if (e.getButton().equals(MouseButton.PRIMARY)) {
         if (!SystemUtils.IS_OS_MAC_OSX || !e.isControlDown()) {
            if (MouseEvent.MOUSE_PRESSED.equals(e.getEventType())) {
               this.closed = false;
               if (this.imeAwarePopOver != null && this.imeAwarePopOver.isShowing() && this.isOutsideImeAwarePopOver(e)) {
                  this.hide();
                  this.closed = true;
               }

               this.pressedScreenX = e.getScreenX();
               this.pressedScreenY = e.getScreenY();
            } else {
               if (!this.closed
                  && MouseEvent.MOUSE_RELEASED.equals(e.getEventType())
                  && Math.abs(e.getScreenX() - this.pressedScreenX) <= 5.0
                  && Math.abs(e.getScreenY() - this.pressedScreenY) <= 5.0) {
                  this.handler.accept(this, new Point2D(e.getScreenX(), e.getScreenY()));
                  e.consume();
               }
            }
         }
      }
   }
}
