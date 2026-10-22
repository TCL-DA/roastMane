package org.girod.javafx.svgimage.tosvg.converters;

import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Labeled;
import org.girod.javafx.svgimage.tosvg.xml.XMLNode;

public class ControlConverter extends RegionConverter {
   private Control control = null;

   public ControlConverter(ConverterDelegate delegate, Control control, XMLNode xmlParent) {
      super(delegate, control, xmlParent);
      this.control = control;
   }

   @Override
   public XMLNode convert() {
      return super.convert();
   }

   @Override
   public Node getAdditionalNode() {
      return this.control instanceof Labeled ? ((Labeled)this.control).getGraphic() : null;
   }
}
