package com.sun.javafx.font;

import com.sun.javafx.scene.text.FontHelper;
import com.sun.javafx.tk.FontLoader;
import com.sun.javafx.tk.FontMetrics;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;

public class PrismFontLoader extends FontLoader {
   private static PrismFontLoader theInstance = new PrismFontLoader();
   private boolean embeddedFontsLoaded = false;
   FontFactory installedFontFactory = null;

   public static PrismFontLoader getInstance() {
      return theInstance;
   }

   Properties loadEmbeddedFontDefinitions() {
      Properties var1 = new Properties();
      ClassLoader var2 = Thread.currentThread().getContextClassLoader();
      if (var2 == null) {
         return var1;
      } else {
         URL var3 = var2.getResource("META-INF/fonts.mf");
         if (var3 == null) {
            return var1;
         } else {
            try (InputStream var4 = var3.openStream()) {
               var1.load(var4);
            } catch (Exception var9) {
               var9.printStackTrace();
            }

            return var1;
         }
      }
   }

   private void loadEmbeddedFonts() {
      if (!this.embeddedFontsLoaded) {
         FontFactory var1 = this.getFontFactoryFromPipeline();
         if (!var1.hasPermission()) {
            this.embeddedFontsLoaded = true;
            return;
         }

         Properties var2 = this.loadEmbeddedFontDefinitions();
         Enumeration var3 = var2.keys();
         ClassLoader var4 = Thread.currentThread().getContextClassLoader();

         while (var3.hasMoreElements()) {
            String var5 = (String)var3.nextElement();
            String var6 = var2.getProperty(var5);
            if (var6.startsWith("/")) {
               var6 = var6.substring(1);

               try (InputStream var7 = var4.getResourceAsStream(var6)) {
                  var1.loadEmbeddedFont(var5, var7, 0.0F, true, false);
               } catch (Exception var12) {
               }
            }
         }

         this.embeddedFontsLoaded = true;
      }
   }

   private Font[] createFonts(PGFont[] var1) {
      if (var1 != null && var1.length != 0) {
         Font[] var2 = new Font[var1.length];

         for (int var3 = 0; var3 < var1.length; var3++) {
            var2[var3] = this.createFont(var1[var3]);
         }

         return var2;
      } else {
         return null;
      }
   }

   @Override
   public Font[] loadFont(InputStream var1, double var2, boolean var4) {
      FontFactory var5 = this.getFontFactoryFromPipeline();
      PGFont[] var6 = var5.loadEmbeddedFont(null, var1, (float)var2, true, var4);
      return this.createFonts(var6);
   }

   @Override
   public Font[] loadFont(String var1, double var2, boolean var4) {
      FontFactory var5 = this.getFontFactoryFromPipeline();
      PGFont[] var6 = var5.loadEmbeddedFont(null, var1, (float)var2, true, var4);
      return this.createFonts(var6);
   }

   private Font createFont(PGFont var1) {
      return FontHelper.nativeFont(var1, var1.getName(), var1.getFamilyName(), var1.getStyleName(), (double)var1.getSize());
   }

   @Override
   public List<String> getFamilies() {
      this.loadEmbeddedFonts();
      return Arrays.asList(this.getFontFactoryFromPipeline().getFontFamilyNames());
   }

   @Override
   public List<String> getFontNames() {
      this.loadEmbeddedFonts();
      return Arrays.asList(this.getFontFactoryFromPipeline().getFontFullNames());
   }

   @Override
   public List<String> getFontNames(String var1) {
      this.loadEmbeddedFonts();
      return Arrays.asList(this.getFontFactoryFromPipeline().getFontFullNames(var1));
   }

   @Override
   public Font font(String var1, FontWeight var2, FontPosture var3, float var4) {
      FontFactory var5 = this.getFontFactoryFromPipeline();
      if (!this.embeddedFontsLoaded && !var5.isPlatformFont(var1)) {
         this.loadEmbeddedFonts();
      }

      boolean var6 = var2 != null && var2.ordinal() >= FontWeight.BOLD.ordinal();
      boolean var7 = var3 == FontPosture.ITALIC;
      PGFont var8 = var5.createFont(var1, var6, var7, var4);
      return FontHelper.nativeFont(var8, var8.getName(), var8.getFamilyName(), var8.getStyleName(), (double)var4);
   }

   @Override
   public void loadFont(Font var1) {
      FontFactory var2 = this.getFontFactoryFromPipeline();
      String var3 = var1.getName();
      if (!this.embeddedFontsLoaded && !var2.isPlatformFont(var3)) {
         this.loadEmbeddedFonts();
      }

      PGFont var4 = var2.createFont(var3, (float)var1.getSize());
      String var5 = var4.getName();
      String var6 = var4.getFamilyName();
      String var7 = var4.getStyleName();
      FontHelper.setNativeFont(var1, var4, var5, var6, var7);
   }

   @Override
   public FontMetrics getFontMetrics(Font var1) {
      if (var1 != null) {
         PGFont var2 = (PGFont)FontHelper.getNativeFont(var1);
         Metrics var3 = PrismFontUtils.getFontMetrics(var2);
         float var4 = -var3.getAscent();
         float var5 = -var3.getAscent();
         float var6 = var3.getXHeight();
         float var7 = var3.getDescent();
         float var8 = var3.getDescent();
         float var9 = var3.getLineGap();
         return FontMetrics.createFontMetrics(var4, var5, var6, var7, var8, var9, var1);
      } else {
         return null;
      }
   }

   @Override
   public float getCharWidth(char var1, Font var2) {
      PGFont var3 = (PGFont)FontHelper.getNativeFont(var2);
      return (float)PrismFontUtils.getCharWidth(var3, var1);
   }

   @Override
   public float getSystemFontSize() {
      return PrismFontFactory.getSystemFontSize();
   }

   private FontFactory getFontFactoryFromPipeline() {
      if (this.installedFontFactory != null) {
         return this.installedFontFactory;
      } else {
         try {
            Class var1 = Class.forName("com.sun.prism.GraphicsPipeline");
            Method var2 = var1.getMethod("getPipeline", (Class<?>[])null);
            Object var3 = var2.invoke(null);
            Method var4 = var1.getMethod("getFontFactory", (Class<?>[])null);
            Object var5 = var4.invoke(var3);
            this.installedFontFactory = (FontFactory)var5;
         } catch (Exception var6) {
         }

         return this.installedFontFactory;
      }
   }
}
