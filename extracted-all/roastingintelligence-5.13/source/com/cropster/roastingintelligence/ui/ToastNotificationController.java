package com.cropster.roastingintelligence.ui;

import com.cropster.roastingintelligence.model.notification.Notification;
import com.cropster.roastingintelligence.model.notification.NotificationType;
import com.cropster.roastingintelligence.ui.util.ActionBuilder;
import com.cropster.roastingintelligence.ui.util.Icon;
import com.cropster.roastingintelligence.ui.util.KeyboardShortcut;
import com.cropster.roastingintelligence.ui.util.WindowUtil;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ParallelTransition;
import javafx.animation.Timeline;
import javafx.animation.Transition;
import javafx.animation.Animation.Status;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.Event;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextFlow;
import javafx.stage.Popup;
import javafx.stage.PopupWindow;
import javafx.stage.Screen;
import javafx.stage.Window;
import javafx.util.Duration;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.controlsfx.control.NotificationPane;
import org.controlsfx.control.action.Action;
import org.controlsfx.control.action.ActionUtils.ActionTextBehavior;
import org.controlsfx.tools.Utils;

public class ToastNotificationController extends AbstractNotificationController {
   public static final Duration TOAST_NOTIFICATION_DURATION = Duration.seconds(8.0);
   private final ToastNotificationController.NotificationPopupHandler handler = new ToastNotificationController.NotificationPopupHandler();

   @Override
   protected Consumer<Notification> createConsumer(String topic) {
      return this::show;
   }

   private void show(Notification notification) {
      Platform.runLater(() -> this.handler.show(notification));
   }

   public ToastNotificationController.NotificationPopupHandler getHandler() {
      return this.handler;
   }

   public void hide(String tag) {
      this.handler.popupsFor(tag).forEach(this::hide);
   }

   public void hide(Popup toast) {
      if (toast != null) {
         if (!toast.getContent().isEmpty()) {
            ToastNotificationController.NotificationPopupHandler.NotificationRegion region = ToastNotificationController.NotificationPopupHandler.regionFromPopup(
               toast
            );
            if (region != null) {
               this.handler.hide(toast, region.notificationPosition);
            }
         }
      }
   }

   public boolean isOpen(String tag) {
      return !this.handler.popupsFor(tag).isEmpty();
   }

   public static final class NotificationPopupHandler {
      private static final String NOTIFICATIONS_STYLE_SHEET = ToastNotificationController.class
         .getResource("components/" + (SystemUtils.IS_OS_MAC_OSX ? "toast-notification-mac.css" : "toast-notification.css"))
         .toExternalForm();
      private static final String FINAL_ANCHOR_Y = "finalAnchorY";
      private static final double PADDING = 8.0;
      private static final double SPACING = 8.0;
      private static final double TOP_BORDER = 12.0;
      private static final double BOTTOM_BORDER = 24.0;
      private static final double LEFT_BORDER = 18.0;
      private static final double RIGHT_BORDER = 18.0;
      private static final double BORDER_THICKNESS = 2.4;
      private static final Duration TRANSITION_DURATION = Duration.millis(350.0);
      private final ParallelTransition parallelTransition = new ParallelTransition();
      private final Map<Pos, List<Popup>> popupsMap = new EnumMap<>(Pos.class);
      private boolean isShowing = false;
      private double screenHeight;
      private double screenWidth;
      private double startX;
      private double startY;
      private boolean firstShow = true;
      private Object owner = null;

      private static ToastNotificationController.NotificationPopupHandler.NotificationRegion regionFromPopup(Popup popup) {
         return popup.getContent().get(0) instanceof ToastNotificationController.NotificationPopupHandler.NotificationRegion region ? region : null;
      }

      public Object getOwner() {
         return this.owner;
      }

      public void setOwner(Object owner) {
         if (!(owner instanceof Screen) && !(owner instanceof Window) && !(owner instanceof Node)) {
            throw new IllegalArgumentException("Owner must be an instance of Screen, Window, or Node");
         } else {
            this.owner = owner;
         }
      }

      synchronized void show(Notification notification) {
         Runnable prerequisite = notification.getPrerequisite();
         if (prerequisite != null && !notification.isPrerequisiteOnSubmission()) {
            prerequisite.run();
         }

         BooleanSupplier shouldBeShown = notification.getProperty(BooleanSupplier.class, "show-boolean-supplier", () -> true);
         if (shouldBeShown.getAsBoolean()) {
            Window ownerWindow = notification.getProperty(Window.class, "owner-window");
            Screen onwerScreen = notification.getProperty(Screen.class, "owner-screen");
            if (this.owner != null) {
               if (this.owner instanceof Screen scrn) {
                  onwerScreen = scrn;
               } else {
                  ownerWindow = Utils.getWindow(this.owner);
               }
            }

            Window window;
            if (ownerWindow == null) {
               window = Utils.getWindow(null);
               Screen screen = onwerScreen != null ? onwerScreen : WindowUtil.screenFor(window);
               Rectangle2D screenBounds = screen.getVisualBounds();
               this.startX = screenBounds.getMinX();
               this.startY = screenBounds.getMinY();
               this.screenWidth = screenBounds.getWidth();
               this.screenHeight = screenBounds.getHeight();
            } else {
               window = ownerWindow;
               this.startX = ownerWindow.getX();
               this.startY = ownerWindow.getY();
               this.screenWidth = ownerWindow.getWidth();
               this.screenHeight = ownerWindow.getHeight();
            }

            this.show(window, new ToastNotificationController.NotificationPopupHandler.NotificationWrapper(notification));
         }
      }

      private void addPopupToMap(Pos p, Popup popup) {
         List<Popup> popups;
         if (!this.popupsMap.containsKey(p)) {
            popups = new LinkedList<>();
            this.popupsMap.put(p, popups);
         } else {
            popups = this.popupsMap.get(p);
         }

         this.doAnimation(p, popup);
         popups.add(popup);
      }

      private Timeline createHideTimeline(Popup popup, ToastNotificationController.NotificationPopupHandler.NotificationRegion region, Pos p) {
         KeyValue fadeOutBegin = new KeyValue(region.opacityProperty(), 1.0);
         KeyValue fadeOutEnd = new KeyValue(region.opacityProperty(), 0.0);
         KeyFrame kfBegin = new KeyFrame(Duration.ZERO, new KeyValue[]{fadeOutBegin});
         KeyFrame kfEnd = new KeyFrame(Duration.millis(500.0), new KeyValue[]{fadeOutEnd});
         Timeline timeline = new Timeline(new KeyFrame[]{kfBegin, kfEnd});
         timeline.setOnFinished(e -> this.hide(popup, p));
         return timeline;
      }

      private Timeline createProgressTimeline(ToastNotificationController.NotificationPopupHandler.NotificationRegion region, Duration duration) {
         KeyValue progressBegin = new KeyValue(region.progressProperty(), 1.0);
         KeyValue progressEnd = new KeyValue(region.progressProperty(), 0.0);
         KeyFrame kfBegin = new KeyFrame(Duration.ZERO, new KeyValue[]{progressBegin});
         KeyFrame kfEnd = new KeyFrame(duration, new KeyValue[]{progressEnd});
         return new Timeline(new KeyFrame[]{kfBegin, kfEnd});
      }

      private void doAnimation(Pos p, Popup changedPopup) {
         List<Popup> popups = this.popupsMap.get(p);
         if (popups != null && changedPopup != null) {
            this.parallelTransition.stop();
            this.parallelTransition.getChildren().clear();
            boolean isShowFromTop = this.isShowFromTop(p);
            double sum = isShowFromTop
               ? this.getFinalAnchorY(changedPopup) + changedPopup.getHeight() + 8.0 - 24.0 - 7.199999999999999
               : this.getFinalAnchorY(changedPopup);
            double[] targetAnchors = new double[popups.size()];

            for (int i = popups.size() - 1; i >= 0; i--) {
               Popup popup = popups.get(i);
               double popupHeight = popup.getHeight();
               if (isShowFromTop) {
                  popup.setAnchorY(sum);
                  targetAnchors[i] = sum;
                  sum += popupHeight + 8.0 - 12.0 - 24.0;
               } else {
                  sum -= popupHeight + 8.0 - 12.0 - 24.0;
                  targetAnchors[i] = sum;
                  popup.setAnchorY(sum + popupHeight);
               }
            }

            for (int ix = popups.size() - 1; ix >= 0; ix--) {
               Popup popup = popups.get(ix);
               popup.setAnchorX(changedPopup.getAnchorX());
               double anchorYTarget = targetAnchors[ix];
               if (anchorYTarget < 0.0) {
                  popup.hide();
               }

               double oldAnchorY = this.getFinalAnchorY(popup);
               double distance = anchorYTarget - oldAnchorY;
               this.setFinalAnchorY(popup, oldAnchorY + distance);
               Transition t = new ToastNotificationController.NotificationPopupHandler.CustomTransition(popup, oldAnchorY, distance);
               t.setCycleCount(1);
               this.parallelTransition.getChildren().add(t);
            }

            this.parallelTransition.play();
         }
      }

      private void doCloseAnimation(Pos p) {
         List<Popup> popups = this.popupsMap.get(p);
         if (popups != null) {
            this.parallelTransition.stop();
            this.parallelTransition.getChildren().clear();
            boolean isShowFromTop = this.isShowFromTop(p);
            double anchorY = this.computeAnchorY(p, 0.0);
            double sum = isShowFromTop ? anchorY + 2.4 : anchorY - 24.0 - 2.4;
            double[] targetAnchors = new double[popups.size()];

            for (int i = popups.size() - 1; i >= 0; i--) {
               Popup popup = popups.get(i);
               double popupHeight = popup.getHeight();
               if (isShowFromTop) {
                  popup.setAnchorY(sum);
                  targetAnchors[i] = sum;
                  sum += popupHeight + 8.0 - 12.0 - 24.0;
               } else {
                  sum -= popupHeight + 8.0 - 12.0 - 24.0;
                  targetAnchors[i] = sum;
                  popup.setAnchorY(sum + popupHeight);
               }
            }

            for (int ix = popups.size() - 1; ix >= 0; ix--) {
               Popup popup = popups.get(ix);
               double anchorYTarget = targetAnchors[ix];
               if (anchorYTarget < 0.0) {
                  popup.hide();
               }

               double oldAnchorY = this.getFinalAnchorY(popup);
               double distance = anchorYTarget - oldAnchorY;
               this.setFinalAnchorY(popup, oldAnchorY + distance);
               Transition t = new ToastNotificationController.NotificationPopupHandler.CustomTransition(popup, oldAnchorY, distance);
               t.setCycleCount(1);
               this.parallelTransition.getChildren().add(t);
            }

            this.parallelTransition.play();
         }
      }

      private double getFinalAnchorY(Popup popup) {
         return (Double)popup.getProperties().get("finalAnchorY");
      }

      public void hide(Popup popup, Pos p) {
         ToastNotificationController.NotificationPopupHandler.NotificationRegion notificationRegion = regionFromPopup(popup);
         if (notificationRegion != null) {
            Action ca = notificationRegion.getCloseAction();
            if (ca != null) {
               ca.handle(null);
            }
         }

         popup.hide();
         this.removePopupFromMap(p, popup);
         this.doCloseAnimation(p);
      }

      private boolean isShowFromTop(Pos p) {
         switch (p) {
            case TOP_LEFT:
            case TOP_RIGHT:
               return true;
            default:
               return false;
         }
      }

      public List<Popup> popupsFor(String tag) {
         return tag == null
            ? List.of()
            : this.popupsMap.values().stream().flatMap(Collection::stream).filter(p -> tag.equals(regionFromPopup(p).getTag())).toList();
      }

      private void removePopupFromMap(Pos p, Popup popup) {
         if (this.popupsMap.containsKey(p)) {
            List<Popup> popups = this.popupsMap.get(p);
            popups.remove(popup);
         }
      }

      private void setFinalAnchorY(Popup popup, double anchorY) {
         popup.getProperties().put("finalAnchorY", anchorY);
      }

      private Popup show(Window owner, ToastNotificationController.NotificationPopupHandler.NotificationWrapper notification) {
         Duration hideAfter = notification.getHideAfter();
         if (hideAfter == Duration.ZERO) {
            return null;
         } else {
            Window ownerWindow = owner;

            while (ownerWindow instanceof PopupWindow) {
               PopupWindow popupwin = (PopupWindow)ownerWindow;
               ownerWindow = popupwin.getOwnerWindow();
            }

            Scene ownerScene = ownerWindow == null ? null : ownerWindow.getScene();
            if (ownerScene != null) {
               ownerScene.getStylesheets().addAll(new String[]{JavaFxController.STYLE_SHEET, NOTIFICATIONS_STYLE_SHEET});
            }

            Popup popup = new Popup();
            popup.setAutoFix(false);
            popup.setConsumeAutoHidingEvents(false);
            popup.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
               if (KeyboardShortcut.ESC.match(e) && notification.getCloseAction() != null) {
                  popup.hide();
                  if (popup.getOnAutoHide() != null) {
                     popup.getOnAutoHide().handle(new Event(popup, popup, Event.ANY));
                  }

                  notification.getCloseAction().handle(null);
                  e.consume();
               }
            });
            Pos p = notification.getPosition();
            ToastNotificationController.NotificationPopupHandler.NotificationRegion notificationRegion = new ToastNotificationController.NotificationPopupHandler.NotificationRegion(
               notification, popup, p
            );
            notificationRegion.getStyleClass().addAll(notification.getStyleClass());
            popup.getContent().add(notificationRegion);
            popup.getScene().getRoot().setStyle("-fx-background-color: transparent;");
            popup.show(owner);
            double height = popup.getHeight();
            double width = popup.getWidth();
            double anchorX = this.computeAnchorX(p, width);
            double anchorY = this.computeAnchorY(p, height);
            this.setFinalAnchorY(popup, anchorY);
            popup.show(owner, anchorX, anchorY);
            this.isShowing = true;
            notificationRegion.doShow();
            this.addPopupToMap(p, popup);
            if (hideAfter != null) {
               Timeline hideTimeline = this.createHideTimeline(popup, notificationRegion, p);
               Timeline progressTimeline = this.createProgressTimeline(notificationRegion, hideAfter);
               progressTimeline.setOnFinished(e -> hideTimeline.play());
               progressTimeline.play();
               notificationRegion.getPane().setOnMouseEntered(e -> progressTimeline.pause());
               notificationRegion.getPane().setOnMouseExited(e -> progressTimeline.play());
            }

            this.firstShow = false;
            return popup;
         }
      }

      private double computeAnchorY(Pos p, double height) {
         return switch (p) {
            case TOP_LEFT, TOP_RIGHT -> -4.0 + this.startY;
            case BOTTOM_LEFT, BOTTOM_RIGHT -> {
               yield this.startY + this.screenHeight - height - 8.0 - 24.0 + 12.0;
               if (!this.firstShow) {
                  yield 36.0;
               }
            }
            default -> throw new IllegalStateException("Unexpected value: " + p);
         };
      }

      private double computeAnchorX(Pos p, double width) {
         return switch (p) {
            case TOP_LEFT, BOTTOM_LEFT -> -10.0 + this.startX;
            case TOP_RIGHT, BOTTOM_RIGHT -> {
               yield this.startX + this.screenWidth - width - 8.0 - 18.0;
               if (!this.firstShow) {
                  yield 36.0;
               }
            }
            default -> throw new IllegalStateException("Unexpected value: " + p);
         };
      }

      static class CustomTransition extends Transition {
         private final double distance;
         private final double oldAnchorY;
         private final WeakReference<Popup> popupWeakReference;

         CustomTransition(Popup popup, double oldAnchorY, double distance) {
            this.popupWeakReference = new WeakReference<>(popup);
            this.oldAnchorY = oldAnchorY;
            this.distance = distance;
            this.setCycleDuration(Duration.millis(350.0));
         }

         protected void interpolate(double fraction) {
            Popup popup = this.popupWeakReference.get();
            if (popup != null) {
               popup.setAnchorY(this.oldAnchorY + this.distance * fraction);
            }
         }
      }

      public class NotificationRegion extends Region {
         private static final double MIN_HEIGHT = 88.0;
         private static final double MIN_WIDTH = 375.0;
         private final VBox centralPane = new VBox();
         private final Button closeButton = new Button();
         private final Label iconLabel = new Label();
         private final TextFlow messageFlow = new TextFlow();
         private final Pos notificationPosition;
         private final ToastNotificationController.NotificationPopupHandler.NotificationWrapper notificationToShow;
         private final HBox pane = new HBox();
         private final Popup popup;
         private final Label titleLabel = new Label();
         private final DoubleProperty transition = new SimpleDoubleProperty() {
            protected void invalidated() {
               NotificationRegion.this.layoutChildren();
            }
         };
         private Timeline timeline;
         private Button actionButoon;
         private ProgressBar progressBar;
         private double transitionStartValue;

         NotificationRegion(ToastNotificationController.NotificationPopupHandler.NotificationWrapper notificationToShow, Popup popup, Pos notificationPosition) {
            this.notificationToShow = notificationToShow;
            this.popup = popup;
            this.notificationPosition = notificationPosition;
            this.getStyleClass().add("notification-bar");
            this.setVisible(this.isShowing());
            HBox skin = new HBox();
            skin.getStyleClass().add("skin");
            skin.setMinHeight(88.0);
            skin.setMinWidth(375.0);
            skin.setMaxWidth(375.0);
            this.getChildren().setAll(new Node[]{skin});
            this.pane.getStyleClass().add("pane");
            this.pane.setMaxWidth(Double.POSITIVE_INFINITY);
            this.pane.setMaxHeight(Double.POSITIVE_INFINITY);
            HBox.setHgrow(this.pane, Priority.ALWAYS);
            skin.getChildren().setAll(new Node[]{this.pane});
            this.iconLabel.getStyleClass().add("icon");
            this.iconLabel.managedProperty().bind(this.iconLabel.visibleProperty());
            this.iconLabel.opacityProperty().bind(this.transition);
            this.iconLabel.visibleProperty().bind(Bindings.isNotNull(this.iconLabel.graphicProperty()));
            this.iconLabel.setGraphic(notificationToShow.graphic);
            HBox.setHgrow(this.iconLabel, Priority.NEVER);
            this.pane.getChildren().add(this.iconLabel);
            this.centralPane.getStyleClass().add("central-pane");
            this.centralPane.setMinWidth(270.0);
            this.centralPane.setMaxWidth(Double.POSITIVE_INFINITY);
            HBox.setHgrow(this.centralPane, Priority.ALWAYS);
            this.pane.getChildren().add(this.centralPane);
            this.titleLabel.setId("notification-title");
            this.titleLabel.getStyleClass().add("title");
            this.titleLabel.managedProperty().bind(this.titleLabel.visibleProperty());
            this.titleLabel.opacityProperty().bind(this.transition);
            this.titleLabel
               .visibleProperty()
               .bind(Bindings.createBooleanBinding(() -> StringUtils.isNotBlank(this.titleLabel.getText()), new Observable[]{this.titleLabel.textProperty()}));
            this.titleLabel.setText(notificationToShow.getTitle());
            this.centralPane.getChildren().add(this.titleLabel);
            this.messageFlow.setId("notification-message");
            this.messageFlow.getStyleClass().add("message");
            this.messageFlow.managedProperty().bind(this.messageFlow.visibleProperty());
            this.messageFlow.opacityProperty().bind(this.transition);
            this.messageFlow
               .visibleProperty()
               .bind(Bindings.createBooleanBinding(() -> !this.messageFlow.getChildren().isEmpty(), new Observable[]{this.messageFlow.getChildren()}));
            this.messageFlow.getChildren().addAll(JavaFxUtil.parseHtmlToTextNodes(notificationToShow.getMessage()));
            this.centralPane.getChildren().add(this.messageFlow);
            if (notificationToShow.getAction() != null) {
               this.actionButoon = ActionBuilder.buttonFrom(notificationToShow.getAction(), ActionTextBehavior.SHOW);
               this.actionButoon.setId("action-button");
               this.actionButoon.getStyleClass().add("button--plain");
               this.actionButoon.managedProperty().bind(this.actionButoon.visibleProperty());
               this.actionButoon.opacityProperty().bind(this.transition);
               this.actionButoon.setOnAction(event -> {
                  notificationToShow.getAction().handle(event);
                  Platform.runLater(() -> this.closeButton.getOnAction().handle(event));
               });
               this.centralPane.getChildren().add(this.actionButoon);
            }

            if (notificationToShow.getHideAfter() != null && notificationToShow.getHideAfter().toMillis() > 0.0) {
               this.progressBar = new ProgressBar();
               this.progressBar.setId("notification-progress");
               this.progressBar.getStyleClass().add("progress");
               this.progressBar.managedProperty().bind(this.progressBar.visibleProperty());
               this.progressBar.setMaxWidth(Double.POSITIVE_INFINITY);
               this.centralPane.getChildren().add(this.progressBar);
            }

            this.closeButton.setId("close-button");
            this.closeButton.getStyleClass().add("button--small-transparent");
            this.closeButton.setGraphic(Icon.CLOSE.load("icon--link"));
            this.closeButton.setOnAction(event -> NotificationPopupHandler.this.hide(popup, notificationPosition));
            HBox.setHgrow(this.iconLabel, Priority.NEVER);
            HBox.setMargin(this.closeButton, new Insets(0.0, 0.0, 0.0, -4.0));
            this.pane.getChildren().add(this.closeButton);
         }

         public void doShow() {
            this.transitionStartValue = 0.0;
            this.doAnimationTransition();
         }

         public Action getCloseAction() {
            return this.notificationToShow.getCloseAction();
         }

         public double getContainerHeight() {
            return NotificationPopupHandler.this.startY + NotificationPopupHandler.this.screenHeight;
         }

         public Node getGraphic() {
            return this.notificationToShow.graphic;
         }

         public Pos getNotificationPosition() {
            return this.notificationPosition;
         }

         public DoubleProperty progressProperty() {
            return this.progressBar.progressProperty();
         }

         public double getProgress() {
            return this.progressBar.getProgress();
         }

         public void setProgress(double progress) {
            this.progressBar.setProgress(progress);
         }

         public String getText() {
            return this.notificationToShow.getMessage();
         }

         public String getTitle() {
            return this.notificationToShow.getTitle();
         }

         public String getTag() {
            return this.notificationToShow.getTag();
         }

         public void hide() {
            NotificationPopupHandler.this.isShowing = false;
            NotificationPopupHandler.this.createHideTimeline(this.popup, this, this.notificationPosition).play();
         }

         public boolean isShowFromTop() {
            return NotificationPopupHandler.this.isShowFromTop(this.notificationToShow.getPosition());
         }

         public boolean isShowing() {
            return NotificationPopupHandler.this.isShowing;
         }

         public void relocateInParent(double y) {
            switch (this.notificationPosition) {
               case BOTTOM_LEFT:
               case BOTTOM_RIGHT:
                  this.popup.setAnchorY(y - 8.0);
            }
         }

         protected double computeMinHeight(double width) {
            return 88.0;
         }

         protected double computeMinWidth(double height) {
            return 375.0;
         }

         protected double computePrefWidth(double height) {
            return 375.0;
         }

         public ToastNotificationController.NotificationPopupHandler.NotificationWrapper getNotificationToShow() {
            return this.notificationToShow;
         }

         private HBox getPane() {
            return this.pane;
         }

         private void doAnimationTransition() {
            Duration duration;
            if (this.timeline != null && this.timeline.getStatus() != Status.STOPPED) {
               duration = this.timeline.getCurrentTime();
               duration = duration == Duration.ZERO ? ToastNotificationController.NotificationPopupHandler.TRANSITION_DURATION : duration;
               this.transitionStartValue = this.transition.get();
               this.timeline.stop();
            } else {
               duration = ToastNotificationController.NotificationPopupHandler.TRANSITION_DURATION;
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

      public static class NotificationWrapper {
         private final Node graphic;
         private final List<String> styleClass = new ArrayList<>();
         private final Notification notification;

         NotificationWrapper(Notification notification) {
            this.notification = notification;

            this.graphic = switch (notification.getType()) {
               case ERROR -> Icon.ERROR.load("icon--error", 1.5);
               case INFO -> Icon.INFO.load("icon--info", 1.5);
               case SUCCESS -> Icon.SUCCESS.load("icon--success", 1.5);
               case WARNING -> Icon.WARNING.load("icon--warning", 1.5);
               default -> null;
            };
            if (notification.getType() != NotificationType.NONE) {
               this.styleClass.add(notification.getType().toString().toLowerCase());
            }
         }

         public Action getAction() {
            return this.notification.getProperty(Action.class, "action");
         }

         public Action getCloseAction() {
            return this.notification.getProperty(Action.class, "action-close");
         }

         public Date getDate() {
            return this.notification.getDate();
         }

         public Node getGraphic() {
            return this.graphic;
         }

         public Duration getHideAfter() {
            return this.notification.getProperty(Duration.class, "hide-after");
         }

         public Pos getPosition() {
            return this.notification.getProperty(Pos.class, "position", Pos.TOP_RIGHT);
         }

         public String getMessage() {
            return this.notification.getMessage();
         }

         public List<String> getStyleClass() {
            return this.styleClass;
         }

         public String getTag() {
            return this.notification.getProperty(String.class, "tag");
         }

         public String getTitle() {
            return this.notification.getTitle();
         }

         public NotificationType getType() {
            return this.notification.getType();
         }
      }
   }
}
