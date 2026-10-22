package org.eclipse.jetty.security.jaas;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import org.eclipse.jetty.security.PropertyUserStore;
import org.eclipse.jetty.util.component.AbstractLifeCycle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropertyUserStoreManager extends AbstractLifeCycle {
   private static final Logger LOG = LoggerFactory.getLogger(PropertyUserStoreManager.class);
   private Map<String, PropertyUserStore> _propertyUserStores;

   public PropertyUserStore getPropertyUserStore(String file) {
      synchronized (this) {
         return this._propertyUserStores == null ? null : this._propertyUserStores.get(file);
      }
   }

   public PropertyUserStore addPropertyUserStore(String file, PropertyUserStore store) {
      synchronized (this) {
         Objects.requireNonNull(this._propertyUserStores);
         PropertyUserStore existing = this._propertyUserStores.get(file);
         if (existing != null) {
            return existing;
         } else {
            this._propertyUserStores.put(file, store);
            return store;
         }
      }
   }

   protected void doStart() throws Exception {
      this._propertyUserStores = new HashMap<>();
      super.doStart();
   }

   protected void doStop() throws Exception {
      for (Entry<String, PropertyUserStore> entry : this._propertyUserStores.entrySet()) {
         try {
            entry.getValue().stop();
         } catch (Exception var4) {
            LOG.warn("Error stopping PropertyUserStore at {}", entry.getKey(), var4);
         }
      }

      this._propertyUserStores = null;
      super.doStop();
   }
}
