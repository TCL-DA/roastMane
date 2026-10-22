package com.yoctopuce.YoctoAPI;

public class YAccelerometer extends YSensor {
   public static final int BANDWIDTH_INVALID = -1;
   public static final double XVALUE_INVALID = -1.79769313486231E308;
   public static final double YVALUE_INVALID = -1.79769313486231E308;
   public static final double ZVALUE_INVALID = -1.79769313486231E308;
   public static final int GRAVITYCANCELLATION_OFF = 0;
   public static final int GRAVITYCANCELLATION_ON = 1;
   public static final int GRAVITYCANCELLATION_INVALID = -1;
   protected int _bandwidth = -1;
   protected double _xValue = -1.79769313486231E308;
   protected double _yValue = -1.79769313486231E308;
   protected double _zValue = -1.79769313486231E308;
   protected int _gravityCancellation = -1;
   protected YAccelerometer.UpdateCallback _valueCallbackAccelerometer = null;
   protected YAccelerometer.TimedReportCallback _timedReportCallbackAccelerometer = null;

   protected YAccelerometer(YAPIContext ctx, String func) {
      super(ctx, func);
      this._className = "Accelerometer";
   }

   protected YAccelerometer(String func) {
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

      if (json_val.has("gravityCancellation")) {
         this._gravityCancellation = json_val.getInt("gravityCancellation") > 0 ? 1 : 0;
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

   public int get_gravityCancellation() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -1 : this._gravityCancellation;
      }
   }

   public int set_gravityCancellation(int newval) throws YAPI_Exception {
      synchronized (this) {
         String rest_val = newval > 0 ? "1" : "0";
         this._setAttr("gravityCancellation", rest_val);
         return 0;
      }
   }

   public static YAccelerometer FindAccelerometer(String func) {
      YAPIContext ctx = YAPI.GetYCtx(true);
      synchronized (ctx._functionCacheLock) {
         YAccelerometer obj = (YAccelerometer)YFunction._FindFromCache("Accelerometer", func);
         if (obj == null) {
            obj = new YAccelerometer(func);
            YFunction._AddToCache("Accelerometer", func, obj);
         }

         return obj;
      }
   }

   public static YAccelerometer FindAccelerometerInContext(YAPIContext yctx, String func) {
      synchronized (yctx._functionCacheLock) {
         YAccelerometer obj = (YAccelerometer)YFunction._FindFromCacheInContext(yctx, "Accelerometer", func);
         if (obj == null) {
            obj = new YAccelerometer(yctx, func);
            YFunction._AddToCache("Accelerometer", func, obj);
         }

         return obj;
      }
   }

   public int registerValueCallback(YAccelerometer.UpdateCallback callback) {
      if (callback != null) {
         YFunction._UpdateValueCallbackList(this, true);
      } else {
         YFunction._UpdateValueCallbackList(this, false);
      }

      this._valueCallbackAccelerometer = callback;
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
      if (this._valueCallbackAccelerometer != null) {
         this._valueCallbackAccelerometer.yNewValue(this, value);
      } else {
         super._invokeValueCallback(value);
      }

      return 0;
   }

   public int registerTimedReportCallback(YAccelerometer.TimedReportCallback callback) {
      if (callback != null) {
         YFunction._UpdateTimedReportCallbackList(this, true);
      } else {
         YFunction._UpdateTimedReportCallbackList(this, false);
      }

      this._timedReportCallbackAccelerometer = callback;
      return 0;
   }

   @Override
   public int _invokeTimedReportCallback(YMeasure value) {
      if (this._timedReportCallbackAccelerometer != null) {
         this._timedReportCallbackAccelerometer.timedReportCallback(this, value);
      } else {
         super._invokeTimedReportCallback(value);
      }

      return 0;
   }

   public YAccelerometer nextAccelerometer() {
      String next_hwid;
      try {
         String hwid = this._yapi._yHash.resolveHwID(this._className, this._func);
         next_hwid = this._yapi._yHash.getNextHardwareId(this._className, hwid);
      } catch (YAPI_Exception var3) {
         next_hwid = null;
      }

      return next_hwid == null ? null : FindAccelerometerInContext(this._yapi, next_hwid);
   }

   public static YAccelerometer FirstAccelerometer() {
      YAPIContext yctx = YAPI.GetYCtx(false);
      if (yctx == null) {
         return null;
      } else {
         String next_hwid = yctx._yHash.getFirstHardwareId("Accelerometer");
         return next_hwid == null ? null : FindAccelerometerInContext(yctx, next_hwid);
      }
   }

   public static YAccelerometer FirstAccelerometerInContext(YAPIContext yctx) {
      String next_hwid = yctx._yHash.getFirstHardwareId("Accelerometer");
      return next_hwid == null ? null : FindAccelerometerInContext(yctx, next_hwid);
   }

   public interface TimedReportCallback {
      void timedReportCallback(YAccelerometer var1, YMeasure var2);
   }

   public interface UpdateCallback {
      void yNewValue(YAccelerometer var1, String var2);
   }
}
