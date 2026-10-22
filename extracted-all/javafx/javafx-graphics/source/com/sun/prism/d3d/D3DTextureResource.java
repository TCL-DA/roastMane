package com.sun.prism.d3d;

import com.sun.prism.impl.DisposerManagedResource;

class D3DTextureResource extends DisposerManagedResource<D3DTextureData> {
   public D3DTextureResource(D3DTextureData var1) {
      super(var1, D3DVramPool.instance, var1);
   }

   @Override
   public void free() {
      if (this.resource != null) {
         this.resource.dispose();
      }
   }

   @Override
   public boolean isValid() {
      return this.resource != null && this.resource.getResource() != 0L;
   }
}
