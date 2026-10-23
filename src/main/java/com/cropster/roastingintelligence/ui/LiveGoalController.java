package com.cropster.roastingintelligence.ui;

import com.cropster.roastingintelligence.listener.ActiveGoalsChangeEvent;
import com.cropster.roastingintelligence.listener.ActiveGoalsChangeListener;
import com.cropster.roastingintelligence.listener.LiveGoalsChangeEvent;
import com.cropster.roastingintelligence.listener.LiveGoalsChangeListener;
import com.cropster.roastingintelligence.model.livegoals.LiveGoal;
import com.cropster.roastingintelligence.model.livegoals.LiveGoalDisplayMode;
import com.cropster.roastingintelligence.service.LiveGoalService;
import com.cropster.roastingintelligence.service.StorageService;
import com.cropster.roastingintelligence.service.impl.LiveGoalServiceImpl;
import com.cropster.roastingintelligence.ui.components.ActiveGoalLabel;
import com.cropster.roastingintelligence.ui.components.LiveGoalLabel;
import com.cropster.roastingintelligence.ui.components.PopOverBubble;
import com.cropster.roastingintelligence.ui.components.SearchableListView;
import com.cropster.roastingintelligence.ui.list.DraggableListCell;
import com.cropster.roastingintelligence.ui.list.EmptyStateGrid;
import com.cropster.roastingintelligence.ui.list.GridCell;
import com.cropster.roastingintelligence.ui.list.GridView;
import com.cropster.roastingintelligence.ui.util.AbstractListCell;
import com.cropster.roastingintelligence.ui.util.Icon;
import com.cropster.roastingintelligence.ui.util.WindowUtil;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.DataFormat;
import javafx.scene.layout.BorderPane;
import javafx.stage.Window;
import org.apache.commons.lang3.StringUtils;
import org.controlsfx.control.PopOver.ArrowLocation;
import org.cropster.roastingintelligence.AppModel;
import org.cropster.roastingintelligence.RoastingIntelligenceApp;
import org.cropster.roastingintelligence.SyncService;
import org.cropster.roastingintelligence.remote.CsarConnection;
import org.cropster.roastingintelligence.service.impl.JsonReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LiveGoalController implements ActiveGoalsChangeListener, LiveGoalsChangeListener {
   public static final String CONFIGURATOR_POPOVER_WINDOW_ID = "CONFIGURATOR_POPOVER_WINDOW_ID";
   private static final Logger LOGGER = LoggerFactory.getLogger(LiveGoalController.class);
   private static final int SAFE_AREA_PADDING = 24;
   private static final DataFormat LIVE_GOAL_LIST_CELL_DATA_FORMAT = new DataFormat(new String[]{LiveGoalController.LiveGoalListCell.class.getSimpleName()});
   private final ObservableList<LiveGoalService.ActiveGoal> activeGoals = FXCollections.observableArrayList();
   private AppModel appModel;
   private final ObservableList<LiveGoal> liveGoals = FXCollections.observableArrayList();
   private final LiveGoalService liveGoalService = new LiveGoalServiceImpl();
   private DoubleBinding popoverContentHeightProperty;
   private boolean smartGoalsEnabled;

   private static List<LiveGoal> loadLiveGoalsFromServer(CsarConnection connection) {
      List<LiveGoal> liveGoals = null;
      String json = null;

      try {
         json = connection.loadLiveGoalPreferences();
      } catch (Exception var6) {
         LOGGER.error("Error loading live goals from server", var6);
      }

      if (json != null) {
         JsonReader jsonReader = new JsonReader();

         try {
            liveGoals = jsonReader.readLiveGoals(new StringReader(json));
            LOGGER.info("Live goals loaded from server");
         } catch (IOException var5) {
            LOGGER.error("Error reading live goals from server", var5);
         }
      } else {
         LOGGER.info("No live goals found on server");
      }

      return liveGoals;
   }

   @Override
   public void liveGoalsChanged(LiveGoalsChangeEvent event) {
      Platform.runLater(() -> {
         List<LiveGoal> changedLiveGoals = event.getChangedLiveGoals();
         List<Integer> changedLiveGoalsIndexes = event.getChangedLiveGoalsIndexes();
         switch (event.getChangeType()) {
            case ADD:
               for (int i = 0; i < changedLiveGoals.size(); i++) {
                  this.liveGoals.add(changedLiveGoalsIndexes.get(i), changedLiveGoals.get(i));
               }
               break;
            case REMOVE:
               for (int i = changedLiveGoals.size() - 1; i >= 0; i--) {
                  this.liveGoals.remove(changedLiveGoalsIndexes.get(i));
               }
               break;
            case CLEAR:
               this.liveGoals.clear();
               break;
            default:
               throw new IllegalStateException("Unknown LiveGoalsChangeEvent.ChangeType: " + event.getChangeType());
         }
      });
   }

   @Override
   public void activeGoalsChanged(ActiveGoalsChangeEvent event) {
      Platform.runLater(() -> {
         List<LiveGoalService.ActiveGoal> changedActiveGoals = event.getChangedActiveGoals();
         List<Integer> changedActiveGoalsIndexes = event.getChangedActiveGoalsIndexes();
         switch (event.getChangeType()) {
            case ADD:
               for (int i = 0; i < changedActiveGoals.size(); i++) {
                  this.activeGoals.add(changedActiveGoalsIndexes.get(i), changedActiveGoals.get(i));
               }
               break;
            case REMOVE:
               for (int i = changedActiveGoals.size() - 1; i >= 0; i--) {
                  this.activeGoals.remove(changedActiveGoalsIndexes.get(i));
               }
               break;
            case UPDATE:
               for (int i = 0; i < changedActiveGoals.size(); i++) {
                  this.activeGoals.set(changedActiveGoalsIndexes.get(i), changedActiveGoals.get(i));
               }
               break;
            case CLEAR:
               this.activeGoals.clear();
               break;
            default:
               throw new IllegalStateException("Unknown ActiveGoalsChangeEvent.ChangeType: " + event.getChangeType());
         }
      });
   }

   public void initializeLiveGoalSystem(SyncService syncService, CsarConnection connection) {
      StorageService storageService = RoastingIntelligenceApp.retrieveSingleton(StorageService.class);
      if (storageService == null) {
         LOGGER.error("StorageService not available");
      } else {
         this.liveGoalService
            .addPropertyChangeListener(
               "dirty",
               e -> {
                  boolean saveNeeded = (Boolean)e.getNewValue();
                  if (saveNeeded) {
                     try {
                        storageService.saveLiveGoals(this.liveGoalService.exportTo());
                     } catch (IOException var6) {
                        LOGGER.error("Unable to write live goals file", var6);
                        WindowUtil.showAlert(
                           AlertType.ERROR,
                           null,
                           org.cropster.roastingintelligence.util.Messages.m_("Error saving live goals"),
                           org.cropster.roastingintelligence.util.Messages.m_("The live goals file could not be written.")
                        );
                     }

                     syncService.uploadLiveGoals();
                  }
               }
            );
         syncService.uploadLiveGoals();
         this.liveGoalService.setDirtyNotificationEnabled(false);
         LOGGER.info("Initializing live goals");
         List<LiveGoal> lGoals = storageService.loadLiveGoals();
         if (lGoals == null) {
            lGoals = loadLiveGoalsFromServer(connection);
            if (lGoals == null) {
               lGoals = List.of();
            }
         }

         List<LiveGoal> finalLiveGoals = this.checkAgainstDefaultLiveGoals(lGoals);
         if (!finalLiveGoals.isEmpty()) {
            this.liveGoalService.addLiveGoals(finalLiveGoals);
         }

         this.liveGoalService.setDirtyNotificationEnabled(true, lGoals.isEmpty());
         synchronized (this.liveGoalService) {
            this.synchronizeLiveGoals();
            this.liveGoalService.addLiveGoalsChangeListener(this);
            this.liveGoalService.addActiveGoalsChangeListener(this);
         }

         this.liveGoalService.resume();
      }
   }

   private List<LiveGoal> checkAgainstDefaultLiveGoals(List<LiveGoal> liveGoals) {
      List<LiveGoal> finalLiveGoals = new ArrayList<>();
      List<LiveGoal> defaultLiveGoals = this.liveGoalService.createDefaultLiveGoals();
      Map<String, LiveGoal> defaultLiveGoalsMap = defaultLiveGoals.stream().collect(Collectors.toMap(LiveGoal::getId, lg -> (LiveGoal)lg));
      liveGoals.forEach(lg -> {
         String id = lg.getId();
         LiveGoal defaultLiveGoal = defaultLiveGoalsMap.get(id);
         if (defaultLiveGoal == null) {
            LOGGER.warn("Live goal '{}' is not recognized and will be ignored", id);
         } else if (lg.getType() != defaultLiveGoal.getType()) {
            LOGGER.warn("Live goal '{}' has a wrong type '{}' and will be replaced with the default one", id, lg.getType());
            finalLiveGoals.add(defaultLiveGoal);
         } else {
            finalLiveGoals.add(lg);
         }
      });
      defaultLiveGoals.stream()
         .filter(defaultLiveGoal -> finalLiveGoals.stream().noneMatch(lg -> lg.getId().equals(defaultLiveGoal.getId())))
         .forEach(finalLiveGoals::add);
      return finalLiveGoals;
   }

   public void initActiveGoalsView(EmptyStateGrid<LiveGoalService.ActiveGoal> liveGoalEmptyStateGrid, AppModel appModel) {
      this.appModel = appModel;
      GridView<LiveGoalService.ActiveGoal> gridView = liveGoalEmptyStateGrid.getGridView();
      gridView.setCellFactory(gv -> new LiveGoalController.ActiveGoalGridCell());
      liveGoalEmptyStateGrid.setListViewItems(this.activeGoals);
      this.popoverContentHeightProperty = liveGoalEmptyStateGrid.heightProperty().subtract(24);
   }

   public ObservableList<LiveGoalService.ActiveGoal> getActiveGoals() {
      return this.activeGoals;
   }

   public ObservableList<LiveGoal> getLiveGoals() {
      return this.liveGoals;
   }

   private void synchronizeLiveGoals() {
      this.liveGoals.setAll(this.liveGoalService.getLiveGoals());
      this.activeGoals.setAll(this.liveGoalService.getActiveGoals());
   }

   public LiveGoalService getLiveGoalService() {
      return this.liveGoalService;
   }

   public void showConfigurationPopup(Button liveGoalConfigurationButton) {
      ObservableList<LiveGoal> properLiveGoals = this.liveGoals;
      if (!this.smartGoalsEnabled) {
         Map<String, Set<LiveGoalDisplayMode>> sdm = this.liveGoalService.getSupportedDisplayModes();
         FilteredList<LiveGoal> reducedLiveGoals = new FilteredList(properLiveGoals);
         reducedLiveGoals.setPredicate(lg -> {
            String id = lg.getId();
            return sdm.getOrDefault(id, Set.of()).contains(LiveGoalDisplayMode.TARGET);
         });
         properLiveGoals = reducedLiveGoals;
      }

      FilteredList<LiveGoal> filteredLiveGoals = new FilteredList(properLiveGoals);
      SearchableListView<LiveGoal> searchableListView = new SearchableListView<>(filteredLiveGoals);
      searchableListView.getStyleClass().add("live-goals-searchable-list-view");
      searchableListView.setCellFactory(lv -> new LiveGoalController.LiveGoalListCell(searchableListView.getSearchTextField().textProperty()));
      searchableListView.getSearchTextField()
         .textProperty()
         .addListener(
            (s, o, n) -> filteredLiveGoals.setPredicate(
                  lg -> StringUtils.isEmpty(n)
                        || StringUtils.containsIgnoreCase(this.liveGoalService.getLocalizedNames().getOrDefault(lg.getId(), lg.getId()), n)
               )
         );
      searchableListView.setMinHeight(this.popoverContentHeightProperty.get());
      ListView<LiveGoal> listView = searchableListView.getListView();
      listView.getStyleClass().removeAll(new String[]{"selectable-list-view", "bordered-list"});
      listView.getStyleClass().addAll(new String[]{"transparent-list", "live-goals-list-view"});
      Label descriptionLabel = new Label("Select and reorder the information to observe across your profiles, if they have data to compare against.");
      descriptionLabel.setWrapText(true);
      BorderPane.setMargin(descriptionLabel, new Insets(0.0, 0.0, 8.0, 0.0));
      BorderPane container = new BorderPane(searchableListView);
      container.setTop(descriptionLabel);
      PopOverBubble popover = PopOverBubble.builder()
         .arrowLocation(ArrowLocation.RIGHT_TOP)
         .closable(true)
         .maximumWidth(320.0)
         .minimumWidth(320.0)
         .onClose(() -> liveGoalConfigurationButton.setDisable(false))
         .windowId("CONFIGURATOR_POPOVER_WINDOW_ID")
         .withHeader(org.cropster.roastingintelligence.util.Messages.m_("Customize"), Icon.DRAG_AND_DROP.load())
         .withContent(container)
         .withDarkBackground()
         .build();
      popover.showAt(liveGoalConfigurationButton.getParent().getParent());
      liveGoalConfigurationButton.setDisable(true);
   }

   public void updateSmartGoals(boolean smartGoalsEnabled) {
      WindowManager.get().getWindow("CONFIGURATOR_POPOVER_WINDOW_ID").ifPresent(Window::hide);
      this.smartGoalsEnabled = smartGoalsEnabled;
      if (!smartGoalsEnabled) {
         Map<String, Set<LiveGoalDisplayMode>> supportedDisplayModes = this.liveGoalService.getSupportedDisplayModes();
         List<LiveGoal> toBeReplacedList = this.liveGoals
            .stream()
            .filter(lg -> lg.getDisplayMode() != LiveGoalDisplayMode.HIDDEN)
            .filter(lg -> lg.getDisplayMode() != LiveGoalDisplayMode.TARGET)
            .toList();
         toBeReplacedList.forEach(
            lg -> this.liveGoalService
                  .replaceLiveGoals(
                     LiveGoal.builder(lg)
                        .withDisplayMode(
                           supportedDisplayModes.getOrDefault(lg.getId(), Set.of()).contains(LiveGoalDisplayMode.TARGET)
                              ? LiveGoalDisplayMode.TARGET
                              : LiveGoalDisplayMode.HIDDEN
                        )
                        .build()
                  )
         );
      }
   }

   private class ActiveGoalGridCell extends GridCell<LiveGoalService.ActiveGoal> {
      private final ActiveGoalLabel viewer = new ActiveGoalLabel(LiveGoalController.this.liveGoalService.getLocalizedNames());

      ActiveGoalGridCell() {
         this.setPrefWidth(0.0);
      }

      protected void initViewer(LiveGoalService.ActiveGoal activeGoal) {
         this.viewer.init(activeGoal, LiveGoalController.this.appModel.getPreferences().isHideLiveGoalCurrentValueEnabled());
         this.setGraphic(this.viewer);
      }
   }

   private class LiveGoalListCell extends AbstractListCell<LiveGoal> implements DraggableListCell<LiveGoal, LiveGoalController.LiveGoalListCell> {
      private final ChangeListener<LiveGoalDisplayMode> modeChangedListener = (s, o, n) -> {
         LiveGoal item = this.getItem();
         if (item != null && n != null && n != item.getDisplayMode()) {
            LiveGoal updatedLiveGoal = LiveGoal.builder(item).withDisplayMode(n).build();
            LiveGoalController.this.liveGoalService.replaceLiveGoals(updatedLiveGoal);
         }
      };
      private final LiveGoalLabel viewer;

      LiveGoalListCell(ReadOnlyStringProperty searchTextProperty) {
         this.viewer = new LiveGoalLabel(
            searchTextProperty, LiveGoalController.this.liveGoalService.getSupportedDisplayModes(), LiveGoalController.this.liveGoalService.getLocalizedNames()
         );
         this.initDragAndDrop(LiveGoalController.LIVE_GOAL_LIST_CELL_DATA_FORMAT, this);
      }

      public void finalizeDragDropped(LiveGoal draggedItem, boolean before) {
         if (draggedItem != null && this.getItem() != draggedItem) {
            String did = draggedItem.getId();
            String id = this.getItem().getId();
            if (before) {
               LiveGoalController.this.liveGoalService.moveLiveGoalBefore(did, id);
            } else {
               LiveGoalController.this.liveGoalService.moveLiveGoalAfter(did, id);
            }
         }
      }

      public LiveGoalController.LiveGoalListCell getCell() {
         return this;
      }

      @Override
      public Node getDragHandle() {
         return this.viewer.getDragLabel();
      }

      @Override
      public Node getDragNode() {
         return this;
      }

      @Override
      public Node getDropTarget() {
         return this;
      }

      public String idForItem(LiveGoal liveGoal) {
         return liveGoal.getId();
      }

      protected void initViewer(LiveGoal item) {
         this.viewer.selectedDisplayModeProperty().removeListener(this.modeChangedListener);
         this.getStyleClass().remove("dragged-list-cell");
         if (item != null) {
            this.viewer.init(item, LiveGoalController.this.smartGoalsEnabled);
            this.viewer.selectedDisplayModeProperty().addListener(this.modeChangedListener);
            this.setGraphic(this.viewer);
            this.setEditable(false);
            if (item == this.getListView().getProperties().get("dragged-list-cell")) {
               this.getStyleClass().add("dragged-list-cell");
            }
         }
      }

      protected void initEditor(LiveGoal item) {
      }
   }
}
