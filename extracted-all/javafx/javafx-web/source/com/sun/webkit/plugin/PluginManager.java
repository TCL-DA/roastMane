package com.sun.webkit.plugin;

import com.sun.javafx.logging.PlatformLogger;
import java.net.URL;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.ServiceLoader;
import java.util.TreeMap;
import java.util.Vector;

public final class PluginManager {
   private static final PlatformLogger log = PlatformLogger.getLogger("com.sun.browser.plugin.PluginManager");
   private static final ServiceLoader<PluginHandler> pHandlers = ServiceLoader.load(PluginHandler.class);
   private static final TreeMap<String, PluginHandler> hndMap = new TreeMap<>();
   private static PluginHandler[] hndArray;
   private static final HashSet<String> disabledPluginHandlers = new HashSet<>();

   private static void updatePluginHandlers() {
      log.fine("Update plugin handlers");
      hndMap.clear();

      for (PluginHandler var1 : pHandlers) {
         if (var1.isSupportedPlatform() && !isDisabledPlugin(var1)) {
            String[] var2 = var1.supportedMIMETypes();

            for (String var6 : var2) {
               hndMap.put(var6, var1);
               log.fine(var6);
            }
         }
      }

      Collection var7 = hndMap.values();
      hndArray = var7.toArray(new PluginHandler[var7.size()]);
   }

   public static Plugin createPlugin(URL var0, String var1, String[] var2, String[] var3) {
      try {
         PluginHandler var4 = hndMap.get(var1);
         if (var4 == null) {
            return new DefaultPlugin(var0, var1, var2, var3);
         } else {
            Plugin var5 = var4.createPlugin(var0, var1, var2, var3);
            return (Plugin)(var5 == null ? new DefaultPlugin(var0, var1, var2, var3) : var5);
         }
      } catch (Throwable var6) {
         log.fine("Cannot create plugin", var6);
         return new DefaultPlugin(var0, var1, var2, var3);
      }
   }

   private static List<PluginHandler> getAvailablePlugins() {
      Vector var0 = new Vector();

      for (PluginHandler var2 : pHandlers) {
         if (var2.isSupportedPlatform()) {
            var0.add(var2);
         }
      }

      return var0;
   }

   private static PluginHandler getEnabledPlugin(int var0) {
      return var0 >= 0 && var0 < hndArray.length ? hndArray[var0] : null;
   }

   private static int getEnabledPluginCount() {
      return hndArray.length;
   }

   private static void disablePlugin(PluginHandler var0) {
      disabledPluginHandlers.add(var0.getClass().getCanonicalName());
      updatePluginHandlers();
   }

   private static void enablePlugin(PluginHandler var0) {
      disabledPluginHandlers.remove(var0.getClass().getCanonicalName());
      updatePluginHandlers();
   }

   private static boolean isDisabledPlugin(PluginHandler var0) {
      return disabledPluginHandlers.contains(var0.getClass().getCanonicalName());
   }

   private static boolean supportsMIMEType(String var0) {
      return hndMap.containsKey(var0);
   }

   private static String getPluginNameForMIMEType(String var0) {
      PluginHandler var1 = hndMap.get(var0);
      return var1 != null ? var1.getName() : "";
   }

   static {
      if ("false".equalsIgnoreCase(System.getProperty("com.sun.browser.plugin"))) {
         for (PluginHandler var1 : getAvailablePlugins()) {
            disabledPluginHandlers.add(var1.getClass().getCanonicalName());
         }
      }

      updatePluginHandlers();
   }
}
