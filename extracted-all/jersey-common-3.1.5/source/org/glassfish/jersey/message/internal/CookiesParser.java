package org.glassfish.jersey.message.internal;

import jakarta.ws.rs.core.Cookie;
import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.core.NewCookie.SameSite;
import java.text.ParseException;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.glassfish.jersey.internal.LocalizationMessages;

public class CookiesParser {
   private static final Logger LOGGER = Logger.getLogger(CookiesParser.class.getName());

   public static Map<String, Cookie> parseCookies(String header) {
      String[] bites = header.split("[;,]");
      Map<String, Cookie> cookies = new LinkedHashMap<>();
      int version = 0;
      CookiesParser.MutableCookie cookie = null;

      for (String bite : bites) {
         String[] crumbs = bite.split("=", 2);
         String name = crumbs.length > 0 ? crumbs[0].trim() : "";
         String value = crumbs.length > 1 ? crumbs[1].trim() : "";
         if (value.startsWith("\"") && value.endsWith("\"") && value.length() > 1) {
            value = value.substring(1, value.length() - 1);
         }

         if (!name.startsWith("$")) {
            checkSimilarCookieName(cookies, cookie);
            cookie = new CookiesParser.MutableCookie(name, value);
            cookie.version = version;
         } else if (name.startsWith("$Version")) {
            version = Integer.parseInt(value);
         } else if (name.startsWith("$Path") && cookie != null) {
            cookie.path = value;
         } else if (name.startsWith("$Domain") && cookie != null) {
            cookie.domain = value;
         }
      }

      checkSimilarCookieName(cookies, cookie);
      return cookies;
   }

   private static void checkSimilarCookieName(Map<String, Cookie> cookies, CookiesParser.MutableCookie cookie) {
      if (cookie != null) {
         if (cookies.containsKey(cookie.name)) {
            if (cookie.value.length() > cookies.get(cookie.name).getValue().length()) {
               cookies.put(cookie.name, cookie.getImmutableCookie());
            }
         } else {
            cookies.put(cookie.name, cookie.getImmutableCookie());
         }
      }
   }

   public static Cookie parseCookie(String header) {
      Map<String, Cookie> cookies = parseCookies(header);
      return cookies.entrySet().iterator().next().getValue();
   }

   public static NewCookie parseNewCookie(String header) {
      String[] bites = header.split("[;,]");
      CookiesParser.MutableNewCookie cookie = null;

      for (int i = 0; i < bites.length; i++) {
         String[] crumbs = bites[i].split("=", 2);
         String name = crumbs.length > 0 ? crumbs[0].trim() : "";
         String value = crumbs.length > 1 ? crumbs[1].trim() : "";
         if (value.startsWith("\"") && value.endsWith("\"") && value.length() > 1) {
            value = value.substring(1, value.length() - 1);
         }

         if (cookie == null) {
            cookie = new CookiesParser.MutableNewCookie(name, value);
         } else {
            String param = name.toLowerCase(Locale.ROOT);
            if (param.startsWith("comment")) {
               cookie.comment = value;
            } else if (param.startsWith("domain")) {
               cookie.domain = value;
            } else if (param.startsWith("max-age")) {
               cookie.maxAge = Integer.parseInt(value);
            } else if (param.startsWith("path")) {
               cookie.path = value;
            } else if (param.startsWith("secure")) {
               cookie.secure = true;
            } else if (param.startsWith("version")) {
               cookie.version = Integer.parseInt(value);
            } else if (param.startsWith("httponly")) {
               cookie.httpOnly = true;
            } else if (param.startsWith("samesite")) {
               cookie.sameSite = SameSite.valueOf(value.toUpperCase());
            } else if (param.startsWith("expires")) {
               try {
                  cookie.expiry = HttpDateFormat.readDate(value + ", " + bites[++i]);
               } catch (ParseException var9) {
                  LOGGER.log(Level.FINE, LocalizationMessages.ERROR_NEWCOOKIE_EXPIRES(value), (Throwable)var9);
               }
            }
         }
      }

      return cookie.getImmutableNewCookie();
   }

   private CookiesParser() {
   }

   private static class MutableCookie {
      String name;
      String value;
      int version = 1;
      String path = null;
      String domain = null;

      public MutableCookie(String name, String value) {
         this.name = name;
         this.value = value;
      }

      public Cookie getImmutableCookie() {
         return new Cookie(this.name, this.value, this.path, this.domain, this.version);
      }
   }

   private static class MutableNewCookie {
      String name = null;
      String value = null;
      String path = null;
      String domain = null;
      int version = 1;
      String comment = null;
      int maxAge = -1;
      boolean secure = false;
      boolean httpOnly = false;
      Date expiry = null;
      SameSite sameSite = null;

      public MutableNewCookie(String name, String value) {
         this.name = name;
         this.value = value;
      }

      public NewCookie getImmutableNewCookie() {
         return new NewCookie(
            this.name, this.value, this.path, this.domain, this.version, this.comment, this.maxAge, this.expiry, this.secure, this.httpOnly, this.sameSite
         );
      }
   }
}
