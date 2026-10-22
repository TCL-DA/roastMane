package javafx.css;

import javafx.scene.text.Font;

public enum SizeUnits {
   PERCENT(false) {
      @Override
      public String toString() {
         return "%";
      }

      @Override
      public double points(double var1, double var3, Font var5) {
         return var1 / 100.0 * var3;
      }

      @Override
      public double pixels(double var1, double var3, Font var5) {
         return var1 / 100.0 * var3;
      }
   },
   IN(true) {
      @Override
      public String toString() {
         return "in";
      }

      @Override
      public double points(double var1, double var3, Font var5) {
         return var1 * 72.0;
      }

      @Override
      public double pixels(double var1, double var3, Font var5) {
         return var1 * 96.0;
      }
   },
   CM(true) {
      @Override
      public String toString() {
         return "cm";
      }

      @Override
      public double points(double var1, double var3, Font var5) {
         return var1 / 2.54 * 72.0;
      }

      @Override
      public double pixels(double var1, double var3, Font var5) {
         return var1 / 2.54 * 96.0;
      }
   },
   MM(true) {
      @Override
      public String toString() {
         return "mm";
      }

      @Override
      public double points(double var1, double var3, Font var5) {
         return var1 / 25.4 * 72.0;
      }

      @Override
      public double pixels(double var1, double var3, Font var5) {
         return var1 / 25.4 * 96.0;
      }
   },
   EM(false) {
      @Override
      public String toString() {
         return "em";
      }

      @Override
      public double points(double var1, double var3, Font var5) {
         return SizeUnits.round(var1 * SizeUnits.pointSize(var5));
      }

      @Override
      public double pixels(double var1, double var3, Font var5) {
         return SizeUnits.round(var1 * SizeUnits.pixelSize(var5));
      }
   },
   EX(false) {
      @Override
      public String toString() {
         return "ex";
      }

      @Override
      public double points(double var1, double var3, Font var5) {
         return SizeUnits.round(var1 / 2.0 * SizeUnits.pointSize(var5));
      }

      @Override
      public double pixels(double var1, double var3, Font var5) {
         return SizeUnits.round(var1 / 2.0 * SizeUnits.pixelSize(var5));
      }
   },
   PT(true) {
      @Override
      public String toString() {
         return "pt";
      }

      @Override
      public double points(double var1, double var3, Font var5) {
         return var1;
      }

      @Override
      public double pixels(double var1, double var3, Font var5) {
         return var1 * 1.3333333333333333;
      }
   },
   PC(true) {
      @Override
      public String toString() {
         return "pc";
      }

      @Override
      public double points(double var1, double var3, Font var5) {
         return var1 * 12.0;
      }

      @Override
      public double pixels(double var1, double var3, Font var5) {
         return var1 * 12.0 * 1.3333333333333333;
      }
   },
   PX(true) {
      @Override
      public String toString() {
         return "px";
      }

      @Override
      public double points(double var1, double var3, Font var5) {
         return var1 * 0.75;
      }

      @Override
      public double pixels(double var1, double var3, Font var5) {
         return var1;
      }
   },
   DEG(true) {
      @Override
      public String toString() {
         return "deg";
      }

      @Override
      public double points(double var1, double var3, Font var5) {
         return SizeUnits.round(var1);
      }

      @Override
      public double pixels(double var1, double var3, Font var5) {
         return SizeUnits.round(var1);
      }
   },
   GRAD(true) {
      @Override
      public String toString() {
         return "grad";
      }

      @Override
      public double points(double var1, double var3, Font var5) {
         return SizeUnits.round(var1 * 9.0 / 10.0);
      }

      @Override
      public double pixels(double var1, double var3, Font var5) {
         return SizeUnits.round(var1 * 9.0 / 10.0);
      }
   },
   RAD(true) {
      @Override
      public String toString() {
         return "rad";
      }

      @Override
      public double points(double var1, double var3, Font var5) {
         return SizeUnits.round(var1 * 180.0 / Math.PI);
      }

      @Override
      public double pixels(double var1, double var3, Font var5) {
         return SizeUnits.round(var1 * 180.0 / Math.PI);
      }
   },
   TURN(true) {
      @Override
      public String toString() {
         return "turn";
      }

      @Override
      public double points(double var1, double var3, Font var5) {
         return SizeUnits.round(var1 * 360.0);
      }

      @Override
      public double pixels(double var1, double var3, Font var5) {
         return SizeUnits.round(var1 * 360.0);
      }
   },
   S(true) {
      @Override
      public String toString() {
         return "s";
      }

      @Override
      public double points(double var1, double var3, Font var5) {
         return var1;
      }

      @Override
      public double pixels(double var1, double var3, Font var5) {
         return var1;
      }
   },
   MS(true) {
      @Override
      public String toString() {
         return "ms";
      }

      @Override
      public double points(double var1, double var3, Font var5) {
         return var1;
      }

      @Override
      public double pixels(double var1, double var3, Font var5) {
         return var1;
      }
   };

   private final boolean absolute;
   private static final double DOTS_PER_INCH = 96.0;
   private static final double POINTS_PER_INCH = 72.0;
   private static final double CM_PER_INCH = 2.54;
   private static final double MM_PER_INCH = 25.4;
   private static final double POINTS_PER_PICA = 12.0;

   public abstract double points(double var1, double var3, Font var5);

   public abstract double pixels(double var1, double var3, Font var5);

   private SizeUnits(boolean var3) {
      this.absolute = var3;
   }

   public boolean isAbsolute() {
      return this.absolute;
   }

   private static double pointSize(Font var0) {
      return pixelSize(var0) * 0.75;
   }

   private static double pixelSize(Font var0) {
      return var0 != null ? var0.getSize() : Font.getDefault().getSize();
   }

   private static double round(double var0) {
      if (var0 == 0.0) {
         return var0;
      } else {
         double var2 = var0 < 0.0 ? -0.05 : 0.05;
         return (double)((long)((var0 + var2) * 10.0)) / 10.0;
      }
   }
}
