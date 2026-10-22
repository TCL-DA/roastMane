package com.cropster.roastingintelligence.ui.layout;

import com.cropster.roastingintelligence.ui.JavaFxUtil;
import com.cropster.roastingintelligence.ui.components.KeyCodeCombinationLabel;
import com.cropster.roastingintelligence.ui.components.TaskEditor;
import com.cropster.roastingintelligence.ui.components.TaskLabel;
import com.cropster.roastingintelligence.ui.list.EmptyStateList;
import com.cropster.roastingintelligence.ui.util.AbstractListCell;
import com.cropster.roastingintelligence.ui.util.ActionBuilder;
import com.cropster.roastingintelligence.ui.util.Icon;
import java.net.URL;
import java.util.Date;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Predicate;
import javafx.beans.DefaultProperty;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ListView.EditEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination.Modifier;
import javafx.scene.input.KeyCombination.ModifierValue;
import org.controlsfx.control.action.Action;
import org.cropster.roastingintelligence.model.WorkerTask;

@DefaultProperty("toggleButtons")
public class TasksSidebar extends DetachableSidebar implements Initializable {
   @FXML
   private KeyCodeCombinationLabel doubleClickKeyCodeCombinationLabel;
   @FXML
   private KeyCodeCombinationLabel clickKeyCodeCombinationLabel;
   @FXML
   private ToggleButton filterButtonTasksAll;
   @FXML
   private ToggleButton filterButtonTasksClosed;
   @FXML
   private ToggleButton filterButtonTasksOpen;
   @FXML
   private EmptyStateList<WorkerTask> taskEmptyStateList;
   @FXML
   private Label footerLabel;
   private ListView<WorkerTask> taskView;
   private FilteredList<WorkerTask> filteredTaskList;
   private final TaskEditor editor = new TaskEditor();
   private final Action markDoneAction = new ActionBuilder(
         org.cropster.roastingintelligence.util.Messages.m_("Mark as done"),
         () -> Optional.ofNullable(this.getTaskListCell(this.taskView.getSelectionModel().getSelectedIndex())).ifPresent(TasksSidebar.TaskListCell::markDone)
      )
      .graphics(Icon.CHECK_MARK.load())
      .build();
   private final Action markWontDoAction = new ActionBuilder(
         org.cropster.roastingintelligence.util.Messages.m_("Mark as Won't do"),
         () -> Optional.ofNullable(this.getTaskListCell(this.taskView.getSelectionModel().getSelectedIndex())).ifPresent(TasksSidebar.TaskListCell::markWontDo)
      )
      .graphics(Icon.CLOSE.load())
      .build();
   private final Action editAction = new ActionBuilder(
         org.cropster.roastingintelligence.util.Messages.m_("Mark with a comment"),
         () -> this.taskView.edit(this.taskView.getSelectionModel().getSelectedIndex())
      )
      .graphics(Icon.COMMENT.load())
      .build();

   public TasksSidebar() {
      JavaFxUtil.loadComponent(this);
   }

   @FXML
   public void filterAll() {
      this.updateTaskListFilter(null);
   }

   @FXML
   public void filterOpen() {
      this.updateTaskListFilter(WorkerTask::isOpen);
   }

   @FXML
   public void filterClosed() {
      this.updateTaskListFilter(WorkerTask::isClosed);
   }

   @Override
   public void initialize(URL location, ResourceBundle resources) {
      super.initialize(location, resources);
      this.doubleClickKeyCodeCombinationLabel.setCustomKeyCode("Double-Click", new KeyCodeCombination(KeyCode.ALL_CANDIDATES, new Modifier[0]));
      this.clickKeyCodeCombinationLabel
         .setCustomKeyCode(
            "Click", new KeyCodeCombination(KeyCode.ALL_CANDIDATES, ModifierValue.UP, ModifierValue.UP, ModifierValue.UP, ModifierValue.UP, ModifierValue.DOWN)
         );
      this.taskView = this.taskEmptyStateList.getListView();
      this.taskView.setEditable(true);
      this.taskView.setCellFactory(v -> new TasksSidebar.TaskListCell());
      this.footerLabel
         .textProperty()
         .bind(
            Bindings.createStringBinding(
               () -> org.cropster.roastingintelligence.util.Messages.p_(
                     "{0,number,#0} task selected", "{0,number,#0} tasks selected", this.taskView.getSelectionModel().getSelectedItems().size()
                  ),
               new Observable[]{this.taskView.getSelectionModel().getSelectedItems()}
            )
         );
   }

   public EmptyStateList<WorkerTask> getTaskEmptyStateList() {
      return this.taskEmptyStateList;
   }

   public void setTaskViewItems(ObservableList<WorkerTask> items) {
      this.filteredTaskList = new FilteredList(items);
      this.taskEmptyStateList.setListViewItems(this.filteredTaskList);
      this.filterButtonTasksAll
         .textProperty()
         .bind(Bindings.createStringBinding(() -> items.size() + " " + org.cropster.roastingintelligence.util.Messages.m_("Total"), new Observable[]{items}));
      this.filterButtonTasksClosed
         .textProperty()
         .bind(
            Bindings.createStringBinding(
               () -> items.stream().filter(WorkerTask::isClosed).count() + " " + org.cropster.roastingintelligence.util.Messages.m_("Closed"),
               new Observable[]{items}
            )
         );
      this.filterButtonTasksOpen
         .textProperty()
         .bind(
            Bindings.createStringBinding(
               () -> items.stream().filter(WorkerTask::isOpen).count() + " " + org.cropster.roastingintelligence.util.Messages.m_("Open"),
               new Observable[]{items}
            )
         );
   }

   public ListView<WorkerTask> getTaskView() {
      return this.taskView;
   }

   private void updateTaskListFilter(Predicate<WorkerTask> filter) {
      this.cancelEdit();
      this.taskView.setDisable(true);
      this.filteredTaskList.setPredicate(filter);
      this.taskView.setDisable(false);
   }

   private void cancelEdit() {
      if (this.taskView.getEditingIndex() != -1) {
         this.taskView.edit(-1);
      }
   }

   private TasksSidebar.TaskListCell getTaskListCell(int index) {
      return (TasksSidebar.TaskListCell)JavaFxUtil.getListCell(this.taskView, index);
   }

   public class TaskListCell extends AbstractListCell<WorkerTask> {
      private final TaskLabel viewer = new TaskLabel(this::showMenu, this::markWithAction);

      public TaskListCell() {
         this.viewer.editableProperty().bind(this.editableProperty());
         this.viewer.getMarkButton().disableProperty().bind(TasksSidebar.this.taskView.editingIndexProperty().greaterThanOrEqualTo(0));
         this.viewer.getMenuButton().disableProperty().bind(TasksSidebar.this.taskView.editingIndexProperty().greaterThanOrEqualTo(0));
      }

      private void fireEditEvent() {
         ((WorkerTask)this.getItem()).setSynced(false);
         TasksSidebar.this.taskView
            .fireEvent(
               new EditEvent(
                  TasksSidebar.this.taskView,
                  ListView.editCommitEvent(),
                  (WorkerTask)this.getItem(),
                  TasksSidebar.this.taskView.getItems().indexOf(this.getItem())
               )
            );
      }

      private void markDone() {
         ((WorkerTask)this.getItem()).setStatus(WorkerTask.TaskStatus.DONE);
         ((WorkerTask)this.getItem()).setCompletionDate(new Date());
         this.fireEditEvent();
         this.viewer.init((WorkerTask)this.getItem());
      }

      private void markWontDo() {
         ((WorkerTask)this.getItem()).setStatus(WorkerTask.TaskStatus.NOT_DONE);
         this.fireEditEvent();
         this.viewer.init((WorkerTask)this.getItem());
      }

      public void commitEdit(WorkerTask newValue) {
         newValue.setSynced(false);
         super.commitEdit(newValue);
      }

      private void showMenu(ActionEvent ae) {
         TasksSidebar.this.taskView.getSelectionModel().select((WorkerTask)this.getItem());
         Node owner = (Node)ae.getSource();
         ContextMenu contextMenu = JavaFxUtil.createContextMenu(owner);
         contextMenu.getItems().add(ActionBuilder.menuItemFrom(TasksSidebar.this.markDoneAction));
         contextMenu.getItems().add(ActionBuilder.menuItemFrom(TasksSidebar.this.markWontDoAction));
         contextMenu.getItems().add(ActionBuilder.menuItemFrom(TasksSidebar.this.editAction));
         contextMenu.show(owner, Side.BOTTOM, 0.0, 0.0);
      }

      private void markWithAction(ActionEvent ae) {
         TasksSidebar.this.taskView.getSelectionModel().select((WorkerTask)this.getItem());
         TasksSidebar.this.editAction.handle(ae);
      }

      protected void initViewer(WorkerTask item) {
         this.editableProperty().set(!item.getFrequency().isProductionBased() || WorkerTask.TaskStatus.TO_DO.equals(item.getStatus()) || !item.isSynced());
         this.viewer.init(item);
         this.setGraphic(this.viewer);
      }

      protected void initEditor(WorkerTask task) {
         TasksSidebar.this.editor.init(task);
         TasksSidebar.this.editor.getCancelButton().setOnAction(e -> this.cancelEdit());
         TasksSidebar.this.editor.getSaveButton().setOnAction(e -> {
            task.setStatus(TasksSidebar.this.editor.getStatus());
            task.setComment(TasksSidebar.this.editor.getComment());
            if (task.getStatus().equals(WorkerTask.TaskStatus.DONE)) {
               ((WorkerTask)this.getItem()).setCompletionDate(new Date());
            }

            this.commitEdit(task);
         });
         this.setGraphic(TasksSidebar.this.editor);
      }
   }
}
