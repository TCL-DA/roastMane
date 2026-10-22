package com.yoctopuce.YoctoAPI;

public class YVoltage extends YSensor {
   public static final int ENABLED_FALSE = 0;
   public static final int ENABLED_TRUE = 1;
   public static final int ENABLED_INVALID = -1;
   protected int _enabled = -1;
   protected YVoltage.UpdateCallback _valueCallbackVoltage = null;
   protected YVoltage.TimedReportCallback _timedReportCallbackVoltage = null;

   protected YVoltage(YAPIContext ctx, String func) {
      super(ctx, func);
      this._className = "Voltage";
   }

   protected YVoltage(String func) {
      this(YAPI.GetYCtx(true), func);
   }

   @Override
   protected void _parseAttr(YJSONObject json_val) throws Exception {
      if (json_val.has("enabled")) {
         this._enabled = json_val.getInt("enabled") > 0 ? 1 : 0;
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

   public static YVoltage FindVoltage(String func) {
      YAPIContext ctx = YAPI.GetYCtx(true);
      synchronized (ctx._functionCacheLock) {
         YVoltage obj = (YVoltage)YFunction._FindFromCache("Voltage", func);
         if (obj == null) {
            obj = new YVoltage(func);
            YFunction._AddToCache("Voltage", func, obj);
         }

         return obj;
      }
   }

   public static YVoltage FindVoltageInContext(YAPIContext yctx, String func) {
      synchronized (yctx._functionCacheLock) {
         YVoltage obj = (YVoltage)YFunction._FindFromCacheInContext(yctx, "Voltage", func);
         if (obj == null) {
            obj = new YVoltage(yctx, func);
            YFunction._AddToCache("Voltage", func, obj);
         }

         return obj;
      }
   }

   public int registerValueCallback(YVoltage.UpdateCallback callback) {
      if (callback != null) {
         YFunction._UpdateValueCallbackList(this, true);
      } else {
         YFunction._UpdateValueCallbackList(this, false);
      }

      this._valueCallbackVoltage = callback;
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
      if (this._valueCallbackVoltage != null) {
         this._valueCallbackVoltage.yNewValue(this, value);
      } else {
         super._invokeValueCallback(value);
      }

      return 0;
   }

   public int registerTimedReportCallback(YVoltage.TimedReportCallback callback) {
      if (callback != null) {
         YFunction._UpdateTimedReportCallbackList(this, true);
      } else {
         YFunction._UpdateTimedReportCallbackList(this, false);
      }

      this._timedReportCallbackVoltage = callback;
      return 0;
   }

   @Override
   public int _invokeTimedReportCallback(YMeasure value) {
      if (this._timedReportCallbackVoltage != null) {
         this._timedReportCallbackVoltage.timedReportCallback(this, value);
      } else {
         super._invokeTimedReportCallback(value);
      }

      return 0;
   }

   public YVoltage nextVoltage() {
      String next_hwid;
      try {
         String hwid = this._yapi._yHash.resolveHwID(this._className, this._func);
         next_hwid = this._yapi._yHash.getNextHardwareId(this._className, hwid);
      } catch (YAPI_Exception var3) {
         next_hwid = null;
      }

      return next_hwid == null ? null : FindVoltageInContext(this._yapi, next_hwid);
   }

   public static YVoltage FirstVoltage() {
      YAPIContext yctx = YAPI.GetYCtx(false);
      if (yctx == null) {
         return null;
      } else {
         String next_hwid = yctx._yHash.getFirstHardwareId("Voltage");
         return next_hwid == null ? null : FindVoltageInContext(yctx, next_hwid);
      }
   }

   public static YVoltage FirstVoltageInContext(YAPIContext yctx) {
      String next_hwid = yctx._yHash.getFirstHardwareId("Voltage");
      return next_hwid == null ? null : FindVoltageInContext(yctx, next_hwid);
   }

   public interface TimedReportCallback {
      void timedReportCallback(YVoltage var1, YMeasure var2);
   }

   public interface UpdateCallback {
      void yNewValue(YVoltage var1, String var2);
   }
}
