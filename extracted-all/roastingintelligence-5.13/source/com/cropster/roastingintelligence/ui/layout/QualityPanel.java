package com.cropster.roastingintelligence.ui.layout;

import com.cropster.roastingintelligence.Data;
import com.cropster.roastingintelligence.ui.JavaFxUtil;
import com.cropster.roastingintelligence.ui.components.LinkToPhysicalLabel;
import com.cropster.roastingintelligence.ui.components.LinkToSensorialLabel;
import com.cropster.roastingintelligence.ui.components.Tooltip;
import com.cropster.roastingintelligence.ui.util.GridPaneZebraStyle;
import com.cropster.roastingintelligence.ui.util.Icon;
import com.cropster.util.Collect;
import java.net.URL;
import java.text.NumberFormat;
import java.util.Collection;
import java.util.ResourceBundle;
import java.util.Map.Entry;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import org.apache.commons.lang3.StringUtils;
import org.cropster.roastingintelligence.model.Analysis;
import org.cropster.roastingintelligence.model.BlendComponent;
import org.cropster.roastingintelligence.model.Descriptor;
import org.cropster.roastingintelligence.model.Lot;
import org.cropster.roastingintelligence.model.Physical;
import org.cropster.roastingintelligence.model.Roast;

public class QualityPanel extends VBox implements Initializable {
   @FXML
   private GridPane productionCuppingPane;
   @FXML
   private GridPane greenGradingPane;
   private final NumberFormat numberFormat = NumberFormat.getNumberInstance();

   public QualityPanel() {
      JavaFxUtil.loadComponent(this);
      this.visibleProperty().bind(this.productionCuppingPane.visibleProperty().or(this.greenGradingPane.visibleProperty()));
      GridPaneZebraStyle.apply(this.productionCuppingPane, (r, c) -> r >= 1 && r % 2 != 0 ? "" : "-fx-background-color: -fx-color-neutral-20")
         .applyToExisting();
      GridPaneZebraStyle.apply(this.greenGradingPane, (r, c) -> r >= 1 && r % 2 != 0 ? "" : "-fx-background-color: -fx-color-neutral-20").applyToExisting();
   }

   public void setAnalysis(Analysis analysis, Roast roast) {
      int y = 0;
      this.productionCuppingPane.getChildren().clear();
      this.productionCuppingPane.setVisible(analysis != null);
      if (analysis != null) {
         Label l = new Label(org.cropster.roastingintelligence.util.Messages.m_("Analysis"));
         l.setMaxWidth(Double.POSITIVE_INFINITY);
         l.getStyleClass().add("statistics-separator");
         this.productionCuppingPane.add(l, 0, y);
         HBox idTagBox = new HBox();
         idTagBox.setMaxWidth(Double.POSITIVE_INFINITY);
         idTagBox.getStyleClass().addAll(new String[]{"statistics-separator"});
         LinkToSensorialLabel ll = new LinkToSensorialLabel();
         ll.getStyleClass().add("label--bold");
         ll.init(analysis);
         idTagBox.getChildren().add(ll);
         l = new Label(String.format("(%s)", roast.getLot().getIdTag()));
         l.getStyleClass().add("label--muted");
         idTagBox.getChildren().add(l);
         this.productionCuppingPane.add(idTagBox, 1, y, Integer.MAX_VALUE, 1);
         y++;
         l = new Label(org.cropster.roastingintelligence.util.Messages.m_("Final score"));
         l.setMinWidth(Double.NEGATIVE_INFINITY);
         this.productionCuppingPane.add(l, 0, y);
         Float score = analysis.getScore();
         l = new Label(score != null ? this.numberFormat.format(score) : "-");
         this.productionCuppingPane.add(l, 1, y);
         GridPane.setHalignment(l, HPos.LEFT);
         y++;
         TextFlow coloredDescriptors = new TextFlow();
         coloredDescriptors.getStyleClass().add("text-flow");
         coloredDescriptors.setPadding(new Insets(1.0, 0.0, 0.0, 0.0));

         for (Entry<Descriptor, Integer> entry : analysis.getDescriptorsCounted().entrySet()) {
            if (!coloredDescriptors.getChildren().isEmpty()) {
               coloredDescriptors.getChildren().add(new Text(", "));
            }

            Descriptor descriptor = entry.getKey();
            Text t = new Text(descriptor.getName() + (entry.getValue() > 1 ? " (" + entry.getValue() + ")" : ""));
            t.setFill(descriptor.isPositive() ? Color.BLACK : JavaFxUtil.ERROR);
            coloredDescriptors.getChildren().add(t);
         }

         if (coloredDescriptors.getChildren().isEmpty()) {
            coloredDescriptors.getChildren().add(new Text("-"));
         }

         StringBuilder comments = new StringBuilder();
         analysis.getCuppings().stream().filter(c -> StringUtils.isNotBlank(c.getNotes())).forEach(c -> {
            if (comments.length() > 0) {
               comments.append("\n");
            }

            comments.append(c.getNotes());
         });
         l = new Label(org.cropster.roastingintelligence.util.Messages.m_("Descriptors"));
         l.setMinWidth(Double.NEGATIVE_INFINITY);
         this.productionCuppingPane.add(l, 0, y);
         GridPane.setValignment(l, VPos.TOP);
         this.productionCuppingPane.add(coloredDescriptors, 1, y);
         y++;
         l = new Label(org.cropster.roastingintelligence.util.Messages.m_("Comments"));
         l.setMinWidth(Double.NEGATIVE_INFINITY);
         this.productionCuppingPane.add(l, 0, y, Integer.MAX_VALUE, 1);
         GridPane.setValignment(l, VPos.TOP);
         l = new Label(comments.length() > 0 ? comments.toString() : "-");
         l.setWrapText(true);
         this.productionCuppingPane.add(l, 1, y, Integer.MAX_VALUE, 1);
         y++;
      }
   }

   public void setBlendComponents(Collection<BlendComponent> components, Data data) {
      int y = 0;
      this.greenGradingPane.getChildren().clear();

      for (BlendComponent component : components) {
         if (!component.getLotIds().isEmpty()) {
            Long lotId = (Long)Collect.first(component.getLotIds());
            Analysis analysis = data.getAnalyses().get(lotId);
            if (analysis != null) {
               Physical physical = analysis.getPhysical();
               if (physical != null) {
                  Lot lot = data.getLots().get(lotId);
                  String lotIdTag = lot != null ? lot.getIdTag() : "?";
                  Label l = new Label(org.cropster.roastingintelligence.util.Messages.m_("Analysis"));
                  l.setMaxWidth(Double.POSITIVE_INFINITY);
                  l.getStyleClass().addAll(new String[]{"statistics-separator"});
                  this.greenGradingPane.add(l, 0, y);
                  HBox idTagBox = new HBox();
                  idTagBox.setMaxWidth(Double.POSITIVE_INFINITY);
                  idTagBox.getStyleClass().addAll(new String[]{"statistics-separator"});
                  LinkToPhysicalLabel ll = new LinkToPhysicalLabel();
                  ll.getStyleClass().add("label--bold");
                  ll.init(analysis);
                  idTagBox.getChildren().add(ll);
                  l = new Label(String.format("(%s)", lotIdTag));
                  l.getStyleClass().add("label--muted");
                  idTagBox.getChildren().add(l);
                  this.greenGradingPane.add(idTagBox, 1, y, Integer.MAX_VALUE, 1);
                  y++;
                  l = new Label(org.cropster.roastingintelligence.util.Messages.m_("Results"));
                  this.greenGradingPane.add(l, 0, y);
                  String moisture = " - ";
                  if (physical.getMoisture() != null) {
                     moisture = this.numberFormat.format(physical.getMoisture()) + " %";
                  }

                  l = new Label(moisture, Icon.MOISTURE.load("icon--muted"));
                  l.setTooltip(new Tooltip(org.cropster.roastingintelligence.util.Messages.m_("Moisture")));
                  this.greenGradingPane.add(l, 1, y);
                  String waterActivity = " - ";
                  if (physical.getWaterActivity() != null) {
                     waterActivity = this.numberFormat.format(physical.getWaterActivity()) + " AW";
                  }

                  l = new Label(waterActivity, Icon.WATER_ACTIVITY.load("icon--muted"));
                  l.setTooltip(new Tooltip(org.cropster.roastingintelligence.util.Messages.m_("Water activity")));
                  this.greenGradingPane.add(l, 2, y);
                  String density = " - ";
                  if (physical.getDensity() != null) {
                     density = this.numberFormat.format(physical.getDensity()) + " g/ml";
                  }

                  l = new Label(density, Icon.DENSITY.load("icon--muted"));
                  l.setTooltip(new Tooltip(org.cropster.roastingintelligence.util.Messages.m_("Density")));
                  this.greenGradingPane.add(l, 3, y);
                  y++;
               }
            }
         }
      }

      this.greenGradingPane.setVisible(!this.greenGradingPane.getChildren().isEmpty());
   }

   public void initialize(URL url, ResourceBundle resourceBundle) {
      JavaFxUtil.setDefaultLineSpacing(this);
   }
}
