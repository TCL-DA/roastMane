package org.jfree.chart.entity;

import java.awt.Shape;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import org.jfree.chart.HashUtils;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.util.Args;
import org.jfree.chart.util.ObjectUtils;
import org.jfree.chart.util.SerialUtils;

public class PlotEntity extends ChartEntity {
   private static final long serialVersionUID = -4445994133561919083L;
   private Plot plot;

   public PlotEntity(Shape area, Plot plot) {
      this(area, plot, null);
   }

   public PlotEntity(Shape area, Plot plot, String toolTipText) {
      this(area, plot, toolTipText, null);
   }

   public PlotEntity(Shape area, Plot plot, String toolTipText, String urlText) {
      super(area, toolTipText, urlText);
      Args.nullNotPermitted(plot, "plot");
      this.plot = plot;
   }

   public Plot getPlot() {
      return this.plot;
   }

   @Override
   public String toString() {
      StringBuilder sb = new StringBuilder("PlotEntity: ");
      sb.append("tooltip = ");
      sb.append(this.getToolTipText());
      return sb.toString();
   }

   @Override
   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      } else if (!(obj instanceof PlotEntity)) {
         return false;
      } else {
         PlotEntity that = (PlotEntity)obj;
         if (!this.getArea().equals(that.getArea())) {
            return false;
         } else if (!ObjectUtils.equal(this.getToolTipText(), that.getToolTipText())) {
            return false;
         } else {
            return !ObjectUtils.equal(this.getURLText(), that.getURLText()) ? false : this.plot.equals(that.plot);
         }
      }
   }

   @Override
   public int hashCode() {
      int result = 39;
      result = HashUtils.hashCode(result, this.getToolTipText());
      return HashUtils.hashCode(result, this.getURLText());
   }

   @Override
   public Object clone() throws CloneNotSupportedException {
      return super.clone();
   }

   private void writeObject(ObjectOutputStream stream) throws IOException {
      stream.defaultWriteObject();
      SerialUtils.writeShape(this.getArea(), stream);
   }

   private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
      stream.defaultReadObject();
      this.setArea(SerialUtils.readShape(stream));
   }
}
