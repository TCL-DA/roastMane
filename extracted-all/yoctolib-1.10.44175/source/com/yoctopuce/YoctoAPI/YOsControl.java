package com.yoctopuce.YoctoAPI;

public class YOsControl extends YFunction {
   public static final int SHUTDOWNCOUNTDOWN_INVALID = -1;
   protected int _shutdownCountdown = -1;
   protected YOsControl.UpdateCallback _valueCallbackOsControl = null;

   protected YOsControl(YAPIContext ctx, String func) {
      super(ctx, func);
      this._className = "OsControl";
   }

   protected YOsControl(String func) {
      this(YAPI.GetYCtx(true), func);
   }

   @Override
   protected void _parseAttr(YJSONObject json_val) throws Exception {
      if (json_val.has("shutdownCountdown")) {
         this._shutdownCountdown = json_val.getInt("shutdownCountdown");
      }

      super._parseAttr(json_val);
   }

   public int get_shutdownCountdown() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -1 : this._shutdownCountdown;
      }
   }

   public int getShutdownCountdown() throws YAPI_Exception {
      return this.get_shutdownCountdown();
   }

   public int set_shutdownCountdown(int newval) throws YAPI_Exception {
      synchronized (this) {
         String rest_val = Integer.toString(newval);
         this._setAttr("shutdownCountdown", rest_val);
         return 0;
      }
   }

   public static YOsControl FindOsControl(String func) {
      YAPIContext ctx = YAPI.GetYCtx(true);
      synchronized (ctx._functionCacheLock) {
         YOsControl obj = (YOsControl)YFunction._FindFromCache("OsControl", func);
         if (obj == null) {
            obj = new YOsControl(func);
            YFunction._AddToCache("OsControl", func, obj);
         }

         return obj;
      }
   }

   public static YOsControl FindOsControlInContext(YAPIContext yctx, String func) {
      synchronized (yctx._functionCacheLock) {
         YOsControl obj = (YOsControl)YFunction._FindFromCacheInContext(yctx, "OsControl", func);
         if (obj == null) {
            obj = new YOsControl(yctx, func);
            YFunction._AddToCache("OsControl", func, obj);
         }

         return obj;
      }
   }

   public int registerValueCallback(YOsControl.UpdateCallback callback) {
      if (callback != null) {
         YFunction._UpdateValueCallbackList(this, true);
      } else {
         YFunction._UpdateValueCallbackList(this, false);
      }

      this._valueCallbackOsControl = callback;
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
      if (this._valueCallbackOsControl != null) {
         this._valueCallbackOsControl.yNewValue(this, value);
      } else {
         super._invokeValueCallback(value);
      }

      return 0;
   }

   public int shutdown(int secBeforeShutDown) throws YAPI_Exception {
      return this.set_shutdownCountdown(secBeforeShutDown);
   }

   public YOsControl nextOsControl() {
      String next_hwid;
      try {
         String hwid = this._yapi._yHash.resolveHwID(this._className, this._func);
         next_hwid = this._yapi._yHash.getNextHardwareId(this._className, hwid);
      } catch (YAPI_Exception var3) {
         next_hwid = null;
      }

      return next_hwid == null ? null : FindOsControlInContext(this._yapi, next_hwid);
   }

   public static YOsControl FirstOsControl() {
      YAPIContext yctx = YAPI.GetYCtx(false);
      if (yctx == null) {
         return null;
      } else {
         String next_hwid = yctx._yHash.getFirstHardwareId("OsControl");
         return next_hwid == null ? null : FindOsControlInContext(yctx, next_hwid);
      }
   }

   public static YOsControl FirstOsControlInContext(YAPIContext yctx) {
      String next_hwid = yctx._yHash.getFirstHardwareId("OsControl");
      return next_hwid == null ? null : FindOsControlInContext(yctx, next_hwid);
   }

   public interface TimedReportCallback {
      void timedReportCallback(YOsControl var1, YMeasure var2);
   }

   public interface UpdateCallback {
      void yNewValue(YOsControl var1, String var2);
   }
}
