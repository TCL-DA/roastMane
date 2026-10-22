package org.glassfish.jersey.server.internal.inject;

import jakarta.ws.rs.core.MultivaluedMap;

final class SingleStringValueExtractor implements MultivaluedParameterExtractor<String> {
   private final String paramName;
   private final String defaultValue;

   public SingleStringValueExtractor(String parameterName, String defaultValue) {
      this.paramName = parameterName;
      this.defaultValue = defaultValue;
   }

   @Override
   public String getName() {
      return this.paramName;
   }

   @Override
   public String getDefaultValueString() {
      return this.defaultValue;
   }

   public String extract(MultivaluedMap<String, String> parameters) {
      String value = (String)parameters.getFirst(this.paramName);
      return value != null ? value : this.defaultValue;
   }
}
