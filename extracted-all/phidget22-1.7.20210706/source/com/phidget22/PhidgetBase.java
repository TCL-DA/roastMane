package com.phidget22;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PhidgetBase {
   long handle;
   private List<AttachListener> attachListeners = new ArrayList<>();
   private long nativeAttachHandler = 0L;
   private List<DetachListener> detachListeners = new ArrayList<>();
   private long nativeDetachHandler = 0L;
   private List<ErrorListener> errorListeners = new ArrayList<>();
   private long nativeErrorHandler = 0L;
   private List<PropertyChangeListener> propertyChangeListeners = new ArrayList<>();
   private long nativePropertyChangeHandler = 0L;
   public static final int ANY_SERIAL_NUMBER = -1;
   public static final int ANY_HUB_PORT = -1;
   public static final int ANY_CHANNEL = -1;
   public static final String ANY_LABEL;
   public static final int INFINITE_TIMEOUT = 0;
   public static final int DEFAULT_TIMEOUT = 1000;

   PhidgetBase(long var1) {
      this.handle = var1;
   }

   public PhidgetBase() throws PhidgetException {
   }

   private final void setEvents(boolean var1) {
      this.enableAttachEvents(var1 && this.attachListeners.size() > 0);
      this.enableDetachEvents(var1 && this.detachListeners.size() > 0);
      this.enableErrorEvents(var1 && this.errorListeners.size() > 0);
      this.enablePropertyChangeEvents(var1 && this.propertyChangeListeners.size() > 0);
   }

   public final void addAttachListener(AttachListener var1) {
      synchronized (this.attachListeners) {
         this.attachListeners.add(var1);
         this.enableAttachEvents(true);
      }
   }

   public final void removeAttachListener(AttachListener var1) {
      synchronized (this.attachListeners) {
         this.attachListeners.remove(var1);
         this.enableAttachEvents(this.attachListeners.size() > 0);
      }
   }

   private void fireAttach(AttachEvent var1) {
      synchronized (this.attachListeners) {
         Iterator var3 = this.attachListeners.iterator();

         while (var3.hasNext()) {
            ((AttachListener)var3.next()).onAttach(var1);
         }
      }
   }

   private native void enableAttachEvents(boolean var1);

   public final void addDetachListener(DetachListener var1) {
      synchronized (this.detachListeners) {
         this.detachListeners.add(var1);
         this.enableDetachEvents(true);
      }
   }

   public final void removeDetachListener(DetachListener var1) {
      synchronized (this.detachListeners) {
         this.detachListeners.remove(var1);
         this.enableDetachEvents(this.detachListeners.size() > 0);
      }
   }

   private void fireDetach(DetachEvent var1) {
      synchronized (this.detachListeners) {
         Iterator var3 = this.detachListeners.iterator();

         while (var3.hasNext()) {
            ((DetachListener)var3.next()).onDetach(var1);
         }
      }
   }

   private native void enableDetachEvents(boolean var1);

   public final void addErrorListener(ErrorListener var1) {
      synchronized (this.errorListeners) {
         this.errorListeners.add(var1);
         this.enableErrorEvents(true);
      }
   }

   public final void removeErrorListener(ErrorListener var1) {
      synchronized (this.errorListeners) {
         this.errorListeners.remove(var1);
         this.enableErrorEvents(this.errorListeners.size() > 0);
      }
   }

   private void fireError(ErrorEvent var1) {
      synchronized (this.errorListeners) {
         Iterator var3 = this.errorListeners.iterator();

         while (var3.hasNext()) {
            ((ErrorListener)var3.next()).onError(var1);
         }
      }
   }

   private native void enableErrorEvents(boolean var1);

   public final void addPropertyChangeListener(PropertyChangeListener var1) {
      synchronized (this.propertyChangeListeners) {
         this.propertyChangeListeners.add(var1);
         this.enablePropertyChangeEvents(true);
      }
   }

   public final void removePropertyChangeListener(PropertyChangeListener var1) {
      synchronized (this.propertyChangeListeners) {
         this.propertyChangeListeners.remove(var1);
         this.enablePropertyChangeEvents(this.propertyChangeListeners.size() > 0);
      }
   }

   private void firePropertyChange(PropertyChangeEvent var1) {
      synchronized (this.propertyChangeListeners) {
         Iterator var3 = this.propertyChangeListeners.iterator();

         while (var3.hasNext()) {
            ((PropertyChangeListener)var3.next()).onPropertyChange(var1);
         }
      }
   }

   private native void enablePropertyChangeEvents(boolean var1);

   public static native void finalize(int var0) throws PhidgetException;

   public static native String getLibraryVersion() throws PhidgetException;

   public static native String getLibraryVersionNumber() throws PhidgetException;

   public static native void resetLibrary() throws PhidgetException;

   public native boolean getAttached() throws PhidgetException;

   public native int getChannel() throws PhidgetException;

   public native void setChannel(int var1) throws PhidgetException;

   public native ChannelClass getChannelClass() throws PhidgetException;

   public native String getChannelClassName() throws PhidgetException;

   public native String getChannelName() throws PhidgetException;

   public native ChannelSubclass getChannelSubclass() throws PhidgetException;

   public native void close() throws PhidgetException;

   public native int getDeviceChannelCount(ChannelClass var1) throws PhidgetException;

   public native DeviceClass getDeviceClass() throws PhidgetException;

   public native String getDeviceClassName() throws PhidgetException;

   public native DeviceID getDeviceID() throws PhidgetException;

   public native String getDeviceLabel() throws PhidgetException;

   public native void setDeviceLabel(String var1) throws PhidgetException;

   public native String getDeviceName() throws PhidgetException;

   public native int getDeviceSerialNumber() throws PhidgetException;

   public native void setDeviceSerialNumber(int var1) throws PhidgetException;

   public native String getDeviceSKU() throws PhidgetException;

   public native int getDeviceVersion() throws PhidgetException;

   public native Phidget getHub() throws PhidgetException;

   public native int getHubPort() throws PhidgetException;

   public native void setHubPort(int var1) throws PhidgetException;

   public native int getHubPortCount() throws PhidgetException;

   public native boolean getIsChannel() throws PhidgetException;

   public native boolean getIsHubPortDevice() throws PhidgetException;

   public native void setIsHubPortDevice(boolean var1) throws PhidgetException;

   public native boolean getIsLocal() throws PhidgetException;

   public native void setIsLocal(boolean var1) throws PhidgetException;

   public native boolean getIsRemote() throws PhidgetException;

   public native void setIsRemote(boolean var1) throws PhidgetException;

   public native void open(int var1) throws PhidgetException;

   public native void open() throws PhidgetException;

   public native Phidget getParent() throws PhidgetException;

   public native String getServerHostname() throws PhidgetException;

   public native String getServerName() throws PhidgetException;

   public native void setServerName(String var1) throws PhidgetException;

   public native String getServerPeerName() throws PhidgetException;

   public native String getServerUniqueName() throws PhidgetException;

   public native void writeDeviceLabel(String var1) throws PhidgetException;

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
         }

         if (var1.startsWith("Windows")) {
            throw new ExceptionInInitializerError(
               var3.getMessage() + "\nError loading the Phidget C libraries.\nInstall the Windows Phidget22 MSI, or specify with java.library.path."
            );
         }

         if (var1.startsWith("Mac")) {
            throw new ExceptionInInitializerError(var3.getMessage() + "\nError loading the Phidget C libraries.\nThe Mac Phidget22 DMG must be installed.");
         }

         throw new ExceptionInInitializerError(var3.getMessage() + "\nError loading the Phidget C libraries.");
      }

      ANY_LABEL = null;
   }
}
