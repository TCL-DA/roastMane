package javafx.css;

import com.sun.javafx.collections.TrackableObservableList;
import com.sun.javafx.css.FontFaceImpl;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.ObservableList;
import javafx.collections.ListChangeListener.Change;

public class Stylesheet {
   static final int BINARY_CSS_VERSION = 6;
   private final String url;
   private StyleOrigin origin = StyleOrigin.AUTHOR;
   private final ObservableList<Rule> rules = new TrackableObservableList<Rule>() {
      protected void onChanged(Change<Rule> var1) {
         var1.reset();

         while (var1.next()) {
            if (var1.wasAdded()) {
               for (Rule var5 : var1.getAddedSubList()) {
                  var5.setStylesheet(Stylesheet.this);
               }
            } else if (var1.wasRemoved()) {
               for (Rule var3 : var1.getRemoved()) {
                  if (var3.getStylesheet() == Stylesheet.this) {
                     var3.setStylesheet(null);
                  }
               }
            }
         }
      }
   };
   private final List<FontFace> fontFaces = new ArrayList<>();
   private String[] stringStore;

   public String getUrl() {
      return this.url;
   }

   public StyleOrigin getOrigin() {
      return this.origin;
   }

   public void setOrigin(StyleOrigin var1) {
      this.origin = var1;
   }

   Stylesheet() {
      this(null);
   }

   Stylesheet(String var1) {
      this.url = var1;
   }

   public List<Rule> getRules() {
      return this.rules;
   }

   public List<FontFace> getFontFaces() {
      return this.fontFaces;
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 instanceof Stylesheet) {
         Stylesheet var2 = (Stylesheet)var1;
         if (this.url == null && var2.url == null) {
            return true;
         } else {
            return this.url != null && var2.url != null ? this.url.equals(var2.url) : false;
         }
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      byte var1 = 7;
      return 13 * var1 + (this.url != null ? this.url.hashCode() : 0);
   }

   @Override
   public String toString() {
      StringBuilder var1 = new StringBuilder();
      var1.append("/* ");
      if (this.url != null) {
         var1.append(this.url);
      }

      if (this.rules.isEmpty()) {
         var1.append(" */");
      } else {
         var1.append(" */\n");

         for (int var2 = 0; var2 < this.rules.size(); var2++) {
            var1.append(this.rules.get(var2));
            var1.append('\n');
         }
      }

      return var1.toString();
   }

   final void writeBinary(DataOutputStream var1, StyleConverter.StringStore var2) throws IOException {
      int var3 = var2.addString(this.origin.name());
      var1.writeShort(var3);
      var1.writeShort(this.rules.size());

      for (Rule var5 : this.rules) {
         var5.writeBinary(var1, var2);
      }

      List var8 = this.getFontFaces();
      int var9 = var8 != null ? var8.size() : 0;
      var1.writeShort(var9);

      for (int var6 = 0; var6 < var9; var6++) {
         FontFace var7 = (FontFace)var8.get(var6);
         if (var7 instanceof FontFaceImpl) {
            ((FontFaceImpl)var7).writeBinary(var1, var2);
         }
      }
   }

   final void readBinary(int var1, DataInputStream var2, String[] var3) throws IOException {
      this.stringStore = var3;
      short var4 = var2.readShort();
      this.setOrigin(StyleOrigin.valueOf(var3[var4]));
      short var5 = var2.readShort();
      ArrayList var6 = new ArrayList(var5);

      for (int var7 = 0; var7 < var5; var7++) {
         var6.add(Rule.readBinary(var1, var2, var3));
      }

      this.rules.addAll(var6);
      if (var1 >= 5) {
         List var11 = this.getFontFaces();
         short var8 = var2.readShort();

         for (int var9 = 0; var9 < var8; var9++) {
            FontFaceImpl var10 = FontFaceImpl.readBinary(var1, var2, var3);
            var11.add(var10);
         }
      }
   }

   final String[] getStringStore() {
      return this.stringStore;
   }

   public static Stylesheet loadBinary(URL var0) throws IOException {
      if (var0 == null) {
         return null;
      } else {
         try {
            Stylesheet var2;
            try (InputStream var1 = var0.openStream()) {
               var2 = loadBinary(var1, var0.toExternalForm());
            }

            return var2;
         } catch (FileNotFoundException var6) {
            return null;
         }
      }
   }

   public static Stylesheet loadBinary(InputStream var0) throws IOException {
      return loadBinary(var0, null);
   }

   private static Stylesheet loadBinary(InputStream var0, String var1) throws IOException {
      Stylesheet var2 = null;

      try (DataInputStream var3 = new DataInputStream(new BufferedInputStream(var0, 40960))) {
         short var4 = var3.readShort();
         if (var4 > 6) {
            throw new IOException(
               String.format(
                  "Wrong binary CSS version %s, expected version less than or equal to %s",
                  var1 != null ? var4 + " in stylesheet \"" + var1 + "\"" : Integer.valueOf(var4),
                  6
               )
            );
         }

         String[] var5 = StyleConverter.StringStore.readBinary(var3);
         var2 = new Stylesheet(var1);

         try {
            var3.mark(Integer.MAX_VALUE);
            var2.readBinary(var4, var3, var5);
         } catch (Exception var8) {
            var2 = new Stylesheet(var1);
            var3.reset();
            if (var4 == 2) {
               var2.readBinary(3, var3, var5);
            } else {
               var2.readBinary(6, var3, var5);
            }
         }
      }

      return var2;
   }

   public static void convertToBinary(File var0, File var1) throws IOException {
      if (var0 != null && var1 != null) {
         if (var0.getAbsolutePath().equals(var1.getAbsolutePath())) {
            throw new IllegalArgumentException("source and destination may not be the same");
         } else if (!var0.canRead()) {
            throw new IllegalArgumentException("cannot read source file");
         } else if (var1.exists() ? var1.canWrite() : var1.createNewFile()) {
            URI var2 = var0.toURI();
            Stylesheet var3 = new CssParser().parse(var2.toURL());
            ByteArrayOutputStream var4 = new ByteArrayOutputStream();
            DataOutputStream var5 = new DataOutputStream(var4);
            StyleConverter.StringStore var6 = new StyleConverter.StringStore();
            var3.writeBinary(var5, var6);
            var5.flush();
            var5.close();
            FileOutputStream var7 = new FileOutputStream(var1);
            DataOutputStream var8 = new DataOutputStream(var7);
            var8.writeShort(6);
            var6.writeBinary(var8);
            var8.write(var4.toByteArray());
            var8.flush();
            var8.close();
         } else {
            throw new IllegalArgumentException("cannot write destination file");
         }
      } else {
         throw new IllegalArgumentException("parameters may not be null");
      }
   }

   void importStylesheet(Stylesheet var1) {
      if (var1 != null) {
         List var2 = var1.getRules();
         if (var2 != null && !var2.isEmpty()) {
            ArrayList var3 = new ArrayList(var2.size());

            for (Rule var5 : var2) {
               ObservableList var6 = var5.getSelectors();
               List var7 = var5.getUnobservedDeclarationList();
               var3.add(new Rule(var6, var7));
            }

            this.rules.addAll(var3);
         }
      }
   }
}
