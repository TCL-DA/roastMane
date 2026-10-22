package com.yoctopuce.YoctoAPI;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Map;
import java.util.Queue;

public class YAPIContext {
   private static final double[] decExp = new double[]{
      1.0E-6, 1.0E-5, 1.0E-4, 0.001, 0.01, 0.1, 1.0, 10.0, 100.0, 1000.0, 10000.0, 100000.0, 1000000.0, 1.0E7, 1.0E8, 1.0E9
   };
   private static final char[] _hexArray = "0123456789ABCDEF".toCharArray();
   String _defaultEncoding = "ISO-8859-1";
   final Charset _deviceCharset;
   private int _apiMode;
   final ArrayList<YGenericHub> _hubs = new ArrayList<>(1);
   private final Queue<YAPIContext.PlugEvent> _pendingCallbacks = new LinkedList<>();
   private final Queue<YAPIContext.DataEvent> _data_events = new LinkedList<>();
   private final Object _regCbLock = new Object();
   private YAPI.DeviceArrivalCallback _arrivalCallback;
   private YAPI.DeviceChangeCallback _namechgCallback;
   private YAPI.DeviceRemovalCallback _removalCallback;
   private final Object _logCallbackLock = new Object();
   private YAPI.LogCallback _logCallback;
   private final Object _newHubCallbackLock = new Object();
   private YAPI.HubDiscoveryCallback _HubDiscoveryCallback;
   private final HashMap<Integer, YAPI.CalibrationHandlerCallback> _calibHandlers = new HashMap<>();
   private final YSSDP _ssdp;
   final YHash _yHash;
   private final ArrayList<YFunction> _ValueCallbackList = new ArrayList<>();
   private final ArrayList<YFunction> _TimedReportCallbackList = new ArrayList<>();
   private final Map<YModule, Integer> _moduleCallbackList = new HashMap<>();
   private int _pktAckDelay = 0;
   long _deviceListValidityMs = 10000L;
   int _networkTimeoutMs = 20000;
   final Object _functionCacheLock;
   protected long _defaultCacheValidity = 5L;
   private final YSSDP.YSSDPReportInterface _ssdpCallback = new YSSDP.YSSDPReportInterface() {
      @Override
      public void HubDiscoveryCallback(String serial, String urlToRegister, String urlToUnregister) {
         if (urlToRegister != null) {
            synchronized (YAPIContext.this._newHubCallbackLock) {
               if (YAPIContext.this._HubDiscoveryCallback != null) {
                  YAPIContext.this._HubDiscoveryCallback.yHubDiscoveryCallback(serial, urlToRegister);
               }
            }
         }

         if ((YAPIContext.this._apiMode & 2) != 0 && urlToRegister != null) {
            if (urlToUnregister != null) {
               YAPIContext.this.UnregisterHub(urlToUnregister);
            }

            try {
               YAPIContext.this.PreregisterHub(urlToRegister);
            } catch (YAPI_Exception var6) {
               YAPIContext.this._Log("Unable to register hub " + urlToRegister + " detected by SSDP:" + var6.toString());
            }
         }
      }
   };
   private static final YAPI.CalibrationHandlerCallback linearCalibrationHandler = new YAPI.CalibrationHandlerCallback() {
      @Override
      public double yCalibrationHandler(double rawValue, int calibType, ArrayList<Integer> params, ArrayList<Double> rawValues, ArrayList<Double> refValues) {
         double x = rawValues.get(0);
         double adj = refValues.get(0) - x;
         int i = 0;
         int npt;
         if (calibType < 30) {
            npt = calibType % 10;
            if (npt > rawValues.size()) {
               npt = rawValues.size();
            }

            if (npt > refValues.size()) {
               npt = refValues.size();
            }
         } else {
            npt = refValues.size();
         }

         while (rawValue > rawValues.get(i)) {
            if (++i >= npt) {
               break;
            }

            double x2 = x;
            double adj2 = adj;
            x = rawValues.get(i);
            adj = refValues.get(i) - x;
            if (rawValue < x && x > x2) {
               adj = adj2 + (adj - adj2) * (rawValue - x2) / (x - x2);
            }
         }

         return rawValue + adj;
      }
   };

   static double _decimalToDouble(int val) {
      boolean negate = false;
      int mantis = val & 2047;
      if (mantis == 0) {
         return 0.0;
      } else {
         if (val > 32767) {
            negate = true;
            val = 65536 - val;
         } else if (val < 0) {
            negate = true;
            val = -val;
         }

         int exp = val >> 11;
         double res = (double)mantis * decExp[exp];
         return negate ? -res : res;
      }
   }

   static long _doubleToDecimal(double val) {
      int negate = 0;
      if (val == 0.0) {
         return 0L;
      } else {
         if (val < 0.0) {
            negate = 1;
            val = -val;
         }

         double comp = val / 1999.0;
         int decpow = 0;

         while (comp > decExp[decpow] && decpow < 15) {
            decpow++;
         }

         double mant = val / decExp[decpow];
         long res;
         if (decpow == 15 && mant > 2047.0) {
            res = 32767L;
         } else {
            res = (long)(decpow << 11) + Math.round(mant);
         }

         return negate != 0 ? -res : res;
      }
   }

   static ArrayList<Integer> _decodeWords(String data) {
      ArrayList<Integer> udata = new ArrayList<>();
      int datalen = data.length();
      int p = 0;

      while (p < datalen) {
         int c = data.charAt(p++);
         int val;
         if (c == 42) {
            val = 0;
         } else if (c == 88) {
            val = 65535;
         } else if (c == 89) {
            val = 32767;
         } else if (c >= 97) {
            int srcpos = udata.size() - 1 - (c - 97);
            if (srcpos < 0) {
               val = 0;
            } else {
               val = udata.get(srcpos);
            }
         } else {
            if (p + 2 > datalen) {
               return udata;
            }

            val = c - 48;
            int var10 = data.charAt(p++);
            val += var10 - '0' << 5;
            var10 = data.charAt(p++);
            if (var10 == 'z') {
               var10 = '\\';
            }

            val += var10 - '0' << 10;
         }

         udata.add(val);
      }

      return udata;
   }

   static ArrayList<Integer> _decodeFloats(String data) {
      ArrayList<Integer> idata = new ArrayList<>();
      int datalen = data.length();
      int p = 0;

      while (p < datalen) {
         int val = 0;
         int sign = 1;
         int dec = 0;
         int decInc = 0;

         int c;
         for (c = data.charAt(p++); c != 45 && (c < 48 || c > 57); c = data.charAt(p++)) {
            if (p >= datalen) {
               return idata;
            }
         }

         if (c == 45) {
            if (p >= datalen) {
               return idata;
            }

            sign = -sign;
            c = data.charAt(p++);
         }

         while (c >= 48 && c <= 57 || c == 46) {
            if (c == 46) {
               decInc = 1;
            } else if (dec < 3) {
               val = val * 10 + (c - 48);
               dec += decInc;
            }

            if (p < datalen) {
               c = data.charAt(p++);
            } else {
               c = 0;
            }
         }

         if (dec < 3) {
            if (dec == 0) {
               val *= 1000;
            } else if (dec == 1) {
               val *= 100;
            } else {
               val *= 10;
            }
         }

         idata.add(sign * val);
      }

      return idata;
   }

   static int _find_in_bytes(byte[] source, byte[] match) {
      if (source != null && match != null) {
         if (source.length != 0 && match.length != 0) {
            int ret = -1;
            int mpos = 0;
            byte m = match[mpos];

            for (int spos = 0; spos < source.length; spos++) {
               if (m == source[spos]) {
                  if (mpos == 0) {
                     ret = spos;
                  } else if (mpos == match.length - 1) {
                     return ret;
                  }

                  m = match[++mpos];
               } else {
                  ret = -1;
                  mpos = 0;
                  m = match[mpos];
               }
            }

            return ret;
         } else {
            return -1;
         }
      } else {
         return -1;
      }
   }

   public static int _atoi(String str) {
      str = str.trim();
      if (str.length() == 0) {
         return 0;
      } else {
         int s = 0;
         if (str.charAt(s) == '+') {
            s++;
         }

         int i = s;
         if (str.charAt(s) == '-') {
            i = s + 1;
         }

         while (i < str.length() && Character.isDigit(str.charAt(i))) {
            i++;
         }

         if (i == 0) {
            return 0;
         } else {
            str = str.substring(s, i);
            return Integer.valueOf(str);
         }
      }
   }

   static String _bytesToHexStr(byte[] bytes, int offset, int len) {
      char[] hexChars = new char[len * 2];

      for (int j = 0; j < len; j++) {
         int v = bytes[offset + j] & 255;
         hexChars[j * 2] = _hexArray[v >>> 4];
         hexChars[j * 2 + 1] = _hexArray[v & 15];
      }

      return new String(hexChars);
   }

   public static byte[] _hexStrToBin(String hex_str) {
      int len = hex_str.length() / 2;
      byte[] res = new byte[len];

      for (int i = 0; i < len; i++) {
         res[i] = (byte)((Character.digit(hex_str.charAt(i * 2), 16) << 4) + Character.digit(hex_str.charAt(i * 2 + 1), 16));
      }

      return res;
   }

   public static byte[] _bytesMerge(byte[] array_a, byte[] array_b) {
      byte[] res = new byte[array_a.length + array_b.length];
      System.arraycopy(array_a, 0, res, 0, array_a.length);
      System.arraycopy(array_b, 0, res, array_a.length, array_b.length);
      return res;
   }

   static String functionClass(String funcid) {
      int dotpos = funcid.indexOf(46);
      if (dotpos >= 0) {
         funcid = funcid.substring(dotpos + 1);
      }

      int classlen = funcid.length();

      while (funcid.charAt(classlen - 1) <= '9') {
         classlen--;
      }

      return funcid.substring(0, 1).toUpperCase(Locale.US) + funcid.substring(1, classlen);
   }

   public YAPIContext() {
      Charset charset;
      try {
         charset = Charset.forName("ISO-8859-1");
      } catch (Exception var3) {
         charset = Charset.defaultCharset();
      }

      this._deviceCharset = charset;
      this._yHash = new YHash(this);
      this._ssdp = new YSSDP(this);
      this._functionCacheLock = new Object();
      this.resetContext();
   }

   private void resetContext() {
      this._apiMode = 0;
      this._pendingCallbacks.clear();
      this._data_events.clear();
      this._arrivalCallback = null;
      this._namechgCallback = null;
      this._removalCallback = null;
      this._logCallback = null;
      this._HubDiscoveryCallback = null;
      this._hubs.clear();
      this._calibHandlers.clear();
      this._ssdp.reset();
      this._yHash.reset();
      this._ValueCallbackList.clear();
      this._TimedReportCallbackList.clear();
      this._moduleCallbackList.clear();

      for (int i = 1; i <= 20; i++) {
         this._calibHandlers.put(i, linearCalibrationHandler);
      }

      this._calibHandlers.put(30, linearCalibrationHandler);
   }

   void _pushPlugEvent(YAPIContext.PlugEvent.Event ev, String serial) {
      synchronized (this._pendingCallbacks) {
         this._pendingCallbacks.add(new YAPIContext.PlugEvent(this, ev, serial));
      }
   }

   void _PushDataEvent(YAPIContext.DataEvent ev) {
      synchronized (this._data_events) {
         this._data_events.add(ev);
      }
   }

   YAPI.CalibrationHandlerCallback _getCalibrationHandler(int calibType) {
      return !this._calibHandlers.containsKey(calibType) ? null : this._calibHandlers.get(calibType);
   }

   YDevice funcGetDevice(String className, String func) throws YAPI_Exception {
      String resolved;
      try {
         resolved = this._yHash.resolveSerial(className, func);
      } catch (YAPI_Exception var5) {
         if (var5.errorType == -4 && this._hubs.isEmpty()) {
            throw new YAPI_Exception(var5.errorType, "Impossible to contact any device because no hub has been registered");
         }

         this._updateDeviceList_internal(true, false);
         resolved = this._yHash.resolveSerial(className, func);
      }

      YDevice dev = this._yHash.getDevice(resolved);
      if (dev == null) {
         this._updateDeviceList_internal(true, false);
         dev = this._yHash.getDevice(resolved);
         if (dev == null) {
            throw new YAPI_Exception(-4, "Device [" + resolved + "] not online");
         }
      }

      return dev;
   }

   void _UpdateValueCallbackList(YFunction func, boolean add) {
      if (add) {
         func.isOnline();
         synchronized (this._ValueCallbackList) {
            if (!this._ValueCallbackList.contains(func)) {
               this._ValueCallbackList.add(func);
            }
         }
      } else {
         synchronized (this._ValueCallbackList) {
            this._ValueCallbackList.remove(func);
         }
      }
   }

   YFunction _GetValueCallback(String hwid) {
      synchronized (this._ValueCallbackList) {
         for (YFunction func : this._ValueCallbackList) {
            YFunction var10000;
            try {
               if (!func.getHardwareId().equals(hwid)) {
                  continue;
               }

               var10000 = func;
            } catch (YAPI_Exception var7) {
               continue;
            }

            return var10000;
         }

         return null;
      }
   }

   void _UpdateTimedReportCallbackList(YFunction func, boolean add) {
      if (add) {
         func.isOnline();
         synchronized (this._TimedReportCallbackList) {
            if (!this._TimedReportCallbackList.contains(func)) {
               this._TimedReportCallbackList.add(func);
            }
         }
      } else {
         synchronized (this._TimedReportCallbackList) {
            this._TimedReportCallbackList.remove(func);
         }
      }
   }

   void _UpdateModuleCallbackList(YModule module, boolean add) {
      if (add) {
         module.isOnline();
         synchronized (this._moduleCallbackList) {
            if (!this._moduleCallbackList.containsKey(module)) {
               this._moduleCallbackList.put(module, 1);
            } else {
               this._moduleCallbackList.put(module, this._moduleCallbackList.get(module) + 1);
            }
         }
      } else {
         synchronized (this._moduleCallbackList) {
            if (this._moduleCallbackList.containsKey(module) && this._moduleCallbackList.get(module) > 1) {
               this._moduleCallbackList.put(module, this._moduleCallbackList.get(module) - 1);
            }
         }
      }
   }

   YModule _GetModuleCallack(String serial) {
      YModule module = YModule.FindModuleInContext(this, serial + ".module");
      synchronized (this._moduleCallbackList) {
         return this._moduleCallbackList.containsKey(module) && this._moduleCallbackList.get(module) > 0 ? module : null;
      }
   }

   YFunction _GetTimedReportCallback(String hwid) {
      synchronized (this._TimedReportCallbackList) {
         for (YFunction func : this._TimedReportCallbackList) {
            YFunction var10000;
            try {
               if (!func.getHardwareId().equals(hwid)) {
                  continue;
               }

               var10000 = func;
            } catch (YAPI_Exception var7) {
               continue;
            }

            return var10000;
         }

         return null;
      }
   }

   public String AddUdevRule_internal(boolean force) {
      return YUSBHub.addUdevRule(force);
   }

   private synchronized int _AddNewHub(String url, boolean reportConnnectionLost, InputStream request, OutputStream response, Object session) throws YAPI_Exception {
      for (YGenericHub h : this._hubs) {
         if (h.isSameHub(url, request, response, session)) {
            return 0;
         }
      }

      YGenericHub.HTTPParams parsedurl = new YGenericHub.HTTPParams(url);
      YGenericHub newhub;
      if (url.equals("usb")) {
         YUSBHub.CheckUSBAcces();
         newhub = new YUSBHub(this, this._hubs.size(), true, this._pktAckDelay);
      } else if (url.equals("usb_silent")) {
         YUSBHub.CheckUSBAcces();
         newhub = new YUSBHub(this, this._hubs.size(), false, this._pktAckDelay);
      } else {
         if (url.equals("net")) {
            if ((this._apiMode & 2) == 0) {
               if (YUSBHub.RegisterLocalhost()) {
                  newhub = new YHTTPHub(this, this._hubs.size(), new YGenericHub.HTTPParams("localhost"), false, null);
                  this._hubs.add(newhub);
                  newhub.startNotifications();
               }

               this._apiMode |= 2;
               this._ssdp.addCallback(this._ssdpCallback);
            }

            return 0;
         }

         if (parsedurl.getHost().equals("callback")) {
            if (session != null) {
               newhub = new YHTTPHub(this, this._hubs.size(), parsedurl, reportConnnectionLost, session);
            } else {
               newhub = new YCallbackHub(this, this._hubs.size(), parsedurl, request, response);
            }
         } else {
            newhub = new YHTTPHub(this, this._hubs.size(), parsedurl, reportConnnectionLost, null);
         }
      }

      this._hubs.add(newhub);
      newhub.startNotifications();
      return 0;
   }

   private void _updateDeviceList_internal(boolean forceupdate, boolean invokecallbacks) throws YAPI_Exception {
      synchronized (this) {
         for (YGenericHub h : this._hubs) {
            try {
               h.updateDeviceList(forceupdate);
            } catch (InterruptedException var11) {
               throw new YAPI_Exception(-8, "Thread has been interrupted");
            }
         }
      }

      if (invokecallbacks) {
         while (true) {
            YAPIContext.PlugEvent evt;
            synchronized (this._pendingCallbacks) {
               if (this._pendingCallbacks.isEmpty()) {
                  break;
               }

               evt = this._pendingCallbacks.poll();
            }

            synchronized (this._regCbLock) {
               switch (evt.ev) {
                  case PLUG:
                     if (this._arrivalCallback != null) {
                        this._arrivalCallback.yDeviceArrival(evt.module);
                     }
                     break;
                  case CHANGE:
                     if (this._namechgCallback != null) {
                        this._namechgCallback.yDeviceChange(evt.module);
                     }
                     break;
                  case UNPLUG:
                     if (this._removalCallback != null) {
                        this._removalCallback.yDeviceRemoval(evt.module);
                     }
               }
            }
         }
      }
   }

   void _Log(String message) {
      synchronized (this._logCallbackLock) {
         if (this._logCallback != null) {
            this._logCallback.yLog(message);
         }
      }
   }

   public void SetNetworkTimeout_internal(int networkMsTimeout) {
      this._networkTimeoutMs = networkMsTimeout;
   }

   public int GetNetworkTimeout_internal() {
      return this._networkTimeoutMs;
   }

   public void SetDeviceListValidity(int deviceListValidity) {
      this.SetDeviceListValidity_internal((long)deviceListValidity);
   }

   public int GetDeviceListValidity() {
      return this.GetDeviceListValidity_internal();
   }

   public String AddUdevRule(boolean force) {
      return this.AddUdevRule_internal(force);
   }

   public void SetNetworkTimeout(int networkMsTimeout) {
      this.SetNetworkTimeout_internal(networkMsTimeout);
   }

   public int GetNetworkTimeout() {
      return this.GetNetworkTimeout_internal();
   }

   public void SetCacheValidity(long cacheValidityMs) {
      this._defaultCacheValidity = cacheValidityMs;
   }

   public long GetCacheValidity() {
      return this._defaultCacheValidity;
   }

   private void SetDeviceListValidity_internal(long deviceListValidity) {
      this._deviceListValidityMs = deviceListValidity * 1000L;
   }

   private int GetDeviceListValidity_internal() {
      return (int)(this._deviceListValidityMs / 1000L);
   }

   public void SetUSBPacketAckMs(int pktAckDelay) {
      this._pktAckDelay = pktAckDelay;
   }

   public static String GetAPIVersion() {
      return YAPI.GetAPIVersion();
   }

   public int InitAPI(int mode) throws YAPI_Exception {
      if ((mode & 2) != 0) {
         this.RegisterHub("net");
      }

      if ((mode & 4) != 0) {
         this._pktAckDelay = 50;
      }

      if ((mode & 1) != 0) {
         this.RegisterHub("usb");
      }

      return 0;
   }

   public void FreeAPI() {
      if ((this._apiMode & 2) != 0) {
         this._ssdp.Stop();
      }

      for (YGenericHub h : this._hubs) {
         h.stopNotifications();
         h.release();
      }

      this.resetContext();
   }

   public int RegisterHub(String url) throws YAPI_Exception {
      this._AddNewHub(url, true, null, null, null);
      this._updateDeviceList_internal(true, false);
      return 0;
   }

   public int RegisterHub(String url, InputStream request, OutputStream response) throws YAPI_Exception {
      this._AddNewHub(url, true, request, response, null);
      this._updateDeviceList_internal(true, false);
      return 0;
   }

   public int RegisterHubHTTPCallback(InputStream request, OutputStream response) throws YAPI_Exception {
      this._AddNewHub("http://callback", true, request, response, null);
      this._updateDeviceList_internal(true, false);
      return 0;
   }

   public int PreregisterHubWebSocketCallback(Object session) throws YAPI_Exception {
      return this.PreregisterHubWebSocketCallback(session, null, null);
   }

   public int PreregisterHubWebSocketCallback(Object session, String user, String pass) throws YAPI_Exception {
      if (user == null) {
         user = "";
      }

      if (pass != null) {
         user = user + ":" + pass;
      }

      String url = "ws://" + user + "@callback";
      this._AddNewHub(url, true, null, null, session);
      return 0;
   }

   public void UnregisterHubWebSocketCallback(Object session) {
      this.unregisterHubEx("ws://callback", null, null, session);
   }

   public void EnableUSBHost(Object osContext) throws YAPI_Exception {
      YUSBHub.SetContextType(osContext);
   }

   public int PreregisterHub(String url) throws YAPI_Exception {
      this._AddNewHub(url, false, null, null, null);
      return 0;
   }

   public void UnregisterHub(String url) {
      if (url.equals("net")) {
         this._apiMode &= -3;
      } else {
         this.unregisterHubEx(url, null, null, null);
      }
   }

   private void unregisterHubEx(String url, InputStream request, OutputStream response, Object session) {
      for (YGenericHub h : this._hubs) {
         if (h.isSameHub(url, request, response, session)) {
            h.stopNotifications();
            h.release();
            this._hubs.remove(h);
            return;
         }
      }
   }

   public int TestHub(String url, int mstimeout) throws YAPI_Exception {
      YGenericHub.HTTPParams parsedurl = new YGenericHub.HTTPParams(url);
      YGenericHub newhub;
      if (url.equals("usb")) {
         YUSBHub.CheckUSBAcces();
         newhub = new YUSBHub(this, 0, true, this._pktAckDelay);
      } else {
         if (url.equals("net")) {
            return 0;
         }

         if (parsedurl.getHost().equals("callback")) {
            newhub = new YCallbackHub(this, 0, parsedurl, null, null);
         } else {
            newhub = new YHTTPHub(this, 0, parsedurl, true, null);
         }
      }

      return newhub.ping(mstimeout);
   }

   public int UpdateDeviceList() throws YAPI_Exception {
      this._updateDeviceList_internal(false, true);
      return 0;
   }

   public int HandleEvents() throws YAPI_Exception {
      while (true) {
         YAPIContext.DataEvent pv;
         synchronized (this._data_events) {
            if (this._data_events.isEmpty()) {
               return 0;
            }

            pv = this._data_events.poll();
         }

         pv.invoke();
      }
   }

   public int Sleep(long ms_duration) throws YAPI_Exception {
      long end = GetTickCount() + ms_duration;

      do {
         this.HandleEvents();
         if (end > GetTickCount()) {
            try {
               Thread.sleep(2L);
            } catch (InterruptedException var6) {
               throw new YAPI_Exception(-8, "Thread has been interrupted");
            }
         }
      } while (end > GetTickCount());

      return 0;
   }

   public int TriggerHubDiscovery() throws YAPI_Exception {
      this._ssdp.addCallback(this._ssdpCallback);
      return 0;
   }

   public static long GetTickCount() {
      return System.currentTimeMillis();
   }

   public boolean CheckLogicalName(String name) {
      return YAPI.CheckLogicalName(name);
   }

   public void RegisterDeviceArrivalCallback(YAPI.DeviceArrivalCallback arrivalCallback) {
      synchronized (this._regCbLock) {
         this._arrivalCallback = arrivalCallback;
      }
   }

   public void RegisterDeviceChangeCallback(YAPI.DeviceChangeCallback changeCallback) {
      synchronized (this._regCbLock) {
         this._namechgCallback = changeCallback;
      }
   }

   public void RegisterDeviceRemovalCallback(YAPI.DeviceRemovalCallback removalCallback) {
      synchronized (this._regCbLock) {
         this._removalCallback = removalCallback;
      }
   }

   public void RegisterHubDiscoveryCallback(YAPI.HubDiscoveryCallback hubDiscoveryCallback) {
      synchronized (this._newHubCallbackLock) {
         this._HubDiscoveryCallback = hubDiscoveryCallback;
      }

      try {
         this.TriggerHubDiscovery();
      } catch (YAPI_Exception var4) {
      }
   }

   public void RegisterLogFunction(YAPI.LogCallback logfun) {
      synchronized (this._logCallbackLock) {
         this._logCallback = logfun;
      }
   }

   static class DataEvent {
      private final YFunction _fun;
      private final String _value;
      private final ArrayList<Integer> _report;
      private final double _timestamp;
      private final double _duration;
      private final YModule _module;
      private final int _beacon;

      DataEvent(YFunction fun, String value) {
         this._module = null;
         this._fun = fun;
         this._value = value;
         this._report = null;
         this._timestamp = 0.0;
         this._duration = 0.0;
         this._beacon = -1;
      }

      DataEvent(YModule module) {
         this._module = module;
         this._fun = null;
         this._value = null;
         this._report = null;
         this._timestamp = 0.0;
         this._duration = 0.0;
         this._beacon = -1;
      }

      DataEvent(YModule module, int beacon) {
         this._module = module;
         this._fun = null;
         this._value = null;
         this._report = null;
         this._timestamp = 0.0;
         this._duration = 0.0;
         this._beacon = beacon;
      }

      DataEvent(YFunction fun, double timestamp, double duration, ArrayList<Integer> report) {
         this._module = null;
         this._fun = fun;
         this._value = null;
         this._timestamp = timestamp;
         this._duration = duration;
         this._report = report;
         this._beacon = -1;
      }

      public void invoke() {
         if (this._module != null) {
            if (this._beacon < 0) {
               this._module._invokeConfigChangeCallback();
            } else {
               this._module._invokeBeaconCallback(this._beacon);
            }
         } else if (this._value == null) {
            YSensor sensor = (YSensor)this._fun;

            assert sensor != null;

            YMeasure mesure = sensor._decodeTimedReport(this._timestamp, this._duration, this._report);
            sensor._invokeTimedReportCallback(mesure);
         } else {
            assert this._fun != null;

            this._fun._invokeValueCallback(this._value);
         }
      }
   }

   static class PlugEvent {
      YAPIContext.PlugEvent.Event ev;
      public YModule module;

      PlugEvent(YAPIContext yctx, YAPIContext.PlugEvent.Event ev, String serial) {
         this.ev = ev;
         this.module = YModule.FindModuleInContext(yctx, serial + ".module");
      }

      public static enum Event {
         PLUG,
         UNPLUG,
         CHANGE;
      }
   }
}
