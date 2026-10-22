package org.controlsfx.dialog;

import impl.org.controlsfx.i18n.Localization;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.concurrent.Worker.State;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class ProgressDialog extends Dialog<Void> {
   public ProgressDialog(Worker<?> worker) {
      if (worker == null || worker.getState() != State.CANCELLED && worker.getState() != State.FAILED && worker.getState() != State.SUCCEEDED) {
         this.setResultConverter(dialogButton -> null);
         DialogPane dialogPane = this.getDialogPane();
         this.setTitle(Localization.getString("progress.dlg.title"));
         dialogPane.setHeaderText(Localization.getString("progress.dlg.header"));
         dialogPane.getStyleClass().add("progress-dialog");
         dialogPane.getStylesheets().add(ProgressDialog.class.getResource("dialogs.css").toExternalForm());
         Label progressMessage = new Label();
         progressMessage.textProperty().bind(worker.messageProperty());
         ProgressDialog.WorkerProgressPane content = new ProgressDialog.WorkerProgressPane(this);
         content.setMaxWidth(Double.MAX_VALUE);
         content.setWorker(worker);
         VBox vbox = new VBox(10.0, new Node[]{progressMessage, content});
         vbox.setMaxWidth(Double.MAX_VALUE);
         vbox.setPrefSize(300.0, 100.0);
         Label contentText = new Label();
         contentText.setWrapText(true);
         vbox.getChildren().add(0, contentText);
         contentText.textProperty().bind(dialogPane.contentTextProperty());
         dialogPane.setContent(vbox);
      }
   }

   private static class WorkerProgressPane extends Region {
      private Worker<?> worker;
      private boolean dialogVisible = false;
      private boolean cancelDialogShow = false;
      private ChangeListener<State> stateListener = new ChangeListener<State>() {
         public void changed(ObservableValue<? extends State> observable, State old, State value) {
            switch (value) {
               case CANCELLED:
               case FAILED:
               case SUCCEEDED:
                  if (!WorkerProgressPane.this.dialogVisible) {
                     WorkerProgressPane.this.cancelDialogShow = true;
                     WorkerProgressPane.this.end();
                  } else if (old == State.SCHEDULED || old == State.RUNNING) {
                     WorkerProgressPane.this.end();
                  }
                  break;
               case SCHEDULED:
                  WorkerProgressPane.this.begin();
            }
         }
      };
      private final ProgressDialog dialog;
      private final ProgressBar progressBar;

      public final void setWorker(Worker<?> newWorker) {
         if (newWorker != this.worker) {
            if (this.worker != null) {
               this.worker.stateProperty().removeListener(this.stateListener);
               this.end();
            }

            this.worker = newWorker;
            if (newWorker != null) {
               newWorker.stateProperty().addListener(this.stateListener);
               if (newWorker.getState() == State.RUNNING || newWorker.getState() == State.SCHEDULED) {
                  this.begin();
               }
            }
         }
      }

      public WorkerProgressPane(ProgressDialog dialog) {
         this.dialog = dialog;
         this.progressBar = new ProgressBar();
         this.progressBar.setMaxWidth(Double.MAX_VALUE);
         this.getChildren().add(this.progressBar);
         if (this.worker != null) {
            this.progressBar.progressProperty().bind(this.worker.progressProperty());
         }
      }

      private void begin() {
         this.cancelDialogShow = false;
         Platform.runLater(() -> {
            if (!this.cancelDialogShow) {
               this.progressBar.progressProperty().bind(this.worker.progressProperty());
               this.dialogVisible = true;
               this.dialog.show();
            }
         });
      }

      private void end() {
         this.progressBar.progressProperty().unbind();
         this.dialogVisible = false;
         DialogUtils.forcefullyHideDialog(this.dialog);
      }

      protected void layoutChildren() {
         if (this.progressBar != null) {
            Insets insets = this.getInsets();
            double w = this.getWidth() - insets.getLeft() - insets.getRight();
            double h = this.getHeight() - insets.getTop() - insets.getBottom();
            double prefH = this.progressBar.prefHeight(-1.0);
            double x = insets.getLeft() + (w - w) / 2.0;
            double y = insets.getTop() + (h - prefH) / 2.0;
            this.progressBar.resizeRelocate(x, y, w, prefH);
         }
      }
   }
}
