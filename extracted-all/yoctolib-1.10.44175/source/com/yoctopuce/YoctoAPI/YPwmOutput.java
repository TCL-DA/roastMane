package com.yoctopuce.YoctoAPI;

import java.util.Locale;

public class YPwmOutput extends YFunction {
   public static final int ENABLED_FALSE = 0;
   public static final int ENABLED_TRUE = 1;
   public static final int ENABLED_INVALID = -1;
   public static final double FREQUENCY_INVALID = -1.79769313486231E308;
   public static final double PERIOD_INVALID = -1.79769313486231E308;
   public static final double DUTYCYCLE_INVALID = -1.79769313486231E308;
   public static final double PULSEDURATION_INVALID = -1.79769313486231E308;
   public static final String PWMTRANSITION_INVALID = "!INVALID!";
   public static final int ENABLEDATPOWERON_FALSE = 0;
   public static final int ENABLEDATPOWERON_TRUE = 1;
   public static final int ENABLEDATPOWERON_INVALID = -1;
   public static final double DUTYCYCLEATPOWERON_INVALID = -1.79769313486231E308;
   protected int _enabled = -1;
   protected double _frequency = -1.79769313486231E308;
   protected double _period = -1.79769313486231E308;
   protected double _dutyCycle = -1.79769313486231E308;
   protected double _pulseDuration = -1.79769313486231E308;
   protected String _pwmTransition = "!INVALID!";
   protected int _enabledAtPowerOn = -1;
   protected double _dutyCycleAtPowerOn = -1.79769313486231E308;
   protected YPwmOutput.UpdateCallback _valueCallbackPwmOutput = null;

   protected YPwmOutput(YAPIContext ctx, String func) {
      super(ctx, func);
      this._className = "PwmOutput";
   }

   protected YPwmOutput(String func) {
      this(YAPI.GetYCtx(true), func);
   }

   @Override
   protected void _parseAttr(YJSONObject json_val) throws Exception {
      if (json_val.has("enabled")) {
         this._enabled = json_val.getInt("enabled") > 0 ? 1 : 0;
      }

      if (json_val.has("frequency")) {
         this._frequency = (double)Math.round(json_val.getDouble("frequency") * 1000.0 / 65536.0) / 1000.0;
      }

      if (json_val.has("period")) {
         this._period = (double)Math.round(json_val.getDouble("period") * 1000.0 / 65536.0) / 1000.0;
      }

      if (json_val.has("dutyCycle")) {
         this._dutyCycle = (double)Math.round(json_val.getDouble("dutyCycle") * 1000.0 / 65536.0) / 1000.0;
      }

      if (json_val.has("pulseDuration")) {
         this._pulseDuration = (double)Math.round(json_val.getDouble("pulseDuration") * 1000.0 / 65536.0) / 1000.0;
      }

      if (json_val.has("pwmTransition")) {
         this._pwmTransition = json_val.getString("pwmTransition");
      }

      if (json_val.has("enabledAtPowerOn")) {
         this._enabledAtPowerOn = json_val.getInt("enabledAtPowerOn") > 0 ? 1 : 0;
      }

      if (json_val.has("dutyCycleAtPowerOn")) {
         this._dutyCycleAtPowerOn = (double)Math.round(json_val.getDouble("dutyCycleAtPowerOn") * 1000.0 / 65536.0) / 1000.0;
      }

      super._parseAttr(json_val);
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

   public int set_frequency(double newval) throws YAPI_Exception {
      synchronized (this) {
         String rest_val = Long.toString(Math.round(newval * 65536.0));
         this._setAttr("frequency", rest_val);
         return 0;
      }
   }

   public int setFrequency(double newval) throws YAPI_Exception {
      return this.set_frequency(newval);
   }

   public double get_frequency() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0
            ? -1.79769313486231E308
            : this._frequency;
      }
   }

   public double getFrequency() throws YAPI_Exception {
      return this.get_frequency();
   }

   public int set_period(double newval) throws YAPI_Exception {
      synchronized (this) {
         String rest_val = Long.toString(Math.round(newval * 65536.0));
         this._setAttr("period", rest_val);
         return 0;
      }
   }

   public int setPeriod(double newval) throws YAPI_Exception {
      return this.set_period(newval);
   }

   public double get_period() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -1.79769313486231E308 : this._period;
      }
   }

   public double getPeriod() throws YAPI_Exception {
      return this.get_period();
   }

   public int set_dutyCycle(double newval) throws YAPI_Exception {
      synchronized (this) {
         String rest_val = Long.toString(Math.round(newval * 65536.0));
         this._setAttr("dutyCycle", rest_val);
         return 0;
      }
   }

   public int setDutyCycle(double newval) throws YAPI_Exception {
      return this.set_dutyCycle(newval);
   }

   public double get_dutyCycle() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0
            ? -1.79769313486231E308
            : this._dutyCycle;
      }
   }

   public double getDutyCycle() throws YAPI_Exception {
      return this.get_dutyCycle();
   }

   public int set_pulseDuration(double newval) throws YAPI_Exception {
      synchronized (this) {
         String rest_val = Long.toString(Math.round(newval * 65536.0));
         this._setAttr("pulseDuration", rest_val);
         return 0;
      }
   }

   public int setPulseDuration(double newval) throws YAPI_Exception {
      return this.set_pulseDuration(newval);
   }

   public double get_pulseDuration() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0
            ? -1.79769313486231E308
            : this._pulseDuration;
      }
   }

   public double getPulseDuration() throws YAPI_Exception {
      return this.get_pulseDuration();
   }

   public String get_pwmTransition() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? "!INVALID!" : this._pwmTransition;
      }
   }

   public int set_pwmTransition(String newval) throws YAPI_Exception {
      synchronized (this) {
         this._setAttr("pwmTransition", newval);
         return 0;
      }
   }

   public int get_enabledAtPowerOn() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -1 : this._enabledAtPowerOn;
      }
   }

   public int getEnabledAtPowerOn() throws YAPI_Exception {
      return this.get_enabledAtPowerOn();
   }

   public int set_enabledAtPowerOn(int newval) throws YAPI_Exception {
      synchronized (this) {
         String rest_val = newval > 0 ? "1" : "0";
         this._setAttr("enabledAtPowerOn", rest_val);
         return 0;
      }
   }

   public int setEnabledAtPowerOn(int newval) throws YAPI_Exception {
      return this.set_enabledAtPowerOn(newval);
   }

   public int set_dutyCycleAtPowerOn(double newval) throws YAPI_Exception {
      synchronized (this) {
         String rest_val = Long.toString(Math.round(newval * 65536.0));
         this._setAttr("dutyCycleAtPowerOn", rest_val);
         return 0;
      }
   }

   public int setDutyCycleAtPowerOn(double newval) throws YAPI_Exception {
      return this.set_dutyCycleAtPowerOn(newval);
   }

   public double get_dutyCycleAtPowerOn() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0
            ? -1.79769313486231E308
            : this._dutyCycleAtPowerOn;
      }
   }

   public double getDutyCycleAtPowerOn() throws YAPI_Exception {
      return this.get_dutyCycleAtPowerOn();
   }

   public static YPwmOutput FindPwmOutput(String func) {
      YAPIContext ctx = YAPI.GetYCtx(true);
      synchronized (ctx._functionCacheLock) {
         YPwmOutput obj = (YPwmOutput)YFunction._FindFromCache("PwmOutput", func);
         if (obj == null) {
            obj = new YPwmOutput(func);
            YFunction._AddToCache("PwmOutput", func, obj);
         }

         return obj;
      }
   }

   public static YPwmOutput FindPwmOutputInContext(YAPIContext yctx, String func) {
      synchronized (yctx._functionCacheLock) {
         YPwmOutput obj = (YPwmOutput)YFunction._FindFromCacheInContext(yctx, "PwmOutput", func);
         if (obj == null) {
            obj = new YPwmOutput(yctx, func);
            YFunction._AddToCache("PwmOutput", func, obj);
         }

         return obj;
      }
   }

   public int registerValueCallback(YPwmOutput.UpdateCallback callback) {
      if (callback != null) {
         YFunction._UpdateValueCallbackList(this, true);
      } else {
         YFunction._UpdateValueCallbackList(this, false);
      }

      this._valueCallbackPwmOutput = callback;
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
      if (this._valueCallbackPwmOutput != null) {
         this._valueCallbackPwmOutput.yNewValue(this, value);
      } else {
         super._invokeValueCallback(value);
      }

      return 0;
   }

   public int pulseDurationMove(double ms_target, int ms_duration) throws YAPI_Exception {
      if (ms_target < 0.0) {
         ms_target = 0.0;
      }

      String newval = String.format(Locale.US, "%dms:%d", (int)((double)Math.round(ms_target * 65536.0)), ms_duration);
      return this.set_pwmTransition(newval);
   }

   public int dutyCycleMove(double target, int ms_duration) throws YAPI_Exception {
      if (target < 0.0) {
         target = 0.0;
      }

      if (target > 100.0) {
         target = 100.0;
      }

      String newval = String.format(Locale.US, "%d:%d", (int)((double)Math.round(target * 65536.0)), ms_duration);
      return this.set_pwmTransition(newval);
   }

   public int frequencyMove(double target, int ms_duration) throws YAPI_Exception {
      if (target < 0.001) {
         target = 0.001;
      }

      String newval = String.format(Locale.US, "%fHz:%d", target, ms_duration);
      return this.set_pwmTransition(newval);
   }

   public int phaseMove(double target, int ms_duration) throws YAPI_Exception {
      String newval = String.format(Locale.US, "%fps:%d", target, ms_duration);
      return this.set_pwmTransition(newval);
   }

   public int triggerPulsesByDuration(double ms_target, int n_pulses) throws YAPI_Exception {
      if (ms_target < 0.0) {
         ms_target = 0.0;
      }

      String newval = String.format(Locale.US, "%dms*%d", (int)((double)Math.round(ms_target * 65536.0)), n_pulses);
      return this.set_pwmTransition(newval);
   }

   public int triggerPulsesByDutyCycle(double target, int n_pulses) throws YAPI_Exception {
      if (target < 0.0) {
         target = 0.0;
      }

      if (target > 100.0) {
         target = 100.0;
      }

      String newval = String.format(Locale.US, "%d*%d", (int)((double)Math.round(target * 65536.0)), n_pulses);
      return this.set_pwmTransition(newval);
   }

   public int triggerPulsesByFrequency(double target, int n_pulses) throws YAPI_Exception {
      if (target < 0.001) {
         target = 0.001;
      }

      String newval = String.format(Locale.US, "%fHz*%d", target, n_pulses);
      return this.set_pwmTransition(newval);
   }

   public int markForRepeat() throws YAPI_Exception {
      return this.set_pwmTransition(":");
   }

   public int repeatFromMark() throws YAPI_Exception {
      return this.set_pwmTransition("R");
   }

   public YPwmOutput nextPwmOutput() {
      String next_hwid;
      try {
         String hwid = this._yapi._yHash.resolveHwID(this._className, this._func);
         next_hwid = this._yapi._yHash.getNextHardwareId(this._className, hwid);
      } catch (YAPI_Exception var3) {
         next_hwid = null;
      }

      return next_hwid == null ? null : FindPwmOutputInContext(this._yapi, next_hwid);
   }

   public static YPwmOutput FirstPwmOutput() {
      YAPIContext yctx = YAPI.GetYCtx(false);
      if (yctx == null) {
         return null;
      } else {
         String next_hwid = yctx._yHash.getFirstHardwareId("PwmOutput");
         return next_hwid == null ? null : FindPwmOutputInContext(yctx, next_hwid);
      }
   }

   public static YPwmOutput FirstPwmOutputInContext(YAPIContext yctx) {
      String next_hwid = yctx._yHash.getFirstHardwareId("PwmOutput");
      return next_hwid == null ? null : FindPwmOutputInContext(yctx, next_hwid);
   }

   public interface TimedReportCallback {
      void timedReportCallback(YPwmOutput var1, YMeasure var2);
   }

   public interface UpdateCallback {
      void yNewValue(YPwmOutput var1, String var2);
   }
}
