package org.apache.logging.log4j.core.config;

import java.net.URI;
import java.util.Objects;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderFactory;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.Required;

@Plugin(
   name = "MonitorResource",
   category = "Core",
   printObject = true
)
public final class MonitorResource {
   private final URI uri;

   @PluginBuilderFactory
   public static MonitorResource.Builder newBuilder() {
      return new MonitorResource.Builder();
   }

   private MonitorResource(final URI uri) {
      this.uri = Objects.requireNonNull(uri, "uri");
      if (!"file".equals(uri.getScheme())) {
         String message = String.format("Only `file` scheme is supported in monitor resource URIs! Illegal URI: `%s`", uri);
         throw new IllegalArgumentException(message);
      }
   }

   public URI getUri() {
      return this.uri;
   }

   @Override
   public int hashCode() {
      return this.uri.hashCode();
   }

   @Override
   public boolean equals(final Object object) {
      if (this == object) {
         return true;
      } else if (!(object instanceof MonitorResource)) {
         return false;
      } else {
         MonitorResource other = (MonitorResource)object;
         return this.uri == other.uri;
      }
   }

   @Override
   public String toString() {
      return String.format("MonitorResource{%s}", this.uri);
   }

   public static final class Builder implements org.apache.logging.log4j.core.util.Builder<MonitorResource> {
      @PluginBuilderAttribute
      @Required(
         message = "No URI provided"
      )
      private URI uri;

      public MonitorResource.Builder setUri(final URI uri) {
         this.uri = uri;
         return this;
      }

      public MonitorResource build() {
         return new MonitorResource(this.uri);
      }
   }
}
