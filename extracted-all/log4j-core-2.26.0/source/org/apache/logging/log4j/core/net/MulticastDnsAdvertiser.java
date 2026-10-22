package org.apache.logging.log4j.core.net;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.util.Integers;
import org.apache.logging.log4j.status.StatusLogger;
import org.apache.logging.log4j.util.LoaderUtil;

@Plugin(
   name = "MulticastDns",
   category = "Core",
   elementType = "advertiser",
   printObject = false
)
public class MulticastDnsAdvertiser implements Advertiser {
   protected static final Logger LOGGER = StatusLogger.getLogger();
   private static final int MAX_LENGTH = 255;
   private static final int DEFAULT_PORT = 4555;
   private static Object jmDNS = initializeJmDns();
   private static Class<?> jmDNSClass;
   private static Class<?> serviceInfoClass;

   @Override
   public Object advertise(final Map<String, String> properties) {
      Map<String, String> truncatedProperties = new HashMap<>();

      for (Entry<String, String> entry : properties.entrySet()) {
         if (entry.getKey().length() <= 255 && entry.getValue().length() <= 255) {
            truncatedProperties.put(entry.getKey(), entry.getValue());
         }
      }

      String protocol = truncatedProperties.get("protocol");
      String zone = "._log4j._" + (protocol != null ? protocol : "tcp") + ".local.";
      String portString = truncatedProperties.get("port");
      int port = Integers.parseInt(portString, 4555);
      String name = truncatedProperties.get("name");
      if (jmDNS != null) {
         boolean isVersion3 = false;

         try {
            jmDNSClass.getMethod("create");
            isVersion3 = true;
         } catch (NoSuchMethodException var13) {
         }

         Object serviceInfo;
         if (isVersion3) {
            serviceInfo = buildServiceInfoVersion3(zone, port, name, truncatedProperties);
         } else {
            serviceInfo = buildServiceInfoVersion1(zone, port, name, truncatedProperties);
         }

         try {
            Method method = jmDNSClass.getMethod("registerService", serviceInfoClass);
            method.invoke(jmDNS, serviceInfo);
         } catch (InvocationTargetException | IllegalAccessException var11) {
            LOGGER.warn("Unable to invoke registerService method", var11);
         } catch (NoSuchMethodException var12) {
            LOGGER.warn("No registerService method", var12);
         }

         return serviceInfo;
      } else {
         LOGGER.warn("JMDNS not available - will not advertise ZeroConf support");
         return null;
      }
   }

   @Override
   public void unadvertise(final Object serviceInfo) {
      if (jmDNS != null) {
         try {
            Method method = jmDNSClass.getMethod("unregisterService", serviceInfoClass);
            method.invoke(jmDNS, serviceInfo);
         } catch (InvocationTargetException | IllegalAccessException var3) {
            LOGGER.warn("Unable to invoke unregisterService method", var3);
         } catch (NoSuchMethodException var4) {
            LOGGER.warn("No unregisterService method", var4);
         }
      }
   }

   private static Object createJmDnsVersion1() {
      try {
         return LoaderUtil.newInstanceOf(jmDNSClass);
      } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException | InstantiationException var1) {
         LOGGER.warn("Unable to instantiate JMDNS", var1);
         return null;
      }
   }

   private static Object createJmDnsVersion3() {
      try {
         Method jmDNSCreateMethod = jmDNSClass.getMethod("create");
         return jmDNSCreateMethod.invoke(null, (Object[])null);
      } catch (InvocationTargetException | IllegalAccessException var1) {
         LOGGER.warn("Unable to invoke create method", var1);
      } catch (NoSuchMethodException var2) {
         LOGGER.warn("Unable to get create method", var2);
      }

      return null;
   }

   private static Object buildServiceInfoVersion1(final String zone, final int port, final String name, final Map<String, String> properties) {
      Hashtable<String, String> hashtableProperties = new Hashtable<>(properties);

      try {
         return serviceInfoClass.getConstructor(String.class, String.class, int.class, int.class, int.class, Hashtable.class)
            .newInstance(zone, name, port, 0, 0, hashtableProperties);
      } catch (InstantiationException | InvocationTargetException | IllegalAccessException var6) {
         LOGGER.warn("Unable to construct ServiceInfo instance", var6);
      } catch (NoSuchMethodException var7) {
         LOGGER.warn("Unable to get ServiceInfo constructor", var7);
      }

      return null;
   }

   private static Object buildServiceInfoVersion3(final String zone, final int port, final String name, final Map<String, String> properties) {
      try {
         return serviceInfoClass.getMethod("create", String.class, String.class, int.class, int.class, int.class, Map.class)
            .invoke(null, zone, name, port, 0, 0, properties);
      } catch (InvocationTargetException | IllegalAccessException var5) {
         LOGGER.warn("Unable to invoke create method", var5);
      } catch (NoSuchMethodException var6) {
         LOGGER.warn("Unable to find create method", var6);
      }

      return null;
   }

   private static Object initializeJmDns() {
      try {
         jmDNSClass = LoaderUtil.loadClass("javax.jmdns.JmDNS");
         serviceInfoClass = LoaderUtil.loadClass("javax.jmdns.ServiceInfo");
         boolean isVersion3 = false;

         try {
            jmDNSClass.getMethod("create");
            isVersion3 = true;
         } catch (NoSuchMethodException var2) {
         }

         return isVersion3 ? createJmDnsVersion3() : createJmDnsVersion1();
      } catch (ExceptionInInitializerError | ClassNotFoundException var3) {
         LOGGER.warn("JmDNS or serviceInfo class not found", var3);
         return null;
      }
   }
}
