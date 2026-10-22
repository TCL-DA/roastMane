package javafx.css;

import com.sun.javafx.css.ParsedValueImpl;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import javafx.css.converter.URLConverter;

public final class Declaration {
   final String property;
   final ParsedValue parsedValue;
   final boolean important;
   Rule rule;

   Declaration(String var1, ParsedValue var2, boolean var3) {
      this.property = var1;
      this.parsedValue = var2;
      this.important = var3;
      if (var1 == null) {
         throw new IllegalArgumentException("propertyName cannot be null");
      } else if (var2 == null) {
         throw new IllegalArgumentException("parsedValue cannot be null");
      }
   }

   public ParsedValue getParsedValue() {
      return this.parsedValue;
   }

   public String getProperty() {
      return this.property;
   }

   public Rule getRule() {
      return this.rule;
   }

   public final boolean isImportant() {
      return this.important;
   }

   private StyleOrigin getOrigin() {
      Rule var1 = this.getRule();
      return var1 != null ? var1.getOrigin() : null;
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 == null) {
         return false;
      } else if (this.getClass() != var1.getClass()) {
         return false;
      } else {
         Declaration var2 = (Declaration)var1;
         if (this.important != var2.important) {
            return false;
         } else if (this.getOrigin() != var2.getOrigin()) {
            return false;
         } else {
            return (this.property == null ? var2.property == null : this.property.equals(var2.property))
               ? this.parsedValue == var2.parsedValue || this.parsedValue != null && this.parsedValue.equals(var2.parsedValue)
               : false;
         }
      }
   }

   @Override
   public int hashCode() {
      int var1 = 5;
      var1 = 89 * var1 + (this.property != null ? this.property.hashCode() : 0);
      var1 = 89 * var1 + (this.parsedValue != null ? this.parsedValue.hashCode() : 0);
      return 89 * var1 + (this.important ? 1 : 0);
   }

   @Override
   public String toString() {
      StringBuilder var1 = new StringBuilder(this.property);
      var1.append(": ");
      var1.append(this.parsedValue);
      if (this.important) {
         var1.append(" !important");
      }

      return var1.toString();
   }

   void fixUrl(String var1) {
      if (var1 != null) {
         StyleConverter var2 = this.parsedValue.getConverter();
         if (var2 == URLConverter.getInstance()) {
            ParsedValue[] var3 = (ParsedValue[])this.parsedValue.getValue();
            var3[1] = new ParsedValueImpl<>(var1, null);
         } else if (var2 == URLConverter.SequenceConverter.getInstance()) {
            ParsedValue[] var6 = (ParsedValue[])this.parsedValue.getValue();

            for (int var4 = 0; var4 < var6.length; var4++) {
               ParsedValue[] var5 = (ParsedValue[])var6[var4].getValue();
               var5[1] = new ParsedValueImpl<>(var1, null);
            }
         }
      }
   }

   final void writeBinary(DataOutputStream var1, StyleConverter.StringStore var2) throws IOException {
      if (this.parsedValue instanceof ParsedValueImpl) {
         var1.writeShort(var2.addString(this.getProperty()));
         ((ParsedValueImpl)this.parsedValue).writeBinary(var1, var2);
         var1.writeBoolean(this.isImportant());
      }
   }

   static Declaration readBinary(int var0, DataInputStream var1, String[] var2) throws IOException {
      String var3 = var2[var1.readShort()];
      ParsedValueImpl var4 = ParsedValueImpl.readBinary(var0, var1, var2);
      boolean var5 = var1.readBoolean();
      return new Declaration(var3, var4, var5);
   }
}
