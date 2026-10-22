package com.yoctopuce.YoctoAPI;

import java.util.Locale;

public class YColorLed extends YFunction {
   public static final int RGBCOLOR_INVALID = -1;
   public static final int HSLCOLOR_INVALID = -1;
   public static final int RGBCOLORATPOWERON_INVALID = -1;
   public static final int BLINKSEQSIZE_INVALID = -1;
   public static final int BLINKSEQMAXSIZE_INVALID = -1;
   public static final int BLINKSEQSIGNATURE_INVALID = -1;
   public static final String COMMAND_INVALID = "!INVALID!";
   public static final YColorLed.YMove RGBMOVE_INVALID = null;
   public static final YColorLed.YMove HSLMOVE_INVALID = null;
   protected int _rgbColor = -1;
   protected int _hslColor = -1;
   protected YColorLed.YMove _rgbMove = new YColorLed.YMove();
   protected YColorLed.YMove _hslMove = new YColorLed.YMove();
   protected int _rgbColorAtPowerOn = -1;
   protected int _blinkSeqSize = -1;
   protected int _blinkSeqMaxSize = -1;
   protected int _blinkSeqSignature = -1;
   protected String _command = "!INVALID!";
   protected YColorLed.UpdateCallback _valueCallbackColorLed = null;

   protected YColorLed(YAPIContext ctx, String func) {
      super(ctx, func);
      this._className = "ColorLed";
   }

   protected YColorLed(String func) {
      this(YAPI.GetYCtx(true), func);
   }

   @Override
   protected void _parseAttr(YJSONObject json_val) throws Exception {
      if (json_val.has("rgbColor")) {
         this._rgbColor = json_val.getInt("rgbColor");
      }

      if (json_val.has("hslColor")) {
         this._hslColor = json_val.getInt("hslColor");
      }

      if (json_val.has("rgbMove")) {
         YJSONObject subjson = json_val.getYJSONObject("rgbMove");
         if (subjson.has("moving")) {
            this._rgbMove.moving = subjson.getInt("moving");
         }

         if (subjson.has("target")) {
            this._rgbMove.target = subjson.getInt("target");
         }

         if (subjson.has("ms")) {
            this._rgbMove.ms = subjson.getInt("ms");
         }
      }

      if (json_val.has("hslMove")) {
         YJSONObject subjsonx = json_val.getYJSONObject("hslMove");
         if (subjsonx.has("moving")) {
            this._hslMove.moving = subjsonx.getInt("moving");
         }

         if (subjsonx.has("target")) {
            this._hslMove.target = subjsonx.getInt("target");
         }

         if (subjsonx.has("ms")) {
            this._hslMove.ms = subjsonx.getInt("ms");
         }
      }

      if (json_val.has("rgbColorAtPowerOn")) {
         this._rgbColorAtPowerOn = json_val.getInt("rgbColorAtPowerOn");
      }

      if (json_val.has("blinkSeqSize")) {
         this._blinkSeqSize = json_val.getInt("blinkSeqSize");
      }

      if (json_val.has("blinkSeqMaxSize")) {
         this._blinkSeqMaxSize = json_val.getInt("blinkSeqMaxSize");
      }

      if (json_val.has("blinkSeqSignature")) {
         this._blinkSeqSignature = json_val.getInt("blinkSeqSignature");
      }

      if (json_val.has("command")) {
         this._command = json_val.getString("command");
      }

      super._parseAttr(json_val);
   }

   public int get_rgbColor() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -1 : this._rgbColor;
      }
   }

   public int getRgbColor() throws YAPI_Exception {
      return this.get_rgbColor();
   }

   public int set_rgbColor(int newval) throws YAPI_Exception {
      synchronized (this) {
         String rest_val = String.format(Locale.US, "0x%06x", newval);
         this._setAttr("rgbColor", rest_val);
         return 0;
      }
   }

   public int setRgbColor(int newval) throws YAPI_Exception {
      return this.set_rgbColor(newval);
   }

   public int get_hslColor() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -1 : this._hslColor;
      }
   }

   public int getHslColor() throws YAPI_Exception {
      return this.get_hslColor();
   }

   public int set_hslColor(int newval) throws YAPI_Exception {
      synchronized (this) {
         String rest_val = String.format(Locale.US, "0x%06x", newval);
         this._setAttr("hslColor", rest_val);
         return 0;
      }
   }

   public int setHslColor(int newval) throws YAPI_Exception {
      return this.set_hslColor(newval);
   }

   public YColorLed.YMove get_rgbMove() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? RGBMOVE_INVALID : this._rgbMove;
      }
   }

   public int set_rgbMove(YColorLed.YMove newval) throws YAPI_Exception {
      synchronized (this) {
         String rest_val = String.format(Locale.US, "%d:%d", newval.target, newval.ms);
         this._setAttr("rgbMove", rest_val);
         return 0;
      }
   }

   public int rgbMove(int rgb_target, int ms_duration) throws YAPI_Exception {
      String rest_val = String.format(Locale.US, "%d:%d", rgb_target, ms_duration);
      this._setAttr("rgbMove", rest_val);
      return 0;
   }

   public YColorLed.YMove get_hslMove() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? HSLMOVE_INVALID : this._hslMove;
      }
   }

   public int set_hslMove(YColorLed.YMove newval) throws YAPI_Exception {
      synchronized (this) {
         String rest_val = String.format(Locale.US, "%d:%d", newval.target, newval.ms);
         this._setAttr("hslMove", rest_val);
         return 0;
      }
   }

   public int hslMove(int hsl_target, int ms_duration) throws YAPI_Exception {
      String rest_val = String.format(Locale.US, "%d:%d", hsl_target, ms_duration);
      this._setAttr("hslMove", rest_val);
      return 0;
   }

   public int get_rgbColorAtPowerOn() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -1 : this._rgbColorAtPowerOn;
      }
   }

   public int getRgbColorAtPowerOn() throws YAPI_Exception {
      return this.get_rgbColorAtPowerOn();
   }

   public int set_rgbColorAtPowerOn(int newval) throws YAPI_Exception {
      synchronized (this) {
         String rest_val = String.format(Locale.US, "0x%06x", newval);
         this._setAttr("rgbColorAtPowerOn", rest_val);
         return 0;
      }
   }

   public int setRgbColorAtPowerOn(int newval) throws YAPI_Exception {
      return this.set_rgbColorAtPowerOn(newval);
   }

   public int get_blinkSeqSize() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -1 : this._blinkSeqSize;
      }
   }

   public int getBlinkSeqSize() throws YAPI_Exception {
      return this.get_blinkSeqSize();
   }

   public int get_blinkSeqMaxSize() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration == 0L && this.load(this._yapi._defaultCacheValidity) != 0 ? -1 : this._blinkSeqMaxSize;
      }
   }

   public int getBlinkSeqMaxSize() throws YAPI_Exception {
      return this.get_blinkSeqMaxSize();
   }

   public int get_blinkSeqSignature() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -1 : this._blinkSeqSignature;
      }
   }

   public int getBlinkSeqSignature() throws YAPI_Exception {
      return this.get_blinkSeqSignature();
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

   public static YColorLed FindColorLed(String func) {
      YAPIContext ctx = YAPI.GetYCtx(true);
      synchronized (ctx._functionCacheLock) {
         YColorLed obj = (YColorLed)YFunction._FindFromCache("ColorLed", func);
         if (obj == null) {
            obj = new YColorLed(func);
            YFunction._AddToCache("ColorLed", func, obj);
         }

         return obj;
      }
   }

   public static YColorLed FindColorLedInContext(YAPIContext yctx, String func) {
      synchronized (yctx._functionCacheLock) {
         YColorLed obj = (YColorLed)YFunction._FindFromCacheInContext(yctx, "ColorLed", func);
         if (obj == null) {
            obj = new YColorLed(yctx, func);
            YFunction._AddToCache("ColorLed", func, obj);
         }

         return obj;
      }
   }

   public int registerValueCallback(YColorLed.UpdateCallback callback) {
      if (callback != null) {
         YFunction._UpdateValueCallbackList(this, true);
      } else {
         YFunction._UpdateValueCallbackList(this, false);
      }

      this._valueCallbackColorLed = callback;
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
      if (this._valueCallbackColorLed != null) {
         this._valueCallbackColorLed.yNewValue(this, value);
      } else {
         super._invokeValueCallback(value);
      }

      return 0;
   }

   public int sendCommand(String command) throws YAPI_Exception {
      return this.set_command(command);
   }

   public int addHslMoveToBlinkSeq(int HSLcolor, int msDelay) throws YAPI_Exception {
      return this.sendCommand(String.format(Locale.US, "H%d,%d", HSLcolor, msDelay));
   }

   public int addRgbMoveToBlinkSeq(int RGBcolor, int msDelay) throws YAPI_Exception {
      return this.sendCommand(String.format(Locale.US, "R%d,%d", RGBcolor, msDelay));
   }

   public int startBlinkSeq() throws YAPI_Exception {
      return this.sendCommand("S");
   }

   public int stopBlinkSeq() throws YAPI_Exception {
      return this.sendCommand("X");
   }

   public int resetBlinkSeq() throws YAPI_Exception {
      return this.sendCommand("Z");
   }

   public YColorLed nextColorLed() {
      String next_hwid;
      try {
         String hwid = this._yapi._yHash.resolveHwID(this._className, this._func);
         next_hwid = this._yapi._yHash.getNextHardwareId(this._className, hwid);
      } catch (YAPI_Exception var3) {
         next_hwid = null;
      }

      return next_hwid == null ? null : FindColorLedInContext(this._yapi, next_hwid);
   }

   public static YColorLed FirstColorLed() {
      YAPIContext yctx = YAPI.GetYCtx(false);
      if (yctx == null) {
         return null;
      } else {
         String next_hwid = yctx._yHash.getFirstHardwareId("ColorLed");
         return next_hwid == null ? null : FindColorLedInContext(yctx, next_hwid);
      }
   }

   public static YColorLed FirstColorLedInContext(YAPIContext yctx) {
      String next_hwid = yctx._yHash.getFirstHardwareId("ColorLed");
      return next_hwid == null ? null : FindColorLedInContext(yctx, next_hwid);
   }

   public interface TimedReportCallback {
      void timedReportCallback(YColorLed var1, YMeasure var2);
   }

   public interface UpdateCallback {
      void yNewValue(YColorLed var1, String var2);
   }

   public static class YMove {
      public int target = Integer.MIN_VALUE;
      public int ms = Integer.MIN_VALUE;
      public int moving = -1;
   }
}
