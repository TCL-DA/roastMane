package com.fasterxml.jackson.module.jakarta.xmlbind.ser;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonArrayFormatVisitor;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatTypes;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import jakarta.activation.DataHandler;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;

public class DataHandlerSerializer extends StdSerializer<DataHandler> {
   private static final long serialVersionUID = 1L;

   public DataHandlerSerializer() {
      super(DataHandler.class);
   }

   public void serialize(DataHandler value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      byte[] buffer = new byte[4096];
      InputStream in = value.getInputStream();

      for (int len = in.read(buffer); len > 0; len = in.read(buffer)) {
         out.write(buffer, 0, len);
      }

      in.close();
      jgen.writeBinary(out.toByteArray());
   }

   public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint) throws JsonMappingException {
      if (visitor != null) {
         JsonArrayFormatVisitor v2 = visitor.expectArrayFormat(typeHint);
         if (v2 != null) {
            v2.itemsFormat(JsonFormatTypes.STRING);
         }
      }
   }

   public JsonNode getSchema(SerializerProvider provider, Type typeHint) {
      ObjectNode o = this.createSchemaNode("array", true);
      ObjectNode itemSchema = this.createSchemaNode("string");
      o.set("items", itemSchema);
      return o;
   }
}
