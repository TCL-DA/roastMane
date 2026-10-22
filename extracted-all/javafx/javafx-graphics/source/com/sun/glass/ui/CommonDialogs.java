package com.sun.glass.ui;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CommonDialogs {
   private CommonDialogs() {
   }

   public static CommonDialogs.FileChooserResult showFileChooser(
      Window var0, File var1, String var2, String var3, int var4, boolean var5, List<CommonDialogs.ExtensionFilter> var6, int var7
   ) {
      Application.checkEventThread();
      String var8 = convertFolder(var1);
      if (var2 == null) {
         var2 = "";
      }

      if (var4 != 0 && var4 != 1) {
         throw new IllegalArgumentException("Type parameter must be equal to one of the constants from Type");
      } else {
         CommonDialogs.ExtensionFilter[] var9 = null;
         if (var6 != null) {
            var9 = var6.toArray(new CommonDialogs.ExtensionFilter[var6.size()]);
         }

         if (var6 == null || var6.isEmpty() || var7 < 0 || var7 >= var6.size()) {
            var7 = 0;
         }

         return Application.GetApplication().staticCommonDialogs_showFileChooser(var0, var8, var2, convertTitle(var3), var4, var5, var9, var7);
      }
   }

   public static File showFolderChooser(Window var0, File var1, String var2) {
      Application.checkEventThread();
      return Application.GetApplication().staticCommonDialogs_showFolderChooser(var0, convertFolder(var1), convertTitle(var2));
   }

   private static String convertFolder(File var0) {
      if (var0 != null) {
         if (var0.isDirectory()) {
            try {
               return var0.getCanonicalPath();
            } catch (IOException var2) {
               throw new IllegalArgumentException("Unable to get a canonical path for folder", var2);
            }
         } else {
            throw new IllegalArgumentException("Folder parameter must be a valid folder");
         }
      } else {
         return "";
      }
   }

   private static String convertTitle(String var0) {
      return var0 != null ? var0 : "";
   }

   protected static CommonDialogs.FileChooserResult createFileChooserResult(String[] var0, CommonDialogs.ExtensionFilter[] var1, int var2) {
      ArrayList var3 = new ArrayList();

      for (String var7 : var0) {
         if (var7 != null) {
            var3.add(new File(var7));
         }
      }

      return new CommonDialogs.FileChooserResult(var3, var1 != null && var2 >= 0 && var2 < var1.length ? var1[var2] : null);
   }

   public static final class ExtensionFilter {
      private final String description;
      private final List<String> extensions;

      public ExtensionFilter(String var1, List<String> var2) {
         Application.checkEventThread();
         if (var1 == null || var1.trim().isEmpty()) {
            throw new IllegalArgumentException("Description parameter must be non-null and not empty");
         } else if (var2 != null && !var2.isEmpty()) {
            for (String var4 : var2) {
               if (var4 == null || var4.length() == 0) {
                  throw new IllegalArgumentException("Each extension must be non-null and not empty");
               }
            }

            this.description = var1;
            this.extensions = var2;
         } else {
            throw new IllegalArgumentException("Extensions parameter must be non-null and not empty");
         }
      }

      public String getDescription() {
         Application.checkEventThread();
         return this.description;
      }

      public List<String> getExtensions() {
         Application.checkEventThread();
         return this.extensions;
      }

      private String[] extensionsToArray() {
         Application.checkEventThread();
         return this.extensions.toArray(new String[this.extensions.size()]);
      }
   }

   public static final class FileChooserResult {
      private final List<File> files;
      private final CommonDialogs.ExtensionFilter filter;

      public FileChooserResult(List<File> var1, CommonDialogs.ExtensionFilter var2) {
         if (var1 == null) {
            throw new NullPointerException("files should not be null");
         } else {
            this.files = var1;
            this.filter = var2;
         }
      }

      public FileChooserResult() {
         this(new ArrayList<>(), null);
      }

      public List<File> getFiles() {
         return this.files;
      }

      public CommonDialogs.ExtensionFilter getExtensionFilter() {
         return this.filter;
      }
   }

   public static final class Type {
      public static final int OPEN = 0;
      public static final int SAVE = 1;
   }
}
