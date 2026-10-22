package com.yoctopuce.YoctoAPI;

public class YLightSensor extends YSensor {
   public static final int MEASURETYPE_HUMAN_EYE = 0;
   public static final int MEASURETYPE_WIDE_SPECTRUM = 1;
   public static final int MEASURETYPE_INFRARED = 2;
   public static final int MEASURETYPE_HIGH_RATE = 3;
   public static final int MEASURETYPE_HIGH_ENERGY = 4;
   public static final int MEASURETYPE_HIGH_RESOLUTION = 5;
   public static final int MEASURETYPE_INVALID = -1;
   protected int _measureType = -1;
   protected YLightSensor.UpdateCallback _valueCallbackLightSensor = null;
   protected YLightSensor.TimedReportCallback _timedReportCallbackLightSensor = null;

   protected YLightSensor(YAPIContext ctx, String func) {
      super(ctx, func);
      this._className = "LightSensor";
   }

   protected YLightSensor(String func) {
      this(YAPI.GetYCtx(true), func);
   }

   @Override
   protected void _parseAttr(YJSONObject json_val) throws Exception {
      if (json_val.has("measureType")) {
         this._measureType = json_val.getInt("measureType");
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

   public int calibrate(double calibratedVal) throws YAPI_Exception {
      String rest_val = Long.toString(Math.round(calibratedVal * 65536.0));
      this._setAttr("currentValue", rest_val);
      return 0;
   }

   public int get_measureType() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -1 : this._measureType;
      }
   }

   public int getMeasureType() throws YAPI_Exception {
      return this.get_measureType();
   }

   public int set_measureType(int newval) throws YAPI_Exception {
      synchronized (this) {
         String rest_val = Integer.toString(newval);
         this._setAttr("measureType", rest_val);
         return 0;
      }
   }

   public int setMeasureType(int newval) throws YAPI_Exception {
      return this.set_measureType(newval);
   }

   public static YLightSensor FindLightSensor(String func) {
      YAPIContext ctx = YAPI.GetYCtx(true);
      synchronized (ctx._functionCacheLock) {
         YLightSensor obj = (YLightSensor)YFunction._FindFromCache("LightSensor", func);
         if (obj == null) {
            obj = new YLightSensor(func);
            YFunction._AddToCache("LightSensor", func, obj);
         }

         return obj;
      }
   }

   public static YLightSensor FindLightSensorInContext(YAPIContext yctx, String func) {
      synchronized (yctx._functionCacheLock) {
         YLightSensor obj = (YLightSensor)YFunction._FindFromCacheInContext(yctx, "LightSensor", func);
         if (obj == null) {
            obj = new YLightSensor(yctx, func);
            YFunction._AddToCache("LightSensor", func, obj);
         }

         return obj;
      }
   }

   public int registerValueCallback(YLightSensor.UpdateCallback callback) {
      if (callback != null) {
         YFunction._UpdateValueCallbackList(this, true);
      } else {
         YFunction._UpdateValueCallbackList(this, false);
      }

      this._valueCallbackLightSensor = callback;
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
      if (this._valueCallbackLightSensor != null) {
         this._valueCallbackLightSensor.yNewValue(this, value);
      } else {
         super._invokeValueCallback(value);
      }

      return 0;
   }

   public int registerTimedReportCallback(YLightSensor.TimedReportCallback callback) {
      if (callback != null) {
         YFunction._UpdateTimedReportCallbackList(this, true);
      } else {
         YFunction._UpdateTimedReportCallbackList(this, false);
      }

      this._timedReportCallbackLightSensor = callback;
      return 0;
   }

   @Override
   public int _invokeTimedReportCallback(YMeasure value) {
      if (this._timedReportCallbackLightSensor != null) {
         this._timedReportCallbackLightSensor.timedReportCallback(this, value);
      } else {
         super._invokeTimedReportCallback(value);
      }

      return 0;
   }

   public YLightSensor nextLightSensor() {
      String next_hwid;
      try {
         String hwid = this._yapi._yHash.resolveHwID(this._className, this._func);
         next_hwid = this._yapi._yHash.getNextHardwareId(this._className, hwid);
      } catch (YAPI_Exception var3) {
         next_hwid = null;
      }

      return next_hwid == null ? null : FindLightSensorInContext(this._yapi, next_hwid);
   }

   public static YLightSensor FirstLightSensor() {
      YAPIContext yctx = YAPI.GetYCtx(false);
      if (yctx == null) {
         return null;
      } else {
         String next_hwid = yctx._yHash.getFirstHardwareId("LightSensor");
         return next_hwid == null ? null : FindLightSensorInContext(yctx, next_hwid);
      }
   }

   public static YLightSensor FirstLightSensorInContext(YAPIContext yctx) {
      String next_hwid = yctx._yHash.getFirstHardwareId("LightSensor");
      return next_hwid == null ? null : FindLightSensorInContext(yctx, next_hwid);
   }

   public interface TimedReportCallback {
      void timedReportCallback(YLightSensor var1, YMeasure var2);
   }

   public interface UpdateCallback {
      void yNewValue(YLightSensor var1, String var2);
   }
}
