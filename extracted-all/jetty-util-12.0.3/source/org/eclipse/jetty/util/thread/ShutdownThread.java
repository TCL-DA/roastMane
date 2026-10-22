package org.eclipse.jetty.util.thread;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.eclipse.jetty.util.component.Destroyable;
import org.eclipse.jetty.util.component.LifeCycle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShutdownThread extends Thread {
   private static final Logger LOG = LoggerFactory.getLogger(ShutdownThread.class);
   private static final ShutdownThread _thread = PrivilegedThreadFactory.newThread(ShutdownThread::new);
   private final AutoLock _lock = new AutoLock();
   private boolean _hooked;
   private final List<LifeCycle> _lifeCycles = new CopyOnWriteArrayList<>();

   private ShutdownThread() {
      super("JettyShutdownThread");
   }

   private void hook() {
      try (AutoLock l = this._lock.lock()) {
         if (!this._hooked) {
            Runtime.getRuntime().addShutdownHook(this);
         }

         this._hooked = true;
      } catch (Exception var6) {
         LOG.trace("IGNORED", var6);
         LOG.info("shutdown already commenced");
      }
   }

   private void unhook() {
      try (AutoLock l = this._lock.lock()) {
         this._hooked = false;
         Runtime.getRuntime().removeShutdownHook(this);
      } catch (Exception var6) {
         LOG.trace("IGNORED", var6);
         LOG.debug("shutdown already commenced");
      }
   }

   public static ShutdownThread getInstance() {
      return _thread;
   }

   public static void register(LifeCycle... lifeCycles) {
      try (AutoLock l = _thread._lock.lock()) {
         _thread._lifeCycles.addAll(Arrays.asList(lifeCycles));
         if (_thread._lifeCycles.size() > 0) {
            _thread.hook();
         }
      }
   }

   public static void register(int index, LifeCycle... lifeCycles) {
      try (AutoLock l = _thread._lock.lock()) {
         _thread._lifeCycles.addAll(index, Arrays.asList(lifeCycles));
         if (_thread._lifeCycles.size() > 0) {
            _thread.hook();
         }
      }
   }

   public static void deregister(LifeCycle lifeCycle) {
      try (AutoLock l = _thread._lock.lock()) {
         _thread._lifeCycles.remove(lifeCycle);
         if (_thread._lifeCycles.size() == 0) {
            _thread.unhook();
         }
      }
   }

   public static boolean isRegistered(LifeCycle lifeCycle) {
      boolean var2;
      try (AutoLock l = _thread._lock.lock()) {
         var2 = _thread._lifeCycles.contains(lifeCycle);
      }

      return var2;
   }

   @Override
   public void run() {
      for (LifeCycle lifeCycle : _thread._lifeCycles) {
         try {
            if (lifeCycle.isStarted()) {
               lifeCycle.stop();
               LOG.debug("Stopped {}", lifeCycle);
            }

            if (lifeCycle instanceof Destroyable) {
               ((Destroyable)lifeCycle).destroy();
               LOG.debug("Destroyed {}", lifeCycle);
            }
         } catch (Exception var4) {
            LOG.debug("Unable to stop", var4);
         }
      }
   }
}
