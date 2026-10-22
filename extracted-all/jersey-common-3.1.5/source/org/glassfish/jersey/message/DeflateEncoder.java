package org.glassfish.jersey.message;

import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Configuration;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;
import org.glassfish.jersey.spi.ContentEncoder;

@Priority(4000)
public class DeflateEncoder extends ContentEncoder {
   private final Configuration config;

   @Inject
   public DeflateEncoder(Configuration config) {
      super("deflate");
      this.config = config;
   }

   @Override
   public InputStream decode(String contentEncoding, InputStream encodedStream) throws IOException {
      InputStream markSupportingStream = (InputStream)(encodedStream.markSupported() ? encodedStream : new BufferedInputStream(encodedStream));
      markSupportingStream.mark(1);
      int firstByte = markSupportingStream.read();
      markSupportingStream.reset();
      return (firstByte & 15) == 8 ? new InflaterInputStream(markSupportingStream) : new InflaterInputStream(markSupportingStream, new Inflater(true));
   }

   @Override
   public OutputStream encode(String contentEncoding, OutputStream entityStream) throws IOException {
      Object value = this.config.getProperty("jersey.config.deflate.nozlib");
      boolean deflateWithoutZLib;
      if (value instanceof String) {
         deflateWithoutZLib = Boolean.valueOf((String)value);
      } else if (value instanceof Boolean) {
         deflateWithoutZLib = (Boolean)value;
      } else {
         deflateWithoutZLib = false;
      }

      return deflateWithoutZLib ? new DeflaterOutputStream(entityStream, new Deflater(-1, true)) : new DeflaterOutputStream(entityStream);
   }
}
