package org.controlsfx.control;

import impl.org.controlsfx.skin.TaskProgressViewSkin;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ListChangeListener.Change;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Skin;
import javafx.util.Callback;

public class TaskProgressView<T extends Task<?>> extends ControlsFXControl {
   private final BooleanProperty retainTasks = new SimpleBooleanProperty(this, "retainTasks", false);
   private final ObservableList<T> tasks = FXCollections.observableArrayList();
   private ObjectProperty<Callback<T, Node>> graphicFactory;

   public TaskProgressView() {
      this.getStyleClass().add("task-progress-view");
      final EventHandler<WorkerStateEvent> taskHandler = evt -> {
         if (!this.isRetainTasks()
            && (
               evt.getEventType().equals(WorkerStateEvent.WORKER_STATE_SUCCEEDED)
                  || evt.getEventType().equals(WorkerStateEvent.WORKER_STATE_CANCELLED)
                  || evt.getEventType().equals(WorkerStateEvent.WORKER_STATE_FAILED)
            )) {
            this.getTasks().remove(evt.getSource());
         }
      };
      this.getTasks().addListener(new ListChangeListener<Task<?>>() {
         public void onChanged(Change<? extends Task<?>> c) {
            while (c.next()) {
               if (c.wasAdded()) {
                  for (Task<?> task : c.getAddedSubList()) {
                     task.addEventHandler(WorkerStateEvent.ANY, taskHandler);
                  }
               } else if (c.wasRemoved()) {
                  for (Task<?> task : c.getRemoved()) {
                     task.removeEventHandler(WorkerStateEvent.ANY, taskHandler);
                  }
               }
            }
         }
      });
   }

   public String getUserAgentStylesheet() {
      return this.getUserAgentStylesheet(TaskProgressView.class, "taskprogressview.css");
   }

   protected Skin<?> createDefaultSkin() {
      return new TaskProgressViewSkin<T>(this);
   }

   public final ObservableList<T> getTasks() {
      return this.tasks;
   }

   public final ObjectProperty<Callback<T, Node>> graphicFactoryProperty() {
      if (this.graphicFactory == null) {
         this.graphicFactory = new SimpleObjectProperty(this, "graphicFactory");
      }

      return this.graphicFactory;
   }

   public final Callback<T, Node> getGraphicFactory() {
      return this.graphicFactory == null ? null : (Callback)this.graphicFactory.get();
   }

   public final void setGraphicFactory(Callback<T, Node> factory) {
      this.graphicFactoryProperty().set(factory);
   }

   public final boolean isRetainTasks() {
      return this.retainTasks.get();
   }

   public final BooleanProperty retainTasksProperty() {
      return this.retainTasks;
   }

   public final void setRetainTasks(boolean retainTasks) {
      this.retainTasks.set(retainTasks);
   }
}
