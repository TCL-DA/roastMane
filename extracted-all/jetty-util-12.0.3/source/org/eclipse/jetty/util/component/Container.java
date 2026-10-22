package org.eclipse.jetty.util.component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EventListener;
import java.util.List;

public interface Container {
   boolean addBean(Object var1);

   boolean addBean(Object var1, boolean var2);

   Collection<Object> getBeans();

   <T> Collection<T> getBeans(Class<T> var1);

   default <T> Collection<T> getCachedBeans(Class<T> clazz) {
      return this.getBeans(clazz);
   }

   <T> T getBean(Class<T> var1);

   boolean removeBean(Object var1);

   boolean addEventListener(EventListener var1);

   boolean removeEventListener(EventListener var1);

   void unmanage(Object var1);

   void manage(Object var1);

   boolean isManaged(Object var1);

   <T> Collection<T> getContainedBeans(Class<T> var1);

   default List<EventListener> getEventListeners() {
      return Collections.unmodifiableList(new ArrayList<>(this.getBeans(EventListener.class)));
   }

   static boolean addBean(Object parent, Object child) {
      return parent instanceof Container ? ((Container)parent).addBean(child) : false;
   }

   static boolean addBean(Object parent, Object child, boolean managed) {
      return parent instanceof Container ? ((Container)parent).addBean(child, managed) : false;
   }

   static void unmanage(Object parent, Object child) {
      if (parent instanceof Container container) {
         container.unmanage(child);
      }
   }

   static boolean removeBean(Object parent, Object child) {
      return parent instanceof Container ? ((Container)parent).removeBean(child) : false;
   }

   public interface InheritedListener extends Container.Listener {
   }

   public interface Listener extends EventListener {
      void beanAdded(Container var1, Object var2);

      void beanRemoved(Container var1, Object var2);
   }
}
