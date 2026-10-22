package org.glassfish.jersey.server.filter;

import jakarta.annotation.Priority;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Priority(1000)
public class CsrfProtectionFilter implements ContainerRequestFilter {
   public static final String HEADER_NAME = "X-Requested-By";
   private static final Set<String> METHODS_TO_IGNORE;

   public void filter(ContainerRequestContext rc) throws IOException {
      if (!METHODS_TO_IGNORE.contains(rc.getMethod()) && !rc.getHeaders().containsKey("X-Requested-By")) {
         throw new BadRequestException();
      }
   }

   static {
      HashSet<String> mti = new HashSet<>();
      mti.add("GET");
      mti.add("OPTIONS");
      mti.add("HEAD");
      METHODS_TO_IGNORE = Collections.unmodifiableSet(mti);
   }
}
