package jakarta.ws.rs.core;

import jakarta.ws.rs.RuntimeType;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

public interface Configuration {
   RuntimeType getRuntimeType();

   Map<String, Object> getProperties();

   Object getProperty(String var1);

   default boolean hasProperty(String name) {
      return this.getProperty(name) != null;
   }

   Collection<String> getPropertyNames();

   boolean isEnabled(Feature var1);

   boolean isEnabled(Class<? extends Feature> var1);

   boolean isRegistered(Object var1);

   boolean isRegistered(Class<?> var1);

   Map<Class<?>, Integer> getContracts(Class<?> var1);

   Set<Class<?>> getClasses();

   Set<Object> getInstances();
}
