package org.eclipse.jetty.http;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.Map.Entry;
import org.eclipse.jetty.util.Index;

public interface HttpCookie {
   String COMMENT_ATTRIBUTE = "Comment";
   String DOMAIN_ATTRIBUTE = "Domain";
   String EXPIRES_ATTRIBUTE = "Expires";
   String HTTP_ONLY_ATTRIBUTE = "HttpOnly";
   String MAX_AGE_ATTRIBUTE = "Max-Age";
   String PATH_ATTRIBUTE = "Path";
   String SAME_SITE_ATTRIBUTE = "SameSite";
   String SECURE_ATTRIBUTE = "Secure";

   String getName();

   String getValue();

   int getVersion();

   Map<String, String> getAttributes();

   default Instant getExpires() {
      String expires = this.getAttributes().get("Expires");
      return expires == null ? null : parseExpires(expires);
   }

   default long getMaxAge() {
      String ma = this.getAttributes().get("Max-Age");
      return ma == null ? -1L : Long.parseLong(ma);
   }

   default boolean isExpired() {
      if (this.getMaxAge() == 0L) {
         return true;
      } else {
         Instant expires = this.getExpires();
         return expires != null && Instant.now().isAfter(expires);
      }
   }

   default String getComment() {
      return this.getAttributes().get("Comment");
   }

   default String getDomain() {
      return this.getAttributes().get("Domain");
   }

   default String getPath() {
      return this.getAttributes().get("Path");
   }

   default boolean isSecure() {
      return Boolean.parseBoolean(this.getAttributes().get("Secure"));
   }

   default HttpCookie.SameSite getSameSite() {
      return HttpCookie.SameSite.from(this.getAttributes().get("SameSite"));
   }

   default boolean isHttpOnly() {
      return Boolean.parseBoolean(this.getAttributes().get("HttpOnly"));
   }

   @Override
   int hashCode();

   @Override
   boolean equals(Object var1);

   static HttpCookie from(String name, String value) {
      return from(name, value, 0, null);
   }

   static HttpCookie from(String name, String value, Map<String, String> attributes) {
      return from(name, value, 0, attributes);
   }

   static HttpCookie from(String name, String value, int version, Map<String, String> attributes) {
      if (attributes != null && !attributes.isEmpty()) {
         Map<String, String> attrs = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
         attrs.putAll(attributes);
         return new HttpCookie.Immutable(name, value, version, attrs);
      } else {
         return new HttpCookie.Immutable(name, value, version, Collections.emptyMap());
      }
   }

   static HttpCookie from(HttpCookie cookie, String... additionalAttributes) {
      if (additionalAttributes.length % 2 != 0) {
         throw new IllegalArgumentException("additional attributes must have name and value");
      } else {
         Map<String, String> attributes = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
         attributes.putAll(Objects.requireNonNull(cookie).getAttributes());

         for (int i = 0; i < additionalAttributes.length; i += 2) {
            attributes.put(additionalAttributes[i], additionalAttributes[i + 1]);
         }

         return from(cookie.getName(), cookie.getValue(), cookie.getVersion(), attributes);
      }
   }

   static HttpCookie from(java.net.HttpCookie httpCookie) {
      return new HttpCookie.JavaNetHttpCookie(httpCookie);
   }

   static HttpCookie.Builder build(String name, String value) {
      return build(name, value, 0);
   }

   static HttpCookie.Builder build(String name, String value, int version) {
      return new HttpCookie.Builder(name, value, version);
   }

   static HttpCookie.Builder build(HttpCookie httpCookie) {
      HttpCookie.Builder builder = new HttpCookie.Builder(httpCookie.getName(), httpCookie.getValue(), httpCookie.getVersion());

      for (Entry<String, String> entry : httpCookie.getAttributes().entrySet()) {
         builder = builder.attribute(entry.getKey(), entry.getValue());
      }

      return builder;
   }

   static HttpCookie.Builder build(java.net.HttpCookie httpCookie) {
      return new HttpCookie.Builder(httpCookie.getName(), httpCookie.getValue(), httpCookie.getVersion())
         .comment(httpCookie.getComment())
         .domain(httpCookie.getDomain())
         .httpOnly(httpCookie.isHttpOnly())
         .maxAge(httpCookie.getMaxAge())
         .path(httpCookie.getPath())
         .secure(httpCookie.getSecure());
   }

   static java.net.HttpCookie asJavaNetHttpCookie(HttpCookie httpCookie) {
      if (httpCookie.getSameSite() != null) {
         throw new IllegalArgumentException("SameSite attribute not supported by " + java.net.HttpCookie.class.getName());
      } else {
         java.net.HttpCookie cookie = new java.net.HttpCookie(httpCookie.getName(), httpCookie.getValue());
         cookie.setVersion(httpCookie.getVersion());
         cookie.setComment(httpCookie.getComment());
         cookie.setDomain(httpCookie.getDomain());
         cookie.setHttpOnly(httpCookie.isHttpOnly());
         cookie.setMaxAge(httpCookie.getMaxAge());
         cookie.setPath(httpCookie.getPath());
         cookie.setSecure(httpCookie.isSecure());
         return cookie;
      }
   }

   static int hashCode(HttpCookie httpCookie) {
      String domain = httpCookie.getDomain();
      if (domain != null) {
         domain = domain.toLowerCase(Locale.ENGLISH);
      }

      return Objects.hash(httpCookie.getName(), domain, httpCookie.getPath());
   }

   static boolean equals(HttpCookie cookie1, Object obj) {
      if (cookie1 == obj) {
         return true;
      } else if (cookie1 == null || obj == null) {
         return false;
      } else if (obj instanceof HttpCookie cookie2) {
         if (!Objects.equals(cookie1.getName(), cookie2.getName())) {
            return false;
         } else {
            return !equalsIgnoreCase(cookie1.getDomain(), cookie2.getDomain()) ? false : Objects.equals(cookie1.getPath(), cookie2.getPath());
         }
      } else {
         return false;
      }
   }

   private static boolean equalsIgnoreCase(String obj1, String obj2) {
      if (obj1 == obj2) {
         return true;
      } else {
         return obj1 != null && obj2 != null ? obj1.equalsIgnoreCase(obj2) : false;
      }
   }

   private static String asString(HttpCookie httpCookie) {
      StringBuilder builder = new StringBuilder();
      builder.append(httpCookie.getName()).append("=").append(httpCookie.getValue());
      Map<String, String> attributes = httpCookie.getAttributes();
      if (!attributes.isEmpty()) {
         for (Entry<String, String> entry : attributes.entrySet()) {
            builder.append("; ");
            builder.append(entry.getKey()).append("=").append(entry.getValue());
         }
      }

      return builder.toString();
   }

   static String toString(HttpCookie httpCookie) {
      return "%s@%x[%s]".formatted(httpCookie.getClass().getSimpleName(), httpCookie.hashCode(), asString(httpCookie));
   }

   static String formatExpires(Instant expires) {
      return DateTimeFormatter.RFC_1123_DATE_TIME.withZone(ZoneOffset.UTC).format(expires);
   }

   static Instant parseExpires(String expires) {
      return ZonedDateTime.parse(expires, DateTimeFormatter.RFC_1123_DATE_TIME).toInstant();
   }

   private static Map<String, String> lazyAttributePut(Map<String, String> attributes, String key, String value) {
      if (value == null) {
         return lazyAttributeRemove(attributes, key);
      } else {
         if (attributes == null) {
            attributes = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
         }

         attributes.put(key, value);
         return attributes;
      }
   }

   private static Map<String, String> lazyAttributeRemove(Map<String, String> attributes, String key) {
      if (attributes == null) {
         return null;
      } else {
         attributes.remove(key);
         return attributes;
      }
   }

   private static Map<String, String> lazyAttributes(Map<String, String> attributes) {
      return attributes != null && !attributes.isEmpty() ? Collections.unmodifiableMap(attributes) : Collections.emptyMap();
   }

   public static class Builder {
      private final String _name;
      private final String _value;
      private final int _version;
      private Map<String, String> _attributes;

      private Builder(String name, String value, int version) {
         this._name = name;
         this._value = value;
         this._version = version;
      }

      public HttpCookie.Builder attribute(String name, String value) {
         if (name == null) {
            return this;
         } else {
            String var3 = name.toLowerCase(Locale.ENGLISH);
            switch (var3) {
               case "expires":
                  this.expires(HttpCookie.parseExpires(value));
                  break;
               case "httponly":
                  if (!this.isTruthy(value)) {
                     throw new IllegalArgumentException("Invalid HttpOnly attribute");
                  }

                  this.httpOnly(true);
                  break;
               case "max-age":
                  this.maxAge(Long.parseLong(value));
                  break;
               case "samesite":
                  HttpCookie.SameSite sameSite = HttpCookie.SameSite.from(value);
                  if (sameSite == null) {
                     throw new IllegalArgumentException("Invalid SameSite attribute");
                  }

                  this.sameSite(sameSite);
                  break;
               case "secure":
                  if (!this.isTruthy(value)) {
                     throw new IllegalArgumentException("Invalid Secure attribute");
                  }

                  this.secure(true);
                  break;
               default:
                  this._attributes = HttpCookie.lazyAttributePut(this._attributes, name, value);
            }

            return this;
         }
      }

      private boolean isTruthy(String value) {
         return value != null && (value.isEmpty() || "true".equalsIgnoreCase(value));
      }

      public HttpCookie.Builder comment(String comment) {
         this._attributes = HttpCookie.lazyAttributePut(this._attributes, "Comment", comment);
         return this;
      }

      public HttpCookie.Builder domain(String domain) {
         this._attributes = HttpCookie.lazyAttributePut(this._attributes, "Domain", domain);
         return this;
      }

      public HttpCookie.Builder httpOnly(boolean httpOnly) {
         if (httpOnly) {
            this._attributes = HttpCookie.lazyAttributePut(this._attributes, "HttpOnly", Boolean.TRUE.toString());
         } else {
            this._attributes = HttpCookie.lazyAttributeRemove(this._attributes, "HttpOnly");
         }

         return this;
      }

      public HttpCookie.Builder maxAge(long maxAge) {
         if (maxAge >= 0L) {
            this._attributes = HttpCookie.lazyAttributePut(this._attributes, "Max-Age", Long.toString(maxAge));
         } else {
            this._attributes = HttpCookie.lazyAttributeRemove(this._attributes, "Max-Age");
         }

         return this;
      }

      public HttpCookie.Builder expires(Instant expires) {
         if (expires != null) {
            this._attributes = HttpCookie.lazyAttributePut(this._attributes, "Expires", HttpCookie.formatExpires(expires));
         } else {
            this._attributes = HttpCookie.lazyAttributeRemove(this._attributes, "Expires");
         }

         return this;
      }

      public HttpCookie.Builder path(String path) {
         this._attributes = HttpCookie.lazyAttributePut(this._attributes, "Path", path);
         return this;
      }

      public HttpCookie.Builder secure(boolean secure) {
         if (secure) {
            this._attributes = HttpCookie.lazyAttributePut(this._attributes, "Secure", Boolean.TRUE.toString());
         } else {
            this._attributes = HttpCookie.lazyAttributeRemove(this._attributes, "Secure");
         }

         return this;
      }

      public HttpCookie.Builder sameSite(HttpCookie.SameSite sameSite) {
         this._attributes = HttpCookie.lazyAttributePut(this._attributes, "SameSite", sameSite.attributeValue);
         return this;
      }

      public HttpCookie build() {
         return new HttpCookie.Immutable(this._name, this._value, this._version, HttpCookie.lazyAttributes(this._attributes));
      }
   }

   public static class Immutable implements HttpCookie {
      private final String _name;
      private final String _value;
      private final int _version;
      private final Map<String, String> _attributes;

      private Immutable(String name, String value, int version, Map<String, String> attributes) {
         this._name = name;
         this._value = value;
         this._version = version;
         this._attributes = attributes != null && !attributes.isEmpty() ? attributes : Collections.emptyMap();
      }

      @Override
      public String getName() {
         return this._name;
      }

      @Override
      public String getValue() {
         return this._value;
      }

      @Override
      public int getVersion() {
         return this._version;
      }

      @Override
      public Map<String, String> getAttributes() {
         return this._attributes;
      }

      @Override
      public int hashCode() {
         return HttpCookie.hashCode(this);
      }

      @Override
      public boolean equals(Object obj) {
         return HttpCookie.equals(this, obj);
      }

      @Override
      public String toString() {
         return HttpCookie.toString(this);
      }
   }

   public static class JavaNetHttpCookie implements HttpCookie {
      private final java.net.HttpCookie _httpCookie;
      private Map<String, String> _attributes;

      private JavaNetHttpCookie(java.net.HttpCookie httpCookie) {
         this._httpCookie = httpCookie;
      }

      @Override
      public String getComment() {
         return this._httpCookie.getComment();
      }

      @Override
      public String getDomain() {
         return this._httpCookie.getDomain();
      }

      @Override
      public long getMaxAge() {
         return this._httpCookie.getMaxAge();
      }

      @Override
      public String getPath() {
         return this._httpCookie.getPath();
      }

      @Override
      public boolean isSecure() {
         return this._httpCookie.getSecure();
      }

      @Override
      public String getName() {
         return this._httpCookie.getName();
      }

      @Override
      public String getValue() {
         return this._httpCookie.getValue();
      }

      @Override
      public int getVersion() {
         return this._httpCookie.getVersion();
      }

      @Override
      public boolean isHttpOnly() {
         return this._httpCookie.isHttpOnly();
      }

      @Override
      public Map<String, String> getAttributes() {
         if (this._attributes == null) {
            Map<String, String> attributes = HttpCookie.lazyAttributePut(null, "Comment", this.getComment());
            attributes = HttpCookie.lazyAttributePut(attributes, "Domain", this.getDomain());
            if (this.isHttpOnly()) {
               attributes = HttpCookie.lazyAttributePut(attributes, "HttpOnly", Boolean.TRUE.toString());
            }

            if (this.getMaxAge() >= 0L) {
               attributes = HttpCookie.lazyAttributePut(attributes, "Max-Age", Long.toString(this.getMaxAge()));
            }

            attributes = HttpCookie.lazyAttributePut(attributes, "Path", this.getPath());
            if (this.isSecure()) {
               attributes = HttpCookie.lazyAttributePut(attributes, "Secure", Boolean.TRUE.toString());
            }

            this._attributes = HttpCookie.lazyAttributes(attributes);
         }

         return this._attributes;
      }

      @Override
      public int hashCode() {
         return HttpCookie.hashCode(this);
      }

      @Override
      public boolean equals(Object obj) {
         return HttpCookie.equals(this, obj);
      }

      @Override
      public String toString() {
         return HttpCookie.toString(this);
      }
   }

   public static enum SameSite {
      NONE("None"),
      STRICT("Strict"),
      LAX("Lax");

      private final String attributeValue;
      private static final Index<HttpCookie.SameSite> CACHE;

      private SameSite(String attributeValue) {
         this.attributeValue = attributeValue;
      }

      public String getAttributeValue() {
         return this.attributeValue;
      }

      public static HttpCookie.SameSite from(String sameSite) {
         return sameSite == null ? null : (HttpCookie.SameSite)CACHE.get(sameSite);
      }

      static {
         CACHE = new org.eclipse.jetty.util.Index.Builder()
            .caseSensitive(false)
            .with(NONE.attributeValue, NONE)
            .with(STRICT.attributeValue, STRICT)
            .with(LAX.attributeValue, LAX)
            .build();
      }
   }

   public static class Wrapper implements HttpCookie {
      private final HttpCookie wrapped;

      public Wrapper(HttpCookie wrapped) {
         this.wrapped = Objects.requireNonNull(wrapped);
      }

      public HttpCookie getWrapped() {
         return this.wrapped;
      }

      @Override
      public String getName() {
         return this.getWrapped().getName();
      }

      @Override
      public String getValue() {
         return this.getWrapped().getValue();
      }

      @Override
      public int getVersion() {
         return this.getWrapped().getVersion();
      }

      @Override
      public Map<String, String> getAttributes() {
         return this.getWrapped().getAttributes();
      }

      @Override
      public Instant getExpires() {
         return this.getWrapped().getExpires();
      }

      @Override
      public long getMaxAge() {
         return this.getWrapped().getMaxAge();
      }

      @Override
      public boolean isExpired() {
         return this.getWrapped().isExpired();
      }

      @Override
      public String getComment() {
         return this.getWrapped().getComment();
      }

      @Override
      public String getDomain() {
         return this.getWrapped().getDomain();
      }

      @Override
      public String getPath() {
         return this.getWrapped().getPath();
      }

      @Override
      public boolean isSecure() {
         return this.getWrapped().isSecure();
      }

      @Override
      public HttpCookie.SameSite getSameSite() {
         return this.getWrapped().getSameSite();
      }

      @Override
      public boolean isHttpOnly() {
         return this.getWrapped().isHttpOnly();
      }

      @Override
      public int hashCode() {
         return HttpCookie.hashCode(this);
      }

      @Override
      public boolean equals(Object obj) {
         return HttpCookie.equals(this, obj);
      }

      @Override
      public String toString() {
         return HttpCookie.toString(this);
      }
   }
}
