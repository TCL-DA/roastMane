package org.glassfish.jersey.logging;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.WriterInterceptor;
import jakarta.ws.rs.ext.WriterInterceptorContext;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.glassfish.jersey.internal.guava.Predicates;
import org.glassfish.jersey.message.MessageUtils;

abstract class LoggingInterceptor implements WriterInterceptor {
   static final String REQUEST_PREFIX = "> ";
   static final String RESPONSE_PREFIX = "< ";
   static final String ENTITY_LOGGER_PROPERTY = LoggingFeature.class.getName() + ".entityLogger";
   static final String LOGGING_ID_PROPERTY = LoggingFeature.class.getName() + ".id";
   private static final String NOTIFICATION_PREFIX = "* ";
   private static final MediaType TEXT_MEDIA_TYPE = new MediaType("text", "*");
   private static final MediaType APPLICATION_VND_API_JSON = new MediaType("application", "vnd.api+json");
   private static final Set<MediaType> READABLE_APP_MEDIA_TYPES = new HashSet<MediaType>() {
      {
         this.add(LoggingInterceptor.TEXT_MEDIA_TYPE);
         this.add(LoggingInterceptor.APPLICATION_VND_API_JSON);
         this.add(MediaType.APPLICATION_ATOM_XML_TYPE);
         this.add(MediaType.APPLICATION_FORM_URLENCODED_TYPE);
         this.add(MediaType.APPLICATION_JSON_TYPE);
         this.add(MediaType.APPLICATION_SVG_XML_TYPE);
         this.add(MediaType.APPLICATION_XHTML_XML_TYPE);
         this.add(MediaType.APPLICATION_XML_TYPE);
      }
   };
   private static final Comparator<Entry<String, List<String>>> COMPARATOR = new Comparator<Entry<String, List<String>>>() {
      public int compare(Entry<String, List<String>> o1, Entry<String, List<String>> o2) {
         return o1.getKey().compareToIgnoreCase(o2.getKey());
      }
   };
   final Logger logger;
   final Level level;
   final AtomicLong _id = new AtomicLong(0L);
   final LoggingFeature.Verbosity verbosity;
   final int maxEntitySize;
   final String separator;
   final Predicate<String> redactHeaderPredicate;

   LoggingInterceptor(LoggingFeature.LoggingFeatureBuilder builder) {
      this.logger = builder.filterLogger;
      this.level = builder.level;
      this.verbosity = builder.verbosity;
      this.maxEntitySize = Math.max(0, builder.maxEntitySize);
      this.separator = builder.separator;
      this.redactHeaderPredicate = (Predicate<String>)(builder.redactHeaders != null && !builder.redactHeaders.isEmpty()
         ? new LoggingInterceptor.RedactHeaderPredicate(builder.redactHeaders)
         : header -> false);
   }

   void log(StringBuilder b) {
      if (this.logger != null && this.logger.isLoggable(this.level)) {
         this.logger.log(this.level, b.toString());
      }
   }

   private StringBuilder prefixId(StringBuilder b, long id) {
      b.append(Long.toString(id)).append(" ");
      return b;
   }

   void printRequestLine(StringBuilder b, String note, long id, String method, URI uri) {
      this.prefixId(b, id).append("* ").append(note).append(" on thread ").append(Thread.currentThread().getName()).append(this.separator);
      this.prefixId(b, id).append("> ").append(method).append(" ").append(uri.toASCIIString()).append(this.separator);
   }

   void printResponseLine(StringBuilder b, String note, long id, int status) {
      this.prefixId(b, id).append("* ").append(note).append(" on thread ").append(Thread.currentThread().getName()).append(this.separator);
      this.prefixId(b, id).append("< ").append(Integer.toString(status)).append(this.separator);
   }

   void printPrefixedHeaders(StringBuilder b, long id, String prefix, MultivaluedMap<String, String> headers) {
      for (Entry<String, List<String>> headerEntry : this.getSortedHeaders(headers.entrySet())) {
         List<?> val = headerEntry.getValue();
         String header = headerEntry.getKey();
         this.prefixId(b, id).append(prefix).append(header).append(": ");
         this.getValuesAppender(header, val).accept(b, val);
         b.append(this.separator);
      }
   }

   private BiConsumer<StringBuilder, List<?>> getValuesAppender(String header, List<?> values) {
      if (this.redactHeaderPredicate.test(header)) {
         return (b, v) -> b.append("[redacted]");
      } else {
         return values.size() == 1 ? (b, v) -> b.append(v.get(0)) : (b, v) -> {
            boolean add = false;

            for (Object s : v) {
               if (add) {
                  b.append(',');
               }

               add = true;
               b.append(s);
            }
         };
      }
   }

   Set<Entry<String, List<String>>> getSortedHeaders(Set<Entry<String, List<String>>> headers) {
      TreeSet<Entry<String, List<String>>> sortedHeaders = new TreeSet<>(COMPARATOR);
      sortedHeaders.addAll(headers);
      return sortedHeaders;
   }

   InputStream logInboundEntity(StringBuilder b, InputStream stream, Charset charset) throws IOException {
      if (!stream.markSupported()) {
         stream = new BufferedInputStream(stream);
      }

      stream.mark(this.maxEntitySize + 1);
      byte[] entity = new byte[this.maxEntitySize + 1];
      int entitySize = 0;

      while (entitySize < entity.length) {
         int readBytes = stream.read(entity, entitySize, entity.length - entitySize);
         if (readBytes < 0) {
            break;
         }

         entitySize += readBytes;
      }

      b.append(new String(entity, 0, Math.min(entitySize, this.maxEntitySize), charset));
      if (entitySize > this.maxEntitySize) {
         b.append("...more...");
      }

      b.append('\n');
      stream.reset();
      return stream;
   }

   public void aroundWriteTo(WriterInterceptorContext writerInterceptorContext) throws IOException, WebApplicationException {
      LoggingInterceptor.LoggingStream stream = (LoggingInterceptor.LoggingStream)writerInterceptorContext.getProperty(ENTITY_LOGGER_PROPERTY);
      writerInterceptorContext.proceed();
      if (this.logger.isLoggable(this.level) && printEntity(this.verbosity, writerInterceptorContext.getMediaType()) && stream != null) {
         this.log(stream.getStringBuilder(MessageUtils.getCharset(writerInterceptorContext.getMediaType())));
      }
   }

   static boolean isReadable(MediaType mediaType) {
      if (mediaType != null) {
         for (MediaType readableMediaType : READABLE_APP_MEDIA_TYPES) {
            if (readableMediaType.isCompatible(mediaType)) {
               return true;
            }
         }
      }

      return false;
   }

   static boolean printEntity(LoggingFeature.Verbosity verbosity, MediaType mediaType) {
      return verbosity == LoggingFeature.Verbosity.PAYLOAD_ANY || verbosity == LoggingFeature.Verbosity.PAYLOAD_TEXT && isReadable(mediaType);
   }

   class LoggingStream extends FilterOutputStream {
      private final StringBuilder b;
      private final ByteArrayOutputStream baos = new ByteArrayOutputStream();

      LoggingStream(StringBuilder b, OutputStream inner) {
         super(inner);
         this.b = b;
      }

      StringBuilder getStringBuilder(Charset charset) {
         byte[] entity = this.baos.toByteArray();
         this.b.append(new String(entity, 0, Math.min(entity.length, LoggingInterceptor.this.maxEntitySize), charset));
         if (entity.length > LoggingInterceptor.this.maxEntitySize) {
            this.b.append("...more...");
         }

         this.b.append('\n');
         return this.b;
      }

      @Override
      public void write(int i) throws IOException {
         if (this.baos.size() <= LoggingInterceptor.this.maxEntitySize) {
            this.baos.write(i);
         }

         this.out.write(i);
      }

      @Override
      public void write(byte[] ba, int off, int len) throws IOException {
         if ((off | len | ba.length - (len + off) | off + len) < 0) {
            throw new IndexOutOfBoundsException();
         } else {
            if (this.baos.size() <= LoggingInterceptor.this.maxEntitySize) {
               this.baos.write(ba, off, len);
            }

            this.out.write(ba, off, len);
         }
      }
   }

   private static final class RedactHeaderPredicate implements Predicate<String> {
      private final Set<String> headersToRedact;

      RedactHeaderPredicate(Collection<String> headersToRedact) {
         this.headersToRedact = headersToRedact.stream()
            .filter(Objects::nonNull)
            .filter(Predicates.not(String::isEmpty))
            .map(LoggingInterceptor.RedactHeaderPredicate::normalize)
            .collect(Collectors.toSet());
      }

      public boolean test(String header) {
         return this.headersToRedact.contains(normalize(header));
      }

      private static String normalize(String input) {
         return input.trim().toLowerCase(Locale.ROOT);
      }
   }
}
