package com.cropster.roastingintelligence.ui.layout;

import com.cropster.roastingintelligence.model.notification.Notification;
import com.cropster.roastingintelligence.model.notification.NotificationType;
import com.cropster.roastingintelligence.service.NotificationBusService;
import com.cropster.roastingintelligence.ui.JavaFxUtil;
import com.cropster.roastingintelligence.ui.ToastNotificationController;
import com.cropster.roastingintelligence.ui.components.MeasureEditor;
import com.cropster.roastingintelligence.ui.components.MessageBox;
import com.cropster.roastingintelligence.ui.util.ActionBuilder;
import com.cropster.roastingintelligence.ui.util.DirtyListener;
import com.cropster.roastingintelligence.ui.util.PlatformScheduler;
import com.cropster.roastingintelligence.ui.util.RendererListCell;
import com.cropster.roastingintelligence.ui.util.Validators;
import com.cropster.util.model.Unit;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanExpression;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.BooleanPropertyBase;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Separator;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.util.Callback;
import org.apache.commons.lang3.Validate;
import org.controlsfx.control.action.Action;
import org.cropster.roastingintelligence.AppModel;
import org.cropster.roastingintelligence.RoastingIntelligenceApp;
import org.cropster.roastingintelligence.model.BlendProfile;
import org.cropster.roastingintelligence.model.BlendProfileComponent;
import org.cropster.roastingintelligence.model.Lot;
import org.cropster.roastingintelligence.model.LotTransaction;
import org.cropster.roastingintelligence.model.Measure;
import org.cropster.roastingintelligence.model.Profile;
import org.cropster.roastingintelligence.model.ProfileGroup;
import org.cropster.roastingintelligence.model.Roast;
import org.cropster.roastingintelligence.remote.CsarConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BlendWithProfilePanel extends AbstractBlendPanel {
   public static final String BLEND_WITH_PROFILE_WINDOW_ID = "BLEND_WITH_PROFILE_WINDOW_ID";
   @FXML
   private MessageBox noProfileMessageBox;
   @FXML
   private ComboBox<BlendProfile> profileSelector;
   @FXML
   private Button addAllAvailableWeightButton;
   @FXML
   private MeasureEditor totalWeightEditor;
   @FXML
   private Label availableNoteChars;
   @FXML
   private TextArea noteEditor;
   @FXML
   private Button openDifferentBlendDialog;
   private final Logger logger = LoggerFactory.getLogger(BlendWithProfilePanel.class);
   private final BooleanProperty weightsValid = new SimpleBooleanProperty();
   private Task<List<BlendProfile>> loadBlendProfilesTask;
   private double maximumTotalWeightAllowed;

   public static void showWindow(Node owner, CsarConnection connection, List<Roast> roastsToBlend, AppModel appModel, Consumer<Lot> showBlendListView) {
      Objects.requireNonNull(connection, "Null 'connection' parameter");
      Objects.requireNonNull(roastsToBlend, "Null 'roastsToBlend' parameter");
      if (roastsToBlend.size() < 2) {
         throw new IllegalArgumentException("At least two roasts are required to blend");
      } else {
         Objects.requireNonNull(connection, "Null 'connection' parameter");
         Objects.requireNonNull(showBlendListView, "Null 'showBlendListView' parameter");
         hideWindow();
         BlendWithProfilePanel blendPanel = new BlendWithProfilePanel(showBlendListView);
         blendPanel.setRoastsToBlend(connection, roastsToBlend, appModel);
         showWindow(org.cropster.roastingintelligence.util.Messages.m_("Blend with profile"), owner, blendPanel, e -> blendPanel.cancelLoadAndCloseWindow());
      }
   }

   private static void hideWindow() {
      hideWindow("BLEND_WITH_PROFILE_WINDOW_ID");
   }

   public BlendWithProfilePanel(Consumer<Lot> showBlendListView) {
      super(showBlendListView, "BLEND_WITH_PROFILE_WINDOW_ID");
      JavaFxUtil.loadComponent(this);
   }

   @Override
   public void initialize(URL location, ResourceBundle resources) {
      super.initialize(location, resources);
      this.headerPanel.visibleProperty().unbind();
      this.headerPanel
         .visibleProperty()
         .bind(Bindings.or(this.infoMessageBox.visibleProperty(), this.warningMessageBox.visibleProperty().or(this.noProfileMessageBox.visibleProperty())));
      this.contentPanel.visibleProperty().unbind();
      this.contentPanel
         .visibleProperty()
         .bind(
            Bindings.and(this.infoMessageBox.visibleProperty().not(), this.warningMessageBox.visibleProperty().not())
               .and(this.noProfileMessageBox.visibleProperty().not())
         );
      JavaFxUtil.createLengthOverrunProperty(200, this.noteEditor, this.availableNoteChars);
      this.noteEditor.setTextFormatter(JavaFxUtil.createLengthLimitingTextFormatter(200));
      Callback<ListView<BlendProfile>, ListCell<BlendProfile>> cellFactory = lv -> new RendererListCell<>(bp -> bp.getName());
      this.profileSelector.setCellFactory(cellFactory);
      this.profileSelector.setButtonCell((ListCell)cellFactory.call(null));
      JavaFxUtil.configureTooltips(this);
      JavaFxUtil.setLineSpacing(0.125, this);
   }

   @Override
   protected void cancelLoadTask() {
      super.cancelLoadTask();
      if (this.loadBlendProfilesTask != null && this.loadBlendProfilesTask.isRunning()) {
         this.loadBlendProfilesTask.cancel();
      }
   }

   @Override
   protected void setRoastsToBlend(CsarConnection connection, List<Roast> roastsToBlend, AppModel appModel) {
      super.setRoastsToBlend(connection, roastsToBlend, appModel);
      this.loadLotsAsynchronously(connection, e -> this.loadBlendProfilesAsynchronously(connection));
   }

   private void loadBlendProfilesAsynchronously(final CsarConnection connection) {
      this.loadBlendProfilesTask = new Task<List<BlendProfile>>() {
         protected List<BlendProfile> call() throws Exception {
            List<BlendProfile> blenProfilesList = new ArrayList<>();

            try {
               Map<Long, Profile> profiles = BlendWithProfilePanel.this.appModel.getData().getProfiles();
               List<Long> groupsInRoasts = BlendWithProfilePanel.this.roastsToBlend
                  .stream()
                  .map(r -> profiles.get(r.getProfile()))
                  .filter(Objects::nonNull)
                  .map(Profile::getProfileGroup)
                  .filter(Objects::nonNull)
                  .map(ProfileGroup::getId)
                  .distinct()
                  .toList();

               for (BlendProfile bp : connection.getBlendProfiles(BlendWithProfilePanel.this.appModel.getSelectedMachine().getGroup().getCode())) {
                  Collection<BlendProfileComponent> bpc = bp.getBlendProfileComponents();
                  if (bpc != null && !bpc.isEmpty()) {
                     List<Long> profileGroupIds = bpc.stream()
                        .map(BlendProfileComponent::getProfileGroup)
                        .filter(Objects::nonNull)
                        .map(ProfileGroup::getId)
                        .filter(Objects::nonNull)
                        .distinct()
                        .toList();
                     if (groupsInRoasts.size() == profileGroupIds.size() && groupsInRoasts.stream().allMatch(profileGroupIds::contains)) {
                        blenProfilesList.add(bp);
                     }
                  }
               }

               if (blenProfilesList.isEmpty()) {
                  throw new BlendWithProfilePanel.NoMatchingProfileException();
               } else {
                  return blenProfilesList;
               }
            } catch (Exception var9) {
               if (this.isCancelled()) {
                  return blenProfilesList;
               } else {
                  throw var9;
               }
            }
         }
      };
      this.loadBlendProfilesTask.setOnCancelled(this::cancelLoadAndCloseWindow);
      this.loadBlendProfilesTask.setOnFailed(this::showWarningMessageBox);
      this.loadBlendProfilesTask.setOnSucceeded(this::showContentPane);
      PlatformScheduler.executeBackgroundTask(this.loadBlendProfilesTask);
   }

   @Override
   protected void showWarningMessageBox(WorkerStateEvent e) {
      if (this.loadBlendProfilesTask != null
         && this.loadBlendProfilesTask.exceptionProperty().get() instanceof BlendWithProfilePanel.NoMatchingProfileException) {
         this.noProfileMessageBox.setVisible(true);
      } else {
         this.warningMessageBox.setVisible(true);
         this.logger.warn("Error loading blend profiles", e.getSource().getException());
      }

      this.infoMessageBox.setVisible(false);
      this.sizeWindowToScene();
   }

   private void showContentPane(WorkerStateEvent e) {
      this.populateChoiceBox((Map<Roast, Lot>)this.loadLotsTask.getValue(), (List<BlendProfile>)this.loadBlendProfilesTask.getValue());
      this.infoMessageBox.setVisible(false);
      this.openDifferentBlendDialog.setVisible(true);
      this.profileSelector.getSelectionModel().selectFirst();
      this.profileSelector.requestFocus();
   }

   private void populateChoiceBox(Map<Roast, Lot> roastLotMap, List<BlendProfile> blendProfileList) {
      this.profileSelector.setItems(FXCollections.observableList(blendProfileList));
      this.profileSelector.setOnAction(event -> {
         for (int r = this.blendTable.getRowConstraints().size() - 1; r > 1; r--) {
            this.blendTable.getRowConstraints().remove(r);
         }

         for (int i = this.blendTable.getChildren().size() - 1; i > 3; i--) {
            this.blendTable.getChildren().remove(i);
         }

         BlendProfile selectedProfile = (BlendProfile)this.profileSelector.getSelectionModel().getSelectedItem();
         this.populateContentPanel(roastLotMap, selectedProfile);
         this.sizeWindowToScene();
      });
   }

   private void populateContentPanel(Map<Roast, Lot> roastLotMap, BlendProfile selectedProfile) {
      int row = 1;
      DoubleProperty totalWeight = new SimpleDoubleProperty(0.0);
      Map<Long, Profile> profiles = this.appModel.getData().getProfiles();
      Collection<BlendProfileComponent> components = selectedProfile.getBlendProfileComponents();
      List<BlendWithProfilePanel.ProfileGroupSummary> profileGroupSummaries = new ArrayList<>(components.size());

      for (BlendProfileComponent component : components) {
         if (row > 1) {
            this.populateSeparatorRow(row++);
         }

         int groupRow = row++;
         ProfileGroup profileGroup = component.getProfileGroup();
         List<AbstractBlendPanel.RoastComponent> groupComponents = new ArrayList<>();

         for (Entry<Roast, Lot> entry : roastLotMap.entrySet()) {
            Roast roast = entry.getKey();
            Profile profile = profiles.get(roast.getProfile());
            if (profile != null && profile.getProfileGroup() != null && profile.getProfileGroup().getId().equals(profileGroup.getId())) {
               Lot lot = entry.getValue();
               groupComponents.add(this.populateRow(row++, roast, lot, totalWeight));
            }
         }

         profileGroupSummaries.add(this.populateGroupRow(groupRow, component, groupComponents));
      }

      this.totalWeightEditor.setConsumeEnterKeyEvent(true);
      this.totalWeightEditor.setMeasure(new Measure(0.0, this.roastComponents.get(0).editor().getUnit()));
      this.maximumTotalWeightAllowed = this.getMaximumTotalWeight(profileGroupSummaries);
      Validators.Validator totalWeightValidator = Validators.setValidator(this.totalWeightEditor, this.maximumTotalWeightAllowed);
      Supplier<String> errorMessageProvider = () -> org.cropster.roastingintelligence.util.Messages.m_(
            "Not enough weight available based on blend ratios. Maximum total weight permitted is {0} {1}.",
            this.maximumTotalWeightAllowed,
            org.cropster.roastingintelligence.util.Messages.m_(this.totalWeightEditor.getUnit())
         );
      this.totalWeightEditor.getProperties().put("restoreOldValueOnError", Boolean.FALSE);
      totalWeightValidator.setErrorMessageProvider(errorMessageProvider);
      this.totalWeightEditor.getInput().focusedProperty().addListener((s, o, n) -> {
         if (n != null && n) {
            this.totalWeightEditor.getProperties().put("restoreOldValueOnError", Boolean.TRUE);
            totalWeightValidator.setErrorMessageProvider(null);
         } else {
            this.totalWeightEditor.getProperties().put("restoreOldValueOnError", Boolean.FALSE);
            totalWeightValidator.setErrorMessageProvider(errorMessageProvider);
         }
      });
      AtomicBoolean locked = new AtomicBoolean(false);
      this.totalWeightEditor.valueProperty().addListener((s, o, n) -> this.computeValues(n, profileGroupSummaries, -1, locked));

      for (int i = 0; i < profileGroupSummaries.size(); i++) {
         int groupIndex = i;
         BlendWithProfilePanel.ProfileGroupSummary pgs = profileGroupSummaries.get(groupIndex);
         pgs.usedWeight().addListener((s, o, n) -> this.computeValues(n, profileGroupSummaries, groupIndex, locked));
      }

      List<DirtyListener<String>> dirties = this.roastComponents.stream().map(AbstractBlendPanel.RoastComponent::dirtyListener).toList();
      if (!dirties.isEmpty()) {
         this.dirty
            .bind(
               Bindings.createBooleanBinding(
                  () -> dirties.stream().filter(BooleanPropertyBase::get).count() > 1L, (Observable[])dirties.toArray(SimpleBooleanProperty[]::new)
               )
            );
      }

      List<Validators.Validator> validators = this.roastComponents.stream().map(AbstractBlendPanel.RoastComponent::validator).toList();
      if (!validators.isEmpty()) {
         BooleanExpression allValid = validators.get(0).highlightProperty().isEqualTo(Validators.Status.NONE);

         for (int i = 1; i < validators.size(); i++) {
            allValid = allValid.and(validators.get(i).highlightProperty().isEqualTo(Validators.Status.NONE));
         }

         this.weightsValid.bind(allValid);
      }

      this.createButton
         .disableProperty()
         .bind(this.dirty.not().or(this.weightsValid.not()).or(totalWeightValidator.highlightProperty().isNotEqualTo(Validators.Status.NONE)));
   }

   private void computeValues(Number newValue, List<BlendWithProfilePanel.ProfileGroupSummary> profileGroupSummaries, int groupIndex, AtomicBoolean locked) {
      Objects.requireNonNull(newValue, "Null 'newValue' parameter");
      Validate.inclusiveBetween(-1, profileGroupSummaries.size() - 1, groupIndex, "Invalid 'groupIndex' parameter: %d", new Object[]{groupIndex});
      if (locked.compareAndSet(false, true)) {
         try {
            double newTTotalWeight = newValue.doubleValue();
            if (groupIndex >= 0) {
               newTTotalWeight /= profileGroupSummaries.get(groupIndex).component().getPercentage().doubleValue();
               this.totalWeightEditor.setValue(newTTotalWeight);
            }

            this.computeValues(newTTotalWeight, groupIndex, profileGroupSummaries);
         } finally {
            locked.set(false);
         }
      }
   }

   private void computeValues(double newTTotalWeight, int groupIndex, List<BlendWithProfilePanel.ProfileGroupSummary> profileGroupSummaries) {
      for (int i = 0; i < profileGroupSummaries.size(); i++) {
         if (i != groupIndex) {
            BlendWithProfilePanel.ProfileGroupSummary pgs = profileGroupSummaries.get(i);
            double newUsedWeight = newTTotalWeight * pgs.component().getPercentage().doubleValue();

            for (AbstractBlendPanel.RoastComponent rc : pgs.groupComponents()) {
               double availableWeight = rc.lot().getWeight().getAmount();
               double newWeight = Math.min(availableWeight, newUsedWeight);
               rc.editor().setValue(newWeight);
               newUsedWeight = Math.max(0.0, newUsedWeight - newWeight);
            }
         }
      }
   }

   private double getMaximumTotalWeight(List<BlendWithProfilePanel.ProfileGroupSummary> profileGroupSummaries) {
      double[] maximumTotalWeight = new double[]{9999999.999};
      profileGroupSummaries.forEach(pgs -> {
         double availableWeight = pgs.availableGroupWeight().getAmount();
         double ratio = pgs.component().getPercentage().doubleValue();
         double possibleTotalWeight = availableWeight / ratio;
         if (possibleTotalWeight < maximumTotalWeight[0]) {
            maximumTotalWeight[0] = possibleTotalWeight;
         }
      });
      return maximumTotalWeight[0];
   }

   private BlendWithProfilePanel.ProfileGroupSummary populateGroupRow(
      int row, BlendProfileComponent bpc, List<AbstractBlendPanel.RoastComponent> groupComponents
   ) {
      RowConstraints rowConstraints = new RowConstraints();
      rowConstraints.setValignment(VPos.CENTER);
      rowConstraints.setVgrow(Priority.NEVER);
      this.blendTable.getRowConstraints().add(row, rowConstraints);
      Label groupLabel = new Label(bpc.getProfileGroup().getName());
      GridPane.setColumnIndex(groupLabel, 0);
      GridPane.setRowIndex(groupLabel, row);
      this.blendTable.getChildren().add(groupLabel);
      Label ratioLabel = new Label(PERCENTAGE_FORMAT.format(bpc.getPercentage()));
      GridPane.setColumnIndex(ratioLabel, 1);
      GridPane.setRowIndex(ratioLabel, row);
      this.blendTable.getChildren().add(ratioLabel);
      Unit unit = groupComponents.get(0).editor().getUnit();
      Measure availableGroupWeight = new Measure(groupComponents.stream().map(rc -> rc.lot().getWeight()).mapToDouble(Measure::getAmount).sum(), unit);
      Label availableWeightLabel = new Label(FORMAT.format(availableGroupWeight));
      GridPane.setColumnIndex(availableWeightLabel, 2);
      GridPane.setRowIndex(availableWeightLabel, row);
      this.blendTable.getChildren().add(availableWeightLabel);
      DoubleProperty usedGroupWeight = new SimpleDoubleProperty(0.0);
      usedGroupWeight.bind(
         Bindings.createDoubleBinding(
            () -> groupComponents.stream().mapToDouble(rc -> rc.editor().getMeasure().getAmount()).sum(),
            (Observable[])groupComponents.stream()
               .map(AbstractBlendPanel.RoastComponent::editor)
               .map(MeasureEditor::valueProperty)
               .toArray(ObjectProperty[]::new)
         )
      );
      Label usedWeightLabel = new Label();
      usedWeightLabel.textProperty()
         .bind(Bindings.createStringBinding(() -> FORMAT.format(new Measure(usedGroupWeight.get(), unit)), new Observable[]{usedGroupWeight}));
      GridPane.setColumnIndex(usedWeightLabel, 4);
      GridPane.setRowIndex(usedWeightLabel, row);
      this.blendTable.getChildren().add(usedWeightLabel);
      return new BlendWithProfilePanel.ProfileGroupSummary(bpc, availableGroupWeight, usedGroupWeight, groupComponents);
   }

   private void populateSeparatorRow(int row) {
      RowConstraints rowConstraints = new RowConstraints(17.0, 17.0, 17.0, Priority.NEVER, VPos.CENTER, false);
      this.blendTable.getRowConstraints().add(row, rowConstraints);
      Separator separator = new Separator();
      GridPane.setColumnIndex(separator, 0);
      GridPane.setColumnSpan(separator, 4);
      GridPane.setRowIndex(separator, row);
      this.blendTable.getChildren().add(separator);
   }

   @Override
   protected void createBlend() {
      Task<Lot> createBlendTask = new Task<Lot>() {
         protected Lot call() {
            return BlendWithProfilePanel.this.connection
               .createBlendWithProfile(
                  ((BlendProfile)BlendWithProfilePanel.this.profileSelector.getSelectionModel().getSelectedItem()).getId(),
                  BlendWithProfilePanel.this.noteEditor.getText(),
                  BlendWithProfilePanel.this.roastComponents
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
                     "Creation of blend with profile “{0}” cancelled: ",
                     ((BlendProfile)this.profileSelector.getSelectionModel().getSelectedItem()).getName(),
                     workerStateEvent.getSource().getException().getMessage()
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
                     "Blend with profile “{0}” cannot be created: {1}",
                     ((BlendProfile)this.profileSelector.getSelectionModel().getSelectedItem()).getName(),
                     workerStateEvent.getSource().getException().getMessage()
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
                  org.cropster.roastingintelligence.util.Messages.m_(
                     "The blend with profile “{0}” was created and uploaded to our servers",
                     ((BlendProfile)this.profileSelector.getSelectionModel().getSelectedItem()).getName()
                  )
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
   protected void showWindowAgain(Node owner) {
      showWindow(owner, this.connection, this.roastsToBlend, this.appModel, this.showBlendListView);
   }

   @FXML
   public void onBlendManually(ActionEvent ignoredEvent) {
      Node owner = (Node)this.getScene().getWindow().getProperties().get("original-owner");
      this.cancelLoadAndCloseWindow();
      ManualBlendPanel.showWindow(owner, this.connection, this.roastsToBlend, this.appModel, this.showBlendListView);
   }

   @FXML
   public void onBlendManuallyWithName(ActionEvent ignoredEvent) {
      Node owner = (Node)this.getScene().getWindow().getProperties().get("original-owner");
      this.cancelLoadAndCloseWindow();
      ManualBlendPanel.showWindow(
         owner,
         this.connection,
         this.roastsToBlend,
         this.appModel,
         this.showBlendListView,
         ((BlendProfile)this.profileSelector.getSelectionModel().getSelectedItem()).getName()
      );
   }

   @FXML
   private void addAllAvailableWeight() {
      this.totalWeightEditor.setValue(this.maximumTotalWeightAllowed);
   }

   private static class NoMatchingProfileException extends Exception {
      NoMatchingProfileException() {
         super("No matching post-roast blend profile found for the selected roasts.");
      }
   }

   protected static record ProfileGroupSummary(
      BlendProfileComponent component, Measure availableGroupWeight, DoubleProperty usedWeight, List<AbstractBlendPanel.RoastComponent> groupComponents
   ) {
   }
}
