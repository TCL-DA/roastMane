package com.phidget22;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class NetBase {
   private List<NetServerAddedListener> serverAddedListeners = new ArrayList<>();
   private long nativeServerAddedHandler = 0L;
   private List<NetServerRemovedListener> serverRemovedListeners = new ArrayList<>();
   private long nativeServerRemovedHandler = 0L;
   public static final int AUTHREQUIRED = 1;

   public NetBase() throws PhidgetException {
   }

   private final void setEvents(boolean var1) {
      this.enableServerAddedEvents(var1 && this.serverAddedListeners.size() > 0);
      this.enableServerRemovedEvents(var1 && this.serverRemovedListeners.size() > 0);
   }

   public final void addServerAddedListener(NetServerAddedListener var1) {
      synchronized (this.serverAddedListeners) {
         this.serverAddedListeners.add(var1);
         this.enableServerAddedEvents(true);
      }
   }

   public final void removeServerAddedListener(NetServerAddedListener var1) {
      synchronized (this.serverAddedListeners) {
         this.serverAddedListeners.remove(var1);
         this.enableServerAddedEvents(this.serverAddedListeners.size() > 0);
      }
   }

   private void fireServerAdded(NetServerAddedEvent var1) {
      synchronized (this.serverAddedListeners) {
         Iterator var3 = this.serverAddedListeners.iterator();

         while (var3.hasNext()) {
            ((NetServerAddedListener)var3.next()).onServerAdded(var1);
         }
      }
   }

   private native void enableServerAddedEvents(boolean var1);

   public final void addServerRemovedListener(NetServerRemovedListener var1) {
      synchronized (this.serverRemovedListeners) {
         this.serverRemovedListeners.add(var1);
         this.enableServerRemovedEvents(true);
      }
   }

   public final void removeServerRemovedListener(NetServerRemovedListener var1) {
      synchronized (this.serverRemovedListeners) {
         this.serverRemovedListeners.remove(var1);
         this.enableServerRemovedEvents(this.serverRemovedListeners.size() > 0);
      }
   }

   private void fireServerRemoved(NetServerRemovedEvent var1) {
      synchronized (this.serverRemovedListeners) {
         Iterator var3 = this.serverRemovedListeners.iterator();

         while (var3.hasNext()) {
            ((NetServerRemovedListener)var3.next()).onServerRemoved(var1);
         }
      }
   }

   private native void enableServerRemovedEvents(boolean var1);

   public static native void addServer(String var0, String var1, int var2, String var3, int var4) throws PhidgetException;

   public static native void removeServer(String var0) throws PhidgetException;

   public static native void enableServer(String var0) throws PhidgetException;

   public static native void disableServer(String var0, int var1) throws PhidgetException;

   public static native void enableServerDiscovery(ServerType var0) throws PhidgetException;

   public static native void disableServerDiscovery(ServerType var0) throws PhidgetException;

   public static native void setServerPassword(String var0, String var1) throws PhidgetException;

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
