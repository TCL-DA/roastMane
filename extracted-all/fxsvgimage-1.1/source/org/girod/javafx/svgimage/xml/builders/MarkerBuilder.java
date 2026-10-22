package org.girod.javafx.svgimage.xml.builders;

import java.util.Iterator;
import java.util.List;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.shape.ArcTo;
import javafx.scene.shape.CubicCurveTo;
import javafx.scene.shape.HLineTo;
import javafx.scene.shape.Line;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.PathElement;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.QuadCurveTo;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.scene.shape.Shape;
import javafx.scene.shape.VLineTo;
import org.girod.javafx.svgimage.LoaderContext;
import org.girod.javafx.svgimage.xml.parsers.ParserUtils;
import org.girod.javafx.svgimage.xml.parsers.SVGTags;
import org.girod.javafx.svgimage.xml.parsers.TransformUtils;
import org.girod.javafx.svgimage.xml.parsers.XMLNode;
import org.girod.javafx.svgimage.xml.specs.MarkerContext;
import org.girod.javafx.svgimage.xml.specs.MarkerSpec;
import org.girod.javafx.svgimage.xml.specs.Viewbox;
import org.girod.javafx.svgimage.xml.specs.Viewport;

public class MarkerBuilder implements SVGTags {
   private MarkerBuilder() {
   }

   public static void buildMarkers(
      Group parent, Node node, XMLNode xmlNode, MarkerContext markerContext, LoaderContext context, Viewport viewport, boolean visible
   ) {
      if (parent != null && markerContext != null && !markerContext.isEmpty()) {
         String var7 = xmlNode.getName();
         switch (var7) {
            case "polygon":
               buildMarkers(parent, (Polygon)node, markerContext, context, viewport, visible);
               break;
            case "polyline":
               buildMarkers(parent, (Polyline)node, markerContext, context, viewport, visible);
               break;
            case "line":
               buildMarkers(parent, (Line)node, markerContext, context, viewport, visible);
               break;
            case "path":
               buildMarkers(parent, (SVGPath)node, markerContext, context, viewport, visible);
         }
      }
   }

   private static void buildMarkers(Group parent, Polyline polyline, MarkerContext markerContext, LoaderContext context, Viewport viewport, boolean visible) {
      ObservableList<Double> points = polyline.getPoints();
      int countPoints = points.size();
      markerContext.setContextNode(polyline);
      if (markerContext.hasMarkerStart() && countPoints >= 2) {
         MarkerSpec spec = markerContext.getMarkerStart();
         Node markerNode = createMarker(parent, spec, markerContext, context, viewport);
         if (markerNode != null) {
            markerNode.setLayoutX((Double)points.get(0) + getLength(spec, spec.getRefX(), true));
            markerNode.setLayoutY((Double)points.get(1) + getLength(spec, spec.getRefY(), false));
         }
      }

      if (markerContext.hasMarkerEnd() && countPoints >= 4) {
         MarkerSpec spec = markerContext.getMarkerEnd();
         Node markerNode = createMarker(parent, spec, markerContext, context, viewport);
         if (markerNode != null) {
            markerNode.setLayoutX((Double)points.get(points.size() - 2) + getLength(spec, spec.getRefX(), true));
            markerNode.setLayoutY((Double)points.get(points.size() - 1) + getLength(spec, spec.getRefY(), false));
         }
      }

      if (markerContext.hasMarkerMid() && countPoints >= 6) {
         MarkerSpec spec = markerContext.getMarkerMid();

         for (int i = 2; i < countPoints - 2; i += 2) {
            Node markerNode = createMarker(parent, spec, markerContext, context, viewport);
            if (markerNode != null) {
               markerNode.setLayoutX((Double)points.get(i) + getLength(spec, spec.getRefX(), true));
               markerNode.setLayoutY((Double)points.get(i + 1) + getLength(spec, spec.getRefY(), false));
            }
         }
      }
   }

   private static void buildMarkers(Group parent, Polygon polygon, MarkerContext markerContext, LoaderContext context, Viewport viewport, boolean visible) {
      ObservableList<Double> points = polygon.getPoints();
      int countPoints = points.size();
      markerContext.setContextNode(polygon);
      if (markerContext.hasMarkerStart() && countPoints >= 2) {
         MarkerSpec spec = markerContext.getMarkerStart();
         Node markerNode = createMarker(parent, spec, markerContext, context, viewport);
         if (markerNode != null) {
            markerNode.setLayoutX((Double)points.get(0) + getLength(spec, spec.getRefX(), true));
            markerNode.setLayoutY((Double)points.get(1) + getLength(spec, spec.getRefY(), false));
         }
      }

      if (markerContext.hasMarkerEnd() && countPoints >= 4) {
         MarkerSpec spec = markerContext.getMarkerEnd();
         Node markerNode = createMarker(parent, spec, markerContext, context, viewport);
         if (markerNode != null) {
            markerNode.setLayoutX((Double)points.get(points.size() - 2) + getLength(spec, spec.getRefX(), true));
            markerNode.setLayoutY((Double)points.get(points.size() - 1) + getLength(spec, spec.getRefY(), false));
         }
      }

      if (markerContext.hasMarkerMid() && countPoints >= 6) {
         MarkerSpec spec = markerContext.getMarkerMid();

         for (int i = 2; i < countPoints - 2; i += 2) {
            Node markerNode = createMarker(parent, spec, markerContext, context, viewport);
            if (markerNode != null) {
               markerNode.setLayoutX((Double)points.get(i) + getLength(spec, spec.getRefX(), true));
               markerNode.setLayoutY((Double)points.get(i + 1) + getLength(spec, spec.getRefY(), false));
            }
         }
      }
   }

   private static void buildMarkers(Group parent, Line line, MarkerContext markerContext, LoaderContext context, Viewport viewport, boolean visible) {
      markerContext.setContextNode(line);
      if (markerContext.hasMarkerStart()) {
         MarkerSpec spec = markerContext.getMarkerStart();
         Node markerNode = createMarker(parent, spec, markerContext, context, viewport);
         if (markerNode != null) {
            markerNode.setLayoutX(line.getStartX() + getLength(spec, spec.getRefX(), true));
            markerNode.setLayoutY(line.getStartY() + getLength(spec, spec.getRefY(), false));
         }
      }

      if (markerContext.hasMarkerEnd()) {
         MarkerSpec spec = markerContext.getMarkerEnd();
         Node markerNode = createMarker(parent, spec, markerContext, context, viewport);
         if (markerNode != null) {
            markerNode.setLayoutX(line.getEndX() + getLength(spec, spec.getRefX(), true));
            markerNode.setLayoutY(line.getEndY() + getLength(spec, spec.getRefY(), false));
         }
      }
   }

   private static double getLength(MarkerSpec spec, double length, boolean isWidth) {
      return !spec.hasViewbox() ? length : spec.getViewbox().scaleValue(isWidth, length);
   }

   private static void buildMarkers(Group parent, SVGPath svgPath, MarkerContext markerContext, LoaderContext context, Viewport viewport, boolean visible) {
      markerContext.setContextNode(svgPath);
      Path path = getPath(svgPath);
      if (markerContext.hasMarkerStart()) {
         MarkerSpec spec = markerContext.getMarkerStart();
         Node markerNode = createMarker(parent, spec, markerContext, context, viewport);
         if (markerNode != null) {
            MoveTo start = getStart(path);
            markerNode.setLayoutX(start.getX() + getLength(spec, spec.getRefX(), true));
            markerNode.setLayoutY(start.getY() + getLength(spec, spec.getRefY(), false));
         }
      }

      if (markerContext.hasMarkerEnd()) {
         MarkerSpec spec = markerContext.getMarkerEnd();
         Node markerNode = createMarker(parent, spec, markerContext, context, viewport);
         if (markerNode != null) {
            MoveTo end = getEnd(path);
            markerNode.setLayoutX(end.getX() + getLength(spec, spec.getRefX(), true));
            markerNode.setLayoutY(end.getY() + getLength(spec, spec.getRefY(), false));
         }
      }
   }

   private static MoveTo getEnd(Path path) {
      double x = 0.0;
      double y = 0.0;

      for (PathElement elt : path.getElements()) {
         if (elt instanceof MoveTo) {
            MoveTo moveTo = (MoveTo)elt;
            x = moveTo.getX();
            y = moveTo.getY();
         } else if (elt instanceof LineTo) {
            LineTo lineTo = (LineTo)elt;
            x = lineTo.getX();
            y = lineTo.getY();
         } else if (elt instanceof HLineTo) {
            HLineTo lineTo = (HLineTo)elt;
            x = lineTo.getX();
         } else if (elt instanceof VLineTo) {
            VLineTo lineTo = (VLineTo)elt;
            y = lineTo.getY();
         } else if (elt instanceof ArcTo) {
            ArcTo arcTo = (ArcTo)elt;
            x = arcTo.getX();
            y = arcTo.getY();
         } else if (elt instanceof CubicCurveTo) {
            CubicCurveTo curveTo = (CubicCurveTo)elt;
            x = curveTo.getX();
            y = curveTo.getY();
         } else if (elt instanceof QuadCurveTo) {
            QuadCurveTo curveTo = (QuadCurveTo)elt;
            x = curveTo.getX();
            y = curveTo.getY();
         }
      }

      return new MoveTo(x, y);
   }

   private static MoveTo getStart(Path path) {
      Iterator<PathElement> it = path.getElements().iterator();
      if (it.hasNext()) {
         PathElement elt = it.next();
         return elt instanceof MoveTo ? (MoveTo)elt : new MoveTo(0.0, 0.0);
      } else {
         return new MoveTo(0.0, 0.0);
      }
   }

   private static Path getPath(SVGPath svgPath) {
      return (Path)Shape.subtract(svgPath, new Rectangle(0.0, 0.0));
   }

   private static Node createMarker(Group parent, MarkerSpec spec, MarkerContext markerContext, LoaderContext context, Viewport viewport) {
      XMLNode xmlNode = spec.getXMLNode();
      Group group = new Group();
      Viewbox viewbox = spec.getViewbox();

      for (XMLNode childNode : xmlNode.getChildren()) {
         List<? extends Node> nodes = null;
         String name = childNode.getName();
         switch (name) {
            case "rect": {
               Node node = SVGShapeBuilder.buildRect(childNode, null, viewbox, viewport);
               nodes = ParserUtils.createNodeList(node);
               break;
            }
            case "circle": {
               Node node = SVGShapeBuilder.buildCircle(childNode, null, viewbox, viewport);
               nodes = ParserUtils.createNodeList(node);
               break;
            }
            case "ellipse": {
               Node node = SVGShapeBuilder.buildEllipse(childNode, null, viewbox, viewport);
               nodes = ParserUtils.createNodeList(node);
               break;
            }
            case "path":
               boolean hasFill = SVGStyleBuilder.hasFill(childNode);
               nodes = SVGShapeBuilder.buildPath(childNode, null, viewbox, viewport, hasFill);
               break;
            case "polygon": {
               Node node = SVGShapeBuilder.buildPolygon(childNode, null, viewbox, viewport);
               nodes = ParserUtils.createNodeList(node);
               break;
            }
            case "line": {
               Node node = SVGShapeBuilder.buildLine(childNode, null, viewbox, viewport);
               nodes = ParserUtils.createNodeList(node);
               break;
            }
            case "polyline": {
               Node node = SVGShapeBuilder.buildPolyline(childNode, null, viewbox, viewport);
               nodes = ParserUtils.createNodeList(node);
            }
         }

         if (nodes != null) {
            for (Node node : nodes) {
               group.getChildren().add(node);
               SVGStyleBuilder.setNodeStyle(markerContext, node, childNode, context, viewport);
               ParserUtils.setOpacity(node, xmlNode);
               TransformUtils.setTransforms(node, childNode, viewport);
            }
         }
      }

      if (!group.getChildren().isEmpty()) {
         parent.getChildren().add(group);
      } else {
         group = null;
      }

      return group;
   }

   public static MarkerContext createMarkerContext(XMLNode xmlNode, LoaderContext context) {
      if (!context.hasMarkers()) {
         return null;
      } else {
         String var2 = xmlNode.getName();
         switch (var2) {
            case "polygon":
            case "polyline":
            case "line":
            case "path":
               return getMarkerContext(xmlNode, context);
            default:
               return null;
         }
      }
   }

   private static MarkerContext getMarkerContext(XMLNode node, LoaderContext context) {
      MarkerSpec startMarker = null;
      if (node.hasAttribute("marker-start")) {
         String id = getMarkerID(node, "marker-start");
         if (context.hasMarker(id)) {
            startMarker = context.getMarker(id);
         }
      }

      MarkerSpec midMarker = null;
      if (node.hasAttribute("marker-mid")) {
         String id = getMarkerID(node, "marker-mid");
         if (context.hasMarker(id)) {
            midMarker = context.getMarker(id);
         }
      }

      MarkerSpec endMarker = null;
      if (node.hasAttribute("marker-end")) {
         String id = getMarkerID(node, "marker-end");
         if (context.hasMarker(id)) {
            endMarker = context.getMarker(id);
         }
      }

      MarkerContext markerContext = new MarkerContext();
      markerContext.setMarkerStart(startMarker);
      markerContext.setMarkerMid(midMarker);
      markerContext.setMarkerEnd(endMarker);
      return markerContext;
   }

   private static String getMarkerID(XMLNode node, String key) {
      String value = node.getAttributeValue(key);
      return ParserUtils.getURL(value);
   }
}
