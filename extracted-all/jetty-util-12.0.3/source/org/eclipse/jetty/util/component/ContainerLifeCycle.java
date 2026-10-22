package org.eclipse.jetty.util.component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EventListener;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import org.eclipse.jetty.util.ExceptionUtil;
import org.eclipse.jetty.util.annotation.ManagedObject;
import org.eclipse.jetty.util.annotation.ManagedOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ManagedObject("Implementation of Container and LifeCycle")
public class ContainerLifeCycle extends AbstractLifeCycle implements Container, Destroyable, Dumpable.DumpableContainer {
   private static final Logger LOG = LoggerFactory.getLogger(ContainerLifeCycle.class);
   private final List<ContainerLifeCycle.Bean> _beans = new CopyOnWriteArrayList<>();
   private final List<Container.Listener> _listeners = new CopyOnWriteArrayList<>();
   private boolean _doStarted;
   private boolean _destroyed;

   @Override
   protected void doStart() throws Exception {
      if (this._destroyed) {
         throw new IllegalStateException("Destroyed container cannot be restarted");
      } else {
         this._doStarted = true;

         try {
            for (ContainerLifeCycle.Bean b : this._beans) {
               if (!this.isStarting()) {
                  break;
               }

               if (b._bean instanceof LifeCycle l) {
                  switch (b._managed) {
                     case MANAGED:
                        if (l.isStopped() || l.isFailed()) {
                           this.start(l);
                        }
                        break;
                     case AUTO:
                        if (l.isStopped()) {
                           this.manage(b);
                           this.start(l);
                        } else {
                           this.unmanage(b);
                        }
                  }
               }
            }
         } catch (Throwable var8) {
            Throwable th = var8;
            List<ContainerLifeCycle.Bean> reverse = new ArrayList<>(this._beans);
            Collections.reverse(reverse);

            for (ContainerLifeCycle.Bean b : reverse) {
               if (b._bean instanceof LifeCycle && b._managed == ContainerLifeCycle.Managed.MANAGED) {
                  LifeCycle l = (LifeCycle)b._bean;
                  if (l.isRunning()) {
                     try {
                        this.stop(l);
                     } catch (Throwable var7) {
                        if (var7 != th) {
                           th.addSuppressed(var7);
                        }
                     }
                  }
               }
            }

            throw th;
         }
      }
   }

   protected void start(LifeCycle l) throws Exception {
      l.start();
   }

   protected void stop(LifeCycle l) throws Exception {
      l.stop();
   }

   @Override
   protected void doStop() throws Exception {
      this._doStarted = false;
      super.doStop();
      List<ContainerLifeCycle.Bean> reverse = new ArrayList<>(this._beans);
      Collections.reverse(reverse);
      Throwable multiException = null;

      for (ContainerLifeCycle.Bean b : reverse) {
         if (!this.isStopping()) {
            break;
         }

         if (b._managed == ContainerLifeCycle.Managed.MANAGED && b._bean instanceof LifeCycle l) {
            try {
               this.stop(l);
            } catch (Throwable var7) {
               multiException = ExceptionUtil.combine(multiException, var7);
            }
         }
      }

      ExceptionUtil.ifExceptionThrow(multiException);
   }

   @Override
   public void destroy() {
      this._destroyed = true;
      List<ContainerLifeCycle.Bean> reverse = new ArrayList<>(this._beans);
      Collections.reverse(reverse);

      for (ContainerLifeCycle.Bean b : reverse) {
         if (b._bean instanceof Destroyable && (b._managed == ContainerLifeCycle.Managed.MANAGED || b._managed == ContainerLifeCycle.Managed.POJO)) {
            Destroyable d = (Destroyable)b._bean;

            try {
               d.destroy();
            } catch (Throwable var6) {
               LOG.warn("Unable to destroy", var6);
            }
         }
      }

      this._beans.clear();
   }

   public boolean contains(Object bean) {
      for (ContainerLifeCycle.Bean b : this._beans) {
         if (b._bean == bean) {
            return true;
         }
      }

      return false;
   }

   @Override
   public boolean isManaged(Object bean) {
      for (ContainerLifeCycle.Bean b : this._beans) {
         if (b._bean == bean) {
            return b.isManaged();
         }
      }

      return false;
   }

   public boolean isAuto(Object bean) {
      for (ContainerLifeCycle.Bean b : this._beans) {
         if (b._bean == bean) {
            return b._managed == ContainerLifeCycle.Managed.AUTO;
         }
      }

      return false;
   }

   public boolean isUnmanaged(Object bean) {
      for (ContainerLifeCycle.Bean b : this._beans) {
         if (b._bean == bean) {
            return b._managed == ContainerLifeCycle.Managed.UNMANAGED;
         }
      }

      return false;
   }

   @Override
   public boolean addBean(Object o) {
      return o instanceof LifeCycle l
         ? this.addBean(o, l.isRunning() ? ContainerLifeCycle.Managed.UNMANAGED : ContainerLifeCycle.Managed.AUTO)
         : this.addBean(o, ContainerLifeCycle.Managed.POJO);
   }

   @Override
   public boolean addBean(Object o, boolean managed) {
      return o instanceof LifeCycle
         ? this.addBean(o, managed ? ContainerLifeCycle.Managed.MANAGED : ContainerLifeCycle.Managed.UNMANAGED)
         : this.addBean(o, managed ? ContainerLifeCycle.Managed.POJO : ContainerLifeCycle.Managed.UNMANAGED);
   }

   private boolean addBean(Object o, ContainerLifeCycle.Managed managed) {
      if (o != null && !this.contains(o)) {
         ContainerLifeCycle.Bean newBean = new ContainerLifeCycle.Bean(o);
         this._beans.add(newBean);

         for (Container.Listener l : this._listeners) {
            l.beanAdded(this, o);
         }

         if (o instanceof EventListener) {
            this.addEventListener((EventListener)o);
         }

         try {
            switch (managed) {
               case POJO:
                  newBean._managed = ContainerLifeCycle.Managed.POJO;
                  break;
               case MANAGED:
                  this.manage(newBean);
                  if (this.isStarting() && this._doStarted) {
                     LifeCycle lx = (LifeCycle)o;
                     if (!lx.isRunning()) {
                        this.start(lx);
                     }
                  }
                  break;
               case UNMANAGED:
                  this.unmanage(newBean);
                  break;
               case AUTO:
                  if (o instanceof LifeCycle l) {
                     if (this.isStarting()) {
                        if (l.isRunning()) {
                           this.unmanage(newBean);
                        } else if (this._doStarted) {
                           this.manage(newBean);
                           this.start(l);
                        } else {
                           newBean._managed = ContainerLifeCycle.Managed.AUTO;
                        }
                     } else if (this.isStarted()) {
                        this.unmanage(newBean);
                     } else {
                        newBean._managed = ContainerLifeCycle.Managed.AUTO;
                     }
                  } else {
                     newBean._managed = ContainerLifeCycle.Managed.POJO;
                  }
                  break;
               default:
                  throw new IllegalStateException(managed.toString());
            }
         } catch (Error | RuntimeException var6) {
            throw var6;
         } catch (Exception var7) {
            throw new RuntimeException(var7);
         }

         if (LOG.isDebugEnabled()) {
            LOG.debug("{} added {}", this, newBean);
         }

         return true;
      } else {
         return false;
      }
   }

   public void addManaged(LifeCycle lifecycle) {
      this.addBean(lifecycle, true);

      try {
         if (this.isRunning() && !lifecycle.isRunning()) {
            this.start(lifecycle);
         }
      } catch (Error | RuntimeException var3) {
         throw var3;
      } catch (Exception var4) {
         throw new RuntimeException(var4);
      }
   }

   @Override
   public boolean addEventListener(EventListener listener) {
      if (super.addEventListener(listener)) {
         if (!this.contains(listener)) {
            this.addBean(listener);
         }

         if (listener instanceof Container.Listener cl) {
            this._listeners.add(cl);

            for (ContainerLifeCycle.Bean b : this._beans) {
               cl.beanAdded(this, b._bean);
               if (listener instanceof Container.InheritedListener && b.isManaged() && b._bean instanceof Container) {
                  if (b._bean instanceof ContainerLifeCycle) {
                     Container.addBean(b._bean, listener, false);
                  } else {
                     Container.addBean(b._bean, listener);
                  }
               }
            }
         }

         return true;
      } else {
         return false;
      }
   }

   @Override
   public boolean removeEventListener(EventListener listener) {
      if (!super.removeEventListener(listener)) {
         return false;
      } else {
         this.removeBean(listener);
         if (listener instanceof Container.Listener && this._listeners.remove(listener)) {
            Container.Listener cl = (Container.Listener)listener;

            for (ContainerLifeCycle.Bean b : this._beans) {
               cl.beanRemoved(this, b._bean);
               if (listener instanceof Container.InheritedListener && b.isManaged()) {
                  Container.removeBean(b._bean, listener);
               }
            }
         }

         return true;
      }
   }

   @Override
   public void manage(Object bean) {
      for (ContainerLifeCycle.Bean b : this._beans) {
         if (b._bean == bean) {
            this.manage(b);
            return;
         }
      }

      throw new IllegalArgumentException("Unknown bean " + bean);
   }

   private void manage(ContainerLifeCycle.Bean bean) {
      if (bean._managed != ContainerLifeCycle.Managed.MANAGED) {
         bean._managed = ContainerLifeCycle.Managed.MANAGED;
         if (bean._bean instanceof Container) {
            for (Container.Listener l : this._listeners) {
               if (l instanceof Container.InheritedListener) {
                  if (bean._bean instanceof ContainerLifeCycle) {
                     Container.addBean(bean._bean, l, false);
                  } else {
                     Container.addBean(bean._bean, l);
                  }
               }
            }
         }
      }
   }

   @Override
   public void unmanage(Object bean) {
      for (ContainerLifeCycle.Bean b : this._beans) {
         if (b._bean == bean) {
            this.unmanage(b);
            return;
         }
      }

      throw new IllegalArgumentException("Unknown bean " + bean);
   }

   private void unmanage(ContainerLifeCycle.Bean bean) {
      if (bean._managed != ContainerLifeCycle.Managed.UNMANAGED) {
         if (bean._managed == ContainerLifeCycle.Managed.MANAGED && bean._bean instanceof Container) {
            for (Container.Listener l : this._listeners) {
               if (l instanceof Container.InheritedListener) {
                  Container.removeBean(bean._bean, l);
               }
            }
         }

         bean._managed = ContainerLifeCycle.Managed.UNMANAGED;
      }
   }

   public void setBeans(Collection<Object> beans) {
      for (Object bean : beans) {
         this.addBean(bean);
      }
   }

   @Override
   public Collection<Object> getBeans() {
      return this.getBeans(Object.class);
   }

   @Override
   public <T> Collection<T> getBeans(Class<T> clazz) {
      ArrayList<T> beans = null;

      for (ContainerLifeCycle.Bean b : this._beans) {
         if (clazz.isInstance(b._bean)) {
            if (beans == null) {
               beans = new ArrayList<>();
            }

            beans.add(clazz.cast(b._bean));
         }
      }

      return (Collection<T>)(beans == null ? Collections.emptyList() : beans);
   }

   @Override
   public <T> T getBean(Class<T> clazz) {
      for (ContainerLifeCycle.Bean b : this._beans) {
         if (clazz.isInstance(b._bean)) {
            return clazz.cast(b._bean);
         }
      }

      return null;
   }

   private ContainerLifeCycle.Bean getBean(Object o) {
      for (ContainerLifeCycle.Bean b : this._beans) {
         if (b._bean == o) {
            return b;
         }
      }

      return null;
   }

   public void removeBeans() {
      for (ContainerLifeCycle.Bean b : new ArrayList<>(this._beans)) {
         this.remove(b);
      }
   }

   @Override
   public boolean removeBean(Object o) {
      ContainerLifeCycle.Bean b = this.getBean(o);
      return b != null && this.remove(b);
   }

   private boolean remove(ContainerLifeCycle.Bean bean) {
      if (!this._beans.remove(bean)) {
         return false;
      } else {
         boolean wasManaged = bean.isManaged();
         this.unmanage(bean);

         for (Container.Listener l : this._listeners) {
            l.beanRemoved(this, bean._bean);
         }

         if (bean._bean instanceof EventListener && this.getEventListeners().contains(bean._bean)) {
            this.removeEventListener((EventListener)bean._bean);
         }

         if (wasManaged && bean._bean instanceof LifeCycle) {
            try {
               this.stop((LifeCycle)bean._bean);
            } catch (Error | RuntimeException var5) {
               throw var5;
            } catch (Exception var6) {
               throw new RuntimeException(var6);
            }
         }

         return true;
      }
   }

   @ManagedOperation("Dump the object to stderr")
   public void dumpStdErr() {
      try {
         this.dump(System.err, "");
         System.err.println("key: +- bean, += managed, +~ unmanaged, +? auto, +: iterable, +] array, +@ map, +> undefined");
      } catch (IOException var2) {
         LOG.warn("Unable to dump", var2);
      }
   }

   @ManagedOperation("Dump the object to a string")
   @Override
   public String dump() {
      return Dumpable.dump(this);
   }

   @Override
   public void dump(Appendable out, String indent) throws IOException {
      this.dumpObjects(out, indent);
   }

   public void dump(Appendable out) throws IOException {
      this.dump(out, "");
   }

   protected void dumpObjects(Appendable out, String indent, Object... items) throws IOException {
      Dumpable.dumpObjects(out, indent, this, items);
   }

   public void updateBean(Object oldBean, Object newBean) {
      if (newBean != oldBean) {
         if (oldBean != null) {
            this.removeBean(oldBean);
         }

         if (newBean != null) {
            this.addBean(newBean);
         }
      }
   }

   public void updateBean(Object oldBean, Object newBean, boolean managed) {
      if (newBean != oldBean) {
         if (oldBean != null) {
            this.removeBean(oldBean);
         }

         if (newBean != null) {
            this.addBean(newBean, managed);
         }
      }
   }

   public void updateBeans(Object[] oldBeans, Object[] newBeans) {
      this.updateBeans(
         oldBeans == null ? Collections.emptyList() : Arrays.asList(oldBeans), newBeans == null ? Collections.emptyList() : Arrays.asList(newBeans)
      );
   }

   public void updateBeans(Collection<?> oldBeans, Collection<?> newBeans) {
      Objects.requireNonNull(oldBeans);
      Objects.requireNonNull(newBeans);

      for (Object o : oldBeans) {
         if (!newBeans.contains(o)) {
            this.removeBean(o);
         }
      }

      for (Object n : newBeans) {
         if (!oldBeans.contains(n)) {
            this.addBean(n);
         }
      }
   }

   @Override
   public <T> Collection<T> getContainedBeans(Class<T> clazz) {
      Set<T> beans = new HashSet<>();
      this.getContainedBeans(clazz, beans);
      return beans;
   }

   protected <T> void getContainedBeans(Class<T> clazz, Collection<T> beans) {
      beans.addAll(this.getBeans(clazz));

      for (Container c : this.getBeans(Container.class)) {
         ContainerLifeCycle.Bean bean = this.getBean(c);
         if (bean != null && bean.isManageable()) {
            if (c instanceof ContainerLifeCycle) {
               ((ContainerLifeCycle)c).getContainedBeans(clazz, beans);
            } else {
               beans.addAll(c.getContainedBeans(clazz));
            }
         }
      }
   }

   private static class Bean {
      private final Object _bean;
      private volatile ContainerLifeCycle.Managed _managed = ContainerLifeCycle.Managed.POJO;

      private Bean(Object b) {
         if (b == null) {
            throw new NullPointerException();
         } else {
            this._bean = b;
         }
      }

      public boolean isManaged() {
         return this._managed == ContainerLifeCycle.Managed.MANAGED;
      }

      public boolean isManageable() {
         switch (this._managed) {
            case MANAGED:
               return true;
            case AUTO:
               return this._bean instanceof LifeCycle && ((LifeCycle)this._bean).isStopped();
            default:
               return false;
         }
      }

      @Override
      public String toString() {
         return String.format("{%s,%s}", this._bean, this._managed);
      }
   }

   static enum Managed {
      POJO,
      MANAGED,
      UNMANAGED,
      AUTO;
   }
}
