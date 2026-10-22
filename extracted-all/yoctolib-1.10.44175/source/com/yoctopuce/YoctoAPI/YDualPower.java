package com.yoctopuce.YoctoAPI;

public class YDualPower extends YFunction {
   public static final int POWERSTATE_OFF = 0;
   public static final int POWERSTATE_FROM_USB = 1;
   public static final int POWERSTATE_FROM_EXT = 2;
   public static final int POWERSTATE_INVALID = -1;
   public static final int POWERCONTROL_AUTO = 0;
   public static final int POWERCONTROL_FROM_USB = 1;
   public static final int POWERCONTROL_FROM_EXT = 2;
   public static final int POWERCONTROL_OFF = 3;
   public static final int POWERCONTROL_INVALID = -1;
   public static final int EXTVOLTAGE_INVALID = -1;
   protected int _powerState = -1;
   protected int _powerControl = -1;
   protected int _extVoltage = -1;
   protected YDualPower.UpdateCallback _valueCallbackDualPower = null;

   protected YDualPower(YAPIContext ctx, String func) {
      super(ctx, func);
      this._className = "DualPower";
   }

   protected YDualPower(String func) {
      this(YAPI.GetYCtx(true), func);
   }

   @Override
   protected void _parseAttr(YJSONObject json_val) throws Exception {
      if (json_val.has("powerState")) {
         this._powerState = json_val.getInt("powerState");
      }

      if (json_val.has("powerControl")) {
         this._powerControl = json_val.getInt("powerControl");
      }

      if (json_val.has("extVoltage")) {
         this._extVoltage = json_val.getInt("extVoltage");
      }

      super._parseAttr(json_val);
   }

   public int get_powerState() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -1 : this._powerState;
      }
   }

   public int getPowerState() throws YAPI_Exception {
      return this.get_powerState();
   }

   public int get_powerControl() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -1 : this._powerControl;
      }
   }

   public int getPowerControl() throws YAPI_Exception {
      return this.get_powerControl();
   }

   public int set_powerControl(int newval) throws YAPI_Exception {
      synchronized (this) {
         String rest_val = Integer.toString(newval);
         this._setAttr("powerControl", rest_val);
         return 0;
      }
   }

   public int setPowerControl(int newval) throws YAPI_Exception {
      return this.set_powerControl(newval);
   }

   public int get_extVoltage() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -1 : this._extVoltage;
      }
   }

   public int getExtVoltage() throws YAPI_Exception {
      return this.get_extVoltage();
   }

   public static YDualPower FindDualPower(String func) {
      YAPIContext ctx = YAPI.GetYCtx(true);
      synchronized (ctx._functionCacheLock) {
         YDualPower obj = (YDualPower)YFunction._FindFromCache("DualPower", func);
         if (obj == null) {
            obj = new YDualPower(func);
            YFunction._AddToCache("DualPower", func, obj);
         }

         return obj;
      }
   }

   public static YDualPower FindDualPowerInContext(YAPIContext yctx, String func) {
      synchronized (yctx._functionCacheLock) {
         YDualPower obj = (YDualPower)YFunction._FindFromCacheInContext(yctx, "DualPower", func);
         if (obj == null) {
            obj = new YDualPower(yctx, func);
            YFunction._AddToCache("DualPower", func, obj);
         }

         return obj;
      }
   }

   public int registerValueCallback(YDualPower.UpdateCallback callback) {
      if (callback != null) {
         YFunction._UpdateValueCallbackList(this, true);
      } else {
         YFunction._UpdateValueCallbackList(this, false);
      }

      this._valueCallbackDualPower = callback;
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
      if (this._valueCallbackDualPower != null) {
         this._valueCallbackDualPower.yNewValue(this, value);
      } else {
         super._invokeValueCallback(value);
      }

      return 0;
   }

   public YDualPower nextDualPower() {
      String next_hwid;
      try {
         String hwid = this._yapi._yHash.resolveHwID(this._className, this._func);
         next_hwid = this._yapi._yHash.getNextHardwareId(this._className, hwid);
      } catch (YAPI_Exception var3) {
         next_hwid = null;
      }

      return next_hwid == null ? null : FindDualPowerInContext(this._yapi, next_hwid);
   }

   public static YDualPower FirstDualPower() {
      YAPIContext yctx = YAPI.GetYCtx(false);
      if (yctx == null) {
         return null;
      } else {
         String next_hwid = yctx._yHash.getFirstHardwareId("DualPower");
         return next_hwid == null ? null : FindDualPowerInContext(yctx, next_hwid);
      }
   }

   public static YDualPower FirstDualPowerInContext(YAPIContext yctx) {
      String next_hwid = yctx._yHash.getFirstHardwareId("DualPower");
      return next_hwid == null ? null : FindDualPowerInContext(yctx, next_hwid);
   }

   public interface TimedReportCallback {
      void timedReportCallback(YDualPower var1, YMeasure var2);
   }

   public interface UpdateCallback {
      void yNewValue(YDualPower var1, String var2);
   }
}
