package com.yoctopuce.YoctoAPI;

public class YAnButton extends YFunction {
   public static final int CALIBRATEDVALUE_INVALID = -1;
   public static final int RAWVALUE_INVALID = -1;
   public static final int ANALOGCALIBRATION_OFF = 0;
   public static final int ANALOGCALIBRATION_ON = 1;
   public static final int ANALOGCALIBRATION_INVALID = -1;
   public static final int CALIBRATIONMAX_INVALID = -1;
   public static final int CALIBRATIONMIN_INVALID = -1;
   public static final int SENSITIVITY_INVALID = -1;
   public static final int ISPRESSED_FALSE = 0;
   public static final int ISPRESSED_TRUE = 1;
   public static final int ISPRESSED_INVALID = -1;
   public static final long LASTTIMEPRESSED_INVALID = -9223372036854775807L;
   public static final long LASTTIMERELEASED_INVALID = -9223372036854775807L;
   public static final long PULSECOUNTER_INVALID = -9223372036854775807L;
   public static final long PULSETIMER_INVALID = -9223372036854775807L;
   public static final int INPUTTYPE_ANALOG_FAST = 0;
   public static final int INPUTTYPE_DIGITAL4 = 1;
   public static final int INPUTTYPE_ANALOG_SMOOTH = 2;
   public static final int INPUTTYPE_INVALID = -1;
   protected int _calibratedValue = -1;
   protected int _rawValue = -1;
   protected int _analogCalibration = -1;
   protected int _calibrationMax = -1;
   protected int _calibrationMin = -1;
   protected int _sensitivity = -1;
   protected int _isPressed = -1;
   protected long _lastTimePressed = -9223372036854775807L;
   protected long _lastTimeReleased = -9223372036854775807L;
   protected long _pulseCounter = -9223372036854775807L;
   protected long _pulseTimer = -9223372036854775807L;
   protected int _inputType = -1;
   protected YAnButton.UpdateCallback _valueCallbackAnButton = null;

   protected YAnButton(YAPIContext ctx, String func) {
      super(ctx, func);
      this._className = "AnButton";
   }

   protected YAnButton(String func) {
      this(YAPI.GetYCtx(true), func);
   }

   @Override
   protected void _parseAttr(YJSONObject json_val) throws Exception {
      if (json_val.has("calibratedValue")) {
         this._calibratedValue = json_val.getInt("calibratedValue");
      }

      if (json_val.has("rawValue")) {
         this._rawValue = json_val.getInt("rawValue");
      }

      if (json_val.has("analogCalibration")) {
         this._analogCalibration = json_val.getInt("analogCalibration") > 0 ? 1 : 0;
      }

      if (json_val.has("calibrationMax")) {
         this._calibrationMax = json_val.getInt("calibrationMax");
      }

      if (json_val.has("calibrationMin")) {
         this._calibrationMin = json_val.getInt("calibrationMin");
      }

      if (json_val.has("sensitivity")) {
         this._sensitivity = json_val.getInt("sensitivity");
      }

      if (json_val.has("isPressed")) {
         this._isPressed = json_val.getInt("isPressed") > 0 ? 1 : 0;
      }

      if (json_val.has("lastTimePressed")) {
         this._lastTimePressed = json_val.getLong("lastTimePressed");
      }

      if (json_val.has("lastTimeReleased")) {
         this._lastTimeReleased = json_val.getLong("lastTimeReleased");
      }

      if (json_val.has("pulseCounter")) {
         this._pulseCounter = json_val.getLong("pulseCounter");
      }

      if (json_val.has("pulseTimer")) {
         this._pulseTimer = json_val.getLong("pulseTimer");
      }

      if (json_val.has("inputType")) {
         this._inputType = json_val.getInt("inputType");
      }

      super._parseAttr(json_val);
   }

   public int get_calibratedValue() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -1 : this._calibratedValue;
      }
   }

   public int getCalibratedValue() throws YAPI_Exception {
      return this.get_calibratedValue();
   }

   public int get_rawValue() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -1 : this._rawValue;
      }
   }

   public int getRawValue() throws YAPI_Exception {
      return this.get_rawValue();
   }

   public int get_analogCalibration() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -1 : this._analogCalibration;
      }
   }

   public int getAnalogCalibration() throws YAPI_Exception {
      return this.get_analogCalibration();
   }

   public int set_analogCalibration(int newval) throws YAPI_Exception {
      synchronized (this) {
         String rest_val = newval > 0 ? "1" : "0";
         this._setAttr("analogCalibration", rest_val);
         return 0;
      }
   }

   public int setAnalogCalibration(int newval) throws YAPI_Exception {
      return this.set_analogCalibration(newval);
   }

   public int get_calibrationMax() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -1 : this._calibrationMax;
      }
   }

   public int getCalibrationMax() throws YAPI_Exception {
      return this.get_calibrationMax();
   }

   public int set_calibrationMax(int newval) throws YAPI_Exception {
      synchronized (this) {
         String rest_val = Integer.toString(newval);
         this._setAttr("calibrationMax", rest_val);
         return 0;
      }
   }

   public int setCalibrationMax(int newval) throws YAPI_Exception {
      return this.set_calibrationMax(newval);
   }

   public int get_calibrationMin() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -1 : this._calibrationMin;
      }
   }

   public int getCalibrationMin() throws YAPI_Exception {
      return this.get_calibrationMin();
   }

   public int set_calibrationMin(int newval) throws YAPI_Exception {
      synchronized (this) {
         String rest_val = Integer.toString(newval);
         this._setAttr("calibrationMin", rest_val);
         return 0;
      }
   }

   public int setCalibrationMin(int newval) throws YAPI_Exception {
      return this.set_calibrationMin(newval);
   }

   public int get_sensitivity() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -1 : this._sensitivity;
      }
   }

   public int getSensitivity() throws YAPI_Exception {
      return this.get_sensitivity();
   }

   public int set_sensitivity(int newval) throws YAPI_Exception {
      synchronized (this) {
         String rest_val = Integer.toString(newval);
         this._setAttr("sensitivity", rest_val);
         return 0;
      }
   }

   public int setSensitivity(int newval) throws YAPI_Exception {
      return this.set_sensitivity(newval);
   }

   public int get_isPressed() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -1 : this._isPressed;
      }
   }

   public int getIsPressed() throws YAPI_Exception {
      return this.get_isPressed();
   }

   public long get_lastTimePressed() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0
            ? -9223372036854775807L
            : this._lastTimePressed;
      }
   }

   public long getLastTimePressed() throws YAPI_Exception {
      return this.get_lastTimePressed();
   }

   public long get_lastTimeReleased() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0
            ? -9223372036854775807L
            : this._lastTimeReleased;
      }
   }

   public long getLastTimeReleased() throws YAPI_Exception {
      return this.get_lastTimeReleased();
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

   public int get_inputType() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -1 : this._inputType;
      }
   }

   public int getInputType() throws YAPI_Exception {
      return this.get_inputType();
   }

   public int set_inputType(int newval) throws YAPI_Exception {
      synchronized (this) {
         String rest_val = Integer.toString(newval);
         this._setAttr("inputType", rest_val);
         return 0;
      }
   }

   public int setInputType(int newval) throws YAPI_Exception {
      return this.set_inputType(newval);
   }

   public static YAnButton FindAnButton(String func) {
      YAPIContext ctx = YAPI.GetYCtx(true);
      synchronized (ctx._functionCacheLock) {
         YAnButton obj = (YAnButton)YFunction._FindFromCache("AnButton", func);
         if (obj == null) {
            obj = new YAnButton(func);
            YFunction._AddToCache("AnButton", func, obj);
         }

         return obj;
      }
   }

   public static YAnButton FindAnButtonInContext(YAPIContext yctx, String func) {
      synchronized (yctx._functionCacheLock) {
         YAnButton obj = (YAnButton)YFunction._FindFromCacheInContext(yctx, "AnButton", func);
         if (obj == null) {
            obj = new YAnButton(yctx, func);
            YFunction._AddToCache("AnButton", func, obj);
         }

         return obj;
      }
   }

   public int registerValueCallback(YAnButton.UpdateCallback callback) {
      if (callback != null) {
         YFunction._UpdateValueCallbackList(this, true);
      } else {
         YFunction._UpdateValueCallbackList(this, false);
      }

      this._valueCallbackAnButton = callback;
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
      if (this._valueCallbackAnButton != null) {
         this._valueCallbackAnButton.yNewValue(this, value);
      } else {
         super._invokeValueCallback(value);
      }

      return 0;
   }

   public int resetCounter() throws YAPI_Exception {
      return this.set_pulseCounter(0L);
   }

   public YAnButton nextAnButton() {
      String next_hwid;
      try {
         String hwid = this._yapi._yHash.resolveHwID(this._className, this._func);
         next_hwid = this._yapi._yHash.getNextHardwareId(this._className, hwid);
      } catch (YAPI_Exception var3) {
         next_hwid = null;
      }

      return next_hwid == null ? null : FindAnButtonInContext(this._yapi, next_hwid);
   }

   public static YAnButton FirstAnButton() {
      YAPIContext yctx = YAPI.GetYCtx(false);
      if (yctx == null) {
         return null;
      } else {
         String next_hwid = yctx._yHash.getFirstHardwareId("AnButton");
         return next_hwid == null ? null : FindAnButtonInContext(yctx, next_hwid);
      }
   }

   public static YAnButton FirstAnButtonInContext(YAPIContext yctx) {
      String next_hwid = yctx._yHash.getFirstHardwareId("AnButton");
      return next_hwid == null ? null : FindAnButtonInContext(yctx, next_hwid);
   }

   public interface TimedReportCallback {
      void timedReportCallback(YAnButton var1, YMeasure var2);
   }

   public interface UpdateCallback {
      void yNewValue(YAnButton var1, String var2);
   }
}
