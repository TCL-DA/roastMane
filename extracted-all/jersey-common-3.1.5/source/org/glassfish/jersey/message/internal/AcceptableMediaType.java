package org.glassfish.jersey.message.internal;

import jakarta.ws.rs.core.MediaType;
import java.text.ParseException;
import java.util.Comparator;
import java.util.Map;

public class AcceptableMediaType extends MediaType implements Qualified {
   public static final Comparator<AcceptableMediaType> COMPARATOR = new Comparator<AcceptableMediaType>() {
      public int compare(AcceptableMediaType o1, AcceptableMediaType o2) {
         int i = Quality.QUALIFIED_COMPARATOR.compare(o1, o2);
         return i != 0 ? i : MediaTypes.PARTIAL_ORDER_COMPARATOR.compare(o1, o2);
      }
   };
   private final int q;

   public AcceptableMediaType(String type, String subtype) {
      super(type, subtype);
      this.q = 1000;
   }

   public AcceptableMediaType(String type, String subtype, int quality, Map<String, String> parameters) {
      super(type, subtype, Quality.enhanceWithQualityParameter(parameters, "q", quality));
      this.q = quality;
   }

   private AcceptableMediaType(String type, String subtype, Map<String, String> parameters, int quality) {
      super(type, subtype, parameters);
      this.q = quality;
   }

   @Override
   public int getQuality() {
      return this.q;
   }

   public static AcceptableMediaType valueOf(HttpHeaderReader reader) throws ParseException {
      reader.hasNext();
      String type = reader.nextToken().toString();
      String subType = "*";
      if (reader.hasNextSeparator('/', false)) {
         reader.next(false);
         subType = reader.nextToken().toString();
      }

      Map<String, String> parameters = null;
      int quality = 1000;
      if (reader.hasNext()) {
         parameters = HttpHeaderReader.readParameters(reader);
         if (parameters != null) {
            String v = parameters.get("q");
            if (v != null) {
               quality = HttpHeaderReader.readQualityFactor(v);
            }
         }
      }

      return new AcceptableMediaType(type, subType, parameters, quality);
   }

   public static AcceptableMediaType valueOf(MediaType mediaType) throws ParseException {
      if (mediaType instanceof AcceptableMediaType) {
         return (AcceptableMediaType)mediaType;
      } else {
         Map<String, String> parameters = mediaType.getParameters();
         int quality = 1000;
         if (parameters != null) {
            String v = parameters.get("q");
            if (v != null) {
               quality = HttpHeaderReader.readQualityFactor(v);
            }
         }

         return new AcceptableMediaType(mediaType.getType(), mediaType.getSubtype(), parameters, quality);
      }
   }

   @Override
   public boolean equals(Object obj) {
      if (!super.equals(obj)) {
         return false;
      } else if (obj instanceof AcceptableMediaType) {
         AcceptableMediaType other = (AcceptableMediaType)obj;
         return this.q == other.q;
      } else {
         return this.q == 1000;
      }
   }

   @Override
   public int hashCode() {
      int hash = super.hashCode();
      return this.q == 1000 ? hash : 47 * hash + this.q;
   }
}
