package com.phidget22;

public class Log extends LogBase {
   public Log() throws PhidgetException {
   }

   public static native String[] getSources() throws PhidgetException;
}
