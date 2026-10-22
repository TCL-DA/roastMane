package com.yoctopuce.YoctoAPI;

public class YPwmInput extends YSensor {
   public static final double DUTYCYCLE_INVALID = -1.79769313486231E308;
   public static final double PULSEDURATION_INVALID = -1.79769313486231E308;
   public static final double FREQUENCY_INVALID = -1.79769313486231E308;
   public static final double PERIOD_INVALID = -1.79769313486231E308;
   public static final long PULSECOUNTER_INVALID = -9223372036854775807L;
   public static final long PULSETIMER_INVALID = -9223372036854775807L;
   public static final int PWMREPORTMODE_PWM_DUTYCYCLE = 0;
   public static final int PWMREPORTMODE_PWM_FREQUENCY = 1;
   public static final int PWMREPORTMODE_PWM_PULSEDURATION = 2;
   public static final int PWMREPORTMODE_PWM_EDGECOUNT = 3;
   public static final int PWMREPORTMODE_PWM_PULSECOUNT = 4;
   public static final int PWMREPORTMODE_PWM_CPS = 5;
   public static final int PWMREPORTMODE_PWM_CPM = 6;
   public static final int PWMREPORTMODE_PWM_STATE = 7;
   public static final int PWMREPORTMODE_PWM_FREQ_CPS = 8;
   public static final int PWMREPORTMODE_PWM_FREQ_CPM = 9;
   public static final int PWMREPORTMODE_PWM_PERIODCOUNT = 10;
   public static final int PWMREPORTMODE_INVALID = -1;
   public static final int DEBOUNCEPERIOD_INVALID = -1;
   public static final int BANDWIDTH_INVALID = -1;
   public static final int EDGESPERPERIOD_INVALID = -1;
   protected double _dutyCycle = -1.79769313486231E308;
   protected double _pulseDuration = -1.79769313486231E308;
   protected double _frequency = -1.79769313486231E308;
   protected double _period = -1.79769313486231E308;
   protected long _pulseCounter = -9223372036854775807L;
   protected long _pulseTimer = -9223372036854775807L;
   protected int _pwmReportMode = -1;
   protected int _debouncePeriod = -1;
   protected int _bandwidth = -1;
   protected int _edgesPerPeriod = -1;
   protected YPwmInput.UpdateCallback _valueCallbackPwmInput = null;
   protected YPwmInput.TimedReportCallback _timedReportCallbackPwmInput = null;

   protected YPwmInput(YAPIContext ctx, String func) {
      super(ctx, func);
      this._className = "PwmInput";
   }

   protected YPwmInput(String func) {
      this(YAPI.GetYCtx(true), func);
   }

   @Override
   protected void _parseAttr(YJSONObject json_val) throws Exception {
      if (json_val.has("dutyCycle")) {
         this._dutyCycle = (double)Math.round(json_val.getDouble("dutyCycle") * 1000.0 / 65536.0) / 1000.0;
      }

      if (json_val.has("pulseDuration")) {
         this._pulseDuration = (double)Math.round(json_val.getDouble("pulseDuration") * 1000.0 / 65536.0) / 1000.0;
      }

      if (json_val.has("frequency")) {
         this._frequency = (double)Math.round(json_val.getDouble("frequency") * 1000.0 / 65536.0) / 1000.0;
      }

      if (json_val.has("period")) {
         this._period = (double)Math.round(json_val.getDouble("period") * 1000.0 / 65536.0) / 1000.0;
      }

      if (json_val.has("pulseCounter")) {
         this._pulseCounter = json_val.getLong("pulseCounter");
      }

      if (json_val.has("pulseTimer")) {
         this._pulseTimer = json_val.getLong("pulseTimer");
      }

      if (json_val.has("pwmReportMode")) {
         this._pwmReportMode = json_val.getInt("pwmReportMode");
      }

      if (json_val.has("debouncePeriod")) {
         this._debouncePeriod = json_val.getInt("debouncePeriod");
      }

      if (json_val.has("bandwidth")) {
         this._bandwidth = json_val.getInt("bandwidth");
      }

      if (json_val.has("edgesPerPeriod")) {
         this._edgesPerPeriod = json_val.getInt("edgesPerPeriod");
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

   public double get_period() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -1.79769313486231E308 : this._period;
      }
   }

   public double getPeriod() throws YAPI_Exception {
      return this.get_period();
   }

   public long get_pulseCounter() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0
            ? -9223372036854775807L
            : this._pulseCounter;
      }
   }

   public long getPulseCounter() throws YAPI_Exception {
      return this.get_pulseCounter();
   }

   public int set_pulseCounter(long newval) throws YAPI_Exception {
      synchronized (this) {
         String rest_val = Long.toString(newval);
         this._setAttr("pulseCounter", rest_val);
         return 0;
      }
   }

   public long get_pulseTimer() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0
            ? -9223372036854775807L
            : this._pulseTimer;
      }
   }

   public long getPulseTimer() throws YAPI_Exception {
      return this.get_pulseTimer();
   }

   public int get_pwmReportMode() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -1 : this._pwmReportMode;
      }
   }

   public int getPwmReportMode() throws YAPI_Exception {
      return this.get_pwmReportMode();
   }

   public int set_pwmReportMode(int newval) throws YAPI_Exception {
      synchronized (this) {
         String rest_val = Integer.toString(newval);
         this._setAttr("pwmReportMode", rest_val);
         return 0;
      }
   }

   public int setPwmReportMode(int newval) throws YAPI_Exception {
      return this.set_pwmReportMode(newval);
   }

   public int get_debouncePeriod() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -1 : this._debouncePeriod;
      }
   }

   public int getDebouncePeriod() throws YAPI_Exception {
      return this.get_debouncePeriod();
   }

   public int set_debouncePeriod(int newval) throws YAPI_Exception {
      synchronized (this) {
         String rest_val = Integer.toString(newval);
         this._setAttr("debouncePeriod", rest_val);
         return 0;
      }
   }

   public int setDebouncePeriod(int newval) throws YAPI_Exception {
      return this.set_debouncePeriod(newval);
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

   public int get_edgesPerPeriod() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -1 : this._edgesPerPeriod;
      }
   }

   public int getEdgesPerPeriod() throws YAPI_Exception {
      return this.get_edgesPerPeriod();
   }

   public static YPwmInput FindPwmInput(String func) {
      YAPIContext ctx = YAPI.GetYCtx(true);
      synchronized (ctx._functionCacheLock) {
         YPwmInput obj = (YPwmInput)YFunction._FindFromCache("PwmInput", func);
         if (obj == null) {
            obj = new YPwmInput(func);
            YFunction._AddToCache("PwmInput", func, obj);
         }

         return obj;
      }
   }

   public static YPwmInput FindPwmInputInContext(YAPIContext yctx, String func) {
      synchronized (yctx._functionCacheLock) {
         YPwmInput obj = (YPwmInput)YFunction._FindFromCacheInContext(yctx, "PwmInput", func);
         if (obj == null) {
            obj = new YPwmInput(yctx, func);
            YFunction._AddToCache("PwmInput", func, obj);
         }

         return obj;
      }
   }

   public int registerValueCallback(YPwmInput.UpdateCallback callback) {
      if (callback != null) {
         YFunction._UpdateValueCallbackList(this, true);
      } else {
         YFunction._UpdateValueCallbackList(this, false);
      }

      this._valueCallbackPwmInput = callback;
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
      if (this._valueCallbackPwmInput != null) {
         this._valueCallbackPwmInput.yNewValue(this, value);
      } else {
         super._invokeValueCallback(value);
      }

      return 0;
   }

   public int registerTimedReportCallback(YPwmInput.TimedReportCallback callback) {
      if (callback != null) {
         YFunction._UpdateTimedReportCallbackList(this, true);
      } else {
         YFunction._UpdateTimedReportCallbackList(this, false);
      }

      this._timedReportCallbackPwmInput = callback;
      return 0;
   }

   @Override
   public int _invokeTimedReportCallback(YMeasure value) {
      if (this._timedReportCallbackPwmInput != null) {
         this._timedReportCallbackPwmInput.timedReportCallback(this, value);
      } else {
         super._invokeTimedReportCallback(value);
      }

      return 0;
   }

   public int resetCounter() throws YAPI_Exception {
      return this.set_pulseCounter(0L);
   }

   public YPwmInput nextPwmInput() {
      String next_hwid;
      try {
         String hwid = this._yapi._yHash.resolveHwID(this._className, this._func);
         next_hwid = this._yapi._yHash.getNextHardwareId(this._className, hwid);
      } catch (YAPI_Exception var3) {
         next_hwid = null;
      }

      return next_hwid == null ? null : FindPwmInputInContext(this._yapi, next_hwid);
   }

   public static YPwmInput FirstPwmInput() {
      YAPIContext yctx = YAPI.GetYCtx(false);
      if (yctx == null) {
         return null;
      } else {
         String next_hwid = yctx._yHash.getFirstHardwareId("PwmInput");
         return next_hwid == null ? null : FindPwmInputInContext(yctx, next_hwid);
      }
   }

   public static YPwmInput FirstPwmInputInContext(YAPIContext yctx) {
      String next_hwid = yctx._yHash.getFirstHardwareId("PwmInput");
      return next_hwid == null ? null : FindPwmInputInContext(yctx, next_hwid);
   }

   public interface TimedReportCallback {
      void timedReportCallback(YPwmInput var1, YMeasure var2);
   }

   public interface UpdateCallback {
      void yNewValue(YPwmInput var1, String var2);
   }
}
