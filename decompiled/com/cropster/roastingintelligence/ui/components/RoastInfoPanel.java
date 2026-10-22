package com.cropster.roastingintelligence.ui.components;

import com.cropster.roastingintelligence.Data;
import com.cropster.roastingintelligence.ui.JavaFxUtil;
import java.math.RoundingMode;
import java.text.NumberFormat;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import org.cropster.roastingintelligence.model.Lot;
import org.cropster.roastingintelligence.model.LotConsistencyCheck;
import org.cropster.roastingintelligence.model.LotTransaction;
import org.cropster.roastingintelligence.model.Measure;
import org.cropster.roastingintelligence.model.ProcessingAlert;
import org.cropster.roastingintelligence.model.Roast;
import org.cropster.roastingintelligence.util.MeasureFormat;

public class RoastInfoPanel extends BorderPane {
   @FXML
   private Label markedAsReferenceTextLabel;
   @FXML
   private Label weightLabel;
   @FXML
   private Tab alertTab;
   @FXML
   private GridPane alertPanel;
   private final StringProperty profile = new SimpleStringProperty();
   private final StringProperty idTag = new SimpleStringProperty();
   private final StringProperty lots = new SimpleStringProperty();
   private final ObjectProperty<Measure> startWeight = new SimpleObjectProperty();
   private final ObjectProperty<Measure> endWeight = new SimpleObjectProperty();
   private final ObjectProperty<Measure> roastValue1 = new SimpleObjectProperty();
   private final ObjectProperty<Measure> roastValue2 = new SimpleObjectProperty();
   private final StringProperty notes = new SimpleStringProperty();
   private final ObjectProperty<LotConsistencyCheck> check = new SimpleObjectProperty();
   private final MeasureFormat measureFormat = MeasureFormat.getInstance();

   public RoastInfoPanel() {
      JavaFxUtil.loadComponent(this);
      MeasureFormat measureFormat = MeasureFormat.getInstance();
      NumberFormat percentFormat = NumberFormat.getPercentInstance();
      percentFormat.setMinimumFractionDigits(0);
      percentFormat.setMaximumFractionDigits(1);
      percentFormat.setRoundingMode(RoundingMode.HALF_EVEN);
      this.markedAsReferenceTextLabel.managedProperty().bind(this.markedAsReferenceTextLabel.visibleProperty());
      this.markedAsReferenceTextLabel.visibleProperty().bind(this.markedAsReferenceTextLabel.textProperty().isNotEmpty());
      this.weightLabel
         .textProperty()
         .bind(
            Bindings.createStringBinding(
               () -> {
                  if (this.startWeight.get() != null && this.endWeight.get() != null) {
                     double weightLoss = ((Measure)this.endWeight.get()).subtract((Measure)this.startWeight.get()).getAmount()
                        / ((Measure)this.startWeight.get()).getAmount();
                     return String.format(
                        "%s/%s (%s)",
                        measureFormat.format(this.startWeight.get()),
                        measureFormat.format(this.endWeight.get()),
                        percentFormat.format(weightLoss)
                     );
                  } else {
                     return "-";
                  }
               },
               new Observable[]{this.startWeight, this.endWeight}
            )
         );
      this.check
         .addListener(
            (s, o, n) -> {
               this.alertPanel.getChildren().clear();
               if (n != null) {
                  this.alertTab.setText(n.getAlerts().size() + " " + org.cropster.roastingintelligence.util.Messages.m_("Missed goals"));
                  int row = 0;
                  Label label = new Label(org.cropster.roastingintelligence.util.Messages.m_("Missed goals"));
                  label.getStyleClass().addAll(new String[]{"input-label"});
                  this.alertPanel.getChildren().add(label);
                  if (n.getAlerts().isEmpty()) {
                     this.alertPanel
                        .add(
                           new Label(org.cropster.roastingintelligence.util.Messages.m_("This roast did not miss any goals.")), 1, row++, Integer.MAX_VALUE, 1
                        );
                  }

                  for (ProcessingAlert alert : n.getAlerts()) {
                     Node goalLabel = new Label(alert.getLocalizedMessage());
                     goalLabel.getStyleClass().addAll(new String[]{"input-label", "input-label--small"});
                     this.alertPanel.add(goalLabel, 1, row++, Integer.MAX_VALUE, 1);
                     TextFlow textFlow = new TextFlow();
                     textFlow.setMaxWidth(Double.MAX_VALUE);
                     Text reason = new Text(alert.getLocalizedReason());
                     reason.setFill(Color.DARKRED);
                     textFlow.getChildren().add(reason);
                     if (alert.getCheckedValue() != null) {
                        textFlow.getChildren().add(new Text(String.format(" — %s: ", "Value")));
                        Text value = new Text(measureFormat.format(alert.getCheckedValue()));
                        value.setFill(Color.DARKRED);
                        textFlow.getChildren().add(value);
                        textFlow.getChildren().add(new Text(String.format(" — %s: ", org.cropster.roastingintelligence.util.Messages.m_("Deviation"))));
                        Text deviation = new Text(measureFormat.format(alert.getDeviation()));
                        deviation.setFill(Color.DARKRED);
                        textFlow.getChildren().add(deviation);
                     }

                     this.alertPanel.add(textFlow, 1, row++, Integer.MAX_VALUE, 1);
                  }
               } else {
                  this.alertPanel
                     .getChildren()
                     .add(new Label(org.cropster.roastingintelligence.util.Messages.m_("There is no data available for this roast at the moment.")));
               }
            }
         );
   }

   public String getWeightLabel() {
      return org.cropster.roastingintelligence.util.Messages.m_("Start weight") + "/" + org.cropster.roastingintelligence.util.Messages.m_("End weight");
   }

   public StringProperty profileProperty() {
      return this.profile;
   }

   public String getProfile() {
      return (String)this.profile.get();
   }

   public void setProfile(String profile) {
      this.profile.set(profile);
   }

   public StringProperty idTagProperty() {
      return this.idTag;
   }

   public String getIdTag() {
      return (String)this.idTag.get();
   }

   public void setIdTag(String idTag) {
      this.idTag.set(idTag);
   }

   public StringProperty lotsProperty() {
      return this.lots;
   }

   public String getLots() {
      return (String)this.lots.get();
   }

   public void setLots(String lots) {
      this.lots.set(lots);
   }

   public ObjectProperty<Measure> startWeightProperty() {
      return this.startWeight;
   }

   public Measure getStartWeight() {
      return (Measure)this.startWeight.get();
   }

   public void setStartWeight(Measure startWeight) {
      this.startWeight.set(startWeight);
   }

   public ObjectProperty<Measure> endWeightProperty() {
      return this.endWeight;
   }

   public Measure getEndWeight() {
      return (Measure)this.endWeight.get();
   }

   public void setEndWeight(Measure endWeight) {
      this.endWeight.set(endWeight);
   }

   public ObjectProperty<Measure> roastValue1Property() {
      return this.roastValue1;
   }

   public Measure getRoastValue1() {
      return (Measure)this.roastValue1.get();
   }

   public void setRoastValue1(Measure roastValue1) {
      this.roastValue1.set(roastValue1);
   }

   public ObjectProperty<Measure> roastValue2Property() {
      return this.roastValue2;
   }

   public Measure getRoastValue2() {
      return (Measure)this.roastValue2.get();
   }

   public void setRoastValue2(Measure roastValue2) {
      this.roastValue2.set(roastValue2);
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

   public ObjectProperty<LotConsistencyCheck> checkProperty() {
      return this.check;
   }

   public LotConsistencyCheck getCheck() {
      return (LotConsistencyCheck)this.check.get();
   }

   public void setCheck(LotConsistencyCheck check) {
      this.check.set(check);
   }

   public void init(Data data, Roast roast) {
      this.setProfile(
         roast.getProfile() != null && data.getProfiles().containsKey(roast.getProfile()) ? data.getProfiles().get(roast.getProfile()).getName() : "-"
      );
      this.setIdTag(roast.getLot() != null && roast.getLot().getIdTag() != null ? roast.getLot().getIdTag() : "-");
      this.markedAsReferenceTextLabel
         .setText(
            roast.getMarkedAsReferenceDate() != null
               ? org.cropster.roastingintelligence.util.Messages.m_("Marked as reference on {0}", JavaFxUtil.formatDateTime(roast.getMarkedAsReferenceDate()))
               : null
         );
      String lots = "";

      for (LotTransaction transaction : roast.getLot().getSources()) {
         Lot lot = data.getLots().get(transaction.getLot());
         lots = lots
            + (lot != null ? String.format("[%s] %s", lot.getIdTag(), lot.getName()) : org.cropster.roastingintelligence.util.Messages.m_("Lot not available"));
         lots = lots + String.format(" (%s)", this.measureFormat.format(transaction.getWeight()));
         lots = lots + "\n";
      }

      this.setLots(lots);
      this.setStartWeight(roast.getStartWeight());
      this.setEndWeight(roast.getEndWeight());
      this.setRoastValue1(roast.getMeasures().get("roastValue"));
      this.setRoastValue2(roast.getMeasures().get("roastValue2"));
      this.setNotes(roast.getNotes() != null ? roast.getNotes() : "-");
      this.setCheck(data.getChecks().get(roast.getLot().getId()));
   }
}
