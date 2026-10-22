package com.yoctopuce.YoctoAPI;

import java.util.Locale;

public class YVoltageOutput extends YFunction {
   public static final double CURRENTVOLTAGE_INVALID = -1.79769313486231E308;
   public static final String VOLTAGETRANSITION_INVALID = "!INVALID!";
   public static final double VOLTAGEATSTARTUP_INVALID = -1.79769313486231E308;
   protected double _currentVoltage = -1.79769313486231E308;
   protected String _voltageTransition = "!INVALID!";
   protected double _voltageAtStartUp = -1.79769313486231E308;
   protected YVoltageOutput.UpdateCallback _valueCallbackVoltageOutput = null;

   protected YVoltageOutput(YAPIContext ctx, String func) {
      super(ctx, func);
      this._className = "VoltageOutput";
   }

   protected YVoltageOutput(String func) {
      this(YAPI.GetYCtx(true), func);
   }

   @Override
   protected void _parseAttr(YJSONObject json_val) throws Exception {
      if (json_val.has("currentVoltage")) {
         this._currentVoltage = (double)Math.round(json_val.getDouble("currentVoltage") * 1000.0 / 65536.0) / 1000.0;
      }

      if (json_val.has("voltageTransition")) {
         this._voltageTransition = json_val.getString("voltageTransition");
      }

      if (json_val.has("voltageAtStartUp")) {
         this._voltageAtStartUp = (double)Math.round(json_val.getDouble("voltageAtStartUp") * 1000.0 / 65536.0) / 1000.0;
      }

      super._parseAttr(json_val);
   }

   public int set_currentVoltage(double newval) throws YAPI_Exception {
      synchronized (this) {
         String rest_val = Long.toString(Math.round(newval * 65536.0));
         this._setAttr("currentVoltage", rest_val);
         return 0;
      }
   }

   public int setCurrentVoltage(double newval) throws YAPI_Exception {
      return this.set_currentVoltage(newval);
   }

   public double get_currentVoltage() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0
            ? -1.79769313486231E308
            : this._currentVoltage;
      }
   }

   public double getCurrentVoltage() throws YAPI_Exception {
      return this.get_currentVoltage();
   }

   public String get_voltageTransition() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? "!INVALID!" : this._voltageTransition;
      }
   }

   public int set_voltageTransition(String newval) throws YAPI_Exception {
      synchronized (this) {
         this._setAttr("voltageTransition", newval);
         return 0;
      }
   }

   public int set_voltageAtStartUp(double newval) throws YAPI_Exception {
      synchronized (this) {
         String rest_val = Long.toString(Math.round(newval * 65536.0));
         this._setAttr("voltageAtStartUp", rest_val);
         return 0;
      }
   }

   public int setVoltageAtStartUp(double newval) throws YAPI_Exception {
      return this.set_voltageAtStartUp(newval);
   }

   public double get_voltageAtStartUp() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0
            ? -1.79769313486231E308
            : this._voltageAtStartUp;
      }
   }

   public double getVoltageAtStartUp() throws YAPI_Exception {
      return this.get_voltageAtStartUp();
   }

   public static YVoltageOutput FindVoltageOutput(String func) {
      YAPIContext ctx = YAPI.GetYCtx(true);
      synchronized (ctx._functionCacheLock) {
         YVoltageOutput obj = (YVoltageOutput)YFunction._FindFromCache("VoltageOutput", func);
         if (obj == null) {
            obj = new YVoltageOutput(func);
            YFunction._AddToCache("VoltageOutput", func, obj);
         }

         return obj;
      }
   }

   public static YVoltageOutput FindVoltageOutputInContext(YAPIContext yctx, String func) {
      synchronized (yctx._functionCacheLock) {
         YVoltageOutput obj = (YVoltageOutput)YFunction._FindFromCacheInContext(yctx, "VoltageOutput", func);
         if (obj == null) {
            obj = new YVoltageOutput(yctx, func);
            YFunction._AddToCache("VoltageOutput", func, obj);
         }

         return obj;
      }
   }

   public int registerValueCallback(YVoltageOutput.UpdateCallback callback) {
      if (callback != null) {
         YFunction._UpdateValueCallbackList(this, true);
      } else {
         YFunction._UpdateValueCallbackList(this, false);
      }

      this._valueCallbackVoltageOutput = callback;
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
      if (this._valueCallbackVoltageOutput != null) {
         this._valueCallbackVoltageOutput.yNewValue(this, value);
      } else {
         super._invokeValueCallback(value);
      }

      return 0;
   }

   public int voltageMove(double V_target, int ms_duration) throws YAPI_Exception {
      if (V_target < 0.0) {
         V_target = 0.0;
      }

      if (V_target > 10.0) {
         V_target = 10.0;
      }

      String newval = String.format(Locale.US, "%d:%d", (int)((double)Math.round(V_target * 65536.0)), ms_duration);
      return this.set_voltageTransition(newval);
   }

   public YVoltageOutput nextVoltageOutput() {
      String next_hwid;
      try {
         String hwid = this._yapi._yHash.resolveHwID(this._className, this._func);
         next_hwid = this._yapi._yHash.getNextHardwareId(this._className, hwid);
      } catch (YAPI_Exception var3) {
         next_hwid = null;
      }

      return next_hwid == null ? null : FindVoltageOutputInContext(this._yapi, next_hwid);
   }

   public static YVoltageOutput FirstVoltageOutput() {
      YAPIContext yctx = YAPI.GetYCtx(false);
      if (yctx == null) {
         return null;
      } else {
         String next_hwid = yctx._yHash.getFirstHardwareId("VoltageOutput");
         return next_hwid == null ? null : FindVoltageOutputInContext(yctx, next_hwid);
      }
   }

   public static YVoltageOutput FirstVoltageOutputInContext(YAPIContext yctx) {
      String next_hwid = yctx._yHash.getFirstHardwareId("VoltageOutput");
      return next_hwid == null ? null : FindVoltageOutputInContext(yctx, next_hwid);
   }

   public interface TimedReportCallback {
      void timedReportCallback(YVoltageOutput var1, YMeasure var2);
   }

   public interface UpdateCallback {
      void yNewValue(YVoltageOutput var1, String var2);
   }
}
