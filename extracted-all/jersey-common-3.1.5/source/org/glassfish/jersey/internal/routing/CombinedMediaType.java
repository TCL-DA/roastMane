package org.glassfish.jersey.internal.routing;

import jakarta.ws.rs.core.MediaType;
import java.util.Comparator;
import org.glassfish.jersey.message.internal.MediaTypes;
import org.glassfish.jersey.message.internal.Quality;
import org.glassfish.jersey.message.internal.QualitySourceMediaType;

public final class CombinedMediaType {
   public static final CombinedMediaType NO_MATCH = new CombinedMediaType(null, 0, 0, 0);
   final MediaType combinedType;
   final int q;
   final int qs;
   final int d;
   public static final Comparator<CombinedMediaType> COMPARATOR = new Comparator<CombinedMediaType>() {
      public int compare(CombinedMediaType c1, CombinedMediaType c2) {
         int delta = MediaTypes.PARTIAL_ORDER_COMPARATOR.compare(c1.combinedType, c2.combinedType);
         if (delta != 0) {
            return delta;
         } else {
            delta = Quality.QUALITY_VALUE_COMPARATOR.compare(c1.q, c2.q);
            if (delta != 0) {
               return delta;
            } else {
               delta = Quality.QUALITY_VALUE_COMPARATOR.compare(c1.qs, c2.qs);
               return delta != 0 ? delta : Integer.compare(c1.d, c2.d);
            }
         }
      }
   };

   private static int matchedWildcards(MediaType clientMt, CombinedMediaType.EffectiveMediaType serverMt) {
      return b2i(clientMt.isWildcardType() ^ serverMt.isWildcardType()) + b2i(clientMt.isWildcardSubtype() ^ serverMt.isWildcardSubType());
   }

   private static int b2i(boolean b) {
      return b ? 1 : 0;
   }

   private CombinedMediaType(MediaType combinedType, int q, int qs, int d) {
      this.combinedType = combinedType;
      this.q = q;
      this.qs = qs;
      this.d = d;
   }

   public MediaType getCombinedType() {
      return this.combinedType;
   }

   public static CombinedMediaType create(MediaType clientType, CombinedMediaType.EffectiveMediaType serverType) {
      if (!clientType.isCompatible(serverType.getMediaType())) {
         return NO_MATCH;
      } else {
         MediaType strippedClientType = MediaTypes.stripQualityParams(clientType);
         MediaType strippedServerType = MediaTypes.stripQualityParams(serverType.getMediaType());
         return new CombinedMediaType(
            MediaTypes.mostSpecific(strippedClientType, strippedServerType),
            MediaTypes.getQuality(clientType),
            QualitySourceMediaType.getQualitySource(serverType.getMediaType()),
            matchedWildcards(clientType, serverType)
         );
      }
   }

   @Override
   public String toString() {
      return String.format("%s;q=%d;qs=%d;d=%d", this.combinedType, this.q, this.qs, this.d);
   }

   public static class EffectiveMediaType {
      private final boolean derived;
      private final MediaType mediaType;

      public EffectiveMediaType(MediaType mediaType, boolean fromMessageBodyProviders) {
         this.derived = fromMessageBodyProviders;
         this.mediaType = mediaType;
      }

      public EffectiveMediaType(String mediaTypeValue) {
         this(MediaType.valueOf(mediaTypeValue), false);
      }

      public EffectiveMediaType(MediaType mediaType) {
         this(mediaType, false);
      }

      public boolean isWildcardType() {
         return this.mediaType.isWildcardType();
      }

      public boolean isWildcardSubType() {
         return this.mediaType.isWildcardSubtype();
      }

      public MediaType getMediaType() {
         return this.mediaType;
      }

      public boolean isDerived() {
         return this.derived;
      }

      @Override
      public String toString() {
         return String.format("mediaType=[%s], fromProviders=%b", this.mediaType, this.derived);
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) {
            return true;
         } else if (!(o instanceof CombinedMediaType.EffectiveMediaType)) {
            return false;
         } else {
            CombinedMediaType.EffectiveMediaType that = (CombinedMediaType.EffectiveMediaType)o;
            return this.derived == that.derived && this.mediaType.equals(that.mediaType);
         }
      }

      @Override
      public int hashCode() {
         int result = this.derived ? 1 : 0;
         return 31 * result + this.mediaType.hashCode();
      }
   }
}
