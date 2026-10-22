package javafx.css.converter;

import com.sun.javafx.logging.PlatformLogger;
import com.sun.javafx.logging.PlatformLogger.Level;
import com.sun.javafx.util.DataURI;
import com.sun.javafx.util.Logging;
import com.sun.javafx.util.Utils;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.AccessController;
import java.security.CodeSource;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.security.ProtectionDomain;
import javafx.application.Application;
import javafx.css.ParsedValue;
import javafx.css.StyleConverter;
import javafx.scene.text.Font;

public final class URLConverter extends StyleConverter<ParsedValue[], String> {
   public static StyleConverter<ParsedValue[], String> getInstance() {
      return URLConverter.Holder.INSTANCE;
   }

   private URLConverter() {
   }

   public String convert(ParsedValue<ParsedValue[], String> var1, Font var2) {
      String var3 = null;
      ParsedValue[] var4 = (ParsedValue[])var1.getValue();
      String var5 = var4.length > 0 ? StringConverter.getInstance().convert(var4[0], var2) : null;
      var5 = var5 != null ? var5.trim() : null;
      if (var5 != null && !var5.isEmpty()) {
         if (var5.startsWith("url(")) {
            var5 = Utils.stripQuotes(var5.substring(4, var5.length() - 1));
         } else {
            var5 = Utils.stripQuotes(var5);
         }

         if (DataURI.matchScheme(var5)) {
            var3 = var5;
         } else if (!var5.isEmpty()) {
            String var6 = var4.length > 1 && var4[1] != null ? (String)var4[1].getValue() : null;
            URL var7 = this.resolve(var6, var5);
            if (var7 != null) {
               var3 = var7.toExternalForm();
            }
         }
      }

      return var3;
   }

   private URL resolve(String var1, String var2) {
      try {
         URI var3 = new URI(var2);
         if (var3.isAbsolute()) {
            return var3.toURL();
         } else {
            URL var10 = this.resolveRuntimeImport(var3);
            if (var10 != null) {
               return var10;
            } else {
               String var5 = var3.getPath();
               if (var5.startsWith("/")) {
                  ClassLoader var11 = Thread.currentThread().getContextClassLoader();
                  return var11.getResource(var5.substring(1));
               } else {
                  String var6 = var1 != null ? var1.trim() : null;
                  if (var6 != null && !var6.isEmpty()) {
                     URI var12 = new URI(var6);
                     if (!var12.isOpaque()) {
                        URI var13 = var12.resolve(var3);
                        return var13.toURL();
                     } else {
                        URL var8 = var12.toURL();
                        return new URL(var8, var3.getPath());
                     }
                  } else {
                     ClassLoader var7 = Thread.currentThread().getContextClassLoader();
                     return var7.getResource(var5);
                  }
               }
            }
         }
      } catch (URISyntaxException | MalformedURLException var9) {
         PlatformLogger var4 = Logging.getCSSLogger();
         if (var4.isLoggable(Level.WARNING)) {
            var4.warning(var9.getLocalizedMessage());
         }

         return null;
      }
   }

   private URL resolveRuntimeImport(URI var1) {
      String var2 = var1.getPath();
      String var3 = var2.startsWith("/") ? var2.substring(1) : var2;
      if ((var3.startsWith("com/sun/javafx/scene/control/skin/modena/") || var3.startsWith("com/sun/javafx/scene/control/skin/caspian/"))
         && (var3.endsWith(".css") || var3.endsWith(".bss"))) {
         System.err.println("WARNING: resolveRuntimeImport cannot resolve: " + var3);
         SecurityManager var4 = System.getSecurityManager();
         if (var4 == null) {
            ClassLoader var14 = Thread.currentThread().getContextClassLoader();
            return var14.getResource(var3);
         }

         try {
            URL var5 = AccessController.doPrivileged((PrivilegedExceptionAction<URL>)(() -> {
               ProtectionDomain var0 = Application.class.getProtectionDomain();
               CodeSource var1x = var0.getCodeSource();
               return var1x.getLocation();
            }));
            URI var6 = var5.toURI();
            String var7 = var6.getScheme();
            String var8 = var6.getPath();
            if ("file".equals(var7) && var8.endsWith(".jar") && "file".equals(var7)) {
               var7 = "jar:file";
               var8 = var8.concat("!/");
            }

            var8 = var8.concat(var3);
            String var9 = var6.getUserInfo();
            String var10 = var6.getHost();
            int var11 = var6.getPort();
            URI var12 = new URI(var7, var9, var10, var11, var8, null, null);
            return var12.toURL();
         } catch (MalformedURLException | PrivilegedActionException | URISyntaxException var13) {
         }
      }

      return null;
   }

   @Override
   public String toString() {
      return "URLType";
   }

   private static class Holder {
      static final URLConverter INSTANCE = new URLConverter();
      static final URLConverter.SequenceConverter SEQUENCE_INSTANCE = new URLConverter.SequenceConverter();
   }

   public static final class SequenceConverter extends StyleConverter<ParsedValue<ParsedValue[], String>[], String[]> {
      public static URLConverter.SequenceConverter getInstance() {
         return URLConverter.Holder.SEQUENCE_INSTANCE;
      }

      private SequenceConverter() {
      }

      public String[] convert(ParsedValue<ParsedValue<ParsedValue[], String>[], String[]> var1, Font var2) {
         ParsedValue[] var3 = (ParsedValue[])var1.getValue();
         String[] var4 = new String[var3.length];

         for (int var5 = 0; var5 < var3.length; var5++) {
            var4[var5] = URLConverter.getInstance().convert(var3[var5], var2);
         }

         return var4;
      }

      @Override
      public String toString() {
         return "URLSeqType";
      }
   }
}
