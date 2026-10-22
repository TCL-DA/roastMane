package javafx.scene.control.cell;

import javafx.beans.value.ObservableValue;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.util.Callback;

public class ProgressBarTreeTableCell<S> extends TreeTableCell<S, Double> {
   private final ProgressBar progressBar;
   private ObservableValue<Double> observable;

   public static <S> Callback<TreeTableColumn<S, Double>, TreeTableCell<S, Double>> forTreeTableColumn() {
      return var0 -> new ProgressBarTreeTableCell();
   }

   public ProgressBarTreeTableCell() {
      this.getStyleClass().add("progress-bar-tree-table-cell");
      this.progressBar = new ProgressBar();
      this.progressBar.setMaxWidth(Double.MAX_VALUE);
   }

   public void updateItem(Double var1, boolean var2) {
      super.updateItem(var1, var2);
      if (var2) {
         this.setGraphic(null);
      } else {
         this.progressBar.progressProperty().unbind();
         TreeTableColumn var3 = this.getTableColumn();
         this.observable = var3 == null ? null : var3.getCellObservableValue(this.getIndex());
         if (this.observable != null) {
            this.progressBar.progressProperty().bind(this.observable);
         } else if (var1 != null) {
            this.progressBar.setProgress(var1);
         }

         this.setGraphic(this.progressBar);
      }
   }
}
