package com.codahale.metrics;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultObjectNameFactory implements ObjectNameFactory {
   private static final Logger LOGGER = LoggerFactory.getLogger(JmxReporter.class);

   @Override
   public ObjectName createName(String type, String domain, String name) {
      try {
         ObjectName objectName = new ObjectName(domain, "name", name);
         if (objectName.isPattern()) {
            objectName = new ObjectName(domain, "name", ObjectName.quote(name));
         }

         return objectName;
      } catch (MalformedObjectNameException var7) {
         try {
            return new ObjectName(domain, "name", ObjectName.quote(name));
         } catch (MalformedObjectNameException var6) {
            LOGGER.warn("Unable to register {} {}", new Object[]{type, name, var6});
            throw new RuntimeException(var6);
         }
      }
   }
}
