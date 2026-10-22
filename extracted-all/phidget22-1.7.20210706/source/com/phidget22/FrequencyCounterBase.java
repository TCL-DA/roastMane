package com.phidget22;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class FrequencyCounterBase extends Phidget {
   private List<FrequencyCounterCountChangeListener> countChangeListeners = new ArrayList<>();
   private long nativeCountChangeHandler = 0L;
   private List<FrequencyCounterFrequencyChangeListener> frequencyChangeListeners = new ArrayList<>();
   private long nativeFrequencyChangeHandler = 0L;

   FrequencyCounterBase(long var1) {
      super(var1);
   }

   public FrequencyCounterBase() throws PhidgetException {
      super(create());
   }

   private static native long create() throws PhidgetException;

   private final void setDeviceEvents(boolean var1) {
      this.enableCountChangeEvents(var1 && this.countChangeListeners.size() > 0);
      this.enableFrequencyChangeEvents(var1 && this.frequencyChangeListeners.size() > 0);
   }

   public final void addCountChangeListener(FrequencyCounterCountChangeListener var1) {
      synchronized (this.countChangeListeners) {
         this.countChangeListeners.add(var1);
         this.enableCountChangeEvents(true);
      }
   }

   public final void removeCountChangeListener(FrequencyCounterCountChangeListener var1) {
      synchronized (this.countChangeListeners) {
         this.countChangeListeners.remove(var1);
         this.enableCountChangeEvents(this.countChangeListeners.size() > 0);
      }
   }

   private void fireCountChange(FrequencyCounterCountChangeEvent var1) {
      synchronized (this.countChangeListeners) {
         Iterator var3 = this.countChangeListeners.iterator();

         while (var3.hasNext()) {
            ((FrequencyCounterCountChangeListener)var3.next()).onCountChange(var1);
         }
      }
   }

   private native void enableCountChangeEvents(boolean var1);

   public final void addFrequencyChangeListener(FrequencyCounterFrequencyChangeListener var1) {
      synchronized (this.frequencyChangeListeners) {
         this.frequencyChangeListeners.add(var1);
         this.enableFrequencyChangeEvents(true);
      }
   }

   public final void removeFrequencyChangeListener(FrequencyCounterFrequencyChangeListener var1) {
      synchronized (this.frequencyChangeListeners) {
         this.frequencyChangeListeners.remove(var1);
         this.enableFrequencyChangeEvents(this.frequencyChangeListeners.size() > 0);
      }
   }

   private void fireFrequencyChange(FrequencyCounterFrequencyChangeEvent var1) {
      synchronized (this.frequencyChangeListeners) {
         Iterator var3 = this.frequencyChangeListeners.iterator();

         while (var3.hasNext()) {
            ((FrequencyCounterFrequencyChangeListener)var3.next()).onFrequencyChange(var1);
         }
      }
   }

   private native void enableFrequencyChangeEvents(boolean var1);

   public native long getCount() throws PhidgetException;

   public native boolean getEnabled() throws PhidgetException;

   public native void setEnabled(boolean var1) throws PhidgetException;

   public native int getDataInterval() throws PhidgetException;

   public native void setDataInterval(int var1) throws PhidgetException;

   public native int getMinDataInterval() throws PhidgetException;

   public native int getMaxDataInterval() throws PhidgetException;

   public native FrequencyFilterType getFilterType() throws PhidgetException;

   public native void setFilterType(FrequencyFilterType var1) throws PhidgetException;

   public native double getFrequency() throws PhidgetException;

   public native double getMaxFrequency() throws PhidgetException;

   public native double getFrequencyCutoff() throws PhidgetException;

   public native void setFrequencyCutoff(double var1) throws PhidgetException;

   public native double getMinFrequencyCutoff() throws PhidgetException;

   public native double getMaxFrequencyCutoff() throws PhidgetException;

   public native InputMode getInputMode() throws PhidgetException;

   public native void setInputMode(InputMode var1) throws PhidgetException;

   public native PowerSupply getPowerSupply() throws PhidgetException;

   public native void setPowerSupply(PowerSupply var1) throws PhidgetException;

   public native void reset() throws PhidgetException;

   public native double getTimeElapsed() throws PhidgetException;
}
