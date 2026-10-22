package org.glassfish.hk2.internal;

import jakarta.inject.Singleton;
import java.lang.annotation.Annotation;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashMap;
import org.glassfish.hk2.api.ActiveDescriptor;
import org.glassfish.hk2.api.Context;
import org.glassfish.hk2.api.DescriptorVisibility;
import org.glassfish.hk2.api.PerThread;
import org.glassfish.hk2.api.ServiceHandle;
import org.glassfish.hk2.api.Visibility;
import org.glassfish.hk2.utilities.CleanerFactory;
import org.glassfish.hk2.utilities.general.Hk2ThreadLocal;
import org.glassfish.hk2.utilities.reflection.Logger;

@Singleton
@Visibility(DescriptorVisibility.LOCAL)
public class PerThreadContext implements Context<PerThread> {
   private static final boolean LOG_THREAD_DESTRUCTION = AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
      public Boolean run() {
         return Boolean.parseBoolean(System.getProperty("org.hk2.debug.perthreadcontext.log", "false"));
      }
   });
   private final Hk2ThreadLocal<PerThreadContext.PerContextThreadWrapper> threadMap = new Hk2ThreadLocal<PerThreadContext.PerContextThreadWrapper>() {
      public PerThreadContext.PerContextThreadWrapper initialValue() {
         return new PerThreadContext.PerContextThreadWrapper();
      }
   };

   @Override
   public Class<? extends Annotation> getScope() {
      return PerThread.class;
   }

   @Override
   public <U> U findOrCreate(ActiveDescriptor<U> activeDescriptor, ServiceHandle<?> root) {
      U retVal = (U)((PerThreadContext.PerContextThreadWrapper)this.threadMap.get()).get(activeDescriptor);
      if (retVal == null) {
         retVal = activeDescriptor.create(root);
         ((PerThreadContext.PerContextThreadWrapper)this.threadMap.get()).put(activeDescriptor, retVal);
      }

      return retVal;
   }

   @Override
   public boolean containsKey(ActiveDescriptor<?> descriptor) {
      return ((PerThreadContext.PerContextThreadWrapper)this.threadMap.get()).has(descriptor);
   }

   @Override
   public boolean isActive() {
      return true;
   }

   @Override
   public boolean supportsNullCreation() {
      return false;
   }

   @Override
   public void shutdown() {
      this.threadMap.removeAll();
   }

   @Override
   public void destroyOne(ActiveDescriptor<?> descriptor) {
   }

   private static class PerContextThreadWrapper {
      private final HashMap<ActiveDescriptor<?>, Object> instances = new HashMap<>();
      private final long id = Thread.currentThread().getId();

      public PerContextThreadWrapper() {
         this.registerStopEvent();
      }

      public boolean has(ActiveDescriptor<?> d) {
         return this.instances.containsKey(d);
      }

      public Object get(ActiveDescriptor<?> d) {
         return this.instances.get(d);
      }

      public void put(ActiveDescriptor<?> d, Object v) {
         this.instances.put(d, v);
      }

      public final void registerStopEvent() {
         CleanerFactory.create().register(this, () -> {
            this.instances.clear();
            if (PerThreadContext.LOG_THREAD_DESTRUCTION) {
               Logger.getLogger().debug("Removing PerThreadContext data for thread " + this.id);
            }
         });
      }
   }
}
