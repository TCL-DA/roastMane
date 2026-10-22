package org.girod.javafx.svgimage.xml.parsers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.shape.FillRule;
import javafx.scene.shape.SVGPath;
import javafx.scene.shape.Shape;
import org.girod.javafx.svgimage.xml.builders.SVGShapeBuilder;
import org.girod.javafx.svgimage.xml.specs.Viewport;

public class ClippingFactory implements SVGTags {
   private final Map<String, XMLNode> clipSpecs = new HashMap<>();

   public void addClipSpec(String id, XMLNode node) {
      this.clipSpecs.put(id, node);
   }

   public boolean hasClip(String id) {
      return this.clipSpecs.containsKey(id);
   }

   public Shape createClip(String id, Node node, Viewport viewport) {
      XMLNode xmlNode = this.clipSpecs.get(id);
      if (this.clipSpecs.containsKey(id)) {
         Bounds objectBoundingBox = null;
         Shape theShape = null;
         if (xmlNode.hasAttribute("clipPathUnits")) {
            String units = xmlNode.getAttributeValue("clipPathUnits");
            if (units.equals("objectBoundingBox")) {
               objectBoundingBox = node.getBoundsInLocal();
            }
         }

         for (XMLNode childNode : xmlNode.getChildren()) {
            Shape shape = null;
            String name = childNode.getName();
            switch (name) {
               case "circle":
                  shape = SVGShapeBuilder.buildCircle(childNode, objectBoundingBox, null, viewport);
                  break;
               case "path":
                  List<? extends Shape> shapes = SVGShapeBuilder.buildPath(childNode, objectBoundingBox, null, viewport, true);
                  if (shape != null) {
                     shape = shapes.get(0);
                     FillRule rule = ParserUtils.getClipRule(childNode);
                     if (rule != null) {
                        ((SVGPath)shape).setFillRule(rule);
                     }
                  }
                  break;
               case "polyline":
                  shape = SVGShapeBuilder.buildPolyline(xmlNode, objectBoundingBox, null, viewport);
                  break;
               case "polygon":
                  shape = SVGShapeBuilder.buildPolygon(xmlNode, objectBoundingBox, null, viewport);
                  break;
               case "ellipse":
                  shape = SVGShapeBuilder.buildEllipse(childNode, objectBoundingBox, null, viewport);
                  break;
               case "rect":
                  shape = SVGShapeBuilder.buildRect(childNode, objectBoundingBox, null, viewport);
                  break;
               case "line":
                  shape = SVGShapeBuilder.buildLine(childNode, objectBoundingBox, null, viewport);
                  break;
               case "text":
                  shape = SVGShapeBuilder.buildText(childNode, objectBoundingBox, null, viewport);
            }

            if (theShape == null) {
               theShape = shape;
            } else {
               theShape = Shape.union(theShape, shape);
            }

            if (xmlNode.hasAttribute("transform")) {
               TransformUtils.setTransforms(theShape, xmlNode, viewport);
            }
         }

         return theShape;
      } else {
         return null;
      }
   }
}
