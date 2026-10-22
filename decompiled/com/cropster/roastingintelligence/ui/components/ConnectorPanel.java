package com.cropster.roastingintelligence.ui.components;

import com.cropster.roastingintelligence.ui.JavaFxController;
import com.cropster.roastingintelligence.ui.JavaFxUtil;
import com.cropster.roastingintelligence.ui.util.Icon;
import com.cropster.roastingintelligence.ui.util.ResourceBundleListCell;
import com.cropster.roastingintelligence.ui.util.WindowUtil;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import javafx.util.converter.DoubleStringConverter;
import javafx.util.converter.IntegerStringConverter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.cropster.roastingintelligence.listener.ConnectorConfigurationListener;
import org.cropster.roastingintelligence.measurement.MeasurementSourceFactory;
import org.cropster.roastingintelligence.measurement.RefreshableSourceParameter;
import org.cropster.roastingintelligence.measurement.SourceParameter;
import org.cropster.roastingintelligence.util.OverrideResourceBundle;

public class ConnectorPanel extends HBox {
   @FXML
   private Label connectorName;
   @FXML
   private Label connectorNotUsed;
   @FXML
   private Button configureButton;
   private final Map<SourceParameter, Control> parameterComponents = new IdentityHashMap<>();
   private final ConnectorConfigurationListener connectorConfigurationListener;
   private MeasurementSourceFactory connector;
   private Set<SourceParameter> deviceParameters;
   private Runnable connectorRemover;

   public ConnectorPanel(ConnectorConfigurationListener connectorConfigurationListener) {
      JavaFxUtil.loadComponent(this);
      this.connectorConfigurationListener = connectorConfigurationListener;
   }

   public void init(MeasurementSourceFactory connector, Set<SourceParameter> deviceParameters, Runnable connectorRemover) {
      assert Platform.isFxApplicationThread();

      this.connector = connector;
      this.deviceParameters = deviceParameters;
      this.connectorRemover = connectorRemover;
      this.connectorName.setText(org.cropster.roastingintelligence.util.Messages.m_(connector.toString()));
      this.configureButton.setDisable(connector.getParameters().isEmpty());
   }

   @FXML
   public void deleteConnector(ActionEvent e) {
      if (this.connectorRemover != null) {
         this.connectorRemover.run();
      }
   }

   public void setNotUsedVisible(boolean visible) {
      this.connectorNotUsed.setVisible(visible);
   }

   @FXML
   public void showConnectorConfigurationDialog(ActionEvent e) {
      Dialog<Set<SourceParameter>> dialog = new Dialog();
      dialog.initOwner(this.configureButton.getScene().getWindow());
      dialog.setTitle(org.cropster.roastingintelligence.util.Messages.m_("Parameters"));
      dialog.setHeaderText(org.cropster.roastingintelligence.util.Messages.m_(this.connector.getClass().getSimpleName()));
      dialog.getDialogPane().getButtonTypes().addAll(new ButtonType[]{ButtonType.OK, ButtonType.CANCEL});
      dialog.getDialogPane().getStylesheets().add(JavaFxController.STYLE_SHEET);
      dialog.getDialogPane().lookupButton(ButtonType.OK).getStyleClass().add("button--primary");
      dialog.getDialogPane().lookupButton(ButtonType.CANCEL).getStyleClass().add("button--plain");
      ColumnConstraints c1 = new ColumnConstraints();
      c1.setHalignment(HPos.RIGHT);
      c1.setMinWidth(Double.NEGATIVE_INFINITY);
      ColumnConstraints c2 = new ColumnConstraints();
      c2.setFillWidth(true);
      c2.setHgrow(Priority.ALWAYS);
      c2.setMinWidth(Double.NEGATIVE_INFINITY);
      GridPane gridPane = new GridPane();
      gridPane.setPadding(new Insets(10.0));
      gridPane.getColumnConstraints().addAll(new ColumnConstraints[]{c1, c2});
      gridPane.setHgap(10.0);
      gridPane.setVgap(10.0);
      int row = 0;

      for (SourceParameter parameter : this.deviceParameters) {
         if (!parameter.isHidden()) {
            this.addParameterComponent(parameter, gridPane, row);
            if (parameter instanceof RefreshableSourceParameter rp) {
               Button refreshButton = new Button(org.cropster.roastingintelligence.util.Messages.m_("Update"), Icon.SYNC.load());
               refreshButton.setOnAction(a -> {
                  ComboBox<String> comboBox = (ComboBox<String>)this.parameterComponents.get(rp);
                  comboBox.getItems().setAll(rp.refresh(this.updateDeviceParameters()));
               });
               gridPane.add(refreshButton, 1, ++row);
            }

            row++;
         }
      }

      if (row > 5) {
         dialog.setResizable(true);
         ScrollPane scrollPane = new ScrollPane(gridPane);
         scrollPane.setFitToWidth(true);
         dialog.getDialogPane().setContent(scrollPane);
      } else {
         dialog.getDialogPane().setContent(gridPane);
      }

      dialog.setResultConverter(b -> ButtonType.OK.equals(b) ? this.updateDeviceParameters() : null);
      dialog.show();
      if (row > 5) {
         Stage stage = (Stage)dialog.getDialogPane().getScene().getWindow();
         WindowUtil.showSafeStage(stage, 300.0, 300.0, stage.getWidth(), stage.getHeight(), 32.0);
      }
   }

   private void addParameterComponent(SourceParameter parameter, GridPane gridPane, int row) {
      String label = parameter.getDisplayName() != null ? parameter.getDisplayName() : org.cropster.roastingintelligence.util.Messages.m_(parameter.getName());
      String value = parameter.getValue();
      if (Boolean.class.equals(parameter.getType())) {
         CheckBox checkbox = new CheckBox(label);
         checkbox.setSelected(Boolean.parseBoolean(value));
         gridPane.add(checkbox, 1, row);
         this.parameterComponents.put(parameter, checkbox);
      } else if (Integer.class.equals(parameter.getType())) {
         TextFormatter<Integer> formatter = new TextFormatter(new IntegerStringConverter());
         TextField textField = new TextField();
         textField.setTextFormatter(formatter);
         formatter.setValue(value != null ? Integer.valueOf(value) : null);
         gridPane.add(new Label(label), 0, row);
         gridPane.add(textField, 1, row);
         this.parameterComponents.put(parameter, textField);
      } else if (Double.class.equals(parameter.getType())) {
         TextFormatter<Double> formatter = new TextFormatter(new DoubleStringConverter());
         TextField textField = new TextField();
         textField.setTextFormatter(formatter);
         formatter.setValue(value != null ? Double.valueOf(value) : null);
         gridPane.add(new Label(label), 0, row);
         gridPane.add(textField, 1, row);
         this.parameterComponents.put(parameter, textField);
      } else if (parameter.getOptions() != null) {
         if (parameter instanceof RefreshableSourceParameter rp) {
            String[] options = rp.refresh(new ArrayList<>(this.deviceParameters));
            rp.setOptions(options);
         }

         OverrideResourceBundle bundle = new OverrideResourceBundle(org.cropster.roastingintelligence.util.Messages.getBundle());
         Stream.of(parameter.getOptions())
            .map(o -> Pair.of(o, parameter.getOptionDisplayName(o)))
            .filter(p -> p.getRight() != null)
            .forEach(p -> bundle.override((String)p.getLeft(), (String)p.getRight()));
         ComboBox<String> comboBox = new ComboBox(FXCollections.observableArrayList(parameter.getOptions()));
         comboBox.setMaxWidth(Double.MAX_VALUE);
         comboBox.setButtonCell(new ResourceBundleListCell(bundle));
         comboBox.setCellFactory(v -> new ResourceBundleListCell(bundle));
         comboBox.getSelectionModel().select(value);
         gridPane.add(new Label(label), 0, row);
         gridPane.add(comboBox, 1, row);
         this.parameterComponents.put(parameter, comboBox);
      } else {
         gridPane.add(new Label(label), 0, row);
         TextField textField = new TextField(value);
         gridPane.add(textField, 1, row);
         this.parameterComponents.put(parameter, textField);
      }
   }

   private Set<SourceParameter> updateDeviceParameters() {
      for (SourceParameter parameter : this.deviceParameters) {
         if (!parameter.isHidden()) {
            parameter.setValue(this.getParameterValue(parameter));
         }
      }

      this.connectorConfigurationListener.settingsChanged(this.connector);
      return this.deviceParameters;
   }

   private String getParameterValue(SourceParameter parameter) {
      if (Boolean.class.equals(parameter.getType())) {
         CheckBox checkbox = (CheckBox)this.parameterComponents.get(parameter);
         return Boolean.toString(checkbox.isSelected());
      } else if (Integer.class.equals(parameter.getType())) {
         TextField textField = (TextField)this.parameterComponents.get(parameter);
         TextFormatter<Integer> formatter = textField.getTextFormatter();
         return formatter.getValue() != null ? ((Integer)formatter.getValue()).toString() : null;
      } else if (Double.class.equals(parameter.getType())) {
         TextField textField = (TextField)this.parameterComponents.get(parameter);
         TextFormatter<Double> formatter = textField.getTextFormatter();
         return formatter.getValue() != null ? ((Number)formatter.getValue()).toString() : null;
      } else if (parameter.getOptions() != null) {
         ComboBox<String> comboBox = (ComboBox<String>)this.parameterComponents.get(parameter);
         return comboBox.getSelectionModel().getSelectedIndex() != -1 ? (String)comboBox.getSelectionModel().getSelectedItem() : null;
      } else {
         TextField textField = (TextField)this.parameterComponents.get(parameter);
         return StringUtils.isNotBlank(textField.getText()) ? textField.getText() : null;
      }
   }
}
