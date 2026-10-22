package org.eclipse.jetty.util;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DecoratedObjectFactory implements Iterable<Decorator>, Decorator {
   private static final Logger LOG = LoggerFactory.getLogger(DecoratedObjectFactory.class);
   public static final String ATTR = DecoratedObjectFactory.class.getName();
   private static final ThreadLocal<Object> decoratorInfo = new ThreadLocal<>();
   private final List<Decorator> decorators = new ArrayList<>();

   public static void associateInfo(Object info) {
      decoratorInfo.set(info);
   }

   public static void disassociateInfo() {
      decoratorInfo.set(null);
   }

   public static Object getAssociatedInfo() {
      return decoratorInfo.get();
   }

   public void addDecorator(Decorator decorator) {
      LOG.debug("Adding Decorator: {}", decorator);
      this.decorators.add(decorator);
   }

   public boolean removeDecorator(Decorator decorator) {
      LOG.debug("Remove Decorator: {}", decorator);
      return this.decorators.remove(decorator);
   }

   public void clear() {
      this.decorators.clear();
   }

   public <T> T createInstance(Class<T> clazz) throws InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
      if (LOG.isDebugEnabled()) {
         LOG.debug("Creating Instance: {}", clazz);
      }

      T o = clazz.getDeclaredConstructor().newInstance();
      return this.decorate(o);
   }

   @Override
   public <T> T decorate(T obj) {
      T f = obj;

      for (int i = this.decorators.size() - 1; i >= 0; i--) {
         f = this.decorators.get(i).decorate(f);
      }

      return f;
   }

   @Override
   public void destroy(Object obj) {
      for (Decorator decorator : this.decorators) {
         decorator.destroy(obj);
      }
   }

   public List<Decorator> getDecorators() {
      return Collections.unmodifiableList(this.decorators);
   }

   @Override
   public Iterator<Decorator> iterator() {
      return this.decorators.iterator();
   }

   public void setDecorators(List<? extends Decorator> decorators) {
      this.decorators.clear();
      if (decorators != null) {
         this.decorators.addAll(decorators);
      }
   }

   @Override
   public String toString() {
      StringBuilder str = new StringBuilder();
      str.append(this.getClass().getName()).append("[decorators=");
      str.append(Integer.toString(this.decorators.size()));
      str.append("]");
      return str.toString();
   }
}
