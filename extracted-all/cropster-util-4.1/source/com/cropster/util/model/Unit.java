package com.cropster.util.model;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public enum Unit {
   M3,
   ML("0.000001", M3),
   FL_OZ("0.000030", M3),
   KG,
   G("0.001", KG),
   LBS("0.45359237", KG),
   OZ("0.0283495", KG),
   BARREL15KG("15", KG),
   BOX12_1KG("12.1", KG),
   BOX20KG("20", KG),
   BOX22_7KG("22.7", KG),
   BOX23KG("23", KG),
   BOX24KG("24", KG),
   BOX24_2KG("24.2", KG),
   BOX25KG("25", KG),
   BOX30KG("30", KG),
   BOX34KG("34", KG),
   BOX35KG("35", KG),
   BOX40KG("40", KG),
   BOX50LBS("22.679619", KG),
   BOX65LBS("29.483504", KG),
   BAG4KG("4", KG),
   BAG8KG("8", KG),
   BAG10KG("10", KG),
   BAG15KG("15", KG),
   BAG16KG("16", KG),
   BAG17KG("17", KG),
   BAG20KG("20", KG),
   BAG25KG("25", KG),
   BAG29KG("29", KG),
   BAG30KG("30", KG),
   BAG32KG("32", KG),
   BAG34_5KG("34.5", KG),
   BAG35KG("35", KG),
   BAG45KG("45", KG),
   BAG46KG("46", KG),
   BAG50KG("50", KG),
   BAG59KG("59", KG),
   BAG64KG("64", KG),
   BAG68KG("68", KG),
   BAG69KG("69", KG),
   BAG70KG("70", KG),
   BAG60KG("60", KG),
   BAG62_5KG("62.5", KG),
   BAG850KG("850", KG),
   BAG50LBS("22.6796185", KG),
   BAG100LBS("45.359237", KG),
   METRIC_TON("1000", KG),
   ARROBA("12.5", KG),
   CARGA("125", KG),
   FANEGA("45.359237", KG),
   QUINTAL("45.359237", KG),
   QQ("55.2", KG),
   QUINTAL_COCO("86.3", KG),
   LATA("13.607771", KG),
   ORIG_LITRE("1", KG),
   ORIG12_5L("12.5", KG),
   ALQUEIRE("60", KG),
   PACKAGE0G("0", KG),
   PACKAGE1G("0.001", KG),
   PACKAGE5G("0.005", KG),
   PACKAGE6G("0.006", KG),
   PACKAGE10G("0.01", KG),
   PACKAGE12G("0.012", KG),
   PACKAGE13_2G("0.0132", KG),
   PACKAGE14_2G("0.0142", KG),
   PACKAGE15G("0.015", KG),
   PACKAGE20G("0.02", KG),
   PACKAGE30G("0.03", KG),
   PACKAGE40G("0.04", KG),
   PACKAGE50G("0.05", KG),
   PACKAGE60G("0.06", KG),
   PACKAGE65G("0.065", KG),
   PACKAGE70G("0.07", KG),
   PACKAGE71G("0.071", KG),
   PACKAGE80G("0.08", KG),
   PACKAGE85G("0.085", KG),
   PACKAGE90G("0.09", KG),
   PACKAGE95G("0.095", KG),
   PACKAGE96G("0.096", KG),
   PACKAGE100G("0.1", KG),
   PACKAGE105G("0.105", KG),
   PACKAGE110G("0.11", KG),
   PACKAGE120G("0.120", KG),
   PACKAGE121G("0.121", KG),
   PACKAGE125G("0.125", KG),
   PACKAGE140G("0.14", KG),
   PACKAGE142G("0.142", KG),
   PACKAGE150G("0.15", KG),
   PACKAGE156_25G("0.15625", KG),
   PACKAGE164G("0.164", KG),
   PACKAGE170G("0.17", KG),
   PACKAGE175G("0.175", KG),
   PACKAGE180G("0.18", KG),
   PACKAGE200G("0.2", KG),
   PACKAGE225G("0.225", KG),
   PACKAGE227G("0.227", KG),
   PACKAGE230G("0.23", KG),
   PACKAGE250G("0.25", KG),
   PACKAGE265G("0.265", KG),
   PACKAGE285G("0.285", KG),
   PACKAGE300G("0.3", KG),
   PACKAGE310G("0.31", KG),
   PACKAGE312_5G("0.3125", KG),
   PACKAGE333G("0.333", KG),
   PACKAGE340G("0.34", KG),
   PACKAGE350G("0.35", KG),
   PACKAGE380G("0.38", KG),
   PACKAGE400G("0.4", KG),
   PACKAGE450G("0.45", KG),
   PACKAGE454G("0.454", KG),
   PACKAGE500G("0.5", KG),
   PACKAGE600G("0.6", KG),
   PACKAGE676G("0.676", KG),
   PACKAGE750G("0.75", KG),
   PACKAGE900G("0.9", KG),
   PACKAGE950G("0.95", KG),
   PACKAGE1KG("1", KG),
   PACKAGE1_2KG("1.2", KG),
   PACKAGE1_25KG("1.25", KG),
   PACKAGE1_5KG("1.5", KG),
   PACKAGE2KG("2", KG),
   PACKAGE2_27KG("2.27", KG),
   PACKAGE2_5KG("2.5", KG),
   PACKAGE2_72KG("2.72", KG),
   PACKAGE2_8KG("2.8", KG),
   PACKAGE3KG("3", KG),
   PACKAGE3_5KG("3.5", KG),
   PACKAGE3_6KG("3.6", KG),
   PACKAGE4KG("4", KG),
   PACKAGE5KG("5", KG),
   PACKAGE5_6KG("5.6", KG),
   PACKAGE6KG("6", KG),
   PACKAGE7KG("7", KG),
   PACKAGE8KG("8", KG),
   PACKAGE9KG("9", KG),
   PACKAGE10KG("10", KG),
   PACKAGE12KG("12", KG),
   PACKAGE14KG("14", KG),
   PACKAGE15KG("15", KG),
   PACKAGE30KG("30", KG),
   PACKAGE35KG("35", KG),
   PACKAGE50KG("50", KG),
   PACKAGE69KG("69", KG),
   PACKAGE70KG("70", KG),
   PACKAGE920KG("920", KG),
   PACKAGE0_5LBS("0.226796", KG),
   PACKAGE1LBS("0.453592", KG),
   PACKAGE1_1LBS("0.498952", KG),
   PACKAGE1_5LBS("0.680389", KG),
   PACKAGE1_75LBS("0.793787", KG),
   PACKAGE2LBS("0.907185", KG),
   PACKAGE2_5LBS("1.133981", KG),
   PACKAGE3LBS("1.360777", KG),
   PACKAGE3_5LBS("1.587573", KG),
   PACKAGE3_75LBS("1.700971", KG),
   PACKAGE3_85LBS("1.746331", KG),
   PACKAGE4LBS("1.814370", KG),
   PACKAGE4_5LBS("2.041166", KG),
   PACKAGE5LBS("2.26796", KG),
   PACKAGE6LBS("2.72155", KG),
   PACKAGE7LBS("3.17514", KG),
   PACKAGE7_5LBS("3.401943", KG),
   PACKAGE8_5LBS("3.85554", KG),
   PACKAGE9LBS("4.082331", KG),
   PACKAGE10LBS("4.53592", KG),
   PACKAGE11LBS("4.989516", KG),
   PACKAGE12LBS("5.443108", KG),
   PACKAGE12_5LBS("5.669905", KG),
   PACKAGE15LBS("6.803886", KG),
   PACKAGE16LBS("7.257478", KG),
   PACKAGE20LBS("9.071847", KG),
   PACKAGE22_5LBS("10.20582", KG),
   PACKAGE24LBS("10.886208", KG),
   PACKAGE25LBS("11.339809", KG),
   PACKAGE30LBS("13.607771", KG),
   PACKAGE40LBS("18.143695", KG),
   PACKAGE50LBS("22.679619", KG),
   PACKAGE125LBS("56.699047", KG),
   PACKAGE800LBS("362.873896", KG),
   PACKAGE1OZ("0.0283495", KG),
   PACKAGE2OZ("0.056699", KG),
   PACKAGE2_5OZ("0.070874", KG),
   PACKAGE3OZ("0.0850486", KG),
   PACKAGE4OZ("0.113398", KG),
   PACKAGE5OZ("0.141748", KG),
   PACKAGE6OZ("0.170097", KG),
   PACKAGE7OZ("0.198447", KG),
   PACKAGE8OZ("0.226796", KG),
   PACKAGE9OZ("0.255146", KG),
   PACKAGE10OZ("0.283495", KG),
   PACKAGE10_5OZ("0.29767", KG),
   PACKAGE10_9OZ("0.30901", KG),
   PACKAGE11OZ("0.311845", KG),
   PACKAGE12OZ("0.340194", KG),
   PACKAGE12_07OZ("0.342179", KG),
   PACKAGE14OZ("0.396893", KG),
   PACKAGE16OZ("0.453592", KG),
   PACKAGE18OZ("0.510291", KG),
   PACKAGE22OZ("0.623690", KG),
   PACKAGE24OZ("0.680389", KG),
   PACKAGE35OZ("0.992233", KG),
   PACKAGE36OZ("1.02058", KG),
   PACKAGE45OZ("1.275728", KG),
   PACKAGE48OZ("1.360776", KG),
   PACKAGE72OZ("2.041164", KG),
   PACKAGE90OZ("2.551457", KG),
   PACKAGE120OZ("3.40194", KG),
   PACKAGE144OZ("4.08233", KG),
   PACKAGE6912OZ("195.95290", KG),
   M,
   CM("0.01", M),
   MM("0.001", M),
   IN("0.0254", M),
   CELSIUS,
   FAHRENHEIT(frac(d("5"), d("9")).toString(), "32", CELSIUS),
   AGTRON_GOURMET,
   AGTRON_COMMERCIAL,
   COLORTRACK,
   DIPPER,
   ESPRESSO_VISION,
   KONICA_MINOLTA,
   NEUHAUS_NEOTEC,
   PROBAT_COLORETTE,
   HUNTER_L,
   JAVALYTICS,
   TONINO,
   LIGHTTELLS,
   ROAMI,
   ROASTRITE,
   SECOND,
   HOUR("3600", SECOND),
   MINUTE("60", SECOND),
   MILLISECOND("0.001", SECOND),
   DURATION("1", SECOND),
   HZ,
   RPM(frac(d("1"), d("60")).toString(), HZ),
   PASCAL,
   DEKAPASCAL("10", PASCAL),
   HEKTOPASCAL("100", PASCAL),
   KILOPASCAL("1000", PASCAL),
   PSI("6894.7572798677575", PASCAL),
   BAR("100000", PASCAL),
   MILLIBAR("100", PASCAL),
   INCH_WC("249.1", PASCAL),
   PERCENT,
   PPM("0.0001", PERCENT),
   NUMBER,
   VOLT,
   KG_PER_S,
   G_PER_S("0.001", KG_PER_S),
   OZ_PER_S("0.0283495", KG_PER_S),
   M3_PER_S,
   ML_PER_S("0.000001", KG_PER_S),
   FL_OZ_PER_S("0.000030", KG_PER_S),
   S_PER_M,
   US_PER_CM("0.0001", S_PER_M),
   MMOL_PER_L,
   FRENCH_DEGREES("0.101", MMOL_PER_L),
   PH;

   private final BigDecimal _factor;
   private final BigDecimal _offset;
   private final Unit _siUnit;

   private static BigDecimal d(String decimal) {
      return new BigDecimal(decimal);
   }

   private static BigDecimal frac(BigDecimal dividend, BigDecimal divisor) {
      return dividend.divide(divisor, MathContext.DECIMAL64);
   }

   private Unit() {
      this("1", "0", null);
   }

   private Unit(String factor, Unit siUnit) {
      this(factor, "0", siUnit);
   }

   private Unit(String factor, String offset, Unit siUnit) {
      this._factor = new BigDecimal(factor, MathContext.DECIMAL64);
      this._offset = new BigDecimal(offset, MathContext.DECIMAL64);
      this._siUnit = siUnit != null ? siUnit : this;
   }

   public BigDecimal getFactor() {
      return this._factor;
   }

   public BigDecimal getOffset() {
      return this._offset;
   }

   public Unit getSiUnit() {
      return this._siUnit;
   }

   public boolean isCompatible(Unit unit) {
      return this._siUnit.equals(unit._siUnit);
   }

   private void checkPrecision(BigDecimal value) {
      if (value.precision() > MathContext.DECIMAL64.getPrecision() && value.scale() <= 0) {
         throw new IllegalArgumentException("Precision not supported");
      }
   }

   public BigDecimal from(BigDecimal value, Unit valueUnit, RoundingMode roundingMode, boolean relative) {
      if (!this.isCompatible(valueUnit)) {
         throw new IllegalArgumentException("Units not compatible: " + this + " - " + valueUnit);
      } else if (this.equals(valueUnit)) {
         return value;
      } else {
         this.checkPrecision(value);
         BigDecimal siValue = value;
         if (!valueUnit.equals(this._siUnit)) {
            if (!relative && !BigDecimal.ZERO.equals(valueUnit._offset)) {
               siValue = value.subtract(valueUnit._offset);
            }

            siValue = siValue.multiply(valueUnit._factor, MathContext.DECIMAL64);
            this.checkPrecision(value);
         }

         BigDecimal result = siValue;
         if (!this.equals(this._siUnit)) {
            result = siValue.divide(this._factor, MathContext.DECIMAL64);
            this.checkPrecision(result);
            if (!relative && !BigDecimal.ZERO.equals(this._offset)) {
               result = result.add(this._offset);
            }
         }

         if (roundingMode != null) {
            int precision = 3;
            if (this._factor != null && this._factor.compareTo(new BigDecimal("1000")) > 0) {
               precision = (int)Math.ceil(Math.log10(this._factor.doubleValue()));
            }

            result = result.setScale(precision, roundingMode);
         }

         result = result.stripTrailingZeros();
         if (result.scale() < 0 || BigDecimal.ZERO.compareTo(result) == 0) {
            result = result.setScale(0);
         }

         this.checkPrecision(result);
         return result;
      }
   }

   public BigDecimal from(BigDecimal value, Unit valueUnit) {
      return this.from(value, valueUnit, null, false);
   }

   public BigDecimal from(BigDecimal value, Unit valueUnit, RoundingMode roundingMode) {
      return this.from(value, valueUnit, roundingMode, false);
   }

   public BigDecimal fromRelative(BigDecimal value, Unit valueUnit) {
      return this.from(value, valueUnit, null, true);
   }

   public BigDecimal fromRelative(BigDecimal value, Unit valueUnit, RoundingMode roundingMode) {
      return this.from(value, valueUnit, roundingMode, true);
   }

   public BigDecimal[] split(BigDecimal value, Unit... units) {
      BigDecimal remainder = value;
      BigDecimal[] values = new BigDecimal[units.length];

      for (int i = 0; i < units.length; i++) {
         BigDecimal unitValue = units[i].from(remainder, this);
         if (i < units.length - 1) {
            unitValue = unitValue.setScale(0, RoundingMode.DOWN);
         }

         values[i] = unitValue;
         remainder = remainder.subtract(this.from(unitValue, units[i]));
      }

      return values;
   }
}
