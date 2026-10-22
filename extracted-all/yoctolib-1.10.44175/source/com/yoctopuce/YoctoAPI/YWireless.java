package com.yoctopuce.YoctoAPI;

import java.util.ArrayList;
import java.util.Locale;

public class YWireless extends YFunction {
   public static final int LINKQUALITY_INVALID = -1;
   public static final String SSID_INVALID = "!INVALID!";
   public static final int CHANNEL_INVALID = -1;
   public static final int SECURITY_UNKNOWN = 0;
   public static final int SECURITY_OPEN = 1;
   public static final int SECURITY_WEP = 2;
   public static final int SECURITY_WPA = 3;
   public static final int SECURITY_WPA2 = 4;
   public static final int SECURITY_INVALID = -1;
   public static final String MESSAGE_INVALID = "!INVALID!";
   public static final String WLANCONFIG_INVALID = "!INVALID!";
   public static final int WLANSTATE_DOWN = 0;
   public static final int WLANSTATE_SCANNING = 1;
   public static final int WLANSTATE_CONNECTED = 2;
   public static final int WLANSTATE_REJECTED = 3;
   public static final int WLANSTATE_INVALID = -1;
   protected int _linkQuality = -1;
   protected String _ssid = "!INVALID!";
   protected int _channel = -1;
   protected int _security = -1;
   protected String _message = "!INVALID!";
   protected String _wlanConfig = "!INVALID!";
   protected int _wlanState = -1;
   protected YWireless.UpdateCallback _valueCallbackWireless = null;

   protected YWireless(YAPIContext yctx, String func) {
      super(yctx, func);
      this._className = "Wireless";
   }

   protected YWireless(String func) {
      this(YAPI.GetYCtx(true), func);
   }

   @Override
   protected void _parseAttr(YJSONObject json_val) throws Exception {
      if (json_val.has("linkQuality")) {
         this._linkQuality = json_val.getInt("linkQuality");
      }

      if (json_val.has("ssid")) {
         this._ssid = json_val.getString("ssid");
      }

      if (json_val.has("channel")) {
         this._channel = json_val.getInt("channel");
      }

      if (json_val.has("security")) {
         this._security = json_val.getInt("security");
      }

      if (json_val.has("message")) {
         this._message = json_val.getString("message");
      }

      if (json_val.has("wlanConfig")) {
         this._wlanConfig = json_val.getString("wlanConfig");
      }

      if (json_val.has("wlanState")) {
         this._wlanState = json_val.getInt("wlanState");
      }

      super._parseAttr(json_val);
   }

   public int get_linkQuality() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -1 : this._linkQuality;
      }
   }

   public int getLinkQuality() throws YAPI_Exception {
      return this.get_linkQuality();
   }

   public String get_ssid() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? "!INVALID!" : this._ssid;
      }
   }

   public String getSsid() throws YAPI_Exception {
      return this.get_ssid();
   }

   public int get_channel() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -1 : this._channel;
      }
   }

   public int getChannel() throws YAPI_Exception {
      return this.get_channel();
   }

   public int get_security() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -1 : this._security;
      }
   }

   public int getSecurity() throws YAPI_Exception {
      return this.get_security();
   }

   public String get_message() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? "!INVALID!" : this._message;
      }
   }

   public String getMessage() throws YAPI_Exception {
      return this.get_message();
   }

   public String get_wlanConfig() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? "!INVALID!" : this._wlanConfig;
      }
   }

   public int set_wlanConfig(String newval) throws YAPI_Exception {
      synchronized (this) {
         this._setAttr("wlanConfig", newval);
         return 0;
      }
   }

   public int get_wlanState() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -1 : this._wlanState;
      }
   }

   public int getWlanState() throws YAPI_Exception {
      return this.get_wlanState();
   }

   public static YWireless FindWireless(String func) {
      YAPIContext ctx = YAPI.GetYCtx(true);
      synchronized (ctx._functionCacheLock) {
         YWireless obj = (YWireless)YFunction._FindFromCache("Wireless", func);
         if (obj == null) {
            obj = new YWireless(func);
            YFunction._AddToCache("Wireless", func, obj);
         }

         return obj;
      }
   }

   public static YWireless FindWirelessInContext(YAPIContext yctx, String func) {
      synchronized (yctx._functionCacheLock) {
         YWireless obj = (YWireless)YFunction._FindFromCacheInContext(yctx, "Wireless", func);
         if (obj == null) {
            obj = new YWireless(yctx, func);
            YFunction._AddToCache("Wireless", func, obj);
         }

         return obj;
      }
   }

   public int registerValueCallback(YWireless.UpdateCallback callback) {
      if (callback != null) {
         YFunction._UpdateValueCallbackList(this, true);
      } else {
         YFunction._UpdateValueCallbackList(this, false);
      }

      this._valueCallbackWireless = callback;
      if (callback != null && this.isOnline()) {
         String val = this._advertisedValue;
         if (!val.equals("")) {
            this._invokeValueCallback(val);
         }
      }

      return 0;
   }

   @Override
   public int _invokeValueCallback(String value) {
      if (this._valueCallbackWireless != null) {
         this._valueCallbackWireless.yNewValue(this, value);
      } else {
         super._invokeValueCallback(value);
      }

      return 0;
   }

   public int startWlanScan() throws YAPI_Exception {
      String config = this.get_wlanConfig();
      return this.set_wlanConfig(config);
   }

   public int joinNetwork(String ssid, String securityKey) throws YAPI_Exception {
      return this.set_wlanConfig(String.format(Locale.US, "INFRA:%s\\%s", ssid, securityKey));
   }

   public int adhocNetwork(String ssid, String securityKey) throws YAPI_Exception {
      return this.set_wlanConfig(String.format(Locale.US, "ADHOC:%s\\%s", ssid, securityKey));
   }

   public int softAPNetwork(String ssid, String securityKey) throws YAPI_Exception {
      return this.set_wlanConfig(String.format(Locale.US, "SOFTAP:%s\\%s", ssid, securityKey));
   }

   public ArrayList<YWlanRecord> get_detectedWlans() throws YAPI_Exception {
      new ArrayList();
      ArrayList<YWlanRecord> res = new ArrayList<>();
      byte[] json = this._download("wlan.json?by=name");
      ArrayList<String> wlanlist = this._json_get_array(json);
      res.clear();

      for (String ii : wlanlist) {
         res.add(new YWlanRecord(ii));
      }

      return res;
   }

   public YWireless nextWireless() {
      String next_hwid;
      try {
         String hwid = this._yapi._yHash.resolveHwID(this._className, this._func);
         next_hwid = this._yapi._yHash.getNextHardwareId(this._className, hwid);
      } catch (YAPI_Exception var3) {
         next_hwid = null;
      }

      return next_hwid == null ? null : FindWirelessInContext(this._yapi, next_hwid);
   }

   public static YWireless FirstWireless() {
      YAPIContext yctx = YAPI.GetYCtx(false);
      if (yctx == null) {
         return null;
      } else {
         String next_hwid = yctx._yHash.getFirstHardwareId("Wireless");
         return next_hwid == null ? null : FindWirelessInContext(yctx, next_hwid);
      }
   }

   public static YWireless FirstWirelessInContext(YAPIContext yctx) {
      String next_hwid = yctx._yHash.getFirstHardwareId("Wireless");
      return next_hwid == null ? null : FindWirelessInContext(yctx, next_hwid);
   }

   public interface TimedReportCallback {
      void timedReportCallback(YWireless var1, YMeasure var2);
   }

   public interface UpdateCallback {
      void yNewValue(YWireless var1, String var2);
   }
}
