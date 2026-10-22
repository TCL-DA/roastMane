package com.sun.prism;

import javafx.scene.shape.CullFace;

public interface MeshView extends GraphicsResource {
   int CULL_NONE = CullFace.NONE.ordinal();
   int CULL_BACK = CullFace.BACK.ordinal();
   int CULL_FRONT = CullFace.FRONT.ordinal();

   void setCullingMode(int var1);

   void setMaterial(Material var1);

   void setWireframe(boolean var1);

   void setAmbientLight(float var1, float var2, float var3);

   void setLight(
      int var1,
      float var2,
      float var3,
      float var4,
      float var5,
      float var6,
      float var7,
      float var8,
      float var9,
      float var10,
      float var11,
      float var12,
      float var13,
      float var14,
      float var15,
      float var16,
      float var17,
      float var18
   );

   void render(Graphics var1);

   boolean isValid();
}
