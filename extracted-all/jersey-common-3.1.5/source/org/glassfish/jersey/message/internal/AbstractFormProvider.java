package org.glassfish.jersey.message.internal;

import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Map.Entry;

public abstract class AbstractFormProvider<T> extends AbstractMessageReaderWriterProvider<T> {
   public <M extends MultivaluedMap<String, String>> M readFrom(M map, MediaType mediaType, boolean decode, InputStream entityStream) throws IOException {
      String encoded = ReaderWriter.readFromAsString(entityStream, mediaType);
      String charsetName = ReaderWriter.getCharset(mediaType).name();
      StringTokenizer tokenizer = new StringTokenizer(encoded, "&");

      try {
         while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            int idx = token.indexOf(61);
            if (idx < 0) {
               map.add(decode ? URLDecoder.decode(token, charsetName) : token, null);
            } else if (idx > 0) {
               if (decode) {
                  map.add(URLDecoder.decode(token.substring(0, idx), charsetName), URLDecoder.decode(token.substring(idx + 1), charsetName));
               } else {
                  map.add(token.substring(0, idx), token.substring(idx + 1));
               }
            }
         }

         return map;
      } catch (IllegalArgumentException var10) {
         throw new BadRequestException(var10);
      }
   }

   public <M extends MultivaluedMap<String, String>> void writeTo(M t, MediaType mediaType, OutputStream entityStream) throws IOException {
      String charsetName = ReaderWriter.getCharset(mediaType).name();
      StringBuilder sb = new StringBuilder();

      for (Entry<String, List<String>> e : t.entrySet()) {
         for (String value : e.getValue()) {
            if (sb.length() > 0) {
               sb.append('&');
            }

            sb.append(URLEncoder.encode(e.getKey(), charsetName));
            if (value != null) {
               sb.append('=');
               sb.append(URLEncoder.encode(value, charsetName));
            }
         }
      }

      ReaderWriter.writeToAsString(sb.toString(), entityStream, mediaType);
   }
}
