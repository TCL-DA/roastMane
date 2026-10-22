package com.cropster.roastingintelligence.ui.components;

import com.cropster.roastingintelligence.model.notification.Notification;
import com.cropster.roastingintelligence.model.notification.NotificationType;
import com.cropster.roastingintelligence.service.NotificationBusService;
import com.cropster.roastingintelligence.ui.JavaFxUtil;
import com.cropster.roastingintelligence.ui.ToastNotificationController;
import com.cropster.roastingintelligence.ui.util.DirtyListener;
import com.cropster.roastingintelligence.ui.util.PlatformScheduler;
import com.cropster.roastingintelligence.ui.util.ProgressTask;
import com.cropster.roastingintelligence.ui.util.Validators;
import java.util.function.Consumer;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import local.OfflineBootstrap;
import org.apache.commons.lang3.StringUtils;
import org.cropster.roastingintelligence.AppModel;
import org.cropster.roastingintelligence.RoastingIntelligenceApp;
import org.cropster.roastingintelligence.model.Measure;
import org.cropster.roastingintelligence.model.Roast;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProductionEditor extends VBox {
   private static final int MAX_COMMENT_LENGTH = 250;
   @FXML
   private Label titleLabel;
   @FXML
   private MeasureEditor startWeightEditor;
   @FXML
   private MeasureEditor endWeightEditor;
   @FXML
   private MeasureEditor roastValue1Editor;
   @FXML
   private MeasureEditor roastValue2Editor;
   @FXML
   private Label availableChars;
   @FXML
   private TextArea notesEditor;
   @FXML
   private Button cancelButton;
   @FXML
   private Button saveButton;
   @FXML
   private Label updateLabel;
   private final Logger logger = LoggerFactory.getLogger(ProductionEditor.class);
   private final DirtyListener<String> roastValue1Dirty = new DirtyListener<>(null);
   private final DirtyListener<String> roastValue2Dirty = new DirtyListener<>(null);
   private final DirtyListener<String> notesDirty = new DirtyListener<>(null);
   private final DirtyListener<String> endWeightDirty = new DirtyListener<>(null);
   private final BooleanProperty dirty = new SimpleBooleanProperty();
   private final BooleanProperty measuresValid = new SimpleBooleanProperty();
   private Roast roast;

   public ProductionEditor() {
      JavaFxUtil.loadComponent(this);
      JavaFxUtil.reverseButtonBars(this);
      JavaFxUtil.createLengthOverrunProperty(250, this.notesEditor, this.availableChars);
      this.notesEditor.setTextFormatter(JavaFxUtil.createLengthLimitingTextFormatter(250));
      this.startWeightEditor.getInput().setEditable(false);
      this.endWeightEditor.getInput().textProperty().addListener(this.endWeightDirty);
      this.roastValue1Editor.getInput().textProperty().addListener(this.roastValue1Dirty);
      this.roastValue2Editor.getInput().textProperty().addListener(this.roastValue2Dirty);
      this.notesEditor.textProperty().addListener(this.notesDirty);
      this.updateLabel.visibleProperty().bind(this.updateLabel.textProperty().isNotEmpty());
      this.saveButton.disableProperty().bind(Bindings.or(this.dirty.not(), this.measuresValid.not()));
   }

   public Label getUpdateLabel() {
      return this.updateLabel;
   }

   public Button getSaveButton() {
      return this.saveButton;
   }

   public Button getCancelButton() {
      return this.cancelButton;
   }

   public ReadOnlyBooleanProperty dirtyProperty() {
      return this.dirty;
   }

   public boolean isDirty() {
      return this.dirty.get();
   }

   public Roast getRoast() {
      return this.roast;
   }

   public void startEditing(Roast roast, AppModel appModel, Consumer<Roast> roastUpdater, Runnable onSuccessfulEdit, boolean notUsedInBlends) {
      this.roast = roast;
      this.dirty.unbind();
      this.titleLabel.setText(String.format("[%s] %s", roast.getLot().getIdTag(), roast.getLot().getName()));
      this.titleLabel.setTooltip(new Tooltip(this.titleLabel.getText()));
      this.startWeightEditor.setValue(roast.getStartWeight().getAmount());
      this.startWeightEditor.setUnit(roast.getStartWeight().getUnit());
      Measure roastValue = roast.getMeasures().get("roastValue");
      Measure roastValue2 = roast.getMeasures().get("roastValue2");
      this.endWeightEditor.setDisable(!OfflineBootstrap.locallySaved(roast));
      this.endWeightEditor.getInput().setEditable(notUsedInBlends);
      this.endWeightEditor.setValue(roast.getEndWeight().getAmount());
      this.endWeightEditor.setUnit(roast.getEndWeight().getUnit());
      this.endWeightDirty.setOriginalValue(this.endWeightEditor.getInput().getText());
      Validators.Validator endWeightValidator = Validators.setValidator(this.endWeightEditor, roast.getStartWeight().getAmount());
      this.roastValue1Editor.setDisable(!OfflineBootstrap.locallySaved(roast));
      this.roastValue1Editor.setValue(roastValue != null ? roastValue.getAmount() : null);
      this.roastValue1Editor.setUnit(roastValue != null ? roastValue.getUnit() : appModel.getPreferences().getRoastValueUnit());
      this.roastValue1Dirty.setOriginalValue(this.roastValue1Editor.getInput().getText());
      Validators.Validator roastValue1Validator = Validators.setValidator(this.roastValue1Editor);
      this.roastValue2Editor.setDisable(!OfflineBootstrap.locallySaved(roast));
      this.roastValue2Editor.setValue(roastValue2 != null ? roastValue2.getAmount() : null);
      this.roastValue2Editor.setUnit(roastValue2 != null ? roastValue2.getUnit() : appModel.getPreferences().getRoastValue2Unit());
      this.roastValue2Dirty.setOriginalValue(this.roastValue2Editor.getInput().getText());
      Validators.Validator roastValue2Validator = Validators.setValidator(this.roastValue2Editor);
      this.measuresValid
         .bind(
            endWeightValidator.highlightProperty()
               .isEqualTo(Validators.Status.NONE)
               .and(roastValue1Validator.highlightProperty().isEqualTo(Validators.Status.NONE))
               .and(roastValue2Validator.highlightProperty().isEqualTo(Validators.Status.NONE))
         );
      this.notesEditor.setDisable(!OfflineBootstrap.locallySaved(roast));
      this.notesEditor.setText(JavaFxUtil.clamp(roast.getNotes(), 250));
      this.notesDirty.setOriginalValue(StringUtils.defaultString(this.notesEditor.getText()));
      this.dirty.bind(this.endWeightDirty.or(this.roastValue1Dirty).or(this.roastValue2Dirty).or(this.notesDirty));
      this.saveButton
         .setOnAction(
            e -> {
               final Measure originalEndWeight = roast.getEndWeight();
               if (this.endWeightEditor.getValue() != null) {
                  double endWeight = this.endWeightEditor.getValue().doubleValue();
                  roast.setEndWeight(new Measure(endWeight, originalEndWeight.getUnit()));
               }

               final Measure originalValue = roast.getMeasures().get("roastValue");
               Number roastValueAmount = this.roastValue1Editor.getValue();
               if (roastValueAmount != null) {
                  roast.getMeasures().put("roastValue", new Measure(roastValueAmount.doubleValue(), this.roastValue1Editor.getUnit()));
               } else {
                  roast.getMeasures().remove("roastValue");
               }

               final Measure originalValue2 = roast.getMeasures().get("roastValue2");
               final Number roastValue2Amount = this.roastValue2Editor.getValue();
               if (roastValue2Amount != null) {
                  roast.getMeasures().put("roastValue2", new Measure(roastValue2Amount.doubleValue(), this.roastValue2Editor.getUnit()));
               } else {
                  roast.getMeasures().remove("roastValue2");
               }

               final String originalNotes = roast.getNotes();
               roast.setNotes(StringUtils.isBlank(this.notesEditor.getText()) ? null : StringUtils.trim(this.notesEditor.getText()));
               ProgressTask<Void> task = new ProgressTask<Void>((Stage)this.getScene().getWindow()) {
                  protected Void call() {
                     roastUpdater.accept(roast);
                     ProductionEditor.this.endWeightDirty.setOriginalValue(ProductionEditor.this.endWeightEditor.getInput().getText());
                     ProductionEditor.this.roastValue1Dirty.setOriginalValue(ProductionEditor.this.roastValue1Editor.getInput().getText());
                     ProductionEditor.this.roastValue2Dirty.setOriginalValue(ProductionEditor.this.roastValue2Editor.getInput().getText());
                     ProductionEditor.this.notesDirty.setOriginalValue(ProductionEditor.this.notesEditor.getText());
                     return null;
                  }

                  @Override
                  protected void failed() {
                     super.succeeded();
                     ProductionEditor.this.logger.error("Error updating roast", this.getException());
                     RoastingIntelligenceApp.retrieveSingleton(NotificationBusService.class)
                        .publish(
                           Notification.builder(NotificationType.ERROR)
                              .withTitle(
                                 org.cropster.roastingintelligence.util.Messages.m_("Error updating roast \"{0}\"", ProductionEditor.this.titleLabel.getText())
                              )
                              .withProperty("hide-after", ToastNotificationController.TOAST_NOTIFICATION_DURATION)
                              .build(),
                           "roast-batch-editing"
                        );
                     Platform.runLater(
                        () -> {
                           ProductionEditor.this.updateLabel
                              .setText(org.cropster.roastingintelligence.util.Messages.m_("Error updating roast: {0}", this.getException().getMessage()));
                           this.restoreOriginalValues();
                        }
                     );
                  }

                  @Override
                  protected void succeeded() {
                     super.succeeded();
                     onSuccessfulEdit.run();
                  }

                  private void restoreOriginalValues() {
                     roast.setEndWeight(originalEndWeight);
                     if (originalValue != null) {
                        roast.getMeasures().put("roastValue", originalValue);
                     } else {
                        roast.getMeasures().remove("roastValue");
                     }

                     if (roastValue2Amount != null) {
                        roast.getMeasures().put("roastValue2", originalValue2);
                     } else {
                        roast.getMeasures().remove("roastValue2");
                     }

                     ProductionEditor.this.notesEditor.setText(originalNotes);
                  }
               };
               PlatformScheduler.executeBackgroundTask(task);
            }
         );
   }

   public void reset() {
      this.roast = null;
      this.dirty.unbind();
      this.endWeightDirty.setOriginalValue("");
      this.roastValue1Dirty.setOriginalValue("");
      this.roastValue2Dirty.setOriginalValue("");
      this.notesDirty.setOriginalValue("");
      this.updateLabel.setText(null);
      this.endWeightEditor.setValue(null);
      this.roastValue1Editor.setValue(null);
      this.roastValue2Editor.setValue(null);
      this.notesEditor.setText("");
   }
}
