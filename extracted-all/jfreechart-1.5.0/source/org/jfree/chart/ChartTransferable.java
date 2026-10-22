package org.jfree.chart;

import java.awt.Graphics2D;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Double;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class ChartTransferable implements Transferable {
   final DataFlavor imageFlavor = new DataFlavor("image/x-java-image; class=java.awt.Image", "Image");
   private JFreeChart chart;
   private int width;
   private int height;
   private int minDrawWidth;
   private int minDrawHeight;
   private int maxDrawWidth;
   private int maxDrawHeight;

   public ChartTransferable(JFreeChart chart, int width, int height) {
      this(chart, width, height, true);
   }

   public ChartTransferable(JFreeChart chart, int width, int height, boolean cloneData) {
      this(chart, width, height, 0, 0, Integer.MAX_VALUE, Integer.MAX_VALUE, true);
   }

   public ChartTransferable(JFreeChart chart, int width, int height, int minDrawW, int minDrawH, int maxDrawW, int maxDrawH, boolean cloneData) {
      try {
         this.chart = (JFreeChart)chart.clone();
      } catch (CloneNotSupportedException var10) {
         this.chart = chart;
      }

      this.width = width;
      this.height = height;
      this.minDrawWidth = minDrawW;
      this.minDrawHeight = minDrawH;
      this.maxDrawWidth = maxDrawW;
      this.maxDrawHeight = maxDrawH;
   }

   @Override
   public DataFlavor[] getTransferDataFlavors() {
      return new DataFlavor[]{this.imageFlavor};
   }

   @Override
   public boolean isDataFlavorSupported(DataFlavor flavor) {
      return this.imageFlavor.equals(flavor);
   }

   @Override
   public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
      if (this.imageFlavor.equals(flavor)) {
         return this.createBufferedImage(this.chart, this.width, this.height, this.minDrawWidth, this.minDrawHeight, this.maxDrawWidth, this.maxDrawHeight);
      } else {
         throw new UnsupportedFlavorException(flavor);
      }
   }

   private BufferedImage createBufferedImage(JFreeChart chart, int w, int h, int minDrawW, int minDrawH, int maxDrawW, int maxDrawH) {
      BufferedImage image = new BufferedImage(w, h, 2);
      Graphics2D g2 = image.createGraphics();
      boolean scale = false;
      double drawWidth = (double)w;
      double drawHeight = (double)h;
      double scaleX = 1.0;
      double scaleY = 1.0;
      if (drawWidth < (double)minDrawW) {
         scaleX = drawWidth / (double)minDrawW;
         drawWidth = (double)minDrawW;
         scale = true;
      } else if (drawWidth > (double)maxDrawW) {
         scaleX = drawWidth / (double)maxDrawW;
         drawWidth = (double)maxDrawW;
         scale = true;
      }

      if (drawHeight < (double)minDrawH) {
         scaleY = drawHeight / (double)minDrawH;
         drawHeight = (double)minDrawH;
         scale = true;
      } else if (drawHeight > (double)maxDrawH) {
         scaleY = drawHeight / (double)maxDrawH;
         drawHeight = (double)maxDrawH;
         scale = true;
      }

      Rectangle2D chartArea = new Double(0.0, 0.0, drawWidth, drawHeight);
      if (scale) {
         AffineTransform st = AffineTransform.getScaleInstance(scaleX, scaleY);
         g2.transform(st);
      }

      chart.draw(g2, chartArea, null, null);
      g2.dispose();
      return image;
   }
}
