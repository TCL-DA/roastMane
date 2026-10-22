package org.glassfish.jersey.message.internal;

import jakarta.ws.rs.core.AbstractMultivaluedMap;
import jakarta.ws.rs.core.Configuration;
import jakarta.ws.rs.core.Cookie;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.ext.RuntimeDelegate;
import jakarta.ws.rs.ext.RuntimeDelegate.HeaderDelegate;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.glassfish.jersey.internal.LocalizationMessages;
import org.glassfish.jersey.internal.RuntimeDelegateDecorator;
import org.glassfish.jersey.internal.util.collection.ImmutableMultivaluedMap;
import org.glassfish.jersey.internal.util.collection.StringKeyIgnoreCaseMultivaluedMap;
import org.glassfish.jersey.internal.util.collection.Views;

public final class HeaderUtils {
   private static final Logger LOGGER = Logger.getLogger(HeaderUtils.class.getName());

   public static AbstractMultivaluedMap<String, String> createInbound() {
      return new StringKeyIgnoreCaseMultivaluedMap();
   }

   public static <V> MultivaluedMap<String, V> empty() {
      return ImmutableMultivaluedMap.empty();
   }

   public static AbstractMultivaluedMap<String, Object> createOutbound() {
      return new StringKeyIgnoreCaseMultivaluedMap();
   }

   public static String asString(Object headerValue, RuntimeDelegate rd) {
      if (headerValue == null) {
         return null;
      } else if (headerValue instanceof String) {
         return (String)headerValue;
      } else {
         if (rd == null) {
            rd = RuntimeDelegate.getInstance();
         }

         HeaderDelegate hp = rd.createHeaderDelegate(headerValue.getClass());
         return hp != null ? hp.toString(headerValue) : headerValue.toString();
      }
   }

   public static String asString(Object headerValue, Configuration configuration) {
      return asString(headerValue, RuntimeDelegateDecorator.configured(configuration));
   }

   public static List<String> asStringList(List<Object> headerValues, RuntimeDelegate rd) {
      return headerValues != null && !headerValues.isEmpty()
         ? Views.listView(headerValues, input -> input == null ? "[null]" : asString(input, rd))
         : Collections.emptyList();
   }

   public static List<String> asStringList(List<Object> headerValues, Configuration configuration) {
      return asStringList(headerValues, RuntimeDelegateDecorator.configured(configuration));
   }

   public static MultivaluedMap<String, String> asStringHeaders(MultivaluedMap<String, Object> headers, Configuration configuration) {
      return headers == null ? null : asStringHeaders(headers, RuntimeDelegateDecorator.configured(configuration));
   }

   public static MultivaluedMap<String, String> asStringHeaders(MultivaluedMap<String, Object> headers, RuntimeDelegate rd) {
      return headers == null ? null : new AbstractMultivaluedMap<String, String>(Views.mapView(headers, input -> asStringList(input, rd))) {
      };
   }

   public static Map<String, String> asStringHeadersSingleValue(MultivaluedMap<String, Object> headers, Configuration configuration) {
      if (headers == null) {
         return null;
      } else {
         RuntimeDelegate rd = RuntimeDelegateDecorator.configured(configuration);
         return Collections.unmodifiableMap(
            headers.entrySet().stream().collect(Collectors.toMap(Entry::getKey, entry -> asHeaderString((List<Object>)entry.getValue(), rd)))
         );
      }
   }

   public static String asHeaderString(List<Object> values, RuntimeDelegate rd) {
      if (values == null) {
         return null;
      } else {
         Iterator<String> stringValues = asStringList(values, rd).iterator();
         if (!stringValues.hasNext()) {
            return "";
         } else {
            StringBuilder buffer = new StringBuilder(stringValues.next());

            while (stringValues.hasNext()) {
               buffer.append(',').append(stringValues.next());
            }

            return buffer.toString();
         }
      }
   }

   public static void checkHeaderChanges(
      Map<String, String> headersSnapshot, MultivaluedMap<String, Object> currentHeaders, String connectorName, Configuration configuration
   ) {
      if (LOGGER.isLoggable(Level.WARNING)) {
         RuntimeDelegate rd = RuntimeDelegateDecorator.configured(configuration);
         Set<String> changedHeaderNames = new HashSet<>();

         for (Entry<? extends String, ? extends List<Object>> entry : currentHeaders.entrySet()) {
            if (!headersSnapshot.containsKey(entry.getKey())) {
               changedHeaderNames.add(entry.getKey());
            } else {
               String prevValue = headersSnapshot.get(entry.getKey());
               String newValue = asHeaderString((List<Object>)currentHeaders.get(entry.getKey()), rd);
               if (!prevValue.equals(newValue)) {
                  changedHeaderNames.add(entry.getKey());
               }
            }
         }

         if (!changedHeaderNames.isEmpty() && LOGGER.isLoggable(Level.WARNING)) {
            LOGGER.warning(LocalizationMessages.SOME_HEADERS_NOT_SENT(connectorName, changedHeaderNames.toString()));
         }
      }
   }

   public static NewCookie getPreferredCookie(NewCookie first, NewCookie second) {
      return Comparator.nullsFirst(
                  Comparator.comparingInt(NewCookie::getMaxAge)
                     .thenComparing(NewCookie::getExpiry, Comparator.nullsLast(Comparator.naturalOrder()))
                     .thenComparing(Cookie::getPath, Comparator.nullsLast(Comparator.comparing(String::length)))
               )
               .compare(first, second)
            > 0
         ? first
         : second;
   }

   @Deprecated
   public static String asString(Object headerValue) {
      return asString(headerValue, (Configuration)null);
   }

   @Deprecated
   public static List<String> asStringList(List<Object> headerValues) {
      return asStringList(headerValues, (Configuration)null);
   }

   @Deprecated
   public static MultivaluedMap<String, String> asStringHeaders(MultivaluedMap<String, Object> headers) {
      return asStringHeaders(headers, (Configuration)null);
   }

   @Deprecated
   public static Map<String, String> asStringHeadersSingleValue(MultivaluedMap<String, Object> headers) {
      return asStringHeadersSingleValue(headers, (Configuration)null);
   }

   @Deprecated
   public static void checkHeaderChanges(Map<String, String> headersSnapshot, MultivaluedMap<String, Object> currentHeaders, String connectorName) {
      checkHeaderChanges(headersSnapshot, currentHeaders, connectorName, (Configuration)null);
   }

   private HeaderUtils() {
      throw new AssertionError("No instances allowed.");
   }
}
