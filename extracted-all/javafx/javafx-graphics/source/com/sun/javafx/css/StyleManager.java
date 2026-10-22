package com.sun.javafx.css;

import com.sun.javafx.logging.PlatformLogger;
import com.sun.javafx.logging.PlatformLogger.Level;
import com.sun.javafx.scene.NodeHelper;
import com.sun.javafx.scene.ParentHelper;
import com.sun.javafx.util.DataURI;
import com.sun.javafx.util.Logging;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.FilePermission;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;
import java.security.AccessControlContext;
import java.security.AccessControlException;
import java.security.AccessController;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PermissionCollection;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.Map.Entry;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ListChangeListener.Change;
import javafx.css.CssParser;
import javafx.css.FontFace;
import javafx.css.PseudoClass;
import javafx.css.Rule;
import javafx.css.Selector;
import javafx.css.StyleConverter;
import javafx.css.StyleOrigin;
import javafx.css.Styleable;
import javafx.css.Stylesheet;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SubScene;
import javafx.scene.image.Image;
import javafx.scene.layout.Region;
import javafx.scene.text.Font;
import javafx.stage.Window;

public final class StyleManager {
   private static final Object styleLock = new Object();
   private static PlatformLogger LOGGER;
   public static final Map<Parent, StyleManager.CacheContainer> cacheContainerMap = new WeakHashMap<>();
   public final List<StyleManager.StylesheetContainer> userAgentStylesheetContainers = new ArrayList<>();
   public final List<StyleManager.StylesheetContainer> platformUserAgentStylesheetContainers = new ArrayList<>();
   public boolean hasDefaultUserAgentStylesheet = false;
   public final Map<String, StyleManager.StylesheetContainer> stylesheetContainerMap = new HashMap<>();
   private final StyleManager.ImageCache imageCache = new StyleManager.ImageCache();
   private static final String skinPrefix = "com/sun/javafx/scene/control/skin/";
   private static final String skinUtilsClassName = "com.sun.javafx.scene.control.skin.Utils";
   private StyleManager.Key key = null;
   private final WeakHashMap<Region, String> weakRegionUserAgentStylesheetMap = new WeakHashMap<>();
   private static ObservableList<CssParser.ParseError> errors = null;
   private static List<String> cacheMapKey;

   private static PlatformLogger getLogger() {
      if (LOGGER == null) {
         LOGGER = Logging.getCSSLogger();
      }

      return LOGGER;
   }

   public static StyleManager getInstance() {
      return StyleManager.InstanceHolder.INSTANCE;
   }

   private StyleManager() {
   }

   StyleManager.CacheContainer getCacheContainer(Styleable var1, SubScene var2) {
      if (var1 == null && var2 == null) {
         return null;
      } else {
         Parent var3 = null;
         if (var2 != null) {
            var3 = var2.getRoot();
         } else if (var1 instanceof Node) {
            Node var4 = (Node)var1;
            Scene var5 = var4.getScene();
            if (var5 != null) {
               var3 = var5.getRoot();
            }
         } else if (var1 instanceof Window) {
            Scene var8 = ((Window)var1).getScene();
            if (var8 != null) {
               var3 = var8.getRoot();
            }
         }

         if (var3 == null) {
            return null;
         } else {
            synchronized (styleLock) {
               StyleManager.CacheContainer var10 = cacheContainerMap.get(var3);
               if (var10 == null) {
                  var10 = new StyleManager.CacheContainer();
                  cacheContainerMap.put(var3, var10);
               }

               return var10;
            }
         }
      }
   }

   public StyleCache getSharedCache(Styleable var1, SubScene var2, StyleCache.Key var3) {
      StyleManager.CacheContainer var4 = this.getCacheContainer(var1, var2);
      if (var4 == null) {
         return null;
      } else {
         Map var5 = var4.getStyleCache();
         if (var5 == null) {
            return null;
         } else {
            StyleCache var6 = (StyleCache)var5.get(var3);
            if (var6 == null) {
               var6 = new StyleCache();
               var5.put(new StyleCache.Key(var3), var6);
            }

            return var6;
         }
      }
   }

   public StyleMap getStyleMap(Styleable var1, SubScene var2, int var3) {
      if (var3 == -1) {
         return StyleMap.EMPTY_MAP;
      } else {
         StyleManager.CacheContainer var4 = this.getCacheContainer(var1, var2);
         return var4 == null ? StyleMap.EMPTY_MAP : var4.getStyleMap(var3);
      }
   }

   public void forget(Scene var1) {
      if (var1 != null) {
         this.forget(var1.getRoot());
         synchronized (styleLock) {
            Object var3 = null;
            if (var1.getUserAgentStylesheet() != null && !(var3 = var1.getUserAgentStylesheet().trim()).isEmpty()) {
               for (int var4 = this.userAgentStylesheetContainers.size() - 1; 0 <= var4; var4--) {
                  StyleManager.StylesheetContainer var5 = this.userAgentStylesheetContainers.get(var4);
                  if (var3.equals(var5.fname)) {
                     var5.parentUsers.remove(var1.getRoot());
                     if (var5.parentUsers.list.size() == 0) {
                        this.userAgentStylesheetContainers.remove(var4);
                     }
                  }
               }
            }

            Set var14 = this.stylesheetContainerMap.entrySet();
            Iterator var15 = var14.iterator();

            while (var15.hasNext()) {
               Entry var6 = (Entry)var15.next();
               StyleManager.StylesheetContainer var7 = (StyleManager.StylesheetContainer)var6.getValue();
               Iterator var8 = var7.parentUsers.list.iterator();

               while (var8.hasNext()) {
                  Reference var9 = (Reference)var8.next();
                  Parent var10 = (Parent)var9.get();
                  if (var10 == null || var10.getScene() == var1 || var10.getScene() == null) {
                     var9.clear();
                     var8.remove();
                  }
               }

               if (var7.parentUsers.list.isEmpty()) {
                  var15.remove();
               }
            }
         }
      }
   }

   public void stylesheetsChanged(Scene var1, Change<String> var2) {
      synchronized (styleLock) {
         for (Entry var6 : cacheContainerMap.entrySet()) {
            Parent var7 = (Parent)var6.getKey();
            StyleManager.CacheContainer var8 = (StyleManager.CacheContainer)var6.getValue();
            if (var7.getScene() == var1) {
               var8.clearCache();
            }
         }

         var2.reset();

         while (var2.next()) {
            if (var2.wasRemoved()) {
               for (String var12 : var2.getRemoved()) {
                  this.stylesheetRemoved(var1, var12);
                  StyleManager.StylesheetContainer var13 = this.stylesheetContainerMap.get(var12);
                  if (var13 != null) {
                     var13.invalidateChecksum();
                  }
               }
            }
         }
      }
   }

   private void stylesheetRemoved(Scene var1, String var2) {
      this.stylesheetRemoved(var1.getRoot(), var2);
   }

   public void forget(Parent var1) {
      if (var1 != null) {
         synchronized (styleLock) {
            StyleManager.CacheContainer var3 = cacheContainerMap.remove(var1);
            if (var3 != null) {
               var3.clearCache();
            }

            ObservableList var4 = var1.getStylesheets();
            if (var4 != null && !var4.isEmpty()) {
               for (String var6 : var4) {
                  this.stylesheetRemoved(var1, var6);
               }
            }

            Iterator var11 = this.stylesheetContainerMap.entrySet().iterator();

            while (var11.hasNext()) {
               Entry var12 = (Entry)var11.next();
               StyleManager.StylesheetContainer var7 = (StyleManager.StylesheetContainer)var12.getValue();
               var7.parentUsers.remove(var1);
               if (var7.parentUsers.list.isEmpty()) {
                  var11.remove();
                  if (var7.selectorPartitioning != null) {
                     var7.selectorPartitioning.reset();
                  }

                  String var8 = var7.fname;
                  this.imageCache.cleanUpImageCache(var8);
               }
            }
         }
      }
   }

   public void stylesheetsChanged(Parent var1, Change<String> var2) {
      synchronized (styleLock) {
         var2.reset();

         while (var2.next()) {
            if (var2.wasRemoved()) {
               for (String var5 : var2.getRemoved()) {
                  this.stylesheetRemoved(var1, var5);
                  StyleManager.StylesheetContainer var6 = this.stylesheetContainerMap.get(var5);
                  if (var6 != null) {
                     var6.invalidateChecksum();
                  }
               }
            }
         }
      }
   }

   private void stylesheetRemoved(Parent var1, String var2) {
      synchronized (styleLock) {
         StyleManager.StylesheetContainer var4 = this.stylesheetContainerMap.get(var2);
         if (var4 != null) {
            var4.parentUsers.remove(var1);
            if (var4.parentUsers.list.isEmpty()) {
               this.removeStylesheetContainer(var4);
            }
         }
      }
   }

   public void forget(SubScene var1) {
      if (var1 != null) {
         Parent var2 = var1.getRoot();
         if (var2 != null) {
            this.forget(var2);
            synchronized (styleLock) {
               Object var4 = null;
               if (var1.getUserAgentStylesheet() != null && !(var4 = var1.getUserAgentStylesheet().trim()).isEmpty()) {
                  Iterator var5 = this.userAgentStylesheetContainers.iterator();

                  while (var5.hasNext()) {
                     StyleManager.StylesheetContainer var6 = (StyleManager.StylesheetContainer)var5.next();
                     if (var4.equals(var6.fname)) {
                        var6.parentUsers.remove(var1.getRoot());
                        if (var6.parentUsers.list.size() == 0) {
                           var5.remove();
                        }
                     }
                  }
               }

               for (StyleManager.StylesheetContainer var7 : new ArrayList<>(this.stylesheetContainerMap.values())) {
                  Iterator var8 = var7.parentUsers.list.iterator();

                  while (var8.hasNext()) {
                     Reference var9 = (Reference)var8.next();
                     Parent var10 = (Parent)var9.get();
                     if (var10 != null) {
                        for (Parent var11 = var10; var11 != null; var11 = var11.getParent()) {
                           if (var2 == var11.getParent()) {
                              var9.clear();
                              var8.remove();
                              this.forget(var10);
                              break;
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private void removeStylesheetContainer(StyleManager.StylesheetContainer var1) {
      if (var1 != null) {
         synchronized (styleLock) {
            String var3 = var1.fname;
            this.stylesheetContainerMap.remove(var3);
            if (var1.selectorPartitioning != null) {
               var1.selectorPartitioning.reset();
            }

            for (Entry var5 : cacheContainerMap.entrySet()) {
               StyleManager.CacheContainer var6 = (StyleManager.CacheContainer)var5.getValue();
               if (var6 != null && var6.cacheMap != null && !var6.cacheMap.isEmpty()) {
                  ArrayList var7 = new ArrayList();

                  for (Entry var9 : var6.cacheMap.entrySet()) {
                     List var10 = (List)var9.getKey();
                     if (var10 != null ? var10.contains(var3) : var3 == null) {
                        var7.add(var10);
                     }
                  }

                  if (!var7.isEmpty()) {
                     for (List var18 : var7) {
                        Map var19 = var6.cacheMap.remove(var18);
                        if (var19 != null) {
                           var19.clear();
                        }
                     }
                  }
               }
            }

            this.imageCache.cleanUpImageCache(var3);
            List var13 = var1.parentUsers.list;

            for (int var14 = var13.size() - 1; 0 <= var14; var14--) {
               Reference var15 = (Reference)var13.remove(var14);
               Parent var16 = (Parent)var15.get();
               var15.clear();
               if (var16 != null && var16.getScene() != null) {
                  NodeHelper.reapplyCSS(var16);
               }
            }
         }
      }
   }

   public Image getCachedImage(String var1) {
      return this.imageCache.getCachedImage(var1);
   }

   private static URL getURL(String var0) {
      if (var0 != null && !var0.trim().isEmpty()) {
         try {
            URI var1 = new URI(var0.trim());
            if (var1.isAbsolute()) {
               return var1.toURL();
            } else if (!var0.startsWith("com/sun/javafx/scene/control/skin/") || !var0.endsWith(".css") && !var0.endsWith(".bss")) {
               ClassLoader var8 = Thread.currentThread().getContextClassLoader();
               String var9 = var1.getPath();
               URL var10 = null;
               if (var9.startsWith("/")) {
                  var10 = var8.getResource(var9.substring(1));
               } else {
                  var10 = var8.getResource(var9);
               }

               return var10;
            } else {
               try {
                  ClassLoader var2 = StyleManager.class.getClassLoader();
                  Class var3 = Class.forName("com.sun.javafx.scene.control.skin.Utils", true, var2);
                  Method var4 = var3.getMethod("getResource", String.class);
                  return (URL)var4.invoke(null, var0.substring("com/sun/javafx/scene/control/skin/".length()));
               } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | ClassNotFoundException var5) {
                  var5.printStackTrace();
                  return null;
               }
            }
         } catch (MalformedURLException var6) {
            return null;
         } catch (URISyntaxException var7) {
            return null;
         }
      } else {
         return null;
      }
   }

   static byte[] calculateCheckSum(String var0) {
      if (var0 != null && !var0.isEmpty()) {
         try {
            URL var1 = getURL(var0);
            if (var1 != null && "file".equals(var1.getProtocol())) {
               byte[] var5;
               try (
                  InputStream var2 = var1.openStream();
                  DigestInputStream var3 = new DigestInputStream(var2, MessageDigest.getInstance("MD5"));
               ) {
                  var3.getMessageDigest().reset();
                  byte[] var4 = new byte[4096];

                  while (var3.read(var4) != -1) {
                  }

                  var5 = var3.getMessageDigest().digest();
               }

               return var5;
            }
         } catch (NoSuchAlgorithmException | IOException | SecurityException | IllegalArgumentException var10) {
         }

         return new byte[0];
      } else {
         return new byte[0];
      }
   }

   public static Stylesheet loadStylesheet(String var0) {
      try {
         return loadStylesheetUnPrivileged(var0);
      } catch (AccessControlException var16) {
         System.err.println("WARNING: security exception trying to load: " + var0);
         if (var0.length() < 7 && var0.indexOf("!/") < var0.length() - 7) {
            return null;
         } else {
            try {
               URI var2 = new URI(var0);
               if ("jar".equals(var2.getScheme())) {
                  URI var3 = AccessController.doPrivileged(
                     (PrivilegedExceptionAction<URI>)(() -> StyleManager.class.getProtectionDomain().getCodeSource().getLocation().toURI())
                  );
                  String var4 = var3.getSchemeSpecificPart();
                  String var5 = var2.getSchemeSpecificPart();
                  String var6 = var5.substring(var5.indexOf(47), var5.indexOf("!/"));
                  if (var4.equals(var6)) {
                     String var7 = var0.substring(var0.indexOf("!/") + 2);
                     if (var0.endsWith(".css") || var0.endsWith(".bss")) {
                        FilePermission var8 = new FilePermission(var4, "read");
                        PermissionCollection var9 = var8.newPermissionCollection();
                        var9.add(var8);
                        AccessControlContext var10 = new AccessControlContext(new ProtectionDomain[]{new ProtectionDomain(null, var9)});
                        JarFile var11 = null;

                        try {
                           var11 = AccessController.doPrivileged((PrivilegedExceptionAction<JarFile>)(() -> new JarFile(var4)), var10);
                        } catch (PrivilegedActionException var13) {
                           return null;
                        }

                        if (var11 != null) {
                           JarEntry var12 = var11.getJarEntry(var7);
                           if (var12 != null) {
                              return AccessController.doPrivileged((PrivilegedAction<Stylesheet>)(() -> loadStylesheetUnPrivileged(var0)), var10);
                           }
                        }
                     }
                  }
               }

               return null;
            } catch (URISyntaxException var14) {
               return null;
            } catch (PrivilegedActionException var15) {
               return null;
            }
         }
      }
   }

   private static Stylesheet loadStylesheetUnPrivileged(String var0) {
      synchronized (styleLock) {
         Boolean var2 = AccessController.doPrivileged((PrivilegedAction<Boolean>)(() -> {
            String var1 = System.getProperty("binary.css");
            return !var0.endsWith(".bss") && var1 != null ? !Boolean.valueOf(var1) : Boolean.FALSE;
         }));

         try {
            String var3 = var2 ? ".css" : ".bss";
            Object var22 = null;
            Stylesheet var24 = null;
            if (!var0.endsWith(".css") && !var0.endsWith(".bss")) {
               var22 = getURL(var0);
            } else {
               String var6 = var0.substring(0, var0.length() - 4);
               var22 = getURL(var6 + var3);
               if (var22 == null && var2 = !var2) {
                  var22 = getURL(var6 + ".css");
               }

               if (var22 != null && !var2) {
                  try {
                     var24 = Stylesheet.loadBinary((URL)var22);
                  } catch (IOException var16) {
                  }

                  if (var24 == null) {
                     var22 = getURL(var0);
                  }
               }
            }

            if (var24 == null) {
               DataURI var25 = null;
               if (var22 != null) {
                  var24 = new CssParser().parse((URL)var22);
               } else {
                  var25 = DataURI.tryParse(var0);
               }

               if (var25 != null) {
                  boolean var7 = "text".equalsIgnoreCase(var25.getMimeType())
                     && ("css".equalsIgnoreCase(var25.getMimeSubtype()) || "plain".equalsIgnoreCase(var25.getMimeSubtype()));
                  boolean var8 = "application".equalsIgnoreCase(var25.getMimeType()) && "octet-stream".equalsIgnoreCase(var25.getMimeSubtype());
                  if (var7) {
                     String var31 = var25.getParameters().get("charset");

                     Charset var10;
                     try {
                        var10 = var31 != null ? Charset.forName(var31) : Charset.defaultCharset();
                     } catch (UnsupportedCharsetException | IllegalCharsetNameException var17) {
                        String var12 = String.format("Unsupported charset \"%s\" in stylesheet URI \"%s\"", var31, var25);
                        if (errors != null) {
                           errors.add(new CssParser.ParseError(var12));
                        }

                        if (getLogger().isLoggable(Level.WARNING)) {
                           getLogger().warning(var12);
                        }

                        return null;
                     }

                     String var11 = new String(var25.getData(), var10);
                     var24 = new CssParser().parse(var11);
                  } else {
                     if (!var8) {
                        String var30 = String.format(
                           "Unexpected MIME type \"%s/%s\" in stylesheet URI \"%s\"", var25.getMimeType(), var25.getMimeSubtype(), var25
                        );
                        if (errors != null) {
                           errors.add(new CssParser.ParseError(var30));
                        }

                        if (getLogger().isLoggable(Level.WARNING)) {
                           getLogger().warning(var30);
                        }

                        return null;
                     }

                     try (ByteArrayInputStream var9 = new ByteArrayInputStream(var25.getData())) {
                        var24 = Stylesheet.loadBinary(var9);
                     }
                  }
               }
            }

            if (var24 == null) {
               if (errors != null) {
                  CssParser.ParseError var26 = new CssParser.ParseError("Resource \"" + var0 + "\" not found.");
                  errors.add(var26);
               }

               if (getLogger().isLoggable(Level.WARNING)) {
                  getLogger().warning(String.format("Resource \"%s\" not found.", var0));
               }
            }

            if (var24 != null) {
               for (FontFace var28 : var24.getFontFaces()) {
                  if (var28 instanceof FontFaceImpl) {
                     for (FontFaceImpl.FontFaceSrc var32 : ((FontFaceImpl)var28).getSources()) {
                        if (var32.getType() == FontFaceImpl.FontFaceSrcType.URL) {
                           Font var33 = Font.loadFont(var32.getSrc(), 10.0);
                           if (var33 == null) {
                              getLogger().info("Could not load @font-face font [" + var32.getSrc() + "]");
                           }
                           break;
                        }
                     }
                  }
               }
            }

            return var24;
         } catch (FileNotFoundException var18) {
            if (errors != null) {
               CssParser.ParseError var21 = new CssParser.ParseError("Stylesheet \"" + var0 + "\" not found.");
               errors.add(var21);
            }

            if (getLogger().isLoggable(Level.INFO)) {
               getLogger().info("Could not find stylesheet: " + var0);
            }
         } catch (IOException var19) {
            DataURI var4 = DataURI.tryParse(var0);
            String var5 = var4 != null ? var4.toString() : var0;
            if (errors != null) {
               errors.add(new CssParser.ParseError("Could not load stylesheet: " + var5));
            }

            if (getLogger().isLoggable(Level.INFO)) {
               getLogger().info("Could not load stylesheet: " + var5);
            }
         }

         return null;
      }
   }

   public void setUserAgentStylesheets(List<String> var1) {
      if (var1 != null && var1.size() != 0) {
         synchronized (styleLock) {
            if (var1.size() == this.platformUserAgentStylesheetContainers.size()) {
               boolean var3 = true;
               int var4 = 0;

               for (int var5 = var1.size(); var4 < var5 && var3; var4++) {
                  String var6 = (String)var1.get(var4);
                  String var7 = var6 != null ? var6.trim() : null;
                  if (var7 == null || var7.isEmpty()) {
                     break;
                  }

                  StyleManager.StylesheetContainer var8 = this.platformUserAgentStylesheetContainers.get(var4);
                  if (var3 = var7.equals(var8.fname)) {
                     String var9 = var8.stylesheet.getUrl();
                     byte[] var10 = calculateCheckSum(var9);
                     var3 = Arrays.equals(var10, var8.checksum);
                  }
               }

               if (var3) {
                  return;
               }
            }

            boolean var13 = false;
            int var14 = 0;

            for (int var15 = var1.size(); var14 < var15; var14++) {
               String var16 = (String)var1.get(var14);
               String var17 = var16 != null ? var16.trim() : null;
               if (var17 != null && !var17.isEmpty()) {
                  if (!var13) {
                     this.platformUserAgentStylesheetContainers.clear();
                     var13 = true;
                  }

                  if (var14 == 0) {
                     this._setDefaultUserAgentStylesheet(var17);
                  } else {
                     this._addUserAgentStylesheet(var17);
                  }
               }
            }

            if (var13) {
               this.userAgentStylesheetsChanged();
            }
         }
      }
   }

   public void addUserAgentStylesheet(String var1) {
      this.addUserAgentStylesheet(null, var1);
   }

   public void addUserAgentStylesheet(Scene var1, String var2) {
      String var3 = var2 != null ? var2.trim() : null;
      if (var3 != null && !var3.isEmpty()) {
         synchronized (styleLock) {
            if (this._addUserAgentStylesheet(var3)) {
               this.userAgentStylesheetsChanged();
            }
         }
      }
   }

   private boolean _addUserAgentStylesheet(String var1) {
      synchronized (styleLock) {
         int var3 = 0;

         for (int var4 = this.platformUserAgentStylesheetContainers.size(); var3 < var4; var3++) {
            StyleManager.StylesheetContainer var5 = this.platformUserAgentStylesheetContainers.get(var3);
            if (var1.equals(var5.fname)) {
               return false;
            }
         }

         Stylesheet var8 = loadStylesheet(var1);
         if (var8 == null) {
            return false;
         } else {
            var8.setOrigin(StyleOrigin.USER_AGENT);
            this.platformUserAgentStylesheetContainers.add(new StyleManager.StylesheetContainer(var1, var8));
            return true;
         }
      }
   }

   public void addUserAgentStylesheet(Scene var1, Stylesheet var2) {
      if (var2 == null) {
         throw new IllegalArgumentException("null arg ua_stylesheet");
      } else {
         String var3 = var2.getUrl();
         String var4 = var3 != null ? var3.trim() : "";
         synchronized (styleLock) {
            int var6 = 0;

            for (int var7 = this.platformUserAgentStylesheetContainers.size(); var6 < var7; var6++) {
               StyleManager.StylesheetContainer var8 = this.platformUserAgentStylesheetContainers.get(var6);
               if (var4.equals(var8.fname)) {
                  return;
               }
            }

            this.platformUserAgentStylesheetContainers.add(new StyleManager.StylesheetContainer(var4, var2));
            if (var2 != null) {
               var2.setOrigin(StyleOrigin.USER_AGENT);
            }

            this.userAgentStylesheetsChanged();
         }
      }
   }

   public void setDefaultUserAgentStylesheet(String var1) {
      this.setDefaultUserAgentStylesheet(null, var1);
   }

   public void setDefaultUserAgentStylesheet(Scene var1, String var2) {
      String var3 = var2 != null ? var2.trim() : null;
      if (var3 != null && !var3.isEmpty()) {
         synchronized (styleLock) {
            if (this._setDefaultUserAgentStylesheet(var3)) {
               this.userAgentStylesheetsChanged();
            }
         }
      }
   }

   private boolean _setDefaultUserAgentStylesheet(String var1) {
      synchronized (styleLock) {
         int var3 = 0;

         for (int var4 = this.platformUserAgentStylesheetContainers.size(); var3 < var4; var3++) {
            StyleManager.StylesheetContainer var5 = this.platformUserAgentStylesheetContainers.get(var3);
            if (var1.equals(var5.fname)) {
               if (var3 > 0) {
                  this.platformUserAgentStylesheetContainers.remove(var3);
                  if (this.hasDefaultUserAgentStylesheet) {
                     this.platformUserAgentStylesheetContainers.set(0, var5);
                  } else {
                     this.platformUserAgentStylesheetContainers.add(0, var5);
                  }
               }

               return var3 > 0;
            }
         }

         Stylesheet var8 = loadStylesheet(var1);
         if (var8 == null) {
            return false;
         } else {
            var8.setOrigin(StyleOrigin.USER_AGENT);
            StyleManager.StylesheetContainer var9 = new StyleManager.StylesheetContainer(var1, var8);
            if (this.platformUserAgentStylesheetContainers.size() == 0) {
               this.platformUserAgentStylesheetContainers.add(var9);
            } else if (this.hasDefaultUserAgentStylesheet) {
               this.platformUserAgentStylesheetContainers.set(0, var9);
            } else {
               this.platformUserAgentStylesheetContainers.add(0, var9);
            }

            this.hasDefaultUserAgentStylesheet = true;
            return true;
         }
      }
   }

   public void removeUserAgentStylesheet(String var1) {
      String var2 = var1 != null ? var1.trim() : null;
      if (var2 != null && !var2.isEmpty()) {
         synchronized (styleLock) {
            boolean var4 = false;

            for (int var5 = this.platformUserAgentStylesheetContainers.size() - 1; var5 >= 0; var5--) {
               if (!var2.equals(Application.getUserAgentStylesheet())) {
                  StyleManager.StylesheetContainer var6 = this.platformUserAgentStylesheetContainers.get(var5);
                  if (var2.equals(var6.fname)) {
                     this.platformUserAgentStylesheetContainers.remove(var5);
                     var4 = true;
                  }
               }
            }

            if (var4) {
               this.userAgentStylesheetsChanged();
            }
         }
      }
   }

   public void setDefaultUserAgentStylesheet(Stylesheet var1) {
      if (var1 != null) {
         String var2 = var1.getUrl();
         String var3 = var2 != null ? var2.trim() : "";
         synchronized (styleLock) {
            int var5 = 0;

            for (int var6 = this.platformUserAgentStylesheetContainers.size(); var5 < var6; var5++) {
               StyleManager.StylesheetContainer var7 = this.platformUserAgentStylesheetContainers.get(var5);
               if (var3.equals(var7.fname)) {
                  if (var5 > 0) {
                     this.platformUserAgentStylesheetContainers.remove(var5);
                     if (this.hasDefaultUserAgentStylesheet) {
                        this.platformUserAgentStylesheetContainers.set(0, var7);
                     } else {
                        this.platformUserAgentStylesheetContainers.add(0, var7);
                     }
                  }

                  return;
               }
            }

            StyleManager.StylesheetContainer var10 = new StyleManager.StylesheetContainer(var3, var1);
            if (this.platformUserAgentStylesheetContainers.size() == 0) {
               this.platformUserAgentStylesheetContainers.add(var10);
            } else if (this.hasDefaultUserAgentStylesheet) {
               this.platformUserAgentStylesheetContainers.set(0, var10);
            } else {
               this.platformUserAgentStylesheetContainers.add(0, var10);
            }

            this.hasDefaultUserAgentStylesheet = true;
            var1.setOrigin(StyleOrigin.USER_AGENT);
            this.userAgentStylesheetsChanged();
         }
      }
   }

   private void userAgentStylesheetsChanged() {
      ArrayList var1 = new ArrayList();
      synchronized (styleLock) {
         for (StyleManager.CacheContainer var4 : cacheContainerMap.values()) {
            var4.clearCache();
         }

         StyleConverter.clearCache();

         for (Parent var10 : cacheContainerMap.keySet()) {
            if (var10 != null) {
               var1.add(var10);
            }
         }
      }

      for (Parent var9 : var1) {
         NodeHelper.reapplyCSS(var9);
      }
   }

   private List<StyleManager.StylesheetContainer> processStylesheets(List<String> var1, Parent var2) {
      synchronized (styleLock) {
         ArrayList var4 = new ArrayList();
         int var5 = 0;

         for (int var6 = var1.size(); var5 < var6; var5++) {
            String var7 = (String)var1.get(var5);
            StyleManager.StylesheetContainer var8 = null;
            if (this.stylesheetContainerMap.containsKey(var7)) {
               var8 = this.stylesheetContainerMap.get(var7);
               if (!var4.contains(var8)) {
                  if (var8.checksumInvalid) {
                     byte[] var9 = calculateCheckSum(var7);
                     if (!Arrays.equals(var9, var8.checksum)) {
                        this.removeStylesheetContainer(var8);
                        Stylesheet var10 = loadStylesheet(var7);
                        var8 = new StyleManager.StylesheetContainer(var7, var10, var9);
                        this.stylesheetContainerMap.put(var7, var8);
                     } else {
                        var8.checksumInvalid = false;
                     }
                  }

                  var4.add(var8);
               }

               var8.parentUsers.add(var2);
            } else {
               Stylesheet var15 = loadStylesheet(var7);
               var8 = new StyleManager.StylesheetContainer(var7, var15);
               var8.parentUsers.add(var2);
               this.stylesheetContainerMap.put(var7, var8);
               var4.add(var8);
            }
         }

         return var4;
      }
   }

   private List<StyleManager.StylesheetContainer> gatherParentStylesheets(Parent var1) {
      if (var1 == null) {
         return Collections.emptyList();
      } else {
         List var2 = ParentHelper.getAllParentStylesheets(var1);
         if (var2 != null && !var2.isEmpty()) {
            synchronized (styleLock) {
               return this.processStylesheets(var2, var1);
            }
         } else {
            return Collections.emptyList();
         }
      }
   }

   private List<StyleManager.StylesheetContainer> gatherSceneStylesheets(Scene var1) {
      if (var1 == null) {
         return Collections.emptyList();
      } else {
         ObservableList var2 = var1.getStylesheets();
         if (var2 != null && !var2.isEmpty()) {
            synchronized (styleLock) {
               return this.processStylesheets(var2, var1.getRoot());
            }
         } else {
            return Collections.emptyList();
         }
      }
   }

   public StyleMap findMatchingStyles(Node var1, SubScene var2, Set<PseudoClass>[] var3) {
      Scene var4 = var1.getScene();
      if (var4 == null) {
         return StyleMap.EMPTY_MAP;
      } else {
         StyleManager.CacheContainer var5 = this.getCacheContainer(var1, var2);
         if (var5 == null) {
            assert false : var1.toString();

            return StyleMap.EMPTY_MAP;
         } else {
            synchronized (styleLock) {
               Parent var7 = var1 instanceof Parent ? (Parent)var1 : var1.getParent();
               List var8 = this.gatherParentStylesheets(var7);
               boolean var9 = !var8.isEmpty();
               List var10 = this.gatherSceneStylesheets(var4);
               boolean var11 = !var10.isEmpty();
               String var12 = var1.getStyle();
               boolean var13 = var12 != null && !var12.trim().isEmpty();
               String var14 = var4.getUserAgentStylesheet();
               boolean var15 = var14 != null && !var14.trim().isEmpty();
               String var16 = var2 != null ? var2.getUserAgentStylesheet() : null;
               boolean var17 = var16 != null && !var16.trim().isEmpty();
               String var18 = null;

               Object var19;
               for (var19 = var1; var19 != null; var19 = ((Node)var19).getParent()) {
                  if (var19 instanceof Region) {
                     var18 = this.weakRegionUserAgentStylesheetMap.computeIfAbsent((Region)var19, Region::getUserAgentStylesheet);
                     if (var18 != null) {
                        break;
                     }
                  }
               }

               boolean var20 = var18 != null && !var18.trim().isEmpty();
               if (!var13 && !var9 && !var11 && !var15 && !var17 && !var20 && this.platformUserAgentStylesheetContainers.isEmpty()) {
                  return StyleMap.EMPTY_MAP;
               } else {
                  String var21 = var1.getTypeSelector();
                  String var22 = var1.getId();
                  ObservableList var23 = var1.getStyleClass();
                  if (this.key == null) {
                     this.key = new StyleManager.Key();
                  }

                  this.key.className = var21;
                  this.key.id = var22;
                  int var24 = 0;

                  for (int var25 = var23.size(); var24 < var25; var24++) {
                     String var26 = (String)var23.get(var24);
                     if (var26 != null && !var26.isEmpty()) {
                        this.key.styleClasses.add(StyleClassSet.getStyleClass(var26));
                     }
                  }

                  Map var33 = var5.getCacheMap(var8, var18);
                  StyleManager.Cache var34 = (StyleManager.Cache)var33.get(this.key);
                  if (var34 != null) {
                     this.key.styleClasses.clear();
                  } else {
                     ArrayList var35 = new ArrayList();
                     if (!var17 && !var15) {
                        if (!this.platformUserAgentStylesheetContainers.isEmpty()) {
                           int var36 = 0;

                           for (int var40 = this.platformUserAgentStylesheetContainers.size(); var36 < var40; var36++) {
                              StyleManager.StylesheetContainer var48 = this.platformUserAgentStylesheetContainers.get(var36);
                              if (var48 != null && var48.selectorPartitioning != null) {
                                 List var53 = var48.selectorPartitioning.match(var22, var21, this.key.styleClasses);
                                 var35.addAll(var53);
                              }
                           }
                        }
                     } else {
                        String var27 = var17 ? var2.getUserAgentStylesheet().trim() : var4.getUserAgentStylesheet().trim();
                        StyleManager.StylesheetContainer var28 = null;
                        int var29 = 0;

                        for (int var30 = this.userAgentStylesheetContainers.size(); var29 < var30; var29++) {
                           var28 = this.userAgentStylesheetContainers.get(var29);
                           if (var27.equals(var28.fname)) {
                              break;
                           }

                           var28 = null;
                        }

                        if (var28 == null) {
                           Stylesheet var46 = loadStylesheet(var27);
                           if (var46 != null) {
                              var46.setOrigin(StyleOrigin.USER_AGENT);
                           }

                           var28 = new StyleManager.StylesheetContainer(var27, var46);
                           this.userAgentStylesheetContainers.add(var28);
                        }

                        if (var28.selectorPartitioning != null) {
                           Parent var47 = var17 ? var2.getRoot() : var4.getRoot();
                           var28.parentUsers.add(var47);
                           List var52 = var28.selectorPartitioning.match(var22, var21, this.key.styleClasses);
                           var35.addAll(var52);
                        }
                     }

                     if (var20) {
                        StyleManager.StylesheetContainer var37 = null;
                        int var41 = 0;

                        for (int var49 = this.userAgentStylesheetContainers.size(); var41 < var49; var41++) {
                           var37 = this.userAgentStylesheetContainers.get(var41);
                           if (var18.equals(var37.fname)) {
                              break;
                           }

                           var37 = null;
                        }

                        if (var37 == null) {
                           Stylesheet var42 = loadStylesheet(var18);
                           if (var42 != null) {
                              var42.setOrigin(StyleOrigin.USER_AGENT);
                           }

                           var37 = new StyleManager.StylesheetContainer(var18, var42);
                           this.userAgentStylesheetContainers.add(var37);
                        }

                        if (var37.selectorPartitioning != null) {
                           var37.parentUsers.add((Parent)var19);
                           List var43 = var37.selectorPartitioning.match(var22, var21, this.key.styleClasses);
                           var35.addAll(var43);
                        }
                     }

                     if (!var10.isEmpty()) {
                        int var38 = 0;

                        for (int var44 = var10.size(); var38 < var44; var38++) {
                           StyleManager.StylesheetContainer var50 = (StyleManager.StylesheetContainer)var10.get(var38);
                           if (var50 != null && var50.selectorPartitioning != null) {
                              List var54 = var50.selectorPartitioning.match(var22, var21, this.key.styleClasses);
                              var35.addAll(var54);
                           }
                        }
                     }

                     if (var9) {
                        int var39 = var8 == null ? 0 : var8.size();

                        for (int var45 = 0; var45 < var39; var45++) {
                           StyleManager.StylesheetContainer var51 = (StyleManager.StylesheetContainer)var8.get(var45);
                           if (var51.selectorPartitioning != null) {
                              List var55 = var51.selectorPartitioning.match(var22, var21, this.key.styleClasses);
                              var35.addAll(var55);
                           }
                        }
                     }

                     var34 = new StyleManager.Cache(var35);
                     var33.put(this.key, var34);
                     this.key = null;
                  }

                  return var34.getStyleMap(var5, var1, var3, var13);
               }
            }
         }
      }
   }

   public static ObservableList<CssParser.ParseError> errorsProperty() {
      if (errors == null) {
         errors = FXCollections.observableArrayList();
      }

      return errors;
   }

   public static ObservableList<CssParser.ParseError> getErrors() {
      return errors;
   }

   private static class Cache {
      private final List<Selector> selectors;
      private final Map<StyleManager.Cache.Key, Integer> cache;

      Cache(List<Selector> var1) {
         this.selectors = var1;
         this.cache = new HashMap<>();
      }

      private StyleMap getStyleMap(StyleManager.CacheContainer var1, Node var2, Set<PseudoClass>[] var3, boolean var4) {
         if ((this.selectors == null || this.selectors.isEmpty()) && !var4) {
            return StyleMap.EMPTY_MAP;
         } else {
            int var5 = this.selectors.size();
            long[] var6 = new long[var5 / 64 + 1];
            boolean var7 = true;

            for (int var8 = 0; var8 < var5; var8++) {
               Selector var9 = this.selectors.get(var8);
               if (var9.applies(var2, var3, 0)) {
                  int var10 = var8 / 64;
                  long var11 = var6[var10] | 1L << var8;
                  var6[var10] = var11;
                  var7 = false;
               }
            }

            if (var7 && !var4) {
               return StyleMap.EMPTY_MAP;
            } else {
               String var17 = var2.getStyle();
               StyleManager.Cache.Key var18 = new StyleManager.Cache.Key(var6, var17);
               if (this.cache.containsKey(var18)) {
                  Integer var20 = this.cache.get(var18);
                  return var20 != null ? var1.getStyleMap(var20) : StyleMap.EMPTY_MAP;
               } else {
                  ArrayList var19 = new ArrayList();
                  if (var4) {
                     Selector var21 = var1.getInlineStyleSelector(var17);
                     if (var21 != null) {
                        var19.add(var21);
                     }
                  }

                  for (int var22 = 0; var22 < var6.length; var22++) {
                     if (var6[var22] != 0L) {
                        int var12 = var22 * 64;

                        for (int var13 = 0; var13 < 64; var13++) {
                           long var14 = 1L << var13;
                           if ((var14 & var6[var22]) == var14) {
                              Selector var16 = this.selectors.get(var12 + var13);
                              var19.add(var16);
                           }
                        }
                     }
                  }

                  int var23 = var1.nextSmapId();
                  this.cache.put(var18, var23);
                  StyleMap var24 = new StyleMap(var23, var19);
                  var1.addStyleMap(var24);
                  return var24;
               }
            }
         }
      }

      private static class Key {
         final long[] key;
         final String inlineStyle;

         Key(long[] var1, String var2) {
            this.key = var1;
            this.inlineStyle = var2 != null && var2.trim().isEmpty() ? null : var2;
         }

         @Override
         public String toString() {
            return Arrays.toString(this.key) + (this.inlineStyle != null ? "* {" + this.inlineStyle + "}" : "");
         }

         @Override
         public int hashCode() {
            int var1 = 3;
            var1 = 17 * var1 + Arrays.hashCode(this.key);
            if (this.inlineStyle != null) {
               var1 = 17 * var1 + this.inlineStyle.hashCode();
            }

            return var1;
         }

         @Override
         public boolean equals(Object var1) {
            if (var1 == null) {
               return false;
            } else if (this.getClass() != var1.getClass()) {
               return false;
            } else {
               StyleManager.Cache.Key var2 = (StyleManager.Cache.Key)var1;
               return (this.inlineStyle == null ? var2.inlineStyle == null : this.inlineStyle.equals(var2.inlineStyle))
                  ? Arrays.equals(this.key, var2.key)
                  : false;
            }
         }
      }
   }

   static class CacheContainer {
      private Map<StyleCache.Key, StyleCache> styleCache;
      private Map<List<String>, Map<StyleManager.Key, StyleManager.Cache>> cacheMap;
      private List<StyleMap> styleMapList;
      private Map<String, Selector> inlineStylesCache;
      private int styleMapId = 0;
      private int baseStyleMapId = 0;

      private Map<StyleCache.Key, StyleCache> getStyleCache() {
         if (this.styleCache == null) {
            this.styleCache = new HashMap<>();
         }

         return this.styleCache;
      }

      private Map<StyleManager.Key, StyleManager.Cache> getCacheMap(List<StyleManager.StylesheetContainer> var1, String var2) {
         if (this.cacheMap == null) {
            this.cacheMap = new HashMap<>();
         }

         synchronized (StyleManager.styleLock) {
            if ((var1 == null || var1.isEmpty()) && (var2 == null || var2.isEmpty())) {
               Object var9 = this.cacheMap.get(null);
               if (var9 == null) {
                  var9 = new HashMap();
                  this.cacheMap.put(null, (Map<StyleManager.Key, StyleManager.Cache>)var9);
               }

               return (Map<StyleManager.Key, StyleManager.Cache>)var9;
            } else {
               int var4 = var1.size();
               if (StyleManager.cacheMapKey == null) {
                  StyleManager.cacheMapKey = new ArrayList<>(var4);
               }

               for (int var5 = 0; var5 < var4; var5++) {
                  StyleManager.StylesheetContainer var6 = (StyleManager.StylesheetContainer)var1.get(var5);
                  if (var6 != null && var6.fname != null && !var6.fname.isEmpty()) {
                     StyleManager.cacheMapKey.add(var6.fname);
                  }
               }

               if (var2 != null) {
                  StyleManager.cacheMapKey.add(var2);
               }

               Object var10 = this.cacheMap.get(StyleManager.cacheMapKey);
               if (var10 == null) {
                  var10 = new HashMap();
                  this.cacheMap.put(StyleManager.cacheMapKey, (Map<StyleManager.Key, StyleManager.Cache>)var10);
                  StyleManager.cacheMapKey = null;
               } else {
                  StyleManager.cacheMapKey.clear();
               }

               return (Map<StyleManager.Key, StyleManager.Cache>)var10;
            }
         }
      }

      private List<StyleMap> getStyleMapList() {
         if (this.styleMapList == null) {
            this.styleMapList = new ArrayList<>();
         }

         return this.styleMapList;
      }

      private int nextSmapId() {
         this.styleMapId = this.baseStyleMapId + this.getStyleMapList().size();
         return this.styleMapId;
      }

      private void addStyleMap(StyleMap var1) {
         this.getStyleMapList().add(var1);
      }

      public StyleMap getStyleMap(int var1) {
         int var2 = var1 - this.baseStyleMapId;
         return 0 <= var2 && var2 < this.getStyleMapList().size() ? this.getStyleMapList().get(var2) : StyleMap.EMPTY_MAP;
      }

      private void clearCache() {
         if (this.cacheMap != null) {
            this.cacheMap.clear();
         }

         if (this.styleCache != null) {
            this.styleCache.clear();
         }

         if (this.styleMapList != null) {
            this.styleMapList.clear();
         }

         this.baseStyleMapId = this.styleMapId;
         if (this.baseStyleMapId > 1879048185) {
            this.baseStyleMapId = this.styleMapId = 0;
         }
      }

      private Selector getInlineStyleSelector(String var1) {
         if (var1 == null || var1.trim().isEmpty()) {
            return null;
         } else if (this.inlineStylesCache != null && this.inlineStylesCache.containsKey(var1)) {
            return this.inlineStylesCache.get(var1);
         } else {
            if (this.inlineStylesCache == null) {
               this.inlineStylesCache = new HashMap<>();
            }

            Stylesheet var2 = new CssParser().parse("*{" + var1 + "}");
            if (var2 != null) {
               var2.setOrigin(StyleOrigin.INLINE);
               List var3 = var2.getRules();
               Rule var4 = var3 != null && !var3.isEmpty() ? (Rule)var3.get(0) : null;
               ObservableList var5 = var4 != null ? var4.getSelectors() : null;
               Selector var6 = var5 != null && !var5.isEmpty() ? (Selector)var5.get(0) : null;
               if (var6 != null) {
                  var6.setOrdinal(-1);
                  this.inlineStylesCache.put(var1, var6);
                  return var6;
               }
            }

            this.inlineStylesCache.put(var1, null);
            return null;
         }
      }
   }

   private static final class ImageCache {
      private Map<String, SoftReference<Image>> imageCache = new HashMap<>();

      Image getCachedImage(String var1) {
         synchronized (StyleManager.styleLock) {
            Image var3 = null;
            if (this.imageCache.containsKey(var1)) {
               var3 = this.imageCache.get(var1).get();
            }

            if (var3 == null) {
               try {
                  var3 = new Image(var1);
                  if (var3.isError()) {
                     PlatformLogger var4 = StyleManager.getLogger();
                     if (var4 != null && var4.isLoggable(Level.WARNING)) {
                        DataURI var9 = DataURI.tryParse(var1);
                        if (var9 != null) {
                           var4.warning("Error loading image: " + var9);
                        } else {
                           var4.warning("Error loading image: " + var1);
                        }
                     }

                     var3 = null;
                  }

                  this.imageCache.put(var1, new SoftReference<>(var3));
               } catch (NullPointerException | IllegalArgumentException var7) {
                  PlatformLogger var5 = StyleManager.getLogger();
                  if (var5 != null && var5.isLoggable(Level.WARNING)) {
                     var5.warning(var7.getLocalizedMessage());
                  }
               }
            }

            return var3;
         }
      }

      void cleanUpImageCache(String var1) {
         synchronized (StyleManager.styleLock) {
            if (var1 != null && !this.imageCache.isEmpty()) {
               String var3 = var1.trim();
               if (!var3.isEmpty()) {
                  int var4 = var3.lastIndexOf(47);
                  String var5 = var4 > 0 ? var3.substring(0, var4) : var3;
                  int var6 = var5.length();
                  String[] var7 = new String[this.imageCache.size()];
                  int var8 = 0;

                  for (Entry var11 : this.imageCache.entrySet()) {
                     String var12 = (String)var11.getKey();
                     if (((SoftReference)var11.getValue()).get() == null) {
                        var7[var8++] = var12;
                     } else {
                        var4 = var12.lastIndexOf(47);
                        String var13 = var4 > 0 ? var12.substring(0, var4) : var12;
                        int var14 = var13.length();
                        boolean var15 = var14 > var6 ? var13.startsWith(var5) : var5.startsWith(var13);
                        if (var15) {
                           var7[var8++] = var12;
                        }
                     }
                  }

                  for (int var19 = 0; var19 < var8; var19++) {
                     this.imageCache.remove(var7[var19]);
                  }
               }
            }
         }
      }
   }

   private static class InstanceHolder {
      static final StyleManager INSTANCE = new StyleManager();
   }

   private static class Key {
      String className;
      String id;
      final StyleClassSet styleClasses = new StyleClassSet();

      @Override
      public boolean equals(Object var1) {
         if (this == var1) {
            return true;
         } else if (!(var1 instanceof StyleManager.Key)) {
            return true;
         } else {
            StyleManager.Key var2 = (StyleManager.Key)var1;
            if (this.className == null ? var2.className == null : this.className.equals(var2.className)) {
               return (this.id == null ? var2.id == null : this.id.equals(var2.id)) ? this.styleClasses.equals(var2.styleClasses) : false;
            } else {
               return false;
            }
         }
      }

      @Override
      public int hashCode() {
         int var1 = 7;
         var1 = 29 * var1 + (this.className != null ? this.className.hashCode() : 0);
         var1 = 29 * var1 + (this.id != null ? this.id.hashCode() : 0);
         return 29 * var1 + this.styleClasses.hashCode();
      }
   }

   static class RefList<K> {
      final List<Reference<K>> list = new ArrayList<>();

      void add(K var1) {
         for (int var2 = this.list.size() - 1; 0 <= var2; var2--) {
            Reference var3 = this.list.get(var2);
            Object var4 = var3.get();
            if (var4 == null) {
               this.list.remove(var2);
            } else if (var4 == var1) {
               return;
            }
         }

         this.list.add(new WeakReference<>((K)var1));
      }

      void remove(K var1) {
         for (int var2 = this.list.size() - 1; 0 <= var2; var2--) {
            Reference var3 = this.list.get(var2);
            Object var4 = var3.get();
            if (var4 == null) {
               this.list.remove(var2);
            } else if (var4 == var1) {
               this.list.remove(var2);
               return;
            }
         }
      }

      boolean contains(K var1) {
         for (int var2 = this.list.size() - 1; 0 <= var2; var2--) {
            Reference var3 = this.list.get(var2);
            Object var4 = var3.get();
            if (var4 == var1) {
               return true;
            }
         }

         return false;
      }
   }

   static class StylesheetContainer {
      final String fname;
      final Stylesheet stylesheet;
      final SelectorPartitioning selectorPartitioning;
      final StyleManager.RefList<Parent> parentUsers;
      final int hash;
      final byte[] checksum;
      boolean checksumInvalid = false;

      StylesheetContainer(String var1, Stylesheet var2) {
         this(var1, var2, var2 != null ? StyleManager.calculateCheckSum(var2.getUrl()) : new byte[0]);
      }

      StylesheetContainer(String var1, Stylesheet var2, byte[] var3) {
         this.fname = var1;
         this.hash = var1 != null ? var1.hashCode() : 127;
         this.stylesheet = var2;
         if (var2 != null) {
            this.selectorPartitioning = new SelectorPartitioning();
            List var4 = var2.getRules();
            int var5 = var4 != null && !var4.isEmpty() ? var4.size() : 0;

            for (int var6 = 0; var6 < var5; var6++) {
               Rule var7 = (Rule)var4.get(var6);
               ObservableList var8 = var7.getSelectors();
               int var9 = var8 != null && !var8.isEmpty() ? var8.size() : 0;

               for (int var10 = 0; var10 < var9; var10++) {
                  Selector var11 = (Selector)var8.get(var10);
                  this.selectorPartitioning.partition(var11);
               }
            }
         } else {
            this.selectorPartitioning = null;
         }

         this.parentUsers = new StyleManager.RefList<>();
         this.checksum = var3;
      }

      void invalidateChecksum() {
         this.checksumInvalid = this.checksum.length > 0;
      }

      @Override
      public int hashCode() {
         return this.hash;
      }

      @Override
      public boolean equals(Object var1) {
         if (var1 == null) {
            return false;
         } else if (this.getClass() != var1.getClass()) {
            return false;
         } else {
            StyleManager.StylesheetContainer var2 = (StyleManager.StylesheetContainer)var1;
            return this.fname == null ? var2.fname == null : this.fname.equals(var2.fname);
         }
      }

      @Override
      public String toString() {
         return this.fname;
      }
   }
}
