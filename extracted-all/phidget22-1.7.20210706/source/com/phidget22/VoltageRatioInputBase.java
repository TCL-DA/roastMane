package com.phidget22;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class VoltageRatioInputBase extends Phidget {
   private List<VoltageRatioInputSensorChangeListener> sensorChangeListeners = new ArrayList<>();
   private long nativeSensorChangeHandler = 0L;
   private List<VoltageRatioInputVoltageRatioChangeListener> voltageRatioChangeListeners = new ArrayList<>();
   private long nativeVoltageRatioChangeHandler = 0L;

   VoltageRatioInputBase(long var1) {
      super(var1);
   }

   public VoltageRatioInputBase() throws PhidgetException {
      super(create());
   }

   private static native long create() throws PhidgetException;

   private final void setDeviceEvents(boolean var1) {
      this.enableSensorChangeEvents(var1 && this.sensorChangeListeners.size() > 0);
      this.enableVoltageRatioChangeEvents(var1 && this.voltageRatioChangeListeners.size() > 0);
   }

   public final void addSensorChangeListener(VoltageRatioInputSensorChangeListener var1) {
      synchronized (this.sensorChangeListeners) {
         this.sensorChangeListeners.add(var1);
         this.enableSensorChangeEvents(true);
      }
   }

   public final void removeSensorChangeListener(VoltageRatioInputSensorChangeListener var1) {
      synchronized (this.sensorChangeListeners) {
         this.sensorChangeListeners.remove(var1);
         this.enableSensorChangeEvents(this.sensorChangeListeners.size() > 0);
      }
   }

   private void fireSensorChange(VoltageRatioInputSensorChangeEvent var1) {
      synchronized (this.sensorChangeListeners) {
         Iterator var3 = this.sensorChangeListeners.iterator();

         while (var3.hasNext()) {
            ((VoltageRatioInputSensorChangeListener)var3.next()).onSensorChange(var1);
         }
      }
   }

   private native void enableSensorChangeEvents(boolean var1);

   public final void addVoltageRatioChangeListener(VoltageRatioInputVoltageRatioChangeListener var1) {
      synchronized (this.voltageRatioChangeListeners) {
         this.voltageRatioChangeListeners.add(var1);
         this.enableVoltageRatioChangeEvents(true);
      }
   }

   public final void removeVoltageRatioChangeListener(VoltageRatioInputVoltageRatioChangeListener var1) {
      synchronized (this.voltageRatioChangeListeners) {
         this.voltageRatioChangeListeners.remove(var1);
         this.enableVoltageRatioChangeEvents(this.voltageRatioChangeListeners.size() > 0);
      }
   }

   private void fireVoltageRatioChange(VoltageRatioInputVoltageRatioChangeEvent var1) {
      synchronized (this.voltageRatioChangeListeners) {
         Iterator var3 = this.voltageRatioChangeListeners.iterator();

         while (var3.hasNext()) {
            ((VoltageRatioInputVoltageRatioChangeListener)var3.next()).onVoltageRatioChange(var1);
         }
      }
   }

   private native void enableVoltageRatioChangeEvents(boolean var1);

   public native boolean getBridgeEnabled() throws PhidgetException;

   public native void setBridgeEnabled(boolean var1) throws PhidgetException;

   public native BridgeGain getBridgeGain() throws PhidgetException;

   public native void setBridgeGain(BridgeGain var1) throws PhidgetException;

   public native int getDataInterval() throws PhidgetException;

   public native void setDataInterval(int var1) throws PhidgetException;

   public native int getMinDataInterval() throws PhidgetException;

   public native int getMaxDataInterval() throws PhidgetException;

   public native VoltageRatioSensorType getSensorType() throws PhidgetException;

   public native void setSensorType(VoltageRatioSensorType var1) throws PhidgetException;

   public native UnitInfo getSensorUnit() throws PhidgetException;

   public native double getSensorValue() throws PhidgetException;

   public native double getSensorValueChangeTrigger() throws PhidgetException;

   public native void setSensorValueChangeTrigger(double var1) throws PhidgetException;

   public native double getVoltageRatio() throws PhidgetException;

   public native double getMinVoltageRatio() throws PhidgetException;

   public native double getMaxVoltageRatio() throws PhidgetException;

   public native double getVoltageRatioChangeTrigger() throws PhidgetException;

   public native void setVoltageRatioChangeTrigger(double var1) throws PhidgetException;

   public native double getMinVoltageRatioChangeTrigger() throws PhidgetException;

   public native double getMaxVoltageRatioChangeTrigger() throws PhidgetException;
}
