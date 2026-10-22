package com.cropster.roastingintelligence.ui.layout;

import com.cropster.roastingintelligence.model.alarms.Alarm;
import com.cropster.roastingintelligence.model.alarms.AlarmType;
import com.cropster.roastingintelligence.ui.AlarmController;
import com.cropster.roastingintelligence.ui.JavaFxUtil;
import com.cropster.roastingintelligence.ui.components.AlarmEditor;
import com.cropster.roastingintelligence.ui.components.AlarmLabel;
import com.cropster.roastingintelligence.ui.components.RadioToggleButton;
import com.cropster.roastingintelligence.ui.components.RandomShortcutPane;
import com.cropster.roastingintelligence.ui.components.TriggeredAlarmLabel;
import com.cropster.roastingintelligence.ui.list.EmptyStateList;
import com.cropster.roastingintelligence.ui.util.AbstractListCell;
import com.cropster.roastingintelligence.ui.util.ActionBuilder;
import com.cropster.roastingintelligence.ui.util.Icon;
import com.cropster.roastingintelligence.ui.util.ReverseList;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import java.util.function.Predicate;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import org.controlsfx.control.action.Action;

public class AlarmsSidebar extends DetachableSidebar implements Initializable {
   private static final String EMPTY_STATE_MESSAGE = org.cropster.roastingintelligence.util.Messages.m_("No alerts to display");
   @FXML
   private RadioToggleButton toggleButtonAlarmsLog;
   @FXML
   private RadioToggleButton toggleButtonAlarmsManage;
   @FXML
   private RadioToggleButton filterButtonAll;
   @FXML
   private VBox alarmsListBox;
   @FXML
   private EmptyStateList<Alarm> alarmEmptyStateList;
   @FXML
   private EmptyStateList<AlarmController.TriggeredAlarmHolder> triggeredAlarmEmptyStateList;
   @FXML
   private Label footerLabel;
   @FXML
   private Button clearButton;
   @FXML
   private Button addButton;
   private final BooleanProperty manageSelected = new SimpleBooleanProperty(true);
   private AlarmEditor editor;
   private ListView<Alarm> alarmsView;
   private ListView<AlarmController.TriggeredAlarmHolder> triggeredAlarmView;
   private AlarmsSidebar.AlarmListCell currentAlarmViewCell = null;
   private Consumer<AlarmController.TriggeredAlarmHolder> triggeredAlarmCleaner = null;
   private Consumer<Alarm> alarmRemover;
   private Consumer<Alarm> alarmReplacer;
   private FilteredList<Alarm> filteredAlarmList;
   private final Action deleteAction = new ActionBuilder(
         org.cropster.roastingintelligence.util.Messages.m_("Delete"),
         () -> this.alarmRemover.accept((Alarm)this.alarmsView.getSelectionModel().getSelectedItem())
      )
      .graphics(Icon.DELETE.load())
      .build();
   private final Action editAction = new ActionBuilder(
         org.cropster.roastingintelligence.util.Messages.m_("Edit"), () -> this.alarmsView.edit(this.alarmsView.getSelectionModel().getSelectedIndex())
      )
      .graphics(Icon.EDIT.load())
      .build();
   private final Action cleanAction = new ActionBuilder(
         org.cropster.roastingintelligence.util.Messages.m_("Clean"),
         () -> Optional.ofNullable(this.getTriggeredAlarmListCell(this.triggeredAlarmView.getSelectionModel().getSelectedIndex()))
               .ifPresent(AlarmsSidebar.TriggeredAlarmListCell::clean)
      )
      .graphics(Icon.CLEAN.load())
      .build();

   public AlarmsSidebar() {
      JavaFxUtil.loadComponent(this);
   }

   public RadioToggleButton getToggleButtonAlarmsLog() {
      return this.toggleButtonAlarmsLog;
   }

   public RadioToggleButton getToggleButtonAlarmsManage() {
      return this.toggleButtonAlarmsManage;
   }

   @FXML
   public void filterAll() {
      this.updateAlarmListFilter(null, org.cropster.roastingintelligence.util.Messages.m_("No alerts have been created"));
   }

   @FXML
   public void filterMeasurements() {
      this.updateAlarmListFilter(a -> a.getType() == AlarmType.MEASURE_BASED, EMPTY_STATE_MESSAGE);
   }

   @FXML
   public void filterEvents() {
      this.updateAlarmListFilter(a -> a.getType() == AlarmType.EVENT_BASED && !"time".equals(a.getTrigger()), EMPTY_STATE_MESSAGE);
   }

   @FXML
   public void filterTime() {
      this.updateAlarmListFilter(a -> a.getType() == AlarmType.EVENT_BASED && "time".equals(a.getTrigger()), EMPTY_STATE_MESSAGE);
   }

   @FXML
   public void showAlarmsLog() {
      this.setManageSelected(false);
   }

   @FXML
   public void showAlarmsManage() {
      this.setManageSelected(true);
   }

   @Override
   public void initialize(URL location, ResourceBundle resources) {
      super.initialize(location, resources);
      this.alarmsView = this.alarmEmptyStateList.getListView();
      this.alarmsView.setEditable(true);
      this.editor = new AlarmEditor(this.alarmsView);
      this.alarmsView.setCellFactory(v -> new AlarmsSidebar.AlarmListCell());
      this.alarmsListBox.visibleProperty().bind(this.manageSelectedProperty());
      this.triggeredAlarmEmptyStateList.visibleProperty().bind(this.manageSelectedProperty().not());
      this.triggeredAlarmView = this.triggeredAlarmEmptyStateList.getListView();
      this.triggeredAlarmView.setCellFactory(v -> new AlarmsSidebar.TriggeredAlarmListCell());
      this.addButton
         .disableProperty()
         .bind(Bindings.createBooleanBinding(() -> this.alarmsView.getEditingIndex() >= 0, new Observable[]{this.alarmsView.editingIndexProperty()}));
      this.addButton.visibleProperty().bind(this.manageSelectedProperty());
      this.clearButton.visibleProperty().bind(this.manageSelectedProperty().not());
   }

   public EmptyStateList<Alarm> getAlarmEmptyStateList() {
      return this.alarmEmptyStateList;
   }

   public ListView<Alarm> getAlarmView() {
      return this.alarmsView;
   }

   public void setAlarmViewItems(ObservableList<Alarm> items) {
      this.filteredAlarmList = new FilteredList(items);
      this.alarmEmptyStateList.setListViewItems(this.filteredAlarmList);
   }

   public EmptyStateList<AlarmController.TriggeredAlarmHolder> getTriggeredAlarmEmptyStateList() {
      return this.triggeredAlarmEmptyStateList;
   }

   public ListView<AlarmController.TriggeredAlarmHolder> getTriggeredAlarmView() {
      return this.triggeredAlarmView;
   }

   public void setTriggeredAlarmViewItems(ObservableList<AlarmController.TriggeredAlarmHolder> items) {
      this.triggeredAlarmEmptyStateList.setListViewItems(new ReverseList<AlarmController.TriggeredAlarmHolder>(items));
   }

   public void setTriggeredAlarmCleaner(Consumer<AlarmController.TriggeredAlarmHolder> triggeredAlarmCleaner) {
      this.triggeredAlarmCleaner = triggeredAlarmCleaner;
   }

   public RadioToggleButton getFilterButtonAll() {
      return this.filterButtonAll;
   }

   public Button getAddButton() {
      return this.addButton;
   }

   public Button getClearButton() {
      return this.clearButton;
   }

   public Label getFooterLabel() {
      return this.footerLabel;
   }

   public void setAlarmRemover(Consumer<Alarm> alarmRemover) {
      this.alarmRemover = alarmRemover;
   }

   public void setAlarmReplacer(Consumer<Alarm> alarmReplacer) {
      this.alarmReplacer = alarmReplacer;
   }

   public BooleanProperty manageSelectedProperty() {
      return this.manageSelected;
   }

   public boolean isManageSelected() {
      return this.manageSelected.get();
   }

   public void setManageSelected(boolean manageSelected) {
      this.manageSelected.set(manageSelected);
   }

   public void refreshViews() {
      if (this.currentAlarmViewCell != null) {
         this.currentAlarmViewCell.cancelEdit();
      }

      this.alarmsView.refresh();
      this.triggeredAlarmView.refresh();
   }

   public RandomShortcutPane getAlarmsRandomShortcutPane() {
      return this.alarmEmptyStateList.getRandomShortcutPane();
   }

   public RandomShortcutPane getTriggeredAlarmsRandomShortcutPane() {
      return this.triggeredAlarmEmptyStateList.getRandomShortcutPane();
   }

   private void updateAlarmListFilter(Predicate<Alarm> filter, String emptyMessage) {
      this.cancelEdit();
      this.alarmsView.setDisable(true);
      this.filteredAlarmList.setPredicate(filter);
      this.alarmEmptyStateList.setEmptyStateMessage(emptyMessage);
      this.alarmsView.setDisable(false);
   }

   private void cancelEdit() {
      if (this.alarmsView.getEditingIndex() != -1) {
         this.alarmsView.edit(-1);
      }
   }

   private AlarmsSidebar.TriggeredAlarmListCell getTriggeredAlarmListCell(int index) {
      return (AlarmsSidebar.TriggeredAlarmListCell)JavaFxUtil.getListCell(this.triggeredAlarmView, index);
   }

   public class AlarmListCell extends AbstractListCell<Alarm> {
      private final AlarmLabel viewer;

      public AlarmListCell() {
         AlarmsSidebar.this.editAction.disabledProperty().bind(this.editableProperty().not());
         Consumer<Alarm> replacer = a -> {
            int index = this.getIndex();
            if (AlarmsSidebar.this.alarmReplacer != null) {
               AlarmsSidebar.this.alarmReplacer.accept(a);
            }

            Platform.runLater(() -> this.getListView().getSelectionModel().select(index));
         };
         this.viewer = new AlarmLabel(this::showMenu, replacer);
         this.viewer.getMenuButton().disableProperty().bind(AlarmsSidebar.this.alarmsView.editingIndexProperty().greaterThanOrEqualTo(0));
         this.setPrefWidth(0.0);
      }

      @Override
      public void cancelEdit() {
         AlarmsSidebar.this.currentAlarmViewCell = null;
         super.cancelEdit();
      }

      public void commitEdit(Alarm newAlarm) {
         AlarmsSidebar.this.currentAlarmViewCell = null;
         super.commitEdit(newAlarm);
      }

      @Override
      public void startEdit() {
         super.startEdit();
         if (this.isEditing()) {
            AlarmsSidebar.this.currentAlarmViewCell = this;
         }
      }

      protected void initViewer(Alarm item) {
         if (item != null) {
            this.viewer.init(item);
            this.setGraphic(this.viewer);
            this.setEditable(Alarm.isSupported((Alarm)this.getItem()));
         }
      }

      protected void initEditor(Alarm alarm) {
         AlarmsSidebar.this.editor.init(alarm);
         AlarmsSidebar.this.editor.getCancelButton().setOnAction(this::cancelEdit);
         AlarmsSidebar.this.editor.getSaveButton().setOnAction(this::commitEdit);
         this.setGraphic(AlarmsSidebar.this.editor);
      }

      private void cancelEdit(ActionEvent e) {
         AlarmsSidebar.this.editor.stopAudioFeedback(e);
         this.cancelEdit();
      }

      private void commitEdit(ActionEvent e) {
         AlarmsSidebar.this.editor.stopAudioFeedback(e);
         Alarm editedAlarm = AlarmsSidebar.this.editor.captureEditedAlarm();
         if (editedAlarm != null) {
            this.commitEdit(editedAlarm);
         } else {
            this.cancelEdit();
         }
      }

      private void showMenu(ActionEvent ae) {
         AlarmsSidebar.this.alarmsView.getSelectionModel().select((Alarm)this.getItem());
         Node owner = (Node)ae.getSource();
         ContextMenu contextMenu = JavaFxUtil.createContextMenu(owner);
         contextMenu.getItems().add(ActionBuilder.menuItemFrom(AlarmsSidebar.this.editAction));
         contextMenu.getItems().add(ActionBuilder.menuItemFrom(AlarmsSidebar.this.deleteAction));
         contextMenu.show(owner, Side.BOTTOM, 0.0, 0.0);
      }
   }

   public class TriggeredAlarmListCell extends AbstractListCell<AlarmController.TriggeredAlarmHolder> {
      private final TriggeredAlarmLabel viewer = new TriggeredAlarmLabel(this::clearAlarm);

      public TriggeredAlarmListCell() {
         this.setPrefWidth(0.0);
      }

      protected void initViewer(AlarmController.TriggeredAlarmHolder alarmHolder) {
         this.viewer.init(alarmHolder);
         this.setGraphic(this.viewer);
      }

      protected void initEditor(AlarmController.TriggeredAlarmHolder item) {
      }

      public void clearAlarm(ActionEvent ae) {
         AlarmsSidebar.this.triggeredAlarmView.getSelectionModel().select((AlarmController.TriggeredAlarmHolder)this.getItem());
         AlarmsSidebar.this.cleanAction.handle(ae);
      }

      private void clean() {
         if (AlarmsSidebar.this.triggeredAlarmCleaner != null) {
            AlarmsSidebar.this.triggeredAlarmCleaner.accept(this.viewer.getAlarmHolder());
         }
      }
   }
}
