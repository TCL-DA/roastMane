package org.eclipse.jetty.util.component;

import java.util.Collection;
import java.util.EventListener;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.eclipse.jetty.util.TypeUtil;
import org.eclipse.jetty.util.Uptime;
import org.eclipse.jetty.util.annotation.ManagedAttribute;
import org.eclipse.jetty.util.annotation.ManagedObject;
import org.eclipse.jetty.util.thread.AutoLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ManagedObject("Abstract Implementation of LifeCycle")
public abstract class AbstractLifeCycle implements LifeCycle {
   private static final Logger LOG = LoggerFactory.getLogger(AbstractLifeCycle.class);
   public static final String STOPPED = AbstractLifeCycle.State.STOPPED.toString();
   public static final String FAILED = AbstractLifeCycle.State.FAILED.toString();
   public static final String STARTING = AbstractLifeCycle.State.STARTING.toString();
   public static final String STARTED = AbstractLifeCycle.State.STARTED.toString();
   public static final String STOPPING = AbstractLifeCycle.State.STOPPING.toString();
   private final List<EventListener> _eventListener = new CopyOnWriteArrayList<>();
   private final AutoLock _lock = new AutoLock();
   private volatile AbstractLifeCycle.State _state = AbstractLifeCycle.State.STOPPED;

   protected void doStart() throws Exception {
   }

   protected void doStop() throws Exception {
   }

   @Override
   public final void start() throws Exception {
      try (AutoLock l = this._lock.lock()) {
         try {
            switch (this._state) {
               case STARTING:
               case STOPPING:
                  throw new IllegalStateException(this.getState());
               case STARTED:
                  return;
               default:
                  try {
                     this.setStarting();
                     this.doStart();
                     this.setStarted();
                  } catch (AbstractLifeCycle.StopException var5) {
                     if (LOG.isDebugEnabled()) {
                        LOG.debug("Unable to stop", var5);
                     }

                     this.setStopping();
                     this.doStop();
                     this.setStopped();
                  }
            }
         } catch (Throwable var6) {
            this.setFailed(var6);
            throw var6;
         }
      }
   }

   @Override
   public final void stop() throws Exception {
      try (AutoLock l = this._lock.lock()) {
         try {
            switch (this._state) {
               case STOPPED:
                  return;
               case STARTING:
               case STOPPING:
                  throw new IllegalStateException(this.getState());
               case STARTED:
            }

            this.setStopping();
            this.doStop();
            this.setStopped();
         } catch (Throwable var5) {
            this.setFailed(var5);
            throw var5;
         }
      }
   }

   @Override
   public boolean isRunning() {
      AbstractLifeCycle.State state = this._state;
      switch (state) {
         case STARTING:
         case STARTED:
            return true;
         default:
            return false;
      }
   }

   @Override
   public boolean isStarted() {
      return this._state == AbstractLifeCycle.State.STARTED;
   }

   @Override
   public boolean isStarting() {
      return this._state == AbstractLifeCycle.State.STARTING;
   }

   @Override
   public boolean isStopping() {
      return this._state == AbstractLifeCycle.State.STOPPING;
   }

   @Override
   public boolean isStopped() {
      return this._state == AbstractLifeCycle.State.STOPPED;
   }

   @Override
   public boolean isFailed() {
      return this._state == AbstractLifeCycle.State.FAILED;
   }

   public List<EventListener> getEventListeners() {
      return this._eventListener;
   }

   public void setEventListeners(Collection<EventListener> eventListeners) {
      for (EventListener l : this._eventListener) {
         if (!eventListeners.contains(l)) {
            this.removeEventListener(l);
         }
      }

      for (EventListener lx : eventListeners) {
         if (!this._eventListener.contains(lx)) {
            this.addEventListener(lx);
         }
      }
   }

   @Override
   public boolean addEventListener(EventListener listener) {
      if (this._eventListener.contains(listener)) {
         return false;
      } else {
         this._eventListener.add(listener);
         return true;
      }
   }

   @Override
   public boolean removeEventListener(EventListener listener) {
      return this._eventListener.remove(listener);
   }

   @ManagedAttribute(
      value = "Lifecycle State for this instance",
      readonly = true
   )
   public String getState() {
      return this._state.toString();
   }

   public static String getState(LifeCycle lc) {
      if (lc instanceof AbstractLifeCycle) {
         return ((AbstractLifeCycle)lc)._state.toString();
      } else if (lc.isStarting()) {
         return AbstractLifeCycle.State.STARTING.toString();
      } else if (lc.isStarted()) {
         return AbstractLifeCycle.State.STARTED.toString();
      } else if (lc.isStopping()) {
         return AbstractLifeCycle.State.STOPPING.toString();
      } else {
         return lc.isStopped() ? AbstractLifeCycle.State.STOPPED.toString() : AbstractLifeCycle.State.FAILED.toString();
      }
   }

   private void setStarted() {
      if (this._state == AbstractLifeCycle.State.STARTING) {
         this._state = AbstractLifeCycle.State.STARTED;
         if (LOG.isDebugEnabled()) {
            LOG.debug("STARTED @{}ms {}", Uptime.getUptime(), this);
         }

         for (EventListener listener : this._eventListener) {
            if (listener instanceof LifeCycle.Listener) {
               ((LifeCycle.Listener)listener).lifeCycleStarted(this);
            }
         }
      }
   }

   private void setStarting() {
      if (LOG.isDebugEnabled()) {
         LOG.debug("STARTING {}", this);
      }

      this._state = AbstractLifeCycle.State.STARTING;

      for (EventListener listener : this._eventListener) {
         if (listener instanceof LifeCycle.Listener) {
            ((LifeCycle.Listener)listener).lifeCycleStarting(this);
         }
      }
   }

   private void setStopping() {
      if (LOG.isDebugEnabled()) {
         LOG.debug("STOPPING {}", this);
      }

      this._state = AbstractLifeCycle.State.STOPPING;

      for (EventListener listener : this._eventListener) {
         if (listener instanceof LifeCycle.Listener) {
            ((LifeCycle.Listener)listener).lifeCycleStopping(this);
         }
      }
   }

   private void setStopped() {
      if (this._state == AbstractLifeCycle.State.STOPPING) {
         this._state = AbstractLifeCycle.State.STOPPED;
         if (LOG.isDebugEnabled()) {
            LOG.debug("STOPPED {}", this);
         }

         for (EventListener listener : this._eventListener) {
            if (listener instanceof LifeCycle.Listener) {
               ((LifeCycle.Listener)listener).lifeCycleStopped(this);
            }
         }
      }
   }

   private void setFailed(Throwable th) {
      this._state = AbstractLifeCycle.State.FAILED;
      if (LOG.isDebugEnabled()) {
         LOG.warn("FAILED {}: {}", new Object[]{this, th, th});
      }

      for (EventListener listener : this._eventListener) {
         if (listener instanceof LifeCycle.Listener) {
            ((LifeCycle.Listener)listener).lifeCycleFailure(this, th);
         }
      }
   }

   @Override
   public String toString() {
      String name = TypeUtil.toShortName(this.getClass());
      return String.format("%s@%x{%s}", name, this.hashCode(), this.getState());
   }

   @Deprecated
   public abstract static class AbstractLifeCycleListener implements LifeCycle.Listener {
      @Override
      public void lifeCycleFailure(LifeCycle event, Throwable cause) {
      }

      @Override
      public void lifeCycleStarted(LifeCycle event) {
      }

      @Override
      public void lifeCycleStarting(LifeCycle event) {
      }

      @Override
      public void lifeCycleStopped(LifeCycle event) {
      }

      @Override
      public void lifeCycleStopping(LifeCycle event) {
      }
   }

   static enum State {
      STOPPED,
      STARTING,
      STARTED,
      STOPPING,
      FAILED;
   }

   public class StopException extends RuntimeException {
   }
}
