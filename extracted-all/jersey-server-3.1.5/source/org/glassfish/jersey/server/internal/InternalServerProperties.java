package org.glassfish.jersey.server.internal;

import org.glassfish.jersey.internal.util.PropertiesClass;

@PropertiesClass
public final class InternalServerProperties {
   public static final String FORM_PROPERTY = "jersey.config.server.representation.form";
   public static final String FORM_DECODED_PROPERTY = "jersey.config.server.representation.decoded.form";

   private InternalServerProperties() {
   }
}
