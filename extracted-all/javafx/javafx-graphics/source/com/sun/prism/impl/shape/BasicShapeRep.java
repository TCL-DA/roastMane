package com.sun.prism.impl.shape;

import com.sun.javafx.geom.BaseBounds;
import com.sun.javafx.geom.Shape;
import com.sun.prism.Graphics;
import com.sun.prism.shape.ShapeRep;

public class BasicShapeRep implements ShapeRep {
   @Override
   public boolean is3DCapable() {
      return false;
   }

   @Override
   public void invalidate(ShapeRep.InvalidationType var1) {
   }

   @Override
   public void fill(Graphics var1, Shape var2, BaseBounds var3) {
      var1.fill(var2);
   }

   @Override
   public void draw(Graphics var1, Shape var2, BaseBounds var3) {
      var1.draw(var2);
   }

   @Override
   public void dispose() {
   }
}
