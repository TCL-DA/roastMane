package org.jfree.data;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.jfree.chart.util.Args;
import org.jfree.chart.util.ObjectUtils;
import org.jfree.chart.util.PublicCloneable;

public class KeyToGroupMap implements Cloneable, PublicCloneable, Serializable {
   private static final long serialVersionUID = -2228169345475318082L;
   private Comparable defaultGroup;
   private List groups;
   private Map keyToGroupMap;

   public KeyToGroupMap() {
      this("Default Group");
   }

   public KeyToGroupMap(Comparable defaultGroup) {
      Args.nullNotPermitted(defaultGroup, "defaultGroup");
      this.defaultGroup = defaultGroup;
      this.groups = new ArrayList();
      this.keyToGroupMap = new HashMap();
   }

   public int getGroupCount() {
      return this.groups.size() + 1;
   }

   public List getGroups() {
      List result = new ArrayList();
      result.add(this.defaultGroup);

      for (Comparable group : this.groups) {
         if (!result.contains(group)) {
            result.add(group);
         }
      }

      return result;
   }

   public int getGroupIndex(Comparable group) {
      int result = this.groups.indexOf(group);
      if (result < 0) {
         if (this.defaultGroup.equals(group)) {
            result = 0;
         }
      } else {
         result++;
      }

      return result;
   }

   public Comparable getGroup(Comparable key) {
      Args.nullNotPermitted(key, "key");
      Comparable result = this.defaultGroup;
      Comparable group = (Comparable)this.keyToGroupMap.get(key);
      if (group != null) {
         result = group;
      }

      return result;
   }

   public void mapKeyToGroup(Comparable key, Comparable group) {
      Args.nullNotPermitted(key, "key");
      Comparable currentGroup = this.getGroup(key);
      if (!currentGroup.equals(this.defaultGroup) && !currentGroup.equals(group)) {
         int count = this.getKeyCount(currentGroup);
         if (count == 1) {
            this.groups.remove(currentGroup);
         }
      }

      if (group == null) {
         this.keyToGroupMap.remove(key);
      } else {
         if (!this.groups.contains(group) && !this.defaultGroup.equals(group)) {
            this.groups.add(group);
         }

         this.keyToGroupMap.put(key, group);
      }
   }

   public int getKeyCount(Comparable group) {
      Args.nullNotPermitted(group, "group");
      int result = 0;

      for (Comparable g : this.keyToGroupMap.values()) {
         if (group.equals(g)) {
            result++;
         }
      }

      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      } else if (!(obj instanceof KeyToGroupMap)) {
         return false;
      } else {
         KeyToGroupMap that = (KeyToGroupMap)obj;
         return !ObjectUtils.equal(this.defaultGroup, that.defaultGroup) ? false : this.keyToGroupMap.equals(that.keyToGroupMap);
      }
   }

   @Override
   public Object clone() throws CloneNotSupportedException {
      KeyToGroupMap result = (KeyToGroupMap)super.clone();
      result.defaultGroup = (Comparable)clone(this.defaultGroup);
      result.groups = (List)clone((Collection)this.groups);
      result.keyToGroupMap = (Map)clone(this.keyToGroupMap);
      return result;
   }

   private static Object clone(Object object) {
      if (object == null) {
         return null;
      } else {
         Class c = object.getClass();
         Object result = null;

         try {
            Method m = c.getMethod("clone", (Class<?>[])null);
            if (Modifier.isPublic(m.getModifiers())) {
               try {
                  result = m.invoke(object, (Object[])null);
               } catch (Exception var5) {
                  var5.printStackTrace();
               }
            }
         } catch (NoSuchMethodException var6) {
            result = object;
         }

         return result;
      }
   }

   private static Collection clone(Collection list) throws CloneNotSupportedException {
      Collection result = null;
      if (list != null) {
         try {
            List clone = (List)list.getClass().newInstance();
            Iterator iterator = list.iterator();

            while (iterator.hasNext()) {
               clone.add(clone(iterator.next()));
            }

            result = clone;
         } catch (Exception var4) {
            throw new CloneNotSupportedException("Exception.");
         }
      }

      return result;
   }
}
