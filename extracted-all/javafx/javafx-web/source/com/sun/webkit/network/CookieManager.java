package com.sun.webkit.network;

import com.sun.javafx.logging.PlatformLogger;
import com.sun.javafx.logging.PlatformLogger.Level;
import java.net.CookieHandler;
import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;

public final class CookieManager extends CookieHandler {
   private static final PlatformLogger logger = PlatformLogger.getLogger(CookieManager.class.getName());
   private final CookieStore store = new CookieStore();

   @Override
   public Map<String, List<String>> get(URI var1, Map<String, List<String>> var2) {
      if (logger.isLoggable(Level.FINEST)) {
         logger.finest("uri: [{0}], requestHeaders: {1}", new Object[]{var1, toLogString(var2)});
      }

      if (var1 == null) {
         throw new IllegalArgumentException("uri is null");
      } else if (var2 == null) {
         throw new IllegalArgumentException("requestHeaders is null");
      } else {
         String var3 = this.get(var1);
         Object var4;
         if (var3 != null) {
            var4 = new HashMap();
            var4.put("Cookie", Arrays.asList(var3));
         } else {
            var4 = Collections.emptyMap();
         }

         if (logger.isLoggable(Level.FINEST)) {
            logger.finest("result: {0}", new Object[]{toLogString((Map<String, List<String>>)var4)});
         }

         return (Map<String, List<String>>)var4;
      }
   }

   private String get(URI var1) {
      String var2 = var1.getHost();
      if (var2 != null && var2.length() != 0) {
         var2 = canonicalize(var2);
         String var3 = var1.getScheme();
         boolean var4 = "https".equalsIgnoreCase(var3) || "javascripts".equalsIgnoreCase(var3);
         boolean var5 = "http".equalsIgnoreCase(var3) || "https".equalsIgnoreCase(var3);
         List var6;
         synchronized (this.store) {
            var6 = this.store.get(var2, var1.getPath(), var4, var5);
         }

         StringBuilder var12 = new StringBuilder();

         for (Cookie var9 : var6) {
            if (var12.length() > 0) {
               var12.append("; ");
            }

            var12.append(var9.getName());
            var12.append('=');
            var12.append(var9.getValue());
         }

         return var12.length() > 0 ? var12.toString() : null;
      } else {
         logger.finest("Null or empty URI host, returning null");
         return null;
      }
   }

   @Override
   public void put(URI var1, Map<String, List<String>> var2) {
      if (logger.isLoggable(Level.FINEST)) {
         logger.finest("uri: [{0}], responseHeaders: {1}", new Object[]{var1, toLogString(var2)});
      }

      if (var1 == null) {
         throw new IllegalArgumentException("uri is null");
      } else if (var2 == null) {
         throw new IllegalArgumentException("responseHeaders is null");
      } else {
         for (Entry var4 : var2.entrySet()) {
            String var5 = (String)var4.getKey();
            if ("Set-Cookie".equalsIgnoreCase(var5)) {
               ExtendedTime var6 = ExtendedTime.currentTime();
               ListIterator var7 = ((List)var4.getValue()).listIterator(((List)var4.getValue()).size());

               while (var7.hasPrevious()) {
                  Cookie var8 = Cookie.parse((String)var7.previous(), var6);
                  if (var8 != null) {
                     this.put(var1, var8);
                     var6 = var6.incrementSubtime();
                  }
               }
            }
         }
      }
   }

   private void put(URI var1, Cookie var2) {
      logger.finest("cookie: {0}", new Object[]{var2});
      String var3 = var1.getHost();
      if (var3 != null && var3.length() != 0) {
         var3 = canonicalize(var3);
         if (!PublicSuffixes.pslFileExists()) {
            var2.setDomain("");
         } else if (PublicSuffixes.isPublicSuffix(var2.getDomain())) {
            if (!var2.getDomain().equals(var3)) {
               logger.finest("Domain is public suffix, ignoring cookie");
               return;
            }

            var2.setDomain("");
         }

         if (var2.getDomain().length() > 0) {
            if (!Cookie.domainMatches(var3, var2.getDomain())) {
               logger.finest("Hostname does not match domain, ignoring cookie");
               return;
            }

            var2.setHostOnly(false);
         } else {
            var2.setHostOnly(true);
            var2.setDomain(var3);
         }

         if (var2.getPath() == null) {
            var2.setPath(Cookie.defaultPath(var1));
         }

         boolean var4 = "http".equalsIgnoreCase(var1.getScheme()) || "https".equalsIgnoreCase(var1.getScheme());
         if (var2.getHttpOnly() && !var4) {
            logger.finest("HttpOnly cookie received from non-HTTP API, ignoring cookie");
         } else {
            synchronized (this.store) {
               Cookie var6 = this.store.get(var2);
               if (var6 != null) {
                  if (var6.getHttpOnly() && !var4) {
                     logger.finest("Non-HTTP API attempts to overwrite HttpOnly cookie, blocked");
                     return;
                  }

                  var2.setCreationTime(var6.getCreationTime());
               }

               this.store.put(var2);
            }

            logger.finest("Stored: {0}", new Object[]{var2});
         }
      } else {
         logger.finest("Null or empty URI host, ignoring cookie");
      }
   }

   private static String toLogString(Map<String, List<String>> var0) {
      if (var0 == null) {
         return null;
      } else if (var0.isEmpty()) {
         return "{}";
      } else {
         StringBuilder var1 = new StringBuilder();

         for (Entry var3 : var0.entrySet()) {
            String var4 = (String)var3.getKey();

            for (String var6 : (List)var3.getValue()) {
               var1.append(String.format("%n    "));
               var1.append(var4);
               var1.append(": ");
               var1.append(var6);
            }
         }

         return var1.toString();
      }
   }

   private static String canonicalize(String var0) {
      return var0.toLowerCase();
   }
}
