package com.sun.javafx.scene.control.skin;

import com.sun.javafx.scene.control.WebColorField;
import java.util.Locale;
import javafx.beans.InvalidationListener;
import javafx.geometry.NodeOrientation;
import javafx.scene.Node;
import javafx.scene.paint.Color;

public class WebColorFieldSkin extends InputFieldSkin {
   private InvalidationListener integerFieldValueListener;
   private boolean noChangeInValue = false;

   public WebColorFieldSkin(WebColorField var1) {
      super(var1);
      var1.valueProperty().addListener(this.integerFieldValueListener = var1x -> this.updateText());
      this.getTextField().setNodeOrientation(NodeOrientation.LEFT_TO_RIGHT);
   }

   public WebColorField getSkinnable() {
      return (WebColorField)this.control;
   }

   @Override
   public Node getNode() {
      return this.getTextField();
   }

   @Override
   public void dispose() {
      ((WebColorField)this.control).valueProperty().removeListener(this.integerFieldValueListener);
      super.dispose();
   }

   @Override
   protected boolean accept(String var1) {
      return var1.length() == 0 ? true : var1.matches("#[a-fA-F0-9]{0,6}") || var1.matches("[a-fA-F0-9]{0,6}");
   }

   @Override
   protected void updateText() {
      Color var1 = ((WebColorField)this.control).getValue();
      if (var1 == null) {
         var1 = Color.BLACK;
      }

      this.getTextField().setText(Utils.formatHexString(var1));
   }

   @Override
   protected void updateValue() {
      if (!this.noChangeInValue) {
         Color var1 = ((WebColorField)this.control).getValue();
         String var2 = this.getTextField().getText() == null ? "" : this.getTextField().getText().trim().toUpperCase(Locale.ROOT);
         if (var2.matches("#[A-F0-9]{6}") || var2.matches("[A-F0-9]{6}")) {
            try {
               Color var3 = var2.charAt(0) == '#' ? Color.web(var2) : Color.web("#" + var2);
               if (!var3.equals(var1)) {
                  ((WebColorField)this.control).setValue(var3);
               } else {
                  this.noChangeInValue = true;
                  this.getTextField().setText(Utils.formatHexString(var3));
                  this.noChangeInValue = false;
               }
            } catch (IllegalArgumentException var4) {
               System.out.println("Failed to parse [" + var2 + "]");
            }
         }
      }
   }
}
