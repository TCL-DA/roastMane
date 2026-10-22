package org.girod.javafx.svgimage.xml.specs;

import javafx.scene.Node;
import javafx.scene.paint.Paint;

public class MarkerContext {
   private Node contextNode = null;
   private MarkerSpec markerStart = null;
   private MarkerSpec markerMid = null;
   private MarkerSpec markerEnd = null;
   private Paint contextFill = null;
   private Paint contextStroke = null;

   public boolean isEmpty() {
      return this.markerStart == null && this.markerMid == null && this.markerEnd == null;
   }

   public void setMarkerStart(MarkerSpec markerStart) {
      this.markerStart = markerStart;
   }

   public boolean hasMarkerStart() {
      return this.markerStart != null;
   }

   public MarkerSpec getMarkerStart() {
      return this.markerStart;
   }

   public void setMarkerMid(MarkerSpec markerMid) {
      this.markerMid = markerMid;
   }

   public boolean hasMarkerMid() {
      return this.markerMid != null;
   }

   public MarkerSpec getMarkerMid() {
      return this.markerMid;
   }

   public boolean hasMarkerEnd() {
      return this.markerEnd != null;
   }

   public MarkerSpec getMarkerEnd() {
      return this.markerEnd;
   }

   public void setMarkerEnd(MarkerSpec markerEnd) {
      this.markerEnd = markerEnd;
   }

   public void setContextFill(Paint fill) {
      this.contextFill = fill;
   }

   public Paint getContextFill() {
      return this.contextFill;
   }

   public void setContextNode(Node node) {
      this.contextNode = node;
   }

   public Node getContextNode() {
      return this.contextNode;
   }

   public void setContextStroke(Paint stroke) {
      this.contextStroke = stroke;
   }

   public Paint getContextStroke() {
      return this.contextStroke;
   }
}
