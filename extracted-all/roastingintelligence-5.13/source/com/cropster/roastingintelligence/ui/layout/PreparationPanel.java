package com.cropster.roastingintelligence.ui.layout;

import com.cropster.roastingintelligence.ui.JavaFxUtil;
import com.cropster.roastingintelligence.ui.components.ComboBoxHighlight;
import com.cropster.roastingintelligence.ui.components.MeasureEditor;
import com.cropster.roastingintelligence.ui.components.Tooltip;
import com.cropster.roastingintelligence.ui.components.UserPanel;
import com.cropster.roastingintelligence.ui.util.ComboBoxSearch;
import com.cropster.roastingintelligence.ui.util.Icon;
import com.cropster.roastingintelligence.ui.util.LotSearch;
import com.cropster.roastingintelligence.ui.util.NumberInputValidBinding;
import com.cropster.roastingintelligence.ui.util.Validators;
import com.cropster.util.model.Unit;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Vector;
import java.util.stream.Stream;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableBooleanValue;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ComboBoxBase;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.text.TextAlignment;
import local.OfflineBootstrap;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.cropster.roastingintelligence.AppModel;
import org.cropster.roastingintelligence.measurement.MeasurementSourceFactory;
import org.cropster.roastingintelligence.model.BlendComponent;
import org.cropster.roastingintelligence.model.ComponentLot;
import org.cropster.roastingintelligence.model.Lot;
import org.cropster.roastingintelligence.model.Measure;
import org.cropster.roastingintelligence.model.Profile;
import org.cropster.roastingintelligence.model.Scheduled;
import org.cropster.roastingintelligence.util.MathUtil;
import org.cropster.roastingintelligence.util.MeasureFormat;
import org.cropster.roastingintelligence.util.StringUtil;

public class PreparationPanel extends GridPane implements Initializable {
   @FXML
   private ComboBoxHighlight<Profile> profileSelect;
   @FXML
   private MeasureEditor totalWeightField;
   @FXML
   private UserPanel userPanel;
   private ComboBoxSearch<Profile> profileSearch;
   private final List<BlendComponent> components = new ArrayList<>();
   private final List<Label> lotLabels = new ArrayList<>();
   private final List<ComboBox<Lot>> lotCombos = new ArrayList<>();
   private final Map<ComboBox<Lot>, LotSearch> lotSearches = new LinkedHashMap<>();
   private final Map<ComboBox<Lot>, Label> percentageLabels = new HashMap<>();
   private final Map<BlendComponent, MeasureEditor> weightFields = new LinkedHashMap<>();
   private final Map<ComboBox<Lot>, List<ComboBox<Lot>>> replacementLotCombos = new LinkedHashMap<>();
   private final Map<ComboBox<Lot>, List<HBox>> replacementLotRows = new LinkedHashMap<>();
   private BooleanBinding totalWeightInvalidBinding;
   private AppModel appModel;
   private boolean updatingReplacementLots = false;
   private final MeasureFormat measureFormat = MeasureFormat.getInstance();
   private final NumberFormat percentageFormat;
   private final IntegerProperty replacementStateVersion = new SimpleIntegerProperty(0);
   private SimpleObjectProperty<Class<? extends MeasurementSourceFactory>> autoStartSourceFactory = new SimpleObjectProperty();

   public PreparationPanel() {
      JavaFxUtil.loadComponent(this);
      this.percentageFormat = NumberFormat.getPercentInstance();
      this.percentageFormat.setMaximumFractionDigits(2);
   }

   public void initialize(URL location, ResourceBundle resources) {
      TextField pComboEditor = this.profileSelect.getEditor();
      this.profileSearch = new ComboBoxSearch<>(this.profileSelect);
      this.profileSelect
         .highlightProperty()
         .bind(
            Bindings.createObjectBinding(
               () -> this.getAutoStartSourceFactory() != null
                        && (this.profileSelect.valueProperty().get() == null || StringUtils.isBlank(pComboEditor.getText()))
                        && !this.profileSelect.isShowing()
                     ? ComboBoxHighlight.Status.WARNING
                     : ComboBoxHighlight.Status.NONE,
               new Observable[]{
                  this.autoStartSourceFactory, this.profileSelect.valueProperty(), pComboEditor.textProperty(), this.profileSelect.showingProperty()
               }
            )
         );
      this.totalWeightField.valueProperty().addListener((s, o, n) -> {
         this.validateRemainingWeights();
         this.updateAllReplacementLots();
      });
      this.totalWeightField.getInput().focusedProperty().addListener((s, o, n) -> {
         if (!n) {
            this.distributeWeightToComponents();
         }
      });
      this.totalWeightField.setOnKeyReleased(e -> {
         if (KeyCode.ENTER.equals(e.getCode())) {
            this.distributeWeightToComponents();
         }
      });
      this.totalWeightInvalidBinding = Bindings.createBooleanBinding(() -> false, new Observable[]{this.totalWeightField.getInput().textProperty()});
      JavaFxUtil.setDefaultLineSpacing(this);
   }

   private BigDecimal getRoastPercentage(int index) {
      return this.components.size() > index ? this.components.get(index).getPercentage() : BigDecimal.ONE;
   }

   private void distributeWeightToComponents() {
      if (this.totalWeightField.getValue() != null && !this.weightFields.isEmpty()) {
         this.totalWeightField.setDisable(true);
         BigDecimal totalWeight = BigDecimal.valueOf(this.totalWeightField.getValue().doubleValue());
         totalWeight = totalWeight.setScale(3, RoundingMode.HALF_EVEN);
         BigDecimal[] percentages = new BigDecimal[this.weightFields.size()];

         for (int i = 0; i < this.weightFields.size(); i++) {
            percentages[i] = this.getRoastPercentage(i);
         }

         Iterator<MeasureEditor> it = this.weightFields.values().iterator();
         BigDecimal[] weights = MathUtil.distribute(totalWeight, percentages);

         for (BigDecimal weight : weights) {
            it.next().setValue(weight.doubleValue());
         }

         this.totalWeightField.setDisable(false);
      }
   }

   private Measure calcTotalWeight() {
      Measure totalWeight = new Measure(0.0, this.totalWeightField.getUnit());

      for (MeasureEditor weightField : this.weightFields.values()) {
         if (weightField.getMeasure() != null) {
            totalWeight = totalWeight.add(weightField.getMeasure());
         }
      }

      return totalWeight;
   }

   public void updateLotLabels(AppModel appModel) {
      for (int i = 0; i < this.lotLabels.size(); i++) {
         String text = null;
         if (this.components.size() > i) {
            BlendComponent component = this.components.get(i);
            text = Integer.toString(i + 1);
            Label label = this.lotLabels.get(i);
            label.setText(StringUtil.abbreviateString(text, 20));
            label.setTooltip(StringUtils.isNotBlank(component.getName()) ? new Tooltip(component.getName()) : null);
            JavaFxUtil.addStyleIfNotPresent(label.getStyleClass(), "tag");
            JavaFxUtil.addStyleIfNotPresent(label.getStyleClass(), "tag--badge");
         }
      }
   }

   public void init(AppModel appModel) {
      this.appModel = appModel;
      this.bumpReplacementState();
      this.components.clear();
      this.lotLabels.clear();
      this.lotCombos.clear();
      this.lotSearches.clear();
      this.weightFields.clear();
      this.replacementLotCombos.clear();
      this.replacementLotRows.clear();
      this.totalWeightInvalidBinding = new NumberInputValidBinding(this.totalWeightField.getInput().textProperty(), v -> {
         double value = v.doubleValue();
         Double maximum = appModel.getMaxStartWeight();
         return value < 0.0 || maximum != null && (double)Double.compare(value, maximum) > 0.0;
      });
      Validators.setValidator(this.totalWeightField, appModel.getMaxStartWeight())
         .setStatusProvider(
            textField -> this.totalWeightInvalidBinding.get() ? Validators.Status.ERROR : Validators.Status.NONE, this.totalWeightInvalidBinding
         );
      this.totalWeightField.setMinWidth(90.0);
      this.totalWeightField.setUnit(appModel.getPreferences().getWeightUnit());
      GridPane.setColumnSpan(this.totalWeightField, Integer.MAX_VALUE);
      this.getColumnConstraints().clear();
      this.getChildren().remove(8, this.getChildren().size());
      if (appModel.getSelectedProfile() != null && appModel.getSelectedProfile().getComponents().size() > 1) {
         this.getColumnConstraints().add(JavaFxUtil.newColumnConstraints(Priority.NEVER));
      }

      this.getColumnConstraints().add(JavaFxUtil.newColumnConstraints(Priority.ALWAYS));
      this.getColumnConstraints().add(JavaFxUtil.newColumnConstraints(Priority.NEVER));
      this.getColumnConstraints().add(JavaFxUtil.newColumnConstraints(Priority.NEVER));
      int x = 0;
      int y = 6;
      int i = 0;
      boolean singleOrigin = true;

      for (Iterator<BlendComponent> it = appModel.getSelectedProfile() != null ? appModel.getSelectedProfile().getComponents().iterator() : null;
         i++ == 0 || it != null && it.hasNext();
         y++
      ) {
         BlendComponent component = it != null && it.hasNext() ? it.next() : null;
         Label lotLabel = null;
         if (component != null) {
            this.components.add(component);
            if (appModel.getSelectedProfile().getComponents().size() > 1) {
               singleOrigin = false;
               lotLabel = new Label();
               lotLabel.setMinWidth(20.0);
               lotLabel.setAlignment(Pos.CENTER);
               lotLabel.setTextAlignment(TextAlignment.CENTER);
               this.lotLabels.add(lotLabel);
               this.add(lotLabel, x++, y);
            }
         }

         ComboBoxHighlight<Lot> lotCombo = new ComboBoxHighlight<>();
         TextField lotComboEditor = lotCombo.getEditor();
         int index = this.lotCombos.size();
         lotCombo.highlightProperty()
            .bind(
               Bindings.createObjectBinding(
                  () -> {
                     if (this.getAutoStartSourceFactory() != null
                        && (lotCombo.valueProperty().get() == null || StringUtils.isBlank(lotComboEditor.getText()))
                        && !lotCombo.isShowing()) {
                        return ComboBoxHighlight.Status.WARNING;
                     } else {
                        return this.isRemainingWeightValid(index) ? ComboBoxHighlight.Status.NONE : ComboBoxHighlight.Status.WARNING;
                     }
                  },
                  new Observable[]{
                     this.autoStartSourceFactory,
                     lotCombo.valueProperty(),
                     lotComboEditor.textProperty(),
                     lotCombo.showingProperty(),
                     this.totalWeightInvalidBinding,
                     this.replacementStateVersion
                  }
               )
            );
         lotCombo.setMinWidth(100.0);
         lotCombo.setMaxWidth(Double.MAX_VALUE);
         lotCombo.setFocusTraversable(false);
         lotCombo.setItems(this.createSelectableLots(appModel.getData().getLots().values()));
         this.lotSearches.put(lotCombo, new LotSearch(lotCombo));
         lotCombo.setCellFactory(v -> new ListCell<Lot>() {
               protected void updateItem(Lot item, boolean empty) {
                  super.updateItem(item, empty);
                  this.getStyleClass().remove("combo-box--highlight");
                  if (item != null) {
                     String text = item.toString();
                     if (item.getProject() != null) {
                        text = item.getProject().getName() + " - " + text;
                     }

                     this.setText(text);
                     if (component != null && component.getLotIds().contains(item.getId())) {
                        this.getStyleClass().add("combo-box--highlight");
                     }
                  } else {
                     this.setText("");
                  }
               }
            });
         lotCombo.valueProperty().addListener((v, o, n) -> {
            for (ComboBox<Lot> otherCombo : this.lotCombos) {
               if (otherCombo != lotCombo && n != null && n.equals(otherCombo.getValue())) {
                  otherCombo.setValue(null);
               }
            }

            this.validateRemainingWeights();
            this.updateReplacementLots(lotCombo, index);
         });
         this.lotCombos.add(lotCombo);
         this.add(lotCombo, x++, y, lotLabel != null ? 1 : Integer.MAX_VALUE, 1);
         if (lotLabel == null) {
            x++;
         }

         if (!singleOrigin) {
            MeasureEditor weightInput = new MeasureEditor(appModel.getPreferences().getWeightUnit());
            weightInput.setEditable(true);
            weightInput.setMinWidth(90.0);
            Validators.setValidator(weightInput, appModel.getMaxStartWeight());
            weightInput.valueProperty().addListener((s, o, n) -> {
               this.totalWeightField.setMeasure(this.calcTotalWeight());
               this.validateRemainingWeights();
               this.updateReplacementLots(lotCombo, index);
            });
            this.weightFields.put(component, weightInput);
            this.add(weightInput, x++, y);
            String percentage = this.percentageFormat.format(component.getPercentage());
            Label label = new Label(percentage);
            label.setMinWidth(Double.NEGATIVE_INFINITY);
            label.setMaxWidth(Double.NEGATIVE_INFINITY);
            GridPane.setHalignment(label, HPos.RIGHT);
            this.percentageLabels.put(lotCombo, label);
            this.add(label, x++, y);
         }

         x = 0;
      }

      this.getRowConstraints().clear();

      for (int j = 0; j < y; j++) {
         RowConstraints rc = new RowConstraints();
         rc.setFillHeight(false);
         this.getRowConstraints().add(rc);
      }

      this.updateLotLabels(appModel);
      this.distributeWeightToComponents();
      JavaFxUtil.setDefaultLineSpacing(this);
   }

   public void resetLots(AppModel appModel, Long[] selectedComponentLotIds) {
      for (int i = 0; i < this.lotCombos.size(); i++) {
         ComboBox<Lot> lotCombo = this.lotCombos.get(i);
         BlendComponent component = this.components.size() > i ? this.components.get(i) : null;
         Vector<Lot> orderedLots = new Vector<>();
         Collection<Lot> allAvailableLots = this.createSelectableLots(appModel.getData().getLots().values());
         if (component != null) {
            orderedLots.addAll(this.getComponentLots(appModel, component));
            orderedLots.addAll(CollectionUtils.subtract(allAvailableLots, orderedLots));
         } else {
            orderedLots.addAll(allAvailableLots);
         }

         this.lotSearches.get(lotCombo).reset(orderedLots);
         Long selectedComponentLotId = selectedComponentLotIds.length > i ? selectedComponentLotIds[i] : null;
         Lot selectedComponentLot = selectedComponentLotId != null ? appModel.getData().getLots().get(selectedComponentLotId) : null;
         if (selectedComponentLot != null && orderedLots.contains(selectedComponentLot)) {
            lotCombo.setValue(selectedComponentLot);
         } else {
            List<Lot> fallbackCandidates = (List<Lot>)(component != null ? this.getComponentLots(appModel, component) : new ArrayList<>(orderedLots));
            lotCombo.setValue(this.pickPrimaryFallbackLot(fallbackCandidates, lotCombo));
         }

         this.clearReplacementLots(lotCombo);
         this.updateReplacementLots(lotCombo, i);
      }

      for (int i = 0; i < this.lotCombos.size(); i++) {
         ComboBox<Lot> lotCombox = this.lotCombos.get(i);
         if (lotCombox.getValue() == null) {
            BlendComponent componentx = this.components.size() > i ? this.components.get(i) : null;
            List<Lot> candidates = (List<Lot>)(componentx != null ? this.getComponentLots(appModel, componentx) : new ArrayList<>(lotCombox.getItems()));
            lotCombox.setValue(this.pickPrimaryFallbackLot(candidates, lotCombox));
         }
      }
   }

   private FilteredList<Lot> createSelectableLots(Collection<Lot> lots) {
      return new FilteredList(FXCollections.observableArrayList(lots), this::isSelectableLot);
   }

   private boolean isSelectableLot(Lot lot) {
      return lot != null && (lot.getWeight() == null || lot.getWeight().toUnit(this.getUnit()).getAmount() > 0.0);
   }

   private List<Lot> getComponentLots(AppModel appModel, BlendComponent component) {
      List<Lot> componentLots = new ArrayList<>();

      for (Long componentLotId : component.getLotIds()) {
         Lot componentLot = appModel.getData().getLots().get(componentLotId);
         if (this.isSelectableLot(componentLot)) {
            componentLots.add(componentLot);
         }
      }

      return componentLots;
   }

   private Lot pickPrimaryFallbackLot(List<Lot> candidates, ComboBox<Lot> targetCombo) {
      return candidates != null && !candidates.isEmpty() ? candidates.get(0) : null;
   }

   private List<Lot> getAvailableReplacementLots(int componentIndex, AppModel appModel) {
      List<Lot> availableLots = new ArrayList<>();
      if (componentIndex >= this.components.size()) {
         return availableLots;
      } else {
         BlendComponent component = this.components.get(componentIndex);
         List<Lot> componentLots = this.getComponentLots(appModel, component);
         List<Lot> alreadySelected = new ArrayList<>();
         ComboBox<Lot> primaryLotCombo = componentIndex < this.lotCombos.size() ? this.lotCombos.get(componentIndex) : null;
         if (primaryLotCombo != null) {
            Lot primaryLot = (Lot)primaryLotCombo.getValue();
            if (primaryLot != null) {
               alreadySelected.add(primaryLot);
            }
         }

         List<ComboBox<Lot>> existingReplacements = this.replacementLotCombos.get(primaryLotCombo);
         if (existingReplacements != null) {
            for (ComboBox<Lot> rc : existingReplacements) {
               if (rc.getValue() != null) {
                  alreadySelected.add((Lot)rc.getValue());
               }
            }
         }

         for (Lot lot : componentLots) {
            if (!alreadySelected.contains(lot)) {
               availableLots.add(lot);
            }
         }

         return availableLots;
      }
   }

   private void updateAllReplacementLots() {
      for (int i = 0; i < this.lotCombos.size(); i++) {
         this.updateReplacementLots(this.lotCombos.get(i), i);
      }
   }

   private void updateReplacementLots(ComboBox<Lot> primaryLotCombo, int componentIndex) {
      if (!this.updatingReplacementLots) {
         this.updatingReplacementLots = true;

         try {
            if (this.appModel == null || !this.appModel.getPreferences().isAutomaticallyReplaceLotEnabled()) {
               this.clearReplacementLots(primaryLotCombo);
               return;
            }

            BlendComponent component = componentIndex < this.components.size() ? this.components.get(componentIndex) : null;
            if (component == null || primaryLotCombo.getValue() == null) {
               this.clearReplacementLots(primaryLotCombo);
               return;
            }

            if (this.isPrimaryWeightValid(componentIndex)) {
               this.clearReplacementLots(primaryLotCombo);
            } else {
               this.ensureReplacementLotsUntilCovered(primaryLotCombo, componentIndex);
            }
         } finally {
            this.updatingReplacementLots = false;
         }
      }
   }

   private void ensureReplacementLotsUntilCovered(ComboBox<Lot> primaryLotCombo, int componentIndex) {
      while (!this.isRemainingWeightValid(componentIndex)) {
         List<Lot> availableLots = this.getAvailableReplacementLots(componentIndex, this.appModel);
         if (!availableLots.isEmpty()) {
            this.createReplacementLotCombo(primaryLotCombo, componentIndex, availableLots);
            continue;
         }
         break;
      }

      List<ComboBox<Lot>> replacementCombos = this.replacementLotCombos.get(primaryLotCombo);

      while (replacementCombos != null && !replacementCombos.isEmpty()) {
         ComboBox<Lot> lastReplacementCombo = replacementCombos.get(replacementCombos.size() - 1);
         Lot lastReplacementLot = (Lot)lastReplacementCombo.getValue();
         if (lastReplacementLot == null) {
            this.removeLastReplacementLotCombo(primaryLotCombo);
            replacementCombos = this.replacementLotCombos.get(primaryLotCombo);
         } else {
            double targetWeight = this.getTargetWeight(componentIndex);
            double availableWeight = this.getSelectedLotsWeight(componentIndex) - this.lotWeightOrZero(lastReplacementLot);
            if (!(targetWeight > 0.0) || !(availableWeight >= targetWeight)) {
               break;
            }

            this.removeLastReplacementLotCombo(primaryLotCombo);
            replacementCombos = this.replacementLotCombos.get(primaryLotCombo);
         }
      }

      this.refreshReplacementComboItems(primaryLotCombo, componentIndex);
      this.bumpReplacementState();
   }

   private boolean isPrimaryWeightValid(int componentIndex) {
      if (componentIndex >= this.lotCombos.size()) {
         return true;
      } else {
         ComboBox<Lot> primaryLotCombo = this.lotCombos.get(componentIndex);
         Lot primaryLot = (Lot)primaryLotCombo.getValue();
         if (primaryLot == null) {
            return true;
         } else {
            double targetWeight = this.getTargetWeight(componentIndex);
            if (targetWeight <= 0.0) {
               return true;
            } else {
               double primaryLotWeight = this.lotWeightOrZero(primaryLot);
               return primaryLotWeight >= targetWeight;
            }
         }
      }
   }

   private double getTargetWeight(int componentIndex) {
      if (this.lotCombos.size() > 1) {
         BlendComponent component = this.components.size() > componentIndex ? this.components.get(componentIndex) : null;
         MeasureEditor weightField = this.weightFields.get(component);
         return weightField != null && weightField.getValue() != null ? weightField.getValue().doubleValue() : 0.0;
      } else {
         return this.totalWeightField.getValue() == null ? 0.0 : this.totalWeightField.getValue().doubleValue();
      }
   }

   private double getSelectedLotsWeight(int componentIndex) {
      if (componentIndex >= this.lotCombos.size()) {
         return 0.0;
      } else {
         ComboBox<Lot> primaryLotCombo = this.lotCombos.get(componentIndex);
         Lot primaryLot = (Lot)primaryLotCombo.getValue();
         double availableWeight = 0.0;
         if (primaryLot != null) {
            availableWeight += this.lotWeightOrZero(primaryLot);
         }

         List<ComboBox<Lot>> replacementCombos = this.replacementLotCombos.get(primaryLotCombo);
         if (replacementCombos != null) {
            for (ComboBox<Lot> replacementCombo : replacementCombos) {
               Lot replacementLot = (Lot)replacementCombo.getValue();
               if (replacementLot != null) {
                  availableWeight += this.lotWeightOrZero(replacementLot);
               }
            }
         }

         return availableWeight;
      }
   }

   private void refreshReplacementComboItems(ComboBox<Lot> primaryLotCombo, int componentIndex) {
      List<ComboBox<Lot>> replacementCombos = this.replacementLotCombos.get(primaryLotCombo);
      if (replacementCombos != null) {
         for (ComboBox<Lot> replacementCombo : replacementCombos) {
            Lot currentValue = (Lot)replacementCombo.getValue();
            List<Lot> options = new ArrayList<>(this.getComponentLots(this.appModel, this.components.get(componentIndex)));
            Lot primaryLot = (Lot)primaryLotCombo.getValue();
            options.remove(primaryLot);

            for (ComboBox<Lot> otherCombo : replacementCombos) {
               if (otherCombo != replacementCombo) {
                  options.remove(otherCombo.getValue());
               }
            }

            replacementCombo.setItems(this.createSelectableLots(options));
            if (currentValue != null && options.contains(currentValue)) {
               replacementCombo.setValue(currentValue);
            } else if (!options.isEmpty()) {
               replacementCombo.setValue(options.get(0));
            } else {
               replacementCombo.setValue(null);
            }
         }
      }
   }

   private void removeLastReplacementLotCombo(ComboBox<Lot> primaryLotCombo) {
      List<ComboBox<Lot>> replacementCombos = this.replacementLotCombos.get(primaryLotCombo);
      if (replacementCombos != null && !replacementCombos.isEmpty()) {
         ComboBox<Lot> replacementCombo = replacementCombos.remove(replacementCombos.size() - 1);
         List<HBox> replacementRows = this.replacementLotRows.get(primaryLotCombo);
         HBox replacementRow = replacementRows != null && !replacementRows.isEmpty() ? replacementRows.remove(replacementRows.size() - 1) : null;
         int row = replacementRow != null ? this.getRow(replacementRow) : this.getRow(replacementCombo);
         if (replacementRow != null) {
            this.getChildren().remove(replacementRow);
         } else {
            this.getChildren().remove(replacementCombo);
         }

         this.shiftRowsUpFrom(row);
         this.bumpReplacementState();
      }
   }

   private void createReplacementLotCombo(ComboBox<Lot> primaryLotCombo, int componentIndex, List<Lot> availableLots) {
      List<ComboBox<Lot>> replacementCombos = this.replacementLotCombos.computeIfAbsent(primaryLotCombo, k -> new ArrayList<>());
      List<HBox> rows = this.replacementLotRows.computeIfAbsent(primaryLotCombo, k -> new ArrayList<>());
      int primaryRow = GridPane.getRowIndex(primaryLotCombo);
      int primaryCol = GridPane.getColumnIndex(primaryLotCombo);
      int insertRow = primaryRow + replacementCombos.size() + 1;
      this.shiftRowsDownFrom(insertRow);
      ComboBox<Lot> replacementCombo = new ComboBoxHighlight<>();
      replacementCombo.setItems(this.createSelectableLots(availableLots));
      replacementCombo.setMinWidth(100.0);
      replacementCombo.setMaxWidth(Double.MAX_VALUE);
      replacementCombo.setFocusTraversable(false);
      replacementCombo.setCellFactory(v -> new ListCell<Lot>() {
            protected void updateItem(Lot item, boolean empty) {
               super.updateItem(item, empty);
               this.getStyleClass().remove("combo-box--highlight");
               if (item != null) {
                  String text = item.toString();
                  if (item.getProject() != null) {
                     text = item.getProject().getName() + " - " + text;
                  }

                  this.setText(text);
               } else {
                  this.setText("");
               }
            }
         });
      replacementCombo.setValue(availableLots.get(0));
      replacementCombo.valueProperty().addListener((v, o, n) -> {
         if (n != null) {
            this.removeLotFromOtherCombosInChain(n, replacementCombo, primaryLotCombo);
         }

         this.validateRemainingWeights();
         this.bumpReplacementState();
         this.updateReplacementLots(primaryLotCombo, componentIndex);
      });
      replacementCombos.add(replacementCombo);
      HBox replacementRow = this.createReplacementRow(replacementCombo);
      rows.add(replacementRow);
      int colspan = this.lotLabels.isEmpty() ? GridPane.getColumnSpan(primaryLotCombo) : 1;
      this.add(replacementRow, primaryCol, insertRow, colspan, 1);
   }

   private HBox createReplacementRow(ComboBox<Lot> replacementCombo) {
      Label replacementIconLabel = new Label(null, Icon.ARROW_RIGHT.load("icon--muted"));
      replacementIconLabel.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
      replacementCombo.setMaxWidth(Double.MAX_VALUE);
      HBox.setHgrow(replacementCombo, Priority.ALWAYS);
      HBox replacementRow = new HBox(8.0, new Node[]{replacementIconLabel, replacementCombo});
      replacementRow.setAlignment(Pos.CENTER_LEFT);
      return replacementRow;
   }

   private void removeLotFromOtherCombosInChain(Lot lot, ComboBox<Lot> sourceCombo, ComboBox<Lot> chainPrimaryCombo) {
      if (chainPrimaryCombo != sourceCombo && lot.equals(chainPrimaryCombo.getValue())) {
         chainPrimaryCombo.setValue(null);
      }

      List<ComboBox<Lot>> chainCombos = this.replacementLotCombos.get(chainPrimaryCombo);
      if (chainCombos != null) {
         for (ComboBox<Lot> otherCombo : chainCombos) {
            if (otherCombo != sourceCombo && lot.equals(otherCombo.getValue())) {
               otherCombo.setValue(null);
            }
         }
      }
   }

   private void clearReplacementLots(ComboBox<Lot> primaryLotCombo) {
      List<ComboBox<Lot>> replacementCombos = this.replacementLotCombos.get(primaryLotCombo);
      List<HBox> replacementRows = this.replacementLotRows.get(primaryLotCombo);
      if (replacementCombos != null && !replacementCombos.isEmpty() || replacementRows != null && !replacementRows.isEmpty()) {
         List<Integer> rowsToRemove = new ArrayList<>();
         if (replacementRows != null && !replacementRows.isEmpty()) {
            for (HBox replacementRow : replacementRows) {
               rowsToRemove.add(this.getRow(replacementRow));
               this.getChildren().remove(replacementRow);
            }

            replacementRows.clear();
         } else if (replacementCombos != null) {
            for (ComboBox<Lot> replacementCombo : replacementCombos) {
               rowsToRemove.add(this.getRow(replacementCombo));
               this.getChildren().remove(replacementCombo);
            }
         }

         if (replacementCombos != null) {
            replacementCombos.clear();
         }

         rowsToRemove.sort((a, b) -> Integer.compare(b, a));

         for (Integer row : rowsToRemove) {
            this.shiftRowsUpFrom(row);
         }

         this.bumpReplacementState();
      }
   }

   private void bumpReplacementState() {
      this.replacementStateVersion.set(this.replacementStateVersion.get() + 1);
   }

   private int getRow(Node node) {
      Integer row = GridPane.getRowIndex(node);
      return row == null ? 0 : row;
   }

   private void shiftRowsDownFrom(int row) {
      for (Node child : new ArrayList(this.getChildren())) {
         int childRow = this.getRow(child);
         if (childRow >= row) {
            GridPane.setRowIndex(child, childRow + 1);
         }
      }

      RowConstraints rc = new RowConstraints();
      rc.setFillHeight(false);
      if (this.getRowConstraints().size() >= row) {
         this.getRowConstraints().add(row, rc);
      } else {
         this.getRowConstraints().add(rc);
      }
   }

   private void shiftRowsUpFrom(int row) {
      for (Node child : new ArrayList(this.getChildren())) {
         int childRow = this.getRow(child);
         if (childRow > row) {
            GridPane.setRowIndex(child, childRow - 1);
         }
      }

      if (row >= 0 && row < this.getRowConstraints().size()) {
         this.getRowConstraints().remove(row);
      }
   }

   public void validateRemainingWeights() {
      for (int i = 0; i < this.lotCombos.size(); i++) {
         if (this.lotCombos.size() > 1) {
            ComboBox<Lot> lotCombo = this.lotCombos.get(i);
            Label percentageLabel = this.percentageLabels.get(lotCombo);
            if (percentageLabel != null && percentageLabel.getParent() != null) {
               percentageLabel.getParent().requestLayout();
            }
         }
      }
   }

   private void refreshPrimaryLotItems() {
      for (int i = 0; i < this.lotCombos.size(); i++) {
         ComboBox<Lot> lotCombo = this.lotCombos.get(i);
         Lot currentValue = (Lot)lotCombo.getValue();
         BlendComponent component = this.components.size() > i ? this.components.get(i) : null;
         Vector<Lot> orderedLots = new Vector<>();
         Collection<Lot> allAvailableLots = this.createSelectableLots(this.appModel.getData().getLots().values());
         if (component != null) {
            orderedLots.addAll(this.getComponentLots(this.appModel, component));
            orderedLots.addAll(CollectionUtils.subtract(allAvailableLots, orderedLots));
         } else {
            orderedLots.addAll(allAvailableLots);
         }

         this.lotSearches.get(lotCombo).reset(orderedLots);
         List<Lot> fallbackCandidates = (List<Lot>)(component != null ? this.getComponentLots(this.appModel, component) : new ArrayList<>(orderedLots));
         if (currentValue != null) {
            lotCombo.setValue(orderedLots.contains(currentValue) ? currentValue : this.pickPrimaryFallbackLot(fallbackCandidates, lotCombo));
         } else if (!fallbackCandidates.isEmpty()) {
            lotCombo.setValue(this.pickPrimaryFallbackLot(fallbackCandidates, lotCombo));
         }
      }

      for (int i = 0; i < this.lotCombos.size(); i++) {
         ComboBox<Lot> lotCombox = this.lotCombos.get(i);
         if (lotCombox.getValue() == null) {
            BlendComponent componentx = this.components.size() > i ? this.components.get(i) : null;
            List<Lot> candidates = (List<Lot>)(componentx != null ? this.getComponentLots(this.appModel, componentx) : new ArrayList<>(lotCombox.getItems()));
            lotCombox.setValue(this.pickPrimaryFallbackLot(candidates, lotCombox));
         }
      }
   }

   public void refreshReplacementLots() {
      if (this.appModel != null) {
         if (this.appModel.getPreferences().isAutomaticallyReplaceLotEnabled()) {
            this.refreshPrimaryLotItems();
            this.bumpReplacementState();
            this.updateAllReplacementLots();
            this.validateRemainingWeights();
         } else {
            for (ComboBox<Lot> lotCombo : this.lotCombos) {
               this.clearReplacementLots(lotCombo);
            }

            this.bumpReplacementState();
            this.validateRemainingWeights();
         }
      }
   }

   private boolean isInsufficientWeightValidationEnabled() {
      return this.appModel != null && this.appModel.getPreferences().isAutomaticallyReplaceLotEnabled();
   }

   public boolean isRemainingWeightValid(int index) {
      if (index >= this.lotCombos.size()) {
         return false;
      } else {
         ComboBox<Lot> lotCombo = this.lotCombos.get(index);
         Lot lot = (Lot)lotCombo.getValue();
         if (lot == null) {
            return true;
         } else {
            double availableWeight = this.lotWeightOrZero(lot);
            List<ComboBox<Lot>> replacements = this.replacementLotCombos.get(lotCombo);
            if (replacements != null) {
               for (ComboBox<Lot> replacementCombo : replacements) {
                  Lot replacementLot = (Lot)replacementCombo.getValue();
                  if (replacementLot != null) {
                     availableWeight += this.lotWeightOrZero(replacementLot);
                  }
               }
            }

            if (this.lotCombos.size() > 1) {
               BlendComponent component = this.components.size() > index ? this.components.get(index) : null;
               MeasureEditor weightField = this.weightFields.get(component);
               return weightField.getValue() == null || !(availableWeight - weightField.getValue().doubleValue() < 0.0);
            } else {
               return this.totalWeightField.getValue() == null || !(availableWeight - this.totalWeightField.getValue().doubleValue() < 0.0);
            }
         }
      }
   }

   private double lotWeightOrZero(Lot lot) {
      return this.lotWeightOrZero(lot, this.getUnit());
   }

   private double lotWeightOrZero(Lot lot, Unit unit) {
      if (lot != null && lot.getWeight() != null) {
         return unit == null ? lot.getWeight().getAmount() : lot.getWeight().toUnit(unit).getAmount();
      } else {
         return 0.0;
      }
   }

   public void setSelectedScheduled(Scheduled scheduled, AppModel appModel) {
      Profile profile = appModel.getData().getProfiles().get(scheduled.getProfileId());
      this.profileSelect.setValue(profile);
      if ((this.components.isEmpty() || this.components.size() == 1) && !scheduled.getComponentLots().isEmpty()) {
         ComponentLot componentLot = scheduled.getComponentLots().get(0);
         Lot scheduledComponentLot = appModel.getData().getLots().get(componentLot.getLotId());
         this.lotCombos.get(0).setValue(scheduledComponentLot);
      } else {
         int i = 0;

         for (ComboBox<Lot> lotCombo : this.lotCombos) {
            if (this.components.size() > i) {
               Lot scheduledComponentLot = null;
               BlendComponent component = this.components.get(i);

               for (ComponentLot componentLot : scheduled.getComponentLots()) {
                  if (component.getId().equals(componentLot.getComponentId())) {
                     scheduledComponentLot = appModel.getData().getLots().get(componentLot.getLotId());
                  }
               }

               lotCombo.setValue(scheduledComponentLot);
            }

            i++;
         }
      }

      if (scheduled.getWeight() != null) {
         this.totalWeightField.setValue(scheduled.getWeight().toUnit(appModel.getPreferences().getWeightUnit()).getAmount());
      }

      this.distributeWeightToComponents();
   }

   public BooleanBinding createValidationBinding(AppModel appModel) {
      ObservableBooleanValue lotsMissing = Bindings.createBooleanBinding(
         () -> this.lotCombos.stream().anyMatch(c -> c.getValue() == null), this.lotCombos.stream().map(ComboBoxBase::valueProperty).toArray(Observable[]::new)
      );
      ObservableBooleanValue weightsMissing = Bindings.createBooleanBinding(
         () -> this.weightFields.values().stream().anyMatch(m -> m.getValue() == null),
         this.weightFields.values().stream().map(MeasureEditor::valueProperty).toArray(Observable[]::new)
      );
      ObservableBooleanValue insufficientWeight = this.createInsufficientWeightBinding();
      return this.profileSelect
         .valueProperty()
         .isNull()
         .or(
            Bindings.createBooleanBinding(
               () -> this.totalWeightField.getValue() == null || this.totalWeightField.getValue().doubleValue() < 0.0,
               new Observable[]{this.totalWeightField.valueProperty()}
            )
         )
         .or(this.totalWeightInvalidBinding)
         .or(lotsMissing)
         .or(weightsMissing)
         .or(insufficientWeight);
   }

   private ObservableBooleanValue createInsufficientWeightBinding() {
      Stream<Observable> insufficientWeightDependencyStream = Stream.concat(
         Stream.concat(
            Stream.concat(this.weightFields.values().stream(), Stream.of(this.totalWeightField)).map(MeasureEditor::valueProperty),
            this.lotCombos.stream().map(ComboBoxBase::valueProperty)
         ),
         Stream.of(this.replacementStateVersion)
      );
      return Bindings.createBooleanBinding(() -> {
         if (!this.isInsufficientWeightValidationEnabled()) {
            return false;
         } else {
            for (int i = 0; i < this.lotCombos.size(); i++) {
               if (!this.isRemainingWeightValid(i)) {
                  return true;
               }
            }

            return false;
         }
      }, insufficientWeightDependencyStream.toArray(Observable[]::new));
   }

   public ObservableValue<? extends String> createValidationMessageBinding() {
      ObservableBooleanValue lotsMissing = Bindings.createBooleanBinding(
         () -> this.lotCombos.stream().anyMatch(c -> c.getValue() == null), this.lotCombos.stream().map(ComboBoxBase::valueProperty).toArray(Observable[]::new)
      );
      ObservableBooleanValue weightsMissing = Bindings.createBooleanBinding(
         () -> this.weightFields.values().stream().anyMatch(m -> m.getValue() == null),
         this.weightFields.values().stream().map(MeasureEditor::valueProperty).toArray(Observable[]::new)
      );
      BooleanBinding totalWeightInvalidBinding2 = Bindings.createBooleanBinding(
         () -> this.totalWeightField.getValue() == null || this.totalWeightField.getValue().doubleValue() < 0.0,
         new Observable[]{this.totalWeightField.valueProperty()}
      );
      ObservableBooleanValue insufficientWeight = this.createInsufficientWeightBinding();
      Collection<Observable> dependencies = new ArrayList<>();
      dependencies.add(this.totalWeightInvalidBinding);
      dependencies.add(totalWeightInvalidBinding2);
      dependencies.add(this.profileSelect.valueProperty());
      dependencies.add(lotsMissing);
      dependencies.add(weightsMissing);
      dependencies.add(insufficientWeight);
      return Bindings.createStringBinding(() -> {
         if (this.totalWeightInvalidBinding.get()) {
            return org.cropster.roastingintelligence.util.Messages.m_("Start weight is larger than the machine's capacity!");
         } else if (this.profileSelect.getValue() == null) {
            return org.cropster.roastingintelligence.util.Messages.m_("To start your roast, please select a profile");
         } else if (lotsMissing.get()) {
            return org.cropster.roastingintelligence.util.Messages.m_("To start your roast, please select a lot for every component");
         } else if (totalWeightInvalidBinding2.get() || weightsMissing.get()) {
            return org.cropster.roastingintelligence.util.Messages.m_("Start weight is invalid!");
         } else {
            return insufficientWeight.get() ? org.cropster.roastingintelligence.util.Messages.m_("Insufficient weight for the next roast!") : "";
         }
      }, dependencies.toArray(new Observable[0]));
   }

   public ComboBox<Profile> getProfileSelect() {
      return this.profileSelect;
   }

   public ComboBoxSearch<Profile> getProfileSearch() {
      return this.profileSearch;
   }

   public Lot[] getSelectedLots() {
      Lot[] lots = new Lot[this.lotCombos.size()];

      for (int i = 0; i < this.lotCombos.size(); i++) {
         ComboBox<Lot> lotCombo = this.lotCombos.get(i);
         lots[i] = (Lot)lotCombo.getValue();
      }

      return lots;
   }

   private void accumulateLotMeasure(Map<Lot, Measure> lotComponents, Lot lot, Measure measure) {
      if (!lotComponents.containsKey(lot)) {
         lotComponents.put(lot, measure);
      } else if (measure != null) {
         Measure existing = lotComponents.get(lot);
         if (existing != null) {
            lotComponents.put(lot, existing.add(measure));
         }
      }
   }

   public Map<Lot, Measure> getLotComponents() {
      LinkedHashMap<Lot, Measure> lotComponents = new LinkedHashMap<>();

      for (int i = 0; i < this.lotCombos.size(); i++) {
         ComboBox<Lot> lotCombo = this.lotCombos.get(i);
         Lot primaryLot = (Lot)lotCombo.getValue();
         MeasureEditor weightField;
         if (this.lotCombos.size() > 1) {
            BlendComponent component = this.components.get(i);
            weightField = this.weightFields.get(component);
         } else {
            weightField = this.totalWeightField;
         }

         Measure targetMeasure = weightField == null ? null : weightField.getMeasure();
         double remainingWeight = 0.0;
         Unit weightUnit = null;
         if (targetMeasure != null) {
            remainingWeight = Math.max(0.0, targetMeasure.getAmount());
            weightUnit = targetMeasure.getUnit();
         }

         if (primaryLot != null) {
            if (targetMeasure == null) {
               this.accumulateLotMeasure(lotComponents, primaryLot, null);
            } else {
               double primaryAvailableWeight = OfflineBootstrap.availableForBatch(this, primaryLot, weightUnit);
               double primaryAssignedWeight = Math.min(primaryAvailableWeight, remainingWeight);
               this.accumulateLotMeasure(lotComponents, primaryLot, new Measure(primaryAssignedWeight, weightUnit));
               remainingWeight = Math.max(0.0, remainingWeight - primaryAssignedWeight);
            }
         }

         List<ComboBox<Lot>> replacementCombos = this.replacementLotCombos.get(lotCombo);
         if (replacementCombos != null) {
            for (ComboBox<Lot> replacementCombo : replacementCombos) {
               Lot replacementLot = (Lot)replacementCombo.getValue();
               if (replacementLot != null) {
                  if (targetMeasure == null) {
                     this.accumulateLotMeasure(lotComponents, replacementLot, null);
                  } else {
                     double replacementAvailableWeight = OfflineBootstrap.availableForBatch(this, replacementLot, weightUnit);
                     double replacementAssignedWeight = Math.min(replacementAvailableWeight, remainingWeight);
                     this.accumulateLotMeasure(lotComponents, replacementLot, new Measure(replacementAssignedWeight, weightUnit));
                     remainingWeight = Math.max(0.0, remainingWeight - replacementAssignedWeight);
                  }
               }
            }
         }
      }

      return lotComponents;
   }

   public MeasureEditor getStartWeightEditor() {
      return this.totalWeightField;
   }

   public Double getStartWeight() {
      return this.totalWeightField.getValue().doubleValue();
   }

   public void setStartWeight(Double startWeight) {
      this.totalWeightField.setValue(startWeight);
      this.distributeWeightToComponents();
   }

   public Unit getUnit() {
      return this.totalWeightField.getUnit();
   }

   public void setUnit(Unit weightUnit) {
      for (MeasureEditor weightField : this.weightFields.values()) {
         weightField.setUnit(weightUnit);
      }

      this.totalWeightField.setUnit(weightUnit);
   }

   public void requestFocus() {
      this.totalWeightField.requestFocus();
   }

   public UserPanel getUserPanel() {
      return this.userPanel;
   }

   public SimpleObjectProperty<Class<? extends MeasurementSourceFactory>> autoStartSourceFactoryProperty() {
      return this.autoStartSourceFactory;
   }

   public Class<? extends MeasurementSourceFactory> getAutoStartSourceFactory() {
      return (Class<? extends MeasurementSourceFactory>)this.autoStartSourceFactory.get();
   }

   public void setAutoStartSourceFactoryProperty(SimpleObjectProperty<Class<? extends MeasurementSourceFactory>> autoStartSourceFactoryProperty) {
      this.autoStartSourceFactory = autoStartSourceFactoryProperty;
   }
}
