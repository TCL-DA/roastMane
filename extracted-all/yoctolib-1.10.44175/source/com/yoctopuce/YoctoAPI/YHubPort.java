package com.yoctopuce.YoctoAPI;

public class YHubPort extends YFunction {
   public static final int ENABLED_FALSE = 0;
   public static final int ENABLED_TRUE = 1;
   public static final int ENABLED_INVALID = -1;
   public static final int PORTSTATE_OFF = 0;
   public static final int PORTSTATE_OVRLD = 1;
   public static final int PORTSTATE_ON = 2;
   public static final int PORTSTATE_RUN = 3;
   public static final int PORTSTATE_PROG = 4;
   public static final int PORTSTATE_INVALID = -1;
   public static final int BAUDRATE_INVALID = -1;
   protected int _enabled = -1;
   protected int _portState = -1;
   protected int _baudRate = -1;
   protected YHubPort.UpdateCallback _valueCallbackHubPort = null;

   protected YHubPort(YAPIContext ctx, String func) {
      super(ctx, func);
      this._className = "HubPort";
   }

   protected YHubPort(String func) {
      this(YAPI.GetYCtx(true), func);
   }

   @Override
   protected void _parseAttr(YJSONObject json_val) throws Exception {
      if (json_val.has("enabled")) {
         this._enabled = json_val.getInt("enabled") > 0 ? 1 : 0;
      }

      if (json_val.has("portState")) {
         this._portState = json_val.getInt("portState");
      }

      if (json_val.has("baudRate")) {
         this._baudRate = json_val.getInt("baudRate");
      }

      super._parseAttr(json_val);
   }

   public int get_enabled() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -1 : this._enabled;
      }
   }

   public int getEnabled() throws YAPI_Exception {
      return this.get_enabled();
   }

   public int set_enabled(int newval) throws YAPI_Exception {
      synchronized (this) {
         String rest_val = newval > 0 ? "1" : "0";
         this._setAttr("enabled", rest_val);
         return 0;
      }
   }

   public int setEnabled(int newval) throws YAPI_Exception {
      return this.set_enabled(newval);
   }

   public int get_portState() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -1 : this._portState;
      }
   }

   public int getPortState() throws YAPI_Exception {
      return this.get_portState();
   }

   public int get_baudRate() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -1 : this._baudRate;
      }
   }

   public int getBaudRate() throws YAPI_Exception {
      return this.get_baudRate();
   }

   public static YHubPort FindHubPort(String func) {
      YAPIContext ctx = YAPI.GetYCtx(true);
      synchronized (ctx._functionCacheLock) {
         YHubPort obj = (YHubPort)YFunction._FindFromCache("HubPort", func);
         if (obj == null) {
            obj = new YHubPort(func);
            YFunction._AddToCache("HubPort", func, obj);
         }

         return obj;
      }
   }

   public static YHubPort FindHubPortInContext(YAPIContext yctx, String func) {
      synchronized (yctx._functionCacheLock) {
         YHubPort obj = (YHubPort)YFunction._FindFromCacheInContext(yctx, "HubPort", func);
         if (obj == null) {
            obj = new YHubPort(yctx, func);
            YFunction._AddToCache("HubPort", func, obj);
         }

         return obj;
      }
   }

   public int registerValueCallback(YHubPort.UpdateCallback callback) {
      if (callback != null) {
         YFunction._UpdateValueCallbackList(this, true);
      } else {
         YFunction._UpdateValueCallbackList(this, false);
      }

      this._valueCallbackHubPort = callback;
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
      if (this._valueCallbackHubPort != null) {
         this._valueCallbackHubPort.yNewValue(this, value);
      } else {
         super._invokeValueCallback(value);
      }

      return 0;
   }

   public YHubPort nextHubPort() {
      String next_hwid;
      try {
         String hwid = this._yapi._yHash.resolveHwID(this._className, this._func);
         next_hwid = this._yapi._yHash.getNextHardwareId(this._className, hwid);
      } catch (YAPI_Exception var3) {
         next_hwid = null;
      }

      return next_hwid == null ? null : FindHubPortInContext(this._yapi, next_hwid);
   }

   public static YHubPort FirstHubPort() {
      YAPIContext yctx = YAPI.GetYCtx(false);
      if (yctx == null) {
         return null;
      } else {
         String next_hwid = yctx._yHash.getFirstHardwareId("HubPort");
         return next_hwid == null ? null : FindHubPortInContext(yctx, next_hwid);
      }
   }

   public static YHubPort FirstHubPortInContext(YAPIContext yctx) {
      String next_hwid = yctx._yHash.getFirstHardwareId("HubPort");
      return next_hwid == null ? null : FindHubPortInContext(yctx, next_hwid);
   }

   public interface TimedReportCallback {
      void timedReportCallback(YHubPort var1, YMeasure var2);
   }

   public interface UpdateCallback {
      void yNewValue(YHubPort var1, String var2);
   }
}
