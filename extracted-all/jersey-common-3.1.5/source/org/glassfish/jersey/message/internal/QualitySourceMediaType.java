package org.glassfish.jersey.message.internal;

import jakarta.ws.rs.core.MediaType;
import java.text.ParseException;
import java.util.Comparator;
import java.util.Map;

public class QualitySourceMediaType extends MediaType implements Qualified {
   public static final Comparator<QualitySourceMediaType> COMPARATOR = new Comparator<QualitySourceMediaType>() {
      public int compare(QualitySourceMediaType o1, QualitySourceMediaType o2) {
         int i = Quality.QUALIFIED_COMPARATOR.compare(o1, o2);
         return i != 0 ? i : MediaTypes.PARTIAL_ORDER_COMPARATOR.compare(o1, o2);
      }
   };
   private final int qs;

   public QualitySourceMediaType(String type, String subtype) {
      super(type, subtype);
      this.qs = 1000;
   }

   public QualitySourceMediaType(String type, String subtype, int quality, Map<String, String> parameters) {
      super(type, subtype, Quality.enhanceWithQualityParameter(parameters, "qs", quality));
      this.qs = quality;
   }

   private QualitySourceMediaType(String type, String subtype, Map<String, String> parameters, int quality) {
      super(type, subtype, parameters);
      this.qs = quality;
   }

   @Override
   public int getQuality() {
      return this.qs;
   }

   public static QualitySourceMediaType valueOf(HttpHeaderReader reader) throws ParseException {
      reader.hasNext();
      String type = reader.nextToken().toString();
      reader.nextSeparator('/');
      String subType = reader.nextToken().toString();
      int qs = 1000;
      Map<String, String> parameters = null;
      if (reader.hasNext()) {
         parameters = HttpHeaderReader.readParameters(reader);
         if (parameters != null) {
            qs = getQs(parameters.get("qs"));
         }
      }

      return new QualitySourceMediaType(type, subType, parameters, qs);
   }

   public static int getQualitySource(MediaType mediaType) throws IllegalArgumentException {
      return mediaType instanceof QualitySourceMediaType ? ((QualitySourceMediaType)mediaType).getQuality() : getQs(mediaType);
   }

   private static int getQs(MediaType mt) throws IllegalArgumentException {
      try {
         return getQs((String)mt.getParameters().get("qs"));
      } catch (ParseException var2) {
         throw new IllegalArgumentException(var2);
      }
   }

   private static int getQs(String v) throws ParseException {
      return v == null ? 1000 : HttpHeaderReader.readQualityFactor(v);
   }

   @Override
   public boolean equals(Object obj) {
      if (!super.equals(obj)) {
         return false;
      } else if (obj instanceof QualitySourceMediaType) {
         QualitySourceMediaType other = (QualitySourceMediaType)obj;
         return this.qs == other.qs;
      } else {
         return this.qs == 1000;
      }
   }

   @Override
   public int hashCode() {
      int hash = super.hashCode();
      return this.qs == 1000 ? hash : 47 * hash + this.qs;
   }

   @Override
   public String toString() {
      return "{" + super.toString() + ", qs=" + this.qs + "}";
   }
}
