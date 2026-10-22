package com.cropster.roastingintelligence.ui.chart;

import com.cropster.roastingintelligence.ui.JavaFxUtil;
import com.cropster.roastingintelligence.ui.util.ChartUtils;
import com.cropster.roastingintelligence.ui.util.KeyboardShortcut;
import com.cropster.roastingintelligence.ui.util.Styles;
import java.util.Objects;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.input.GestureEvent;
import javafx.scene.input.TouchEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import org.apache.commons.lang3.Validate;
import org.cropster.roastingintelligence.ui.chart.CurveChart;
import org.jfree.chart.fx.ChartCanvas;
import org.jfree.chart.fx.ChartViewer;

public class ChartWrapper extends BorderPane {
   private CurveChart chart;
   private AnchorPane chartAnchorPane;
   private ButtonBar chartToolbar;
   private ChartViewer chartViewer;
   private MouseCoordinatesPane mouseCoordinates;
   private MouseCoordinatesTracker mouseCoordinatesTracker;
   private MenuItem resetMenuItem;

   public void init(CurveChart curveChart) {
      this.chart = curveChart;
      this.chartViewer = new ChartViewer(curveChart.getJFreeChart());
      if (JavaFxUtil.isTouchScreenDetected()) {
         this.chartViewer.addEventFilter(TouchEvent.ANY, Event::consume);
         this.chartViewer.addEventFilter(GestureEvent.ANY, Event::consume);
      }

      ChartCanvas chartCanvas = this.chartViewer.getCanvas();
      ChartUtils.replaceZoomHandler(chartCanvas);
      this.resetMenuItem = new MenuItem(org.cropster.roastingintelligence.util.Messages.m_("Reset axes"));
      this.resetMenuItem.setAccelerator(KeyboardShortcut.CTRL_R.getKeyCodeCombination());
      this.resetMenuItem.setOnAction(e -> {
         ChartUtils.restoreZoom(this.chartViewer);
         e.consume();
      });
      this.chartViewer.getContextMenu().getItems().add(this.resetMenuItem);
      this.chartAnchorPane = new AnchorPane(new Node[]{this.chartViewer});
      AnchorPane.setTopAnchor(this.chartViewer, 0.0);
      AnchorPane.setBottomAnchor(this.chartViewer, 0.0);
      AnchorPane.setLeftAnchor(this.chartViewer, 0.0);
      AnchorPane.setRightAnchor(this.chartViewer, 0.0);
      BorderPane chartAlarmBorderPane = new BorderPane(this.chartAnchorPane);
      chartAlarmBorderPane.getStyleClass().add("pane--elevated");
      this.mouseCoordinates = new MouseCoordinatesPane();
      ButtonBar.setButtonData(this.mouseCoordinates, ButtonData.RIGHT);
      ButtonBar.setButtonUniformSize(this.mouseCoordinates, false);
      this.chartToolbar = new ButtonBar(ButtonData.LEFT.getTypeCode() + ButtonData.BIG_GAP.getTypeCode() + ButtonData.RIGHT.getTypeCode());
      this.chartToolbar.setStyle("-fx-background-insets: 0 8 0 8;");
      this.chartToolbar.setMinHeight(32.0);
      BorderPane.setMargin(this.chartToolbar, Styles.INSETS_TOP_RIGHT_LEFT_SMALL);
      this.chartToolbar.getButtons().add(this.mouseCoordinates);
      this.setTop(this.chartToolbar);
      this.setCenter(chartAlarmBorderPane);
      this.mouseCoordinatesTracker = new MouseCoordinatesTracker(this.chartViewer, this.mouseCoordinates, this.chartAnchorPane);
   }

   public void addToLeftToolbarSide(Node leftToolbarContent) {
      Objects.requireNonNull(leftToolbarContent, "Null 'leftToolbarContent' parameter.");
      ButtonBar.setButtonData(leftToolbarContent, ButtonData.LEFT);
      ButtonBar.setButtonUniformSize(leftToolbarContent, false);
      this.chartToolbar.getButtons().add(0, leftToolbarContent);
   }

   public void addToRightToolbarSide(Node rightToolbarContent) {
      Objects.requireNonNull(rightToolbarContent, "Null 'rightToolbarContent' parameter.");
      ButtonBar.setButtonData(rightToolbarContent, ButtonData.RIGHT);
      ButtonBar.setButtonUniformSize(rightToolbarContent, false);
      this.chartToolbar.getButtons().add(rightToolbarContent);
   }

   public void removeFromToolbar(Node node) {
      Objects.requireNonNull(node, "Null 'node' parameter.");
      Validate.isTrue(node != this.mouseCoordinates, "Cannot remove the mouse coordinates pane.", new Object[0]);
      this.chartToolbar.getButtons().remove(node);
   }

   public MouseCoordinatesPane getMouseCoordinates() {
      return this.mouseCoordinates;
   }

   public AnchorPane getChartAnchorPane() {
      return this.chartAnchorPane;
   }

   public ChartViewer getChartViewer() {
      return this.chartViewer;
   }

   public CurveChart getChart() {
      return this.chart;
   }

   public void dispose() {
      this.chart.dispose();
      this.chart = null;
   }

   public EventHandler<ActionEvent> getOnResetAction() {
      return this.resetMenuItem.getOnAction();
   }

   public void setOnResetAction(EventHandler<ActionEvent> action) {
      this.resetMenuItem.setOnAction(action);
   }

   public void updateCrosshairs() {
      this.mouseCoordinatesTracker.updateCrosshairs();
   }
}
