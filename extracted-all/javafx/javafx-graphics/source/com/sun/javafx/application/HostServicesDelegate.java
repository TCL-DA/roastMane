package com.sun.javafx.application;

import java.awt.Desktop;
import java.io.File;
import java.net.URI;
import javafx.application.Application;

public abstract class HostServicesDelegate {
   public static HostServicesDelegate getInstance(Application var0) {
      return HostServicesDelegate.StandaloneHostService.getInstance(var0);
   }

   protected HostServicesDelegate() {
   }

   public abstract String getCodeBase();

   public abstract String getDocumentBase();

   public abstract void showDocument(String var1);

   private static class StandaloneHostService extends HostServicesDelegate {
      private static HostServicesDelegate instance = null;
      private Class appClass = null;
      static final String[] browsers = new String[]{"xdg-open", "google-chrome", "firefox", "opera", "konqueror", "mozilla"};

      public static HostServicesDelegate getInstance(Application var0) {
         synchronized (HostServicesDelegate.StandaloneHostService.class) {
            if (instance == null) {
               instance = new HostServicesDelegate.StandaloneHostService(var0);
            }

            return instance;
         }
      }

      private StandaloneHostService(Application var1) {
         this.appClass = var1.getClass();
      }

      @Override
      public String getCodeBase() {
         String var1 = this.appClass.getName();
         int var2 = var1.lastIndexOf(".");
         if (var2 >= 0) {
            var1 = var1.substring(var2 + 1);
         }

         var1 = var1 + ".class";
         String var3 = this.appClass.getResource(var1).toString();
         if (var3.startsWith("jar:file:") && var3.indexOf("!") != -1) {
            String var4 = var3.substring(4, var3.lastIndexOf("!"));
            File var5 = null;

            try {
               var5 = new File(new URI(var4).getPath());
            } catch (Exception var7) {
            }

            if (var5 != null) {
               String var6 = var5.getParent();
               if (var6 != null) {
                  return this.toURIString(var6);
               }
            }

            return "";
         } else {
            return "";
         }
      }

      private String toURIString(String var1) {
         try {
            return new File(var1).toURI().toString();
         } catch (Exception var3) {
            var3.printStackTrace();
            return "";
         }
      }

      @Override
      public String getDocumentBase() {
         return this.toURIString(System.getProperty("user.dir"));
      }

      @Override
      public void showDocument(String var1) {
         String var2 = System.getProperty("os.name");

         try {
            if (var2.startsWith("Mac OS")) {
               Desktop.getDesktop().browse(URI.create(var1));
            } else if (var2.startsWith("Windows")) {
               Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + var1);
            } else {
               String var3 = null;

               for (String var7 : browsers) {
                  if (var3 == null && Runtime.getRuntime().exec(new String[]{"which", var7}).getInputStream().read() != -1) {
                     Runtime var10000 = Runtime.getRuntime();
                     String[] var10001 = new String[2];
                     var3 = var7;
                     var10001[0] = var7;
                     var10001[1] = var1;
                     var10000.exec(var10001);
                  }
               }

               if (var3 == null) {
                  throw new Exception("No web browser found");
               }
            }
         } catch (Exception var8) {
            var8.printStackTrace();
         }
      }
   }
}
