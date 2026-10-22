package com.sun.webkit.network;

import com.sun.javafx.logging.PlatformLogger;
import com.sun.javafx.logging.PlatformLogger.Level;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.IDN;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

final class PublicSuffixes {
   private static final PlatformLogger logger = PlatformLogger.getLogger(PublicSuffixes.class.getName());
   private static final Map<String, PublicSuffixes.Rules> rulesCache = new ConcurrentHashMap<>();
   private static final File pslFile = AccessController.doPrivileged(
      (PrivilegedAction<File>)(() -> new File(System.getProperty("java.home"), "lib/security/public_suffix_list.dat"))
   );
   private static final boolean pslFileExists = AccessController.doPrivileged((PrivilegedAction<Boolean>)(() -> {
      if (!pslFile.exists()) {
         logger.warning("Resource not found: lib/security/public_suffix_list.dat");
         return false;
      } else {
         return true;
      }
   }));

   private PublicSuffixes() {
      throw new AssertionError();
   }

   static boolean pslFileExists() {
      return pslFileExists;
   }

   static boolean isPublicSuffix(String var0) {
      if (var0.length() == 0) {
         return false;
      } else if (!pslFileExists()) {
         return false;
      } else {
         PublicSuffixes.Rules var1 = PublicSuffixes.Rules.getRules(var0);
         return var1 == null ? false : var1.match(var0);
      }
   }

   private static String toLogString(Map<String, PublicSuffixes.Rule> var0) {
      if (var0.isEmpty()) {
         return "{}";
      } else {
         StringBuilder var1 = new StringBuilder();

         for (Entry var3 : var0.entrySet()) {
            var1.append(String.format("%n    "));
            var1.append((String)var3.getKey());
            var1.append(": ");
            var1.append(var3.getValue());
         }

         return var1.toString();
      }
   }

   private static enum Rule {
      SIMPLE_RULE,
      WILDCARD_RULE,
      EXCEPTION_RULE;
   }

   private static class Rules {
      private final Map<String, PublicSuffixes.Rule> rules = new HashMap<>();

      private Rules(InputStream var1) throws IOException {
         InputStreamReader var2 = new InputStreamReader(var1, "UTF-8");
         BufferedReader var3 = new BufferedReader(var2);
         int var5 = var3.read();

         String var4;
         while (var5 != -1 && (var4 = var3.readLine()) != null) {
            PublicSuffixes.Rule var6;
            if (var4.startsWith("!")) {
               var4 = var4.substring(1);
               var6 = PublicSuffixes.Rule.EXCEPTION_RULE;
            } else if (var4.startsWith("*.")) {
               var4 = var4.substring(2);
               var6 = PublicSuffixes.Rule.WILDCARD_RULE;
            } else {
               var6 = PublicSuffixes.Rule.SIMPLE_RULE;
            }

            try {
               var4 = IDN.toASCII(var4, 1);
            } catch (Exception var8) {
               PublicSuffixes.logger.warning(String.format("Error parsing rule: [%s]", var4), var8);
               continue;
            }

            this.rules.put(var4, var6);
            var5 = var3.read();
         }

         if (PublicSuffixes.logger.isLoggable(Level.FINEST)) {
            PublicSuffixes.logger.finest("rules: {0}", new Object[]{PublicSuffixes.toLogString(this.rules)});
         }
      }

      static PublicSuffixes.Rules getRules(String var0) {
         String var1 = getTopLevelDomain(var0);
         return var1.isEmpty() ? null : PublicSuffixes.rulesCache.computeIfAbsent(var1, var1x -> createRules(var1));
      }

      private static String getTopLevelDomain(String var0) {
         var0 = IDN.toUnicode(var0, 1);
         int var1 = var0.lastIndexOf(46);
         return var1 == -1 ? var0 : var0.substring(var1 + 1);
      }

      private static PublicSuffixes.Rules createRules(String var0) {
         try {
            label79: {
               Object var8;
               try (InputStream var1 = getPubSuffixStream()) {
                  if (var1 != null) {
                     ZipInputStream var2 = new ZipInputStream(var1);

                     for (ZipEntry var3 = var2.getNextEntry(); var3 != null; var3 = var2.getNextEntry()) {
                        if (var3.getName().equals(var0)) {
                           return new PublicSuffixes.Rules(var2);
                        }
                     }
                     break label79;
                  }

                  var8 = null;
               }

               return (PublicSuffixes.Rules)var8;
            }
         } catch (IOException var7) {
            PublicSuffixes.logger.warning("Unexpected error", var7);
         }

         return null;
      }

      private static InputStream getPubSuffixStream() {
         return AccessController.doPrivileged((PrivilegedAction<InputStream>)(() -> {
            try {
               return new FileInputStream(PublicSuffixes.pslFile);
            } catch (FileNotFoundException var1) {
               PublicSuffixes.logger.warning("Resource not found: lib/security/public_suffix_list.dat");
               return null;
            }
         }));
      }

      boolean match(String var1) {
         PublicSuffixes.Rule var2 = this.rules.get(var1);
         if (var2 == PublicSuffixes.Rule.EXCEPTION_RULE) {
            return false;
         } else if (var2 != PublicSuffixes.Rule.SIMPLE_RULE && var2 != PublicSuffixes.Rule.WILDCARD_RULE) {
            int var3 = var1.indexOf(46) + 1;
            if (var3 == 0) {
               var3 = var1.length();
            }

            String var4 = var1.substring(var3);
            return this.rules.get(var4) == PublicSuffixes.Rule.WILDCARD_RULE;
         } else {
            return true;
         }
      }
   }
}
