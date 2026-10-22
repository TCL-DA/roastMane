package org.glassfish.jersey.client.filter;

import jakarta.inject.Inject;
import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientRequestFilter;
import jakarta.ws.rs.core.Context;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Logger;
import org.glassfish.jersey.client.internal.LocalizationMessages;
import org.glassfish.jersey.internal.inject.InjectionManager;
import org.glassfish.jersey.spi.ContentEncoder;

public final class EncodingFilter implements ClientRequestFilter {
   private final InjectionManager injectionManager;
   private volatile List<Object> supportedEncodings = null;

   @Inject
   public EncodingFilter(@Context InjectionManager injectionManager) {
      this.injectionManager = injectionManager;
   }

   public void filter(ClientRequestContext request) throws IOException {
      if (!this.getSupportedEncodings().isEmpty()) {
         request.getHeaders().addAll("Accept-Encoding", this.getSupportedEncodings());
         String useEncoding = (String)request.getConfiguration().getProperty("jersey.config.client.useEncoding");
         if (useEncoding != null) {
            if (!this.getSupportedEncodings().contains(useEncoding)) {
               Logger.getLogger(this.getClass().getName())
                  .warning(LocalizationMessages.USE_ENCODING_IGNORED("jersey.config.client.useEncoding", useEncoding, this.getSupportedEncodings()));
            } else if (request.hasEntity() && request.getHeaders().getFirst("Content-Encoding") == null) {
               request.getHeaders().putSingle("Content-Encoding", useEncoding);
            }
         }
      }
   }

   List<Object> getSupportedEncodings() {
      if (this.supportedEncodings == null) {
         SortedSet<String> se = new TreeSet<>();

         for (ContentEncoder encoder : this.injectionManager.getAllInstances(ContentEncoder.class)) {
            se.addAll(encoder.getSupportedEncodings());
         }

         this.supportedEncodings = new ArrayList<>(se);
      }

      return this.supportedEncodings;
   }
}
