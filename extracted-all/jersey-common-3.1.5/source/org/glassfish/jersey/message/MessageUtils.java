package org.glassfish.jersey.message;

import jakarta.ws.rs.core.MediaType;
import java.nio.charset.Charset;
import org.glassfish.jersey.message.internal.ReaderWriter;

public final class MessageUtils {
   public static Charset getCharset(MediaType media) {
      return ReaderWriter.getCharset(media);
   }

   private MessageUtils() {
      throw new AssertionError("No instances allowed.");
   }
}
