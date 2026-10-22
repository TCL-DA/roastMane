package com.yoctopuce.YoctoAPI;

public class YCompass extends YSensor {
   public static final int BANDWIDTH_INVALID = -1;
   public static final int AXIS_X = 0;
   public static final int AXIS_Y = 1;
   public static final int AXIS_Z = 2;
   public static final int AXIS_INVALID = -1;
   public static final double MAGNETICHEADING_INVALID = -1.79769313486231E308;
   protected int _bandwidth = -1;
   protected int _axis = -1;
   protected double _magneticHeading = -1.79769313486231E308;
   protected YCompass.UpdateCallback _valueCallbackCompass = null;
   protected YCompass.TimedReportCallback _timedReportCallbackCompass = null;

   protected YCompass(YAPIContext ctx, String func) {
      super(ctx, func);
      this._className = "Compass";
   }

   protected YCompass(String func) {
      this(YAPI.GetYCtx(true), func);
   }

   @Override
   protected void _parseAttr(YJSONObject json_val) throws Exception {
      if (json_val.has("bandwidth")) {
         this._bandwidth = json_val.getInt("bandwidth");
      }

      if (json_val.has("axis")) {
         this._axis = json_val.getInt("axis");
      }

      if (json_val.has("magneticHeading")) {
         this._magneticHeading = (double)Math.round(json_val.getDouble("magneticHeading") * 1000.0 / 65536.0) / 1000.0;
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

   public int get_axis() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -1 : this._axis;
      }
   }

   public double get_magneticHeading() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0
            ? -1.79769313486231E308
            : this._magneticHeading;
      }
   }

   public double getMagneticHeading() throws YAPI_Exception {
      return this.get_magneticHeading();
   }

   public static YCompass FindCompass(String func) {
      YAPIContext ctx = YAPI.GetYCtx(true);
      synchronized (ctx._functionCacheLock) {
         YCompass obj = (YCompass)YFunction._FindFromCache("Compass", func);
         if (obj == null) {
            obj = new YCompass(func);
            YFunction._AddToCache("Compass", func, obj);
         }

         return obj;
      }
   }

   public static YCompass FindCompassInContext(YAPIContext yctx, String func) {
      synchronized (yctx._functionCacheLock) {
         YCompass obj = (YCompass)YFunction._FindFromCacheInContext(yctx, "Compass", func);
         if (obj == null) {
            obj = new YCompass(yctx, func);
            YFunction._AddToCache("Compass", func, obj);
         }

         return obj;
      }
   }

   public int registerValueCallback(YCompass.UpdateCallback callback) {
      if (callback != null) {
         YFunction._UpdateValueCallbackList(this, true);
      } else {
         YFunction._UpdateValueCallbackList(this, false);
      }

      this._valueCallbackCompass = callback;
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
      if (this._valueCallbackCompass != null) {
         this._valueCallbackCompass.yNewValue(this, value);
      } else {
         super._invokeValueCallback(value);
      }

      return 0;
   }

   public int registerTimedReportCallback(YCompass.TimedReportCallback callback) {
      if (callback != null) {
         YFunction._UpdateTimedReportCallbackList(this, true);
      } else {
         YFunction._UpdateTimedReportCallbackList(this, false);
      }

      this._timedReportCallbackCompass = callback;
      return 0;
   }

   @Override
   public int _invokeTimedReportCallback(YMeasure value) {
      if (this._timedReportCallbackCompass != null) {
         this._timedReportCallbackCompass.timedReportCallback(this, value);
      } else {
         super._invokeTimedReportCallback(value);
      }

      return 0;
   }

   public YCompass nextCompass() {
      String next_hwid;
      try {
         String hwid = this._yapi._yHash.resolveHwID(this._className, this._func);
         next_hwid = this._yapi._yHash.getNextHardwareId(this._className, hwid);
      } catch (YAPI_Exception var3) {
         next_hwid = null;
      }

      return next_hwid == null ? null : FindCompassInContext(this._yapi, next_hwid);
   }

   public static YCompass FirstCompass() {
      YAPIContext yctx = YAPI.GetYCtx(false);
      if (yctx == null) {
         return null;
      } else {
         String next_hwid = yctx._yHash.getFirstHardwareId("Compass");
         return next_hwid == null ? null : FindCompassInContext(yctx, next_hwid);
      }
   }

   public static YCompass FirstCompassInContext(YAPIContext yctx) {
      String next_hwid = yctx._yHash.getFirstHardwareId("Compass");
      return next_hwid == null ? null : FindCompassInContext(yctx, next_hwid);
   }

   public interface TimedReportCallback {
      void timedReportCallback(YCompass var1, YMeasure var2);
   }

   public interface UpdateCallback {
      void yNewValue(YCompass var1, String var2);
   }
}
