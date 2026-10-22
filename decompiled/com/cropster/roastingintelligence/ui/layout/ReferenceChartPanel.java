package com.cropster.roastingintelligence.ui.layout;

import com.cropster.roastingintelligence.ui.JavaFxController;
import com.cropster.roastingintelligence.ui.JavaFxUtil;
import com.cropster.roastingintelligence.ui.WindowManager;
import com.cropster.roastingintelligence.ui.util.ChartUtils;
import com.cropster.roastingintelligence.ui.util.KeyboardShortcut;
import com.cropster.roastingintelligence.ui.util.WindowUtil;
import com.cropster.util.model.Unit;
import java.awt.Color;
import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Map.Entry;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.cropster.roastingintelligence.AppModel;
import org.cropster.roastingintelligence.BetweenBatchModel;
import org.cropster.roastingintelligence.RoastingIntelligenceApp;
import org.cropster.roastingintelligence.model.BetweenBatchLog;
import org.cropster.roastingintelligence.model.Channel;
import org.cropster.roastingintelligence.model.Curve;
import org.cropster.roastingintelligence.model.CurveModel;
import org.cropster.roastingintelligence.model.Roast;
import org.cropster.roastingintelligence.model.impl.ConverterCurveModel;
import org.cropster.roastingintelligence.model.impl.OffsetCurveModel;
import org.cropster.roastingintelligence.model.impl.StandardCurveModel;
import org.cropster.roastingintelligence.ui.chart.CurveChart;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.fx.ChartViewer;

public class ReferenceChartPanel extends BorderPane implements Initializable {
   public static final String REFERENCE_CHART_WINDOW_ID = "REFERENCE_CHART_WINDOW_ID";
   @FXML
   private Label idTagLabel;
   @FXML
   private Label profileLabel;
   @FXML
   private Button openInBrowserButton;
   @FXML
   private ChartViewer chartViewer;
   private final MenuItem resetMenuItem = new MenuItem(org.cropster.roastingintelligence.util.Messages.m_("Reset axes"));

   public static void showWindow(AppModel appModel, Roast roast, Node owner) {
      hideWindow();
      CurveChart previewChart = createPreviewChart(appModel, roast);
      ReferenceChartPanel previewChartPanel = new ReferenceChartPanel();
      previewChartPanel.init(appModel, roast, previewChart.getJFreeChart());
      Stage previewChartWindow = new Stage();
      previewChartWindow.initOwner(owner.getScene().getWindow());
      previewChartWindow.setTitle(org.cropster.roastingintelligence.util.Messages.m_("Reference chart"));
      previewChartWindow.setMinWidth(700.0);
      previewChartWindow.setMinHeight(500.0);
      previewChartWindow.setScene(new Scene(previewChartPanel));
      previewChartWindow.getScene().getStylesheets().add(JavaFxController.STYLE_SHEET);
      previewChartWindow.getScene()
         .getAccelerators()
         .put(KeyboardShortcut.CTRL_R.getKeyCodeCombination(), (Runnable)() -> ChartUtils.restoreZoom(previewChartPanel.getChartViewer()));
      previewChartWindow.getScene().getAccelerators().put(KeyboardShortcut.ESC.getKeyCodeCombination(), previewChartWindow::hide);
      Platform.runLater(() -> {
         if (WindowManager.get().register(previewChartWindow, "REFERENCE_CHART_WINDOW_ID")) {
            WindowUtil.showSafeStage(previewChartWindow, 700.0, 500.0, -1.0, -1.0, 32.0);
            previewChartWindow.requestFocus();
            Platform.runLater(() -> {
               Point2D position = WindowUtil.getScreenPosition(previewChartWindow, (Stage)owner.getScene().getWindow());
               previewChartWindow.setX(position.getX());
               previewChartWindow.setY(position.getY());
               ChartUtils.restoreZoom(previewChartPanel.getChartViewer());
            });
         }
      });
   }

   public static void hideWindow() {
      WindowManager.get().getWindow("REFERENCE_CHART_WINDOW_ID").ifPresent(Window::hide);
   }

   public static void updateWindow(AppModel appModel, Roast roast) {
      WindowManager.get().getWindow("REFERENCE_CHART_WINDOW_ID").ifPresent(w -> {
         CurveChart previewChart = createPreviewChart(appModel, roast);
         ReferenceChartPanel previewChartPanel = (ReferenceChartPanel)w.getScene().getRoot();
         previewChartPanel.init(appModel, roast, previewChart.getJFreeChart());
      });
   }

   private static CurveChart createPreviewChart(AppModel appModel, Roast roast) {
      Unit tempUnit = appModel.getPreferences().getPreferredUnit(Unit.CELSIUS);
      CurveChart previewChart = new CurveChart(1, null);
      previewChart.setValueAxisLabel(tempUnit, org.cropster.roastingintelligence.util.Messages.m_(tempUnit));
      BetweenBatchLog betweenBatchLog = appModel.getData().getBetweenBatchLogs().get(roast.getLot().getId());
      Map<String, Curve> activeCurves = roast.getCurves(appModel.getActiveChannels());

      for (Entry<String, Curve> entry : activeCurves.entrySet()) {
         Color color = Color.GRAY;
         StandardCurveModel rootModel = new StandardCurveModel(entry.getKey(), roast.getStartDate(), entry.getValue().getUnit());
         rootModel.setValues(entry.getValue().clone());
         CurveModel model = rootModel;
         Unit preferredUnit = appModel.getPreferences().getPreferredUnit(rootModel.getUnit());
         if (preferredUnit != null) {
            model = new ConverterCurveModel(rootModel, preferredUnit);
         }

         previewChart.putPrimaryCurveModel(model, color);
         if (betweenBatchLog != null) {
            long offset = -betweenBatchLog.getDuration();
            Curve bblCurve = betweenBatchLog.getCurves().get(entry.getKey());
            if (bblCurve != null) {
               bblCurve.entrySet().forEach(e -> rootModel.getValues().put(Long.valueOf(e.getKey() + offset), e.getValue()));
               rootModel.fireCurveChanged();
            }

            BetweenBatchModel betweenBatchModel = appModel.getBetweenBatchModel();
            if (betweenBatchModel != null) {
               betweenBatchModel.getChannels().stream().filter(c -> c.isVisible(Channel.Visibility.CHART)).forEach(c -> {
                  CurveModel curveModel = betweenBatchModel.getCurveModel(c);
                  CurveModel var7x = new OffsetCurveModel(curveModel, t -> t - appModel.getBetweenBatchModel().getStartDate().getTime() + offset);
                  previewChart.putPrimaryCurveModel(var7x, c.getColor());
               });
            }
         }
      }

      roast.getComments().stream().filter(appModel::isCommentEnabledInChart).forEach(c -> previewChart.addComment(c, null));
      if (betweenBatchLog != null) {
         previewChart.setStartMarker(0L);
      }

      return previewChart;
   }

   public ReferenceChartPanel() {
      JavaFxUtil.loadComponent(this);
      this.openInBrowserButton.managedProperty().bind(this.openInBrowserButton.visibleProperty());
      this.openInBrowserButton.visibleProperty().bind(this.openInBrowserButton.onActionProperty().isNotNull());
   }

   public void init(AppModel appModel, Roast roast, JFreeChart chart) {
      this.idTagLabel.setText(roast.getLot().getIdTag());
      this.profileLabel
         .setText(appModel.getData().getProfiles().containsKey(roast.getProfile()) ? appModel.getData().getProfiles().get(roast.getProfile()).getName() : "?");
      String uri = appModel.getUri().resolve(String.format("/r/roast/%s", roast.getLot().getId())).toString();
      this.openInBrowserButton.setOnAction(e -> RoastingIntelligenceApp.showDocument(uri));
      this.chartViewer.setChart(chart);
      ChartUtils.replaceZoomHandler(this.chartViewer.getCanvas());
      if (!this.chartViewer.getContextMenu().getItems().contains(this.resetMenuItem)) {
         this.resetMenuItem.setAccelerator(KeyboardShortcut.CTRL_R.getKeyCodeCombination());
         this.resetMenuItem.setOnAction(e -> ChartUtils.restoreZoom(this.chartViewer));
         this.chartViewer.getContextMenu().getItems().add(this.resetMenuItem);
      }
   }

   public ChartViewer getChartViewer() {
      return this.chartViewer;
   }

   public void initialize(URL url, ResourceBundle resourceBundle) {
      JavaFxUtil.setDefaultLineSpacing(this);
   }
}
