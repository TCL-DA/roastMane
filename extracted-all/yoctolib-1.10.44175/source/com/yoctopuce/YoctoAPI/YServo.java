package com.yoctopuce.YoctoAPI;

import java.util.Locale;

public class YServo extends YFunction {
   public static final int POSITION_INVALID = Integer.MIN_VALUE;
   public static final int ENABLED_FALSE = 0;
   public static final int ENABLED_TRUE = 1;
   public static final int ENABLED_INVALID = -1;
   public static final int RANGE_INVALID = -1;
   public static final int NEUTRAL_INVALID = -1;
   public static final int POSITIONATPOWERON_INVALID = Integer.MIN_VALUE;
   public static final int ENABLEDATPOWERON_FALSE = 0;
   public static final int ENABLEDATPOWERON_TRUE = 1;
   public static final int ENABLEDATPOWERON_INVALID = -1;
   public static final YServo.YMove MOVE_INVALID = null;
   protected int _position = Integer.MIN_VALUE;
   protected int _enabled = -1;
   protected int _range = -1;
   protected int _neutral = -1;
   protected YServo.YMove _move = new YServo.YMove();
   protected int _positionAtPowerOn = Integer.MIN_VALUE;
   protected int _enabledAtPowerOn = -1;
   protected YServo.UpdateCallback _valueCallbackServo = null;

   protected YServo(YAPIContext ctx, String func) {
      super(ctx, func);
      this._className = "Servo";
   }

   protected YServo(String func) {
      this(YAPI.GetYCtx(true), func);
   }

   @Override
   protected void _parseAttr(YJSONObject json_val) throws Exception {
      if (json_val.has("position")) {
         this._position = json_val.getInt("position");
      }

      if (json_val.has("enabled")) {
         this._enabled = json_val.getInt("enabled") > 0 ? 1 : 0;
      }

      if (json_val.has("range")) {
         this._range = json_val.getInt("range");
      }

      if (json_val.has("neutral")) {
         this._neutral = json_val.getInt("neutral");
      }

      if (json_val.has("move")) {
         YJSONObject subjson = json_val.getYJSONObject("move");
         if (subjson.has("moving")) {
            this._move.moving = subjson.getInt("moving");
         }

         if (subjson.has("target")) {
            this._move.target = subjson.getInt("target");
         }

         if (subjson.has("ms")) {
            this._move.ms = subjson.getInt("ms");
         }
      }

      if (json_val.has("positionAtPowerOn")) {
         this._positionAtPowerOn = json_val.getInt("positionAtPowerOn");
      }

      if (json_val.has("enabledAtPowerOn")) {
         this._enabledAtPowerOn = json_val.getInt("enabledAtPowerOn") > 0 ? 1 : 0;
      }

      super._parseAttr(json_val);
   }

   public int get_position() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? Integer.MIN_VALUE : this._position;
      }
   }

   public int getPosition() throws YAPI_Exception {
      return this.get_position();
   }

   public int set_position(int newval) throws YAPI_Exception {
      synchronized (this) {
         String rest_val = Integer.toString(newval);
         this._setAttr("position", rest_val);
         return 0;
      }
   }

   public int setPosition(int newval) throws YAPI_Exception {
      return this.set_position(newval);
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

   public int get_range() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -1 : this._range;
      }
   }

   public int getRange() throws YAPI_Exception {
      return this.get_range();
   }

   public int set_range(int newval) throws YAPI_Exception {
      synchronized (this) {
         String rest_val = Integer.toString(newval);
         this._setAttr("range", rest_val);
         return 0;
      }
   }

   public int setRange(int newval) throws YAPI_Exception {
      return this.set_range(newval);
   }

   public int get_neutral() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -1 : this._neutral;
      }
   }

   public int getNeutral() throws YAPI_Exception {
      return this.get_neutral();
   }

   public int set_neutral(int newval) throws YAPI_Exception {
      synchronized (this) {
         String rest_val = Integer.toString(newval);
         this._setAttr("neutral", rest_val);
         return 0;
      }
   }

   public int setNeutral(int newval) throws YAPI_Exception {
      return this.set_neutral(newval);
   }

   public YServo.YMove get_move() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? MOVE_INVALID : this._move;
      }
   }

   public int set_move(YServo.YMove newval) throws YAPI_Exception {
      synchronized (this) {
         String rest_val = String.format(Locale.US, "%d:%d", newval.target, newval.ms);
         this._setAttr("move", rest_val);
         return 0;
      }
   }

   public int move(int target, int ms_duration) throws YAPI_Exception {
      String rest_val = String.format(Locale.US, "%d:%d", target, ms_duration);
      this._setAttr("move", rest_val);
      return 0;
   }

   public int get_positionAtPowerOn() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0
            ? Integer.MIN_VALUE
            : this._positionAtPowerOn;
      }
   }

   public int getPositionAtPowerOn() throws YAPI_Exception {
      return this.get_positionAtPowerOn();
   }

   public int set_positionAtPowerOn(int newval) throws YAPI_Exception {
      synchronized (this) {
         String rest_val = Integer.toString(newval);
         this._setAttr("positionAtPowerOn", rest_val);
         return 0;
      }
   }

   public int setPositionAtPowerOn(int newval) throws YAPI_Exception {
      return this.set_positionAtPowerOn(newval);
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

   public static YServo FindServo(String func) {
      YAPIContext ctx = YAPI.GetYCtx(true);
      synchronized (ctx._functionCacheLock) {
         YServo obj = (YServo)YFunction._FindFromCache("Servo", func);
         if (obj == null) {
            obj = new YServo(func);
            YFunction._AddToCache("Servo", func, obj);
         }

         return obj;
      }
   }

   public static YServo FindServoInContext(YAPIContext yctx, String func) {
      synchronized (yctx._functionCacheLock) {
         YServo obj = (YServo)YFunction._FindFromCacheInContext(yctx, "Servo", func);
         if (obj == null) {
            obj = new YServo(yctx, func);
            YFunction._AddToCache("Servo", func, obj);
         }

         return obj;
      }
   }

   public int registerValueCallback(YServo.UpdateCallback callback) {
      if (callback != null) {
         YFunction._UpdateValueCallbackList(this, true);
      } else {
         YFunction._UpdateValueCallbackList(this, false);
      }

      this._valueCallbackServo = callback;
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
      if (this._valueCallbackServo != null) {
         this._valueCallbackServo.yNewValue(this, value);
      } else {
         super._invokeValueCallback(value);
      }

      return 0;
   }

   public YServo nextServo() {
      String next_hwid;
      try {
         String hwid = this._yapi._yHash.resolveHwID(this._className, this._func);
         next_hwid = this._yapi._yHash.getNextHardwareId(this._className, hwid);
      } catch (YAPI_Exception var3) {
         next_hwid = null;
      }

      return next_hwid == null ? null : FindServoInContext(this._yapi, next_hwid);
   }

   public static YServo FirstServo() {
      YAPIContext yctx = YAPI.GetYCtx(false);
      if (yctx == null) {
         return null;
      } else {
         String next_hwid = yctx._yHash.getFirstHardwareId("Servo");
         return next_hwid == null ? null : FindServoInContext(yctx, next_hwid);
      }
   }

   public static YServo FirstServoInContext(YAPIContext yctx) {
      String next_hwid = yctx._yHash.getFirstHardwareId("Servo");
      return next_hwid == null ? null : FindServoInContext(yctx, next_hwid);
   }

   public interface TimedReportCallback {
      void timedReportCallback(YServo var1, YMeasure var2);
   }

   public interface UpdateCallback {
      void yNewValue(YServo var1, String var2);
   }

   public static class YMove {
      public int target = Integer.MIN_VALUE;
      public int ms = Integer.MIN_VALUE;
      public int moving = -1;
   }
}
