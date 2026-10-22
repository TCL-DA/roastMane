package com.phidget22;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class RFIDBase extends Phidget {
   private List<RFIDTagListener> tagListeners = new ArrayList<>();
   private long nativeTagHandler = 0L;
   private List<RFIDTagLostListener> tagLostListeners = new ArrayList<>();
   private long nativeTagLostHandler = 0L;

   RFIDBase(long var1) {
      super(var1);
   }

   public RFIDBase() throws PhidgetException {
      super(create());
   }

   private static native long create() throws PhidgetException;

   private final void setDeviceEvents(boolean var1) {
      this.enableTagEvents(var1 && this.tagListeners.size() > 0);
      this.enableTagLostEvents(var1 && this.tagLostListeners.size() > 0);
   }

   public final void addTagListener(RFIDTagListener var1) {
      synchronized (this.tagListeners) {
         this.tagListeners.add(var1);
         this.enableTagEvents(true);
      }
   }

   public final void removeTagListener(RFIDTagListener var1) {
      synchronized (this.tagListeners) {
         this.tagListeners.remove(var1);
         this.enableTagEvents(this.tagListeners.size() > 0);
      }
   }

   private void fireTag(RFIDTagEvent var1) {
      synchronized (this.tagListeners) {
         Iterator var3 = this.tagListeners.iterator();

         while (var3.hasNext()) {
            ((RFIDTagListener)var3.next()).onTag(var1);
         }
      }
   }

   private native void enableTagEvents(boolean var1);

   public final void addTagLostListener(RFIDTagLostListener var1) {
      synchronized (this.tagLostListeners) {
         this.tagLostListeners.add(var1);
         this.enableTagLostEvents(true);
      }
   }

   public final void removeTagLostListener(RFIDTagLostListener var1) {
      synchronized (this.tagLostListeners) {
         this.tagLostListeners.remove(var1);
         this.enableTagLostEvents(this.tagLostListeners.size() > 0);
      }
   }

   private void fireTagLost(RFIDTagLostEvent var1) {
      synchronized (this.tagLostListeners) {
         Iterator var3 = this.tagLostListeners.iterator();

         while (var3.hasNext()) {
            ((RFIDTagLostListener)var3.next()).onTagLost(var1);
         }
      }
   }

   private native void enableTagLostEvents(boolean var1);

   public native boolean getAntennaEnabled() throws PhidgetException;

   public native void setAntennaEnabled(boolean var1) throws PhidgetException;

   public native RFIDTag getLastTag() throws PhidgetException;

   public native boolean getTagPresent() throws PhidgetException;

   public native void write(String var1, RFIDProtocol var2, boolean var3) throws PhidgetException;
}
