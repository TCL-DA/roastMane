package com.sun.javafx.css;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import javafx.css.ParsedValue;
import javafx.css.Size;
import javafx.css.SizeUnits;
import javafx.css.StyleConverter;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class ParsedValueImpl<V, T> extends ParsedValue<V, T> {
   private final boolean lookup;
   private final boolean containsLookups;
   private static int indent = 0;
   private int hash = Integer.MIN_VALUE;
   private static final byte NULL_VALUE = 0;
   private static final byte VALUE = 1;
   private static final byte VALUE_ARRAY = 2;
   private static final byte ARRAY_OF_VALUE_ARRAY = 3;
   private static final byte STRING = 4;
   private static final byte COLOR = 5;
   private static final byte ENUM = 6;
   private static final byte BOOLEAN = 7;
   private static final byte URL = 8;
   private static final byte SIZE = 9;

   @Override
   public final boolean isLookup() {
      return this.lookup;
   }

   @Override
   public final boolean isContainsLookups() {
      return this.containsLookups;
   }

   private static boolean getContainsLookupsFlag(Object var0) {
      boolean var1 = false;
      if (var0 instanceof Size) {
         var1 = false;
      } else if (var0 instanceof ParsedValueImpl) {
         ParsedValueImpl var2 = (ParsedValueImpl)var0;
         var1 = var2.lookup || var2.containsLookups;
      } else if (var0 instanceof ParsedValueImpl[]) {
         ParsedValueImpl[] var5 = (ParsedValueImpl[])var0;

         for (int var3 = 0; var3 < var5.length && !var1; var3++) {
            if (var5[var3] != null) {
               var1 = var1 || var5[var3].lookup || var5[var3].containsLookups;
            }
         }
      } else if (var0 instanceof ParsedValueImpl[][]) {
         ParsedValueImpl[][] var6 = (ParsedValueImpl[][])var0;

         for (int var7 = 0; var7 < var6.length && !var1; var7++) {
            if (var6[var7] != null) {
               for (int var4 = 0; var4 < var6[var7].length && !var1; var4++) {
                  if (var6[var7][var4] != null) {
                     var1 = var1 || var6[var7][var4].lookup || var6[var7][var4].containsLookups;
                  }
               }
            }
         }
      }

      return var1;
   }

   public static boolean containsFontRelativeSize(ParsedValue var0, boolean var1) {
      boolean var2 = false;
      Object var3 = var0.getValue();
      if (var3 instanceof Size) {
         Size var4 = (Size)var3;
         var2 = var4.getUnits() == SizeUnits.PERCENT ? var1 : !var4.isAbsolute();
      } else if (var3 instanceof ParsedValue) {
         ParsedValueImpl var7 = (ParsedValueImpl)var3;
         var2 = containsFontRelativeSize(var7, var1);
      } else if (var3 instanceof ParsedValue[]) {
         ParsedValue[] var8 = (ParsedValue[])var3;

         for (int var5 = 0; var5 < var8.length && !var2; var5++) {
            if (var8[var5] != null) {
               var2 = containsFontRelativeSize(var8[var5], var1);
            }
         }
      } else if (var3 instanceof ParsedValueImpl[][]) {
         ParsedValueImpl[][] var9 = (ParsedValueImpl[][])var3;

         for (int var10 = 0; var10 < var9.length && !var2; var10++) {
            if (var9[var10] != null) {
               for (int var6 = 0; var6 < var9[var10].length && !var2; var6++) {
                  if (var9[var10][var6] != null) {
                     var2 = containsFontRelativeSize(var9[var10][var6], var1);
                  }
               }
            }
         }
      }

      return var2;
   }

   public ParsedValueImpl(V var1, StyleConverter<V, T> var2, boolean var3) {
      super((V)var1, var2);
      this.lookup = var3;
      this.containsLookups = var3 || getContainsLookupsFlag(var1);
   }

   public ParsedValueImpl(V var1, StyleConverter<V, T> var2) {
      this((V)var1, var2, false);
   }

   @Override
   public T convert(Font var1) {
      return (T)(this.converter != null ? this.converter.convert(this, var1) : this.value);
   }

   private static String spaces() {
      return new String(new char[indent]).replace('\u0000', ' ');
   }

   private static void indent() {
      indent += 2;
   }

   private static void outdent() {
      indent = Math.max(0, indent - 2);
   }

   @Override
   public String toString() {
      String var1 = System.lineSeparator();
      StringBuilder var2 = new StringBuilder();
      var2.append(spaces()).append(this.lookup ? "<Value lookup=\"true\">" : "<Value>").append(var1);
      indent();
      if (this.value != null) {
         this.appendValue(var2, this.value, "value");
      } else {
         this.appendValue(var2, "null", "value");
      }

      var2.append(spaces()).append("<converter>").append(this.converter).append("</converter>").append(var1);
      outdent();
      var2.append(spaces()).append("</Value>");
      return var2.toString();
   }

   private void appendValue(StringBuilder var1, Object var2, String var3) {
      String var4 = System.lineSeparator();
      if (var2 instanceof ParsedValueImpl[][]) {
         ParsedValueImpl[][] var5 = (ParsedValueImpl[][])var2;
         var1.append(spaces()).append('<').append(var3).append(" layers=\"").append(var5.length).append("\">").append(var4);
         indent();

         for (ParsedValueImpl[] var9 : var5) {
            var1.append(spaces()).append("<layer>").append(var4);
            indent();
            if (var9 == null) {
               var1.append(spaces()).append("null").append(var4);
            } else {
               for (ParsedValueImpl var13 : var9) {
                  if (var13 == null) {
                     var1.append(spaces()).append("null").append(var4);
                  } else {
                     var1.append(var13);
                  }
               }

               outdent();
               var1.append(spaces()).append("</layer>").append(var4);
            }
         }

         outdent();
         var1.append(spaces()).append("</").append(var3).append('>').append(var4);
      } else if (var2 instanceof ParsedValueImpl[]) {
         ParsedValueImpl[] var14 = (ParsedValueImpl[])var2;
         var1.append(spaces()).append('<').append(var3).append(" values=\"").append(var14.length).append("\">").append(var4);
         indent();

         for (ParsedValueImpl var18 : var14) {
            if (var18 == null) {
               var1.append(spaces()).append("null").append(var4);
            } else {
               var1.append(var18);
            }
         }

         outdent();
         var1.append(spaces()).append("</").append(var3).append('>').append(var4);
      } else if (var2 instanceof ParsedValueImpl) {
         var1.append(spaces()).append('<').append(var3).append('>').append(var4);
         indent();
         var1.append(var2);
         outdent();
         var1.append(spaces()).append("</").append(var3).append('>').append(var4);
      } else {
         var1.append(spaces()).append('<').append(var3).append('>');
         var1.append(var2);
         var1.append("</").append(var3).append('>').append(var4);
      }
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (var1 != null && var1.getClass() == this.getClass()) {
         ParsedValueImpl var2 = (ParsedValueImpl)var1;
         if (this.hash != var2.hash) {
            return false;
         } else if (this.value instanceof ParsedValueImpl[][]) {
            if (!(var2.value instanceof ParsedValueImpl[][])) {
               return false;
            } else {
               ParsedValueImpl[][] var9 = (ParsedValueImpl[][])this.value;
               ParsedValueImpl[][] var10 = (ParsedValueImpl[][])var2.value;
               if (var9.length != var10.length) {
                  return false;
               } else {
                  for (int var11 = 0; var11 < var9.length; var11++) {
                     if (var9[var11] != null || var10[var11] != null) {
                        if (var9[var11] == null || var10[var11] == null) {
                           return false;
                        }

                        if (var9[var11].length != var10[var11].length) {
                           return false;
                        }

                        for (int var12 = 0; var12 < var9[var11].length; var12++) {
                           ParsedValueImpl var13 = var9[var11][var12];
                           ParsedValueImpl var8 = var10[var11][var12];
                           if (var13 != null ? !var13.equals(var8) : var8 != null) {
                              return false;
                           }
                        }
                     }
                  }

                  return true;
               }
            }
         } else if (!(this.value instanceof ParsedValueImpl[])) {
            if (this.value instanceof String && var2.value instanceof String) {
               return this.value.toString().equalsIgnoreCase(var2.value.toString());
            } else {
               return this.value != null ? this.value.equals(var2.value) : var2.value == null;
            }
         } else if (!(var2.value instanceof ParsedValueImpl[])) {
            return false;
         } else {
            ParsedValueImpl[] var3 = (ParsedValueImpl[])this.value;
            ParsedValueImpl[] var4 = (ParsedValueImpl[])var2.value;
            if (var3.length != var4.length) {
               return false;
            } else {
               for (int var5 = 0; var5 < var3.length; var5++) {
                  ParsedValueImpl var6 = var3[var5];
                  ParsedValueImpl var7 = var4[var5];
                  if (var6 != null ? !var6.equals(var7) : var7 != null) {
                     return false;
                  }
               }

               return true;
            }
         }
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      if (this.hash == Integer.MIN_VALUE) {
         this.hash = 17;
         if (this.value instanceof ParsedValueImpl[][]) {
            ParsedValueImpl[][] var1 = (ParsedValueImpl[][])this.value;

            for (int var2 = 0; var2 < var1.length; var2++) {
               for (int var3 = 0; var3 < var1[var2].length; var3++) {
                  ParsedValueImpl var4 = var1[var2][var3];
                  this.hash = 37 * this.hash + (var4 != null && var4.value != null ? var4.value.hashCode() : 0);
               }
            }
         } else if (this.value instanceof ParsedValueImpl[]) {
            ParsedValueImpl[] var5 = (ParsedValueImpl[])this.value;

            for (int var6 = 0; var6 < var5.length; var6++) {
               if (var5[var6] != null && var5[var6].value != null) {
                  ParsedValueImpl var7 = var5[var6];
                  this.hash = 37 * this.hash + (var7 != null && var7.value != null ? var7.value.hashCode() : 0);
               }
            }
         } else {
            this.hash = 37 * this.hash + (this.value != null ? this.value.hashCode() : 0);
         }
      }

      return this.hash;
   }

   public final void writeBinary(DataOutputStream var1, StyleConverter.StringStore var2) throws IOException {
      var1.writeBoolean(this.lookup);
      if (this.converter != null) {
         var1.writeBoolean(true);
         this.converter.writeBinary(var1, var2);
      } else {
         var1.writeBoolean(false);
      }

      if (this.value instanceof ParsedValue) {
         var1.writeByte(1);
         ParsedValue var3 = (ParsedValue)this.value;
         if (var3 instanceof ParsedValueImpl) {
            ((ParsedValueImpl)var3).writeBinary(var1, var2);
         } else {
            ParsedValueImpl var4 = new ParsedValueImpl<>(var3.getValue(), var3.getConverter());
            var4.writeBinary(var1, var2);
         }
      } else if (this.value instanceof ParsedValue[]) {
         var1.writeByte(2);
         ParsedValue[] var11 = (ParsedValue[])this.value;
         if (var11 != null) {
            var1.writeByte(1);
         } else {
            var1.writeByte(0);
         }

         int var19 = var11 != null ? var11.length : 0;
         var1.writeInt(var19);

         for (int var5 = 0; var5 < var19; var5++) {
            if (var11[var5] != null) {
               var1.writeByte(1);
               ParsedValue var6 = var11[var5];
               if (var6 instanceof ParsedValueImpl) {
                  ((ParsedValueImpl)var6).writeBinary(var1, var2);
               } else {
                  ParsedValueImpl var7 = new ParsedValueImpl<>(var6.getValue(), var6.getConverter());
                  var7.writeBinary(var1, var2);
               }
            } else {
               var1.writeByte(0);
            }
         }
      } else if (this.value instanceof ParsedValue[][]) {
         var1.writeByte(3);
         ParsedValue[][] var12 = (ParsedValue[][])this.value;
         if (var12 != null) {
            var1.writeByte(1);
         } else {
            var1.writeByte(0);
         }

         int var20 = var12 != null ? var12.length : 0;
         var1.writeInt(var20);

         for (int var23 = 0; var23 < var20; var23++) {
            ParsedValue[] var24 = var12[var23];
            if (var24 != null) {
               var1.writeByte(1);
            } else {
               var1.writeByte(0);
            }

            int var26 = var24 != null ? var24.length : 0;
            var1.writeInt(var26);

            for (int var8 = 0; var8 < var26; var8++) {
               if (var24[var8] != null) {
                  var1.writeByte(1);
                  ParsedValue var9 = var24[var8];
                  if (var9 instanceof ParsedValueImpl) {
                     ((ParsedValueImpl)var9).writeBinary(var1, var2);
                  } else {
                     ParsedValueImpl var10 = new ParsedValueImpl<>(var9.getValue(), var9.getConverter());
                     var10.writeBinary(var1, var2);
                  }
               } else {
                  var1.writeByte(0);
               }
            }
         }
      } else if (this.value instanceof Color) {
         Color var13 = (Color)this.value;
         var1.writeByte(5);
         var1.writeLong(Double.doubleToLongBits(var13.getRed()));
         var1.writeLong(Double.doubleToLongBits(var13.getGreen()));
         var1.writeLong(Double.doubleToLongBits(var13.getBlue()));
         var1.writeLong(Double.doubleToLongBits(var13.getOpacity()));
      } else if (this.value instanceof Enum) {
         Enum var14 = (Enum)this.value;
         int var21 = var2.addString(var14.name());
         var1.writeByte(6);
         var1.writeShort(var21);
      } else if (this.value instanceof Boolean) {
         Boolean var15 = (Boolean)this.value;
         var1.writeByte(7);
         var1.writeBoolean(var15);
      } else if (this.value instanceof Size) {
         Size var16 = (Size)this.value;
         var1.writeByte(9);
         double var22 = var16.getValue();
         long var25 = Double.doubleToLongBits(var22);
         var1.writeLong(var25);
         int var27 = var2.addString(var16.getUnits().name());
         var1.writeShort(var27);
      } else if (this.value instanceof String) {
         var1.writeByte(4);
         int var17 = var2.addString((String)this.value);
         var1.writeShort(var17);
      } else if (this.value instanceof URL) {
         var1.writeByte(8);
         int var18 = var2.addString(this.value.toString());
         var1.writeShort(var18);
      } else {
         if (this.value != null) {
            throw new InternalError("cannot writeBinary " + this);
         }

         var1.writeByte(0);
      }
   }

   public static ParsedValueImpl readBinary(int var0, DataInputStream var1, String[] var2) throws IOException {
      boolean var3 = var1.readBoolean();
      boolean var4 = var1.readBoolean();
      StyleConverter var5 = var4 ? StyleConverter.readBinary(var1, var2) : null;
      byte var6 = var1.readByte();
      if (var6 == 1) {
         ParsedValueImpl var25 = readBinary(var0, var1, var2);
         return new ParsedValueImpl<>(var25, var5, var3);
      } else if (var6 == 2) {
         if (var0 >= 4) {
            var1.readByte();
         }

         int var24 = var1.readInt();
         ParsedValueImpl[] var28 = var24 > 0 ? new ParsedValueImpl[var24] : null;

         for (int var32 = 0; var32 < var24; var32++) {
            byte var34 = var1.readByte();
            if (var34 == 1) {
               var28[var32] = readBinary(var0, var1, var2);
            } else {
               var28[var32] = null;
            }
         }

         return new ParsedValueImpl<>(var28, var5, var3);
      } else if (var6 != 3) {
         if (var6 == 5) {
            double var23 = Double.longBitsToDouble(var1.readLong());
            double var31 = Double.longBitsToDouble(var1.readLong());
            double var35 = Double.longBitsToDouble(var1.readLong());
            double var13 = Double.longBitsToDouble(var1.readLong());
            return new ParsedValueImpl<>(Color.color(var23, var31, var35, var13), var5, var3);
         } else if (var6 == 6) {
            short var22 = var1.readShort();
            String var27 = var2[var22];
            if (var0 == 2) {
               short var30 = var1.readShort();
               if (var30 >= var2.length) {
                  throw new IllegalArgumentException("bad version " + var0);
               }
            }

            return new ParsedValueImpl<>(var27, var5, var3);
         } else if (var6 == 7) {
            Boolean var21 = var1.readBoolean();
            return new ParsedValueImpl<>(var21, var5, var3);
         } else if (var6 == 9) {
            double var20 = Double.longBitsToDouble(var1.readLong());
            SizeUnits var29 = SizeUnits.PX;
            String var33 = var2[var1.readShort()];

            try {
               var29 = Enum.valueOf(SizeUnits.class, var33);
            } catch (IllegalArgumentException var15) {
               System.err.println(var15.toString());
            } catch (NullPointerException var16) {
               System.err.println(var16.toString());
            }

            return new ParsedValueImpl<>(new Size(var20, var29), var5, var3);
         } else if (var6 == 4) {
            String var19 = var2[var1.readShort()];
            return new ParsedValueImpl<>(var19, var5, var3);
         } else if (var6 == 8) {
            String var18 = var2[var1.readShort()];

            try {
               URL var26 = new URL(var18);
               return new ParsedValueImpl<>(var26, var5, var3);
            } catch (MalformedURLException var17) {
               throw new InternalError("Exception in Value.readBinary: " + var17);
            }
         } else if (var6 == 0) {
            return new ParsedValueImpl(null, var5, var3);
         } else {
            throw new InternalError("unknown type: " + var6);
         }
      } else {
         if (var0 >= 4) {
            var1.readByte();
         }

         int var7 = var1.readInt();
         ParsedValueImpl[][] var8 = var7 > 0 ? new ParsedValueImpl[var7][0] : null;

         for (int var9 = 0; var9 < var7; var9++) {
            if (var0 >= 4) {
               var1.readByte();
            }

            int var10 = var1.readInt();
            var8[var9] = var10 > 0 ? new ParsedValueImpl[var10] : null;

            for (int var11 = 0; var11 < var10; var11++) {
               byte var12 = var1.readByte();
               if (var12 == 1) {
                  var8[var9][var11] = readBinary(var0, var1, var2);
               } else {
                  var8[var9][var11] = null;
               }
            }
         }

         return new ParsedValueImpl<>(var8, var5, var3);
      }
   }
}
