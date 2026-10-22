package com.sun.prism.impl;

import com.sun.prism.PhongMaterial;

public abstract class BasePhongMaterial extends BaseGraphicsResource implements PhongMaterial {
   protected BasePhongMaterial(Disposer.Record var1) {
      super(var1);
   }

   @Override
   public boolean isValid() {
      return true;
   }
}
