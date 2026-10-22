package com.phidget22;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DigitalInputBase extends Phidget {
   private List<DigitalInputStateChangeListener> stateChangeListeners = new ArrayList<>();
   private long nativeStateChangeHandler = 0L;

   DigitalInputBase(long var1) {
      super(var1);
   }

   public DigitalInputBase() throws PhidgetException {
      super(create());
   }

   private static native long create() throws PhidgetException;

   private final void setDeviceEvents(boolean var1) {
      this.enableStateChangeEvents(var1 && this.stateChangeListeners.size() > 0);
   }

   public final void addStateChangeListener(DigitalInputStateChangeListener var1) {
      synchronized (this.stateChangeListeners) {
         this.stateChangeListeners.add(var1);
         this.enableStateChangeEvents(true);
      }
   }

   public final void removeStateChangeListener(DigitalInputStateChangeListener var1) {
      synchronized (this.stateChangeListeners) {
         this.stateChangeListeners.remove(var1);
         this.enableStateChangeEvents(this.stateChangeListeners.size() > 0);
      }
   }

   private void fireStateChange(DigitalInputStateChangeEvent var1) {
      synchronized (this.stateChangeListeners) {
         Iterator var3 = this.stateChangeListeners.iterator();

         while (var3.hasNext()) {
            ((DigitalInputStateChangeListener)var3.next()).onStateChange(var1);
         }
      }
   }

   private native void enableStateChangeEvents(boolean var1);

   public native InputMode getInputMode() throws PhidgetException;

   public native void setInputMode(InputMode var1) throws PhidgetException;

   public native PowerSupply getPowerSupply() throws PhidgetException;

   public native void setPowerSupply(PowerSupply var1) throws PhidgetException;

   public native boolean getState() throws PhidgetException;
}
