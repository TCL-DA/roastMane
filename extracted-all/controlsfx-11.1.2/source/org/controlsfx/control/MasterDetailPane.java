package org.controlsfx.control;

import impl.org.controlsfx.skin.MasterDetailPaneSkin;
import java.util.Objects;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Skin;

public class MasterDetailPane extends ControlsFXControl {
   private final DoubleProperty dividerSizeHint = new SimpleDoubleProperty(this, "dividerSizeHint", 10.0) {
      public void set(double newValue) {
         super.set(Math.max(0.0, newValue));
      }
   };
   private final ObjectProperty<Side> detailSide = new SimpleObjectProperty(this, "detailSide", Side.RIGHT);
   private final BooleanProperty showDetailNode = new SimpleBooleanProperty(this, "showDetailNode", true);
   private final ObjectProperty<Node> masterNode = new SimpleObjectProperty(this, "masterNode");
   private final ObjectProperty<Node> detailNode = new SimpleObjectProperty(this, "detailNode");
   private final BooleanProperty animated = new SimpleBooleanProperty(this, "animated", true);
   private DoubleProperty dividerPosition = new SimpleDoubleProperty(this, "dividerPosition", 0.33);

   public MasterDetailPane(Side side, Node masterNode, Node detailNode, boolean showDetail) {
      Objects.requireNonNull(side);
      Objects.requireNonNull(masterNode);
      Objects.requireNonNull(detailNode);
      this.getStyleClass().add("master-detail-pane");
      this.setDetailSide(side);
      this.setMasterNode(masterNode);
      this.setDetailNode(detailNode);
      this.setShowDetailNode(showDetail);
      switch (side) {
         case BOTTOM:
         case RIGHT:
            this.setDividerPosition(0.8);
            break;
         case TOP:
         case LEFT:
            this.setDividerPosition(0.2);
      }
   }

   public MasterDetailPane(Side pos, boolean showDetail) {
      this(pos, new MasterDetailPane.Placeholder(true), new MasterDetailPane.Placeholder(false), showDetail);
   }

   public MasterDetailPane(Side pos) {
      this(pos, new MasterDetailPane.Placeholder(true), new MasterDetailPane.Placeholder(false), true);
   }

   public MasterDetailPane() {
      this(Side.RIGHT, new MasterDetailPane.Placeholder(true), new MasterDetailPane.Placeholder(false), true);
   }

   protected Skin<?> createDefaultSkin() {
      return new MasterDetailPaneSkin(this);
   }

   public String getUserAgentStylesheet() {
      return this.getUserAgentStylesheet(MasterDetailPane.class, "masterdetailpane.css");
   }

   public final void resetDividerPosition() {
      Node node = this.getDetailNode();
      if (node != null) {
         boolean wasShowing = this.isShowDetailNode();
         boolean wasAnimated = this.isAnimated();
         if (!wasShowing) {
            this.setAnimated(false);
            this.setShowDetailNode(true);
            node.applyCss();
         }

         double dividerSize = this.getDividerSizeHint();
         double ps;
         switch (this.getDetailSide()) {
            case BOTTOM:
            case TOP:
            default:
               ps = node.prefHeight(-1.0) + dividerSize;
               break;
            case RIGHT:
            case LEFT:
               ps = node.prefWidth(-1.0) + dividerSize;
         }

         double position = 0.0;
         switch (this.getDetailSide()) {
            case BOTTOM:
               position = 1.0 - ps / this.getHeight();
               break;
            case RIGHT:
               position = 1.0 - ps / this.getWidth();
               break;
            case TOP:
               position = ps / this.getHeight();
               break;
            case LEFT:
               position = ps / this.getWidth();
         }

         this.setDividerPosition(Math.min(1.0, Math.max(0.0, position)));
         if (!wasShowing) {
            this.setShowDetailNode(wasShowing);
            this.setAnimated(wasAnimated);
         }
      }
   }

   public final DoubleProperty dividerSizeHintProperty() {
      return this.dividerSizeHint;
   }

   public final void setDividerSizeHint(double size) {
      this.dividerSizeHint.set(size);
   }

   public final double getDividerSizeHint() {
      return this.dividerSizeHint.get();
   }

   public final ObjectProperty<Side> detailSideProperty() {
      return this.detailSide;
   }

   public final Side getDetailSide() {
      return (Side)this.detailSideProperty().get();
   }

   public final void setDetailSide(Side side) {
      Objects.requireNonNull(side);
      this.detailSideProperty().set(side);
   }

   public final BooleanProperty showDetailNodeProperty() {
      return this.showDetailNode;
   }

   public final boolean isShowDetailNode() {
      return this.showDetailNodeProperty().get();
   }

   public final void setShowDetailNode(boolean show) {
      this.showDetailNodeProperty().set(show);
   }

   public final ObjectProperty<Node> masterNodeProperty() {
      return this.masterNode;
   }

   public final Node getMasterNode() {
      return (Node)this.masterNodeProperty().get();
   }

   public final void setMasterNode(Node node) {
      Objects.requireNonNull(node);
      this.masterNodeProperty().set(node);
   }

   public final ObjectProperty<Node> detailNodeProperty() {
      return this.detailNode;
   }

   public final Node getDetailNode() {
      return (Node)this.detailNodeProperty().get();
   }

   public final void setDetailNode(Node node) {
      this.detailNodeProperty().set(node);
   }

   public final BooleanProperty animatedProperty() {
      return this.animated;
   }

   public final boolean isAnimated() {
      return this.animatedProperty().get();
   }

   public final void setAnimated(boolean animated) {
      this.animatedProperty().set(animated);
   }

   public final DoubleProperty dividerPositionProperty() {
      return this.dividerPosition;
   }

   public final double getDividerPosition() {
      return this.dividerPosition.get();
   }

   public final void setDividerPosition(double position) {
      if (this.getDividerPosition() == position) {
         this.dividerPosition.set(-1.0);
      }

      this.dividerPosition.set(position);
   }

   private static final class Placeholder extends Label {
      public Placeholder(boolean master) {
         super(master ? "Master" : "Detail");
         this.setAlignment(Pos.CENTER);
         if (master) {
            this.setStyle("-fx-background-color: -fx-background;");
         } else {
            this.setStyle("-fx-background-color: derive(-fx-background, -10%);");
         }
      }
   }
}
