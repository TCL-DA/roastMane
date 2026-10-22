package org.jvnet.hk2.internal;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.glassfish.hk2.api.ActiveDescriptor;
import org.glassfish.hk2.api.Context;
import org.glassfish.hk2.api.Injectee;
import org.glassfish.hk2.api.PerLookup;
import org.glassfish.hk2.api.ServiceHandle;
import org.glassfish.hk2.utilities.reflection.ReflectionHelper;

public class ServiceHandleImpl<T> implements ServiceHandle<T> {
   private final ActiveDescriptor<T> root;
   private final ServiceLocatorImpl locator;
   private final LinkedList<Injectee> injectees = new LinkedList<>();
   private final Object lock = new Object();
   private boolean serviceDestroyed = false;
   private boolean serviceSet = false;
   private T service;
   private Object serviceData;
   private final LinkedList<ServiceHandleImpl<?>> subHandles = new LinkedList<>();

   ServiceHandleImpl(ServiceLocatorImpl locator, ActiveDescriptor<T> root, Injectee injectee) {
      this.root = root;
      this.locator = locator;
      if (injectee != null) {
         this.injectees.add(injectee);
      }
   }

   public T getService() {
      return this.getService(this);
   }

   private Injectee getLastInjectee() {
      synchronized (this.lock) {
         return this.injectees.isEmpty() ? null : this.injectees.getLast();
      }
   }

   T getService(ServiceHandle<T> handle) {
      if (this.root instanceof Closeable) {
         Closeable closeable = (Closeable)this.root;
         if (closeable.isClosed()) {
            throw new IllegalStateException("This service has been unbound: " + this.root);
         }
      }

      synchronized (this.lock) {
         if (this.serviceDestroyed) {
            throw new IllegalStateException("Service has been disposed");
         } else if (this.serviceSet) {
            return this.service;
         } else {
            Injectee injectee = this.getLastInjectee();
            Class<?> requiredClass = injectee == null ? null : ReflectionHelper.getRawClass(injectee.getRequiredType());
            this.service = Utilities.createService(this.root, injectee, this.locator, handle, requiredClass);
            this.serviceSet = true;
            return this.service;
         }
      }
   }

   public ActiveDescriptor<T> getActiveDescriptor() {
      return this.root;
   }

   public boolean isActive() {
      if (this.serviceDestroyed) {
         return false;
      } else if (this.serviceSet) {
         return true;
      } else {
         try {
            Context<?> context = this.locator.resolveContext(this.root.getScopeAnnotation());
            return context.containsKey(this.root);
         } catch (IllegalStateException var2) {
            return false;
         }
      }
   }

   public void close() {
      if (this.root.isReified()) {
         boolean localServiceSet;
         boolean serviceActive;
         List<ServiceHandleImpl<?>> localSubHandles;
         synchronized (this.lock) {
            serviceActive = this.isActive();
            if (this.serviceDestroyed) {
               return;
            }

            this.serviceDestroyed = true;
            localServiceSet = this.serviceSet;
            localSubHandles = new ArrayList<>(this.subHandles);
            this.subHandles.clear();
         }

         if (this.root.getScopeAnnotation().equals(PerLookup.class)) {
            if (localServiceSet) {
               this.root.dispose(this.service);
            }
         } else if (serviceActive) {
            Context<?> context;
            try {
               context = this.locator.resolveContext(this.root.getScopeAnnotation());
            } catch (Throwable var6) {
               return;
            }

            context.destroyOne(this.root);
         }

         for (ServiceHandleImpl<?> subHandle : localSubHandles) {
            subHandle.destroy();
         }
      }
   }

   public void setServiceData(Object serviceData) {
      synchronized (this.lock) {
         this.serviceData = serviceData;
      }
   }

   public Object getServiceData() {
      synchronized (this.lock) {
         return this.serviceData;
      }
   }

   public List<ServiceHandle<?>> getSubHandles() {
      synchronized (this.lock) {
         return new ArrayList<>(this.subHandles);
      }
   }

   public void pushInjectee(Injectee push) {
      synchronized (this.lock) {
         this.injectees.add(push);
      }
   }

   public void popInjectee() {
      synchronized (this.lock) {
         this.injectees.removeLast();
      }
   }

   public void addSubHandle(ServiceHandleImpl<?> subHandle) {
      synchronized (this.lock) {
         this.subHandles.add(subHandle);
      }
   }

   public Injectee getOriginalRequest() {
      return this.getLastInjectee();
   }

   @Override
   public String toString() {
      return "ServiceHandle(" + this.root + "," + System.identityHashCode(this) + ")";
   }
}
