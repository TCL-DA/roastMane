package impl.org.controlsfx.skin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.SkinBase;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import org.controlsfx.control.Rating;
import org.controlsfx.tools.Utils;

public class RatingSkin extends SkinBase<Rating> {
   private static final String STRONG = "strong";
   private boolean updateOnHover;
   private boolean partialRating;
   private Pane backgroundContainer;
   private Pane foregroundContainer;
   private double rating = -1.0;
   private Rectangle forgroundClipRect;
   private final EventHandler<MouseEvent> mouseMoveHandler = new EventHandler<MouseEvent>() {
      public void handle(MouseEvent event) {
         if (RatingSkin.this.updateOnHover) {
            RatingSkin.this.updateRatingFromMouseEvent(event);
         }
      }
   };
   private final EventHandler<MouseEvent> mouseClickHandler = new EventHandler<MouseEvent>() {
      public void handle(MouseEvent event) {
         if (!RatingSkin.this.updateOnHover) {
            RatingSkin.this.updateRatingFromMouseEvent(event);
         }
      }
   };

   private void updateRatingFromMouseEvent(MouseEvent event) {
      Rating control = (Rating)this.getSkinnable();
      if (!control.ratingProperty().isBound()) {
         Point2D mouseLocation = new Point2D(event.getSceneX(), event.getSceneY());
         control.setRating(this.calculateRating(mouseLocation));
      }
   }

   public RatingSkin(Rating control) {
      super(control);
      this.updateOnHover = control.isUpdateOnHover();
      this.partialRating = control.isPartialRating();
      this.recreateButtons();
      this.updateRating();
      this.registerChangeListener(control.ratingProperty(), e -> this.updateRating());
      this.registerChangeListener(control.maxProperty(), e -> this.recreateButtons());
      this.registerChangeListener(control.orientationProperty(), e -> this.recreateButtons());
      this.registerChangeListener(control.updateOnHoverProperty(), e -> {
         this.updateOnHover = ((Rating)this.getSkinnable()).isUpdateOnHover();
         this.recreateButtons();
      });
      this.registerChangeListener(control.partialRatingProperty(), e -> {
         this.partialRating = ((Rating)this.getSkinnable()).isPartialRating();
         this.recreateButtons();
      });
      this.registerChangeListener(control.boundsInLocalProperty(), e -> {
         if (this.partialRating) {
            this.updateClip();
         }
      });
   }

   private void recreateButtons() {
      this.backgroundContainer = null;
      this.foregroundContainer = null;
      this.backgroundContainer = (Pane)(this.isVertical() ? new VBox() : new HBox());
      this.backgroundContainer.getStyleClass().add("container");
      this.getChildren().setAll(new Node[]{this.backgroundContainer});
      if (this.updateOnHover || this.partialRating) {
         this.foregroundContainer = (Pane)(this.isVertical() ? new VBox() : new HBox());
         this.foregroundContainer.getStyleClass().add("container");
         this.foregroundContainer.setMouseTransparent(true);
         this.getChildren().add(this.foregroundContainer);
         this.forgroundClipRect = new Rectangle();
         this.foregroundContainer.setClip(this.forgroundClipRect);
      }

      for (int index = 0; index <= ((Rating)this.getSkinnable()).getMax(); index++) {
         Node backgroundNode = this.createButton();
         if (index > 0) {
            if (this.isVertical()) {
               this.backgroundContainer.getChildren().add(0, backgroundNode);
            } else {
               this.backgroundContainer.getChildren().add(backgroundNode);
            }

            if (this.partialRating) {
               Node foregroundNode = this.createButton();
               foregroundNode.getStyleClass().add("strong");
               foregroundNode.setMouseTransparent(true);
               if (this.isVertical()) {
                  this.foregroundContainer.getChildren().add(0, foregroundNode);
               } else {
                  this.foregroundContainer.getChildren().add(foregroundNode);
               }
            }
         }
      }

      this.updateRating();
   }

   private double calculateRating(Point2D sceneLocation) {
      Point2D b = this.backgroundContainer.sceneToLocal(sceneLocation);
      double x = b.getX();
      double y = b.getY();
      Rating control = (Rating)this.getSkinnable();
      int max = control.getMax();
      double w = control.getWidth() - (this.snappedLeftInset() + this.snappedRightInset());
      double h = control.getHeight() - (this.snappedTopInset() + this.snappedBottomInset());
      double newRating = -1.0;
      if (this.isVertical()) {
         newRating = (h - y) / h * (double)max;
      } else {
         newRating = x / w * (double)max;
      }

      if (!this.partialRating) {
         newRating = Utils.clamp(1.0, Math.ceil(newRating), (double)control.getMax());
      }

      return newRating;
   }

   private void updateClip() {
      Rating control = (Rating)this.getSkinnable();
      double h = control.getHeight() - (this.snappedTopInset() + this.snappedBottomInset());
      double w = control.getWidth() - (this.snappedLeftInset() + this.snappedRightInset());
      if (this.isVertical()) {
         double y = h * this.rating / (double)control.getMax();
         this.forgroundClipRect.relocate(0.0, h - y);
         this.forgroundClipRect.setWidth(control.getWidth());
         this.forgroundClipRect.setHeight(y);
      } else {
         double x = w * this.rating / (double)control.getMax();
         this.forgroundClipRect.setWidth(x);
         this.forgroundClipRect.setHeight(control.getHeight());
      }
   }

   protected Node createButtonNode() {
      return new Region();
   }

   private Node createButton() {
      Node btn = this.createButtonNode();
      btn.getStyleClass().add("button");
      btn.setOnMouseMoved(this.mouseMoveHandler);
      btn.setOnMouseClicked(this.mouseClickHandler);
      return btn;
   }

   private void updateRating() {
      double newRating = ((Rating)this.getSkinnable()).getRating();
      if (newRating != this.rating) {
         this.rating = Utils.clamp(0.0, newRating, (double)((Rating)this.getSkinnable()).getMax());
         if (this.partialRating) {
            this.updateClip();
         } else {
            this.updateButtonStyles();
         }
      }
   }

   private void updateButtonStyles() {
      int max = ((Rating)this.getSkinnable()).getMax();
      List<Node> buttons = new ArrayList<>(this.backgroundContainer.getChildren());
      if (this.isVertical()) {
         Collections.reverse(buttons);
      }

      for (int i = 0; i < max; i++) {
         Node button = buttons.get(i);
         List<String> styleClass = button.getStyleClass();
         boolean containsStrong = styleClass.contains("strong");
         if ((double)i < this.rating) {
            if (!containsStrong) {
               styleClass.add("strong");
            }
         } else if (containsStrong) {
            styleClass.remove("strong");
         }
      }
   }

   private boolean isVertical() {
      return ((Rating)this.getSkinnable()).getOrientation() == Orientation.VERTICAL;
   }

   protected double computeMaxWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
      return super.computePrefWidth(height, topInset, rightInset, bottomInset, leftInset);
   }
}
