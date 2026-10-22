package javafx.scene.text;

import com.sun.javafx.scene.text.FontHelper;
import com.sun.javafx.tk.Toolkit;
import java.io.File;
import java.io.FilePermission;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import javafx.beans.NamedArg;

public final class Font {
   private static final String DEFAULT_FAMILY = "System";
   private static final String DEFAULT_FULLNAME = "System Regular";
   private static float defaultSystemFontSize = -1.0F;
   private static Font DEFAULT;
   private String name;
   private String family;
   private String style;
   private double size;
   private int hash = 0;
   private Object nativeFont;

   private static float getDefaultSystemFontSize() {
      if (defaultSystemFontSize == -1.0F) {
         defaultSystemFontSize = Toolkit.getToolkit().getFontLoader().getSystemFontSize();
      }

      return defaultSystemFontSize;
   }

   public static synchronized Font getDefault() {
      if (DEFAULT == null) {
         DEFAULT = new Font("System Regular", (double)getDefaultSystemFontSize());
      }

      return DEFAULT;
   }

   public static List<String> getFamilies() {
      return Toolkit.getToolkit().getFontLoader().getFamilies();
   }

   public static List<String> getFontNames() {
      return Toolkit.getToolkit().getFontLoader().getFontNames();
   }

   public static List<String> getFontNames(String var0) {
      return Toolkit.getToolkit().getFontLoader().getFontNames(var0);
   }

   public static Font font(String var0, FontWeight var1, FontPosture var2, double var3) {
      String var5 = var0 != null && !"".equals(var0) ? var0 : "System";
      double var6 = var3 < 0.0 ? (double)getDefaultSystemFontSize() : var3;
      return Toolkit.getToolkit().getFontLoader().font(var5, var1, var2, (float)var6);
   }

   public static Font font(String var0, FontWeight var1, double var2) {
      return font(var0, var1, null, var2);
   }

   public static Font font(String var0, FontPosture var1, double var2) {
      return font(var0, null, var1, var2);
   }

   public static Font font(String var0, double var1) {
      return font(var0, null, null, var1);
   }

   public static Font font(String var0) {
      return font(var0, null, null, -1.0);
   }

   public static Font font(double var0) {
      return font(null, null, null, var0);
   }

   public final String getName() {
      return this.name;
   }

   public final String getFamily() {
      return this.family;
   }

   public final String getStyle() {
      return this.style;
   }

   public final double getSize() {
      return this.size;
   }

   public Font(@NamedArg("size") double var1) {
      this(null, var1);
   }

   public Font(@NamedArg("name") String var1, @NamedArg("size") double var2) {
      this.name = var1;
      this.size = var2;
      if (var1 == null || "".equals(var1)) {
         this.name = "System Regular";
      }

      if (var2 < 0.0) {
         this.size = (double)getDefaultSystemFontSize();
      }

      Toolkit.getToolkit().getFontLoader().loadFont(this);
   }

   private Font(Object var1, String var2, String var3, String var4, double var5) {
      this.nativeFont = var1;
      this.family = var2;
      this.name = var3;
      this.style = var4;
      this.size = var5;
   }

   public static Font loadFont(String var0, double var1) {
      Font[] var3 = loadFontInternal(var0, var1, false);
      return var3 == null ? null : var3[0];
   }

   public static Font[] loadFonts(String var0, double var1) {
      return loadFontInternal(var0, var1, true);
   }

   private static Font[] loadFontInternal(String var0, double var1, boolean var3) {
      URL var4 = null;

      try {
         var4 = new URL(var0);
      } catch (Exception var21) {
         return null;
      }

      if (var1 <= 0.0) {
         var1 = (double)getDefaultSystemFontSize();
      }

      if (var4.getProtocol().equals("file")) {
         String var27 = var4.getFile();
         var27 = new File(var27).getPath();

         try {
            SecurityManager var30 = System.getSecurityManager();
            if (var30 != null) {
               FilePermission var31 = new FilePermission(var27, "read");
               var30.checkPermission(var31);
            }
         } catch (Exception var22) {
            return null;
         }

         return Toolkit.getToolkit().getFontLoader().loadFont(var27, var1, var3);
      } else {
         Object var5 = null;
         URLConnection var6 = null;
         InputStream var7 = null;

         Object var9;
         try {
            var6 = var4.openConnection();
            var7 = var6.getInputStream();
            return Toolkit.getToolkit().getFontLoader().loadFont(var7, var1, var3);
         } catch (Exception var23) {
            var9 = null;
         } finally {
            try {
               if (var7 != null) {
                  var7.close();
               }
            } catch (Exception var20) {
            }
         }

         return (Font[])var9;
      }
   }

   public static Font loadFont(InputStream var0, double var1) {
      if (var1 <= 0.0) {
         var1 = (double)getDefaultSystemFontSize();
      }

      Font[] var3 = Toolkit.getToolkit().getFontLoader().loadFont(var0, var1, false);
      return var3 == null ? null : var3[0];
   }

   public static Font[] loadFonts(InputStream var0, double var1) {
      if (var1 <= 0.0) {
         var1 = (double)getDefaultSystemFontSize();
      }

      Font[] var3 = Toolkit.getToolkit().getFontLoader().loadFont(var0, var1, true);
      return var3 == null ? null : var3;
   }

   @Override
   public String toString() {
      StringBuilder var1 = new StringBuilder("Font[name=");
      var1 = var1.append(this.name);
      var1 = var1.append(", family=").append(this.family);
      var1 = var1.append(", style=").append(this.style);
      var1 = var1.append(", size=").append(this.size);
      var1 = var1.append("]");
      return var1.toString();
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof Font)) {
         return false;
      } else {
         Font var2 = (Font)var1;
         return (this.name == null ? var2.name == null : this.name.equals(var2.name)) && this.size == var2.size;
      }
   }

   @Override
   public int hashCode() {
      if (this.hash == 0) {
         long var1 = 17L;
         var1 = 37L * var1 + (long)this.name.hashCode();
         var1 = 37L * var1 + Double.doubleToLongBits(this.size);
         this.hash = (int)(var1 ^ var1 >> 32);
      }

      return this.hash;
   }

   Object getNativeFont() {
      return this.nativeFont;
   }

   void setNativeFont(Object var1, String var2, String var3, String var4) {
      this.nativeFont = var1;
      this.name = var2;
      this.family = var3;
      this.style = var4;
   }

   static Font nativeFont(Object var0, String var1, String var2, String var3, double var4) {
      return new Font(var0, var2, var1, var3, var4);
   }

   static {
      FontHelper.setFontAccessor(new FontHelper.FontAccessor() {
         @Override
         public Object getNativeFont(Font var1) {
            return var1.getNativeFont();
         }

         @Override
         public void setNativeFont(Font var1, Object var2, String var3, String var4, String var5) {
            var1.setNativeFont(var2, var3, var4, var5);
         }

         @Override
         public Font nativeFont(Object var1, String var2, String var3, String var4, double var5) {
            return Font.nativeFont(var1, var2, var3, var4, var5);
         }
      });
   }
}
