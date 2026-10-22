package com.phidget22;

public class HubBase extends Phidget {
   HubBase(long var1) {
      super(var1);
   }

   public HubBase() throws PhidgetException {
      super(create());
   }

   private static native long create() throws PhidgetException;

   private final void setDeviceEvents(boolean var1) {
   }

   public native void setPortPower(int var1, boolean var2) throws PhidgetException;
}
