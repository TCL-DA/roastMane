package org.eclipse.jetty.server;

import java.net.HttpCookie;
import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import org.eclipse.jetty.http.CookieCompliance;
import org.eclipse.jetty.http.HttpField;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.Syntax;
import org.eclipse.jetty.http.HttpCookie.SameSite;
import org.eclipse.jetty.util.Attributes;
import org.eclipse.jetty.util.Index;
import org.eclipse.jetty.util.QuotedStringTokenizer;
import org.eclipse.jetty.util.Index.Builder;

public final class HttpCookieUtils {
   public static final String SAME_SITE_DEFAULT_ATTRIBUTE = "org.eclipse.jetty.cookie.sameSiteDefault";
   private static final Index<String> KNOWN_ATTRIBUTES = new Builder()
      .caseSensitive(false)
      .with("Comment")
      .with("Domain")
      .with("Expires")
      .with("HttpOnly")
      .with("Max-Age")
      .with("Path")
      .with("SameSite")
      .with("Secure")
      .build();
   private static final String EPOCH_EXPIRES = "Thu, 01 Jan 1970 00:00:00 GMT";

   public static org.eclipse.jetty.http.HttpCookie checkSameSite(org.eclipse.jetty.http.HttpCookie cookie, Attributes attributes) {
      if (cookie != null && cookie.getSameSite() == null) {
         SameSite contextDefault = getSameSiteDefault(attributes);
         return contextDefault == null ? cookie : org.eclipse.jetty.http.HttpCookie.from(cookie, new String[]{"SameSite", contextDefault.getAttributeValue()});
      } else {
         return cookie;
      }
   }

   public static SameSite getSameSiteDefault(Attributes contextAttributes) {
      if (contextAttributes == null) {
         return null;
      } else {
         Object o = contextAttributes.getAttribute("org.eclipse.jetty.cookie.sameSiteDefault");
         if (o == null) {
            return null;
         } else if (o instanceof SameSite) {
            return (SameSite)o;
         } else {
            try {
               SameSite samesite = Enum.valueOf(SameSite.class, o.toString().trim().toUpperCase(Locale.ENGLISH));
               contextAttributes.setAttribute("org.eclipse.jetty.cookie.sameSiteDefault", samesite);
               return samesite;
            } catch (Exception var3) {
               throw new IllegalStateException(var3);
            }
         }
      }
   }

   public static String getSetCookie(org.eclipse.jetty.http.HttpCookie httpCookie, CookieCompliance compliance) {
      return compliance != null && !CookieCompliance.RFC6265_LEGACY.compliesWith(compliance)
         ? getRFC2965SetCookie(httpCookie)
         : getRFC6265SetCookie(httpCookie);
   }

   public static String getRFC2965SetCookie(org.eclipse.jetty.http.HttpCookie httpCookie) {
      String name = httpCookie.getName();
      if (name != null && name.length() != 0) {
         StringBuilder builder = new StringBuilder();
         quoteIfNeededAndAppend(name, builder);
         builder.append('=');
         String value = httpCookie.getValue();
         quoteIfNeededAndAppend(value, builder);
         String domain = httpCookie.getDomain();
         boolean hasDomain = domain != null && domain.length() > 0;
         boolean quoteDomain = hasDomain && isQuoteNeeded(domain);
         String path = httpCookie.getPath();
         boolean hasPath = path != null && path.length() > 0;
         boolean quotePath = hasPath && isQuoteNeeded(path);
         int version = httpCookie.getVersion();
         String comment = httpCookie.getComment();
         if (version == 0
            && (
               comment != null
                  || isQuoteNeeded(name)
                  || isQuoteNeeded(value)
                  || quoteDomain
                  || quotePath
                  || QuotedStringTokenizer.isQuoted(name)
                  || QuotedStringTokenizer.isQuoted(value)
                  || QuotedStringTokenizer.isQuoted(path)
                  || QuotedStringTokenizer.isQuoted(domain)
            )) {
            version = 1;
         }

         if (version == 1) {
            builder.append(";Version=1");
         } else if (version > 1) {
            builder.append(";Version=").append(version);
         }

         if (hasDomain) {
            builder.append(";Domain=");
            if (quoteDomain) {
               HttpField.PARAMETER_TOKENIZER.quote(builder, domain);
            } else {
               builder.append(domain);
            }
         }

         if (hasPath) {
            builder.append(";Path=");
            if (quotePath) {
               HttpField.PARAMETER_TOKENIZER.quote(builder, path);
            } else {
               builder.append(path);
            }
         }

         long maxAge = httpCookie.getMaxAge();
         if (maxAge >= 0L) {
            builder.append(";Expires=");
            if (maxAge == 0L) {
               builder.append("Thu, 01 Jan 1970 00:00:00 GMT");
            } else {
               builder.append(org.eclipse.jetty.http.HttpCookie.formatExpires(Instant.now().plusSeconds(maxAge)));
            }

            builder.append(";Max-Age=");
            builder.append(maxAge);
         }

         if (httpCookie.isSecure()) {
            builder.append(";Secure");
         }

         if (httpCookie.isHttpOnly()) {
            builder.append(";HttpOnly");
         }

         SameSite sameSite = httpCookie.getSameSite();
         if (sameSite != null) {
            builder.append(";SameSite=").append(sameSite.getAttributeValue());
         }

         if (comment != null) {
            builder.append(";Comment=");
            quoteIfNeededAndAppend(comment, builder);
         }

         return builder.toString();
      } else {
         throw new IllegalArgumentException("Invalid cookie name");
      }
   }

   public static String getRFC6265SetCookie(org.eclipse.jetty.http.HttpCookie httpCookie) {
      String name = httpCookie.getName();
      if (name != null && name.length() != 0) {
         Syntax.requireValidRFC2616Token(name, "RFC6265 Cookie name");
         String value = httpCookie.getValue();
         Syntax.requireValidRFC6265CookieValue(value);
         StringBuilder builder = new StringBuilder();
         builder.append(name).append('=').append(value == null ? "" : value);
         String path = httpCookie.getPath();
         if (path != null && path.length() > 0) {
            builder.append("; Path=").append(path);
         }

         String domain = httpCookie.getDomain();
         if (domain != null && domain.length() > 0) {
            builder.append("; Domain=").append(domain);
         }

         long maxAge = httpCookie.getMaxAge();
         if (maxAge >= 0L) {
            builder.append("; Expires=");
            if (maxAge == 0L) {
               builder.append("Thu, 01 Jan 1970 00:00:00 GMT");
            } else {
               builder.append(org.eclipse.jetty.http.HttpCookie.formatExpires(Instant.now().plusSeconds(maxAge)));
            }

            builder.append("; Max-Age=");
            builder.append(maxAge);
         }

         if (httpCookie.isSecure()) {
            builder.append("; Secure");
         }

         if (httpCookie.isHttpOnly()) {
            builder.append("; HttpOnly");
         }

         Map<String, String> attributes = httpCookie.getAttributes();
         String sameSiteAttr = attributes.get("SameSite");
         if (sameSiteAttr != null) {
            builder.append("; SameSite=");
            builder.append(sameSiteAttr);
         } else {
            SameSite sameSite = httpCookie.getSameSite();
            if (sameSite != null) {
               builder.append("; SameSite=");
               builder.append(sameSite.getAttributeValue());
            }
         }

         for (Entry<String, String> e : attributes.entrySet()) {
            if (!KNOWN_ATTRIBUTES.contains(e.getKey())) {
               builder.append("; ").append(e.getKey()).append("=");
               builder.append(e.getValue());
            }
         }

         return builder.toString();
      } else {
         throw new IllegalArgumentException("Bad cookie name");
      }
   }

   private static boolean isQuoteNeeded(String text) {
      if (text == null || text.length() == 0) {
         return true;
      } else if (QuotedStringTokenizer.isQuoted(text)) {
         return false;
      } else {
         for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if ("\",;\\ \t".indexOf(c) >= 0) {
               return true;
            }

            if (c < ' ' || c >= 127) {
               throw new IllegalArgumentException("Illegal character in cookie value");
            }
         }

         return false;
      }
   }

   public static boolean match(String setCookieHeader, String name, String domain, String path) {
      List<HttpCookie> cookies = HttpCookie.parse(setCookieHeader);
      if (cookies.size() != 1) {
         return false;
      } else {
         HttpCookie cookie = cookies.get(0);
         return match(cookie.getName(), cookie.getDomain(), cookie.getPath(), name, domain, path);
      }
   }

   public static boolean match(org.eclipse.jetty.http.HttpCookie cookie, String name, String domain, String path) {
      return cookie == null ? false : match(cookie.getName(), cookie.getDomain(), cookie.getPath(), name, domain, path);
   }

   private static boolean match(String oldName, String oldDomain, String oldPath, String newName, String newDomain, String newPath) {
      if (oldName == null) {
         if (newName != null) {
            return false;
         }
      } else if (!oldName.equals(newName)) {
         return false;
      }

      if (oldDomain == null) {
         if (newDomain != null) {
            return false;
         }
      } else if (!oldDomain.equalsIgnoreCase(newDomain)) {
         return false;
      }

      return oldPath == null ? newPath == null : oldPath.equals(newPath);
   }

   private static void quoteIfNeededAndAppend(String text, StringBuilder builder) {
      if (isQuoteNeeded(text)) {
         HttpField.PARAMETER_TOKENIZER.quote(builder, text);
      } else {
         builder.append(text);
      }
   }

   private HttpCookieUtils() {
   }

   public static class SetCookieHttpField extends HttpField {
      private final org.eclipse.jetty.http.HttpCookie _cookie;
      private final CookieCompliance _compliance;

      public SetCookieHttpField(org.eclipse.jetty.http.HttpCookie cookie, CookieCompliance compliance) {
         super(HttpHeader.SET_COOKIE, (String)null);
         this._cookie = cookie;
         this._compliance = compliance;
      }

      public org.eclipse.jetty.http.HttpCookie getHttpCookie() {
         return this._cookie;
      }

      public String getValue() {
         return HttpCookieUtils.getSetCookie(this._cookie, this._compliance);
      }
   }
}
