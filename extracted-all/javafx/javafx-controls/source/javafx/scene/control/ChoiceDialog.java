package javafx.scene.control;

import com.sun.javafx.scene.control.skin.resources.ControlResources;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

public class ChoiceDialog<T> extends Dialog<T> {
   private final GridPane grid;
   private final Label label;
   private final ComboBox<T> comboBox;
   private final T defaultChoice;

   public ChoiceDialog() {
      this(null, (T[])((Object[])null));
   }

   public ChoiceDialog(T var1, T... var2) {
      this((T)var1, var2 == null ? Collections.emptyList() : Arrays.asList((T[])var2));
   }

   public ChoiceDialog(T var1, Collection<T> var2) {
      DialogPane var3 = this.getDialogPane();
      this.grid = new GridPane();
      this.grid.setHgap(10.0);
      this.grid.setMaxWidth(Double.MAX_VALUE);
      this.grid.setAlignment(Pos.CENTER_LEFT);
      this.label = DialogPane.createContentLabel(var3.getContentText());
      this.label.setPrefWidth(-1.0);
      this.label.textProperty().bind(var3.contentTextProperty());
      var3.contentTextProperty().addListener(var1x -> this.updateGrid());
      this.setTitle(ControlResources.getString("Dialog.confirm.title"));
      var3.setHeaderText(ControlResources.getString("Dialog.confirm.header"));
      var3.getStyleClass().add("choice-dialog");
      var3.getButtonTypes().addAll(new ButtonType[]{ButtonType.OK, ButtonType.CANCEL});
      this.comboBox = new ComboBox<>();
      this.comboBox.setMinWidth(150.0);
      if (var2 != null) {
         this.comboBox.getItems().addAll(var2);
      }

      this.comboBox.setMaxWidth(Double.MAX_VALUE);
      GridPane.setHgrow(this.comboBox, Priority.ALWAYS);
      GridPane.setFillWidth(this.comboBox, true);
      this.defaultChoice = (T)(this.comboBox.getItems().contains(var1) ? var1 : null);
      if (var1 == null) {
         this.comboBox.getSelectionModel().selectFirst();
      } else {
         this.comboBox.getSelectionModel().select((T)var1);
      }

      this.updateGrid();
      this.setResultConverter(var1x -> {
         ButtonBar.ButtonData var2x = var1x == null ? null : var1x.getButtonData();
         return var2x == ButtonBar.ButtonData.OK_DONE ? this.getSelectedItem() : null;
      });
   }

   public final T getSelectedItem() {
      return this.comboBox.getSelectionModel().getSelectedItem();
   }

   public final ReadOnlyObjectProperty<T> selectedItemProperty() {
      return this.comboBox.getSelectionModel().selectedItemProperty();
   }

   public final void setSelectedItem(T var1) {
      this.comboBox.getSelectionModel().select((T)var1);
   }

   public final ObservableList<T> getItems() {
      return this.comboBox.getItems();
   }

   public final T getDefaultChoice() {
      return this.defaultChoice;
   }

   private void updateGrid() {
      this.grid.getChildren().clear();
      this.grid.add(this.label, 0, 0);
      this.grid.add(this.comboBox, 1, 0);
      this.getDialogPane().setContent(this.grid);
      Platform.runLater(() -> this.comboBox.requestFocus());
   }
}
