package impl.org.controlsfx.spreadsheet;

import java.util.Stack;
import javafx.beans.InvalidationListener;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.control.skin.TableColumnHeader;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import org.controlsfx.control.spreadsheet.Picker;
import org.controlsfx.control.spreadsheet.SpreadsheetView;

public class HorizontalPicker extends StackPane {
   private static final String PICKER_INDEX = "PickerIndex";
   private final HorizontalHeader horizontalHeader;
   private final SpreadsheetView spv;
   private final Stack<Label> pickerPile;
   private final Stack<Label> pickerUsed;
   private final HorizontalPicker.InnerHorizontalPicker innerPicker = new HorizontalPicker.InnerHorizontalPicker();
   private final EventHandler<MouseEvent> pickerMouseEvent = mouseEvent -> {
      Label picker = (Label)mouseEvent.getSource();
      ((Picker)picker.getProperties().get("PickerIndex")).onClick();
   };
   private final InvalidationListener layoutListener = arg0 -> this.innerPicker.requestLayout();

   public HorizontalPicker(HorizontalHeader horizontalHeader, SpreadsheetView spv) {
      this.horizontalHeader = horizontalHeader;
      this.spv = spv;
      this.pickerPile = new Stack<>();
      this.pickerUsed = new Stack<>();
      Rectangle clip = new Rectangle();
      clip.setSmooth(true);
      clip.setHeight(16.0);
      clip.widthProperty().bind(horizontalHeader.widthProperty());
      this.setClip(clip);
      this.getChildren().add(this.innerPicker);
      horizontalHeader.getRootHeader().getColumnHeaders().addListener(this.layoutListener);
      spv.getColumnPickers().addListener(this.layoutListener);
   }

   protected void layoutChildren() {
      this.innerPicker.relocate(this.horizontalHeader.getRootHeader().getLayoutX(), this.snappedTopInset());

      for (Label label : this.pickerUsed) {
         label.setVisible(label.getLayoutX() + this.innerPicker.getLayoutX() + label.getWidth() > this.horizontalHeader.gridViewSkin.fixedColumnWidth);
      }
   }

   public void updateScrollX() {
      this.requestLayout();
   }

   private Label getPicker(Picker picker) {
      Label pickerLabel;
      if (this.pickerPile.isEmpty()) {
         pickerLabel = new Label();
         pickerLabel.getStyleClass().addListener(this.layoutListener);
         pickerLabel.setOnMouseClicked(this.pickerMouseEvent);
      } else {
         pickerLabel = this.pickerPile.pop();
      }

      this.pickerUsed.push(pickerLabel);
      pickerLabel.getStyleClass().setAll(picker.getStyleClass());
      pickerLabel.getProperties().put("PickerIndex", picker);
      return pickerLabel;
   }

   private class InnerHorizontalPicker extends Region {
      protected void layoutChildren() {
         HorizontalPicker.this.pickerPile.addAll(HorizontalPicker.this.pickerUsed.subList(0, HorizontalPicker.this.pickerUsed.size()));

         for (Label label : HorizontalPicker.this.pickerUsed) {
            label.layoutXProperty().unbind();
            label.setVisible(true);
         }

         HorizontalPicker.this.pickerUsed.clear();
         this.getChildren().clear();
         int index = 0;

         for (TableColumnHeader column : HorizontalPicker.this.horizontalHeader.getRootHeader().getColumnHeaders()) {
            int modelColumn = HorizontalPicker.this.spv.getModelColumn(index);
            if (HorizontalPicker.this.spv.getColumnPickers().containsKey(modelColumn)) {
               Label label = HorizontalPicker.this.getPicker((Picker)HorizontalPicker.this.spv.getColumnPickers().get(modelColumn));
               label.resize(column.getWidth(), 16.0);
               label.layoutXProperty().bind(column.layoutXProperty());
               this.getChildren().add(0, label);
            }

            index++;
         }
      }
   }
}
