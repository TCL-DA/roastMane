package org.controlsfx.control;

import impl.org.controlsfx.i18n.Localization;
import impl.org.controlsfx.skin.PopOverSkin;
import java.util.Objects;
import javafx.animation.FadeTransition;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.WeakInvalidationListener;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.WeakChangeListener;
import javafx.event.EventHandler;
import javafx.event.WeakEventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.PopupControl;
import javafx.scene.control.Skin;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import javafx.stage.PopupWindow.AnchorLocation;
import javafx.util.Duration;

public class PopOver extends PopupControl {
   private static final String DEFAULT_STYLE_CLASS = "popover";
   private static final Duration DEFAULT_FADE_DURATION = Duration.seconds(0.2);
   private double targetX;
   private double targetY;
   private final SimpleBooleanProperty animated = new SimpleBooleanProperty(true);
   private final ObjectProperty<Duration> fadeInDuration = new SimpleObjectProperty(DEFAULT_FADE_DURATION);
   private final ObjectProperty<Duration> fadeOutDuration = new SimpleObjectProperty(DEFAULT_FADE_DURATION);
   private final StackPane root = new StackPane();
   private final ObjectProperty<Node> contentNode = new SimpleObjectProperty<Node>(this, "contentNode") {
      public void setValue(Node node) {
         if (node == null) {
            throw new IllegalArgumentException("content node can not be null");
         }
      }
   };
   private InvalidationListener hideListener = new InvalidationListener() {
      public void invalidated(Observable observable) {
         if (!PopOver.this.isDetached()) {
            PopOver.this.hide(Duration.ZERO);
         }
      }
   };
   private WeakInvalidationListener weakHideListener = new WeakInvalidationListener(this.hideListener);
   private ChangeListener<Number> xListener = new ChangeListener<Number>() {
      public void changed(ObservableValue<? extends Number> value, Number oldX, Number newX) {
         if (!PopOver.this.isDetached()) {
            PopOver.this.setAnchorX(PopOver.this.getAnchorX() + (newX.doubleValue() - oldX.doubleValue()));
         }
      }
   };
   private WeakChangeListener<Number> weakXListener = new WeakChangeListener(this.xListener);
   private ChangeListener<Number> yListener = new ChangeListener<Number>() {
      public void changed(ObservableValue<? extends Number> value, Number oldY, Number newY) {
         if (!PopOver.this.isDetached()) {
            PopOver.this.setAnchorY(PopOver.this.getAnchorY() + (newY.doubleValue() - oldY.doubleValue()));
         }
      }
   };
   private WeakChangeListener<Number> weakYListener = new WeakChangeListener(this.yListener);
   private Window ownerWindow;
   private final EventHandler<WindowEvent> closePopOverOnOwnerWindowCloseLambda = event -> this.ownerWindowHiding();
   private final WeakEventHandler<WindowEvent> closePopOverOnOwnerWindowClose = new WeakEventHandler(this.closePopOverOnOwnerWindowCloseLambda);
   private final BooleanProperty headerAlwaysVisible = new SimpleBooleanProperty(this, "headerAlwaysVisible");
   private final BooleanProperty closeButtonEnabled = new SimpleBooleanProperty(this, "closeButtonEnabled", true);
   private final BooleanProperty detachable = new SimpleBooleanProperty(this, "detachable", true);
   private final BooleanProperty detached = new SimpleBooleanProperty(this, "detached", false);
   private final DoubleProperty arrowSize = new SimpleDoubleProperty(this, "arrowSize", 12.0);
   private final DoubleProperty arrowIndent = new SimpleDoubleProperty(this, "arrowIndent", 12.0);
   private final DoubleProperty cornerRadius = new SimpleDoubleProperty(this, "cornerRadius", 6.0);
   private final StringProperty title = new SimpleStringProperty(this, "title", Localization.localize(Localization.asKey("popOver.default.title")));
   private final ObjectProperty<PopOver.ArrowLocation> arrowLocation = new SimpleObjectProperty(this, "arrowLocation", PopOver.ArrowLocation.LEFT_TOP);

   public PopOver() {
      this.getStyleClass().add("popover");
      this.getRoot().getStylesheets().add(PopOver.class.getResource("popover.css").toExternalForm());
      this.setAnchorLocation(AnchorLocation.WINDOW_TOP_LEFT);
      this.setOnHiding(new EventHandler<WindowEvent>() {
         public void handle(WindowEvent evt) {
            PopOver.this.setDetached(false);
         }
      });
      Label label = new Label(Localization.localize(Localization.asKey("popOver.default.content")));
      label.setPrefSize(200.0, 200.0);
      label.setPadding(new Insets(4.0));
      this.setContentNode(label);
      InvalidationListener repositionListener = observable -> {
         if (this.isShowing() && !this.isDetached()) {
            this.show(this.getOwnerNode(), this.targetX, this.targetY);
            this.adjustWindowLocation();
         }
      };
      this.arrowSize.addListener(repositionListener);
      this.cornerRadius.addListener(repositionListener);
      this.arrowLocation.addListener(repositionListener);
      this.arrowIndent.addListener(repositionListener);
      this.headerAlwaysVisible.addListener(repositionListener);
      this.detached.addListener(it -> {
         if (this.isDetached()) {
            this.setAutoHide(false);
         } else {
            this.setAutoHide(true);
         }
      });
      this.setAutoHide(true);
   }

   public PopOver(Node content) {
      this();
      this.setContentNode(content);
   }

   protected Skin<?> createDefaultSkin() {
      return new PopOverSkin(this);
   }

   public final StackPane getRoot() {
      return this.root;
   }

   public final ObjectProperty<Node> contentNodeProperty() {
      return this.contentNode;
   }

   public final Node getContentNode() {
      return (Node)this.contentNodeProperty().get();
   }

   public final void setContentNode(Node content) {
      this.contentNodeProperty().set(content);
   }

   public final void show(Node owner) {
      this.show(owner, 4.0);
   }

   public final void show(Node owner, double offset) {
      Objects.requireNonNull(owner);
      Bounds bounds = owner.localToScreen(owner.getBoundsInLocal());
      switch (this.getArrowLocation()) {
         case BOTTOM_CENTER:
         case BOTTOM_LEFT:
         case BOTTOM_RIGHT:
            this.show(owner, bounds.getMinX() + bounds.getWidth() / 2.0, bounds.getMinY() + offset);
            break;
         case LEFT_BOTTOM:
         case LEFT_CENTER:
         case LEFT_TOP:
            this.show(owner, bounds.getMaxX() - offset, bounds.getMinY() + bounds.getHeight() / 2.0);
            break;
         case RIGHT_BOTTOM:
         case RIGHT_CENTER:
         case RIGHT_TOP:
            this.show(owner, bounds.getMinX() + offset, bounds.getMinY() + bounds.getHeight() / 2.0);
            break;
         case TOP_CENTER:
         case TOP_LEFT:
         case TOP_RIGHT:
            this.show(owner, bounds.getMinX() + bounds.getWidth() / 2.0, bounds.getMinY() + bounds.getHeight() - offset);
      }
   }

   public final void show(Window owner) {
      super.show(owner);
      this.ownerWindow = owner;
      if (this.isAnimated()) {
         this.showFadeInAnimation(this.getFadeInDuration());
      }

      this.ownerWindow.addEventFilter(WindowEvent.WINDOW_HIDING, this.closePopOverOnOwnerWindowClose);
   }

   public final void show(Window ownerWindow, double anchorX, double anchorY) {
      super.show(ownerWindow, anchorX, anchorY);
      this.ownerWindow = ownerWindow;
      if (this.isAnimated()) {
         this.showFadeInAnimation(this.getFadeInDuration());
      }

      ownerWindow.addEventFilter(WindowEvent.WINDOW_HIDING, this.closePopOverOnOwnerWindowClose);
   }

   public final void show(Node owner, double x, double y) {
      this.show(owner, x, y, this.getFadeInDuration());
   }

   public final void show(Node owner, double x, double y, Duration fadeInDuration) {
      if (this.ownerWindow != null && this.isShowing()) {
         super.hide();
      }

      this.targetX = x;
      this.targetY = y;
      if (owner == null) {
         throw new IllegalArgumentException("owner can not be null");
      } else {
         if (fadeInDuration == null) {
            fadeInDuration = DEFAULT_FADE_DURATION;
         }

         if (this.ownerWindow != null) {
            this.ownerWindow.xProperty().removeListener(this.weakXListener);
            this.ownerWindow.yProperty().removeListener(this.weakYListener);
            this.ownerWindow.widthProperty().removeListener(this.weakHideListener);
            this.ownerWindow.heightProperty().removeListener(this.weakHideListener);
         }

         this.ownerWindow = owner.getScene().getWindow();
         this.ownerWindow.xProperty().addListener(this.weakXListener);
         this.ownerWindow.yProperty().addListener(this.weakYListener);
         this.ownerWindow.widthProperty().addListener(this.weakHideListener);
         this.ownerWindow.heightProperty().addListener(this.weakHideListener);
         this.setOnShown(evt -> {
            this.getScene().addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> {
               if (mouseEvent.getTarget().equals(this.getScene().getRoot()) && !this.isDetached()) {
                  this.hide();
               }
            });
            this.adjustWindowLocation();
         });
         super.show(owner, x, y);
         if (this.isAnimated()) {
            this.showFadeInAnimation(fadeInDuration);
         }

         this.ownerWindow.addEventFilter(WindowEvent.WINDOW_HIDING, this.closePopOverOnOwnerWindowClose);
      }
   }

   private void showFadeInAnimation(Duration fadeInDuration) {
      Node skinNode = this.getSkin().getNode();
      skinNode.setOpacity(0.0);
      FadeTransition fadeIn = new FadeTransition(fadeInDuration, skinNode);
      fadeIn.setFromValue(0.0);
      fadeIn.setToValue(1.0);
      fadeIn.play();
   }

   private void ownerWindowHiding() {
      this.hide(Duration.ZERO);
      if (this.ownerWindow != null) {
         this.ownerWindow.removeEventFilter(WindowEvent.WINDOW_HIDING, this.closePopOverOnOwnerWindowClose);
      }
   }

   public final void hide() {
      this.hide(this.getFadeOutDuration());
   }

   public final void hide(Duration fadeOutDuration) {
      if (fadeOutDuration == null) {
         fadeOutDuration = DEFAULT_FADE_DURATION;
      }

      if (this.isShowing()) {
         if (this.isAnimated()) {
            Node skinNode = this.getSkin().getNode();
            FadeTransition fadeOut = new FadeTransition(fadeOutDuration, skinNode);
            fadeOut.setFromValue(skinNode.getOpacity());
            fadeOut.setToValue(0.0);
            fadeOut.setOnFinished(evt -> super.hide());
            fadeOut.play();
         } else {
            super.hide();
         }
      }
   }

   private void adjustWindowLocation() {
      Bounds bounds = this.getSkin().getNode().getBoundsInParent();
      switch (this.getArrowLocation()) {
         case BOTTOM_CENTER:
         case BOTTOM_LEFT:
         case BOTTOM_RIGHT:
            this.setAnchorX(this.getAnchorX() + bounds.getMinX() - this.computeXOffset());
            this.setAnchorY(this.getAnchorY() - bounds.getMinY() - bounds.getMaxY() - 1.0);
            break;
         case LEFT_BOTTOM:
         case LEFT_CENTER:
         case LEFT_TOP:
            this.setAnchorX(this.getAnchorX() + bounds.getMinX() + this.getArrowSize());
            this.setAnchorY(this.getAnchorY() + bounds.getMinY() - this.computeYOffset());
            break;
         case RIGHT_BOTTOM:
         case RIGHT_CENTER:
         case RIGHT_TOP:
            this.setAnchorX(this.getAnchorX() - bounds.getMinX() - bounds.getMaxX() - 1.0);
            this.setAnchorY(this.getAnchorY() + bounds.getMinY() - this.computeYOffset());
            break;
         case TOP_CENTER:
         case TOP_LEFT:
         case TOP_RIGHT:
            this.setAnchorX(this.getAnchorX() + bounds.getMinX() - this.computeXOffset());
            this.setAnchorY(this.getAnchorY() + bounds.getMinY() + this.getArrowSize());
      }
   }

   private double computeXOffset() {
      switch (this.getArrowLocation()) {
         case BOTTOM_CENTER:
         case TOP_CENTER:
            return this.getContentNode().prefWidth(-1.0) / 2.0;
         case BOTTOM_LEFT:
         case TOP_LEFT:
            return this.getCornerRadius() + this.getArrowIndent() + this.getArrowSize();
         case BOTTOM_RIGHT:
         case TOP_RIGHT:
            return this.getContentNode().prefWidth(-1.0) - this.getArrowIndent() - this.getCornerRadius() - this.getArrowSize();
         case LEFT_BOTTOM:
         case LEFT_CENTER:
         case LEFT_TOP:
         case RIGHT_BOTTOM:
         case RIGHT_CENTER:
         case RIGHT_TOP:
         default:
            return 0.0;
      }
   }

   private double computeYOffset() {
      double prefContentHeight = this.getContentNode().prefHeight(-1.0);
      switch (this.getArrowLocation()) {
         case LEFT_BOTTOM:
         case RIGHT_BOTTOM:
            return Math.max(
               prefContentHeight - this.getCornerRadius() - this.getArrowIndent() - this.getArrowSize(),
               this.getCornerRadius() + this.getArrowIndent() + this.getArrowSize()
            );
         case LEFT_CENTER:
         case RIGHT_CENTER:
            return Math.max(prefContentHeight, 2.0 * (this.getCornerRadius() + this.getArrowIndent() + this.getArrowSize())) / 2.0;
         case LEFT_TOP:
         case RIGHT_TOP:
            return this.getCornerRadius() + this.getArrowIndent() + this.getArrowSize();
         default:
            return 0.0;
      }
   }

   public final void detach() {
      if (this.isDetachable()) {
         this.setDetached(true);
      }
   }

   public final BooleanProperty headerAlwaysVisibleProperty() {
      return this.headerAlwaysVisible;
   }

   public final void setHeaderAlwaysVisible(boolean visible) {
      this.headerAlwaysVisible.setValue(visible);
   }

   public final boolean isHeaderAlwaysVisible() {
      return this.headerAlwaysVisible.getValue();
   }

   public final BooleanProperty closeButtonEnabledProperty() {
      return this.closeButtonEnabled;
   }

   public final void setCloseButtonEnabled(boolean enabled) {
      this.closeButtonEnabled.setValue(enabled);
   }

   public final boolean isCloseButtonEnabled() {
      return this.closeButtonEnabled.getValue();
   }

   public final BooleanProperty detachableProperty() {
      return this.detachable;
   }

   public final void setDetachable(boolean detachable) {
      this.detachableProperty().set(detachable);
   }

   public final boolean isDetachable() {
      return this.detachableProperty().get();
   }

   public final BooleanProperty detachedProperty() {
      return this.detached;
   }

   public final void setDetached(boolean detached) {
      this.detachedProperty().set(detached);
   }

   public final boolean isDetached() {
      return this.detachedProperty().get();
   }

   public final DoubleProperty arrowSizeProperty() {
      return this.arrowSize;
   }

   public final double getArrowSize() {
      return this.arrowSizeProperty().get();
   }

   public final void setArrowSize(double size) {
      this.arrowSizeProperty().set(size);
   }

   public final DoubleProperty arrowIndentProperty() {
      return this.arrowIndent;
   }

   public final double getArrowIndent() {
      return this.arrowIndentProperty().get();
   }

   public final void setArrowIndent(double size) {
      this.arrowIndentProperty().set(size);
   }

   public final DoubleProperty cornerRadiusProperty() {
      return this.cornerRadius;
   }

   public final double getCornerRadius() {
      return this.cornerRadiusProperty().get();
   }

   public final void setCornerRadius(double radius) {
      this.cornerRadiusProperty().set(radius);
   }

   public final StringProperty titleProperty() {
      return this.title;
   }

   public final String getTitle() {
      return (String)this.titleProperty().get();
   }

   public final void setTitle(String title) {
      if (title == null) {
         throw new IllegalArgumentException("title can not be null");
      } else {
         this.titleProperty().set(title);
      }
   }

   public final ObjectProperty<PopOver.ArrowLocation> arrowLocationProperty() {
      return this.arrowLocation;
   }

   public final void setArrowLocation(PopOver.ArrowLocation location) {
      this.arrowLocationProperty().set(location);
   }

   public final PopOver.ArrowLocation getArrowLocation() {
      return (PopOver.ArrowLocation)this.arrowLocationProperty().get();
   }

   public final ObjectProperty<Duration> fadeInDurationProperty() {
      return this.fadeInDuration;
   }

   public final ObjectProperty<Duration> fadeOutDurationProperty() {
      return this.fadeOutDuration;
   }

   public final Duration getFadeInDuration() {
      return (Duration)this.fadeInDurationProperty().get();
   }

   public final void setFadeInDuration(Duration duration) {
      this.fadeInDurationProperty().setValue(duration);
   }

   public final Duration getFadeOutDuration() {
      return (Duration)this.fadeOutDurationProperty().get();
   }

   public final void setFadeOutDuration(Duration duration) {
      this.fadeOutDurationProperty().setValue(duration);
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

   public static enum ArrowLocation {
      LEFT_TOP,
      LEFT_CENTER,
      LEFT_BOTTOM,
      RIGHT_TOP,
      RIGHT_CENTER,
      RIGHT_BOTTOM,
      TOP_LEFT,
      TOP_CENTER,
      TOP_RIGHT,
      BOTTOM_LEFT,
      BOTTOM_CENTER,
      BOTTOM_RIGHT;
   }
}
