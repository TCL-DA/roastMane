package org.controlsfx.control;

import impl.org.controlsfx.skin.HiddenSidesPaneSkin;
import java.util.Objects;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.Skin;
import javafx.util.Duration;

public class HiddenSidesPane extends ControlsFXControl {
   private DoubleProperty triggerDistance = new SimpleDoubleProperty(this, "triggerDistance", 16.0);
   private ObjectProperty<Node> content = new SimpleObjectProperty(this, "content");
   private ObjectProperty<Node> top = new SimpleObjectProperty(this, "top");
   private ObjectProperty<Node> right = new SimpleObjectProperty(this, "right");
   private ObjectProperty<Node> bottom = new SimpleObjectProperty(this, "bottom");
   private ObjectProperty<Node> left = new SimpleObjectProperty(this, "left");
   private ObjectProperty<Side> pinnedSide = new SimpleObjectProperty(this, "pinnedSide");
   private final ObjectProperty<Duration> animationDelay = new SimpleObjectProperty(this, "animationDelay", Duration.millis(300.0));
   private final ObjectProperty<Duration> animationDuration = new SimpleObjectProperty(this, "animationDuration", Duration.millis(200.0));

   public HiddenSidesPane(Node content, Node top, Node right, Node bottom, Node left) {
      this.setContent(content);
      this.setTop(top);
      this.setRight(right);
      this.setBottom(bottom);
      this.setLeft(left);
   }

   public HiddenSidesPane() {
      this(null, null, null, null, null);
   }

   protected Skin<?> createDefaultSkin() {
      return new HiddenSidesPaneSkin(this);
   }

   public final DoubleProperty triggerDistanceProperty() {
      return this.triggerDistance;
   }

   public final double getTriggerDistance() {
      return this.triggerDistance.get();
   }

   public final void setTriggerDistance(double distance) {
      this.triggerDistance.set(distance);
   }

   public final ObjectProperty<Node> contentProperty() {
      return this.content;
   }

   public final Node getContent() {
      return (Node)this.contentProperty().get();
   }

   public final void setContent(Node content) {
      this.contentProperty().set(content);
   }

   public final ObjectProperty<Node> topProperty() {
      return this.top;
   }

   public final Node getTop() {
      return (Node)this.topProperty().get();
   }

   public final void setTop(Node top) {
      this.topProperty().set(top);
   }

   public final ObjectProperty<Node> rightProperty() {
      return this.right;
   }

   public final Node getRight() {
      return (Node)this.rightProperty().get();
   }

   public final void setRight(Node right) {
      this.rightProperty().set(right);
   }

   public final ObjectProperty<Node> bottomProperty() {
      return this.bottom;
   }

   public final Node getBottom() {
      return (Node)this.bottomProperty().get();
   }

   public final void setBottom(Node bottom) {
      this.bottomProperty().set(bottom);
   }

   public final ObjectProperty<Node> leftProperty() {
      return this.left;
   }

   public final Node getLeft() {
      return (Node)this.leftProperty().get();
   }

   public final void setLeft(Node left) {
      this.leftProperty().set(left);
   }

   public final ObjectProperty<Side> pinnedSideProperty() {
      return this.pinnedSide;
   }

   public final Side getPinnedSide() {
      return (Side)this.pinnedSideProperty().get();
   }

   public final void setPinnedSide(Side side) {
      this.pinnedSideProperty().set(side);
   }

   public final ObjectProperty<Duration> animationDelayProperty() {
      return this.animationDelay;
   }

   public final Duration getAnimationDelay() {
      return (Duration)this.animationDelay.get();
   }

   public final void setAnimationDelay(Duration duration) {
      this.animationDelay.set(duration);
   }

   public final ObjectProperty<Duration> animationDurationProperty() {
      return this.animationDuration;
   }

   public final Duration getAnimationDuration() {
      return (Duration)this.animationDuration.get();
   }

   public final void setAnimationDuration(Duration duration) {
      this.animationDuration.set(duration);
   }

   public void show(Side side) {
      Objects.requireNonNull(side, "side cannot be null");
      this.getProperties().put("showPane", side);
   }

   public void hide() {
      this.getProperties().put("showPane", null);
   }
}
