package org.girod.javafx.svgimage.xml.builders;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.effect.Light.Distant;
import javafx.scene.effect.Light.Point;
import javafx.scene.effect.Light.Spot;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.FillRule;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import org.girod.javafx.svgimage.GlobalConfig;
import org.girod.javafx.svgimage.LoaderContext;
import org.girod.javafx.svgimage.xml.parsers.ParserUtils;
import org.girod.javafx.svgimage.xml.parsers.PathParser;
import org.girod.javafx.svgimage.xml.parsers.SVGTags;
import org.girod.javafx.svgimage.xml.parsers.TransformUtils;
import org.girod.javafx.svgimage.xml.parsers.XMLNode;
import org.girod.javafx.svgimage.xml.specs.ExtendedFontPosture;
import org.girod.javafx.svgimage.xml.specs.FilterSpec;
import org.girod.javafx.svgimage.xml.specs.GradientSpec;
import org.girod.javafx.svgimage.xml.specs.LinearGradientSpec;
import org.girod.javafx.svgimage.xml.specs.MarkerContext;
import org.girod.javafx.svgimage.xml.specs.RadialGradientSpec;
import org.girod.javafx.svgimage.xml.specs.SpanGroup;
import org.girod.javafx.svgimage.xml.specs.Viewbox;
import org.girod.javafx.svgimage.xml.specs.Viewport;

public class SVGShapeBuilder implements SVGTags {
   private static final Pattern NUMBER = Pattern.compile("\\d+");

   private SVGShapeBuilder() {
   }

   public static Shape buildRect(XMLNode xmlNode, Bounds bounds, Viewbox viewbox, Viewport viewport) {
      double x = 0.0;
      double y = 0.0;
      if (xmlNode.hasAttribute("x")) {
         x = xmlNode.getPositionValue("x", true, bounds, viewport);
      }

      if (xmlNode.hasAttribute("y")) {
         y = xmlNode.getPositionValue("y", false, viewport);
      }

      double width = xmlNode.getLengthValue("width", true, bounds, viewport, 0.0);
      double height = xmlNode.getLengthValue("height", false, bounds, viewport, 0.0);
      if (viewbox != null) {
         x = viewbox.scaleValue(true, x);
         y = viewbox.scaleValue(false, y);
         width = viewbox.scaleValue(true, width);
         height = viewbox.scaleValue(false, height);
      }

      Rectangle rect = new Rectangle(x, y, width, height);
      if (xmlNode.hasAttribute("rx")) {
         double rx = 2.0 * xmlNode.getLengthValue("rx", true, bounds, viewport, 0.0);
         rect.setArcWidth(rx);
      }

      if (xmlNode.hasAttribute("ry")) {
         double ry = 2.0 * xmlNode.getLengthValue("ry", false, bounds, viewport, 0.0);
         rect.setArcHeight(ry);
      }

      if (viewbox != null) {
         viewbox.scaleNode(rect);
      }

      return rect;
   }

   public static Shape buildCircle(XMLNode xmlNode, Bounds bounds, Viewbox viewbox, Viewport viewport) {
      double cx = xmlNode.getPositionValue("cx", true, bounds, viewport, 0.0);
      double cy = xmlNode.getPositionValue("cy", false, bounds, viewport, 0.0);
      double r = xmlNode.getLengthValue("r", true, bounds, viewport, 0.0);
      if (viewbox != null) {
         cx = viewbox.scaleValue(true, cx);
         cy = viewbox.scaleValue(false, cy);
         r = viewbox.scaleValue(true, r);
      }

      Circle circle = new Circle(cx, cy, r);
      if (viewbox != null) {
         viewbox.scaleNode(circle);
      }

      return circle;
   }

   public static void applyFontOblique(Text text) {
      String style = text.getStyle();
      String addStyle = "-fx-font-style: oblique;";
      if (style == null) {
         text.setStyle(addStyle);
      } else {
         style = style + addStyle;
         text.setStyle(style);
      }
   }

   public static void applyTextAnchor(Text text, String value) {
      switch (value) {
         case "start":
            text.setTextAlignment(TextAlignment.LEFT);
            break;
         case "middle": {
            text.setTextAlignment(TextAlignment.CENTER);
            text.applyCss();
            double width = text.getLayoutBounds().getWidth();
            text.setX(text.getX() - width / 2.0);
            break;
         }
         case "end": {
            text.setTextAlignment(TextAlignment.RIGHT);
            text.applyCss();
            double width = text.getLayoutBounds().getWidth();
            text.setX(text.getX() - width);
         }
      }
   }

   public static void applyTextDecoration(Text text, String value) {
      String style = text.getStyle();
      String addStyle = null;
      StringTokenizer tok = new StringTokenizer(value, " ");

      while (tok.hasMoreTokens()) {
         String tk = tok.nextToken().trim();
         if (!tk.isEmpty()) {
            if (tk.equals("underline")) {
               if (addStyle == null) {
                  addStyle = "-fx-underline: true;";
               } else {
                  addStyle = addStyle + "-fx-underline: true;";
               }
            } else if (tk.equals("line-through")) {
               if (addStyle == null) {
                  addStyle = "-fx-strikethrough: true;";
               } else {
                  addStyle = addStyle + "-fx-strikethrough: true;";
               }
            }
         }
      }

      if (addStyle != null) {
         if (style == null) {
            text.setStyle(addStyle);
         } else {
            style = style + addStyle;
            text.setStyle(style);
         }
      }
   }

   public static FontPosture applyFontPosture(Text text, String value) {
      if (value == null) {
         return FontPosture.REGULAR;
      } else if (value.equals("oblique")) {
         String style = text.getStyle();
         String addStyle = "-fx-font-style: oblique;";
         if (style == null) {
            text.setStyle(addStyle);
         } else {
            style = style + addStyle;
            text.setStyle(style);
         }

         return FontPosture.REGULAR;
      } else {
         return getFontPosture(value);
      }
   }

   public static ExtendedFontPosture getExtendedFontPosture(String value) {
      if (value == null) {
         return new ExtendedFontPosture(FontPosture.REGULAR);
      } else if (value.equals("italic")) {
         return new ExtendedFontPosture(FontPosture.ITALIC);
      } else {
         return value.equals("oblique") ? new ExtendedFontPosture(true) : new ExtendedFontPosture(FontPosture.REGULAR);
      }
   }

   public static FontPosture getFontPosture(String value) {
      FontPosture posture = FontPosture.REGULAR;
      if (value == null) {
         return posture;
      } else {
         if (value.equals("italic")) {
            posture = FontPosture.ITALIC;
         }

         return posture;
      }
   }

   public static FontWeight getFontWeight(String value) {
      FontWeight weight = FontWeight.NORMAL;
      if (value == null) {
         return weight;
      } else {
         switch (value) {
            case "bold":
               weight = FontWeight.BOLD;
               break;
            case "bolder":
               weight = FontWeight.EXTRA_BOLD;
               break;
            case "lighter":
               weight = FontWeight.LIGHT;
               break;
            default:
               Matcher m = NUMBER.matcher(value);
               if (m.matches()) {
                  int weightNumber = Integer.parseInt(value);
                  weight = FontWeight.findByWeight(weightNumber);
               }
         }

         return weight;
      }
   }

   public static SpanGroup buildTSpanGroup(XMLNode xmlNode, Bounds bounds, Viewbox viewbox, Viewport viewport) {
      Group group = new Group();
      double x = xmlNode.getLengthValue("x", true, bounds, viewport, 0.0);
      double y = xmlNode.getLengthValue("y", false, bounds, viewport, 0.0);
      if (viewbox != null) {
         x = viewbox.scaleValue(true, x);
         y = viewbox.scaleValue(false, y);
      }

      group.setLayoutX(x);
      group.setLayoutY(y);
      SpanGroup spanGroup = new SpanGroup(group);
      Iterator<XMLNode> it = xmlNode.getChildren().iterator();
      Text previous = null;

      while (it.hasNext()) {
         XMLNode childNode = it.next();
         String name = childNode.getName();
         List<Text> tspans = null;
         switch (name) {
            case "tspan":
               tspans = buildTspan(group, previous, childNode, bounds, viewbox, viewport);
               if (tspans.isEmpty()) {
                  tspans = null;
               } else {
                  previous = tspans.get(tspans.size() - 1);
               }
         }

         if (tspans != null) {
            for (Text text : tspans) {
               group.getChildren().add(text);
               spanGroup.addTSpan(childNode, text);
            }
         }
      }

      return spanGroup;
   }

   public static Text buildText(XMLNode xmlNode, Bounds bounds, Viewbox viewbox, Viewport viewport) {
      boolean hasFamily = xmlNode.hasAttribute("font-family");
      boolean hasSize = xmlNode.hasAttribute("font-size");
      String family = null;
      if (hasFamily) {
         family = xmlNode.getAttributeValue("font-family").replace("'", "");
      }

      double size = 12.0;
      if (hasSize) {
         size = ParserUtils.parseFontSize(xmlNode.getAttributeValue("font-size"));
      }

      size = viewport.scaleLength(size);
      FontWeight weight = getFontWeight(xmlNode.getAttributeValue("font-weight"));
      FontPosture posture = getFontPosture(xmlNode.getAttributeValue("font-style"));
      Font font = Font.font(family, weight, posture, size);
      String cdata = xmlNode.getCDATA();
      if (cdata != null) {
         double x = xmlNode.getPositionValue("x", true, bounds, viewport, 0.0);
         double y = xmlNode.getPositionValue("y", false, bounds, viewport, 0.0);
         if (viewbox != null) {
            x = viewbox.scaleValue(true, x);
            y = viewbox.scaleValue(false, y);
         }

         Text text = new Text(x, y, cdata);
         if (xmlNode.hasAttribute("text-decoration")) {
            applyTextDecoration(text, xmlNode.getAttributeValue("text-decoration"));
         }

         if (xmlNode.hasAttribute("text-anchor")) {
            applyTextAnchor(text, xmlNode.getAttributeValue("text-anchor"));
         }

         if (font != null) {
            text.setFont(font);
         }

         if (viewbox != null) {
            viewbox.scaleNode(text);
         }

         return text;
      } else {
         return null;
      }
   }

   public static List<Text> buildTspan(Group group, Text previous, XMLNode xmlNode, Bounds bounds, Viewbox viewbox, Viewport viewport) {
      List<Text> tspans = new ArrayList<>();
      boolean hasFamily = xmlNode.hasAttribute("font-family");
      boolean hasSize = xmlNode.hasAttribute("font-size");
      String family = null;
      if (hasFamily) {
         family = xmlNode.getAttributeValue("font-family").replace("'", "");
      }

      double size = 12.0;
      if (hasSize) {
         size = ParserUtils.parseFontSize(xmlNode.getAttributeValue("font-size"));
      }

      size = viewport.scaleLength(size);
      FontWeight weight = getFontWeight(xmlNode.getAttributeValue("font-weight"));
      FontPosture posture = getFontPosture(xmlNode.getAttributeValue("font-style"));
      Font font = Font.font(family, weight, posture, size);
      String cdata = xmlNode.getCDATA();
      if (cdata != null) {
         double x = 0.0;
         double y = 0.0;
         if (xmlNode.hasAttribute("dx")) {
            x = xmlNode.getPositionValue("dx", true, bounds, viewport, 0.0);
         } else if (xmlNode.hasAttribute("x")) {
            double _x = xmlNode.getPositionValue("x", true, bounds, viewport, 0.0);
            if (viewbox != null) {
               _x = viewbox.scaleValue(true, _x);
            }

            x = _x - group.getLayoutX();
         } else if (previous != null) {
            x = previous.getLayoutX() + previous.getLayoutBounds().getWidth();
         }

         if (xmlNode.hasAttribute("dy")) {
            y = xmlNode.getPositionValue("dy", false, bounds, viewport, 0.0);
         } else if (xmlNode.hasAttribute("y")) {
            double _y = xmlNode.getPositionValue("y", true, bounds, viewport, 0.0);
            if (viewbox != null) {
               _y = viewbox.scaleValue(false, _y);
            }

            y = _y - group.getLayoutY();
         }

         Text text = new Text(x, y, cdata);
         tspans.add(text);
         if (xmlNode.hasAttribute("text-decoration")) {
            applyTextDecoration(text, xmlNode.getAttributeValue("text-decoration"));
         }

         if (font != null) {
            text.setFont(font);
         }

         return tspans;
      } else {
         return null;
      }
   }

   private static List<Stop> convertStops(List<GradientSpec.StopSpec> specstops) {
      List<Stop> stops = new ArrayList<>();

      for (GradientSpec.StopSpec theStop : specstops) {
         Stop stop = new Stop(theStop.offset, theStop.color);
         stops.add(stop);
      }

      return stops;
   }

   public static void buildRadialGradient(Map<String, GradientSpec> gradientSpecs, Map<String, Paint> gradients, XMLNode xmlNode, Viewport viewport) {
      if (xmlNode.hasAttribute("id")) {
         String id = xmlNode.getAttributeValue("id");
         String href = null;
         if (xmlNode.hasAttribute("xlink:href")) {
            href = xmlNode.getAttributeValue("xlink:href");
            if (href.startsWith("#")) {
               href = href.substring(1);
            } else {
               href = null;
            }
         }

         RadialGradientSpec spec = new RadialGradientSpec(xmlNode, href);
         gradientSpecs.put(id, spec);
      }
   }

   private static double getGradientPos(XMLNode xmlNode, String id) {
      String attrvalue = xmlNode.getAttributeValue(id);
      if (attrvalue.endsWith("%") && attrvalue.length() > 1) {
         attrvalue = attrvalue.substring(0, attrvalue.length() - 1);
      }

      return ParserUtils.parseDoubleProtected(attrvalue) / 100.0;
   }

   public static void buildLinearGradient(Map<String, GradientSpec> gradientSpecs, Map<String, Paint> gradients, XMLNode xmlNode, Viewport viewport) {
      if (xmlNode.hasAttribute("id")) {
         String id = xmlNode.getAttributeValue("id");
         String href = null;
         if (xmlNode.hasAttribute("xlink:href")) {
            href = xmlNode.getAttributeValue("xlink:href");
            if (href.startsWith("#")) {
               href = href.substring(1);
            } else {
               href = null;
            }
         }

         LinearGradientSpec spec = new LinearGradientSpec(xmlNode, href);
         gradientSpecs.put(id, spec);
      }
   }

   private static List<Node> createNodeList(Node node) {
      List<Node> list = new ArrayList<>(1);
      list.add(node);
      return list;
   }

   public static List<? extends Node> buildUse(XMLNode xmlNode, LoaderContext context, Bounds bounds, Viewport viewport) {
      String id = null;
      if (xmlNode.hasAttribute("href")) {
         id = xmlNode.getAttributeValue("href");
      } else if (xmlNode.hasAttribute("xlink:href")) {
         id = xmlNode.getAttributeValue("xlink:href");
      }

      if (id != null && id.startsWith("#")) {
         id = id.substring(1);
      }

      if (id != null && context.hasNamedNode(id)) {
         XMLNode namedNode = context.getNamedNode(id);
         List<? extends Node> nodesFromUse = null;
         Viewbox viewbox = null;
         String name = namedNode.getName();
         SpanGroup spanGroup = null;
         if (context.hasSymbol(id)) {
            viewbox = context.getSymbol(id).getViewbox();
         }

         switch (name) {
            case "rect": {
               Node node = buildRect(namedNode, null, viewbox, viewport);
               nodesFromUse = ParserUtils.createNodeList(node);
               break;
            }
            case "circle": {
               Node node = buildCircle(namedNode, null, viewbox, viewport);
               nodesFromUse = ParserUtils.createNodeList(node);
               break;
            }
            case "ellipse": {
               Node node = buildEllipse(namedNode, null, viewbox, viewport);
               nodesFromUse = ParserUtils.createNodeList(node);
               break;
            }
            case "path":
               boolean hasFill = SVGStyleBuilder.hasFill(namedNode);
               nodesFromUse = buildPath(namedNode, null, viewbox, viewport, hasFill);
               break;
            case "polygon": {
               Node node = buildPolygon(namedNode, null, viewbox, viewport);
               nodesFromUse = ParserUtils.createNodeList(node);
               break;
            }
            case "line": {
               Node node = buildLine(namedNode, null, viewbox, viewport);
               nodesFromUse = ParserUtils.createNodeList(node);
               break;
            }
            case "polyline": {
               Node node = buildPolyline(namedNode, null, viewbox, viewport);
               nodesFromUse = ParserUtils.createNodeList(node);
               break;
            }
            case "image": {
               Node node = buildImage(namedNode, context.url, null, viewbox, viewport);
               nodesFromUse = ParserUtils.createNodeList(node);
               break;
            }
            case "g":
            case "symbol": {
               Node node = buildGroupForUse(context, namedNode, viewbox, viewport);
               nodesFromUse = ParserUtils.createNodeList(node);
               break;
            }
            case "text": {
               Node node = buildText(namedNode, null, viewbox, viewport);
               if (node == null) {
                  spanGroup = buildTSpanGroup(namedNode, null, viewbox, viewport);
               }
            }
         }

         if (nodesFromUse != null) {
            for (Node node : nodesFromUse) {
               SVGStyleBuilder.setNodeStyle(node, namedNode, context, viewport);
               if (xmlNode.hasAttribute("x")) {
                  double x = xmlNode.getPositionValue("x", true, viewport);
                  node.setLayoutX(x);
               }

               if (xmlNode.hasAttribute("y")) {
                  double y = xmlNode.getPositionValue("y", true, viewport);
                  node.setLayoutY(y);
               }

               SVGStyleBuilder.setNodeStyle(node, xmlNode, context, viewport);
            }

            return nodesFromUse;
         } else if (spanGroup != null) {
            Map<String, String> theStylesMap = ParserUtils.getStyles(namedNode);
            Iterator<SpanGroup.TSpan> it2 = spanGroup.getSpans().iterator();
            SpanGroup.TSpan previous = null;

            while (it2.hasNext()) {
               SpanGroup.TSpan tspan = it2.next();
               Text tspanText = tspan.text;
               String theStyles = ParserUtils.mergeStyles(theStylesMap, tspan.node);
               tspan.node.addAttribute("style", theStyles);
               addStyles(context, null, tspanText, tspan.node, viewport);
               if (tspan.node.hasAttribute("baseline-shift")) {
                  String shiftValue = tspan.node.getAttributeValue("baseline-shift");
                  ParserUtils.setBaselineShift(tspanText, shiftValue);
               }

               if (!ParserUtils.hasXPosition(tspan.node) && previous != null) {
                  double width = previous.text.getLayoutBounds().getWidth();
                  tspanText.setLayoutX(width + previous.text.getLayoutX());
               }

               previous = tspan;
            }

            return ParserUtils.createNodeList(spanGroup.getTextGroup());
         } else {
            return null;
         }
      } else {
         return null;
      }
   }

   private static Group buildGroupForUse(LoaderContext context, XMLNode xmlNode, Viewbox viewbox, Viewport viewport) {
      Group group = new Group();

      for (XMLNode childNode : xmlNode.getChildren()) {
         List<? extends Node> nodes = null;
         SpanGroup spanGroup = null;
         String name = childNode.getName();
         switch (name) {
            case "rect": {
               Node node = buildRect(childNode, null, viewbox, viewport);
               nodes = ParserUtils.createNodeList(node);
               break;
            }
            case "circle": {
               Node node = buildCircle(childNode, null, viewbox, viewport);
               nodes = ParserUtils.createNodeList(node);
               break;
            }
            case "ellipse": {
               Node node = buildEllipse(childNode, null, viewbox, viewport);
               nodes = ParserUtils.createNodeList(node);
               break;
            }
            case "path":
               boolean hasFill = SVGStyleBuilder.hasFill(childNode);
               nodes = buildPath(childNode, null, viewbox, viewport, hasFill);
               break;
            case "polygon": {
               Node node = buildPolygon(childNode, null, viewbox, viewport);
               nodes = ParserUtils.createNodeList(node);
               break;
            }
            case "line": {
               Node node = buildLine(childNode, null, viewbox, viewport);
               nodes = ParserUtils.createNodeList(node);
               break;
            }
            case "polyline": {
               Node node = buildPolyline(childNode, null, viewbox, viewport);
               nodes = ParserUtils.createNodeList(node);
               break;
            }
            case "image": {
               Node node = buildImage(childNode, context.url, null, viewbox, viewport);
               nodes = ParserUtils.createNodeList(node);
               break;
            }
            case "text": {
               Node node = buildText(childNode, null, viewbox, viewport);
               if (node == null) {
                  spanGroup = buildTSpanGroup(childNode, null, viewbox, viewport);
               }
               break;
            }
            case "g": {
               Node node = buildGroupForUse(context, childNode, viewbox, viewport);
               nodes = ParserUtils.createNodeList(node);
            }
         }

         if (nodes != null) {
            for (Node node : nodes) {
               addStyles(context, group, node, childNode, viewport);
               group.getChildren().add(node);
            }
         } else if (spanGroup != null) {
            Map<String, String> theStylesMap = ParserUtils.getStyles(childNode);
            Iterator<SpanGroup.TSpan> it2 = spanGroup.getSpans().iterator();
            SpanGroup.TSpan previous = null;

            while (it2.hasNext()) {
               SpanGroup.TSpan tspan = it2.next();
               Text tspanText = tspan.text;
               String theStyles = ParserUtils.mergeStyles(theStylesMap, tspan.node);
               tspan.node.addAttribute("style", theStyles);
               addStyles(context, group, tspanText, tspan.node, viewport);
               if (tspan.node.hasAttribute("baseline-shift")) {
                  String shiftValue = tspan.node.getAttributeValue("baseline-shift");
                  ParserUtils.setBaselineShift(tspanText, shiftValue);
               }

               if (!ParserUtils.hasXPosition(tspan.node) && previous != null) {
                  double width = previous.text.getLayoutBounds().getWidth();
                  tspanText.setLayoutX(width + previous.text.getLayoutX());
               }

               previous = tspan;
            }

            group.getChildren().add(spanGroup.getTextGroup());
         }
      }

      return group;
   }

   private static void addStyles(LoaderContext context, Group group, Node node, XMLNode xmlNode, Viewport viewport) {
      MarkerContext markerContext = SVGStyleBuilder.setNodeStyle(node, xmlNode, context, viewport);
      ParserUtils.setOpacity(node, xmlNode);
      boolean visible = ParserUtils.setVisibility(node, xmlNode);
      TransformUtils.setTransforms(node, xmlNode, viewport);
      if (markerContext != null) {
         MarkerBuilder.buildMarkers(group, node, xmlNode, markerContext, context, viewport, visible);
      }
   }

   public static ImageView buildImage(XMLNode xmlNode, URL url, Bounds bounds, Viewbox viewbox, Viewport viewport) {
      double width = xmlNode.getLengthValue("width", true, bounds, viewport, 0.0);
      double height = xmlNode.getLengthValue("height", false, bounds, viewport, 0.0);
      double x = xmlNode.getLengthValue("x", true, bounds, viewport, 0.0);
      double y = xmlNode.getLengthValue("y", false, bounds, viewport, 0.0);
      String hrefAttribute = xmlNode.getAttributeValue("href");
      if (hrefAttribute == null) {
         hrefAttribute = xmlNode.getAttributeValue("xlink:href");
      }

      if (hrefAttribute != null && viewbox != null) {
         width = viewbox.scaleValue(true, width);
         height = viewbox.scaleValue(false, height);
         x = viewbox.scaleValue(true, x);
         y = viewbox.scaleValue(false, y);
      }

      Image image = ParserUtils.getImage(url, hrefAttribute, width, height);
      if (image != null) {
         ImageView view = new ImageView(image);
         view.setX(x);
         view.setY(y);
         if (viewbox != null) {
            viewbox.scaleNode(view);
         }

         return view;
      } else {
         return null;
      }
   }

   public static Shape buildEllipse(XMLNode xmlNode, Bounds bounds, Viewbox viewbox, Viewport viewport) {
      double cx = xmlNode.getPositionValue("cx", true, bounds, viewport, 0.0);
      double cy = xmlNode.getPositionValue("cy", false, bounds, viewport, 0.0);
      double rx = xmlNode.getLengthValue("rx", true, bounds, viewport, 0.0);
      double ry = xmlNode.getLengthValue("ry", false, bounds, viewport, 0.0);
      if (viewbox != null) {
         cx = viewbox.scaleValue(true, cx);
         cy = viewbox.scaleValue(false, cy);
         rx = viewbox.scaleValue(true, rx);
         ry = viewbox.scaleValue(true, ry);
      }

      Ellipse ellipse = new Ellipse(cx, cy, rx, ry);
      if (viewbox != null) {
         viewbox.scaleNode(ellipse);
      }

      return ellipse;
   }

   public static List<SVGPath> buildPath(XMLNode xmlNode, Bounds bounds, Viewbox viewbox, Viewport viewport, boolean hasFill) {
      String content = xmlNode.getAttributeValue("d");
      FillRule rule = ParserUtils.getFillRule(xmlNode);
      content = content.replace('−', '-');
      PathParser pathParser = new PathParser();
      List<SVGPath> list = pathParser.parsePathContent(content, viewport, hasFill);
      if (list != null) {
         for (SVGPath path : list) {
            if (rule != null) {
               path.setFillRule(rule);
            }

            if (viewbox != null) {
               viewbox.scaleNode(path);
            }
         }
      }

      return list;
   }

   public static Polygon buildPolygon(XMLNode xmlNode, Bounds bounds, Viewbox viewbox, Viewport viewport) {
      String pointsAttribute = xmlNode.getAttributeValue("points");
      Polygon polygon = new Polygon();
      StringTokenizer tokenizer = new StringTokenizer(pointsAttribute, " ,");

      for (boolean isX = true; tokenizer.hasMoreTokens(); isX = !isX) {
         String point = tokenizer.nextToken();
         if (isX) {
            double x = ParserUtils.parsePositionValue(point, true, bounds, viewport);
            if (viewbox != null) {
               x = viewbox.scaleValue(true, x);
            }

            polygon.getPoints().add(x);
         } else {
            double y = ParserUtils.parsePositionValue(point, false, bounds, viewport);
            if (viewbox != null) {
               y = viewbox.scaleValue(false, y);
            }

            polygon.getPoints().add(y);
         }
      }

      if (viewbox != null) {
         viewbox.scaleNode(polygon);
      }

      return polygon;
   }

   public static Line buildLine(XMLNode xmlNode, Bounds bounds, Viewbox viewbox, Viewport viewport) {
      double x1 = 0.0;
      double y1 = 0.0;
      double x2 = 0.0;
      double y2 = 0.0;
      if (xmlNode.hasAttribute("x1")) {
         x1 = xmlNode.getPositionValue("x1", true, bounds, viewport);
      }

      if (xmlNode.hasAttribute("y1")) {
         y1 = xmlNode.getPositionValue("y1", false, bounds, viewport);
      }

      if (xmlNode.hasAttribute("x2")) {
         x2 = xmlNode.getPositionValue("x2", true, bounds, viewport);
      }

      if (xmlNode.hasAttribute("y2")) {
         y2 = xmlNode.getPositionValue("y2", false, bounds, viewport);
      }

      if (viewbox != null) {
         x1 = viewbox.scaleValue(true, x1);
         y1 = viewbox.scaleValue(false, y1);
         x2 = viewbox.scaleValue(true, x2);
         y2 = viewbox.scaleValue(false, y2);
      }

      Line line = new Line(x1, y1, x2, y2);
      if (viewbox != null) {
         viewbox.scaleNode(line);
      }

      return line;
   }

   public static Polyline buildPolyline(XMLNode xmlNode, Bounds bounds, Viewbox viewbox, Viewport viewport) {
      Polyline polyline = new Polyline();
      String pointsAttribute = xmlNode.getAttributeValue("points");
      StringTokenizer tokenizer = new StringTokenizer(pointsAttribute, " ,");

      for (boolean isX = true; tokenizer.hasMoreTokens(); isX = !isX) {
         String point = tokenizer.nextToken();
         if (isX) {
            double x = ParserUtils.parsePositionValue(point, true, bounds, viewport);
            if (viewbox != null) {
               x = viewbox.scaleValue(true, x);
            }

            polyline.getPoints().add(x);
         } else {
            double y = ParserUtils.parsePositionValue(point, false, bounds, viewport);
            if (viewbox != null) {
               y = viewbox.scaleValue(false, y);
            }

            polyline.getPoints().add(y);
         }
      }

      return polyline;
   }

   public static void buildFEGaussianBlur(FilterSpec spec, XMLNode node) {
      double stdDeviation = 0.0;
      String resultId = node.getAttributeValue("result");
      if (node.hasAttribute("stdDeviation")) {
         String stdDevS = node.getAttributeValue("stdDeviation");
         stdDevS = ParserUtils.parseFirstArgument(stdDevS);
         stdDeviation = ParserUtils.parseDoubleProtected(stdDevS);
      }

      FilterSpec.FEGaussianBlur effect = new FilterSpec.FEGaussianBlur(resultId, stdDeviation);
      if (node.hasAttribute("in")) {
         effect.setIn(node.getAttributeValue("in"));
      }

      spec.addEffect(resultId, effect);
   }

   public static void buildFEDropShadow(FilterSpec spec, XMLNode node, Viewport viewport) {
      double dx = node.getLengthValue("dx", true, viewport);
      double dy = node.getLengthValue("dy", true, viewport);
      double opacity = 1.0;
      double stdDeviation = 0.0;
      Color col = Color.BLACK;
      String resultId = node.getAttributeValue("result");
      if (node.hasAttribute("flood-opacity")) {
         opacity = node.getDoubleValue("flood-opacity", 1.0);
      }

      if (node.hasAttribute("stdDeviation")) {
         String stdDevS = node.getAttributeValue("stdDeviation");
         stdDevS = ParserUtils.parseFirstArgument(stdDevS);
         stdDeviation = ParserUtils.parseDoubleProtected(stdDevS);
      }

      if (node.hasAttribute("flood-color")) {
         String colorS = node.getAttributeValue("flood-color");
         col = ParserUtils.getColor(colorS, opacity);
      }

      FilterSpec.FEDropShadow effect = new FilterSpec.FEDropShadow(resultId, dx, dy, stdDeviation, col);
      if (node.hasAttribute("in")) {
         effect.setIn(node.getAttributeValue("in"));
      }

      spec.addEffect(resultId, effect);
   }

   public static void buildFEFlood(FilterSpec spec, XMLNode node, Viewport viewport) {
      double x = node.getLengthValue("x", true, viewport);
      double y = node.getLengthValue("y", true, viewport);
      double width = node.getLengthValue("width", true, viewport);
      double height = node.getLengthValue("height", true, viewport);
      double opacity = 1.0;
      Color col = Color.BLACK;
      String resultId = node.getAttributeValue("result");
      if (node.hasAttribute("flood-opacity")) {
         opacity = node.getDoubleValue("flood-opacity", 1.0);
      }

      if (node.hasAttribute("flood-color")) {
         String colorS = node.getAttributeValue("flood-color");
         col = ParserUtils.getColor(colorS, opacity);
      }

      FilterSpec.FEFlood effect = new FilterSpec.FEFlood(resultId, x, y, width, height, col);
      if (node.hasAttribute("in")) {
         effect.setIn(node.getAttributeValue("in"));
      }

      spec.addEffect(resultId, effect);
   }

   public static void buildFEOffset(FilterSpec spec, XMLNode node, Viewport viewport) {
      double dx = node.getLengthValue("dx", true, viewport);
      double dy = node.getLengthValue("dy", true, viewport);
      String resultId = node.getAttributeValue("result");
      FilterSpec.FEOffset effect = new FilterSpec.FEOffset(resultId, dx, dy);
      if (node.hasAttribute("in")) {
         effect.setIn(node.getAttributeValue("in"));
      }

      spec.addEffect(resultId, effect);
   }

   public static void buildFEImage(FilterSpec spec, URL url, XMLNode node, Viewport viewport) {
      double x = node.getLengthValue("x", true, viewport);
      double y = node.getLengthValue("y", true, viewport);
      double width = node.getLengthValue("width", true, viewport);
      double height = node.getLengthValue("height", true, viewport);
      String hrefAttribute = node.getAttributeValue("xlink:href");
      String resultId = node.getAttributeValue("result");
      Image image = null;
      URL imageUrl = null;

      try {
         imageUrl = new URL(hrefAttribute);
      } catch (MalformedURLException var19) {
         try {
            imageUrl = new URL(url, hrefAttribute);
         } catch (MalformedURLException var18) {
            GlobalConfig.getInstance().handleParsingError("URL " + hrefAttribute + " is not well formed");
         }
      }

      if (imageUrl != null) {
         image = new Image(imageUrl.toString(), width, height, true, true);
      }

      FilterSpec.FEImage effect = new FilterSpec.FEImage(resultId, x, y, image);
      spec.addEffect(resultId, effect);
   }

   public static void buildFESpecularLighting(FilterSpec spec, XMLNode node, Viewport viewport) {
      XMLNode child = node.getFirstChild();
      if (child != null) {
         String var4 = child.getName();
         switch (var4) {
            case "feDistantLight":
               double surfaceScalexx = node.getDoubleValue("surfaceScale", 1.5);
               double specularConstantxx = node.getDoubleValue("specularConstant", 0.3);
               double specularExponentxx = node.getDoubleValue("specularExponent", 20.0);
               Color colxx = null;
               if (node.hasAttribute("lighting-color")) {
                  String colorS = node.getAttributeValue("lighting-color");
                  colxx = ParserUtils.getColor(colorS);
               }

               double azimuth = child.getDoubleValue("azimuth");
               double elevation = child.getDoubleValue("elevation");
               Distant light = new Distant(azimuth, elevation, colxx);
               String resultId = node.getAttributeValue("result");
               FilterSpec.FESpecularLighting effect = new FilterSpec.FESpecularLighting(resultId, specularConstantxx, specularExponentxx, surfaceScalexx, light);
               if (node.hasAttribute("in")) {
                  effect.setIn(node.getAttributeValue("in"));
               }

               spec.addEffect(resultId, effect);
               break;
            case "fePointLight":
               double surfaceScalex = node.getDoubleValue("surfaceScale", 1.5);
               double specularConstantx = node.getDoubleValue("specularConstant", 0.3);
               double specularExponentx = node.getDoubleValue("specularExponent", 20.0);
               Color colx = null;
               if (node.hasAttribute("lighting-color")) {
                  String colorS = node.getAttributeValue("lighting-color");
                  colx = ParserUtils.getColor(colorS);
               }

               double x = child.getLengthValue("x", true, viewport);
               double y = child.getLengthValue("y", true, viewport);
               double z = child.getLengthValue("z", true, null);
               Point light = new Point(x, y, z, colx);
               String resultId = node.getAttributeValue("result");
               FilterSpec.FESpecularLighting effect = new FilterSpec.FESpecularLighting(resultId, specularConstantx, specularExponentx, surfaceScalex, light);
               if (node.hasAttribute("in")) {
                  effect.setIn(node.getAttributeValue("in"));
               }

               spec.addEffect(resultId, effect);
               break;
            case "feSpotLight":
               double surfaceScale = node.getDoubleValue("surfaceScale", 1.5);
               double specularConstant = node.getDoubleValue("specularConstant", 0.3);
               double specularExponent = node.getDoubleValue("specularExponent", 20.0);
               Color col = null;
               if (node.hasAttribute("lighting-color")) {
                  String colorS = node.getAttributeValue("lighting-color");
                  col = ParserUtils.getColor(colorS);
               }

               double x = child.getLengthValue("x", true, viewport);
               double y = child.getLengthValue("y", false, viewport);
               double z = child.getLengthValue("z", true, viewport);
               double pointAtX = child.getLengthValue("pointsAtX", true, viewport);
               double pointAtY = child.getLengthValue("pointsAtY", false, viewport);
               double pointAtZ = child.getLengthValue("pointsAtZ", true, viewport);
               Spot light = new Spot(x, y, z, specularExponent, col);
               light.setPointsAtX(pointAtX);
               light.setPointsAtY(pointAtY);
               light.setPointsAtZ(pointAtZ);
               String resultId = node.getAttributeValue("result");
               FilterSpec.FESpecularLighting effect = new FilterSpec.FESpecularLighting(resultId, specularConstant, specularExponent, surfaceScale, light);
               if (node.hasAttribute("in")) {
                  effect.setIn(node.getAttributeValue("in"));
               }

               spec.addEffect(resultId, effect);
         }
      }
   }

   public static void buildFEDiffuseLighting(FilterSpec spec, XMLNode node, Viewport viewport) {
      XMLNode child = node.getFirstChild();
      if (child != null) {
         String var4 = child.getName();
         switch (var4) {
            case "feDistantLight":
               double diffuseConstantxx = node.getDoubleValue("diffuseConstant", 0.3);
               Color colxx = null;
               if (node.hasAttribute("lighting-color")) {
                  String colorS = node.getAttributeValue("lighting-color");
                  colxx = ParserUtils.getColor(colorS);
               }

               double azimuth = child.getDoubleValue("azimuth");
               double elevation = child.getDoubleValue("elevation");
               Distant light = new Distant(azimuth, elevation, colxx);
               String resultId = node.getAttributeValue("result");
               FilterSpec.FEDiffuseLighting effect = new FilterSpec.FEDiffuseLighting(resultId, diffuseConstantxx, light);
               if (node.hasAttribute("in")) {
                  effect.setIn(node.getAttributeValue("in"));
               }

               spec.addEffect(resultId, effect);
               break;
            case "fePointLight":
               double diffuseConstantx = node.getDoubleValue("diffuseConstant", 0.3);
               Color colx = null;
               if (node.hasAttribute("lighting-color")) {
                  String colorS = node.getAttributeValue("lighting-color");
                  colx = ParserUtils.getColor(colorS);
               }

               double x = child.getLengthValue("x", true, viewport);
               double y = child.getLengthValue("y", true, viewport);
               double z = child.getLengthValue("z", true, null);
               Point light = new Point(x, y, z, colx);
               String resultId = node.getAttributeValue("result");
               FilterSpec.FEDiffuseLighting effect = new FilterSpec.FEDiffuseLighting(resultId, diffuseConstantx, light);
               if (node.hasAttribute("in")) {
                  effect.setIn(node.getAttributeValue("in"));
               }

               spec.addEffect(resultId, effect);
               break;
            case "feSpotLight":
               double diffuseConstant = node.getDoubleValue("diffuseConstant", 0.3);
               double specularExponent = node.getDoubleValue("specularExponent", 20.0);
               Color col = null;
               if (node.hasAttribute("lighting-color")) {
                  String colorS = node.getAttributeValue("lighting-color");
                  col = ParserUtils.getColor(colorS);
               }

               double x = child.getLengthValue("x", true, viewport);
               double y = child.getLengthValue("y", false, viewport);
               double z = child.getLengthValue("z", true, viewport);
               double pointAtX = child.getLengthValue("pointsAtX", true, viewport);
               double pointAtY = child.getLengthValue("pointsAtY", false, viewport);
               double pointAtZ = child.getLengthValue("pointsAtZ", true, viewport);
               Spot light = new Spot(x, y, z, specularExponent, col);
               light.setPointsAtX(pointAtX);
               light.setPointsAtY(pointAtY);
               light.setPointsAtZ(pointAtZ);
               String resultId = node.getAttributeValue("result");
               FilterSpec.FEDiffuseLighting effect = new FilterSpec.FEDiffuseLighting(resultId, diffuseConstant, light);
               if (node.hasAttribute("in")) {
                  effect.setIn(node.getAttributeValue("in"));
               }

               spec.addEffect(resultId, effect);
         }
      }
   }

   public static void buildFEComposite(FilterSpec spec, XMLNode node) {
      String resultId = node.getAttributeValue("result");
      String in = node.getAttributeValue("in");
      String in2 = node.getAttributeValue("in2");
      String operator = node.getAttributeValue("operator");
      short type = 0;
      switch (operator) {
         case "over":
            type = 0;
            break;
         case "in":
            type = 1;
            break;
         case "out":
            type = 2;
            break;
         case "atop":
            type = 3;
            break;
         case "xor":
            type = 4;
            break;
         case "arithmetic":
            type = 5;
      }

      FilterSpec.FEComposite effect = new FilterSpec.FEComposite(resultId, type, in, in2);
      spec.addEffect(resultId, effect);
   }

   public static void buildFEMerge(FilterSpec spec, XMLNode node) {
      String resultId = node.getAttributeValue("result");
      FilterSpec.FEMerge effect = new FilterSpec.FEMerge(resultId);
      spec.addEffect(resultId, effect);

      for (XMLNode child : node.getChildren()) {
         if (child.getName().equals("feMergeNode")) {
            String in = child.getAttributeValue("in");
            if (in != null) {
               effect.addMergeNode(in);
            }
         }
      }
   }
}
