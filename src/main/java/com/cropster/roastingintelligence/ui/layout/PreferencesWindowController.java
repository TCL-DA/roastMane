package com.cropster.roastingintelligence.ui.layout;

import com.cropster.roastingintelligence.Preferences;
import com.cropster.roastingintelligence.annotation.NotMutuallyExclusiveSource;
import com.cropster.roastingintelligence.listener.ControlStateConfigurationListener;
import com.cropster.roastingintelligence.model.ControlStateConfiguration;
import com.cropster.roastingintelligence.ui.components.AbstractDraggableConfigurationPanel;
import com.cropster.roastingintelligence.ui.components.ChannelConfigurationPanel;
import com.cropster.roastingintelligence.ui.components.ConnectorPanel;
import com.cropster.roastingintelligence.ui.components.ControlStateConfigurationPanel;
import com.cropster.roastingintelligence.ui.components.DragAndDropPlaceholder;
import com.cropster.roastingintelligence.ui.components.PopOverBubble;
import com.cropster.roastingintelligence.ui.components.SearchableListView;
import com.cropster.roastingintelligence.ui.layout.preferences.ChartPreferencesPage;
import com.cropster.roastingintelligence.ui.layout.preferences.ConfigurationPreferencesPage;
import com.cropster.roastingintelligence.ui.layout.preferences.ControlSliderPreferencesPage;
import com.cropster.roastingintelligence.ui.layout.preferences.ControlSwitchPreferencesPage;
import com.cropster.roastingintelligence.ui.layout.preferences.MeasurementPreferencesPage;
import com.cropster.roastingintelligence.ui.layout.preferences.PreferencesPanel;
import com.cropster.roastingintelligence.ui.layout.preferences.RorPreferencesPage;
import com.cropster.roastingintelligence.ui.layout.preferences.SmartPreferencesPage;
import com.cropster.roastingintelligence.ui.layout.preferences.UnitPreferencesPage;
import com.cropster.roastingintelligence.ui.util.ActionBuilder;
import com.cropster.roastingintelligence.ui.util.Icon;
import com.cropster.roastingintelligence.ui.util.ProgressTask;
import com.cropster.roastingintelligence.ui.util.SearchHighlightingListCell;
import com.cropster.roastingintelligence.ui.util.Styles;
import com.cropster.roastingintelligence.ui.util.WindowUtil;
import com.cropster.roastingintelligence.util.ChannelComparator;
import com.cropster.roastingintelligence.util.RenamingThreadFactory;
import com.cropster.util.model.Unit;
import java.io.StringReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ListView.EditEvent;
import javafx.scene.image.WritableImage;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.input.KeyCombination.Modifier;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import org.apache.commons.lang3.StringUtils;
import org.controlsfx.control.PopOver.ArrowLocation;
import org.controlsfx.control.action.Action;
import org.cropster.roastingintelligence.Defaults;
import org.cropster.roastingintelligence.RoastingIntelligenceApp;
import org.cropster.roastingintelligence.listener.ChannelConfigurationListener;
import org.cropster.roastingintelligence.listener.ConnectorConfigurationListener;
import org.cropster.roastingintelligence.measurement.MeasurementSourceFactory;
import org.cropster.roastingintelligence.measurement.SourceParameter;
import org.cropster.roastingintelligence.measurement.impl.DemoSourceFactory;
import org.cropster.roastingintelligence.model.Channel;
import org.cropster.roastingintelligence.model.ControlState;
import org.cropster.roastingintelligence.model.GroupMember;
import org.cropster.roastingintelligence.model.Machine;
import org.cropster.roastingintelligence.remote.CsarConnection;
import org.cropster.roastingintelligence.service.impl.JsonReader;
import org.cropster.roastingintelligence.util.MathUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PreferencesWindowController implements ChannelConfigurationListener, ControlStateConfigurationListener, ConnectorConfigurationListener {
   private static final String BORDER_STYLE = "-fx-border-style: solid; -fx-border-width: 1px 0 0 0; -fx-border-color: -fx-color-neutral-40; ";
   private static final String DISABLED_TEXT_STYLE = "-fx-text-fill: -fx-color-text-disabled; ";
   private final Logger logger = LoggerFactory.getLogger(PreferencesWindowController.class);
   private final ExecutorService syncExecutor = Executors.newSingleThreadExecutor(new RenamingThreadFactory("PreferencesSync"));
   private final PreferencesPanel preferencesPanel;
   private final BooleanProperty flickPredictionPossible = new SimpleBooleanProperty(false);
   private final BooleanProperty partiallyDisabled = new SimpleBooleanProperty();
   private final ObjectProperty<Unit> temperatureUnit = new SimpleObjectProperty();
   private final ObjectProperty<Unit> pressureUnit = new SimpleObjectProperty();
   private final ObjectProperty<Unit> weightUnit = new SimpleObjectProperty();
   private final SimpleObjectProperty<Class<? extends MeasurementSourceFactory>> autoStartSourceFactory = new SimpleObjectProperty();
   private final BooleanProperty filterPossible = new SimpleBooleanProperty(false);
   private final Collection<MeasurementSourceFactory> sourceFactories = new LinkedHashSet<>();
   private final ObservableList<MeasurementSourceFactory> connectors = FXCollections.observableArrayList();
   private final Set<MeasurementSourceFactory> filteredSourceFactories = new LinkedHashSet<>();
   private final ObservableList<Channel> channels = FXCollections.observableArrayList();
   private final ObservableList<ControlStateConfiguration> states = FXCollections.observableArrayList();
   private final Map<Class<? extends MeasurementSourceFactory>, Set<SourceParameter>> deviceParameters = new HashMap<>();
   private FilteredList<Channel> observableMeasurements;
   private FilteredList<Channel> observableSliderControls;
   private String[] channelOrder;
   private final HashMap<Channel, Boolean> channelExpandedMap = new HashMap<>();
   private final HashMap<ControlStateConfiguration, Boolean> stateExpandedMap = new HashMap<>();
   private DragAndDropPlaceholder dragOverPlaceholder = new DragAndDropPlaceholder();
   private BorderPane dragOverWrapper = new BorderPane();
   private AtomicReference<Channel> draggedChannelReference = new AtomicReference<>();
   private AtomicReference<ControlStateConfiguration> draggedStateReference = new AtomicReference<>();
   private Long machineId;
   private CsarConnection csarConnection;

   public PreferencesWindowController() {
      for (MeasurementSourceFactory sourceFactory : RoastingIntelligenceApp.retrieveService(MeasurementSourceFactory.class)) {
         Set<SourceParameter> parameters = sourceFactory.getParameters();
         if (!parameters.isEmpty()) {
            this.deviceParameters.put((Class<? extends MeasurementSourceFactory>)sourceFactory.getClass(), parameters);
         }

         this.sourceFactories.add(sourceFactory);
      }

      this.preferencesPanel = new PreferencesPanel();
      this.init();
   }

   private void init() {
      this.channels.addListener((ListChangeListener.Change<? extends Channel> c) -> this.updateConnectors());
      this.states.addListener((ListChangeListener.Change<? extends ControlStateConfiguration> c) -> this.updateConnectors());
      this.observableMeasurements = this.channels.filtered(c -> !c.getName().matches(".+[cC]ontrol"));
      this.observableSliderControls = this.channels.filtered(c -> c.getName().matches(".+[cC]ontrol"));
      this.connectors.addListener(
         (ListChangeListener.Change<? extends MeasurementSourceFactory> c) -> this.filterPossible.set(this.checkFilterPossible())
      );
      UnitPreferencesPage unitPage = this.getPage(UnitPreferencesPage.class);
      this.temperatureUnitProperty()
         .bind(Bindings.createObjectBinding(unitPage::getTemperatureUnit, new Observable[]{unitPage.getTemperatureUnitToggleGroup().selectedToggleProperty()}));
      this.pressureUnitProperty()
         .bind(Bindings.createObjectBinding(unitPage::getPressureUnit, new Observable[]{unitPage.getPressureUnitToggleGroup().selectedToggleProperty()}));
      this.weightUnitProperty()
         .bind(Bindings.createObjectBinding(unitPage::getWeightUnit, new Observable[]{unitPage.getWeightUnitToggleGroup().selectedToggleProperty()}));
      RorPreferencesPage rorPreferencesPage = this.getPage(RorPreferencesPage.class);
      rorPreferencesPage.rorValueProperty().addListener((s, o, n) -> {
         if (o != null) {
            int oldDefault = this.getDefault(MathUtil.roundHalfToEven((double)o.intValue() * 0.94, 0), true);
            ChartPreferencesPage chartPreferencesPage = this.getPage(ChartPreferencesPage.class);
            if (chartPreferencesPage.getChartRorMax() == oldDefault) {
               chartPreferencesPage.setChartRorMax(this.getDefault(MathUtil.roundHalfToEven((double)n.intValue() * 0.94, 0), true));
            }
         }
      });
      SmartPreferencesPage smartPage = this.getPage(SmartPreferencesPage.class);
      smartPage.temperatureUnitProperty().bind(this.temperatureUnitProperty());
      smartPage.flickPredictionCheckboxDisabledProperty()
         .bind(
            this.partiallyDisabledProperty()
               .or(smartPage.firstCrackPredictionCheckboxSelectedProperty().not())
               .or(this.flickPredictionPossibleProperty().not())
         );
      smartPage.autoStartPossibleProperty().bind(this.autoStartSourceFactoryProperty().isNotNull());
      BooleanBinding autoDetectionDisabled = smartPage.autoStartPossibleProperty()
         .and(smartPage.autoDetectionRadiobuttonSelectedProperty().not().or(smartPage.startEndCheckboxSelectedProperty().not()))
         .or(smartPage.autoStartPossibleProperty().not().and(smartPage.autoDetectionCheckboxSelectedProperty().not()));
      smartPage.startThresholdFieldDisabledProperty().bind(this.partiallyDisabledProperty().or(autoDetectionDisabled));
      smartPage.startThresholdFieldUnitProperty().bind(this.temperatureUnitProperty());
      smartPage.endThresholdFieldDisabledProperty().bind(this.partiallyDisabledProperty().or(autoDetectionDisabled));
      smartPage.endThresholdFieldUnitProperty().bind(this.temperatureUnitProperty());
      BooleanBinding startThresholdUnchanged = Bindings.createBooleanBinding(
         smartPage::compareStartThresholds, new Observable[]{smartPage.startThresholdFieldValueProperty()}
      );
      BooleanBinding endThresholdUnchanged = Bindings.createBooleanBinding(
         smartPage::compareEndThresholds, new Observable[]{smartPage.endThresholdFieldValueProperty()}
      );
      smartPage.resetThresholdButtonDisabledProperty()
         .bind(this.partiallyDisabledProperty().or(autoDetectionDisabled).or(Bindings.and(startThresholdUnchanged, endThresholdUnchanged)));
      smartPage.autoStartRadiobuttonDisableProperty().bind(this.partiallyDisabledProperty().or(smartPage.startEndCheckboxSelectedProperty().not()));
      smartPage.autoDetectionRadiobuttonDisableProperty().bind(this.partiallyDisabledProperty().or(smartPage.startEndCheckboxSelectedProperty().not()));
      this.autoStartSourceFactoryProperty().addListener((v, o, n) -> {
         if (n != null) {
            smartPage.showAutoStartPane(n);
         } else {
            smartPage.showNoAutoStartPane();
         }
      });
      if (this.getAutoStartSourceFactory() == null) {
         smartPage.showNoAutoStartPane();
      } else {
         smartPage.showAutoStartPane();
      }

      ChartPreferencesPage chartPage = this.getPage(ChartPreferencesPage.class);
      chartPage.chartYAxisMinUnitProperty().bind(this.temperatureUnitProperty());
      chartPage.chartYAxisMaxUnitProperty().bind(this.temperatureUnitProperty());
      chartPage.chartRorMinUnitProperty().bind(this.temperatureUnitProperty());
      chartPage.chartRorMaxUnitProperty().bind(this.temperatureUnitProperty());
      chartPage.setChartAxesResetter(() -> this.resetChartAxes(chartPage, rorPreferencesPage));
      chartPage.resetChartAxes();
      chartPage.chartAxesResetButtonDisableProperty()
         .bind(
            Bindings.createBooleanBinding(
               () -> this.isResetButtonDisabled(chartPage, rorPreferencesPage),
               new Observable[]{
                  rorPreferencesPage.rorValueProperty(),
                  chartPage.chartXAxisProperty(),
                  chartPage.chartYAxisMinProperty(),
                  chartPage.chartYAxisMaxProperty(),
                  chartPage.chartRorMinProperty(),
                  chartPage.chartRorMaxProperty()
               }
            )
         );
      ConfigurationPreferencesPage configurationPage = this.getPage(ConfigurationPreferencesPage.class);
      configurationPage.getAddConnectorButton().setOnAction(this::addConnector);
      configurationPage.getConnectorView().setCellFactory(lv -> new PreferencesWindowController.ConnectorListCell());
      configurationPage.getConnectorView().setItems(new SortedList(this.getObservableConnectors(), MeasurementSourceFactory.SOURCE_FACTORY_COMPARATOR));
      configurationPage.filterCheckboxSelectedProperty().addListener((v, o, n) -> this.setFiltered(n));
      configurationPage.filterPossibleProperty().bind(this.filterPossibleProperty());
      MeasurementPreferencesPage measurementPage = this.getPage(MeasurementPreferencesPage.class);
      measurementPage.getEmptyStateList().setListViewItems(this.observableMeasurements);
      measurementPage.getChannelView().setCellFactory(lv -> new PreferencesWindowController.ChannelListCell());
      ControlSliderPreferencesPage controlSliderPage = this.getPage(ControlSliderPreferencesPage.class);
      controlSliderPage.getSliderControlsEmptyStateList().setListViewItems(this.observableSliderControls);
      controlSliderPage.getSliderControlChannelView().setCellFactory(lv -> new PreferencesWindowController.ChannelListCell());
      ControlSwitchPreferencesPage controlSwitchPage = this.getPage(ControlSwitchPreferencesPage.class);
      controlSwitchPage.getSwitchControlsEmptyStateList().setListViewItems(this.states);
      controlSwitchPage.getSwitchControlChannelView().setCellFactory(lv -> new PreferencesWindowController.ControlStateConfigurationListCell());
      this.connectors
         .addListener(
            (ListChangeListener.Change<? extends MeasurementSourceFactory> c) -> {
               while (c.next()) {
                  if (c.wasRemoved()) {
                     c.getRemoved()
                        .forEach(
                           connector -> this.observableMeasurements
                                 .stream()
                                 .filter(channel -> channel.getSourceFactory() != null)
                                 .filter(channel -> channel.getSourceFactory().equals(connector.getClass()))
                                 .forEach(channel -> channel.setSourceFactory(null))
                        );
                     c.getRemoved()
                        .forEach(
                           connector -> this.observableSliderControls
                                 .stream()
                                 .filter(channel -> channel.getSourceFactory() != null)
                                 .filter(channel -> channel.getSourceFactory().equals(connector.getClass()))
                                 .forEach(channel -> channel.setSourceFactory(null))
                        );
                     Set<ControlStateConfiguration> toBeRemoved = new HashSet<>();
                     c.getRemoved()
                        .forEach(
                           connector -> this.states
                                 .stream()
                                 .filter(state -> state.getState().getSourceFactory().equals(connector.getClass()))
                                 .forEach(toBeRemoved::add)
                        );
                     this.states.removeAll(toBeRemoved);
                  }
               }

               Platform.runLater(() -> {
                  measurementPage.setWarningPanelVisible(this.checkNotAllChannelsConnectable(this.observableMeasurements));
                  controlSliderPage.setSliderControlWarningPanelVisible(this.checkNotAllChannelsConnectable(this.observableSliderControls));
               });
               measurementPage.getChannelView().refresh();
               controlSliderPage.getSliderControlChannelView().refresh();
               controlSwitchPage.getSwitchControlChannelView().refresh();
            }
         );
   }

   private void updateConnectors() {
      this.channels
         .stream()
         .map(Channel::getSourceFactory)
         .map(sfc -> this.getSourceFactories().stream().filter(sf -> sf.getClass().equals(sfc)).findFirst().orElse(null))
         .filter(Objects::nonNull)
         .filter(sf -> !this.connectors.contains(sf))
         .forEach(this.connectors::add);
      this.states
         .stream()
         .map(csc -> csc.getState().getSourceFactory())
         .map(sfc -> this.getSourceFactories().stream().filter(sf -> sf.getClass().equals(sfc)).findFirst().orElse(null))
         .filter(Objects::nonNull)
         .filter(sf -> !this.connectors.contains(sf))
         .forEach(this.connectors::add);
   }

   private boolean checkNotAllChannelsConnectable(ObservableList<Channel> channels) {
      boolean allowDemoSources = ((ConfigurationPreferencesPage)this.getPage(ConfigurationPreferencesPage.class)).isShowDemoOptions();
      return channels.stream()
         .filter(c -> !c.isActive())
         .anyMatch(
            c -> this.connectors
                  .stream()
                  .filter(sf -> (allowDemoSources || !(sf instanceof DemoSourceFactory)) && sf.getUnit(c.getName()) != null)
                  .toList()
                  .isEmpty()
         );
   }

   private void addConnector(ActionEvent actionEvent) {
      ConfigurationPreferencesPage confPage = this.getPage(ConfigurationPreferencesPage.class);
      Button addConnectorButton = confPage.getAddConnectorButton();
      FilteredList<MeasurementSourceFactory> selectableConnectors = new FilteredList(this.createSelectableConnectors(confPage.isShowDemoOptions()));
      SearchableListView<MeasurementSourceFactory> listView = new SearchableListView<>(selectableConnectors);
      listView.getStyleClass().add("connectors-selection-panel");
      listView.setCellFactory(this.createConnectorCellFactory(listView));
      Consumer<ActionEvent> applySelection = e -> {
         MeasurementSourceFactory selected = (MeasurementSourceFactory)listView.getSelectionModel().getSelectedItem();
         if (selected != null) {
            this.connectors.add(selected);
            this.states.addAll(this.getConnectorStates(selected));
         }

         Event.fireEvent(e.getTarget(), new WindowEvent(listView.getScene().getWindow(), WindowEvent.WINDOW_CLOSE_REQUEST));
      };
      Action selectAction = new ActionBuilder(org.cropster.roastingintelligence.util.Messages.m_("Select"), applySelection)
         .disabled(true)
         .graphics(Icon.CHECK_MARK.load())
         .accelerator(new KeyCodeCombination(KeyCode.ENTER, new Modifier[0]))
         .build();
      listView.getSelectionModel().selectedItemProperty().addListener((v, o, n) -> selectAction.setDisabled(n == null));
      listView.getListView().setOnMouseClicked(e -> {
         if (e.getClickCount() == 2 && listView.getSelectionModel().getSelectedItem() != null) {
            applySelection.accept(new ActionEvent(e.getTarget(), e.getTarget()));
         }
      });
      listView.getListView().setOnKeyReleased(e -> {
         if (KeyCode.ENTER.equals(e.getCode()) && listView.getSelectionModel().getSelectedItem() != null) {
            applySelection.accept(new ActionEvent(e.getTarget(), e.getTarget()));
         } else if (KeyCode.ESCAPE.equals(e.getCode())) {
            Event.fireEvent(e.getTarget(), new WindowEvent(listView.getScene().getWindow(), WindowEvent.WINDOW_CLOSE_REQUEST));
         }

         e.consume();
      });
      listView.getSearchTextField()
         .textProperty()
         .addListener(
            (v, o, n) -> selectableConnectors.setPredicate(
                  sf -> StringUtils.isEmpty(n) || StringUtils.containsIgnoreCase(org.cropster.roastingintelligence.util.Messages.m_(sf.toString()), n)
               )
         );
      PopOverBubble popover = PopOverBubble.builder()
         .arrowLocation(ArrowLocation.BOTTOM_CENTER)
         .closable(true)
         .maximumWidth(500.0)
         .onClose(() -> addConnectorButton.setDisable(false))
         .withHeader(org.cropster.roastingintelligence.util.Messages.m_("Available connectors"))
         .withContent(listView)
         .withDarkBackground()
         .withFooter(null, false, List.of(selectAction), true, true)
         .build();
      popover.setOnShowing(e -> listView.getSearchTextField().requestFocus());
      popover.showAt(addConnectorButton);
      addConnectorButton.setDisable(true);
   }

   private List<ControlStateConfiguration> getConnectorStates(MeasurementSourceFactory sourceFactory) {
      return Arrays.stream(sourceFactory.getSupportedStates(this.deviceParameters.get(sourceFactory.getClass()))).map(ControlStateConfiguration::new).toList();
   }

   public void deleteConnector(MeasurementSourceFactory sourceFactory) {
      if (sourceFactory != null) {
         int channelsUsing = (int)this.getObservableChannels().stream().filter(c -> sourceFactory.getClass().equals(c.getSourceFactory())).count();
         if (channelsUsing > 0) {
            Alert alert = WindowUtil.createAlert(
               AlertType.WARNING,
               org.cropster.roastingintelligence.util.Messages.m_("Removing connector"),
               org.cropster.roastingintelligence.util.Messages.p_(
                     "Measurements using this connector will be disconnected.", "{0} measurements using this connector will be disconnected.", channelsUsing
                  )
                  + "\n\n"
                  + org.cropster.roastingintelligence.util.Messages.m_(
                     "Do you want to delete “{0}”?", org.cropster.roastingintelligence.util.Messages.m_(sourceFactory.toString())
                  ),
               "button--warning",
               ButtonType.YES,
               ButtonType.NO
            );
            alert.getDialogPane().setMinWidth(600.0);
            Optional<ButtonType> result = WindowUtil.center((Stage)this.preferencesPanel.getScene().getWindow(), alert).showAndWait();
            if (result.isEmpty() || result.get() != ButtonType.YES) {
               return;
            }
         }

         this.connectors.remove(sourceFactory);
      }
   }

   private Callback<ListView<MeasurementSourceFactory>, ListCell<MeasurementSourceFactory>> createConnectorCellFactory(
      SearchableListView<MeasurementSourceFactory> listView
   ) {
      return v -> new SearchHighlightingListCell<MeasurementSourceFactory>(listView.getSearchTextField().textProperty(), true)
            .withStyleProvider(
               (cell, item) -> {
                  boolean collectorsContainExclusiveSources = this.connectors.stream().anyMatch(this::isExclusiveSource);
                  boolean disabled = collectorsContainExclusiveSources && this.isExclusiveSource(item);
                  String disabledStyle = disabled ? "-fx-text-fill: -fx-color-text-disabled; " : "";
                  cell.setMouseTransparent(disabled);
                  cell.setFocusTraversable(!disabled);
                  cell.setDisable(disabled);
                  ObservableList<MeasurementSourceFactory> items = cell.getListView().getItems();
                  int i = cell.getIndex();
                  String separatorStyle = i > 0
                        && MeasurementSourceFactory.groupIndexOf(item) > MeasurementSourceFactory.groupIndexOf((MeasurementSourceFactory)items.get(i - 1))
                     ? "-fx-border-style: solid; -fx-border-width: 1px 0 0 0; -fx-border-color: -fx-color-neutral-40; "
                     : "";
                  return StringUtils.defaultString(separatorStyle + disabledStyle, null);
               }
            );
   }

   private ObservableList<MeasurementSourceFactory> createSelectableConnectors(boolean showDemoSources) {
      return new SortedList(
         FXCollections.observableArrayList(
            this.getSourceFactories()
               .stream()
               .filter(sf -> showDemoSources || !(sf instanceof DemoSourceFactory))
               .filter(this::containsChannelsUnit)
               .filter(sf -> !this.connectors.contains(sf))
               .toList()
         ),
         MeasurementSourceFactory.SOURCE_FACTORY_COMPARATOR
      );
   }

   private boolean isExclusiveSource(MeasurementSourceFactory sf) {
      return sf.getClass().getDeclaredAnnotation(NotMutuallyExclusiveSource.class) == null;
   }

   private boolean containsChannelsUnit(MeasurementSourceFactory sf) {
      return this.getObservableChannels().stream().map(Channel::getName).anyMatch(channelName -> sf.getUnit(channelName) != null);
   }

   private boolean isResetButtonDisabled(ChartPreferencesPage chartPage, RorPreferencesPage rorPreferencesPage) {
      if (1020L != chartPage.getChartXAxis()) {
         return false;
      } else if (this.getDefault(45.0, false) != chartPage.getChartYAxisMin()) {
         return false;
      } else if (this.getDefault(275.0, false) != chartPage.getChartYAxisMax()) {
         return false;
      } else if (this.getDefault(0.0, true) != chartPage.getChartRorMin()) {
         return false;
      } else {
         int ror = rorPreferencesPage.getRor();
         int rorMaxDefault = this.getDefault(MathUtil.roundHalfToEven((double)ror * 0.94, 0), true);
         return rorMaxDefault == chartPage.getChartRorMax();
      }
   }

   private void resetChartAxes(ChartPreferencesPage chartPage, RorPreferencesPage rorPreferencesPage) {
      chartPage.setChartXAxis(1020L);
      chartPage.setChartYAxisMin(this.getDefault(45.0, false));
      chartPage.setChartYAxisMax(this.getDefault(275.0, false));
      chartPage.setChartRorMin(this.getDefault(0.0, true));
      chartPage.setChartRorMax(this.getDefault(MathUtil.roundHalfToEven((double)rorPreferencesPage.getRor() * 0.94, 0), true));
      chartPage.setChartAxesChangedLabelVisible(true);
   }

   @Override
   public void settingsChanged(Channel channel) {
      ListView<Channel> channelView = ((MeasurementPreferencesPage)this.getPage(MeasurementPreferencesPage.class)).getChannelView();
      this.setFlickPredictionPossible(this.checkGasEnabled());
      if (!channel.getName().matches(".+[cC]ontrol")) {
         this.channels.stream().filter(c -> c.getName().matches(channel.getName() + "Control")).findFirst().ifPresent(c -> {
            c.setColor(channel.getColor());
            channelView.fireEvent(new EditEvent(channelView, ListView.editCommitEvent(), c, channelView.getItems().indexOf(c)));
         });
      }

      this.checkAutoStartPossible();
      this.filterPossible.set(this.checkFilterPossible());
      ((ConfigurationPreferencesPage)this.getPage(ConfigurationPreferencesPage.class)).getConnectorView().refresh();
   }

   @Override
   public void settingsChanged(ControlStateConfiguration stateConfiguration) {
      this.setFlickPredictionPossible(this.checkGasEnabled());
      this.checkAutoStartPossible();
      ((ConfigurationPreferencesPage)this.getPage(ConfigurationPreferencesPage.class)).getConnectorView().refresh();
   }

   @Override
   public void settingsChanged(MeasurementSourceFactory sourceFactory) {
      this.setFlickPredictionPossible(this.checkGasEnabled());
      this.checkAutoStartPossible();
      this.filterPossible.set(this.checkFilterPossible());
      ((MeasurementPreferencesPage)this.getPage(MeasurementPreferencesPage.class)).getChannelView().refresh();
      this.updateStates(sourceFactory);
   }

   private void updateStates(MeasurementSourceFactory sourceFactory) {
      List<ControlState> expectedStates = Stream.of(sourceFactory.getSupportedStates(this.deviceParameters.get(sourceFactory.getClass()))).toList();
      List<ControlStateConfiguration> existingStateConfigurations = this.states
         .stream()
         .filter(csc -> csc.getState().getSourceFactory().equals(sourceFactory.getClass()))
         .toList();
      this.states.removeAll(existingStateConfigurations);
      this.states.addAll(this.getConnectorStates(expectedStates, existingStateConfigurations));
      ((ControlSwitchPreferencesPage)this.getPage(ControlSwitchPreferencesPage.class)).getSwitchControlChannelView().refresh();
   }

   private List<ControlStateConfiguration> getConnectorStates(List<ControlState> expectedStates, List<ControlStateConfiguration> existingStateConfigurations) {
      List<ControlStateConfiguration> toBeReturned = new ArrayList<>();
      expectedStates.stream()
         .filter(cs -> existingStateConfigurations.stream().noneMatch(csc -> csc.getState().equals(cs)))
         .map(ControlStateConfiguration::new)
         .forEach(toBeReturned::add);
      expectedStates.stream()
         .map(cs -> existingStateConfigurations.stream().filter(csc -> csc.getState().equals(cs)).findFirst().orElse(null))
         .filter(csc -> csc != null)
         .forEach(toBeReturned::add);
      return toBeReturned;
   }

   public <T extends Node> T getPage(Class<T> pageClass) {
      return this.preferencesPanel.getPage(pageClass);
   }

   public void selectPage(Class<? extends Node> pageClass) {
      this.preferencesPanel.selectPage(pageClass);
   }

   public void setDisablePartially(boolean disable) {
      this.setPartiallyDisabled(disable);
      this.preferencesPanel.setDisablePartially(disable);
   }

   public void resetChangeLabels() {
      this.preferencesPanel.resetChangeLabels();
   }

   public void setSaveHandler(EventHandler<ActionEvent> saveHandler) {
      this.preferencesPanel.setSaveHandler(saveHandler);
   }

   public void setCancelHandler(EventHandler<ActionEvent> cancelHandler) {
      this.preferencesPanel.setCancelHandler(cancelHandler);
   }

   public PreferencesPanel getPreferencesPanel() {
      return this.preferencesPanel;
   }

   public Machine getMachine() {
      return (Machine)((ConfigurationPreferencesPage)this.getPage(ConfigurationPreferencesPage.class))
         .getMachineComboBox()
         .getSelectionModel()
         .getSelectedItem();
   }

   public void setMachine(Machine machine, boolean riPermissionGranted) {
      ConfigurationPreferencesPage configPage = this.getPage(ConfigurationPreferencesPage.class);
      if (!riPermissionGranted && machine != null) {
         configPage.getWarningMessageBox()
            .setText(
               org.cropster.roastingintelligence.util.Messages.m_(
                  "The user’s permissions to access Roasting Intelligence for the following group and its associated machines were revoked: <b>{0}</b>.",
                  machine.getGroup().getName()
               )
            );
         configPage.getWarningMessageBox().setVisible(true);
         machine = null;
      } else if (machine != null && !configPage.getMachineComboBox().getItems().contains(machine)) {
         configPage.getWarningMessageBox()
            .setText(
               org.cropster.roastingintelligence.util.Messages.m_("The following machine was deleted in the web platform: <b>{0}</b>.", machine.getName())
            );
         configPage.getWarningMessageBox().setVisible(true);
         machine = null;
      } else {
         configPage.getWarningMessageBox().setVisible(false);
      }

      configPage.getMachineComboBox().getSelectionModel().select(machine);
   }

   public void setMachines(Collection<Machine> machines, Map<String, GroupMember> groupMembers) {
      List<Machine> validMachines = machines.stream()
         .filter(m -> m.getGroup() != null)
         .filter(m -> groupMembers.containsKey(m.getGroup().getCode()))
         .filter(m -> groupMembers.get(m.getGroup().getCode()).getPermissions().contains("roastRoastingIntelligence"))
         .toList();
      ComboBox<Machine> machineComboBox = ((ConfigurationPreferencesPage)this.getPage(ConfigurationPreferencesPage.class)).getMachineComboBox();
      machineComboBox.setOnAction(null);
      machineComboBox.setItems(FXCollections.observableArrayList(validMachines));
      machineComboBox.setOnAction(e -> this.switchMachine((Machine)machineComboBox.getValue()));
   }

   public Collection<Channel> getChannels() {
      return new LinkedHashSet<>(this.channels);
   }

   public ObservableList<Channel> getObservableChannels() {
      return this.channels;
   }

   public Collection<ControlStateConfiguration> getStates() {
      return new LinkedHashSet<>(this.states);
   }

   public ObservableList<ControlStateConfiguration> getObservableStates() {
      return this.states;
   }

   public ObservableList<MeasurementSourceFactory> getObservableConnectors() {
      return this.connectors;
   }

   public List<MeasurementSourceFactory> getUnconnectedConnectors() {
      return this.connectors
         .stream()
         .filter(sf -> this.getObservableChannels().stream().noneMatch(c -> sf.getClass().equals(c.getSourceFactory())))
         .filter(sf -> this.getObservableStates().stream().noneMatch(sc -> sf.getClass().equals(sc.getState().getSourceFactory()) && sc.isEnabled()))
         .toList();
   }

   public void setCsarConnection(CsarConnection csarConnection) {
      this.csarConnection = csarConnection;
   }

   public Map<Class<? extends MeasurementSourceFactory>, Set<SourceParameter>> getDeviceParameters() {
      return Collections.unmodifiableMap(this.deviceParameters);
   }

   public Set<SourceParameter> getDeviceParameters(Class<? extends MeasurementSourceFactory> sourceFactoryClass) {
      return this.deviceParameters.containsKey(sourceFactoryClass) ? this.deviceParameters.get(sourceFactoryClass) : Collections.emptySet();
   }

   public void setDeviceParameters(Class<? extends MeasurementSourceFactory> sourceFactory, Set<SourceParameter> parameters) {
      this.deviceParameters.put(sourceFactory, parameters);
   }

   public Collection<MeasurementSourceFactory> getSourceFactories() {
      return Collections.unmodifiableCollection(this.sourceFactories);
   }

   public boolean checkGasEnabled() {
      for (Channel channel : this.channels) {
         if (channel.isActive() && "gas".equals(channel.getName()) && channel.getSourceFactory() != null) {
            return true;
         }
      }

      return false;
   }

   public ObjectProperty<Unit> temperatureUnitProperty() {
      return this.temperatureUnit;
   }

   public Unit getTemperatureUnit() {
      return (Unit)this.temperatureUnit.get();
   }

   public ObjectProperty<Unit> pressureUnitProperty() {
      return this.pressureUnit;
   }

   public Unit getPressureUnit() {
      return (Unit)this.pressureUnit.get();
   }

   public ObjectProperty<Unit> weightUnitProperty() {
      return this.weightUnit;
   }

   public Unit getWeightUnit() {
      return (Unit)this.weightUnit.get();
   }

   public BooleanProperty flickPredictionPossibleProperty() {
      return this.flickPredictionPossible;
   }

   public boolean isFlickPredictionPossible() {
      return this.flickPredictionPossible.get();
   }

   public void setFlickPredictionPossible(boolean flickPredictionPossible) {
      this.flickPredictionPossible.set(flickPredictionPossible);
   }

   public BooleanProperty partiallyDisabledProperty() {
      return this.partiallyDisabled;
   }

   public boolean isPartiallyDisabled() {
      return this.partiallyDisabled.get();
   }

   public void setPartiallyDisabled(boolean partiallyDisabled) {
      this.partiallyDisabled.set(partiallyDisabled);
   }

   public SimpleObjectProperty<Class<? extends MeasurementSourceFactory>> autoStartSourceFactoryProperty() {
      return this.autoStartSourceFactory;
   }

   public Class<? extends MeasurementSourceFactory> getAutoStartSourceFactory() {
      return (Class<? extends MeasurementSourceFactory>)this.autoStartSourceFactory.get();
   }

   public void setAutoStartSourceFactory(Class<? extends MeasurementSourceFactory> autoStartSourceFactory) {
      this.autoStartSourceFactory.set(autoStartSourceFactory);
   }

   public void checkAutoStartPossible() {
      List<Class<? extends MeasurementSourceFactory>> autoStartSourceFactories = this.findAutoStartSourceFactories();
      this.setAutoStartSourceFactory(autoStartSourceFactories.isEmpty() ? null : autoStartSourceFactories.get(0));
   }

   public List<Class<? extends MeasurementSourceFactory>> findAutoStartSourceFactories() {
      List<Class<? extends MeasurementSourceFactory>> autoStartSourceFactories = new ArrayList<>();

      for (Channel channel : this.channels) {
         if (channel.isActive() && channel.getSourceFactory() != null) {
            MeasurementSourceFactory sourceFactory = RoastingIntelligenceApp.retrieveSingleton(channel.getSourceFactory());
            Set<SourceParameter> parameters = this.deviceParameters.get(channel.getSourceFactory());
            if (parameters != null && sourceFactory.supportsAutoStart(parameters)) {
               autoStartSourceFactories.add(channel.getSourceFactory());
            }
         }
      }

      return autoStartSourceFactories;
   }

   public BooleanProperty filterPossibleProperty() {
      return this.filterPossible;
   }

   public boolean isFilterPossible() {
      return this.filterPossible.get();
   }

   public void setFilterPossible(boolean filterPossible) {
      this.filterPossible.set(filterPossible);
   }

   public String[] getChannelOrder() {
      return this.channelOrder;
   }

   public void setChannelOrder(String[] channelOrder) {
      this.channelOrder = channelOrder;
   }

   public void applyPreferences(Machine selectedMachine, Preferences preferences) {
      assert Platform.isFxApplicationThread();

      Map<Class<? extends MeasurementSourceFactory>, MeasurementSourceFactory> toBeRemoved = this.connectors
         .stream()
         .collect(Collectors.toMap(connector -> connector.getClass(), connector -> connector));
      if (selectedMachine != null && selectedMachine.getChannels() != null) {
         int c = 0;
         int index = 0;
         List<Channel> mergedChannels = new LinkedList<>();

         for (String channelName : selectedMachine.getChannels()) {
            Channel channel = Optional.ofNullable(preferences)
               .map(Preferences::getChannels)
               .map(Collection::stream)
               .orElse(Stream.empty())
               .filter(cc -> channelName.equals(cc.getName()))
               .findFirst()
               .orElse(null);
            if (channel == null) {
               channel = this.createChannel(channelName, index++, c);
            } else {
               index = channel.getIndex() + 1;
               channel = channel.clone();
            }

            mergedChannels.add(channel);
            if (channel.getSourceFactory() != null) {
               MeasurementSourceFactory sourceFactory = RoastingIntelligenceApp.retrieveSingleton(channel.getSourceFactory());
               this.setDeviceParameters(channel.getSourceFactory(), preferences.merge(sourceFactory));
            }

            c++;
         }

         mergedChannels.sort(new ChannelComparator(this.channelOrder));
         this.channels.setAll(mergedChannels);
         if (preferences != null) {
            this.states.setAll(this.getAvailableStates(preferences));
         }
      } else {
         this.channels.clear();
         this.states.clear();
      }

      this.channels.forEach(cx -> toBeRemoved.remove(cx.getSourceFactory()));
      this.states.stream().filter(s -> s.isEnabled()).forEach(s -> toBeRemoved.remove(s.getState().getSourceFactory()));
      toBeRemoved.values().forEach(this.connectors::remove);
      this.setFlickPredictionPossible(this.checkGasEnabled());
      this.checkAutoStartPossible();
      this.filterPossible.set(this.checkFilterPossible());
      ((ConfigurationPreferencesPage)this.getPage(ConfigurationPreferencesPage.class)).filterCheckboxSelectedProperty().set(this.isFiltered());
   }

   private Collection<ControlStateConfiguration> getAvailableStates(Preferences preferences) {
      List<MeasurementSourceFactory> factories = new ArrayList<>(this.getObservableConnectors());
      preferences.getStates().forEach(configuration -> {
         Class<? extends MeasurementSourceFactory> factoryClass = configuration.getState().getSourceFactory();
         boolean alreadyExists = factories.stream().anyMatch(f -> f.getClass().equals(factoryClass));
         if (!alreadyExists) {
            factories.add(RoastingIntelligenceApp.retrieveSingleton(factoryClass));
         }
      });
      List<ControlStateConfiguration> availableStates = factories.stream().flatMap(sf -> this.getConnectorStates(sf).stream()).collect(Collectors.toList());
      availableStates.forEach(s -> s.setEnabled(false));
      List<ControlStateConfiguration> toBeReplaced = new ArrayList<>();
      preferences.getStates()
         .forEach(configuration -> availableStates.stream().filter(c -> c.getState().equals(configuration.getState())).findFirst().ifPresent(toBeReplaced::add));
      availableStates.removeAll(toBeReplaced);
      preferences.getStates().forEach(s -> s.setEnabled(true));
      int i = 0;

      for (ControlStateConfiguration state : preferences.getStates()) {
         MeasurementSourceFactory factory = factories.stream()
            .filter(msf -> msf.getClass().equals(state.getState().getSourceFactory()))
            .findFirst()
            .orElse(null);
         if (factory != null) {
            int index = i++;
            Stream.of(factory.getSupportedStates(this.deviceParameters.get(factory.getClass())))
               .filter(cs -> cs.equals(state.getState()))
               .findFirst()
               .ifPresent(s -> availableStates.add(index, state));
         }
      }

      return availableStates;
   }

   private void switchMachine(Machine selectedMachine) {
      if (selectedMachine != null) {
         if (selectedMachine.getChannels() != null && !selectedMachine.getId().equals(this.machineId)) {
            this.loadPreferences(selectedMachine);
            this.machineId = selectedMachine.getId();
         }
      } else {
         this.machineId = null;
      }

      this.connectors.clear();
   }

   private void loadPreferences(final Machine selectedMachine) {
      this.logger.info("Loading preferences for machine {}", selectedMachine.getId());
      ProgressTask<Preferences> syncTask = new ProgressTask<Preferences>(
         (Stage)this.preferencesPanel.getScene().getWindow(), org.cropster.roastingintelligence.util.Messages.m_("Loading machine preferences ...")
      ) {
         protected Preferences call() throws Exception {
            String json = PreferencesWindowController.this.csarConnection.loadPreferences(selectedMachine.getId());
            if (json != null) {
               PreferencesWindowController.this.logger.info("Preferences loaded for machine {}", selectedMachine.getId());
               JsonReader jsonReader = new JsonReader();
               return jsonReader.readPreferences(new StringReader(json));
            } else {
               PreferencesWindowController.this.logger.info("No preferences found for machine {}", selectedMachine.getId());
               return null;
            }
         }

         @Override
         protected void done() {
            try {
               Preferences preferences = (Preferences)this.get();
               if (preferences != null) {
                  preferences.applyChannelChecks();
               }

               Platform.runLater(() -> PreferencesWindowController.this.applyPreferences(selectedMachine, preferences));
            } catch (Exception var2) {
               PreferencesWindowController.this.logger.error("Error loading preferences", var2);
               Platform.runLater(() -> PreferencesWindowController.this.applyPreferences(selectedMachine, new Preferences()));
            }

            super.done();
         }

         @Override
         protected void succeeded() {
            PreferencesWindowController.this.logger.info("Preferences loaded");
         }

         @Override
         protected void cancelled() {
            PreferencesWindowController.this.logger.info("Preferences loading cancelled");
         }

         @Override
         protected void failed() {
            if (PreferencesWindowController.this.logger.isDebugEnabled()) {
               PreferencesWindowController.this.logger.debug("Error loading preferences", this.getException());
            } else {
               PreferencesWindowController.this.logger.info("Error loading preferences: {}", this.getException().getMessage());
            }
         }
      };
      this.syncExecutor.execute(syncTask);
   }

   private Channel createChannel(String channelName, int index, int colorIndex) {
      Channel channel = new Channel(channelName);
      channel.setIndex(index);
      if ("beanTemperature".equals(channelName)) {
         channel.setRorEnabled(true);
      } else if (channelName.matches(".+[cC]ontrol")) {
         channel.setVisibility(EnumSet.of(Channel.Visibility.CHART));
      }

      if (colorIndex < Defaults.COLORS.length) {
         channel.setColor(Defaults.COLORS[colorIndex]);
      }

      return channel;
   }

   public boolean checkFilterPossible() {
      this.filteredSourceFactories.clear();
      this.filteredSourceFactories.addAll(this.findFilteredSourceFactories());
      return !this.filteredSourceFactories.isEmpty();
   }

   public void setFiltered(boolean isFiltered) {
      this.filteredSourceFactories
         .forEach(
            sourceFactory -> Optional.ofNullable(this.deviceParameters.get(sourceFactory.getClass()))
                  .ifPresent(
                     parameters -> parameters.stream()
                           .filter(parameter -> "filter".equals(parameter.getName()))
                           .findFirst()
                           .ifPresent(
                              parameter -> parameter.setValue(
                                    String.valueOf(isFiltered && sourceFactory.supportsFilterOption((Set<SourceParameter>)parameters))
                                 )
                           )
                  )
         );
   }

   public boolean isFiltered() {
      return this.filteredSourceFactories
         .stream()
         .anyMatch(
            sourceFactory -> Optional.ofNullable(this.deviceParameters.get(sourceFactory.getClass()))
                  .map(
                     parameters -> sourceFactory.supportsFilterOption((Set<SourceParameter>)parameters)
                           && parameters.stream().anyMatch(parameter -> "filter".equals(parameter.getName()) && Boolean.parseBoolean(parameter.getValue()))
                  )
                  .orElse(false)
         );
   }

   public List<MeasurementSourceFactory> findFilteredSourceFactories() {
      return this.connectors
         .stream()
         .filter(
            sourceFactory -> Optional.ofNullable(this.deviceParameters.get(sourceFactory.getClass()))
                  .map(
                     parameters -> parameters.stream().anyMatch(p -> "filter".equals(p.getName()))
                           && sourceFactory.supportsFilterOption((Set<SourceParameter>)parameters)
                  )
                  .orElse(false)
         )
         .collect(Collectors.toList());
   }

   private int getDefault(Double defaultValue, boolean relative) {
      return Unit.FAHRENHEIT.equals(this.temperatureUnit.get())
         ? Unit.FAHRENHEIT.from(BigDecimal.valueOf(defaultValue), Unit.CELSIUS, null, relative).setScale(0, RoundingMode.HALF_EVEN).intValue()
         : defaultValue.intValue();
   }

   private abstract class AbstractListCell<T, P extends AbstractDraggableConfigurationPanel> extends ListCell<T> {
      private final DataFormat dataFormat;
      private final P configurationPanel;
      private final AtomicReference<T> draggedItemReference;

      protected AbstractListCell(DataFormat dataFormat, P configurationPanel, HashMap<T, Boolean> expandedMap, AtomicReference<T> draggedItemReference) {
         this.dataFormat = dataFormat;
         this.configurationPanel = configurationPanel;
         this.draggedItemReference = draggedItemReference;
         configurationPanel.getDragRegion().setOnDragDetected(this::onDragDetected);
         configurationPanel.setOnDragOver(this::onDragOver);
         configurationPanel.setOnDragEntered(this::onDragEntered);
         configurationPanel.setOnDragExited(this::onDragExited);
         configurationPanel.setOnDragDropped(this::onDragDropped);
         configurationPanel.setOnDragDone(this::onDragDone);
         configurationPanel.expandedProperty().addListener((v, o, n) -> {
            T item = (T)this.getItem();
            if (item != null) {
               expandedMap.put(item, n);
            }
         });
         Platform.runLater(configurationPanel::layout);
      }

      public P getConfigurationPanel() {
         return this.configurationPanel;
      }

      private void onDragDetected(MouseEvent e) {
         ClipboardContent content = new ClipboardContent();
         content.put(this.dataFormat, this.getItem().hashCode());
         SnapshotParameters params = new SnapshotParameters();
         WritableImage dragImage = new WritableImage((int)this.configurationPanel.getWidth(), (int)this.configurationPanel.getHeight());
         WritableImage snapshot = this.configurationPanel.snapshot(params, dragImage);
         Dragboard dragBoard = this.configurationPanel.startDragAndDrop(new TransferMode[]{TransferMode.MOVE});
         dragBoard.setContent(content);
         dragBoard.setDragView(snapshot, snapshot.getWidth() / 2.0, -snapshot.getHeight() / 2.0);
         this.draggedItemReference.set((T)this.getItem());
         this.configurationPanel.getStyleClass().add("channel-configuration-panel--dragged");
         e.consume();
      }

      private void onDragDone(DragEvent e) {
         this.configurationPanel.getStyleClass().remove("channel-configuration-panel--dragged");
         if (e.getTransferMode() == TransferMode.MOVE) {
            e.getDragboard().clear();
            this.draggedItemReference.set(null);
         }

         e.consume();
      }

      private void onDragDropped(DragEvent e) {
         if (this.getItem() != null) {
            boolean success = false;
            Dragboard dragBoard = e.getDragboard();
            if (dragBoard.hasContent(this.dataFormat)) {
               success = this.finalizeDragDropped(e, this.draggedItemReference.get());
            }

            e.setDropCompleted(success);
            e.consume();
         }
      }

      protected abstract boolean finalizeDragDropped(DragEvent var1, T var2);

      private void onDragEntered(DragEvent e) {
         if (this.getItem() != this.draggedItemReference.get()) {
            this.getStyleClass().add("channel-configuration-panel--dragover");
         }

         e.consume();
      }

      private void onDragExited(DragEvent e) {
         PreferencesWindowController.this.dragOverWrapper.setTop(null);
         PreferencesWindowController.this.dragOverWrapper.setBottom(null);
         PreferencesWindowController.this.dragOverWrapper.setCenter(null);
         this.setGraphic(this.configurationPanel);
         this.getStyleClass().remove("channel-configuration-panel--dragover");
         e.consume();
      }

      private void onDragOver(DragEvent e) {
         if (e.getGestureSource() != this && e.getDragboard().hasContent(this.dataFormat) && this.getItem() != null) {
            e.acceptTransferModes(new TransferMode[]{TransferMode.MOVE});
            if (this.getGraphic() != PreferencesWindowController.this.dragOverWrapper) {
               PreferencesWindowController.this.dragOverWrapper.setBottom(PreferencesWindowController.this.dragOverPlaceholder);
               PreferencesWindowController.this.dragOverWrapper.setCenter(this.configurationPanel);
               this.setGraphic(PreferencesWindowController.this.dragOverWrapper);
            }
         }

         e.consume();
      }

      protected void updateItem(T item, boolean empty) {
         super.updateItem(item, empty);
         this.setPadding(Styles.INSETS_EMPTY);
         if (empty || item == null) {
            this.getConfigurationPanel().reset();
            this.setGraphic(null);
         }

         this.setText(null);
      }
   }

   private final class ChannelListCell extends PreferencesWindowController.AbstractListCell<Channel, ChannelConfigurationPanel> {
      private static final DataFormat DATA_FORMAT = new DataFormat(new String[]{PreferencesWindowController.ChannelListCell.class.getSimpleName()});

      ChannelListCell() {
         super(
            DATA_FORMAT,
            new ChannelConfigurationPanel(PreferencesWindowController.this),
            PreferencesWindowController.this.channelExpandedMap,
            PreferencesWindowController.this.draggedChannelReference
         );
      }

      protected boolean finalizeDragDropped(DragEvent e, Channel draggedChannel) {
         ObservableList<Channel> observableChannels = PreferencesWindowController.this.getObservableChannels();
         int oldIndex = observableChannels.indexOf(draggedChannel);
         int newIndex = observableChannels.indexOf(this.getItem());
         if (e.getY() > this.getConfigurationPanel().getHeight() / 2.0) {
            newIndex++;
         }

         observableChannels.remove(draggedChannel);
         if (newIndex > oldIndex) {
            observableChannels.add(newIndex - 1, draggedChannel);
         } else {
            observableChannels.add(newIndex, draggedChannel);
         }

         PreferencesWindowController.this.setChannelOrder(observableChannels.stream().map(Channel::getName).toArray(String[]::new));
         return true;
      }

      protected void updateItem(Channel item, boolean empty) {
         super.updateItem(item, empty);
         ChannelConfigurationPanel channelPanel = this.getConfigurationPanel();
         if (!empty && item != null) {
            UnitPreferencesPage unitPage = PreferencesWindowController.this.getPage(UnitPreferencesPage.class);
            boolean allowDemoSources = ((ConfigurationPreferencesPage)PreferencesWindowController.this.getPage(ConfigurationPreferencesPage.class))
               .isShowDemoOptions();
            Collection<MeasurementSourceFactory> selectableSourceFactories = PreferencesWindowController.this.connectors
               .stream()
               .filter(sf -> (allowDemoSources || !(sf instanceof DemoSourceFactory)) && sf.getUnit(item.getName()) != null)
               .toList();
            if (this.containsUnit(item, selectableSourceFactories, unitPage.getTemperatureUnitToggleGroup())) {
               channelPanel.offsetUnitProperty().bind(PreferencesWindowController.this.temperatureUnitProperty());
            } else if (this.containsUnit(item, selectableSourceFactories, unitPage.getPressureUnitToggleGroup())) {
               channelPanel.offsetUnitProperty().bind(PreferencesWindowController.this.pressureUnitProperty());
            } else if (this.containsUnit(item, selectableSourceFactories, unitPage.getWeightUnitToggleGroup())) {
               channelPanel.offsetUnitProperty().bind(PreferencesWindowController.this.weightUnitProperty());
            } else {
               selectableSourceFactories.stream()
                  .map(sf -> sf.getUnit(item.getName()))
                  .filter(Objects::nonNull)
                  .findFirst()
                  .ifPresent(u -> channelPanel.offsetUnitProperty().bind(new SimpleObjectProperty(u)));
            }

            channelPanel.init(item, selectableSourceFactories, PreferencesWindowController.this.getDeviceParameters());
            channelPanel.setExpanded(PreferencesWindowController.this.channelExpandedMap.getOrDefault(item, false));
            this.setGraphic(channelPanel);
         } else {
            channelPanel.offsetUnitProperty().unbind();
            channelPanel.getInfoMessage().setText("");
            channelPanel.getInfoMessage().setVisible(false);
            channelPanel.getInfoMessage().setManaged(false);
         }
      }

      private boolean containsUnit(Channel channel, Collection<? extends MeasurementSourceFactory> sourceFactories, ToggleGroup group) {
         Optional<Unit> optionalUnit = sourceFactories.stream().map(sf -> sf.getUnit(channel.getName())).filter(Objects::nonNull).findFirst();
         return optionalUnit.isPresent()
            && group.getToggles().stream().<Object>map(Toggle::getUserData).anyMatch(ud -> Objects.equals(ud, optionalUnit.get().name()));
      }
   }

   private final class ConnectorListCell extends com.cropster.roastingintelligence.ui.util.AbstractListCell<MeasurementSourceFactory> {
      private final ConnectorPanel viewer = new ConnectorPanel(PreferencesWindowController.this);

      protected void initViewer(MeasurementSourceFactory item) {
         if (item != null) {
            this.viewer
               .init(item, PreferencesWindowController.this.deviceParameters.get(item.getClass()), () -> PreferencesWindowController.this.deleteConnector(item));
            this.updateNotUsedVisibility();
            this.setGraphic(this.viewer);
            this.setEditable(false);
         }
      }

      protected void initEditor(MeasurementSourceFactory item) {
      }

      protected void updateItem(MeasurementSourceFactory item, boolean empty) {
         super.updateItem(item, empty);
         if (item != null) {
            this.updateNotUsedVisibility();
         }
      }

      private void updateNotUsedVisibility() {
         boolean notUsedInChannels = PreferencesWindowController.this.getObservableChannels()
            .stream()
            .noneMatch(channel -> ((MeasurementSourceFactory)this.getItem()).getClass().equals(channel.getSourceFactory()));
         boolean notUsedInStates = PreferencesWindowController.this.getObservableStates()
            .stream()
            .noneMatch(sc -> ((MeasurementSourceFactory)this.getItem()).getClass().equals(sc.getState().getSourceFactory()) && sc.isEnabled());
         this.viewer.setNotUsedVisible(notUsedInChannels && notUsedInStates);
      }
   }

   private final class ControlStateConfigurationListCell
      extends PreferencesWindowController.AbstractListCell<ControlStateConfiguration, ControlStateConfigurationPanel> {
      private static final DataFormat DATA_FORMAT = new DataFormat(
         new String[]{PreferencesWindowController.ControlStateConfigurationListCell.class.getSimpleName()}
      );

      ControlStateConfigurationListCell() {
         super(
            DATA_FORMAT,
            new ControlStateConfigurationPanel(PreferencesWindowController.this),
            PreferencesWindowController.this.stateExpandedMap,
            PreferencesWindowController.this.draggedStateReference
         );
      }

      protected boolean finalizeDragDropped(DragEvent e, ControlStateConfiguration draggedState) {
         ObservableList<ControlStateConfiguration> observableStates = PreferencesWindowController.this.getObservableStates();
         int oldIndex = observableStates.indexOf(draggedState);
         int newIndex = observableStates.indexOf(this.getItem());
         if (e.getY() > this.getConfigurationPanel().getHeight() / 2.0) {
            newIndex++;
         }

         observableStates.remove(draggedState);
         if (newIndex > oldIndex) {
            observableStates.add(newIndex - 1, draggedState);
         } else {
            observableStates.add(newIndex, draggedState);
         }

         return true;
      }

      protected void updateItem(ControlStateConfiguration item, boolean empty) {
         super.updateItem(item, empty);
         ControlStateConfigurationPanel statePanel = this.getConfigurationPanel();
         if (!empty && item != null) {
            statePanel.init(item);
            statePanel.setExpanded(PreferencesWindowController.this.stateExpandedMap.getOrDefault(item, false));
            this.setGraphic(statePanel);
         }
      }
   }
}
