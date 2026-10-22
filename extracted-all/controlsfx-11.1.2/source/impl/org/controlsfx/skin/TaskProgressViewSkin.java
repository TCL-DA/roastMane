package impl.org.controlsfx.skin;

import javafx.beans.binding.Bindings;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.SkinBase;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import org.controlsfx.control.TaskProgressView;

public class TaskProgressViewSkin<T extends Task<?>> extends SkinBase<TaskProgressView<T>> {
   public TaskProgressViewSkin(TaskProgressView<T> monitor) {
      super(monitor);
      BorderPane borderPane = new BorderPane();
      borderPane.getStyleClass().add("box");
      ListView<T> listView = new ListView();
      listView.setPrefSize(500.0, 400.0);
      listView.setPlaceholder(new Label("No tasks running"));
      listView.setCellFactory(param -> new TaskProgressViewSkin.TaskCell());
      listView.setFocusTraversable(false);
      Bindings.bindContent(listView.getItems(), monitor.getTasks());
      borderPane.setCenter(listView);
      this.getChildren().add(listView);
   }

   class TaskCell extends ListCell<T> {
      private ProgressBar progressBar;
      private Label titleText = new Label();
      private Label messageText;
      private Button cancelButton;
      private T task;
      private BorderPane borderPane;

      public TaskCell() {
         this.titleText.getStyleClass().add("task-title");
         this.messageText = new Label();
         this.messageText.getStyleClass().add("task-message");
         this.progressBar = new ProgressBar();
         this.progressBar.setMaxWidth(Double.MAX_VALUE);
         this.progressBar.setMaxHeight(8.0);
         this.progressBar.getStyleClass().add("task-progress-bar");
         this.cancelButton = new Button("Cancel");
         this.cancelButton.getStyleClass().add("task-cancel-button");
         this.cancelButton.setTooltip(new Tooltip("Cancel Task"));
         this.cancelButton.setOnAction(evt -> {
            if (this.task != null) {
               this.task.cancel();
            }
         });
         VBox vbox = new VBox();
         vbox.setSpacing(4.0);
         vbox.getChildren().add(this.titleText);
         vbox.getChildren().add(this.progressBar);
         vbox.getChildren().add(this.messageText);
         BorderPane.setAlignment(this.cancelButton, Pos.CENTER);
         BorderPane.setMargin(this.cancelButton, new Insets(0.0, 0.0, 0.0, 4.0));
         this.borderPane = new BorderPane();
         this.borderPane.setCenter(vbox);
         this.borderPane.setRight(this.cancelButton);
         this.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
      }

      public void updateIndex(int index) {
         super.updateIndex(index);
         if (index == -1) {
            this.setGraphic(null);
            this.getStyleClass().setAll(new String[]{"task-list-cell-empty"});
         }
      }

      protected void updateItem(T task, boolean empty) {
         super.updateItem(task, empty);
         this.task = task;
         if (empty || task == null) {
            this.getStyleClass().setAll(new String[]{"task-list-cell-empty"});
            this.setGraphic(null);
         } else if (task != null) {
            this.getStyleClass().setAll(new String[]{"task-list-cell"});
            this.progressBar.progressProperty().bind(task.progressProperty());
            this.titleText.textProperty().bind(task.titleProperty());
            this.messageText.textProperty().bind(task.messageProperty());
            this.cancelButton.disableProperty().bind(Bindings.not(task.runningProperty()));
            Callback<T, Node> factory = ((TaskProgressView)TaskProgressViewSkin.this.getSkinnable()).getGraphicFactory();
            if (factory != null) {
               Node graphic = (Node)factory.call(task);
               if (graphic != null) {
                  BorderPane.setAlignment(graphic, Pos.CENTER);
                  BorderPane.setMargin(graphic, new Insets(0.0, 4.0, 0.0, 0.0));
                  this.borderPane.setLeft(graphic);
               }
            } else {
               this.borderPane.setLeft(null);
            }

            this.setGraphic(this.borderPane);
         }
      }
   }
}
