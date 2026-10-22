package org.jfree.fx;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.LinearGradientPaint;
import java.awt.MultipleGradientPaint;
import java.awt.Paint;
import java.awt.RadialGradientPaint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.RenderingHints.Key;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.geom.Rectangle2D.Double;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ColorModel;
import java.awt.image.ImageObserver;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.awt.image.renderable.RenderableImage;
import java.text.AttributedCharacterIterator;
import java.text.AttributedCharacterIterator.Attribute;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.BlendMode;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;

public class FXGraphics2D extends Graphics2D {
   private final GraphicsContext gc;
   private final RenderingHints hints;
   private Shape clip;
   private Paint paint = Color.BLACK;
   private Color color = Color.BLACK;
   private Composite composite = AlphaComposite.getInstance(3, 1.0F);
   private Stroke stroke = new BasicStroke(1.0F);
   private double zeroStrokeWidth;
   private Font font = new Font("SansSerif", 0, 12);
   private final FontRenderContext fontRenderContext = new FontRenderContext(null, false, true);
   private AffineTransform transform = new AffineTransform();
   private Color background = Color.BLACK;
   private boolean stateSaved = false;
   private Stroke savedStroke;
   private Paint savedPaint;
   private Color savedColor;
   private Font savedFont;
   private AffineTransform savedTransform;
   private Line2D line;
   Rectangle2D rect;
   private RoundRectangle2D roundRect;
   private Ellipse2D oval;
   private Arc2D arc;
   private BufferedImage fmImage;
   private Graphics2D fmImageG2;
   private FXFontMetrics fxFontMetrics;
   private GraphicsConfiguration deviceConfiguration;
   private final double[] coords = new double[6];

   private static void nullNotPermitted(Object arg, String name) {
      if (arg == null) {
         throw new IllegalArgumentException("Null '" + name + "' argument.");
      }
   }

   public FXGraphics2D(GraphicsContext gc) {
      nullNotPermitted(gc, "gc");
      this.gc = gc;
      this.zeroStrokeWidth = 0.5;
      this.hints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_DEFAULT);
      this.hints.put(FXHints.KEY_USE_FX_FONT_METRICS, true);
   }

   public double getZeroStrokeWidth() {
      return this.zeroStrokeWidth;
   }

   public void setZeroStrokeWidth(double width) {
      if (width < 0.0) {
         throw new IllegalArgumentException("Width cannot be negative.");
      } else {
         this.zeroStrokeWidth = width;
      }
   }

   @Override
   public GraphicsConfiguration getDeviceConfiguration() {
      if (this.deviceConfiguration == null) {
         int width = (int)this.gc.getCanvas().getWidth();
         int height = (int)this.gc.getCanvas().getHeight();
         this.deviceConfiguration = new FXGraphicsConfiguration(width, height);
      }

      return this.deviceConfiguration;
   }

   @Override
   public Graphics create() {
      FXGraphics2D copy = new FXGraphics2D(this.gc);
      copy.setRenderingHints(this.getRenderingHints());
      copy.setClip(this.getClip());
      copy.setPaint(this.getPaint());
      copy.setColor(this.getColor());
      copy.setComposite(this.getComposite());
      copy.setStroke(this.getStroke());
      copy.setFont(this.getFont());
      copy.setTransform(this.getTransform());
      copy.setBackground(this.getBackground());
      return copy;
   }

   @Override
   public Paint getPaint() {
      return this.paint;
   }

   @Override
   public void setPaint(Paint paint) {
      if (paint != null) {
         if (!paintsAreEqual(paint, this.paint)) {
            this.applyPaint(paint);
         }
      }
   }

   private void applyPaint(Paint paint) {
      this.paint = paint;
      if (paint instanceof Color) {
         this.setColor((Color)paint);
      } else if (paint instanceof GradientPaint) {
         GradientPaint gp = (GradientPaint)paint;
         Stop[] stops = new Stop[]{new Stop(0.0, this.awtColorToJavaFX(gp.getColor1())), new Stop(1.0, this.awtColorToJavaFX(gp.getColor2()))};
         Point2D p1 = gp.getPoint1();
         Point2D p2 = gp.getPoint2();
         LinearGradient lg = new LinearGradient(p1.getX(), p1.getY(), p2.getX(), p2.getY(), false, CycleMethod.NO_CYCLE, stops);
         this.gc.setStroke(lg);
         this.gc.setFill(lg);
      } else if (paint instanceof MultipleGradientPaint) {
         MultipleGradientPaint mgp = (MultipleGradientPaint)paint;
         Color[] colors = mgp.getColors();
         float[] fractions = mgp.getFractions();
         Stop[] stops = new Stop[colors.length];

         for (int i = 0; i < colors.length; i++) {
            stops[i] = new Stop((double)fractions[i], this.awtColorToJavaFX(colors[i]));
         }

         if (paint instanceof RadialGradientPaint) {
            RadialGradientPaint rgp = (RadialGradientPaint)paint;
            Point2D center = rgp.getCenterPoint();
            Point2D focus = rgp.getFocusPoint();
            double focusDistance = focus.distance(center);
            double focusAngle = 0.0;
            if (!focus.equals(center)) {
               focusAngle = Math.atan2(focus.getY() - center.getY(), focus.getX() - center.getX());
            }

            double radius = (double)rgp.getRadius();
            RadialGradient rg = new RadialGradient(
               Math.toDegrees(focusAngle), focusDistance, center.getX(), center.getY(), radius, false, CycleMethod.NO_CYCLE, stops
            );
            this.gc.setStroke(rg);
            this.gc.setFill(rg);
         } else if (paint instanceof LinearGradientPaint) {
            LinearGradientPaint lgp = (LinearGradientPaint)paint;
            Point2D start = lgp.getStartPoint();
            Point2D end = lgp.getEndPoint();
            LinearGradient lg = new LinearGradient(start.getX(), start.getY(), end.getX(), end.getY(), false, CycleMethod.NO_CYCLE, stops);
            this.gc.setStroke(lg);
            this.gc.setFill(lg);
         }
      }
   }

   @Override
   public Color getColor() {
      return this.color;
   }

   @Override
   public void setColor(Color c) {
      if (c != null && !c.equals(this.color)) {
         this.applyColor(c);
      }
   }

   private void applyColor(Color c) {
      this.color = c;
      this.paint = c;
      javafx.scene.paint.Color fxcolor = this.awtColorToJavaFX(c);
      this.gc.setFill(fxcolor);
      this.gc.setStroke(fxcolor);
   }

   private javafx.scene.paint.Color awtColorToJavaFX(Color c) {
      return javafx.scene.paint.Color.rgb(c.getRed(), c.getGreen(), c.getBlue(), (double)c.getAlpha() / 255.0);
   }

   @Override
   public Color getBackground() {
      return this.background;
   }

   @Override
   public void setBackground(Color color) {
      this.background = color;
   }

   @Override
   public Composite getComposite() {
      return this.composite;
   }

   @Override
   public void setComposite(Composite comp) {
      nullNotPermitted(comp, "comp");
      this.composite = comp;
      if (comp instanceof AlphaComposite) {
         AlphaComposite ac = (AlphaComposite)comp;
         this.gc.setGlobalAlpha((double)ac.getAlpha());
         this.gc.setGlobalBlendMode(this.blendMode(ac.getRule()));
      }
   }

   private BlendMode blendMode(int rule) {
      switch (rule) {
         case 1:
         case 2:
         case 3:
         case 4:
         case 5:
         case 6:
         case 7:
         case 8:
         case 9:
         case 11:
         case 12:
            return BlendMode.SRC_OVER;
         case 10:
            return BlendMode.SRC_ATOP;
         default:
            return BlendMode.SRC_OVER;
      }
   }

   @Override
   public Stroke getStroke() {
      return this.stroke;
   }

   @Override
   public void setStroke(Stroke s) {
      nullNotPermitted(s, "s");
      if (s != this.stroke) {
         if (this.stroke instanceof BasicStroke) {
            BasicStroke bs = (BasicStroke)s;
            if (bs.equals(this.stroke)) {
               return;
            }
         }

         this.stroke = s;
         this.applyStroke(s);
      }
   }

   private void applyStroke(Stroke s) {
      if (s instanceof BasicStroke) {
         this.applyBasicStroke((BasicStroke)s);
      }
   }

   private void applyBasicStroke(BasicStroke bs) {
      double lineWidth = (double)bs.getLineWidth();
      if (lineWidth == 0.0) {
         lineWidth = this.zeroStrokeWidth;
      }

      this.gc.setLineWidth(lineWidth);
      this.gc.setLineCap(this.awtToJavaFXLineCap(bs.getEndCap()));
      this.gc.setLineJoin(this.awtToJavaFXLineJoin(bs.getLineJoin()));
      this.gc.setMiterLimit((double)bs.getMiterLimit());
      this.gc.setLineDashes(this.floatToDoubleArray(bs.getDashArray()));
      this.gc.setLineDashOffset((double)bs.getDashPhase());
   }

   private StrokeLineCap awtToJavaFXLineCap(int c) {
      if (c == 0) {
         return StrokeLineCap.BUTT;
      } else if (c == 1) {
         return StrokeLineCap.ROUND;
      } else if (c == 2) {
         return StrokeLineCap.SQUARE;
      } else {
         throw new IllegalArgumentException("Unrecognised cap code: " + c);
      }
   }

   private StrokeLineJoin awtToJavaFXLineJoin(int j) {
      if (j == 2) {
         return StrokeLineJoin.BEVEL;
      } else if (j == 0) {
         return StrokeLineJoin.MITER;
      } else if (j == 1) {
         return StrokeLineJoin.ROUND;
      } else {
         throw new IllegalArgumentException("Unrecognised join code: " + j);
      }
   }

   private double[] floatToDoubleArray(float[] f) {
      if (f == null) {
         return null;
      } else {
         double[] d = new double[f.length];

         for (int i = 0; i < f.length; i++) {
            d[i] = (double)f[i];
         }

         return d;
      }
   }

   @Override
   public Object getRenderingHint(Key hintKey) {
      return this.hints.get(hintKey);
   }

   @Override
   public void setRenderingHint(Key hintKey, Object hintValue) {
      this.hints.put(hintKey, hintValue);
   }

   @Override
   public RenderingHints getRenderingHints() {
      return (RenderingHints)this.hints.clone();
   }

   @Override
   public void setRenderingHints(Map<?, ?> hints) {
      this.hints.clear();
      this.hints.putAll(hints);
   }

   @Override
   public void addRenderingHints(Map<?, ?> hints) {
      this.hints.putAll(hints);
   }

   @Override
   public void draw(Shape s) {
      if (!(this.stroke instanceof BasicStroke)) {
         this.fill(this.stroke.createStrokedShape(s));
      } else {
         if (s instanceof Line2D) {
            Line2D l = (Line2D)s;
            Object hint = this.getRenderingHint(RenderingHints.KEY_STROKE_CONTROL);
            if (hint != RenderingHints.VALUE_STROKE_PURE) {
               double x1 = Math.rint(l.getX1()) - 0.5;
               double y1 = Math.rint(l.getY1()) - 0.5;
               double x2 = Math.rint(l.getX2()) - 0.5;
               double y2 = Math.rint(l.getY2()) - 0.5;
               l.setLine(x1, y1, x2, y2);
            }

            this.gc.strokeLine(l.getX1(), l.getY1(), l.getX2(), l.getY2());
         } else if (s instanceof Rectangle2D) {
            Rectangle2D r = (Rectangle2D)s;
            if (s instanceof Rectangle) {
               r = new Double(r.getX(), r.getY(), r.getWidth(), r.getHeight());
            }

            Object hint = this.getRenderingHint(RenderingHints.KEY_STROKE_CONTROL);
            if (hint != RenderingHints.VALUE_STROKE_PURE) {
               double x = Math.rint(r.getX()) - 0.5;
               double y = Math.rint(r.getY()) - 0.5;
               double w = Math.floor(r.getWidth());
               double h = Math.floor(r.getHeight());
               r.setRect(x, y, w, h);
            }

            this.gc.strokeRect(r.getX(), r.getY(), r.getWidth(), r.getHeight());
         } else if (s instanceof RoundRectangle2D) {
            RoundRectangle2D rr = (RoundRectangle2D)s;
            this.gc.strokeRoundRect(rr.getX(), rr.getY(), rr.getWidth(), rr.getHeight(), rr.getArcWidth(), rr.getArcHeight());
         } else if (s instanceof Ellipse2D) {
            Ellipse2D e = (Ellipse2D)s;
            this.gc.strokeOval(e.getX(), e.getY(), e.getWidth(), e.getHeight());
         } else if (s instanceof Arc2D) {
            Arc2D a = (Arc2D)s;
            this.gc.strokeArc(a.getX(), a.getY(), a.getWidth(), a.getHeight(), a.getAngleStart(), a.getAngleExtent(), this.intToArcType(a.getArcType()));
         } else {
            this.shapeToPath(s);
            this.gc.stroke();
         }
      }
   }

   private void shapeToPath(Shape s) {
      this.gc.beginPath();

      for (PathIterator iterator = s.getPathIterator(null); !iterator.isDone(); iterator.next()) {
         int segType = iterator.currentSegment(this.coords);
         switch (segType) {
            case 0:
               this.gc.moveTo(this.coords[0], this.coords[1]);
               break;
            case 1:
               this.gc.lineTo(this.coords[0], this.coords[1]);
               break;
            case 2:
               this.gc.quadraticCurveTo(this.coords[0], this.coords[1], this.coords[2], this.coords[3]);
               break;
            case 3:
               this.gc.bezierCurveTo(this.coords[0], this.coords[1], this.coords[2], this.coords[3], this.coords[4], this.coords[5]);
               break;
            case 4:
               this.gc.closePath();
               break;
            default:
               throw new RuntimeException("Unrecognised segment type " + segType);
         }
      }
   }

   private ArcType intToArcType(int t) {
      if (t == 1) {
         return ArcType.CHORD;
      } else if (t == 0) {
         return ArcType.OPEN;
      } else if (t == 2) {
         return ArcType.ROUND;
      } else {
         throw new IllegalArgumentException("Unrecognised t: " + t);
      }
   }

   @Override
   public void fill(Shape s) {
      if (s instanceof Rectangle2D) {
         Rectangle2D r = (Rectangle2D)s;
         this.gc.fillRect(r.getX(), r.getY(), r.getWidth(), r.getHeight());
      } else if (s instanceof RoundRectangle2D) {
         RoundRectangle2D rr = (RoundRectangle2D)s;
         this.gc.fillRoundRect(rr.getX(), rr.getY(), rr.getWidth(), rr.getHeight(), rr.getArcWidth(), rr.getArcHeight());
      } else if (s instanceof Ellipse2D) {
         Ellipse2D e = (Ellipse2D)s;
         this.gc.fillOval(e.getX(), e.getY(), e.getWidth(), e.getHeight());
      } else if (s instanceof Arc2D) {
         Arc2D a = (Arc2D)s;
         this.gc.fillArc(a.getX(), a.getY(), a.getWidth(), a.getHeight(), a.getAngleStart(), a.getAngleExtent(), this.intToArcType(a.getArcType()));
      } else {
         this.shapeToPath(s);
         this.gc.fill();
      }
   }

   @Override
   public Font getFont() {
      return this.font;
   }

   @Override
   public void setFont(Font font) {
      if (font != null && !this.font.equals(font)) {
         this.applyFont(font);
      }
   }

   private void applyFont(Font font) {
      this.font = font;
      FontWeight weight = font.isBold() ? FontWeight.BOLD : FontWeight.NORMAL;
      FontPosture posture = font.isItalic() ? FontPosture.ITALIC : FontPosture.REGULAR;
      javafx.scene.text.Font jfxfont = javafx.scene.text.Font.font(font.getFamily(), weight, posture, (double)font.getSize());
      this.gc.setFont(jfxfont);
   }

   @Override
   public FontMetrics getFontMetrics(Font f) {
      if (this.getRenderingHint(FXHints.KEY_USE_FX_FONT_METRICS) != Boolean.TRUE) {
         if (this.fmImage == null) {
            this.fmImage = new BufferedImage(10, 10, 1);
            this.fmImageG2 = this.fmImage.createGraphics();
            this.fmImageG2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
         }

         return this.fmImageG2.getFontMetrics(f);
      } else {
         if (this.fxFontMetrics == null || !f.equals(this.fxFontMetrics.getFont())) {
            this.fxFontMetrics = new FXFontMetrics(this.font, this);
         }

         return this.fxFontMetrics;
      }
   }

   @Override
   public FontRenderContext getFontRenderContext() {
      return this.fontRenderContext;
   }

   @Override
   public void drawString(String str, int x, int y) {
      this.drawString(str, (float)x, (float)y);
   }

   @Override
   public void drawString(String str, float x, float y) {
      if (str == null) {
         throw new NullPointerException("Null 'str' argument.");
      } else {
         this.gc.fillText(str, (double)x, (double)y);
      }
   }

   @Override
   public void drawString(AttributedCharacterIterator iterator, int x, int y) {
      this.drawString(iterator, (float)x, (float)y);
   }

   @Override
   public void drawString(AttributedCharacterIterator iterator, float x, float y) {
      Set<Attribute> s = iterator.getAllAttributeKeys();
      if (!s.isEmpty()) {
         TextLayout layout = new TextLayout(iterator, this.getFontRenderContext());
         layout.draw(this, x, y);
      } else {
         StringBuilder strb = new StringBuilder();
         iterator.first();

         for (int i = iterator.getBeginIndex(); i < iterator.getEndIndex(); i++) {
            strb.append(iterator.current());
            iterator.next();
         }

         this.drawString(strb.toString(), x, y);
      }
   }

   @Override
   public void drawGlyphVector(GlyphVector g, float x, float y) {
      this.fill(g.getOutline(x, y));
   }

   @Override
   public void translate(int tx, int ty) {
      this.translate((double)tx, (double)ty);
   }

   @Override
   public void translate(double tx, double ty) {
      this.transform.translate(tx, ty);
      this.gc.translate(tx, ty);
   }

   @Override
   public void rotate(double theta) {
      this.transform.rotate(theta);
      this.gc.rotate(Math.toDegrees(theta));
   }

   @Override
   public void rotate(double theta, double x, double y) {
      this.translate(x, y);
      this.rotate(theta);
      this.translate(-x, -y);
   }

   @Override
   public void scale(double sx, double sy) {
      this.transform.scale(sx, sy);
      this.gc.scale(sx, sy);
   }

   @Override
   public void shear(double shx, double shy) {
      this.transform(AffineTransform.getShearInstance(shx, shy));
   }

   @Override
   public void transform(AffineTransform t) {
      AffineTransform tx = this.getTransform();
      tx.concatenate(t);
      this.setTransform(tx);
   }

   @Override
   public AffineTransform getTransform() {
      return (AffineTransform)this.transform.clone();
   }

   @Override
   public void setTransform(AffineTransform t) {
      if (t == null) {
         this.transform = new AffineTransform();
         t = this.transform;
      } else {
         this.transform = new AffineTransform(t);
      }

      this.gc.setTransform(t.getScaleX(), t.getShearY(), t.getShearX(), t.getScaleY(), t.getTranslateX(), t.getTranslateY());
   }

   @Override
   public boolean hit(Rectangle rect, Shape s, boolean onStroke) {
      Shape ts;
      if (onStroke) {
         ts = this.transform.createTransformedShape(this.stroke.createStrokedShape(s));
      } else {
         ts = this.transform.createTransformedShape(s);
      }

      if (!rect.getBounds2D().intersects(ts.getBounds2D())) {
         return false;
      } else {
         Area a1 = new Area(rect);
         Area a2 = new Area(ts);
         a1.intersect(a2);
         return !a1.isEmpty();
      }
   }

   @Override
   public void setPaintMode() {
   }

   @Override
   public void setXORMode(Color c1) {
   }

   @Override
   public Rectangle getClipBounds() {
      return this.clip == null ? null : this.getClip().getBounds();
   }

   @Override
   public Shape getClip() {
      if (this.clip == null) {
         return null;
      } else {
         try {
            AffineTransform inv = this.transform.createInverse();
            return inv.createTransformedShape(this.clip);
         } catch (NoninvertibleTransformException var3) {
            return null;
         }
      }
   }

   @Override
   public void setClip(Shape shape) {
      if (this.stateSaved) {
         this.gc.restore();
         this.reapplyAttributes();
         this.stateSaved = false;
      }

      this.clip = this.transform.createTransformedShape(shape);
      if (this.clip != null) {
         this.gc.save();
         this.rememberSavedAttributes();
         this.shapeToPath(shape);
         this.gc.clip();
      }
   }

   private void rememberSavedAttributes() {
      this.stateSaved = true;
      this.savedColor = this.color;
      this.savedFont = this.font;
      this.savedPaint = this.paint;
      this.savedStroke = this.stroke;
      this.savedTransform = new AffineTransform(this.transform);
   }

   private void reapplyAttributes() {
      if (!paintsAreEqual(this.paint, this.savedPaint)) {
         this.applyPaint(this.paint);
      }

      if (!this.color.equals(this.savedColor)) {
         this.applyColor(this.color);
      }

      if (!this.stroke.equals(this.savedStroke)) {
         this.applyStroke(this.stroke);
      }

      if (!this.font.equals(this.savedFont)) {
         this.applyFont(this.font);
      }

      if (!this.transform.equals(this.savedTransform)) {
         this.setTransform(this.transform);
      }

      this.savedColor = null;
      this.savedFont = null;
      this.savedPaint = null;
      this.savedStroke = null;
      this.savedTransform = null;
   }

   @Override
   public void clip(Shape s) {
      if (s instanceof Line2D) {
         s = s.getBounds2D();
      }

      if (this.clip == null) {
         this.setClip(s);
      } else {
         Shape ts = this.transform.createTransformedShape(s);
         Shape clipNew;
         if (!ts.intersects(this.clip.getBounds2D())) {
            clipNew = new Double();
         } else {
            Area a1 = new Area(ts);
            Area a2 = new Area(this.clip);
            a1.intersect(a2);
            clipNew = new java.awt.geom.Path2D.Double(a1);
         }

         this.clip = clipNew;
         if (!this.stateSaved) {
            this.gc.save();
            this.rememberSavedAttributes();
         }

         this.shapeToPath(this.clip);
         this.gc.clip();
      }
   }

   @Override
   public void clipRect(int x, int y, int width, int height) {
      this.clip(this.rect(x, y, width, height));
   }

   @Override
   public void setClip(int x, int y, int width, int height) {
      this.setClip(this.rect(x, y, width, height));
   }

   @Override
   public void drawLine(int x1, int y1, int x2, int y2) {
      if (this.line == null) {
         this.line = new java.awt.geom.Line2D.Double((double)x1, (double)y1, (double)x2, (double)y2);
      } else {
         this.line.setLine((double)x1, (double)y1, (double)x2, (double)y2);
      }

      this.draw(this.line);
   }

   @Override
   public void fillRect(int x, int y, int width, int height) {
      this.fill(this.rect(x, y, width, height));
   }

   @Override
   public void clearRect(int x, int y, int width, int height) {
      if (this.getBackground() != null) {
         Paint saved = this.getPaint();
         this.setPaint(this.getBackground());
         this.fillRect(x, y, width, height);
         this.setPaint(saved);
      }
   }

   @Override
   public void drawRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
      this.draw(this.roundRect(x, y, width, height, arcWidth, arcHeight));
   }

   @Override
   public void fillRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
      this.fill(this.roundRect(x, y, width, height, arcWidth, arcHeight));
   }

   @Override
   public void drawOval(int x, int y, int width, int height) {
      this.draw(this.oval(x, y, width, height));
   }

   @Override
   public void fillOval(int x, int y, int width, int height) {
      this.fill(this.oval(x, y, width, height));
   }

   @Override
   public void drawArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
      this.draw(this.arc(x, y, width, height, startAngle, arcAngle));
   }

   @Override
   public void fillArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
      this.fill(this.arc(x, y, width, height, startAngle, arcAngle));
   }

   @Override
   public void drawPolyline(int[] xPoints, int[] yPoints, int nPoints) {
      GeneralPath p = this.createPolygon(xPoints, yPoints, nPoints, false);
      this.draw(p);
   }

   @Override
   public void drawPolygon(int[] xPoints, int[] yPoints, int nPoints) {
      GeneralPath p = this.createPolygon(xPoints, yPoints, nPoints, true);
      this.draw(p);
   }

   @Override
   public void fillPolygon(int[] xPoints, int[] yPoints, int nPoints) {
      GeneralPath p = this.createPolygon(xPoints, yPoints, nPoints, true);
      this.fill(p);
   }

   public GeneralPath createPolygon(int[] xPoints, int[] yPoints, int nPoints, boolean close) {
      GeneralPath p = new GeneralPath();
      p.moveTo((float)xPoints[0], (float)yPoints[0]);

      for (int i = 1; i < nPoints; i++) {
         p.lineTo((float)xPoints[i], (float)yPoints[i]);
      }

      if (close) {
         p.closePath();
      }

      return p;
   }

   @Override
   public boolean drawImage(Image img, int x, int y, ImageObserver observer) {
      if (img == null) {
         return true;
      } else {
         int w = img.getWidth(observer);
         if (w < 0) {
            return false;
         } else {
            int h = img.getHeight(observer);
            return h < 0 ? false : this.drawImage(img, x, y, w, h, observer);
         }
      }
   }

   @Override
   public boolean drawImage(Image img, int x, int y, int w, int h, ImageObserver observer) {
      BufferedImage buffered;
      if (img instanceof BufferedImage) {
         buffered = (BufferedImage)img;
      } else {
         buffered = new BufferedImage(w, h, 2);
         Graphics2D g2 = buffered.createGraphics();
         g2.drawImage(img, 0, 0, w, h, null);
         g2.dispose();
      }

      WritableImage fxImage = SwingFXUtils.toFXImage(buffered, null);
      this.gc.drawImage(fxImage, (double)x, (double)y, (double)w, (double)h);
      return true;
   }

   @Override
   public boolean drawImage(Image img, int x, int y, Color bgcolor, ImageObserver observer) {
      if (img == null) {
         return true;
      } else {
         int w = img.getWidth(null);
         if (w < 0) {
            return false;
         } else {
            int h = img.getHeight(null);
            return h < 0 ? false : this.drawImage(img, x, y, w, h, bgcolor, observer);
         }
      }
   }

   @Override
   public boolean drawImage(Image img, int x, int y, int w, int h, Color bgcolor, ImageObserver observer) {
      Paint saved = this.getPaint();
      this.setPaint(bgcolor);
      this.fillRect(x, y, w, h);
      this.setPaint(saved);
      return this.drawImage(img, x, y, w, h, observer);
   }

   @Override
   public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2, ImageObserver observer) {
      int w = dx2 - dx1;
      int h = dy2 - dy1;
      BufferedImage img2 = new BufferedImage(w, h, 2);
      Graphics2D g2 = img2.createGraphics();
      g2.drawImage(img, 0, 0, w, h, sx1, sy1, sx2, sy2, null);
      return this.drawImage(img2, dx1, dy1, null);
   }

   @Override
   public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2, Color bgcolor, ImageObserver observer) {
      Paint saved = this.getPaint();
      this.setPaint(bgcolor);
      this.fillRect(dx1, dy1, dx2 - dx1, dy2 - dy1);
      this.setPaint(saved);
      return this.drawImage(img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, observer);
   }

   @Override
   public void drawRenderedImage(RenderedImage img, AffineTransform xform) {
      BufferedImage bi = convertRenderedImage(img);
      this.drawImage(bi, xform, null);
   }

   private static BufferedImage convertRenderedImage(RenderedImage img) {
      if (img instanceof BufferedImage) {
         return (BufferedImage)img;
      } else {
         ColorModel cm = img.getColorModel();
         int width = img.getWidth();
         int height = img.getHeight();
         WritableRaster raster = cm.createCompatibleWritableRaster(width, height);
         boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
         Hashtable properties = new Hashtable();
         String[] keys = img.getPropertyNames();
         if (keys != null) {
            for (int i = 0; i < keys.length; i++) {
               properties.put(keys[i], img.getProperty(keys[i]));
            }
         }

         BufferedImage result = new BufferedImage(cm, raster, isAlphaPremultiplied, properties);
         img.copyData(raster);
         return result;
      }
   }

   @Override
   public void drawRenderableImage(RenderableImage img, AffineTransform xform) {
      RenderedImage ri = img.createDefaultRendering();
      this.drawRenderedImage(ri, xform);
   }

   @Override
   public boolean drawImage(Image img, AffineTransform xform, ImageObserver obs) {
      AffineTransform savedTransform = this.getTransform();
      if (xform != null) {
         this.transform(xform);
      }

      boolean result = this.drawImage(img, 0, 0, obs);
      if (xform != null) {
         this.setTransform(savedTransform);
      }

      return result;
   }

   @Override
   public void drawImage(BufferedImage img, BufferedImageOp op, int x, int y) {
      BufferedImage imageToDraw = op.filter(img, null);
      this.drawImage(imageToDraw, new AffineTransform(1.0F, 0.0F, 0.0F, 1.0F, (float)x, (float)y), null);
   }

   @Override
   public void copyArea(int x, int y, int width, int height, int dx, int dy) {
   }

   @Override
   public void dispose() {
   }

   private Rectangle2D rect(int x, int y, int width, int height) {
      if (this.rect == null) {
         this.rect = new Double((double)x, (double)y, (double)width, (double)height);
      } else {
         this.rect.setRect((double)x, (double)y, (double)width, (double)height);
      }

      return this.rect;
   }

   private RoundRectangle2D roundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
      if (this.roundRect == null) {
         this.roundRect = new java.awt.geom.RoundRectangle2D.Double((double)x, (double)y, (double)width, (double)height, (double)arcWidth, (double)arcHeight);
      } else {
         this.roundRect.setRoundRect((double)x, (double)y, (double)width, (double)height, (double)arcWidth, (double)arcHeight);
      }

      return this.roundRect;
   }

   private Arc2D arc(int x, int y, int width, int height, int startAngle, int arcAngle) {
      if (this.arc == null) {
         this.arc = new java.awt.geom.Arc2D.Double((double)x, (double)y, (double)width, (double)height, (double)startAngle, (double)arcAngle, 0);
      } else {
         this.arc.setArc((double)x, (double)y, (double)width, (double)height, (double)startAngle, (double)arcAngle, 0);
      }

      return this.arc;
   }

   private Ellipse2D oval(int x, int y, int width, int height) {
      if (this.oval == null) {
         this.oval = new java.awt.geom.Ellipse2D.Double((double)x, (double)y, (double)width, (double)height);
      } else {
         this.oval.setFrame((double)x, (double)y, (double)width, (double)height);
      }

      return this.oval;
   }

   private static boolean paintsAreEqual(Paint p1, Paint p2) {
      if (p1 == p2) {
         return true;
      } else if (p1 == null) {
         return p2 == null;
      } else if (p2 == null) {
         return false;
      } else if (p1 instanceof Color && p2 instanceof Color) {
         return p1.equals(p2);
      } else if (p1 instanceof GradientPaint && p2 instanceof GradientPaint) {
         GradientPaint gp1 = (GradientPaint)p1;
         GradientPaint gp2 = (GradientPaint)p2;
         return gp1.getColor1().equals(gp2.getColor1())
            && gp1.getColor2().equals(gp2.getColor2())
            && gp1.getPoint1().equals(gp2.getPoint1())
            && gp1.getPoint2().equals(gp2.getPoint2())
            && gp1.isCyclic() == gp2.isCyclic()
            && gp1.getTransparency() == gp1.getTransparency();
      } else if (p1 instanceof LinearGradientPaint && p2 instanceof LinearGradientPaint) {
         LinearGradientPaint lgp1 = (LinearGradientPaint)p1;
         LinearGradientPaint lgp2 = (LinearGradientPaint)p2;
         return lgp1.getStartPoint().equals(lgp2.getStartPoint())
            && lgp1.getEndPoint().equals(lgp2.getEndPoint())
            && Arrays.equals(lgp1.getFractions(), lgp2.getFractions())
            && Arrays.equals((Object[])lgp1.getColors(), (Object[])lgp2.getColors())
            && lgp1.getCycleMethod() == lgp2.getCycleMethod()
            && lgp1.getColorSpace() == lgp2.getColorSpace()
            && lgp1.getTransform().equals(lgp2.getTransform());
      } else if (p1 instanceof RadialGradientPaint && p2 instanceof RadialGradientPaint) {
         RadialGradientPaint rgp1 = (RadialGradientPaint)p1;
         RadialGradientPaint rgp2 = (RadialGradientPaint)p2;
         return rgp1.getCenterPoint().equals(rgp2.getCenterPoint())
            && rgp1.getRadius() == rgp2.getRadius()
            && rgp1.getFocusPoint().equals(rgp2.getFocusPoint())
            && Arrays.equals(rgp1.getFractions(), rgp2.getFractions())
            && Arrays.equals((Object[])rgp1.getColors(), (Object[])rgp2.getColors())
            && rgp1.getCycleMethod() == rgp2.getCycleMethod()
            && rgp1.getColorSpace() == rgp2.getColorSpace()
            && rgp1.getTransform().equals(rgp2.getTransform());
      } else {
         return p1.equals(p2);
      }
   }
}
