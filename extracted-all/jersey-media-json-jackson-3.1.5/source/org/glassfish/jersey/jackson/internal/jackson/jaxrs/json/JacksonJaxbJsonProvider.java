package org.glassfish.jersey.jackson.internal.jackson.jaxrs.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.ext.Provider;
import org.glassfish.jersey.jackson.internal.jackson.jaxrs.cfg.Annotations;

@Provider
@Consumes({"*/*"})
@Produces({"*/*"})
public class JacksonJaxbJsonProvider extends JacksonJsonProvider {
   public static final Annotations[] DEFAULT_ANNOTATIONS = new Annotations[]{Annotations.JACKSON, Annotations.JAXB};

   public JacksonJaxbJsonProvider() {
      this(null, DEFAULT_ANNOTATIONS);
   }

   public JacksonJaxbJsonProvider(Annotations... annotationsToUse) {
      this(null, annotationsToUse);
   }

   public JacksonJaxbJsonProvider(ObjectMapper mapper, Annotations[] annotationsToUse) {
      super(mapper, annotationsToUse);
   }

   protected JacksonJaxbJsonProvider(JsonMapperConfigurator configurator) {
      super(configurator);
   }
}
