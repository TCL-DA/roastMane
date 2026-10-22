package org.cropster.roastingintelligence.ui.chart;

import com.cropster.util.Collect;
import com.cropster.util.model.Unit;
import com.cropster.util.text.DurationFormat;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.stream.Stream;
import javafx.application.Platform;
import javax.imageio.ImageIO;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.cropster.roastingintelligence.Defaults;
import org.cropster.roastingintelligence.listener.CurveModelListener;
import org.cropster.roastingintelligence.model.CurveModel;
import org.cropster.roastingintelligence.model.Measure;
import org.cropster.roastingintelligence.model.RoastingComment;
import org.cropster.roastingintelligence.util.MeasureFormat;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYAnnotation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.axis.TickUnits;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.Marker;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.renderer.xy.XYStepRenderer;
import org.jfree.chart.ui.Layer;
import org.jfree.chart.ui.RectangleAnchor;
import org.jfree.chart.ui.RectangleInsets;
import org.jfree.chart.ui.TextAnchor;
import org.jfree.data.Range;
import org.jfree.data.RangeType;
import org.jfree.data.xy.XYRangeInfo;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CurveChart implements CurveModelListener {
   private final Logger logger = LoggerFactory.getLogger(CurveChart.class);
   private static final String PATH_BRANDING = "/images/roastmane-branding.png";
   public static final Font FONT_BIG = new Font(null, 0, 32);
   public static final Font FONT_MEDIUM = new Font(null, 0, 20);
   public static final Font FONT_SMALL = new Font(null, 0, 14);
   public static final Font FONT_TINY = new Font(null, 0, 10);
   private static final double MINUTE = 60000.0;
   private static final Color COLOR_TRANSPARENT = new Color(0, 0, 0, 0);
   private static final Paint COLOR_ORANGE = new Color(255, 153, 0);
   private static final Stroke STROKE_DASHED = new BasicStroke(1.0F, 2, 1, 1.0F, new float[]{10.0F, 10.0F}, 0.0F);
   private static final int MAIN_PLOT_WEIGHT = 7;
   private static final double MARKER_OFFSET_DIFF = 12.0;
   private final DurationFormat _durationFormat = DurationFormat.getDurationInstance(true);
   private final MeasureFormat _commentMeasureFormat;
   private final TickUnits _tickUnitsTime;
   private final TickUnits _tickUnits;
   private final TickUnits _tickUnitsRiseRate;
   private final int _strokeWidth;
   private final BufferedImage _brandingImage;
   private final Font _font;
   private final JFreeChart _chart;
   private final NumberAxis _timeAxis;
   private String _primaryCurveName = "beanTemperature";
   private final Map<String, CurveModel> _curveModels;
   private final Map<Unit, XYPlot> _subPlots;
   private final Map<CurveModel, XYSeries> _series;
   private final Map<CurveModel, Integer> _datasetIndexes;
   private final LinkedHashMap<Triple<Long, String, XYPlot>, Marker> _markers;
   private final HashMap<Pair<Marker, XYPlot>, Marker> _autoMarkers = new HashMap<>();
   private PredictionAnnotation _predictionAnnotation;
   private AreaAnnotation _areaAnnotation;
   private FirstCrackAnnotation _firstCrackPredictionAnnotation;
   private final Map<XYPlot, BetweenBatchAnnotation> _betweenBatchAnnotations = new LinkedHashMap<>();
   private final Color _markerColor = Color.BLACK;
   private Color _predictionColor = Color.BLUE;
   private ValueMarker _startMarker;
   private ValueMarker _endMarker;
   private double _markerOffset = 20.0;

   public CurveChart(int strokeWidth, Font font) {
      NumberFormat durationFormat = DurationFormat.getDurationInstance(true);
      this._tickUnitsTime = new TickUnits();
      this._tickUnitsTime.add(new NumberTickUnit(1000.0, durationFormat));
      this._tickUnitsTime.add(new NumberTickUnit(5000.0, durationFormat));
      this._tickUnitsTime.add(new NumberTickUnit(10000.0, durationFormat));
      this._tickUnitsTime.add(new NumberTickUnit(30000.0, durationFormat));
      this._tickUnitsTime.add(new NumberTickUnit(60000.0, durationFormat));
      this._tickUnitsTime.add(new NumberTickUnit(120000.0, durationFormat));
      this._tickUnitsTime.add(new NumberTickUnit(180000.0, durationFormat));
      this._tickUnitsTime.add(new NumberTickUnit(300000.0, durationFormat));
      this._tickUnitsTime.add(new NumberTickUnit(600000.0, durationFormat));
      this._tickUnitsTime.add(new NumberTickUnit(900000.0, durationFormat));
      this._tickUnitsTime.add(new NumberTickUnit(1800000.0, durationFormat));
      DecimalFormat format = new DecimalFormat("0.#");
      this._tickUnits = new TickUnits();
      this._tickUnits.add(new NumberTickUnit(0.1, format));
      this._tickUnits.add(new NumberTickUnit(0.5, format));
      this._tickUnits.add(new NumberTickUnit(1.0, format));
      this._tickUnits.add(new NumberTickUnit(5.0, format));
      this._tickUnits.add(new NumberTickUnit(10.0, format));
      this._tickUnits.add(new NumberTickUnit(25.0, format));
      this._tickUnits.add(new NumberTickUnit(50.0, format));
      this._tickUnits.add(new NumberTickUnit(100.0, format));
      DecimalFormat riseRateFormat = new DecimalFormat("0");
      this._tickUnitsRiseRate = new TickUnits();
      this._tickUnitsRiseRate.add(new NumberTickUnit(1.0, riseRateFormat));
      this._tickUnitsRiseRate.add(new NumberTickUnit(2.0, riseRateFormat));
      this._tickUnitsRiseRate.add(new NumberTickUnit(5.0, riseRateFormat));
      this._tickUnitsRiseRate.add(new NumberTickUnit(10.0, riseRateFormat));
      this._tickUnitsRiseRate.add(new NumberTickUnit(50.0, riseRateFormat));
      this._tickUnitsRiseRate.add(new NumberTickUnit(100.0, riseRateFormat));
      this._commentMeasureFormat = MeasureFormat.getInstance();
      this._commentMeasureFormat.getNumberFormat().setMaximumFractionDigits(1);

      try {
         this._brandingImage = ImageIO.read(CurveChart.class.getResource("/images/roastmane-branding.png"));
      } catch (IOException var7) {
         throw new RuntimeException("Failed to load /images/roastmane-branding.png", var7);
      }

      this._strokeWidth = strokeWidth;
      this._font = font;
      this._curveModels = new LinkedHashMap<>();
      this._markers = new LinkedHashMap<>();
      this._subPlots = Collections.synchronizedMap(new TreeMap<>());
      this._series = Collections.synchronizedMap(new HashMap<>());
      this._datasetIndexes = new HashMap<>();
      this._timeAxis = new NumberAxis();
      this._timeAxis.setAxisLineVisible(false);
      this._timeAxis.setLowerBound(0.0);
      this._timeAxis.setAutoRange(true);
      this._timeAxis.setStandardTickUnits(this._tickUnitsTime);
      if (this._font != null) {
         this._timeAxis.setTickLabelFont(this._font);
         this._timeAxis.setLabelFont(this._font);
      }

      durationFormat = new CombinedDomainXYPlot(this._timeAxis);
      durationFormat.setInsets(new RectangleInsets(15.0, 5.0, 5.0, 5.0));
      durationFormat.setForegroundAlpha(0.5F);
      durationFormat.setBackgroundAlpha(1.0F);
      durationFormat.setDomainGridlinePaint(Color.GRAY);
      durationFormat.setRangeGridlinePaint(Color.GRAY);
      durationFormat.setBackgroundPaint(Color.WHITE);
      durationFormat.setDomainPannable(true);
      durationFormat.setRenderer(new XYLineAndShapeRenderer());
      durationFormat.mapDatasetToRangeAxis(0, 0);
      this._chart = new JFreeChart(durationFormat);
      this._chart.setTextAntiAlias(true);
      this._chart.setAntiAlias(true);
      this._chart.removeLegend();
      this._chart.setBackgroundPaint(Color.WHITE);
      this.setStartMarker(0L);
   }

   public JFreeChart getJFreeChart() {
      return this._chart;
   }

   public Color getPredictionColor() {
      return this._predictionColor;
   }

   public void setPredictionColor(Color predictionColor) {
      this._predictionColor = predictionColor;
   }

   public String getPrimaryCurveName() {
      return this._primaryCurveName;
   }

   public void setPrimaryCurveName(String primaryCurveName) {
      this._primaryCurveName = primaryCurveName;
   }

   public Collection<CurveModel> getCurveModels() {
      return this._curveModels.values();
   }

   public synchronized CurveModel removeCurveModel(String name) {
      CurveModel model = this._curveModels.remove(name);
      if (model != null) {
         model.removeListener(this);
         this._series.remove(model).clear();
         this._chart.getXYPlot().setDataset(this._datasetIndexes.remove(model), null);
      }

      return model;
   }

   private synchronized CurveModel putCurveModel(CurveModel model, Color color, int axisIndex) {
      boolean primaryPlot = Unit.CELSIUS.isCompatible(model.getUnit());
      CurveModel previousModel = this._curveModels.put(model.getName(), model);
      if (previousModel == null) {
         NumberAxis rangeAxis = new NumberAxis(org.cropster.roastingintelligence.util.Messages.m_(model.getUnit()));
         rangeAxis.setAxisLineVisible(false);
         rangeAxis.setRangeType(RangeType.POSITIVE);
         rangeAxis.setAutoRange(true);
         rangeAxis.setAutoRangeIncludesZero(false);
         rangeAxis.setAutoRangeMinimumSize(1.0);
         rangeAxis.setStandardTickUnits(this._tickUnits);
         if (this._font != null) {
            rangeAxis.setTickLabelFont(this._font);
            rangeAxis.setLabelFont(this._font);
         }

         XYPlot subPlot = this._subPlots.get(model.getUnit());
         if (subPlot == null) {
            subPlot = new XYPlot();
            if (this._font != null) {
               subPlot.setNoDataMessageFont(this._font);
            }

            subPlot.setOutlineVisible(false);
            subPlot.setRangeAxis(rangeAxis);
            subPlot.setForegroundAlpha(0.5F);
            subPlot.setBackgroundAlpha(1.0F);
            subPlot.setDomainGridlinePaint(Color.GRAY);
            subPlot.setRangeGridlinePaint(Color.GRAY);
            subPlot.setBackgroundPaint(Color.WHITE);
            subPlot.setRangePannable(true);
            subPlot.setRenderer(new XYLineAndShapeRenderer());
            subPlot.mapDatasetToRangeAxis(0, 0);
            CombinedDomainXYPlot plot = (CombinedDomainXYPlot)this._chart.getPlot();
            if (primaryPlot) {
               if (this._brandingImage != null) {
                  subPlot.setBackgroundImage(this._brandingImage);
                  subPlot.setBackgroundImageAlignment(6);
               }

               plot.add(subPlot, 7);
               this._subPlots.put(model.getUnit(), subPlot);
            } else {
               plot.add(subPlot);
               this._subPlots.put(model.getUnit(), subPlot);
            }
         }

         XYSeries series = new XYSeries(model.getName(), true, false);
         XYSeriesCollection dataset = new CurveChart.XYSeriesFullRangeCollection(series);
         XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer)(model.getName().matches(".+[cC]ontrol")
            ? new XYStepRenderer()
            : new XYLineAndShapeRenderer());
         renderer.setSeriesPaint(0, color);
         renderer.setSeriesShapesVisible(0, false);
         if ((!model.getName().matches(".+[cC]ontrol") || !model.getName().startsWith("Perfect "))
            && !model.getName().matches(".+[pP]rediction")
            && !model.getName().matches("\\[.+\\]RoR.*")) {
            renderer.setSeriesStroke(0, new BasicStroke((float)this._strokeWidth));
         } else {
            renderer.setDrawSeriesLineAsPath(true);
            renderer.setSeriesStroke(0, new BasicStroke(2.0F, 1, 1, 1.0F, new float[]{5.0F, 5.0F}, 0.0F));
         }

         int index = subPlot.getDatasetCount();
         subPlot.setInsets(new RectangleInsets(primaryPlot ? 0.0 : 20.0, 0.0, 3.0, 0.0));
         subPlot.setDataset(index, dataset);
         subPlot.setRenderer(index, renderer);
         subPlot.mapDatasetToRangeAxis(index, axisIndex);
         XYSeries s = this._series.put(model, series);

         assert s == null;

         this._datasetIndexes.put(model, index);
      } else {
         previousModel.removeListener(this);
         this._series.put(model, this._series.remove(previousModel));
         this._datasetIndexes.put(model, this._datasetIndexes.remove(previousModel));
      }

      model.addListener(this);
      model.fireCurveChanged();
      return previousModel;
   }

   public CurveModel putPrimaryCurveModel(CurveModel model, Color color) {
      return this.putCurveModel(model, color, 0);
   }

   public CurveModel putSecondaryCurveModel(CurveModel model, Color color) {
      XYPlot plot = this._subPlots.get(model.getUnit());
      if (plot.getRangeAxisCount() == 1) {
         NumberAxis secondaryAxis = new NumberAxis(org.cropster.roastingintelligence.util.Messages.m_("Rate of Rise"));
         secondaryAxis.setAxisLineVisible(false);
         secondaryAxis.setRange(0.0, Defaults.MAX_RISE_RATE);
         secondaryAxis.setAutoRange(false);
         secondaryAxis.setStandardTickUnits(this._tickUnitsRiseRate);
         if (this._font != null) {
            secondaryAxis.setTickLabelFont(this._font);
            secondaryAxis.setLabelFont(this._font);
         }

         plot.setRangeAxis(1, secondaryAxis);
      }

      return this.putCurveModel(model, color, 1);
   }

   public long getTimeRangeLowerBound() {
      return (long)this._timeAxis.getRange().getLowerBound() / 1000L;
   }

   public void setTimeRangeLowerBound(long lowerBound) {
      this._timeAxis.setRange((double)(lowerBound * 1000L), this._timeAxis.getRange().getUpperBound());
   }

   public long getTimeRangeUpperBound() {
      return (long)this._timeAxis.getRange().getUpperBound() / 1000L;
   }

   public void setTimeRangeUpperBound(long upperBound) {
      this._timeAxis.setRange(this._timeAxis.getRange().getLowerBound(), (double)(upperBound * 1000L));
   }

   public synchronized void setTimeRange(long lowerBound, long upperBound) {
      this._timeAxis.setRange((double)(lowerBound * 1000L), (double)(upperBound * 1000L));
   }

   public synchronized void setValueAutoRange(boolean autoRange) {
      for (XYPlot plot : this._subPlots.values()) {
         plot.getRangeAxis().setAutoRange(autoRange);
      }
   }

   public synchronized void setValueRange(Unit unit, double minValue, double maxValue) {
      if (this._subPlots.containsKey(unit)) {
         this._subPlots.get(unit).getRangeAxis().setRange(minValue, maxValue);
      }
   }

   public synchronized void setSecondaryValueRange(Unit unit, double minValue, double maxValue) {
      if (this._subPlots.containsKey(unit)) {
         XYPlot subPlot = this._subPlots.get(unit);
         if (subPlot.getRangeAxisCount() > 1) {
            subPlot.getRangeAxis(1).setRange(minValue, maxValue);
         }
      }
   }

   public synchronized void setTimeAxisLabel(String label) {
      this._timeAxis.setLabel(label);
   }

   public synchronized void setValueAxisLabel(Unit unit, String label) {
      if (this._subPlots.containsKey(unit)) {
         this._subPlots.get(unit).getRangeAxis().setLabel(label);
      }
   }

   private String getSuffix(String suffixCurve, Long time) {
      String suffix = null;
      CurveModel curveModel = this._curveModels.get(suffixCurve);
      if (curveModel != null) {
         Long key = Collect.proximate(curveModel.getValues(), time, 500L);
         if (key != null) {
            Double value = curveModel.getValue(key);
            Measure measure = new Measure(value, curveModel.getUnit());
            suffix = " @ " + this._commentMeasureFormat.format(measure);
         }
      }

      return suffix;
   }

   private String createCommentLabel(RoastingComment comment, String curveName) {
      String label = String.format("[%s]", this._durationFormat.format(comment.getTime()));
      if (comment.getEvent() != null) {
         label = label
            + " "
            + (
               org.cropster.roastingintelligence.util.Messages.getBundle().containsKey(comment.getEvent())
                  ? org.cropster.roastingintelligence.util.Messages.m_(comment.getEvent())
                  : comment.getEvent()
            );
      }

      if (comment.getText() != null) {
         label = label + (label.endsWith("]") ? " " : " - ");
         label = label + comment.getText();
      }

      String suffix = this.getSuffix(curveName, comment.getTime());
      if (suffix != null) {
         label = label + suffix;
      }

      return label.trim();
   }

   public synchronized void addComment(RoastingComment comment, Color color, String curveName) {
      this.addComment(comment, color, color, curveName);
   }

   public synchronized void addComment(RoastingComment comment, Color color, Color labelColor, String curveName) {
      String label = this.createCommentLabel(comment, curveName);
      int i = 0;

      for (Entry<Unit, XYPlot> entry : this._subPlots.entrySet()) {
         Marker marker = new ValueMarker((double)comment.getTime());
         marker.setStroke(new BasicStroke(1.0F));
         marker.setPaint(color);
         if (this._font != null) {
            marker.setLabelFont(this._font);
         }

         if (i++ == 0) {
            marker.setLabelPaint(labelColor);
            marker.setLabelBackgroundColor(COLOR_TRANSPARENT);
            marker.setLabel(label);
            marker.setLabelAnchor(RectangleAnchor.TOP_RIGHT);
            marker.setLabelTextAnchor(TextAnchor.TOP_LEFT);
            marker.setLabelOffset(new RectangleInsets(this._markerOffset, 0.0, 0.0, 5.0));
         }

         entry.getValue().addDomainMarker(marker);
         this._markers.put(new ImmutableTriple(comment.getTime(), curveName, entry.getValue()), marker);
      }

      this._markerOffset += 12.0;
      if (this._font != null) {
         this._markerOffset += 5.0;
      }
   }

   public synchronized void addComment(RoastingComment comment, String curveName) {
      this.addComment(comment, Color.RED, curveName);
   }

   public synchronized void updateComment(RoastingComment comment) {
      XYPlot mainPlot = this._subPlots.get(Unit.CELSIUS);

      for (Entry<Triple<Long, String, XYPlot>, Marker> entry : this._markers.entrySet()) {
         if (mainPlot == entry.getKey().getRight() && ((Long)entry.getKey().getLeft()).equals(comment.getTime())) {
            Marker marker = entry.getValue();
            marker.setLabel(this.createCommentLabel(comment, "beanTemperature"));
            return;
         }
      }
   }

   public synchronized void removeComment(RoastingComment comment, String curveName) {
      int i = 0;

      for (XYPlot plot : this._subPlots.values()) {
         Triple<Long, String, XYPlot> key = new ImmutableTriple(comment.getTime(), curveName, plot);
         i++;
         if (i == this._subPlots.size() && !this._markers.isEmpty() && ((Triple)Collect.last(this._markers.keySet())).equals(key)) {
            this._markerOffset -= 12.0;
         }

         plot.removeDomainMarker(this._markers.remove(key));
      }
   }

   public AreaAnnotation annotateArea(long start, long end, Long referenceTime, String text, Color color) {
      if (this._subPlots.isEmpty()) {
         throw new IllegalStateException("No models added");
      } else {
         String toolTip = text + " - " + this._durationFormat.format(end - start);
         if (referenceTime != null) {
            toolTip = toolTip + " / " + this._durationFormat.format(referenceTime);
         }

         XYPlot plot = (XYPlot)Collect.first(this._subPlots.values());
         this._areaAnnotation = new AreaAnnotation((double)start, (double)end, this._font, color);
         this._areaAnnotation.setToolTipText(toolTip);
         plot.getRenderer().addAnnotation(this._areaAnnotation);
         return this._areaAnnotation;
      }
   }

   public void removeAreaAnnotation(XYAnnotation areaAnnotation) {
      if (this._subPlots.isEmpty()) {
         throw new IllegalStateException("No models added");
      } else {
         XYPlot plot = (XYPlot)Collect.first(this._subPlots.values());
         plot.getRenderer().removeAnnotation(areaAnnotation);
         if (this._areaAnnotation == areaAnnotation) {
            this._areaAnnotation = null;
         }
      }
   }

   public double getLargestAnnotatedTime() {
      if (this._predictionAnnotation != null) {
         return this._predictionAnnotation.getX2();
      } else {
         return this._areaAnnotation != null ? this._areaAnnotation.getX2() : Double.NaN;
      }
   }

   public PredictionAnnotation setPredictionAnnotation(long start, long end) {
      if (this._predictionAnnotation == null) {
         this._predictionAnnotation = new PredictionAnnotation((double)start, (double)end, this._predictionColor);
         this._subPlots
            .entrySet()
            .stream()
            .filter(e -> Unit.CELSIUS.equals(e.getKey().getSiUnit()))
            .flatMap(e -> Stream.of(e.getValue()))
            .findFirst()
            .ifPresent(plot -> plot.getRenderer().addAnnotation(this._predictionAnnotation, Layer.BACKGROUND));
      } else {
         this._predictionAnnotation.setX1((double)start);
         this._predictionAnnotation.setX2((double)end);
      }

      return this._predictionAnnotation;
   }

   public void removePredictionAnnotation() {
      Platform.runLater(() -> {
         if (this._predictionAnnotation != null) {
            this.removeAreaAnnotation(this._predictionAnnotation);
         }

         this._predictionAnnotation = null;
      });
   }

   public void setFirstCrackPredictionAnnotation(long percentile10, long percentile50, long percentile90) {
      Platform.runLater(
         () -> {
            if (this._firstCrackPredictionAnnotation == null) {
               this._firstCrackPredictionAnnotation = new FirstCrackAnnotation(percentile10, percentile50, percentile90);
               this._subPlots
                  .entrySet()
                  .stream()
                  .filter(e -> Unit.CELSIUS.equals(e.getKey().getSiUnit()))
                  .flatMap(e -> Stream.of(e.getValue()))
                  .findFirst()
                  .ifPresent(plot -> plot.getRenderer().addAnnotation(this._firstCrackPredictionAnnotation, Layer.BACKGROUND));
            } else {
               this._firstCrackPredictionAnnotation.setPercentile10(percentile10);
               this._firstCrackPredictionAnnotation.setPercentile50(percentile50);
               this._firstCrackPredictionAnnotation.setPercentile90(percentile90);
            }
         }
      );
   }

   public void removeFirstCrackPredictionAnnotation() {
      Platform.runLater(() -> {
         if (this._firstCrackPredictionAnnotation != null) {
            this.removeAreaAnnotation(this._firstCrackPredictionAnnotation);
         }

         this._firstCrackPredictionAnnotation = null;
      });
   }

   public void setBetweenBatchAnnotation(long start, long end) {
      if (this._betweenBatchAnnotations.isEmpty()) {
         for (Entry<Unit, XYPlot> entry : this._subPlots.entrySet()) {
            XYPlot plot = entry.getValue();
            BetweenBatchAnnotation annotation = new BetweenBatchAnnotation(
               (double)start, (double)end, this._font.deriveFont((float)this._font.getSize() - 2.0F)
            );
            if (Unit.CELSIUS.equals(entry.getKey().getSiUnit())) {
               annotation.setToolTipText(org.cropster.roastingintelligence.util.Messages.m_("Between batch time"));
            }

            plot.getRenderer().addAnnotation(annotation, Layer.BACKGROUND);
            this._betweenBatchAnnotations.put(plot, annotation);
         }
      }
   }

   public void removeBetweenBatchAnnotation() {
      for (Entry<XYPlot, BetweenBatchAnnotation> entry : this._betweenBatchAnnotations.entrySet()) {
         entry.getKey().getRenderer().removeAnnotation((XYAnnotation)entry.getValue());
      }

      this._betweenBatchAnnotations.clear();
   }

   private ValueMarker createMarker(long duration, Color color) {
      ValueMarker marker = new ValueMarker((double)duration);
      marker.setStroke(new BasicStroke(1.0F));
      marker.setPaint(color);
      return marker;
   }

   private ValueMarker createMarker(long duration, Color color, String label) {
      ValueMarker marker = this.createMarker(duration, color);
      marker.setLabelPaint(color);
      marker.setLabelBackgroundColor(COLOR_TRANSPARENT);
      marker.setLabel(label);
      if (this._font != null) {
         marker.setLabelFont(this._font);
      }

      marker.setLabelAnchor(RectangleAnchor.TOP_RIGHT);
      marker.setLabelTextAnchor(TextAnchor.TOP_LEFT);
      marker.setLabelOffset(new RectangleInsets(5.0, 0.0, 0.0, 5.0));
      return marker;
   }

   private <T extends Marker> T cloneMarker(T marker) {
      try {
         return (T)marker.clone();
      } catch (CloneNotSupportedException var3) {
         throw new RuntimeException(var3);
      }
   }

   private <T extends Marker> T replaceMarker(T oldMarker, T newMarker) {
      if (oldMarker != null) {
         if (newMarker != null) {
            newMarker.setLabelOffset(oldMarker.getLabelOffset());
         }

         for (Entry<Unit, XYPlot> e : this._subPlots.entrySet()) {
            XYPlot plot = e.getValue();
            if (Unit.CELSIUS.isCompatible(e.getKey())) {
               plot.removeDomainMarker(oldMarker);
            } else {
               plot.removeDomainMarker(this._autoMarkers.remove(new ImmutablePair(oldMarker, plot)));
            }
         }
      } else {
         newMarker.setLabelOffset(new RectangleInsets(5.0, 0.0, 0.0, 5.0));
      }

      if (newMarker != null) {
         for (Entry<Unit, XYPlot> ex : this._subPlots.entrySet()) {
            XYPlot plot = ex.getValue();
            if (Unit.CELSIUS.isCompatible(ex.getKey())) {
               plot.addDomainMarker(newMarker);
            } else {
               Marker marker = this.cloneMarker(newMarker);
               marker.setLabel(null);
               plot.addDomainMarker(marker);
               this._autoMarkers.put(new ImmutablePair(newMarker, plot), marker);
            }
         }
      }

      return newMarker;
   }

   public synchronized void setDetectingMarker() {
      ValueMarker marker = this.createMarker(0L, Color.ORANGE, org.cropster.roastingintelligence.util.Messages.m_("Detecting start ..."));
      marker.setStroke(STROKE_DASHED);
      marker.setPaint(COLOR_ORANGE);
      marker.setLabelPaint(COLOR_ORANGE);
      this._startMarker = this.replaceMarker(this._startMarker, marker);
   }

   public synchronized void setStartMarker(long value) {
      String text = org.cropster.roastingintelligence.util.Messages.m_("Start");
      String suffix = this.getSuffix("beanTemperature", value);
      if (suffix != null) {
         text = text + suffix;
      }

      this._startMarker = this.replaceMarker(this._startMarker, this.createMarker(value, this._markerColor, text));
   }

   public synchronized void setEndMarker(Long value) {
      if (value != null) {
         String text = org.cropster.roastingintelligence.util.Messages.m_("End");
         String suffix = this.getSuffix(this._primaryCurveName, value);
         if (suffix != null) {
            text = text + suffix;
         }

         this._endMarker = this.replaceMarker(this._endMarker, this.createMarker(value, this._markerColor, text));
      } else if (this._endMarker != null) {
         this._endMarker = this.replaceMarker(this._endMarker, null);
      }
   }

   private synchronized ValueMarker addTimeMarkerInternal(long value, String label) {
      String text = String.format("[%s] %s", this._durationFormat.format(value), label);
      String suffix = this.getSuffix(this._primaryCurveName, value);
      if (suffix != null) {
         text = text + suffix;
      }

      ValueMarker textMarker = null;

      for (Entry<Unit, XYPlot> e : this._subPlots.entrySet()) {
         XYPlot plot = e.getValue();
         if (Unit.CELSIUS.isCompatible(e.getKey())) {
            textMarker = this.createMarker(value, this._markerColor, text);
            plot.addDomainMarker(textMarker);
         } else {
            plot.addDomainMarker(this.createMarker(value, this._markerColor));
         }
      }

      return textMarker;
   }

   public synchronized void addTimeMarker(long value, String label) {
      this.addTimeMarkerInternal(value, label);
   }

   public void addTimeMarkerAtCenter(long value, String label) {
      ValueMarker marker = this.addTimeMarkerInternal(value, label);
      marker.setLabelOffset(new RectangleInsets(5.0, -5.0, 0.0, 5.0));
      marker.setLabelAnchor(RectangleAnchor.CENTER);
      marker.setLabelTextAnchor(TextAnchor.CENTER_LEFT);
   }

   public void addTimeMarkerAtBottom(long value, String label) {
      ValueMarker marker = this.addTimeMarkerInternal(value, label);
      marker.setLabelOffset(new RectangleInsets(5.0, 5.0, 0.0, 5.0));
      marker.setLabelAnchor(RectangleAnchor.BOTTOM_LEFT);
      marker.setLabelTextAnchor(TextAnchor.BOTTOM_RIGHT);
   }

   public void resetMarkerOffset() {
      this._markerOffset = 15.0;
   }

   public synchronized void dispose() {
      for (CurveModel curveModel : this._curveModels.values()) {
         curveModel.removeListener(this);
      }

      for (XYPlot plot : this._subPlots.values()) {
         for (int i = 0; i < plot.getDatasetCount(); i++) {
            plot.setDataset(i, null);
         }

         ((CombinedDomainXYPlot)this._chart.getPlot()).remove(plot);
      }
   }

   private void updatePredictionAnnotation(CurveModel source) {
      if (source.getName().matches(".+[pP]rediction") && !source.getValues().isEmpty()) {
         this.setPredictionAnnotation(source.getValues().firstKey(), source.getValues().lastKey());
      }
   }

   @Override
   public void notifyCurveChanged(CurveModel source, long time) {
      Platform.runLater(() -> {
         if (this._series.containsKey(source)) {
            this._chart.setNotify(false);

            try {
               XYSeries series = this._series.get(source);
               Double value = source.getValue(time);
               if (value != null) {
                  series.addOrUpdate(time, value);
                  if (value < 0.0 && !source.getUnit().isCompatible(Unit.CELSIUS) && this._subPlots.containsKey(source.getUnit())) {
                     XYPlot plot = this._subPlots.get(source.getUnit());
                     ((NumberAxis)plot.getRangeAxis()).setRangeType(RangeType.FULL);
                  }
               } else if (series.indexOf(time) >= 0) {
                  series.remove(time);
               } else {
                  this.logger.trace("Cannot remove from {} at {}: {}", new Object[]{source.getName(), time, series.getItems()});
               }

               this.updatePredictionAnnotation(source);
            } finally {
               this._chart.setNotify(true);
            }
         }
      });
   }

   @Override
   public void notifyCurveChanged(CurveModel source) {
      Platform.runLater(() -> {
         if (this._series.containsKey(source)) {
            this._chart.setNotify(false);

            try {
               XYSeries series = this._series.get(source);
               series.clear();

               for (Entry<Long, Double> entry : source.getValues().entrySet()) {
                  if (entry.getValue() != null) {
                     series.addOrUpdate(entry.getKey(), entry.getValue());
                  }
               }

               this.updatePredictionAnnotation(source);
            } finally {
               this._chart.setNotify(true);
            }
         }
      });
   }

   protected XYSeries getSeries(CurveModel source) {
      return this._series == null ? null : this._series.get(source);
   }

   private static final class XYSeriesFullRangeCollection extends XYSeriesCollection implements XYRangeInfo {
      private static final long serialVersionUID = -372187342594733629L;

      private XYSeriesFullRangeCollection(XYSeries series) {
         super(series);
      }

      public Range getRangeBounds(List visibleSeriesKeys, Range xRange, boolean includeInterval) {
         double lowerBound = this.getRangeLowerBound(true);
         double upperBound = this.getRangeUpperBound(true);
         return !Double.isNaN(lowerBound) && !Double.isNaN(upperBound) ? new Range(Math.floor(lowerBound), Math.floor(upperBound + 1.0)) : null;
      }
   }
}
