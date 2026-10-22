package com.phidget22;

public class PowerGuardBase extends Phidget {
   PowerGuardBase(long var1) {
      super(var1);
   }

   public PowerGuardBase() throws PhidgetException {
      super(create());
   }

   private static native long create() throws PhidgetException;

   private final void setDeviceEvents(boolean var1) {
   }

   public native void enableFailsafe(int var1) throws PhidgetException;

   public native int getMinFailsafeTime() throws PhidgetException;

   public native int getMaxFailsafeTime() throws PhidgetException;

   public native FanMode getFanMode() throws PhidgetException;

   public native void setFanMode(FanMode var1) throws PhidgetException;

   public native double getOverVoltage() throws PhidgetException;

   public native void setOverVoltage(double var1) throws PhidgetException;

   public native double getMinOverVoltage() throws PhidgetException;

   public native double getMaxOverVoltage() throws PhidgetException;

   public native boolean getPowerEnabled() throws PhidgetException;

   public native void setPowerEnabled(boolean var1) throws PhidgetException;

   public native void resetFailsafe() throws PhidgetException;
}
