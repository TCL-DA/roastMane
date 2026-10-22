package com.phidget22;

public class VoltageOutputBase extends Phidget {
   VoltageOutputBase(long var1) {
      super(var1);
   }

   public VoltageOutputBase() throws PhidgetException {
      super(create());
   }

   private static native long create() throws PhidgetException;

   private final void setDeviceEvents(boolean var1) {
   }

   public native void setEnabled(boolean var1) throws PhidgetException;

   public native boolean getEnabled() throws PhidgetException;

   public native void enableFailsafe(int var1) throws PhidgetException;

   public native int getMinFailsafeTime() throws PhidgetException;

   public native int getMaxFailsafeTime() throws PhidgetException;

   public native void resetFailsafe() throws PhidgetException;

   public native double getVoltage() throws PhidgetException;

   public native void setVoltage(double var1, AsyncListener var3);

   public native void setVoltage(double var1) throws PhidgetException;

   public native double getMinVoltage() throws PhidgetException;

   public native double getMaxVoltage() throws PhidgetException;

   public native VoltageOutputRange getVoltageOutputRange() throws PhidgetException;

   public native void setVoltageOutputRange(VoltageOutputRange var1) throws PhidgetException;
}
