package com.cropster.roastingintelligence.ui.layout;

import com.cropster.roastingintelligence.ui.JavaFxUtil;
import com.cropster.roastingintelligence.ui.components.LinkToRoastLabel;
import com.cropster.roastingintelligence.ui.components.MeasureLabel;
import com.cropster.roastingintelligence.ui.components.TimeLabel;
import com.cropster.roastingintelligence.ui.util.GridPaneZebraStyle;
import com.cropster.util.model.Unit;
import com.cropster.util.text.DurationFormat;
import java.net.URL;
import java.text.NumberFormat;
import java.util.Collection;
import java.util.Iterator;
import java.util.Queue;
import java.util.ResourceBundle;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import org.cropster.roastingintelligence.model.Curve;
import org.cropster.roastingintelligence.model.Goal;
import org.cropster.roastingintelligence.model.Measure;
import org.cropster.roastingintelligence.model.Roast;
import org.cropster.roastingintelligence.util.MeasureFormat;

public class StatisticsPanel extends VBox implements Initializable {
   private static final int START_Y = 1;
   @FXML
   private GridPane gridPane;
   @FXML
   private GridPane gridPaneOtherGoals;
   @FXML
   private VBox notesContainer;
   @FXML
   private VBox profileNotesContainer;
   @FXML
   private LinkToRoastLabel idTagLabel;
   @FXML
   private Label dateLabel;
   @FXML
   private Button previewChartButton;
   private final StringProperty notes = new SimpleStringProperty();
   private final StringProperty profileNotes = new SimpleStringProperty();
   private int y1 = 1;
   private int y2 = 1;
   private Unit tempUnit = Unit.CELSIUS;

   public StatisticsPanel() {
      JavaFxUtil.loadComponent(this);
      this.notesContainer.visibleProperty().bind(this.notes.isNotEmpty());
      this.profileNotesContainer.visibleProperty().bind(this.profileNotes.isNotEmpty());
      this.gridPaneOtherGoals.visibleProperty().bind(this.gridPane.visibleProperty().and(Bindings.size(this.gridPaneOtherGoals.getChildren()).greaterThan(2)));
      GridPaneZebraStyle.apply(this.gridPane, (r, c) -> r % 2 == 0 ? "" : "-fx-background-color: -fx-color-neutral-20")
         .withAlignmentSelector((r, c) -> c == 1 ? Pos.TOP_RIGHT : Pos.TOP_LEFT)
         .applyToExisting();
      GridPaneZebraStyle.apply(this.gridPaneOtherGoals, (r, c) -> r % 2 == 0 ? "" : "-fx-background-color: -fx-color-neutral-20").applyToExisting();
   }

   public void initialize(URL url, ResourceBundle resourceBundle) {
      JavaFxUtil.setDefaultLineSpacing(this);
   }

   public void setGoalStstisticsVisible(boolean visible) {
      this.gridPane.setVisible(visible);
   }

   public StringProperty notesProperty() {
      return this.notes;
   }

   public String getNotes() {
      return (String)this.notes.get();
   }

   public void setNotes(String notes) {
      this.notes.set(notes);
   }

   public BooleanBinding notesVisibleProperty() {
      return this.notes.isNotEmpty();
   }

   public boolean isNotesVisible() {
      return this.notesVisibleProperty().get();
   }

   public StringProperty profileNotesProperty() {
      return this.profileNotes;
   }

   public String getProfileNotes() {
      return (String)this.profileNotes.get();
   }

   public void setProfileNotes(String profileNotes) {
      this.profileNotes.set(profileNotes);
   }

   public BooleanBinding profileNotesVisibleProperty() {
      return this.profileNotes.isNotEmpty();
   }

   public Button getPreviewChartButton() {
      return this.previewChartButton;
   }

   public void init(Roast roast) {
      this.idTagLabel.init(roast);
      this.dateLabel.setText(String.format("(%s %s)", JavaFxUtil.formatDate(roast.getStartDate()), JavaFxUtil.formatTime(roast.getStartDate())));
   }

   public void setTempUnit(Unit tempUnit) {
      this.tempUnit = tempUnit;
   }

   private Label createGoalRangeLabel(Collection<Goal> goals, String goalName) {
      NumberFormat durationFormat = DurationFormat.getDurationInstance(true);
      MeasureFormat measureFormat = MeasureFormat.getInstance(4);
      Label label = new Label("-");
      Iterator<Goal> it = goals.iterator();

      while (it.hasNext()) {
         Goal goal = it.next();
         if (goal.getName().equals(goalName)) {
            it.remove();
            String bounds = "";
            if (goal.getLowerBound() != null) {
               if (goal.getLowerBound().getUnit().equals(Unit.DURATION)) {
                  bounds = bounds + durationFormat.format(goal.getLowerBound().getAmount() * 1000.0);
               } else if (goal.getUpperBound() != null && goal.getLowerBound().getUnit().equals(goal.getUpperBound().getUnit())) {
                  bounds = bounds + measureFormat.getNumberFormat().format(goal.getLowerBound().getAmount());
               } else {
                  bounds = bounds + measureFormat.format(goal.getLowerBound());
               }
            }

            if (goal.getUpperBound() != null) {
               if (goal.isAbsolute()) {
                  bounds = bounds + (goal.getLowerBound() != null ? " - " : "< ");
               } else if (goal.getLowerBound() != null && goal.getUpperBound().equals(goal.getLowerBound().abs())) {
                  bounds = "+/- ";
               } else {
                  bounds = bounds + "/+";
               }

               if (goal.getUpperBound().getUnit().equals(Unit.DURATION)) {
                  bounds = bounds + durationFormat.format(goal.getUpperBound().getAmount() * 1000.0);
               } else {
                  bounds = bounds + measureFormat.format(goal.getUpperBound());
               }
            } else if (goal.getLowerBound() != null) {
               bounds = "> " + bounds;
            }

            String description = "";
            if (goal.getStartTime() != null) {
               String time = durationFormat.format(goal.getStartTime());
               time = time + " - ";
               time = time + durationFormat.format(goal.getEndTime());
               Matcher m = Pattern.compile(".+ \\((.+)\\)").matcher(description);
               if (m.matches()) {
                  description = description.replace(m.group(1), time);
               } else {
                  description = String.format(" (%s)", time);
               }
            }

            label.setText(bounds + description);
         }
      }

      return label;
   }

   public void reset(Unit tempUnit) {
      this.tempUnit = tempUnit;
      Iterator<Node> it = this.gridPane.getChildren().iterator();

      while (it.hasNext()) {
         Node node = it.next();
         Integer rowIndex = GridPane.getRowIndex(node);
         if (rowIndex != null && rowIndex >= 1) {
            it.remove();
         }
      }

      this.y1 = 1;
      it = this.gridPaneOtherGoals.getChildren().iterator();

      while (it.hasNext()) {
         Node node = it.next();
         Integer rowIndex = GridPane.getRowIndex(node);
         if (rowIndex != null && rowIndex >= 1) {
            it.remove();
         }
      }

      this.y2 = 1;
   }

   public void addStatisticMeasure(String measureName, Measure measure, Queue<Goal> goals, String label) {
      if (measure != null) {
         Measure convertedMeasure = measure.getUnit().isCompatible(Unit.CELSIUS) ? measure.toUnit(this.tempUnit) : measure;
         this.gridPane.add(new Label(label), 0, this.y1);
         this.gridPane.add(new MeasureLabel(convertedMeasure), 1, this.y1);
         if (goals != null) {
            this.gridPane.add(this.createGoalRangeLabel(goals, measureName), 2, this.y1, Integer.MAX_VALUE, 1);
         } else {
            this.gridPane.add(new Label(""), 2, this.y1, Integer.MAX_VALUE, 1);
         }

         this.y1++;
      }
   }

   public void addStatisticMeasure(String measureName, Roast roast, Queue<Goal> goals, String label) {
      this.addStatisticMeasure(measureName, roast.getMeasures().get(measureName), goals, label);
   }

   public void addStatisticMeasure(String measureName, Roast roast, String label) {
      this.addStatisticMeasure(measureName, roast.getMeasures().get(measureName), null, label);
   }

   public void addStatisticMeasure(String measureName, Measure measure, Queue<Goal> goals) {
      this.addStatisticMeasure(measureName, measure, goals, org.cropster.roastingintelligence.util.Messages.m_(measureName));
   }

   public void addStatisticMeasure(String measureName, Roast roast) {
      this.addStatisticMeasure(measureName, roast.getMeasures().get(measureName), null, org.cropster.roastingintelligence.util.Messages.m_(measureName));
   }

   public void addStatisticMeasure(String measureName, Roast roast, Queue<Goal> goals) {
      this.addStatisticMeasure(measureName, roast.getMeasures().get(measureName), goals);
   }

   public void addStatisticCurveStart(String measureName, Roast roast) {
      Curve curve = roast.getCurves().get(measureName);
      if (curve != null) {
         Entry<Long, Double> first = curve.entrySet().stream().findFirst().orElse(null);
         if (first != null) {
            this.gridPane
               .add(
                  new Label(
                     org.cropster.roastingintelligence.util.Messages.m_("Charge") + " " + org.cropster.roastingintelligence.util.Messages.m_(measureName)
                  ),
                  0,
                  this.y1
               );
            this.gridPane.add(new MeasureLabel(new Measure(first.getValue(), curve.getUnit())), 1, this.y1);
            this.gridPane.add(new Label(""), 2, this.y1, Integer.MAX_VALUE, 1);
            this.y1++;
         }
      }
   }

   public void addTime(String name, long time, Queue<Goal> goals) {
      this.gridPane.add(new Label(org.cropster.roastingintelligence.util.Messages.m_(name)), 0, this.y1);
      this.gridPane.add(new TimeLabel(time), 1, this.y1);
      this.gridPane.add(this.createGoalRangeLabel(goals, name), 2, this.y1, Integer.MAX_VALUE, 1);
      this.y1++;
   }

   public void addTime(String measureName, Roast roast, Queue<Goal> goals) {
      Measure timeMeasure = roast.getMeasures().get(measureName);
      if (timeMeasure != null) {
         this.gridPane.add(new Label(org.cropster.roastingintelligence.util.Messages.m_(measureName)), 0, this.y1);
         this.gridPane.add(new TimeLabel(timeMeasure), 1, this.y1);
         this.gridPane.add(this.createGoalRangeLabel(goals, measureName), 2, this.y1, Integer.MAX_VALUE, 1);
         this.y1++;
      }
   }

   public void addOtherGoals(Queue<Goal> goals) {
      while (!goals.isEmpty()) {
         Goal goal = goals.peek();
         this.gridPaneOtherGoals.add(new Label(goal.getLocalizedMessage()), 0, this.y2);
         this.gridPaneOtherGoals.add(this.createGoalRangeLabel(goals, goal.getName()), 1, this.y2);
         this.y2++;
      }
   }
}
