package com.cropster.roastingintelligence.ui.layout;

import com.cropster.roastingintelligence.ui.JavaFxUtil;
import com.cropster.roastingintelligence.ui.util.GridPaneZebraStyle;
import com.cropster.util.model.Unit;
import com.cropster.util.text.DurationFormat;
import java.net.URL;
import java.util.Collection;
import java.util.Iterator;
import java.util.NavigableMap;
import java.util.ResourceBundle;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import org.cropster.roastingintelligence.model.Curve;
import org.cropster.roastingintelligence.model.Measure;
import org.cropster.roastingintelligence.model.RoastingComment;
import org.cropster.roastingintelligence.util.MeasureFormat;

public class CommentsGrid extends VBox implements Initializable {
   private static final int START_Y = 1;
   @FXML
   private GridPane gridPane;
   private final Property<Unit> temperatureUnit = new SimpleObjectProperty(Unit.CELSIUS);

   public CommentsGrid() {
      JavaFxUtil.loadComponent(this);
      GridPaneZebraStyle.apply(this.gridPane, (r, c) -> r >= 1 && r % 2 != 0 ? "-fx-background-color: -fx-color-neutral-20" : "").applyToExisting();
   }

   public Property<Unit> temperatureUnitProperty() {
      return this.temperatureUnit;
   }

   public Unit getTemperatureUnit() {
      return (Unit)this.temperatureUnit.getValue();
   }

   public void setTemperatureUnit(Unit temperatureUnit) {
      this.temperatureUnit.setValue(temperatureUnit);
   }

   public void setComments(Collection<RoastingComment> comments, Curve curve) {
      int y = 1;
      MeasureFormat measureFormat = MeasureFormat.getInstance();
      DurationFormat durationFormat = DurationFormat.getDurationInstance();

      for (RoastingComment comment : comments) {
         Measure beanTemp = null;
         if (curve != null) {
            NavigableMap<Long, Double> headMap = curve.headMap(Long.valueOf(comment.getTime()), true);
            if (!headMap.isEmpty()) {
               beanTemp = new Measure(headMap.lastEntry().getValue(), curve.getUnit());
            }
         }

         String timeLabel = durationFormat.format(comment.getTime());
         String commentLabel = "";
         if (comment.getEvent() != null) {
            commentLabel = commentLabel + org.cropster.roastingintelligence.util.Messages.m_(comment.getEvent());
            if (comment.getText() != null) {
               commentLabel = commentLabel + ": " + comment.getText();
            }
         } else {
            commentLabel = commentLabel + comment.getText();
         }

         if (beanTemp != null) {
            commentLabel = commentLabel + " @ " + measureFormat.format(beanTemp.toUnit((Unit)this.temperatureUnit.getValue()));
         }

         this.gridPane.add(new Label(timeLabel), 0, y);
         this.gridPane.add(new Label(commentLabel), 1, y);
         y++;
      }

      JavaFxUtil.setDefaultLineSpacing(this);
   }

   public void reset() {
      Iterator<Node> it = this.gridPane.getChildren().iterator();

      while (it.hasNext()) {
         Node node = it.next();
         Integer rowIndex = GridPane.getRowIndex(node);
         if (rowIndex != null && rowIndex >= 1) {
            it.remove();
         }
      }
   }

   public void initialize(URL url, ResourceBundle resourceBundle) {
      JavaFxUtil.setDefaultLineSpacing(this);
   }
}
