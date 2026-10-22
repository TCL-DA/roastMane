package org.glassfish.jersey.message.internal;

import jakarta.inject.Singleton;
import jakarta.ws.rs.core.CacheControl;
import java.text.ParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.glassfish.jersey.internal.LocalizationMessages;
import org.glassfish.jersey.spi.HeaderDelegateProvider;

@Singleton
public final class CacheControlProvider implements HeaderDelegateProvider<CacheControl> {
   private static final Pattern WHITESPACE = Pattern.compile("\\s");
   private static final Pattern COMMA_SEPARATED_LIST = Pattern.compile("[\\s]*,[\\s]*");

   @Override
   public boolean supports(Class<?> type) {
      return type == CacheControl.class;
   }

   public String toString(CacheControl header) {
      Utils.throwIllegalArgumentExceptionIfNull(header, LocalizationMessages.CACHE_CONTROL_IS_NULL());
      StringBuilder b = new StringBuilder();
      if (header.isPrivate()) {
         this.appendQuotedWithSeparator(b, "private", this.buildListValue(header.getPrivateFields()));
      }

      if (header.isNoCache()) {
         this.appendQuotedWithSeparator(b, "no-cache", this.buildListValue(header.getNoCacheFields()));
      }

      if (header.isNoStore()) {
         this.appendWithSeparator(b, "no-store");
      }

      if (header.isNoTransform()) {
         this.appendWithSeparator(b, "no-transform");
      }

      if (header.isMustRevalidate()) {
         this.appendWithSeparator(b, "must-revalidate");
      }

      if (header.isProxyRevalidate()) {
         this.appendWithSeparator(b, "proxy-revalidate");
      }

      if (header.getMaxAge() != -1) {
         this.appendWithSeparator(b, "max-age", header.getMaxAge());
      }

      if (header.getSMaxAge() != -1) {
         this.appendWithSeparator(b, "s-maxage", header.getSMaxAge());
      }

      for (Entry<String, String> e : header.getCacheExtension().entrySet()) {
         this.appendWithSeparator(b, e.getKey(), this.quoteIfWhitespace(e.getValue()));
      }

      return b.toString();
   }

   private void readFieldNames(List<String> fieldNames, HttpHeaderReader reader) throws ParseException {
      if (reader.hasNextSeparator('=', false)) {
         reader.nextSeparator('=');
         fieldNames.addAll(Arrays.asList(COMMA_SEPARATED_LIST.split(reader.nextQuotedString())));
      }
   }

   private int readIntValue(HttpHeaderReader reader, String directiveName) throws ParseException {
      reader.nextSeparator('=');
      int index = reader.getIndex();

      try {
         return Integer.parseInt(reader.nextToken().toString());
      } catch (NumberFormatException var6) {
         ParseException pe = new ParseException("Error parsing integer value for " + directiveName + " directive", index);
         pe.initCause(var6);
         throw pe;
      }
   }

   private void readDirective(CacheControl cacheControl, HttpHeaderReader reader) throws ParseException {
      String directiveName = reader.nextToken().toString().toLowerCase(Locale.ROOT);
      if ("private".equals(directiveName)) {
         cacheControl.setPrivate(true);
         this.readFieldNames(cacheControl.getPrivateFields(), reader);
      } else if ("public".equals(directiveName)) {
         cacheControl.getCacheExtension().put(directiveName, null);
      } else if ("no-cache".equals(directiveName)) {
         cacheControl.setNoCache(true);
         this.readFieldNames(cacheControl.getNoCacheFields(), reader);
      } else if ("no-store".equals(directiveName)) {
         cacheControl.setNoStore(true);
      } else if ("no-transform".equals(directiveName)) {
         cacheControl.setNoTransform(true);
      } else if ("must-revalidate".equals(directiveName)) {
         cacheControl.setMustRevalidate(true);
      } else if ("proxy-revalidate".equals(directiveName)) {
         cacheControl.setProxyRevalidate(true);
      } else if ("max-age".equals(directiveName)) {
         cacheControl.setMaxAge(this.readIntValue(reader, directiveName));
      } else if ("s-maxage".equals(directiveName)) {
         cacheControl.setSMaxAge(this.readIntValue(reader, directiveName));
      } else {
         String value = null;
         if (reader.hasNextSeparator('=', false)) {
            reader.nextSeparator('=');
            value = reader.nextTokenOrQuotedString().toString();
         }

         cacheControl.getCacheExtension().put(directiveName, value);
      }
   }

   public CacheControl fromString(String header) {
      Utils.throwIllegalArgumentExceptionIfNull(header, LocalizationMessages.CACHE_CONTROL_IS_NULL());

      try {
         HttpHeaderReader reader = HttpHeaderReader.newInstance(header);
         CacheControl cacheControl = new CacheControl();
         cacheControl.setNoTransform(false);

         while (reader.hasNext()) {
            this.readDirective(cacheControl, reader);
            if (reader.hasNextSeparator(',', true)) {
               reader.nextSeparator(',');
            }
         }

         return cacheControl;
      } catch (ParseException var4) {
         throw new IllegalArgumentException("Error parsing cache control '" + header + "'", var4);
      }
   }

   private void appendWithSeparator(StringBuilder b, String field) {
      if (b.length() > 0) {
         b.append(", ");
      }

      b.append(field);
   }

   private void appendQuotedWithSeparator(StringBuilder b, String field, String value) {
      this.appendWithSeparator(b, field);
      if (value != null && !value.isEmpty()) {
         b.append("=\"");
         b.append(value);
         b.append("\"");
      }
   }

   private void appendWithSeparator(StringBuilder b, String field, String value) {
      this.appendWithSeparator(b, field);
      if (value != null && !value.isEmpty()) {
         b.append("=");
         b.append(value);
      }
   }

   private void appendWithSeparator(StringBuilder b, String field, int value) {
      this.appendWithSeparator(b, field);
      b.append("=");
      b.append(value);
   }

   private String buildListValue(List<String> values) {
      StringBuilder b = new StringBuilder();

      for (String value : values) {
         this.appendWithSeparator(b, value);
      }

      return b.toString();
   }

   private String quoteIfWhitespace(String value) {
      if (value == null) {
         return null;
      } else {
         Matcher m = WHITESPACE.matcher(value);
         return m.find() ? "\"" + value + "\"" : value;
      }
   }
}
