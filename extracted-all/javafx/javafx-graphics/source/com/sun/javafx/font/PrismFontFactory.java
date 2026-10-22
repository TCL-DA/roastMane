package com.sun.javafx.font;

import com.sun.glass.ui.Screen;
import com.sun.glass.utils.NativeLibLoader;
import com.sun.javafx.FXPermissions;
import com.sun.javafx.PlatformUtil;
import com.sun.javafx.text.GlyphLayout;
import java.io.File;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;

public abstract class PrismFontFactory implements FontFactory {
   public static final boolean debugFonts;
   public static final boolean isWindows = PlatformUtil.isWindows();
   public static final boolean isLinux = PlatformUtil.isLinux();
   public static final boolean isMacOSX = PlatformUtil.isMac();
   public static final boolean isIOS = PlatformUtil.isIOS();
   public static final boolean isAndroid = PlatformUtil.isAndroid();
   public static final boolean isEmbedded = PlatformUtil.isEmbedded();
   public static final int cacheLayoutSize;
   private static int subPixelMode;
   public static final int SUB_PIXEL_ON = 1;
   public static final int SUB_PIXEL_Y = 2;
   public static final int SUB_PIXEL_NATIVE = 4;
   private static float fontSizeLimit = 80.0F;
   private static boolean lcdEnabled;
   private static float lcdContrast = -1.0F;
   private static String jreFontDir;
   private static final String jreDefaultFont = "Lucida Sans Regular";
   private static final String jreDefaultFontLC = "lucida sans regular";
   private static final String jreDefaultFontFile = "LucidaSansRegular.ttf";
   private static final String CT_FACTORY = "com.sun.javafx.font.coretext.CTFactory";
   private static final String DW_FACTORY = "com.sun.javafx.font.directwrite.DWFactory";
   private static final String FT_FACTORY = "com.sun.javafx.font.freetype.FTFactory";
   HashMap<String, FontResource> fontResourceMap = new HashMap<>();
   HashMap<String, CompositeFontResource> compResourceMap = new HashMap<>();
   private static PrismFontFactory theFontFactory = null;
   private HashMap<String, PrismFontFile> fileNameToFontResourceMap = new HashMap<>();
   private ArrayList<WeakReference<PrismFontFile>> tmpFonts;
   private static final String[] STR_ARRAY = new String[0];
   private volatile HashMap<String, String> fontToFileMap = null;
   private HashMap<String, String> fileToFontMap = null;
   private HashMap<String, String> fontToFamilyNameMap = null;
   private HashMap<String, ArrayList<String>> familyToFontListMap = null;
   private static String sysFontDir = null;
   private static String userFontDir = null;
   private static ArrayList<String> allFamilyNames;
   private static ArrayList<String> allFontNames;
   private static Thread fileCloser = null;
   private HashMap<String, PrismFontFile> embeddedFonts;
   private int numEmbeddedFonts = 0;
   private static float systemFontSize;
   private static String systemFontFamily = null;
   private static String monospaceFontFamily = null;

   private static String getJDKFontDir() {
      return System.getProperty("java.home", "") + File.separator + "lib" + File.separator + "fonts";
   }

   private static String getNativeFactoryName() {
      if (isWindows) {
         return "com.sun.javafx.font.directwrite.DWFactory";
      } else if (isMacOSX || isIOS) {
         return "com.sun.javafx.font.coretext.CTFactory";
      } else {
         return !isLinux && !isAndroid ? null : "com.sun.javafx.font.freetype.FTFactory";
      }
   }

   public static float getFontSizeLimit() {
      return fontSizeLimit;
   }

   public static synchronized PrismFontFactory getFontFactory() {
      if (theFontFactory != null) {
         return theFontFactory;
      } else {
         String var0 = getNativeFactoryName();
         if (var0 == null) {
            throw new InternalError("cannot find a native font factory");
         } else {
            if (debugFonts) {
               System.err.println("Loading FontFactory " + var0);
               if (subPixelMode != 0) {
                  String var1 = "Subpixel: enabled";
                  if ((subPixelMode & 2) != 0) {
                     var1 = var1 + ", vertical";
                  }

                  if ((subPixelMode & 4) != 0) {
                     var1 = var1 + ", native";
                  }

                  System.err.println(var1);
               }
            }

            theFontFactory = getFontFactory(var0);
            if (theFontFactory == null) {
               throw new InternalError("cannot load font factory: " + var0);
            } else {
               return theFontFactory;
            }
         }
      }
   }

   private static synchronized PrismFontFactory getFontFactory(String var0) {
      try {
         Class var1 = Class.forName(var0);
         Method var2 = var1.getMethod("getFactory", (Class<?>[])null);
         return (PrismFontFactory)var2.invoke(null);
      } catch (Throwable var3) {
         if (debugFonts) {
            System.err.println("Loading font factory failed " + var0);
         }

         return null;
      }
   }

   protected abstract PrismFontFile createFontFile(String var1, String var2, int var3, boolean var4, boolean var5, boolean var6, boolean var7) throws Exception;

   public abstract GlyphLayout createGlyphLayout();

   private PrismFontFile createFontResource(String var1, int var2) {
      return this.createFontResource(null, var1, var2, true, false, false, false);
   }

   private PrismFontFile createFontResource(String var1, String var2, int var3, boolean var4, boolean var5, boolean var6, boolean var7) {
      String var8 = (var2 + var3).toLowerCase();
      PrismFontFile var9 = this.fileNameToFontResourceMap.get(var8);
      if (var9 != null) {
         return var9;
      } else {
         try {
            var9 = this.createFontFile(var1, var2, var3, var4, var5, var6, var7);
            if (var4) {
               this.storeInMap(var9.getFullName(), var9);
               this.fileNameToFontResourceMap.put(var8, var9);
            }

            return var9;
         } catch (Exception var11) {
            if (debugFonts) {
               var11.printStackTrace();
            }

            return null;
         }
      }
   }

   private PrismFontFile createFontResource(String var1, String var2) {
      PrismFontFile[] var3 = this.createFontResources(var1, var2, true, false, false, false, false);
      return var3 != null && var3.length != 0 ? var3[0] : null;
   }

   private PrismFontFile[] createFontResources(String var1, String var2, boolean var3, boolean var4, boolean var5, boolean var6, boolean var7) {
      PrismFontFile var8 = null;
      if (var2 == null) {
         return null;
      } else {
         PrismFontFile var9 = this.createFontResource(var1, var2, 0, var3, var4, var5, var6);
         if (var9 == null) {
            return null;
         } else {
            int var10 = !var7 ? 1 : var9.getFontCount();
            var8 = new PrismFontFile[var10];
            ((Object[])var8)[0] = var9;
            if (var10 == 1) {
               return var8;
            } else {
               PrismFontFile.FileRefCounter var11 = null;
               if (var5) {
                  var11 = var9.createFileRefCounter();
               }

               int var12 = 1;

               do {
                  String var13 = (var2 + var12).toLowerCase();

                  try {
                     var9 = this.fileNameToFontResourceMap.get(var13);
                     if (var9 != null) {
                        ((Object[])var8)[var12] = var9;
                     } else {
                        var9 = this.createFontFile(null, var2, var12, var3, var4, var5, var6);
                        if (var9 == null) {
                           return null;
                        }

                        if (var11 != null) {
                           var9.setAndIncFileRefCounter(var11);
                        }

                        ((Object[])var8)[var12] = var9;
                        String var14 = var9.getFullName();
                        if (var3) {
                           this.storeInMap(var14, var9);
                           this.fileNameToFontResourceMap.put(var13, var9);
                        }
                     }
                  } catch (Exception var15) {
                     if (debugFonts) {
                        var15.printStackTrace();
                     }

                     return null;
                  }
               } while (++var12 < var10);

               return var8;
            }
         }
      }
   }

   private String dotStyleStr(boolean var1, boolean var2) {
      if (!var1) {
         return !var2 ? "" : ".italic";
      } else {
         return !var2 ? ".bold" : ".bolditalic";
      }
   }

   private void storeInMap(String var1, FontResource var2) {
      if (var1 != null && var2 != null) {
         if (var2 instanceof PrismCompositeFontResource) {
            System.err.println(var1 + " is a composite " + var2);
            Thread.dumpStack();
         } else {
            this.fontResourceMap.put(var1.toLowerCase(), var2);
         }
      }
   }

   synchronized void addDecodedFont(PrismFontFile var1) {
      var1.setIsDecoded(true);
      this.addTmpFont(var1);
   }

   private synchronized void addTmpFont(PrismFontFile var1) {
      if (this.tmpFonts == null) {
         this.tmpFonts = new ArrayList<>();
      }

      WeakReference var2;
      if (var1.isRegistered()) {
         var2 = new WeakReference<>(var1);
      } else {
         var2 = var1.createFileDisposer(this, var1.getFileRefCounter());
      }

      this.tmpFonts.add(var2);
      this.addFileCloserHook();
   }

   synchronized void removeTmpFont(WeakReference<PrismFontFile> var1) {
      if (this.tmpFonts != null) {
         this.tmpFonts.remove(var1);
      }
   }

   public synchronized FontResource getFontResource(String var1, boolean var2, boolean var3, boolean var4) {
      if (var1 != null && !var1.isEmpty()) {
         String var5 = var1.toLowerCase();
         String var6 = this.dotStyleStr(var2, var3);
         FontResource var7 = this.lookupResource(var5 + var6, var4);
         if (var7 != null) {
            return var7;
         } else {
            if (this.embeddedFonts != null && var4) {
               var7 = this.lookupResource(var5 + var6, false);
               if (var7 != null) {
                  return new PrismCompositeFontResource(var7, var5 + var6);
               }

               for (PrismFontFile var9 : this.embeddedFonts.values()) {
                  String var10 = var9.getFamilyName().toLowerCase();
                  if (var10.equals(var5)) {
                     return new PrismCompositeFontResource(var9, var5 + var6);
                  }
               }
            }

            if (isWindows) {
               int var21 = (var2 ? 1 : 0) + (var3 ? 2 : 0);
               String var23 = WindowsFontMap.findFontFile(var5, var21);
               if (var23 != null) {
                  var7 = this.createFontResource(null, var23);
                  if (var7 != null) {
                     if (var2 == var7.isBold() && var3 == var7.isItalic() && !var6.isEmpty()) {
                        this.storeInMap(var5 + var6, var7);
                     }

                     if (var4) {
                        var7 = new PrismCompositeFontResource(var7, var5 + var6);
                     }

                     return var7;
                  }
               }
            }

            this.getFullNameToFileMap();
            ArrayList var22 = this.familyToFontListMap.get(var5);
            if (var22 == null) {
               return null;
            } else {
               Object var24 = null;
               Object var25 = null;
               Object var11 = null;
               Object var12 = null;

               for (String var14 : var22) {
                  String var15 = var14.toLowerCase();
                  var7 = this.fontResourceMap.get(var15);
                  if (var7 == null) {
                     String var16 = this.findFile(var15);
                     if (var16 != null) {
                        var7 = this.getFontResource(var14, var16);
                     }

                     if (var7 == null) {
                        continue;
                     }

                     this.storeInMap(var15, var7);
                  }

                  if (var2 == var7.isBold() && var3 == var7.isItalic()) {
                     this.storeInMap(var5 + var6, var7);
                     if (var4) {
                        var7 = new PrismCompositeFontResource(var7, var5 + var6);
                     }

                     return var7;
                  }

                  if (!var7.isBold()) {
                     if (!var7.isItalic()) {
                        var24 = var7;
                     } else {
                        var11 = var7;
                     }
                  } else if (!var7.isItalic()) {
                     var25 = var7;
                  } else {
                     var12 = var7;
                  }
               }

               if (!var2 && !var3) {
                  if (var25 != null) {
                     var7 = (FontResource)var25;
                  } else if (var11 != null) {
                     var7 = (FontResource)var11;
                  } else {
                     var7 = (FontResource)var12;
                  }
               } else if (var2 && !var3) {
                  if (var24 != null) {
                     var7 = (FontResource)var24;
                  } else if (var12 != null) {
                     var7 = (FontResource)var12;
                  } else {
                     var7 = (FontResource)var11;
                  }
               } else if (!var2 && var3) {
                  if (var12 != null) {
                     var7 = (FontResource)var12;
                  } else if (var24 != null) {
                     var7 = (FontResource)var24;
                  } else {
                     var7 = (FontResource)var25;
                  }
               } else if (var11 != null) {
                  var7 = (FontResource)var11;
               } else if (var25 != null) {
                  var7 = (FontResource)var25;
               } else {
                  var7 = (FontResource)var24;
               }

               if (var7 != null) {
                  this.storeInMap(var5 + var6, var7);
                  if (var4) {
                     var7 = new PrismCompositeFontResource(var7, var5 + var6);
                  }
               }

               return var7;
            }
         }
      } else {
         return null;
      }
   }

   @Override
   public synchronized PGFont createFont(String var1, boolean var2, boolean var3, float var4) {
      FontResource var5 = null;
      if (var1 != null && !var1.isEmpty()) {
         PGFont var6 = LogicalFont.getLogicalFont(var1, var2, var3, var4);
         if (var6 != null) {
            return var6;
         }

         var5 = this.getFontResource(var1, var2, var3, true);
      }

      return (PGFont)(var5 == null ? LogicalFont.getLogicalFont("System", var2, var3, var4) : new PrismFont(var5, var5.getFullName(), var4));
   }

   @Override
   public synchronized PGFont createFont(String var1, float var2) {
      FontResource var3 = null;
      if (var1 != null && !var1.isEmpty()) {
         PGFont var4 = LogicalFont.getLogicalFont(var1, var2);
         if (var4 != null) {
            return var4;
         }

         var3 = this.getFontResource(var1, null, true);
      }

      return (PGFont)(var3 == null ? LogicalFont.getLogicalFont("System Regular", var2) : new PrismFont(var3, var3.getFullName(), var2));
   }

   private PrismFontFile getFontResource(String var1, String var2) {
      PrismFontFile var3 = null;
      if (isMacOSX) {
         DFontDecoder var4 = null;
         if (var1 != null && var2.endsWith(".dfont")) {
            var4 = new DFontDecoder();

            try {
               var4.openFile();
               var4.decode(var1);
               var4.closeFile();
               var2 = var4.getFile().getPath();
            } catch (Exception var6) {
               var2 = null;
               var4.deleteFile();
               var4 = null;
               if (debugFonts) {
                  var6.printStackTrace();
               }
            }
         }

         if (var2 != null) {
            var3 = this.createFontResource(var1, var2);
         }

         if (var4 != null) {
            if (var3 != null) {
               this.addDecodedFont(var3);
            } else {
               var4.deleteFile();
            }
         }
      } else {
         var3 = this.createFontResource(var1, var2);
      }

      return var3;
   }

   @Override
   public synchronized PGFont deriveFont(PGFont var1, boolean var2, boolean var3, float var4) {
      FontResource var5 = var1.getFontResource();
      return new PrismFont(var5, var5.getFullName(), var4);
   }

   private FontResource lookupResource(String var1, boolean var2) {
      return var2 ? this.compResourceMap.get(var1) : this.fontResourceMap.get(var1);
   }

   public synchronized FontResource getFontResource(String var1, String var2, boolean var3) {
      FontResource var4 = null;
      if (var1 != null) {
         String var5 = var1.toLowerCase();
         FontResource var6 = this.lookupResource(var5, var3);
         if (var6 != null) {
            return var6;
         }

         if (this.embeddedFonts != null && var3) {
            var4 = this.lookupResource(var5, false);
            if (var4 != null) {
               var4 = new PrismCompositeFontResource((FontResource)var4, var5);
            }

            if (var4 != null) {
               return (FontResource)var4;
            }
         }
      }

      if (isWindows && var1 != null) {
         String var12 = var1.toLowerCase();
         String var13 = WindowsFontMap.findFontFile(var12, -1);
         if (var13 != null) {
            var4 = this.createFontResource(null, var13);
            if (var4 != null) {
               if (var3) {
                  var4 = new PrismCompositeFontResource((FontResource)var4, var12);
               }

               return (FontResource)var4;
            }
         }
      }

      this.getFullNameToFileMap();
      if (var1 != null && var2 != null) {
         var4 = this.getFontResource(var1, var2);
         if (var4 != null) {
            if (var3) {
               var4 = new PrismCompositeFontResource((FontResource)var4, var1.toLowerCase());
            }

            return (FontResource)var4;
         }
      }

      if (var1 != null) {
         var4 = this.getFontResourceByFullName(var1, var3);
         if (var4 != null) {
            return var4;
         }
      }

      if (var2 != null) {
         var4 = this.getFontResourceByFileName(var2, var3);
         if (var4 != null) {
            return var4;
         }
      }

      return null;
   }

   boolean isInstalledFont(String var1) {
      String var2;
      if (isWindows) {
         if (var1.toLowerCase().contains("\\windows\\fonts")) {
            return true;
         }

         File var3 = new File(var1);
         var2 = var3.getName();
      } else {
         if (isMacOSX && var1.toLowerCase().contains("/library/fonts")) {
            return true;
         }

         File var4 = new File(var1);
         var2 = var4.getPath();
      }

      this.getFullNameToFileMap();
      return this.fileToFontMap.get(var2.toLowerCase()) != null;
   }

   private synchronized FontResource getFontResourceByFileName(String var1, boolean var2) {
      if (this.fontToFileMap.size() <= 1) {
         return null;
      } else {
         String var3 = this.fileToFontMap.get(var1.toLowerCase());
         Object var4 = null;
         if (var3 == null) {
            var4 = this.createFontResource(var1, 0);
            if (var4 != null) {
               String var5 = ((FontResource)var4).getFullName().toLowerCase();
               this.storeInMap(var5, (FontResource)var4);
               if (var2) {
                  var4 = new PrismCompositeFontResource((FontResource)var4, var5);
               }
            }
         } else {
            String var8 = var3.toLowerCase();
            var4 = this.lookupResource(var8, var2);
            if (var4 == null) {
               String var6 = this.findFile(var8);
               if (var6 != null) {
                  var4 = this.getFontResource(var3, var6);
                  if (var4 != null) {
                     this.storeInMap(var8, (FontResource)var4);
                  }

                  if (var2) {
                     var4 = new PrismCompositeFontResource((FontResource)var4, var8);
                  }
               }
            }
         }

         return (FontResource)var4;
      }
   }

   private synchronized FontResource getFontResourceByFullName(String var1, boolean var2) {
      String var3 = var1.toLowerCase();
      if (this.fontToFileMap.size() <= 1) {
         var1 = "Lucida Sans Regular";
      }

      Object var4 = null;
      String var5 = this.findFile(var3);
      if (var5 != null) {
         var4 = this.getFontResource(var1, var5);
         if (var4 != null) {
            this.storeInMap(var3, (FontResource)var4);
            if (var2) {
               var4 = new PrismCompositeFontResource((FontResource)var4, var3);
            }
         }
      }

      return (FontResource)var4;
   }

   FontResource getDefaultFontResource(boolean var1) {
      Object var2 = this.lookupResource("lucida sans regular", var1);
      if (var2 == null) {
         var2 = this.createFontResource("Lucida Sans Regular", jreFontDir + "LucidaSansRegular.ttf");
         if (var2 == null) {
            for (String var4 : this.fontToFileMap.keySet()) {
               String var5 = this.findFile(var4);
               var2 = this.createFontResource("lucida sans regular", var5);
               if (var2 != null) {
                  break;
               }
            }

            if (var2 == null && isLinux) {
               String var6 = FontConfigManager.getDefaultFontPath();
               if (var6 != null) {
                  var2 = this.createFontResource("lucida sans regular", var6);
               }
            }

            if (var2 == null) {
               return null;
            }
         }

         this.storeInMap("lucida sans regular", (FontResource)var2);
         if (var1) {
            var2 = new PrismCompositeFontResource((FontResource)var2, "lucida sans regular");
         }
      }

      return (FontResource)var2;
   }

   private String findFile(String var1) {
      if (var1.equals("lucida sans regular")) {
         return jreFontDir + "LucidaSansRegular.ttf";
      } else {
         this.getFullNameToFileMap();
         String var2 = this.fontToFileMap.get(var1);
         if (isWindows) {
            var2 = getPathNameWindows(var2);
         }

         return var2;
      }
   }

   private static native byte[] getFontPath();

   private static native String regReadFontLink(String var0);

   private static native String getEUDCFontFile();

   private static void getPlatformFontDirs() {
      if (userFontDir == null && sysFontDir == null) {
         byte[] var0 = getFontPath();
         String var1 = new String(var0);
         int var2 = var1.indexOf(59);
         if (var2 < 0) {
            sysFontDir = var1;
         } else {
            sysFontDir = var1.substring(0, var2);
            userFontDir = var1.substring(var2 + 1, var1.length());
         }
      }
   }

   static ArrayList<String>[] getLinkedFonts(String var0, boolean var1) {
      ArrayList[] var2 = new ArrayList[]{new ArrayList(), new ArrayList()};
      if (isMacOSX) {
         var2[0].add("/Library/Fonts/Arial Unicode.ttf");
         var2[1].add("Arial Unicode MS");
         var2[0].add(jreFontDir + "LucidaSansRegular.ttf");
         var2[1].add("Lucida Sans Regular");
         var2[0].add("/System/Library/Fonts/Apple Symbols.ttf");
         var2[1].add("Apple Symbols");
         var2[0].add("/System/Library/Fonts/Apple Color Emoji.ttc");
         var2[1].add("Apple Color Emoji");
         var2[0].add("/System/Library/Fonts/STHeiti Light.ttf");
         var2[1].add("Heiti SC Light");
         return var2;
      } else if (!isWindows) {
         return var2;
      } else {
         if (var1) {
            var2[0].add(null);
            var2[1].add(var0);
         }

         String var3 = regReadFontLink(var0);
         if (var3 != null && var3.length() > 0) {
            String[] var4 = var3.split("\u0000");
            int var5 = var4.length;

            for (int var6 = 0; var6 < var5; var6++) {
               String[] var7 = var4[var6].split(",");
               int var8 = var7.length;
               String var9 = getPathNameWindows(var7[0]);
               String var10 = var8 > 1 ? var7[1] : null;
               if ((var10 == null || !var2[1].contains(var10)) && (var10 != null || !var2[0].contains(var9))) {
                  var2[0].add(var9);
                  var2[1].add(var10);
               }
            }
         }

         String var11 = getEUDCFontFile();
         if (var11 != null) {
            var2[0].add(var11);
            var2[1].add(null);
         }

         var2[0].add(jreFontDir + "LucidaSansRegular.ttf");
         var2[1].add("Lucida Sans Regular");
         if (PlatformUtil.isWinVistaOrLater()) {
            var2[0].add(getPathNameWindows("mingliub.ttc"));
            var2[1].add("MingLiU-ExtB");
            if (PlatformUtil.isWin7OrLater()) {
               var2[0].add(getPathNameWindows("seguisym.ttf"));
               var2[1].add("Segoe UI Symbol");
            } else {
               var2[0].add(getPathNameWindows("cambria.ttc"));
               var2[1].add("Cambria Math");
            }
         }

         return var2;
      }
   }

   private void resolveWindowsFonts(HashMap<String, String> var1, HashMap<String, String> var2, HashMap<String, ArrayList<String>> var3) {
      ArrayList var4 = null;

      for (String var6 : var2.keySet()) {
         String var7 = (String)var1.get(var6);
         if (var7 == null) {
            int var8 = var6.indexOf("  ");
            if (var8 > 0) {
               String var9 = var6.substring(0, var8);
               var9 = var9.concat(var6.substring(var8 + 1));
               var7 = (String)var1.get(var9);
               if (var7 != null && !var2.containsKey(var9)) {
                  var1.remove(var9);
                  var1.put(var6, var7);
               }
            } else if (var6.equals("marlett")) {
               var1.put(var6, "marlett.ttf");
            } else if (var6.equals("david")) {
               var7 = (String)var1.get("david regular");
               if (var7 != null) {
                  var1.remove("david regular");
                  var1.put("david", var7);
               }
            } else {
               if (var4 == null) {
                  var4 = new ArrayList();
               }

               var4.add(var6);
            }
         }
      }

      if (var4 != null) {
         HashSet var12 = new HashSet();
         HashMap var13 = new HashMap();
         var13.putAll(var1);

         for (String var19 : var2.keySet()) {
            var13.remove(var19);
         }

         for (String var20 : var13.keySet()) {
            var12.add((String)var13.get(var20));
            var1.remove(var20);
         }

         this.resolveFontFiles(var12, var4, var1, var2, var3);
         if (var4.size() > 0) {
            int var18 = var4.size();

            for (int var21 = 0; var21 < var18; var21++) {
               String var23 = (String)var4.get(var21);
               String var10 = (String)var2.get(var23);
               if (var10 != null) {
                  ArrayList var11 = (ArrayList)var3.get(var10);
                  if (var11 != null && var11.size() <= 1) {
                     var3.remove(var10);
                  }
               }

               var2.remove(var23);
            }
         }
      }
   }

   private void resolveFontFiles(
      HashSet<String> var1, ArrayList<String> var2, HashMap<String, String> var3, HashMap<String, String> var4, HashMap<String, ArrayList<String>> var5
   ) {
      for (String var7 : var1) {
         try {
            int var8 = 0;
            String var10 = getPathNameWindows(var7);

            while (true) {
               PrismFontFile var9 = this.createFontResource(var10, var8++);
               if (var9 == null) {
                  break;
               }

               String var11 = var9.getFullName().toLowerCase();
               String var12 = var9.getLocaleFullName().toLowerCase();
               if (var2.contains(var11) || var2.contains(var12)) {
                  var3.put(var11, var7);
                  var2.remove(var11);
                  if (var2.contains(var12)) {
                     var2.remove(var12);
                     String var13 = var9.getFamilyName();
                     String var14 = var13.toLowerCase();
                     var4.remove(var12);
                     var4.put(var11, var13);
                     ArrayList var15 = (ArrayList)var5.get(var14);
                     if (var15 != null) {
                        var15.remove(var9.getLocaleFullName());
                     } else {
                        String var16 = var9.getLocaleFamilyName().toLowerCase();
                        var15 = (ArrayList)var5.get(var16);
                        if (var15 != null) {
                           var5.remove(var16);
                        }

                        var15 = new ArrayList();
                        var5.put(var14, var15);
                     }

                     var15.add(var9.getFullName());
                  }
               }

               if (var8 >= var9.getFontCount()) {
                  break;
               }
            }
         } catch (Exception var17) {
            if (debugFonts) {
               var17.printStackTrace();
            }
         }
      }
   }

   static native void populateFontFileNameMap(HashMap<String, String> var0, HashMap<String, String> var1, HashMap<String, ArrayList<String>> var2, Locale var3);

   static String getPathNameWindows(final String var0) {
      if (var0 == null) {
         return null;
      } else {
         getPlatformFontDirs();
         File var1 = new File(var0);
         if (var1.isAbsolute()) {
            return var0;
         } else if (userFontDir == null) {
            return sysFontDir + "\\" + var0;
         } else {
            String var2 = AccessController.doPrivileged(new PrivilegedAction<String>() {
               public String run() {
                  File var1 = new File(PrismFontFactory.sysFontDir + "\\" + var0);
                  return var1.exists() ? var1.getAbsolutePath() : PrismFontFactory.userFontDir + "\\" + var0;
               }
            });
            return var2 != null ? var2 : null;
         }
      }
   }

   @Override
   public String[] getFontFamilyNames() {
      if (allFamilyNames == null) {
         ArrayList var1 = new ArrayList();
         LogicalFont.addFamilies(var1);
         if (this.embeddedFonts != null) {
            for (PrismFontFile var3 : this.embeddedFonts.values()) {
               if (!var1.contains(var3.getFamilyName())) {
                  var1.add(var3.getFamilyName());
               }
            }
         }

         this.getFullNameToFileMap();

         for (String var5 : this.fontToFamilyNameMap.values()) {
            if (!var1.contains(var5)) {
               var1.add(var5);
            }
         }

         Collections.sort(var1);
         allFamilyNames = new ArrayList<>(var1);
      }

      return allFamilyNames.toArray(STR_ARRAY);
   }

   @Override
   public String[] getFontFullNames() {
      if (allFontNames == null) {
         ArrayList var1 = new ArrayList();
         LogicalFont.addFullNames(var1);
         if (this.embeddedFonts != null) {
            for (PrismFontFile var3 : this.embeddedFonts.values()) {
               if (!var1.contains(var3.getFullName())) {
                  var1.add(var3.getFullName());
               }
            }
         }

         this.getFullNameToFileMap();

         for (ArrayList var7 : this.familyToFontListMap.values()) {
            for (String var5 : var7) {
               var1.add(var5);
            }
         }

         Collections.sort(var1);
         allFontNames = var1;
      }

      return allFontNames.toArray(STR_ARRAY);
   }

   @Override
   public String[] getFontFullNames(String var1) {
      String[] var2 = LogicalFont.getFontsInFamily(var1);
      if (var2 != null) {
         return var2;
      } else {
         if (this.embeddedFonts != null) {
            ArrayList var3 = null;

            for (PrismFontFile var5 : this.embeddedFonts.values()) {
               if (var5.getFamilyName().equalsIgnoreCase(var1)) {
                  if (var3 == null) {
                     var3 = new ArrayList();
                  }

                  var3.add(var5.getFullName());
               }
            }

            if (var3 != null) {
               return var3.toArray(STR_ARRAY);
            }
         }

         this.getFullNameToFileMap();
         var1 = var1.toLowerCase();
         ArrayList var7 = this.familyToFontListMap.get(var1);
         return var7 != null ? var7.toArray(STR_ARRAY) : STR_ARRAY;
      }
   }

   public final int getSubPixelMode() {
      return subPixelMode;
   }

   public boolean isLCDTextSupported() {
      return lcdEnabled;
   }

   @Override
   public boolean isPlatformFont(String var1) {
      if (var1 == null) {
         return false;
      } else {
         String var2 = var1.toLowerCase();
         if (LogicalFont.isLogicalFont(var2)) {
            return true;
         } else if (var2.startsWith("lucida sans")) {
            return true;
         } else {
            String var3 = getSystemFont("System").toLowerCase();
            return var2.startsWith(var3);
         }
      }
   }

   public static boolean isJreFont(FontResource var0) {
      String var1 = var0.getFileName();
      return var1.startsWith(jreFontDir);
   }

   public static float getLCDContrast() {
      if (lcdContrast == -1.0F) {
         if (isWindows) {
            lcdContrast = (float)getLCDContrastWin32() / 1000.0F;
         } else {
            lcdContrast = 1.3F;
         }
      }

      return lcdContrast;
   }

   private synchronized void addFileCloserHook() {
      if (fileCloser == null) {
         Runnable var1 = () -> {
            if (this.embeddedFonts != null) {
               for (PrismFontFile var2x : this.embeddedFonts.values()) {
                  var2x.disposeOnShutdown();
               }
            }

            if (this.tmpFonts != null) {
               for (WeakReference var5 : this.tmpFonts) {
                  PrismFontFile var3 = (PrismFontFile)var5.get();
                  if (var3 != null) {
                     var3.disposeOnShutdown();
                  }
               }
            }
         };
         Object var2 = AccessController.doPrivileged((PrivilegedAction)(() -> {
            ThreadGroup var1x = Thread.currentThread().getThreadGroup();

            for (ThreadGroup var2x = var1x; var2x != null; var2x = var2x.getParent()) {
               var1x = var2x;
            }

            fileCloser = new Thread(var1x, var1);
            fileCloser.setContextClassLoader(null);
            Runtime.getRuntime().addShutdownHook(fileCloser);
            return null;
         }));
      }
   }

   @Override
   public PGFont[] loadEmbeddedFont(String var1, InputStream var2, float var3, boolean var4, boolean var5) {
      if (!this.hasPermission()) {
         return new PGFont[]{this.createFont("System Regular", var3)};
      } else if (FontFileWriter.hasTempPermission()) {
         return this.loadEmbeddedFont0(var1, var2, var3, var4, var5);
      } else {
         FontFileWriter.FontTracker var6 = FontFileWriter.FontTracker.getTracker();
         boolean var7 = false;

         Object var8;
         try {
            var7 = var6.acquirePermit();
            if (var7) {
               return this.loadEmbeddedFont0(var1, var2, var3, var4, var5);
            }

            var8 = null;
         } catch (InterruptedException var13) {
            return null;
         } finally {
            if (var7) {
               var6.releasePermit();
            }
         }

         return (PGFont[])var8;
      }
   }

   private PGFont[] loadEmbeddedFont0(String var1, InputStream var2, float var3, boolean var4, boolean var5) {
      PrismFontFile[] var6 = null;
      FontFileWriter var7 = new FontFileWriter();

      try {
         File var8 = var7.openFile();
         byte[] var9 = new byte[8192];

         while (true) {
            int var10 = var2.read(var9);
            if (var10 < 0) {
               var7.closeFile();
               var6 = this.loadEmbeddedFont1(var1, var8.getPath(), var4, true, var7.isTracking(), var5);
               if (var6 != null && var6.length > 0 && var6[0].isDecoded()) {
                  var7.deleteFile();
               }

               this.addFileCloserHook();
               break;
            }

            var7.writeBytes(var9, 0, var10);
         }
      } catch (Exception var14) {
         var7.deleteFile();
      } finally {
         if (var6 == null) {
            var7.deleteFile();
         }
      }

      if (var6 != null && var6.length > 0) {
         if (var3 <= 0.0F) {
            var3 = getSystemFontSize();
         }

         int var16 = var6.length;
         PrismFont[] var17 = new PrismFont[var16];

         for (int var18 = 0; var18 < var16; var18++) {
            var17[var18] = new PrismFont(var6[var18], var6[var18].getFullName(), var3);
         }

         return var17;
      } else {
         return null;
      }
   }

   @Override
   public PGFont[] loadEmbeddedFont(String var1, String var2, float var3, boolean var4, boolean var5) {
      if (!this.hasPermission()) {
         return new PGFont[]{this.createFont("System Regular", var3)};
      } else {
         this.addFileCloserHook();
         PrismFontFile[] var6 = this.loadEmbeddedFont1(var1, var2, var4, false, false, var5);
         if (var6 != null && var6.length > 0) {
            if (var3 <= 0.0F) {
               var3 = getSystemFontSize();
            }

            int var7 = var6.length;
            PGFont[] var8 = new PGFont[var7];

            for (int var9 = 0; var9 < var7; var9++) {
               var8[var9] = new PrismFont(var6[var9], var6[var9].getFullName(), var3);
            }

            return var8;
         } else {
            return null;
         }
      }
   }

   private void removeEmbeddedFont(String var1) {
      PrismFontFile var2 = this.embeddedFonts.get(var1);
      if (var2 != null) {
         this.embeddedFonts.remove(var1);
         String var3 = var1.toLowerCase();
         this.fontResourceMap.remove(var3);
         this.compResourceMap.remove(var3);
         Iterator var4 = this.compResourceMap.values().iterator();

         while (var4.hasNext()) {
            CompositeFontResource var5 = (CompositeFontResource)var4.next();
            if (var5.getSlotResource(0) == var2) {
               var4.remove();
            }
         }
      }
   }

   protected boolean registerEmbeddedFont(String var1) {
      return true;
   }

   public int test_getNumEmbeddedFonts() {
      return this.numEmbeddedFonts;
   }

   private synchronized PrismFontFile[] loadEmbeddedFont1(String var1, String var2, boolean var3, boolean var4, boolean var5, boolean var6) {
      this.numEmbeddedFonts++;
      PrismFontFile[] var7 = this.createFontResources(var1, var2, var3, true, var4, var5, var6);
      if (var7 != null && var7.length != 0) {
         if (this.embeddedFonts == null) {
            this.embeddedFonts = new HashMap<>();
         }

         boolean var8 = true;
         int var9 = 0;

         while (var9 < var7.length) {
            PrismFontFile var10 = var7[var9];
            String var11 = var10.getFamilyName();
            if (var11 != null && var11.length() != 0) {
               String var12 = var10.getFullName();
               if (var12 != null && var12.length() != 0) {
                  String var13 = var10.getPSName();
                  if (var13 != null && var13.length() != 0) {
                     FontResource var14 = this.embeddedFonts.get(var12);
                     if (var14 != null && var10.equals(var14)) {
                        var8 = false;
                     }

                     var9++;
                     continue;
                  }

                  return null;
               }

               return null;
            }

            return null;
         }

         if (var8 && !this.registerEmbeddedFont(var7[0].getFileName())) {
            return null;
         } else {
            if (var4 && !var7[0].isDecoded()) {
               this.addTmpFont(var7[0]);
            }

            if (!var3) {
               return var7;
            } else {
               if (var1 != null && !var1.isEmpty()) {
                  this.embeddedFonts.put(var1, var7[0]);
                  this.storeInMap(var1, var7[0]);
               }

               for (int var15 = 0; var15 < var7.length; var15++) {
                  PrismFontFile var16 = var7[var15];
                  String var17 = var16.getFamilyName();
                  String var19 = var16.getFullName();
                  this.removeEmbeddedFont(var19);
                  this.embeddedFonts.put(var19, var16);
                  this.storeInMap(var19, var16);
                  var17 = var17 + this.dotStyleStr(var16.isBold(), var16.isItalic());
                  this.storeInMap(var17, var16);
                  this.compResourceMap.remove(var17.toLowerCase());
               }

               return var7;
            }
         }
      } else {
         return null;
      }
   }

   private void logFontInfo(String var1, HashMap<String, String> var2, HashMap<String, String> var3, HashMap<String, ArrayList<String>> var4) {
      System.err.println(var1);

      for (String var6 : var2.keySet()) {
         System.err.println("font=" + var6 + " file=" + (String)var2.get(var6));
      }

      for (String var9 : var3.keySet()) {
         System.err.println("font=" + var9 + " family=" + (String)var3.get(var9));
      }

      for (String var10 : var4.keySet()) {
         System.err.println("family=" + var10 + " fonts=" + var4.get(var10));
      }
   }

   private synchronized HashMap<String, String> getFullNameToFileMap() {
      if (this.fontToFileMap == null) {
         HashMap var1 = new HashMap(100);
         this.fontToFamilyNameMap = new HashMap<>(100);
         this.familyToFontListMap = new HashMap<>(50);
         this.fileToFontMap = new HashMap<>(100);
         if (isWindows) {
            getPlatformFontDirs();
            populateFontFileNameMap(var1, this.fontToFamilyNameMap, this.familyToFontListMap, Locale.ENGLISH);
            if (debugFonts) {
               System.err.println("Windows Locale ID=" + getSystemLCID());
               this.logFontInfo(" *** WINDOWS FONTS BEFORE RESOLVING", var1, this.fontToFamilyNameMap, this.familyToFontListMap);
            }

            this.resolveWindowsFonts(var1, this.fontToFamilyNameMap, this.familyToFontListMap);
            if (debugFonts) {
               this.logFontInfo(" *** WINDOWS FONTS AFTER RESOLVING", var1, this.fontToFamilyNameMap, this.familyToFontListMap);
            }
         } else if (isMacOSX || isIOS) {
            MacFontFinder.populateFontFileNameMap(var1, this.fontToFamilyNameMap, this.familyToFontListMap, Locale.ENGLISH);
         } else if (isLinux) {
            FontConfigManager.populateMaps(var1, this.fontToFamilyNameMap, this.familyToFontListMap, Locale.getDefault());
            if (debugFonts) {
               this.logFontInfo(" *** FONTCONFIG LOCATED FONTS:", var1, this.fontToFamilyNameMap, this.familyToFontListMap);
            }
         } else {
            if (!isAndroid) {
               this.fontToFileMap = var1;
               return this.fontToFileMap;
            }

            AndroidFontFinder.populateFontFileNameMap(var1, this.fontToFamilyNameMap, this.familyToFontListMap, Locale.ENGLISH);
         }

         for (String var3 : var1.keySet()) {
            String var4 = (String)var1.get(var3);
            this.fileToFontMap.put(var4.toLowerCase(), var3);
         }

         this.fontToFileMap = var1;
         if (isAndroid) {
            this.populateFontFileNameMapGeneric(AndroidFontFinder.getSystemFontsDir());
         }

         this.populateFontFileNameMapGeneric(jreFontDir);
      }

      return this.fontToFileMap;
   }

   @Override
   public final boolean hasPermission() {
      try {
         SecurityManager var1 = System.getSecurityManager();
         if (var1 != null) {
            var1.checkPermission(FXPermissions.LOAD_FONT_PERMISSION);
         }

         return true;
      } catch (SecurityException var2) {
         return false;
      }
   }

   void addToMaps(PrismFontFile var1) {
      if (var1 != null) {
         String var2 = var1.getFullName();
         String var3 = var1.getFamilyName();
         if (var2 != null && var3 != null) {
            String var4 = var2.toLowerCase();
            String var5 = var3.toLowerCase();
            this.fontToFileMap.put(var4, var1.getFileName());
            this.fontToFamilyNameMap.put(var4, var3);
            ArrayList var6 = this.familyToFontListMap.get(var5);
            if (var6 == null) {
               var6 = new ArrayList();
               this.familyToFontListMap.put(var5, var6);
            }

            var6.add(var2);
         }
      }
   }

   void populateFontFileNameMapGeneric(String var1) {
      File var2 = new File(var1);
      String[] var3 = null;

      try {
         String[] var4 = AccessController.doPrivileged((PrivilegedExceptionAction<String[]>)(() -> var2.list(PrismFontFactory.TTFilter.getInstance())));
         var3 = var4;
      } catch (Exception var8) {
      }

      if (var3 != null) {
         for (int var10 = 0; var10 < var3.length; var10++) {
            try {
               String var5 = var1 + File.separator + var3[var10];
               if (this.registerEmbeddedFont(var5)) {
                  int var6 = 0;
                  PrismFontFile var7 = this.createFontResource(var5, var6++);
                  if (var7 != null) {
                     this.addToMaps(var7);

                     while (var6 < var7.getFontCount()) {
                        var7 = this.createFontResource(var5, var6++);
                        if (var7 == null) {
                           break;
                        }

                        this.addToMaps(var7);
                     }
                  }
               }
            } catch (Exception var9) {
            }
         }
      }
   }

   static native int getLCDContrastWin32();

   private static native float getSystemFontSizeNative();

   private static native String getSystemFontNative();

   public static float getSystemFontSize() {
      if (systemFontSize == -1.0F) {
         if (isWindows) {
            systemFontSize = getSystemFontSizeNative();
         } else if (isMacOSX || isIOS) {
            systemFontSize = MacFontFinder.getSystemFontSize();
         } else if (isAndroid) {
            systemFontSize = AndroidFontFinder.getSystemFontSize();
         } else if (isEmbedded) {
            try {
               int var0 = Screen.getMainScreen().getResolutionY();
               systemFontSize = (float)var0 / 6.0F;
            } catch (NullPointerException var1) {
               systemFontSize = 13.0F;
            }
         } else {
            systemFontSize = 13.0F;
         }
      }

      return systemFontSize;
   }

   public static String getSystemFont(String var0) {
      if (!var0.equals("System")) {
         if (var0.equals("SansSerif")) {
            return "Arial";
         } else if (var0.equals("Serif")) {
            return "Times New Roman";
         } else {
            if (monospaceFontFamily == null && isMacOSX) {
            }

            if (monospaceFontFamily == null) {
               monospaceFontFamily = "Courier New";
            }

            return monospaceFontFamily;
         }
      } else {
         if (systemFontFamily == null) {
            if (isWindows) {
               systemFontFamily = getSystemFontNative();
               if (systemFontFamily == null) {
                  systemFontFamily = "Arial";
               }
            } else if (isMacOSX || isIOS) {
               systemFontFamily = MacFontFinder.getSystemFont();
               if (systemFontFamily == null) {
                  systemFontFamily = "Lucida Grande";
               }
            } else if (isAndroid) {
               systemFontFamily = AndroidFontFinder.getSystemFont();
            } else {
               systemFontFamily = "Lucida Sans";
            }
         }

         return systemFontFamily;
      }
   }

   static native short getSystemLCID();

   static {
      int[] var0 = new int[]{65536};
      boolean var1 = AccessController.doPrivileged((PrivilegedAction<Boolean>)(() -> {
         NativeLibLoader.loadLibrary("javafx_font");
         String var1x = System.getProperty("prism.debugfonts", "");
         boolean var2 = "true".equals(var1x);
         jreFontDir = getJDKFontDir();
         String var3 = System.getProperty("com.sun.javafx.fontSize");
         systemFontSize = -1.0F;
         if (var3 != null) {
            try {
               systemFontSize = Float.parseFloat(var3);
            } catch (NumberFormatException var10) {
               System.err.println("Cannot parse font size '" + var3 + "'");
            }
         }

         var3 = System.getProperty("prism.subpixeltext", "on");
         if (var3.indexOf("on") != -1 || var3.indexOf("true") != -1) {
            subPixelMode = 1;
         }

         if (var3.indexOf("native") != -1) {
            subPixelMode |= 5;
         }

         if (var3.indexOf("vertical") != -1) {
            subPixelMode |= 7;
         }

         var3 = System.getProperty("prism.fontSizeLimit");
         if (var3 != null) {
            try {
               fontSizeLimit = Float.parseFloat(var3);
               if (fontSizeLimit <= 0.0F) {
                  fontSizeLimit = Float.POSITIVE_INFINITY;
               }
            } catch (NumberFormatException var9) {
               System.err.println("Cannot parse fontSizeLimit '" + var3 + "'");
            }
         }

         boolean var4 = isIOS || isAndroid || isEmbedded;
         String var5 = var4 ? "false" : "true";
         String var6 = System.getProperty("prism.lcdtext", var5);
         lcdEnabled = var6.equals("true");
         var3 = System.getProperty("prism.cacheLayoutSize");
         if (var3 != null) {
            try {
               var0[0] = Integer.parseInt(var3);
               if (var0[0] < 0) {
                  var0[0] = 0;
               }
            } catch (NumberFormatException var8) {
               System.err.println("Cannot parse cache layout size '" + var3 + "'");
            }
         }

         return var2;
      }));
      debugFonts = var1;
      cacheLayoutSize = var0[0];
   }

   private static class TTFilter implements FilenameFilter {
      static PrismFontFactory.TTFilter ttFilter;

      @Override
      public boolean accept(File var1, String var2) {
         int var3 = var2.length() - 4;
         return var3 <= 0
            ? false
            : var2.startsWith(".ttf", var3)
               || var2.startsWith(".TTF", var3)
               || var2.startsWith(".ttc", var3)
               || var2.startsWith(".TTC", var3)
               || var2.startsWith(".otf", var3)
               || var2.startsWith(".OTF", var3);
      }

      static PrismFontFactory.TTFilter getInstance() {
         if (ttFilter == null) {
            ttFilter = new PrismFontFactory.TTFilter();
         }

         return ttFilter;
      }
   }
}
