package com.cropster.roastingintelligence.ui.layout;

import com.cropster.roastingintelligence.ui.JavaFxUtil;
import com.cropster.roastingintelligence.ui.ToastNotificationController;
import com.cropster.roastingintelligence.ui.components.MessageBox;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Date;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.scene.text.TextFlow;
import org.cropster.roastingintelligence.Defaults;
import org.cropster.roastingintelligence.RoastingIntelligenceApp;

public class SystemSidebar extends DetachableSidebar implements Initializable {
   @FXML
   private Label statusTitle;
   @FXML
   private Label connectionStatus;
   @FXML
   private Label synchronizationStatus;
   @FXML
   private Label ambientTemperatureStatus;
   @FXML
   private MessageBox errorStatusMessage;
   @FXML
   private MessageBox warningStatusMessage;
   @FXML
   private Label loggedInUser;
   @FXML
   private Label lastLoginDate;
   @FXML
   private TextFlow userPerformanceNoteTextFlow;
   @FXML
   private Label roastedBatches;
   @FXML
   private Label roastedBatchesCaption;
   @FXML
   private Label abortedRoasts;
   @FXML
   private Label abortedRoastsCaption;
   @FXML
   private Label weightOutliers;
   @FXML
   private Label weightOutliersCaption;
   @FXML
   private Label updateTitle;
   @FXML
   private TextFlow currentVersionTextFlow;
   @FXML
   private Label updateDescription;
   @FXML
   private Button updateActionButton;
   @FXML
   private ProgressBar progressBar;
   @FXML
   private HBox updateSuspendedTag;
   @FXML
   private HBox updateNotFoundTag;

   public SystemSidebar() {
      JavaFxUtil.loadComponent(this);
   }

   @Override
   public void initialize(URL location, ResourceBundle resources) {
      super.initialize(location, resources);
      this.visibleProperty().addListener((s, o, n) -> {
         if (n) {
            ToastNotificationController tnc = RoastingIntelligenceApp.retrieveSingleton(ToastNotificationController.class);
            if (tnc != null) {
               tnc.hide("system-status-connection-important-tag");
               tnc.hide("synchronization-tag");
            }
         }
      });
      this.lastLoginDate.setText(MessageFormat.format("({0})", Defaults.PRINT_DATE_FORMATTER.getFormatter().format(new Date())));
      String note = "<span style=\"-fx-fill: -fx-color-text-muted;\">"
         + org.cropster.roastingintelligence.util.Messages.m_("We track these statistics to enhance your experience and ensure compliance.")
         + "</span> <a href=\"https://www.cropster.com/terms-of-service/\">"
         + org.cropster.roastingintelligence.util.Messages.m_("Visit our Terms of Service")
         + "</a>.";
      this.userPerformanceNoteTextFlow.getChildren().addAll(JavaFxUtil.parseHtmlToTextNodes(note).toArray(Node[]::new));
      this.roastedBatchesCaption.setText(org.cropster.roastingintelligence.util.Messages.m_("Roasted\nbatches"));
      this.abortedRoastsCaption.setText(org.cropster.roastingintelligence.util.Messages.m_("Aborted\nroasts"));
      this.weightOutliersCaption.setText(org.cropster.roastingintelligence.util.Messages.m_("Weight\noutliers"));
      Platform.runLater(() -> {
         this.roastedBatchesCaption.setLineSpacing(-2.0);
         this.abortedRoastsCaption.setLineSpacing(-2.0);
         this.weightOutliersCaption.setLineSpacing(-2.0);
      });
      JavaFxUtil.configureTooltips(this);
      JavaFxUtil.setDefaultLineSpacing(this);
   }

   public Label getRoastedBatches() {
      return this.roastedBatches;
   }

   public Label getAbortedRoasts() {
      return this.abortedRoasts;
   }

   public Label getWeightOutliers() {
      return this.weightOutliers;
   }

   public Label getConnectionStatus() {
      return this.connectionStatus;
   }

   public Label getAmbientTemperatureStatus() {
      return this.ambientTemperatureStatus;
   }

   public MessageBox getErrorStatusMessage() {
      return this.errorStatusMessage;
   }

   public Label getStatusTitle() {
      return this.statusTitle;
   }

   public Label getLoggedInUser() {
      return this.loggedInUser;
   }

   public Label getSynchronizationStatus() {
      return this.synchronizationStatus;
   }

   public MessageBox getWarningStatusMessage() {
      return this.warningStatusMessage;
   }

   public Label getUpdateTitle() {
      return this.updateTitle;
   }

   public TextFlow getCurrentVersionTextFlow() {
      return this.currentVersionTextFlow;
   }

   public Label getUpdateDescription() {
      return this.updateDescription;
   }

   public Button getUpdateActionButton() {
      return this.updateActionButton;
   }

   public ProgressBar getProgressBar() {
      return this.progressBar;
   }

   public HBox getUpdateSuspendedTag() {
      return this.updateSuspendedTag;
   }

   public HBox getUpdateNotFoundTag() {
      return this.updateNotFoundTag;
   }
}
