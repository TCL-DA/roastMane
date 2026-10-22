package com.yoctopuce.YoctoAPI;

public class YPwmPowerSource extends YFunction {
   public static final int POWERMODE_USB_5V = 0;
   public static final int POWERMODE_USB_3V = 1;
   public static final int POWERMODE_EXT_V = 2;
   public static final int POWERMODE_OPNDRN = 3;
   public static final int POWERMODE_INVALID = -1;
   protected int _powerMode = -1;
   protected YPwmPowerSource.UpdateCallback _valueCallbackPwmPowerSource = null;

   protected YPwmPowerSource(YAPIContext ctx, String func) {
      super(ctx, func);
      this._className = "PwmPowerSource";
   }

   protected YPwmPowerSource(String func) {
      this(YAPI.GetYCtx(true), func);
   }

   @Override
   protected void _parseAttr(YJSONObject json_val) throws Exception {
      if (json_val.has("powerMode")) {
         this._powerMode = json_val.getInt("powerMode");
      }

      super._parseAttr(json_val);
   }

   public int get_powerMode() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -1 : this._powerMode;
      }
   }

   public int getPowerMode() throws YAPI_Exception {
      return this.get_powerMode();
   }

   public int set_powerMode(int newval) throws YAPI_Exception {
      synchronized (this) {
         String rest_val = Integer.toString(newval);
         this._setAttr("powerMode", rest_val);
         return 0;
      }
   }

   public int setPowerMode(int newval) throws YAPI_Exception {
      return this.set_powerMode(newval);
   }

   public static YPwmPowerSource FindPwmPowerSource(String func) {
      YAPIContext ctx = YAPI.GetYCtx(true);
      synchronized (ctx._functionCacheLock) {
         YPwmPowerSource obj = (YPwmPowerSource)YFunction._FindFromCache("PwmPowerSource", func);
         if (obj == null) {
            obj = new YPwmPowerSource(func);
            YFunction._AddToCache("PwmPowerSource", func, obj);
         }

         return obj;
      }
   }

   public static YPwmPowerSource FindPwmPowerSourceInContext(YAPIContext yctx, String func) {
      synchronized (yctx._functionCacheLock) {
         YPwmPowerSource obj = (YPwmPowerSource)YFunction._FindFromCacheInContext(yctx, "PwmPowerSource", func);
         if (obj == null) {
            obj = new YPwmPowerSource(yctx, func);
            YFunction._AddToCache("PwmPowerSource", func, obj);
         }

         return obj;
      }
   }

   public int registerValueCallback(YPwmPowerSource.UpdateCallback callback) {
      if (callback != null) {
         YFunction._UpdateValueCallbackList(this, true);
      } else {
         YFunction._UpdateValueCallbackList(this, false);
      }

      this._valueCallbackPwmPowerSource = callback;
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
      if (this._valueCallbackPwmPowerSource != null) {
         this._valueCallbackPwmPowerSource.yNewValue(this, value);
      } else {
         super._invokeValueCallback(value);
      }

      return 0;
   }

   public YPwmPowerSource nextPwmPowerSource() {
      String next_hwid;
      try {
         String hwid = this._yapi._yHash.resolveHwID(this._className, this._func);
         next_hwid = this._yapi._yHash.getNextHardwareId(this._className, hwid);
      } catch (YAPI_Exception var3) {
         next_hwid = null;
      }

      return next_hwid == null ? null : FindPwmPowerSourceInContext(this._yapi, next_hwid);
   }

   public static YPwmPowerSource FirstPwmPowerSource() {
      YAPIContext yctx = YAPI.GetYCtx(false);
      if (yctx == null) {
         return null;
      } else {
         String next_hwid = yctx._yHash.getFirstHardwareId("PwmPowerSource");
         return next_hwid == null ? null : FindPwmPowerSourceInContext(yctx, next_hwid);
      }
   }

   public static YPwmPowerSource FirstPwmPowerSourceInContext(YAPIContext yctx) {
      String next_hwid = yctx._yHash.getFirstHardwareId("PwmPowerSource");
      return next_hwid == null ? null : FindPwmPowerSourceInContext(yctx, next_hwid);
   }

   public interface TimedReportCallback {
      void timedReportCallback(YPwmPowerSource var1, YMeasure var2);
   }

   public interface UpdateCallback {
      void yNewValue(YPwmPowerSource var1, String var2);
   }
}
