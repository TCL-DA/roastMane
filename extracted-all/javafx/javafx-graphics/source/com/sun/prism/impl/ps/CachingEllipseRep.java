package com.sun.prism.impl.ps;

public class CachingEllipseRep extends CachingShapeRep {
   @Override
   CachingShapeRepState createState() {
      return new CachingEllipseRepState();
   }
}
