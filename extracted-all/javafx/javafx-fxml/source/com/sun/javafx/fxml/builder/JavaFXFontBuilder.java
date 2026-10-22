package com.sun.javafx.fxml.builder;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.AbstractMap;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Map.Entry;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.util.Builder;

public final class JavaFXFontBuilder extends AbstractMap<String, Object> implements Builder<Font> {
   private String name = null;
   private double size = 12.0;
   private FontWeight weight = null;
   private FontPosture posture = null;
   private URL url = null;

   public Font build() {
      Font var1;
      if (this.url != null) {
         InputStream var2 = null;

         try {
            var2 = this.url.openStream();
            var1 = Font.loadFont(var2, this.size);
         } catch (Exception var11) {
            throw new RuntimeException("Load of font file failed from " + this.url, var11);
         } finally {
            try {
               if (var2 != null) {
                  var2.close();
               }
            } catch (Exception var10) {
               var10.printStackTrace();
            }
         }
      } else if (this.weight == null && this.posture == null) {
         var1 = new Font(this.name, this.size);
      } else {
         if (this.weight == null) {
            this.weight = FontWeight.NORMAL;
         }

         if (this.posture == null) {
            this.posture = FontPosture.REGULAR;
         }

         var1 = Font.font(this.name, this.weight, this.posture, this.size);
      }

      return var1;
   }

   public Object put(String var1, Object var2) {
      if ("name".equals(var1)) {
         if (var2 instanceof URL) {
            this.url = (URL)var2;
         } else {
            this.name = (String)var2;
         }
      } else if ("size".equals(var1)) {
         this.size = Double.parseDouble((String)var2);
      } else if ("style".equals(var1)) {
         String var3 = (String)var2;
         if (var3 != null && var3.length() > 0) {
            boolean var4 = false;
            StringTokenizer var5 = new StringTokenizer(var3, " ");

            while (var5.hasMoreTokens()) {
               String var6 = var5.nextToken();
               FontWeight var7;
               if (!var4 && (var7 = FontWeight.findByName(var6)) != null) {
                  this.weight = var7;
                  var4 = true;
               } else {
                  FontPosture var8;
                  if ((var8 = FontPosture.findByName(var6)) != null) {
                     this.posture = var8;
                  }
               }
            }
         }
      } else {
         if (!"url".equals(var1)) {
            throw new IllegalArgumentException("Unknown Font property: " + var1);
         }

         if (var2 instanceof URL) {
            this.url = (URL)var2;
         } else {
            try {
               this.url = new URL(var2.toString());
            } catch (MalformedURLException var9) {
               throw new IllegalArgumentException("Invalid url " + var2.toString(), var9);
            }
         }
      }

      return null;
   }

   @Override
   public boolean containsKey(Object var1) {
      return false;
   }

   @Override
   public Object get(Object var1) {
      return null;
   }

   @Override
   public Set<Entry<String, Object>> entrySet() {
      throw new UnsupportedOperationException();
   }
}
