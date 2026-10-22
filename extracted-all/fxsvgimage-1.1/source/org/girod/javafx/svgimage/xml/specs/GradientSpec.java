package org.girod.javafx.svgimage.xml.specs;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.Paint;
import javafx.scene.paint.Stop;
import javafx.scene.transform.Transform;
import org.girod.javafx.svgimage.xml.parsers.ParserUtils;
import org.girod.javafx.svgimage.xml.parsers.PercentParser;
import org.girod.javafx.svgimage.xml.parsers.SVGTags;
import org.girod.javafx.svgimage.xml.parsers.XMLNode;

public abstract class GradientSpec implements SVGTags {
   protected String href = null;
   protected XMLNode xmlNode = null;
   protected boolean isResolved = false;
   protected List<GradientSpec.StopSpec> specStops = new ArrayList<>();
   protected List<Transform> transformList = null;

   public GradientSpec(XMLNode node) {
      this.xmlNode = node;
   }

   public GradientSpec(XMLNode node, String href) {
      this.xmlNode = node;
      this.href = href;
   }

   public XMLNode getNode() {
      return this.xmlNode;
   }

   public boolean isResolved() {
      return this.isResolved;
   }

   public abstract Paint getPaint();

   public abstract void resolve(Map<String, GradientSpec> var1, Viewport var2);

   public void setTransformList(List<Transform> transformList) {
      this.transformList = transformList;
   }

   public List<Transform> getTransformList() {
      return this.transformList;
   }

   protected double getGradientPos(XMLNode xmlNode, String id) {
      String attrvalue = xmlNode.getAttributeValue(id);
      if (attrvalue.endsWith("%") && attrvalue.length() > 1) {
         attrvalue = attrvalue.substring(0, attrvalue.length() - 1);
         return ParserUtils.parseDoubleProtected(attrvalue) / 100.0;
      } else {
         return ParserUtils.parseDoubleProtected(attrvalue);
      }
   }

   protected CycleMethod getCycleMethod(String value) {
      if (value.equals("reflect")) {
         return CycleMethod.REFLECT;
      } else {
         return value.equals("repeat") ? CycleMethod.REPEAT : CycleMethod.NO_CYCLE;
      }
   }

   public GradientSpec.StopSpec addStop(double offset, double opacity, Color color) {
      GradientSpec.StopSpec stop = new GradientSpec.StopSpec(offset, opacity, color);
      this.specStops.add(stop);
      return stop;
   }

   public List<GradientSpec.StopSpec> getStops() {
      return this.specStops;
   }

   protected List<Stop> convertStops(List<GradientSpec.StopSpec> specstops) {
      List<Stop> stops = new ArrayList<>();

      for (GradientSpec.StopSpec theStop : specstops) {
         Stop stop = new Stop(theStop.offset, theStop.color);
         stops.add(stop);
      }

      return stops;
   }

   protected List<GradientSpec.StopSpec> buildStops(GradientSpec spec, XMLNode xmlNode, String kindOfGradient) {
      List<GradientSpec.StopSpec> stops = new ArrayList<>();

      for (XMLNode childNode : xmlNode.getChildren()) {
         if (childNode.getName().equals("stop")) {
            double offset = 0.0;
            String color = null;
            double opacity = 1.0;

            for (String attrname : childNode.getAttributes().keySet()) {
               switch (attrname) {
                  case "offset":
                     offset = PercentParser.parseValue(childNode, attrname);
                     break;
                  case "stop-color":
                     color = childNode.getAttributeValue(attrname);
                     break;
                  case "stop-opacity":
                     opacity = ParserUtils.parseDoubleProtected(childNode.getAttributeValue(attrname));
                     break;
                  case "style":
                     String style = childNode.getAttributeValue(attrname);
                     StringTokenizer tokenizer = new StringTokenizer(style, ";");

                     while (tokenizer.hasMoreTokens()) {
                        String item = tokenizer.nextToken().trim();
                        if (item.startsWith("stop-color")) {
                           color = item.substring(11);
                        } else if (item.startsWith("stop-opacity")) {
                           opacity = ParserUtils.parseDoubleProtected(item.substring(13));
                        } else if (item.startsWith("offset")) {
                           offset = PercentParser.parseValue(item.substring(7));
                        }
                     }
               }
            }

            if (color != null) {
               Color colour = Color.web(color, opacity);
               GradientSpec.StopSpec stop = spec.addStop(offset, opacity, colour);
               stops.add(stop);
            }
         }
      }

      return stops;
   }

   protected static class Coord {
      protected final double value;
      protected final boolean isProportional;

      private Coord(double value) {
         this.value = value;
         this.isProportional = false;
      }

      private Coord(double value, boolean isProportional) {
         this.value = value;
         this.isProportional = true;
      }
   }

   public static class StopSpec {
      public final double offset;
      public final double opacity;
      public final Color color;

      private StopSpec(double offset, double opacity, Color color) {
         this.offset = offset;
         this.opacity = opacity;
         this.color = color;
      }
   }
}
