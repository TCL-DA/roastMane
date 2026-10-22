package jakarta.json.bind.spi;

import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbException;
import java.util.Iterator;
import java.util.ServiceLoader;

public abstract class JsonbProvider {
   private static final String DEFAULT_PROVIDER = "org.eclipse.yasson.JsonBindingProvider";

   protected JsonbProvider() {
   }

   public static JsonbProvider provider() {
      ServiceLoader<JsonbProvider> loader = ServiceLoader.load(JsonbProvider.class);
      Iterator<JsonbProvider> it = loader.iterator();
      if (it.hasNext()) {
         return it.next();
      } else {
         try {
            Class<?> clazz = Class.forName("org.eclipse.yasson.JsonBindingProvider");
            return (JsonbProvider)clazz.newInstance();
         } catch (ClassNotFoundException var3) {
            throw new JsonbException("JSON Binding provider org.eclipse.yasson.JsonBindingProvider not found", var3);
         } catch (Exception var4) {
            throw new JsonbException("JSON Binding provider org.eclipse.yasson.JsonBindingProvider could not be instantiated: " + var4, var4);
         }
      }
   }

   public static JsonbProvider provider(String providerName) {
      if (providerName == null) {
         throw new IllegalArgumentException();
      } else {
         for (JsonbProvider provider : ServiceLoader.load(JsonbProvider.class)) {
            if (providerName.equals(provider.getClass().getName())) {
               return provider;
            }
         }

         throw new JsonbException("JSON Binding provider " + providerName + " not found", new ClassNotFoundException(providerName));
      }
   }

   public abstract JsonbBuilder create();
}
