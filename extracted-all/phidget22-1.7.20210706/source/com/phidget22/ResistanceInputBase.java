package com.phidget22;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ResistanceInputBase extends Phidget {
   private List<ResistanceInputResistanceChangeListener> resistanceChangeListeners = new ArrayList<>();
   private long nativeResistanceChangeHandler = 0L;

   ResistanceInputBase(long var1) {
      super(var1);
   }

   public ResistanceInputBase() throws PhidgetException {
      super(create());
   }

   private static native long create() throws PhidgetException;

   private final void setDeviceEvents(boolean var1) {
      this.enableResistanceChangeEvents(var1 && this.resistanceChangeListeners.size() > 0);
   }

   public final void addResistanceChangeListener(ResistanceInputResistanceChangeListener var1) {
      synchronized (this.resistanceChangeListeners) {
         this.resistanceChangeListeners.add(var1);
         this.enableResistanceChangeEvents(true);
      }
   }

   public final void removeResistanceChangeListener(ResistanceInputResistanceChangeListener var1) {
      synchronized (this.resistanceChangeListeners) {
         this.resistanceChangeListeners.remove(var1);
         this.enableResistanceChangeEvents(this.resistanceChangeListeners.size() > 0);
      }
   }

   private void fireResistanceChange(ResistanceInputResistanceChangeEvent var1) {
      synchronized (this.resistanceChangeListeners) {
         Iterator var3 = this.resistanceChangeListeners.iterator();

         while (var3.hasNext()) {
            ((ResistanceInputResistanceChangeListener)var3.next()).onResistanceChange(var1);
         }
      }
   }

   private native void enableResistanceChangeEvents(boolean var1);

   public native int getDataInterval() throws PhidgetException;

   public native void setDataInterval(int var1) throws PhidgetException;

   public native int getMinDataInterval() throws PhidgetException;

   public native int getMaxDataInterval() throws PhidgetException;

   public native double getResistance() throws PhidgetException;

   public native double getMinResistance() throws PhidgetException;

   public native double getMaxResistance() throws PhidgetException;

   public native double getResistanceChangeTrigger() throws PhidgetException;

   public native void setResistanceChangeTrigger(double var1) throws PhidgetException;

   public native double getMinResistanceChangeTrigger() throws PhidgetException;

   public native double getMaxResistanceChangeTrigger() throws PhidgetException;

   public native RTDWireSetup getRTDWireSetup() throws PhidgetException;

   public native void setRTDWireSetup(RTDWireSetup var1) throws PhidgetException;
}
