package org.glassfish.jersey.internal;

import org.glassfish.jersey.internal.util.PropertiesClass;

@PropertiesClass
public class InternalProperties {
   public static final String JSON_FEATURE = "jersey.config.jsonFeature";
   public static final String JSON_FEATURE_CLIENT = "jersey.config.client.jsonFeature";
   public static final String JSON_FEATURE_SERVER = "jersey.config.server.jsonFeature";

   private InternalProperties() {
   }
}
