package com.cropster.roastingintelligence.ui.components;

import com.cropster.roastingintelligence.ui.JavaFxUtil;
import com.cropster.roastingintelligence.ui.util.Icon;
import java.util.function.Consumer;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.apache.commons.lang3.StringUtils;
import org.cropster.roastingintelligence.model.WorkerTask;
import org.cropster.roastingintelligence.util.MeasureFormat;

public class TaskLabel extends VBox {
   private static final String DARKER_STYLE = "darker";
   @FXML
   private HBox buttonsBar;
   @FXML
   private Button markButton;
   @FXML
   private Button menuButton;
   @FXML
   private Label missedBadge;
   @FXML
   private Label overdueBadge;
   @FXML
   private Label titleLabel;
   @FXML
   private Label dateLabel;
   @FXML
   private Label frequencyLabel;
   @FXML
   private Label overdueCountLabel;
   @FXML
   private Label assigneeLabel;
   @FXML
   private Label descriptionLabel;
   @FXML
   private Label commentLabel;
   @FXML
   private Label statusBadge;
   private final BooleanProperty editable = new SimpleBooleanProperty();
   private final Consumer<ActionEvent> menuAction;
   private final Consumer<ActionEvent> markWithCommentAction;
   private final MeasureFormat measureFormat = MeasureFormat.getInstance();

   public TaskLabel(Consumer<ActionEvent> menuAction, Consumer<ActionEvent> markWithCommentAction) {
      JavaFxUtil.loadComponent(this);
      this.menuAction = menuAction;
      this.markWithCommentAction = markWithCommentAction;
      this.buttonsBar.visibleProperty().bind(this.editable);
      this.missedBadge.setGraphic(Icon.CLOSE.load("icon--error"));
      this.dateLabel.visibleProperty().bind(this.dateLabel.textProperty().isNotNull());
      this.frequencyLabel.visibleProperty().bind(this.frequencyLabel.textProperty().isNotNull());
      this.overdueCountLabel.visibleProperty().bind(this.overdueCountLabel.textProperty().isNotNull());
      this.overdueBadge.visibleProperty().bind(this.overdueCountLabel.visibleProperty());
   }

   public void init(WorkerTask item) {
      this.titleLabel.setText(item.getName());
      String date = null;
      String frequency = null;
      String overdueCount = null;

      switch (item.getFrequency()) {
         case ROAST_VOLUME, ROAST_DURATION -> {
            frequency = org.cropster.roastingintelligence.util.Messages.m_("Every {0} roasted", this.measureFormat.format(item.getProductionThreshold()));
            if (item.getDueProductionValue() != null && item.getDueProductionValue().getAmount() != 0.0) {
               overdueCount = String.format("+%s", this.measureFormat.format(item.getDueProductionValue().abs()));
            }
         }
         case ROAST_NUMBER -> {
            frequency = org.cropster.roastingintelligence.util.Messages.p_("Every roast", "Every {0} roasts", (int)item.getProductionThreshold().getAmount());
            if (item.getDueProductionValue() != null && item.getDueProductionValue().getAmount() != 0.0) {
               overdueCount = org.cropster.roastingintelligence.util.Messages.p_("+{0} roast", "+{0} roasts", (int)item.getDueProductionValue().abs().getAmount());
            }
         }
         default -> {
            date = item.getDueDate() != null ? JavaFxUtil.formatDate(item.getDueDate()) : null;
            if (item.getFrequency() != WorkerTask.TaskFrequency.ONE_TIME) {
               frequency = org.cropster.roastingintelligence.util.Messages.m_(item.getFrequency().toString());
            }

            overdueCount = item.getOverdueDays() != null ? org.cropster.roastingintelligence.util.Messages.p_("+{0} day", "+{0} days", item.getOverdueDays()) : null;
         }
      }

      this.dateLabel.setText(date);
      this.frequencyLabel.setText(frequency);
      this.overdueCountLabel.setText(overdueCount);
      this.assigneeLabel
         .setText(item.getAssignedTo() != null ? item.getAssignedTo().getFullName() : org.cropster.roastingintelligence.util.Messages.m_("Anyone"));
      this.descriptionLabel.setText(item.getDetail());
      this.descriptionLabel.setVisible(StringUtils.isNotBlank(item.getDetail()));
      this.commentLabel.setText(item.getComment());
      this.commentLabel.setVisible(StringUtils.isNotBlank(item.getComment()));
      this.missedBadge.setVisible(item.isMissed());
      this.statusBadge.setVisible(!item.isMissed());
      this.statusBadge.setText(org.cropster.roastingintelligence.util.Messages.m_(item.getStatus().name()).toUpperCase());
      switch (item.getStatus()) {
         case TO_DO:
            this.getStyleClass().remove("darker");
            JavaFxUtil.replaceStyles(this.statusBadge.getStyleClass(), "tag--info", "tag--success", "tag--transparent");
            break;
         case NOT_DONE:
            JavaFxUtil.addStyleIfNotPresent(this.getStyleClass(), "darker");
            JavaFxUtil.replaceStyles(this.statusBadge.getStyleClass(), "tag--transparent", "tag--info", "tag--success");
            break;
         case DONE:
            JavaFxUtil.addStyleIfNotPresent(this.getStyleClass(), "darker");
            JavaFxUtil.replaceStyles(this.statusBadge.getStyleClass(), "tag--success", "tag--info", "tag--transparent");
            break;
         case IN_PROGRESS:
            this.getStyleClass().remove("darker");
            break;
         default:
            JavaFxUtil.addStyleIfNotPresent(this.getStyleClass(), "darker");
      }
   }

   public BooleanProperty editableProperty() {
      return this.editable;
   }

   public boolean isEditable() {
      return this.editable.get();
   }

   public void setEditable(boolean editable) {
      this.editable.set(editable);
   }

   public Button getMarkButton() {
      return this.markButton;
   }

   public Button getMenuButton() {
      return this.menuButton;
   }

   @FXML
   protected void markWithComment(ActionEvent e) {
      this.markWithCommentAction.accept(e);
   }

   @FXML
   protected void showMenu(ActionEvent e) {
      this.menuAction.accept(e);
   }
}
