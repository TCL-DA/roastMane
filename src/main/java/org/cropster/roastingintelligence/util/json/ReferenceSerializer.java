package org.cropster.roastingintelligence.util.json;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import java.io.IOException;

public class ReferenceSerializer extends StdSerializer<Long> {
   private static final long serialVersionUID = 1L;

   public ReferenceSerializer() {
      super(Long.class, true);
   }

   public void serialize(Long value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonGenerationException {
      jgen.writeStartObject();
      jgen.writeFieldName("id");
      jgen.writeNumber(value);
      jgen.writeEndObject();
   }
}
