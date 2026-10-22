package org.girod.javafx.svgimage.tosvg.converters;

import javafx.scene.Parent;
import javafx.scene.SubScene;
import org.girod.javafx.svgimage.tosvg.xml.XMLNode;

public class SubSceneConverter extends AbstractConverter {
   private SubScene subScene = null;

   public SubSceneConverter(ConverterDelegate delegate, SubScene subScene, XMLNode xmlParent) {
      super(delegate, subScene, xmlParent);
      this.subScene = subScene;
   }

   public SubScene getSubScene() {
      return this.subScene;
   }

   @Override
   public Parent getParent() {
      return this.subScene.getRoot();
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
