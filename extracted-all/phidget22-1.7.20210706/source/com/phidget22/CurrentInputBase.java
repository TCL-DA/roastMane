package com.phidget22;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CurrentInputBase extends Phidget {
   private List<CurrentInputCurrentChangeListener> currentChangeListeners = new ArrayList<>();
   private long nativeCurrentChangeHandler = 0L;

   CurrentInputBase(long var1) {
      super(var1);
   }

   public CurrentInputBase() throws PhidgetException {
      super(create());
   }

   private static native long create() throws PhidgetException;

   private final void setDeviceEvents(boolean var1) {
      this.enableCurrentChangeEvents(var1 && this.currentChangeListeners.size() > 0);
   }

   public final void addCurrentChangeListener(CurrentInputCurrentChangeListener var1) {
      synchronized (this.currentChangeListeners) {
         this.currentChangeListeners.add(var1);
         this.enableCurrentChangeEvents(true);
      }
   }

   public final void removeCurrentChangeListener(CurrentInputCurrentChangeListener var1) {
      synchronized (this.currentChangeListeners) {
         this.currentChangeListeners.remove(var1);
         this.enableCurrentChangeEvents(this.currentChangeListeners.size() > 0);
      }
   }

   private void fireCurrentChange(CurrentInputCurrentChangeEvent var1) {
      synchronized (this.currentChangeListeners) {
         Iterator var3 = this.currentChangeListeners.iterator();

         while (var3.hasNext()) {
            ((CurrentInputCurrentChangeListener)var3.next()).onCurrentChange(var1);
         }
      }
   }

   private native void enableCurrentChangeEvents(boolean var1);

   public native double getCurrent() throws PhidgetException;

   public native double getMinCurrent() throws PhidgetException;

   public native double getMaxCurrent() throws PhidgetException;

   public native double getCurrentChangeTrigger() throws PhidgetException;

   public native void setCurrentChangeTrigger(double var1) throws PhidgetException;

   public native double getMinCurrentChangeTrigger() throws PhidgetException;

   public native double getMaxCurrentChangeTrigger() throws PhidgetException;

   public native int getDataInterval() throws PhidgetException;

   public native void setDataInterval(int var1) throws PhidgetException;

   public native int getMinDataInterval() throws PhidgetException;

   public native int getMaxDataInterval() throws PhidgetException;

   public native PowerSupply getPowerSupply() throws PhidgetException;

   public native void setPowerSupply(PowerSupply var1) throws PhidgetException;
}
