package org.girod.javafx.svgimage.tosvg.converters;

import javafx.scene.Group;
import javafx.scene.Parent;
import org.girod.javafx.svgimage.tosvg.xml.XMLNode;

public class GroupConverter extends AbstractConverter {
   private Group group = null;

   public GroupConverter(ConverterDelegate delegate, Group group, XMLNode xmlParent) {
      super(delegate, group, xmlParent);
      this.group = group;
   }

   @Override
   public Parent getParent() {
      return this.group;
   }

   @Override
   public void applyStyle(XMLNode node, String clipID) {
      if (clipID != null) {
         StringBuilder buf = new StringBuilder();
         String style = buf.toString();
         node.addAttribute("style", style);
      }
   }

   @Override
   public XMLNode convert() {
      XMLNode xmlGroup = new XMLNode("g");
      this.xmlParent.addChild(xmlGroup);
      return xmlGroup;
   }
}
