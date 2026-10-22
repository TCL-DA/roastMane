package com.yoctopuce.YoctoAPI;

import java.util.Locale;

public class YPowerSupply extends YFunction {
   public static final double VOLTAGESETPOINT_INVALID = -1.79769313486231E308;
   public static final double CURRENTLIMIT_INVALID = -1.79769313486231E308;
   public static final int POWEROUTPUT_OFF = 0;
   public static final int POWEROUTPUT_ON = 1;
   public static final int POWEROUTPUT_INVALID = -1;
   public static final int VOLTAGESENSE_INT = 0;
   public static final int VOLTAGESENSE_EXT = 1;
   public static final int VOLTAGESENSE_INVALID = -1;
   public static final double MEASUREDVOLTAGE_INVALID = -1.79769313486231E308;
   public static final double MEASUREDCURRENT_INVALID = -1.79769313486231E308;
   public static final double INPUTVOLTAGE_INVALID = -1.79769313486231E308;
   public static final double VINT_INVALID = -1.79769313486231E308;
   public static final double LDOTEMPERATURE_INVALID = -1.79769313486231E308;
   public static final String VOLTAGETRANSITION_INVALID = "!INVALID!";
   public static final double VOLTAGEATSTARTUP_INVALID = -1.79769313486231E308;
   public static final double CURRENTATSTARTUP_INVALID = -1.79769313486231E308;
   public static final String COMMAND_INVALID = "!INVALID!";
   protected double _voltageSetPoint = -1.79769313486231E308;
   protected double _currentLimit = -1.79769313486231E308;
   protected int _powerOutput = -1;
   protected int _voltageSense = -1;
   protected double _measuredVoltage = -1.79769313486231E308;
   protected double _measuredCurrent = -1.79769313486231E308;
   protected double _inputVoltage = -1.79769313486231E308;
   protected double _vInt = -1.79769313486231E308;
   protected double _ldoTemperature = -1.79769313486231E308;
   protected String _voltageTransition = "!INVALID!";
   protected double _voltageAtStartUp = -1.79769313486231E308;
   protected double _currentAtStartUp = -1.79769313486231E308;
   protected String _command = "!INVALID!";
   protected YPowerSupply.UpdateCallback _valueCallbackPowerSupply = null;

   protected YPowerSupply(YAPIContext ctx, String func) {
      super(ctx, func);
      this._className = "PowerSupply";
   }

   protected YPowerSupply(String func) {
      this(YAPI.GetYCtx(true), func);
   }

   @Override
   protected void _parseAttr(YJSONObject json_val) throws Exception {
      if (json_val.has("voltageSetPoint")) {
         this._voltageSetPoint = (double)Math.round(json_val.getDouble("voltageSetPoint") * 1000.0 / 65536.0) / 1000.0;
      }

      if (json_val.has("currentLimit")) {
         this._currentLimit = (double)Math.round(json_val.getDouble("currentLimit") * 1000.0 / 65536.0) / 1000.0;
      }

      if (json_val.has("powerOutput")) {
         this._powerOutput = json_val.getInt("powerOutput") > 0 ? 1 : 0;
      }

      if (json_val.has("voltageSense")) {
         this._voltageSense = json_val.getInt("voltageSense");
      }

      if (json_val.has("measuredVoltage")) {
         this._measuredVoltage = (double)Math.round(json_val.getDouble("measuredVoltage") * 1000.0 / 65536.0) / 1000.0;
      }

      if (json_val.has("measuredCurrent")) {
         this._measuredCurrent = (double)Math.round(json_val.getDouble("measuredCurrent") * 1000.0 / 65536.0) / 1000.0;
      }

      if (json_val.has("inputVoltage")) {
         this._inputVoltage = (double)Math.round(json_val.getDouble("inputVoltage") * 1000.0 / 65536.0) / 1000.0;
      }

      if (json_val.has("vInt")) {
         this._vInt = (double)Math.round(json_val.getDouble("vInt") * 1000.0 / 65536.0) / 1000.0;
      }

      if (json_val.has("ldoTemperature")) {
         this._ldoTemperature = (double)Math.round(json_val.getDouble("ldoTemperature") * 1000.0 / 65536.0) / 1000.0;
      }

      if (json_val.has("voltageTransition")) {
         this._voltageTransition = json_val.getString("voltageTransition");
      }

      if (json_val.has("voltageAtStartUp")) {
         this._voltageAtStartUp = (double)Math.round(json_val.getDouble("voltageAtStartUp") * 1000.0 / 65536.0) / 1000.0;
      }

      if (json_val.has("currentAtStartUp")) {
         this._currentAtStartUp = (double)Math.round(json_val.getDouble("currentAtStartUp") * 1000.0 / 65536.0) / 1000.0;
      }

      if (json_val.has("command")) {
         this._command = json_val.getString("command");
      }

      super._parseAttr(json_val);
   }

   public int set_voltageSetPoint(double newval) throws YAPI_Exception {
      synchronized (this) {
         String rest_val = Long.toString(Math.round(newval * 65536.0));
         this._setAttr("voltageSetPoint", rest_val);
         return 0;
      }
   }

   public int setVoltageSetPoint(double newval) throws YAPI_Exception {
      return this.set_voltageSetPoint(newval);
   }

   public double get_voltageSetPoint() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0
            ? -1.79769313486231E308
            : this._voltageSetPoint;
      }
   }

   public double getVoltageSetPoint() throws YAPI_Exception {
      return this.get_voltageSetPoint();
   }

   public int set_currentLimit(double newval) throws YAPI_Exception {
      synchronized (this) {
         String rest_val = Long.toString(Math.round(newval * 65536.0));
         this._setAttr("currentLimit", rest_val);
         return 0;
      }
   }

   public int setCurrentLimit(double newval) throws YAPI_Exception {
      return this.set_currentLimit(newval);
   }

   public double get_currentLimit() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0
            ? -1.79769313486231E308
            : this._currentLimit;
      }
   }

   public double getCurrentLimit() throws YAPI_Exception {
      return this.get_currentLimit();
   }

   public int get_powerOutput() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -1 : this._powerOutput;
      }
   }

   public int getPowerOutput() throws YAPI_Exception {
      return this.get_powerOutput();
   }

   public int set_powerOutput(int newval) throws YAPI_Exception {
      synchronized (this) {
         String rest_val = newval > 0 ? "1" : "0";
         this._setAttr("powerOutput", rest_val);
         return 0;
      }
   }

   public int setPowerOutput(int newval) throws YAPI_Exception {
      return this.set_powerOutput(newval);
   }

   public int get_voltageSense() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -1 : this._voltageSense;
      }
   }

   public int getVoltageSense() throws YAPI_Exception {
      return this.get_voltageSense();
   }

   public int set_voltageSense(int newval) throws YAPI_Exception {
      synchronized (this) {
         String rest_val = Integer.toString(newval);
         this._setAttr("voltageSense", rest_val);
         return 0;
      }
   }

   public int setVoltageSense(int newval) throws YAPI_Exception {
      return this.set_voltageSense(newval);
   }

   public double get_measuredVoltage() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0
            ? -1.79769313486231E308
            : this._measuredVoltage;
      }
   }

   public double getMeasuredVoltage() throws YAPI_Exception {
      return this.get_measuredVoltage();
   }

   public double get_measuredCurrent() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0
            ? -1.79769313486231E308
            : this._measuredCurrent;
      }
   }

   public double getMeasuredCurrent() throws YAPI_Exception {
      return this.get_measuredCurrent();
   }

   public double get_inputVoltage() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0
            ? -1.79769313486231E308
            : this._inputVoltage;
      }
   }

   public double getInputVoltage() throws YAPI_Exception {
      return this.get_inputVoltage();
   }

   public double get_vInt() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -1.79769313486231E308 : this._vInt;
      }
   }

   public double getVInt() throws YAPI_Exception {
      return this.get_vInt();
   }

   public double get_ldoTemperature() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0
            ? -1.79769313486231E308
            : this._ldoTemperature;
      }
   }

   public double getLdoTemperature() throws YAPI_Exception {
      return this.get_ldoTemperature();
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

   public int set_currentAtStartUp(double newval) throws YAPI_Exception {
      synchronized (this) {
         String rest_val = Long.toString(Math.round(newval * 65536.0));
         this._setAttr("currentAtStartUp", rest_val);
         return 0;
      }
   }

   public int setCurrentAtStartUp(double newval) throws YAPI_Exception {
      return this.set_currentAtStartUp(newval);
   }

   public double get_currentAtStartUp() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0
            ? -1.79769313486231E308
            : this._currentAtStartUp;
      }
   }

   public double getCurrentAtStartUp() throws YAPI_Exception {
      return this.get_currentAtStartUp();
   }

   public String get_command() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? "!INVALID!" : this._command;
      }
   }

   public int set_command(String newval) throws YAPI_Exception {
      synchronized (this) {
         this._setAttr("command", newval);
         return 0;
      }
   }

   public static YPowerSupply FindPowerSupply(String func) {
      YAPIContext ctx = YAPI.GetYCtx(true);
      synchronized (ctx._functionCacheLock) {
         YPowerSupply obj = (YPowerSupply)YFunction._FindFromCache("PowerSupply", func);
         if (obj == null) {
            obj = new YPowerSupply(func);
            YFunction._AddToCache("PowerSupply", func, obj);
         }

         return obj;
      }
   }

   public static YPowerSupply FindPowerSupplyInContext(YAPIContext yctx, String func) {
      synchronized (yctx._functionCacheLock) {
         YPowerSupply obj = (YPowerSupply)YFunction._FindFromCacheInContext(yctx, "PowerSupply", func);
         if (obj == null) {
            obj = new YPowerSupply(yctx, func);
            YFunction._AddToCache("PowerSupply", func, obj);
         }

         return obj;
      }
   }

   public int registerValueCallback(YPowerSupply.UpdateCallback callback) {
      if (callback != null) {
         YFunction._UpdateValueCallbackList(this, true);
      } else {
         YFunction._UpdateValueCallbackList(this, false);
      }

      this._valueCallbackPowerSupply = callback;
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
      if (this._valueCallbackPowerSupply != null) {
         this._valueCallbackPowerSupply.yNewValue(this, value);
      } else {
         super._invokeValueCallback(value);
      }

      return 0;
   }

   public int voltageMove(double V_target, int ms_duration) throws YAPI_Exception {
      if (V_target < 0.0) {
         V_target = 0.0;
      }

      String newval = String.format(Locale.US, "%d:%d", (int)((double)Math.round(V_target * 65536.0)), ms_duration);
      return this.set_voltageTransition(newval);
   }

   public YPowerSupply nextPowerSupply() {
      String next_hwid;
      try {
         String hwid = this._yapi._yHash.resolveHwID(this._className, this._func);
         next_hwid = this._yapi._yHash.getNextHardwareId(this._className, hwid);
      } catch (YAPI_Exception var3) {
         next_hwid = null;
      }

      return next_hwid == null ? null : FindPowerSupplyInContext(this._yapi, next_hwid);
   }

   public static YPowerSupply FirstPowerSupply() {
      YAPIContext yctx = YAPI.GetYCtx(false);
      if (yctx == null) {
         return null;
      } else {
         String next_hwid = yctx._yHash.getFirstHardwareId("PowerSupply");
         return next_hwid == null ? null : FindPowerSupplyInContext(yctx, next_hwid);
      }
   }

   public static YPowerSupply FirstPowerSupplyInContext(YAPIContext yctx) {
      String next_hwid = yctx._yHash.getFirstHardwareId("PowerSupply");
      return next_hwid == null ? null : FindPowerSupplyInContext(yctx, next_hwid);
   }

   public interface TimedReportCallback {
      void timedReportCallback(YPowerSupply var1, YMeasure var2);
   }

   public interface UpdateCallback {
      void yNewValue(YPowerSupply var1, String var2);
   }
}
