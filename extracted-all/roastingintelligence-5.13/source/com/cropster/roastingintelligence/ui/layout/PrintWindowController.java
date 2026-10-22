package com.cropster.roastingintelligence.ui.layout;

import com.cropster.roastingintelligence.ui.JavaFxUtil;
import com.cropster.roastingintelligence.ui.components.MeasureEditor;
import com.cropster.roastingintelligence.ui.util.Validators;
import com.cropster.util.model.Unit;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.print.PageOrientation;
import javafx.print.Printer;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PrintWindowController implements Initializable {
   private final Logger logger = LoggerFactory.getLogger(PrintWindowController.class);
   @FXML
   private VBox content;
   @FXML
   private ComboBox<Printer> printerComboBox;
   @FXML
   private MeasureEditor widthEditor;
   @FXML
   private MeasureEditor heightEditor;
   @FXML
   private HBox buttonBar;
   private Button switchButton;
   @FXML
   private Button previewButton;
   @FXML
   private Button printButton;
   @FXML
   private ToggleGroup orientationToggleGroup;
   private final ObjectProperty<Unit> unit = new SimpleObjectProperty();
   private final ObservableList<Printer> printers = FXCollections.observableArrayList();
   private ObjectProperty<Printer> selectedPrinter;
   private ObjectProperty<Number> width;
   private ObjectProperty<Number> height;
   private final ObjectProperty<PageOrientation> orientation = new SimpleObjectProperty(PageOrientation.PORTRAIT);

   public PrintWindowController() {
      String country = Locale.getDefault().getCountry();
      if (country == null || !country.equals(Locale.US.getCountry()) && !country.equals("LR") && !country.equals("MM")) {
         this.unit.set(Unit.MM);
      } else {
         this.unit.set(Unit.IN);
      }
   }

   public void initialize(URL location, ResourceBundle resources) {
      JavaFxUtil.setDefaultLineSpacing(this.content);
      this.selectedPrinter = this.printerComboBox.valueProperty();
      this.width = this.widthEditor.valueProperty();
      this.height = this.heightEditor.valueProperty();
      this.orientationToggleGroup.selectedToggleProperty().addListener((s, o, n) -> this.orientation.set((PageOrientation)((RadioButton)n).getUserData()));
      JavaFxUtil.reverseButtonBars(this.content);
      this.switchButton = new Button(org.cropster.roastingintelligence.util.Messages.m_("Switch to system print dialogue"));
      this.switchButton.visibleProperty().bind(this.switchButton.onActionProperty().isNotNull());
      this.buttonBar.getChildren().add(0, this.switchButton);
      this.previewButton.visibleProperty().bind(this.previewButton.onActionProperty().isNotNull());
      this.previewButton.managedProperty().bind(this.previewButton.visibleProperty());
      Validators.setValidator(this.widthEditor, 10000.0);
      Validators.setValidator(this.heightEditor, 10000.0);
   }

   public void init(ObservableSet<Printer> availablePrinters) {
      this.logger.info("Printers found: {}", availablePrinters.stream().map(p -> p.getName()).collect(Collectors.joining("; ")));
      this.logger.info("Default printer: {}", Printer.getDefaultPrinter());
      this.printers.setAll(availablePrinters);
   }

   public void setOnSwitchToSystemDialogue(EventHandler<ActionEvent> eventHandler) {
      this.switchButton.setOnAction(eventHandler);
   }

   public void setOnPreview(EventHandler<ActionEvent> eventHandler) {
      this.previewButton.setOnAction(eventHandler);
   }

   public void setOnPrint(EventHandler<ActionEvent> eventHandler) {
      this.printButton.setOnAction(eventHandler);
   }

   public HBox getButtonBar() {
      return this.buttonBar;
   }

   public ReadOnlyObjectProperty<Unit> unitProperty() {
      return this.unit;
   }

   public Unit getUnit() {
      return (Unit)this.unit.get();
   }

   public ObservableList<Printer> getAvailablePrinters() {
      return this.printers;
   }

   public ObjectProperty<Printer> selectedPrinterProperty() {
      return this.selectedPrinter;
   }

   public Printer getSelectedPrinter() {
      return (Printer)this.selectedPrinter.get();
   }

   public void setSelectedPrinter(Printer selectedPrinter) {
      this.selectedPrinter.set(selectedPrinter);
   }

   public ObjectProperty<Number> widthProperty() {
      return this.width;
   }

   public Number getWidth() {
      return (Number)this.width.get();
   }

   public void setWidth(Number width) {
      this.width.set(width);
   }

   public ObjectProperty<Number> heightProperty() {
      return this.height;
   }

   public Number getHeight() {
      return (Number)this.height.get();
   }

   public void setHeight(Number height) {
      this.height.set(height);
   }

   public ObjectProperty<PageOrientation> orientationProperty() {
      return this.orientation;
   }

   public PageOrientation getOrientation() {
      return (PageOrientation)this.orientation.get();
   }

   public void setOrientation(PageOrientation orientation) {
      JavaFxUtil.selectToggle(this.orientationToggleGroup, orientation);
   }

   @FXML
   public void close() {
      this.content.getScene().getWindow().hide();
   }
}
