package com.cropster.roastingintelligence.ui.components;

import com.cropster.roastingintelligence.ui.JavaFxUtil;
import com.cropster.roastingintelligence.ui.util.Icon;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Separator;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import org.apache.commons.lang3.StringUtils;
import org.controlsfx.control.PopOver.ArrowLocation;
import org.cropster.roastingintelligence.AppModel;
import org.cropster.roastingintelligence.model.BlendComponent;
import org.cropster.roastingintelligence.model.ComponentLot;
import org.cropster.roastingintelligence.model.Lot;
import org.cropster.roastingintelligence.model.LotTransaction;
import org.cropster.roastingintelligence.model.Measure;
import org.cropster.roastingintelligence.model.Profile;
import org.cropster.roastingintelligence.model.Roast;
import org.cropster.roastingintelligence.model.Scheduled;
import org.cropster.roastingintelligence.util.MeasureFormat;
import org.girod.javafx.svgimage.SVGImage;

public class ProductionLabel extends VBox {
   private static final String ID_TAG_FORMAT = "[%s]";
   private static final String NAME_FORMAT = "[%s] %s";
   private static final String RESIDUAL_WEIGHT_FORMAT = "%s";
   private static final String SYNCING_TEXT = org.cropster.roastingintelligence.util.Messages.m_("Syncing ...");
   private static final String DARKER_STYLE = "darker";
   private static final String LIGHTER_STYLE = "lighter";
   private static final String CHECKBOX_STYLE = "-fx-font-size: 1.2em important;";
   private static final BooleanProperty SHOW_ROAST_NOTES = new SimpleBooleanProperty(true);
   private static final BooleanProperty SHOW_GREEN_INVENTORY = new SimpleBooleanProperty(true);
   private static final BooleanProperty SHOW_BLEND_CONTRIBUTIONS = new SimpleBooleanProperty(true);
   private static final BooleanProperty SHOW_ROAST_GOALS = new SimpleBooleanProperty(true);
   private static final BooleanProperty SHOW_ROAST_TECHNICIAN = new SimpleBooleanProperty(true);
   @FXML
   private Button editButton;
   @FXML
   private Button menuButton;
   @FXML
   private Label notesLabel;
   @FXML
   private HBox profileBox;
   @FXML
   private Label titleLeftLabel;
   @FXML
   private Label titleRightLabel;
   @FXML
   private GridPane subSection;
   @FXML
   private HBox buttonsBar;
   @FXML
   private Label selectedBadge;
   @FXML
   private Label syncedBadge;
   @FXML
   private Label syncingBadge;
   @FXML
   private Label unsyncedBadge;
   @FXML
   private HBox headerTopArea;
   @FXML
   private Label profileLabel;
   @FXML
   private Label startWeightLabel;
   @FXML
   private HBox residualBox;
   @FXML
   private Label residualWeightLabel;
   @FXML
   private Separator headerSeparator;
   @FXML
   private HBox bodyBox;
   private final StringProperty titleLeft = new SimpleStringProperty();
   private final StringProperty titleRight = new SimpleStringProperty();
   private final StringProperty notes = new SimpleStringProperty();
   private final BooleanProperty selected = new SimpleBooleanProperty();
   private final MeasureFormat format = MeasureFormat.getInstance();
   private final NumberFormat percentageFormat;
   private final Consumer<ActionEvent> menuAction;
   private final Consumer<ActionEvent> editAction;

   public static BooleanProperty showRoastNotesProperty() {
      return SHOW_ROAST_NOTES;
   }

   public static BooleanProperty showGreenInventoryProperty() {
      return SHOW_GREEN_INVENTORY;
   }

   public static BooleanProperty showBlendContributionsProperty() {
      return SHOW_BLEND_CONTRIBUTIONS;
   }

   public static BooleanProperty showRoastGoalsProperty() {
      return SHOW_ROAST_GOALS;
   }

   public static BooleanProperty showRoastTechnicianProperty() {
      return SHOW_ROAST_TECHNICIAN;
   }

   public ProductionLabel(Consumer<ActionEvent> menuAction, Consumer<ActionEvent> editAction) {
      JavaFxUtil.loadComponent(this);
      this.menuAction = menuAction;
      this.editAction = editAction;
      this.percentageFormat = NumberFormat.getPercentInstance();
      this.percentageFormat.setMinimumFractionDigits(0);
      this.percentageFormat.setMaximumFractionDigits(0);
      this.notesLabel.visibleProperty().bind(this.notes.isNotEmpty());
      this.notesLabel.textProperty().bind(this.notes);
      this.syncedBadge.setVisible(false);
      this.unsyncedBadge.setVisible(false);
      this.syncingBadge.setVisible(true);
      this.headerTopArea
         .visibleProperty()
         .bind(
            this.syncedBadge
               .visibleProperty()
               .or(this.syncingBadge.visibleProperty())
               .or(this.unsyncedBadge.visibleProperty())
               .or(this.selectedBadge.visibleProperty())
               .or(this.buttonsBar.visibleProperty())
         );
      this.titleLeftLabel.textProperty().addListener((s, o, n) -> {
         this.titleLeftLabel.getStyleClass().removeAll(new String[]{"header-title--bold", "header-title--warning-bold"});
         this.titleLeftLabel.getStyleClass().add(SYNCING_TEXT.equals(n) ? "header-title--warning-bold" : "header-title--bold");
      });
   }

   public static void customizeInformation(ListView<Object> productionView, Button button) {
      CheckBox roastNotesCheckBox = new CheckBox(org.cropster.roastingintelligence.util.Messages.m_("Roast notes"));
      roastNotesCheckBox.setStyle("-fx-font-size: 1.2em important;");
      roastNotesCheckBox.selectedProperty().bindBidirectional(SHOW_ROAST_NOTES);
      roastNotesCheckBox.setOnAction(e -> productionView.refresh());
      CheckBox greenInventoryCheckBox = new CheckBox(org.cropster.roastingintelligence.util.Messages.m_("Green inventory"));
      greenInventoryCheckBox.setStyle("-fx-font-size: 1.2em important;");
      greenInventoryCheckBox.selectedProperty().bindBidirectional(SHOW_GREEN_INVENTORY);
      greenInventoryCheckBox.setOnAction(e -> productionView.refresh());
      CheckBox blendContributionsCheckBox = new CheckBox(org.cropster.roastingintelligence.util.Messages.m_("Blend contributions"));
      blendContributionsCheckBox.setStyle("-fx-font-size: 1.2em important;");
      blendContributionsCheckBox.selectedProperty().bindBidirectional(SHOW_BLEND_CONTRIBUTIONS);
      blendContributionsCheckBox.setOnAction(e -> productionView.refresh());
      CheckBox roastGoalsCheckBox = new CheckBox(org.cropster.roastingintelligence.util.Messages.m_("Roast goals"));
      roastGoalsCheckBox.setStyle("-fx-font-size: 1.2em important;");
      roastGoalsCheckBox.selectedProperty().bindBidirectional(SHOW_ROAST_GOALS);
      roastGoalsCheckBox.setOnAction(e -> productionView.refresh());
      CheckBox roastTechnicianCheckBox = new CheckBox(org.cropster.roastingintelligence.util.Messages.m_("Roast technician"));
      roastTechnicianCheckBox.setStyle("-fx-font-size: 1.2em important;");
      roastTechnicianCheckBox.selectedProperty().bindBidirectional(SHOW_ROAST_TECHNICIAN);
      roastTechnicianCheckBox.setOnAction(e -> productionView.refresh());
      VBox content = new VBox();
      content.setSpacing(8.0);
      content.getChildren()
         .addAll(new Node[]{roastNotesCheckBox, greenInventoryCheckBox, blendContributionsCheckBox, roastGoalsCheckBox, roastTechnicianCheckBox});
      PopOverBubble popover = PopOverBubble.builder()
         .arrowLocation(ArrowLocation.LEFT_TOP)
         .closable(true)
         .withoutDetachable()
         .onClose(() -> button.setDisable(false))
         .withHeader(org.cropster.roastingintelligence.util.Messages.m_("Customize"))
         .withContent(content)
         .withDarkBackground()
         .build();
      popover.showAt(button);
      button.setDisable(true);
   }

   public void resetLabels() {
      this.getStyleClass().remove("darker");
      this.titleLeft.set(null);
      this.titleRight.set(null);
      this.subSection.getChildren().clear();
      this.notes.set(null);
      this.syncedBadge.setVisible(false);
      this.unsyncedBadge.setVisible(false);
      this.syncingBadge.setVisible(false);
      this.selectedBadge.visibleProperty().unbind();
      this.selectedBadge.setVisible(false);
      this.residualWeightLabel.setText(null);
      JavaFxUtil.addStyleIfNotPresent(this.titleRightLabel.getStyleClass(), "header-title--bold");
   }

   public void init(Roast roast, int goalCount, int errorCount, AppModel appModel, List<Lot> blends) {
      JavaFxUtil.replaceStyles(this.getStyleClass(), "darker", "lighter");
      this.syncedBadge.setVisible(roast.isSynced());
      this.syncingBadge.setVisible(false);
      this.unsyncedBadge.setVisible(!roast.isSynced());
      this.selectedBadge.visibleProperty().unbind();
      this.selectedBadge.setVisible(false);
      this.buttonsBar.setVisible(roast.isSynced());
      this.updateRoastData(roast);
      Profile profile = appModel.getData().getProfiles().get(roast.getProfile());
      this.profileBox.setVisible(true);
      this.profileLabel.setText(profile != null ? profile.getName() : org.cropster.roastingintelligence.util.Messages.m_("No profile"));
      List<MeasureLabel> measures = this.getMeasureLabels();
      List<Label> labels = this.getLabels();
      int index = 0;
      int row = 0;
      boolean addSeparator = false;
      boolean notesVisible = this.setupNotes(roast);
      if (SHOW_GREEN_INVENTORY.get()) {
         index = this.setupGreenInventory(roast, appModel, labels, measures, index, row);
         row += roast.getLot().getSources().size();
         addSeparator = true;
      }

      if (!blends.isEmpty()) {
         Long id = roast.getLot().getId();
         if (SHOW_BLEND_CONTRIBUTIONS.get()) {
            if (addSeparator) {
               index = this.addSubSectionLabel(labels, index, row++, null, null);
            }

            index = this.setupBlendContributions(blends, id, labels, measures, index, row);
            row += blends.size();
            addSeparator = true;
         }

         this.setupResidualWeight(blends, id, roast, this.format);
      } else {
         this.residualBox.setVisible(false);
      }

      if (SHOW_ROAST_GOALS.get() && goalCount > 0) {
         if (addSeparator) {
            index = this.addSubSectionLabel(labels, index, row++, null, null);
         }

         index = this.addGoalsSubSectionLabels(labels, index, row++, goalCount, errorCount);
         addSeparator = true;
      }

      if (SHOW_ROAST_TECHNICIAN.get() && roast.getWorker() != null && !roast.getWorker().equals(appModel.getWorker())) {
         if (addSeparator) {
            index = this.addSubSectionLabel(labels, index, row++, null, null);
         }

         this.addSubSectionLabel(labels, index, row, Icon.EVALUATOR.load(), roast.getWorker());
         addSeparator = true;
      }

      this.headerSeparator.setVisible(notesVisible || addSeparator);
      this.bodyBox.setVisible(notesVisible || addSeparator);
      measures.forEach(l -> this.subSection.getChildren().remove(l));
      labels.forEach(l -> this.subSection.getChildren().remove(l));
   }

   public void init(Scheduled scheduled, AppModel appModel) {
      JavaFxUtil.replaceStyles(this.getStyleClass(), "lighter", "darker");
      this.syncedBadge.setVisible(false);
      this.syncingBadge.setVisible(false);
      this.unsyncedBadge.setVisible(false);
      this.selectedBadge.visibleProperty().bind(this.selectedProperty());
      this.buttonsBar.setVisible(false);
      Profile profile = appModel.getData().getProfiles().get(scheduled.getProfileId());
      this.titleLeft.set(profile != null ? profile.getName() : org.cropster.roastingintelligence.util.Messages.m_("No profile"));
      this.titleRight.set(this.format.format(scheduled.getWeight()));
      this.residualBox.setVisible(false);
      this.profileBox.setVisible(false);
      List<MeasureLabel> measures = this.getMeasureLabels();
      List<Label> labels = this.getLabels();
      int index = 0;
      int row = 0;
      boolean addSeparator = false;
      boolean notesVisible = this.setupNotes(scheduled);
      if (SHOW_GREEN_INVENTORY.get()) {
         index = this.setupGreenInventory(scheduled, profile, appModel, labels, measures, index, row);
         row += scheduled.getComponentLots().size();
         addSeparator = true;
      }

      if (SHOW_ROAST_TECHNICIAN.get() && scheduled.getRoastingPerson() != null && !scheduled.getRoastingPerson().equals(appModel.getWorker())) {
         if (addSeparator) {
            index = this.addSubSectionLabel(labels, index, row++, null, null);
         }

         this.addSubSectionLabel(labels, index, row, Icon.EVALUATOR.load(), scheduled.getRoastingPerson());
         addSeparator = true;
      }

      this.headerSeparator.setVisible(notesVisible || addSeparator);
      this.bodyBox.setVisible(notesVisible || addSeparator);
      measures.forEach(l -> this.subSection.getChildren().remove(l));
      labels.forEach(l -> this.subSection.getChildren().remove(l));
   }

   private boolean setupNotes(Roast roast) {
      return this.setupNotes(roast.getNotes());
   }

   private boolean setupNotes(Scheduled scheduled) {
      return this.setupNotes(scheduled.getNotes());
   }

   private boolean setupNotes(String notesText) {
      if (SHOW_ROAST_NOTES.get()) {
         this.notes.set(notesText);
         return StringUtils.isNotBlank(notesText);
      } else {
         this.notes.set(null);
         return false;
      }
   }

   private List<MeasureLabel> getMeasureLabels() {
      return new ArrayList<>(this.subSection.getChildren().stream().filter(MeasureLabel.class::isInstance).map(MeasureLabel.class::cast).toList());
   }

   private List<Label> getLabels() {
      return new ArrayList<>(
         this.subSection.getChildren().stream().filter(n -> n instanceof Label && !(n instanceof MeasureLabel)).map(Label.class::cast).toList()
      );
   }

   private int setupGreenInventory(Roast roast, AppModel appModel, List<Label> labels, List<MeasureLabel> measures, int index, int row) {
      SVGImage icon = Icon.COFFEE_BEAN.load();

      for (LotTransaction transaction : roast.getLot().getSources()) {
         Lot source = appModel.getData().getLots().get(transaction.getLot());
         String lotName = source != null
            ? String.format("[%s] %s", source.getIdTag(), source.getName())
            : org.cropster.roastingintelligence.util.Messages.m_("Lot not available");
         index = this.addSubSectionLabels(labels, measures, index, row++, icon, lotName, transaction.getWeight());
         icon = Icon.EMPTY.load();
      }

      return index;
   }

   private int setupGreenInventory(Scheduled scheduled, Profile profile, AppModel appModel, List<Label> labels, List<MeasureLabel> measures, int index, int row) {
      SVGImage icon = Icon.COFFEE_BEAN.load();

      for (ComponentLot component : scheduled.getComponentLots()) {
         BlendComponent blendComponent = Optional.ofNullable(profile)
            .map(Profile::getComponents)
            .stream()
            .flatMap(Collection::stream)
            .filter(c -> c.getId().equals(component.getComponentId()))
            .findFirst()
            .orElse(null);
         String lotName;
         if (component.getLotId() != null && appModel.getData().getLots().containsKey(component.getLotId())) {
            Lot lot = appModel.getData().getLots().get(component.getLotId());
            lotName = String.format("[%s] %s", lot.getIdTag(), lot.getName());
         } else if (blendComponent != null && blendComponent.getName() != null) {
            lotName = blendComponent.getName();
         } else {
            lotName = org.cropster.roastingintelligence.util.Messages.m_("Lot not available");
         }

         if (blendComponent != null && scheduled.getComponentLots().size() > 1) {
            String percentage = this.percentageFormat.format(blendComponent.getPercentage().doubleValue());
            Measure weight = scheduled.getWeight().multiply(blendComponent.getPercentage().doubleValue());
            index = this.addSubSectionLabels(labels, measures, index, row++, icon, lotName, percentage, weight);
         } else {
            index = this.addSubSectionLabel(labels, index, row++, icon, lotName);
         }

         icon = Icon.EMPTY.load();
      }

      return index;
   }

   private int setupBlendContributions(List<Lot> blends, Long id, List<Label> labels, List<MeasureLabel> measures, int index, int row) {
      SVGImage icon = Icon.SPLIT.load();

      for (Lot blend : blends) {
         LotTransaction t = blend.getSources().stream().filter(lt -> Objects.equals(lt.getLot(), id)).findFirst().orElse(null);
         if (t != null) {
            String lotName = blend.getBlendProfile() != null
               ? String.format("[%s]", blend.getIdTag())
               : String.format("[%s] %s", blend.getIdTag(), blend.getName());
            Measure w = t.getWeight();
            double a = w.getAmount();
            String percentage = this.percentageFormat.format(a / blend.getWeight().getAmount());
            index = this.addSubSectionLabels(labels, measures, index, row++, icon, lotName, percentage, w);
            icon = Icon.EMPTY.load();
         }
      }

      return index;
   }

   private void setupResidualWeight(List<Lot> blends, Long id, Roast roast, MeasureFormat format) {
      double usedWeight = blends.stream()
         .map(blend -> blend.getSources().stream().filter(lt -> Objects.equals(lt.getLot(), id)).findFirst().orElse(null))
         .filter(Objects::nonNull)
         .mapToDouble(t -> t.getWeight().getAmount())
         .sum();
      Measure residualWeight = roast.getEndWeight().subtract(new Measure(usedWeight, roast.getEndWeight().getUnit()));
      this.residualWeightLabel.setText(String.format("%s", format.format(residualWeight)));
      this.residualBox.setVisible(true);
      this.titleRightLabel.getStyleClass().removeAll(new String[]{"header-title--bold"});
   }

   private int addGoalsSubSectionLabels(List<Label> labels, int index, int row, int goalCount, int errorCount) {
      Label leftLabel = !labels.isEmpty() ? labels.remove(0) : new Label();
      leftLabel.getStyleClass().clear();
      leftLabel.setText(goalCount + " " + org.cropster.roastingintelligence.util.Messages.p_("goal", "goals", goalCount));
      leftLabel.setGraphic(JavaFxUtil.squareImage(Icon.GOAL.load(), 16.0));
      leftLabel.setMaxWidth(Double.POSITIVE_INFINITY);
      leftLabel.setTextAlignment(TextAlignment.LEFT);
      GridPane.clearConstraints(leftLabel);
      GridPane.setConstraints(leftLabel, 0, row);
      GridPane.setHalignment(leftLabel, HPos.LEFT);
      this.subSection.getChildren().remove(leftLabel);
      this.subSection.getChildren().add(index++, leftLabel);
      Label centerLabel = !labels.isEmpty() ? labels.remove(0) : new Label();
      centerLabel.getStyleClass().clear();
      centerLabel.setText(String.valueOf(goalCount - errorCount));
      centerLabel.setGraphic(Icon.SUCCESS.load("icon--success"));
      centerLabel.setMaxWidth(Double.NEGATIVE_INFINITY);
      centerLabel.setTextAlignment(TextAlignment.RIGHT);
      GridPane.clearConstraints(centerLabel);
      GridPane.setConstraints(centerLabel, 1, row);
      GridPane.setHalignment(centerLabel, HPos.RIGHT);
      this.subSection.getChildren().remove(centerLabel);
      this.subSection.getChildren().add(index++, centerLabel);
      Label rightLabel = !labels.isEmpty() ? labels.remove(0) : new Label();
      rightLabel.getStyleClass().clear();
      rightLabel.setText(String.valueOf(errorCount));
      rightLabel.setGraphic(Icon.ERROR.load("icon--error"));
      rightLabel.setMaxWidth(Double.NEGATIVE_INFINITY);
      rightLabel.setTextAlignment(TextAlignment.RIGHT);
      GridPane.clearConstraints(rightLabel);
      GridPane.setConstraints(rightLabel, 2, row);
      GridPane.setHalignment(rightLabel, HPos.RIGHT);
      this.subSection.getChildren().remove(rightLabel);
      this.subSection.getChildren().add(index++, rightLabel);
      return index;
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

   private int addSubSectionLabels(List<Label> labels, List<MeasureLabel> measures, int index, int row, SVGImage icon, String left, Measure right) {
      Label leftLabel = !labels.isEmpty() ? labels.remove(0) : new Label();
      leftLabel.getStyleClass().clear();
      leftLabel.setText(left);
      leftLabel.setGraphic(JavaFxUtil.squareImage(icon, 16.0));
      leftLabel.setMaxWidth(Double.POSITIVE_INFINITY);
      leftLabel.setTextAlignment(TextAlignment.LEFT);
      GridPane.clearConstraints(leftLabel);
      GridPane.setConstraints(leftLabel, 0, row, 2, 1);
      GridPane.setHalignment(leftLabel, HPos.LEFT);
      this.subSection.getChildren().remove(leftLabel);
      this.subSection.getChildren().add(index++, leftLabel);
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

   private int addSubSectionLabel(List<Label> labels, int index, int row, SVGImage icon, String text) {
      Label leftLabel = !labels.isEmpty() ? labels.remove(0) : new Label();
      leftLabel.getStyleClass().clear();
      leftLabel.setText(text);
      leftLabel.setGraphic(JavaFxUtil.squareImage(icon, 16.0));
      leftLabel.setMaxWidth(Double.POSITIVE_INFINITY);
      leftLabel.setTextAlignment(TextAlignment.LEFT);
      GridPane.clearConstraints(leftLabel);
      GridPane.setConstraints(leftLabel, 0, row, 3, 1);
      GridPane.setHalignment(leftLabel, HPos.LEFT);
      this.subSection.getChildren().remove(leftLabel);
      this.subSection.getChildren().add(index++, leftLabel);
      return index;
   }

   public void updateRoastData(Roast roast) {
      if (roast.getLot().getIdTag() != null) {
         this.titleLeft.set(String.format("[%s] %s", roast.getLot().getIdTag(), roast.getLot().getName()));
      } else {
         this.titleLeft.set(SYNCING_TEXT);
      }

      this.startWeightLabel.setText(this.format.format(roast.getStartWeight()));
      this.titleRight.set(this.format.format(roast.getEndWeight()));
   }

   public Button getEditButton() {
      return this.editButton;
   }

   public Button getMenuButton() {
      return this.menuButton;
   }

   public StringProperty titleLeftProperty() {
      return this.titleLeft;
   }

   public String getTitleLeft() {
      return (String)this.titleLeft.get();
   }

   public void setTitleLeft(String titleLeft) {
      this.titleLeft.set(titleLeft);
   }

   public StringProperty titleRightProperty() {
      return this.titleRight;
   }

   public String getTitleRight() {
      return (String)this.titleRight.get();
   }

   public void setTitleRight(String titleRight) {
      this.titleRight.set(titleRight);
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

   public BooleanProperty selectedProperty() {
      return this.selected;
   }

   public boolean isSelected() {
      return this.selected.get();
   }

   public void setSelected(boolean selected) {
      this.selected.set(selected);
   }

   @FXML
   protected void edit(ActionEvent e) {
      this.editAction.accept(e);
   }

   @FXML
   protected void showMenu(ActionEvent e) {
      this.menuAction.accept(e);
   }
}
