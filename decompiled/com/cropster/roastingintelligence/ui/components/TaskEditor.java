package com.cropster.roastingintelligence.ui.components;

import com.cropster.roastingintelligence.ui.JavaFxUtil;
import com.cropster.roastingintelligence.ui.util.DirtyListener;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;
import org.apache.commons.lang3.StringUtils;
import org.cropster.roastingintelligence.model.WorkerTask;

public class TaskEditor extends VBox {
   static final int MAX_COMMENT_LENGTH = 250;
   @FXML
   private Label titleLabel;
   @FXML
   private Label availableChars;
   @FXML
   private ToggleGroup statusToggleGroup;
   @FXML
   private TextArea commentField;
   @FXML
   private Button cancelButton;
   @FXML
   private Button saveButton;
   private final DirtyListener<Toggle> statusDirty = new DirtyListener<>(null);
   private final DirtyListener<String> commentsDirty = new DirtyListener<>(null);

   public TaskEditor() {
      JavaFxUtil.loadComponent(this);
      BooleanProperty dirty = new SimpleBooleanProperty();
      JavaFxUtil.createLengthOverrunProperty(250, this.commentField, this.availableChars);
      this.commentField.setTextFormatter(JavaFxUtil.createLengthLimitingTextFormatter(250));
      this.commentField.textProperty().addListener(this.commentsDirty);
      this.statusToggleGroup.selectedToggleProperty().addListener(this.statusDirty);
      dirty.bind(this.statusDirty.or(this.commentsDirty));
      this.saveButton.disableProperty().bind(dirty.not());
   }

   public void init(WorkerTask item) {
      this.titleLabel.setText(item.getName());
      this.titleLabel.setTooltip(new Tooltip(item.getName()));
      this.setStatus(item.getStatus());
      this.statusDirty.setOriginalValue(this.statusToggleGroup.getSelectedToggle());
      this.setComment(item.getComment());
      this.commentsDirty.setOriginalValue(StringUtils.defaultString(this.commentField.getText()));
   }

   public WorkerTask.TaskStatus getStatus() {
      return (WorkerTask.TaskStatus)this.statusToggleGroup.getSelectedToggle().getUserData();
   }

   public void setStatus(WorkerTask.TaskStatus status) {
      JavaFxUtil.selectToggle(
         this.statusToggleGroup, WorkerTask.TaskStatus.NOT_DONE.equals(status) ? WorkerTask.TaskStatus.NOT_DONE : WorkerTask.TaskStatus.DONE
      );
   }

   public String getComment() {
      return this.commentField.getText();
   }

   public void setComment(String comment) {
      this.commentField.setText(JavaFxUtil.clamp(comment, 250));
   }

   public Button getCancelButton() {
      return this.cancelButton;
   }

   public Button getSaveButton() {
      return this.saveButton;
   }
}
