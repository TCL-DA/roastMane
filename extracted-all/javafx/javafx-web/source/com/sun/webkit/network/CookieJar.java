package com.sun.webkit.network;

import java.io.IOException;
import java.net.CookieHandler;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

final class CookieJar {
   private CookieJar() {
   }

   private static void fwkPut(String var0, String var1) {
      CookieHandler var2 = CookieHandler.getDefault();
      if (var2 != null) {
         Object var3 = null;

         try {
            var3 = new URI(var0);
            var3 = rewriteToFilterOutHttpOnlyCookies((URI)var3);
         } catch (URISyntaxException var8) {
            return;
         }

         HashMap var4 = new HashMap();
         ArrayList var5 = new ArrayList();
         var5.add(var1);
         var4.put("Set-Cookie", var5);

         try {
            var2.put((URI)var3, var4);
         } catch (IOException var7) {
         }
      }
   }

   private static String fwkGet(String var0, boolean var1) {
      CookieHandler var2 = CookieHandler.getDefault();
      if (var2 != null) {
         Object var3 = null;

         try {
            var3 = new URI(var0);
            if (!var1) {
               var3 = rewriteToFilterOutHttpOnlyCookies((URI)var3);
            }
         } catch (URISyntaxException var13) {
            return null;
         }

         HashMap var4 = new HashMap();
         Object var5 = null;

         try {
            var5 = var2.get((URI)var3, var4);
         } catch (IOException var12) {
            return null;
         }

         if (var5 != null) {
            StringBuilder var6 = new StringBuilder();

            for (Entry var8 : var5.entrySet()) {
               String var9 = (String)var8.getKey();
               if ("Cookie".equalsIgnoreCase(var9)) {
                  for (String var11 : (List)var8.getValue()) {
                     if (var6.length() > 0) {
                        var6.append("; ");
                     }

                     var6.append(var11);
                  }
               }
            }

            return var6.toString();
         }
      }

      return null;
   }

   private static URI rewriteToFilterOutHttpOnlyCookies(URI var0) throws URISyntaxException {
      return new URI(var0.getScheme().equalsIgnoreCase("https") ? "javascripts" : "javascript", var0.getRawSchemeSpecificPart(), var0.getRawFragment());
   }
}
