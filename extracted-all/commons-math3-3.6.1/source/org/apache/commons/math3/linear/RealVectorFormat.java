package org.apache.commons.math3.linear;

import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.apache.commons.math3.exception.MathParseException;
import org.apache.commons.math3.util.CompositeFormat;

public class RealVectorFormat {
   private static final String DEFAULT_PREFIX = "{";
   private static final String DEFAULT_SUFFIX = "}";
   private static final String DEFAULT_SEPARATOR = "; ";
   private final String prefix;
   private final String suffix;
   private final String separator;
   private final String trimmedPrefix;
   private final String trimmedSuffix;
   private final String trimmedSeparator;
   private final NumberFormat format;

   public RealVectorFormat() {
      this("{", "}", "; ", CompositeFormat.getDefaultNumberFormat());
   }

   public RealVectorFormat(NumberFormat format) {
      this("{", "}", "; ", format);
   }

   public RealVectorFormat(String prefix, String suffix, String separator) {
      this(prefix, suffix, separator, CompositeFormat.getDefaultNumberFormat());
   }

   public RealVectorFormat(String prefix, String suffix, String separator, NumberFormat format) {
      this.prefix = prefix;
      this.suffix = suffix;
      this.separator = separator;
      this.trimmedPrefix = prefix.trim();
      this.trimmedSuffix = suffix.trim();
      this.trimmedSeparator = separator.trim();
      this.format = format;
   }

   public static Locale[] getAvailableLocales() {
      return NumberFormat.getAvailableLocales();
   }

   public String getPrefix() {
      return this.prefix;
   }

   public String getSuffix() {
      return this.suffix;
   }

   public String getSeparator() {
      return this.separator;
   }

   public NumberFormat getFormat() {
      return this.format;
   }

   public static RealVectorFormat getInstance() {
      return getInstance(Locale.getDefault());
   }

   public static RealVectorFormat getInstance(Locale locale) {
      return new RealVectorFormat(CompositeFormat.getDefaultNumberFormat(locale));
   }

   public String format(RealVector v) {
      return this.format(v, new StringBuffer(), new FieldPosition(0)).toString();
   }

   public StringBuffer format(RealVector vector, StringBuffer toAppendTo, FieldPosition pos) {
      pos.setBeginIndex(0);
      pos.setEndIndex(0);
      toAppendTo.append(this.prefix);

      for (int i = 0; i < vector.getDimension(); i++) {
         if (i > 0) {
            toAppendTo.append(this.separator);
         }

         CompositeFormat.formatDouble(vector.getEntry(i), this.format, toAppendTo, pos);
      }

      toAppendTo.append(this.suffix);
      return toAppendTo;
   }

   public ArrayRealVector parse(String source) {
      ParsePosition parsePosition = new ParsePosition(0);
      ArrayRealVector result = this.parse(source, parsePosition);
      if (parsePosition.getIndex() == 0) {
         throw new MathParseException(source, parsePosition.getErrorIndex(), ArrayRealVector.class);
      } else {
         return result;
      }
   }

   public ArrayRealVector parse(String source, ParsePosition pos) {
      int initialIndex = pos.getIndex();
      CompositeFormat.parseAndIgnoreWhitespace(source, pos);
      if (!CompositeFormat.parseFixedstring(source, this.trimmedPrefix, pos)) {
         return null;
      } else {
         List<Number> components = new ArrayList<>();
         boolean loop = true;

         while (loop) {
            if (!components.isEmpty()) {
               CompositeFormat.parseAndIgnoreWhitespace(source, pos);
               if (!CompositeFormat.parseFixedstring(source, this.trimmedSeparator, pos)) {
                  loop = false;
               }
            }

            if (loop) {
               CompositeFormat.parseAndIgnoreWhitespace(source, pos);
               Number component = CompositeFormat.parseNumber(source, this.format, pos);
               if (component == null) {
                  pos.setIndex(initialIndex);
                  return null;
               }

               components.add(component);
            }
         }

         CompositeFormat.parseAndIgnoreWhitespace(source, pos);
         if (!CompositeFormat.parseFixedstring(source, this.trimmedSuffix, pos)) {
            return null;
         } else {
            double[] data = new double[components.size()];

            for (int i = 0; i < data.length; i++) {
               data[i] = components.get(i).doubleValue();
            }

            return new ArrayRealVector(data, false);
         }
      }
   }
}
