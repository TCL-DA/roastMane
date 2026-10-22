package org.eclipse.jetty.websocket.core;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;
import org.eclipse.jetty.http.BadMessageException;
import org.eclipse.jetty.util.StringUtil;
import org.eclipse.jetty.util.TypeUtil;

public class WebSocketExtensionRegistry implements Iterable<Class<? extends Extension>> {
   private final Map<String, Class<? extends Extension>> availableExtensions = new HashMap<>();

   public WebSocketExtensionRegistry() {
      TypeUtil.serviceStream(ServiceLoader.load(Extension.class, this.getClass().getClassLoader()))
         .forEach(ext -> this.availableExtensions.put(ext.getName(), (Class<? extends Extension>)ext.getClass()));
   }

   public Map<String, Class<? extends Extension>> getAvailableExtensions() {
      return this.availableExtensions;
   }

   public Class<? extends Extension> getExtension(String name) {
      return this.availableExtensions.get(name);
   }

   public Set<String> getAvailableExtensionNames() {
      return this.availableExtensions.keySet();
   }

   public boolean isAvailable(String name) {
      return this.availableExtensions.containsKey(name);
   }

   @Override
   public Iterator<Class<? extends Extension>> iterator() {
      return this.availableExtensions.values().iterator();
   }

   public Extension newInstance(ExtensionConfig config, WebSocketComponents components) {
      if (config == null) {
         return null;
      } else {
         String name = config.getName();
         if (StringUtil.isBlank(name)) {
            return null;
         } else {
            Class<? extends Extension> extClass = this.getExtension(name);
            if (extClass == null) {
               return null;
            } else {
               try {
                  Extension ext = (Extension)components.getObjectFactory().createInstance(extClass);
                  ext.init(config, components);
                  return ext;
               } catch (Throwable var6) {
                  throw new BadMessageException("Cannot instantiate extension: " + extClass, var6);
               }
            }
         }
      }
   }

   public void register(String name, Class<? extends Extension> extension) {
      this.availableExtensions.put(name, extension);
   }

   public void unregister(String name) {
      this.availableExtensions.remove(name);
   }
}
