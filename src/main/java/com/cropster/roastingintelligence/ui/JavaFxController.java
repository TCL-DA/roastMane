package com.cropster.roastingintelligence.ui;

import com.cropster.roastingintelligence.Data;
import com.cropster.roastingintelligence.Preferences;
import com.cropster.roastingintelligence.config.FileSystemConfiguration;
import com.cropster.roastingintelligence.model.ControlStateConfiguration;
import com.cropster.roastingintelligence.model.RoastingPhase;
import com.cropster.roastingintelligence.model.livegoals.LiveGoalBetweenBatchModel;
import com.cropster.roastingintelligence.model.livegoals.LiveGoalPostRoastingModel;
import com.cropster.roastingintelligence.model.notification.Notification;
import com.cropster.roastingintelligence.model.notification.NotificationType;
import com.cropster.roastingintelligence.service.AlarmService;
import com.cropster.roastingintelligence.service.LiveGoalService;
import com.cropster.roastingintelligence.service.NotificationBusService;
import com.cropster.roastingintelligence.service.StorageService;
import com.cropster.roastingintelligence.task.RoastReplayer;
import com.cropster.roastingintelligence.task.control.WriteCommand;
import com.cropster.roastingintelligence.ui.audio.SoundManager;
import com.cropster.roastingintelligence.ui.chart.BetweenBatchChartWrapper;
import com.cropster.roastingintelligence.ui.chart.RoastChartWrapper;
import com.cropster.roastingintelligence.ui.components.ButtonBarWindow;
import com.cropster.roastingintelligence.ui.components.CommentLabel;
import com.cropster.roastingintelligence.ui.components.CommentPopupContent;
import com.cropster.roastingintelligence.ui.components.CommentPopupHeader;
import com.cropster.roastingintelligence.ui.components.ControlSlider;
import com.cropster.roastingintelligence.ui.components.ControlToggleBar;
import com.cropster.roastingintelligence.ui.components.KeyCodeCombinationLabel;
import com.cropster.roastingintelligence.ui.components.LineBreak;
import com.cropster.roastingintelligence.ui.components.MessageBox;
import com.cropster.roastingintelligence.ui.components.NewBadge;
import com.cropster.roastingintelligence.ui.components.PredictionPanel;
import com.cropster.roastingintelligence.ui.components.QrCodeRenderer;
import com.cropster.roastingintelligence.ui.components.RoastInfoPanel;
import com.cropster.roastingintelligence.ui.components.RoastLabel;
import com.cropster.roastingintelligence.ui.components.SidebarButton;
import com.cropster.roastingintelligence.ui.components.Tooltip;
import com.cropster.roastingintelligence.ui.components.UserPanel;
import com.cropster.roastingintelligence.ui.handler.measurement.ChannelStatusHandler;
import com.cropster.roastingintelligence.ui.handler.measurement.ChannelStatusHandlerAutoMode;
import com.cropster.roastingintelligence.ui.handler.measurement.ChannelStatusHandlerGiesen;
import com.cropster.roastingintelligence.ui.handler.measurement.ControlHandlerAuto;
import com.cropster.roastingintelligence.ui.handler.measurement.ControlHandlerBase;
import com.cropster.roastingintelligence.ui.handler.measurement.ControlHandlerGiesenGas;
import com.cropster.roastingintelligence.ui.handler.measurement.ControlHandlerGiesenGasControl;
import com.cropster.roastingintelligence.ui.handler.measurement.ControlHandlerManual;
import com.cropster.roastingintelligence.ui.handler.measurement.ControlHandlerManualMeasurement;
import com.cropster.roastingintelligence.ui.handler.measurement.ControlHandlerMode;
import com.cropster.roastingintelligence.ui.handler.measurement.ControlStateBarHandler;
import com.cropster.roastingintelligence.ui.handler.measurement.HandlerChainManager;
import com.cropster.roastingintelligence.ui.handler.measurement.MeasurementHandler;
import com.cropster.roastingintelligence.ui.layout.AlarmsSidebar;
import com.cropster.roastingintelligence.ui.layout.BetweenBatchPanel;
import com.cropster.roastingintelligence.ui.layout.FinishPanel;
import com.cropster.roastingintelligence.ui.layout.MainPanelController;
import com.cropster.roastingintelligence.ui.layout.PreferencesWindowController;
import com.cropster.roastingintelligence.ui.layout.PreparationPanel;
import com.cropster.roastingintelligence.ui.layout.PrintWindowController;
import com.cropster.roastingintelligence.ui.layout.ProductionSidebar;
import com.cropster.roastingintelligence.ui.layout.SupportSidebar;
import com.cropster.roastingintelligence.ui.layout.preferences.ChartPreferencesPage;
import com.cropster.roastingintelligence.ui.layout.preferences.ConfigurationPreferencesPage;
import com.cropster.roastingintelligence.ui.layout.preferences.GeneralPreferencesPage;
import com.cropster.roastingintelligence.ui.layout.preferences.InterfacePreferencesPage;
import com.cropster.roastingintelligence.ui.layout.preferences.MeasurementPreferencesPage;
import com.cropster.roastingintelligence.ui.layout.preferences.PrintPreferencesPage;
import com.cropster.roastingintelligence.ui.layout.preferences.RorPreferencesPage;
import com.cropster.roastingintelligence.ui.layout.preferences.SmartPreferencesPage;
import com.cropster.roastingintelligence.ui.layout.preferences.UnitPreferencesPage;
import com.cropster.roastingintelligence.ui.list.GridView;
import com.cropster.roastingintelligence.ui.list.skin.AccessibleListViewSkin;
import com.cropster.roastingintelligence.ui.listener.CurvePredictionListener;
import com.cropster.roastingintelligence.ui.listener.FirstCrackPredictionListener;
import com.cropster.roastingintelligence.ui.listener.FlickPredictionListener;
import com.cropster.roastingintelligence.ui.util.ActionBuilder;
import com.cropster.roastingintelligence.ui.util.AlarmsUtil;
import com.cropster.roastingintelligence.ui.util.DelegatingBuilderFactory;
import com.cropster.roastingintelligence.ui.util.FxListenerUtil;
import com.cropster.roastingintelligence.ui.util.Icon;
import com.cropster.roastingintelligence.ui.util.KeyboardShortcut;
import com.cropster.roastingintelligence.ui.util.PlatformScheduler;
import com.cropster.roastingintelligence.ui.util.PopOverHandler;
import com.cropster.roastingintelligence.ui.util.ProgressMonitor;
import com.cropster.roastingintelligence.ui.util.ProgressTask;
import com.cropster.roastingintelligence.ui.util.Tours;
import com.cropster.roastingintelligence.ui.util.VeilSupport;
import com.cropster.roastingintelligence.ui.util.WindowUtil;
import com.cropster.roastingintelligence.ui.whatsnew.WhatsNewDialog;
import com.cropster.roastingintelligence.util.DataUtil;
import com.cropster.roastingintelligence.util.MinusPlusIterator;
import com.cropster.roastingintelligence.util.RenamingThreadFactory;
import com.cropster.util.model.Unit;
import com.segment.analytics.Analytics;
import com.sun.javafx.print.PrintHelper;
import jakarta.ws.rs.WebApplicationException;
import java.awt.Color;
import java.awt.Toolkit;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BooleanSupplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipOutputStream;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.WeakEventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.print.PageLayout;
import javafx.print.PageOrientation;
import javafx.print.PageRange;
import javafx.print.Paper;
import javafx.print.PrintQuality;
import javafx.print.PrintResolution;
import javafx.print.Printer;
import javafx.print.PrinterJob;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.Skin;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.SplitPane.Divider;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TouchEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.scene.transform.Rotate;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.PopupWindow.AnchorLocation;
import javafx.util.Duration;
import local.OfflineBootstrap;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.controlsfx.control.action.Action;
import org.cropster.roastingintelligence.AppModel;
import org.cropster.roastingintelligence.ApplicationController;
import org.cropster.roastingintelligence.BetweenBatchManager;
import org.cropster.roastingintelligence.BetweenBatchModel;
import org.cropster.roastingintelligence.Defaults;
import org.cropster.roastingintelligence.MeasurementHub;
import org.cropster.roastingintelligence.PreferencesStore;
import org.cropster.roastingintelligence.RoastingIntelligenceApp;
import org.cropster.roastingintelligence.RoastingManager;
import org.cropster.roastingintelligence.RoastingModel;
import org.cropster.roastingintelligence.SyncService;
import org.cropster.roastingintelligence.VersionChecker;
import org.cropster.roastingintelligence.listener.CsarDataListener;
import org.cropster.roastingintelligence.listener.MessageListener;
import org.cropster.roastingintelligence.listener.RoastingListener;
import org.cropster.roastingintelligence.listener.SettingsListener;
import org.cropster.roastingintelligence.measurement.CheckableSource;
import org.cropster.roastingintelligence.measurement.MeasurementListener;
import org.cropster.roastingintelligence.measurement.MeasurementReaderWriter;
import org.cropster.roastingintelligence.measurement.MeasurementSource;
import org.cropster.roastingintelligence.measurement.MeasurementSourceFactory;
import org.cropster.roastingintelligence.measurement.SourceParameter;
import org.cropster.roastingintelligence.measurement.impl.GiesenPlcSourceFactory;
import org.cropster.roastingintelligence.model.AutoStartListener;
import org.cropster.roastingintelligence.model.BlendComponent;
import org.cropster.roastingintelligence.model.Channel;
import org.cropster.roastingintelligence.model.CommentListener;
import org.cropster.roastingintelligence.model.ControlState;
import org.cropster.roastingintelligence.model.Curve;
import org.cropster.roastingintelligence.model.CurveModel;
import org.cropster.roastingintelligence.model.Lot;
import org.cropster.roastingintelligence.model.LotTransaction;
import org.cropster.roastingintelligence.model.Machine;
import org.cropster.roastingintelligence.model.Measure;
import org.cropster.roastingintelligence.model.Profile;
import org.cropster.roastingintelligence.model.Roast;
import org.cropster.roastingintelligence.model.RoastingAnalyzerListener;
import org.cropster.roastingintelligence.model.RoastingComment;
import org.cropster.roastingintelligence.model.Scheduled;
import org.cropster.roastingintelligence.model.WorkerTask;
import org.cropster.roastingintelligence.model.impl.CombinedCurveModel;
import org.cropster.roastingintelligence.model.impl.ConverterCurveModel;
import org.cropster.roastingintelligence.model.impl.LinearInterpolationCurveModel;
import org.cropster.roastingintelligence.model.impl.MovingAverageCurveModel;
import org.cropster.roastingintelligence.model.impl.OverwriteCurveModel;
import org.cropster.roastingintelligence.model.impl.RemoveDuplicatesCurveModel;
import org.cropster.roastingintelligence.model.impl.RorCurveModel;
import org.cropster.roastingintelligence.model.impl.ScalingCurveModel;
import org.cropster.roastingintelligence.model.impl.SkipCurveModel;
import org.cropster.roastingintelligence.model.impl.SnapshotCurveModel;
import org.cropster.roastingintelligence.model.impl.StandardCurveModel;
import org.cropster.roastingintelligence.model.impl.TailCurveModel;
import org.cropster.roastingintelligence.remote.CsarConnection;
import org.cropster.roastingintelligence.remote.Version;
import org.cropster.roastingintelligence.ui.chart.AreaAnnotation;
import org.cropster.roastingintelligence.ui.chart.CurveChart;
import org.cropster.roastingintelligence.util.BoolMap;
import org.cropster.roastingintelligence.util.DateUtil;
import org.cropster.roastingintelligence.util.FileComparator;
import org.cropster.roastingintelligence.util.MathUtil;
import org.cropster.roastingintelligence.util.MultiResourceBundle;
import org.cropster.roastingintelligence.util.NanoTime;
import org.cropster.roastingintelligence.util.RoastFilters;
import org.ocpsoft.prettytime.PrettyTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JavaFxController
   implements ApplicationController,
   MeasurementListener,
   CsarDataListener,
   RoastingListener,
   RoastingAnalyzerListener,
   SettingsListener,
   AutoStartListener {
   public static final String STYLE_SHEET = SystemUtils.IS_OS_MAC_OSX ? "/main-mac.css" : "/main.css";
   public static final String PREF_SELECTED_LOTS = "RoastingStartPanel.selectedLots";
   public static final String PREF_NOTIFICATION_INDEX = "notificationIndex";
   public static final String PREF_WHATS_NEW_IDENTIFIER = "whatsNewIdentifier";
   public static final String REFERENCE_CURVE_NAME = "Perfect " + org.cropster.roastingintelligence.util.Messages.m_("beanTemperature");
   private static final int CHART_STROKE_WIDTH = 2;
   private static final String KEYBOARD_SHORTCUTS_WINDOW_ID = "KEYBOARD_SHORTCUTS_WINDOW_ID";
   private static final String PREFERENCES_WINDOW_ID = "PREFERENCES_WINDOW_ID";
   private static final String WHATS_NEW_WINDOW_ID = "WHATS_NEW_WINDOW_ID";
   private static final String RESTART_APPLICATION_TITLE = "Restart application";
   private final Logger logger = LoggerFactory.getLogger(JavaFxController.class);
   private final PrettyTime prettyTime = new PrettyTime();
   private final ExecutorService syncExecutor = Executors.newSingleThreadExecutor(new RenamingThreadFactory("BlockingSync"));
   private final ExecutorService controlExecutor = Executors.newSingleThreadExecutor(new RenamingThreadFactory("Control"));
   private final Set<RoastReplayer> replayers = new CopyOnWriteArraySet<>();
   private final Set<ScheduledFuture<?>> controlFutures = new CopyOnWriteArraySet<>();
   private final RoastingIntelligenceApp application;
   private final JavaFxApplicationState state = new JavaFxApplicationState();
   private final AtomicBoolean shiftDown = new AtomicBoolean();
   private final PreferencesStore preferences;
   private final AppModel appModel;
   private final RoastingManager roastingManager;
   private BetweenBatchManager betweenBatchManager;
   private final CsarConnection connection;
   private final SyncService syncService;
   private final ComponentManager componentManager;
   private final AlarmController alarmController = new AlarmController();
   private final LiveGoalController liveGoalController = new LiveGoalController();
   private final ToastNotificationController toastNotificationController = new ToastNotificationController();
   private final SystemNotificationController systemNotificationController = new SystemNotificationController();
   private final PermissionNotificationController permissionNotificationController = new PermissionNotificationController();
   private final BlendNotificationController blendNotificationController = new BlendNotificationController();
   private final NotificationBusService notificationBusService = RoastingIntelligenceApp.retrieveSingleton(NotificationBusService.class);
   private MeasurementHub measurementHub;
   private volatile RoastingModel roastingModel;
   private Timeline timeline;
   private MainPanelController mainPanelController;
   private RoastChartWrapper roastChartWrapper;
   private final Map<String, AreaAnnotation> eventAnnotations = new HashMap<>();
   private Runnable shutdownHook;
   private FinishPanel finishPanel;
   private Timeline stopRoastingTimer;
   private Long[] selectedLotIds;
   private Stage preferencesWindow;
   private Stage printWindow;
   private ButtonBarWindow roastInfoWindow;
   private AnchorPane mainPanel;
   private final RoastInfoPanel roastInfoPanel = new RoastInfoPanel();
   private PopOverHandler<CommentPopupContent> commentPopOver;
   private Popup profilePopup;
   private PreferencesWindowController preferencesController;
   private PrintWindowController printWindowController;
   private BooleanProperty curvePredictionEnabledProperty;
   private BooleanProperty firstCrackPredictionEnabledProperty;
   private BooleanProperty flickPredictionEnabledProperty;
   private List<String> setupWarnings;
   private ButtonBarWindow keyboardShortcutsWindow;
   private WhatsNewDialog startUpWhatsNewDialog = null;
   private ChangeListener<Boolean> buttonsToggleButtonListener;
   private ChangeListener<Boolean> controlsToggleButtonListener;
   private ChangeListener<Boolean> slidersToggleButtonListener;

   public JavaFxController(
      RoastingIntelligenceApp application, PreferencesStore preferences, AppModel appModel, RoastingManager roastingManager, CsarConnection connection
   ) {
      this.application = application;
      this.preferences = preferences;
      this.appModel = appModel;
      this.roastingManager = roastingManager;
      this.connection = connection;
      this.syncService = new SyncService(connection, appModel);
      this.componentManager = new ComponentManager(preferences.getPreferences());
   }

   private FXMLLoader loadPage(String templateFile) {
      try {
         URL fxmlResource = this.getClass().getResource(templateFile);
         FXMLLoader fxmlLoader = new FXMLLoader(
            fxmlResource,
            new JavaFxUtil.FailSafeResourceBundle(
               new MultiResourceBundle(
                  org.cropster.roastingintelligence.util.Messages.getBundle(), org.cropster.roastingintelligence.util.Messages.forEnum(Unit.class)
               )
            )
         );
         fxmlLoader.setBuilderFactory(new DelegatingBuilderFactory());
         fxmlLoader.load();
         return fxmlLoader;
      } catch (IOException var4) {
         throw new RuntimeException("Error loading page", var4);
      }
   }

   private void addAutoComment(RoastingComment comment) {
      MinusPlusIterator minusPlusIterator = new MinusPlusIterator(10);

      for (long targetTime = comment.getTime(); minusPlusIterator.hasNext(); targetTime = comment.getTime() + (long)minusPlusIterator.next().intValue() * 1000L) {
         if (this.roastingModel.getComment(targetTime) == null) {
            this.roastingModel.addComment(new RoastingComment(targetTime, comment.getEvent(), comment.getText()));
            this.state.getAutoMarkedEvents().add(comment.getEvent());
            break;
         }
      }
   }

   private void startTimeline() {
      this.stopTimeline();
      this.timeline = new Timeline(
         new KeyFrame[]{
            new KeyFrame(
               Duration.seconds(1.0),
               e -> {
                  String lastSynced = this.appModel.getSyncTime() > 0L
                     ? this.prettyTime.format(new Date(this.appModel.getSyncTime()))
                     : org.cropster.roastingintelligence.util.Messages.m_("Never");
                  this.notificationBusService
                     .publish(
                        Notification.of(org.cropster.roastingintelligence.util.Messages.m_("Last synced: {0}", lastSynced)),
                        "system-status-synchronization-date"
                     );
                  Measure ambientTemperature = this.measurementHub.getAmbientTemperature();
                  if (ambientTemperature != null) {
                     Measure measure = ambientTemperature.toUnit(this.appModel.getPreferences().getPreferredUnit(Unit.CELSIUS));
                     String value = String.format(
                        "%s %.1f%s",
                        org.cropster.roastingintelligence.util.Messages.m_("Ambient temperature: "),
                        measure.getAmount(),
                        org.cropster.roastingintelligence.util.Messages.m_(measure.getUnit())
                     );
                     this.notificationBusService.publish(Notification.of(value), "system-status-ambient-temperature");
                  }

                  if (this.roastingModel != null && this.roastingModel.getEndDate() == null) {
                     this.mainPanelController.updateStatusPanels(this.roastingModel);
                  } else {
                     this.mainPanelController.updateStatusPanels(this.measurementHub);
                  }

                  this.betweenBatchManager.updateBetweenBatchTime();
                  if (this.roastingModel != null) {
                     long duration;
                     if (JavaFxApplicationState.Phase.STOPPED.equals(this.state.getPhase())) {
                        duration = this.finishPanel.getEndTime() - this.finishPanel.getStartTime();
                     } else {
                        duration = this.roastingModel.getDuration();
                     }

                     this.mainPanelController.getTimerLabel().setTime(duration);
                     Roast reference = this.roastingModel.getData().getReferences().get(this.roastingModel.getRoast().getProfile());
                     if (reference != null && this.roastingModel.getCurves().containsKey("beanTemperature")) {
                        this.mainPanelController.getCommentsPanel().setVisible(true);
                        Collection<RoastingComment> comments = reference.getCommentsWithControl();
                        List<RoastingComment> upcomingComments = comments.stream()
                           .filter(c -> this.appModel.isCommentEnabledInSidebar(c))
                           .filter(c -> c.getTime() >= duration)
                           .collect(Collectors.toList());
                        int expectedMaxComments = upcomingComments.size() + 1;
                        this.initUpcomingCommentsPanel(expectedMaxComments);
                        VBox commentsPanel = this.mainPanelController.getUpcomingCommentsPanel();
                        Measure currentTemp = this.roastingModel.getTempAtTime(duration);
                        CommentLabel pastCL = (CommentLabel)commentsPanel.getChildren().get(0);
                        comments.stream()
                           .filter(c -> this.appModel.isCommentEnabledInSidebar(c))
                           .filter(c -> c.getTime() < duration)
                           .sorted(Collections.reverseOrder(new RoastingComment.TimeComparator()))
                           .limit(1L)
                           .findFirst()
                           .ifPresentOrElse(
                              c -> {
                                 Measure commentTemperature = Optional.ofNullable(reference.getTempAtTime(c.getTime()))
                                    .map(m -> m.toUnit(this.appModel.getPreferences().getUnit(Unit.CELSIUS)))
                                    .orElse(null);
                                 pastCL.init(c, commentTemperature, this.roastingModel, duration, this.state);
                              },
                              () -> pastCL.reset()
                           );

                        for (int i = expectedMaxComments - 1; i >= 1; i--) {
                           CommentLabel cLabel = (CommentLabel)commentsPanel.getChildren().get(i);
                           if (i <= upcomingComments.size()) {
                              RoastingComment comment = upcomingComments.get(i - 1);
                              Measure commentTemperature = Optional.ofNullable(reference.getTempAtTime(comment.getTime()))
                                 .map(m -> m.toUnit(this.appModel.getPreferences().getUnit(Unit.CELSIUS)))
                                 .orElse(null);
                              cLabel.init(comment, commentTemperature, this.roastingModel, duration, this.state);
                           } else if (i == 1 && upcomingComments.isEmpty()) {
                              cLabel.reset();
                           } else {
                              commentsPanel.getChildren().remove(i);
                           }
                        }

                        if (this.appModel.getPreferences().isEventAutoMarking()) {
                           for (RoastingComment comment : reference.getComments()) {
                              if (comment.getEvent() != null
                                 && !this.state.getAutoMarkedEvents().contains(comment.getEvent())
                                 && Arrays.binarySearch(Defaults.EVENTS_AUTO_MARK, comment.getEvent()) >= 0
                                 && this.appModel.isCommentEnabledInChart(comment)
                                 && this.roastingModel.getEventTime(comment.getEvent()) == null) {
                                 if (this.roastingModel.getTurningPoint() != null) {
                                    Measure commentTemp = reference.getTempAtTime(comment.getTime());
                                    if (commentTemp != null && currentTemp.getAmount() >= commentTemp.getAmount()) {
                                       MeasurementSourceFactory sourceFactory = RoastingIntelligenceApp.retrieveSingleton(
                                          this.roastingModel.getChannel("beanTemperature").getSourceFactory()
                                       );
                                       Long commentTime = sourceFactory.getSamplingInterval() > 1000
                                          ? this.roastingModel.getTimeForTemp(commentTemp.getAmount())
                                          : duration;
                                       this.addAutoComment(
                                          new RoastingComment(commentTime != null ? commentTime : duration, comment.getEvent(), comment.getText())
                                       );
                                    }
                                 } else if (duration >= comment.getTime()) {
                                    this.addAutoComment(comment.clone());
                                 }
                                 break;
                              }
                           }
                        }

                        if (this.appModel.getPreferences().isReferenceCommentHistoryHidden()) {
                           Long tpTime = reference.getEventTimes().get("turningPoint");

                           for (RoastingComment commentx : reference.getComments()) {
                              if (commentx.getEvent() == null || Arrays.binarySearch(Defaults.EVENTS, commentx.getEvent()) < 0) {
                                 if (this.appModel.getPreferences().isCommentingByTemp() && tpTime != null && duration > tpTime && commentx.getTime() > tpTime) {
                                    Measure commentTemp = reference.getTempAtTime(commentx.getTime());
                                    if (commentTemp != null && currentTemp != null && currentTemp.getAmount() > commentTemp.getAmount()) {
                                       this.roastChartWrapper.getChart().removeComment(commentx, REFERENCE_CURVE_NAME);
                                    }
                                 } else if (duration > commentx.getTime()) {
                                    this.roastChartWrapper.getChart().removeComment(commentx, REFERENCE_CURVE_NAME);
                                 }
                              }
                           }
                        }
                     } else {
                        this.mainPanelController.getCommentsPanel().setVisible(false);
                     }

                     AppModel.TimerType timerType = this.appModel.getPreferences().getTimerType();
                     if (timerType != null && !AppModel.TimerType.NONE.equals(timerType)) {
                        String text = org.cropster.roastingintelligence.util.Messages.m_("n/a");
                        Long time = null;
                        Long firstCrackTime = this.roastingModel.getEventTime("firstCrack");
                        this.mainPanelController.getPercentageLabel().setVisible(firstCrackTime != null);
                        if (firstCrackTime != null) {
                           text = org.cropster.roastingintelligence.util.Messages.m_("Development");
                           time = duration - firstCrackTime;
                           if (JavaFxApplicationState.Phase.STOPPED.equals(this.state.getPhase())) {
                              time = time + this.finishPanel.getStartTime();
                           }

                           Measure developmentTimeRatio = this.roastingModel.calcDevelopmentTimeRatio(duration, time);
                           if (developmentTimeRatio != null) {
                              this.mainPanelController.getPercentageLabel().setPercentage(developmentTimeRatio.getAmount() / 100.0);
                           }
                        } else if (AppModel.TimerType.MODULATION.equals(timerType)) {
                           Long colorChangeTime = this.roastingModel.getEventTime("colorChange");
                           if (colorChangeTime != null) {
                              text = org.cropster.roastingintelligence.util.Messages.m_("Maillard");
                              time = duration - colorChangeTime;
                              if (JavaFxApplicationState.Phase.STOPPED.equals(this.state.getPhase())) {
                                 time = time + this.finishPanel.getStartTime();
                              }
                           }
                        }

                        this.mainPanelController.getSecondaryTimerLabel().setVisible(time != null);
                        if (time != null && time >= 0L) {
                           this.mainPanelController.getSecondaryTimerLabel().setText(text);
                           this.mainPanelController.getSecondaryTimerLabel().setTime(time);
                        }

                        if (AppModel.TimerType.MODULATION.equals(timerType)) {
                           this.updateModulationStages();
                        }
                     }
                  } else {
                     this.mainPanelController.getCommentsPanel().setVisible(false);
                  }
               },
               new KeyValue[0]
            )
         }
      );
      this.timeline.setCycleCount(-1);
      this.timeline.play();
   }

   public void stopTimeline() {
      if (this.timeline != null) {
         this.timeline.stop();
      }
   }

   private EventHandler<ActionEvent> createStopHandler() {
      if (this.appModel.getPreferences().isStopRoastingConfirmationEnabled()) {
         return new JavaFxController.ConfirmStopHandler(e -> this.stopRoasting());
      }
      return e -> this.stopRoasting();
   }

   private void initUpcomingCommentsPanel(int commentsCount) {
      ObservableList<Node> commentLabels = this.mainPanelController.getUpcomingCommentsPanel().getChildren();

      while (commentLabels.size() > commentsCount) {
         commentLabels.remove(commentLabels.size() - 1);
      }

      for (int i = 0; i < commentsCount; i++) {
         CommentLabel commentLabel;
         if (i < commentLabels.size()) {
            commentLabel = (CommentLabel)commentLabels.get(i);
         } else {
            commentLabel = new CommentLabel();
            commentLabels.add(commentLabel);
         }

         commentLabel.removeContentStyleClass("comment-label--past");
         commentLabel.removeContentStyleClass("comment-label--current");
         if (i == 0) {
            commentLabel.addContentStyleClass("comment-label--past");
         } else if (i == 1) {
            commentLabel.addContentStyleClass("comment-label--current");
         }
      }
   }

   private MenuItem addMenuItem(Menu menu, String text, KeyCodeCombination keyCodeCombination, EventHandler<ActionEvent> event) {
      MenuItem menuItem = new MenuItem(text);
      if (keyCodeCombination != null) {
         menuItem.setAccelerator(keyCodeCombination);
      }

      menuItem.setOnAction(event);
      menu.getItems().add(menuItem);
      return menuItem;
   }

   private MenuItem addMenuItem(Menu menu, String text, KeyboardShortcut shortcut, EventHandler<ActionEvent> event) {
      return this.addMenuItem(menu, text, shortcut.getKeyCodeCombination(), event);
   }

   private MenuItem addMenuItem(Menu menu, String text, EventHandler<ActionEvent> event) {
      return this.addMenuItem(menu, text, (KeyCodeCombination)null, event);
   }

   private MenuItem addMenuSeparator(Menu menu) {
      SeparatorMenuItem separator = new SeparatorMenuItem();
      menu.getItems().add(separator);
      return separator;
   }

   private void showPreferences() {
      assert Platform.isFxApplicationThread();

      boolean showDemo = this.shiftDown.get();
      File demoFile = new File(FileUtils.getUserDirectory(), "demo-roast.json");
      if (showDemo && !demoFile.exists()) {
         this.shiftDown.set(false);
         WindowUtil.showAlert(
            AlertType.ERROR,
            this.application.getStage(),
            "File not found",
            String.format(
               "Demo file to  simulate roast curve not found!\nPut a roast file named \"%s\" in your home directory (%s).",
               "demo-roast.json",
               FileUtils.getUserDirectoryPath()
            )
         );
      } else {
         Task<Void> groupMemberUpdater = new Task<Void>() {
            protected Void call() throws Exception {
               PermissionNotificationController.updateGroupsMembers(JavaFxController.this.appModel, JavaFxController.this.connection);
               JavaFxController.this.logger.info("Group members synced from server");
               JavaFxController.this.appModel.fireCsarDataReceived();
               return null;
            }

            protected void cancelled() {
               JavaFxController.this.preferencesController.getPreferencesPanel().resetConfigurationItem();
            }

            protected void failed() {
               JavaFxController.this.preferencesController.getPreferencesPanel().resetConfigurationItem();
            }

            protected void succeeded() {
               JavaFxController.this.updateMachine();
            }

            protected void running() {
               JavaFxController.this.preferencesController.getPreferencesPanel().putConfigurationItemOnWait();
            }
         };
         this.syncExecutor.execute(groupMemberUpdater);
         Preferences jsonPreferences = this.appModel.getPreferences();
         UnitPreferencesPage unitPage = this.preferencesController.getPage(UnitPreferencesPage.class);
         ConfigurationPreferencesPage configurationPage = this.preferencesController.getPage(ConfigurationPreferencesPage.class);
         unitPage.reset();
         this.preferencesController.setChannelOrder(jsonPreferences.getChannelOrder());
         configurationPage.setShowDemoOptions(showDemo || this.appModel.isDemoMode());
         configurationPage.setTimeLapseEnabled(jsonPreferences.isTimeLapseEnabled());
         unitPage.setWeightUnit(jsonPreferences.getWeightUnit());
         unitPage.setTemperatureUnit(jsonPreferences.getPreferredUnit(Unit.CELSIUS));
         unitPage.setPressureUnit(jsonPreferences.getPreferredUnit(Unit.PASCAL));
         unitPage.setRoastValueUnit(jsonPreferences.getRoastValueUnit());
         unitPage.setRoastValue2Unit(jsonPreferences.getRoastValue2Unit());
         GeneralPreferencesPage generalPage = this.preferencesController.getPage(GeneralPreferencesPage.class);
         generalPage.setLocale(jsonPreferences.getLocale());
         ChartPreferencesPage chartPage = this.preferencesController.getPage(ChartPreferencesPage.class);
         InterfacePreferencesPage interfacePage = this.preferencesController.getPage(InterfacePreferencesPage.class);
         SmartPreferencesPage smartPage = this.preferencesController.getPage(SmartPreferencesPage.class);
         RorPreferencesPage rorPage = this.preferencesController.getPage(RorPreferencesPage.class);
         chartPage.setCommentingByTempEnabled(jsonPreferences.isCommentingByTemp());
         interfacePage.setTimerType(jsonPreferences.getTimerType());
         interfacePage.setStopRoastingConfirmationEnabled(jsonPreferences.isStopRoastingConfirmationEnabled());
         interfacePage.setSliderControlButtonsOnTop(jsonPreferences.isSliderControlButtonsOnTop());
         interfacePage.setCondensedChannelStatus(jsonPreferences.isCondensedChannelStatus());
         smartPage.setCurvePredictionEnabled(jsonPreferences.isCurvePredictionEnabled());
         smartPage.setFirstCrackEnabled(jsonPreferences.isFirstCrackPredictionEnabled());
         smartPage.setFlickPredictionEnabled(jsonPreferences.isFlickPredictionEnabled());
         smartPage.setSoundAlarmOnEventsEnabled(jsonPreferences.isSoundAlarmOnEventsEnabled());
         smartPage.setAlarmsToastNotificationEnabled(jsonPreferences.isAlarmsToastNotificationEnabled());
         smartPage.setAlarmSoundRepetitions(jsonPreferences.getAlarmsSoundRepetitions());
         smartPage.setAlarmSoundVolume(jsonPreferences.getAlarmsSoundVolume());
         interfacePage.setAutomaticallyReplaceLotEnabled(jsonPreferences.isAutomaticallyReplaceLotEnabled());
         interfacePage.setBetweenBatchProtocolEnabled(jsonPreferences.isBetweenBatchProtocolEnabled());
         rorPage.setRorSmoothing(jsonPreferences.getRorSmoothing());
         rorPage.setRor(jsonPreferences.getRor());
         Label rlsFormTitleLabel = interfacePage.getReplacementLotsSelectionForm().getTitleLabel();
         rlsFormTitleLabel.setContentDisplay(ContentDisplay.RIGHT);
         rlsFormTitleLabel.setGraphic(new NewBadge(this.appModel, new Version("5.13")));
         smartPage.setSmartGoalsAndReferencesEnabled(jsonPreferences.isSmartGoalsAndReferencesEnabled());
         smartPage.setHideLiveGoalCurrentValueEnabled(jsonPreferences.isHideLiveGoalCurrentValueEnabled());
         smartPage.setHideGoalStatisticsEnabled(jsonPreferences.isHideGoalStatisticsEnabled());
         smartPage.setSmartGoalsEnabled(
            jsonPreferences.isSmartGoalsAndReferencesEnabled() && this.permissionNotificationController.goalsModuleAvailableProperty().get()
         );
         this.updateMachine();
         smartPage.setReplayEnabled(jsonPreferences.isReplayEnabled());
         smartPage.setResetGasThreshold(jsonPreferences.getResetGasThreshold());
         chartPage.setEventAutoMarkingEnabled(jsonPreferences.isEventAutoMarking());
         smartPage.setAutoDetectionEnabled(jsonPreferences.isAutoDetectionEnabled());
         smartPage.setAutoStartEnabled(jsonPreferences.isAutoStartEnabled());
         smartPage.setStartThreshold((int)jsonPreferences.getStartThreshold());
         smartPage.setEndThreshold((int)jsonPreferences.getEndThreshold());
         chartPage.setChartXAxis(jsonPreferences.getTime());
         chartPage.setChartYAxisMin((int)jsonPreferences.getMinTemp());
         chartPage.setChartYAxisMax((int)jsonPreferences.getMaxTemp());
         chartPage.setChartRorMin((int)jsonPreferences.getMinRiseRate());
         chartPage.setChartRorMax((int)jsonPreferences.getMaxRiseRate());
         chartPage.setReferenceCommentHistoryHidden(jsonPreferences.isReferenceCommentHistoryHidden());
         chartPage.setGasCurveAsCommentsThreshold(jsonPreferences.getGasCurveAsCommentsThreshold());
         chartPage.setEnabledEventsPopup(jsonPreferences.getEnabledEventsPopup());
         chartPage.setEnabledEventsSidebar(jsonPreferences.getEnabledEventsSidebar());
         chartPage.setEnabledEventsChart(jsonPreferences.getEnabledEventsChart());
         chartPage.setPreFills(jsonPreferences.getPreFills());
         PrintPreferencesPage printPage = this.preferencesController.getPage(PrintPreferencesPage.class);
         printPage.setPrintLabelFontSize(jsonPreferences.getPrintFontSize());
         printPage.setPrintLabelQrCodePosition(jsonPreferences.getPrintQrCodePosition());
         printPage.setPrintLabelStyle(jsonPreferences.getPrintLabelStyle());
         printPage.setPrintTitleVisible(jsonPreferences.isPrintTitleVisible());
         printPage.setPrintDateFormatter(jsonPreferences.getPrintDateFormatter());
         printPage.setPrintLabelMapping(jsonPreferences.getPrintLabelMapping());
         generalPage.setResetHintsHandler(re -> this.appModel.getHints().clear());
         this.preferencesController.setSaveHandler(se -> this.savePreferences());
         this.preferencesController.setCancelHandler(ce -> this.preferencesWindow.hide());
         this.preferencesController.setDisablePartially(!JavaFxApplicationState.Phase.SETUP.equals(this.state.getPhase()));
         this.preferencesController.resetChangeLabels();
         if (WindowManager.get().register(this.preferencesWindow, "PREFERENCES_WINDOW_ID")) {
            WindowUtil.showSafeStage(this.preferencesWindow, 870.0, 700.0, 0.0, 0.0, 32.0);
         }

         this.shiftDown.set(false);
      }
   }

   private synchronized void updateMachine() {
      this.preferencesController.setMachines(this.appModel.getData().getMachines().values(), this.appModel.getData().getGroupMembers());
      this.preferencesController
         .setMachine(this.appModel.getSelectedMachine(), PermissionNotificationController.isRiPermissionGrantedForCurrentMachine(this.appModel));
      this.preferencesController.applyPreferences(this.appModel.getSelectedMachine(), this.appModel.getPreferences());
      this.preferencesController.getObservableConnectors().addAll(this.appModel.getUnconnectedConnectors());
      this.preferencesController.getPreferencesPanel().resetConfigurationItem();
   }

   private void showPreferences(ActionEvent e) {
      this.showPreferences();
   }

   private void showMachinePreferences() {
      if (this.appModel.getSelectedMachine() != null && PermissionNotificationController.isRiPermissionGrantedForCurrentMachine(this.appModel)) {
         this.preferencesController.selectPage(MeasurementPreferencesPage.class);
      } else {
         this.closeMachineInvalidToast();
         this.preferencesController.selectPage(ConfigurationPreferencesPage.class);
      }

      this.showPreferences();
   }

   private boolean isRestartRequired() {
      UnitPreferencesPage unitPage = this.preferencesController.getPage(UnitPreferencesPage.class);
      this.appModel.getPreferences();
      boolean unitsChanged = this.appModel.getPreferences().getTemperatureUnit() != unitPage.getTemperatureUnit()
         || this.appModel.getPreferences().getPressureUnit() != unitPage.getPressureUnit()
         || this.appModel.getPreferences().getWeightUnit() != unitPage.getWeightUnit();
      Long machineId = this.preferencesController.getMachine() != null ? this.preferencesController.getMachine().getId() : null;
      boolean machineChanged = !Objects.equals(machineId, this.appModel.getPreferences().getSelectedMachineId());
      boolean channelsChanged = this.preferencesController
         .getChannels()
         .stream()
         .filter(c -> Objects.nonNull(c.getSourceFactory()))
         .anyMatch(c -> this.isRestartRequired(c, this.appModel.getChannel(c.getName())));
      boolean statesChanged = !this.preferencesController
         .getStates()
         .stream()
         .filter(ControlStateConfiguration::isEnabled)
         .allMatch(
            csc -> this.measurementHub
                  .getMeasurementSources()
                  .stream()
                  .map(MeasurementSource::getFactory)
                  .anyMatch(f -> List.of(f.getSupportedStates(f.getParameters())).contains(csc.getState()))
         );
      return unitsChanged || machineChanged || channelsChanged || statesChanged;
   }

   private boolean isRestartRequired(Channel channel, Channel previousChannel) {
      if (channel.getSourceFactory() != null) {
         ConfigurationPreferencesPage configurationPage = this.preferencesController.getPage(ConfigurationPreferencesPage.class);
         MeasurementSourceFactory sourceFactory = RoastingIntelligenceApp.retrieveSingleton(channel.getSourceFactory());
         if (this.appModel.getPreferences().isTimeLapseEnabled() != configurationPage.isTimeLapseEnabled()) {
            return true;
         }

         if (previousChannel == null) {
            return true;
         }

         if (channel.getSourceFactory() != previousChannel.getSourceFactory()) {
            return true;
         }

         if (!this.preferencesController.getDeviceParameters(channel.getSourceFactory()).equals(sourceFactory.getParameters())) {
            return true;
         }
      }

      return false;
   }

   private void savePreferences() {
      this.appModel.setRestartRequired(this.isRestartRequired());
      Machine selectedMachine = this.appModel.getSelectedMachine();

      try {
         Set<Integer> hashCodes = new HashSet<>();
         Set<String> channelOrder = new LinkedHashSet<>();

         for (Channel channel : this.preferencesController.getChannels()) {
            channelOrder.add(channel.getName());
            if (channel.getSourceFactory() != null) {
               MeasurementSourceFactory sourceFactory = RoastingIntelligenceApp.retrieveSingleton(channel.getSourceFactory());
               if (sourceFactory.getChannelCount() != 0) {
                  int prime = 31;
                  int result = 1;
                  result = 31 * result + channel.getIndex();
                  result = 31 * result + channel.getSourceFactory().hashCode();
                  if (hashCodes.contains(result)) {
                     WindowUtil.showAlert(
                        AlertType.ERROR,
                        this.application.getStage(),
                        org.cropster.roastingintelligence.util.Messages.m_("Error saving preferences"),
                        org.cropster.roastingintelligence.util.Messages.m_(
                           "{0}: Duplicate use of channel {1} of {2}!",
                           org.cropster.roastingintelligence.util.Messages.m_(channel.getName()),
                           channel.getIndex(),
                           org.cropster.roastingintelligence.util.Messages.m_(channel.getSourceFactory().getSimpleName())
                        )
                     );
                     return;
                  }

                  hashCodes.add(result);
               }
            }
         }

         Set<MeasurementSourceFactory> alreadySetup = new HashSet<>();
         Map<String, Set<SourceParameter>> sourceConfigurations = new LinkedHashMap<>();

         for (Channel channelx : this.preferencesController.getChannels()) {
            if (channelx.getSourceFactory() != null) {
               MeasurementSourceFactory sourceFactory = RoastingIntelligenceApp.retrieveSingleton(channelx.getSourceFactory());
               Channel previousChannel = this.appModel.getChannel(channelx.getName());
               if (this.isRestartRequired(channelx, previousChannel)) {
                  if (!this.setup(sourceFactory)) {
                     return;
                  }

                  alreadySetup.add(sourceFactory);
               }

               sourceConfigurations.put(
                  sourceFactory.getClass().getSimpleName(),
                  this.preferencesController.getDeviceParameters((Class<? extends MeasurementSourceFactory>)sourceFactory.getClass())
               );
            }
         }

         for (ControlStateConfiguration stateConfiguration : this.preferencesController.getStates()) {
            ControlState controlState = stateConfiguration.getState();
            if (controlState.getSourceFactory() != null) {
               MeasurementSourceFactory sourceFactory = RoastingIntelligenceApp.retrieveSingleton(controlState.getSourceFactory());
               if (!alreadySetup.contains(sourceFactory)) {
                  if (!this.setup(sourceFactory)) {
                     return;
                  }

                  sourceConfigurations.put(
                     sourceFactory.getClass().getSimpleName(),
                     this.preferencesController.getDeviceParameters((Class<? extends MeasurementSourceFactory>)sourceFactory.getClass())
                  );
               }
            }
         }

         this.appModel.getPreferences().setSourceConfigurations(sourceConfigurations);
         if (!alreadySetup.isEmpty()) {
            this.publishConfigurationWarningsFromPreferences(alreadySetup);
         }

         if (this.preferencesController.getMachine() == null) {
            WindowUtil.showAlert(
               AlertType.ERROR,
               this.application.getStage(),
               org.cropster.roastingintelligence.util.Messages.m_("Error saving preferences"),
               org.cropster.roastingintelligence.util.Messages.m_("Please select a machine!")
            );
            return;
         }

         if (!this.preferencesController.getMachine().getId().equals(this.appModel.getPreferences().getSelectedMachineId())) {
            final Machine oldMachine = this.appModel.getSelectedMachine();
            this.appModel.setSelectedMachine(this.preferencesController.getMachine());
            ProgressTask<Void> syncTask = new ProgressTask<Void>(this.application.getStage()) {
               protected Void call() throws Exception {
                  JavaFxController.this.syncService.uploadRoastsBlocking();
                  JavaFxController.this.syncService.syncDataBlocking();
                  return null;
               }

               @Override
               protected void succeeded() {
                  JavaFxController.this.logger.info("Sync done");
                  JavaFxController.this.updateAppModel();
                  JavaFxController.this.closePreferences(JavaFxController.this.appModel.isRestartRequired());
               }

               @Override
               protected void cancelled() {
                  JavaFxController.this.logger.info("Syncing data cancelled");
               }

               @Override
               protected void failed() {
                  try {
                     this.get();
                     JavaFxController.this.logger.info("Sync done");
                  } catch (CancellationException var4) {
                     JavaFxController.this.logger.info("Syncing data cancelled");
                  } catch (Exception var5) {
                     JavaFxController.this.appModel.setSelectedMachine(oldMachine);
                     if (JavaFxController.this.logger.isInfoEnabled()) {
                        JavaFxController.this.logger.info("Error syncing data: {}", var5.getMessage());
                     } else if (JavaFxController.this.logger.isDebugEnabled()) {
                        JavaFxController.this.logger.debug("Error syncing data", var5);
                     }

                     Throwable cause = var5;

                     while (Objects.nonNull(cause.getCause())) {
                        cause = cause.getCause();
                     }

                     if (cause instanceof IOException) {
                        Class<?> clazz = cause.getClass();

                        while (!clazz.getSuperclass().equals(IOException.class)) {
                           clazz = clazz.getSuperclass();
                        }

                        if (clazz.getPackage().equals(SocketTimeoutException.class.getPackage())) {
                           Platform.runLater(
                              () -> WindowUtil.showAlert(
                                    AlertType.ERROR,
                                    JavaFxController.this.application.getStage(),
                                    org.cropster.roastingintelligence.util.Messages.m_("Error saving preferences"),
                                    org.cropster.roastingintelligence.util.Messages.m_(
                                       "An active internet connection is required to change the machine preferences."
                                    )
                                 )
                           );
                        } else {
                           Platform.runLater(
                              () -> WindowUtil.showAlert(
                                    AlertType.ERROR,
                                    JavaFxController.this.application.getStage(),
                                    org.cropster.roastingintelligence.util.Messages.m_("Error saving preferences"),
                                    org.cropster.roastingintelligence.util.Messages.m_("A corrupted roast file cannot be read!")
                                 )
                           );
                        }

                        return;
                     }

                     if (cause instanceof WebApplicationException) {
                        switch (((WebApplicationException)cause).getResponse().getStatus()) {
                           case 403:
                              Platform.runLater(
                                 () -> WindowUtil.showAlert(
                                       AlertType.ERROR,
                                       JavaFxController.this.application.getStage(),
                                       org.cropster.roastingintelligence.util.Messages.m_("Error saving preferences"),
                                       org.cropster.roastingintelligence.util.Messages.m_("Access to group denied! Missing or invalid permissions.")
                                    )
                              );
                              return;
                           case 500:
                        }
                     }

                     Platform.runLater(
                        () -> WindowUtil.showAlert(
                              AlertType.ERROR,
                              JavaFxController.this.application.getStage(),
                              org.cropster.roastingintelligence.util.Messages.m_("Error saving preferences"),
                              org.cropster.roastingintelligence.util.Messages.m_("Unexpected error occurred! Contact support for help.")
                           )
                     );
                  }
               }
            };
            this.roastingManager.getExecutor().execute(syncTask);
         } else {
            this.updateAppModel();
            this.closePreferences(this.appModel.isRestartRequired());
         }
      } catch (Exception var10) {
         this.appModel.setSelectedMachine(selectedMachine);
         this.logger.error("Error saving preferences", var10);
         WindowUtil.showAlert(
            AlertType.ERROR,
            this.application.getStage(),
            org.cropster.roastingintelligence.util.Messages.m_("Error"),
            org.cropster.roastingintelligence.util.Messages.m_("Error saving preferences") + ": " + var10.getLocalizedMessage()
         );
      }
   }

   private boolean setup(MeasurementSourceFactory sourceFactory) {
      try {
         sourceFactory.setup(this.preferencesController.getDeviceParameters((Class<? extends MeasurementSourceFactory>)sourceFactory.getClass()));
         return true;
      } catch (LinkageError var3) {
         WindowUtil.showSetupAlert(this.application.getStage(), var3);
         return false;
      } catch (UnknownHostException var4) {
         WindowUtil.showSetupAlert(this.application.getStage(), (Class<? extends MeasurementSourceFactory>)sourceFactory.getClass(), var4);
         return false;
      } catch (Exception var5) {
         WindowUtil.showSetupAlert(this.application.getStage(), (Class<? extends MeasurementSourceFactory>)sourceFactory.getClass(), var5);
         return false;
      }
   }

   private void publishConfigurationWarningsFromPreferences(Set<MeasurementSourceFactory> sourceFactories) {
      for (MeasurementSourceFactory sourceFactory : sourceFactories) {
         if (sourceFactory != null) {
            Set<Channel> factoryChannels = this.preferencesController
               .getChannels()
               .stream()
               .filter(c -> sourceFactory.getClass().equals(c.getSourceFactory()))
               .collect(Collectors.toSet());
            RoastingIntelligenceApp.publishConfigurationWarnings(this.notificationBusService, sourceFactory.getConfigurationWarnings(factoryChannels));
         }
      }
   }

   private void updateAppModel() {
      Preferences jsonPreferences = this.appModel.getPreferences();
      ChartPreferencesPage chartPage = this.preferencesController.getPage(ChartPreferencesPage.class);
      if (!JavaFxApplicationState.Phase.ROASTING.equals(this.state.getPhase())) {
         UnitPreferencesPage unitPage = this.preferencesController.getPage(UnitPreferencesPage.class);
         if (!unitPage.getTemperatureUnit().equals(this.appModel.getPreferences().getPreferredUnit(Unit.CELSIUS))) {
            this.appModel.setSyncTime(0L);
         }

         this.appModel.getUnconnectedConnectors().clear();
         this.appModel.getUnconnectedConnectors().addAll(this.preferencesController.getUnconnectedConnectors());
         ConfigurationPreferencesPage configurationPage = this.preferencesController.getPage(ConfigurationPreferencesPage.class);
         this.appModel.mergeChannelOrder(this.preferencesController.getChannelOrder());
         GeneralPreferencesPage generalPage = this.preferencesController.getPage(GeneralPreferencesPage.class);
         Locale locale = generalPage.getLocale();
         jsonPreferences.setLocale(locale);
         if (locale != null) {
            this.preferences.getPreferences().put("locale", locale.toString());
         } else {
            this.preferences.getPreferences().remove("locale");
         }

         jsonPreferences.setPreferredUnit(unitPage.getTemperatureUnit());
         jsonPreferences.setPreferredUnit(unitPage.getPressureUnit());
         jsonPreferences.setWeightUnit(unitPage.getWeightUnit());
         jsonPreferences.setRoastValueUnit(unitPage.getRoastValueUnit());
         jsonPreferences.setRoastValue2Unit(unitPage.getRoastValue2Unit());
         InterfacePreferencesPage interfacePage = this.preferencesController.getPage(InterfacePreferencesPage.class);
         RorPreferencesPage rorPage = this.preferencesController.getPage(RorPreferencesPage.class);
         SmartPreferencesPage smartPage = this.preferencesController.getPage(SmartPreferencesPage.class);
         jsonPreferences.setCommentingByTemp(chartPage.isCommentingByTempEnabled());
         jsonPreferences.setTimerType(interfacePage.getTimerType());
         jsonPreferences.setStopRoastingConfirmationEnabled(interfacePage.isStopRoastingConfirmationEnabled());
         jsonPreferences.setSliderControlButtonsOnTop(interfacePage.isSliderControlButtonsOnTop());
         jsonPreferences.setCondensedChannelStatus(interfacePage.isCondensedChannelStatus());
         jsonPreferences.setCurvePredictionEnabled(smartPage.isCurvePredictionEnabled());
         jsonPreferences.setFirstCrackPredictionEnabled(smartPage.isFirstCrackPredictionEnabled());
         jsonPreferences.setFlickPredictionEnabled(smartPage.isFlickPredictionEnabled());
         jsonPreferences.setSoundAlarmOnEventsEnabled(smartPage.isSoundAlarmOnEventsEnabled());
         jsonPreferences.setAlarmsToastNotificationEnabled(smartPage.isAlarmsToastNotificationEnabled());
         jsonPreferences.setAlarmsSoundRepetitions(smartPage.getAlarmSoundRepetitions());
         jsonPreferences.setAlarmsSoundVolume(smartPage.getAlarmSoundVolume());
         SoundManager.get().setRepetitions(jsonPreferences.getAlarmsSoundRepetitions());
         SoundManager.get().setVolume(jsonPreferences.getAlarmsSoundVolume());
         jsonPreferences.setAutomaticallyReplaceLotEnabled(interfacePage.isAutomaticallyReplaceLotEnabled());
         jsonPreferences.setBetweenBatchProtocolEnabled(interfacePage.isBetweenBatchProtocolEnabled());
         jsonPreferences.setRorSmoothing(rorPage.getRorSmoothing());
         jsonPreferences.setRor(rorPage.getRor());
         jsonPreferences.setSmartGoalsAndReferencesEnabled(smartPage.isSmartGoalsAndReferencesEnabled());
         jsonPreferences.setHideLiveGoalCurrentValueEnabled(smartPage.isHideLiveGoalCurrentValueEnabled());
         jsonPreferences.setHideGoalStatisticsEnabled(smartPage.isHideGoalStatisticsEnabled());
         jsonPreferences.setReplayEnabled(smartPage.isReplayEnabled());
         jsonPreferences.setResetGasThreshold(smartPage.getResetGasThreshold());
         jsonPreferences.setEventAutoMarking(chartPage.isEventAutoMarkingEnabled());
         jsonPreferences.setAutoDetectionEnabled(smartPage.isAutoDetectionEnabled());
         jsonPreferences.setAutoStartEnabled(smartPage.isAutoStartEnabled());
         jsonPreferences.setStartThreshold((double)smartPage.getStartThreshold());
         jsonPreferences.setEndThreshold((double)smartPage.getEndThreshold());
         this.appModel.updateAutoStartMode();
         jsonPreferences.setReferenceCommentHistoryHidden(chartPage.isReferenceCommentHistoryHidden());
         jsonPreferences.setGasCurveAsCommentsThreshold(chartPage.getGasCurveAsCommentsThreshold());
         jsonPreferences.setEnabledEventsChart(chartPage.getEnabledEventsChart());
         jsonPreferences.setEnabledEventsSidebar(chartPage.getEnabledEventsSidebar());
         jsonPreferences.setEnabledEventsPopup(chartPage.getEnabledEventsPopup());
         jsonPreferences.setPreFill("gas", chartPage.getPreFills().contains("gas"));
         jsonPreferences.setPreFill("airflow", chartPage.getPreFills().contains("airflow"));
         jsonPreferences.setTimeLapseEnabled(configurationPage.isTimeLapseEnabled());
         jsonPreferences.setChannels(this.preferencesController.getChannels());
         jsonPreferences.setStates(this.preferencesController.getStates().stream().filter(ControlStateConfiguration::isEnabled).toList());
         PrintPreferencesPage printPage = this.preferencesController.getPage(PrintPreferencesPage.class);
         jsonPreferences.setPrintFontSize(printPage.getPrintFontSize());
         jsonPreferences.setPrintQrCodePosition(printPage.getPrintLabelQrCodePosition());
         jsonPreferences.setPrintLabelStyle(printPage.getPrintLabelStyle());
         jsonPreferences.setPrintTitleVisible(printPage.isPrintTitleVisible());
         jsonPreferences.setPrintDateFormatter(printPage.getPrintDateFormatter());
         jsonPreferences.setPrintLabelMapping(printPage.getPrintLabelMapping());
      }

      jsonPreferences.setTime(chartPage.getChartXAxis());
      jsonPreferences.setMinTemp((double)chartPage.getChartYAxisMin());
      jsonPreferences.setMaxTemp((double)chartPage.getChartYAxisMax());
      jsonPreferences.setMinRiseRate((double)chartPage.getChartRorMin());
      jsonPreferences.setMaxRiseRate((double)chartPage.getChartRorMax());

      try {
         this.logger.info("Saving preferences");
         StorageService storageService = RoastingIntelligenceApp.retrieveSingleton(StorageService.class);
         storageService.savePreferences(this.appModel.getPreferences());
         this.syncService.uploadRoasts();
      } catch (IOException var11) {
         this.logger.error("Error storing preferences", var11);
      }
   }

   private void closePreferences(boolean restartRequired) {
      assert Platform.isFxApplicationThread();

      if (restartRequired) {
         this.stopTimeline();
         WindowUtil.showAlert(
            AlertType.WARNING,
            this.application.getStage(),
            org.cropster.roastingintelligence.util.Messages.m_("Restart application"),
            org.cropster.roastingintelligence.util.Messages.m_(
               "Some of the changes will only be applied after a restart of the Roasting intelligence application. The application is closed now."
            )
         );
         this.preferencesWindow.hide();
         this.close();
      } else {
         this.preferencesWindow.hide();
         this.appModel.fireSettingsChanged();
      }
   }

   private MenuBar createMenuBar() {
      MenuBar menuBar = new MenuBar();
      menuBar.setUseSystemMenuBar(SystemUtils.IS_OS_MAC);
      Menu menuAccount = new Menu(org.cropster.roastingintelligence.util.Messages.m_("_Account"));
      this.addMenuItem(menuAccount, org.cropster.roastingintelligence.util.Messages.m_("_Synchronize"), KeyboardShortcut.CTRL_S, e -> this.syncAll())
         .disableProperty()
         .bind(this.state.roastInProgressProperty());
      this.addMenuItem(
         menuAccount, org.cropster.roastingintelligence.util.Messages.m_("_Preferences"), KeyboardShortcut.CTRL_COMMA, e -> this.showPreferences(e)
      );
      this.addMenuItem(menuAccount, org.cropster.roastingintelligence.util.Messages.m_("_Logout"), e -> this.logout());
      if (!SystemUtils.IS_OS_MAC) {
         this.addMenuItem(menuAccount, org.cropster.roastingintelligence.util.Messages.m_("_Exit"), e -> this.close());
      }

      menuBar.getMenus().addAll(new Menu[]{menuAccount});
      if (this.application.getHostServices() != null) {
         Menu menuLinks = new Menu(org.cropster.roastingintelligence.util.Messages.m_("_Links"));

         for (int i = 0; i < Defaults.LINK_URLS.length; i++) {
            URI uri = this.appModel.getUri().resolve(Defaults.LINK_URLS[i]);
            this.addMenuItem(menuLinks, Defaults.LINK_LABELS[i], e -> this.application.getHostServices().showDocument(uri.toString()));
         }

         menuBar.getMenus().add(menuLinks);
      }

      Menu menuHelp = new Menu(org.cropster.roastingintelligence.util.Messages.m_("_Help"));
      this.addMenuItem(
         menuHelp, org.cropster.roastingintelligence.util.Messages.m_("Keyboard shortcuts"), KeyboardShortcut.SHIFT_CTRL_COMMA, this::showKeyboardShortcuts
      );
      this.addMenuSeparator(menuHelp);
      this.addMenuItem(menuHelp, org.cropster.roastingintelligence.util.Messages.m_("Export log files"), KeyboardShortcut.CTRL_E, e -> this.exportFiles());
      this.addMenuSeparator(menuHelp);
      this.addMenuItem(
         menuHelp,
         org.cropster.roastingintelligence.util.Messages.m_("Online help"),
         KeyboardShortcut.F1,
         e -> this.application.getHostServices().showDocument("https://help.cropster.com")
      );
      this.addMenuItem(menuHelp, org.cropster.roastingintelligence.util.Messages.m_("Interface Tour"), this::showInterfaceTour)
         .disableProperty()
         .bind(Bindings.createBooleanBinding(() -> this.state.getPhase() != JavaFxApplicationState.Phase.SETUP, new Observable[]{this.state.phaseProperty()}));
      this.addMenuItem(menuHelp, org.cropster.roastingintelligence.util.Messages.m_("What's New?"), this::showWhatsNew)
         .disableProperty()
         .bind(Bindings.createBooleanBinding(() -> this.state.getPhase() != JavaFxApplicationState.Phase.SETUP, new Observable[]{this.state.phaseProperty()}));
      this.addMenuSeparator(menuHelp);
      this.addMenuItem(
         menuHelp,
         org.cropster.roastingintelligence.util.Messages.m_("Release notes"),
         e -> this.application.getHostServices().showDocument("https://www.cropster.com/product-updates/ri-release-notes/")
      );
      menuBar.getMenus().add(menuHelp);
      return menuBar;
   }

   private Optional<RoastingComment> commentForMeasure(String measure) {
      Channel channel = this.roastingModel.getChannel(measure);
      if (channel != null) {
         CurveModel curveModel = this.roastingModel.getCurveModel(channel);
         if (curveModel != null) {
            long time = this.roastingModel.getDuration();
            return Optional.of(new RoastingComment(time, measure, CommentPopupContent.NUMBER_FORMAT.format(curveModel.getValue(time))));
         }
      }

      return Optional.empty();
   }

   private void setupKeyboardShortcuts(Scene scene) {
      scene.addEventFilter(
         KeyEvent.KEY_PRESSED,
         e -> {
            if (this.permissionNotificationController.riPermissionGrantedProperty().get()) {
               if (this.state.getRoastInProgress()) {
                  if (KeyboardShortcut.CTRL_ESC.match(e)) {
                     this.abortRoasting();
                     e.consume();
                  } else if (this.state.getPhase().equals(JavaFxApplicationState.Phase.DETECTING)) {
                     if (KeyboardShortcut.CTRL_ENTER.match(e)) {
                        this.forceStart();
                        e.consume();
                     }
                  } else if (this.state.getPhase().equals(JavaFxApplicationState.Phase.ROASTING)) {
                     if (KeyboardShortcut.SHIFT_ENTER.match(e)) {
                        this.mainPanelController.getPrimaryButton().fire();
                        e.consume();
                     } else if (KeyboardShortcut.CTRL_1.match(e)) {
                        this.roastingModel.addComment(new RoastingComment(this.roastingModel.getDuration(), "firstCrack", null));
                     } else if (KeyboardShortcut.CTRL_2.match(e)) {
                        this.roastingModel.addComment(new RoastingComment(this.roastingModel.getDuration(), "secondCrack", null));
                     } else if (KeyboardShortcut.CTRL_3.match(e)) {
                        this.roastingModel.addComment(new RoastingComment(this.roastingModel.getDuration(), "colorChange", null));
                     } else if (KeyboardShortcut.CTRL_4.match(e)) {
                        this.commentForMeasure("gas").ifPresentOrElse(c -> this.roastingModel.addComment(c), Toolkit.getDefaultToolkit()::beep);
                     } else if (KeyboardShortcut.CTRL_5.match(e)) {
                        this.commentForMeasure("airflow").ifPresentOrElse(c -> this.roastingModel.addComment(c), Toolkit.getDefaultToolkit()::beep);
                     }
                  } else if (this.state.getPhase().equals(JavaFxApplicationState.Phase.STOPPED) && KeyboardShortcut.SHIFT_CTRL_ENTER.match(e)) {
                     this.mainPanelController.getPrimaryButton().requestFocus();
                     this.finishRoasting();
                     e.consume();
                  }

                  if (this.state.isReplayStarted() && KeyboardShortcut.SHIFT_CTRL_A.match(e)) {
                     this.mainPanelController.getReplaySwitch().toggle();
                     e.consume();
                  }
               } else if (KeyboardShortcut.CTRL_ENTER.match(e) && this.state.getPhase().equals(JavaFxApplicationState.Phase.SETUP)) {
                  this.mainPanelController.getPrimaryButton().requestFocus();
                  this.mainPanelController.getPrimaryButton().fire();
                  e.consume();
               }

               if (KeyboardShortcut.ESC.match(e) && SoundManager.get().isPlaying()) {
                  String soundId = SoundManager.get().getCurrentlyPlayingIdentifier();
                  SoundManager.get().stop();
                  if (soundId != null) {
                     this.alarmController
                        .getTriggeredAlarms()
                        .stream()
                        .filter(holder -> soundId.equals(holder.getIdentifier()))
                        .findFirst()
                        .ifPresent(holder -> holder.clearVisualFeedback());
                  }

                  e.consume();
               } else if (KeyboardShortcut.CTRL_P.match(e)) {
                  this.mainPanelController.getProductionPanel().getPrintSelectedRoastLabelsAction().handle(null);
               } else if (KeyboardShortcut.CTRL_R.match(e)) {
                  this.resetChartAxes();
                  e.consume();
               } else if (KeyboardShortcut.CTRL_ARROW_LEFT.match(e)) {
                  this.mainPanelController.getControlToggleBar().togglePrevious();
                  e.consume();
               } else if (KeyboardShortcut.CTRL_ARROW_RIGHT.match(e)) {
                  this.mainPanelController.getControlToggleBar().toggleNext();
                  e.consume();
               }

               if (!SystemUtils.IS_OS_MAC && KeyboardShortcut.F11.match(e) || SystemUtils.IS_OS_MAC && KeyboardShortcut.CTRL_F11.match(e)) {
                  this.application.getStage().setFullScreen(!this.application.getStage().isFullScreen());
               }

               if (!this.mainPanelController.getGasControl().isVisible() || !KeyboardShortcut.ADD.match(e) && !KeyboardShortcut.ARROW_UP.match(e)) {
                  if (this.mainPanelController.getGasControl().isVisible() && (KeyboardShortcut.SUBTRACT.match(e) || KeyboardShortcut.ARROW_DOWN.match(e))) {
                     if (this.logger.isDebugEnabled()) {
                        this.logger.debug("Decreasing gas manually by shortcut {}", e.getCode());
                     }

                     e.consume();
                     this.mainPanelController.getGasControl().dec();
                  }
               } else {
                  if (this.logger.isDebugEnabled()) {
                     this.logger.debug("Increasing gas manually by shortcut {}", e.getCode());
                  }

                  e.consume();
                  this.mainPanelController.getGasControl().inc();
               }
            }
         }
      );
   }

   private void showRoastInfo(Roast roast) {
      this.roastInfoPanel.init(this.appModel.getData(), roast);
      this.roastInfoWindow.setTitle(roast.getLot().getIdTag());
      this.roastInfoWindow.toFront();
      if (!this.roastInfoWindow.isShowing()) {
         Runnable sizeSetter = () -> {
            this.roastInfoWindow.setMinWidth(600.0);
            this.roastInfoWindow.setMinHeight(300.0);
            this.roastInfoWindow.setMaxWidth(800.0);
            this.roastInfoWindow.setMaxHeight(600.0);
         };
         if (!SystemUtils.IS_OS_WINDOWS) {
            sizeSetter.run();
         }

         this.roastInfoWindow.show();
         if (SystemUtils.IS_OS_WINDOWS) {
            Platform.runLater(sizeSetter);
         }
      }
   }

   public void showKeyboardShortcuts() {
      assert Platform.isFxApplicationThread();

      if (this.keyboardShortcutsWindow == null) {
         Map<String, Collection<KeyboardShortcut>> shortcuts = new LinkedHashMap<>();
         shortcuts.put(
            org.cropster.roastingintelligence.util.Messages.m_("General"),
            Arrays.asList(
               KeyboardShortcut.ESC,
               KeyboardShortcut.F1,
               SystemUtils.IS_OS_MAC ? KeyboardShortcut.CTRL_F11 : KeyboardShortcut.F11,
               KeyboardShortcut.CTRL_E,
               KeyboardShortcut.CTRL_P,
               KeyboardShortcut.CTRL_S,
               KeyboardShortcut.CTRL_COMMA,
               KeyboardShortcut.SHIFT_CTRL_COMMA
            )
         );
         shortcuts.put(
            org.cropster.roastingintelligence.util.Messages.m_("Roasting"),
            Arrays.asList(
               KeyboardShortcut.CTRL_ENTER,
               KeyboardShortcut.SHIFT_ENTER,
               KeyboardShortcut.SHIFT_CTRL_ENTER,
               KeyboardShortcut.CTRL_ESC,
               KeyboardShortcut.SHIFT_CTRL_A
            )
         );
         shortcuts.put(
            org.cropster.roastingintelligence.util.Messages.m_("Slider controls"),
            Arrays.asList(
               KeyboardShortcut.ADD,
               KeyboardShortcut.SUBTRACT,
               KeyboardShortcut.ARROW_UP,
               KeyboardShortcut.ARROW_DOWN,
               KeyboardShortcut.CTRL_ARROW_LEFT,
               KeyboardShortcut.CTRL_ARROW_RIGHT,
               KeyboardShortcut.CTRL_WHEEL_SLIDER
            )
         );
         shortcuts.put(org.cropster.roastingintelligence.util.Messages.m_("Charts"), Arrays.asList(KeyboardShortcut.CTRL_R, KeyboardShortcut.CTRL_WHEEL_CHART));
         shortcuts.put(
            org.cropster.roastingintelligence.util.Messages.m_("Comments"),
            Arrays.asList(KeyboardShortcut.CTRL_1, KeyboardShortcut.CTRL_2, KeyboardShortcut.CTRL_3, KeyboardShortcut.CTRL_4, KeyboardShortcut.CTRL_5)
         );
         VBox shortcutList = new VBox();
         shortcutList.getStyleClass().add("keyboard-shortcut-list");

         for (Entry<String, Collection<KeyboardShortcut>> e : shortcuts.entrySet()) {
            Node headingLabel = new Label(e.getKey());
            headingLabel.getStyleClass().add("keyboard-shortcut-heading");
            shortcutList.getChildren().add(headingLabel);

            for (KeyboardShortcut shortcut : e.getValue()) {
               shortcutList.getChildren().add(new KeyCodeCombinationLabel(shortcut));
            }
         }

         ScrollPane shortcutsContainer = new ScrollPane(shortcutList);
         shortcutsContainer.getStyleClass().add("keyboard-shortcut-container");
         this.keyboardShortcutsWindow = new ButtonBarWindow()
            .withButton(org.cropster.roastingintelligence.util.Messages.m_("Close"), true, true, ex -> this.keyboardShortcutsWindow.close());
         this.keyboardShortcutsWindow.initOwner(this.application.getStage());
         this.keyboardShortcutsWindow.setMinWidth(520.0);
         this.keyboardShortcutsWindow.setMaxHeight(800.0);
         this.keyboardShortcutsWindow.setTitle(org.cropster.roastingintelligence.util.Messages.m_("Keyboard shortcuts"));
         this.keyboardShortcutsWindow.getRoot().setCenter(shortcutsContainer);
      }

      if (WindowManager.get().register(this.keyboardShortcutsWindow, "KEYBOARD_SHORTCUTS_WINDOW_ID")) {
         WindowUtil.showSafeStage(this.keyboardShortcutsWindow, 520.0, 600.0, 0.0, 1.0, 32.0);
      }
   }

   public void showKeyboardShortcuts(ActionEvent e) {
      this.showKeyboardShortcuts();
   }

   public void showInterfaceTour(ActionEvent e) {
      Tours.onboardingTour(this.appModel.getPreferences()).show(this.mainPanel);
   }

   public void showWhatsNew(ActionEvent e) {
      WhatsNewDialog whatsNewDialog = WhatsNewDialog.builder(
            () -> this.preferences.getPreferences().getInt("whatsNewIdentifier", -1), i -> this.preferences.getPreferences().putInt("whatsNewIdentifier", i)
         )
         .withTitle("Roasting Intelligence 5.0: What’s new?")
         .centered(true)
         .build()
         .lookupPages("com.cropster.roastingintelligence.ui.whatsnew.pages");
      if (WindowManager.get().register(whatsNewDialog, "WHATS_NEW_WINDOW_ID")) {
         whatsNewDialog.showAt(this.application.getStage().getScene());
      }
   }

   private void previewRoast(Map<String, RoastLabel.RoastLabelData> data, Integer baseFontSize, boolean printTitleVisible) {
      boolean portraitMode = PageOrientation.PORTRAIT.equals(this.printWindowController.getOrientation());
      double pageWidth = portraitMode ? this.printWindowController.getWidth().doubleValue() : this.printWindowController.getHeight().doubleValue();
      double pageHeight = portraitMode ? this.printWindowController.getHeight().doubleValue() : this.printWindowController.getWidth().doubleValue();
      QrCodeRenderer qrCodeRenderer = new QrCodeRenderer(100, 100, 0.2 + (double)(baseFontSize - 6) * 0.01);
      RoastLabel roastLabel = new RoastLabel(data, qrCodeRenderer, printTitleVisible);
      roastLabel.setPrefWidth(pageWidth);
      roastLabel.setPrefHeight(pageHeight);
      if (PageOrientation.REVERSE_LANDSCAPE.equals(this.printWindowController.getOrientation())) {
         roastLabel.getTransforms().add(Rotate.rotate(180.0, pageWidth / 2.0, pageHeight / 2.0));
      }

      Group group = new Group(new Node[]{roastLabel});
      StackPane rootPane = new StackPane();
      rootPane.getChildren().add(group);
      Scene scene = new Scene(rootPane, pageWidth, pageHeight);
      double aspectRatio = pageWidth / pageHeight;
      scene.widthProperty().addListener((s, o, n) -> {
         double sw = scene.getWidth();
         double ghx = group.getBoundsInLocal().getHeight();
         double syx = sw / aspectRatio / pageHeight;
         if (ghx * syx <= scene.getHeight()) {
            group.setScaleX(sw / pageWidth);
            group.setScaleY(syx);
         }
      });
      scene.heightProperty().addListener((s, o, n) -> {
         double sh = scene.getHeight();
         double gw = group.getBoundsInLocal().getWidth();
         double sx = sh * aspectRatio / pageWidth;
         if (gw * sx <= scene.getWidth()) {
            group.setScaleX(sx);
            group.setScaleY(sh / pageHeight);
         }
      });
      Stage window = new Stage();
      window.initOwner(this.application.getStage());
      window.initModality(Modality.APPLICATION_MODAL);
      window.setTitle(org.cropster.roastingintelligence.util.Messages.m_("Print preview"));
      window.setScene(scene);
      window.getScene().getStylesheets().add(STYLE_SHEET);
      window.getScene().getRoot().setStyle(String.format("-fx-font-size: %spt", baseFontSize.doubleValue() * 0.29));
      window.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
         if (e.getCode() == KeyCode.ESCAPE) {
            window.close();
            e.consume();
         }
      });
      window.sizeToScene();
      window.show();
      Rectangle2D screenBounds = WindowUtil.getScreenBounds(window);
      double possibleWidth = screenBounds.getWidth() * 0.7;
      double possibleHeight = screenBounds.getHeight() * 0.7;
      double windowBorderW = window.getWidth() - scene.getWidth();
      double windowBorderH = window.getHeight() - scene.getHeight();
      double ww = possibleWidth - windowBorderW;
      double wh = possibleHeight - windowBorderH;
      double gsx = 1.0;
      double gsy = 1.0;
      double gh = group.getBoundsInLocal().getHeight();
      double sy = ww / aspectRatio / pageHeight;
      if (gh * sy + windowBorderH <= possibleHeight) {
         gsx = ww / pageWidth;
         gsy = sy;
         wh = gh * sy;
      } else {
         double gw = group.getBoundsInLocal().getWidth();
         double sx = wh * aspectRatio / pageWidth;
         if (gw * sx + windowBorderW <= possibleWidth) {
            gsx = sx;
            gsy = wh / pageHeight;
            ww = gw * sx;
         }
      }

      if (gsx == 1.0 && gsy == 1.0) {
         window.setWidth(200.0 + windowBorderW);
         window.setHeight(200.0 / aspectRatio + windowBorderH);
         window.centerOnScreen();
         PlatformScheduler.schedule(() -> {
            group.setScaleX(200.0 / pageWidth);
            group.setScaleY(200.0 / aspectRatio / pageHeight);
         }, 333, TimeUnit.MILLISECONDS);
      } else {
         window.setWidth(ww + windowBorderW);
         window.setHeight(wh + windowBorderH);
         window.centerOnScreen();
         double finalGsx = gsx;
         double finalGsy = gsy;
         PlatformScheduler.schedule(() -> {
            group.setScaleX(finalGsx);
            group.setScaleY(finalGsy);
         }, 333, TimeUnit.MILLISECONDS);
      }
   }

   private void printRegions(PrinterJob printerJob, Region... regions) {
      this.roastingManager
         .getExecutor()
         .submit(
            () -> {
               for (Region region : regions) {
                  if (printerJob.printPage(region)) {
                     PrintResolution resolution = printerJob.getJobSettings().getPrintResolution();
                     PageLayout pageLayout = printerJob.getJobSettings().getPageLayout();
                     this.logger
                        .info(
                           "Printing {} {} x {} to {} {} x {} w/ print res {} x {}",
                           new Object[]{
                              region.getClass().getSimpleName(),
                              region.getWidth(),
                              region.getHeight(),
                              pageLayout.getPaper(),
                              pageLayout.getPrintableWidth(),
                              pageLayout.getPrintableHeight(),
                              resolution.getFeedResolution(),
                              resolution.getCrossFeedResolution()
                           }
                        );
                  }
               }

               if (printerJob.endJob()) {
                  this.logger.info("Printing done");
               }
            }
         );
   }

   private void printRoasts(AppModel model, Roast... roasts) {
      Platform.runLater(
         () -> {
            PrinterJob printerJob = PrinterJob.createPrinterJob();
            String jobName = org.cropster.roastingintelligence.util.Messages.m_("Roast") + " " + roasts[0].getLot().getIdTag();
            if (roasts.length > 1) {
               jobName = jobName + " + " + (roasts.length - 1);
            }

            printerJob.getJobSettings().setJobName(jobName);
            printerJob.getJobSettings().setPageRanges(new PageRange[]{new PageRange(1, roasts.length)});
            Paper paper = PrintHelper.createPaper(
               "CustomLabel",
               this.printWindowController.getWidth().doubleValue(),
               this.printWindowController.getHeight().doubleValue(),
               JavaFxUtil.toPrintUnit(this.printWindowController.getUnit())
            );
            PageLayout pageLayout = printerJob.getPrinter().createPageLayout(paper, this.printWindowController.getOrientation(), 0.0, 0.0, 0.0, 0.0);
            printerJob.getJobSettings().setPageLayout(pageLayout);
            if (printerJob.showPrintDialog(this.application.getStage())) {
               Preferences prefs = model.getPreferences();
               List<RoastLabel> labels = new ArrayList<>();
               pageLayout = printerJob.getJobSettings().getPageLayout();

               for (PageRange range : printerJob.getJobSettings().getPageRanges()) {
                  for (int i = range.getStartPage() - 1; i < range.getEndPage(); i++) {
                     Roast roast = roasts[i];
                     RoastLabel roastLabel = new RoastLabel(
                        RoastLabel.toData(model, roast, prefs.getPrintDateFormatter()),
                        new QrCodeRenderer(prefs.getPrintFontSize() * 30, prefs.getPrintFontSize() * 30, 0.25),
                        prefs.isPrintTitleVisible()
                     );
                     roastLabel.setPrefWidth(pageLayout.getPrintableWidth());
                     roastLabel.setPrefHeight(pageLayout.getPrintableHeight());
                     Scene scene = new Scene(new AnchorPane(new Node[]{roastLabel}), pageLayout.getPrintableWidth(), pageLayout.getPrintableHeight());
                     scene.getStylesheets().add(STYLE_SHEET);
                     scene.getRoot().setStyle(String.format("-fx-font-size: %spt", prefs.getPrintFontSize()));
                     Stage stage = new Stage();
                     stage.setScene(scene);
                     stage.show();
                     stage.hide();
                     labels.add(roastLabel);
                  }
               }

               this.printRegions(printerJob, labels.toArray(new Region[labels.size()]));
            }
         }
      );
   }

   private void printRoastsCustom(Roast... roasts) {
      this.printRoastsCustom(this.appModel, roasts);
   }

   private void printRoastsCustom(AppModel model, Roast... roasts) {
      assert Platform.isFxApplicationThread();

      this.printWindowController.init(Printer.getAllPrinters());
      PrinterJob defaultPrinterJob = null;
      String orientation = this.preferences.getPreferences().get("PrinterJob.orientation", PageOrientation.PORTRAIT.name());
      this.printWindowController.setOrientation(PageOrientation.valueOf(orientation));
      String printerName = this.preferences.getPreferences().get("PrinterJob.printer", null);
      Printer defaultPrinter = Printer.getAllPrinters().stream().filter(p -> p.getName().equals(printerName)).findFirst().orElse(null);
      if (defaultPrinter == null) {
         defaultPrinter = Printer.getDefaultPrinter();
      }

      if (defaultPrinter != null) {
         defaultPrinterJob = PrinterJob.createPrinterJob(defaultPrinter);
         this.printWindowController.setSelectedPrinter(defaultPrinter);
      }

      if (defaultPrinterJob == null) {
         defaultPrinterJob = PrinterJob.createPrinterJob();
         if (defaultPrinterJob == null) {
            defaultPrinterJob = Printer.getAllPrinters().stream().findFirst().<PrinterJob>map(PrinterJob::createPrinterJob).orElse(null);
            if (defaultPrinterJob == null) {
               WindowUtil.showAlert(
                  AlertType.ERROR,
                  this.application.getStage(),
                  org.cropster.roastingintelligence.util.Messages.m_("Error"),
                  org.cropster.roastingintelligence.util.Messages.m_("No printer found!")
               );
               return;
            }
         }
      }

      double labelWidth = this.preferences.getPreferences().getDouble("PrintDialog.pageFormat.width", -1.0);
      double labelHeight = this.preferences.getPreferences().getDouble("PrintDialog.pageFormat.height", -1.0);
      if (labelWidth > 0.0 && labelHeight > 0.0) {
         this.printWindowController.setWidth(labelWidth);
         this.printWindowController.setHeight(labelHeight);
      } else if (defaultPrinter != null) {
         Paper paper = defaultPrinter.getDefaultPageLayout().getPaper();
         Measure w = new Measure(paper.getWidth() / 72.0, Unit.IN);
         Measure h = new Measure(paper.getHeight() / 72.0, Unit.IN);
         this.printWindowController.setWidth(MathUtil.roundHalfToEven(w.toUnit(this.printWindowController.getUnit()).getAmount(), 0));
         this.printWindowController.setHeight(MathUtil.roundHalfToEven(h.toUnit(this.printWindowController.getUnit()).getAmount(), 0));
      }

      this.printWindowController.setOnSwitchToSystemDialogue(e -> {
         this.printWindow.hide();
         this.printRoasts(model, roasts);
      });
      Preferences prefs = model.getPreferences();
      this.printWindowController
         .setOnPreview(
            e -> this.previewRoast(RoastLabel.toData(model, roasts[0], prefs.getPrintDateFormatter()), prefs.getPrintFontSize(), prefs.isPrintTitleVisible())
         );
      this.printWindowController
         .setOnPrint(
            e -> {
               this.printWindow.hide();
               Printer printer = this.printWindowController.getSelectedPrinter();
               PrinterJob printerJob = PrinterJob.createPrinterJob(printer);
               String jobName = org.cropster.roastingintelligence.util.Messages.m_("Roast") + " " + roasts[0].getLot().getIdTag();
               if (roasts.length > 1) {
                  jobName = jobName + " + " + (roasts.length - 1);
               }

               printerJob.getJobSettings().setJobName(jobName);
               printerJob.getJobSettings().setPrintQuality(PrintQuality.HIGH);
               Paper paper = PrintHelper.createPaper(
                  "CustomLabel",
                  this.printWindowController.getWidth().doubleValue(),
                  this.printWindowController.getHeight().doubleValue(),
                  JavaFxUtil.toPrintUnit(this.printWindowController.getUnit())
               );
               PageLayout pageLayout = printer.createPageLayout(paper, this.printWindowController.getOrientation(), 0.0, 0.0, 0.0, 0.0);
               printerJob.getJobSettings().setPageLayout(pageLayout);
               this.logger.info("Printing job {} to {}", printerJob.getJobSettings().getJobName(), printerJob.getPrinter());
               List<RoastLabel> labels = new ArrayList<>();

               for (Roast roast : roasts) {
                  RoastLabel roastLabel = new RoastLabel(
                     RoastLabel.toData(model, roast, prefs.getPrintDateFormatter()),
                     new QrCodeRenderer(prefs.getPrintFontSize() * 30, prefs.getPrintFontSize() * 30, 0.25),
                     prefs.isPrintTitleVisible()
                  );
                  roastLabel.setPrefWidth(pageLayout.getPrintableWidth());
                  roastLabel.setPrefHeight(pageLayout.getPrintableHeight());
                  Scene scene = new Scene(roastLabel, pageLayout.getPrintableWidth(), pageLayout.getPrintableHeight());
                  scene.getStylesheets().add(STYLE_SHEET);
                  scene.getRoot().setStyle(String.format("-fx-font-size: %spt", prefs.getPrintFontSize()));
                  Stage stage = new Stage();
                  stage.setScene(scene);
                  stage.show();
                  stage.hide();
                  labels.add(roastLabel);
               }

               this.preferences.getPreferences().put("PrinterJob.printer", printerJob.getPrinter().getName());
               this.preferences.getPreferences().put("PrinterJob.orientation", pageLayout.getPageOrientation().name());
               this.preferences.getPreferences().putDouble("PrintDialog.pageFormat.width", this.printWindowController.getWidth().doubleValue());
               this.preferences.getPreferences().putDouble("PrintDialog.pageFormat.height", this.printWindowController.getHeight().doubleValue());
               this.printRegions(printerJob, labels.toArray(new Region[labels.size()]));
            }
         );
      this.printWindow.showAndWait();
   }

   private void printBlendsCustom(Lot... blends) {
      this.printBlendsCustom(this.appModel, blends);
   }

   private void printBlendsCustom(AppModel model, Lot... blends) {
   }

   public void openBlendOnline(Lot blend) {
      String uri = this.appModel.getUri().resolve(String.format("/r/roast/%s", blend.getId())).toString();
      RoastingIntelligenceApp.showDocument(uri);
   }

   private void initComponents() {
      this.finishPanel = new FinishPanel();
      this.finishPanel.setStartTime(0L);
      this.finishPanel.setEndTime(0L);
      this.finishPanel.startTimeProperty().addListener((s, o, n) -> {
         if (!this.appModel.getPreferences().isSmartGoalsAndReferencesEnabled()) {
            Long startTime = n;
            if (this.roastingModel != null) {
               this.roastingModel.setStartOffset(startTime);
               this.roastChartWrapper.getChart().setStartMarker(startTime);
            }
         }
      });
      this.finishPanel.endTimeProperty().addListener((s, o, n) -> {
         if (!this.appModel.getPreferences().isSmartGoalsAndReferencesEnabled()) {
            Long endTime = n;
            if (this.roastingModel != null) {
               this.roastingModel.setEndOffset(endTime - (this.roastingModel.getEndDate().getTime() - this.roastingModel.getStartDate().getTime()));
               this.roastChartWrapper.getChart().setEndMarker(endTime);
            }
         }
      });
      this.preferencesController = new PreferencesWindowController();
      this.preferencesController.setCsarConnection(this.syncService.getConnection());
      ((PrintPreferencesPage)this.preferencesController.getPage(PrintPreferencesPage.class))
         .setPrintFunction((x$0, xva$1) -> this.printRoastsCustom(x$0, xva$1));
      this.preferencesWindow = new Stage();
      this.preferencesWindow.initOwner(this.application.getStage());
      this.preferencesWindow.initModality(Modality.APPLICATION_MODAL);
      this.preferencesWindow.setTitle(org.cropster.roastingintelligence.util.Messages.m_("Preferences"));
      this.preferencesWindow.setMinWidth(800.0);
      this.preferencesWindow.setMinHeight(600.0);
      this.preferencesWindow.setScene(new Scene(this.preferencesController.getPreferencesPanel()));
      this.preferencesWindow.getScene().getStylesheets().add(STYLE_SHEET);
      this.preferencesWindow.setOnHiding(e -> {
         SoundManager soundManager = SoundManager.get();
         if (soundManager.isPlaying("test-sound-identifier")) {
            soundManager.stop();
         }
      });
      this.roastInfoWindow = new ButtonBarWindow().withButton(org.cropster.roastingintelligence.util.Messages.m_("Close"), e -> this.roastInfoWindow.hide());
      this.roastInfoWindow.initModality(Modality.NONE);
      this.roastInfoWindow.setAlwaysOnTop(false);
      this.roastInfoWindow.getRoot().setCenter(this.roastInfoPanel);
      JavaFxUtil.reverseButtonBars(this.roastInfoWindow.getRoot());
      FXMLLoader fxmlLoader = this.loadPage("layout/PrintWindow.fxml");
      this.printWindowController = (PrintWindowController)fxmlLoader.getController();
      this.printWindow = new Stage();
      this.printWindow.initOwner(this.application.getStage());
      this.printWindow.initModality(Modality.APPLICATION_MODAL);
      this.printWindow.setTitle(org.cropster.roastingintelligence.util.Messages.m_("Print"));
      this.printWindow.setResizable(false);
      this.printWindow.setScene(new Scene((Parent)fxmlLoader.getRoot()));
      this.printWindow.getScene().getStylesheets().add(STYLE_SHEET);
      FXMLLoader mainPanelLoader = this.loadPage("layout/MainPanel.fxml");
      this.mainPanelController = (MainPanelController)mainPanelLoader.getController();
      this.mainPanelController.setMessageListener(this);
      this.mainPanelController.getProductionPanel().getRandomShortcutPane().setButtonEventHandler(this::showKeyboardShortcuts);
      this.mainPanelController.getCommentsSlidingPanel().getRandomShortcutPane().setButtonEventHandler(this::showKeyboardShortcuts);
      this.mainPanelController.getAlarmsSlidingPanel().getAlarmsRandomShortcutPane().setButtonEventHandler(this::showKeyboardShortcuts);
      this.mainPanelController.getAlarmsSlidingPanel().getTriggeredAlarmsRandomShortcutPane().setButtonEventHandler(this::showKeyboardShortcuts);
      this.mainPanelController.setComponentManager(this.componentManager);
      this.alarmController.initSidebar(this.mainPanelController.getAlarmsSlidingPanel());
      this.mainPanelController
         .getUnacknowledgedTriggeredAlarmsTag()
         .visibleProperty()
         .bind(
            Bindings.createBooleanBinding(
               () -> this.alarmController.getUnacknowledgedTriggeredAlarms() > 0
                     && (!this.mainPanelController.getAlarmsSlidingPanel().isVisible() || this.mainPanelController.getAlarmsSlidingPanel().isManageSelected()),
               new Observable[]{
                  this.alarmController.unacknowledgedTriggeredAlarmsProperty(),
                  this.mainPanelController.getAlarmsSlidingPanel().visibleProperty(),
                  this.mainPanelController.getAlarmsSlidingPanel().manageSelectedProperty()
               }
            )
         );
      this.mainPanelController.getUnacknowledgedTriggeredAlarmsTag().textProperty().bind(Bindings.createStringBinding(() -> {
         String text = "";
         if (this.alarmController.getUnacknowledgedTriggeredAlarms() > 0) {
            text = String.valueOf(this.alarmController.getUnacknowledgedTriggeredAlarms());
         }

         return text;
      }, new Observable[]{this.alarmController.unacknowledgedTriggeredAlarmsProperty()}));
      this.liveGoalController.initActiveGoalsView(this.mainPanelController.getActiveGoalEmptyStateGrid(), this.appModel);
      this.permissionNotificationController.goalsModuleAvailableProperty().addListener(this::onPermissionsChanged);
      this.onPermissionsChanged(null, false, this.permissionNotificationController.goalsModuleAvailableProperty().get());
      this.mainPanel = (AnchorPane)mainPanelLoader.getRoot();
      this.mainPanelController.getContentPanel().setTop(this.createMenuBar());
   }

   private void onPermissionsChanged(ObservableValue<? extends Boolean> s, Boolean o, Boolean n) {
      this.mainPanelController.getReferencePanel().setText(org.cropster.roastingintelligence.util.Messages.m_("Parameters"));
      TextFlow flow = new TextFlow();
      flow.getChildren()
         .setAll(
            new Node[]{
               new Text(org.cropster.roastingintelligence.util.Messages.m_("To ensure all information is displayed, note the following:\n")),
               new LineBreak(),
               new Label(" ", Icon.TARGET.load("icon--muted")),
               (Node)JavaFxUtil.parseHtmlToTextNodes("<b>" + org.cropster.roastingintelligence.util.Messages.m_("Reference parameters") + "</b>").get(0),
               new LineBreak(),
               new Text(org.cropster.roastingintelligence.util.Messages.m_("The selected profile must have a reference value assigned for the parameter."))
            }
         );
      if (n != null && n) {
         this.mainPanelController.getReferencePanel().setText(org.cropster.roastingintelligence.util.Messages.m_("Parameters and goals"));
         flow.getChildren()
            .addAll(
               new Node[]{
                  new LineBreak(),
                  new LineBreak(),
                  new Label(" ", Icon.PROFILE.load("icon--muted")),
                  (Node)JavaFxUtil.parseHtmlToTextNodes("<b>" + org.cropster.roastingintelligence.util.Messages.m_("Relative smart goals") + "</b>").get(0),
                  new LineBreak(),
                  new Text(
                     org.cropster.roastingintelligence.util.Messages.m_(
                        "The selected profile must have a reference value assigned for the parameter, and a pre-set goal matching the selected profile and parameter.\n"
                     )
                  ),
                  new LineBreak(),
                  new Label(" ", Icon.GOAL.load("icon--muted")),
                  (Node)JavaFxUtil.parseHtmlToTextNodes("<b>" + org.cropster.roastingintelligence.util.Messages.m_("Absolute smart goals") + "</b>").get(0),
                  new LineBreak(),
                  new Text(org.cropster.roastingintelligence.util.Messages.m_("A pre-set goal matching the selected profile and parameter is required."))
               }
            );
      }

      ObservableList<Node> footer = this.mainPanelController.getLiveGoalHintsButton().getFooter();
      footer.clear();
      footer.add(flow);
   }

   public void proceedLogin() {
      this.preferences.save();
      Stage stage = this.application.getStage();
      stage.hide();
      this.componentManager.restore(stage, "RoastingIntelligenceFrame");
      this.initNotificationControllers();
      this.startPeriodicUpdateCheck();
      this.createMeasurementHub();
      this.appModel.addSettingsListener(this);
      this.appModel.addSettingsListener(this.measurementHub);
      this.appModel.addCsarDataListener(this);
      this.roastingManager.addRoastingListener(this);
      this.initSegment();
      this.alarmController.initializeAlarmSystem(this.appModel.getPreferences(), this.syncService, this.connection);
      this.liveGoalController.initializeLiveGoalSystem(this.syncService, this.connection);
      this.initComponents();
      this.toastNotificationController.getHandler().setOwner(this.mainPanel);
      this.betweenBatchManager = new BetweenBatchManager(
         this.application,
         this.alarmController,
         this.liveGoalController,
         this.measurementHub,
         this.mainPanelController,
         this.state,
         this.appModel,
         this.toastNotificationController
      );
      this.mainPanelController.getSystemSidebar().visibleProperty().addListener((s, o, visible) -> {
         if (visible) {
            this.toastNotificationController.hide("system-status-connection-important-tag");
         }
      });
      this.systemNotificationController.getHandler().globalStatusProperty().addListener((s, o, n) -> {
         if (n != null && n != NotificationType.ERROR) {
            this.toastNotificationController.hide("system-status-connection-important-tag");
         }
      });
      this.systemNotificationController
         .control(
            this.mainPanelController.getSystemSidebarButton(),
            this.mainPanelController.getSystemNorificationTag(),
            this.mainPanelController.getSystemSidebar(),
            this.application::checkForUpdate,
            this.appModel.getUserName()
         );
      this.statusChanged(null);
      this.mainPanelController.getProductionPanel().setVisible(false);
      this.mainPanelController.getProductionPanel().init(this.appModel, this.state, this.connection, this::updateFinalValues, this::updateRoastInfo);
      this.mainPanelController
         .getProductionPanel()
         .initActions(
            this::markRoastAsReference,
            xva$0 -> this.printRoastsCustom(xva$0),
            this::openRoastOnline,
            this::showRoastInfo,
            xva$0 -> this.printBlendsCustom(xva$0),
            this::openBlendOnline
         );
      this.mainPanelController
         .getProductionPanel()
         .setPrintSelectedRoastLabelsConsumer(
            () -> {
               if (!this.mainPanelController.getProductionList().getSelectionModel().getSelectedIndices().isEmpty()) {
                  Roast[] selectedRoasts = this.mainPanelController
                     .getProductionList()
                     .getSelectionModel()
                     .getSelectedItems()
                     .stream()
                     .filter(Roast.class::isInstance)
                     .map(Roast.class::cast)
                     .toArray(Roast[]::new);
                  this.printRoastsCustom(selectedRoasts);
               }
            }
         );
      this.mainPanelController.getProductionPanel().setPrintSelectedBlendLabelsConsumer(() -> {
         if (!this.mainPanelController.getBlendList().getSelectionModel().getSelectedIndices().isEmpty()) {
            Lot[] selectedBlends = (Lot[])this.mainPanelController.getBlendList().getSelectionModel().getSelectedItems().toArray(Lot[]::new);
            this.printBlendsCustom(selectedBlends);
         }
      });
      this.mainPanelController.getProductionPanel().blendsSelectedProperty().addListener((s, o, n) -> {
         if (n != null && n) {
            this.toastNotificationController.hide("blends-tag");
         }
      });
      this.blendNotificationController.control(this.mainPanelController.getProductionPanel());
      this.initPermissionController();
      this.mainPanelController.getPreparationPanel().getUserPanel().workerProperty().addListener((s, o, n) -> {
         this.appModel.setSelectedRoastingPerson(UserPanel.CURRENT_USER_SELECTION == n ? null : n);
         this.mainPanelController.getPreparationPanel().getUserPanel().setUserInfo(this.appModel.getUserInfo());
         this.mainPanelController.getPreparationPanel().getUserPanel().setUserEmail(this.appModel.getUserName());
      });
      this.mainPanelController.getProductionList().getSelectionModel().selectedItemProperty().addListener((v, o, n) -> {
         if (!this.state.getRoastInProgress()) {
            if (!this.mainPanelController.getProductionList().isDisabled()) {
               if (n == null && o == this.appModel.getCurrentScheduled()) {
                  this.resetSchedule();
               } else if (n instanceof Scheduled selectedSchedule) {
                  this.logger.info("Selected scheduled id {}", selectedSchedule.getId());
                  this.selectSchedule(selectedSchedule);
               }
            }
         }
      });
      this.mainPanelController.getTasksSidebar().getTaskView().setOnEditCommit(e -> this.syncTasksBlocking());
      this.appModel.addSettingsListener(this.mainPanelController);
      Map<String, MeasurementHandler> controlHandlers = new LinkedHashMap<>();
      Map<String, MeasurementHandler> channelStatusHandlers = new LinkedHashMap<>();
      String airMeasurement = this.appModel.isChannelActive("drumPressureControl")
         ? "drumPressureControl"
         : (this.appModel.isChannelActive("fanSpeedControl") ? "fanSpeedControl" : "airflowControl");
      String gasMeasurement = this.appModel.isChannelActive("customControl") ? "customControl" : "gasControl";

      for (String controlMeasurement : new String[]{gasMeasurement, airMeasurement, "drumSpeedControl", "setTemperatureControl"}) {
         if (this.appModel.isChannelActive(controlMeasurement)) {
            ControlSlider control = this.mainPanelController.getControlSlider(controlMeasurement);
            this.measurementHub
               .getMeasurementSources()
               .stream()
               .filter(
                  s -> s instanceof MeasurementReaderWriter
                        && s.getFactory().getUnit(controlMeasurement) != null
                        && this.appModel.getChannel(controlMeasurement).getSourceFactory().equals(s.getFactory().getClass())
               )
               .findFirst()
               .ifPresent(
                  s -> {
                     Unit unit = s.getFactory().getUnit(controlMeasurement);
                     control.setSource(
                        (MeasurementReaderWriter)s,
                        controlMeasurement,
                        unit.isCompatible(Unit.CELSIUS) ? this.appModel.getPreferences().getPreferredUnit(Unit.CELSIUS) : unit
                     );
                     this.createMeasurementHandler(controlMeasurement, control, this.appModel, controlHandlers, channelStatusHandlers);
                  }
               );
         }
      }

      this.mainPanelController.getControlStateContainer().setSources(this.measurementHub.getMeasurementSources());
      HandlerChainManager handlerManager = new HandlerChainManager(
         new ChannelStatusHandler(this.mainPanelController.getChannelStatusPanels()),
         controlHandlers.values(),
         channelStatusHandlers.values(),
         List.of(new ControlStateBarHandler(this.mainPanelController.getControlStateContainer()))
      );
      this.mainPanelController.setMeasurementHandlerManager(handlerManager);
      this.mainPanelController.getSwitchProfileButton().setOnAction(e -> this.showProfilePopup((Node)e.getSource()));
      this.mainPanelController.getReplaySwitch().visibleProperty().bind(this.state.replayStartedProperty().and(this.state.roastInProgressProperty()));
      this.mainPanelController.getReplaySwitch().onProperty().bindBidirectional(this.state.replayingProperty());
      this.state
         .replayAvailableProperty()
         .bind(
            this.state
               .replayEnabledProperty()
               .and(this.mainPanelController.getGasControl().configuredProperty().or(this.mainPanelController.getSetTemperatureControl().configuredProperty()))
               .and(
                  Bindings.createBooleanBinding(
                     () -> this.appModel
                           .isReplayAvailable((Profile)this.mainPanelController.getPreparationPanel().getProfileSearch().selectedItemProperty().get()),
                     new Observable[]{this.mainPanelController.getPreparationPanel().getProfileSearch().selectedItemProperty()}
                  )
               )
         );
      this.initScene(stage);
      this.selectedLotIds = this.preferences.getArray("RoastingStartPanel.selectedLots", Long[].class);
      if (this.selectedLotIds == null) {
         this.selectedLotIds = new Long[0];
      }

      this.mainPanelController
         .getPreparationPanel()
         .getProfileSearch()
         .selectedItemProperty()
         .addListener(
            (c, o, n) -> {
               this.appModel.setSelectedProfile(n);
               this.mainPanelController
                  .populateStartPanel(this.appModel, this::showMachinePreferences, this.permissionNotificationController.riPermissionGrantedProperty());
               Long[] componentLotIds;
               if (n != null) {
                  if (n.getBatchSize() != null) {
                     this.mainPanelController
                        .getPreparationPanel()
                        .setStartWeight(n.getBatchSize().toUnit(this.appModel.getPreferences().getWeightUnit()).getAmount());
                  }

                  componentLotIds = new Long[Math.max(1, n.getComponents().size())];
                  int i = 0;

                  for (BlendComponent component : n.getComponents()) {
                     if (!component.getLotIds().isEmpty()) {
                        componentLotIds[i++] = component.getLotIds().iterator().next();
                     }
                  }
               } else {
                  componentLotIds = new Long[1];
               }

               this.mainPanelController.getPreparationPanel().resetLots(this.appModel, componentLotIds);
               this.mainPanelController.setStartPanelReference(n, this.appModel);
               BetweenBatchPanel bbp = this.mainPanelController.getBetweenBatchPanel();
               BetweenBatchChartWrapper bbpcw = bbp.getChartWrapper();
               if (bbpcw != null) {
                  bbp.getRestartMenuItem().setOnAction(this.betweenBatchManager::resetBetweenBatchLog);
                  bbp.getStopMenuItem().setOnAction(this.betweenBatchManager::stopBetweenBatchLog);
                  bbp.dimmedProperty().bind(this.state.betweenBatchStoppedProperty());
                  AlarmsUtil.addAlarmsBorderHandler(bbpcw, this.alarmController);
               }

               this.liveGoalController.getLiveGoalService().setCurrentProfile(this.appModel);
            }
         );
      SupportSidebar supportSlidingPanel = this.mainPanelController.getSupportSlidingPanel();
      supportSlidingPanel.getSubmitButton().setOnAction(e -> this.createSupportTicket(supportSlidingPanel.getSubject(), supportSlidingPanel.getDescription()));
      this.mainPanelController.getSyncButton().setOnAction(this::syncAll);
      this.mainPanelController.getPreferencesButton().setOnAction(this::showPreferences);
      this.profilePopup = new Popup();
      this.profilePopup.getScene().getRoot().getStylesheets().add(STYLE_SHEET);
      this.profilePopup.setAnchorLocation(AnchorLocation.WINDOW_BOTTOM_LEFT);
      this.profilePopup.setAutoHide(true);
      this.profilePopup.setConsumeAutoHidingEvents(true);
      this.profilePopup.setHideOnEscape(true);
      this.profilePopup.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
         if (e.getCode() == KeyCode.ESCAPE) {
            this.profilePopup.hide();
            e.consume();
         }
      });
      this.switchToStartPanel();
      this.measurementHub.start();
      this.appModel.updateAutoStartMode();
      this.appModel.fireSettingsChanged();
      this.syncService.addMessageListener(this);
      this.appModel.activate();
      this.dataReceived(this.appModel.getData());
      if (this.appModel.isTokenSet()) {
         try {
            this.syncService.loadMachines();
            this.syncService.uploadRoasts();
            this.syncService.syncDataNonBlocking();
            this.syncTasks();
            this.syncBlends();
         } catch (Exception var11) {
            throw new RuntimeException(var11);
         }
      }

      this.syncService.execute(new VersionChecker(this, this.appModel.getVersion()));
      this.startTimeline();
      Preferences prefs = this.appModel.getPreferences();
      if (prefs.isSoundAlarmOnEventsEnabled()) {
         this.alarmController.getAlarmService().attach(this.measurementHub, RoastingPhase.BETWEEN_BATCHES);
      }

      if (prefs.isSmartGoalsAndReferencesEnabled()) {
         PreparationPanel pPanel = this.mainPanelController.getPreparationPanel();
         this.liveGoalController
            .getLiveGoalService()
            .attach(new LiveGoalBetweenBatchModel(this.measurementHub, pPanel.getStartWeightEditor()), RoastingPhase.BETWEEN_BATCHES);
      }

      this.state.roastInProgressProperty().addListener((v, o, n) -> {
         if (!o && n) {
            this.updateStatus(this.roastingModel.getData().getProfiles().get(this.roastingModel.getRoast().getProfile()));
         } else {
            this.updateStatus(null);
         }
      });
      if (!this.setupWarnings.isEmpty()) {
         Button closeButton = new Button();
         closeButton.setAlignment(Pos.TOP_LEFT);
         closeButton.setGraphic(Icon.CLOSE.load("icon--link"));
         closeButton.getStyleClass().add("button--transparent");
         MessageBox warningPanel = this.mainPanelController
            .showMessagePanel(
               Stream.concat(
                     Stream.of(org.cropster.roastingintelligence.util.Messages.m_("The following errors occurred on application startup:")),
                     this.setupWarnings.stream()
                  )
                  .collect(Collectors.joining("\n\n"))
                  .toString(),
               "info-message--warning",
               closeButton
            );
         warningPanel.setGraphic(Icon.WARNING.load("icon--warning"));
         closeButton.setOnAction(e -> this.mainPanelController.removeMessagePanel(warningPanel));
      }

      this.state.replayingProperty().addListener((s, o, n) -> this.replayers.forEach(r -> r.setPaused(!n)));
      this.mainPanelController
         .getAutoStartReadyIcon()
         .visibleProperty()
         .bind(this.mainPanelController.autoStartSourceFactoryProperty().isNotNull().and(this.state.roastInProgressProperty().not()));
      this.mainPanelController.getAutoStartReadyIcon().managedProperty().bind(this.mainPanelController.getAutoStartReadyIcon().visibleProperty());
      PlatformScheduler.schedule(() -> {
         this.initSplittersPositionManagement();
         this.installWhatsNewHandler();
         this.mainPanelController.getPreparationPanel().requestFocus();
         this.initSlidersLayout();
      }, 777, TimeUnit.MILLISECONDS);
   }

   private void initScene(Stage stage) {
      String titleSuffix = System.getProperty("org.cropster.roastingintelligence.version");
      titleSuffix = titleSuffix == null ? "0.0" : titleSuffix.trim();
      titleSuffix = titleSuffix + (this.appModel.isDemoMode() ? " - Demo" : "");
      String instance = System.getProperty("org.cropster.roastingintelligence.instance");
      if (instance != null) {
         titleSuffix = titleSuffix + " - " + instance;
      }

      Scene scene = this.application.switchScene(titleSuffix, this.mainPanel);
      stage.show();
      stage.setResizable(true);
      stage.setMinWidth(700.0);
      stage.setMinHeight(590.0);
      this.componentManager.restore(stage, "RoastingIntelligenceFrame");
      scene.addEventFilter(TouchEvent.ANY, new JavaFxController.TouchScreenDetector(scene));
      scene.setOnKeyPressed(k -> {
         this.shiftDown.set(k.isShiftDown());
         if (k.isControlDown() && (k.isAltDown() || KeyCode.R == k.getCode())) {
            this.mainPanelController.setConnectedOveride(true);
         }
      });
      scene.setOnKeyReleased(k -> {
         this.shiftDown.set(k.isShiftDown());
         if (!k.isControlDown() || !k.isAltDown() || KeyCode.R == k.getCode()) {
            this.mainPanelController.setConnectedOveride(false);
         }
      });
      this.setupKeyboardShortcuts(scene);
   }

   private void initPermissionController() {
      this.permissionNotificationController.init(this.appModel, this.connection, this::notifyIfNotAuthorized);
      ProductionSidebar productionSidebar = this.mainPanelController.getProductionPanel();
      productionSidebar.getMarkRoastAsReferenceAction()
         .disabledProperty()
         .bind(this.permissionNotificationController.markReferencePermissionGrantedProperty().not());
      FilteredList<Object> selectedRoasts = productionSidebar.getProductionView().getSelectionModel().getSelectedItems().filtered(Roast.class::isInstance);
      productionSidebar.getBlendManuallyAction()
         .disabledProperty()
         .bind(this.permissionNotificationController.blendPermissionGrantedProperty().not().or(Bindings.size(selectedRoasts).lessThan(2)));
      productionSidebar.getBlendWithProfileAction()
         .disabledProperty()
         .bind(this.permissionNotificationController.blendPermissionGrantedProperty().not().or(Bindings.size(selectedRoasts).lessThan(2)));
      productionSidebar.getToggleButtonBar().visibleProperty().bind(this.permissionNotificationController.blendPermissionGrantedProperty());
      this.permissionNotificationController.taskModuleAvailableProperty().addListener((s, o, n) -> {
         SidebarButton tasksSidebarButton = this.mainPanelController.getTasksSidebarButton();
         if (n != null && n) {
            tasksSidebarButton.setVisible(true);
         } else {
            tasksSidebarButton.closePanel();
            tasksSidebarButton.setVisible(false);
         }
      });
      ChangeListener<? super Boolean> goalsModuleAvailableListener = (s, o, n) -> {
         boolean smartGoalsEnabled = n != null && n;
         this.liveGoalController.updateSmartGoals(smartGoalsEnabled);
         this.mainPanelController
            .getActiveGoalEmptyStateGrid()
            .setEmptyStateMessage(
               smartGoalsEnabled
                  ? org.cropster.roastingintelligence.util.Messages.m_("No parameters or smart goals to display")
                  : org.cropster.roastingintelligence.util.Messages.m_("No parameters to display")
            );
         String message = org.cropster.roastingintelligence.util.Messages.m_(
            "Reference parameters are only shown if the selected profile has a reference and there is a value for the parameter."
         );
         if (smartGoalsEnabled) {
            message = message
               + "\n\n"
               + org.cropster.roastingintelligence.util.Messages.m_(
                  "Smart goals are only shown if the selected profile is affected by them or if they apply to all profiles."
               );
         }

         this.mainPanelController.getActiveGoalEmptyStateGrid().setEmptyStateNote(message);
      };
      this.permissionNotificationController.goalsModuleAvailableProperty().addListener(goalsModuleAvailableListener);
      goalsModuleAvailableListener.changed(
         this.permissionNotificationController.goalsModuleAvailableProperty(), null, this.permissionNotificationController.goalsModuleAvailableProperty().get()
      );
      ControlToggleBar controlToggleBar = this.mainPanelController.getControlToggleBar();
      controlToggleBar.visibleProperty()
         .bind(
            Bindings.or(this.permissionNotificationController.riPermissionGrantedProperty(), this.state.roastInProgressProperty())
               .and(controlToggleBar.getSlidersToggleButton().visibleProperty().or(controlToggleBar.getButtonsToggleButton().visibleProperty()))
         );
      this.permissionNotificationController.riPermissionGrantedProperty().addListener((s, o, n) -> {
         if (n == null || !n) {
            controlToggleBar.closeSidebar();
         }
      });
   }

   private void notifyIfNotAuthorized(boolean authorized) {
      this.mainPanelController.populateStatusPanel(this.appModel);
      if (!PermissionNotificationController.isAnyRiPermissionGranted(this.appModel)) {
         this.notifyPermissionRevoked();
      } else {
         this.notifyInvalidMachine(authorized);
      }
   }

   private void notifyInvalidMachine(boolean authorized) {
      VeilSupport veilSupport = (VeilSupport)this.mainPanel.getProperties().get("permissionVeil");
      if (veilSupport != null) {
         veilSupport.unveil();
      }

      VeilSupport.enableApplicationMenu(this.mainPanel.getScene());
      if (!authorized) {
         NotificationBusService nbs = RoastingIntelligenceApp.retrieveSingleton(NotificationBusService.class);
         if (nbs != null) {
            Machine machine = this.appModel.getSelectedMachine();
            String groupName = "<b>" + (machine == null ? "–" : machine.getGroup().getName()) + "</b>";
            nbs.publish(
               Notification.builder(NotificationType.WARNING)
                  .withTitle(org.cropster.roastingintelligence.util.Messages.m_("User permissions have been changed"))
                  .withMessage(
                     org.cropster.roastingintelligence.util.Messages.m_("The user permissions for the following group were modified: <b>{0}</b>", groupName)
                  )
                  .withPrerequisite(this::closeMachineInvalidToast)
                  .withProperty("tag", "system-status-machine-invalid-tag")
                  .withProperty(
                     "action",
                     new ActionBuilder(org.cropster.roastingintelligence.util.Messages.m_("Select another machine"), this::showMachinePreferences).build()
                  )
                  .build(),
               "system-message"
            );
         }
      } else {
         Platform.runLater(this::closeMachineInvalidToast);
      }
   }

   private void closeMachineInvalidToast() {
      ToastNotificationController tnc = RoastingIntelligenceApp.retrieveSingleton(ToastNotificationController.class);
      if (tnc != null) {
         tnc.hide("system-status-machine-invalid-tag");
      }
   }

   private void notifyPermissionRevoked() {
      NotificationBusService nbs = RoastingIntelligenceApp.retrieveSingleton(NotificationBusService.class);
      if (nbs != null) {
         Machine machine = this.appModel.getSelectedMachine();
         String groupName = "<b>" + (machine == null ? "–" : machine.getGroup().getName()) + "</b>";
         nbs.publish(
            Notification.builder(NotificationType.WARNING)
               .withTitle(org.cropster.roastingintelligence.util.Messages.m_("User permissions have been changed"))
               .withMessage(
                  org.cropster.roastingintelligence.util.Messages.m_("The user permissions for the following group were modified: <b>{0}</b>", groupName)
               )
               .withPrerequisite(this::closePermissionDeniedToast)
               .withProperty("tag", "system-status-permission-denied-tag")
               .withProperty("action", new ActionBuilder(org.cropster.roastingintelligence.util.Messages.m_("Restart application"), () -> {
               }).build())
               .withProperty("action-close", new ActionBuilder(org.cropster.roastingintelligence.util.Messages.m_("Restart application"), () -> {
                  this.appModel.setRestartRequired(true);
                  this.startShutdown();
               }).build())
               .build(),
            "system-message"
         );
      }

      VeilSupport.disableApplicationMenu(this.mainPanel.getScene());
      VeilSupport veilSupport = (VeilSupport)this.mainPanel.getProperties().get("permissionVeil");
      if (veilSupport == null) {
         veilSupport = new VeilSupport(false);
         this.mainPanel.getProperties().put("permissionVeil", veilSupport);
         veilSupport.setVeiledScene(this.mainPanel, false);
      }
   }

   private void closePermissionDeniedToast() {
      ToastNotificationController tnc = RoastingIntelligenceApp.retrieveSingleton(ToastNotificationController.class);
      if (tnc != null) {
         tnc.hide("system-status-permission-denied-tag");
      }

      if (WindowManager.get().isShowing("PREFERENCES_WINDOW_ID")) {
         this.preferencesWindow.hide();
      }
   }

   private void createMeasurementHub() {
      try {
         Set<MeasurementSourceFactory> sourceFactories = RoastingIntelligenceApp.retrieveService(MeasurementSourceFactory.class);
         this.measurementHub = new MeasurementHub(this.roastingManager, this.appModel, sourceFactories, this, this);
      } catch (IOException var5) {
         throw new RuntimeException("Error setting up measurement sources", var5);
      } catch (RuntimeException var6) {
         String messagePrefix = "Error loading ";
         String message = var6.getMessage();
         if (StringUtils.isNotBlank(message) && message.startsWith(messagePrefix) && message.endsWith("demo-roast.json")) {
            WindowUtil.showAlert(
               AlertType.WARNING,
               this.application.getStage(),
               org.cropster.roastingintelligence.util.Messages.m_("Restart application"),
               org.cropster.roastingintelligence.util.Messages.m_(
                  "No JSON file was found to start demo mode, so the application is now closed. Please add the missing file to the following folder and restart the application:\n\n{0}",
                  message.substring(messagePrefix.length())
               )
            );
            Window window = this.application.getStage();
            window.fireEvent(new WindowEvent(window, WindowEvent.WINDOW_CLOSE_REQUEST));
         }

         throw var6;
      }
   }

   private void startPeriodicUpdateCheck() {
   }

   private void initNotificationControllers() {
      this.toastNotificationController
         .subscribe(
            this.notificationBusService,
            new String[]{
               "alarm",
               "between-batch",
               "blend-created",
               "blend-collection-downloaded",
               "roasting",
               "roast-batch-editing",
               "system-message",
               "system-status-connection-important",
               "system-status-synchronization",
               "system-connector-configuration"
            }
         );
      this.systemNotificationController
         .subscribe(
            this.notificationBusService,
            new String[]{
               "system-status-connection",
               "system-status-ambient-temperature",
               "system-status-data-received",
               "system-status-synchronization",
               "system-status-synchronization-date",
               "system-update",
               "system-update-progress",
               "activity-insights"
            }
         );
      this.permissionNotificationController.subscribe(this.notificationBusService, new String[]{"system-status-data-received"});
      this.blendNotificationController.subscribe(this.notificationBusService, new String[]{"blend-created", "blend-collection-downloaded"});
      RoastingIntelligenceApp.registerService(ToastNotificationController.class, this.toastNotificationController);
   }

   private void initSegment() {
   }

   private void initSlidersLayout() {
      BooleanProperty singleColumnProperty = this.mainPanelController.getSliderContainer().singleColumnProperty();
      this.componentManager.restore(singleColumnProperty, "slidersContainer.singleColumn", true);
      singleColumnProperty.addListener((s, o, n) -> this.componentManager.store(singleColumnProperty, "slidersContainer.singleColumn"));
      ControlToggleBar controlToggleBar = this.mainPanelController.getControlToggleBar();
      ToggleButton slidersToggleButton = controlToggleBar.getSlidersToggleButton();
      if (this.slidersToggleButtonListener == null) {
         this.slidersToggleButtonListener = (s, o, n) -> this.componentManager
               .store(slidersToggleButton.selectedProperty(), "controlToggleBar.slidersToggleButton.selected");
      }

      ToggleButton buttonsToggleButton = controlToggleBar.getButtonsToggleButton();
      if (this.buttonsToggleButtonListener == null) {
         this.buttonsToggleButtonListener = (s, o, n) -> this.componentManager
               .store(buttonsToggleButton.selectedProperty(), "controlToggleBar.buttonsToggleButton.selected");
      }

      ToggleButton controlsToggleButton = controlToggleBar.getControlsToggleButton();
      if (this.controlsToggleButtonListener == null) {
         this.controlsToggleButtonListener = (s, o, n) -> this.componentManager
               .store(controlsToggleButton.selectedProperty(), "controlToggleBar.controlsToggleButton.selected");
      }

      PlatformScheduler.schedule(() -> {
         if (slidersToggleButton.isVisible()) {
            this.componentManager.restore(slidersToggleButton.selectedProperty(), "controlToggleBar.slidersToggleButton.selected", false);
         }

         FxListenerUtil.replaceListener(slidersToggleButton.selectedProperty(), this.slidersToggleButtonListener);
         if (buttonsToggleButton.isVisible()) {
            this.componentManager.restore(buttonsToggleButton.selectedProperty(), "controlToggleBar.buttonsToggleButton.selected", false);
         }

         FxListenerUtil.replaceListener(buttonsToggleButton.selectedProperty(), this.buttonsToggleButtonListener);
         if (controlsToggleButton.isVisible()) {
            this.componentManager.restore(controlsToggleButton.selectedProperty(), "controlToggleBar.controlsToggleButton.selected", false);
         }

         FxListenerUtil.replaceListener(controlsToggleButton.selectedProperty(), this.controlsToggleButtonListener);
      }, 555, TimeUnit.MILLISECONDS);
   }

   private void initSplittersPositionManagement() {
      SplitPane splitterReferencesToChannels = this.mainPanelController.getReferencesToChannelsSplitter();
      SplitPane splitterCenterToRight = this.mainPanelController.getCenterToRightSplitter();
      SplitPane splitterBbpToStart = this.mainPanelController.getStartPanel();
      ChangeListener<Number> referencesToChannelsPositionListener = (s, o, n) -> this.componentManager
            .store(splitterReferencesToChannels, "splitterReferencesToChannels");
      ((Divider)splitterReferencesToChannels.getDividers().get(0)).positionProperty().addListener(referencesToChannelsPositionListener);
      this.mainPanelController.getChannelGroupContainer().visibleProperty().addListener((s, o, n) -> {
         if (n) {
            this.componentManager.restore(splitterReferencesToChannels, "splitterReferencesToChannels", 0.0);
            ((Divider)splitterReferencesToChannels.getDividers().get(0)).positionProperty().addListener(referencesToChannelsPositionListener);
         } else {
            ((Divider)splitterReferencesToChannels.getDividers().get(0)).positionProperty().removeListener(referencesToChannelsPositionListener);
         }
      });
      ChangeListener<Number> splitterCenterToRightPositionListener = (s, o, n) -> this.componentManager.store(splitterCenterToRight, "splitterCenterToRight");
      ((Divider)splitterCenterToRight.getDividers().get(0)).positionProperty().addListener(splitterCenterToRightPositionListener);
      ChangeListener<Number> splitterBbpToStartPositionListener = (s, o, n) -> this.componentManager.store(splitterBbpToStart, "splitterBbpToStart");
      ((Divider)splitterBbpToStart.getDividers().get(0)).positionProperty().addListener(splitterBbpToStartPositionListener);
      this.componentManager.restore(splitterReferencesToChannels, "splitterReferencesToChannels", 0.75);
      PlatformScheduler.schedule(() -> {
         this.componentManager.restore(splitterCenterToRight, "splitterCenterToRight", 0.8);
         this.componentManager.restore(splitterBbpToStart, "splitterBbpToStart", 0.45);
      }, 111, TimeUnit.MILLISECONDS);
   }

   private void createMeasurementHandler(
      String controlMeasurement,
      ControlSlider control,
      AppModel appModel,
      Map<String, MeasurementHandler> controlHandlers,
      Map<String, MeasurementHandler> channelStatusHandlers
   ) {
      MeasurementReaderWriter measurementReaderWriter = control.getSource();
      MeasurementSourceFactory sourceFactory = measurementReaderWriter.getFactory();
      if (GiesenPlcSourceFactory.class.equals(sourceFactory.getClass()) && "gasControl".equals(controlMeasurement)) {
         controlHandlers.put(controlMeasurement, new ControlHandlerGiesenGasControl(control));
         Channel measurementChannel = appModel.getChannel("gas");
         if (measurementChannel != null && measurementChannel.isActive()) {
            controlHandlers.put(measurementChannel.getName(), new ControlHandlerGiesenGas(control));
         }

         channelStatusHandlers.put(controlMeasurement, new ChannelStatusHandlerGiesen(this.mainPanelController.getChannelStatusPanels()));
      } else if (!sourceFactory.supportsAutoMode(controlMeasurement)) {
         controlHandlers.put(controlMeasurement, new ControlHandlerBase(control));
      } else if (sourceFactory.supportsAutoMode(controlMeasurement)) {
         String autoModeMeasurement = sourceFactory.getAutoModeMeasurement(controlMeasurement);
         String manualModeMeasurement = sourceFactory.getManualModeMeasurement(controlMeasurement);
         if (controlMeasurement.equals(autoModeMeasurement)) {
            ControlHandlerMode autoModeHandler = new ControlHandlerAuto(control);
            MeasurementHandler manualModeHandler = controlHandlers.get(manualModeMeasurement);
            if (manualModeHandler != null) {
               autoModeHandler.bind((ControlHandlerMode)manualModeHandler);
            }

            controlHandlers.put(controlMeasurement, autoModeHandler);
         } else if (controlMeasurement.equals(manualModeMeasurement)) {
            channelStatusHandlers.put(
               controlMeasurement,
               new ChannelStatusHandlerAutoMode(controlMeasurement, measurementReaderWriter, this.mainPanelController.getChannelStatusPanels())
            );
            ControlHandlerMode manualModeHandler = new ControlHandlerManual(control);
            MeasurementHandler autoModeHandler = controlHandlers.get(autoModeMeasurement);
            if (autoModeHandler != null) {
               manualModeHandler.bind((ControlHandlerMode)autoModeHandler);
            }

            controlHandlers.put(controlMeasurement, manualModeHandler);
            Channel measurementChannel = appModel.getChannel(controlMeasurement.replaceAll("Control", ""));
            if (measurementChannel != null && measurementChannel.isActive()) {
               controlHandlers.put(measurementChannel.getName(), new ControlHandlerManualMeasurement(measurementChannel.getName(), control));
               ((ControlHandlerManual)manualModeHandler).setSliderFollowsControlMeasurement(false);
            }
         }
      }
   }

   private void switchToStartPanel() {
      assert Platform.isFxApplicationThread();

      this.measurementHub.removeModel(this.roastingModel);
      if (this.commentPopOver != null) {
         this.commentPopOver.hide();
         this.commentPopOver = null;
      }

      if (this.roastingModel != null) {
         this.roastingModel.removeCommentListener(this.mainPanelController.getCommentsSlidingPanel());
      }

      this.roastingModel = null;
      this.state.setPhase(JavaFxApplicationState.Phase.SETUP);
      this.mainPanelController.getCenterPanel().setLeft(null);
      this.mainPanelController.getCenterPanel().setCenter(this.mainPanelController.getStartPanel());
      this.mainPanelController.getMessagePanel().setVisible(true);
      this.mainPanelController.getMessagePanel().setManaged(true);
      this.mainPanelController.getCommentsPanel().setVisible(false);
      Preferences prefs = this.appModel.getPreferences();
      this.mainPanelController.getReferencePanel().setVisible(prefs.isSmartGoalsAndReferencesEnabled());
      if (this.roastChartWrapper != null) {
         this.mainPanelController.getCenterPanel().getChildren().removeAll(new Node[]{this.roastChartWrapper});
         this.roastChartWrapper.dispose();
         this.roastChartWrapper = null;
      }

      if (this.curvePredictionEnabledProperty != null) {
         this.curvePredictionEnabledProperty.unbind();
      }

      if (this.firstCrackPredictionEnabledProperty != null) {
         this.firstCrackPredictionEnabledProperty.unbind();
      }

      if (this.flickPredictionEnabledProperty != null) {
         this.flickPredictionEnabledProperty.unbind();
      }

      JavaFxUtil.bindButtonToBoolean(
         this.mainPanelController.getPrimaryButton(),
         this.state.replayAvailableProperty(),
         Icon.REPLAY.load(),
         org.cropster.roastingintelligence.util.Messages.m_("Replay"),
         e -> this.replayRoasting(),
         null,
         Icon.START.load(),
         org.cropster.roastingintelligence.util.Messages.m_("Start"),
         e -> this.startRoasting(),
         new Tooltip(org.cropster.roastingintelligence.util.Messages.m_("Start"), KeyboardShortcut.CTRL_ENTER)
      );
      this.mainPanelController.getSecondaryButton().getStyleClass().remove("button--plain");
      JavaFxUtil.bindButtonToBoolean(
         this.mainPanelController.getSecondaryButton(),
         this.state.replayAvailableProperty(),
         Icon.START.load(),
         org.cropster.roastingintelligence.util.Messages.m_("Start"),
         e -> this.startRoasting(),
         new Tooltip(org.cropster.roastingintelligence.util.Messages.m_("Start"), KeyboardShortcut.CTRL_ENTER)
      );
      this.mainPanelController
         .getPrimaryButton()
         .setOnMouseClicked(
            e -> {
               if (e.getButton() == MouseButton.PRIMARY
                  && this.mainPanelController.isConnectedOveride()
                  && JavaFxApplicationState.Phase.SETUP.equals(this.state.getPhase())) {
                  e.consume();
                  this.mainPanelController.getPrimaryButton().fire();
               }
            }
         );
      this.mainPanelController.getTimerPanel().setVisible(false);
      this.mainPanelController.getSecondaryTimerLabel().setVisible(false);
      this.mainPanelController.getPercentageLabel().setVisible(false);
      this.mainPanelController.getSelectMachineButton().setOnAction(e -> this.showMachinePreferences());
      Button lgcButton = this.mainPanelController.getLiveGoalConfigurationButton();
      lgcButton.setOnAction(e -> this.liveGoalController.showConfigurationPopup(lgcButton));
      String activeGoalColumns = "activeGoalGrid.columns";
      Button lglButton = this.mainPanelController.getLiveGoalLayoutButton();
      GridView<LiveGoalService.ActiveGoal> gridView = this.mainPanelController.getActiveGoalEmptyStateGrid().getGridView();
      lglButton.setOnAction(e -> {
         gridView.setColumns(gridView.getColumns() == 1 ? 2 : 1);
         this.liveGoalController.getLiveGoalService().refresh();
         this.componentManager.store(gridView.columnsProperty(), "activeGoalGrid.columns");
      });
      lglButton.disableProperty().bind(Bindings.isEmpty(gridView.getItems()));
      this.componentManager.restore(gridView.columnsProperty(), "activeGoalGrid.columns", 2);
      if (!this.mainPanelController.getProductionPanel().isVisible()) {
         Platform.runLater(() -> this.mainPanelController.getSlidingPanel().toggleSlidingPanel(this.mainPanelController.getProductionPanel()));
      }

      this.betweenBatchManager.switchToStartPanel();
      this.resetProfiles();
      this.mainPanelController.getPreparationPanel().resetLots(this.appModel, this.selectedLotIds);
   }

   private void installWhatsNewHandler() {
      int lastSeenPageIdentifier = this.preferences.getPreferences().getInt("whatsNewIdentifier", -1);
      WhatsNewDialog whatsNewDialog = WhatsNewDialog.builder(
            () -> this.preferences.getPreferences().getInt("whatsNewIdentifier", -1), i -> this.preferences.getPreferences().putInt("whatsNewIdentifier", i)
         )
         .withTitle("Roasting Intelligence 5.0: What’s new?")
         .centered(true)
         .build()
         .lookupPages("com.cropster.roastingintelligence.ui.whatsnew.pages");
      if (lastSeenPageIdentifier == -1 || lastSeenPageIdentifier < whatsNewDialog.lastPageIdentifier()) {
         Scene scene = this.application.getStage().getScene();
         PlatformScheduler.schedule(
            () -> {
               WindowManager wm = WindowManager.get();
               if (wm.showingWindows().isEmpty()
                  && this.state.getPhase() == JavaFxApplicationState.Phase.SETUP
                  && wm.register(whatsNewDialog, "WHATS_NEW_WINDOW_ID")) {
                  whatsNewDialog.showAt(scene);
               }
            },
            777,
            TimeUnit.MILLISECONDS
         );
      }
   }

   private boolean isFlickPredictionPossible() {
      return this.appModel.getPreferences().isFirstCrackPredictionEnabled()
         && this.appModel.getPreferences().isFlickPredictionEnabled()
         && this.roastingModel.getCurves().containsKey("beanTemperature")
         && this.roastingModel.getCurves().containsKey("gas");
   }

   private void switchToProgressPanel() {
      assert Platform.isFxApplicationThread();

      this.mainPanelController.disposeBetweenBatchChart();
      this.roastChartWrapper = this.createChartWrapper();
      CommentPopupHeader header = new CommentPopupHeader();
      CommentPopupContent commentPopupContent = new CommentPopupContent(this.roastingModel.getPreferences(), this.roastingModel, header);
      if (this.roastingModel.getPreferences().isCommentingByTemp()) {
         commentPopupContent.initTempInput(this.roastingModel.getPreferences().getPreferredUnit(Unit.CELSIUS));
      }

      commentPopupContent.setCommentValueProvider(new CommentPopupContent.RoastingModelValueProvider(this.roastingModel));
      this.commentPopOver = new PopOverHandler<>(header, commentPopupContent, this::showCommentPopup);
      this.roastChartWrapper.getChartViewer().addEventHandler(MouseEvent.MOUSE_PRESSED, new WeakEventHandler(this.commentPopOver));
      this.roastChartWrapper.getChartViewer().addEventHandler(MouseEvent.MOUSE_RELEASED, new WeakEventHandler(this.commentPopOver));
      Preferences prefs = this.appModel.getPreferences();
      PredictionPanel predictionPanel = this.roastChartWrapper.getPredictionPanel();
      predictionPanel.beanPredictionOnProperty().set(prefs.isCurvePredictionEnabled());
      predictionPanel.firstCrackPredictionOnProperty().set(prefs.isFirstCrackPredictionEnabled());
      predictionPanel.flickPredictionOnProperty().set(prefs.isFirstCrackPredictionEnabled() && prefs.isFlickPredictionEnabled());

      for (Channel channel : this.appModel.getVisibleChannels(Channel.Visibility.CHART)) {
         CurveModel curveModel = this.roastingModel.getCurveModel(channel);
         this.roastChartWrapper.getChart().putPrimaryCurveModel(curveModel, channel.getColor());
         CurveModel riseRateModel = null;
         if (channel.isRorEnabled() && channel.isRorSupported()) {
            riseRateModel = this.roastingModel.getRorModel(channel);
            this.roastChartWrapper.getChart().putSecondaryCurveModel(riseRateModel, channel.getColor().brighter());
         }

         if (prefs.isCurvePredictionEnabled() && prefs.getCurvePredictionRequestInterval() > 0 && curveModel.getName().equals("beanTemperature")) {
            this.roastChartWrapper.getChart().setPredictionColor(channel.getColor());
            StandardCurveModel predictionModel = new StandardCurveModel("beanTemperaturePrediction", curveModel.getUnit());
            CurveModel filterModel = this.roastingModel.getFilteredModels().get("beanTemperature");
            CurveModel sourceModel = Optional.ofNullable(filterModel)
               .map(model -> model.getModel(RemoveDuplicatesCurveModel.class))
               .<CurveModel>map(
                  duplicateModel -> {
                     SnapshotCurveModel predictionCacheModel = new SnapshotCurveModel(predictionModel);
                     OverwriteCurveModel overwriteCurveModel = new OverwriteCurveModel(
                        filterModel,
                        predictionCacheModel,
                        () -> duplicateModel.getValues().isEmpty() ? null : duplicateModel.getLastStepTime() + 1L,
                        () -> duplicateModel.getSource().getValues().isEmpty() ? null : duplicateModel.getSource().getValues().lastKey()
                     );
                     duplicateModel.setLastStepTimeListener(lastStepTime -> predictionCacheModel.clear());
                     return overwriteCurveModel;
                  }
               )
               .orElse(curveModel);
            CurvePredictionListener predictionListener = new CurvePredictionListener(this.roastingManager, this.roastingModel, predictionModel, sourceModel);
            predictionListener.setRequestInterval((long)prefs.getCurvePredictionRequestInterval() * 1000L);
            predictionListener.getEnabledProperty().bind(predictionPanel.beanPredictionOnProperty());
            sourceModel.addListener(predictionListener);
            this.roastChartWrapper.getChart().putPrimaryCurveModel(new SkipCurveModel(predictionModel), channel.getColor());
            predictionListener.getEnabledProperty().addListener((v, o, n) -> {
               if (!n) {
                  curveModel.removeListener(predictionListener);
                  predictionListener.clear();
                  this.roastChartWrapper.getChart().removePredictionAnnotation();
               } else {
                  curveModel.addListener(predictionListener);
               }
            });
            this.curvePredictionEnabledProperty = predictionListener.getEnabledProperty();
            if (channel.isRorEnabled() && channel.isRorSupported()) {
               CombinedCurveModel combinedModel = new CombinedCurveModel(curveModel, predictionModel);
               predictionModel.addListener(combinedModel);
               CurveModel riseRatePredictionModel = new RorCurveModel(combinedModel, prefs.getRorSmoothing());
               riseRatePredictionModel = new ScalingCurveModel(riseRatePredictionModel, prefs.getRor());
               riseRatePredictionModel = new MovingAverageCurveModel(riseRatePredictionModel, 4);
               TailCurveModel tailCurveModel = new TailCurveModel(
                  riseRatePredictionModel, () -> predictionModel.getValues().isEmpty() ? null : predictionModel.getValues().firstKey()
               );
               this.roastChartWrapper.getChart().putSecondaryCurveModel(new SkipCurveModel(tailCurveModel), channel.getColor().brighter());
            }
         }

         if (prefs.isFirstCrackPredictionEnabled() && curveModel.getName().equals("beanTemperature")) {
            CurveModel filledCurve = (CurveModel)(curveModel.getModel(LinearInterpolationCurveModel.class) == null
               ? new LinearInterpolationCurveModel(curveModel)
               : curveModel);
            FirstCrackPredictionListener firstCrackPredictionListener = new FirstCrackPredictionListener(
               this.roastingManager, this.roastingModel, this.roastChartWrapper.getChart(), filledCurve
            );
            filledCurve.addListener(firstCrackPredictionListener);
            firstCrackPredictionListener.getSmartPredictionsEnabledProperty().bind(predictionPanel.firstCrackPredictionOnProperty());
            this.firstCrackPredictionEnabledProperty = firstCrackPredictionListener.getSmartPredictionsEnabledProperty();
            if (this.isFlickPredictionPossible()) {
               try {
                  FlickPredictionListener flickPredictionListener = new FlickPredictionListener(
                     this.roastingManager, this.roastingModel, firstCrackPredictionListener, this.roastChartWrapper.getFlickPredictionPanel()
                  );
                  filledCurve.addListener(flickPredictionListener);
                  flickPredictionListener.getSmartPredictionsEnabledProperty().bind(predictionPanel.flickPredictionOnProperty());
                  this.flickPredictionEnabledProperty = flickPredictionListener.getSmartPredictionsEnabledProperty();
               } catch (IllegalArgumentException var16) {
                  this.logger.error("Error enabling flick prediction", var16);
               }
            }
         }
      }

      this.updateRoastChart();
      this.roastChartWrapper.updateCrosshairs();
      if (AppModel.AutoStartMode.AUTO_DETECTION.equals(this.appModel.getAutoStartMode())) {
         this.roastChartWrapper.getChart().setTimeRange(-60L, prefs.getTime());
         this.roastChartWrapper.getChart().setDetectingMarker();
      }

      if (this.roastingModel.getCurrentReference() != null) {
         this.setRoastingReference(this.roastingModel, this.roastingModel.getCurrentReference());
      }

      this.mainPanelController.getCenterPanel().setCenter(this.roastChartWrapper);
      this.mainPanelController.getMessagePanel().setVisible(false);
      this.mainPanelController.getCommentsPanel().setVisible(true);
      predictionPanel.beanPredictionVisibleProperty().set(prefs.isCurvePredictionEnabled());
      predictionPanel.firstCrackPredictionVisibleProperty().set(prefs.isFirstCrackPredictionEnabled());
      predictionPanel.flickPredictionVisibleProperty().set(prefs.isFirstCrackPredictionEnabled() && prefs.isFlickPredictionEnabled());
      predictionPanel.beanPredictionDisableProperty().bind(this.state.phaseProperty().isEqualTo(JavaFxApplicationState.Phase.STOPPED));
      predictionPanel.firstCrackPredictionDisableProperty().bind(this.state.phaseProperty().isEqualTo(JavaFxApplicationState.Phase.STOPPED));
      predictionPanel.flickPredictionDisableProperty().bind(this.state.phaseProperty().isEqualTo(JavaFxApplicationState.Phase.STOPPED));
      this.mainPanelController.getPrimaryButton().disableProperty().unbind();
      this.mainPanelController.getPrimaryButton().graphicProperty().unbind();
      this.mainPanelController.getPrimaryButton().textProperty().unbind();
      this.mainPanelController.getPrimaryButton().tooltipProperty().unbind();
      this.mainPanelController.getPrimaryButton().onActionProperty().unbind();
      this.mainPanelController.getPrimaryButton().setDisable(false);
      if (AppModel.AutoStartMode.AUTO_DETECTION.equals(this.appModel.getAutoStartMode())) {
         this.mainPanelController.getPrimaryButton().setGraphic(Icon.FORCE_START.load());
         this.mainPanelController.getPrimaryButton().setText(org.cropster.roastingintelligence.util.Messages.m_("Force start"));
         this.mainPanelController.getPrimaryButton().setOnAction(ex -> this.forceStart());
         this.mainPanelController
            .getPrimaryButton()
            .setTooltip(new Tooltip(org.cropster.roastingintelligence.util.Messages.m_("Force start"), KeyboardShortcut.CTRL_ENTER));
      } else {
         this.mainPanelController.getPrimaryButton().setGraphic(Icon.STOP.load());
         this.mainPanelController.getPrimaryButton().setText(org.cropster.roastingintelligence.util.Messages.m_("Stop"));
         this.mainPanelController.getPrimaryButton().setOnAction(this.createStopHandler());
         this.mainPanelController
            .getPrimaryButton()
            .setTooltip(new Tooltip(org.cropster.roastingintelligence.util.Messages.m_("Stop"), KeyboardShortcut.SHIFT_ENTER));
      }

      this.mainPanelController.getPrimaryButton().setOnMouseClicked(null);
      this.mainPanelController.getSecondaryButton().disableProperty().unbind();
      this.mainPanelController.getSecondaryButton().visibleProperty().unbind();
      this.mainPanelController.getSecondaryButton().graphicProperty().unbind();
      this.mainPanelController.getSecondaryButton().textProperty().unbind();
      this.mainPanelController.getSecondaryButton().tooltipProperty().unbind();
      this.mainPanelController.getSecondaryButton().onActionProperty().unbind();
      this.mainPanelController.getSecondaryButton().setGraphic(Icon.CLOSE.load());
      this.mainPanelController.getSecondaryButton().setText(org.cropster.roastingintelligence.util.Messages.m_("Abort"));
      this.mainPanelController
         .getSecondaryButton()
         .setTooltip(new Tooltip(org.cropster.roastingintelligence.util.Messages.m_("Abort"), KeyboardShortcut.CTRL_ESC));
      this.mainPanelController.getSecondaryButton().getStyleClass().add("button--plain");
      this.mainPanelController.getSecondaryButton().setOnAction(ex -> this.abortRoasting());
      this.mainPanelController.getSecondaryButton().setDisable(false);
      this.mainPanelController.getSecondaryButton().setVisible(true);
      this.mainPanelController.getTimerPanel().setVisible(true);
      this.mainPanelController.getTimerLabel().setVisible(true);
      this.mainPanelController.getSecondaryTimerLabel().setText(org.cropster.roastingintelligence.util.Messages.m_("n/a"));
      this.mainPanelController.getSecondaryTimerLabel().setTime(null);
      this.mainPanelController.getPercentageLabel().setText(org.cropster.roastingintelligence.util.Messages.m_("Dev. time ratio"));
      this.mainPanelController.getPercentageLabel().setPercentage(0.0);
      this.mainPanelController.initCommentsPanel(this.appModel.getHints());
      this.mainPanelController.hideBetweenBatchTimer();
      if (this.mainPanelController.getSlidingPanel().isVisible()) {
         this.mainPanelController.getSlidingPanel().toggleSlidingPanel(this.mainPanelController.getProductionPanel());
      }

      this.betweenBatchManager.switchToProgressPanel();
      this.mainPanelController.getCommentsSidebarButton().setVisible(true);
      this.mainPanelController.getCommentsSidebarButton().setDisable(false);
      this.mainPanelController.getCommentsSlidingPanel().init(this.roastingModel);
      this.mainPanelController.getReferencePanel().setVisible(prefs.isSmartGoalsAndReferencesEnabled());
   }

   private void showCommentPopup(PopOverHandler<CommentPopupContent> popup, Point2D location) {
      long time = this.roastingModel.getDuration();
      Measure temp = this.roastingModel.getTempAtTime(time);
      CommentPopupContent commentPopupContent = popup.getContentNode();
      commentPopupContent.getTimeInput().setValue(time > 0L ? time : 0L);
      commentPopupContent.getTempInput().setValue(temp != null ? temp.getAmount() : null);
      commentPopupContent.getTempInput().setVisible(this.appModel.getPreferences().isCommentingByTemp() && temp != null);
      commentPopupContent.updateEventButtons(this.appModel.getPreferences().getEnabledEventsPopup());
      commentPopupContent.toFront();
      popup.show(this.roastChartWrapper.getChartViewer(), location);
   }

   private void showProfilePopup(Node node) {
      if (this.profilePopup.isShowing()) {
         this.profilePopup.hide();
      } else {
         ListView<Profile> profileView = new ListView<Profile>() {
            protected Skin<?> createDefaultSkin() {
               return new AccessibleListViewSkin(this);
            }
         };
         profileView.getStyleClass().add("selectable-list-view");
         profileView.setFixedCellSize(24.0);
         profileView.setPrefWidth(300.0);
         profileView.setPrefHeight(Math.min(362.0, 24.0 * (double)this.roastingModel.getData().getProfiles().size() + 2.0));

         for (Profile profile : this.roastingModel.getData().getProfiles().values()) {
            profileView.getItems().add(profile);
            if (profile.getId().equals(this.roastingModel.getRoast().getProfile())) {
               profileView.getSelectionModel().select(profile);
            }
         }

         profileView.getSelectionModel().selectedItemProperty().addListener((s, o, n) -> {
            if (o != n) {
               this.switchProfile(n);
            }

            this.profilePopup.hide();
         });
         this.profilePopup.getContent().add(profileView);
         this.profilePopup.sizeToScene();
         Point2D offset = node.localToScreen(0.0, -2.0);
         this.profilePopup.show(node, offset.getX(), offset.getY());
         profileView.requestFocus();
         int selectedIndex = profileView.getSelectionModel().getSelectedIndex();
         if (selectedIndex >= 0 && ((AccessibleListViewSkin)profileView.getSkin()).accessVbar().isVisible()) {
            profileView.scrollTo(selectedIndex);
         }
      }
   }

   private synchronized void switchProfile(Profile profile) {
      this.logger.info("Switching profile to {}", profile);
      Roast currentReference = this.roastingModel.getCurrentReference();
      if (currentReference != null) {
         this.removeReference(currentReference);
      }

      this.roastingModel.getRoast().setProfile(profile.getId());
      Roast newReference = this.roastingModel.getData().getReferences().get(profile.getId());
      if (newReference != null) {
         this.setRoastingReference(this.roastingModel, newReference);
      }

      this.updateStatus(profile);
      this.alarmController.getAlarmService().refresh();
      this.liveGoalController.getLiveGoalService().setCurrentProfile(profile, newReference, this.roastingModel.getData().getGoals());
   }

   private synchronized void updateStatus(Profile profile) {
      assert Platform.isFxApplicationThread();

      this.mainPanelController.getStatusLabel().getChildren().clear();
      this.mainPanelController.getSwitchProfilePanel().setVisible(this.state.getRoastInProgress());
      if (this.state.getRoastInProgress()) {
         this.mainPanelController
            .getProfileLabel()
            .setText(profile != null ? profile.getName() : org.cropster.roastingintelligence.util.Messages.m_("No profile"));
         Text lotText = new Text(DataUtil.formatSourceLotsInfo(this.roastingModel.getData(), this.roastingModel.getRoast()));
         this.mainPanelController.getStatusLabel().getChildren().add(lotText);
      }
   }

   private void switchToFinishPanel() {
      assert Platform.isFxApplicationThread();

      this.state.setRoastConsistent(false);
      this.roastChartWrapper.getChart().setEndMarker(this.roastingModel.getDuration());
      if (this.appModel.getPreferences().isBetweenBatchProtocolEnabled()) {
         this.mainPanelController.showBetweenBatchTimer(NanoTime.currentTimeMillis() - this.appModel.getBetweenBatchModel().getStartDate().getTime());
      } else {
         this.mainPanelController.hideBetweenBatchTimer();
      }

      this.mainPanelController.getPrimaryButton().setGraphic(Icon.UPLOAD.load());
      this.mainPanelController.getPrimaryButton().setText(org.cropster.roastingintelligence.util.Messages.m_("Save"));
      this.mainPanelController.getPrimaryButton().setOnAction(e -> this.finishRoasting());
      this.mainPanelController
         .getPrimaryButton()
         .disableProperty()
         .bind(
            this.finishPanel
               .startTimeProperty()
               .isNull()
               .or(this.finishPanel.endTimeProperty().isNull())
               .or(this.finishPanel.startWeightProperty().isNull())
               .or(this.finishPanel.endWeightProperty().isNull())
         );
      this.mainPanelController
         .getPrimaryButton()
         .setTooltip(new Tooltip(org.cropster.roastingintelligence.util.Messages.m_("Save"), KeyboardShortcut.SHIFT_CTRL_ENTER));
      this.finishPanel.setStartTime(0L);
      this.finishPanel.setEndTime(this.roastingModel.getDuration());
      this.finishPanel.setUnit(this.roastingModel.getRoast().getStartWeight().getUnit());
      this.finishPanel.setStartWeight(this.roastingModel.getRoast().getStartWeight().getAmount());
      this.finishPanel.setEndWeight(this.roastingModel.getRoast().getEndWeight().getAmount());
      this.finishPanel.setRoastValue1(null);
      this.finishPanel.setRoastValue1Unit(this.appModel.getPreferences().getRoastValueUnit());
      this.finishPanel.setRoastValue2(null);
      this.finishPanel.setRoastValue2Unit(this.appModel.getPreferences().getRoastValue2Unit());
      this.finishPanel.setNotes(null);
      this.finishPanel.installValidators(this.roastingModel.getMaxStartWeight());
      this.finishPanel.endTimeProperty().addListener((s, o, n) -> this.appModel.getBetweenBatchModel().setOffset(n - this.roastingModel.getDuration()));
      this.mainPanelController.getCenterPanel().setLeft(this.finishPanel);
   }

   private void resetProfiles() {
      PreparationPanel panel = this.mainPanelController.getPreparationPanel();
      Long selectedProfileId = this.appModel.getSelectedProfileId();
      panel.getProfileSearch().reset(this.appModel.getData().getProfiles().values());
      panel.getProfileSelect().getSelectionModel().select(this.appModel.getData().getProfiles().get(selectedProfileId));
   }

   private void selectSchedule(Scheduled scheduled) {
      this.appModel.setCurrentScheduledId(scheduled.getId());
      this.state.setScheduled(scheduled.getId());
      this.mainPanelController.getPreparationPanel().setSelectedScheduled(scheduled, this.appModel);
   }

   private void resetSchedule() {
      this.appModel.setCurrentScheduledId(null);
      this.state.setScheduled(null);
   }

   private MeasurementReaderWriter findWritableSource(String measurementName) {
      for (MeasurementSource source : this.measurementHub.getMeasurementSources()) {
         if (source instanceof MeasurementReaderWriter) {
            CurveModel curve = this.roastingModel.getCurves().get(measurementName);
            MeasurementReaderWriter measurementReaderWriter = (MeasurementReaderWriter)source;
            if (curve != null && (!curve.getValues().isEmpty() || measurementReaderWriter.getFactory().supportsAutoMode(measurementName))) {
               return measurementReaderWriter;
            }
         }
      }

      return null;
   }

   private void initWritableSource(
      String replayMeasurement, String controlMeasurement, Unit sourceUnit, MeasurementReaderWriter writableSource, long delayInSeconds
   ) {
      if (delayInSeconds >= 0L) {
         Roast replayRoast = this.roastingModel.getData().getReferences().get(this.appModel.getSelectedProfileId());
         if (replayRoast != null) {
            if (writableSource.getFactory().supportsAutoMode(controlMeasurement)) {
               Boolean autoMode = writableSource.getAutoMode(controlMeasurement);
               if (autoMode == null
                  || (
                     autoMode
                        ? !controlMeasurement.equals(writableSource.getFactory().getAutoModeMeasurement(controlMeasurement))
                        : !controlMeasurement.equals(writableSource.getFactory().getManualModeMeasurement(controlMeasurement))
                  )) {
                  return;
               }
            }

            Curve curve = replayRoast.getCurves().get(replayMeasurement);
            Unit replayUnit = curve.getUnit();
            if (curve != null && curve.containsKey(Long.valueOf(0L))) {
               Double value = sourceUnit.isCompatible(Unit.CELSIUS)
                  ? sourceUnit.from(BigDecimal.valueOf(curve.get(Long.valueOf(0L))), replayUnit).doubleValue()
                  : curve.get(Long.valueOf(0L));
               this.logger.info("Scheduling {} reset to {} after {} seconds", new Object[]{controlMeasurement, value, delayInSeconds});
               this.controlFutures
                  .add(
                     this.roastingManager.getExecutor().schedule(new WriteCommand(writableSource, controlMeasurement, value), delayInSeconds, TimeUnit.SECONDS)
                  );
            }
         }
      }
   }

   @Override
   public void statusChanged(MeasurementSource source) {
      Platform.runLater(
         () -> {
            BoolMap<MeasurementSource> ready = new BoolMap<>();

            for (MeasurementSource measurementSource : this.measurementHub.getMeasurementSources()) {
               if (measurementSource.isReady() && measurementSource instanceof CheckableSource) {
                  Set<Channel> channels = this.appModel
                     .getChannelsBySource((Class<? extends MeasurementSourceFactory>)measurementSource.getFactory().getClass());
                  String message = ((CheckableSource)measurementSource).checkVersion(channels);
                  if (message != null) {
                     this.publishMessage(MessageListener.MessageType.ERROR, message);
                  }
               }

               ready.put(measurementSource, Boolean.valueOf(measurementSource.isReady()));
            }

            boolean connected = OfflineBootstrap.any(ready);
            String message = connected
               ? org.cropster.roastingintelligence.util.Messages.p_("Connector operational", "Connectors operational", ready.size())
               : org.cropster.roastingintelligence.util.Messages.p_("Connector not operational", "Connectors not operational", ready.size());
            if (ready.size() > 1) {
               message = message + String.format(" (%d/%d)", ready.getTrueCount(), ready.size());
            }

            String machine = this.appModel.getSelectedMachine() != null
               ? this.appModel.getSelectedMachine().toString()
               : org.cropster.roastingintelligence.util.Messages.m_("No machine selected");
            message = machine + "\n" + message;
            if (connected) {
               this.mainPanelController.setConnected(message);
            } else {
               this.mainPanelController.setDisconnected(message);
            }
         }
      );
   }

   @Override
   public void start(List<String> var1) throws Throwable {
      this.setupWarnings = var1;
      OfflineBootstrap.start(this);
   }

   @Override
   public boolean startRoasting() {
      assert Platform.isFxApplicationThread();

      if (this.roastingModel != null) {
         throw new IllegalStateException("Roasting model already set");
      } else {
         NanoTime.sync();

         for (Channel channel : this.appModel.getActiveChannels()) {
            this.measurementHub.getCurveModel(channel).getRootModel().getValues().clear();
            this.measurementHub.getCurveModel(channel).getRootModel().fireCurveChanged();
         }

         if (this.appModel.getSelectedMachine() == null) {
            this.publishMessage(MessageListener.MessageType.CRITICAL, org.cropster.roastingintelligence.util.Messages.m_("Please select a machine first!"));
            return false;
         } else {
            Map<Lot, Measure> lotComponents = this.mainPanelController.getPreparationPanel().getLotComponents();
            if (lotComponents.isEmpty()) {
               this.publishMessage(
                  MessageListener.MessageType.CRITICAL, org.cropster.roastingintelligence.util.Messages.m_("You have to select a lot for every component!")
               );
               return false;
            } else {
               BoolMap<Channel> channelsActive = new BoolMap<>();

               for (Channel channel : this.appModel.getActiveChannels()) {
                  channelsActive.put(channel, Boolean.valueOf(channel.isVisible(Channel.Visibility.CHART) && channel.isVisible(Channel.Visibility.STATUS)));
               }

               if (channelsActive.getTrueCount() == 0) {
                  this.publishMessage(
                     MessageListener.MessageType.CRITICAL,
                     org.cropster.roastingintelligence.util.Messages.m_(
                        "Configure at least one channel to start roasting!\nOpen \"Preferences -> Machine\" and select a probe type, a curve name and enable visibility."
                     )
                  );
                  return false;
               } else {
                  int i = 0;
                  this.selectedLotIds = new Long[lotComponents.size()];

                  for (Lot lot : lotComponents.keySet()) {
                     this.selectedLotIds[i++] = lot.getId();
                  }

                  this.preferences.setArray("RoastingStartPanel.selectedLots", this.selectedLotIds);
                  this.preferences.setId("selectedProfileId", this.appModel.getSelectedProfileId());
                  Number startWeight = this.mainPanelController.getPreparationPanel().getStartWeight();
                  this.roastingModel = this.roastingManager.startRoasting(this.appModel, this.measurementHub, startWeight, this);
                  this.roastingModel.addCommentListener(new CommentListener() {
                     @Override
                     public void commentAdded(RoastingComment comment) {
                        Platform.runLater(() -> JavaFxController.this.addRoastingComment(comment));
                     }

                     @Override
                     public void commentUpdated(RoastingComment comment) {
                        Platform.runLater(() -> JavaFxController.this.updateRoastingComment(comment));
                     }

                     @Override
                     public void commentRemoved(RoastingComment comment) {
                        Platform.runLater(() -> JavaFxController.this.removeRoastingComment(comment));
                     }
                  });

                  for (Entry<Lot, Measure> entry : lotComponents.entrySet()) {
                     this.roastingModel.addSelectedLot(entry.getKey(), entry.getValue());
                     if (this.logger.isInfoEnabled()) {
                        this.logger.info("Roasting {} / {}", entry.getKey(), entry.getValue());
                     }
                  }

                  this.roastingModel.getRoast().getLot().getSources().addAll(this.roastingModel.createTransactions());
                  this.state
                     .setPhase(
                        AppModel.AutoStartMode.AUTO_DETECTION.equals(this.appModel.getAutoStartMode())
                           ? JavaFxApplicationState.Phase.DETECTING
                           : JavaFxApplicationState.Phase.ROASTING
                     );
                  Map<MeasurementSource, Map<String, Double>> valuesBySource = new HashMap<>();

                  for (Channel channel : this.roastingModel.getChannels()) {
                     if (this.roastingModel.getCurveModel(channel).getValue(0L) == null) {
                        for (MeasurementSource source : this.measurementHub.getMeasurementSources()) {
                           Map<String, Double> values = valuesBySource.get(source);
                           if (values == null) {
                              values = source.getValues();
                              valuesBySource.put(source, values);
                           }

                           if (values.containsKey(channel.getName()) && source.getFactory().getClass().equals(channel.getSourceFactory())) {
                              double initialValue = MathUtil.roundHalfToEven(values.get(channel.getName()), 1);
                              if (source.isValid(channel.getName(), initialValue)) {
                                 this.roastingModel.put(channel, this.roastingModel.getInitialDate(), initialValue);
                              }
                           }
                        }
                     }
                  }

                  this.measurementHub.addModel(this.roastingModel);
                  Measure ambientTemperature = this.measurementHub.getAmbientTemperature();
                  Preferences prefs = this.appModel.getPreferences();
                  if (ambientTemperature != null) {
                     this.roastingModel.getRoast().getMeasures().put("ambientTemperature", ambientTemperature.toUnit(prefs.getPreferredUnit(Unit.CELSIUS)));
                  }

                  this.switchToProgressPanel();
                  if (prefs.isSoundAlarmOnEventsEnabled()) {
                     this.alarmController.getAlarmService().attach(this.roastingModel, RoastingPhase.ROASTING);
                     if (this.mainPanelController.getAlarmsSlidingPanel().isManageSelected()) {
                        this.mainPanelController.getToggleButtonAlarmsLog().fire();
                     }
                  }

                  if (prefs.isSmartGoalsAndReferencesEnabled()) {
                     this.liveGoalController.getLiveGoalService().attach(this.roastingModel, RoastingPhase.ROASTING);
                  }

                  return true;
               }
            }
         }
      }
   }

   private Map<String, String> getReplayMeasurements(Collection<Channel> channels, Roast replayRoast) {
      List<Channel> controlChannels = new ArrayList<>();

      for (Channel channel : channels) {
         if (channel.isActive() && channel.getName().matches(".+[cC]ontrol")) {
            controlChannels.add(channel);
         }
      }

      Map<String, String> replayControlMeasurement = new HashMap<>();

      for (Channel controlChannel : controlChannels) {
         if (replayRoast.getCurves().containsKey(controlChannel.getName())) {
            replayControlMeasurement.put(controlChannel.getName(), controlChannel.getName());
         } else {
            MeasurementSourceFactory sourceFactory = RoastingIntelligenceApp.retrieveSingleton(controlChannel.getSourceFactory());
            if (sourceFactory != null) {
               String replayMeasurement = sourceFactory.getAlias(controlChannel.getName());
               if (!controlChannel.getName().equals(replayMeasurement) && replayRoast.getCurves().containsKey(replayMeasurement)) {
                  replayControlMeasurement.put(controlChannel.getName(), replayMeasurement);
               }
            }
         }
      }

      return replayControlMeasurement;
   }

   private void replayRoasting() {
      assert Platform.isFxApplicationThread();

      Data data = this.appModel.getData();
      if (this.appModel.getSelectedProfileId() != null && data.getReferences().containsKey(this.appModel.getSelectedProfileId())) {
         Roast replayRoast = data.getReferences().get(this.appModel.getSelectedProfileId());
         Map<String, String> replayControlMeasurement = this.getReplayMeasurements(this.appModel.getActiveChannels(), replayRoast);
         if (this.startRoasting()) {
            for (Entry<String, String> entry : replayControlMeasurement.entrySet()) {
               String controlMeasurement = entry.getKey();
               String replayMeasurement = entry.getValue();
               if (controlMeasurement.matches(".+[cC]ontrol") && this.appModel.isChannelActive(controlMeasurement)) {
                  Unit sourceUnit = RoastingIntelligenceApp.retrieveSingleton(this.appModel.getChannel(controlMeasurement).getSourceFactory())
                     .getUnit(controlMeasurement);
                  MeasurementReaderWriter writableSource = this.findWritableSource(controlMeasurement);
                  if (writableSource == null) {
                     WindowUtil.showAlert(
                        AlertType.WARNING,
                        this.application.getStage(),
                        org.cropster.roastingintelligence.util.Messages.m_(
                           "Error starting replay for {0}", org.cropster.roastingintelligence.util.Messages.m_(controlMeasurement)
                        ),
                        org.cropster.roastingintelligence.util.Messages.m_("No writable source found to control!")
                     );
                  } else {
                     this.initWritableSource(replayMeasurement, controlMeasurement, sourceUnit, writableSource, 0L);
                     this.state.setReplayStarted(true);
                     this.state.setReplaying(true);
                     long time = NanoTime.currentTimeMillis();
                     int initialDelay = (int)(time / 1000L * 1000L + 1000L - time - 250L);
                     RoastReplayer replayer = new RoastReplayer(
                        replayMeasurement,
                        controlMeasurement,
                        sourceUnit,
                        writableSource,
                        this.roastingModel,
                        replayRoast,
                        c -> this.controlExecutor.execute(c)
                     );
                     this.replayers.add(replayer);
                     this.controlFutures
                        .add(this.roastingManager.getExecutor().scheduleAtFixedRate(replayer, (long)initialDelay, 1000L, TimeUnit.MILLISECONDS));
                  }
               }
            }
         }
      } else {
         WindowUtil.showAlert(
            AlertType.ERROR,
            this.application.getStage(),
            org.cropster.roastingintelligence.util.Messages.m_("No profile"),
            org.cropster.roastingintelligence.util.Messages.m_("No profile loaded!")
         );
      }
   }

   private void forceStart() {
      this.roastingManager.forceStart(this.roastingModel, DateUtil.newDateTruncated(3L));
   }

   private void suspendRoasting() {
      if (!this.replayers.isEmpty()) {
         this.controlFutures.forEach(f -> f.cancel(false));
         this.controlFutures.clear();
         this.replayers.clear();
         Roast replayRoast = this.roastingModel.getData().getReferences().get(this.appModel.getSelectedProfileId());
         Map<String, String> replayControlMeasurement = this.getReplayMeasurements(this.appModel.getActiveChannels(), replayRoast);

         for (Entry<String, String> entry : replayControlMeasurement.entrySet()) {
            String controlMeasurement = entry.getKey();
            String replayMeasurement = entry.getValue();
            Unit sourceUnit = RoastingIntelligenceApp.retrieveSingleton(this.appModel.getChannel(controlMeasurement).getSourceFactory())
               .getUnit(controlMeasurement);
            this.initWritableSource(
               replayMeasurement,
               controlMeasurement,
               sourceUnit,
               this.findWritableSource(controlMeasurement),
               (long)this.appModel.getPreferences().getResetGasThreshold()
            );
         }
      }

      this.state.setReplaying(false);
      this.state.setReplayStarted(false);
      this.alarmController.getAlarmService().detach();
      this.liveGoalController.getLiveGoalService().detach();
   }

   private void abortRoasting() {
      assert Platform.isFxApplicationThread();

      if (this.roastingModel == null) {
         throw new IllegalStateException("No roasting model set");
      } else {
         Alert alert = WindowUtil.createAlert(
            AlertType.WARNING,
            org.cropster.roastingintelligence.util.Messages.m_("Abort roasting"),
            org.cropster.roastingintelligence.util.Messages.m_("Do you want to abort this roast and discard all recorded data?"),
            "button--warning",
            ButtonType.OK,
            ButtonType.CANCEL
         );
         Button okButton = (Button)alert.getDialogPane().lookupButton(ButtonType.OK);
         okButton.setText(org.cropster.roastingintelligence.util.Messages.m_("Discard"));
         Optional<ButtonType> result = WindowUtil.center(this.application.getStage(), alert).showAndWait();
         if (result.get() == ButtonType.OK) {
            if (this.mainPanelController.getPrimaryButton().getOnAction() instanceof JavaFxController.ConfirmStopHandler) {
               JavaFxController.ConfirmStopHandler stopHandler = (JavaFxController.ConfirmStopHandler)this.mainPanelController.getPrimaryButton().getOnAction();
               stopHandler.reset(this.mainPanelController.getPrimaryButton());
            }

            this.suspendRoasting();
            this.roastingManager.abortRoasting(this.roastingModel);
            this.betweenBatchManager.abortRoasting();
            this.notificationBusService.publish(Notification.builder().withProperty("aborted-roast", Boolean.TRUE).build(), "activity-insights");
            this.switchToStartPanel();
            this.state.setRoastConsistent(true);
         }
      }
   }

   @Override
   public void stopRoasting() {
      assert Platform.isFxApplicationThread();

      this.suspendRoasting();
      this.roastingManager.stopRoasting(this.roastingModel);
      this.betweenBatchManager.initBetweenBatchModel(new Date(this.roastingModel.getEndDate().getTime() + 1000L));
      Preferences prefs = this.appModel.getPreferences();
      if (prefs.isSoundAlarmOnEventsEnabled()) {
         this.alarmController.getAlarmService().attach(this.roastingModel, RoastingPhase.ROASTING);
      }

      this.switchToFinishPanel();
      if (prefs.isSmartGoalsAndReferencesEnabled()) {
         this.liveGoalController.getLiveGoalService().attach(new LiveGoalPostRoastingModel(this.roastingModel, this.finishPanel, (startMarker, endMarker) -> {
            this.roastChartWrapper.getChart().setStartMarker(startMarker);
            this.roastChartWrapper.getChart().setEndMarker(endMarker);
         }), RoastingPhase.ROASTING, true);
      }
   }

   @Override
   public void finishRoasting() {
      assert Platform.isFxApplicationThread();

      double start = this.finishPanel.getStartWeight().doubleValue();
      double end = this.finishPanel.getEndWeight().doubleValue();
      if (end < 0.0) {
         WindowUtil.showAlert(
            AlertType.ERROR,
            this.application.getStage(),
            org.cropster.roastingintelligence.util.Messages.m_("Error"),
            org.cropster.roastingintelligence.util.Messages.m_("End weight is negative!")
         );
      } else if (start < end) {
         WindowUtil.showAlert(
            AlertType.ERROR,
            this.application.getStage(),
            org.cropster.roastingintelligence.util.Messages.m_("Error"),
            org.cropster.roastingintelligence.util.Messages.m_("End weight is greater than start weight!")
         );
      } else if (!this.finishPanel.hasValidRequiredValues()) {
         WindowUtil.showAlert(
            AlertType.ERROR,
            this.application.getStage(),
            org.cropster.roastingintelligence.util.Messages.m_("Error"),
            org.cropster.roastingintelligence.util.Messages.m_("Please correct invalid finish panel values before saving.")
         );
      } else {
         Roast roast = this.roastingModel.getRoast();
         this.roastingModel
            .setEndOffset(this.finishPanel.getEndTime() - (this.roastingModel.getEndDate().getTime() - this.roastingModel.getStartDate().getTime()));
         this.roastingModel.setStartOffset(this.finishPanel.getStartTime());
         if (this.roastingModel.getStartOffset() != 0L || this.roastingModel.getEndOffset() != 0L) {
            for (CurveModel curve : this.roastingModel.getCurves().values()) {
               if (!curve.getValues().isEmpty() && curve.getValues().lastKey() > this.roastingModel.getDuration()) {
                  Alert alert = WindowUtil.createAlert(
                     AlertType.WARNING,
                     org.cropster.roastingintelligence.util.Messages.m_("Warning"),
                     org.cropster.roastingintelligence.util.Messages.m_(
                        "Some curves range past the end time of the roast and will be cropped!\nDo you want to proceed and trim the curves?"
                     ),
                     "button--warning",
                     ButtonType.YES,
                     ButtonType.NO
                  );
                  alert.getDialogPane().setMinWidth(600.0);
                  Optional<ButtonType> result = WindowUtil.center(this.application.getStage(), alert).showAndWait();
                  if (result.get() != ButtonType.YES) {
                     return;
                  }
                  break;
               }
            }
         }

         if (this.roastingModel.getCroppedComments().size() != this.roastingModel.getComments().size()) {
            Alert alert = WindowUtil.createAlert(
               AlertType.WARNING,
               org.cropster.roastingintelligence.util.Messages.m_("Warning"),
               org.cropster.roastingintelligence.util.Messages.m_(
                  "One or more comments are out of the time range and will be removed if you don't adjust their times in the comments window!\nOr do you want to proceed and drop them?"
               ),
               "button--warning",
               ButtonType.YES,
               ButtonType.NO
            );
            alert.getDialogPane().setMinWidth(600.0);
            Optional<ButtonType> result = WindowUtil.center(this.application.getStage(), alert).showAndWait();
            if (result.get() != ButtonType.YES) {
               return;
            }
         }

         this.roastingModel
            .getRoast()
            .setStartWeight(new Measure(this.finishPanel.getStartWeight().doubleValue(), this.roastingModel.getPreferences().getWeightUnit()));
         Number roastValue1Amount = this.finishPanel.getRoastValue1();
         if (roastValue1Amount != null) {
            Measure roastValue = new Measure(roastValue1Amount.doubleValue(), this.finishPanel.getRoastValue1Unit());
            roast.getMeasures().put("roastValue", roastValue);
         }

         Number roastValue2Amount = this.finishPanel.getRoastValue2();
         if (roastValue2Amount != null) {
            Measure roastValue2 = new Measure(roastValue2Amount.doubleValue(), this.finishPanel.getRoastValue2Unit());
            roast.getMeasures().put("roastValue2", roastValue2);
         }

         roast = this.roastingManager
            .finalizeRoasting(this.appModel, this.roastingModel, this.finishPanel.getEndWeight().doubleValue(), this.finishPanel.getNotes());
         this.betweenBatchManager.switchBetweenBatchLog(this.appModel, this.roastingModel);
         this.appModel.addRecentRoast(roast);
         this.mainPanelController.populateProductionPanel(this.appModel.getRecentRoasts());
         this.liveGoalController.getLiveGoalService().forceEvaluation(roast);
         this.syncService.uploadRoasts();
         this.syncService.syncGroupMembers();

         for (LotTransaction transaction : this.roastingModel.getRoast().getLot().getSources()) {
            Lot sourceLot = this.roastingModel.getData().getLots().get(transaction.getLot());
            if (sourceLot != null) {
               OfflineBootstrap.setInventoryWeight(
                  sourceLot,
                  OfflineBootstrap.inventoryWeight(sourceLot).subtract(transaction.getWeight().toUnit(OfflineBootstrap.inventoryWeight(sourceLot).getUnit()))
               );
            }
         }

         if (this.appModel.getCurrentScheduleId() != null) {
            Platform.runLater(
               () -> this.mainPanelController
                     .getProductionList()
                     .getItems()
                     .stream()
                     .filter(e -> e instanceof Scheduled)
                     .findFirst()
                     .ifPresent(s -> this.selectSchedule((Scheduled)s))
            );
         }

         this.resetSchedule();
         this.mainPanelController.populateProductionPanel(this.appModel.getScheduledRoasts().values(), this.state);
         this.mainPanelController.getPrimaryButton().disableProperty().unbind();
         this.betweenBatchManager.syncBetweenBatchModel(this.appModel, this.roastingModel);
         this.notificationBusService.publish(Notification.builder().withProperty("roasted-batch", Boolean.TRUE).build(), "activity-insights");
         double startW = roast.getStartWeight() != null ? roast.getStartWeight().getAmount() : 0.0;
         double capacity = this.appModel.getSelectedMachine() != null && this.appModel.getSelectedMachine().getCapacity() != null
            ? this.appModel.getSelectedMachine().getCapacity().getAmount()
            : 0.0;
         if (startW < capacity / 4.0) {
            this.notificationBusService.publish(Notification.builder().withProperty("weight-outlier", Boolean.TRUE).build(), "activity-insights");
         }

         BetweenBatchModel bbModel = this.appModel.getBetweenBatchModel();
         Preferences prefs = this.appModel.getPreferences();
         if (prefs.isSoundAlarmOnEventsEnabled()) {
            this.alarmController.getAlarmService().attach(bbModel, RoastingPhase.BETWEEN_BATCHES);
         }

         if (prefs.isSmartGoalsAndReferencesEnabled()) {
            PreparationPanel pPanel = this.mainPanelController.getPreparationPanel();
            this.liveGoalController
               .getLiveGoalService()
               .attach(new LiveGoalBetweenBatchModel(bbModel, pPanel.getStartWeightEditor()), RoastingPhase.BETWEEN_BATCHES);
         }

         this.switchToStartPanel();
      }
   }

   private boolean confirmClose() {
      WindowManager wm = WindowManager.get();
      wm.lock();
      new HashSet<>(wm.showingWindows()).forEach(w -> w.hide());
      if (this.mainPanel != null) {
         VeilSupport.disableApplicationMenu(this.mainPanel.getScene());
      }

      boolean var5;
      try {
         if (this.state.getRoastInProgress()) {
            Alert alert = WindowUtil.createAlert(
               AlertType.WARNING,
               org.cropster.roastingintelligence.util.Messages.m_("Window closing"),
               org.cropster.roastingintelligence.util.Messages.m_("A roast is in progress! Do you really want to close the application?"),
               "button--warning",
               ButtonType.YES,
               ButtonType.NO
            );
            Optional<ButtonType> result = WindowUtil.center(this.application.getStage(), alert).showAndWait();
            if (result.get() != ButtonType.YES) {
               return false;
            }
         }

         File[] unsynchedFiles = this.appModel.getRoastPersister().getFilesToUpload(RoastFilters.FILE_FILTER);
         if (this.appModel.isRestartRequired() || unsynchedFiles == null || unsynchedFiles.length <= 0) {
            return true;
         }

         Alert alert = WindowUtil.createAlert(
            AlertType.WARNING,
            org.cropster.roastingintelligence.util.Messages.m_("Synchronization pending"),
            org.cropster.roastingintelligence.util.Messages.p_(
                  "An unsynchronized file is still waiting for upload!", "{0} unsynchronized files are still waiting for upload!", unsynchedFiles.length
               )
               + "\n"
               + org.cropster.roastingintelligence.util.Messages.m_("Do you really want to close the application?")
               + "\n\n"
               + org.cropster.roastingintelligence.util.Messages.m_(
                  "Note: all data is stored locally on your computer and will be uploaded the next time you log on and are connected to the internet."
               ),
            "button--warning",
            ButtonType.YES,
            ButtonType.NO
         );
         Optional<ButtonType> result = WindowUtil.center(this.application.getStage(), alert).showAndWait();
         if (result.get() == ButtonType.YES) {
            return true;
         }

         var5 = false;
      } finally {
         if (this.mainPanel != null) {
            VeilSupport.enableApplicationMenu(this.mainPanel.getScene());
         }

         wm.unlock();
      }

      return var5;
   }

   public void logout() {
   }

   private void doClose() {
      if (this.confirmClose()) {
         if (this.mainPanelController != null) {
            ControlToggleBar controlToggleBar = this.mainPanelController.getControlToggleBar();
            FxListenerUtil.removeListener(controlToggleBar.getSlidersToggleButton().selectedProperty(), this.slidersToggleButtonListener);
            FxListenerUtil.removeListener(controlToggleBar.getButtonsToggleButton().selectedProperty(), this.buttonsToggleButtonListener);
            FxListenerUtil.removeListener(controlToggleBar.getControlsToggleButton().selectedProperty(), this.controlsToggleButtonListener);
            if (controlToggleBar.isSlidingPanelVisible()) {
               controlToggleBar.getSelectedButton().fire();
               PlatformScheduler.schedule(this::startShutdown, 777, TimeUnit.MILLISECONDS);
               return;
            }
         }

         this.startShutdown();
      }
   }

   @Override
   public void close() {
      Platform.runLater(this::doClose);
   }

   public synchronized void startShutdown() {
      Task<Void> shutdownTask = new ProgressTask<Void>(this.application.getStage(), org.cropster.roastingintelligence.util.Messages.m_("Shutting down ...")) {
         protected Void call() throws Exception {
            try {
               JavaFxController.this.shutdown();
            } catch (Exception var5) {
               JavaFxController.this.logger.error("Error shutting down", var5);
            } finally {
               Platform.exit();
            }

            return null;
         }
      };
      Thread thread = new Thread(shutdownTask, "Shutdown");
      thread.start();
   }

   protected synchronized void shutdown() throws Exception {
      PlatformScheduler.shutdown();
      this.syncExecutor.shutdown();
      this.controlExecutor.shutdown();
      Analytics analytics = RoastingIntelligenceApp.retrieveSingleton(Analytics.class);
      if (analytics != null) {
         analytics.flush();
      }

      if (this.mainPanelController != null) {
         this.componentManager.store(this.application.getStage(), "RoastingIntelligenceFrame");
         this.stopTimeline();
         this.stopStopRoastingTimer();
         this.syncService.removeMessageListener(this);
         this.appModel.removeCsarDataListener(this);
         this.measurementHub.stop();
         this.syncService.shutdown();
      }

      try {
         this.connection.close();
      } catch (Exception var3) {
         this.logger.error("Error closing connection", var3);
      }

      if (this.shutdownHook != null) {
         this.shutdownHook.run();
      }
   }

   @Override
   public void publishMessage(MessageListener.MessageType type, String message) {
      Platform.runLater(
         () -> {
            switch (type) {
               case FATAL:
                  WindowUtil.showAlert(AlertType.ERROR, this.application.getStage(), org.cropster.roastingintelligence.util.Messages.m_("Fatal error"), message);
                  this.close();
                  break;
               case CRITICAL:
                  WindowUtil.showAlert(
                     AlertType.ERROR, this.application.getStage(), org.cropster.roastingintelligence.util.Messages.m_("Critical Error"), message
                  );
                  break;
               case ERROR:
                  WindowUtil.showAlert(AlertType.ERROR, this.application.getStage(), org.cropster.roastingintelligence.util.Messages.m_("Error"), message);
                  break;
               case WARN:
                  WindowUtil.showAlert(AlertType.WARNING, this.application.getStage(), org.cropster.roastingintelligence.util.Messages.m_("Warning"), message);
                  break;
               case SUCCESS:
                  WindowUtil.showAlert(
                     AlertType.CONFIRMATION, this.application.getStage(), org.cropster.roastingintelligence.util.Messages.m_("Success"), message
                  );
               default:
                  WindowUtil.showAlert(
                     AlertType.INFORMATION, this.application.getStage(), org.cropster.roastingintelligence.util.Messages.m_("Information"), message
                  );
            }
         }
      );
   }

   public void openRoastOnline(Roast roast) {
      String uri = this.appModel.getUri().resolve(String.format("/r/roast/%s", roast.getLot().getId())).toString();
      RoastingIntelligenceApp.showDocument(uri);
   }

   public void addRoastingComment(RoastingComment comment) {
      assert Platform.isFxApplicationThread();

      this.roastChartWrapper.getChart().addComment(comment, "beanTemperature");
   }

   public void updateRoastingComment(RoastingComment comment) {
      assert Platform.isFxApplicationThread();

      this.roastChartWrapper.getChart().updateComment(comment);

      for (AreaAnnotation annotation : this.eventAnnotations.values()) {
         this.roastChartWrapper.getChart().removeAreaAnnotation(annotation);
      }

      this.eventAnnotations.clear();
   }

   public void removeRoastingComment(RoastingComment comment) {
      assert Platform.isFxApplicationThread();

      this.roastChartWrapper.getChart().removeComment(comment, "beanTemperature");

      for (AreaAnnotation annotation : this.eventAnnotations.values()) {
         this.roastChartWrapper.getChart().removeAreaAnnotation(annotation);
      }

      this.eventAnnotations.clear();
   }

   @Override
   public void setShutdownHook(Runnable shutdownHook) {
      this.shutdownHook = shutdownHook;
   }

   private void syncData() {
      if (this.appModel.getSelectedMachine() == null) {
         WindowUtil.showAlert(
            AlertType.ERROR,
            this.application.getStage(),
            org.cropster.roastingintelligence.util.Messages.m_("Error"),
            org.cropster.roastingintelligence.util.Messages.m_("Please select a machine first!")
         );
         this.showMachinePreferences();
      } else {
         ProgressTask<Void> syncTask = new ProgressTask<Void>(this.application.getStage(), this.mainPanelController.getSyncAnimation()) {
            private String errorTitle;

            protected Void call() throws Exception {
               this.errorTitle = org.cropster.roastingintelligence.util.Messages.m_("Upload sync failed");
               JavaFxController.this.syncService.uploadRoastsBlocking();
               if (Thread.interrupted()) {
                  return null;
               } else {
                  this.errorTitle = org.cropster.roastingintelligence.util.Messages.m_("Download sync failed");
                  JavaFxController.this.syncService.syncDataBlocking();
                  if (Thread.interrupted()) {
                     return null;
                  } else {
                     JavaFxController.this.syncService.execute(new VersionChecker(JavaFxController.this, JavaFxController.this.appModel.getVersion()));
                     return null;
                  }
               }
            }

            @Override
            protected void succeeded() {
               JavaFxController.this.publishSynchronizationSuccessful(org.cropster.roastingintelligence.util.Messages.m_("Download sync successful"));
               JavaFxController.this.logger.info("Sync done");
            }

            @Override
            protected void cancelled() {
               JavaFxController.this.logger.info("Syncing data cancelled");
            }

            @Override
            protected void failed() {
               if (JavaFxController.this.logger.isInfoEnabled()) {
                  JavaFxController.this.logger.info("Error syncing data: {}", this.getException().getMessage());
               } else if (JavaFxController.this.logger.isDebugEnabled()) {
                  JavaFxController.this.logger.debug("Error syncing data", this.getException());
               }

               JavaFxController.this.publishSynchronizationFailed(this.errorTitle, this.getException());
            }
         };
         this.syncExecutor.execute(syncTask);
      }
   }

   public void syncData(ActionEvent e) {
      this.syncData();
   }

   private CompletableFuture<Collection<WorkerTask>> syncTasks() {
      Machine selectedMachine = this.appModel.getSelectedMachine();
      CompletableFuture<Collection<WorkerTask>> future = null;
      if (selectedMachine != null) {
         future = this.syncService.syncTasksAsync(selectedMachine.getId(), (WorkerTask[])this.mainPanelController.getTasksList().toArray(new WorkerTask[0]));
         future.whenComplete((r, t) -> {
            if (t != null) {
               this.logger.error("Error loading tasks", t);
            } else {
               this.appModel.setTasks((Collection<WorkerTask>)r);
               this.appModel.fireCsarTasksReceived();
            }
         });
      } else {
         future = CompletableFuture.completedFuture(Set.of());
      }

      return future;
   }

   private void syncTasksBlocking() {
      this.roastingManager.getExecutor().submit(new JavaFxController.TaskUpdateMonitor(this.application.getStage(), this.syncTasks()));
   }

   private CompletableFuture<Collection<Lot>> syncBlends() {
      Machine selectedMachine = this.appModel.getSelectedMachine();
      CompletableFuture<Collection<Lot>> future = null;
      if (selectedMachine != null) {
         future = this.syncService.loadBlendsAsync(selectedMachine.getGroup().getCode(), this.appModel.getUserName());
         future.whenComplete(
            (c, t) -> {
               if (t != null) {
                  this.logger.error("Error loading blends", t);
                  this.publishSynchronizationFailed(org.cropster.roastingintelligence.util.Messages.m_("Blends download sync failed"), t);
               } else {
                  String title = org.cropster.roastingintelligence.util.Messages.m_("Blends list updated");
                  NotificationBusService ns = RoastingIntelligenceApp.retrieveSingleton(NotificationBusService.class);
                  ProductionSidebar productionPanel = this.mainPanelController.getProductionPanel();
                  if (!c.isEmpty()) {
                     String message = c.size() == 1
                        ? org.cropster.roastingintelligence.util.Messages.m_("1 blend was downloaded from our servers")
                        : org.cropster.roastingintelligence.util.Messages.m_("{0,number,#0} blends were downloaded from our servers", c.size());
                     Action showBlendsAction = new ActionBuilder(
                           org.cropster.roastingintelligence.util.Messages.m_("Show blends"), e -> productionPanel.getToggleButtonBlends().fire()
                        )
                        .build();
                     ns.publish(
                        Notification.builder(NotificationType.SUCCESS)
                           .withTitle(title)
                           .withMessage(message)
                           .withProperty("blend-lot-collection", c)
                           .withProperty("tag", "blends-tag")
                           .withProperty("action", showBlendsAction)
                           .withProperty("hide-after", Duration.ZERO)
                           .build(),
                        "blend-collection-downloaded"
                     );
                  } else if (!productionPanel.getBlendView().getItems().isEmpty()) {
                     ns.publish(
                        Notification.builder(NotificationType.WARNING)
                           .withTitle(title)
                           .withMessage(
                              org.cropster.roastingintelligence.util.Messages.m_(
                                 "Changes from the web platform (deletions or rollbacks) have been applied, and no blends remain."
                              )
                           )
                           .withProperty("blend-lot-collection", c)
                           .withProperty("tag", "blends-tag")
                           .withProperty("hide-after", ToastNotificationController.TOAST_NOTIFICATION_DURATION)
                           .build(),
                        "blend-collection-downloaded"
                     );
                  }
               }
            }
         );
      } else {
         future = CompletableFuture.completedFuture(Set.of());
      }

      return future;
   }

   private void syncAll() {
   }

   private void syncAll(ActionEvent var1) {
   }

   public void updateFinalValues(Roast roast) {
      if (!OfflineBootstrap.locallySaved(roast)) {
         throw new IllegalArgumentException("Roast not synced");
      } else {
         this.connection.update(roast);
         this.syncService.loadChecks(roast);
         Platform.runLater(() -> this.mainPanelController.getProductionList().refresh());

         try {
            this.appModel.persistRoast(roast);
         } catch (IOException var3) {
            throw new RuntimeException("Error persisting roast", var3);
         }
      }
   }

   public void updateFinalValues(Double endWeight, Double roastValue1, Double roastValue2) {
   }

   public void setRoastingReference(RoastingModel roastingModel, Roast roast) {
      this.mainPanelController.getCommentsPanel().setVisible(roast != null);
      CurveChart chart = this.roastChartWrapper.getChart();
      Map<String, Curve> activeCurves = roast.getCurves(roastingModel.getChannels());

      for (Entry<String, Curve> entry : activeCurves.entrySet()) {
         String modelName = "Perfect " + org.cropster.roastingintelligence.util.Messages.m_(entry.getKey());
         CurveModel model = new StandardCurveModel(modelName, roast.getStartDate(), entry.getValue().getUnit());
         model.getValues().putAll(entry.getValue());
         Unit preferredUnit = roastingModel.getPreferences().getPreferredUnit(model.getUnit());
         if (preferredUnit != null) {
            model = new ConverterCurveModel(model, preferredUnit);
         }

         Channel channel = roastingModel.getChannel(entry.getKey());
         if (chart != null && channel != null && channel.isActive() && channel.isVisible(Channel.Visibility.CHART)) {
            chart.putPrimaryCurveModel(model, Color.GRAY);
            if (channel.isRorEnabled() && channel.isRorSupported()) {
               CurveModel rorModel = new RorCurveModel(model, roastingModel.getPreferences().getRorSmoothing());
               rorModel = new ScalingCurveModel(rorModel, roastingModel.getPreferences().getRor());
               rorModel = new MovingAverageCurveModel(rorModel, 4);
               chart.putSecondaryCurveModel(rorModel, Color.LIGHT_GRAY);
            }
         }
      }

      if (chart != null) {
         for (RoastingComment comment : roast.getComments()) {
            if (this.appModel.isCommentEnabledInChart(comment)) {
               chart.addComment(
                  comment, Color.GRAY, Defaults.COLOR_COMMENTS, "Perfect " + org.cropster.roastingintelligence.util.Messages.m_("beanTemperature")
               );
            }
         }
      }
   }

   public RoastChartWrapper createChartWrapper() {
      Font fxFont = Font.getDefault();
      java.awt.Font awtFont = new java.awt.Font(fxFont.getName(), 0, (int)Math.floor(fxFont.getSize()));
      CurveChart roastingChart = new CurveChart(2, awtFont);
      roastingChart.setTimeAxisLabel(null);
      this.eventAnnotations.clear();
      RoastChartWrapper wrapper = new RoastChartWrapper();
      wrapper.init(roastingChart, this.isFlickPredictionPossible());
      wrapper.setOnResetAction(e -> this.resetChartAxes());
      AlarmsUtil.addAlarmsBorderHandler(wrapper, this.alarmController);
      return wrapper;
   }

   public void updateRoastChart() {
      CurveChart roastingChart = this.roastChartWrapper != null ? this.roastChartWrapper.getChart() : null;
      if (roastingChart != null) {
         Unit tempUnit = this.appModel.getPreferences().getPreferredUnit(Unit.CELSIUS);
         long minTimeSecs = this.appModel.getPreferences().isAutoDetectionEnabled() ? -60L : 0L;
         roastingChart.setTimeRange(minTimeSecs, this.appModel.getPreferences().getTime());
         roastingChart.setValueAxisLabel(tempUnit, org.cropster.roastingintelligence.util.Messages.m_(tempUnit));
         roastingChart.setValueAutoRange(true);
         roastingChart.setValueRange(tempUnit, this.appModel.getPreferences().getMinTemp(), this.appModel.getPreferences().getMaxTemp());
         roastingChart.setSecondaryValueRange(tempUnit, this.appModel.getPreferences().getMinRiseRate(), this.appModel.getPreferences().getMaxRiseRate());
      }
   }

   public void resetChartAxes() {
      this.updateRoastChart();
      this.betweenBatchManager.resetChartAxes();
   }

   public void removeReference(Roast roast) {
      CurveChart chart = this.roastChartWrapper.getChart();

      for (Entry<String, Curve> entry : roast.getCurves().entrySet()) {
         String modelName = "Perfect " + org.cropster.roastingintelligence.util.Messages.m_(entry.getKey());
         chart.removeCurveModel(modelName);
         chart.removeCurveModel(modelName + "RoR");
      }

      for (RoastingComment comment : roast.getComments()) {
         chart.removeComment(comment, "Perfect " + org.cropster.roastingintelligence.util.Messages.m_("beanTemperature"));
      }

      chart.resetMarkerOffset();
   }

   private void updateAnnotation(String event, Long start, Long end, Long referenceTime, String text, Color color) {
      AreaAnnotation annotation = this.eventAnnotations.get(event);
      if (annotation != null) {
         if (annotation.getX1() == start.doubleValue() && annotation.getX2() == end.doubleValue()) {
            return;
         }

         this.eventAnnotations.remove(event);
         this.roastChartWrapper.getChart().removeAreaAnnotation(annotation);
      }

      try {
         annotation = this.roastChartWrapper.getChart().annotateArea(start, end, referenceTime, text, color);
         this.eventAnnotations.put(event, annotation);
      } catch (Exception var9) {
         throw new RuntimeException("Error adding annotation", var9);
      }
   }

   public void updateModulationStages() {
      Long colorChangeTime = this.roastingModel.getEventTime("colorChange");
      Long firstCrackTime = this.roastingModel.getEventTime("firstCrack");
      Map<String, Long> modulationPhases = Collections.emptyMap();
      if (this.roastingModel.getCurrentReference() != null) {
         modulationPhases = this.roastingModel.getCurrentReference().getModulationPhases();
      }

      if (colorChangeTime == null && firstCrackTime == null) {
         Long referenceTime = modulationPhases.get("drying");
         this.updateAnnotation(
            "colorChange",
            0L,
            this.roastingModel.getDuration(),
            referenceTime,
            org.cropster.roastingintelligence.util.Messages.m_("Drying"),
            Defaults.COLOR_DRYING
         );
      } else {
         if (colorChangeTime != null) {
            Long referenceTime = modulationPhases.get("drying");
            this.updateAnnotation(
               "colorChange", 0L, colorChangeTime, referenceTime, org.cropster.roastingintelligence.util.Messages.m_("Drying"), Defaults.COLOR_DRYING
            );
         } else if (firstCrackTime != null) {
            Long referenceTime = modulationPhases.get("drying");
            this.updateAnnotation(
               "colorChange", 0L, firstCrackTime, referenceTime, org.cropster.roastingintelligence.util.Messages.m_("Drying"), Defaults.COLOR_DRYING
            );
         }

         if (colorChangeTime != null) {
            if (firstCrackTime != null) {
               Long referenceTime = modulationPhases.get("maillard");
               this.updateAnnotation(
                  "firstCrack",
                  colorChangeTime,
                  firstCrackTime,
                  referenceTime,
                  org.cropster.roastingintelligence.util.Messages.m_("Maillard"),
                  Defaults.COLOR_MAILLARD
               );
            } else {
               Long referenceTime = modulationPhases.get("maillard");
               this.updateAnnotation(
                  "firstCrack",
                  colorChangeTime,
                  this.roastingModel.getDuration(),
                  referenceTime,
                  org.cropster.roastingintelligence.util.Messages.m_("Maillard"),
                  Defaults.COLOR_MAILLARD
               );
            }
         }

         if (firstCrackTime != null) {
            Long referenceTime = modulationPhases.get("development");
            this.updateAnnotation(
               "end",
               firstCrackTime,
               this.roastingModel.getDuration(),
               referenceTime,
               org.cropster.roastingintelligence.util.Messages.m_("Development"),
               Defaults.COLOR_DEVELOPMENT
            );
         }
      }
   }

   public void packFiles(File zipFile) throws IOException {
      String[] fileNames = new String[]{
         "alarms.json", "alarms.json.bak", "livegoals.json", "livegoals.json.bak", "log4j2.yaml", "machine.json", "preferences.json", "cache", "log", "outbox"
      };
      FileSystemConfiguration.Directories directories = this.appModel.getDirectories();
      Map<File, ZipEntry> files = new LinkedHashMap<>();

      for (String fileName : fileNames) {
         File file = new File(directories.dataDirectory(), fileName);
         if (!file.exists()) {
            this.logger.warn("{} not found", file.getAbsolutePath());
         } else if (file.isDirectory()) {
            for (File child : file.listFiles()) {
               if (child.isFile()) {
                  files.put(child, new ZipEntry(file.getName() + "/" + child.getName()));
               }
            }
         } else {
            files.put(file, new ZipEntry(file.getName()));
         }
      }

      List<File> logDirectories = new ArrayList<>();
      logDirectories.add(new File("."));
      if (System.getProperty("java.io.tmpdir") != null && !StringUtils.isBlank(System.getProperty("java.io.tmpdir"))) {
         logDirectories.add(new File(System.getProperty("java.io.tmpdir")));
      }

      for (File logDirectory : logDirectories) {
         File[] crashFiles = logDirectory.listFiles(f -> f.getName().startsWith("hs_err_pid") || f.getName().startsWith("replay_pid"));
         if (crashFiles == null) {
            this.logger.warn("Could not resolve working directory {}", logDirectory.getAbsolutePath());
         } else {
            for (File file : crashFiles) {
               files.put(file, new ZipEntry(file.getName()));
            }
         }
      }

      File sentDirectory = new File(directories.dataDirectory(), "sent");
      if (sentDirectory.exists() && sentDirectory.isDirectory()) {
         File[] sentFiles = sentDirectory.listFiles(RoastFilters.FILE_FILTER);
         Arrays.sort(sentFiles, Collections.reverseOrder(new FileComparator()));

         for (int i = 0; i < sentFiles.length && i < 5; i++) {
            files.put(sentFiles[i], new ZipEntry("sent/" + sentFiles[i].getName()));
         }
      }

      byte[] buffer = new byte[1024];
      FileInputStream inStream = null;
      ZipOutputStream zipStream = null;

      try {
         zipStream = new ZipOutputStream(new FileOutputStream(zipFile));

         for (Entry<File, ZipEntry> entry : files.entrySet()) {
            File file = entry.getKey();
            if (!file.getName().endsWith(".lck")) {
               try {
                  zipStream.putNextEntry(entry.getValue());
               } catch (ZipException var18) {
                  this.logger.error("Error adding file to archive", var18);
                  continue;
               }

               inStream = new FileInputStream(file);

               int bytesRead;
               while ((bytesRead = inStream.read(buffer)) > 0) {
                  zipStream.write(buffer, 0, bytesRead);
               }

               inStream.close();
               zipStream.closeEntry();
            }
         }
      } finally {
         IOUtils.closeQuietly(inStream);
         IOUtils.closeQuietly(zipStream);
      }
   }

   public void exportFiles() {
      try {
         FileChooser fileChooser = new FileChooser();
         fileChooser.setInitialDirectory(FileUtils.getUserDirectory());
         fileChooser.setInitialFileName("ri.log.zip");
         fileChooser.setSelectedExtensionFilter(
            new ExtensionFilter(org.cropster.roastingintelligence.util.Messages.m_("ZIP files (.zip)"), new String[]{"zip"})
         );
         File zipFile = fileChooser.showSaveDialog(this.application.getStage());
         if (zipFile != null) {
            this.packFiles(zipFile);
         }
      } catch (Exception var3) {
         this.logger.error("Error zipping files", var3);
         this.publishMessage(MessageListener.MessageType.CRITICAL, org.cropster.roastingintelligence.util.Messages.m_("Error packing files"));
      }
   }

   public void createSupportTicket(String subject, String description) {
      try {
         File tempZipFile = File.createTempFile("ri.log.", ".zip");
         this.packFiles(tempZipFile);
         URL ticketUrl = this.connection.createSupportTicket(subject, description, tempZipFile);
         if (ticketUrl != null) {
            this.logger.info("Support ticket created: {}", ticketUrl);
            String ticketId = "?";
            Matcher matcher = Pattern.compile(".+/(\\d+)").matcher(ticketUrl.toString());
            if (matcher.matches()) {
               ticketId = matcher.group(1);
            }

            this.publishMessage(
               MessageListener.MessageType.INFO,
               org.cropster.roastingintelligence.util.Messages.m_(
                  "A support ticket (#{0}) has been created. A support representative will send you a personal response.\nWe have sent you a ticket confirmation email to {1}. If you want to add comments or an update, simply respond to the confirmation email.",
                  ticketId,
                  this.appModel.getUserName()
               )
            );
            this.mainPanelController.getSupportSlidingPanel().reset();
            return;
         }
      } catch (Exception var7) {
         this.logger.error("Error creating support ticket", var7);
      }

      this.publishMessage(
         MessageListener.MessageType.CRITICAL,
         org.cropster.roastingintelligence.util.Messages.m_(
            "The support ticket could not be created!\nPlease try again or contact us directly\nby sending an email to support@cropster.com."
         )
      );
   }

   @Override
   public void settingsChanged(AppModel appModel) {
      Platform.runLater(() -> {
         if (!this.state.getRoastInProgress()) {
         }

         Preferences prefs = appModel.getPreferences();
         this.state.setReplayEnabled(prefs.isReplayEnabled());
         if (Objects.nonNull(this.roastingModel)) {
            this.startStopRoastingTimer(prefs.getMaxRoastingTime() - this.roastingModel.getDuration().intValue());
         }

         this.updateRoastChart();
         AlarmService alarmService = this.alarmController.getAlarmService();
         Button alarmsSidebarButton = this.mainPanelController.getAlarmsSidebarButton();
         AlarmsSidebar alarmsSlidingPanel = this.mainPanelController.getAlarmsSlidingPanel();
         boolean alarmsEnabled = prefs.isSoundAlarmOnEventsEnabled();
         if (!alarmsEnabled && alarmsSlidingPanel.isVisible()) {
            alarmsSidebarButton.fire();
         }

         alarmsSidebarButton.setVisible(alarmsEnabled);
         SoundManager soundManager = SoundManager.get();
         soundManager.setRepetitions(prefs.getAlarmsSoundRepetitions());
         soundManager.setVolume(prefs.getAlarmsSoundVolume());
         if (alarmsEnabled) {
            alarmService.resume();
         } else {
            alarmService.suspend();
         }

         this.alarmController.setToastNotificationEnabled(prefs.isAlarmsToastNotificationEnabled());
         this.alarmController.setToastNotificationAction(new ActionBuilder(org.cropster.roastingintelligence.util.Messages.m_("Show alerts log"), event -> {
            alarmsSlidingPanel.setManageSelected(false);
            if (!alarmsSlidingPanel.isVisible()) {
               alarmsSidebarButton.fire();
            }
         }).build());
         this.alarmController.toastNotificationSuspendedProperty().unbind();
         this.alarmController.toastNotificationSuspendedProperty().bind(alarmsSlidingPanel.visibleProperty());
         this.betweenBatchManager.settingsChanged(appModel);
         LiveGoalService liveGoalService = this.liveGoalController.getLiveGoalService();
         boolean liveGoalsEnabled = prefs.isSmartGoalsAndReferencesEnabled();
         TitledPane referenceBox = this.mainPanelController.getReferencePanel();
         referenceBox.setVisible(liveGoalsEnabled);
         if (liveGoalsEnabled) {
            liveGoalService.resume();
         } else {
            liveGoalService.suspend();
         }
      });
   }

   @Override
   public void tasksReceived(Collection<WorkerTask> tasks) {
      this.logger.info("{} tasks received", tasks.size());
      Platform.runLater(() -> this.mainPanelController.populateTasksPanel(tasks));
   }

   @Override
   public void dataReceived(Data data) {
      Platform.runLater(
         () -> {
            this.mainPanelController
               .getPreparationPanel()
               .getUserPanel()
               .populateWorkerSelection(
                  data.getWorkers(),
                  this.appModel.getSelectedRoastingPerson() != null ? this.appModel.getSelectedRoastingPerson() : UserPanel.CURRENT_USER_SELECTION
               );
            Long lastScheduleId = this.appModel.getCurrentScheduleId();
            Scheduled lastSchedule = this.appModel.getScheduledRoasts().get(lastScheduleId);
            boolean scheduledSelected = false;
            if (this.mainPanelController.getProductionList().getSelectionModel().getSelectedItem() instanceof Scheduled) {
               scheduledSelected = true;
               this.mainPanelController.getProductionList().getSelectionModel().clearSelection();
            }

            this.mainPanelController.populateProductionPanel(this.appModel.getScheduledRoasts().values(), this.state);
            if (!this.state.getRoastInProgress()) {
               int i = 0;
               Long[] lotIds = new Long[this.mainPanelController.getPreparationPanel().getSelectedLots().length];

               for (Lot lot : this.mainPanelController.getPreparationPanel().getSelectedLots()) {
                  lotIds[i++] = lot != null ? lot.getId() : null;
               }

               this.resetProfiles();
               this.mainPanelController.getPreparationPanel().resetLots(this.appModel, lotIds);
            }

            if (lastSchedule != null) {
               if (scheduledSelected) {
                  this.mainPanelController.getProductionList().getSelectionModel().select(lastSchedule);
               }

               this.selectSchedule(lastSchedule);
            } else if (lastScheduleId != null) {
               this.resetSchedule();
               if (!this.state.getRoastInProgress()) {
                  this.mainPanelController.getPreparationPanel().getProfileSelect().getSelectionModel().clearSelection();
               }
            }

            this.mainPanelController.getProductionList().refresh();
            Notification notification = Notification.builder(NotificationType.SUCCESS).withBinding(this.state.roastInProgressProperty().not()).build();
            this.notificationBusService.publish(notification, "system-status-data-received");
         }
      );
   }

   @Override
   public void roastsUploaded(int count, Throwable ex) {
      if (count > 0) {
         this.publishSynchronizationSuccessful(org.cropster.roastingintelligence.util.Messages.m_("Upload sync successful"));
      } else if (count < 0) {
         this.publishSynchronizationFailed(org.cropster.roastingintelligence.util.Messages.m_("Upload sync failed"), ex);
      }

      Platform.runLater(() -> this.mainPanelController.populateProductionPanel(this.appModel.getRecentRoasts()));
      this.syncTasks();
   }

   private void publishSynchronizationSuccessful(String title) {
      Notification notification = Notification.builder(NotificationType.SUCCESS)
         .withTitle(title)
         .withMessage(org.cropster.roastingintelligence.util.Messages.m_("Roasting Intelligence has been successfully synchronized with our servers."))
         .withPrerequisite(() -> this.toastNotificationController.hide("synchronization-tag"))
         .withProperty("hide-after", ToastNotificationController.TOAST_NOTIFICATION_DURATION)
         .withProperty("tag", "synchronization-tag")
         .build();
      this.notificationBusService.publish(notification, "system-status-synchronization");
   }

   private void publishSynchronizationFailed(String title, Throwable ex) {
      Action action = new ActionBuilder(
            org.cropster.roastingintelligence.util.Messages.m_("Open the System sidebar"), this.mainPanelController.getSystemSidebarButton()::fire
         )
         .build();
      Notification notification = Notification.builder(NotificationType.WARNING)
         .withTitle(title)
         .withMessage(
            org.cropster.roastingintelligence.util.Messages.m_(
               "The synchronization could not be completed at the moment. Please, try again later or check for more information on the System sidebar."
            )
         )
         .withPrerequisite(() -> this.toastNotificationController.hide("synchronization-tag"))
         .withProperty("hide-after", ToastNotificationController.TOAST_NOTIFICATION_DURATION)
         .withProperty("exception", ex)
         .withProperty("tag", "synchronization-tag")
         .withProperty("action", action)
         .withProperty(
            "show-boolean-supplier",
            (BooleanSupplier)() -> !this.mainPanelController.getSystemSidebar().isVisible() || this.mainPanelController.getSystemSidebar().isDetached()
         )
         .build();
      this.notificationBusService.publish(notification, "system-status-synchronization");
   }

   private void startStopRoastingTimer(int delay) {
      this.stopStopRoastingTimer();
      Duration duration = new Duration((double)delay);
      if (!duration.lessThanOrEqualTo(Duration.ZERO) && !duration.equals(Duration.UNKNOWN)) {
         this.stopRoastingTimer = new Timeline(
            new KeyFrame[]{
               new KeyFrame(
                  duration,
                  e -> {
                     if (this.state.getRoastInProgress()) {
                        this.notificationBusService
                           .publish(
                              Notification.of(
                                 NotificationType.INFO,
                                 org.cropster.roastingintelligence.util.Messages.m_("Roast duration exceeded"),
                                 org.cropster.roastingintelligence.util.Messages.m_(
                                    "This roast seems to have run past the set time! You may want to stop roasting."
                                 )
                              ),
                              "roasting"
                           );
                     }
                  },
                  new KeyValue[0]
               )
            }
         );
         this.stopRoastingTimer.play();
      }
   }

   private void stopStopRoastingTimer() {
      if (this.stopRoastingTimer != null) {
         this.stopRoastingTimer.stop();
         this.stopRoastingTimer = null;
      }
   }

   @Override
   public void roastingStarted(RoastingModel roastingModel) {
      this.startStopRoastingTimer(
         this.appModel.getPreferences().getMaxRoastingTime() + (AppModel.AutoStartMode.AUTO_DETECTION.equals(this.appModel.getAutoStartMode()) ? '\uea60' : 0)
      );
      this.controlFutures.stream().forEach(f -> f.cancel(false));
      this.controlFutures.clear();
   }

   @Override
   public void roastingForced(RoastingModel roastingModel) {
      assert Platform.isFxApplicationThread();

      this.startStopRoastingTimer(this.appModel.getPreferences().getMaxRoastingTime() - roastingModel.getDuration().intValue());
      if (this.mainPanelController.getPrimaryButton().graphicProperty().isBound()) {
         this.mainPanelController.getPrimaryButton().graphicProperty().unbind();
      }

      this.mainPanelController.getPrimaryButton().setGraphic(Icon.STOP.load());
      this.mainPanelController.getPrimaryButton().setText(org.cropster.roastingintelligence.util.Messages.m_("Stop"));
      this.mainPanelController.getPrimaryButton().setOnAction(this.createStopHandler());
      this.mainPanelController
         .getPrimaryButton()
         .setTooltip(new Tooltip(org.cropster.roastingintelligence.util.Messages.m_("Stop"), KeyboardShortcut.SHIFT_ENTER));
      this.roastChartWrapper.getChart().setStartMarker(roastingModel.getStartOffset());
      this.state.setPhase(JavaFxApplicationState.Phase.ROASTING);
      this.betweenBatchManager.finishBetweenBatchModel();
   }

   @Override
   public void roastingAborted(RoastingModel roastingModel) {
      this.stopStopRoastingTimer();
   }

   @Override
   public void roastingStopped(RoastingModel roastingModel) {
      assert Platform.isFxApplicationThread();

      this.stopStopRoastingTimer();
      this.roastChartWrapper.getChart().setEndMarker(roastingModel.getDuration());
      this.state.setPhase(JavaFxApplicationState.Phase.STOPPED);
      this.betweenBatchManager.roastingStopped(this.roastChartWrapper, roastingModel);
      PredictionPanel predictionPanel = this.roastChartWrapper.getPredictionPanel();
      predictionPanel.beanPredictionOnProperty().set(false);
      predictionPanel.firstCrackPredictionOnProperty().set(false);
      predictionPanel.flickPredictionOnProperty().set(false);
   }

   @Override
   public void roastingFinalized(RoastingModel roastingModel) {
   }

   @Override
   public void startDetected(RoastingModel model, Date startDate) {
      this.roastingManager.forceStart(model, startDate);
   }

   @Override
   public void truningPointDetected(RoastingModel model, Date turningPointDate) {
      model.setTurningPoint(turningPointDate);
      long time = turningPointDate.getTime() - model.getStartDate().getTime();
      Platform.runLater(() -> this.roastChartWrapper.getChart().addTimeMarker(time, org.cropster.roastingintelligence.util.Messages.m_("Turning point")));
   }

   @Override
   public void endDetected(RoastingModel model, Date endDate) {
      Platform.runLater(
         () -> {
            Alert alert = WindowUtil.createAlert(
               AlertType.WARNING,
               org.cropster.roastingintelligence.util.Messages.m_("Curve analyzer"),
               org.cropster.roastingintelligence.util.Messages.m_("End of roast detected! Do you want to stop roasting?"),
               "button--warning",
               ButtonType.YES,
               ButtonType.NO
            );
            alert.resultProperty().addListener(e -> {
               if (ButtonType.YES.equals(alert.getResult())) {
                  model.setEndDate(endDate);
                  this.stopRoasting();
               }
            });
            WindowUtil.center(this.application.getStage(), alert).show();
         }
      );
   }

   @Override
   public void autoStart() {
      if (this.mainPanelController.getAutoStartReady()) {
         this.logger.info("Roast Auto Start detected");
         if (!this.state.getRoastInProgress()) {
            Platform.runLater(() -> {
               this.mainPanelController.getPrimaryButton().requestFocus();
               this.startRoasting();
            });
         }
      }
   }

   @Override
   public void autoStop() {
      if (this.mainPanelController.getAutoStartReady()) {
         this.logger.info("Roast Auto End detected");
         if (this.roastingModel != null && JavaFxApplicationState.Phase.ROASTING.equals(this.state.getPhase())) {
            Platform.runLater(() -> this.stopRoasting());
         }
      }
   }

   private void updateRoastInfo(Roast roast) {
      if (this.roastInfoWindow.isShowing()) {
         if (roast.getLot().getIdTag().equals(this.roastInfoPanel.getIdTag())) {
            this.showRoastInfo(roast);
         } else {
            Platform.runLater(() -> this.roastInfoWindow.toBack());
         }
      }
   }

   private void markRoastAsReference(final Roast roast) {
      this.roastingManager
         .getExecutor()
         .submit(
            new ProgressTask<Void>(this.application.getStage()) {
               protected Void call() throws Exception {
                  Date markDate = new Date();
                  JavaFxController.this.connection.markReference(roast.getProfile(), roast.getLot().getId());
                  roast.setMarkedAsReferenceDate(markDate);

                  try {
                     JavaFxController.this.appModel.persistRoast(roast);
                     return null;
                  } catch (IOException var3) {
                     throw new RuntimeException("Error persisting roast", var3);
                  }
               }

               @Override
               protected void succeeded() {
                  JavaFxController.this.updateRoastInfo(roast);
                  JavaFxController.this.syncData();
               }

               @Override
               protected void failed() {
                  JavaFxController.this.logger.error("Error marking reference", this.getException());
                  WindowUtil.showAlert(
                     AlertType.ERROR,
                     JavaFxController.this.application.getStage(),
                     org.cropster.roastingintelligence.util.Messages.m_("Error"),
                     org.cropster.roastingintelligence.util.Messages.m_("Error marking reference:") + " " + this.getException().getMessage()
                  );
               }
            }
         );
   }

   private class ConfirmStopHandler implements EventHandler<ActionEvent> {
      private final EventHandler<ActionEvent> delegate;
      private Animation animation;
      private String text;
      private Date stopDate;

      ConfirmStopHandler(EventHandler<ActionEvent> delegate) {
         this.delegate = delegate;
      }

      public void reset(Button button) {
         if (this.animation != null) {
            this.animation.stop();
            this.animation = null;
            this.setButtonText(button, this.text);
            button.setStyle(null);
         }
      }

      public void handle(ActionEvent event) {
         Button button = (Button)event.getSource();
         if (this.animation == null) {
            this.stopDate = JavaFxController.this.roastingManager.getCurrentEndDate(JavaFxController.this.roastingModel);
            this.text = button.getText();
            button.setStyle("-fx-background-color: -fx-color-warning-50 !important");
            this.animation = new SequentialTransition(
               new Animation[]{
                  new Timeline(
                     new KeyFrame[]{
                        new KeyFrame(
                           Duration.ONE,
                           e -> this.setButtonText(button, org.cropster.roastingintelligence.util.Messages.m_("Confirm in {0}", 3)),
                           new KeyValue[0]
                        )
                     }
                  ),
                  new Timeline(
                     new KeyFrame[]{
                        new KeyFrame(
                           Duration.seconds(1.0),
                           e -> this.setButtonText(button, org.cropster.roastingintelligence.util.Messages.m_("Confirm in {0}", 2)),
                           new KeyValue[0]
                        )
                     }
                  ),
                  new Timeline(
                     new KeyFrame[]{
                        new KeyFrame(
                           Duration.seconds(1.0),
                           e -> this.setButtonText(button, org.cropster.roastingintelligence.util.Messages.m_("Confirm in {0}", 1)),
                           new KeyValue[0]
                        )
                     }
                  ),
                  new Timeline(new KeyFrame[]{new KeyFrame(Duration.seconds(1.0), e -> this.reset(button), new KeyValue[0])})
               }
            );
            this.animation.play();
         } else {
            this.reset(button);
            JavaFxController.this.roastingModel.setEndDate(this.stopDate);
            this.delegate.handle(event);
         }
      }

      private void setButtonText(Button button, String text) {
         if (!button.textProperty().isBound()) {
            button.setText(text);
         }
      }
   }

   private class TaskUpdateMonitor extends ProgressMonitor<Collection<WorkerTask>> {
      TaskUpdateMonitor(Stage stage, CompletableFuture<Collection<WorkerTask>> future) {
         super(stage, future);
      }

      @Override
      public void failed() {
         JavaFxController.this.logger.error("Error updating tasks", this.getException());
         WindowUtil.showAlert(
            AlertType.ERROR,
            JavaFxController.this.application.getStage(),
            org.cropster.roastingintelligence.util.Messages.m_("Error"),
            org.cropster.roastingintelligence.util.Messages.m_("Error updating task:") + " " + this.getException().getMessage()
         );
      }
   }

   private class TouchScreenDetector implements EventHandler<TouchEvent> {
      private final Scene scene;

      TouchScreenDetector(Scene scene) {
         this.scene = scene;
      }

      public void handle(TouchEvent event) {
         JavaFxUtil.setTouchScreenDetected(true);
         this.scene.removeEventFilter(TouchEvent.ANY, this);
      }
   }
}
