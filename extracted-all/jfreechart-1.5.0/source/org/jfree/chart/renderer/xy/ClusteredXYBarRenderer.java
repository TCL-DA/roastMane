package org.jfree.chart.renderer.xy;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.labels.XYItemLabelGenerator;
import org.jfree.chart.plot.CrosshairState;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.ui.RectangleEdge;
import org.jfree.chart.util.Args;
import org.jfree.chart.util.PublicCloneable;
import org.jfree.data.Range;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.data.xy.XYDataset;

public class ClusteredXYBarRenderer extends XYBarRenderer implements Cloneable, PublicCloneable, Serializable {
   private static final long serialVersionUID = 5864462149177133147L;
   private boolean centerBarAtStartValue;

   public ClusteredXYBarRenderer() {
      this(0.0, false);
   }

   public ClusteredXYBarRenderer(double margin, boolean centerBarAtStartValue) {
      super(margin);
      this.centerBarAtStartValue = centerBarAtStartValue;
   }

   @Override
   public int getPassCount() {
      return 2;
   }

   @Override
   public Range findDomainBounds(XYDataset dataset) {
      if (dataset == null) {
         return null;
      } else {
         return this.centerBarAtStartValue ? this.findDomainBoundsWithOffset((IntervalXYDataset)dataset) : super.findDomainBounds(dataset);
      }
   }

   protected Range findDomainBoundsWithOffset(IntervalXYDataset dataset) {
      Args.nullNotPermitted(dataset, "dataset");
      double minimum = Double.POSITIVE_INFINITY;
      double maximum = Double.NEGATIVE_INFINITY;
      int seriesCount = dataset.getSeriesCount();

      for (int series = 0; series < seriesCount; series++) {
         int itemCount = dataset.getItemCount(series);

         for (int item = 0; item < itemCount; item++) {
            double lvalue = dataset.getStartXValue(series, item);
            double uvalue = dataset.getEndXValue(series, item);
            double offset = (uvalue - lvalue) / 2.0;
            lvalue -= offset;
            uvalue -= offset;
            minimum = Math.min(minimum, lvalue);
            maximum = Math.max(maximum, uvalue);
         }
      }

      return minimum > maximum ? null : new Range(minimum, maximum);
   }

   @Override
   public void drawItem(
      Graphics2D g2,
      XYItemRendererState state,
      Rectangle2D dataArea,
      PlotRenderingInfo info,
      XYPlot plot,
      ValueAxis domainAxis,
      ValueAxis rangeAxis,
      XYDataset dataset,
      int series,
      int item,
      CrosshairState crosshairState,
      int pass
   ) {
      IntervalXYDataset intervalDataset = (IntervalXYDataset)dataset;
      double y0;
      double y1;
      if (this.getUseYInterval()) {
         y0 = intervalDataset.getStartYValue(series, item);
         y1 = intervalDataset.getEndYValue(series, item);
      } else {
         y0 = this.getBase();
         y1 = intervalDataset.getYValue(series, item);
      }

      if (!Double.isNaN(y0) && !Double.isNaN(y1)) {
         double yy0 = rangeAxis.valueToJava2D(y0, dataArea, plot.getRangeAxisEdge());
         double yy1 = rangeAxis.valueToJava2D(y1, dataArea, plot.getRangeAxisEdge());
         RectangleEdge xAxisLocation = plot.getDomainAxisEdge();
         double x0 = intervalDataset.getStartXValue(series, item);
         double xx0 = domainAxis.valueToJava2D(x0, dataArea, xAxisLocation);
         double x1 = intervalDataset.getEndXValue(series, item);
         double xx1 = domainAxis.valueToJava2D(x1, dataArea, xAxisLocation);
         double intervalW = xx1 - xx0;
         double baseX = xx0;
         if (this.centerBarAtStartValue) {
            baseX = xx0 - intervalW / 2.0;
         }

         double m = this.getMargin();
         if (m > 0.0) {
            double cut = intervalW * this.getMargin();
            intervalW -= cut;
            baseX += cut / 2.0;
         }

         double intervalH = Math.abs(yy0 - yy1);
         PlotOrientation orientation = plot.getOrientation();
         int numSeries = dataset.getSeriesCount();
         double seriesBarWidth = intervalW / (double)numSeries;
         Rectangle2D bar = null;
         java.awt.geom.Rectangle2D.Double var57;
         if (orientation == PlotOrientation.HORIZONTAL) {
            double barY0 = baseX + seriesBarWidth * (double)series;
            double barY1 = barY0 + seriesBarWidth;
            double rx = Math.min(yy0, yy1);
            double ry = Math.min(barY0, barY1);
            double rh = Math.abs(barY1 - barY0);
            var57 = new java.awt.geom.Rectangle2D.Double(rx, ry, intervalH, rh);
         } else {
            if (orientation != PlotOrientation.VERTICAL) {
               throw new IllegalStateException();
            }

            double barX0 = baseX + seriesBarWidth * (double)series;
            double barX1 = barX0 + seriesBarWidth;
            double rx = Math.min(barX0, barX1);
            double rw = Math.abs(barX1 - barX0);
            double ry = Math.min(yy0, yy1);
            var57 = new java.awt.geom.Rectangle2D.Double(rx, ry, rw, intervalH);
         }

         boolean positive = y1 > 0.0;
         boolean inverted = rangeAxis.isInverted();
         RectangleEdge barBase;
         if (orientation == PlotOrientation.HORIZONTAL) {
            if ((!positive || !inverted) && (positive || inverted)) {
               barBase = RectangleEdge.LEFT;
            } else {
               barBase = RectangleEdge.RIGHT;
            }
         } else if ((!positive || inverted) && (positive || !inverted)) {
            barBase = RectangleEdge.TOP;
         } else {
            barBase = RectangleEdge.BOTTOM;
         }

         if (pass == 0 && this.getShadowsVisible()) {
            this.getBarPainter().paintBarShadow(g2, this, series, item, var57, barBase, !this.getUseYInterval());
         }

         if (pass == 1) {
            this.getBarPainter().paintBar(g2, this, series, item, var57, barBase);
            if (this.isItemLabelVisible(series, item)) {
               XYItemLabelGenerator generator = this.getItemLabelGenerator(series, item);
               this.drawItemLabel(g2, dataset, series, item, plot, generator, var57, y1 < 0.0);
            }

            if (info != null) {
               EntityCollection entities = info.getOwner().getEntityCollection();
               if (entities != null) {
                  this.addEntity(entities, var57, dataset, series, item, var57.getCenterX(), var57.getCenterY());
               }
            }
         }
      }
   }

   @Override
   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      } else if (!(obj instanceof ClusteredXYBarRenderer)) {
         return false;
      } else {
         ClusteredXYBarRenderer that = (ClusteredXYBarRenderer)obj;
         return this.centerBarAtStartValue != that.centerBarAtStartValue ? false : super.equals(obj);
      }
   }

   @Override
   public Object clone() throws CloneNotSupportedException {
      return super.clone();
   }
}
