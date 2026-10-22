package com.yoctopuce.YoctoAPI;

public class YAltitude extends YSensor {
   public static final double QNH_INVALID = -1.79769313486231E308;
   public static final String TECHNOLOGY_INVALID = "!INVALID!";
   protected double _qnh = -1.79769313486231E308;
   protected String _technology = "!INVALID!";
   protected YAltitude.UpdateCallback _valueCallbackAltitude = null;
   protected YAltitude.TimedReportCallback _timedReportCallbackAltitude = null;

   protected YAltitude(YAPIContext ctx, String func) {
      super(ctx, func);
      this._className = "Altitude";
   }

   protected YAltitude(String func) {
      this(YAPI.GetYCtx(true), func);
   }

   @Override
   protected void _parseAttr(YJSONObject json_val) throws Exception {
      if (json_val.has("qnh")) {
         this._qnh = (double)Math.round(json_val.getDouble("qnh") * 1000.0 / 65536.0) / 1000.0;
      }

      if (json_val.has("technology")) {
         this._technology = json_val.getString("technology");
      }

      super._parseAttr(json_val);
   }

   public int set_currentValue(double newval) throws YAPI_Exception {
      synchronized (this) {
         String rest_val = Long.toString(Math.round(newval * 65536.0));
         this._setAttr("currentValue", rest_val);
         return 0;
      }
   }

   public int setCurrentValue(double newval) throws YAPI_Exception {
      return this.set_currentValue(newval);
   }

   public int set_qnh(double newval) throws YAPI_Exception {
      synchronized (this) {
         String rest_val = Long.toString(Math.round(newval * 65536.0));
         this._setAttr("qnh", rest_val);
         return 0;
      }
   }

   public int setQnh(double newval) throws YAPI_Exception {
      return this.set_qnh(newval);
   }

   public double get_qnh() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -1.79769313486231E308 : this._qnh;
      }
   }

   public double getQnh() throws YAPI_Exception {
      return this.get_qnh();
   }

   public String get_technology() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? "!INVALID!" : this._technology;
      }
   }

   public String getTechnology() throws YAPI_Exception {
      return this.get_technology();
   }

   public static YAltitude FindAltitude(String func) {
      YAPIContext ctx = YAPI.GetYCtx(true);
      synchronized (ctx._functionCacheLock) {
         YAltitude obj = (YAltitude)YFunction._FindFromCache("Altitude", func);
         if (obj == null) {
            obj = new YAltitude(func);
            YFunction._AddToCache("Altitude", func, obj);
         }

         return obj;
      }
   }

   public static YAltitude FindAltitudeInContext(YAPIContext yctx, String func) {
      synchronized (yctx._functionCacheLock) {
         YAltitude obj = (YAltitude)YFunction._FindFromCacheInContext(yctx, "Altitude", func);
         if (obj == null) {
            obj = new YAltitude(yctx, func);
            YFunction._AddToCache("Altitude", func, obj);
         }

         return obj;
      }
   }

   public int registerValueCallback(YAltitude.UpdateCallback callback) {
      if (callback != null) {
         YFunction._UpdateValueCallbackList(this, true);
      } else {
         YFunction._UpdateValueCallbackList(this, false);
      }

      this._valueCallbackAltitude = callback;
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
      if (this._valueCallbackAltitude != null) {
         this._valueCallbackAltitude.yNewValue(this, value);
      } else {
         super._invokeValueCallback(value);
      }

      return 0;
   }

   public int registerTimedReportCallback(YAltitude.TimedReportCallback callback) {
      if (callback != null) {
         YFunction._UpdateTimedReportCallbackList(this, true);
      } else {
         YFunction._UpdateTimedReportCallbackList(this, false);
      }

      this._timedReportCallbackAltitude = callback;
      return 0;
   }

   @Override
   public int _invokeTimedReportCallback(YMeasure value) {
      if (this._timedReportCallbackAltitude != null) {
         this._timedReportCallbackAltitude.timedReportCallback(this, value);
      } else {
         super._invokeTimedReportCallback(value);
      }

      return 0;
   }

   public YAltitude nextAltitude() {
      String next_hwid;
      try {
         String hwid = this._yapi._yHash.resolveHwID(this._className, this._func);
         next_hwid = this._yapi._yHash.getNextHardwareId(this._className, hwid);
      } catch (YAPI_Exception var3) {
         next_hwid = null;
      }

      return next_hwid == null ? null : FindAltitudeInContext(this._yapi, next_hwid);
   }

   public static YAltitude FirstAltitude() {
      YAPIContext yctx = YAPI.GetYCtx(false);
      if (yctx == null) {
         return null;
      } else {
         String next_hwid = yctx._yHash.getFirstHardwareId("Altitude");
         return next_hwid == null ? null : FindAltitudeInContext(yctx, next_hwid);
      }
   }

   public static YAltitude FirstAltitudeInContext(YAPIContext yctx) {
      String next_hwid = yctx._yHash.getFirstHardwareId("Altitude");
      return next_hwid == null ? null : FindAltitudeInContext(yctx, next_hwid);
   }

   public interface TimedReportCallback {
      void timedReportCallback(YAltitude var1, YMeasure var2);
   }

   public interface UpdateCallback {
      void yNewValue(YAltitude var1, String var2);
   }
}
