package org.cropster.roastingintelligence.util;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ResourceBundle;
import org.apache.commons.collections4.iterators.EnumerationIterator;
import org.apache.commons.collections4.iterators.IteratorChain;
import org.apache.commons.collections4.iterators.IteratorEnumeration;

public class OverrideResourceBundle extends ResourceBundle {
   private final Map<String, String> overrides = new HashMap<>();

   public OverrideResourceBundle(ResourceBundle parent) {
      this.setParent(parent);
   }

   public OverrideResourceBundle override(String key, String message) {
      this.overrides.put(key, message);
      return this;
   }

   @Override
   protected Object handleGetObject(String key) {
      return this.overrides.containsKey(key) ? this.overrides.get(key) : this.parent.getObject(key);
   }

   @Override
   public Enumeration<String> getKeys() {
      Iterator<String> it = this.overrides.keySet().iterator();
      if (this.parent != null) {
         it = new IteratorChain(it, new EnumerationIterator(this.parent.getKeys()));
      }

      return new IteratorEnumeration(it);
   }
}
