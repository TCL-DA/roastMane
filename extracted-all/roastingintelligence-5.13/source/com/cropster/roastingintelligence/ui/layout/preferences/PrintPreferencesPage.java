package com.cropster.roastingintelligence.ui.layout.preferences;

import com.cropster.roastingintelligence.Data;
import com.cropster.roastingintelligence.ui.JavaFxUtil;
import com.cropster.roastingintelligence.ui.components.DragAndDropLabelEditor;
import com.cropster.roastingintelligence.ui.components.FormComponent;
import com.cropster.roastingintelligence.ui.components.RoastLabel;
import com.cropster.roastingintelligence.ui.util.RendererListCell;
import com.cropster.roastingintelligence.ui.util.TranslatedListCell;
import com.cropster.util.model.Unit;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.function.BiConsumer;
import java.util.function.Function;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import javafx.util.Callback;
import org.cropster.roastingintelligence.AppModel;
import org.cropster.roastingintelligence.Defaults;
import org.cropster.roastingintelligence.model.Lot;
import org.cropster.roastingintelligence.model.LotTransaction;
import org.cropster.roastingintelligence.model.Machine;
import org.cropster.roastingintelligence.model.Measure;
import org.cropster.roastingintelligence.model.Profile;
import org.cropster.roastingintelligence.model.Roast;
import org.cropster.roastingintelligence.remote.Version;

public class PrintPreferencesPage extends GridPane implements Initializable {
   @FXML
   private FormComponent labelStyleForm;
   @FXML
   private ComboBox<RoastLabel.RoastLabelStyle> labelStyleSelector;
   @FXML
   private FormComponent qrCodePositionForm;
   @FXML
   private ComboBox<RoastLabel.QrCodePosition> qrCodePositionSelector;
   @FXML
   private ComboBox<Integer> printFontSizeComboBox;
   @FXML
   private FormComponent titleVisibleForm;
   @FXML
   private CheckBox titleVisibleCheckBox;
   @FXML
   private FormComponent dateFormatForm;
   @FXML
   private ComboBox<RoastLabel.DateFormatter> dateFormatComboBox;
   @FXML
   private DragAndDropLabelEditor printLabelEditor;
   @FXML
   private FormComponent baseFontSizeForm;
   private BiConsumer<AppModel, Roast> printFunction;

   public PrintPreferencesPage() {
      JavaFxUtil.loadComponent(this);
   }

   public void initialize(URL location, ResourceBundle resources) {
      this.labelStyleSelector.setButtonCell(new TranslatedListCell());
      this.labelStyleSelector.setCellFactory(v -> new TranslatedListCell());
      this.labelStyleSelector.getItems().setAll(RoastLabel.RoastLabelStyle.values());
      this.printLabelEditor.reducedStyleProperty().bind(this.labelStyleSelector.valueProperty().isEqualTo(RoastLabel.RoastLabelStyle.CARD));
      this.labelStyleSelector.setValue(RoastLabel.RoastLabelStyle.DEFAULT);
      this.qrCodePositionSelector.setButtonCell(new TranslatedListCell());
      this.qrCodePositionSelector.setCellFactory(v -> new TranslatedListCell());
      this.qrCodePositionSelector.getItems().setAll(RoastLabel.QrCodePosition.values());
      this.printLabelEditor
         .qrCodePositionProperty()
         .bind(Bindings.createObjectBinding(this.qrCodePositionSelector::getValue, new Observable[]{this.qrCodePositionSelector.valueProperty()}));
      this.qrCodePositionSelector.setValue(RoastLabel.QrCodePosition.TOP);
      Callback<ListView<Integer>, ListCell<Integer>> fontCellFactory = lv -> new RendererListCell<>(v -> v + " pt");
      this.printFontSizeComboBox.setButtonCell((ListCell)fontCellFactory.call(null));
      this.printFontSizeComboBox.setCellFactory(fontCellFactory);
      this.printFontSizeComboBox.setItems(FXCollections.observableArrayList(Defaults.PRINT_FONT_SIZES));
      this.titleVisibleCheckBox.setSelected(true);
      Function<RoastLabel.DateFormatter, String> converter = df -> df == null ? null : df.getExample();
      Callback<ListView<RoastLabel.DateFormatter>, ListCell<RoastLabel.DateFormatter>> dateFormatCellFactory = lv -> new RendererListCell<>(converter)
            .withNullValue(org.cropster.roastingintelligence.util.Messages.m_("Select a format"))
            .withNullValueStyle("-fx-text-fill: -fx-color-text-disabled; ")
            .withStyleProvider((c, df) -> df.getStyle());
      this.dateFormatComboBox.setButtonCell((ListCell)dateFormatCellFactory.call(null));
      this.dateFormatComboBox.setCellFactory(dateFormatCellFactory);
      this.dateFormatComboBox.getItems().setAll(RoastLabel.DateFormatter.values());
      this.dateFormatComboBox.setValue(Defaults.PRINT_DATE_FORMATTER);
      PreferencesPanel.addTitleArrow(this.qrCodePositionForm);
      PreferencesPanel.addTitleArrow(this.labelStyleForm);
      PreferencesPanel.addTitleArrow(this.baseFontSizeForm);
      PreferencesPanel.addTitleArrow(this.titleVisibleForm);
      PreferencesPanel.addTitleArrow(this.dateFormatForm);
   }

   @FXML
   public void resetPrintLabel() {
      this.printLabelEditor.setLabelMapping(Defaults.PRINT_LABEL_MAPPING);
   }

   @FXML
   public void printLabelPreview() throws URISyntaxException {
      AppModel model = this.createPreviewModel();
      Roast roast = this.createPreviewRoast(model.getData());
      this.printFunction.accept(model, roast);
   }

   public RoastLabel.QrCodePosition getPrintLabelQrCodePosition() {
      return (RoastLabel.QrCodePosition)this.qrCodePositionSelector.getValue();
   }

   public void setPrintLabelQrCodePosition(RoastLabel.QrCodePosition qrCodePosition) {
      this.qrCodePositionSelector.setValue(qrCodePosition);
   }

   public String getPrintLabelStyle() {
      return ((RoastLabel.RoastLabelStyle)this.labelStyleSelector.getValue()).name();
   }

   public void setPrintLabelStyle(String printLabelTemplate) {
      Platform.runLater(() -> {
         if (printLabelTemplate != null) {
            this.labelStyleSelector.setValue(RoastLabel.RoastLabelStyle.valueOf(printLabelTemplate));
         }
      });
   }

   public Integer getPrintFontSize() {
      return (Integer)this.printFontSizeComboBox.getValue();
   }

   public void setPrintLabelFontSize(Integer printFontSize) {
      this.printFontSizeComboBox.setValue(printFontSize);
   }

   public boolean isPrintTitleVisible() {
      return this.titleVisibleCheckBox.isSelected();
   }

   public void setPrintTitleVisible(boolean visible) {
      this.titleVisibleCheckBox.setSelected(visible);
   }

   public RoastLabel.DateFormatter getPrintDateFormatter() {
      return (RoastLabel.DateFormatter)this.dateFormatComboBox.getValue();
   }

   public void setPrintDateFormatter(RoastLabel.DateFormatter printDateFormatter) {
      this.dateFormatComboBox.setValue(printDateFormatter);
   }

   public Map<String, String> getPrintLabelMapping() {
      return this.printLabelEditor.getLabelMapping();
   }

   public void setPrintLabelMapping(Map<String, String> printLabelMapping) {
      Platform.runLater(() -> this.printLabelEditor.setLabelMapping(printLabelMapping));
   }

   public void setPrintFunction(BiConsumer<AppModel, Roast> printFunction) {
      this.printFunction = printFunction;
   }

   private AppModel createPreviewModel() throws URISyntaxException {
      AppModel model = new AppModel(new Version("99.0"));
      model.getPreferences().setPrintFontSize(this.getPrintFontSize());
      model.getPreferences().setPrintQrCodePosition(this.getPrintLabelQrCodePosition());
      model.getPreferences().setPrintLabelStyle(this.getPrintLabelStyle());
      model.getPreferences().setPrintTitleVisible(this.isPrintTitleVisible());
      model.getPreferences().setPrintDateFormatter(this.getPrintDateFormatter());
      model.getPreferences().setPrintLabelMapping(this.printLabelEditor.getLabelMapping());
      Map<Long, Machine> machines = new HashMap<>();
      machines.put(1L, new Machine(1L, "", "", "Probat Probatone", null));
      model.getData().setMachines(machines);
      Map<Long, Profile> profiles = new HashMap<>();
      profiles.put(1L, new Profile(1L, "Colombia Filter"));
      model.getData().setProfiles(profiles);
      Map<Long, Lot> lots = new LinkedHashMap<>();
      Lot source1 = new Lot(2L, "Component #1");
      source1.setIdTag("PG-0001");
      lots.put(source1.getId(), source1);
      Lot source2 = new Lot(3L, "Component #2");
      source2.setIdTag("PG-0002");
      lots.put(source2.getId(), source2);
      Lot lot = new Lot(1L, "Colombia Blend");
      lot.setIdTag("PR-0123");
      lot.getSources().add(new LotTransaction(source1.getId(), new Measure(4.0, Unit.KG)));
      lot.getSources().add(new LotTransaction(source2.getId(), new Measure(2.0, Unit.KG)));
      lots.put(lot.getId(), lot);
      model.getData().setLots(lots);
      return model;
   }

   private Roast createPreviewRoast(Data data) {
      Roast roast = new Roast();
      roast.setLot(data.getLots().get(1L));
      roast.setWorker("John Doe");
      roast.setMachine(1L);
      roast.setProfile(1L);
      roast.setEndDate(new Date());
      roast.setStartDate(new Date(roast.getEndDate().getTime() - 720000L));
      roast.setStartWeight(new Measure(6.0, Unit.KG));
      roast.setEndWeight(new Measure(5.5, Unit.KG));
      roast.getMeasures().put("ambientTemperature", new Measure(18.0, Unit.CELSIUS));
      roast.getMeasures().put("roastValue", new Measure(83.0, Unit.AGTRON_COMMERCIAL));
      roast.setNotes(
         "Lorem ipsum dolor sit amet, consectetur adipisici elit, sed eiusmod tempor incidunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquid ex ea commodi consequat."
      );
      return roast;
   }
}
