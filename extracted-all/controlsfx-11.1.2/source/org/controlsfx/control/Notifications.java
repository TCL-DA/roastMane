package org.controlsfx.control;

import impl.org.controlsfx.i18n.Localization;
import impl.org.controlsfx.skin.NotificationBar;
import java.lang.ref.WeakReference;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ParallelTransition;
import javafx.animation.Timeline;
import javafx.animation.Transition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.stage.Popup;
import javafx.stage.PopupWindow;
import javafx.stage.Screen;
import javafx.stage.Window;
import javafx.util.Duration;
import org.controlsfx.control.action.Action;
import org.controlsfx.tools.Utils;

public class Notifications {
   private static final String STYLE_CLASS_DARK = "dark";
   private String title;
   private String text;
   private Node graphic;
   private ObservableList<Action> actions = FXCollections.observableArrayList();
   private Pos position = Pos.BOTTOM_RIGHT;
   private Duration hideAfterDuration = Duration.seconds(5.0);
   private boolean hideCloseButton;
   private EventHandler<ActionEvent> onAction;
   private Window owner;
   private Screen screen = null;
   private List<String> styleClass = new ArrayList<>();
   private int threshold;
   private Notifications thresholdNotification;

   private Notifications() {
   }

   public static Notifications create() {
      return new Notifications();
   }

   public Notifications text(String text) {
      this.text = text;
      return this;
   }

   public Notifications title(String title) {
      this.title = title;
      return this;
   }

   public Notifications graphic(Node graphic) {
      this.graphic = graphic;
      return this;
   }

   public Notifications position(Pos position) {
      this.position = position;
      return this;
   }

   public Notifications owner(Object owner) {
      if (owner instanceof Screen) {
         this.screen = (Screen)owner;
      } else {
         this.owner = Utils.getWindow(owner);
      }

      return this;
   }

   public Notifications hideAfter(Duration duration) {
      this.hideAfterDuration = duration;
      return this;
   }

   public Notifications onAction(EventHandler<ActionEvent> onAction) {
      this.onAction = onAction;
      return this;
   }

   public Notifications darkStyle() {
      this.styleClass.add("dark");
      return this;
   }

   public Notifications styleClass(String... styleClasses) {
      this.styleClass.addAll(Arrays.asList(styleClasses));
      return this;
   }

   public Notifications hideCloseButton() {
      this.hideCloseButton = true;
      return this;
   }

   public Notifications action(Action... actions) {
      this.actions = actions == null ? FXCollections.observableArrayList() : FXCollections.observableArrayList(actions);
      return this;
   }

   public Notifications threshold(int threshold, Notifications thresholdNotification) {
      this.threshold = threshold;
      this.thresholdNotification = thresholdNotification;
      return this;
   }

   public void showWarning() {
      this.graphic(new ImageView(Notifications.class.getResource("/org/controlsfx/dialog/dialog-warning.png").toExternalForm()));
      this.show();
   }

   public void showInformation() {
      this.graphic(new ImageView(Notifications.class.getResource("/org/controlsfx/dialog/dialog-information.png").toExternalForm()));
      this.show();
   }

   public void showError() {
      this.graphic(new ImageView(Notifications.class.getResource("/org/controlsfx/dialog/dialog-error.png").toExternalForm()));
      this.show();
   }

   public void showConfirm() {
      this.graphic(new ImageView(Notifications.class.getResource("/org/controlsfx/dialog/dialog-confirm.png").toExternalForm()));
      this.show();
   }

   public void show() {
      Notifications.NotificationPopupHandler.getInstance().show(this);
   }

   public List<String> getStyleClass() {
      return this.styleClass;
   }

   private static final class NotificationPopupHandler {
      private static final Notifications.NotificationPopupHandler INSTANCE = new Notifications.NotificationPopupHandler();
      private static final String FINAL_ANCHOR_Y = "finalAnchorY";
      private double startX;
      private double startY;
      private double screenWidth;
      private double screenHeight;
      private final Map<Pos, List<Popup>> popupsMap = new HashMap<>();
      private static final double PADDING = 15.0;
      private static final double SPACING = 15.0;
      private ParallelTransition parallelTransition = new ParallelTransition();
      private boolean isShowing = false;

      static final Notifications.NotificationPopupHandler getInstance() {
         return INSTANCE;
      }

      public void show(Notifications notification) {
         Window window;
         if (notification.owner == null) {
            window = Utils.getWindow(null);
            Screen screen = notification.screen != null ? notification.screen : this.getScreenBounds(window).orElse(Screen.getPrimary());
            Rectangle2D screenBounds = screen.getBounds();
            this.startX = screenBounds.getMinX();
            this.startY = screenBounds.getMinY();
            this.screenWidth = screenBounds.getWidth();
            this.screenHeight = screenBounds.getHeight();
         } else {
            this.startX = notification.owner.getX();
            this.startY = notification.owner.getY();
            this.screenWidth = notification.owner.getWidth();
            this.screenHeight = notification.owner.getHeight();
            window = notification.owner;
         }

         this.show(window, notification);
      }

      private Optional<Screen> getScreenBounds(Window window) {
         if (window == null) {
            return Optional.empty();
         } else {
            ObservableList<Screen> screensForRectangle = Screen.getScreensForRectangle(window.getX(), window.getY(), window.getWidth(), window.getHeight());
            return screensForRectangle.stream().filter(Objects::nonNull).findFirst();
         }
      }

      private void show(Window owner, Notifications notification) {
         Window ownerWindow = owner;

         while (ownerWindow instanceof PopupWindow) {
            ownerWindow = ((PopupWindow)ownerWindow).getOwnerWindow();
         }

         Scene ownerScene = ownerWindow == null ? null : ownerWindow.getScene();
         if (ownerScene != null) {
            String stylesheetUrl = Notifications.class.getResource("notificationpopup.css").toExternalForm();
            if (!ownerScene.getStylesheets().contains(stylesheetUrl)) {
               ownerScene.getStylesheets().add(0, stylesheetUrl);
            }
         }

         final Popup popup = new Popup();
         popup.setAutoFix(false);
         final Pos p = notification.position;
         List<Popup> popups = this.popupsMap.get(p);
         final Notifications notificationToShow;
         if (notification.threshold > 0 && popups != null && popups.size() >= notification.threshold) {
            for (Popup popupElement : popups) {
               popupElement.hide();
            }

            Notifications thresholdNotification = notification.thresholdNotification;
            if (thresholdNotification.text == null || thresholdNotification.text.isEmpty()) {
               thresholdNotification.text = MessageFormat.format(Localization.getString("notifications.threshold.text"), popups.size());
            }

            notificationToShow = thresholdNotification;
         } else {
            notificationToShow = notification;
         }

         NotificationBar notificationBar = new NotificationBar() {
            @Override
            public String getTitle() {
               return notificationToShow.title;
            }

            @Override
            public String getText() {
               return notificationToShow.text;
            }

            @Override
            public Node getGraphic() {
               return notificationToShow.graphic;
            }

            @Override
            public ObservableList<Action> getActions() {
               return notificationToShow.actions;
            }

            @Override
            public boolean isShowing() {
               return NotificationPopupHandler.this.isShowing;
            }

            @Override
            public boolean isShowFromTop() {
               return NotificationPopupHandler.this.isShowFromTop(notificationToShow.position);
            }

            @Override
            public void hide() {
               NotificationPopupHandler.this.isShowing = false;
               NotificationPopupHandler.this.createHideTimeline(popup, this, p, Duration.ZERO).play();
            }

            @Override
            public boolean isCloseButtonVisible() {
               return !notificationToShow.hideCloseButton;
            }

            @Override
            public double getContainerHeight() {
               return NotificationPopupHandler.this.startY + NotificationPopupHandler.this.screenHeight;
            }

            @Override
            public void relocateInParent(double x, double y) {
               switch (p) {
                  case BOTTOM_LEFT:
                  case BOTTOM_CENTER:
                  case BOTTOM_RIGHT:
                     popup.setAnchorY(y - 15.0);
               }
            }
         };
         notificationBar.setMinWidth(400.0);
         notificationBar.getStyleClass().addAll(notificationToShow.styleClass);
         notificationBar.setOnMouseClicked(e -> {
            if (notificationToShow.onAction != null) {
               ActionEvent actionEvent = new ActionEvent(notificationBar, notificationBar);
               notificationToShow.onAction.handle(actionEvent);
               this.createHideTimeline(popup, notificationBar, p, Duration.ZERO).play();
            }
         });
         popup.getContent().add(notificationBar);
         popup.show(ownerWindow, 0.0, 0.0);
         double anchorX = 0.0;
         double anchorY = 0.0;
         double barWidth = notificationBar.getWidth();
         double barHeight = notificationBar.getHeight();
         switch (p) {
            case BOTTOM_LEFT:
            case TOP_LEFT:
            case CENTER_LEFT:
               anchorX = 15.0 + this.startX;
               break;
            case BOTTOM_CENTER:
            case TOP_CENTER:
            case CENTER:
               anchorX = this.startX + this.screenWidth / 2.0 - barWidth / 2.0 - 7.5;
               break;
            case BOTTOM_RIGHT:
            case TOP_RIGHT:
            case CENTER_RIGHT:
            default:
               anchorX = this.startX + this.screenWidth - barWidth - 15.0;
         }

         switch (p) {
            case BOTTOM_LEFT:
            case BOTTOM_CENTER:
            case BOTTOM_RIGHT:
            default:
               anchorY = this.startY + this.screenHeight - barHeight - 15.0;
               break;
            case TOP_LEFT:
            case TOP_CENTER:
            case TOP_RIGHT:
               anchorY = 15.0 + this.startY;
               break;
            case CENTER_LEFT:
            case CENTER:
            case CENTER_RIGHT:
               anchorY = this.startY + this.screenHeight / 2.0 - barHeight / 2.0 - 7.5;
         }

         popup.setAnchorX(anchorX);
         this.setFinalAnchorY(popup, anchorY);
         popup.setAnchorY(anchorY);
         this.isShowing = true;
         notificationBar.doShow();
         this.addPopupToMap(p, popup);
         Timeline timeline = this.createHideTimeline(popup, notificationBar, p, notification.hideAfterDuration);
         timeline.play();
      }

      private void hide(Popup popup, Pos p) {
         popup.hide();
         this.removePopupFromMap(p, popup);
      }

      private Timeline createHideTimeline(Popup popup, NotificationBar bar, Pos p, Duration startDelay) {
         KeyValue fadeOutBegin = new KeyValue(bar.opacityProperty(), 1.0);
         KeyValue fadeOutEnd = new KeyValue(bar.opacityProperty(), 0.0);
         KeyFrame kfBegin = new KeyFrame(Duration.ZERO, new KeyValue[]{fadeOutBegin});
         KeyFrame kfEnd = new KeyFrame(Duration.millis(500.0), new KeyValue[]{fadeOutEnd});
         Timeline timeline = new Timeline(new KeyFrame[]{kfBegin, kfEnd});
         timeline.setDelay(startDelay);
         timeline.setOnFinished(e -> this.hide(popup, p));
         return timeline;
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

      private void removePopupFromMap(Pos p, Popup popup) {
         if (this.popupsMap.containsKey(p)) {
            List<Popup> popups = this.popupsMap.get(p);
            popups.remove(popup);
         }
      }

      private void doAnimation(Pos p, Popup changedPopup) {
         List<Popup> popups = this.popupsMap.get(p);
         if (popups != null) {
            this.parallelTransition.stop();
            this.parallelTransition.getChildren().clear();
            boolean isShowFromTop = this.isShowFromTop(p);
            double sum = 0.0;
            double[] targetAnchors = new double[popups.size()];

            for (int i = popups.size() - 1; i >= 0; i--) {
               Popup _popup = popups.get(i);
               NotificationBar notificationBar = (NotificationBar)_popup.getContent().get(0);
               double popupHeight = notificationBar.minHeight(notificationBar.getWidth());
               if (isShowFromTop) {
                  if (i == popups.size() - 1) {
                     sum = this.getFinalAnchorY(changedPopup) + popupHeight + 15.0;
                  } else {
                     sum += popupHeight + 15.0;
                  }

                  targetAnchors[i] = sum;
                  _popup.setAnchorY(sum - popupHeight);
               } else {
                  if (i == popups.size() - 1) {
                     sum = this.getFinalAnchorY(changedPopup) - (popupHeight + 15.0);
                  } else {
                     sum -= popupHeight + 15.0;
                  }

                  targetAnchors[i] = sum;
                  _popup.setAnchorY(sum + popupHeight);
               }
            }

            for (int ix = popups.size() - 1; ix >= 0; ix--) {
               Popup _popup = popups.get(ix);
               _popup.setAnchorX(changedPopup.getAnchorX());
               double anchorYTarget = targetAnchors[ix];
               if (anchorYTarget < this.startY) {
                  _popup.hide();
               }

               double oldAnchorY = this.getFinalAnchorY(_popup);
               double distance = anchorYTarget - oldAnchorY;
               this.setFinalAnchorY(_popup, oldAnchorY + distance);
               Transition t = new Notifications.NotificationPopupHandler.CustomTransition(_popup, oldAnchorY, distance);
               t.setCycleCount(1);
               this.parallelTransition.getChildren().add(t);
            }

            this.parallelTransition.play();
         }
      }

      private double getFinalAnchorY(Popup popup) {
         return (Double)popup.getProperties().get("finalAnchorY");
      }

      private void setFinalAnchorY(Popup popup, double anchorY) {
         popup.getProperties().put("finalAnchorY", anchorY);
      }

      private boolean isShowFromTop(Pos p) {
         switch (p) {
            case TOP_LEFT:
            case TOP_CENTER:
            case TOP_RIGHT:
               return true;
            case CENTER_LEFT:
            case CENTER:
            default:
               return false;
         }
      }

      class CustomTransition extends Transition {
         private WeakReference<Popup> popupWeakReference;
         private double oldAnchorY;
         private double distance;

         CustomTransition(Popup popup, double oldAnchorY, double distance) {
            this.popupWeakReference = new WeakReference<>(popup);
            this.oldAnchorY = oldAnchorY;
            this.distance = distance;
            this.setCycleDuration(Duration.millis(350.0));
         }

         protected void interpolate(double frac) {
            Popup popup = this.popupWeakReference.get();
            if (popup != null) {
               double newAnchorY = this.oldAnchorY + this.distance * frac;
               popup.setAnchorY(newAnchorY);
            }
         }
      }
   }
}
