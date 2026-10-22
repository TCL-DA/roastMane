package org.glassfish.jersey.jackson.internal.jackson.jaxrs.cfg;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectReader;
import jakarta.ws.rs.core.MultivaluedMap;
import java.io.IOException;

public abstract class ObjectReaderModifier {
   public abstract ObjectReader modify(EndpointConfigBase<?> var1, MultivaluedMap<String, String> var2, JavaType var3, ObjectReader var4, JsonParser var5) throws IOException;
}
