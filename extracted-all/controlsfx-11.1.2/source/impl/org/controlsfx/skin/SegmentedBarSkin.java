package impl.org.controlsfx.skin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.OptionalDouble;
import javafx.beans.InvalidationListener;
import javafx.beans.WeakInvalidationListener;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.SkinBase;
import javafx.util.Callback;
import org.controlsfx.control.PopOver;
import org.controlsfx.control.SegmentedBar;

public class SegmentedBarSkin<T extends SegmentedBar.Segment> extends SkinBase<SegmentedBar<T>> {
   private Map<T, Node> segmentNodes = new HashMap<>();
   private InvalidationListener buildListener = it -> this.buildSegments();
   private WeakInvalidationListener weakBuildListener = new WeakInvalidationListener(this.buildListener);
   private InvalidationListener layoutListener = it -> ((SegmentedBar)this.getSkinnable()).requestLayout();
   private WeakInvalidationListener weakLayoutListener = new WeakInvalidationListener(this.layoutListener);
   private PopOver popOver;

   public SegmentedBarSkin(SegmentedBar<T> bar) {
      super(bar);
      bar.segmentViewFactoryProperty().addListener(this.weakBuildListener);
      bar.getSegments().addListener(this.weakBuildListener);
      bar.orientationProperty().addListener(this.weakLayoutListener);
      bar.totalProperty().addListener(this.weakBuildListener);
      bar.orientationProperty().addListener(it -> {
         if (this.popOver != null) {
            switch (bar.getOrientation()) {
               case HORIZONTAL:
                  this.popOver.setArrowLocation(PopOver.ArrowLocation.BOTTOM_CENTER);
                  break;
               case VERTICAL:
                  this.popOver.setArrowLocation(PopOver.ArrowLocation.RIGHT_CENTER);
            }
         }
      });
      this.buildSegments();
   }

   protected double computePrefHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
      if (((SegmentedBar)this.getSkinnable()).getOrientation().equals(Orientation.HORIZONTAL)) {
         OptionalDouble maxHeight = this.getChildren().stream().mapToDouble(node -> node.prefHeight(-1.0)).max();
         if (maxHeight.isPresent()) {
            return maxHeight.getAsDouble();
         }
      }

      return ((SegmentedBar)this.getSkinnable()).getPrefHeight();
   }

   protected double computePrefWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
      if (((SegmentedBar)this.getSkinnable()).getOrientation().equals(Orientation.VERTICAL)) {
         OptionalDouble maxWidth = this.getChildren().stream().mapToDouble(node -> node.prefWidth(height)).max();
         if (maxWidth.isPresent()) {
            return maxWidth.getAsDouble();
         }
      }

      return ((SegmentedBar)this.getSkinnable()).getPrefWidth();
   }

   protected double computeMinHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
      return ((SegmentedBar)this.getSkinnable()).getOrientation().equals(Orientation.HORIZONTAL)
         ? this.computePrefHeight(width, topInset, rightInset, bottomInset, leftInset)
         : 0.0;
   }

   protected double computeMinWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
      return ((SegmentedBar)this.getSkinnable()).getOrientation().equals(Orientation.VERTICAL)
         ? this.computePrefWidth(height, topInset, rightInset, bottomInset, leftInset)
         : 0.0;
   }

   protected double computeMaxHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
      return ((SegmentedBar)this.getSkinnable()).getOrientation().equals(Orientation.HORIZONTAL)
         ? this.computePrefHeight(width, topInset, rightInset, bottomInset, leftInset)
         : Double.MAX_VALUE;
   }

   protected double computeMaxWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
      return ((SegmentedBar)this.getSkinnable()).getOrientation().equals(Orientation.VERTICAL)
         ? this.computePrefWidth(height, topInset, rightInset, bottomInset, leftInset)
         : Double.MAX_VALUE;
   }

   private void buildSegments() {
      this.segmentNodes.clear();
      this.getChildren().clear();
      List<T> segments = ((SegmentedBar)this.getSkinnable()).getSegments();
      int size = segments.size();
      Callback<T, Node> cellFactory = ((SegmentedBar)this.getSkinnable()).getSegmentViewFactory();

      for (int i = 0; i < size; i++) {
         T segment = segments.get(i);
         Node segmentNode = (Node)cellFactory.call(segment);
         this.segmentNodes.put(segment, segmentNode);
         this.getChildren().add(segmentNode);
         segmentNode.getStyleClass().add("segment");
         if (i == 0) {
            if (size == 1) {
               segmentNode.getStyleClass().add("only-segment");
            } else {
               segmentNode.getStyleClass().add("first-segment");
            }
         } else if (i == size - 1) {
            segmentNode.getStyleClass().add("last-segment");
         } else {
            segmentNode.getStyleClass().add("middle-segment");
         }

         segmentNode.setOnMouseEntered(evt -> this.showPopOver(segmentNode, segment));
         segmentNode.setOnMouseExited(evt -> this.hidePopOver());
      }

      ((SegmentedBar)this.getSkinnable()).requestLayout();
   }

   private void showPopOver(Node owner, T segment) {
      Callback<T, Node> infoNodeFactory = ((SegmentedBar)this.getSkinnable()).getInfoNodeFactory();
      Node infoNode = null;
      if (infoNodeFactory != null) {
         infoNode = (Node)infoNodeFactory.call(segment);
      }

      if (infoNode != null) {
         if (this.popOver == null) {
            this.popOver = new PopOver();
            this.popOver.setAnimated(false);
            this.popOver.setArrowLocation(PopOver.ArrowLocation.BOTTOM_CENTER);
            this.popOver.setDetachable(false);
            this.popOver.setArrowSize(6.0);
            this.popOver.setCornerRadius(3.0);
            this.popOver.setAutoFix(false);
            this.popOver.setAutoHide(true);
         }

         this.popOver.setContentNode(infoNode);
         this.popOver.show(owner, -2.0);
      }
   }

   private void hidePopOver() {
      if (this.popOver != null && this.popOver.isShowing()) {
         this.popOver.hide();
      }
   }

   protected void layoutChildren(double contentX, double contentY, double contentWidth, double contentHeight) {
      double total = ((SegmentedBar)this.getSkinnable()).getTotal();
      List<T> segments = ((SegmentedBar)this.getSkinnable()).getSegments();
      int size = segments.size();
      double x = contentX;
      double y = contentY + contentHeight;

      for (int i = 0; i < size; i++) {
         SegmentedBar.Segment segment = segments.get(i);
         Node segmentNode = this.segmentNodes.get(segment);
         double segmentValue = segment.getValue();
         if (((SegmentedBar)this.getSkinnable()).getOrientation().equals(Orientation.HORIZONTAL)) {
            double segmentWidth = segmentValue / total * contentWidth;
            segmentNode.resizeRelocate(x, contentY, segmentWidth, contentHeight);
            x += segmentWidth;
         } else {
            double segmentHeight = segmentValue / total * contentHeight;
            segmentNode.resizeRelocate(contentX, y - segmentHeight, contentWidth, segmentHeight);
            y -= segmentHeight;
         }
      }
   }
}
