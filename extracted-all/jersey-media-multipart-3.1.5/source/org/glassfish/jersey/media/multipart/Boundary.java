package org.glassfish.jersey.media.multipart;

import jakarta.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public final class Boundary {
   public static final String BOUNDARY_PARAMETER = "boundary";
   private static final AtomicInteger boundaryCounter = new AtomicInteger();

   public static MediaType addBoundary(MediaType mediaType) {
      if (mediaType == null) {
         return MultiPartMediaTypes.createMixed();
      } else if (!mediaType.getParameters().containsKey("boundary")) {
         Map<String, String> parameters = new HashMap<>(mediaType.getParameters());
         parameters.put("boundary", createBoundary());
         return new MediaType(mediaType.getType(), mediaType.getSubtype(), parameters);
      } else {
         return mediaType;
      }
   }

   public static String createBoundary() {
      return "Boundary_" + boundaryCounter.incrementAndGet() + '_' + new Object().hashCode() + '_' + System.currentTimeMillis();
   }
}
