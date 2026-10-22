package org.cropster.roastingintelligence.util;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ResourceBundle;
import org.apache.commons.collections4.iterators.EnumerationIterator;
import org.apache.commons.collections4.iterators.IteratorChain;
import org.apache.commons.collections4.iterators.IteratorEnumeration;
import org.cropster.roastingintelligence.measurement.MeasurementSourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class AliasResourceBundle extends ResourceBundle {
   private final Logger logger = LoggerFactory.getLogger(AliasResourceBundle.class);
   private final Map<String, String> _aliases = new HashMap<>();

   public AliasResourceBundle(ResourceBundle parent) {
      this.setParent(parent);
   }

   @Override
   protected Object handleGetObject(String key) {
      if (this._aliases.containsKey(key)) {
         String targetKey = this._aliases.get(key);
         if (this.parent.containsKey(targetKey)) {
            return this.parent.getObject(targetKey);
         } else {
            if (this.logger.isTraceEnabled()) {
               this.logger.trace("Missing key {} for alias {}", targetKey, key);
            }

            return targetKey;
         }
      } else {
         return null;
      }
   }

   @Override
   public Enumeration<String> getKeys() {
      Iterator<String> it = this._aliases.keySet().iterator();
      if (this.parent != null) {
         it = new IteratorChain(it, new EnumerationIterator(this.parent.getKeys()));
      }

      return new IteratorEnumeration(it);
   }

   public AliasResourceBundle.Alias alias(String alias) {
      return new AliasResourceBundle.Alias(alias);
   }

   public AliasResourceBundle.Alias alias(Enum<?> e) {
      return new AliasResourceBundle.Alias(e.name());
   }

   public AliasResourceBundle.Alias alias(String alias, Class<? extends MeasurementSourceFactory> sourceFactoryClass) {
      return new AliasResourceBundle.Alias(alias + sourceFactoryClass.getSimpleName());
   }

   public final class Alias {
      private final String _alias;

      private Alias(String alias) {
         if (alias == null) {
            throw new NullPointerException();
         } else {
            this._alias = alias;
         }
      }

      public void m_(String target) {
         if (target == null) {
            throw new NullPointerException();
         } else {
            AliasResourceBundle.this._aliases.put(this._alias, target);
         }
      }
   }
}
