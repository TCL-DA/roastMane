package org.glassfish.jersey.internal;

import java.util.Collection;

public interface PropertiesDelegate {
   Object getProperty(String var1);

   default boolean hasProperty(String name) {
      return this.getProperty(name) != null;
   }

   Collection<String> getPropertyNames();

   void setProperty(String var1, Object var2);

   void removeProperty(String var1);
}
