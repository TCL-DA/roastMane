package com.yoctopuce.YoctoAPI;

import java.util.ArrayList;

public class YJniWrapper {
   private static boolean loaded;

   public static void Check() throws YAPI_Exception {
      if (!loaded) {
         String os_arch = System.getProperty("os.arch");
         String os_name = System.getProperty("os.name");
         String os_lower = os_name.toLowerCase();
         if (os_lower.contains("win")) {
            if (os_arch.equals("x86")) {
               try {
                  System.loadLibrary("yapi");
               } catch (UnsatisfiedLinkError var10) {
                  throw new YAPI_Exception(-8, "Cannot load yapi.dll. Check your java.library.path property.");
               }

               loaded = true;
            } else {
               if (!os_arch.equals("amd64")) {
                  loaded = false;
                  throw new YAPI_Exception("Unknown Windows version:" + os_name + " (" + os_arch + ")");
               }

               try {
                  System.loadLibrary("yapi64");
               } catch (UnsatisfiedLinkError var9) {
                  throw new YAPI_Exception(-8, "Cannot load yapi64.dll. Check your java.library.path property.");
               }

               loaded = true;
            }
         } else if (os_lower.contains("linux")) {
            if (os_arch.equals("x86")) {
               try {
                  System.loadLibrary("yapi-i386");
               } catch (UnsatisfiedLinkError var8) {
                  throw new YAPI_Exception(-8, "Cannot load libyapi-i386.so. Check your java.library.path property.");
               }

               loaded = true;
            } else if (os_arch.equals("amd64")) {
               try {
                  System.loadLibrary("yapi-amd64");
               } catch (UnsatisfiedLinkError var7) {
                  throw new YAPI_Exception(-8, "Cannot load libyapi-amd64.so Check your java.library.path property.");
               }

               loaded = true;
            } else if (os_arch.equals("arm")) {
               try {
                  System.loadLibrary("yapi-armhf");
               } catch (UnsatisfiedLinkError var6) {
                  throw new YAPI_Exception(-8, "Cannot load libyapi-armhf.so. Check your java.library.path property.");
               }

               loaded = true;
            } else {
               if (!os_arch.equals("aarch64")) {
                  loaded = false;
                  throw new YAPI_Exception("Unsupported Linux architecture:" + os_name + " (" + os_arch + ")");
               }

               try {
                  System.loadLibrary("yapi-aarch64");
               } catch (UnsatisfiedLinkError var5) {
                  throw new YAPI_Exception(-8, "Cannot load libyapi-aarch64.so. Check your java.library.path property.");
               }

               loaded = true;
            }
         } else {
            if (!os_lower.contains("mac")) {
               throw new YAPI_Exception(-3, "USB direct access is not supported on " + os_name + " " + os_arch);
            }

            if (!os_arch.equals("x86_64")) {
               loaded = false;
               throw new YAPI_Exception("Unsupported Linux architecture:" + os_name + " (" + os_arch + ")");
            }

            try {
               System.loadLibrary("yapi");
            } catch (UnsatisfiedLinkError var4) {
               throw new YAPI_Exception(-8, "Cannot load libyapi.dylib. Check your java.library.path property.");
            }

            loaded = true;
         }
      }
   }

   public static native String getAPIVersion();

   public static native String addUdevRule(int var0);

   public static native void reserveUSBAccess();

   public static native void releaseUSBAccess();

   public static native ArrayList<String> getBootloaders();

   public static native void updateDeviceList(ArrayList<WPEntry> var0, ArrayList<YPEntry> var1);

   public static native byte[] devRequestSync(String var0, byte[] var1);

   public static native void devRequestAsync(String var0, byte[] var1, YGenericHub.RequestAsyncResult var2, Object var3);

   public static native void startNotifications(YUSBHub var0);

   public static native void stopNotifications();

   public static native void usbProcess(YUSBHub var0);
}
