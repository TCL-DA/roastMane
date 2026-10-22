package org.controlsfx.control.action;

import java.lang.reflect.Method;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCombination;
import org.controlsfx.glyphfont.Glyph;

public class DefaultActionFactory implements AnnotatedActionFactory {
   @Override
   public AnnotatedAction createAction(ActionProxy annotation, Method method, Object target) {
      AnnotatedAction action;
      if (method.isAnnotationPresent(ActionCheck.class)) {
         action = new AnnotatedCheckAction(annotation.text(), method, target);
      } else {
         action = new AnnotatedAction(annotation.text(), method, target);
      }

      this.configureAction(annotation, action);
      return action;
   }

   protected void configureAction(ActionProxy annotation, AnnotatedAction action) {
      Node graphic = this.resolveGraphic(annotation);
      action.setGraphic(graphic);
      String longText = annotation.longText().trim();
      if (graphic != null) {
         action.setLongText(longText);
      }

      String acceleratorText = annotation.accelerator().trim();
      if (!acceleratorText.isEmpty()) {
         action.setAccelerator(KeyCombination.keyCombination(acceleratorText));
      }
   }

   protected Node resolveGraphic(ActionProxy annotation) {
      String graphicDef = annotation.graphic().trim();
      if (!graphicDef.isEmpty()) {
         String[] def = graphicDef.split("\\>");
         if (def.length == 1) {
            return new ImageView(new Image(def[0]));
         } else {
            String var4 = def[0];
            switch (var4) {
               case "font":
                  return Glyph.create(def[1]);
               case "image":
                  return new ImageView(new Image(def[1]));
               default:
                  throw new IllegalArgumentException(String.format("Unknown ActionProxy graphic protocol: %s", def[0]));
            }
         }
      } else {
         return null;
      }
   }
}
