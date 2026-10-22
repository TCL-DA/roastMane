package com.cropster.roastingintelligence.ui.components.skin;

import com.cropster.roastingintelligence.ui.components.PopOverBubble;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.InvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Skin;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.HLineTo;
import javafx.scene.shape.Line;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.PathElement;
import javafx.scene.shape.QuadCurveTo;
import javafx.scene.shape.VLineTo;
import javafx.stage.Window;
import org.controlsfx.control.PopOver;
import org.controlsfx.control.PopOver.ArrowLocation;

public class NoTitlePopOverSkin implements Skin<PopOver> {
   private static final String DETACHED_STYLE_CLASS = "detached";
   private double xOffset;
   private double yOffset;
   private boolean tornOff;
   private Path path;
   private Path clip;
   private Point2D dragStartLocation;
   private BorderPane content;
   private StackPane stackPane;
   private PopOver popOver;
   private MoveTo moveTo;
   private QuadCurveTo topCurveTo;
   private QuadCurveTo rightCurveTo;
   private QuadCurveTo bottomCurveTo;
   private QuadCurveTo leftCurveTo;
   private HLineTo lineBTop;
   private HLineTo lineETop;
   private HLineTo lineHTop;
   private HLineTo lineKTop;
   private LineTo lineCTop;
   private LineTo lineDTop;
   private LineTo lineFTop;
   private LineTo lineGTop;
   private LineTo lineITop;
   private LineTo lineJTop;
   private VLineTo lineBRight;
   private VLineTo lineERight;
   private VLineTo lineHRight;
   private VLineTo lineKRight;
   private LineTo lineCRight;
   private LineTo lineDRight;
   private LineTo lineFRight;
   private LineTo lineGRight;
   private LineTo lineIRight;
   private LineTo lineJRight;
   private HLineTo lineBBottom;
   private HLineTo lineEBottom;
   private HLineTo lineHBottom;
   private HLineTo lineKBottom;
   private LineTo lineCBottom;
   private LineTo lineDBottom;
   private LineTo lineFBottom;
   private LineTo lineGBottom;
   private LineTo lineIBottom;
   private LineTo lineJBottom;
   private VLineTo lineBLeft;
   private VLineTo lineELeft;
   private VLineTo lineHLeft;
   private VLineTo lineKLeft;
   private LineTo lineCLeft;
   private LineTo lineDLeft;
   private LineTo lineFLeft;
   private LineTo lineGLeft;
   private LineTo lineILeft;
   private LineTo lineJLeft;

   public NoTitlePopOverSkin(PopOver popOver) {
      this.popOver = popOver;
      this.stackPane = popOver.getRoot();
      this.stackPane.setPickOnBounds(false);
      Bindings.bindContent(this.stackPane.getStyleClass(), popOver.getStyleClass());
      this.stackPane
         .minWidthProperty()
         .bind(
            Bindings.add(
               Bindings.multiply(2, popOver.arrowSizeProperty()),
               Bindings.add(Bindings.multiply(2, popOver.cornerRadiusProperty()), Bindings.multiply(2, popOver.arrowIndentProperty()))
            )
         );
      this.stackPane.minHeightProperty().bind(this.stackPane.minWidthProperty());
      this.content = new BorderPane();
      this.content.setCenter(popOver.getContentNode());
      this.content.getStyleClass().add("content");
      if (popOver.isDetached()) {
         popOver.getStyleClass().add("detached");
         this.content.getStyleClass().add("detached");
      }

      InvalidationListener updatePathListener = observable -> this.updatePath();
      this.getPopupWindow().xProperty().addListener(updatePathListener);
      this.getPopupWindow().yProperty().addListener(updatePathListener);
      popOver.arrowLocationProperty().addListener(updatePathListener);
      popOver.contentNodeProperty().addListener((value, oldContent, newContent) -> this.content.setCenter(newContent));
      popOver.detachedProperty().addListener((value, oldDetached, newDetached) -> {
         if (newDetached) {
            popOver.getStyleClass().add("detached");
            this.content.getStyleClass().add("detached");
         } else {
            popOver.getStyleClass().remove("detached");
            this.content.getStyleClass().remove("detached");
         }

         popOver.sizeToScene();
         this.updatePath();
      });
      this.path = new Path();
      this.path.getStyleClass().add("border");
      this.path.setManaged(false);
      this.clip = new Path();
      this.clip.setFill(Color.YELLOW);
      this.createPathElements();
      this.updatePath();
      EventHandler<MouseEvent> mousePressedHandler = evt -> {
         this.tornOff = false;
         this.xOffset = evt.getScreenX();
         this.yOffset = evt.getScreenY();
         this.dragStartLocation = new Point2D(this.xOffset, this.yOffset);
      };
      EventHandler<MouseEvent> mouseReleasedHandler = evt -> {
         if (this.tornOff && !this.getSkinnable().isDetached()) {
            this.tornOff = false;
            this.getSkinnable().detach();
         }
      };
      EventHandler<MouseEvent> mouseDragHandler = evt -> {
         if (popOver.isDetachable() || popOver.isDetached()) {
            double deltaX = evt.getScreenX() - this.xOffset;
            double deltaY = evt.getScreenY() - this.yOffset;
            Window window = this.getSkinnable().getScene().getWindow();
            window.setX(window.getX() + deltaX);
            window.setY(window.getY() + deltaY);
            this.xOffset = evt.getScreenX();
            this.yOffset = evt.getScreenY();
            if (this.dragStartLocation == null) {
               this.dragStartLocation = new Point2D(this.xOffset, this.yOffset);
            }

            if (this.dragStartLocation.distance(this.xOffset, this.yOffset) > 8.0) {
               this.tornOff = true;
               this.updatePath();
            } else if (this.tornOff) {
               this.tornOff = false;
               this.updatePath();
            }
         }
      };
      this.stackPane.setOnMousePressed(mousePressedHandler);
      this.stackPane.setOnMouseDragged(mouseDragHandler);
      this.stackPane.setOnMouseReleased(mouseReleasedHandler);
      if (popOver instanceof PopOverBubble bubble && bubble.getBorderStyle() != null) {
         this.path.setStyle(bubble.getBorderStyle());
      }

      this.stackPane.getChildren().add(this.path);
      this.stackPane.getChildren().add(this.content);
      this.content.setClip(this.clip);
      popOver.getScene().getRoot().setStyle("-fx-background-color: transparent;");
   }

   public Node getNode() {
      return this.stackPane;
   }

   public Path getPath() {
      return this.path;
   }

   public PopOver getSkinnable() {
      return this.popOver;
   }

   public void dispose() {
   }

   private Node createCloseIcon() {
      Group group = new Group();
      group.getStyleClass().add("graphics");
      Circle circle = new Circle();
      circle.getStyleClass().add("circle");
      circle.setRadius(6.0);
      circle.setCenterX(6.0);
      circle.setCenterY(6.0);
      group.getChildren().add(circle);
      Line line1 = new Line();
      line1.getStyleClass().add("line");
      line1.setStartX(4.0);
      line1.setStartY(4.0);
      line1.setEndX(8.0);
      line1.setEndY(8.0);
      group.getChildren().add(line1);
      Line line2 = new Line();
      line2.getStyleClass().add("line");
      line2.setStartX(8.0);
      line2.setStartY(4.0);
      line2.setEndX(4.0);
      line2.setEndY(8.0);
      group.getChildren().add(line2);
      return group;
   }

   private void createPathElements() {
      DoubleProperty centerYProperty = new SimpleDoubleProperty();
      DoubleProperty centerXProperty = new SimpleDoubleProperty();
      DoubleProperty leftEdgeProperty = new SimpleDoubleProperty();
      DoubleProperty leftEdgePlusRadiusProperty = new SimpleDoubleProperty();
      DoubleProperty topEdgeProperty = new SimpleDoubleProperty();
      DoubleProperty topEdgePlusRadiusProperty = new SimpleDoubleProperty();
      DoubleProperty rightEdgeProperty = new SimpleDoubleProperty();
      DoubleProperty rightEdgeMinusRadiusProperty = new SimpleDoubleProperty();
      DoubleProperty bottomEdgeProperty = new SimpleDoubleProperty();
      DoubleProperty bottomEdgeMinusRadiusProperty = new SimpleDoubleProperty();
      DoubleProperty cornerProperty = this.getSkinnable().cornerRadiusProperty();
      DoubleProperty arrowSizeProperty = this.getSkinnable().arrowSizeProperty();
      DoubleProperty arrowIndentProperty = this.getSkinnable().arrowIndentProperty();
      centerYProperty.bind(Bindings.divide(this.stackPane.heightProperty(), 2));
      centerXProperty.bind(Bindings.divide(this.stackPane.widthProperty(), 2));
      leftEdgePlusRadiusProperty.bind(Bindings.add(leftEdgeProperty, this.getSkinnable().cornerRadiusProperty()));
      topEdgePlusRadiusProperty.bind(Bindings.add(topEdgeProperty, this.getSkinnable().cornerRadiusProperty()));
      rightEdgeProperty.bind(this.stackPane.widthProperty());
      rightEdgeMinusRadiusProperty.bind(Bindings.subtract(rightEdgeProperty, this.getSkinnable().cornerRadiusProperty()));
      bottomEdgeProperty.bind(this.stackPane.heightProperty());
      bottomEdgeMinusRadiusProperty.bind(Bindings.subtract(bottomEdgeProperty, this.getSkinnable().cornerRadiusProperty()));
      this.moveTo = new MoveTo();
      this.moveTo.xProperty().bind(leftEdgePlusRadiusProperty);
      this.moveTo.yProperty().bind(topEdgeProperty);
      this.lineBTop = new HLineTo();
      this.lineBTop.xProperty().bind(Bindings.add(leftEdgePlusRadiusProperty, arrowIndentProperty));
      this.lineCTop = new LineTo();
      this.lineCTop.xProperty().bind(Bindings.add(this.lineBTop.xProperty(), arrowSizeProperty));
      this.lineCTop.yProperty().bind(Bindings.subtract(topEdgeProperty, arrowSizeProperty));
      this.lineDTop = new LineTo();
      this.lineDTop.xProperty().bind(Bindings.add(this.lineCTop.xProperty(), arrowSizeProperty));
      this.lineDTop.yProperty().bind(topEdgeProperty);
      this.lineETop = new HLineTo();
      this.lineETop.xProperty().bind(Bindings.subtract(centerXProperty, arrowSizeProperty));
      this.lineFTop = new LineTo();
      this.lineFTop.xProperty().bind(centerXProperty);
      this.lineFTop.yProperty().bind(Bindings.subtract(topEdgeProperty, arrowSizeProperty));
      this.lineGTop = new LineTo();
      this.lineGTop.xProperty().bind(Bindings.add(centerXProperty, arrowSizeProperty));
      this.lineGTop.yProperty().bind(topEdgeProperty);
      this.lineHTop = new HLineTo();
      this.lineHTop
         .xProperty()
         .bind(Bindings.subtract(Bindings.subtract(rightEdgeMinusRadiusProperty, arrowIndentProperty), Bindings.multiply(arrowSizeProperty, 2)));
      this.lineITop = new LineTo();
      this.lineITop.xProperty().bind(Bindings.subtract(Bindings.subtract(rightEdgeMinusRadiusProperty, arrowIndentProperty), arrowSizeProperty));
      this.lineITop.yProperty().bind(Bindings.subtract(topEdgeProperty, arrowSizeProperty));
      this.lineJTop = new LineTo();
      this.lineJTop.xProperty().bind(Bindings.subtract(rightEdgeMinusRadiusProperty, arrowIndentProperty));
      this.lineJTop.yProperty().bind(topEdgeProperty);
      this.lineKTop = new HLineTo();
      this.lineKTop.xProperty().bind(rightEdgeMinusRadiusProperty);
      this.rightCurveTo = new QuadCurveTo();
      this.rightCurveTo.xProperty().bind(rightEdgeProperty);
      this.rightCurveTo.yProperty().bind(Bindings.add(topEdgeProperty, cornerProperty));
      this.rightCurveTo.controlXProperty().bind(rightEdgeProperty);
      this.rightCurveTo.controlYProperty().bind(topEdgeProperty);
      this.lineBRight = new VLineTo();
      this.lineBRight.yProperty().bind(Bindings.add(topEdgePlusRadiusProperty, arrowIndentProperty));
      this.lineCRight = new LineTo();
      this.lineCRight.xProperty().bind(Bindings.add(rightEdgeProperty, arrowSizeProperty));
      this.lineCRight.yProperty().bind(Bindings.add(this.lineBRight.yProperty(), arrowSizeProperty));
      this.lineDRight = new LineTo();
      this.lineDRight.xProperty().bind(rightEdgeProperty);
      this.lineDRight.yProperty().bind(Bindings.add(this.lineCRight.yProperty(), arrowSizeProperty));
      this.lineERight = new VLineTo();
      this.lineERight.yProperty().bind(Bindings.subtract(centerYProperty, arrowSizeProperty));
      this.lineFRight = new LineTo();
      this.lineFRight.xProperty().bind(Bindings.add(rightEdgeProperty, arrowSizeProperty));
      this.lineFRight.yProperty().bind(centerYProperty);
      this.lineGRight = new LineTo();
      this.lineGRight.xProperty().bind(rightEdgeProperty);
      this.lineGRight.yProperty().bind(Bindings.add(centerYProperty, arrowSizeProperty));
      this.lineHRight = new VLineTo();
      this.lineHRight
         .yProperty()
         .bind(Bindings.subtract(Bindings.subtract(bottomEdgeMinusRadiusProperty, arrowIndentProperty), Bindings.multiply(arrowSizeProperty, 2)));
      this.lineIRight = new LineTo();
      this.lineIRight.xProperty().bind(Bindings.add(rightEdgeProperty, arrowSizeProperty));
      this.lineIRight.yProperty().bind(Bindings.subtract(Bindings.subtract(bottomEdgeMinusRadiusProperty, arrowIndentProperty), arrowSizeProperty));
      this.lineJRight = new LineTo();
      this.lineJRight.xProperty().bind(rightEdgeProperty);
      this.lineJRight.yProperty().bind(Bindings.subtract(bottomEdgeMinusRadiusProperty, arrowIndentProperty));
      this.lineKRight = new VLineTo();
      this.lineKRight.yProperty().bind(bottomEdgeMinusRadiusProperty);
      this.bottomCurveTo = new QuadCurveTo();
      this.bottomCurveTo.xProperty().bind(rightEdgeMinusRadiusProperty);
      this.bottomCurveTo.yProperty().bind(bottomEdgeProperty);
      this.bottomCurveTo.controlXProperty().bind(rightEdgeProperty);
      this.bottomCurveTo.controlYProperty().bind(bottomEdgeProperty);
      this.lineBBottom = new HLineTo();
      this.lineBBottom.xProperty().bind(Bindings.subtract(rightEdgeMinusRadiusProperty, arrowIndentProperty));
      this.lineCBottom = new LineTo();
      this.lineCBottom.xProperty().bind(Bindings.subtract(this.lineBBottom.xProperty(), arrowSizeProperty));
      this.lineCBottom.yProperty().bind(Bindings.add(bottomEdgeProperty, arrowSizeProperty));
      this.lineDBottom = new LineTo();
      this.lineDBottom.xProperty().bind(Bindings.subtract(this.lineCBottom.xProperty(), arrowSizeProperty));
      this.lineDBottom.yProperty().bind(bottomEdgeProperty);
      this.lineEBottom = new HLineTo();
      this.lineEBottom.xProperty().bind(Bindings.add(centerXProperty, arrowSizeProperty));
      this.lineFBottom = new LineTo();
      this.lineFBottom.xProperty().bind(centerXProperty);
      this.lineFBottom.yProperty().bind(Bindings.add(bottomEdgeProperty, arrowSizeProperty));
      this.lineGBottom = new LineTo();
      this.lineGBottom.xProperty().bind(Bindings.subtract(centerXProperty, arrowSizeProperty));
      this.lineGBottom.yProperty().bind(bottomEdgeProperty);
      this.lineHBottom = new HLineTo();
      this.lineHBottom.xProperty().bind(Bindings.add(Bindings.add(leftEdgePlusRadiusProperty, arrowIndentProperty), Bindings.multiply(arrowSizeProperty, 2)));
      this.lineIBottom = new LineTo();
      this.lineIBottom.xProperty().bind(Bindings.add(Bindings.add(leftEdgePlusRadiusProperty, arrowIndentProperty), arrowSizeProperty));
      this.lineIBottom.yProperty().bind(Bindings.add(bottomEdgeProperty, arrowSizeProperty));
      this.lineJBottom = new LineTo();
      this.lineJBottom.xProperty().bind(Bindings.add(leftEdgePlusRadiusProperty, arrowIndentProperty));
      this.lineJBottom.yProperty().bind(bottomEdgeProperty);
      this.lineKBottom = new HLineTo();
      this.lineKBottom.xProperty().bind(leftEdgePlusRadiusProperty);
      this.leftCurveTo = new QuadCurveTo();
      this.leftCurveTo.xProperty().bind(leftEdgeProperty);
      this.leftCurveTo.yProperty().bind(Bindings.subtract(bottomEdgeProperty, cornerProperty));
      this.leftCurveTo.controlXProperty().bind(leftEdgeProperty);
      this.leftCurveTo.controlYProperty().bind(bottomEdgeProperty);
      this.lineBLeft = new VLineTo();
      this.lineBLeft.yProperty().bind(Bindings.subtract(bottomEdgeMinusRadiusProperty, arrowIndentProperty));
      this.lineCLeft = new LineTo();
      this.lineCLeft.xProperty().bind(Bindings.subtract(leftEdgeProperty, arrowSizeProperty));
      this.lineCLeft.yProperty().bind(Bindings.subtract(this.lineBLeft.yProperty(), arrowSizeProperty));
      this.lineDLeft = new LineTo();
      this.lineDLeft.xProperty().bind(leftEdgeProperty);
      this.lineDLeft.yProperty().bind(Bindings.subtract(this.lineCLeft.yProperty(), arrowSizeProperty));
      this.lineELeft = new VLineTo();
      this.lineELeft.yProperty().bind(Bindings.add(centerYProperty, arrowSizeProperty));
      this.lineFLeft = new LineTo();
      this.lineFLeft.xProperty().bind(Bindings.subtract(leftEdgeProperty, arrowSizeProperty));
      this.lineFLeft.yProperty().bind(centerYProperty);
      this.lineGLeft = new LineTo();
      this.lineGLeft.xProperty().bind(leftEdgeProperty);
      this.lineGLeft.yProperty().bind(Bindings.subtract(centerYProperty, arrowSizeProperty));
      this.lineHLeft = new VLineTo();
      this.lineHLeft.yProperty().bind(Bindings.add(Bindings.add(topEdgePlusRadiusProperty, arrowIndentProperty), Bindings.multiply(arrowSizeProperty, 2)));
      this.lineILeft = new LineTo();
      this.lineILeft.xProperty().bind(Bindings.subtract(leftEdgeProperty, arrowSizeProperty));
      this.lineILeft.yProperty().bind(Bindings.add(Bindings.add(topEdgePlusRadiusProperty, arrowIndentProperty), arrowSizeProperty));
      this.lineJLeft = new LineTo();
      this.lineJLeft.xProperty().bind(leftEdgeProperty);
      this.lineJLeft.yProperty().bind(Bindings.add(topEdgePlusRadiusProperty, arrowIndentProperty));
      this.lineKLeft = new VLineTo();
      this.lineKLeft.yProperty().bind(topEdgePlusRadiusProperty);
      this.topCurveTo = new QuadCurveTo();
      this.topCurveTo.xProperty().bind(leftEdgePlusRadiusProperty);
      this.topCurveTo.yProperty().bind(topEdgeProperty);
      this.topCurveTo.controlXProperty().bind(leftEdgeProperty);
      this.topCurveTo.controlYProperty().bind(topEdgeProperty);
   }

   private Window getPopupWindow() {
      return this.getSkinnable().getScene().getWindow();
   }

   private boolean showArrow(ArrowLocation location) {
      ArrowLocation arrowLocation = this.getSkinnable().getArrowLocation();
      return location.equals(arrowLocation) && !this.getSkinnable().isDetached() && !this.tornOff;
   }

   private void updatePath() {
      List<PathElement> elements = new ArrayList<>();
      elements.add(this.moveTo);
      if (this.showArrow(ArrowLocation.TOP_LEFT)) {
         elements.add(this.lineBTop);
         elements.add(this.lineCTop);
         elements.add(this.lineDTop);
      }

      if (this.showArrow(ArrowLocation.TOP_CENTER)) {
         elements.add(this.lineETop);
         elements.add(this.lineFTop);
         elements.add(this.lineGTop);
      }

      if (this.showArrow(ArrowLocation.TOP_RIGHT)) {
         elements.add(this.lineHTop);
         elements.add(this.lineITop);
         elements.add(this.lineJTop);
      }

      elements.add(this.lineKTop);
      elements.add(this.rightCurveTo);
      if (this.showArrow(ArrowLocation.RIGHT_TOP)) {
         elements.add(this.lineBRight);
         elements.add(this.lineCRight);
         elements.add(this.lineDRight);
      }

      if (this.showArrow(ArrowLocation.RIGHT_CENTER)) {
         elements.add(this.lineERight);
         elements.add(this.lineFRight);
         elements.add(this.lineGRight);
      }

      if (this.showArrow(ArrowLocation.RIGHT_BOTTOM)) {
         elements.add(this.lineHRight);
         elements.add(this.lineIRight);
         elements.add(this.lineJRight);
      }

      elements.add(this.lineKRight);
      elements.add(this.bottomCurveTo);
      if (this.showArrow(ArrowLocation.BOTTOM_RIGHT)) {
         elements.add(this.lineBBottom);
         elements.add(this.lineCBottom);
         elements.add(this.lineDBottom);
      }

      if (this.showArrow(ArrowLocation.BOTTOM_CENTER)) {
         elements.add(this.lineEBottom);
         elements.add(this.lineFBottom);
         elements.add(this.lineGBottom);
      }

      if (this.showArrow(ArrowLocation.BOTTOM_LEFT)) {
         elements.add(this.lineHBottom);
         elements.add(this.lineIBottom);
         elements.add(this.lineJBottom);
      }

      elements.add(this.lineKBottom);
      elements.add(this.leftCurveTo);
      if (this.showArrow(ArrowLocation.LEFT_BOTTOM)) {
         elements.add(this.lineBLeft);
         elements.add(this.lineCLeft);
         elements.add(this.lineDLeft);
      }

      if (this.showArrow(ArrowLocation.LEFT_CENTER)) {
         elements.add(this.lineELeft);
         elements.add(this.lineFLeft);
         elements.add(this.lineGLeft);
      }

      if (this.showArrow(ArrowLocation.LEFT_TOP)) {
         elements.add(this.lineHLeft);
         elements.add(this.lineILeft);
         elements.add(this.lineJLeft);
      }

      elements.add(this.lineKLeft);
      elements.add(this.topCurveTo);
      this.path.getElements().setAll(elements);
      this.clip.getElements().setAll(elements);
   }
}
