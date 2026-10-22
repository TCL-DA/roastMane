package com.yoctopuce.YoctoAPI;

public class YCurrent extends YSensor {
   public static final int ENABLED_FALSE = 0;
   public static final int ENABLED_TRUE = 1;
   public static final int ENABLED_INVALID = -1;
   protected int _enabled = -1;
   protected YCurrent.UpdateCallback _valueCallbackCurrent = null;
   protected YCurrent.TimedReportCallback _timedReportCallbackCurrent = null;

   protected YCurrent(YAPIContext ctx, String func) {
      super(ctx, func);
      this._className = "Current";
   }

   protected YCurrent(String func) {
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

   public static YCurrent FindCurrent(String func) {
      YAPIContext ctx = YAPI.GetYCtx(true);
      synchronized (ctx._functionCacheLock) {
         YCurrent obj = (YCurrent)YFunction._FindFromCache("Current", func);
         if (obj == null) {
            obj = new YCurrent(func);
            YFunction._AddToCache("Current", func, obj);
         }

         return obj;
      }
   }

   public static YCurrent FindCurrentInContext(YAPIContext yctx, String func) {
      synchronized (yctx._functionCacheLock) {
         YCurrent obj = (YCurrent)YFunction._FindFromCacheInContext(yctx, "Current", func);
         if (obj == null) {
            obj = new YCurrent(yctx, func);
            YFunction._AddToCache("Current", func, obj);
         }

         return obj;
      }
   }

   public int registerValueCallback(YCurrent.UpdateCallback callback) {
      if (callback != null) {
         YFunction._UpdateValueCallbackList(this, true);
      } else {
         YFunction._UpdateValueCallbackList(this, false);
      }

      this._valueCallbackCurrent = callback;
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
      if (this._valueCallbackCurrent != null) {
         this._valueCallbackCurrent.yNewValue(this, value);
      } else {
         super._invokeValueCallback(value);
      }

      return 0;
   }

   public int registerTimedReportCallback(YCurrent.TimedReportCallback callback) {
      if (callback != null) {
         YFunction._UpdateTimedReportCallbackList(this, true);
      } else {
         YFunction._UpdateTimedReportCallbackList(this, false);
      }

      this._timedReportCallbackCurrent = callback;
      return 0;
   }

   @Override
   public int _invokeTimedReportCallback(YMeasure value) {
      if (this._timedReportCallbackCurrent != null) {
         this._timedReportCallbackCurrent.timedReportCallback(this, value);
      } else {
         super._invokeTimedReportCallback(value);
      }

      return 0;
   }

   public YCurrent nextCurrent() {
      String next_hwid;
      try {
         String hwid = this._yapi._yHash.resolveHwID(this._className, this._func);
         next_hwid = this._yapi._yHash.getNextHardwareId(this._className, hwid);
      } catch (YAPI_Exception var3) {
         next_hwid = null;
      }

      return next_hwid == null ? null : FindCurrentInContext(this._yapi, next_hwid);
   }

   public static YCurrent FirstCurrent() {
      YAPIContext yctx = YAPI.GetYCtx(false);
      if (yctx == null) {
         return null;
      } else {
         String next_hwid = yctx._yHash.getFirstHardwareId("Current");
         return next_hwid == null ? null : FindCurrentInContext(yctx, next_hwid);
      }
   }

   public static YCurrent FirstCurrentInContext(YAPIContext yctx) {
      String next_hwid = yctx._yHash.getFirstHardwareId("Current");
      return next_hwid == null ? null : FindCurrentInContext(yctx, next_hwid);
   }

   public interface TimedReportCallback {
      void timedReportCallback(YCurrent var1, YMeasure var2);
   }

   public interface UpdateCallback {
      void yNewValue(YCurrent var1, String var2);
   }
}
