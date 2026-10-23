package org.cropster.roastingintelligence.util;

import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.ResourceBundle;
import org.apache.commons.collections4.iterators.EnumerationIterator;
import org.apache.commons.collections4.iterators.IteratorChain;
import org.apache.commons.collections4.iterators.IteratorEnumeration;

public class MultiResourceBundle extends ResourceBundle {
   private final ResourceBundle[] _additionalBundles;

   public MultiResourceBundle(ResourceBundle parent, ResourceBundle... additionalBundles) {
      this.setParent(parent);
      this._additionalBundles = additionalBundles;
   }

   @Override
   protected Object handleGetObject(String key) {
      if (this.parent.containsKey(key)) {
         return this.parent.getObject(key);
      } else {
         for (ResourceBundle bundle : this._additionalBundles) {
            if (bundle.containsKey(key)) {
               return bundle.getObject(key);
            }
         }

         return null;
      }
   }

   @Override
   public Enumeration<String> getKeys() {
      Collection<Iterator<? extends String>> its = new LinkedHashSet<>();
      its.add(new EnumerationIterator(this.parent.getKeys()));

      for (ResourceBundle bundle : this._additionalBundles) {
         its.add(new EnumerationIterator(bundle.getKeys()));
      }

      return new IteratorEnumeration(new IteratorChain(its));
   }
}
