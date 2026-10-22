package org.controlsfx.tools;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

public final class Borders {
   private static final Color DEFAULT_BORDER_COLOR = Color.DARKGRAY;
   private final Node node;
   private final List<Borders.Border> borders;

   public static Borders wrap(Node n) {
      return new Borders(n);
   }

   private Borders(Node n) {
      this.node = n;
      this.borders = new ArrayList<>();
   }

   public Borders.EmptyBorders emptyBorder() {
      return new Borders.EmptyBorders(this);
   }

   public Borders.EtchedBorders etchedBorder() {
      return new Borders.EtchedBorders(this);
   }

   public Borders.LineBorders lineBorder() {
      return new Borders.LineBorders(this);
   }

   public Borders addBorder(Borders.Border border) {
      this.borders.add(border);
      return this;
   }

   public Node build() {
      Node bundle = this.node;

      for (int i = this.borders.size() - 1; i >= 0; i--) {
         Borders.Border border = this.borders.get(i);
         bundle = border.wrap(bundle);
      }

      return bundle;
   }

   @FunctionalInterface
   public interface Border {
      Node wrap(Node var1);
   }

   public class EmptyBorders {
      private final Borders parent;
      private double top;
      private double right;
      private double bottom;
      private double left;

      private EmptyBorders(Borders parent) {
         this.parent = parent;
      }

      public Borders.EmptyBorders padding(double padding) {
         return this.padding(padding, padding, padding, padding);
      }

      public Borders.EmptyBorders padding(double top, double right, double bottom, double left) {
         this.top = top;
         this.right = right;
         this.bottom = bottom;
         this.left = left;
         return this;
      }

      public Borders build() {
         this.parent.addBorder(new Borders.StrokeBorder(null, this.buildStroke()));
         return this.parent;
      }

      public Node buildAll() {
         this.build();
         return this.parent.build();
      }

      private BorderStroke buildStroke() {
         return new BorderStroke(null, BorderStrokeStyle.NONE, null, new BorderWidths(this.top, this.right, this.bottom, this.left), Insets.EMPTY);
      }
   }

   public class EtchedBorders {
      private final Borders parent;
      private String title;
      private boolean raised = false;
      private double outerTopPadding = 10.0;
      private double outerRightPadding = 10.0;
      private double outerBottomPadding = 10.0;
      private double outerLeftPadding = 10.0;
      private double innerTopPadding = 15.0;
      private double innerRightPadding = 15.0;
      private double innerBottomPadding = 15.0;
      private double innerLeftPadding = 15.0;
      private double topLeftRadius = 0.0;
      private double topRightRadius = 0.0;
      private double bottomRightRadius = 0.0;
      private double bottomLeftRadius = 0.0;
      private Color highlightColor = Borders.DEFAULT_BORDER_COLOR;
      private Color shadowColor = Color.WHITE;

      private EtchedBorders(Borders parent) {
         this.parent = parent;
      }

      public Borders.EtchedBorders highlight(Color highlight) {
         this.highlightColor = highlight;
         return this;
      }

      public Borders.EtchedBorders shadow(Color shadow) {
         this.shadowColor = shadow;
         return this;
      }

      public Borders.EtchedBorders raised() {
         this.raised = true;
         return this;
      }

      public Borders.EtchedBorders title(String title) {
         this.title = title;
         return this;
      }

      public Borders.EtchedBorders outerPadding(double padding) {
         return this.outerPadding(padding, padding, padding, padding);
      }

      public Borders.EtchedBorders outerPadding(double topPadding, double rightPadding, double bottomPadding, double leftPadding) {
         this.outerTopPadding = topPadding;
         this.outerRightPadding = rightPadding;
         this.outerBottomPadding = bottomPadding;
         this.outerLeftPadding = leftPadding;
         return this;
      }

      public Borders.EtchedBorders innerPadding(double padding) {
         return this.innerPadding(padding, padding, padding, padding);
      }

      public Borders.EtchedBorders innerPadding(double topPadding, double rightPadding, double bottomPadding, double leftPadding) {
         this.innerTopPadding = topPadding;
         this.innerRightPadding = rightPadding;
         this.innerBottomPadding = bottomPadding;
         this.innerLeftPadding = leftPadding;
         return this;
      }

      public Borders.EtchedBorders radius(double radius) {
         return this.radius(radius, radius, radius, radius);
      }

      public Borders.EtchedBorders radius(double topLeft, double topRight, double bottomRight, double bottomLeft) {
         this.topLeftRadius = topLeft;
         this.topRightRadius = topRight;
         this.bottomRightRadius = bottomRight;
         this.bottomLeftRadius = bottomLeft;
         return this;
      }

      public Borders build() {
         Color inner = this.raised ? this.shadowColor : this.highlightColor;
         Color outer = this.raised ? this.highlightColor : this.shadowColor;
         BorderStroke innerStroke = new BorderStroke(
            inner,
            BorderStrokeStyle.SOLID,
            new CornerRadii(this.topLeftRadius, this.topRightRadius, this.bottomRightRadius, this.bottomLeftRadius, false),
            new BorderWidths(1.0)
         );
         BorderStroke outerStroke = new BorderStroke(
            outer,
            BorderStrokeStyle.SOLID,
            new CornerRadii(this.topLeftRadius, this.topRightRadius, this.bottomRightRadius, this.bottomLeftRadius, false),
            new BorderWidths(1.0),
            new Insets(1.0)
         );
         BorderStroke outerPadding = Borders.this.new EmptyBorders(this.parent)
            .padding(this.outerTopPadding, this.outerRightPadding, this.outerBottomPadding, this.outerLeftPadding)
            .buildStroke();
         BorderStroke innerPadding = Borders.this.new EmptyBorders(this.parent)
            .padding(this.innerTopPadding, this.innerRightPadding, this.innerBottomPadding, this.innerLeftPadding)
            .buildStroke();
         this.parent.addBorder(new Borders.StrokeBorder(null, outerPadding));
         this.parent.addBorder(new Borders.StrokeBorder(this.title, innerStroke, outerStroke));
         this.parent.addBorder(new Borders.StrokeBorder(null, innerPadding));
         return this.parent;
      }

      public Node buildAll() {
         this.build();
         return this.parent.build();
      }
   }

   public class LineBorders {
      private final Borders parent;
      private String title;
      private BorderStrokeStyle strokeStyle = BorderStrokeStyle.SOLID;
      private Color topColor = Borders.DEFAULT_BORDER_COLOR;
      private Color rightColor = Borders.DEFAULT_BORDER_COLOR;
      private Color bottomColor = Borders.DEFAULT_BORDER_COLOR;
      private Color leftColor = Borders.DEFAULT_BORDER_COLOR;
      private double outerTopPadding = 10.0;
      private double outerRightPadding = 10.0;
      private double outerBottomPadding = 10.0;
      private double outerLeftPadding = 10.0;
      private double innerTopPadding = 15.0;
      private double innerRightPadding = 15.0;
      private double innerBottomPadding = 15.0;
      private double innerLeftPadding = 15.0;
      private double topThickness = 1.0;
      private double rightThickness = 1.0;
      private double bottomThickness = 1.0;
      private double leftThickness = 1.0;
      private double topLeftRadius = 0.0;
      private double topRightRadius = 0.0;
      private double bottomRightRadius = 0.0;
      private double bottomLeftRadius = 0.0;

      private LineBorders(Borders parent) {
         this.parent = parent;
      }

      public Borders.LineBorders color(Color color) {
         return this.color(color, color, color, color);
      }

      public Borders.LineBorders color(Color topColor, Color rightColor, Color bottomColor, Color leftColor) {
         this.topColor = topColor;
         this.rightColor = rightColor;
         this.bottomColor = bottomColor;
         this.leftColor = leftColor;
         return this;
      }

      public Borders.LineBorders strokeStyle(BorderStrokeStyle strokeStyle) {
         this.strokeStyle = strokeStyle;
         return this;
      }

      public Borders.LineBorders outerPadding(double padding) {
         return this.outerPadding(padding, padding, padding, padding);
      }

      public Borders.LineBorders outerPadding(double topPadding, double rightPadding, double bottomPadding, double leftPadding) {
         this.outerTopPadding = topPadding;
         this.outerRightPadding = rightPadding;
         this.outerBottomPadding = bottomPadding;
         this.outerLeftPadding = leftPadding;
         return this;
      }

      public Borders.LineBorders innerPadding(double padding) {
         return this.innerPadding(padding, padding, padding, padding);
      }

      public Borders.LineBorders innerPadding(double topPadding, double rightPadding, double bottomPadding, double leftPadding) {
         this.innerTopPadding = topPadding;
         this.innerRightPadding = rightPadding;
         this.innerBottomPadding = bottomPadding;
         this.innerLeftPadding = leftPadding;
         return this;
      }

      public Borders.LineBorders thickness(double thickness) {
         return this.thickness(thickness, thickness, thickness, thickness);
      }

      public Borders.LineBorders thickness(double topThickness, double rightThickness, double bottomThickness, double leftThickness) {
         this.topThickness = topThickness;
         this.rightThickness = rightThickness;
         this.bottomThickness = bottomThickness;
         this.leftThickness = leftThickness;
         return this;
      }

      public Borders.LineBorders radius(double radius) {
         return this.radius(radius, radius, radius, radius);
      }

      public Borders.LineBorders radius(double topLeft, double topRight, double bottomRight, double bottomLeft) {
         this.topLeftRadius = topLeft;
         this.topRightRadius = topRight;
         this.bottomRightRadius = bottomRight;
         this.bottomLeftRadius = bottomLeft;
         return this;
      }

      public Borders.LineBorders title(String title) {
         this.title = title;
         return this;
      }

      public Borders build() {
         BorderStroke borderStroke = new BorderStroke(
            this.topColor,
            this.rightColor,
            this.bottomColor,
            this.leftColor,
            this.strokeStyle,
            this.strokeStyle,
            this.strokeStyle,
            this.strokeStyle,
            new CornerRadii(this.topLeftRadius, this.topRightRadius, this.bottomRightRadius, this.bottomLeftRadius, false),
            new BorderWidths(this.topThickness, this.rightThickness, this.bottomThickness, this.leftThickness),
            null
         );
         BorderStroke outerPadding = Borders.this.new EmptyBorders(this.parent)
            .padding(this.outerTopPadding, this.outerRightPadding, this.outerBottomPadding, this.outerLeftPadding)
            .buildStroke();
         BorderStroke innerPadding = Borders.this.new EmptyBorders(this.parent)
            .padding(this.innerTopPadding, this.innerRightPadding, this.innerBottomPadding, this.innerLeftPadding)
            .buildStroke();
         this.parent.addBorder(new Borders.StrokeBorder(null, outerPadding));
         this.parent.addBorder(new Borders.StrokeBorder(this.title, borderStroke));
         this.parent.addBorder(new Borders.StrokeBorder(null, innerPadding));
         return this.parent;
      }

      public Node buildAll() {
         this.build();
         return this.parent.build();
      }
   }

   private static class StrokeBorder implements Borders.Border {
      private static final int TITLE_PADDING = 3;
      private static final double GAP_PADDING = 5.0;
      private final String title;
      private final BorderStroke[] borderStrokes;

      public StrokeBorder(String title, BorderStroke... borderStrokes) {
         this.title = title;
         this.borderStrokes = borderStrokes;
      }

      @Override
      public Node wrap(final Node n) {
         StackPane pane = new StackPane() {
            Label titleLabel;

            {
               this.getChildren().add(n);
               if (StrokeBorder.this.title != null) {
                  this.titleLabel = new Label(StrokeBorder.this.title);
                  this.titleLabel.setPadding(new Insets(0.0, 0.0, 0.0, 3.0));
                  this.getChildren().add(this.titleLabel);
               }
            }

            protected void layoutChildren() {
               super.layoutChildren();
               if (this.titleLabel != null) {
                  double labelHeight = this.titleLabel.prefHeight(-1.0);
                  double labelWidth = this.titleLabel.prefWidth(labelHeight) + 3.0;
                  this.titleLabel.resize(labelWidth, labelHeight);
                  this.titleLabel.relocate(6.0, -labelHeight / 2.0 - 1.0);
                  List<BorderStroke> newBorderStrokes = new ArrayList<>(2);

                  for (BorderStroke bs : StrokeBorder.this.borderStrokes) {
                     List<Double> dashList = new ArrayList<>();
                     if (bs.getTopStyle().getDashArray().isEmpty()) {
                        dashList.addAll(Arrays.asList(5.0, labelWidth, Double.MAX_VALUE));
                     } else {
                        double origDashWidth = bs.getTopStyle().getDashArray().stream().mapToDouble(d -> d).sum();
                        if (origDashWidth > 5.0) {
                           dashList.add(5.0);
                           dashList.add(labelWidth);
                        } else {
                           int no = (int)(5.0 / origDashWidth);

                           for (int i = 0; i < no; i++) {
                              dashList.addAll(bs.getTopStyle().getDashArray());
                           }

                           if ((dashList.size() & 1) == 0) {
                              dashList.add(0.0);
                           }

                           dashList.add(labelWidth + 5.0 - (double)no * origDashWidth);
                        }

                        for (int i = 0; (double)i < (this.getWidth() - labelWidth - origDashWidth) / origDashWidth; i++) {
                           dashList.addAll(bs.getTopStyle().getDashArray());
                        }
                     }

                     BorderStrokeStyle topStrokeStyle = new BorderStrokeStyle(
                        bs.getTopStyle().getType(),
                        bs.getTopStyle().getLineJoin(),
                        bs.getTopStyle().getLineCap(),
                        bs.getTopStyle().getMiterLimit(),
                        bs.getTopStyle().getDashOffset(),
                        dashList
                     );
                     newBorderStrokes.add(
                        new BorderStroke(
                           bs.getTopStroke(),
                           bs.getRightStroke(),
                           bs.getBottomStroke(),
                           bs.getLeftStroke(),
                           topStrokeStyle,
                           bs.getRightStyle(),
                           bs.getBottomStyle(),
                           bs.getLeftStyle(),
                           bs.getRadii(),
                           bs.getWidths(),
                           null
                        )
                     );
                  }

                  this.setBorder(new javafx.scene.layout.Border(newBorderStrokes.toArray(new BorderStroke[newBorderStrokes.size()])));
               }
            }
         };
         pane.setBorder(new javafx.scene.layout.Border(this.borderStrokes));
         return pane;
      }
   }
}
