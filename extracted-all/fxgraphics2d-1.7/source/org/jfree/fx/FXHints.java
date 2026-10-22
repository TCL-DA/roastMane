package org.jfree.fx;

import java.awt.RenderingHints;

public final class FXHints {
   public static final FXHints.Key KEY_USE_FX_FONT_METRICS = new FXHints.Key(0);

   private FXHints() {
   }

   public static class Key extends RenderingHints.Key {
      public Key(int privateKey) {
         super(privateKey);
      }

      @Override
      public boolean isCompatibleValue(Object val) {
         switch (this.intKey()) {
            case 0:
               return val == null || val instanceof Boolean;
            default:
               throw new RuntimeException("Not expected!");
         }
      }
   }
}
