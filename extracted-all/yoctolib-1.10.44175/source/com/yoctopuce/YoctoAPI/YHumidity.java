package com.yoctopuce.YoctoAPI;

public class YHumidity extends YSensor {
   public static final double RELHUM_INVALID = -1.79769313486231E308;
   public static final double ABSHUM_INVALID = -1.79769313486231E308;
   protected double _relHum = -1.79769313486231E308;
   protected double _absHum = -1.79769313486231E308;
   protected YHumidity.UpdateCallback _valueCallbackHumidity = null;
   protected YHumidity.TimedReportCallback _timedReportCallbackHumidity = null;

   protected YHumidity(YAPIContext ctx, String func) {
      super(ctx, func);
      this._className = "Humidity";
   }

   protected YHumidity(String func) {
      this(YAPI.GetYCtx(true), func);
   }

   @Override
   protected void _parseAttr(YJSONObject json_val) throws Exception {
      if (json_val.has("relHum")) {
         this._relHum = (double)Math.round(json_val.getDouble("relHum") * 1000.0 / 65536.0) / 1000.0;
      }

      if (json_val.has("absHum")) {
         this._absHum = (double)Math.round(json_val.getDouble("absHum") * 1000.0 / 65536.0) / 1000.0;
      }

      super._parseAttr(json_val);
   }

   public int set_unit(String newval) throws YAPI_Exception {
      synchronized (this) {
         this._setAttr("unit", newval);
         return 0;
      }
   }

   public int setUnit(String newval) throws YAPI_Exception {
      return this.set_unit(newval);
   }

   public double get_relHum() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -1.79769313486231E308 : this._relHum;
      }
   }

   public double getRelHum() throws YAPI_Exception {
      return this.get_relHum();
   }

   public double get_absHum() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -1.79769313486231E308 : this._absHum;
      }
   }

   public double getAbsHum() throws YAPI_Exception {
      return this.get_absHum();
   }

   public static YHumidity FindHumidity(String func) {
      YAPIContext ctx = YAPI.GetYCtx(true);
      synchronized (ctx._functionCacheLock) {
         YHumidity obj = (YHumidity)YFunction._FindFromCache("Humidity", func);
         if (obj == null) {
            obj = new YHumidity(func);
            YFunction._AddToCache("Humidity", func, obj);
         }

         return obj;
      }
   }

   public static YHumidity FindHumidityInContext(YAPIContext yctx, String func) {
      synchronized (yctx._functionCacheLock) {
         YHumidity obj = (YHumidity)YFunction._FindFromCacheInContext(yctx, "Humidity", func);
         if (obj == null) {
            obj = new YHumidity(yctx, func);
            YFunction._AddToCache("Humidity", func, obj);
         }

         return obj;
      }
   }

   public int registerValueCallback(YHumidity.UpdateCallback callback) {
      if (callback != null) {
         YFunction._UpdateValueCallbackList(this, true);
      } else {
         YFunction._UpdateValueCallbackList(this, false);
      }

      this._valueCallbackHumidity = callback;
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
      if (this._valueCallbackHumidity != null) {
         this._valueCallbackHumidity.yNewValue(this, value);
      } else {
         super._invokeValueCallback(value);
      }

      return 0;
   }

   public int registerTimedReportCallback(YHumidity.TimedReportCallback callback) {
      if (callback != null) {
         YFunction._UpdateTimedReportCallbackList(this, true);
      } else {
         YFunction._UpdateTimedReportCallbackList(this, false);
      }

      this._timedReportCallbackHumidity = callback;
      return 0;
   }

   @Override
   public int _invokeTimedReportCallback(YMeasure value) {
      if (this._timedReportCallbackHumidity != null) {
         this._timedReportCallbackHumidity.timedReportCallback(this, value);
      } else {
         super._invokeTimedReportCallback(value);
      }

      return 0;
   }

   public YHumidity nextHumidity() {
      String next_hwid;
      try {
         String hwid = this._yapi._yHash.resolveHwID(this._className, this._func);
         next_hwid = this._yapi._yHash.getNextHardwareId(this._className, hwid);
      } catch (YAPI_Exception var3) {
         next_hwid = null;
      }

      return next_hwid == null ? null : FindHumidityInContext(this._yapi, next_hwid);
   }

   public static YHumidity FirstHumidity() {
      YAPIContext yctx = YAPI.GetYCtx(false);
      if (yctx == null) {
         return null;
      } else {
         String next_hwid = yctx._yHash.getFirstHardwareId("Humidity");
         return next_hwid == null ? null : FindHumidityInContext(yctx, next_hwid);
      }
   }

   public static YHumidity FirstHumidityInContext(YAPIContext yctx) {
      String next_hwid = yctx._yHash.getFirstHardwareId("Humidity");
      return next_hwid == null ? null : FindHumidityInContext(yctx, next_hwid);
   }

   public interface TimedReportCallback {
      void timedReportCallback(YHumidity var1, YMeasure var2);
   }

   public interface UpdateCallback {
      void yNewValue(YHumidity var1, String var2);
   }
}
