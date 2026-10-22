package com.cropster.roastingintelligence.ui.util;

import com.cropster.roastingintelligence.ui.JavaFxController;
import com.cropster.roastingintelligence.ui.JavaFxUtil;
import java.net.UnknownHostException;
import java.util.Objects;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextFlow;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.cropster.roastingintelligence.measurement.MeasurementSourceFactory;

public final class WindowUtil {
   public static final double ALERT_MIN_WIDTH = 300.0;
   public static final double SAFE_SIZE_MEAN = 1.0;
   public static final double SAFE_SIZE_MINIMUM = 0.0;
   public static final double SAFE_SIZE_PREFERRED = -1.0;

   public static <T> Dialog<T> center(Stage stage, Dialog<T> dialog) {
      dialog.initOwner(stage);
      Window window = dialog.getDialogPane().getScene().getWindow();
      window.addEventFilter(WindowEvent.WINDOW_SHOWN, e -> {
         dialog.setX(stage.getX() + stage.getWidth() / 2.0 - dialog.getWidth() / 2.0);
         dialog.setY(stage.getY() + stage.getHeight() / 2.0 - dialog.getHeight() / 2.0);
      });
      return dialog;
   }

   public static void center(Stage stage) {
      Objects.requireNonNull(stage, "Null 'stage' parameter");
      Window owner = stage.getOwner();
      if (owner == null) {
         stage.centerOnScreen();
      } else {
         stage.setX(owner.getX() + (owner.getWidth() - stage.getWidth()) / 2.0);
         stage.setY(owner.getY() + (owner.getHeight() - stage.getHeight()) / 2.0);
      }
   }

   public static Alert createAlert(AlertType type, String title, String contentText, ButtonType... buttonTypes) {
      return createAlert(type, title, contentText, null, buttonTypes);
   }

   public static Alert createAlert(AlertType type, String title, String contentText, String positiveButtonStyle, ButtonType... buttonTypes) {
      Alert alert = new Alert(type, contentText, buttonTypes);
      alert.getDialogPane().setMinHeight(Double.NEGATIVE_INFINITY);
      alert.setTitle(title);
      alert.setHeaderText(null);
      if (StringUtils.isNotBlank(positiveButtonStyle)) {
         JavaFxUtil.applyButtonStyles(alert, positiveButtonStyle);
      }

      JavaFxUtil.setDefaultLineSpacing(alert.getDialogPane());
      return alert;
   }

   public static Alert createAlert(
      AlertType type, String title, String contentText, Node furtherContentNode, String positiveButtonStyle, ButtonType... buttonTypes
   ) {
      Alert alert = new Alert(type, null, buttonTypes);
      alert.getDialogPane().setMinHeight(Double.NEGATIVE_INFINITY);
      alert.setTitle(title);
      alert.setHeaderText(null);
      boolean validContentText = StringUtils.isNotBlank(contentText);
      boolean validContentNode = furtherContentNode != null;
      if (validContentText || validContentNode) {
         Node content = furtherContentNode;
         if (validContentText) {
            TextFlow tf = new TextFlow(JavaFxUtil.parseHtmlToTextNodes(contentText).toArray(new Node[0]));
            if (validContentNode) {
               VBox vbox = new VBox();
               vbox.setSpacing(16.0);
               vbox.getChildren().addAll(new Node[]{tf, furtherContentNode});
               content = vbox;
            } else {
               content = tf;
            }
         }

         alert.getDialogPane().setContent(content);
      }

      if (StringUtils.isNotBlank(positiveButtonStyle)) {
         JavaFxUtil.applyButtonStyles(alert, positiveButtonStyle);
      }

      JavaFxUtil.setDefaultLineSpacing(alert.getDialogPane());
      return alert;
   }

   public static Alert createTextFlowAlert(AlertType type, String title, String contentText) {
      return createTextFlowAlert(type, title, contentText, null);
   }

   public static Alert createTextFlowAlert(AlertType type, String title, String contentText, String positiveButtonStyle) {
      return createAlert(type, title, contentText, null, positiveButtonStyle);
   }

   public static Alert createProgressAlert(Stage owner, String headerText, Runnable onCloseCallback) {
      ProgressBar progressBar = new ProgressBar();
      progressBar.setProgress(-1.0);
      Alert alert = new Alert(AlertType.NONE);
      if (owner != null) {
         alert.initOwner(owner);
      }

      alert.setResizable(false);
      alert.setHeaderText(headerText);
      alert.getDialogPane().setMinWidth(300.0);
      alert.getDialogPane().getStylesheets().add(JavaFxController.STYLE_SHEET);
      alert.getDialogPane().setContent(progressBar);
      if (onCloseCallback != null) {
         alert.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
         alert.setOnCloseRequest(e -> onCloseCallback.run());
      }

      JavaFxUtil.applyButtonStyles(alert);
      JavaFxUtil.setDefaultLineSpacing(alert.getDialogPane());
      return alert;
   }

   public static Point2D getCenteredPosition(Stage stage, Rectangle2D containerBounds) {
      double x = containerBounds.getMinX();
      double y = containerBounds.getMinY();
      x += containerBounds.getWidth() / 2.0 - stage.getWidth() / 2.0;
      y += containerBounds.getHeight() / 2.0 - stage.getHeight() / 2.0;
      return new Point2D(x, y);
   }

   public static Rectangle2D getScreenBounds(Stage stage) {
      Rectangle2D screenBounds = null;
      if (stage != null) {
         for (Screen screen : Screen.getScreens()) {
            if (screen.getBounds().contains(stage.getX(), stage.getY())) {
               screenBounds = screen.getVisualBounds();
               break;
            }
         }
      }

      if (screenBounds == null) {
         screenBounds = Screen.getPrimary().getVisualBounds();
      }

      return screenBounds;
   }

   public static Rectangle2D getSafeScreenBounds(Stage stage, double horizontalPadding, double verticalPadding) {
      Rectangle2D screenBounds = getScreenBounds(stage);
      return new Rectangle2D(
         screenBounds.getMinX() + horizontalPadding,
         screenBounds.getMinY() + verticalPadding,
         screenBounds.getWidth() - horizontalPadding * 2.0,
         screenBounds.getHeight() - verticalPadding * 2.0
      );
   }

   public static double getSafeHeight(double height, Stage stage, double verticalPadding) {
      return Math.min(height, getSafeScreenBounds(stage, 0.0, verticalPadding).getHeight());
   }

   public static double getSafeWidth(double width, Stage stage, double horizontalPadding) {
      return Math.min(width, getSafeScreenBounds(stage, horizontalPadding, 0.0).getWidth());
   }

   public static Point2D getScreenPosition(Stage stage, Stage parentStage) {
      return parentStage != null
         ? getCenteredPosition(stage, new Rectangle2D(parentStage.getX(), parentStage.getY(), parentStage.getWidth(), parentStage.getHeight()))
         : getCenteredPosition(stage, getScreenBounds(stage));
   }

   public static Screen screenFor(Window window) {
      return window == null ? Screen.getPrimary() : screensFor(window).stream().filter(Objects::nonNull).findFirst().orElse(Screen.getPrimary());
   }

   public static ObservableList<Screen> screensFor(Window window) {
      return window == null
         ? FXCollections.observableArrayList()
         : Screen.getScreensForRectangle(window.getX(), window.getY(), window.getWidth(), window.getHeight());
   }

   public static Screen screenForTopLeft(Scene scene) {
      if (scene != null) {
         Window sceneContainer = scene.getWindow();
         if (sceneContainer != null) {
            return Screen.getScreens().stream().filter(s -> s.getBounds().contains(sceneContainer.getX(), sceneContainer.getY())).findFirst().orElse(null);
         }
      }

      return null;
   }

   public static void setWindowMinWidth(Window window, double minWidth) {
      if (window instanceof Stage) {
         ((Stage)window).setMinWidth(minWidth);
      }
   }

   public static void setWindowMinHeight(Window window, double minHeight) {
      if (window instanceof Stage) {
         ((Stage)window).setMinHeight(minHeight);
      }
   }

   public static void setWindowMaxHeight(Window window, double maxHeight) {
      if (window instanceof Stage) {
         ((Stage)window).setMaxHeight(maxHeight);
      }
   }

   public static void showAlert(AlertType type, Stage stage, String title, String contentText) {
      Alert alert = createTextFlowAlert(type, title, contentText);
      switch (type) {
         case ERROR:
            JavaFxUtil.applyButtonStyles(alert, ButtonType.OK, "button--error");
            break;
         case WARNING:
            JavaFxUtil.applyButtonStyles(alert, ButtonType.OK, "button--warning");
            break;
         case CONFIRMATION:
            alert.getButtonTypes().setAll(new ButtonType[]{ButtonType.OK});
            JavaFxUtil.applyButtonStyles(alert, ButtonType.OK, "button--primary");
            break;
         default:
            JavaFxUtil.applyButtonStyles(alert);
      }

      alert.getDialogPane().getStylesheets().add(JavaFxController.STYLE_SHEET);
      if (stage != null) {
         alert.getDialogPane().setMaxWidth(Math.max(800.0, stage.getWidth() / 2.0));
         center(stage, alert).showAndWait();
      } else {
         alert.getDialogPane().setMaxWidth(800.0);
         alert.showAndWait();
      }
   }

   public static void showSetupAlert(Stage stage, Class<? extends MeasurementSourceFactory> sourceFactoryClass, Exception ex) {
      showAlert(
         AlertType.ERROR,
         stage,
         org.cropster.roastingintelligence.util.Messages.m_("Error setting up measurement source"),
         org.cropster.roastingintelligence.util.Messages.m_(
               "Error setting up measurement source: {0}", org.cropster.roastingintelligence.util.Messages.m_(sourceFactoryClass.getSimpleName())
            )
            + "\n\n"
            + ex
      );
   }

   public static void showSetupAlert(Stage stage, Class<? extends MeasurementSourceFactory> sourceFactoryClass, UnknownHostException ex) {
      showAlert(
         AlertType.ERROR,
         stage,
         org.cropster.roastingintelligence.util.Messages.m_("Unknown host!"),
         org.cropster.roastingintelligence.util.Messages.m_(
               "Error setting up measurement source: {0}", org.cropster.roastingintelligence.util.Messages.m_(sourceFactoryClass.getSimpleName())
            )
            + "\n\n"
            + org.cropster.roastingintelligence.util.Messages.m_("Unknown host: {0}", ex.getLocalizedMessage())
      );
   }

   public static void showSetupAlert(Stage stage, LinkageError e) {
      showAlert(
         AlertType.ERROR,
         stage,
         org.cropster.roastingintelligence.util.Messages.m_("Error loading native library!"),
         e.getClass().getSimpleName() + ": " + e.getLocalizedMessage()
      );
   }

   public static void showSafeStage(Stage stage, double minimumWidth, double minimumHeight, double preferredWidth, double preferredHeight, double padding) {
      Stage ownerStage = (Stage)stage.getOwner();
      double minWidth = getSafeWidth(minimumWidth, ownerStage, padding);
      double minHeight = getSafeHeight(minimumHeight, ownerStage, padding);
      Rectangle2D safeScreenBounds = getSafeScreenBounds(ownerStage, 32.0, 32.0);
      double maxWidth = safeScreenBounds.getWidth();
      double maxHeight = safeScreenBounds.getHeight();
      stage.setMinWidth(minWidth);
      stage.setMinHeight(minHeight);
      stage.setMaxWidth(maxWidth);
      stage.setMaxHeight(maxHeight);
      stage.setX(safeScreenBounds.getMinX());
      stage.setY(safeScreenBounds.getMinY());
      double width = preferredWidth == 0.0 ? minWidth : (preferredWidth == 1.0 ? (minWidth + maxWidth) / 2.0 : preferredWidth);
      double height = preferredHeight == 0.0 ? minHeight : (preferredHeight == 1.0 ? (minHeight + maxHeight) / 2.0 : preferredHeight);
      if (SystemUtils.IS_OS_WINDOWS) {
         Platform.runLater(() -> {
            if (width > -1.0) {
               stage.setWidth(Math.min(maxWidth, Math.max(minWidth, width)));
            }

            if (height > -1.0) {
               stage.setHeight(Math.min(maxHeight, Math.max(minHeight, height)));
            }

            stage.show();
            stage.centerOnScreen();
         });
      } else {
         if (width > -1.0) {
            stage.setWidth(Math.min(maxWidth, Math.max(minWidth, width)));
         }

         if (height > -1.0) {
            stage.setHeight(Math.min(maxHeight, Math.max(minHeight, height)));
         }

         stage.show();
         stage.centerOnScreen();
      }
   }

   private WindowUtil() {
   }
}
