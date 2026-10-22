package impl.org.controlsfx.skin;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.collections.ObservableList;
import javafx.scene.control.SkinBase;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import org.controlsfx.control.SegmentedButton;

public class SegmentedButtonSkin extends SkinBase<SegmentedButton> {
   private static final String ONLY_BUTTON = "only-button";
   private static final String LEFT_PILL = "left-pill";
   private static final String CENTER_PILL = "center-pill";
   private static final String RIGHT_PILL = "right-pill";
   private final HBox container = new HBox();

   public SegmentedButtonSkin(SegmentedButton control) {
      super(control);
      this.getChildren().add(this.container);
      this.updateButtons();
      this.getButtons().addListener(new InvalidationListener() {
         public void invalidated(Observable observable) {
            SegmentedButtonSkin.this.updateButtons();
         }
      });
      control.toggleGroupProperty().addListener((observable, oldValue, newValue) -> this.getButtons().forEach(button -> button.setToggleGroup(newValue)));
   }

   private ObservableList<ToggleButton> getButtons() {
      return ((SegmentedButton)this.getSkinnable()).getButtons();
   }

   private void updateButtons() {
      ObservableList<ToggleButton> buttons = this.getButtons();
      ToggleGroup group = ((SegmentedButton)this.getSkinnable()).getToggleGroup();
      this.container.getChildren().clear();

      for (int i = 0; i < this.getButtons().size(); i++) {
         ToggleButton t = (ToggleButton)buttons.get(i);
         if (group != null) {
            t.setToggleGroup(group);
         }

         t.getStyleClass().removeAll(new String[]{"only-button", "left-pill", "center-pill", "right-pill"});
         this.container.getChildren().add(t);
         if (i == buttons.size() - 1) {
            if (i == 0) {
               t.getStyleClass().add("only-button");
            } else {
               t.getStyleClass().add("right-pill");
            }
         } else if (i == 0) {
            t.getStyleClass().add("left-pill");
         } else {
            t.getStyleClass().add("center-pill");
         }
      }
   }

   protected double computeMaxWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
      return ((SegmentedButton)this.getSkinnable()).prefWidth(height);
   }

   protected double computeMaxHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
      return ((SegmentedButton)this.getSkinnable()).prefHeight(width);
   }
}
