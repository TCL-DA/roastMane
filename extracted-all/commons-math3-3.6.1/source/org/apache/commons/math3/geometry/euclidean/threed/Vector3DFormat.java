package org.apache.commons.math3.geometry.euclidean.threed;

import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.Locale;
import org.apache.commons.math3.exception.MathParseException;
import org.apache.commons.math3.geometry.Vector;
import org.apache.commons.math3.geometry.VectorFormat;
import org.apache.commons.math3.util.CompositeFormat;

public class Vector3DFormat extends VectorFormat<Euclidean3D> {
   public Vector3DFormat() {
      super("{", "}", "; ", CompositeFormat.getDefaultNumberFormat());
   }

   public Vector3DFormat(NumberFormat format) {
      super("{", "}", "; ", format);
   }

   public Vector3DFormat(String prefix, String suffix, String separator) {
      super(prefix, suffix, separator, CompositeFormat.getDefaultNumberFormat());
   }

   public Vector3DFormat(String prefix, String suffix, String separator, NumberFormat format) {
      super(prefix, suffix, separator, format);
   }

   public static Vector3DFormat getInstance() {
      return getInstance(Locale.getDefault());
   }

   public static Vector3DFormat getInstance(Locale locale) {
      return new Vector3DFormat(CompositeFormat.getDefaultNumberFormat(locale));
   }

   @Override
   public StringBuffer format(Vector<Euclidean3D> vector, StringBuffer toAppendTo, FieldPosition pos) {
      Vector3D v3 = (Vector3D)vector;
      return this.format(toAppendTo, pos, new double[]{v3.getX(), v3.getY(), v3.getZ()});
   }

   public Vector3D parse(String source) throws MathParseException {
      ParsePosition parsePosition = new ParsePosition(0);
      Vector3D result = this.parse(source, parsePosition);
      if (parsePosition.getIndex() == 0) {
         throw new MathParseException(source, parsePosition.getErrorIndex(), Vector3D.class);
      } else {
         return result;
      }
   }

   public Vector3D parse(String source, ParsePosition pos) {
      double[] coordinates = this.parseCoordinates(3, source, pos);
      return coordinates == null ? null : new Vector3D(coordinates[0], coordinates[1], coordinates[2]);
   }
}
