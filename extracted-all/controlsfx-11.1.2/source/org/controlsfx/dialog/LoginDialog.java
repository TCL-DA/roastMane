package org.controlsfx.dialog;

import impl.org.controlsfx.i18n.Localization;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import javafx.util.Pair;
import org.controlsfx.control.textfield.CustomPasswordField;
import org.controlsfx.control.textfield.CustomTextField;
import org.controlsfx.control.textfield.TextFields;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;

public class LoginDialog extends Dialog<Pair<String, String>> {
   private final ButtonType loginButtonType;
   private final CustomTextField txUserName;
   private final CustomPasswordField txPassword;

   public LoginDialog(Pair<String, String> initialUserInfo, Callback<Pair<String, String>, Void> authenticator) {
      DialogPane dialogPane = this.getDialogPane();
      this.setTitle(Localization.getString("login.dlg.title"));
      dialogPane.setHeaderText(Localization.getString("login.dlg.header"));
      dialogPane.getStyleClass().add("login-dialog");
      dialogPane.getStylesheets().add(LoginDialog.class.getResource("dialogs.css").toExternalForm());
      dialogPane.getButtonTypes().addAll(new ButtonType[]{ButtonType.CANCEL});
      this.txUserName = (CustomTextField)TextFields.createClearableTextField();
      this.txUserName.setLeft(new ImageView(LoginDialog.class.getResource("/org/controlsfx/dialog/user.png").toExternalForm()));
      this.txPassword = (CustomPasswordField)TextFields.createClearablePasswordField();
      this.txPassword.setLeft(new ImageView(LoginDialog.class.getResource("/org/controlsfx/dialog/lock.png").toExternalForm()));
      Label lbMessage = new Label("");
      lbMessage.getStyleClass().addAll(new String[]{"message-banner"});
      lbMessage.setVisible(false);
      lbMessage.setManaged(false);
      VBox content = new VBox(10.0);
      content.getChildren().add(lbMessage);
      content.getChildren().add(this.txUserName);
      content.getChildren().add(this.txPassword);
      dialogPane.setContent(content);
      this.loginButtonType = new ButtonType(Localization.getString("login.dlg.login.button"), ButtonData.OK_DONE);
      dialogPane.getButtonTypes().addAll(new ButtonType[]{this.loginButtonType});
      Button loginButton = (Button)dialogPane.lookupButton(this.loginButtonType);
      loginButton.setOnAction(actionEvent -> {
         try {
            if (authenticator != null) {
               authenticator.call(new Pair(this.txUserName.getText(), this.txPassword.getText()));
            }

            lbMessage.setVisible(false);
            lbMessage.setManaged(false);
            this.hide();
         } catch (Throwable var5x) {
            lbMessage.setVisible(true);
            lbMessage.setManaged(true);
            lbMessage.setText(var5x.getMessage());
            var5x.printStackTrace();
         }
      });
      String userNameCation = Localization.getString("login.dlg.user.caption");
      String passwordCaption = Localization.getString("login.dlg.pswd.caption");
      this.txUserName.setPromptText(userNameCation);
      this.txUserName.setText(initialUserInfo == null ? "" : (String)initialUserInfo.getKey());
      this.txPassword.setPromptText(passwordCaption);
      this.txPassword.setText(new String(initialUserInfo == null ? "" : (String)initialUserInfo.getValue()));
      ValidationSupport validationSupport = new ValidationSupport();
      Platform.runLater(() -> {
         String requiredFormat = "'%s' is required";
         validationSupport.registerValidator(this.txUserName, Validator.createEmptyValidator(String.format(requiredFormat, userNameCation)));
         validationSupport.registerValidator(this.txPassword, Validator.createEmptyValidator(String.format(requiredFormat, passwordCaption)));
         this.txUserName.requestFocus();
      });
      this.setResultConverter(dialogButton -> dialogButton == this.loginButtonType ? new Pair(this.txUserName.getText(), this.txPassword.getText()) : null);
   }
}
