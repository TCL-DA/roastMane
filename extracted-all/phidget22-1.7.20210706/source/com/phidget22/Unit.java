package com.phidget22;

public enum Unit {
   NONE(0) {
      @Override
      public String getMessage() {
         return "Unitless";
      }
   },
   BOOLEAN(1) {
      @Override
      public String getMessage() {
         return "Boolean";
      }
   },
   PERCENT(2) {
      @Override
      public String getMessage() {
         return "Percent";
      }
   },
   DECIBEL(3) {
      @Override
      public String getMessage() {
         return "Decibel";
      }
   },
   MILLIMETER(4) {
      @Override
      public String getMessage() {
         return "Millimeter";
      }
   },
   CENTIMETER(5) {
      @Override
      public String getMessage() {
         return "Centimeter";
      }
   },
   METER(6) {
      @Override
      public String getMessage() {
         return "Meter";
      }
   },
   GRAM(7) {
      @Override
      public String getMessage() {
         return "Gram";
      }
   },
   KILOGRAM(8) {
      @Override
      public String getMessage() {
         return "Kilogram";
      }
   },
   MILLIAMPERE(9) {
      @Override
      public String getMessage() {
         return "Milliampere";
      }
   },
   AMPERE(10) {
      @Override
      public String getMessage() {
         return "Ampere";
      }
   },
   KILOPASCAL(11) {
      @Override
      public String getMessage() {
         return "Kilopascal";
      }
   },
   VOLT(12) {
      @Override
      public String getMessage() {
         return "Volt";
      }
   },
   DEGREE_CELCIUS(13) {
      @Override
      public String getMessage() {
         return "Degree Celcius";
      }
   },
   LUX(14) {
      @Override
      public String getMessage() {
         return "Lux";
      }
   },
   GAUSS(15) {
      @Override
      public String getMessage() {
         return "Gauss";
      }
   },
   P_H(16) {
      @Override
      public String getMessage() {
         return "pH";
      }
   },
   WATT(17) {
      @Override
      public String getMessage() {
         return "Watt";
      }
   };

   private final int code;

   public static Unit getEnum(int var0) {
      switch (var0) {
         case 0:
            return NONE;
         case 1:
            return BOOLEAN;
         case 2:
            return PERCENT;
         case 3:
            return DECIBEL;
         case 4:
            return MILLIMETER;
         case 5:
            return CENTIMETER;
         case 6:
            return METER;
         case 7:
            return GRAM;
         case 8:
            return KILOGRAM;
         case 9:
            return MILLIAMPERE;
         case 10:
            return AMPERE;
         case 11:
            return KILOPASCAL;
         case 12:
            return VOLT;
         case 13:
            return DEGREE_CELCIUS;
         case 14:
            return LUX;
         case 15:
            return GAUSS;
         case 16:
            return P_H;
         case 17:
            return WATT;
         default:
            return null;
      }
   }

   private Unit(int var3) {
      this.code = var3;
   }

   public int getCode() {
      return this.code;
   }

   public abstract String getMessage();
}
