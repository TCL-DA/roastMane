package com.cropster.roastingintelligence.ui;

import com.cropster.roastingintelligence.exception.TokenRefreshException;
import com.cropster.roastingintelligence.model.notification.Notification;
import com.cropster.roastingintelligence.model.notification.NotificationType;
import com.cropster.roastingintelligence.service.NotificationBusService;
import com.cropster.roastingintelligence.ui.components.MessageBox;
import com.cropster.roastingintelligence.ui.layout.SystemSidebar;
import com.cropster.roastingintelligence.ui.util.ActionBuilder;
import com.cropster.roastingintelligence.ui.util.Icon;
import com.cropster.roastingintelligence.ui.util.PlatformScheduler;
import jakarta.ws.rs.NotAuthorizedException;
import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.WebApplicationException;
import java.net.UnknownHostException;
import java.text.MessageFormat;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.controlsfx.control.action.Action;
import org.controlsfx.control.action.ActionUtils.ActionTextBehavior;
import org.cropster.roastingintelligence.RoastingIntelligenceApp;
import org.girod.javafx.svgimage.SVGImage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SystemNotificationController extends AbstractNotificationController {
   private static final Logger LOGGER = LoggerFactory.getLogger(SystemNotificationController.class);
   private final SystemNotificationController.NotificationHandler handler = new SystemNotificationController.NotificationHandler();

   @Override
   protected Consumer<Notification> createConsumer(String topic) {
      Consumer<Notification> consumer = switch (topic) {
         case "system-status-connection" -> this.handler::updateConnectionStatus;
         case "system-status-ambient-temperature" -> this.handler::updateAmbientTemperatureStatus;
         case "system-status-data-received" -> this.handler::updateSynchronizationStatusIcon;
         case "system-status-synchronization" -> this.handler::updateSynchronizationStatus;
         case "system-status-synchronization-date" -> this.handler::updateSynchronizationDateStatus;
         case "system-update" -> this.handler::updateUpdaterStatus;
         case "system-update-progress" -> this.handler::updateUpdaterProgressStatus;
         case "activity-insights" -> this.handler::updateActivityInsights;
         default -> throw new IllegalArgumentException("Unknown topic: " + topic);
      };
      return n -> Platform.runLater(() -> consumer.accept(n));
   }

   public void control(Button systemSidebarButton, Label systemNotificationTag, SystemSidebar systemSidebar, Runnable checkForUpdate, String loggedInUser) {
      this.handler.init(systemSidebarButton, systemNotificationTag, systemSidebar, checkForUpdate, loggedInUser);
   }

   public SystemNotificationController.NotificationHandler getHandler() {
      return this.handler;
   }

   public static final class NotificationHandler {
      private static final String STATUS_401 = org.cropster.roastingintelligence.util.Messages.m_(
         "You are not authorized. Please restart the application and log in again."
      );
      private static final String STATUS_403 = org.cropster.roastingintelligence.util.Messages.m_(
         "Permission denied. Contact your group administrator and verify that you have access to the group."
      );
      private static final String STATUS_500 = org.cropster.roastingintelligence.util.Messages.m_(
         "The application cannot be synchronized. Please contact our support team."
      );
      private static final String STATUS_UNKNOWN_HOST = org.cropster.roastingintelligence.util.Messages.m_(
         "The application cannot be synchronized as our servers are unreachable. Check your internet connection or try again later."
      );
      private static final String FAILOVER_STATUS = STATUS_500;
      private static final String AUTHORIZATION_FAILED_MESSAGE = org.cropster.roastingintelligence.util.Messages.m_(
            "Authorization failed! Please try to login again."
         )
         + "\n\n"
         + org.cropster.roastingintelligence.util.Messages.m_("Hint: Credentials are the same as for the Cropster web platform.");
      private static final String SESSION_EXPIRED_MESSAGE = org.cropster.roastingintelligence.util.Messages.m_(
         "Your session has expired or is invalid!\nPlease log in again to restore connectivity."
      );
      private static final String ERROR_STYLE_CLASS = "error";
      private static final String INFO_STYLE_CLASS = "info";
      private static final String WARNING_STYLE_CLASS = "warning";
      private static final String HIGHLIGHTED_STYLE_CLASS = "highlighted";
      private static final String SEMI_TRANSPARENT_ICON_STYLE_CLASS = "icon--semi-transparent";
      private static final String CONNECTION_HELP_URL = "https://help.cropster.com/en/knowledge/connecting-your-roast-machine";
      private final BooleanProperty disconnected = new SimpleBooleanProperty(true);
      private final BooleanProperty notSynchronized = new SimpleBooleanProperty(false);
      private final BooleanProperty updateDetected = new SimpleBooleanProperty(false);
      private final ObjectProperty<NotificationType> globalStatus = new SimpleObjectProperty(NotificationType.NONE);
      private final IntegerProperty roastedBatches = new SimpleIntegerProperty(0);
      private final IntegerProperty abortedRoasts = new SimpleIntegerProperty(0);
      private final IntegerProperty weightOutliers = new SimpleIntegerProperty(0);
      private Button systemSidebarButton;
      private Label systemNotificationTag;
      private SystemSidebar systemSidebar;
      private Runnable checkForUpdate;
      private String currentVersion;
      private ScheduledFuture<?> hideUpdateNotFoundTagTask;

      private static Node createIcon(NotificationType type) {
         return switch (type) {
            case INFO -> Icon.INFO.load("icon--info");
            case SUCCESS -> Icon.SUCCESS.load("icon--success");
            case WARNING -> Icon.WARNING.load("icon--warning");
            case ERROR -> Icon.ERROR.load("icon--error");
            case NONE -> null;
         };
      }

      private static Node createTagIcon(NotificationType type) {
         Group group = null;

         SVGImage icon = switch (type) {
            case INFO -> Icon.INFO.load("icon--info", 0.75);
            case SUCCESS, NONE -> null;
            case WARNING -> Icon.WARNING.load("icon--warning", 0.75);
            case ERROR -> Icon.ERROR.load("icon--error", 0.75);
         };
         if (icon != null) {
            Circle circle = new Circle(10.0);
            circle.getStyleClass().add("system-sidebar-tag");

            circle.getStyleClass().add(switch (type) {
               case INFO -> "info";
               case SUCCESS, NONE -> "";
               case WARNING -> "warning";
               case ERROR -> "error";
            });
            icon.setTranslateX(-8.0);
            icon.setTranslateY(-8.0);
            group = new Group(new Node[]{circle, icon});
         }

         return group;
      }

      public void updateConnectionStatus(Notification notification) {
         NotificationType type = notification.getType();
         VBox iconContainer = new VBox();
         iconContainer.getChildren().add(createIcon(type));
         Label connectionStatus = this.systemSidebar.getConnectionStatus();
         connectionStatus.setGraphic(iconContainer);
         connectionStatus.setText(notification.getMessage());
         iconContainer.prefHeightProperty().bind(connectionStatus.heightProperty().subtract(connectionStatus.getBaselineOffset()));
         if (type == NotificationType.ERROR) {
            connectionStatus.getStyleClass().add("highlighted");
         } else {
            connectionStatus.getStyleClass().remove("highlighted");
         }

         this.systemSidebar.getAmbientTemperatureStatus().setVisible(false);
         this.disconnected.set(type == NotificationType.ERROR);
      }

      public void updateAmbientTemperatureStatus(Notification notification) {
         Label ambientTemperatureStatus = this.systemSidebar.getAmbientTemperatureStatus();
         ambientTemperatureStatus.setVisible(true);
         ambientTemperatureStatus.setText(notification.getMessage());
      }

      public void updateSynchronizationStatus(Notification notification) {
         NotificationType type = notification.getType();
         Label synchronizationStatus = this.systemSidebar.getSynchronizationStatus();
         synchronizationStatus.setGraphic(createIcon(type));
         if (type == NotificationType.WARNING) {
            synchronizationStatus.getStyleClass().add("highlighted");
         } else {
            synchronizationStatus.getStyleClass().remove("highlighted");
         }

         Throwable exception = notification.getProperty(Throwable.class, "exception");
         if (exception != null) {
            this.systemSidebar.getWarningStatusMessage().setText(this.messageFor(exception));
         }

         this.notSynchronized.set(type == NotificationType.WARNING);
      }

      public void updateSynchronizationStatusIcon(Notification notification) {
         Label synchronizationStatus = this.systemSidebar.getSynchronizationStatus();
         synchronizationStatus.setGraphic(createIcon(NotificationType.SUCCESS));
      }

      public void updateSynchronizationDateStatus(Notification notification) {
         Label synchronizationStatus = this.systemSidebar.getSynchronizationStatus();
         synchronizationStatus.setText(notification.getMessage());
      }

      public void updateUpdaterStatus(Notification notification) {
         if (this.systemSidebar != null) {
            NotificationType type = notification.getType();
            Label updateTitle = this.systemSidebar.getUpdateTitle();
            updateTitle.setGraphic(createIcon(type));
            updateTitle.setText(notification.getTitle());
            boolean updateNotFound = notification.getProperty(Boolean.class, "version-update-not-found", false);
            String version = notification.getProperty(String.class, "version-current");
            if (version != null) {
               this.currentVersion = org.cropster.roastingintelligence.util.Messages.m_("Currently on {0}", "RI " + version.trim());
            }

            TextFlow currentVersionTextFlow = this.systemSidebar.getCurrentVersionTextFlow();
            currentVersionTextFlow.setVisible(false);
            HBox updateNotFoundTag = this.systemSidebar.getUpdateNotFoundTag();
            Label updateDescription = this.systemSidebar.getUpdateDescription();
            updateDescription.setText(notification.getMessage());
            updateDescription.setVisible(StringUtils.isNotBlank(notification.getMessage()));
            Button updateActionButton = this.systemSidebar.getUpdateActionButton();
            updateActionButton.setVisible(false);
            Action action = notification.getProperty(Action.class, "action");
            if (action != null) {
               ActionBuilder.configureButton(action, updateActionButton, ActionTextBehavior.SHOW);
               updateActionButton.setVisible(true);
            }

            if (this.hideUpdateNotFoundTagTask != null) {
               this.hideUpdateNotFoundTagTask.cancel(false);
               updateNotFoundTag.setVisible(false);
            }

            if (type == NotificationType.SUCCESS && updateNotFound) {
               updateNotFoundTag.setVisible(true);
               this.hideUpdateNotFoundTagTask = PlatformScheduler.schedule(() -> {
                  updateNotFoundTag.setVisible(false);
                  updateActionButton.setVisible(true);
               }, 10, TimeUnit.SECONDS);
            }

            if (this.currentVersion != null) {
               currentVersionTextFlow.getChildren().clear();
               String availableVersion = notification.getProperty(String.class, "version-available");
               String readyVersion = notification.getProperty(String.class, "version-ready");
               if (availableVersion == null && readyVersion == null) {
                  currentVersionTextFlow.getChildren().add(new Text(this.currentVersion));
               } else {
                  if (availableVersion != null) {
                     currentVersionTextFlow.getChildren()
                        .add(new Text(org.cropster.roastingintelligence.util.Messages.m_("RI {0} available", availableVersion)));
                  } else {
                     currentVersionTextFlow.getChildren().add(new Text(org.cropster.roastingintelligence.util.Messages.m_("RI {0} available", readyVersion)));
                  }

                  Text currentVersionText = new Text(" (" + this.currentVersion + ")");
                  currentVersionText.getStyleClass().add("text-muted");
                  currentVersionTextFlow.getChildren().add(currentVersionText);
               }

               currentVersionTextFlow.setVisible(true);
            }

            Boolean autoUpdateSuspended = notification.getProperty(Boolean.class, "auto-update-suspended", false);
            if (autoUpdateSuspended != null && autoUpdateSuspended) {
               updateActionButton.setVisible(false);
               this.systemSidebar.getUpdateSuspendedTag().setVisible(true);
            }

            this.systemSidebar.getProgressBar().setVisible(false);
            this.updateDetected.set(type != NotificationType.SUCCESS);
         }
      }

      public void updateUpdaterProgressStatus(Notification notification) {
         Double progress = notification.getProperty(Double.class, "progress");
         if (progress != null) {
            if (this.systemSidebar.getProgressBar().getProgress() < 1.0) {
               this.systemSidebar.getProgressBar().setProgress(progress);
               this.systemSidebar.getProgressBar().setVisible(true);
            }

            if (progress >= 1.0) {
               PlatformScheduler.scheduleBackgroundTask(this.checkForUpdate, 1000, TimeUnit.MILLISECONDS);
            }
         }
      }

      public void updateActivityInsights(Notification notification) {
         Boolean isRB = notification.getProperty(Boolean.class, "roasted-batch");
         if (Boolean.TRUE.equals(isRB)) {
            this.roastedBatches.set(1 + this.roastedBatches.get());
         }

         Boolean isAR = notification.getProperty(Boolean.class, "aborted-roast");
         if (Boolean.TRUE.equals(isAR)) {
            this.abortedRoasts.set(1 + this.abortedRoasts.get());
         }

         Boolean isWO = notification.getProperty(Boolean.class, "weight-outlier");
         if (Boolean.TRUE.equals(isWO)) {
            this.weightOutliers.set(1 + this.weightOutliers.get());
         }
      }

      public ReadOnlyObjectProperty<NotificationType> globalStatusProperty() {
         return this.globalStatus;
      }

      public NotificationType getGlobalStatus() {
         return (NotificationType)this.globalStatus.get();
      }

      private void init(Button systemSidebarButton, Label systemNotificationTag, SystemSidebar systemSidebar, Runnable checkForUpdate, String loggedInUser) {
         this.systemSidebarButton = systemSidebarButton;
         this.systemNotificationTag = systemNotificationTag;
         this.systemSidebar = systemSidebar;
         this.checkForUpdate = checkForUpdate;
         systemSidebarButton.getStyleClass().remove("button");
         MessageBox warningStatusMessage = systemSidebar.getWarningStatusMessage();
         warningStatusMessage.setGraphic(null);
         MessageBox errorStatusMessage = systemSidebar.getErrorStatusMessage();
         errorStatusMessage.setGraphic(null);
         errorStatusMessage.setText(
            org.cropster.roastingintelligence.util.Messages.m_(
               "<b>Some troubleshooting steps</b>\n\nCheck the physical connection between the roasting machine and the computer.\n\nIf the problem persists, submit a ticket using the Support sidebar (log files will be attached automatically), or visit <a href=\"{0}\">the following link</a>.",
               "https://help.cropster.com/en/knowledge/connecting-your-roast-machine"
            )
         );
         this.globalStatus.addListener((s, o, status) -> {
            this.updateSystemStatusTitle(status);
            this.updateSystemSidebarButton(status);
            this.showToastNotification(status);
         });
         this.globalStatus.bind(Bindings.createObjectBinding(() -> {
            if (this.disconnected.get()) {
               return NotificationType.ERROR;
            } else if (this.notSynchronized.get()) {
               return NotificationType.WARNING;
            } else {
               return this.updateDetected.get() ? NotificationType.INFO : NotificationType.NONE;
            }
         }, new Observable[]{this.disconnected, this.notSynchronized, this.updateDetected}));
         errorStatusMessage.visibleProperty().bind(this.globalStatus.isEqualTo(NotificationType.ERROR));
         warningStatusMessage.visibleProperty().bind(this.globalStatus.isEqualTo(NotificationType.WARNING));
         systemSidebar.getLoggedInUser().setText(loggedInUser);
         systemSidebar.getRoastedBatches().textProperty().bind(this.roastedBatches.asString());
         systemSidebar.getAbortedRoasts().textProperty().bind(this.abortedRoasts.asString());
         systemSidebar.getWeightOutliers().textProperty().bind(this.weightOutliers.asString());
      }

      private String messageFor(Throwable exception) {
         String format = "<b>{0}</b>\n\n{1}";
         String title = org.cropster.roastingintelligence.util.Messages.m_("Synchronization failed");
         if (ExceptionUtils.indexOfThrowable(exception, NotAuthorizedException.class) >= 0) {
            return MessageFormat.format(format, title, AUTHORIZATION_FAILED_MESSAGE);
         } else if (ExceptionUtils.indexOfThrowable(exception, TokenRefreshException.class) >= 0) {
            return MessageFormat.format(format, title, SESSION_EXPIRED_MESSAGE);
         } else if (ExceptionUtils.indexOfThrowable(exception, WebApplicationException.class) >= 0) {
            WebApplicationException waex = (WebApplicationException)ExceptionUtils.throwableOfThrowable(exception, WebApplicationException.class);
            if (waex.getResponse() != null) {
               int status = waex.getResponse().getStatus();
               if (status == 401) {
                  return MessageFormat.format(format, title, STATUS_401);
               } else if (status == 403) {
                  return MessageFormat.format(format, title, STATUS_403);
               } else {
                  return status >= 500 && status < 600 ? MessageFormat.format(format, title, STATUS_500) : MessageFormat.format(format, title, FAILOVER_STATUS);
               }
            } else {
               this.logFailoverMessage(exception);
               return MessageFormat.format(format, title, waex.getLocalizedMessage());
            }
         } else if (ExceptionUtils.indexOfThrowable(exception, ProcessingException.class) >= 0) {
            if (ExceptionUtils.indexOfThrowable(exception, UnknownHostException.class) >= 0) {
               return MessageFormat.format(format, title, STATUS_UNKNOWN_HOST);
            } else {
               this.logFailoverMessage(exception);
               return MessageFormat.format(format, title, exception.getLocalizedMessage());
            }
         } else {
            this.logFailoverMessage(exception);
            return MessageFormat.format(format, title, exception.getLocalizedMessage());
         }
      }

      private void logFailoverMessage(Throwable exception) {
         SystemNotificationController.LOGGER
            .warn(
               "Failover used for exception: {} [cause: {}, message: {}]",
               new Object[]{
                  exception.getClass().getName(), exception.getCause() != null ? exception.getCause().getClass().getName() : "-", exception.getMessage()
               }
            );
      }

      private void updateSystemSidebarButton(NotificationType status) {
         this.systemSidebarButton.getStyleClass().remove("error");
         this.systemSidebarButton.getStyleClass().remove("warning");
         this.systemSidebarButton.getStyleClass().remove("info");
         this.systemNotificationTag.setText(null);
         this.systemNotificationTag.setGraphic(null);
         this.systemNotificationTag.setVisible(false);
         switch (status) {
            case INFO:
               this.systemSidebarButton.getStyleClass().add("info");
               this.systemSidebarButton.setGraphic(Icon.LOGO_CROPSTER_COLOR.load());
               this.updateSystemNotificationTagIcon(status);
               break;
            case SUCCESS:
            default:
               this.systemSidebarButton.setGraphic(Icon.LOGO_CROPSTER_COLOR.load());
               this.updateSystemNotificationTagText(status);
               break;
            case WARNING:
               this.systemSidebarButton.getStyleClass().add("warning");
               this.systemSidebarButton.setGraphic(Icon.LOGO_CROPSTER.load("icon--semi-transparent"));
               this.updateSystemNotificationTagIcon(status);
               break;
            case ERROR:
               this.systemSidebarButton.getStyleClass().add("error");
               this.systemSidebarButton.setGraphic(Icon.LOGO_CROPSTER.load("icon--semi-transparent"));
               this.updateSystemNotificationTagIcon(status);
         }
      }

      private void updateSystemNotificationTagIcon(NotificationType status) {
         this.systemNotificationTag.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
         this.systemNotificationTag.setGraphic(createTagIcon(status));
         this.systemNotificationTag.setVisible(true);
      }

      private void updateSystemNotificationTagText(NotificationType status) {
         this.systemNotificationTag.setContentDisplay(ContentDisplay.TEXT_ONLY);
      }

      private void updateSystemStatusTitle(NotificationType status) {
         switch (status) {
            case WARNING:
               this.systemSidebar.getStatusTitle().setText(org.cropster.roastingintelligence.util.Messages.m_("Synchronization issues"));
               this.systemSidebar.getStatusTitle().setGraphic(Icon.LOGO_CROPSTER.load("icon--semi-transparent"));
               break;
            case ERROR:
               this.systemSidebar.getStatusTitle().setText(org.cropster.roastingintelligence.util.Messages.m_("Disruption encountered"));
               this.systemSidebar.getStatusTitle().setGraphic(Icon.LOGO_CROPSTER.load("icon--semi-transparent"));
               break;
            default:
               this.systemSidebar.getStatusTitle().setText(org.cropster.roastingintelligence.util.Messages.m_("Fully operational"));
               this.systemSidebar.getStatusTitle().setGraphic(Icon.LOGO_CROPSTER_COLOR.load());
         }
      }

      private void showToastNotification(NotificationType status) {
         if (status == NotificationType.ERROR) {
            RoastingIntelligenceApp.retrieveSingleton(NotificationBusService.class)
               .publish(
                  Notification.builder(NotificationType.ERROR)
                     .withTitle(org.cropster.roastingintelligence.util.Messages.m_("Machine connection lost"))
                     .withMessage(
                        org.cropster.roastingintelligence.util.Messages.m_(
                           "Measurement readings cannot be displayed. Please check your machine hardware connection."
                        )
                     )
                     .withProperty("tag", "system-status-connection-important-tag")
                     .withProperty(
                        "action",
                        new ActionBuilder(org.cropster.roastingintelligence.util.Messages.m_("Open the System sidebar"), e -> this.systemSidebarButton.fire())
                           .build()
                     )
                     .withProperty("show-boolean-supplier", (BooleanSupplier)() -> !this.systemSidebar.isVisible() || this.systemSidebar.isDetached())
                     .build(),
                  "system-status-connection-important"
               );
         }
      }
   }
}
