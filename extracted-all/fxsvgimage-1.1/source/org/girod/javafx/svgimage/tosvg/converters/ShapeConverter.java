package org.girod.javafx.svgimage.tosvg.converters;

import java.util.Iterator;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcTo;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Circle;
import javafx.scene.shape.ClosePath;
import javafx.scene.shape.CubicCurve;
import javafx.scene.shape.CubicCurveTo;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.HLineTo;
import javafx.scene.shape.Line;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.PathElement;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.QuadCurve;
import javafx.scene.shape.QuadCurveTo;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.scene.shape.Shape;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.shape.VLineTo;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import org.girod.javafx.svgimage.tosvg.utils.Utilities;
import org.girod.javafx.svgimage.tosvg.xml.XMLNode;

public class ShapeConverter extends AbstractConverter implements DefaultStrokeValues {
   private Shape shape = null;

   public ShapeConverter(ConverterDelegate delegate, Shape shape, XMLNode xmlParent) {
      super(delegate, shape, xmlParent);
      this.shape = shape;
   }

   private boolean hasStrokeProperty() {
      return this.properties.containsKey("-fx-stroke-width")
         || this.properties.containsKey("-fx-stroke-line-cap")
         || this.properties.containsKey("-fx-stroke-line-join")
         || this.properties.containsKey("-fx-stroke-miter-limit")
         || this.properties.containsKey("-fx-stroke-dash-array")
         || this.properties.containsKey("-fx-stroke-dash-offset");
   }

   private boolean hasFontProperty() {
      return this.properties.containsKey("-fx-font-family")
         || this.properties.containsKey("-fx-font-size")
         || this.properties.containsKey("-fx-font-style")
         || this.properties.containsKey("-fx-font-weight");
   }

   private double getArcWidth(Rectangle rec) {
      return this.properties.containsKey("-fx-arc-width") ? (Double)this.properties.get("-fx-arc-width") : rec.getArcWidth();
   }

   private double getArcHeight(Rectangle rec) {
      return this.properties.containsKey("-fx-arc-height") ? (Double)this.properties.get("-fx-arc-height") : rec.getArcWidth();
   }

   private Font getFont(Text text) {
      if (this.properties.containsKey("-fx-font")) {
         Font font = (Font)this.properties.get("-fx-font");
         if (font != Font.getDefault()) {
            return font;
         }
      }

      if (this.hasFontProperty()) {
         Font font = text.getFont();
         double size = font.getSize();
         String family = font.getFamily();
         FontWeight weight = Utilities.getWeight(font);
         FontPosture posture = Utilities.getPosture(font);
         return Font.font(family, weight, posture, size);
      } else {
         return text.getFont();
      }
   }

   protected double getOpacityFill() {
      if (this.properties.containsKey("-fx-opacity")) {
         Number opacity = (Number)this.properties.get("-fx-opacity");
         return opacity.doubleValue();
      } else {
         double opacity = this.shape.getOpacity();
         Paint paint = this.shape.getFill();
         if (paint == null) {
            opacity = 0.0;
         } else if (paint instanceof Color) {
            Color color = (Color)paint;
            double _opacity = color.getOpacity();
            if (_opacity < opacity) {
               opacity = _opacity;
            }
         }

         return opacity;
      }
   }

   protected double getOpacityStroke() {
      if (this.properties.containsKey("-fx-opacity")) {
         Number opacity = (Number)this.properties.get("-fx-opacity");
         return opacity.doubleValue();
      } else {
         double opacity = this.shape.getOpacity();
         Paint paint = this.shape.getStroke();
         if (paint == null) {
            opacity = 0.0;
         } else if (paint instanceof Color) {
            Color color = (Color)paint;
            double _opacity = color.getOpacity();
            if (_opacity < opacity) {
               opacity = _opacity;
            }
         }

         return opacity;
      }
   }

   private Paint getFillPaint() {
      double opacity = this.getOpacityFill();
      if (opacity <= 0.0) {
         return null;
      } else {
         return this.properties.containsKey("-fx-fill") ? (Paint)this.properties.get("-fx-fill") : (Paint)this.shape.fillProperty().get();
      }
   }

   private Paint getStrokePaint() {
      double opacity = this.getOpacityStroke();
      if (opacity <= 0.0) {
         return null;
      } else {
         return this.properties.containsKey("-fx-stroke") ? (Paint)this.properties.get("-fx-stroke") : (Paint)this.shape.strokeProperty().get();
      }
   }

   private void setOpacity(Paint paint, XMLNode node) {
      if (paint instanceof Color) {
         Color color = (Color)paint;
         if (color.getOpacity() != 1.0) {
            node.addAttribute("opacity", color.getOpacity());
         }
      }
   }

   @Override
   public void applyStyle(XMLNode node, String clipID) {
      StringBuilder buf = new StringBuilder();
      if (this.shape instanceof Line) {
         Paint paint = this.getStrokePaint();
         this.setClip(buf, clipID);
         this.addStroke(paint, buf);
         this.setLineStroke(this.shape, buf);
         this.setOpacity(paint, node);
         String style = buf.toString();
         node.addAttribute("style", style);
      } else if (this.shape instanceof Rectangle) {
         Paint paint = this.getStrokePaint();
         this.setClip(buf, clipID);
         this.addStroke(paint, buf);
         this.setLineStroke(this.shape, buf);
         this.setStrokeOpacity(paint, node);
         paint = this.getFillPaint();
         this.addFill(paint, buf);
         this.setFillOpacity(paint, node);
         String style = buf.toString();
         node.addAttribute("style", style);
      } else if (this.shape instanceof Circle) {
         Paint paint = this.getStrokePaint();
         this.setClip(buf, clipID);
         this.addStroke(paint, buf);
         this.setLineStroke(this.shape, buf);
         this.setStrokeOpacity(paint, node);
         paint = this.getFillPaint();
         this.addFill(paint, buf);
         this.setFillOpacity(paint, node);
         String style = buf.toString();
         node.addAttribute("style", style);
      } else if (this.shape instanceof Ellipse) {
         Paint paint = this.getStrokePaint();
         this.setClip(buf, clipID);
         this.addStroke(paint, buf);
         this.setLineStroke(this.shape, buf);
         this.setStrokeOpacity(paint, node);
         paint = this.getFillPaint();
         this.addFill(paint, buf);
         this.setFillOpacity(paint, node);
         String style = buf.toString();
         node.addAttribute("style", style);
      } else if (this.shape instanceof Polyline) {
         Paint paint = this.getStrokePaint();
         this.setClip(buf, clipID);
         this.addStroke(paint, buf);
         this.setLineStroke(this.shape, buf);
         this.setOpacity(paint, node);
         String style = buf.toString();
         node.addAttribute("style", style);
      } else if (this.shape instanceof Polygon) {
         Paint paint = this.getStrokePaint();
         this.setClip(buf, clipID);
         this.addStroke(paint, buf);
         this.setLineStroke(this.shape, buf);
         this.setStrokeOpacity(paint, node);
         paint = this.getFillPaint();
         this.addFill(paint, buf);
         this.setFillOpacity(paint, node);
         String style = buf.toString();
         node.addAttribute("style", style);
      } else if (this.shape instanceof SVGPath) {
         Paint paint = this.getStrokePaint();
         this.setClip(buf, clipID);
         this.addStroke(paint, buf);
         this.setLineStroke(this.shape, buf);
         this.setStrokeOpacity(paint, node);
         paint = this.getFillPaint();
         this.addFill(paint, buf);
         this.setFillOpacity(paint, node);
         String style = buf.toString();
         node.addAttribute("style", style);
      } else if (this.shape instanceof Path) {
         Paint paint = this.getStrokePaint();
         this.setClip(buf, clipID);
         this.addStroke(paint, buf);
         this.setLineStroke(this.shape, buf);
         this.setStrokeOpacity(paint, node);
         paint = this.getFillPaint();
         this.addFill(paint, buf);
         this.setFillOpacity(paint, node);
         String style = buf.toString();
         node.addAttribute("style", style);
      } else if (this.shape instanceof Arc) {
         Paint paint = this.getStrokePaint();
         this.setClip(buf, clipID);
         this.addStroke(paint, buf);
         this.setLineStroke(this.shape, buf);
         this.setStrokeOpacity(paint, node);
         paint = this.getFillPaint();
         this.addFill(paint, buf);
         this.setFillOpacity(paint, node);
         String style = buf.toString();
         node.addAttribute("style", style);
      } else if (this.shape instanceof Text) {
         Paint paint = this.getFillPaint();
         this.setClip(buf, clipID);
         this.addFill(paint, buf);
         this.setOpacity(paint, node);
         Text text = (Text)this.shape;
         this.addFontStyle(text, buf);
         String style = buf.toString();
         node.addAttribute("style", style);
      }
   }

   private void addFontStyle(Text text, StringBuilder buf) {
      Font font = text.getFont();
      FontWeight weight = Utilities.getWeight(font);
      FontPosture posture = Utilities.getPosture(font);
      if (null == weight) {
         buf.append("font-weight:normal;");
      } else {
         switch (weight) {
            case BOLD:
               buf.append("font-weight:bold;");
               break;
            case EXTRA_BOLD:
               buf.append("font-weight:bold;");
               break;
            case LIGHT:
               buf.append("font-weight:lighter;");
               break;
            default:
               buf.append("font-weight:normal;");
         }
      }

      if (posture == FontPosture.ITALIC) {
         buf.append("font-style:italic;");
      } else {
         buf.append("font-style:normal;");
      }

      double size = font.getSize();
      buf.append("font-size:").append(size).append(";");
      String family = font.getFamily();
      if (family.equals("System")) {
         family = "Arial";
      }

      buf.append("font-family:").append(family).append(";");
   }

   private void appendPoints(Polygon polygon, XMLNode node) {
      StringBuilder buf = new StringBuilder();
      ObservableList<Double> points = polygon.getPoints();

      for (int i = 0; i < points.size(); i += 2) {
         double x = (Double)points.get(i);
         double y = (Double)points.get(i + 1);
         buf.append(x).append(",").append(y);
         if (i < points.size() - 1) {
            buf.append(" ");
         }
      }

      node.addAttribute("points", buf.toString());
   }

   private void appendPoints(Polyline polyline, XMLNode node) {
      StringBuilder buf = new StringBuilder();
      ObservableList<Double> points = polyline.getPoints();

      for (int i = 0; i < points.size(); i += 2) {
         double x = (Double)points.get(i);
         double y = (Double)points.get(i + 1);
         buf.append(x).append(",").append(y);
         if (i < points.size() - 1) {
            buf.append(" ");
         }
      }

      node.addAttribute("points", buf.toString());
   }

   private void setLineStroke(Shape shape, StringBuilder buf) {
      StrokeLineJoin lineJoin = shape.getStrokeLineJoin();
      if (lineJoin != DEFAULT_STROKE_LINE_JOIN) {
         if (lineJoin == StrokeLineJoin.BEVEL) {
            buf.append("stroke-linejoin: bevel;");
         } else if (lineJoin == StrokeLineJoin.MITER) {
            buf.append("stroke-linejoin: miter;");
         } else if (lineJoin == StrokeLineJoin.ROUND) {
            buf.append("stroke-linejoin: round;");
         }
      }

      StrokeLineCap lineCap = shape.getStrokeLineCap();
      if (lineCap != DEFAULT_STROKE_LINE_CAP) {
         if (lineCap == StrokeLineCap.BUTT) {
            buf.append("stroke-linecap: butt;");
         } else if (lineCap == StrokeLineCap.ROUND) {
            buf.append("stroke-linecap: round;");
         } else if (lineCap == StrokeLineCap.SQUARE) {
            buf.append("stroke-linecap: square;");
         }
      }

      double miterLimit = shape.getStrokeMiterLimit();
      if (miterLimit != 10.0) {
         buf.append("stroke-miterlimit:").append(miterLimit).append(";");
      }

      double dashoffset = shape.getStrokeDashOffset();
      if (dashoffset != 0.0) {
         buf.append("stroke-dashoffset:").append(dashoffset).append(";");
      }

      double width = shape.getStrokeWidth();
      if (width != 1.0) {
         buf.append("stroke-width:").append(width).append(";");
      }

      ObservableList<Double> array = shape.getStrokeDashArray();
      if (array.size() > 0) {
         buf.append("stroke-dasharray:");
         Iterator<Double> it = array.iterator();

         while (it.hasNext()) {
            double value = it.next();
            buf.append(value);
            if (it.hasNext()) {
               buf.append(",");
            }
         }

         buf.append(";");
      }
   }

   private void setPath(Path path, XMLNode node) {
      StringBuilder buf = new StringBuilder();
      ObservableList<PathElement> elements = path.getElements();
      Iterator<PathElement> it = elements.iterator();

      while (it.hasNext()) {
         PathElement element = it.next();
         if (element instanceof MoveTo) {
            MoveTo moveTo = (MoveTo)element;
            buf.append("M").append(moveTo.getX()).append(",").append(moveTo.getY());
         } else if (element instanceof ClosePath) {
            buf.append("Z");
         } else if (element instanceof HLineTo) {
            HLineTo lineTo = (HLineTo)element;
            buf.append("H").append(lineTo.getX());
         } else if (element instanceof VLineTo) {
            VLineTo lineTo = (VLineTo)element;
            buf.append("V").append(lineTo.getY());
         } else if (element instanceof LineTo) {
            LineTo lineTo = (LineTo)element;
            buf.append("L").append(lineTo.getX()).append(",").append(lineTo.getY());
         } else if (element instanceof ArcTo) {
            ArcTo arcTo = (ArcTo)element;
            buf.append("A").append(arcTo.getRadiusX()).append(",").append(arcTo.getRadiusY());
            buf.append(" ").append(arcTo.getXAxisRotation());
            buf.append(" ").append(arcTo.isLargeArcFlag() ? "1" : "0").append(",").append(arcTo.isSweepFlag() ? "1" : "0");
            buf.append(" ").append(arcTo.getX()).append(",").append(arcTo.getY());
         } else if (element instanceof QuadCurveTo) {
            QuadCurveTo curveTo = (QuadCurveTo)element;
            buf.append("Q").append(curveTo.getControlX()).append(",").append(curveTo.getControlY());
            buf.append(" ").append(curveTo.getX()).append(",").append(curveTo.getY());
         } else if (element instanceof CubicCurveTo) {
            CubicCurveTo curveTo = (CubicCurveTo)element;
            buf.append("C").append(curveTo.getControlX1()).append(",").append(curveTo.getControlY1());
            buf.append(" ").append(curveTo.getControlX2()).append(",").append(curveTo.getControlY2());
            buf.append(" ").append(curveTo.getX()).append(",").append(curveTo.getY());
         }

         if (it.hasNext()) {
            buf.append(" ");
         }
      }

      node.addAttribute("d", buf.toString());
   }

   @Override
   public XMLNode convert() {
      XMLNode node = null;
      if (this.shape instanceof Line) {
         Line line = (Line)this.shape;
         node = new XMLNode("line");
         node.addAttribute("x1", line.getStartX());
         node.addAttribute("y1", line.getStartY());
         node.addAttribute("x2", line.getEndX());
         node.addAttribute("y2", line.getEndY());
         this.xmlParent.addChild(node);
      } else if (this.shape instanceof Text) {
         Text text = (Text)this.shape;
         String str = text.getText();
         node = new XMLNode("text");
         node.addAttribute("xml:space", "preserve");
         node.addAttribute("x", text.getX());
         node.addAttribute("y", text.getY());
         node.setCDATA(str);
         this.xmlParent.addChild(node);
      } else if (this.shape instanceof Polygon) {
         Polygon polygon = (Polygon)this.shape;
         node = new XMLNode("polygon");
         this.appendPoints(polygon, node);
         this.xmlParent.addChild(node);
      } else if (this.shape instanceof Polyline) {
         Polyline polyline = (Polyline)this.shape;
         node = new XMLNode("polyline");
         this.appendPoints(polyline, node);
         this.xmlParent.addChild(node);
      } else if (this.shape instanceof SVGPath) {
         SVGPath path = (SVGPath)this.shape;
         node = new XMLNode("path");
         node.addAttribute("d", path.getContent());
         this.xmlParent.addChild(node);
      } else if (this.shape instanceof Path) {
         Path path = (Path)this.shape;
         node = new XMLNode("path");
         this.setPath(path, node);
         this.xmlParent.addChild(node);
      } else if (this.shape instanceof Circle) {
         Circle circle = (Circle)this.shape;
         node = new XMLNode("circle");
         node.addAttribute("cx", circle.getCenterX());
         node.addAttribute("cy", circle.getCenterY());
         node.addAttribute("r", circle.getRadius());
         this.xmlParent.addChild(node);
      } else if (this.shape instanceof Ellipse) {
         Ellipse ellipse = (Ellipse)this.shape;
         node = new XMLNode("ellipse");
         node.addAttribute("cx", ellipse.getCenterX());
         node.addAttribute("cy", ellipse.getCenterY());
         node.addAttribute("rx", ellipse.getRadiusX());
         node.addAttribute("ry", ellipse.getRadiusY());
         this.xmlParent.addChild(node);
      } else if (this.shape instanceof Arc) {
         Arc arc = (Arc)this.shape;
         Path path = new Path();
         double startX = arc.getCenterX() - arc.getRadiusX();
         double startY = arc.getCenterY() - arc.getRadiusY();
         MoveTo moveTo = new MoveTo();
         moveTo.setX(startX);
         moveTo.setY(startY);
         path.getElements().add(moveTo);
         double endX = arc.getCenterX() + arc.getRadiusX() * Math.cos(Math.toRadians(arc.getStartAngle() + arc.getLength()));
         double endY = arc.getCenterY() + arc.getRadiusY() * Math.sin(Math.toRadians(arc.getStartAngle() + arc.getLength()));
         ArcTo arcTo = new ArcTo();
         arcTo.setX(endX);
         arcTo.setY(endY);
         path.getElements().add(arcTo);
         if (arc.getType() == ArcType.CHORD) {
            path.getElements().add(new ClosePath());
         }

         node = new XMLNode("path");
         this.setPath(path, node);
         this.xmlParent.addChild(node);
      } else if (this.shape instanceof Rectangle) {
         Rectangle rec = (Rectangle)this.shape;
         double arcWidth = this.getArcWidth(rec);
         double arcHeight = this.getArcHeight(rec);
         node = new XMLNode("rect");
         node.addAttribute("width", rec.getWidth());
         node.addAttribute("height", rec.getHeight());
         node.addAttribute("x", rec.getX());
         node.addAttribute("y", rec.getY());
         if (arcWidth != 0.0) {
            node.addAttribute("rx", arcWidth / 2.0);
         }

         if (arcHeight != 0.0) {
            node.addAttribute("ry", arcHeight / 2.0);
         }

         this.xmlParent.addChild(node);
      } else if (this.shape instanceof QuadCurve) {
         QuadCurve curve = (QuadCurve)this.shape;
         Path path = new Path();
         MoveTo moveTo = new MoveTo();
         moveTo.setX(curve.getStartX());
         moveTo.setY(curve.getStartY());
         QuadCurveTo curveTo = new QuadCurveTo();
         curveTo.setX(curve.getEndX());
         curveTo.setY(curve.getEndY());
         curveTo.setControlX(curve.getControlX());
         curveTo.setControlY(curve.getControlY());
         path.getElements().add(moveTo);
         path.getElements().add(curveTo);
         node = new XMLNode("path");
         this.setPath(path, node);
         this.xmlParent.addChild(node);
      } else if (this.shape instanceof CubicCurve) {
         CubicCurve curve = (CubicCurve)this.shape;
         Path path = new Path();
         MoveTo moveTo = new MoveTo();
         moveTo.setX(curve.getStartX());
         moveTo.setY(curve.getStartY());
         CubicCurveTo curveTo = new CubicCurveTo();
         curveTo.setX(curve.getEndX());
         curveTo.setY(curve.getEndY());
         curveTo.setControlX1(curve.getControlX1());
         curveTo.setControlY1(curve.getControlY1());
         curveTo.setControlX2(curve.getControlX2());
         curveTo.setControlY2(curve.getControlY2());
         path.getElements().add(moveTo);
         path.getElements().add(curveTo);
         node = new XMLNode("path");
         this.setPath(path, node);
         this.xmlParent.addChild(node);
      }

      return node;
   }
}
