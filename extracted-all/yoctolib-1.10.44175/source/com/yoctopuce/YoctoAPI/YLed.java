package com.yoctopuce.YoctoAPI;

public class YLed extends YFunction {
   public static final int POWER_OFF = 0;
   public static final int POWER_ON = 1;
   public static final int POWER_INVALID = -1;
   public static final int LUMINOSITY_INVALID = -1;
   public static final int BLINKING_STILL = 0;
   public static final int BLINKING_RELAX = 1;
   public static final int BLINKING_AWARE = 2;
   public static final int BLINKING_RUN = 3;
   public static final int BLINKING_CALL = 4;
   public static final int BLINKING_PANIC = 5;
   public static final int BLINKING_INVALID = -1;
   protected int _power = -1;
   protected int _luminosity = -1;
   protected int _blinking = -1;
   protected YLed.UpdateCallback _valueCallbackLed = null;

   protected YLed(YAPIContext ctx, String func) {
      super(ctx, func);
      this._className = "Led";
   }

   protected YLed(String func) {
      this(YAPI.GetYCtx(true), func);
   }

   @Override
   protected void _parseAttr(YJSONObject json_val) throws Exception {
      if (json_val.has("power")) {
         this._power = json_val.getInt("power") > 0 ? 1 : 0;
      }

      if (json_val.has("luminosity")) {
         this._luminosity = json_val.getInt("luminosity");
      }

      if (json_val.has("blinking")) {
         this._blinking = json_val.getInt("blinking");
      }

      super._parseAttr(json_val);
   }

   public int get_power() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -1 : this._power;
      }
   }

   public int getPower() throws YAPI_Exception {
      return this.get_power();
   }

   public int set_power(int newval) throws YAPI_Exception {
      synchronized (this) {
         String rest_val = newval > 0 ? "1" : "0";
         this._setAttr("power", rest_val);
         return 0;
      }
   }

   public int setPower(int newval) throws YAPI_Exception {
      return this.set_power(newval);
   }

   public int get_luminosity() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -1 : this._luminosity;
      }
   }

   public int getLuminosity() throws YAPI_Exception {
      return this.get_luminosity();
   }

   public int set_luminosity(int newval) throws YAPI_Exception {
      synchronized (this) {
         String rest_val = Integer.toString(newval);
         this._setAttr("luminosity", rest_val);
         return 0;
      }
   }

   public int setLuminosity(int newval) throws YAPI_Exception {
      return this.set_luminosity(newval);
   }

   public int get_blinking() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -1 : this._blinking;
      }
   }

   public int getBlinking() throws YAPI_Exception {
      return this.get_blinking();
   }

   public int set_blinking(int newval) throws YAPI_Exception {
      synchronized (this) {
         String rest_val = Integer.toString(newval);
         this._setAttr("blinking", rest_val);
         return 0;
      }
   }

   public int setBlinking(int newval) throws YAPI_Exception {
      return this.set_blinking(newval);
   }

   public static YLed FindLed(String func) {
      YAPIContext ctx = YAPI.GetYCtx(true);
      synchronized (ctx._functionCacheLock) {
         YLed obj = (YLed)YFunction._FindFromCache("Led", func);
         if (obj == null) {
            obj = new YLed(func);
            YFunction._AddToCache("Led", func, obj);
         }

         return obj;
      }
   }

   public static YLed FindLedInContext(YAPIContext yctx, String func) {
      synchronized (yctx._functionCacheLock) {
         YLed obj = (YLed)YFunction._FindFromCacheInContext(yctx, "Led", func);
         if (obj == null) {
            obj = new YLed(yctx, func);
            YFunction._AddToCache("Led", func, obj);
         }

         return obj;
      }
   }

   public int registerValueCallback(YLed.UpdateCallback callback) {
      if (callback != null) {
         YFunction._UpdateValueCallbackList(this, true);
      } else {
         YFunction._UpdateValueCallbackList(this, false);
      }

      this._valueCallbackLed = callback;
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
      if (this._valueCallbackLed != null) {
         this._valueCallbackLed.yNewValue(this, value);
      } else {
         super._invokeValueCallback(value);
      }

      return 0;
   }

   public YLed nextLed() {
      String next_hwid;
      try {
         String hwid = this._yapi._yHash.resolveHwID(this._className, this._func);
         next_hwid = this._yapi._yHash.getNextHardwareId(this._className, hwid);
      } catch (YAPI_Exception var3) {
         next_hwid = null;
      }

      return next_hwid == null ? null : FindLedInContext(this._yapi, next_hwid);
   }

   public static YLed FirstLed() {
      YAPIContext yctx = YAPI.GetYCtx(false);
      if (yctx == null) {
         return null;
      } else {
         String next_hwid = yctx._yHash.getFirstHardwareId("Led");
         return next_hwid == null ? null : FindLedInContext(yctx, next_hwid);
      }
   }

   public static YLed FirstLedInContext(YAPIContext yctx) {
      String next_hwid = yctx._yHash.getFirstHardwareId("Led");
      return next_hwid == null ? null : FindLedInContext(yctx, next_hwid);
   }

   public interface TimedReportCallback {
      void timedReportCallback(YLed var1, YMeasure var2);
   }

   public interface UpdateCallback {
      void yNewValue(YLed var1, String var2);
   }
}
