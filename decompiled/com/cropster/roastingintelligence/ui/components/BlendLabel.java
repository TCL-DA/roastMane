package com.cropster.roastingintelligence.ui.components;

import com.cropster.roastingintelligence.ui.JavaFxUtil;
import com.cropster.roastingintelligence.ui.util.Icon;
import java.net.URL;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import org.cropster.roastingintelligence.model.Lot;
import org.cropster.roastingintelligence.model.LotTransaction;
import org.cropster.roastingintelligence.model.Measure;
import org.cropster.roastingintelligence.model.Roast;
import org.cropster.roastingintelligence.util.MeasureFormat;
import org.girod.javafx.svgimage.SVGImage;

public class BlendLabel extends VBox implements Initializable {
   private static final String ID_TAG_FORMAT = "[%s]";
   private static final String NAME_FORMAT = "[%s] %s";
   @FXML
   private Label manualBadge;
   @FXML
   private Label profileLabel;
   @FXML
   private Button printButton;
   @FXML
   private Button openInBrowserButton;
   @FXML
   private Label titleLabel;
   @FXML
   private Label totalWeightLabel;
   @FXML
   private Label notesLabel;
   @FXML
   private GridPane subSection;
   private final MeasureFormat format = MeasureFormat.getInstance();
   private final Consumer<ActionEvent> openInBrowserAction;
   private final Consumer<ActionEvent> printAction;
   private final NumberFormat percentageFormat;

   private static boolean isProfileBlend(Lot lot) {
      return lot.getBlendProfile() != null;
   }

   private static String getProfileName(Lot lot) {
      return isProfileBlend(lot) ? lot.getBlendProfile().getName() : null;
   }

   public BlendLabel(Consumer<ActionEvent> printAction, Consumer<ActionEvent> openInBrowserAction) {
      JavaFxUtil.loadComponent(this);
      this.printAction = printAction;
      this.openInBrowserAction = openInBrowserAction;
      this.percentageFormat = NumberFormat.getPercentInstance();
      this.percentageFormat.setMinimumFractionDigits(0);
      this.percentageFormat.setMaximumFractionDigits(0);
   }

   public void initialize(URL location, ResourceBundle resources) {
      this.notesLabel.visibleProperty().bind(this.notesLabel.textProperty().isNotEmpty());
      this.printButton.setVisible(false);
   }

   public void init(Lot item, List<Object> roastList) {
      boolean isProfileBlend = isProfileBlend(item);
      this.manualBadge.setVisible(!isProfileBlend);
      this.profileLabel.setVisible(isProfileBlend);
      if (isProfileBlend) {
         this.profileLabel.setText(getProfileName(item));
         this.titleLabel.setText(String.format("[%s]", item.getIdTag()));
      } else {
         this.titleLabel.setText(String.format("[%s] %s", item.getIdTag(), item.getName()));
      }

      this.totalWeightLabel.setText(this.format.format(item.getWeight()));
      this.notesLabel.setText(item.getNotes());
      List<MeasureLabel> measures = new ArrayList<>(
         this.subSection.getChildren().stream().filter(MeasureLabel.class::isInstance).map(MeasureLabel.class::cast).toList()
      );
      List<Label> labels = new ArrayList<>(
         this.subSection.getChildren().stream().filter(n -> n instanceof Label && !(n instanceof MeasureLabel)).map(Label.class::cast).toList()
      );
      SVGImage icon = Icon.ROAST.load();
      Map<Long, Lot> roastMap = roastList.stream().map(Roast.class::cast).collect(Collectors.toMap(r -> r.getLot().getId(), Roast::getLot));
      int index = 0;
      int row = 0;

      for (LotTransaction transaction : item.getSources()) {
         Lot source = roastMap.get(transaction.getLot());
         String lotName = source != null
            ? String.format("[%s] %s", source.getIdTag(), source.getName())
            : org.cropster.roastingintelligence.util.Messages.m_("Lot not available");
         String percentage = this.percentageFormat.format(transaction.getWeight().getAmount() / item.getWeight().getAmount());
         index = this.addSubSectionLabels(labels, measures, index, row++, icon, lotName, percentage, transaction.getWeight());
         icon = Icon.EMPTY.load();
      }
   }

   private int addSubSectionLabels(
      List<Label> labels, List<MeasureLabel> measures, int index, int row, SVGImage icon, String left, String center, Measure right
   ) {
      Label leftLabel = !labels.isEmpty() ? labels.remove(0) : new Label();
      leftLabel.getStyleClass().clear();
      leftLabel.setText(left);
      leftLabel.setGraphic(JavaFxUtil.squareImage(icon, 16.0));
      leftLabel.setMaxWidth(Double.POSITIVE_INFINITY);
      leftLabel.setTextAlignment(TextAlignment.LEFT);
      GridPane.clearConstraints(leftLabel);
      GridPane.setConstraints(leftLabel, 0, row);
      GridPane.setHalignment(leftLabel, HPos.LEFT);
      this.subSection.getChildren().remove(leftLabel);
      this.subSection.getChildren().add(index++, leftLabel);
      Label centerLabel = !labels.isEmpty() ? labels.remove(0) : new Label();
      centerLabel.getStyleClass().clear();
      centerLabel.setText(center);
      centerLabel.setGraphic(null);
      centerLabel.setMaxWidth(Double.NEGATIVE_INFINITY);
      centerLabel.setTextAlignment(TextAlignment.RIGHT);
      JavaFxUtil.addStyleIfNotPresent(centerLabel.getStyleClass(), "label--for");
      GridPane.clearConstraints(centerLabel);
      GridPane.setConstraints(centerLabel, 1, row);
      GridPane.setHalignment(centerLabel, HPos.RIGHT);
      this.subSection.getChildren().remove(centerLabel);
      this.subSection.getChildren().add(index++, centerLabel);
      MeasureLabel rightLabel = !measures.isEmpty() ? measures.remove(0) : new MeasureLabel();
      rightLabel.getStyleClass().clear();
      rightLabel.setValue(right);
      rightLabel.setGraphic(null);
      rightLabel.setMaxWidth(Double.NEGATIVE_INFINITY);
      rightLabel.setTextAlignment(TextAlignment.RIGHT);
      GridPane.clearConstraints(rightLabel);
      GridPane.setConstraints(rightLabel, 2, row);
      GridPane.setHalignment(rightLabel, HPos.RIGHT);
      this.subSection.getChildren().remove(rightLabel);
      this.subSection.getChildren().add(index++, rightLabel);
      return index;
   }

   public void resetLabels() {
      this.manualBadge.setVisible(true);
      this.profileLabel.setVisible(false);
      this.profileLabel.setText(null);
      this.titleLabel.setText(null);
      this.totalWeightLabel.setText(null);
      this.notesLabel.setText(null);
      this.subSection.getChildren().clear();
   }

   @FXML
   protected void openInBrowser(ActionEvent e) {
      this.openInBrowserAction.accept(e);
   }

   @FXML
   protected void print(ActionEvent e) {
      this.printAction.accept(e);
   }
}
