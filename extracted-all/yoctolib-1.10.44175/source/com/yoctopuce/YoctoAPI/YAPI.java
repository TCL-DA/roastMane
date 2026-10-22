package com.yoctopuce.YoctoAPI;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class YAPI {
   public static final String INVALID_STRING = "!INVALID!";
   public static final double INVALID_DOUBLE = -1.79769313486231E308;
   static final double MAX_DOUBLE = Double.MAX_VALUE;
   static final double MIN_DOUBLE = Double.MIN_VALUE;
   public static final int INVALID_INT = Integer.MIN_VALUE;
   public static final long INVALID_LONG = -9223372036854775807L;
   public static final int INVALID_UINT = -1;
   public static final String YOCTO_API_VERSION_STR = "1.10";
   public static final String YOCTO_API_BUILD_STR = "44175";
   public static final int YOCTO_API_VERSION_BCD = 272;
   public static final int YOCTO_VENDORID = 9440;
   public static final int YOCTO_DEVID_FACTORYBOOT = 1;
   public static final int YOCTO_DEVID_BOOTLOADER = 2;
   static final int HASH_BUF_SIZE = 28;
   public static final int SUCCESS = 0;
   public static final int NOT_INITIALIZED = -1;
   public static final int INVALID_ARGUMENT = -2;
   public static final int NOT_SUPPORTED = -3;
   public static final int DEVICE_NOT_FOUND = -4;
   public static final int VERSION_MISMATCH = -5;
   public static final int DEVICE_BUSY = -6;
   public static final int TIMEOUT = -7;
   public static final int IO_ERROR = -8;
   public static final int NO_MORE_DATA = -9;
   public static final int EXHAUSTED = -10;
   public static final int DOUBLE_ACCES = -11;
   public static final int UNAUTHORIZED = -12;
   public static final int RTC_NOT_READY = -13;
   public static final int FILE_NOT_FOUND = -14;
   static final String DefaultEncoding = "ISO-8859-1";
   static final int YOCTO_CALIB_TYPE_OFS = 30;
   public static final int YOCTO_MANUFACTURER_LEN = 20;
   public static final int YOCTO_SERIAL_LEN = 20;
   public static final int YOCTO_BASE_SERIAL_LEN = 8;
   public static final int YOCTO_PRODUCTNAME_LEN = 28;
   public static final int YOCTO_FIRMWARE_LEN = 22;
   public static final int YOCTO_LOGICAL_LEN = 20;
   public static final int YOCTO_FUNCTION_LEN = 20;
   static final int YOCTO_PUBVAL_SIZE = 6;
   static final int YOCTO_PUBVAL_LEN = 16;
   static final int YOCTO_PASS_LEN = 20;
   static final int YOCTO_REALM_LEN = 20;
   public static final int DETECT_NONE = 0;
   public static final int DETECT_USB = 1;
   public static final int DETECT_NET = 2;
   public static final int RESEND_MISSING_PKT = 4;
   public static final int DETECT_ALL = 3;
   static final HashMap<String, YPEntry.BaseClass> _BaseType = new HashMap<>();
   private static HashMap<Long, YAPIContext> _MultipleYAPI = null;
   private static YAPIContext _SingleYAPI = null;

   public static synchronized void SetThreadSpecificMode() throws YAPI_Exception {
      if (_SingleYAPI != null) {
         throw new YAPI_Exception(-2, "SetSingleThreadMode must be called before start using the Yoctopuce API");
      } else {
         _MultipleYAPI = new HashMap<>();
      }
   }

   static synchronized YAPIContext GetYCtx(boolean instanciateNew) {
      if (_MultipleYAPI != null) {
         YAPIContext context = _MultipleYAPI.get(Thread.currentThread().getId());
         if (context == null && instanciateNew) {
            context = new YAPIContext();
            _MultipleYAPI.put(Thread.currentThread().getId(), context);
         }

         return context;
      } else {
         if (_SingleYAPI == null && instanciateNew) {
            _SingleYAPI = new YAPIContext();
         }

         return _SingleYAPI;
      }
   }

   static String Base64Encode(byte[] data, int ofs, int len) {
      StringBuilder res = new StringBuilder(len * 3 / 2);

      while (len > 0) {
         byte[] tmp_buff = new byte[]{-1, -1, -1, -1};
         int i = data[ofs++] & 255;
         len--;
         tmp_buff[0] = (byte)((i & 252) >> 2);
         tmp_buff[1] = (byte)((i & 3) << 4);
         if (len > 0) {
            int var7 = data[ofs++];
            len--;
            tmp_buff[1] = (byte)(tmp_buff[1] | (var7 & 240) >> 4);
            tmp_buff[2] = (byte)((var7 & 15) << 2);
            if (len > 0) {
               var7 = data[ofs++];
               len--;
               tmp_buff[2] = (byte)(tmp_buff[2] | (var7 & 192) >> 6);
               tmp_buff[3] = (byte)(var7 & 63);
            }
         }

         for (int var9 = 0; var9 < 4; var9++) {
            int j = tmp_buff[var9] & 255;
            if (j <= 25) {
               res.append((char)(65 + j));
            } else if (j <= 51) {
               res.append((char)(97 + j - 26));
            } else if (j <= 61) {
               res.append((char)(48 + j - 52));
            } else if (j == 62) {
               res.append('+');
            } else if (j == 63) {
               res.append('/');
            } else {
               res.append('=');
            }
         }
      }

      return res.toString();
   }

   public static void SetUSBPacketAckMs(int pktAckDelay) {
      YAPIContext yctx = GetYCtx(true);
      yctx.SetUSBPacketAckMs(pktAckDelay);
   }

   public static String GetAPIVersion() {
      return "1.10.44175" + YUSBHub.getAPIVersion();
   }

   public static int InitAPI(int mode) throws YAPI_Exception {
      YAPIContext yctx = GetYCtx(true);
      return yctx.InitAPI(mode);
   }

   public static void FreeAPI() {
      if (_MultipleYAPI != null) {
         YAPIContext yctx = _MultipleYAPI.get(Thread.currentThread().getId());
         if (yctx != null) {
            yctx.FreeAPI();
         }

         _MultipleYAPI.remove(Thread.currentThread().getId());
      } else {
         YAPIContext yctx = _SingleYAPI;
         if (yctx != null) {
            yctx.FreeAPI();
         }

         _SingleYAPI = null;
      }
   }

   public static int RegisterHub(String url) throws YAPI_Exception {
      return GetYCtx(true).RegisterHub(url);
   }

   public static int RegisterHub(String url, InputStream request, OutputStream response) throws YAPI_Exception {
      return GetYCtx(true).RegisterHub(url, request, response);
   }

   public static int PreregisterHubWebSocketCallback(Object session) throws YAPI_Exception {
      return GetYCtx(true).PreregisterHubWebSocketCallback(session, null, null);
   }

   public static int PreregisterHubWebSocketCallback(Object session, String user, String pass) throws YAPI_Exception {
      return GetYCtx(true).PreregisterHubWebSocketCallback(session, user, pass);
   }

   public static void UnregisterHubWebSocketCallback(Object session) {
      YAPIContext yCtx = GetYCtx(false);
      if (yCtx != null) {
         yCtx.UnregisterHubWebSocketCallback(session);
      }
   }

   public static void EnableUSBHost(Object osContext) throws YAPI_Exception {
      GetYCtx(true).EnableUSBHost(osContext);
   }

   public static int PreregisterHub(String url) throws YAPI_Exception {
      return GetYCtx(true).PreregisterHub(url);
   }

   public static void UnregisterHub(String url) {
      YAPIContext yCtx = GetYCtx(false);
      if (yCtx != null) {
         yCtx.UnregisterHub(url);
      }
   }

   public static int TestHub(String url, int mstimeout) throws YAPI_Exception {
      return GetYCtx(true).TestHub(url, mstimeout);
   }

   public static int UpdateDeviceList() throws YAPI_Exception {
      YAPIContext yCtx = GetYCtx(false);
      if (yCtx == null) {
         throw new YAPI_Exception(-1, "API not initialized");
      } else {
         return yCtx.UpdateDeviceList();
      }
   }

   public static int HandleEvents() throws YAPI_Exception {
      YAPIContext yCtx = GetYCtx(false);
      if (yCtx == null) {
         throw new YAPI_Exception(-1, "API not initialized");
      } else {
         return yCtx.HandleEvents();
      }
   }

   public static int Sleep(long ms_duration) throws YAPI_Exception {
      YAPIContext yCtx = GetYCtx(false);
      if (yCtx == null) {
         throw new YAPI_Exception(-1, "API not initialized");
      } else {
         return yCtx.Sleep(ms_duration);
      }
   }

   public static int TriggerHubDiscovery() throws YAPI_Exception {
      YAPIContext yCtx = GetYCtx(false);
      if (yCtx == null) {
         throw new YAPI_Exception(-1, "API not initialized");
      } else {
         return yCtx.TriggerHubDiscovery();
      }
   }

   public static long GetTickCount() {
      return System.currentTimeMillis();
   }

   public static boolean CheckLogicalName(String name) {
      return name != null && (name.equals("") || name.length() <= 19 && name.matches("^[A-Za-z0-9_-]*$"));
   }

   public static void RegisterDeviceArrivalCallback(YAPI.DeviceArrivalCallback arrivalCallback) {
      GetYCtx(true).RegisterDeviceArrivalCallback(arrivalCallback);
   }

   public static void RegisterDeviceChangeCallback(YAPI.DeviceChangeCallback changeCallback) {
      GetYCtx(true).RegisterDeviceChangeCallback(changeCallback);
   }

   public static void RegisterDeviceRemovalCallback(YAPI.DeviceRemovalCallback removalCallback) {
      GetYCtx(true).RegisterDeviceRemovalCallback(removalCallback);
   }

   public static void RegisterHubDiscoveryCallback(YAPI.HubDiscoveryCallback hubDiscoveryCallback) {
      YAPIContext yCtx = GetYCtx(false);
      if (yCtx != null) {
         yCtx.RegisterHubDiscoveryCallback(hubDiscoveryCallback);
      }
   }

   public static void RegisterLogFunction(YAPI.LogCallback logfun) {
      GetYCtx(true).RegisterLogFunction(logfun);
   }

   public static void SetDeviceListValidity(int deviceListValidity) {
      GetYCtx(true).SetDeviceListValidity(deviceListValidity);
   }

   public static int GetDeviceListValidity() {
      return GetYCtx(true).GetDeviceListValidity();
   }

   public static String AddUdevRule(boolean force) {
      return GetYCtx(true).AddUdevRule(force);
   }

   public static void SetNetworkTimeout(int networkMsTimeout) {
      GetYCtx(true).SetNetworkTimeout(networkMsTimeout);
   }

   public static int GetNetworkTimeout() {
      return GetYCtx(true).GetNetworkTimeout();
   }

   public static void SetCacheValidity(long cacheValidityMs) {
      GetYCtx(true).SetCacheValidity(cacheValidityMs);
   }

   public static long GetCacheValidity() {
      return GetYCtx(true).GetCacheValidity();
   }

   static {
      _BaseType.put("Function", YPEntry.BaseClass.Function);
      _BaseType.put("Sensor", YPEntry.BaseClass.Sensor);
   }

   public interface CalibrationHandlerCallback {
      double yCalibrationHandler(double var1, int var3, ArrayList<Integer> var4, ArrayList<Double> var5, ArrayList<Double> var6);
   }

   public interface DeviceArrivalCallback {
      void yDeviceArrival(YModule var1);
   }

   public interface DeviceChangeCallback {
      void yDeviceChange(YModule var1);
   }

   public interface DeviceRemovalCallback {
      void yDeviceRemoval(YModule var1);
   }

   public interface HubDiscoveryCallback {
      void yHubDiscoveryCallback(String var1, String var2);
   }

   public interface LogCallback {
      void yLog(String var1);
   }
}
