package org.controlsfx.control.decoration;

import impl.org.controlsfx.ImplUtils;
import java.util.List;
import java.util.Optional;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;

public class GraphicDecoration extends Decoration {
   private final Node decorationNode;
   private final Pos pos;
   private final double xOffset;
   private final double yOffset;
   private final ChangeListener<Boolean> targetNeedsLayoutListener;

   public GraphicDecoration(Node decorationNode) {
      this(decorationNode, Pos.TOP_LEFT);
   }

   public GraphicDecoration(Node decorationNode, Pos position) {
      this(decorationNode, position, 0.0, 0.0);
   }

   public GraphicDecoration(Node decorationNode, Pos position, double xOffset, double yOffset) {
      this.decorationNode = decorationNode;
      this.decorationNode.setManaged(false);
      this.pos = position;
      this.xOffset = xOffset;
      this.yOffset = yOffset;
      this.targetNeedsLayoutListener = (__, ___, ____) -> Optional.ofNullable(decorationNode.getParent()).ifPresent(this::layoutGraphic);
   }

   @Override
   public Node applyDecoration(Node targetNode) {
      List<Node> targetNodeChildren = ImplUtils.getChildren((Parent)targetNode, true);
      this.layoutGraphic(targetNode);
      if (!targetNodeChildren.contains(this.decorationNode)) {
         targetNodeChildren.add(this.decorationNode);
      }

      ((Parent)targetNode).needsLayoutProperty().removeListener(this.targetNeedsLayoutListener);
      ((Parent)targetNode).needsLayoutProperty().addListener(this.targetNeedsLayoutListener);
      return null;
   }

   @Override
   public void removeDecoration(Node targetNode) {
      List<Node> targetNodeChildren = ImplUtils.getChildren((Parent)targetNode, true);
      if (targetNodeChildren.contains(this.decorationNode)) {
         targetNodeChildren.remove(this.decorationNode);
      }

      ((Parent)targetNode).needsLayoutProperty().removeListener(this.targetNeedsLayoutListener);
   }

   private void layoutGraphic(Node targetNode) {
      this.decorationNode.autosize();
      Bounds decorationNodeLayoutBounds = this.decorationNode.getLayoutBounds();
      double decorationNodeWidth = decorationNodeLayoutBounds.getWidth();
      double decorationNodeHeight = decorationNodeLayoutBounds.getHeight();
      Bounds targetBounds = targetNode.getLayoutBounds();
      double x = targetBounds.getMinX();
      double y = targetBounds.getMinY();
      double targetWidth = targetBounds.getWidth();
      if (targetWidth <= 0.0) {
         targetWidth = targetNode.prefWidth(-1.0);
      }

      double targetHeight = targetBounds.getHeight();
      if (targetHeight <= 0.0) {
         targetHeight = targetNode.prefHeight(-1.0);
      }

      switch (this.pos.getHpos()) {
         case CENTER:
            x += targetWidth / 2.0 - decorationNodeWidth / 2.0;
            break;
         case LEFT:
            x -= decorationNodeWidth / 2.0;
            break;
         case RIGHT:
            x += targetWidth - decorationNodeWidth / 2.0;
      }

      switch (this.pos.getVpos()) {
         case CENTER:
            y += targetHeight / 2.0 - decorationNodeHeight / 2.0;
            break;
         case TOP:
            y -= decorationNodeHeight / 2.0;
            break;
         case BOTTOM:
            y += targetHeight - decorationNodeHeight / 2.0;
            break;
         case BASELINE:
            y += targetNode.getBaselineOffset() - this.decorationNode.getBaselineOffset() - decorationNodeHeight / 2.0;
      }

      this.decorationNode.setLayoutX(x + this.xOffset);
      this.decorationNode.setLayoutY(y + this.yOffset);
   }
}
