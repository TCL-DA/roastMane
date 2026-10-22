package com.sun.javafx.font;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Hashtable;

public class Disposer implements Runnable {
   private static final ReferenceQueue queue = new ReferenceQueue();
   private static final Hashtable records = new Hashtable();
   private static Disposer disposerInstance = new Disposer();

   public static WeakReference addRecord(Object var0, DisposerRecord var1) {
      WeakReference var2 = new WeakReference<>(var0, queue);
      records.put(var2, var1);
      return var2;
   }

   @Override
   public void run() {
      while (true) {
         try {
            Reference var1 = queue.remove();
            var1.clear();
            DisposerRecord var2 = (DisposerRecord)records.remove(var1);
            var2.dispose();
            var1 = null;
            Object var5 = null;
         } catch (Exception var3) {
            System.out.println("Exception while removing reference: " + var3);
            var3.printStackTrace();
         }
      }
   }

   static {
      ThreadGroup var0 = Thread.currentThread().getThreadGroup();
      Object var1 = AccessController.doPrivileged(new PrivilegedAction() {
         @Override
         public Object run() {
            ThreadGroup var1 = Thread.currentThread().getThreadGroup();

            for (ThreadGroup var2 = var1; var2 != null; var2 = var2.getParent()) {
               var1 = var2;
            }

            Thread var3 = new Thread(var1, Disposer.disposerInstance, "Prism Font Disposer");
            var3.setContextClassLoader(null);
            var3.setDaemon(true);
            var3.setPriority(10);
            var3.start();
            return null;
         }
      });
   }
}
