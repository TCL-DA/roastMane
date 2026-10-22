package com.cropster.roastingintelligence.ui.layout;

import com.cropster.roastingintelligence.exception.BetaAccessDeniedException;
import com.cropster.roastingintelligence.exception.LoginTimeoutException;
import com.cropster.roastingintelligence.exception.TokenRefreshException;
import com.cropster.roastingintelligence.remote.impl.OAuth2Connection;
import com.cropster.roastingintelligence.ui.JavaFxUtil;
import com.cropster.roastingintelligence.ui.PermissionNotificationController;
import com.cropster.roastingintelligence.ui.components.MessageBox;
import com.cropster.roastingintelligence.ui.util.Icon;
import com.cropster.roastingintelligence.ui.util.OnceRunnable;
import com.cropster.roastingintelligence.ui.util.PlatformScheduler;
import jakarta.ws.rs.NotAuthorizedException;
import jakarta.ws.rs.ProcessingException;
import java.net.URL;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import org.apache.commons.lang3.StringUtils;
import org.cropster.roastingintelligence.AppModel;
import org.cropster.roastingintelligence.model.User;
import org.cropster.roastingintelligence.remote.CsarConnection;
import org.cropster.roastingintelligence.util.NanoTime;
import org.girod.javafx.svgimage.SVGImage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginWindowController implements Initializable {
   private static final String ACCESS_DENIED_MESSAGE = "Access denied: The user {0} is not registered in any ongoing beta program. To collaborate on new machine connections, join our <a href=\"{1}\">RMM Beta Program</a>.";
   private static final String STYLE_MESSAGE = "info-message";
   private static final String STYLE_MESSAGE_WARNING = "info-message--warning";
   private static final String STYLE_MESSAGE_ERROR = "info-message--error";
   private static final SVGImage ICON_INFO = Icon.INFO.load("icon--info");
   private static final SVGImage ICON_WARNING = Icon.WARNING.load("icon--warning");
   private static final SVGImage ICON_ERROR = Icon.ERROR.load("icon--error");
   private static final long DAY = 86400000L;
   @FXML
   private ResourceBundle resources;
   @FXML
   private GridPane oAuth2LoginPanel;
   @FXML
   private Label oAuth2loginInfoLabel;
   @FXML
   private MessageBox statusLabel;
   @FXML
   private Label statusSpacer;
   @FXML
   private Button resolveButton;
   @FXML
   private CheckBox rememberMeCheckbox;
   @FXML
   private Button continueOAuth2OfflineButton;
   @FXML
   private Button loginOAuth2Button;
   @FXML
   private Button cancelOAuth2Button;
   @FXML
   private ButtonBar buttonBar;
   private final Logger logger = LoggerFactory.getLogger(LoginWindowController.class);
   private AppModel appModel;
   private CsarConnection connection;
   private OAuth2Connection oAuth2Authenticator;
   private Runnable successAction;
   private Task<Void> loginTask;
   private boolean autoLogin;
   private Date thresholdDate = null;
   private int loginAttempts = 0;

   public void initialize(URL location, ResourceBundle resources) {
      this.statusLabel
         .visibleProperty()
         .bind(Bindings.createBooleanBinding(() -> StringUtils.isNotBlank(this.statusLabel.getText()), new Observable[]{this.statusLabel.textProperty()}));
      this.statusLabel.graphicProperty().bind(Bindings.createObjectBinding(() -> {
         if (this.statusLabel.getStyleClass().contains("info-message--error")) {
            return ICON_ERROR;
         } else {
            return this.statusLabel.getStyleClass().contains("info-message--warning") ? ICON_WARNING : ICON_INFO;
         }
      }, new Observable[]{this.statusLabel.getStyleClass()}));
      JavaFxUtil.setDefaultLineSpacing(this.oAuth2LoginPanel);
      Platform.runLater(
         () -> {
            String version = this.appModel != null && this.appModel.getVersion() != null ? this.appModel.getVersion().toString() : "";
            this.oAuth2loginInfoLabel.setMaxWidth(this.buttonBar.getWidth() + 20.0);
            this.oAuth2loginInfoLabel
               .setText(
                  org.cropster.roastingintelligence.util.Messages.m_(
                     "The login to roastMane {0} is performed through the browser via the roastMane web platform.", version
                  )
               );
            Platform.runLater(() -> this.oAuth2LoginPanel.getScene().getWindow().sizeToScene());
         }
      );
   }

   public void setAppModel(AppModel appModel) {
      this.appModel = appModel;
      this.rememberMeCheckbox.setSelected(appModel.isRememberMe());
      if (!appModel.isTokenSet() || System.currentTimeMillis() - appModel.getScore() > 2592000000L || appModel.getCount() >= 90L) {
         this.continueOAuth2OfflineButton.setDisable(true);
      }

      this.thresholdDate = appModel.getLoginThresholdDate();
      if (this.thresholdDate == null) {
         this.thresholdDate = new Date(System.currentTimeMillis() + 2592000000L);
      }
   }

   public void setConnection(CsarConnection connection) {
      this.connection = connection;
   }

   public void setSuccessAction(Runnable successAction) {
      this.successAction = new OnceRunnable(successAction);
   }

   private void setButtonsToWaitingForLogin(boolean waitingForLogin) {
      this.rememberMeCheckbox.setDisable(waitingForLogin);
      this.loginOAuth2Button.setDisable(waitingForLogin);
      this.cancelOAuth2Button.setDisable(!waitingForLogin);
   }

   private void resetLogin(String message, String styleClass) {
      this.autoLogin = false;
      this.statusLabel.setText(message);
      this.statusLabel.getStyleClass().clear();
      this.statusLabel.getStyleClass().add("info-message");
      this.statusLabel.getStyleClass().add(styleClass);
      this.setButtonsToWaitingForLogin(false);
      this.statusLabel.getScene().getWindow().sizeToScene();
   }

   protected void loginWithOAuth2() {
      this.appModel.setLoginThresholdDate(this.thresholdDate);
      this.statusLabel.getStyleClass().clear();
      this.statusLabel.getStyleClass().add("info-message");
      this.setButtonsToWaitingForLogin(true);
      this.statusLabel.setText(org.cropster.roastingintelligence.util.Messages.m_("Logging in ..."));
      this.statusLabel.getScene().getWindow().sizeToScene();
      this.resolveButton.setVisible(false);
      this.loginTask = new Task<Void>() {
         protected Void call() throws Exception {
            long start = NanoTime.currentTimeMillis();
            if (LoginWindowController.this.oAuth2Authenticator == null) {
               LoginWindowController.this.oAuth2Authenticator = new OAuth2Connection(LoginWindowController.this.appModel.getRefreshToken());
               LoginWindowController.this.connection.init(LoginWindowController.this.oAuth2Authenticator);
            }

            LoginWindowController.this.oAuth2Authenticator
               .init(LoginWindowController.this.appModel.getUri(), t -> LoginWindowController.this.appModel.setRefreshToken(t));
            Platform.runLater(
               () -> {
                  if (!LoginWindowController.this.loginTask.isCancelled()) {
                     LoginWindowController.this.statusLabel
                        .setText(
                           org.cropster.roastingintelligence.util.Messages.m_(
                              "Waiting for login at {0} ...", LoginWindowController.this.oAuth2Authenticator.getUri()
                           )
                        );
                     LoginWindowController.this.statusLabel.getScene().getWindow().sizeToScene();
                  }
               }
            );
            LoginWindowController.this.connection.open();
            LoginWindowController.this.appModel.setScore(System.currentTimeMillis());
            LoginWindowController.this.appModel.setCount(0L);
            if (PermissionNotificationController.updateGroupsMembers(LoginWindowController.this.appModel, LoginWindowController.this.connection)) {
               if (!PermissionNotificationController.isAnyRiPermissionGranted(LoginWindowController.this.appModel)) {
                  LoginWindowController.this.connection.close();
                  throw new IllegalStateException(org.cropster.roastingintelligence.util.Messages.m_("User does not have permission to use roastMane."));
               }
            } else {
               LoginWindowController.this.connection.close();
            }

            User user = LoginWindowController.this.connection.getCurrentUser();
            LoginWindowController.this.appModel.setUserName(user.getUsername());
            LoginWindowController.this.appModel.setUser(user.getFullName());
            LoginWindowController.this.connection.log();
            long duration = NanoTime.currentTimeMillis() - start;
            if (LoginWindowController.this.autoLogin && duration < 2000L) {
               Thread.sleep(2000L - duration);
            }

            return null;
         }

         protected void succeeded() {
            LoginWindowController.this.appModel.setRememberMe(LoginWindowController.this.rememberMeCheckbox.isSelected());
            LoginWindowController.this.continueLogin();
         }

         protected void failed() {
            boolean showResolveButton = false;
            Throwable ex = this.getException();
            if (ex != null) {
               LoginWindowController.this.logger.error("Error logging in!", ex);
               Throwable cause = ex;
               if (ex instanceof ExecutionException || ex instanceof ProcessingException) {
                  cause = ex.getCause();
               }

               if (cause instanceof LoginTimeoutException) {
                  LoginWindowController.this.resetLogin(
                     org.cropster.roastingintelligence.util.Messages.m_("Authentication failed!")
                        + "\n"
                        + org.cropster.roastingintelligence.util.Messages.m_("Timeout waiting for browser login."),
                     "info-message--error"
                  );
                  showResolveButton = true;
               } else if (cause instanceof NotAuthorizedException) {
                  LoginWindowController.this.resetLogin(
                     org.cropster.roastingintelligence.util.Messages.m_("Authentication failed!")
                        + "\n"
                        + org.cropster.roastingintelligence.util.Messages.m_("Hint: Credentials are the same as for the roastMane web platform."),
                     "info-message--error"
                  );
                  LoginWindowController.this.continueOAuth2OfflineButton.setDisable(true);
                  showResolveButton = true;
               } else if (cause instanceof BetaAccessDeniedException) {
                  LoginWindowController.this.resetLogin(
                     org.cropster.roastingintelligence.util.Messages.m_(
                        "Access denied: The user {0} is not registered in any ongoing beta program. To collaborate on new machine connections, join our <a href=\"{1}\">RMM Beta Program</a>.",
                        LoginWindowController.this.appModel.getUserName(),
                        "https://www.cropster.com/products/roast/rmm-sign-up/"
                     ),
                     "info-message--error"
                  );
                  LoginWindowController.this.continueOAuth2OfflineButton.setDisable(true);
               } else if (cause instanceof IllegalStateException) {
                  LoginWindowController.this.resetLogin(cause.getMessage(), "info-message--error");
                  LoginWindowController.this.continueOAuth2OfflineButton.setDisable(true);
               } else if (ex instanceof TokenRefreshException) {
                  LoginWindowController.this.resetLogin(
                     org.cropster.roastingintelligence.util.Messages.m_("Your session has expired or is invalid!\nPlease log in again to restore connectivity."),
                     "info-message--error"
                  );
                  showResolveButton = true;
               } else {
                  LoginWindowController.this.resetLogin(
                     String.format(
                        "%s%n%s",
                        org.cropster.roastingintelligence.util.Messages.m_("Error connecting to {0}!", LoginWindowController.this.appModel.getUri()),
                        ex.getMessage()
                     ),
                     "info-message--error"
                  );
               }
            } else {
               LoginWindowController.this.resetLogin(org.cropster.roastingintelligence.util.Messages.m_("Login failed"), "info-message--error");
            }

            if (showResolveButton || ++LoginWindowController.this.loginAttempts >= 3) {
               LoginWindowController.this.resolveButton.setVisible(true);
               Platform.runLater(LoginWindowController.this.oAuth2LoginPanel::layout);
            }

            if (LoginWindowController.this.oAuth2Authenticator != null) {
               PlatformScheduler.executeBackgroundTask(LoginWindowController.this.oAuth2Authenticator::cancel);
            }
         }

         protected void cancelled() {
            if (LoginWindowController.this.oAuth2Authenticator != null) {
               PlatformScheduler.executeBackgroundTask(LoginWindowController.this.oAuth2Authenticator::cancel);
            }

            LoginWindowController.this.resetLogin(org.cropster.roastingintelligence.util.Messages.m_("Login cancelled by the user"), "info-message--warning");
         }

         protected void done() {
            Platform.runLater(((Stage)LoginWindowController.this.oAuth2LoginPanel.getScene().getWindow())::toFront);
         }
      };
      PlatformScheduler.executeBackgroundTask(this.loginTask);
   }

   private synchronized void continueLogin() {
      Platform.runLater(this.successAction);
   }

   @FXML
   protected void handleLogin(ActionEvent event) {
      this.loginWithOAuth2();
   }

   @FXML
   protected void cancelLogin(ActionEvent event) {
      this.loginTask.cancel();
   }

   public void handleAutoLogin() {
      this.autoLogin = true;
      this.loginWithOAuth2();
   }

   @FXML
   public void continueOffline(ActionEvent event) {
      this.appModel.setCount(this.appModel.getCount() + 1L);
      this.continueLogin();
   }

   @FXML
   public void handleResolve() {
      this.appModel.setRefreshToken(null);
      this.appModel.setLoginThresholdDate(null);
      this.appModel.setRestartRequired(true);
      Window window = this.oAuth2LoginPanel.getScene().getWindow();
      window.fireEvent(new WindowEvent(window, WindowEvent.WINDOW_CLOSE_REQUEST));
   }
}
