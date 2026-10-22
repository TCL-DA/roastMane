package org.glassfish.jersey.client.filter;

import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientRequestFilter;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class CsrfProtectionFilter implements ClientRequestFilter {
   public static final String HEADER_NAME = "X-Requested-By";
   private static final Set<String> METHODS_TO_IGNORE;
   private final String requestedBy;

   public CsrfProtectionFilter() {
      this("");
   }

   public CsrfProtectionFilter(String requestedBy) {
      this.requestedBy = requestedBy;
   }

   public void filter(ClientRequestContext rc) throws IOException {
      if (!METHODS_TO_IGNORE.contains(rc.getMethod()) && !rc.getHeaders().containsKey("X-Requested-By")) {
         rc.getHeaders().add("X-Requested-By", this.requestedBy);
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
