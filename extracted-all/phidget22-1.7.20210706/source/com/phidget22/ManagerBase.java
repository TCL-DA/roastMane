package com.phidget22;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ManagerBase {
   long handle = 0L;
   private List<ManagerAttachListener> attachListeners = new ArrayList<>();
   private long nativeAttachHandler = 0L;
   private List<ManagerDetachListener> detachListeners = new ArrayList<>();
   private long nativeDetachHandler = 0L;

   public ManagerBase() throws PhidgetException {
      this.handle = create();
   }

   private static native long create() throws PhidgetException;

   private final void setEvents(boolean var1) {
      this.enableAttachEvents(var1 && this.attachListeners.size() > 0);
      this.enableDetachEvents(var1 && this.detachListeners.size() > 0);
   }

   public final void addAttachListener(ManagerAttachListener var1) {
      synchronized (this.attachListeners) {
         this.attachListeners.add(var1);
         this.enableAttachEvents(true);
      }
   }

   public final void removeAttachListener(ManagerAttachListener var1) {
      synchronized (this.attachListeners) {
         this.attachListeners.remove(var1);
         this.enableAttachEvents(this.attachListeners.size() > 0);
      }
   }

   private void fireAttach(ManagerAttachEvent var1) {
      synchronized (this.attachListeners) {
         Iterator var3 = this.attachListeners.iterator();

         while (var3.hasNext()) {
            ((ManagerAttachListener)var3.next()).onAttach(var1);
         }
      }
   }

   private native void enableAttachEvents(boolean var1);

   public final void addDetachListener(ManagerDetachListener var1) {
      synchronized (this.detachListeners) {
         this.detachListeners.add(var1);
         this.enableDetachEvents(true);
      }
   }

   public final void removeDetachListener(ManagerDetachListener var1) {
      synchronized (this.detachListeners) {
         this.detachListeners.remove(var1);
         this.enableDetachEvents(this.detachListeners.size() > 0);
      }
   }

   private void fireDetach(ManagerDetachEvent var1) {
      synchronized (this.detachListeners) {
         Iterator var3 = this.detachListeners.iterator();

         while (var3.hasNext()) {
            ((ManagerDetachListener)var3.next()).onDetach(var1);
         }
      }
   }

   private native void enableDetachEvents(boolean var1);

   public native void close() throws PhidgetException;

   public native void open() throws PhidgetException;

   static {
      try {
         System.loadLibrary("phidget22");
      } catch (UnsatisfiedLinkError var2) {
      }

      try {
         System.loadLibrary("phidget22java");
      } catch (UnsatisfiedLinkError var3) {
         String var1 = System.getProperty("os.name");
         if (var1.startsWith("Linux")) {
            throw new ExceptionInInitializerError(
               var3.getMessage()
                  + "\nError loading the Phidget C libraries (libphidget22.so and libphidget22java.so).\nMake sure they are available, and add their path to LD_LIBRARY_PATH, or specify with java.library.path."
            );
         } else if (var1.startsWith("Windows")) {
            throw new ExceptionInInitializerError(
               var3.getMessage() + "\nError loading the Phidget C libraries.\nInstall the Windows Phidget22 MSI, or specify with java.library.path."
            );
         } else if (var1.startsWith("Mac")) {
            throw new ExceptionInInitializerError(var3.getMessage() + "\nError loading the Phidget C libraries.\nThe Mac Phidget22 DMG must be installed.");
         } else {
            throw new ExceptionInInitializerError(var3.getMessage() + "\nError loading the Phidget C libraries.");
         }
      }
   }
}
