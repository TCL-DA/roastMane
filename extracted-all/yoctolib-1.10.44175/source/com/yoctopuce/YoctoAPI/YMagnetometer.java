package com.yoctopuce.YoctoAPI;

public class YMagnetometer extends YSensor {
   public static final int BANDWIDTH_INVALID = -1;
   public static final double XVALUE_INVALID = -1.79769313486231E308;
   public static final double YVALUE_INVALID = -1.79769313486231E308;
   public static final double ZVALUE_INVALID = -1.79769313486231E308;
   protected int _bandwidth = -1;
   protected double _xValue = -1.79769313486231E308;
   protected double _yValue = -1.79769313486231E308;
   protected double _zValue = -1.79769313486231E308;
   protected YMagnetometer.UpdateCallback _valueCallbackMagnetometer = null;
   protected YMagnetometer.TimedReportCallback _timedReportCallbackMagnetometer = null;

   protected YMagnetometer(YAPIContext ctx, String func) {
      super(ctx, func);
      this._className = "Magnetometer";
   }

   protected YMagnetometer(String func) {
      this(YAPI.GetYCtx(true), func);
   }

   @Override
   protected void _parseAttr(YJSONObject json_val) throws Exception {
      if (json_val.has("bandwidth")) {
         this._bandwidth = json_val.getInt("bandwidth");
      }

      if (json_val.has("xValue")) {
         this._xValue = (double)Math.round(json_val.getDouble("xValue") * 1000.0 / 65536.0) / 1000.0;
      }

      if (json_val.has("yValue")) {
         this._yValue = (double)Math.round(json_val.getDouble("yValue") * 1000.0 / 65536.0) / 1000.0;
      }

      if (json_val.has("zValue")) {
         this._zValue = (double)Math.round(json_val.getDouble("zValue") * 1000.0 / 65536.0) / 1000.0;
      }

      super._parseAttr(json_val);
   }

   public int get_bandwidth() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -1 : this._bandwidth;
      }
   }

   public int getBandwidth() throws YAPI_Exception {
      return this.get_bandwidth();
   }

   public int set_bandwidth(int newval) throws YAPI_Exception {
      synchronized (this) {
         String rest_val = Integer.toString(newval);
         this._setAttr("bandwidth", rest_val);
         return 0;
      }
   }

   public int setBandwidth(int newval) throws YAPI_Exception {
      return this.set_bandwidth(newval);
   }

   public double get_xValue() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -1.79769313486231E308 : this._xValue;
      }
   }

   public double getXValue() throws YAPI_Exception {
      return this.get_xValue();
   }

   public double get_yValue() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -1.79769313486231E308 : this._yValue;
      }
   }

   public double getYValue() throws YAPI_Exception {
      return this.get_yValue();
   }

   public double get_zValue() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -1.79769313486231E308 : this._zValue;
      }
   }

   public double getZValue() throws YAPI_Exception {
      return this.get_zValue();
   }

   public static YMagnetometer FindMagnetometer(String func) {
      YAPIContext ctx = YAPI.GetYCtx(true);
      synchronized (ctx._functionCacheLock) {
         YMagnetometer obj = (YMagnetometer)YFunction._FindFromCache("Magnetometer", func);
         if (obj == null) {
            obj = new YMagnetometer(func);
            YFunction._AddToCache("Magnetometer", func, obj);
         }

         return obj;
      }
   }

   public static YMagnetometer FindMagnetometerInContext(YAPIContext yctx, String func) {
      synchronized (yctx._functionCacheLock) {
         YMagnetometer obj = (YMagnetometer)YFunction._FindFromCacheInContext(yctx, "Magnetometer", func);
         if (obj == null) {
            obj = new YMagnetometer(yctx, func);
            YFunction._AddToCache("Magnetometer", func, obj);
         }

         return obj;
      }
   }

   public int registerValueCallback(YMagnetometer.UpdateCallback callback) {
      if (callback != null) {
         YFunction._UpdateValueCallbackList(this, true);
      } else {
         YFunction._UpdateValueCallbackList(this, false);
      }

      this._valueCallbackMagnetometer = callback;
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
      if (this._valueCallbackMagnetometer != null) {
         this._valueCallbackMagnetometer.yNewValue(this, value);
      } else {
         super._invokeValueCallback(value);
      }

      return 0;
   }

   public int registerTimedReportCallback(YMagnetometer.TimedReportCallback callback) {
      if (callback != null) {
         YFunction._UpdateTimedReportCallbackList(this, true);
      } else {
         YFunction._UpdateTimedReportCallbackList(this, false);
      }

      this._timedReportCallbackMagnetometer = callback;
      return 0;
   }

   @Override
   public int _invokeTimedReportCallback(YMeasure value) {
      if (this._timedReportCallbackMagnetometer != null) {
         this._timedReportCallbackMagnetometer.timedReportCallback(this, value);
      } else {
         super._invokeTimedReportCallback(value);
      }

      return 0;
   }

   public YMagnetometer nextMagnetometer() {
      String next_hwid;
      try {
         String hwid = this._yapi._yHash.resolveHwID(this._className, this._func);
         next_hwid = this._yapi._yHash.getNextHardwareId(this._className, hwid);
      } catch (YAPI_Exception var3) {
         next_hwid = null;
      }

      return next_hwid == null ? null : FindMagnetometerInContext(this._yapi, next_hwid);
   }

   public static YMagnetometer FirstMagnetometer() {
      YAPIContext yctx = YAPI.GetYCtx(false);
      if (yctx == null) {
         return null;
      } else {
         String next_hwid = yctx._yHash.getFirstHardwareId("Magnetometer");
         return next_hwid == null ? null : FindMagnetometerInContext(yctx, next_hwid);
      }
   }

   public static YMagnetometer FirstMagnetometerInContext(YAPIContext yctx) {
      String next_hwid = yctx._yHash.getFirstHardwareId("Magnetometer");
      return next_hwid == null ? null : FindMagnetometerInContext(yctx, next_hwid);
   }

   public interface TimedReportCallback {
      void timedReportCallback(YMagnetometer var1, YMeasure var2);
   }

   public interface UpdateCallback {
      void yNewValue(YMagnetometer var1, String var2);
   }
}
