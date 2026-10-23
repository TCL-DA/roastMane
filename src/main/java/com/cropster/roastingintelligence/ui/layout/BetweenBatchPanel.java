package com.cropster.roastingintelligence.ui.layout;

import com.cropster.roastingintelligence.ui.JavaFxUtil;
import com.cropster.roastingintelligence.ui.chart.BetweenBatchChartWrapper;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Side;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import org.jfree.chart.fx.ChartViewer;

public class BetweenBatchPanel extends BorderPane implements Initializable {
   @FXML
   private VBox placeholder;
   private BooleanProperty dimmedProperty = new SimpleBooleanProperty(false);
   private DoubleProperty chartOpacityProperty = new SimpleDoubleProperty(0.0);
   private BetweenBatchChartWrapper chartWrapper = null;

   public BetweenBatchPanel() {
      JavaFxUtil.loadComponent(this);
   }

   public void setChart(BetweenBatchChartWrapper chartWrapper) {
      if (this.chartWrapper != null) {
         this.getChildren().remove(this.chartWrapper);
      }

      this.chartWrapper = chartWrapper;
      if (chartWrapper != null) {
         this.placeholder.setVisible(false);
         chartWrapper.updateCrosshairs();
         chartWrapper.getChartViewer().opacityProperty().bind(this.chartOpacityProperty);
         chartWrapper.getStopMenuItem().disableProperty().bind(this.placeholder.visibleProperty().or(this.dimmedProperty));
         chartWrapper.getRestartMenuItem().disableProperty().bind(this.placeholder.visibleProperty());
         this.setCenter(chartWrapper);
      } else {
         this.placeholder.setVisible(true);
      }
   }

   public void initialize(URL url, ResourceBundle resourceBundle) {
      JavaFxUtil.setDefaultLineSpacing(this);
      this.chartOpacityProperty
         .bind(
            Bindings.createDoubleBinding(
               () -> this.dimmedProperty.get() && !this.placeholder.isVisible() ? 0.3 : 1.0,
               new Observable[]{this.dimmedProperty, this.placeholder.visibleProperty()}
            )
         );
   }

   public BetweenBatchChartWrapper getChartWrapper() {
      return this.chartWrapper;
   }

   public MenuItem getRestartMenuItem() {
      return this.chartWrapper.getRestartMenuItem();
   }

   public MenuItem getStopMenuItem() {
      return this.chartWrapper.getStopMenuItem();
   }

   @FXML
   private void showContextMenu(ActionEvent e) {
      this.chartWrapper.getMoreButton().getContextMenu().show(this.chartWrapper.getMoreButton(), Side.BOTTOM, 0.0, 0.0);
   }

   public BooleanProperty dimmedProperty() {
      return this.dimmedProperty;
   }

   public boolean isDimmed() {
      return this.dimmedProperty.get();
   }

   public void setDimmed(boolean dimmed) {
      this.dimmedProperty.set(dimmed);
   }

   public ReadOnlyDoubleProperty chartOpacityProperty() {
      return this.chartOpacityProperty;
   }

   public double getChartOpacity() {
      return this.chartOpacityProperty.get();
   }

   public ChartViewer getChartViewer() {
      return this.chartWrapper != null ? this.chartWrapper.getChartViewer() : null;
   }
}
