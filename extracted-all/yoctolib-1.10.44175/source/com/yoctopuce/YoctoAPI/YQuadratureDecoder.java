package com.yoctopuce.YoctoAPI;

public class YQuadratureDecoder extends YSensor {
   public static final double SPEED_INVALID = -1.79769313486231E308;
   public static final int DECODING_OFF = 0;
   public static final int DECODING_ON = 1;
   public static final int DECODING_DIV2 = 2;
   public static final int DECODING_DIV4 = 3;
   public static final int DECODING_INVALID = -1;
   protected double _speed = -1.79769313486231E308;
   protected int _decoding = -1;
   protected YQuadratureDecoder.UpdateCallback _valueCallbackQuadratureDecoder = null;
   protected YQuadratureDecoder.TimedReportCallback _timedReportCallbackQuadratureDecoder = null;

   protected YQuadratureDecoder(YAPIContext ctx, String func) {
      super(ctx, func);
      this._className = "QuadratureDecoder";
   }

   protected YQuadratureDecoder(String func) {
      this(YAPI.GetYCtx(true), func);
   }

   @Override
   protected void _parseAttr(YJSONObject json_val) throws Exception {
      if (json_val.has("speed")) {
         this._speed = (double)Math.round(json_val.getDouble("speed") * 1000.0 / 65536.0) / 1000.0;
      }

      if (json_val.has("decoding")) {
         this._decoding = json_val.getInt("decoding");
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

   public double get_speed() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -1.79769313486231E308 : this._speed;
      }
   }

   public double getSpeed() throws YAPI_Exception {
      return this.get_speed();
   }

   public int get_decoding() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -1 : this._decoding;
      }
   }

   public int getDecoding() throws YAPI_Exception {
      return this.get_decoding();
   }

   public int set_decoding(int newval) throws YAPI_Exception {
      synchronized (this) {
         String rest_val = Integer.toString(newval);
         this._setAttr("decoding", rest_val);
         return 0;
      }
   }

   public int setDecoding(int newval) throws YAPI_Exception {
      return this.set_decoding(newval);
   }

   public static YQuadratureDecoder FindQuadratureDecoder(String func) {
      YAPIContext ctx = YAPI.GetYCtx(true);
      synchronized (ctx._functionCacheLock) {
         YQuadratureDecoder obj = (YQuadratureDecoder)YFunction._FindFromCache("QuadratureDecoder", func);
         if (obj == null) {
            obj = new YQuadratureDecoder(func);
            YFunction._AddToCache("QuadratureDecoder", func, obj);
         }

         return obj;
      }
   }

   public static YQuadratureDecoder FindQuadratureDecoderInContext(YAPIContext yctx, String func) {
      synchronized (yctx._functionCacheLock) {
         YQuadratureDecoder obj = (YQuadratureDecoder)YFunction._FindFromCacheInContext(yctx, "QuadratureDecoder", func);
         if (obj == null) {
            obj = new YQuadratureDecoder(yctx, func);
            YFunction._AddToCache("QuadratureDecoder", func, obj);
         }

         return obj;
      }
   }

   public int registerValueCallback(YQuadratureDecoder.UpdateCallback callback) {
      if (callback != null) {
         YFunction._UpdateValueCallbackList(this, true);
      } else {
         YFunction._UpdateValueCallbackList(this, false);
      }

      this._valueCallbackQuadratureDecoder = callback;
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
      if (this._valueCallbackQuadratureDecoder != null) {
         this._valueCallbackQuadratureDecoder.yNewValue(this, value);
      } else {
         super._invokeValueCallback(value);
      }

      return 0;
   }

   public int registerTimedReportCallback(YQuadratureDecoder.TimedReportCallback callback) {
      if (callback != null) {
         YFunction._UpdateTimedReportCallbackList(this, true);
      } else {
         YFunction._UpdateTimedReportCallbackList(this, false);
      }

      this._timedReportCallbackQuadratureDecoder = callback;
      return 0;
   }

   @Override
   public int _invokeTimedReportCallback(YMeasure value) {
      if (this._timedReportCallbackQuadratureDecoder != null) {
         this._timedReportCallbackQuadratureDecoder.timedReportCallback(this, value);
      } else {
         super._invokeTimedReportCallback(value);
      }

      return 0;
   }

   public YQuadratureDecoder nextQuadratureDecoder() {
      String next_hwid;
      try {
         String hwid = this._yapi._yHash.resolveHwID(this._className, this._func);
         next_hwid = this._yapi._yHash.getNextHardwareId(this._className, hwid);
      } catch (YAPI_Exception var3) {
         next_hwid = null;
      }

      return next_hwid == null ? null : FindQuadratureDecoderInContext(this._yapi, next_hwid);
   }

   public static YQuadratureDecoder FirstQuadratureDecoder() {
      YAPIContext yctx = YAPI.GetYCtx(false);
      if (yctx == null) {
         return null;
      } else {
         String next_hwid = yctx._yHash.getFirstHardwareId("QuadratureDecoder");
         return next_hwid == null ? null : FindQuadratureDecoderInContext(yctx, next_hwid);
      }
   }

   public static YQuadratureDecoder FirstQuadratureDecoderInContext(YAPIContext yctx) {
      String next_hwid = yctx._yHash.getFirstHardwareId("QuadratureDecoder");
      return next_hwid == null ? null : FindQuadratureDecoderInContext(yctx, next_hwid);
   }

   public interface TimedReportCallback {
      void timedReportCallback(YQuadratureDecoder var1, YMeasure var2);
   }

   public interface UpdateCallback {
      void yNewValue(YQuadratureDecoder var1, String var2);
   }
}
