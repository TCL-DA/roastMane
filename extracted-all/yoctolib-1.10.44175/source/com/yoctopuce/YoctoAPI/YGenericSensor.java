package com.yoctopuce.YoctoAPI;

public class YGenericSensor extends YSensor {
   public static final double SIGNALVALUE_INVALID = -1.79769313486231E308;
   public static final String SIGNALUNIT_INVALID = "!INVALID!";
   public static final String SIGNALRANGE_INVALID = "!INVALID!";
   public static final String VALUERANGE_INVALID = "!INVALID!";
   public static final double SIGNALBIAS_INVALID = -1.79769313486231E308;
   public static final int SIGNALSAMPLING_HIGH_RATE = 0;
   public static final int SIGNALSAMPLING_HIGH_RATE_FILTERED = 1;
   public static final int SIGNALSAMPLING_LOW_NOISE = 2;
   public static final int SIGNALSAMPLING_LOW_NOISE_FILTERED = 3;
   public static final int SIGNALSAMPLING_HIGHEST_RATE = 4;
   public static final int SIGNALSAMPLING_INVALID = -1;
   public static final int ENABLED_FALSE = 0;
   public static final int ENABLED_TRUE = 1;
   public static final int ENABLED_INVALID = -1;
   protected double _signalValue = -1.79769313486231E308;
   protected String _signalUnit = "!INVALID!";
   protected String _signalRange = "!INVALID!";
   protected String _valueRange = "!INVALID!";
   protected double _signalBias = -1.79769313486231E308;
   protected int _signalSampling = -1;
   protected int _enabled = -1;
   protected YGenericSensor.UpdateCallback _valueCallbackGenericSensor = null;
   protected YGenericSensor.TimedReportCallback _timedReportCallbackGenericSensor = null;

   protected YGenericSensor(YAPIContext ctx, String func) {
      super(ctx, func);
      this._className = "GenericSensor";
   }

   protected YGenericSensor(String func) {
      this(YAPI.GetYCtx(true), func);
   }

   @Override
   protected void _parseAttr(YJSONObject json_val) throws Exception {
      if (json_val.has("signalValue")) {
         this._signalValue = (double)Math.round(json_val.getDouble("signalValue") * 1000.0 / 65536.0) / 1000.0;
      }

      if (json_val.has("signalUnit")) {
         this._signalUnit = json_val.getString("signalUnit");
      }

      if (json_val.has("signalRange")) {
         this._signalRange = json_val.getString("signalRange");
      }

      if (json_val.has("valueRange")) {
         this._valueRange = json_val.getString("valueRange");
      }

      if (json_val.has("signalBias")) {
         this._signalBias = (double)Math.round(json_val.getDouble("signalBias") * 1000.0 / 65536.0) / 1000.0;
      }

      if (json_val.has("signalSampling")) {
         this._signalSampling = json_val.getInt("signalSampling");
      }

      if (json_val.has("enabled")) {
         this._enabled = json_val.getInt("enabled") > 0 ? 1 : 0;
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

   public double get_signalValue() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0
            ? -1.79769313486231E308
            : (double)Math.round(this._signalValue * 1000.0) / 1000.0;
      }
   }

   public double getSignalValue() throws YAPI_Exception {
      return this.get_signalValue();
   }

   public String get_signalUnit() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration == 0L && this.load(this._yapi._defaultCacheValidity) != 0 ? "!INVALID!" : this._signalUnit;
      }
   }

   public String getSignalUnit() throws YAPI_Exception {
      return this.get_signalUnit();
   }

   public String get_signalRange() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? "!INVALID!" : this._signalRange;
      }
   }

   public String getSignalRange() throws YAPI_Exception {
      return this.get_signalRange();
   }

   public int set_signalRange(String newval) throws YAPI_Exception {
      synchronized (this) {
         this._setAttr("signalRange", newval);
         return 0;
      }
   }

   public int setSignalRange(String newval) throws YAPI_Exception {
      return this.set_signalRange(newval);
   }

   public String get_valueRange() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? "!INVALID!" : this._valueRange;
      }
   }

   public String getValueRange() throws YAPI_Exception {
      return this.get_valueRange();
   }

   public int set_valueRange(String newval) throws YAPI_Exception {
      synchronized (this) {
         this._setAttr("valueRange", newval);
         return 0;
      }
   }

   public int setValueRange(String newval) throws YAPI_Exception {
      return this.set_valueRange(newval);
   }

   public int set_signalBias(double newval) throws YAPI_Exception {
      synchronized (this) {
         String rest_val = Long.toString(Math.round(newval * 65536.0));
         this._setAttr("signalBias", rest_val);
         return 0;
      }
   }

   public int setSignalBias(double newval) throws YAPI_Exception {
      return this.set_signalBias(newval);
   }

   public double get_signalBias() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0
            ? -1.79769313486231E308
            : this._signalBias;
      }
   }

   public double getSignalBias() throws YAPI_Exception {
      return this.get_signalBias();
   }

   public int get_signalSampling() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -1 : this._signalSampling;
      }
   }

   public int getSignalSampling() throws YAPI_Exception {
      return this.get_signalSampling();
   }

   public int set_signalSampling(int newval) throws YAPI_Exception {
      synchronized (this) {
         String rest_val = Integer.toString(newval);
         this._setAttr("signalSampling", rest_val);
         return 0;
      }
   }

   public int setSignalSampling(int newval) throws YAPI_Exception {
      return this.set_signalSampling(newval);
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

   public static YGenericSensor FindGenericSensor(String func) {
      YAPIContext ctx = YAPI.GetYCtx(true);
      synchronized (ctx._functionCacheLock) {
         YGenericSensor obj = (YGenericSensor)YFunction._FindFromCache("GenericSensor", func);
         if (obj == null) {
            obj = new YGenericSensor(func);
            YFunction._AddToCache("GenericSensor", func, obj);
         }

         return obj;
      }
   }

   public static YGenericSensor FindGenericSensorInContext(YAPIContext yctx, String func) {
      synchronized (yctx._functionCacheLock) {
         YGenericSensor obj = (YGenericSensor)YFunction._FindFromCacheInContext(yctx, "GenericSensor", func);
         if (obj == null) {
            obj = new YGenericSensor(yctx, func);
            YFunction._AddToCache("GenericSensor", func, obj);
         }

         return obj;
      }
   }

   public int registerValueCallback(YGenericSensor.UpdateCallback callback) {
      if (callback != null) {
         YFunction._UpdateValueCallbackList(this, true);
      } else {
         YFunction._UpdateValueCallbackList(this, false);
      }

      this._valueCallbackGenericSensor = callback;
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
      if (this._valueCallbackGenericSensor != null) {
         this._valueCallbackGenericSensor.yNewValue(this, value);
      } else {
         super._invokeValueCallback(value);
      }

      return 0;
   }

   public int registerTimedReportCallback(YGenericSensor.TimedReportCallback callback) {
      if (callback != null) {
         YFunction._UpdateTimedReportCallbackList(this, true);
      } else {
         YFunction._UpdateTimedReportCallbackList(this, false);
      }

      this._timedReportCallbackGenericSensor = callback;
      return 0;
   }

   @Override
   public int _invokeTimedReportCallback(YMeasure value) {
      if (this._timedReportCallbackGenericSensor != null) {
         this._timedReportCallbackGenericSensor.timedReportCallback(this, value);
      } else {
         super._invokeTimedReportCallback(value);
      }

      return 0;
   }

   public int zeroAdjust() throws YAPI_Exception {
      double currSignal = this.get_signalValue();
      double currBias = this.get_signalBias();
      return this.set_signalBias(currSignal + currBias);
   }

   public YGenericSensor nextGenericSensor() {
      String next_hwid;
      try {
         String hwid = this._yapi._yHash.resolveHwID(this._className, this._func);
         next_hwid = this._yapi._yHash.getNextHardwareId(this._className, hwid);
      } catch (YAPI_Exception var3) {
         next_hwid = null;
      }

      return next_hwid == null ? null : FindGenericSensorInContext(this._yapi, next_hwid);
   }

   public static YGenericSensor FirstGenericSensor() {
      YAPIContext yctx = YAPI.GetYCtx(false);
      if (yctx == null) {
         return null;
      } else {
         String next_hwid = yctx._yHash.getFirstHardwareId("GenericSensor");
         return next_hwid == null ? null : FindGenericSensorInContext(yctx, next_hwid);
      }
   }

   public static YGenericSensor FirstGenericSensorInContext(YAPIContext yctx) {
      String next_hwid = yctx._yHash.getFirstHardwareId("GenericSensor");
      return next_hwid == null ? null : FindGenericSensorInContext(yctx, next_hwid);
   }

   public interface TimedReportCallback {
      void timedReportCallback(YGenericSensor var1, YMeasure var2);
   }

   public interface UpdateCallback {
      void yNewValue(YGenericSensor var1, String var2);
   }
}
