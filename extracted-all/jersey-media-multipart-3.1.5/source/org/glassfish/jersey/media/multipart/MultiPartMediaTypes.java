package org.glassfish.jersey.media.multipart;

import jakarta.ws.rs.core.MediaType;
import java.util.Collections;

public final class MultiPartMediaTypes {
   public static final String MULTIPART_ALTERNATIVE = "multipart/alternative";
   public static final MediaType MULTIPART_ALTERNATIVE_TYPE = new MediaType("multipart", "alternative");
   public static final String MULTIPART_DIGEST = "multipart/digest";
   public static final MediaType MULTIPART_DIGEST_TYPE = new MediaType("multipart", "digest");
   public static final String MULTIPART_MIXED = "multipart/mixed";
   public static final MediaType MULTIPART_MIXED_TYPE = new MediaType("multipart", "mixed");
   public static final String MULTIPART_PARALLEL = "multipart/parallel";
   public static final MediaType MULTIPART_PARELLEL_TYPE = new MediaType("multipart", "parallel");

   public static MediaType createAlternative() {
      return create(MULTIPART_ALTERNATIVE_TYPE);
   }

   public static MediaType createDigest() {
      return create(MULTIPART_DIGEST_TYPE);
   }

   public static MediaType createMixed() {
      return create(MULTIPART_MIXED_TYPE);
   }

   public static MediaType createParallel() {
      return create(MULTIPART_PARELLEL_TYPE);
   }

   public static MediaType createFormData() {
      return create(MediaType.MULTIPART_FORM_DATA_TYPE);
   }

   private static MediaType create(MediaType mt) {
      return new MediaType(mt.getType(), mt.getSubtype(), Collections.singletonMap("boundary", Boundary.createBoundary()));
   }
}
