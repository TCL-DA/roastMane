package com.phidget22;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class VoltageInputBase extends Phidget {
   private List<VoltageInputSensorChangeListener> sensorChangeListeners = new ArrayList<>();
   private long nativeSensorChangeHandler = 0L;
   private List<VoltageInputVoltageChangeListener> voltageChangeListeners = new ArrayList<>();
   private long nativeVoltageChangeHandler = 0L;

   VoltageInputBase(long var1) {
      super(var1);
   }

   public VoltageInputBase() throws PhidgetException {
      super(create());
   }

   private static native long create() throws PhidgetException;

   private final void setDeviceEvents(boolean var1) {
      this.enableSensorChangeEvents(var1 && this.sensorChangeListeners.size() > 0);
      this.enableVoltageChangeEvents(var1 && this.voltageChangeListeners.size() > 0);
   }

   public final void addSensorChangeListener(VoltageInputSensorChangeListener var1) {
      synchronized (this.sensorChangeListeners) {
         this.sensorChangeListeners.add(var1);
         this.enableSensorChangeEvents(true);
      }
   }

   public final void removeSensorChangeListener(VoltageInputSensorChangeListener var1) {
      synchronized (this.sensorChangeListeners) {
         this.sensorChangeListeners.remove(var1);
         this.enableSensorChangeEvents(this.sensorChangeListeners.size() > 0);
      }
   }

   private void fireSensorChange(VoltageInputSensorChangeEvent var1) {
      synchronized (this.sensorChangeListeners) {
         Iterator var3 = this.sensorChangeListeners.iterator();

         while (var3.hasNext()) {
            ((VoltageInputSensorChangeListener)var3.next()).onSensorChange(var1);
         }
      }
   }

   private native void enableSensorChangeEvents(boolean var1);

   public final void addVoltageChangeListener(VoltageInputVoltageChangeListener var1) {
      synchronized (this.voltageChangeListeners) {
         this.voltageChangeListeners.add(var1);
         this.enableVoltageChangeEvents(true);
      }
   }

   public final void removeVoltageChangeListener(VoltageInputVoltageChangeListener var1) {
      synchronized (this.voltageChangeListeners) {
         this.voltageChangeListeners.remove(var1);
         this.enableVoltageChangeEvents(this.voltageChangeListeners.size() > 0);
      }
   }

   private void fireVoltageChange(VoltageInputVoltageChangeEvent var1) {
      synchronized (this.voltageChangeListeners) {
         Iterator var3 = this.voltageChangeListeners.iterator();

         while (var3.hasNext()) {
            ((VoltageInputVoltageChangeListener)var3.next()).onVoltageChange(var1);
         }
      }
   }

   private native void enableVoltageChangeEvents(boolean var1);

   public native int getDataInterval() throws PhidgetException;

   public native void setDataInterval(int var1) throws PhidgetException;

   public native int getMinDataInterval() throws PhidgetException;

   public native int getMaxDataInterval() throws PhidgetException;

   public native PowerSupply getPowerSupply() throws PhidgetException;

   public native void setPowerSupply(PowerSupply var1) throws PhidgetException;

   public native VoltageSensorType getSensorType() throws PhidgetException;

   public native void setSensorType(VoltageSensorType var1) throws PhidgetException;

   public native UnitInfo getSensorUnit() throws PhidgetException;

   public native double getSensorValue() throws PhidgetException;

   public native double getSensorValueChangeTrigger() throws PhidgetException;

   public native void setSensorValueChangeTrigger(double var1) throws PhidgetException;

   public native double getVoltage() throws PhidgetException;

   public native double getMinVoltage() throws PhidgetException;

   public native double getMaxVoltage() throws PhidgetException;

   public native double getVoltageChangeTrigger() throws PhidgetException;

   public native void setVoltageChangeTrigger(double var1) throws PhidgetException;

   public native double getMinVoltageChangeTrigger() throws PhidgetException;

   public native double getMaxVoltageChangeTrigger() throws PhidgetException;

   public native VoltageRange getVoltageRange() throws PhidgetException;

   public native void setVoltageRange(VoltageRange var1) throws PhidgetException;
}
