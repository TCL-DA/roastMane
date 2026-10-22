package com.sun.prism.j2d;

import com.sun.javafx.PlatformUtil;
import com.sun.javafx.font.FontFactory;
import com.sun.javafx.font.FontResource;
import com.sun.javafx.font.PGFont;
import java.awt.Font;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;

final class J2DFontFactory implements FontFactory {
   FontFactory prismFontFactory;
   private static boolean compositeFontMethodsInitialized = false;
   private static Method getCompositeFontUIResource = null;

   J2DFontFactory(FontFactory var1) {
      this.prismFontFactory = var1;
   }

   @Override
   public PGFont createFont(String var1, float var2) {
      return this.prismFontFactory.createFont(var1, var2);
   }

   @Override
   public PGFont createFont(String var1, boolean var2, boolean var3, float var4) {
      return this.prismFontFactory.createFont(var1, var2, var3, var4);
   }

   @Override
   public synchronized PGFont deriveFont(PGFont var1, boolean var2, boolean var3, float var4) {
      return this.prismFontFactory.deriveFont(var1, var2, var3, var4);
   }

   @Override
   public String[] getFontFamilyNames() {
      return this.prismFontFactory.getFontFamilyNames();
   }

   @Override
   public String[] getFontFullNames() {
      return this.prismFontFactory.getFontFullNames();
   }

   @Override
   public String[] getFontFullNames(String var1) {
      return this.prismFontFactory.getFontFullNames(var1);
   }

   @Override
   public boolean isPlatformFont(String var1) {
      return this.prismFontFactory.isPlatformFont(var1);
   }

   @Override
   public final boolean hasPermission() {
      return this.prismFontFactory.hasPermission();
   }

   @Override
   public PGFont[] loadEmbeddedFont(String var1, InputStream var2, float var3, boolean var4, boolean var5) {
      if (!this.hasPermission()) {
         return new PGFont[]{this.createFont("System Regular", var3)};
      } else {
         PGFont[] var6 = this.prismFontFactory.loadEmbeddedFont(var1, var2, var3, var4, var5);
         if (var6 != null && var6.length != 0) {
            FontResource var7 = var6[0].getFontResource();
            registerFont(var6[0].getFontResource());
            return var6;
         } else {
            return null;
         }
      }
   }

   public static void registerFont(FontResource var0) {
      AccessController.doPrivileged((PrivilegedAction)(() -> {
         FileInputStream var1 = null;

         try {
            File var2 = new File(var0.getFileName());
            var1 = new FileInputStream(var2);
            Font var3 = Font.createFont(0, var1);
            var0.setPeer(var3);
         } catch (Exception var12) {
            var12.printStackTrace();
         } finally {
            if (var1 != null) {
               try {
                  var1.close();
               } catch (Exception var11) {
               }
            }
         }

         return null;
      }));
   }

   @Override
   public PGFont[] loadEmbeddedFont(String var1, String var2, float var3, boolean var4, boolean var5) {
      if (!this.hasPermission()) {
         return new PGFont[]{this.createFont("System Regular", var3)};
      } else {
         PGFont[] var6 = this.prismFontFactory.loadEmbeddedFont(var1, var2, var3, var4, var5);
         if (var6 != null && var6.length != 0) {
            final FontResource var7 = var6[0].getFontResource();
            Object var8 = AccessController.doPrivileged(new PrivilegedAction<Object>() {
               @Override
               public Object run() {
                  try {
                     File var1 = new File(var7.getFileName());
                     Font var2 = Font.createFont(0, var1);
                     var7.setPeer(var2);
                  } catch (Exception var3) {
                     var3.printStackTrace();
                  }

                  return null;
               }
            });
            return var6;
         } else {
            return null;
         }
      }
   }

   static Font getCompositeFont(Font var0) {
      if (PlatformUtil.isMac()) {
         return var0;
      } else {
         synchronized (J2DFontFactory.class) {
            if (!compositeFontMethodsInitialized) {
               Void var2 = AccessController.doPrivileged((PrivilegedAction<Void>)(() -> {
                  compositeFontMethodsInitialized = true;

                  Class var0x;
                  try {
                     var0x = Class.forName("sun.font.FontUtilities", true, null);
                  } catch (ClassNotFoundException var5x) {
                     try {
                        var0x = Class.forName("sun.font.FontManager", true, null);
                     } catch (ClassNotFoundException var4x) {
                        return null;
                     }
                  }

                  try {
                     getCompositeFontUIResource = var0x.getMethod("getCompositeFontUIResource", Font.class);
                  } catch (NoSuchMethodException var3) {
                  }

                  return null;
               }));
            }
         }

         if (getCompositeFontUIResource != null) {
            try {
               return (Font)getCompositeFontUIResource.invoke(null, var0);
            } catch (IllegalAccessException var4) {
            } catch (InvocationTargetException var5) {
            }
         }

         return var0;
      }
   }
}
