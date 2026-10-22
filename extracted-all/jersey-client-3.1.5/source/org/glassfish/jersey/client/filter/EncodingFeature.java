package org.glassfish.jersey.client.filter;

import jakarta.ws.rs.core.Feature;
import jakarta.ws.rs.core.FeatureContext;
import org.glassfish.jersey.internal.inject.Providers;
import org.glassfish.jersey.spi.ContentEncoder;

public class EncodingFeature implements Feature {
   private final String useEncoding;
   private final Class<?>[] encodingProviders;

   public EncodingFeature(Class<?>... encodingProviders) {
      this(null, encodingProviders);
   }

   public EncodingFeature(String useEncoding, Class<?>... encoders) {
      this.useEncoding = useEncoding;
      Providers.ensureContract(ContentEncoder.class, encoders);
      this.encodingProviders = encoders;
   }

   public boolean configure(FeatureContext context) {
      if (this.useEncoding != null && !context.getConfiguration().getProperties().containsKey("jersey.config.client.useEncoding")) {
         context.property("jersey.config.client.useEncoding", this.useEncoding);
      }

      for (Class<?> provider : this.encodingProviders) {
         context.register(provider);
      }

      boolean enable = this.useEncoding != null || this.encodingProviders.length > 0;
      if (enable) {
         context.register(EncodingFilter.class);
      }

      return enable;
   }
}
