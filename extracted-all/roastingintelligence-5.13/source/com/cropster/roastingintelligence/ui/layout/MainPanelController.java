package com.cropster.roastingintelligence.ui.layout;

import com.cropster.roastingintelligence.Preferences;
import com.cropster.roastingintelligence.model.CombinedMeasure;
import com.cropster.roastingintelligence.model.notification.Notification;
import com.cropster.roastingintelligence.model.notification.NotificationType;
import com.cropster.roastingintelligence.service.LiveGoalService;
import com.cropster.roastingintelligence.service.NotificationBusService;
import com.cropster.roastingintelligence.ui.ComponentManager;
import com.cropster.roastingintelligence.ui.JavaFxApplicationState;
import com.cropster.roastingintelligence.ui.JavaFxUtil;
import com.cropster.roastingintelligence.ui.PermissionNotificationController;
import com.cropster.roastingintelligence.ui.ToastNotificationController;
import com.cropster.roastingintelligence.ui.chart.BetweenBatchChartWrapper;
import com.cropster.roastingintelligence.ui.components.ChannelStatusLabel;
import com.cropster.roastingintelligence.ui.components.CommentPopupContent;
import com.cropster.roastingintelligence.ui.components.ControlSlider;
import com.cropster.roastingintelligence.ui.components.ControlSliderToggleBar;
import com.cropster.roastingintelligence.ui.components.ControlStateBar;
import com.cropster.roastingintelligence.ui.components.ControlToggleBar;
import com.cropster.roastingintelligence.ui.components.HintsButton;
import com.cropster.roastingintelligence.ui.components.MessageBox;
import com.cropster.roastingintelligence.ui.components.PercentageLabel;
import com.cropster.roastingintelligence.ui.components.PopOverBubble;
import com.cropster.roastingintelligence.ui.components.RadioToggleButton;
import com.cropster.roastingintelligence.ui.components.ReferenceInfoPanel;
import com.cropster.roastingintelligence.ui.components.SidebarButton;
import com.cropster.roastingintelligence.ui.components.StatusDot;
import com.cropster.roastingintelligence.ui.components.TimerLabel;
import com.cropster.roastingintelligence.ui.components.TitledPaneRight;
import com.cropster.roastingintelligence.ui.components.ToggleSwitch;
import com.cropster.roastingintelligence.ui.components.Tooltip;
import com.cropster.roastingintelligence.ui.handler.measurement.HandlerChainManager;
import com.cropster.roastingintelligence.ui.list.EmptyStateGrid;
import com.cropster.roastingintelligence.ui.list.GridView;
import com.cropster.roastingintelligence.ui.util.ActionBuilder;
import com.cropster.roastingintelligence.ui.util.ChartUtils;
import com.cropster.roastingintelligence.ui.util.Icon;
import com.cropster.roastingintelligence.ui.util.KeyboardShortcut;
import com.cropster.roastingintelligence.ui.util.PopOverHandler;
import com.cropster.roastingintelligence.ui.util.SlidingAnimation;
import com.cropster.roastingintelligence.ui.util.Styles;
import com.cropster.util.model.Unit;
import java.net.URL;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.Map.Entry;
import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.RotateTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.event.WeakEventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.Orientation;
import javafx.geometry.Point2D;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TitledPane;
import javafx.scene.control.SplitPane.Divider;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;
import org.controlsfx.control.PopOver.ArrowLocation;
import org.cropster.roastingintelligence.AppModel;
import org.cropster.roastingintelligence.BetweenBatchModel;
import org.cropster.roastingintelligence.Defaults;
import org.cropster.roastingintelligence.RoastingIntelligenceApp;
import org.cropster.roastingintelligence.listener.MessageListener;
import org.cropster.roastingintelligence.listener.SettingsListener;
import org.cropster.roastingintelligence.measurement.MeasurementSourceFactory;
import org.cropster.roastingintelligence.model.Analysis;
import org.cropster.roastingintelligence.model.BetweenBatchLog;
import org.cropster.roastingintelligence.model.Channel;
import org.cropster.roastingintelligence.model.ControlState;
import org.cropster.roastingintelligence.model.Curve;
import org.cropster.roastingintelligence.model.CurveModel;
import org.cropster.roastingintelligence.model.Goal;
import org.cropster.roastingintelligence.model.Lot;
import org.cropster.roastingintelligence.model.Machine;
import org.cropster.roastingintelligence.model.Measure;
import org.cropster.roastingintelligence.model.MeasurementModel;
import org.cropster.roastingintelligence.model.Profile;
import org.cropster.roastingintelligence.model.Roast;
import org.cropster.roastingintelligence.model.RoastingComment;
import org.cropster.roastingintelligence.model.Scheduled;
import org.cropster.roastingintelligence.model.WorkerTask;
import org.cropster.roastingintelligence.model.impl.ConverterCurveModel;
import org.cropster.roastingintelligence.model.impl.LinearInterpolationCurveModel;
import org.cropster.roastingintelligence.model.impl.OffsetCurveModel;
import org.cropster.roastingintelligence.model.impl.RemoveDuplicatesCurveModel;
import org.cropster.roastingintelligence.model.impl.StandardCurveModel;
import org.cropster.roastingintelligence.model.impl.TailCurveModel;
import org.cropster.roastingintelligence.ui.chart.CurveChart;
import org.cropster.roastingintelligence.util.CollectionUtil;
import org.cropster.roastingintelligence.util.Measures;
import org.cropster.roastingintelligence.util.NanoTime;

public class MainPanelController implements Initializable, SettingsListener {
   public static final String ACTIVE_SLIDER_PROPERTY = "active-slider";
   private static final String WARNING_PLAYER_BAR_STYLE = "player-bar--warning";
   private static final double BLUR_SHADOW_DIAMETER = 6.0;
   private static final String SPLITTER_VALUES_TO_COMMENTS_ID = "splitterValuesToComments";
   @FXML
   private AnchorPane mainContainer;
   @FXML
   private BorderPane contentPanel;
   @FXML
   private BorderPane playerbarContainer;
   @FXML
   private BorderPane centerPanel;
   @FXML
   private VBox messagePanel;
   @FXML
   private VBox statusPanel;
   @FXML
   private GridPane bottomPanel;
   @FXML
   private AnchorPane sidebarMenu;
   @FXML
   private Button systemSidebarButton;
   @FXML
   private Label systemNorificationTag;
   @FXML
   private Button commentsSidebarButton;
   @FXML
   private SidebarButton productionSidebarButton;
   @FXML
   private SidebarButton tasksSidebarButton;
   @FXML
   private Label tasksTodoTag;
   @FXML
   private Button alarmsSidebarButton;
   @FXML
   private Label unacknowledgedTriggeredAlarmsTag;
   @FXML
   private SlidingPanel slidingPanel;
   @FXML
   private SystemSidebar systemSidebar;
   @FXML
   private ProductionSidebar productionPanel;
   @FXML
   private CommentsSidebar commentsSlidingPanel;
   @FXML
   private SupportSidebar supportSlidingPanel;
   @FXML
   private ControlToggleBar controlToggleBar;
   @FXML
   private VBox syncPanel;
   @FXML
   private TasksSidebar tasksSlidingPanel;
   @FXML
   private PreparationPanel preparationPanel;
   @FXML
   private AlarmsSidebar alarmsSlidingPanel;
   @FXML
   private SlidingPanel rightSlidingPanel;
   @FXML
   private MachineControlSidebar machineControlSlidingPanel;
   @FXML
   private Tab chartTab;
   @FXML
   private TitledPaneRight commentsPanel;
   @FXML
   private Button supportButton;
   @FXML
   private Button syncButton;
   @FXML
   private Button preferencesButton;
   @FXML
   private Button primaryButton;
   @FXML
   private ToggleSwitch replaySwitch;
   @FXML
   private StatusDot replayStatusDot;
   @FXML
   private Label replayStatusLabel;
   @FXML
   private HBox replayStatusContainer;
   @FXML
   private Button secondaryButton;
   @FXML
   private HBox switchProfilePanel;
   @FXML
   private Button switchProfileButton;
   @FXML
   private Label profileLabel;
   @FXML
   private TextFlow statusLabel;
   @FXML
   private HBox timerPanel;
   @FXML
   private TimerLabel betweenBatchTimerLabel;
   @FXML
   private TimerLabel timerLabel;
   @FXML
   private TimerLabel secondaryTimerLabel;
   @FXML
   private PercentageLabel percentageLabel;
   @FXML
   private SplitPane startPanel;
   @FXML
   private BorderPane leftStartPanel;
   @FXML
   private BorderPane rightStartPanel;
   @FXML
   private ScrollPane profilePanel;
   @FXML
   private StatisticsPanel statisticsPanel;
   @FXML
   private QualityPanel qualityPanel;
   @FXML
   private CommentsGrid commentsPreviewPanel;
   @FXML
   private BetweenBatchPanel betweenBatchPanel;
   @FXML
   private StackPane channelContainer;
   @FXML
   private SplitPane referencesToChannelsSplitter;
   @FXML
   private StackPane referencesContainer;
   @FXML
   private SplitPane valuesToCommentsSplitter;
   @FXML
   private BorderPane noMachineMessageContainer;
   @FXML
   private VBox channelGroupContainer;
   @FXML
   private Button selectMachineButton;
   @FXML
   private TitledPaneRight referencePanel;
   @FXML
   private EmptyStateGrid<LiveGoalService.ActiveGoal> activeGoalEmptyStateGrid;
   @FXML
   private HintsButton liveGoalHintsButton;
   @FXML
   private Button liveGoalLayoutButton;
   @FXML
   private Button liveGoalConfigurationButton;
   @FXML
   private VBox upcomingCommentsPanel;
   @FXML
   private GridPane controlPanel;
   @FXML
   private ControlSliderToggleBar sliderContainer;
   @FXML
   private ControlStateBar controlStateContainer;
   @FXML
   private ControlSlider gasControl;
   @FXML
   private ControlSlider airflowControl;
   @FXML
   private ControlSlider speedControl;
   @FXML
   private ControlSlider setTemperatureControl;
   @FXML
   private Separator machineControlSeparator;
   @FXML
   private SplitPane centerToRightSplitter;
   @FXML
   private Group scalableContent;
   @FXML
   private Label autoStartReadyIcon;
   private BetweenBatchChartWrapper betweenBatchChartWrapper = null;
   private PopOverHandler<CommentPopupContent> betweenBatchChartHandler = null;
   private RotateTransition syncAnimation;
   private MessageListener messageListener;
   private final BooleanProperty connectedProperty = new SimpleBooleanProperty();
   private final BooleanProperty connectedOverrideProperty = new SimpleBooleanProperty();
   private String statusText;
   private final ObservableList<Object> observableProductionList = FXCollections.observableArrayList();
   private final ObservableList<Lot> observableBlendList = FXCollections.observableArrayList();
   private final ObservableList<WorkerTask> observableTasksList = FXCollections.observableArrayList();
   private final Map<String, ChannelStatusLabel> channelStatusPanels = new LinkedHashMap<>();
   private double scalingResidualHeight = -1.0;
   private SimpleObjectProperty<Class<? extends MeasurementSourceFactory>> autoStartSourceFactory = new SimpleObjectProperty();
   private SimpleBooleanProperty autoStartReady = new SimpleBooleanProperty();
   private HandlerChainManager measurementHandlerManager;
   private ComponentManager componentManager;
   private final ChangeListener<Number> valuesToCommentsPositionListener = (s, o, n) -> {
      if (this.componentManager != null) {
         this.componentManager.store(this.valuesToCommentsSplitter, "splitterValuesToComments");
      }
   };

   public void initialize(URL location, ResourceBundle resources) {
      this.playerbarContainer.heightProperty().addListener((s, o, n) -> {
         if (n != null) {
            AnchorPane.setBottomAnchor(this.contentPanel, n.doubleValue());
         }
      });
      this.sidebarMenu.lookupAll(".button").forEach(b -> {
         Button button = (Button)b;
         if (button.getTooltip() == null) {
            Tooltip tooltip = new Tooltip();
            tooltip.textProperty().bind(button.textProperty());
            button.setTooltip(tooltip);
         }
      });
      this.messagePanel
         .paddingProperty()
         .bind(
            Bindings.createObjectBinding(
               () -> this.messagePanel.getChildren().stream().anyMatch(Node::isVisible) ? Styles.INSETS_SMALL : Styles.INSETS_EMPTY,
               new Observable[]{this.messagePanel.getChildren()}
            )
         );
      this.syncAnimation = new RotateTransition(Duration.millis(3500.0), this.syncButton.getGraphic());
      this.syncAnimation.setCycleCount(-1);
      this.syncAnimation.setByAngle(-350.0);
      this.productionPanel.setProductionViewItems(this.observableProductionList);
      this.productionPanel.setBlendViewItems(this.observableBlendList);
      this.tasksSlidingPanel.setTaskViewItems(this.observableTasksList);
      this.observableTasksList.addListener(c -> {
         long todoCount = this.observableTasksList.stream().filter(WorkerTask::isOpen).count();
         this.tasksTodoTag.setText(String.valueOf(todoCount));
         this.tasksTodoTag.setVisible(todoCount > 0L);
      });
      this.primaryButton.managedProperty().bind(this.primaryButton.visibleProperty());
      this.secondaryButton.managedProperty().bind(this.secondaryButton.visibleProperty());
      this.slidingPanel
         .setShrinkingPane(
            this.centerPanel,
            () -> this.centerPanel.getCenter() == this.startPanel ? SlidingAnimation.SlidingMode.SLIDE_AND_PUSH : SlidingAnimation.SlidingMode.SLIDE_OVER
         );
      this.rightSlidingPanel
         .setShrinkingPane(this.centerToRightSplitter, SlidingAnimation.SlidingDirection.RTL, () -> SlidingAnimation.SlidingMode.SLIDE_AND_PUSH);
      this.machineControlSeparator.visibleProperty().bind(this.sliderContainer.visibleProperty().and(this.controlStateContainer.visibleProperty()));
      this.controlToggleBar.setOnPreSliding(() -> {
         ObservableMap<Object, Object> properties = this.referencesToChannelsSplitter.getProperties();
         properties.put("minWidth", this.referencesToChannelsSplitter.getMinWidth());
         properties.put("maxWidth", this.referencesToChannelsSplitter.getMaxWidth());
         this.referencesToChannelsSplitter.setMinWidth(this.referencesToChannelsSplitter.getWidth());
         this.referencesToChannelsSplitter.setMaxWidth(this.referencesToChannelsSplitter.getWidth());
      });
      this.controlToggleBar.setOnPostSliding(() -> {
         ObservableMap<Object, Object> properties = this.referencesToChannelsSplitter.getProperties();
         this.referencesToChannelsSplitter.setMinWidth((Double)properties.get("minWidth"));
         this.referencesToChannelsSplitter.setMaxWidth((Double)properties.get("maxWidth"));
      });
      this.controlToggleBar.init(this.rightSlidingPanel, this.machineControlSlidingPanel, this.sliderContainer, this.controlStateContainer);
      this.switchProfileButton.setGraphic(Icon.EDIT.load());
      this.replaySwitch.managedProperty().bind(this.replaySwitch.visibleProperty());
      this.replaySwitch
         .textProperty()
         .bind(
            Bindings.createStringBinding(
               () -> this.replaySwitch.isOn()
                     ? org.cropster.roastingintelligence.util.Messages.m_("Replay on")
                     : org.cropster.roastingintelligence.util.Messages.m_("Replay off"),
               new Observable[]{this.replaySwitch.onProperty()}
            )
         );
      this.replayStatusContainer.visibleProperty().bind(this.replaySwitch.visibleProperty());
      this.replayStatusDot
         .statusProperty()
         .bind(
            Bindings.createObjectBinding(
               () -> this.replaySwitch.isOn() ? StatusDot.Status.ON : StatusDot.Status.OFF, new Observable[]{this.replaySwitch.onProperty()}
            )
         );
      this.replayStatusLabel
         .textProperty()
         .bind(
            Bindings.createStringBinding(
               () -> this.replaySwitch.isOn()
                     ? org.cropster.roastingintelligence.util.Messages.m_("Replay on")
                     : org.cropster.roastingintelligence.util.Messages.m_("Replay off"),
               new Observable[]{this.replaySwitch.onProperty()}
            )
         );
      this.timerPanel.managedProperty().bind(this.timerPanel.visibleProperty());
      this.timerLabel.managedProperty().bind(this.timerLabel.visibleProperty());
      this.secondaryTimerLabel.managedProperty().bind(this.secondaryTimerLabel.visibleProperty());
      this.percentageLabel.managedProperty().bind(this.percentageLabel.visibleProperty());
      this.upcomingCommentsPanel.managedProperty().bind(this.upcomingCommentsPanel.visibleProperty());
      ChangeListener<Boolean> updateStructureListener = (s, o, n) -> Platform.runLater(this::updateReferencesContainer);
      this.referencePanel.visibleProperty().addListener(updateStructureListener);
      this.commentsPanel.visibleProperty().addListener(updateStructureListener);
      this.updateReferencesContainer();
      JavaFxUtil.configureTooltips(this.sidebarMenu);
      JavaFxUtil.configureTooltips(this.slidingPanel);
      JavaFxUtil.configureTooltips(this.rightSlidingPanel);
      this.channelContainer.heightProperty().addListener((s, o, n) -> {
         if (this.channelGroupContainer.isVisible()) {
            Platform.runLater(this::handleChannelsStatusScaling);
         }
      });
      this.channelContainer
         .maxHeightProperty()
         .bind(
            Bindings.createDoubleBinding(
               () -> {
                  if (this.channelGroupContainer.isVisible()) {
                     double snappedVerticalInsets = this.channelGroupContainer.snappedTopInset() + this.channelGroupContainer.snappedBottomInset();
                     double replayStatusHeight = this.replayStatusContainer.isVisible()
                        ? this.channelGroupContainer.getSpacing() + this.replayStatusContainer.getHeight()
                        : 0.0;
                     return this.statusPanel.getHeight() + snappedVerticalInsets + replayStatusHeight - 6.0;
                  } else {
                     return this.noMachineMessageContainer.getHeight();
                  }
               },
               new Observable[]{
                  this.statusPanel.heightProperty(), this.noMachineMessageContainer.heightProperty(), this.channelGroupContainer.visibleProperty()
               }
            )
         );
      this.channelContainer
         .minHeightProperty()
         .bind(
            Bindings.createDoubleBinding(
               () -> this.channelGroupContainer.isVisible()
                     ? Math.max(this.channelGroupContainer.getMinHeight() * 0.75, 80.0)
                     : this.noMachineMessageContainer.getHeight(),
               new Observable[]{
                  this.noMachineMessageContainer.heightProperty(), this.channelGroupContainer.minHeightProperty(), this.channelGroupContainer.visibleProperty()
               }
            )
         );
      this.channelGroupContainer.visibleProperty().addListener((s, o, n) -> {
         if (n) {
            Platform.runLater(this::handleChannelsStatusScaling);
         }
      });
      this.statusPanel.heightProperty().addListener((s, o, n) -> {
         if (this.replayStatusContainer.isVisible()) {
            double h = this.statusPanel.getHeight() + 10.0 + this.replayStatusContainer.getHeight();
            this.channelGroupContainer.setMinHeight(h < 240.0 ? h : 240.0);
         } else {
            this.channelGroupContainer.setMinHeight(this.statusPanel.getHeight() < 240.0 ? this.statusPanel.getHeight() : 240.0);
         }
      });
      JavaFxUtil.addDividerDoubleClickHandler(
         this.referencesToChannelsSplitter, 0, () -> ((Divider)this.referencesToChannelsSplitter.getDividers().get(0)).setPosition(0.0)
      );
      JavaFxUtil.addDividerDoubleClickHandler(this.centerToRightSplitter, 0, () -> ((Divider)this.centerToRightSplitter.getDividers().get(0)).setPosition(1.0));
      JavaFxUtil.addDividerDoubleClickHandler(this.startPanel, 0, () -> ((Divider)this.startPanel.getDividers().get(0)).setPosition(0.45));
      this.betweenBatchTimerLabel.opacityProperty().bind(this.betweenBatchPanel.chartOpacityProperty());
      JavaFxUtil.setDefaultLineSpacing(this.mainContainer);
      this.autoStartReady.addListener((s, o, n) -> this.updateAutoStartStyle());
      this.autoStartSourceFactory.addListener((s, o, n) -> this.updateAutoStartStyle());
      this.autoStartReadyIcon
         .graphicProperty()
         .bind(
            Bindings.createObjectBinding(
               () -> this.getAutoStartReady() ? Icon.SUCCESS.load("icon--success") : Icon.WARNING.load("icon--warning"), new Observable[]{this.autoStartReady}
            )
         );
      this.leftStartPanel
         .sceneProperty()
         .addListener(
            (s, o, n) -> {
               if (n != null && this.leftStartPanel.getCenter() != null && this.betweenBatchPanel.getChartViewer() != null) {
                  n.getAccelerators()
                     .put(KeyboardShortcut.CTRL_R.getKeyCodeCombination(), (Runnable)() -> ChartUtils.restoreZoom(this.betweenBatchPanel.getChartViewer()));
               }
            }
         );
      if (this.componentManager != null) {
         this.componentManager.restore(this.valuesToCommentsSplitter, "splitterValuesToComments", 0.5);
         GridView<LiveGoalService.ActiveGoal> gridView = this.activeGoalEmptyStateGrid.getGridView();
         gridView.columnsProperty().addListener((s, o, n) -> {
            if (n != null) {
               this.componentManager.store(gridView.columnsProperty(), "activeGoalGridColumns");
            }
         });
      }
   }

   private void updateReferencesContainer() {
      if (this.componentManager != null && !this.valuesToCommentsSplitter.getDividers().isEmpty()) {
         ((Divider)this.valuesToCommentsSplitter.getDividers().get(0)).positionProperty().removeListener(this.valuesToCommentsPositionListener);
      }

      try {
         this.performUpdateReferencesContainer();
      } finally {
         if (this.componentManager != null && !this.valuesToCommentsSplitter.getDividers().isEmpty()) {
            this.componentManager.restore(this.valuesToCommentsSplitter, "splitterValuesToComments", 0.5);
            ((Divider)this.valuesToCommentsSplitter.getDividers().get(0)).positionProperty().addListener(this.valuesToCommentsPositionListener);
         }
      }
   }

   private void performUpdateReferencesContainer() {
      ObservableList<Node> containerChildren = this.referencesContainer.getChildren();
      ObservableList<Node> splitterItems = this.valuesToCommentsSplitter.getItems();
      boolean valuesVisible = this.referencePanel.isVisible();
      boolean commentsVisible = this.commentsPanel.isVisible();
      if (valuesVisible && commentsVisible) {
         if (!containerChildren.contains(this.valuesToCommentsSplitter)) {
            containerChildren.clear();
            splitterItems.clear();
            splitterItems.addAll(new Node[]{this.referencePanel, this.commentsPanel});
            containerChildren.add(this.valuesToCommentsSplitter);
         }
      } else if (valuesVisible) {
         containerChildren.clear();
         splitterItems.clear();
         containerChildren.add(this.referencePanel);
      } else if (commentsVisible) {
         containerChildren.clear();
         splitterItems.clear();
         containerChildren.add(this.commentsPanel);
      } else {
         containerChildren.clear();
      }
   }

   private void handleChannelsStatusScaling() {
      double snappedVerticalInsets = this.channelGroupContainer.snappedTopInset() + this.channelGroupContainer.snappedBottomInset();
      double replayStatusHeight = this.replayStatusContainer.isVisible()
         ? this.channelGroupContainer.getSpacing() + this.replayStatusContainer.getHeight()
         : 0.0;
      double newGroupHeight = this.channelContainer.getHeight() - snappedVerticalInsets - replayStatusHeight - 6.0;
      double scale = Math.min(1.0, newGroupHeight / this.statusPanel.getHeight());
      this.statusPanel.setScaleX(scale);
      this.statusPanel.setScaleY(scale);
   }

   public RadioToggleButton getToggleButtonAlarmsManage() {
      return this.alarmsSlidingPanel.getToggleButtonAlarmsManage();
   }

   public RadioToggleButton getToggleButtonAlarmsLog() {
      return this.alarmsSlidingPanel.getToggleButtonAlarmsLog();
   }

   public BorderPane getContentPanel() {
      return this.contentPanel;
   }

   public GridPane getControlPanel() {
      return this.controlPanel;
   }

   public BorderPane getCenterPanel() {
      return this.centerPanel;
   }

   public VBox getMessagePanel() {
      return this.messagePanel;
   }

   public VBox getChannelStatusPanel() {
      return this.statusPanel;
   }

   public VBox getChannelGroupContainer() {
      return this.channelGroupContainer;
   }

   public SplitPane getStartPanel() {
      return this.startPanel;
   }

   public BetweenBatchPanel getBetweenBatchPanel() {
      return this.betweenBatchPanel;
   }

   public Button getSupportButton() {
      return this.supportButton;
   }

   public Button getSyncButton() {
      return this.syncButton;
   }

   public Button getPreferencesButton() {
      return this.preferencesButton;
   }

   public Button getPrimaryButton() {
      return this.primaryButton;
   }

   public Button getSecondaryButton() {
      return this.secondaryButton;
   }

   public Button getCommentsSidebarButton() {
      return this.commentsSidebarButton;
   }

   public CurveChart getBetweenBatchChart() {
      return this.betweenBatchChartWrapper != null ? this.betweenBatchChartWrapper.getChart() : null;
   }

   public SystemSidebar getSystemSidebar() {
      return this.systemSidebar;
   }

   public Button getSystemSidebarButton() {
      return this.systemSidebarButton;
   }

   public Label getSystemNorificationTag() {
      return this.systemNorificationTag;
   }

   public SlidingPanel getSlidingPanel() {
      return this.slidingPanel;
   }

   public ProductionSidebar getProductionPanel() {
      return this.productionPanel;
   }

   public CommentsSidebar getCommentsSlidingPanel() {
      return this.commentsSlidingPanel;
   }

   public AlarmsSidebar getAlarmsSlidingPanel() {
      return this.alarmsSlidingPanel;
   }

   public SupportSidebar getSupportSlidingPanel() {
      return this.supportSlidingPanel;
   }

   public ListView<Object> getProductionList() {
      return this.productionPanel.getProductionView();
   }

   public ListView<Lot> getBlendList() {
      return this.productionPanel.getBlendView();
   }

   public TasksSidebar getTasksSidebar() {
      return this.tasksSlidingPanel;
   }

   public ObservableList<WorkerTask> getTasksList() {
      return this.observableTasksList;
   }

   public SplitPane getCenterToRightSplitter() {
      return this.centerToRightSplitter;
   }

   public SplitPane getReferencesToChannelsSplitter() {
      return this.referencesToChannelsSplitter;
   }

   public SplitPane getValuesToCommentsSplitter() {
      return this.valuesToCommentsSplitter;
   }

   public TitledPane getReferencePanel() {
      return this.referencePanel;
   }

   public EmptyStateGrid<LiveGoalService.ActiveGoal> getActiveGoalEmptyStateGrid() {
      return this.activeGoalEmptyStateGrid;
   }

   public TitledPane getCommentsPanel() {
      return this.commentsPanel;
   }

   public HintsButton getLiveGoalHintsButton() {
      return this.liveGoalHintsButton;
   }

   public Button getLiveGoalLayoutButton() {
      return this.liveGoalLayoutButton;
   }

   public Button getLiveGoalConfigurationButton() {
      return this.liveGoalConfigurationButton;
   }

   public VBox getUpcomingCommentsPanel() {
      return this.upcomingCommentsPanel;
   }

   public Animation getSyncAnimation() {
      return this.syncAnimation;
   }

   public HBox getTimerPanel() {
      return this.timerPanel;
   }

   public Label getUnacknowledgedTriggeredAlarmsTag() {
      return this.unacknowledgedTriggeredAlarmsTag;
   }

   public Button getAlarmsSidebarButton() {
      return this.alarmsSidebarButton;
   }

   public SidebarButton getTasksSidebarButton() {
      return this.tasksSidebarButton;
   }

   public Button getSelectMachineButton() {
      return this.selectMachineButton;
   }

   public void showBetweenBatchTimer(long initialTime) {
      Duration duration = Duration.millis(500.0);
      ScaleTransition shrinkTransition = new ScaleTransition(duration, this.timerPanel);
      shrinkTransition.setToX(0.8);
      shrinkTransition.setToY(0.8);
      this.betweenBatchTimerLabel.setTime(initialTime);
      this.betweenBatchTimerLabel.setManaged(true);
      this.timerPanel.setTranslateX(120.0);
      TranslateTransition shiftTransition = new TranslateTransition(duration, this.timerPanel);
      shiftTransition.setToX(0.1 * this.timerPanel.getWidth());
      FadeTransition fadeTransition = new FadeTransition(duration, this.timerLabel);
      fadeTransition.setToValue(0.5);
      ParallelTransition parallelTransition = new ParallelTransition(new Animation[]{shrinkTransition, shiftTransition, fadeTransition});
      parallelTransition.setOnFinished(e -> this.betweenBatchTimerLabel.setVisible(true));
      parallelTransition.play();
   }

   public void hideBetweenBatchTimer() {
      this.betweenBatchTimerLabel.setVisible(false);
      this.betweenBatchTimerLabel.setManaged(false);
      this.betweenBatchTimerLabel.setTime(0L);
      this.timerPanel.setScaleX(1.0);
      this.timerPanel.setScaleY(1.0);
      this.timerPanel.setTranslateX(0.0);
      this.timerLabel.setOpacity(1.0);
   }

   public TimerLabel getBetweenBatchTimerLabel() {
      return this.betweenBatchTimerLabel;
   }

   public TimerLabel getTimerLabel() {
      return this.timerLabel;
   }

   public TimerLabel getSecondaryTimerLabel() {
      return this.secondaryTimerLabel;
   }

   public PercentageLabel getPercentageLabel() {
      return this.percentageLabel;
   }

   public HBox getSwitchProfilePanel() {
      return this.switchProfilePanel;
   }

   public Button getSwitchProfileButton() {
      return this.switchProfileButton;
   }

   public Label getProfileLabel() {
      return this.profileLabel;
   }

   public TextFlow getStatusLabel() {
      return this.statusLabel;
   }

   public ToggleSwitch getReplaySwitch() {
      return this.replaySwitch;
   }

   public ControlSlider getGasControl() {
      return this.gasControl;
   }

   public ControlSlider getAirflowControl() {
      return this.airflowControl;
   }

   public ControlSlider getSpeedControl() {
      return this.speedControl;
   }

   public ControlSliderToggleBar getSliderContainer() {
      return this.sliderContainer;
   }

   public ControlStateBar getControlStateContainer() {
      return this.controlStateContainer;
   }

   public ControlSlider getSetTemperatureControl() {
      return this.setTemperatureControl;
   }

   public PreparationPanel getPreparationPanel() {
      return this.preparationPanel;
   }

   public void setMessageListener(MessageListener messageListener) {
      this.messageListener = messageListener;
   }

   public ControlToggleBar getControlToggleBar() {
      return this.controlToggleBar;
   }

   public void setConnected(String text) {
      this.connectedProperty.set(true);
      RoastingIntelligenceApp.retrieveSingleton(NotificationBusService.class)
         .publish(Notification.of(NotificationType.SUCCESS, text), "system-status-connection");
   }

   public void setDisconnected(String text) {
      this.connectedProperty.set(false);
      RoastingIntelligenceApp.retrieveSingleton(NotificationBusService.class)
         .publish(Notification.of(NotificationType.ERROR, text), "system-status-connection");
   }

   public boolean isConnectedOveride() {
      return this.connectedOverrideProperty.get();
   }

   public void setConnectedOveride(boolean connectedOveride) {
      this.connectedOverrideProperty.set(connectedOveride);
   }

   public Label getAutoStartReadyIcon() {
      return this.autoStartReadyIcon;
   }

   public SimpleBooleanProperty autoStartReadyProperty() {
      return this.autoStartReady;
   }

   public boolean getAutoStartReady() {
      return this.autoStartReady.get();
   }

   public void setAutoStartReady(boolean autoStartReady) {
      this.autoStartReady.set(autoStartReady);
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

   public Map<String, ChannelStatusLabel> getChannelStatusPanels() {
      return this.channelStatusPanels;
   }

   public ControlSlider getControlSlider(String controlMeasurement) {
      switch (controlMeasurement) {
         case "airflowControl":
         case "drumPressureControl":
         case "fanSpeedControl":
            return this.getAirflowControl();
         case "drumSpeedControl":
            return this.getSpeedControl();
         case "setTemperatureControl":
            return this.getSetTemperatureControl();
         default:
            return this.getGasControl();
      }
   }

   public HandlerChainManager getMeasurementHandlerManager() {
      return this.measurementHandlerManager;
   }

   public void setMeasurementHandlerManager(HandlerChainManager measurementHandlerManager) {
      this.measurementHandlerManager = measurementHandlerManager;
   }

   public void setComponentManager(ComponentManager componentManager) {
      this.componentManager = componentManager;
      if (componentManager != null && !this.valuesToCommentsSplitter.getDividers().isEmpty()) {
         componentManager.restore(this.valuesToCommentsSplitter, "splitterValuesToComments", 0.5);
         ((Divider)this.valuesToCommentsSplitter.getDividers().get(0)).positionProperty().addListener(this.valuesToCommentsPositionListener);
      }

      if (componentManager != null) {
         componentManager.restore(this.valuesToCommentsSplitter, "splitterValuesToComments", 0.5);
      }

      if (componentManager != null) {
         componentManager.restore(this.activeGoalEmptyStateGrid.getGridView().columnsProperty(), "activeGoalGridColumns", 2);
      }
   }

   public void populateProductionPanel(Collection<Scheduled> scheduledRoasts, JavaFxApplicationState state) {
      updateProductionPanelAndRestoreSelection(
         this.productionPanel.getProductionView(), () -> replaceScheduledItems(this.observableProductionList, scheduledRoasts, state)
      );
   }

   public void populateProductionPanel(Collection<Roast> roasts) {
      updateProductionPanelAndRestoreSelection(this.productionPanel.getProductionView(), () -> replaceRoastItems(this.observableProductionList, roasts));
   }

   static void updateProductionPanelAndRestoreSelection(ListView<Object> productionView, Runnable updateAction) {
      MultipleSelectionModel<Object> selectionModel = productionView.getSelectionModel();
      Set<MainPanelController.SelectionSnapshot> selectedSnapshots = snapshotSelection(selectionModel.getSelectedItems());
      updateAction.run();
      restoreSelection(selectionModel, productionView.getItems(), selectedSnapshots);
   }

   private static Set<MainPanelController.SelectionSnapshot> snapshotSelection(Collection<Object> selectedItems) {
      Set<MainPanelController.SelectionSnapshot> selectedSnapshots = new LinkedHashSet<>();
      selectedItems.forEach(item -> selectedSnapshots.add(MainPanelController.SelectionSnapshot.of(item)));
      return selectedSnapshots;
   }

   private static void restoreSelection(
      MultipleSelectionModel<Object> selectionModel, ObservableList<Object> currentItems, Set<MainPanelController.SelectionSnapshot> selectedSnapshots
   ) {
      if (!selectedSnapshots.isEmpty()) {
         selectionModel.clearSelection();

         for (int i = 0; i < currentItems.size(); i++) {
            if (selectedSnapshots.contains(MainPanelController.SelectionSnapshot.of(currentItems.get(i)))) {
               selectionModel.select(i);
            }
         }
      }
   }

   static void replaceScheduledItems(ObservableList<Object> productionList, Collection<Scheduled> scheduledRoasts, JavaFxApplicationState state) {
      List<Object> updatedItems = new ArrayList<>(productionList);
      updatedItems.removeIf(Scheduled.class::isInstance);
      scheduledRoasts.stream().filter(e -> !e.isDone() || e.getId().equals(state.getScheduled())).forEach(updatedItems::add);
      productionList.setAll(updatedItems);
   }

   static void replaceRoastItems(ObservableList<Object> productionList, Collection<Roast> roasts) {
      Instant midnight = LocalDate.now().atStartOfDay(ZoneOffset.systemDefault()).toInstant();
      List<Object> updatedItems = new ArrayList<>(productionList);
      updatedItems.removeIf(Roast.class::isInstance);
      roasts.stream()
         .filter(e -> !e.getStartDate().toInstant().isBefore(midnight) || !e.isSynced())
         .sorted(Collections.reverseOrder(new Roast.DateComparator()))
         .forEach(e -> updatedItems.add(0, e));
      productionList.setAll(updatedItems);
   }

   public void populateTasksPanel(Collection<WorkerTask> tasks) {
      this.observableTasksList.setAll(tasks);
   }

   public void populateStartPanel(AppModel appModel, Runnable showMachinePreferences, ReadOnlyBooleanProperty riPermissionGranted) {
      this.preparationPanel.autoStartSourceFactoryProperty().bind(this.autoStartSourceFactory);
      this.preparationPanel.init(appModel);
      ObservableValue<? extends String> preparationValidationMessage = this.preparationPanel.createValidationMessageBinding();
      Collection<Observable> dependencies = new ArrayList<>();
      dependencies.add(preparationValidationMessage);
      dependencies.add(this.autoStartReady);
      dependencies.add(this.autoStartSourceFactory);
      dependencies.add(this.connectedProperty);
      dependencies.add(this.connectedOverrideProperty);
      ObservableValue<? extends String> validationMessage = Bindings.createStringBinding(
         () -> {
            Machine machine = appModel.getSelectedMachine();
            boolean deletedMachine = machine != null && !appModel.getData().getMachines().containsKey(machine.getId());
            boolean noMachine = machine == null;
            if (!deletedMachine && !noMachine) {
               Class<? extends MeasurementSourceFactory> sourceFactory = this.getAutoStartSourceFactory();
               if (sourceFactory == null) {
                  return ((String)preparationValidationMessage.getValue()).isEmpty() && !this.connectedProperty.get() && !this.connectedOverrideProperty.get()
                     ? org.cropster.roastingintelligence.util.Messages.m_("MachineNotConnected")
                     : (String)preparationValidationMessage.getValue();
               } else if (!((String)preparationValidationMessage.getValue()).isEmpty()) {
                  return org.cropster.roastingintelligence.util.Messages.m_("AutoStartNotReadyMessage")
                     + ": "
                     + (String)preparationValidationMessage.getValue();
               } else if (!this.connectedProperty.get() && !this.connectedOverrideProperty.get()) {
                  return org.cropster.roastingintelligence.util.Messages.m_("MachineNotConnected");
               } else if (!this.getAutoStartReady()) {
                  return org.cropster.roastingintelligence.util.Messages.m_("AutoStartNotReadyMessage");
               } else {
                  String autoStartReadyMessage = org.cropster.roastingintelligence.util.Messages.m_("AutoStartReadyMessage", sourceFactory);
                  return autoStartReadyMessage.equals(org.cropster.roastingintelligence.util.Messages.m_("AutoStartReadyMessage"))
                     ? org.cropster.roastingintelligence.util.Messages.m_("AutoStartReadyMessage")
                     : org.cropster.roastingintelligence.util.Messages.m_("AutoStartReadyMessage") + ": " + autoStartReadyMessage;
               }
            } else {
               return org.cropster.roastingintelligence.util.Messages.m_("NoMachineAccess");
            }
         },
         dependencies.toArray(new Observable[0])
      );
      notifyIfDeletedMachine(appModel, showMachinePreferences);
      Text text = new Text();
      text.getStyleClass().add("heading");
      text.textProperty().bind(validationMessage);
      this.statusLabel.getChildren().clear();
      this.statusLabel.getChildren().add(text);
      this.primaryButton
         .disableProperty()
         .bind(
            Bindings.and(this.connectedProperty.not(), this.connectedOverrideProperty.not())
               .or(this.preparationPanel.createValidationBinding(appModel))
               .or(riPermissionGranted.not())
         );
      this.secondaryButton
         .disableProperty()
         .bind(Bindings.and(this.connectedProperty.not(), this.connectedOverrideProperty.not()).or(this.preparationPanel.createValidationBinding(appModel)));
      this.autoStartReady
         .bind(
            this.autoStartSourceFactory
               .isNotNull()
               .and(Bindings.or(this.connectedProperty, this.connectedOverrideProperty))
               .and(this.preparationPanel.createValidationBinding(appModel).not())
         );
      this.preparationPanel.requestFocus();
   }

   private static void notifyIfDeletedMachine(AppModel appModel, Runnable showMachinePreferences) {
      boolean toastAlreadyOpen = false;
      ToastNotificationController tnc = RoastingIntelligenceApp.retrieveSingleton(ToastNotificationController.class);
      if (tnc != null) {
         toastAlreadyOpen = tnc.isOpen("system-status-machine-deleted-tag");
      }

      if (!toastAlreadyOpen) {
         Machine machine = appModel.getSelectedMachine();
         String mName = "<b>" + (machine == null ? "–" : machine.getName()) + "</b>";
         boolean deletedMachine = machine != null && !appModel.getData().getMachines().containsKey(machine.getId());
         if (deletedMachine) {
            NotificationBusService nbs = RoastingIntelligenceApp.retrieveSingleton(NotificationBusService.class);
            if (nbs != null) {
               nbs.publish(
                  Notification.builder(NotificationType.WARNING)
                     .withTitle(org.cropster.roastingintelligence.util.Messages.m_("Machine deleted"))
                     .withMessage(org.cropster.roastingintelligence.util.Messages.m_("The following machine was deleted from the web platform: {0}", mName))
                     .withProperty("tag", "system-status-machine-deleted-tag")
                     .withProperty(
                        "action",
                        new ActionBuilder(org.cropster.roastingintelligence.util.Messages.m_("Select another machine"), showMachinePreferences).build()
                     )
                     .build(),
                  "system-message"
               );
            }
         }
      }
   }

   public void populateStatusPanel(AppModel appModel) {
      this.channelStatusPanels.clear();
      this.statusPanel.getChildren().clear();
      boolean condensedChannelStatus = appModel.getPreferences().isCondensedChannelStatus();
      this.statusPanel.setSpacing(condensedChannelStatus ? 8.0 : 16.0);
      this.channelGroupContainer.setSpacing(condensedChannelStatus ? 8.0 : 16.0);
      int items = 0;
      ChannelStatusLabel firstOfAPair = null;

      for (Channel channel : appModel.getVisibleChannels(Channel.Visibility.STATUS)) {
         Color javaFxColor = Color.rgb(channel.getColor().getRed(), channel.getColor().getGreen(), channel.getColor().getBlue());
         ChannelStatusLabel channelStatusPanel = new ChannelStatusLabel(condensedChannelStatus);
         channelStatusPanel.setValueColor(javaFxColor);
         channelStatusPanel.setPrimaryValue(channel.isPrimary());
         channelStatusPanel.setRorVisible(channel.isRorEnabled() && channel.isRorSupported());
         if (!"custom".equals(channel.getName()) && !"customControl".equals(channel.getName())) {
            channelStatusPanel.setName(org.cropster.roastingintelligence.util.Messages.m_(channel.getName()));
         } else {
            MeasurementSourceFactory sourceFactory = RoastingIntelligenceApp.retrieveSingleton(channel.getSourceFactory());
            channelStatusPanel.setName(
               org.cropster.roastingintelligence.util.Messages.m_(channel.getName(), (Class<? extends MeasurementSourceFactory>)sourceFactory.getClass())
            );
         }

         if (channel.isRorEnabled() && channel.isRorSupported()) {
            channelStatusPanel.setRorColor(javaFxColor);
         }

         if ((!channel.isRorEnabled() || !channel.isRorSupported()) && !channel.isPrimary()) {
            if (firstOfAPair == null) {
               firstOfAPair = channelStatusPanel;
            } else {
               this.addChannelSeparator(items++);
               this.statusPanel.getChildren().add(this.createChannelsGrid(firstOfAPair, channelStatusPanel));
               firstOfAPair = null;
            }
         } else {
            this.addChannelSeparator(items++);
            if (firstOfAPair != null) {
               this.statusPanel.getChildren().add(firstOfAPair);
               this.addChannelSeparator(items++);
               firstOfAPair = null;
            }

            this.statusPanel.getChildren().add(channelStatusPanel);
         }

         this.channelStatusPanels.put(channel.getName(), channelStatusPanel);
      }

      if (firstOfAPair != null) {
         this.addChannelSeparator(items);
         this.statusPanel.getChildren().add(firstOfAPair);
      }

      boolean permissionGranted = PermissionNotificationController.isRiPermissionGrantedForCurrentMachine(appModel);
      boolean showStatusPanel = appModel.getSelectedMachine() != null
         && appModel.getSelectedMachine().getChannels() != null
         && appModel.getSelectedMachine().getChannels().length > 0
         && !this.statusPanel.getChildren().isEmpty()
         && permissionGranted;
      this.channelGroupContainer.setVisible(showStatusPanel);
      this.selectMachineButton
         .setText(
            appModel.getSelectedMachine() != null && permissionGranted
               ? org.cropster.roastingintelligence.util.Messages.m_("Select measurements")
               : org.cropster.roastingintelligence.util.Messages.m_("Select machine")
         );
      String propertyKey = "condensed-channel-status";
      ObservableMap<Object, Object> properties = this.referencesToChannelsSplitter.getProperties();
      if (!properties.containsKey(propertyKey)) {
         properties.put(propertyKey, condensedChannelStatus);
      } else if ((Boolean)properties.get(propertyKey) != condensedChannelStatus) {
         Platform.runLater(() -> ((Divider)this.referencesToChannelsSplitter.getDividers().get(0)).setPosition(0.0));
         properties.put(propertyKey, condensedChannelStatus);
      }
   }

   public void updateStatusPanels(MeasurementModel model) {
      for (Channel channel : model.getChannels()) {
         CurveModel curveModel = model.getCurveModel(channel);
         Long referenceTime = NanoTime.currentTimeMillis() - curveModel.getZeroPoint().getTime();
         MeasurementSourceFactory sourceFactory = RoastingIntelligenceApp.retrieveSingleton(channel.getSourceFactory());
         Long proximateKey = CollectionUtil.getProximateKey(curveModel.getValues(), referenceTime, (long)(2 * sourceFactory.getSamplingInterval()));
         Measure measure = null;
         if (proximateKey != null && curveModel.getValue(proximateKey) != null) {
            Double amount = curveModel.getValue(proximateKey);
            measure = new Measure(amount, curveModel.getUnit());
         }

         Measure rorMeasure = null;
         if (measure != null && channel.isRorEnabled() && channel.isRorSupported()) {
            CurveModel rorModel = model.getRorModel(channel);
            proximateKey = Optional.ofNullable(rorModel.getModel(RemoveDuplicatesCurveModel.class))
               .map(RemoveDuplicatesCurveModel::getLastStepTime)
               .orElse(proximateKey);
            if (rorModel.getValue(proximateKey) != null) {
               Double rorAmount = rorModel.getValue(proximateKey);
               rorMeasure = new Measure(rorAmount, rorModel.getUnit());
            }
         }

         CombinedMeasure combinedMeasure = new CombinedMeasure(measure, rorMeasure);
         this.measurementHandlerManager.propagateValues(channel, combinedMeasure);
      }

      for (ControlState controlState : model.getControlStates()) {
         this.measurementHandlerManager.propagateValues(controlState, model.getState(controlState));
      }
   }

   private void addChannelSeparator(int itmesCount) {
      if (itmesCount > 0) {
         Separator separator = new Separator(Orientation.HORIZONTAL);
         separator.getStyleClass().add("temperature-box-separator");
         this.statusPanel.getChildren().add(separator);
      }
   }

   private GridPane createChannelsGrid(ChannelStatusLabel firstChannel, ChannelStatusLabel secondChannel) {
      firstChannel.makeValueSpanColumns();
      secondChannel.makeValueSpanColumns();
      GridPane gridpane = new GridPane();
      gridpane.setHgap(16.0);
      gridpane.setVgap(0.0);
      gridpane.setPadding(Styles.INSETS_EMPTY);
      ColumnConstraints col0 = new ColumnConstraints(-1.0, -1.0, -1.0, Priority.ALWAYS, HPos.RIGHT, true);
      col0.setPercentWidth(50.0);
      ColumnConstraints col1 = new ColumnConstraints(-1.0, -1.0, -1.0, Priority.ALWAYS, HPos.RIGHT, true);
      col1.setPercentWidth(50.0);
      gridpane.getColumnConstraints().addAll(new ColumnConstraints[]{col0, col1});
      RowConstraints row = new RowConstraints(-1.0, -1.0, -1.0, Priority.NEVER, VPos.CENTER, true);
      gridpane.getRowConstraints().add(row);
      gridpane.setPrefWidth(firstChannel.getPrefWidth());
      firstChannel.setPrefWidth(-1.0);
      secondChannel.setPrefWidth(-1.0);
      gridpane.add(firstChannel, 0, 0);
      gridpane.add(secondChannel, 1, 0);
      return gridpane;
   }

   public void setBetweenBatchChartHandler(PopOverHandler<CommentPopupContent> betweenBatchChartHandler) {
      this.betweenBatchChartHandler = betweenBatchChartHandler;
   }

   public void setStartPanelReference(Profile profile, AppModel appModel) {
      if (profile != null) {
         this.rightStartPanel.setTop(null);
         this.leftStartPanel.setCenter(null);
         Unit tempUnit = appModel.getPreferences().getPreferredUnit(Unit.CELSIUS);
         Roast roast = appModel.getCurrentReference();
         Queue<Goal> goals = new LinkedList<>();
         if (appModel.getData().getGoals().containsKey(profile.getId())) {
            goals.addAll(appModel.getData().getGoals().get(profile.getId()));
         }

         BetweenBatchLog betweenBatchLog = null;
         BetweenBatchModel betweenBatchModel = appModel.getBetweenBatchModel();
         this.statisticsPanel.reset(tempUnit);
         if (roast != null) {
            betweenBatchLog = appModel.getData().getBetweenBatchLogs().get(roast.getLot().getId());
            this.statisticsPanel.init(roast);
            this.statisticsPanel.setNotes(roast.getNotes());
            this.statisticsPanel.setProfileNotes(profile.getNotes());
            this.statisticsPanel
               .addStatisticMeasure(
                  "chargeTemperature",
                  roast,
                  goals,
                  org.cropster.roastingintelligence.util.Messages.m_("Charge") + " " + org.cropster.roastingintelligence.util.Messages.m_("beanTemperature")
               );

            for (String curveName : roast.getCurves().keySet()) {
               String chargeTemp = "chargeTemperature-" + curveName;
               this.statisticsPanel
                  .addStatisticMeasure(
                     chargeTemp,
                     roast,
                     org.cropster.roastingintelligence.util.Messages.m_("Charge") + " " + org.cropster.roastingintelligence.util.Messages.m_(curveName)
                  );
            }

            this.statisticsPanel.addStatisticCurveStart("gas", roast);
            this.statisticsPanel.addStatisticCurveStart("airflow", roast);
            this.statisticsPanel.addStatisticMeasure("ambientTemperature", roast);
            RoastingComment firstCrackComment = roast.getComments().stream().filter(c -> "firstCrack".equals(c.getEvent())).findFirst().orElse(null);
            if (firstCrackComment != null) {
               this.statisticsPanel.addTime("firstCrack", firstCrackComment.getTime(), goals);
            }

            this.statisticsPanel.addTime("developmentTime", roast, goals);
            this.statisticsPanel.addStatisticMeasure("developmentTimeRatio", roast, goals);
            this.statisticsPanel.addTime("duration", roast.getDuration(), goals);
            this.statisticsPanel
               .addStatisticMeasure(
                  "endTemperature",
                  roast,
                  goals,
                  org.cropster.roastingintelligence.util.Messages.m_("End") + " " + org.cropster.roastingintelligence.util.Messages.m_("beanTemperature")
               );

            for (String curveName : roast.getCurves().keySet()) {
               String endTemp = "endTemperature-" + curveName;
               this.statisticsPanel
                  .addStatisticMeasure(
                     endTemp,
                     roast,
                     org.cropster.roastingintelligence.util.Messages.m_("End") + " " + org.cropster.roastingintelligence.util.Messages.m_(curveName)
                  );
            }

            this.statisticsPanel.addStatisticMeasure("startWeight", roast.getStartWeight(), goals);
            this.statisticsPanel.addStatisticMeasure("endWeight", roast.getEndWeight(), goals);
            this.statisticsPanel.addStatisticMeasure("weightLoss", new Measure(-roast.getWeightChange() * 100.0, Unit.PERCENT), goals);
            this.statisticsPanel.addStatisticMeasure("roastValue", roast, goals);
            this.statisticsPanel.addStatisticMeasure("roastValue2", roast, goals);
            this.statisticsPanel.addOtherGoals(goals);
            this.commentsPreviewPanel.setTemperatureUnit(appModel.getPreferences().getUnit(Unit.CELSIUS));
            this.commentsPreviewPanel.reset();
            if (!roast.getComments().isEmpty()) {
               Curve curve = roast.getCurves().get("beanTemperature");
               this.commentsPreviewPanel.setComments(roast.getComments(), curve);
               this.commentsPreviewPanel.setVisible(true);
            } else {
               this.commentsPreviewPanel.setVisible(false);
            }

            Analysis analysis = appModel.getData().getAnalyses().get(roast.getLot().getId());
            this.qualityPanel.setAnalysis(analysis, roast);
            this.qualityPanel.setBlendComponents(profile.getComponents(), appModel.getData());
            this.rightStartPanel.setCenter(this.profilePanel);
         } else {
            ReferenceInfoPanel referenceInfoPanel = new ReferenceInfoPanel();
            referenceInfoPanel.getStyleClass().add("padding-top-right");
            this.rightStartPanel.setCenter(referenceInfoPanel);
         }

         if (roast != null && !roast.getCurves().isEmpty()) {
            ReferenceChartPanel.updateWindow(appModel, roast);
            Button previewButton = this.statisticsPanel.getPreviewChartButton();
            previewButton.setVisible(true);
            previewButton.setOnAction(e -> ReferenceChartPanel.showWindow(appModel, roast, previewButton));
         } else {
            this.statisticsPanel.getPreviewChartButton().setVisible(false);
            ReferenceChartPanel.hideWindow();
         }

         if (appModel.getPreferences().isBetweenBatchProtocolEnabled()) {
            this.rightStartPanel.setTop(this.preparationPanel);
            if (betweenBatchModel == null) {
               this.betweenBatchPanel.setChart(null);
            } else {
               if (this.betweenBatchChartWrapper != null) {
                  this.betweenBatchChartWrapper.dispose();
               }

               CurveChart betweenBatchChart = new CurveChart(1, null);
               betweenBatchChart.setValueAxisLabel(tempUnit, org.cropster.roastingintelligence.util.Messages.m_(tempUnit));
               this.betweenBatchChartWrapper = new BetweenBatchChartWrapper();
               this.betweenBatchChartWrapper.init(betweenBatchChart);
               this.betweenBatchChartWrapper.setVisible(appModel.getSelectedProfile() != null);
               if (this.betweenBatchChartHandler != null) {
                  this.betweenBatchChartWrapper.getChartViewer().addEventHandler(MouseEvent.MOUSE_PRESSED, new WeakEventHandler(this.betweenBatchChartHandler));
                  this.betweenBatchChartWrapper
                     .getChartViewer()
                     .addEventHandler(MouseEvent.MOUSE_RELEASED, new WeakEventHandler(this.betweenBatchChartHandler));
               }

               for (Channel channel : betweenBatchModel.getChannels()) {
                  if (channel.isVisible(Channel.Visibility.CHART)) {
                     CurveModel curveModel = betweenBatchModel.getCurveModel(channel);
                     if (curveModel.getModel(LinearInterpolationCurveModel.class) != null) {
                        curveModel = new TailCurveModel(curveModel, () -> betweenBatchModel.getStartDate().getTime());
                     }

                     curveModel = new OffsetCurveModel(curveModel, t -> t - betweenBatchModel.getStartDate().getTime());
                     betweenBatchChart.putPrimaryCurveModel(curveModel, channel.getColor());
                  }
               }

               if (betweenBatchLog != null && !betweenBatchLog.isEmpty()) {
                  Map<String, Curve> bblCurves = betweenBatchLog.getCurves(appModel.getActiveChannels());

                  for (Entry<String, Curve> entry : bblCurves.entrySet()) {
                     java.awt.Color color = java.awt.Color.GRAY;
                     StandardCurveModel rootModel = new StandardCurveModel(
                        Measures.curveNameForReference(Measures.curveNameForBetweenBatch(entry.getKey())), roast.getStartDate(), entry.getValue().getUnit()
                     );
                     rootModel.setValues(entry.getValue());
                     CurveModel model = rootModel;
                     Unit preferredUnit = appModel.getPreferences().getPreferredUnit(rootModel.getUnit());
                     if (preferredUnit != null) {
                        model = new ConverterCurveModel(rootModel, preferredUnit);
                     }

                     betweenBatchChart.putPrimaryCurveModel(model, color);
                  }

                  betweenBatchChart.setPrimaryCurveName(Measures.curveNameForReference(Measures.curveNameForBetweenBatch("beanTemperature")));
                  betweenBatchChart.addTimeMarker(0L, org.cropster.roastingintelligence.util.Messages.m_("End of roast"));
                  if (betweenBatchLog.getLowestTemperatureTime() != null) {
                     betweenBatchChart.addTimeMarkerAtCenter(
                        betweenBatchLog.getLowestTemperatureTime(), org.cropster.roastingintelligence.util.Messages.m_("Lowest temperature")
                     );
                  }

                  betweenBatchChart.addTimeMarkerAtBottom(betweenBatchLog.getDuration(), org.cropster.roastingintelligence.util.Messages.m_("Start of roast"));

                  for (RoastingComment comment : betweenBatchLog.getComments()) {
                     betweenBatchChart.addComment(comment, Defaults.COLOR_COMMENTS, "beanTemperature");
                  }
               }

               this.showBetweenBatchTimer(NanoTime.currentTimeMillis() - appModel.getBetweenBatchModel().getStartDate().getTime());
               this.betweenBatchPanel.setChart(this.betweenBatchChartWrapper);
            }

            this.leftStartPanel.setCenter(this.betweenBatchPanel);
         } else {
            this.hideBetweenBatchTimer();
            this.leftStartPanel.setCenter(this.preparationPanel);
         }
      }
   }

   public void initCommentsPanel(EnumSet<AppModel.Hint> hints) {
      if (!hints.contains(AppModel.Hint.COMMENTS)) {
         Point2D anchor = this.centerPanel.localToScreen(this.centerPanel.getWidth() / 2.0, this.centerPanel.getHeight() / 2.0);
         Platform.runLater(
            () -> PopOverBubble.builder()
                  .closable(true)
                  .withoutDetachable()
                  .onClose(() -> hints.add(AppModel.Hint.COMMENTS))
                  .maximumWidth(360.0)
                  .withHeader(org.cropster.roastingintelligence.util.Messages.m_("Click anywhere on the chart to add a comment"), Icon.TIP.load("icon--muted"))
                  .withContent(
                     org.cropster.roastingintelligence.util.Messages.m_(
                        "To add a comment simply click on the chart. The comment will appear at the exact time where you clicked. You can of course change the time if you want!"
                     )
                  )
                  .build()
                  .showAt(this.centerPanel, ArrowLocation.BOTTOM_CENTER, anchor.getX(), anchor.getY())
         );
      }
   }

   @Override
   public void settingsChanged(AppModel appModel) {
      double ctrPosition = ((Divider)this.centerToRightSplitter.getDividers().get(0)).getPosition();
      double rtcPosition = ((Divider)this.referencesToChannelsSplitter.getDividers().get(0)).getPosition();
      this.populateStatusPanel(appModel);
      Platform.runLater(() -> {
         ((Divider)this.referencesToChannelsSplitter.getDividers().get(0)).setPosition(rtcPosition);
         ((Divider)this.centerToRightSplitter.getDividers().get(0)).setPosition(ctrPosition);
      });
      Preferences preferences = appModel.getPreferences();
      Unit weightUnit = preferences.getWeightUnit();
      this.preparationPanel.setUnit(weightUnit);
      this.preparationPanel.updateLotLabels(appModel);
      this.preparationPanel.refreshReplacementLots();
      Profile profile = appModel.getSelectedProfile();
      Machine machine = appModel.getSelectedMachine();
      if (profile != null && profile.getBatchSize() != null) {
         this.preparationPanel.setStartWeight(profile.getBatchSize().toUnit(weightUnit).getAmount());
      } else if (machine != null && machine.getCapacity() != null) {
         this.preparationPanel.setStartWeight(machine.getCapacity().toUnit(weightUnit).getAmount());
      }

      this.setStartPanelReference(appModel.getSelectedProfile(), appModel);
      this.updateLiveGoalsAndStatistics(preferences);
      this.updateMachineControls(appModel);
   }

   private void updateLiveGoalsAndStatistics(Preferences preferences) {
      this.statisticsPanel.setGoalStstisticsVisible(!preferences.isHideGoalStatisticsEnabled());
      this.getActiveGoalEmptyStateGrid().getGridView().refresh();
   }

   private void updateMachineControls(AppModel appModel) {
      this.gasControl.init(appModel);
      this.setTemperatureControl.init(appModel);
      this.airflowControl.init(appModel);
      this.speedControl.init(appModel);
      boolean gasControlVisible = appModel.isChannelActive("gasControl") || appModel.isChannelActive("customControl");
      boolean setTemperatureControlVisible = appModel.isChannelActive("setTemperatureControl");
      boolean airflowControlVisible = appModel.isChannelActive("airflowControl")
         || appModel.isChannelActive("drumPressureControl")
         || appModel.isChannelActive("fanSpeedControl");
      boolean speedControlVisible = appModel.isChannelActive("drumSpeedControl");
      boolean slidersVisible = gasControlVisible || setTemperatureControlVisible || airflowControlVisible || speedControlVisible;
      boolean buttonsVisible = this.controlStateContainer.updateControls(appModel);
      this.controlToggleBar.controlsChanged(slidersVisible, buttonsVisible);
      this.gasControl.getToggle().setDisplayable(gasControlVisible);
      this.setTemperatureControl.getToggle().setDisplayable(setTemperatureControlVisible);
      this.airflowControl.getToggle().setDisplayable(airflowControlVisible);
      this.speedControl.getToggle().setDisplayable(speedControlVisible);
      this.airflowControl.initButtons();
      this.speedControl.initButtons();
      this.setAutoStartSourceFactory(AppModel.AutoStartMode.MACHINE_EVENT.equals(appModel.getAutoStartMode()) ? appModel.getAutoStartSourceFactory() : null);
      boolean buttonsOnTop = appModel.getPreferences().isSliderControlButtonsOnTop();
      this.gasControl.setButtonsOnTop(buttonsOnTop);
      this.setTemperatureControl.setButtonsOnTop(buttonsOnTop);
      this.airflowControl.setButtonsOnTop(buttonsOnTop);
      this.speedControl.setButtonsOnTop(buttonsOnTop);
   }

   public MessageBox showMessagePanel(String message, String styleClass, Button button) {
      Iterator<Node> it = this.messagePanel.getChildren().iterator();

      while (it.hasNext()) {
         Node node = it.next();
         if (node instanceof MessageBox) {
            MessageBox messageBox = (MessageBox)node;
            if (messageBox.getText().equals(message)) {
               it.remove();
            }
         }
      }

      MessageBox messageBox = new MessageBox();
      messageBox.getStyleClass().clear();
      messageBox.getStyleClass().add("info-message");
      messageBox.getStyleClass().add(styleClass);
      messageBox.setText(message);
      if (button != null) {
         Region spacer = new Region();
         spacer.setMinWidth(10.0);
         spacer.setMaxWidth(Double.MAX_VALUE);
         HBox.setHgrow(spacer, Priority.ALWAYS);
         button.setMinWidth(Double.NEGATIVE_INFINITY);
         button.getStyleClass().add("button--plain");
         messageBox.getChildren().add(spacer);
         messageBox.getChildren().add(button);
      }

      this.messagePanel.getChildren().add(messageBox);
      return messageBox;
   }

   public void removeMessagePanel(Pane messageBox) {
      this.messagePanel.getChildren().remove(messageBox);
   }

   private void updateAutoStartStyle() {
      if (this.getAutoStartSourceFactory() != null && !this.getAutoStartReady()) {
         this.bottomPanel.getStyleClass().add("player-bar--warning");
         this.controlToggleBar.getStyleClass().add("player-bar--warning");
      } else {
         this.bottomPanel.getStyleClass().remove("player-bar--warning");
         this.controlToggleBar.getStyleClass().remove("player-bar--warning");
      }
   }

   public void disposeBetweenBatchChart() {
      if (this.betweenBatchChartWrapper != null) {
         this.betweenBatchPanel.setChart(null);
         this.betweenBatchChartWrapper.dispose();
         this.betweenBatchChartWrapper = null;
      }
   }

   private static final class SelectionSnapshot {
      private final MainPanelController.SelectionSnapshot.Kind kind;
      private final Long id;
      private final Object item;

      private SelectionSnapshot(MainPanelController.SelectionSnapshot.Kind kind, Long id, Object item) {
         this.kind = kind;
         this.id = id;
         this.item = item;
      }

      static MainPanelController.SelectionSnapshot of(Object item) {
         if (item instanceof Roast roast) {
            Long lotId = roast.getLot() != null ? roast.getLot().getId() : null;
            return new MainPanelController.SelectionSnapshot(MainPanelController.SelectionSnapshot.Kind.ROAST, lotId, item);
         } else {
            return item instanceof Scheduled scheduled
               ? new MainPanelController.SelectionSnapshot(MainPanelController.SelectionSnapshot.Kind.SCHEDULED, scheduled.getId(), item)
               : new MainPanelController.SelectionSnapshot(MainPanelController.SelectionSnapshot.Kind.OTHER, null, item);
         }
      }

      @Override
      public boolean equals(Object other) {
         if (this == other) {
            return true;
         } else {
            if (!(other instanceof MainPanelController.SelectionSnapshot snapshot) || this.kind != snapshot.kind) {
               return false;
            }

            return this.id == null && snapshot.id == null ? this.item == snapshot.item : this.id != null && this.id.equals(snapshot.id);
         }
      }

      @Override
      public int hashCode() {
         int result = this.kind.hashCode();
         return 31 * result + (this.id != null ? this.id.hashCode() : System.identityHashCode(this.item));
      }

      private static enum Kind {
         ROAST,
         SCHEDULED,
         OTHER;
      }
   }
}
