package com.cropster.roastingintelligence.ui.components;

import com.cropster.roastingintelligence.ui.JavaFxUtil;
import com.cropster.roastingintelligence.ui.util.Icon;
import com.cropster.roastingintelligence.util.NumberUtils;
import com.cropster.util.text.DurationFormat;
import java.text.DecimalFormatSymbols;
import java.text.Format;
import java.util.Locale;
import java.util.Locale.Category;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.util.converter.FormatStringConverter;
import javafx.util.converter.NumberStringConverter;
import org.apache.commons.lang3.StringUtils;
import org.cropster.roastingintelligence.model.Commentable;
import org.cropster.roastingintelligence.model.Measure;
import org.cropster.roastingintelligence.model.RoastingComment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommentEditor extends GridPane {
   private static NumberStringConverter converter = new NumberStringConverter(Locale.getDefault(Category.FORMAT));
   private static DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(Locale.getDefault(Category.FORMAT));
   private final Logger logger = LoggerFactory.getLogger(CommentEditor.class);
   @FXML
   private Button cancelButton;
   @FXML
   private TextField commentEditor;
   @FXML
   private TextField timeEditor;
   @FXML
   private TextField tempEditor;
   @FXML
   private TextField valueEditor;
   @FXML
   private Label typeLabel;
   @FXML
   private Button updateButton;
   private final TextFormatter<Long> timeFormatter;
   private final TextFormatter<Number> tempFormatter;
   private final TextFormatter<Number> valueFormatter;
   private final Format timeFormat = DurationFormat.getDurationInstance(true);
   private RoastingComment comment;
   private Measure temperature;
   private Commentable commentable;

   public CommentEditor() {
      JavaFxUtil.loadComponent(this);
      this.updateButton.setGraphic(Icon.CHECK_MARK.load());
      this.timeFormatter = new TextFormatter(new FormatStringConverter(this.timeFormat));
      this.timeEditor.setTextFormatter(this.timeFormatter);
      this.tempFormatter = new TextFormatter(converter);
      this.tempEditor.setTextFormatter(this.tempFormatter);
      this.valueFormatter = new TextFormatter(converter);
      this.valueEditor.setTextFormatter(this.valueFormatter);
      this.timeEditor.addEventHandler(KeyEvent.KEY_PRESSED, this::updateCommentOnEnter);
      this.tempEditor.addEventHandler(KeyEvent.KEY_PRESSED, this::updateCommentOnEnter);
      this.valueEditor.addEventHandler(KeyEvent.KEY_PRESSED, this::updateCommentOnEnter);
      this.commentEditor.addEventHandler(KeyEvent.KEY_PRESSED, this::updateCommentOnEnter);
      this.valueEditor.visibleProperty().bind(Bindings.createBooleanBinding(this::isValueVisible, new Observable[]{this.typeLabel.textProperty()}));
      this.commentEditor.visibleProperty().bind(Bindings.createBooleanBinding(this::isCommentVisible, new Observable[]{this.typeLabel.textProperty()}));
   }

   public void init(Commentable commentable, RoastingComment comment, Measure temperature) {
      this.commentable = commentable;
      this.comment = comment;
      this.temperature = temperature;
      this.typeLabel
         .setText(
            comment.getEvent() != null
               ? org.cropster.roastingintelligence.util.Messages.m_(comment.getEvent())
               : org.cropster.roastingintelligence.util.Messages.m_("Text")
         );
      this.typeLabel.setGraphic(CommentLabel.iconForEvent(comment.getEvent()));
      this.typeLabel.setTooltip(new Tooltip(this.typeLabel.getText()));
      this.timeFormatter.setValue(comment.getTime());
      this.tempFormatter.setValue(temperature != null ? temperature.getAmount() : null);
      if (this.isValueVisible()) {
         String valueText = StringUtils.trim(comment.getText());
         if (NumberUtils.isParsable(valueText, decimalFormatSymbols)) {
            this.valueFormatter.setValue(converter.fromString(valueText));
         } else {
            this.logger.warn("Comment value is not a number: \"{}\". Using it as a plain text instead.", valueText);
            this.valueEditor.setTextFormatter(null);
            this.valueEditor.setText(valueText);
         }
      } else {
         this.valueFormatter.setValue(null);
      }

      this.commentEditor.setText(this.isCommentVisible() ? comment.getText() : null);
      Platform.runLater(() -> {
         if (this.commentEditor.isVisible()) {
            this.commentEditor.selectAll();
            this.commentEditor.requestFocus();
         } else if (this.valueEditor.isVisible()) {
            this.valueEditor.selectAll();
            this.valueEditor.requestFocus();
         } else {
            this.tempEditor.selectAll();
            this.tempEditor.requestFocus();
         }
      });
   }

   private RoastingComment updateComment() {
      if (this.isCommentVisible()) {
         this.comment.setText(this.commentEditor.getText());
      } else if (this.isValueVisible() && this.valueFormatter.getValue() != null) {
         this.comment.setText(Double.toString(((Number)this.valueFormatter.getValue()).doubleValue()));
      } else {
         this.comment.setText(null);
      }

      if (this.timeFormatter.getValue() != null && !((Long)this.timeFormatter.getValue()).equals(this.comment.getTime())) {
         this.commentable.removeComment(this.comment);
         this.commentable.addComment(new RoastingComment((Long)this.timeFormatter.getValue(), this.comment.getEvent(), this.comment.getText()));
      } else if (this.tempFormatter.getValue() != null
         && (this.temperature == null || !((Number)this.tempFormatter.getValue()).equals(this.temperature.getAmount()))) {
         Long time = this.commentable.getTimeForTemp(this.comment.getTime(), ((Number)this.tempFormatter.getValue()).doubleValue());
         if (time != null) {
            this.commentable.removeComment(this.comment);
            this.commentable.addComment(new RoastingComment(time, this.comment.getEvent(), this.comment.getText()));
         }
      } else {
         this.commentable.fireCommentUpdated(this.comment);
      }

      return this.comment;
   }

   public RoastingComment updateAndGetComment() {
      this.timeEditor.commitValue();
      this.tempEditor.commitValue();
      this.valueEditor.commitValue();
      this.commentEditor.commitValue();
      return this.updateComment();
   }

   public Button getCancelButton() {
      return this.cancelButton;
   }

   public Button getUpdateButton() {
      return this.updateButton;
   }

   private boolean isCommentVisible() {
      String event = this.comment != null ? this.comment.getEvent() : null;
      return event == null
         || ("firstCrack".equals(event) || "secondCrack".equals(event) || "colorChange".equals(event))
            && this.comment != null
            && StringUtils.isNotEmpty(this.comment.getText());
   }

   private boolean isValueVisible() {
      String event = this.comment != null ? this.comment.getEvent() : null;
      return event != null && ("gas".equals(event) || "airflow".equals(event));
   }

   private void updateCommentOnEnter(KeyEvent e) {
      if (KeyCode.ENTER.equals(e.getCode())) {
         e.consume();
         this.updateButton.fire();
      }
   }
}
