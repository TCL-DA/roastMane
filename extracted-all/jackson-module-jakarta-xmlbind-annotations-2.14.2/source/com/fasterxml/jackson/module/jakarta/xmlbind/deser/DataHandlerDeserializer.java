package com.fasterxml.jackson.module.jakarta.xmlbind.deser;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer;
import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DataHandlerDeserializer extends StdScalarDeserializer<DataHandler> {
   private static final long serialVersionUID = 1L;

   public DataHandlerDeserializer() {
      super(DataHandler.class);
   }

   public DataHandler deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
      final byte[] value = p.getBinaryValue();
      return new DataHandler(new DataSource() {
         public InputStream getInputStream() throws IOException {
            return new ByteArrayInputStream(value);
         }

         public OutputStream getOutputStream() throws IOException {
            throw new IOException();
         }

         public String getContentType() {
            return "application/octet-stream";
         }

         public String getName() {
            return "json-binary-data";
         }
      });
   }
}
