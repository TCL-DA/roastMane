package org.eclipse.jetty.util.component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.eclipse.jetty.util.Attributes;
import org.eclipse.jetty.util.thread.AutoLock;

public class AttributeContainerMap extends ContainerLifeCycle implements Attributes {
   private final AutoLock _lock = new AutoLock();
   private final Map<String, Object> _map = new HashMap<>();

   @Override
   public Object setAttribute(String name, Object attribute) {
      Object var5;
      try (AutoLock l = this._lock.lock()) {
         Object old = this._map.put(name, attribute);
         this.updateBean(old, attribute);
         var5 = old;
      }

      return var5;
   }

   @Override
   public Object removeAttribute(String name) {
      Object var4;
      try (AutoLock l = this._lock.lock()) {
         Object removed = this._map.remove(name);
         if (removed != null) {
            this.removeBean(removed);
         }

         var4 = removed;
      }

      return var4;
   }

   @Override
   public Object getAttribute(String name) {
      Object var3;
      try (AutoLock l = this._lock.lock()) {
         var3 = this._map.get(name);
      }

      return var3;
   }

   @Override
   public Set<String> getAttributeNameSet() {
      Set var2;
      try (AutoLock l = this._lock.lock()) {
         var2 = this._map.keySet();
      }

      return var2;
   }

   @Override
   public void clearAttributes() {
      try (AutoLock l = this._lock.lock()) {
         this._map.clear();
         this.removeBeans();
      }
   }

   @Override
   public void dump(Appendable out, String indent) throws IOException {
      Dumpable.dumpObject(out, this);
      Dumpable.dumpMapEntries(out, indent, this._map, true);
   }

   @Override
   public String toString() {
      return String.format("%s@%x{size=%d}", this.getClass().getSimpleName(), this.hashCode(), this._map.size());
   }
}
