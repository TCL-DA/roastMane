package com.yoctopuce.YoctoAPI;

public class YPowerOutput extends YFunction {
   public static final int VOLTAGE_OFF = 0;
   public static final int VOLTAGE_OUT3V3 = 1;
   public static final int VOLTAGE_OUT5V = 2;
   public static final int VOLTAGE_OUT4V7 = 3;
   public static final int VOLTAGE_OUT1V8 = 4;
   public static final int VOLTAGE_INVALID = -1;
   protected int _voltage = -1;
   protected YPowerOutput.UpdateCallback _valueCallbackPowerOutput = null;

   protected YPowerOutput(YAPIContext ctx, String func) {
      super(ctx, func);
      this._className = "PowerOutput";
   }

   protected YPowerOutput(String func) {
      this(YAPI.GetYCtx(true), func);
   }

   @Override
   protected void _parseAttr(YJSONObject json_val) throws Exception {
      if (json_val.has("voltage")) {
         this._voltage = json_val.getInt("voltage");
      }

      super._parseAttr(json_val);
   }

   public int get_voltage() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -1 : this._voltage;
      }
   }

   public int getVoltage() throws YAPI_Exception {
      return this.get_voltage();
   }

   public int set_voltage(int newval) throws YAPI_Exception {
      synchronized (this) {
         String rest_val = Integer.toString(newval);
         this._setAttr("voltage", rest_val);
         return 0;
      }
   }

   public int setVoltage(int newval) throws YAPI_Exception {
      return this.set_voltage(newval);
   }

   public static YPowerOutput FindPowerOutput(String func) {
      YAPIContext ctx = YAPI.GetYCtx(true);
      synchronized (ctx._functionCacheLock) {
         YPowerOutput obj = (YPowerOutput)YFunction._FindFromCache("PowerOutput", func);
         if (obj == null) {
            obj = new YPowerOutput(func);
            YFunction._AddToCache("PowerOutput", func, obj);
         }

         return obj;
      }
   }

   public static YPowerOutput FindPowerOutputInContext(YAPIContext yctx, String func) {
      synchronized (yctx._functionCacheLock) {
         YPowerOutput obj = (YPowerOutput)YFunction._FindFromCacheInContext(yctx, "PowerOutput", func);
         if (obj == null) {
            obj = new YPowerOutput(yctx, func);
            YFunction._AddToCache("PowerOutput", func, obj);
         }

         return obj;
      }
   }

   public int registerValueCallback(YPowerOutput.UpdateCallback callback) {
      if (callback != null) {
         YFunction._UpdateValueCallbackList(this, true);
      } else {
         YFunction._UpdateValueCallbackList(this, false);
      }

      this._valueCallbackPowerOutput = callback;
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
      if (this._valueCallbackPowerOutput != null) {
         this._valueCallbackPowerOutput.yNewValue(this, value);
      } else {
         super._invokeValueCallback(value);
      }

      return 0;
   }

   public YPowerOutput nextPowerOutput() {
      String next_hwid;
      try {
         String hwid = this._yapi._yHash.resolveHwID(this._className, this._func);
         next_hwid = this._yapi._yHash.getNextHardwareId(this._className, hwid);
      } catch (YAPI_Exception var3) {
         next_hwid = null;
      }

      return next_hwid == null ? null : FindPowerOutputInContext(this._yapi, next_hwid);
   }

   public static YPowerOutput FirstPowerOutput() {
      YAPIContext yctx = YAPI.GetYCtx(false);
      if (yctx == null) {
         return null;
      } else {
         String next_hwid = yctx._yHash.getFirstHardwareId("PowerOutput");
         return next_hwid == null ? null : FindPowerOutputInContext(yctx, next_hwid);
      }
   }

   public static YPowerOutput FirstPowerOutputInContext(YAPIContext yctx) {
      String next_hwid = yctx._yHash.getFirstHardwareId("PowerOutput");
      return next_hwid == null ? null : FindPowerOutputInContext(yctx, next_hwid);
   }

   public interface TimedReportCallback {
      void timedReportCallback(YPowerOutput var1, YMeasure var2);
   }

   public interface UpdateCallback {
      void yNewValue(YPowerOutput var1, String var2);
   }
}
