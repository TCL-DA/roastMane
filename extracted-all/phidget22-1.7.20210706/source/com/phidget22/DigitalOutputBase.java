package com.phidget22;

public class DigitalOutputBase extends Phidget {
   DigitalOutputBase(long var1) {
      super(var1);
   }

   public DigitalOutputBase() throws PhidgetException {
      super(create());
   }

   private static native long create() throws PhidgetException;

   private final void setDeviceEvents(boolean var1) {
   }

   public native double getDutyCycle() throws PhidgetException;

   public native void setDutyCycle(double var1, AsyncListener var3);

   public native void setDutyCycle(double var1) throws PhidgetException;

   public native double getMinDutyCycle() throws PhidgetException;

   public native double getMaxDutyCycle() throws PhidgetException;

   public native void enableFailsafe(int var1) throws PhidgetException;

   public native int getMinFailsafeTime() throws PhidgetException;

   public native int getMaxFailsafeTime() throws PhidgetException;

   public native double getFrequency() throws PhidgetException;

   public native void setFrequency(double var1) throws PhidgetException;

   public native double getMinFrequency() throws PhidgetException;

   public native double getMaxFrequency() throws PhidgetException;

   public native double getLEDCurrentLimit() throws PhidgetException;

   public native void setLEDCurrentLimit(double var1) throws PhidgetException;

   public native void setLEDCurrentLimit(double var1, AsyncListener var3);

   public native double getMinLEDCurrentLimit() throws PhidgetException;

   public native double getMaxLEDCurrentLimit() throws PhidgetException;

   public native LEDForwardVoltage getLEDForwardVoltage() throws PhidgetException;

   public native void setLEDForwardVoltage(LEDForwardVoltage var1) throws PhidgetException;

   public native void resetFailsafe() throws PhidgetException;

   public native boolean getState() throws PhidgetException;

   public native void setState(boolean var1) throws PhidgetException;

   public native void setState(boolean var1, AsyncListener var2);
}
