package com.yoctopuce.YoctoAPI;

import java.util.Locale;

public class YNetwork extends YFunction {
   public static final int READINESS_DOWN = 0;
   public static final int READINESS_EXISTS = 1;
   public static final int READINESS_LINKED = 2;
   public static final int READINESS_LAN_OK = 3;
   public static final int READINESS_WWW_OK = 4;
   public static final int READINESS_INVALID = -1;
   public static final String MACADDRESS_INVALID = "!INVALID!";
   public static final String IPADDRESS_INVALID = "!INVALID!";
   public static final String SUBNETMASK_INVALID = "!INVALID!";
   public static final String ROUTER_INVALID = "!INVALID!";
   public static final String IPCONFIG_INVALID = "!INVALID!";
   public static final String PRIMARYDNS_INVALID = "!INVALID!";
   public static final String SECONDARYDNS_INVALID = "!INVALID!";
   public static final String NTPSERVER_INVALID = "!INVALID!";
   public static final String USERPASSWORD_INVALID = "!INVALID!";
   public static final String ADMINPASSWORD_INVALID = "!INVALID!";
   public static final int HTTPPORT_INVALID = -1;
   public static final String DEFAULTPAGE_INVALID = "!INVALID!";
   public static final int DISCOVERABLE_FALSE = 0;
   public static final int DISCOVERABLE_TRUE = 1;
   public static final int DISCOVERABLE_INVALID = -1;
   public static final int WWWWATCHDOGDELAY_INVALID = -1;
   public static final String CALLBACKURL_INVALID = "!INVALID!";
   public static final int CALLBACKMETHOD_POST = 0;
   public static final int CALLBACKMETHOD_GET = 1;
   public static final int CALLBACKMETHOD_PUT = 2;
   public static final int CALLBACKMETHOD_INVALID = -1;
   public static final int CALLBACKENCODING_FORM = 0;
   public static final int CALLBACKENCODING_JSON = 1;
   public static final int CALLBACKENCODING_JSON_ARRAY = 2;
   public static final int CALLBACKENCODING_CSV = 3;
   public static final int CALLBACKENCODING_YOCTO_API = 4;
   public static final int CALLBACKENCODING_JSON_NUM = 5;
   public static final int CALLBACKENCODING_EMONCMS = 6;
   public static final int CALLBACKENCODING_AZURE = 7;
   public static final int CALLBACKENCODING_INFLUXDB = 8;
   public static final int CALLBACKENCODING_MQTT = 9;
   public static final int CALLBACKENCODING_YOCTO_API_JZON = 10;
   public static final int CALLBACKENCODING_PRTG = 11;
   public static final int CALLBACKENCODING_INFLUXDB_V2 = 12;
   public static final int CALLBACKENCODING_INVALID = -1;
   public static final String CALLBACKCREDENTIALS_INVALID = "!INVALID!";
   public static final int CALLBACKINITIALDELAY_INVALID = -1;
   public static final String CALLBACKSCHEDULE_INVALID = "!INVALID!";
   public static final int CALLBACKMINDELAY_INVALID = -1;
   public static final int CALLBACKMAXDELAY_INVALID = -1;
   public static final int POECURRENT_INVALID = -1;
   protected int _readiness = -1;
   protected String _macAddress = "!INVALID!";
   protected String _ipAddress = "!INVALID!";
   protected String _subnetMask = "!INVALID!";
   protected String _router = "!INVALID!";
   protected String _ipConfig = "!INVALID!";
   protected String _primaryDNS = "!INVALID!";
   protected String _secondaryDNS = "!INVALID!";
   protected String _ntpServer = "!INVALID!";
   protected String _userPassword = "!INVALID!";
   protected String _adminPassword = "!INVALID!";
   protected int _httpPort = -1;
   protected String _defaultPage = "!INVALID!";
   protected int _discoverable = -1;
   protected int _wwwWatchdogDelay = -1;
   protected String _callbackUrl = "!INVALID!";
   protected int _callbackMethod = -1;
   protected int _callbackEncoding = -1;
   protected String _callbackCredentials = "!INVALID!";
   protected int _callbackInitialDelay = -1;
   protected String _callbackSchedule = "!INVALID!";
   protected int _callbackMinDelay = -1;
   protected int _callbackMaxDelay = -1;
   protected int _poeCurrent = -1;
   protected YNetwork.UpdateCallback _valueCallbackNetwork = null;

   protected YNetwork(YAPIContext ctx, String func) {
      super(ctx, func);
      this._className = "Network";
   }

   protected YNetwork(String func) {
      this(YAPI.GetYCtx(true), func);
   }

   @Override
   protected void _parseAttr(YJSONObject json_val) throws Exception {
      if (json_val.has("readiness")) {
         this._readiness = json_val.getInt("readiness");
      }

      if (json_val.has("macAddress")) {
         this._macAddress = json_val.getString("macAddress");
      }

      if (json_val.has("ipAddress")) {
         this._ipAddress = json_val.getString("ipAddress");
      }

      if (json_val.has("subnetMask")) {
         this._subnetMask = json_val.getString("subnetMask");
      }

      if (json_val.has("router")) {
         this._router = json_val.getString("router");
      }

      if (json_val.has("ipConfig")) {
         this._ipConfig = json_val.getString("ipConfig");
      }

      if (json_val.has("primaryDNS")) {
         this._primaryDNS = json_val.getString("primaryDNS");
      }

      if (json_val.has("secondaryDNS")) {
         this._secondaryDNS = json_val.getString("secondaryDNS");
      }

      if (json_val.has("ntpServer")) {
         this._ntpServer = json_val.getString("ntpServer");
      }

      if (json_val.has("userPassword")) {
         this._userPassword = json_val.getString("userPassword");
      }

      if (json_val.has("adminPassword")) {
         this._adminPassword = json_val.getString("adminPassword");
      }

      if (json_val.has("httpPort")) {
         this._httpPort = json_val.getInt("httpPort");
      }

      if (json_val.has("defaultPage")) {
         this._defaultPage = json_val.getString("defaultPage");
      }

      if (json_val.has("discoverable")) {
         this._discoverable = json_val.getInt("discoverable") > 0 ? 1 : 0;
      }

      if (json_val.has("wwwWatchdogDelay")) {
         this._wwwWatchdogDelay = json_val.getInt("wwwWatchdogDelay");
      }

      if (json_val.has("callbackUrl")) {
         this._callbackUrl = json_val.getString("callbackUrl");
      }

      if (json_val.has("callbackMethod")) {
         this._callbackMethod = json_val.getInt("callbackMethod");
      }

      if (json_val.has("callbackEncoding")) {
         this._callbackEncoding = json_val.getInt("callbackEncoding");
      }

      if (json_val.has("callbackCredentials")) {
         this._callbackCredentials = json_val.getString("callbackCredentials");
      }

      if (json_val.has("callbackInitialDelay")) {
         this._callbackInitialDelay = json_val.getInt("callbackInitialDelay");
      }

      if (json_val.has("callbackSchedule")) {
         this._callbackSchedule = json_val.getString("callbackSchedule");
      }

      if (json_val.has("callbackMinDelay")) {
         this._callbackMinDelay = json_val.getInt("callbackMinDelay");
      }

      if (json_val.has("callbackMaxDelay")) {
         this._callbackMaxDelay = json_val.getInt("callbackMaxDelay");
      }

      if (json_val.has("poeCurrent")) {
         this._poeCurrent = json_val.getInt("poeCurrent");
      }

      super._parseAttr(json_val);
   }

   public int get_readiness() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -1 : this._readiness;
      }
   }

   public int getReadiness() throws YAPI_Exception {
      return this.get_readiness();
   }

   public String get_macAddress() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration == 0L && this.load(this._yapi._defaultCacheValidity) != 0 ? "!INVALID!" : this._macAddress;
      }
   }

   public String getMacAddress() throws YAPI_Exception {
      return this.get_macAddress();
   }

   public String get_ipAddress() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? "!INVALID!" : this._ipAddress;
      }
   }

   public String getIpAddress() throws YAPI_Exception {
      return this.get_ipAddress();
   }

   public String get_subnetMask() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? "!INVALID!" : this._subnetMask;
      }
   }

   public String getSubnetMask() throws YAPI_Exception {
      return this.get_subnetMask();
   }

   public String get_router() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? "!INVALID!" : this._router;
      }
   }

   public String getRouter() throws YAPI_Exception {
      return this.get_router();
   }

   public String get_ipConfig() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? "!INVALID!" : this._ipConfig;
      }
   }

   public String getIpConfig() throws YAPI_Exception {
      return this.get_ipConfig();
   }

   public int set_ipConfig(String newval) throws YAPI_Exception {
      synchronized (this) {
         this._setAttr("ipConfig", newval);
         return 0;
      }
   }

   public String get_primaryDNS() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? "!INVALID!" : this._primaryDNS;
      }
   }

   public String getPrimaryDNS() throws YAPI_Exception {
      return this.get_primaryDNS();
   }

   public int set_primaryDNS(String newval) throws YAPI_Exception {
      synchronized (this) {
         this._setAttr("primaryDNS", newval);
         return 0;
      }
   }

   public int setPrimaryDNS(String newval) throws YAPI_Exception {
      return this.set_primaryDNS(newval);
   }

   public String get_secondaryDNS() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? "!INVALID!" : this._secondaryDNS;
      }
   }

   public String getSecondaryDNS() throws YAPI_Exception {
      return this.get_secondaryDNS();
   }

   public int set_secondaryDNS(String newval) throws YAPI_Exception {
      synchronized (this) {
         this._setAttr("secondaryDNS", newval);
         return 0;
      }
   }

   public int setSecondaryDNS(String newval) throws YAPI_Exception {
      return this.set_secondaryDNS(newval);
   }

   public String get_ntpServer() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? "!INVALID!" : this._ntpServer;
      }
   }

   public String getNtpServer() throws YAPI_Exception {
      return this.get_ntpServer();
   }

   public int set_ntpServer(String newval) throws YAPI_Exception {
      synchronized (this) {
         this._setAttr("ntpServer", newval);
         return 0;
      }
   }

   public int setNtpServer(String newval) throws YAPI_Exception {
      return this.set_ntpServer(newval);
   }

   public String get_userPassword() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? "!INVALID!" : this._userPassword;
      }
   }

   public String getUserPassword() throws YAPI_Exception {
      return this.get_userPassword();
   }

   public int set_userPassword(String newval) throws YAPI_Exception {
      if (newval.length() > 28) {
         this._throw(-2, String.format(Locale.US, "Password too long (max %d chars) :%s", 28, newval));
      }

      synchronized (this) {
         this._setAttr("userPassword", newval);
         return 0;
      }
   }

   public int setUserPassword(String newval) throws YAPI_Exception {
      return this.set_userPassword(newval);
   }

   public String get_adminPassword() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? "!INVALID!" : this._adminPassword;
      }
   }

   public String getAdminPassword() throws YAPI_Exception {
      return this.get_adminPassword();
   }

   public int set_adminPassword(String newval) throws YAPI_Exception {
      if (newval.length() > 28) {
         this._throw(-2, String.format(Locale.US, "Password too long (max %d chars) :%s", 28, newval));
      }

      synchronized (this) {
         this._setAttr("adminPassword", newval);
         return 0;
      }
   }

   public int setAdminPassword(String newval) throws YAPI_Exception {
      return this.set_adminPassword(newval);
   }

   public int get_httpPort() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -1 : this._httpPort;
      }
   }

   public int getHttpPort() throws YAPI_Exception {
      return this.get_httpPort();
   }

   public int set_httpPort(int newval) throws YAPI_Exception {
      synchronized (this) {
         String rest_val = Integer.toString(newval);
         this._setAttr("httpPort", rest_val);
         return 0;
      }
   }

   public int setHttpPort(int newval) throws YAPI_Exception {
      return this.set_httpPort(newval);
   }

   public String get_defaultPage() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? "!INVALID!" : this._defaultPage;
      }
   }

   public String getDefaultPage() throws YAPI_Exception {
      return this.get_defaultPage();
   }

   public int set_defaultPage(String newval) throws YAPI_Exception {
      synchronized (this) {
         this._setAttr("defaultPage", newval);
         return 0;
      }
   }

   public int setDefaultPage(String newval) throws YAPI_Exception {
      return this.set_defaultPage(newval);
   }

   public int get_discoverable() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -1 : this._discoverable;
      }
   }

   public int getDiscoverable() throws YAPI_Exception {
      return this.get_discoverable();
   }

   public int set_discoverable(int newval) throws YAPI_Exception {
      synchronized (this) {
         String rest_val = newval > 0 ? "1" : "0";
         this._setAttr("discoverable", rest_val);
         return 0;
      }
   }

   public int setDiscoverable(int newval) throws YAPI_Exception {
      return this.set_discoverable(newval);
   }

   public int get_wwwWatchdogDelay() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -1 : this._wwwWatchdogDelay;
      }
   }

   public int getWwwWatchdogDelay() throws YAPI_Exception {
      return this.get_wwwWatchdogDelay();
   }

   public int set_wwwWatchdogDelay(int newval) throws YAPI_Exception {
      synchronized (this) {
         String rest_val = Integer.toString(newval);
         this._setAttr("wwwWatchdogDelay", rest_val);
         return 0;
      }
   }

   public int setWwwWatchdogDelay(int newval) throws YAPI_Exception {
      return this.set_wwwWatchdogDelay(newval);
   }

   public String get_callbackUrl() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? "!INVALID!" : this._callbackUrl;
      }
   }

   public String getCallbackUrl() throws YAPI_Exception {
      return this.get_callbackUrl();
   }

   public int set_callbackUrl(String newval) throws YAPI_Exception {
      synchronized (this) {
         this._setAttr("callbackUrl", newval);
         return 0;
      }
   }

   public int setCallbackUrl(String newval) throws YAPI_Exception {
      return this.set_callbackUrl(newval);
   }

   public int get_callbackMethod() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -1 : this._callbackMethod;
      }
   }

   public int getCallbackMethod() throws YAPI_Exception {
      return this.get_callbackMethod();
   }

   public int set_callbackMethod(int newval) throws YAPI_Exception {
      synchronized (this) {
         String rest_val = Integer.toString(newval);
         this._setAttr("callbackMethod", rest_val);
         return 0;
      }
   }

   public int setCallbackMethod(int newval) throws YAPI_Exception {
      return this.set_callbackMethod(newval);
   }

   public int get_callbackEncoding() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -1 : this._callbackEncoding;
      }
   }

   public int getCallbackEncoding() throws YAPI_Exception {
      return this.get_callbackEncoding();
   }

   public int set_callbackEncoding(int newval) throws YAPI_Exception {
      synchronized (this) {
         String rest_val = Integer.toString(newval);
         this._setAttr("callbackEncoding", rest_val);
         return 0;
      }
   }

   public int setCallbackEncoding(int newval) throws YAPI_Exception {
      return this.set_callbackEncoding(newval);
   }

   public String get_callbackCredentials() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0
            ? "!INVALID!"
            : this._callbackCredentials;
      }
   }

   public String getCallbackCredentials() throws YAPI_Exception {
      return this.get_callbackCredentials();
   }

   public int set_callbackCredentials(String newval) throws YAPI_Exception {
      synchronized (this) {
         this._setAttr("callbackCredentials", newval);
         return 0;
      }
   }

   public int setCallbackCredentials(String newval) throws YAPI_Exception {
      return this.set_callbackCredentials(newval);
   }

   public int callbackLogin(String username, String password) throws YAPI_Exception {
      String rest_val = String.format(Locale.US, "%s:%s", username, password);
      this._setAttr("callbackCredentials", rest_val);
      return 0;
   }

   public int get_callbackInitialDelay() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -1 : this._callbackInitialDelay;
      }
   }

   public int getCallbackInitialDelay() throws YAPI_Exception {
      return this.get_callbackInitialDelay();
   }

   public int set_callbackInitialDelay(int newval) throws YAPI_Exception {
      synchronized (this) {
         String rest_val = Integer.toString(newval);
         this._setAttr("callbackInitialDelay", rest_val);
         return 0;
      }
   }

   public int setCallbackInitialDelay(int newval) throws YAPI_Exception {
      return this.set_callbackInitialDelay(newval);
   }

   public String get_callbackSchedule() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? "!INVALID!" : this._callbackSchedule;
      }
   }

   public String getCallbackSchedule() throws YAPI_Exception {
      return this.get_callbackSchedule();
   }

   public int set_callbackSchedule(String newval) throws YAPI_Exception {
      synchronized (this) {
         this._setAttr("callbackSchedule", newval);
         return 0;
      }
   }

   public int setCallbackSchedule(String newval) throws YAPI_Exception {
      return this.set_callbackSchedule(newval);
   }

   public int get_callbackMinDelay() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -1 : this._callbackMinDelay;
      }
   }

   public int getCallbackMinDelay() throws YAPI_Exception {
      return this.get_callbackMinDelay();
   }

   public int set_callbackMinDelay(int newval) throws YAPI_Exception {
      synchronized (this) {
         String rest_val = Integer.toString(newval);
         this._setAttr("callbackMinDelay", rest_val);
         return 0;
      }
   }

   public int setCallbackMinDelay(int newval) throws YAPI_Exception {
      return this.set_callbackMinDelay(newval);
   }

   public int get_callbackMaxDelay() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -1 : this._callbackMaxDelay;
      }
   }

   public int getCallbackMaxDelay() throws YAPI_Exception {
      return this.get_callbackMaxDelay();
   }

   public int set_callbackMaxDelay(int newval) throws YAPI_Exception {
      synchronized (this) {
         String rest_val = Integer.toString(newval);
         this._setAttr("callbackMaxDelay", rest_val);
         return 0;
      }
   }

   public int setCallbackMaxDelay(int newval) throws YAPI_Exception {
      return this.set_callbackMaxDelay(newval);
   }

   public int get_poeCurrent() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -1 : this._poeCurrent;
      }
   }

   public int getPoeCurrent() throws YAPI_Exception {
      return this.get_poeCurrent();
   }

   public static YNetwork FindNetwork(String func) {
      YAPIContext ctx = YAPI.GetYCtx(true);
      synchronized (ctx._functionCacheLock) {
         YNetwork obj = (YNetwork)YFunction._FindFromCache("Network", func);
         if (obj == null) {
            obj = new YNetwork(func);
            YFunction._AddToCache("Network", func, obj);
         }

         return obj;
      }
   }

   public static YNetwork FindNetworkInContext(YAPIContext yctx, String func) {
      synchronized (yctx._functionCacheLock) {
         YNetwork obj = (YNetwork)YFunction._FindFromCacheInContext(yctx, "Network", func);
         if (obj == null) {
            obj = new YNetwork(yctx, func);
            YFunction._AddToCache("Network", func, obj);
         }

         return obj;
      }
   }

   public int registerValueCallback(YNetwork.UpdateCallback callback) {
      if (callback != null) {
         YFunction._UpdateValueCallbackList(this, true);
      } else {
         YFunction._UpdateValueCallbackList(this, false);
      }

      this._valueCallbackNetwork = callback;
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
      if (this._valueCallbackNetwork != null) {
         this._valueCallbackNetwork.yNewValue(this, value);
      } else {
         super._invokeValueCallback(value);
      }

      return 0;
   }

   public int useDHCP(String fallbackIpAddr, int fallbackSubnetMaskLen, String fallbackRouter) throws YAPI_Exception {
      return this.set_ipConfig(String.format(Locale.US, "DHCP:%s/%d/%s", fallbackIpAddr, fallbackSubnetMaskLen, fallbackRouter));
   }

   public int useDHCPauto() throws YAPI_Exception {
      return this.set_ipConfig("DHCP:");
   }

   public int useStaticIP(String ipAddress, int subnetMaskLen, String router) throws YAPI_Exception {
      return this.set_ipConfig(String.format(Locale.US, "STATIC:%s/%d/%s", ipAddress, subnetMaskLen, router));
   }

   public String ping(String host) throws YAPI_Exception {
      byte[] content = this._download(String.format(Locale.US, "ping.txt?host=%s", host));
      return new String(content);
   }

   public int triggerCallback() throws YAPI_Exception {
      return this.set_callbackMethod(this.get_callbackMethod());
   }

   public int set_periodicCallbackSchedule(String interval, int offset) throws YAPI_Exception {
      return this.set_callbackSchedule(String.format(Locale.US, "every %s+%d", interval, offset));
   }

   public YNetwork nextNetwork() {
      String next_hwid;
      try {
         String hwid = this._yapi._yHash.resolveHwID(this._className, this._func);
         next_hwid = this._yapi._yHash.getNextHardwareId(this._className, hwid);
      } catch (YAPI_Exception var3) {
         next_hwid = null;
      }

      return next_hwid == null ? null : FindNetworkInContext(this._yapi, next_hwid);
   }

   public static YNetwork FirstNetwork() {
      YAPIContext yctx = YAPI.GetYCtx(false);
      if (yctx == null) {
         return null;
      } else {
         String next_hwid = yctx._yHash.getFirstHardwareId("Network");
         return next_hwid == null ? null : FindNetworkInContext(yctx, next_hwid);
      }
   }

   public static YNetwork FirstNetworkInContext(YAPIContext yctx) {
      String next_hwid = yctx._yHash.getFirstHardwareId("Network");
      return next_hwid == null ? null : FindNetworkInContext(yctx, next_hwid);
   }

   public interface TimedReportCallback {
      void timedReportCallback(YNetwork var1, YMeasure var2);
   }

   public interface UpdateCallback {
      void yNewValue(YNetwork var1, String var2);
   }
}
