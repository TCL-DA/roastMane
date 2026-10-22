package com.cropster.roastingintelligence.ui.components;

import com.cropster.roastingintelligence.Preferences;
import com.cropster.roastingintelligence.ui.JavaFxUtil;
import com.cropster.roastingintelligence.ui.util.Validators;
import com.cropster.util.model.Unit;
import java.net.URL;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import org.apache.commons.lang3.StringUtils;
import org.cropster.roastingintelligence.RoastingModel;
import org.cropster.roastingintelligence.model.Commentable;
import org.cropster.roastingintelligence.model.Roast;
import org.cropster.roastingintelligence.model.RoastingComment;

public class CommentPopupContent extends GridPane implements Initializable {
   public static final NumberFormat NUMBER_FORMAT = NumberFormat.getNumberInstance();
   @FXML
   private VBox buttonBox;
   @FXML
   private MeasureEditor gasEditor;
   @FXML
   private MeasureEditor airEditor;
   @FXML
   private Button gasButton;
   @FXML
   private Button airButton;
   @FXML
   private Button colorChangeButton;
   @FXML
   private Button firstCrackButton;
   @FXML
   private Button secondCrackButton;
   @FXML
   private Button textCommentButton;
   @FXML
   private TextArea noteInput;
   private Preferences preferences;
   private final Commentable commentable;
   private CommentPopupContent.CommentValueProvider commentValueProvider;
   private final CommentPopupHeader header;

   public CommentPopupContent(Preferences preferences, Commentable commentable, CommentPopupHeader header) {
      JavaFxUtil.loadComponent(this);
      this.header = Objects.requireNonNull(header);
      this.preferences = preferences;
      this.commentable = commentable;
   }

   public void initialize(URL location, ResourceBundle resources) {
      this.gasEditor.getParent().managedProperty().bind(this.gasEditor.getParent().visibleProperty());
      this.airEditor.getParent().managedProperty().bind(this.airEditor.getParent().visibleProperty());
      this.colorChangeButton.managedProperty().bind(this.colorChangeButton.visibleProperty());
      this.firstCrackButton.managedProperty().bind(this.firstCrackButton.visibleProperty());
      this.secondCrackButton.managedProperty().bind(this.secondCrackButton.visibleProperty());
      this.gasButton.disableProperty().bind(this.gasEditor.getInput().textProperty().isEmpty());
      this.gasEditor.setUnit(Unit.PERCENT);
      this.gasEditor.getInput().setOnAction(e -> {
         e.consume();
         this.gasButton.fire();
      });
      this.airButton.disableProperty().bind(this.airEditor.getInput().textProperty().isEmpty());
      this.airEditor.setUnit(Unit.PERCENT);
      this.airEditor.getInput().setOnAction(e -> {
         e.consume();
         this.airButton.fire();
      });
      this.textCommentButton.disableProperty().bind(this.noteInput.textProperty().isEmpty());
      Validators.setValidator(this.gasEditor, -10000.0, 10000.0);
      Validators.setValidator(this.airEditor, -10000.0, 10000.0);
   }

   public void initTempInput(Unit unit) {
      this.header.initTempInput(unit);
   }

   public void setCommentValueProvider(CommentPopupContent.CommentValueProvider commentValueProvider) {
      this.commentValueProvider = commentValueProvider;
      this.updateCommentsPanel("gas");
      this.updateCommentsPanel("airflow");
   }

   public TimeEditor getTimeInput() {
      return this.header.getTimeInput();
   }

   public MeasureEditor getTempInput() {
      return this.header.getTempInput();
   }

   public TextField getGasEditor() {
      return this.gasEditor.getInput();
   }

   public TextField getAirEditor() {
      return this.airEditor.getInput();
   }

   public TextArea getNoteInput() {
      return this.noteInput;
   }

   private long getTime() {
      if (this.header.getTempInput().isVisible() && this.header.getTempInput().getValue() != null) {
         Long time = this.commentable.getTimeForTemp(this.header.getTempInput().getValue().doubleValue());
         if (time != null) {
            return time;
         }
      }

      Long time = this.header.getTimeInput().getValue();
      return time != null ? time : 0L;
   }

   private void addComment(String event) {
      Long commentTime = this.getTime();
      RoastingComment comment = new RoastingComment(commentTime, event, StringUtils.trimToNull(this.noteInput.getText()));
      this.commentable.addComment(comment);
      this.noteInput.setText(null);
      this.closePopup();
   }

   @FXML
   protected void addGasComment(ActionEvent event) {
      Number value = this.gasEditor.getValue();
      this.commentable.addComment(new RoastingComment(this.getTime(), "gas", NUMBER_FORMAT.format(value)));
      new Timeline(new KeyFrame[]{new KeyFrame(Duration.millis(200.0), e -> {
         this.gasEditor.getInput().setText(null);
         this.updateCommentsPanel("gas");
      }, new KeyValue[0])}).play();
      this.closePopup();
   }

   @FXML
   protected void addAirComment(ActionEvent event) {
      Number value = this.airEditor.getValue();
      this.commentable.addComment(new RoastingComment(this.getTime(), "airflow", NUMBER_FORMAT.format(value)));
      new Timeline(new KeyFrame[]{new KeyFrame(Duration.millis(200.0), e -> {
         this.airEditor.getInput().setText(null);
         this.updateCommentsPanel("airflow");
      }, new KeyValue[0])}).play();
      this.closePopup();
   }

   @FXML
   void addColorChange() {
      this.addComment("colorChange");
   }

   @FXML
   void addFirstCrack() {
      this.addComment("firstCrack");
   }

   @FXML
   void addSecondCrack() {
      this.addComment("secondCrack");
   }

   @FXML
   void addTextComment() {
      this.addComment(null);
   }

   public void updateEventButtons(String... events) {
      Arrays.sort((Object[])events);
      int row = 0;
      boolean gasVisible = Arrays.binarySearch(events, "gas") >= 0;
      this.gasEditor.setVisible(gasVisible);
      this.gasButton.setVisible(gasVisible);
      if (gasVisible) {
         GridPane.setRowIndex(this.gasEditor, row);
         GridPane.setRowIndex(this.gasButton, row);
         row++;
      }

      boolean airVisible = Arrays.binarySearch(events, "airflow") >= 0;
      this.airEditor.setVisible(airVisible);
      this.airButton.setVisible(airVisible);
      if (airVisible) {
         GridPane.setRowIndex(this.airEditor, row);
         GridPane.setRowIndex(this.airButton, row);
         row++;
      }

      boolean colorChangeVisible = Arrays.binarySearch(events, "colorChange") >= 0;
      this.colorChangeButton.setVisible(colorChangeVisible);
      if (colorChangeVisible) {
         GridPane.setRowIndex(this.colorChangeButton, row);
         row++;
      }

      boolean firstCrackVisible = Arrays.binarySearch(events, "firstCrack") >= 0;
      this.firstCrackButton.setVisible(firstCrackVisible);
      boolean secondCrackVisible = Arrays.binarySearch(events, "secondCrack") >= 0;
      this.secondCrackButton.setVisible(secondCrackVisible);
      if (firstCrackVisible || secondCrackVisible) {
         GridPane.setRowIndex(this.firstCrackButton, row);
         GridPane.setRowIndex(this.secondCrackButton, row);
         row++;
         if (firstCrackVisible && secondCrackVisible) {
            GridPane.setColumnIndex(this.firstCrackButton, 0);
            GridPane.setColumnSpan(this.firstCrackButton, 1);
            GridPane.setColumnIndex(this.secondCrackButton, 1);
            GridPane.setColumnSpan(this.secondCrackButton, 1);
         } else if (firstCrackVisible) {
            GridPane.setColumnIndex(this.firstCrackButton, 0);
            GridPane.setColumnSpan(this.firstCrackButton, 2);
         } else {
            GridPane.setColumnIndex(this.secondCrackButton, 0);
            GridPane.setColumnSpan(this.secondCrackButton, 2);
         }
      }

      GridPane.setRowIndex(this.noteInput, row);
      GridPane.setRowIndex(this.textCommentButton, ++row);
   }

   public void updateCommentsPanel(String event) {
      if (this.commentValueProvider != null) {
         Double d = null;
         RoastingComment comment = this.commentValueProvider.nextValue(event);
         if (comment != null) {
            try {
               d = NUMBER_FORMAT.parse(comment.getText()).doubleValue();
            } catch (ParseException var5) {
            }
         }

         if ("gas".equals(event) && this.preferences.isPreFillEnabled("gas")) {
            this.gasEditor.setValue(d);
         } else if ("airflow".equals(event) && this.preferences.isPreFillEnabled("airflow")) {
            this.airEditor.setValue(d);
         }
      }
   }

   public Button getColorChangeButton() {
      return this.colorChangeButton;
   }

   public Button getFirstCrackButton() {
      return this.firstCrackButton;
   }

   public Button getSecondCrackButton() {
      return this.secondCrackButton;
   }

   private void closePopup() {
      this.getScene().getWindow().hide();
   }

   public interface CommentValueProvider {
      RoastingComment nextValue(String var1);
   }

   public static class RoastingModelValueProvider implements CommentPopupContent.CommentValueProvider {
      private final RoastingModel roastingModel;
      private final Map<String, Long> lastComments = new HashMap<>();

      public RoastingModelValueProvider(RoastingModel roastingModel) {
         this.roastingModel = roastingModel;
         this.lastComments.clear();
         this.lastComments.put("gas", -1L);
         this.lastComments.put("airflow", -1L);
      }

      @Override
      public RoastingComment nextValue(String event) {
         if (!this.roastingModel.getPreferences().isPreFillEnabled("gas") && !this.roastingModel.getPreferences().isPreFillEnabled("airflow")) {
            return null;
         } else {
            Roast reference = this.roastingModel.getCurrentReference();
            RoastingComment comment = null;
            if (reference != null) {
               comment = reference.getComments()
                  .stream()
                  .filter(c -> event.equals(c.getEvent()) && StringUtils.isNotBlank(c.getText()) && c.getTime() > this.lastComments.get(c.getEvent()))
                  .findFirst()
                  .orElse(null);
               if (comment != null) {
                  this.lastComments.put(comment.getEvent(), comment.getTime());
               }
            }

            return comment;
         }
      }
   }
}
