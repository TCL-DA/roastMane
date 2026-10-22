package impl.org.controlsfx.skin;

import impl.org.controlsfx.ImplUtils;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.animation.Animation.Status;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import org.controlsfx.control.NotificationPane;
import org.controlsfx.control.action.Action;
import org.controlsfx.control.action.ActionUtils;

public abstract class NotificationBar extends Region {
   private static final double MIN_HEIGHT = 40.0;
   final Label label;
   Label title;
   ButtonBar actionsBar;
   Button closeBtn;
   private final GridPane pane;
   public DoubleProperty transition = new SimpleDoubleProperty() {
      protected void invalidated() {
         NotificationBar.this.requestContainerLayout();
      }
   };
   private final Duration TRANSITION_DURATION = new Duration(350.0);
   private Timeline timeline;
   private double transitionStartValue;

   public void requestContainerLayout() {
      this.layoutChildren();
   }

   public String getTitle() {
      return "";
   }

   public boolean isCloseButtonVisible() {
      return true;
   }

   public abstract String getText();

   public abstract Node getGraphic();

   public abstract ObservableList<Action> getActions();

   public abstract void hide();

   public abstract boolean isShowing();

   public abstract boolean isShowFromTop();

   public abstract double getContainerHeight();

   public abstract void relocateInParent(double var1, double var3);

   public NotificationBar() {
      this.getStyleClass().add("notification-bar");
      this.setVisible(this.isShowing());
      this.pane = new GridPane();
      this.pane.getStyleClass().add("pane");
      this.pane.setAlignment(Pos.BASELINE_LEFT);
      this.getChildren().setAll(new Node[]{this.pane});
      String titleStr = this.getTitle();
      if (titleStr != null && !titleStr.isEmpty()) {
         this.title = new Label();
         this.title.setFocusTraversable(false);
         this.title.getStyleClass().add("title");
         this.title.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
         GridPane.setHgrow(this.title, Priority.ALWAYS);
         this.title.setText(titleStr);
         this.title.opacityProperty().bind(this.transition);
      }

      this.label = new Label();
      this.label.setFocusTraversable(false);
      this.label.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
      GridPane.setVgrow(this.label, Priority.ALWAYS);
      GridPane.setHgrow(this.label, Priority.ALWAYS);
      this.label.setText(this.getText());
      this.label.setGraphic(this.getGraphic());
      this.label.opacityProperty().bind(this.transition);
      this.getActions().addListener(o -> this.updatePane());
      this.closeBtn = new Button();
      this.closeBtn.setFocusTraversable(false);
      this.closeBtn.setOnAction(arg0 -> this.hide());
      this.closeBtn.getStyleClass().setAll(new String[]{"close-button"});
      StackPane graphic = new StackPane();
      graphic.getStyleClass().setAll(new String[]{"graphic"});
      this.closeBtn.setGraphic(graphic);
      this.closeBtn.setMinSize(17.0, 17.0);
      this.closeBtn.setPrefSize(17.0, 17.0);
      this.closeBtn.opacityProperty().bind(this.transition);
      GridPane.setMargin(this.closeBtn, new Insets(0.0, 0.0, 0.0, 8.0));
      GridPane.setValignment(this.closeBtn, VPos.TOP);
      this.updatePane();
      this.focusedProperty().addListener((o, ov, hasFocus) -> {
         if (hasFocus) {
            this.actionsBar.requestFocus();
         }
      });
      EventHandler<KeyEvent> keyPressEventHandler = event -> {
         if (KeyCode.TAB.equals(event.getCode())) {
            this.handleTraversal(event);
         }

         event.consume();
      };
      this.addEventHandler(KeyEvent.KEY_PRESSED, keyPressEventHandler);
   }

   void updatePane() {
      this.actionsBar = ActionUtils.createButtonBar(this.getActions());
      this.actionsBar.opacityProperty().bind(this.transition);
      this.actionsBar.focusedProperty().addListener((o, ov, hasFocus) -> {
         if (!this.actionsBar.getButtons().isEmpty()) {
            ((Node)this.actionsBar.getButtons().get(0)).requestFocus();
         }
      });
      GridPane.setHgrow(this.actionsBar, Priority.SOMETIMES);
      this.pane.getChildren().clear();
      int row = 0;
      if (this.title != null) {
         this.pane.add(this.title, 0, row++);
      }

      this.pane.add(this.label, 0, row);
      this.pane.add(this.actionsBar, 1, row);
      if (this.isCloseButtonVisible()) {
         this.pane.add(this.closeBtn, 2, 0, 1, row + 1);
      }
   }

   public void handleTraversal(KeyEvent event) {
      if (this.actionsBar != null && this.actionsBar.getButtons().size() > 0) {
         if (event.isShiftDown()) {
            if (((Node)this.actionsBar.getButtons().get(0)).isFocused()) {
               ImplUtils.focusPreviousSibling(this.getParent());
            }
         } else if (((Node)this.actionsBar.getButtons().get(this.actionsBar.getButtons().size() - 1)).isFocused()) {
            ImplUtils.focusNextSibling(this.getParent());
         }
      }
   }

   protected void layoutChildren() {
      double w = this.getWidth();
      double h = this.computePrefHeight(-1.0);
      double notificationBarHeight = this.prefHeight(w);
      double notificationMinHeight = this.minHeight(w);
      if (this.isShowFromTop()) {
         this.pane.resize(w, h);
         this.relocateInParent(0.0, (this.transition.get() - 1.0) * notificationMinHeight);
      } else {
         this.pane.resize(w, notificationBarHeight);
         this.relocateInParent(0.0, this.getContainerHeight() - notificationBarHeight);
      }
   }

   protected double computeMinHeight(double width) {
      return Math.max(super.computePrefHeight(width), 40.0);
   }

   protected double computePrefHeight(double width) {
      return Math.max(this.pane.prefHeight(width), this.minHeight(width)) * this.transition.get();
   }

   public void doShow() {
      this.transitionStartValue = 0.0;
      this.doAnimationTransition();
   }

   public void doHide() {
      this.transitionStartValue = 1.0;
      this.doAnimationTransition();
   }

   private void doAnimationTransition() {
      Duration duration;
      if (this.timeline != null && this.timeline.getStatus() != Status.STOPPED) {
         duration = this.timeline.getCurrentTime();
         duration = duration == Duration.ZERO ? this.TRANSITION_DURATION : duration;
         this.transitionStartValue = this.transition.get();
         this.timeline.stop();
      } else {
         duration = this.TRANSITION_DURATION;
      }

      this.timeline = new Timeline();
      this.timeline.setCycleCount(1);
      KeyFrame k1;
      KeyFrame k2;
      if (this.isShowing()) {
         k1 = new KeyFrame(Duration.ZERO, event -> {
            this.setCache(true);
            this.setVisible(true);
            this.pane.fireEvent(new Event(NotificationPane.ON_SHOWING));
         }, new KeyValue[]{new KeyValue(this.transition, this.transitionStartValue)});
         k2 = new KeyFrame(duration, event -> {
            this.pane.setCache(false);
            this.pane.fireEvent(new Event(NotificationPane.ON_SHOWN));
         }, new KeyValue[]{new KeyValue(this.transition, 1, Interpolator.EASE_OUT)});
      } else {
         k1 = new KeyFrame(Duration.ZERO, event -> {
            this.pane.setCache(true);
            this.pane.fireEvent(new Event(NotificationPane.ON_HIDING));
         }, new KeyValue[]{new KeyValue(this.transition, this.transitionStartValue)});
         k2 = new KeyFrame(duration, event -> {
            this.setCache(false);
            this.setVisible(false);
            this.pane.fireEvent(new Event(NotificationPane.ON_HIDDEN));
         }, new KeyValue[]{new KeyValue(this.transition, 0, Interpolator.EASE_IN)});
      }

      this.timeline.getKeyFrames().setAll(new KeyFrame[]{k1, k2});
      this.timeline.play();
   }
}
