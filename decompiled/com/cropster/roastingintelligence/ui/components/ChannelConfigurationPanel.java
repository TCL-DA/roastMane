package com.cropster.roastingintelligence.ui.components;

import com.cropster.roastingintelligence.ui.JavaFxUtil;
import com.cropster.roastingintelligence.ui.util.TranslatedListCell;
import com.cropster.roastingintelligence.ui.util.Validators;
import com.cropster.util.model.Unit;
import java.awt.Color;
import java.net.URL;
import java.util.Collection;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.WeakHashMap;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import org.apache.commons.lang3.StringUtils;
import org.cropster.roastingintelligence.Defaults;
import org.cropster.roastingintelligence.RoastingIntelligenceApp;
import org.cropster.roastingintelligence.listener.ChannelConfigurationListener;
import org.cropster.roastingintelligence.measurement.MeasurementSourceFactory;
import org.cropster.roastingintelligence.measurement.SourceParameter;
import org.cropster.roastingintelligence.model.Channel;

public class ChannelConfigurationPanel extends AbstractDraggableConfigurationPanel {
   private static final String DISABLED_COLOR = "#cacecf";
   private static final String BORDER_STYLE = "-fx-border-style: solid; -fx-border-width: 1px 0 0 0; -fx-border-color: -fx-color-neutral-black";
   @FXML
   private Label dragRegion;
   @FXML
   private TitledPaneRight titledPanel;
   @FXML
   private ComboBox<MeasurementSourceFactory> sourceComboBox;
   @FXML
   private GridPane configurationPanel;
   @FXML
   private ComboBox<Integer> indexComboBox;
   @FXML
   private FormComponent indexLabel;
   @FXML
   private ComboBox<String> typeComboBox;
   @FXML
   private ColorPicker colorPicker;
   @FXML
   private CheckBox statusVisibleCheckbox;
   @FXML
   private CheckBox chartVisibleCheckbox;
   @FXML
   private MeasureEditor offsetEditor;
   @FXML
   private CheckBox rorCheckbox;
   @FXML
   private ToggleSwitch enableSwitch;
   @FXML
   private GridPane formContainer;
   @FXML
   private ToggleGroup primaryToggleGroup;
   @FXML
   private RadioButton primarySmallRadio;
   @FXML
   private RadioButton primaryLargeRadio;
   @FXML
   private MessageBox infoMessage;
   private final StringProperty channelName = new SimpleStringProperty();
   private final ObjectProperty<Unit> temperatureUnit = new SimpleObjectProperty();
   private final BooleanProperty updateListenersSuspended = new SimpleBooleanProperty(true);
   private final ObservableList<MeasurementSourceFactory> sourceFactories = FXCollections.observableArrayList();
   private volatile Channel channel;
   private Map<Class<? extends MeasurementSourceFactory>, Set<SourceParameter>> deviceParameters;
   private final ChannelConfigurationListener channelConfigurationListener;
   private final WeakHashMap<Channel, Boolean> enablementCache = new WeakHashMap<>();
   private final WeakHashMap<Channel, MeasurementSourceFactory> sourceCache = new WeakHashMap<>();

   public ChannelConfigurationPanel(ChannelConfigurationListener channelConfigurationListener) {
      JavaFxUtil.loadComponent(this);
      this.channelConfigurationListener = channelConfigurationListener;
   }

   private Integer[] createIndexes(int numIndexes) {
      Integer[] indexes = new Integer[numIndexes];

      for (int i = 0; i < indexes.length; i++) {
         indexes[i] = i;
      }

      return indexes;
   }

   private <T> ChangeListener<T> wrap(ChangeListener<T> listener) {
      return new ChannelConfigurationPanel.SuspendableChangeListener<>(listener, this.updateListenersSuspended);
   }

   public void initialize(URL location, ResourceBundle resources) {
      JavaFxUtil.bindStyleToColor("-fx-background-color", this.colorPicker.styleProperty(), this.colorPicker.valueProperty());
      this.colorPicker.valueProperty().addListener(this.wrap((s, o, n) -> {
         if (this.sourceComboBox.getValue() != null) {
            this.channel.setColor(new Color((float)n.getRed(), (float)n.getGreen(), (float)n.getBlue(), (float)n.getOpacity()));
            this.channelConfigurationListener.settingsChanged(this.channel);
         }
      }));
      this.sourceComboBox.setItems(new SortedList(this.sourceFactories, MeasurementSourceFactory.SOURCE_FACTORY_COMPARATOR));
      this.sourceComboBox.getSelectionModel().selectedItemProperty().addListener(this.wrap((s, o, n) -> {
         this.setSourceFactory(n);
         this.channelConfigurationListener.settingsChanged(this.channel);
      }));
      this.sourceComboBox.setButtonCell(new TranslatedListCell(org.cropster.roastingintelligence.util.Messages.m_("Select source ...")));
      this.sourceComboBox
         .setCellFactory(
            v -> new TranslatedListCell<MeasurementSourceFactory>(org.cropster.roastingintelligence.util.Messages.m_("Select source ..."))
                  .withStyleProvider(
                     (cell, item) -> {
                        ObservableList<MeasurementSourceFactory> items = cell.getListView().getItems();
                        int i = cell.getIndex();
                        return i > 0
                              && MeasurementSourceFactory.groupIndexOf(item)
                                 > MeasurementSourceFactory.groupIndexOf((MeasurementSourceFactory)items.get(i - 1))
                           ? "-fx-border-style: solid; -fx-border-width: 1px 0 0 0; -fx-border-color: -fx-color-neutral-black"
                           : null;
                     }
                  )
         );
      this.statusVisibleCheckbox.selectedProperty().addListener(this.wrap((s, o, n) -> {
         if (n) {
            this.channel.getVisibility().add(Channel.Visibility.STATUS);
         } else {
            this.channel.getVisibility().remove(Channel.Visibility.STATUS);
         }

         this.channelConfigurationListener.settingsChanged(this.channel);
      }));
      this.chartVisibleCheckbox.selectedProperty().addListener(this.wrap((s, o, n) -> {
         if (n) {
            this.channel.getVisibility().add(Channel.Visibility.CHART);
         } else {
            this.channel.getVisibility().remove(Channel.Visibility.CHART);
         }

         this.channelConfigurationListener.settingsChanged(this.channel);
      }));
      this.indexComboBox.getSelectionModel().selectedItemProperty().addListener(this.wrap((s, o, n) -> {
         this.channel.setIndex(n != null ? n : 0);
         this.channelConfigurationListener.settingsChanged(this.channel);
      }));
      this.typeComboBox.getSelectionModel().selectedItemProperty().addListener(this.wrap((s, o, n) -> {
         this.channel.setProbeType(n);
         this.channelConfigurationListener.settingsChanged(this.channel);
      }));
      this.offsetEditor.unitProperty().bind(this.temperatureUnit);
      this.offsetEditor.valueProperty().addListener(this.wrap((s, o, n) -> {
         this.channel.setOffset(n.doubleValue());
         this.channelConfigurationListener.settingsChanged(this.channel);
      }));
      Validators.setValidator(this.offsetEditor, -10000.0, 10000.0);
      this.rorCheckbox.selectedProperty().addListener(this.wrap((s, o, n) -> {
         this.channel.setRorEnabled(n);
         this.channelConfigurationListener.settingsChanged(this.channel);
      }));
      this.enableSwitch.setOn(true);
      this.enableSwitch.onProperty().addListener((s, o, n) -> {
         this.enablementCache.put(this.channel, n);
         this.sourceComboBox.setValue(n ? this.sourceCache.getOrDefault(this.channel, null) : null);
         if (n && this.sourceComboBox.getValue() == null) {
            this.titledPanel.setExpanded(true);
         }
      });
      this.statusVisibleCheckbox.setText(this.statusVisibleCheckbox.getText() + ":");
      this.primarySmallRadio.setSelected(true);
      this.primaryToggleGroup.selectedToggleProperty().addListener((s, o, n) -> this.channel.setPrimary(n == this.primaryLargeRadio));
   }

   private void setIndexLabel() {
      MeasurementSourceFactory sourceFactory = (MeasurementSourceFactory)this.sourceComboBox.getSelectionModel().selectedItemProperty().get();
      if (sourceFactory != null
         && (
            Defaults.HUB_PORT_SOURCE_FACTORIES.contains(sourceFactory.getClass())
               || this.containsHubPortParameter(this.deviceParameters.get(sourceFactory.getClass()))
         )) {
         this.indexLabel.setTitle(org.cropster.roastingintelligence.util.Messages.m_("Hub port"));
      } else {
         this.indexLabel.setTitle(org.cropster.roastingintelligence.util.Messages.m_("Channel"));
      }
   }

   private boolean containsHubPortParameter(Set<SourceParameter> parameters) {
      return parameters == null
         ? false
         : parameters.stream().anyMatch(parameter -> "phidgetHub".equals(parameter.getName()) ? "VINT Hub".equals(parameter.getValue()) : false);
   }

   public void init(
      Channel channel,
      Collection<? extends MeasurementSourceFactory> sourceFactories,
      Map<Class<? extends MeasurementSourceFactory>, Set<SourceParameter>> deviceParameters
   ) {
      assert Platform.isFxApplicationThread();

      this.channel = channel;
      this.channelName.set(org.cropster.roastingintelligence.util.Messages.m_(channel.getName()));
      this.deviceParameters = deviceParameters;
      this.updateListenersSuspended.set(true);
      this.sourceFactories.clear();
      this.sourceFactories.add(null);
      this.sourceFactories.addAll(sourceFactories);
      this.updateListenersSuspended.set(false);
      if (channel.getSourceFactory() != null) {
         MeasurementSourceFactory factory = RoastingIntelligenceApp.retrieveSingleton(channel.getSourceFactory());
         if (this.sourceComboBox.getItems().contains(factory)) {
            this.setSourceFactory(factory);
         } else {
            this.setSourceFactory(null);
         }
      } else {
         this.setSourceFactory(null);
      }

      if (channel != null && channel.isPrimary()) {
         this.primaryLargeRadio.setSelected(true);
      } else {
         this.primarySmallRadio.setSelected(true);
      }

      if (sourceFactories.isEmpty()) {
         this.enableSwitch.setOn(false);
         this.enableSwitch.setDisable(true);
         this.titledPanel.setExpanded(false);
      } else {
         this.enableSwitch.setDisable(false);
      }
   }

   @Override
   public void reset() {
      this.channel = null;
      this.channelName.set(null);
      this.deviceParameters = null;
   }

   private void setSourceFactory(MeasurementSourceFactory sourceFactory) {
      String colorHex = "#cacecf";
      boolean validSourceFactory = sourceFactory != null;
      String infoMessageText = "";
      this.updateListenersSuspended.set(true);
      String channelText = org.cropster.roastingintelligence.util.Messages.m_(this.channel.getName());
      if (sourceFactory != null && ("custom".equals(this.channel.getName()) || "customControl".equals(this.channel.getName()))) {
         String alternativeText = org.cropster.roastingintelligence.util.Messages.m_(
            this.channel.getName(), (Class<? extends MeasurementSourceFactory>)sourceFactory.getClass()
         );
         if (alternativeText.equals(channelText)) {
            this.channelName.set(channelText);
         } else {
            this.channelName
               .set(
                  String.format(
                     "%s - %s",
                     channelText,
                     org.cropster.roastingintelligence.util.Messages.m_(
                        this.channel.getName().replaceAll("Control", ""), (Class<? extends MeasurementSourceFactory>)sourceFactory.getClass()
                     )
                  )
               );
         }
      } else {
         this.channelName.set(channelText);
      }

      if (validSourceFactory) {
         if (!sourceFactory.getClass().equals(this.channel.getSourceFactory())) {
            this.channel.setSourceFactory((Class<? extends MeasurementSourceFactory>)sourceFactory.getClass());
            this.channel.setProbeType(sourceFactory.getTypes()[0]);
         }

         colorHex = StringUtils.leftPad(Integer.toHexString(this.channel.getColor().getRGB() & 16777215), 6, "0");
         this.colorPicker.setDisable(this.channel.getName().matches(".+[cC]ontrol") && !"setTemperatureControl".equals(this.channel.getName()));
         this.statusVisibleCheckbox.setDisable("beanTemperature".equals(this.channel.getName()));
         this.chartVisibleCheckbox.setDisable("beanTemperature".equals(this.channel.getName()));
         if ("beanTemperature".equals(this.channel.getName())) {
            this.statusVisibleCheckbox.setSelected(true);
            this.chartVisibleCheckbox.setSelected(true);
         }

         this.indexComboBox.getItems().setAll(this.createIndexes(sourceFactory.getChannelCount()));
         this.indexComboBox.setDisable(sourceFactory.getChannelCount() <= 0);
         this.typeComboBox.getItems().setAll(sourceFactory.getTypes());
         this.typeComboBox.setDisable(sourceFactory.getTypes().length <= 1);
         this.sourceCache.put(this.channel, sourceFactory);
         if (sourceFactory.supportsAutoMode(this.channel.getName())
            && this.channel.getName().equals(sourceFactory.getAutoModeMeasurement(this.channel.getName()))) {
            String manualModeMeasurement = sourceFactory.getManualModeMeasurement(this.channel.getName());
            if (manualModeMeasurement != null) {
               String manualModeMeasurementText = org.cropster.roastingintelligence.util.Messages.m_(manualModeMeasurement);
               if ("customControl".equals(manualModeMeasurement)) {
                  String customMeasurementText = org.cropster.roastingintelligence.util.Messages.m_(
                     "custom", (Class<? extends MeasurementSourceFactory>)sourceFactory.getClass()
                  );
                  if (!manualModeMeasurementText.equals(customMeasurementText)) {
                     manualModeMeasurementText = String.format("%s - %s", manualModeMeasurementText, customMeasurementText);
                  }
               }

               infoMessageText = org.cropster.roastingintelligence.util.Messages.m_(
                  "When displayed, this control will take the place of {0}.", manualModeMeasurementText
               );
            }
         }
      } else {
         this.channel.setSourceFactory(null);
      }

      this.configurationPanel.setVisible(validSourceFactory);
      this.rorCheckbox.setVisible(this.channel.isRorSupported());
      this.sourceComboBox.getSelectionModel().select(sourceFactory);
      this.typeComboBox.getSelectionModel().select(this.channel.getProbeType());
      this.colorPicker.setValue(javafx.scene.paint.Color.valueOf(colorHex));
      this.indexComboBox
         .getSelectionModel()
         .select(sourceFactory != null && this.channel.getIndex() < sourceFactory.getChannelCount() ? Integer.valueOf(this.channel.getIndex()) : 0);
      this.setIndexLabel();
      this.statusVisibleCheckbox.setSelected(this.channel.isVisible(Channel.Visibility.STATUS));
      this.chartVisibleCheckbox.setSelected(this.channel.isVisible(Channel.Visibility.CHART));
      this.offsetEditor.setValue(this.channel.getOffset());
      this.rorCheckbox.setSelected(this.channel.isRorEnabled() && this.channel.isRorSupported());
      this.enableSwitch.setOn(validSourceFactory || this.enablementCache.getOrDefault(this.channel, Boolean.valueOf(false)));
      this.infoMessage.setTitle(null);
      this.infoMessage.setText(infoMessageText);
      this.infoMessage.setVisible(StringUtils.isNotBlank(infoMessageText));
      this.infoMessage.setManaged(StringUtils.isNotBlank(infoMessageText));
      this.updateListenersSuspended.set(false);
   }

   @Override
   public Node getDragRegion() {
      return this.dragRegion;
   }

   public ObservableList<MeasurementSourceFactory> getSourceFactories() {
      return this.sourceFactories;
   }

   public StringProperty channelNameProperty() {
      return this.channelName;
   }

   public String getChannelName() {
      return (String)this.channelName.get();
   }

   public void setChannelName(String channelName) {
      this.channelName.set(channelName);
   }

   public ObjectProperty<Unit> offsetUnitProperty() {
      return this.offsetEditor.unitProperty();
   }

   public Unit getOffsetUnit() {
      return (Unit)this.offsetEditor.unitProperty().get();
   }

   public void setOffsetUnit(Unit unit) {
      this.offsetEditor.unitProperty().set(unit);
   }

   public MessageBox getInfoMessage() {
      return this.infoMessage;
   }

   @Override
   public BooleanProperty expandedProperty() {
      return this.titledPanel.expandedProperty();
   }

   private class SuspendableChangeListener<T> implements ChangeListener<T> {
      private final ChangeListener<T> delegate;
      private final BooleanProperty suspended = new SimpleBooleanProperty();

      SuspendableChangeListener(ChangeListener<T> delegate, BooleanProperty suspended) {
         this.delegate = delegate;
         this.suspended.bind(suspended);
      }

      public void changed(ObservableValue<? extends T> observable, T oldValue, T newValue) {
         if (!this.suspended.get()) {
            this.delegate.changed(observable, oldValue, newValue);
         }
      }
   }
}
