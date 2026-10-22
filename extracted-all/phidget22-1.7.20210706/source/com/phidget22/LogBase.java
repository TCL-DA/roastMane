package com.phidget22;

public class LogBase {
   public LogBase() throws PhidgetException {
   }

   private final void setEvents(boolean var1) {
   }

   public static native void disable() throws PhidgetException;

   public static native void enable(LogLevel var0, String var1) throws PhidgetException;

   public static native LogLevel getLevel() throws PhidgetException;

   public static native void setLevel(LogLevel var0) throws PhidgetException;

   public static native void log(LogLevel var0, String var1, String var2) throws PhidgetException;

   public static native void log(LogLevel var0, String var1) throws PhidgetException;

   public static native void rotate() throws PhidgetException;

   public static native boolean isRotating() throws PhidgetException;

   public static native LogRotating getRotating() throws PhidgetException;

   public static native void setRotating(long var0, int var2) throws PhidgetException;

   public static native void enableRotating() throws PhidgetException;

   public static native void disableRotating() throws PhidgetException;

   public static native void addSource(String var0, LogLevel var1) throws PhidgetException;

   public static native LogLevel getSourceLevel(String var0) throws PhidgetException;

   public static native void setSourceLevel(String var0, LogLevel var1) throws PhidgetException;

   static {
      try {
         System.loadLibrary("phidget22");
      } catch (UnsatisfiedLinkError var2) {
      }

      try {
         System.loadLibrary("phidget22java");
      } catch (UnsatisfiedLinkError var3) {
         String var1 = System.getProperty("os.name");
         if (var1.startsWith("Linux")) {
            throw new ExceptionInInitializerError(
               var3.getMessage()
                  + "\nError loading the Phidget C libraries (libphidget22.so and libphidget22java.so).\nMake sure they are available, and add their path to LD_LIBRARY_PATH, or specify with java.library.path."
            );
         } else if (var1.startsWith("Windows")) {
            throw new ExceptionInInitializerError(
               var3.getMessage() + "\nError loading the Phidget C libraries.\nInstall the Windows Phidget22 MSI, or specify with java.library.path."
            );
         } else if (var1.startsWith("Mac")) {
            throw new ExceptionInInitializerError(var3.getMessage() + "\nError loading the Phidget C libraries.\nThe Mac Phidget22 DMG must be installed.");
         } else {
            throw new ExceptionInInitializerError(var3.getMessage() + "\nError loading the Phidget C libraries.");
         }
      }
   }
}
