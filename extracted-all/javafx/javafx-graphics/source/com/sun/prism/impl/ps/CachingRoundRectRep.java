package com.sun.prism.impl.ps;

public class CachingRoundRectRep extends CachingShapeRep {
   @Override
   CachingShapeRepState createState() {
      return new CachingRoundRectRepState();
   }
}
