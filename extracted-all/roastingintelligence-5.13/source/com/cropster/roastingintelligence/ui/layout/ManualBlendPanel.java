package com.cropster.roastingintelligence.ui.layout;

import com.cropster.roastingintelligence.model.notification.Notification;
import com.cropster.roastingintelligence.model.notification.NotificationType;
import com.cropster.roastingintelligence.service.NotificationBusService;
import com.cropster.roastingintelligence.ui.JavaFxUtil;
import com.cropster.roastingintelligence.ui.ToastNotificationController;
import com.cropster.roastingintelligence.ui.components.MeasureEditor;
import com.cropster.roastingintelligence.ui.util.ActionBuilder;
import com.cropster.roastingintelligence.ui.util.DirtyListener;
import com.cropster.roastingintelligence.ui.util.PlatformScheduler;
import com.cropster.roastingintelligence.ui.util.Validators;
import com.cropster.util.model.Unit;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.Map.Entry;
import java.util.function.Consumer;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanExpression;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.BooleanPropertyBase;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.apache.commons.lang3.StringUtils;
import org.controlsfx.control.action.Action;
import org.cropster.roastingintelligence.AppModel;
import org.cropster.roastingintelligence.RoastingIntelligenceApp;
import org.cropster.roastingintelligence.model.Lot;
import org.cropster.roastingintelligence.model.LotTransaction;
import org.cropster.roastingintelligence.model.Measure;
import org.cropster.roastingintelligence.model.Roast;
import org.cropster.roastingintelligence.remote.CsarConnection;

public class ManualBlendPanel extends AbstractBlendPanel {
   public static final String MANUAL_BLEND_WINDOW_ID = "MANUAL_BLEND_WINDOW_ID";
   @FXML
   private Label availableNameChars;
   @FXML
   private TextField nameEditor;
   @FXML
   private Button addAllAvailableWeightButton;
   @FXML
   private Label totalWeightLabel;
   @FXML
   private Label availableNoteChars;
   @FXML
   private TextArea noteEditor;
   @FXML
   private Button openDifferentBlendDialog;
   private final DirtyListener<String> nameEditorDirty = new DirtyListener<>(null);
   private final BooleanProperty weightsValid = new SimpleBooleanProperty();

   public static void showWindow(Node owner, CsarConnection connection, List<Roast> roastsToBlend, AppModel appModel, Consumer<Lot> showBlendListView) {
      showWindow(owner, connection, roastsToBlend, appModel, showBlendListView, null);
   }

   public static void showWindow(
      Node owner, CsarConnection connection, List<Roast> roastsToBlend, AppModel appModel, Consumer<Lot> showBlendListView, String initialName
   ) {
      Objects.requireNonNull(connection, "Null 'connection' parameter");
      Objects.requireNonNull(roastsToBlend, "Null 'roastsToBlend' parameter");
      if (roastsToBlend.size() < 2) {
         throw new IllegalArgumentException("At least two roasts are required to blend");
      } else {
         Objects.requireNonNull(connection, "Null 'connection' parameter");
         Objects.requireNonNull(showBlendListView, "Null 'showBlendListView' parameter");
         hideWindow();
         ManualBlendPanel blendPanel = new ManualBlendPanel(showBlendListView);
         blendPanel.setRoastsToBlend(connection, roastsToBlend, appModel);
         if (StringUtils.isNotBlank(initialName)) {
            blendPanel.setName(initialName);
         }

         showWindow(
            org.cropster.roastingintelligence.util.Messages.m_("Manually blend selected roasts"), owner, blendPanel, e -> blendPanel.cancelLoadAndCloseWindow()
         );
      }
   }

   private static void hideWindow() {
      hideWindow("MANUAL_BLEND_WINDOW_ID");
   }

   public ManualBlendPanel(Consumer<Lot> showBlendListView) {
      super(showBlendListView, "MANUAL_BLEND_WINDOW_ID");
      JavaFxUtil.loadComponent(this);
   }

   @Override
   public void initialize(URL location, ResourceBundle resources) {
      super.initialize(location, resources);
      this.nameEditor.getProperties().put("restoreOldValueOnError", Boolean.FALSE);
      Validators.Validator nameValidator = Validators.setValidator(this.nameEditor, false, false, 1, 200);
      JavaFxUtil.createLengthOverrunProperty(200, this.nameEditor, this.availableNameChars);
      this.nameEditor.setTextFormatter(JavaFxUtil.createLengthLimitingTextFormatter(200));
      JavaFxUtil.createLengthOverrunProperty(200, this.noteEditor, this.availableNoteChars);
      this.noteEditor.setTextFormatter(JavaFxUtil.createLengthLimitingTextFormatter(200));
      this.nameEditorDirty.setOriginalValue(this.nameEditor.getText());
      this.nameEditor.textProperty().addListener(this.nameEditorDirty);
      this.createButton
         .disableProperty()
         .bind(this.dirty.not().or(this.weightsValid.not()).or(nameValidator.highlightProperty().isNotEqualTo(Validators.Status.NONE)));
      JavaFxUtil.configureTooltips(this);
      JavaFxUtil.setLineSpacing(0.125, this);
   }

   @Override
   protected void showWindowAgain(Node owner) {
      showWindow(owner, this.connection, this.roastsToBlend, this.appModel, this.showBlendListView);
   }

   @Override
   protected void createBlend() {
      Task<Lot> createBlendTask = new Task<Lot>() {
         protected Lot call() {
            return ManualBlendPanel.this.connection
               .createManualBlend(
                  ManualBlendPanel.this.nameEditor.getText(),
                  ManualBlendPanel.this.noteEditor.getText(),
                  ManualBlendPanel.this.roastComponents
                     .stream()
                     .filter(rc -> rc.editor().getValue().doubleValue() > 0.0)
                     .map(rc -> new LotTransaction(rc.lot().getId(), rc.editor().getMeasure()))
                     .toList()
               );
         }
      };
      createBlendTask.setOnCancelled(this::showCreationCancelledMessage);
      createBlendTask.setOnFailed(this::showCreationFailedMessage);
      createBlendTask.setOnSucceeded(this::showCreationSucceededMessage);
      PlatformScheduler.executeBackgroundTask(createBlendTask);
   }

   private void showCreationCancelledMessage(WorkerStateEvent workerStateEvent) {
      NotificationBusService nbs = RoastingIntelligenceApp.retrieveSingleton(NotificationBusService.class);
      if (nbs != null) {
         nbs.publish(
            Notification.builder(NotificationType.WARNING)
               .withTitle(org.cropster.roastingintelligence.util.Messages.m_("The blend could not be created"))
               .withMessage(
                  org.cropster.roastingintelligence.util.Messages.m_(
                     "Creation of blend “{0}” cancelled: ", this.nameEditor.getText(), workerStateEvent.getSource().getException().getMessage()
                  )
               )
               .build(),
            "blend-created"
         );
      }
   }

   private void showCreationFailedMessage(WorkerStateEvent workerStateEvent) {
      NotificationBusService nbs = RoastingIntelligenceApp.retrieveSingleton(NotificationBusService.class);
      if (nbs != null) {
         nbs.publish(
            Notification.builder(NotificationType.ERROR)
               .withTitle(org.cropster.roastingintelligence.util.Messages.m_("The blend could not be created"))
               .withMessage(
                  org.cropster.roastingintelligence.util.Messages.m_(
                     "Blend “{0}” cannot be created: {1}", this.nameEditor.getText(), workerStateEvent.getSource().getException().getMessage()
                  )
               )
               .build(),
            "blend-created"
         );
      }
   }

   private void showCreationSucceededMessage(WorkerStateEvent workerStateEvent) {
      NotificationBusService nbs = RoastingIntelligenceApp.retrieveSingleton(NotificationBusService.class);
      if (nbs != null) {
         Lot createdBlend = (Lot)((Task)workerStateEvent.getSource()).getValue();
         Action showBlendAction = new ActionBuilder(
               org.cropster.roastingintelligence.util.Messages.m_("Show created blends"), e -> this.showBlendListView.accept(createdBlend)
            )
            .build();
         nbs.publish(
            Notification.builder(NotificationType.SUCCESS)
               .withTitle(org.cropster.roastingintelligence.util.Messages.m_("The blend was successfully created"))
               .withMessage(
                  org.cropster.roastingintelligence.util.Messages.m_("The blend “{0}” was created and uploaded to our servers", this.nameEditor.getText())
               )
               .withProperty("blend-lot", createdBlend)
               .withProperty("tag", "blends-tag")
               .withProperty("action", showBlendAction)
               .withProperty("hide-after", ToastNotificationController.TOAST_NOTIFICATION_DURATION)
               .build(),
            "blend-created"
         );
      }
   }

   @Override
   protected void setRoastsToBlend(CsarConnection connection, List<Roast> roastsToBlend, AppModel appModel) {
      super.setRoastsToBlend(connection, roastsToBlend, appModel);
      this.loadLotsAsynchronously(connection, this::showContentPane);
   }

   private void setName(String name) {
      this.nameEditor.setText(name);
   }

   private void showContentPane(WorkerStateEvent e) {
      Map<Roast, Lot> loadedLots = (Map<Roast, Lot>)this.loadLotsTask.getValue();
      this.populateContentPanel(loadedLots);
      this.infoMessageBox.setVisible(false);
      this.openDifferentBlendDialog.setVisible(true);
      this.sizeWindowToScene();
      if (StringUtils.isBlank(this.nameEditor.getText())) {
         long profiles = this.roastsToBlend.stream().mapToLong(Roast::getProfile).distinct().count();
         if (profiles == 1L) {
            String profileName = this.appModel.getData().getProfiles().get(this.roastsToBlend.get(0).getProfile()).getName();
            this.nameEditor.setText(profileName);
         }
      }

      this.nameEditor.requestFocus();
   }

   private void populateContentPanel(Map<Roast, Lot> roastLotMap) {
      int row = 1;
      DoubleProperty totalWeight = new SimpleDoubleProperty(0.0);

      for (Entry<Roast, Lot> entry : roastLotMap.entrySet()) {
         Roast roast = entry.getKey();
         Lot lot = entry.getValue();
         this.populateRow(row++, roast, lot, totalWeight);
      }

      List<MeasureEditor> editors = this.roastComponents.stream().map(AbstractBlendPanel.RoastComponent::editor).toList();
      List<DirtyListener<String>> dirties = this.roastComponents.stream().map(AbstractBlendPanel.RoastComponent::dirtyListener).toList();
      List<Validators.Validator> validators = this.roastComponents.stream().map(AbstractBlendPanel.RoastComponent::validator).toList();
      totalWeight.bind(
         Bindings.createDoubleBinding(
            () -> editors.stream().filter(e -> e.getValue() != null).mapToDouble(e -> e.getValue().doubleValue()).sum(),
            (Observable[])editors.stream().map(MeasureEditor::valueProperty).toArray(ObjectProperty[]::new)
         )
      );
      this.totalWeightLabel.textProperty().bind(Bindings.createStringBinding(() -> {
         Unit unit = roastLotMap.values().iterator().next().getWeight().getUnit();
         double total = totalWeight.get();
         return total > 0.0 ? FORMAT.format(new Measure(total, unit)) : "–";
      }, new Observable[]{totalWeight}));
      if (!dirties.isEmpty()) {
         this.dirty
            .bind(
               Bindings.createBooleanBinding(
                  () -> dirties.stream().filter(BooleanPropertyBase::get).count() > 1L, (Observable[])dirties.toArray(SimpleBooleanProperty[]::new)
               )
            );
      }

      if (!validators.isEmpty()) {
         BooleanExpression allValid = validators.get(0).highlightProperty().isEqualTo(Validators.Status.NONE);

         for (int i = 1; i < validators.size(); i++) {
            allValid = allValid.and(validators.get(i).highlightProperty().isEqualTo(Validators.Status.NONE));
         }

         this.weightsValid.bind(allValid);
      }
   }

   @FXML
   public void onBlendWithProfile(ActionEvent ignoredEvent) {
      Node owner = (Node)this.getScene().getWindow().getProperties().get("original-owner");
      this.cancelLoadAndCloseWindow();
      BlendWithProfilePanel.showWindow(owner, this.connection, this.roastsToBlend, this.appModel, this.showBlendListView);
   }

   @FXML
   private void addAllAvailableWeight() {
      this.roastComponents.stream().map(AbstractBlendPanel.RoastComponent::addAvailabeButton).forEach(Button::fire);
   }
}
