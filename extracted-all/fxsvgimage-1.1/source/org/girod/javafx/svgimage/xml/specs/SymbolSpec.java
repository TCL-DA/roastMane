package org.girod.javafx.svgimage.xml.specs;

import org.girod.javafx.svgimage.xml.parsers.SVGTags;
import org.girod.javafx.svgimage.xml.parsers.XMLNode;

public class SymbolSpec implements SVGTags {
   private Viewbox viewbox = null;
   private final XMLNode node;

   public SymbolSpec(XMLNode node) {
      this.node = node;
   }

   public void setViewbox(Viewbox viewbox) {
      this.viewbox = viewbox;
   }

   public boolean hasViewbox() {
      return this.viewbox != null;
   }

   public Viewbox getViewbox() {
      return this.viewbox;
   }

   public XMLNode getXMLNode() {
      return this.node;
   }
}
