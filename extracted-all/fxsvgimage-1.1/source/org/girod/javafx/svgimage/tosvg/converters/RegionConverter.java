package org.girod.javafx.svgimage.tosvg.converters;

import java.net.URL;
import java.util.List;
import javafx.geometry.Bounds;
import javafx.scene.Parent;
import javafx.scene.layout.Background;
import javafx.scene.layout.Border;
import javafx.scene.layout.Region;
import javafx.scene.paint.Paint;
import javafx.scene.shape.SVGPath;
import javafx.scene.transform.Scale;
import org.girod.javafx.svgimage.tosvg.utils.Utilities;
import org.girod.javafx.svgimage.tosvg.wrappers.BackgroundWrapper;
import org.girod.javafx.svgimage.tosvg.wrappers.BorderWrapper;
import org.girod.javafx.svgimage.tosvg.xml.XMLNode;

public class RegionConverter extends AbstractConverter {
   protected Region region = null;

   public RegionConverter(ConverterDelegate delegate, Region region, XMLNode xmlParent) {
      super(delegate, region, xmlParent);
      this.region = region;
   }

   @Override
   public Parent getParent() {
      return this.region;
   }

   @Override
   public void applyStyle(XMLNode node, String clipID) {
      if (clipID != null) {
         StringBuilder buf = new StringBuilder();
         String style = buf.toString();
         node.addAttribute("style", style);
      }
   }

   private SVGPath getSVGPath() {
      return this.allProperties.containsKey("-fx-shape") ? (SVGPath)this.allProperties.get("-fx-shape") : null;
   }

   private List<BackgroundWrapper> getRegionBackground() {
      double opacity = this.getOpacity();
      if (this.allProperties.containsKey("-fx-region-background")) {
         Background background = (Background)this.allProperties.get("-fx-region-background");
         return Utilities.getPaintList(this.region, background, this, opacity);
      } else {
         return this.region.getBackground() != null ? Utilities.getPaintList(this.region, this.region.getBackground(), this, opacity) : null;
      }
   }

   private List<BorderWrapper> getRegionBorder() {
      if (this.allProperties.containsKey("-fx-region-border")) {
         Border border = (Border)this.allProperties.get("-fx-region-border");
         return Utilities.getPaintList(this.region, border);
      } else {
         return this.region.getBorder() != null ? Utilities.getPaintList(this.region, this.region.getBorder()) : null;
      }
   }

   private boolean isScaleShape() {
      return this.allProperties.containsKey("-fx-scale-shape") ? (Boolean)this.allProperties.get("-fx-scale-shape") : false;
   }

   private Paint getBackground() {
      double opacity = this.getOpacity();
      if (this.allProperties.containsKey("-fx-background-color")) {
         return (Paint)this.allProperties.get("-fx-background-color");
      } else if (this.allProperties.containsKey("-fx-background-image")) {
         URL url = (URL)this.allProperties.get("-fx-background-image");
         return null;
      } else {
         return null;
      }
   }

   @Override
   public XMLNode convert() {
      SVGPath path = this.getSVGPath();
      boolean isScaled = this.isScaleShape();
      XMLNode node = new XMLNode("g");
      this.convertBackground(node, path, isScaled);
      this.convertBorder(node, path, isScaled);
      this.xmlParent.addChild(node);
      return node;
   }

   private Scale getScale(SVGPath path, double width, double height) {
      Bounds rec = path.getBoundsInLocal();
      double scaleX = width / rec.getWidth();
      double scaleY = height / rec.getHeight();
      return new Scale(scaleX, scaleY);
   }

   private void convertBackground(XMLNode node, SVGPath path, boolean isScaled) {
      List<BackgroundWrapper> wrappers = this.getRegionBackground();
      if (wrappers != null && !wrappers.isEmpty()) {
         for (BackgroundWrapper wrapper : wrappers) {
            if (wrapper.getPaint() != null) {
               int width = (int)wrapper.getWidth();
               int height = (int)wrapper.getHeight();
               if (width > 0 && height > 0) {
                  if (path == null) {
                     double x = 0.0;
                     double y = 0.0;
                     if (wrapper.isTexture()) {
                        x = wrapper.getX();
                        y = wrapper.getY();
                     }

                     this.fillRect(node, wrapper, x, y, (double)width, (double)height);
                  } else if (isScaled) {
                     Scale scale = this.getScale(path, (double)width, (double)height);
                     this.fillPath(node, path, wrapper, scale);
                  }
               }
            }
         }
      }

      Paint paint = this.getBackground();
      if (paint != null) {
         this.fillRect(node, null, this.region.getLayoutX(), this.region.getLayoutY(), this.region.getPrefWidth(), this.region.getPrefHeight());
      }
   }

   private void fillPath(XMLNode gNode, SVGPath path, BackgroundWrapper wrapper, Scale scale) {
      if (!wrapper.isTexture()) {
         XMLNode node = new XMLNode("path");
         double x = path.getLayoutX();
         double y = path.getLayoutY();
         node.addAttribute("x", x);
         node.addAttribute("y", y);
         node.addAttribute("d", path.getContent());
         StringBuilder buf = new StringBuilder();
         Paint paint = wrapper.getPaint();
         this.addFill(paint, buf);
         this.setFillOpacity(paint, node);
         String style = buf.toString();
         node.addAttribute("style", style);
         if (scale != null) {
            node.addAttribute("transform", Utilities.getScale(scale));
         }

         gNode.addChild(node);
      }
   }

   private void fillRect(XMLNode gNode, BackgroundWrapper wrapper, double x, double y, double width, double height) {
      if (!wrapper.isTexture()) {
         double arcWidth = (double)wrapper.getMeanRadiiWidth();
         double arcHeight = (double)wrapper.getMeanRadiiHeight();
         XMLNode node = new XMLNode("rect");
         node.addAttribute("width", width);
         node.addAttribute("height", height);
         node.addAttribute("x", x);
         node.addAttribute("y", y);
         if (arcWidth != 0.0) {
            node.addAttribute("rx", arcWidth / 2.0);
         }

         if (arcHeight != 0.0) {
            node.addAttribute("ry", arcHeight / 2.0);
         }

         StringBuilder buf = new StringBuilder();
         Paint paint = wrapper.getPaint();
         this.addFill(paint, buf);
         this.setFillOpacity(paint, node);
         String style = buf.toString();
         node.addAttribute("style", style);
         gNode.addChild(node);
      }
   }

   private void convertBorder(XMLNode node, SVGPath path, boolean isScaled) {
      List<BorderWrapper> bWrappers = this.getRegionBorder();
      if (bWrappers != null && !bWrappers.isEmpty()) {
         for (BorderWrapper wrapper : bWrappers) {
            if (wrapper.getPaint() != null) {
               int width = (int)this.region.getWidth();
               int height = (int)this.region.getHeight();
               if (width > 0 && height > 0) {
                  if (path == null) {
                     double x = 0.0;
                     double y = 0.0;
                     this.strokeRect(node, wrapper, x, y, (double)width, (double)height);
                  } else if (isScaled) {
                     Scale scale = this.getScale(path, (double)width, (double)height);
                     this.strokePath(node, path, wrapper, scale);
                  } else {
                     this.strokePath(node, path, wrapper, null);
                  }
               }
            }
         }
      }
   }

   private void strokeRect(XMLNode gNode, BorderWrapper wrapper, double x, double y, double width, double height) {
      double arcWidth = (double)wrapper.getMeanRadiiWidth();
      double arcHeight = (double)wrapper.getMeanRadiiHeight();
      XMLNode node = new XMLNode("rect");
      node.addAttribute("width", width);
      node.addAttribute("height", height);
      node.addAttribute("x", x);
      node.addAttribute("y", y);
      if (arcWidth != 0.0) {
         node.addAttribute("rx", arcWidth / 2.0);
      }

      if (arcHeight != 0.0) {
         node.addAttribute("ry", arcHeight / 2.0);
      }

      StringBuilder buf = new StringBuilder();
      Paint paint = wrapper.getPaint();
      this.addStroke(paint, buf);
      this.setStrokeOpacity(paint, node);
      String style = buf.toString();
      node.addAttribute("style", style);
      gNode.addChild(node);
   }

   private void strokePath(XMLNode gNode, SVGPath path, BorderWrapper wrapper, Scale scale) {
      XMLNode node = new XMLNode("path");
      node.addAttribute("d", path.getContent());
      StringBuilder buf = new StringBuilder();
      Paint paint = wrapper.getPaint();
      this.addStroke(paint, buf);
      this.setStrokeOpacity(paint, node);
      String style = buf.toString();
      node.addAttribute("style", style);
      if (scale != null) {
         node.addAttribute("transform", Utilities.getScale(scale));
      }

      gNode.addChild(node);
   }
}
