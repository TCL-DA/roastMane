package com.sun.prism.impl.ps;

import com.sun.prism.Image;
import com.sun.prism.Texture;
import com.sun.prism.impl.BaseResourceFactory;
import com.sun.prism.impl.PrismSettings;
import com.sun.prism.impl.shape.BasicEllipseRep;
import com.sun.prism.impl.shape.BasicRoundRectRep;
import com.sun.prism.impl.shape.BasicShapeRep;
import com.sun.prism.ps.ShaderFactory;
import com.sun.prism.shape.ShapeRep;
import java.util.Map;

public abstract class BaseShaderFactory extends BaseResourceFactory implements ShaderFactory {
   public BaseShaderFactory() {
   }

   public BaseShaderFactory(Map<Image, Texture> var1, Map<Image, Texture> var2, Map<Image, Texture> var3) {
      super(var1, var2, var3);
   }

   @Override
   public ShapeRep createPathRep() {
      return (ShapeRep)(PrismSettings.cacheComplexShapes ? new CachingShapeRep() : new BasicShapeRep());
   }

   @Override
   public ShapeRep createRoundRectRep() {
      return (ShapeRep)(PrismSettings.cacheSimpleShapes ? new CachingRoundRectRep() : new BasicRoundRectRep());
   }

   @Override
   public ShapeRep createEllipseRep() {
      return (ShapeRep)(PrismSettings.cacheSimpleShapes ? new CachingEllipseRep() : new BasicEllipseRep());
   }

   @Override
   public ShapeRep createArcRep() {
      return (ShapeRep)(PrismSettings.cacheComplexShapes ? new CachingShapeRep() : new BasicShapeRep());
   }
}
