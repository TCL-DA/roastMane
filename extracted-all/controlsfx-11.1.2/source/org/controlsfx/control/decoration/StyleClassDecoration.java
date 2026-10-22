package org.controlsfx.control.decoration;

import java.util.List;
import javafx.scene.Node;

public class StyleClassDecoration extends Decoration {
   private final String[] styleClasses;

   public StyleClassDecoration(String... styleClass) {
      if (styleClass != null && styleClass.length != 0) {
         this.styleClasses = styleClass;
      } else {
         throw new IllegalArgumentException("var-arg style class array must not be null or empty");
      }
   }

   @Override
   public Node applyDecoration(Node targetNode) {
      List<String> styleClassList = targetNode.getStyleClass();

      for (String styleClass : this.styleClasses) {
         if (!styleClassList.contains(styleClass)) {
            styleClassList.add(styleClass);
         }
      }

      return null;
   }

   @Override
   public void removeDecoration(Node targetNode) {
      targetNode.getStyleClass().removeAll(this.styleClasses);
   }
}
