package impl.org.controlsfx.skin;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.SkinBase;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import org.controlsfx.control.StatusBar;

public class StatusBarSkin extends SkinBase<StatusBar> {
   private HBox leftBox;
   private HBox rightBox;
   private Label label;
   private ProgressBar progressBar;

   public StatusBarSkin(StatusBar statusBar) {
      super(statusBar);
      BooleanBinding notZeroProgressProperty = Bindings.notEqual(0, statusBar.progressProperty());
      GridPane gridPane = new GridPane();
      this.leftBox = new HBox();
      this.leftBox.getStyleClass().add("left-items");
      this.rightBox = new HBox();
      this.rightBox.getStyleClass().add("right-items");
      this.progressBar = new ProgressBar();
      this.progressBar.progressProperty().bind(statusBar.progressProperty());
      this.progressBar.visibleProperty().bind(notZeroProgressProperty);
      this.progressBar.managedProperty().bind(notZeroProgressProperty);
      this.label = new Label();
      this.label.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
      this.label.textProperty().bind(statusBar.textProperty());
      this.label.graphicProperty().bind(statusBar.graphicProperty());
      this.label.styleProperty().bind(((StatusBar)this.getSkinnable()).styleProperty());
      this.label.getStyleClass().add("status-label");
      this.leftBox.getChildren().setAll(((StatusBar)this.getSkinnable()).getLeftItems());
      this.rightBox.getChildren().setAll(((StatusBar)this.getSkinnable()).getRightItems());
      statusBar.getLeftItems().addListener(evt -> this.leftBox.getChildren().setAll(((StatusBar)this.getSkinnable()).getLeftItems()));
      statusBar.getRightItems().addListener(evt -> this.rightBox.getChildren().setAll(((StatusBar)this.getSkinnable()).getRightItems()));
      GridPane.setFillHeight(this.leftBox, true);
      GridPane.setFillHeight(this.rightBox, true);
      GridPane.setFillHeight(this.label, true);
      GridPane.setFillHeight(this.progressBar, true);
      GridPane.setVgrow(this.leftBox, Priority.ALWAYS);
      GridPane.setVgrow(this.rightBox, Priority.ALWAYS);
      GridPane.setVgrow(this.label, Priority.ALWAYS);
      GridPane.setVgrow(this.progressBar, Priority.ALWAYS);
      GridPane.setHgrow(this.label, Priority.ALWAYS);
      gridPane.add(this.leftBox, 0, 0);
      gridPane.add(this.label, 1, 0);
      gridPane.add(this.progressBar, 2, 0);
      gridPane.add(this.rightBox, 3, 0);
      this.getChildren().add(gridPane);
   }
}
