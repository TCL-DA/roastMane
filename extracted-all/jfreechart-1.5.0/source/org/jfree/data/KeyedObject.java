package org.jfree.data;

import java.io.Serializable;
import org.jfree.chart.util.ObjectUtils;
import org.jfree.chart.util.PublicCloneable;

public class KeyedObject implements Cloneable, PublicCloneable, Serializable {
   private static final long serialVersionUID = 2677930479256885863L;
   private Comparable key;
   private Object object;

   public KeyedObject(Comparable key, Object object) {
      this.key = key;
      this.object = object;
   }

   public Comparable getKey() {
      return this.key;
   }

   public Object getObject() {
      return this.object;
   }

   public void setObject(Object object) {
      this.object = object;
   }

   @Override
   public Object clone() throws CloneNotSupportedException {
      KeyedObject clone = (KeyedObject)super.clone();
      if (this.object instanceof PublicCloneable) {
         PublicCloneable pc = (PublicCloneable)this.object;
         clone.object = pc.clone();
      }

      return clone;
   }

   @Override
   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      } else if (!(obj instanceof KeyedObject)) {
         return false;
      } else {
         KeyedObject that = (KeyedObject)obj;
         return !ObjectUtils.equal(this.key, that.key) ? false : ObjectUtils.equal(this.object, that.object);
      }
   }
}
