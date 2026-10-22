package com.yoctopuce.YoctoAPI;

import java.util.ArrayList;
import java.util.Locale;

public class YDisplay extends YFunction {
   public static final int ENABLED_FALSE = 0;
   public static final int ENABLED_TRUE = 1;
   public static final int ENABLED_INVALID = -1;
   public static final String STARTUPSEQ_INVALID = "!INVALID!";
   public static final int BRIGHTNESS_INVALID = -1;
   public static final int ORIENTATION_LEFT = 0;
   public static final int ORIENTATION_UP = 1;
   public static final int ORIENTATION_RIGHT = 2;
   public static final int ORIENTATION_DOWN = 3;
   public static final int ORIENTATION_INVALID = -1;
   public static final int DISPLAYWIDTH_INVALID = -1;
   public static final int DISPLAYHEIGHT_INVALID = -1;
   public static final int DISPLAYTYPE_MONO = 0;
   public static final int DISPLAYTYPE_GRAY = 1;
   public static final int DISPLAYTYPE_RGB = 2;
   public static final int DISPLAYTYPE_INVALID = -1;
   public static final int LAYERWIDTH_INVALID = -1;
   public static final int LAYERHEIGHT_INVALID = -1;
   public static final int LAYERCOUNT_INVALID = -1;
   public static final String COMMAND_INVALID = "!INVALID!";
   protected int _enabled = -1;
   protected String _startupSeq = "!INVALID!";
   protected int _brightness = -1;
   protected int _orientation = -1;
   protected int _displayWidth = -1;
   protected int _displayHeight = -1;
   protected int _displayType = -1;
   protected int _layerWidth = -1;
   protected int _layerHeight = -1;
   protected int _layerCount = -1;
   protected String _command = "!INVALID!";
   protected YDisplay.UpdateCallback _valueCallbackDisplay = null;
   protected ArrayList<YDisplayLayer> _allDisplayLayers = new ArrayList<>();
   private Boolean _recording = false;
   private String _sequence;

   protected YDisplay(YAPIContext yctx, String func) {
      super(yctx, func);
      this._className = "Display";
   }

   protected YDisplay(String func) {
      this(YAPI.GetYCtx(true), func);
   }

   @Override
   protected void _parseAttr(YJSONObject json_val) throws Exception {
      if (json_val.has("enabled")) {
         this._enabled = json_val.getInt("enabled") > 0 ? 1 : 0;
      }

      if (json_val.has("startupSeq")) {
         this._startupSeq = json_val.getString("startupSeq");
      }

      if (json_val.has("brightness")) {
         this._brightness = json_val.getInt("brightness");
      }

      if (json_val.has("orientation")) {
         this._orientation = json_val.getInt("orientation");
      }

      if (json_val.has("displayWidth")) {
         this._displayWidth = json_val.getInt("displayWidth");
      }

      if (json_val.has("displayHeight")) {
         this._displayHeight = json_val.getInt("displayHeight");
      }

      if (json_val.has("displayType")) {
         this._displayType = json_val.getInt("displayType");
      }

      if (json_val.has("layerWidth")) {
         this._layerWidth = json_val.getInt("layerWidth");
      }

      if (json_val.has("layerHeight")) {
         this._layerHeight = json_val.getInt("layerHeight");
      }

      if (json_val.has("layerCount")) {
         this._layerCount = json_val.getInt("layerCount");
      }

      if (json_val.has("command")) {
         this._command = json_val.getString("command");
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

   public String get_startupSeq() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? "!INVALID!" : this._startupSeq;
      }
   }

   public String getStartupSeq() throws YAPI_Exception {
      return this.get_startupSeq();
   }

   public int set_startupSeq(String newval) throws YAPI_Exception {
      synchronized (this) {
         this._setAttr("startupSeq", newval);
         return 0;
      }
   }

   public int setStartupSeq(String newval) throws YAPI_Exception {
      return this.set_startupSeq(newval);
   }

   public int get_brightness() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -1 : this._brightness;
      }
   }

   public int getBrightness() throws YAPI_Exception {
      return this.get_brightness();
   }

   public int set_brightness(int newval) throws YAPI_Exception {
      synchronized (this) {
         String rest_val = Integer.toString(newval);
         this._setAttr("brightness", rest_val);
         return 0;
      }
   }

   public int setBrightness(int newval) throws YAPI_Exception {
      return this.set_brightness(newval);
   }

   public int get_orientation() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -1 : this._orientation;
      }
   }

   public int getOrientation() throws YAPI_Exception {
      return this.get_orientation();
   }

   public int set_orientation(int newval) throws YAPI_Exception {
      synchronized (this) {
         String rest_val = Integer.toString(newval);
         this._setAttr("orientation", rest_val);
         return 0;
      }
   }

   public int setOrientation(int newval) throws YAPI_Exception {
      return this.set_orientation(newval);
   }

   public int get_displayWidth() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration == 0L && this.load(this._yapi._defaultCacheValidity) != 0 ? -1 : this._displayWidth;
      }
   }

   public int getDisplayWidth() throws YAPI_Exception {
      return this.get_displayWidth();
   }

   public int get_displayHeight() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration == 0L && this.load(this._yapi._defaultCacheValidity) != 0 ? -1 : this._displayHeight;
      }
   }

   public int getDisplayHeight() throws YAPI_Exception {
      return this.get_displayHeight();
   }

   public int get_displayType() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration == 0L && this.load(this._yapi._defaultCacheValidity) != 0 ? -1 : this._displayType;
      }
   }

   public int getDisplayType() throws YAPI_Exception {
      return this.get_displayType();
   }

   public int get_layerWidth() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration == 0L && this.load(this._yapi._defaultCacheValidity) != 0 ? -1 : this._layerWidth;
      }
   }

   public int getLayerWidth() throws YAPI_Exception {
      return this.get_layerWidth();
   }

   public int get_layerHeight() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration == 0L && this.load(this._yapi._defaultCacheValidity) != 0 ? -1 : this._layerHeight;
      }
   }

   public int getLayerHeight() throws YAPI_Exception {
      return this.get_layerHeight();
   }

   public int get_layerCount() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration == 0L && this.load(this._yapi._defaultCacheValidity) != 0 ? -1 : this._layerCount;
      }
   }

   public int getLayerCount() throws YAPI_Exception {
      return this.get_layerCount();
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

   public static YDisplay FindDisplay(String func) {
      YAPIContext ctx = YAPI.GetYCtx(true);
      synchronized (ctx._functionCacheLock) {
         YDisplay obj = (YDisplay)YFunction._FindFromCache("Display", func);
         if (obj == null) {
            obj = new YDisplay(func);
            YFunction._AddToCache("Display", func, obj);
         }

         return obj;
      }
   }

   public static YDisplay FindDisplayInContext(YAPIContext yctx, String func) {
      synchronized (yctx._functionCacheLock) {
         YDisplay obj = (YDisplay)YFunction._FindFromCacheInContext(yctx, "Display", func);
         if (obj == null) {
            obj = new YDisplay(yctx, func);
            YFunction._AddToCache("Display", func, obj);
         }

         return obj;
      }
   }

   public int registerValueCallback(YDisplay.UpdateCallback callback) {
      if (callback != null) {
         YFunction._UpdateValueCallbackList(this, true);
      } else {
         YFunction._UpdateValueCallbackList(this, false);
      }

      this._valueCallbackDisplay = callback;
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
      if (this._valueCallbackDisplay != null) {
         this._valueCallbackDisplay.yNewValue(this, value);
      } else {
         super._invokeValueCallback(value);
      }

      return 0;
   }

   public int resetAll() throws YAPI_Exception {
      this.flushLayers();
      this.resetHiddenLayerFlags();
      return this.sendCommand("Z");
   }

   public int fade(int brightness, int duration) throws YAPI_Exception {
      this.flushLayers();
      return this.sendCommand(String.format(Locale.US, "+%d,%d", brightness, duration));
   }

   public int newSequence() throws YAPI_Exception {
      this.flushLayers();
      this._sequence = "";
      this._recording = true;
      return 0;
   }

   public int saveSequence(String sequenceName) throws YAPI_Exception {
      this.flushLayers();
      this._recording = false;
      this._upload(sequenceName, this._sequence.getBytes());
      this._sequence = "";
      return 0;
   }

   public int playSequence(String sequenceName) throws YAPI_Exception {
      this.flushLayers();
      return this.sendCommand(String.format(Locale.US, "S%s", sequenceName));
   }

   public int pauseSequence(int delay_ms) throws YAPI_Exception {
      this.flushLayers();
      return this.sendCommand(String.format(Locale.US, "W%d", delay_ms));
   }

   public int stopSequence() throws YAPI_Exception {
      this.flushLayers();
      return this.sendCommand("S");
   }

   public int upload(String pathname, byte[] content) throws YAPI_Exception {
      return this._upload(pathname, content);
   }

   public int copyLayerContent(int srcLayerId, int dstLayerId) throws YAPI_Exception {
      this.flushLayers();
      return this.sendCommand(String.format(Locale.US, "o%d,%d", srcLayerId, dstLayerId));
   }

   public int swapLayerContent(int layerIdA, int layerIdB) throws YAPI_Exception {
      this.flushLayers();
      return this.sendCommand(String.format(Locale.US, "E%d,%d", layerIdA, layerIdB));
   }

   public YDisplayLayer get_displayLayer(int layerId) throws YAPI_Exception {
      int layercount = this.get_layerCount();
      if (layerId >= 0 && layerId < layercount) {
         if (this._allDisplayLayers.size() == 0) {
            for (int idx = 0; idx < layercount; idx++) {
               this._allDisplayLayers.add(new YDisplayLayer(this, idx));
            }
         }

         return this._allDisplayLayers.get(layerId);
      } else {
         throw new YAPI_Exception(-2, "invalid DisplayLayer index");
      }
   }

   public YDisplay nextDisplay() {
      String next_hwid;
      try {
         String hwid = this._yapi._yHash.resolveHwID(this._className, this._func);
         next_hwid = this._yapi._yHash.getNextHardwareId(this._className, hwid);
      } catch (YAPI_Exception var3) {
         next_hwid = null;
      }

      return next_hwid == null ? null : FindDisplayInContext(this._yapi, next_hwid);
   }

   public static YDisplay FirstDisplay() {
      YAPIContext yctx = YAPI.GetYCtx(false);
      if (yctx == null) {
         return null;
      } else {
         String next_hwid = yctx._yHash.getFirstHardwareId("Display");
         return next_hwid == null ? null : FindDisplayInContext(yctx, next_hwid);
      }
   }

   public static YDisplay FirstDisplayInContext(YAPIContext yctx) {
      String next_hwid = yctx._yHash.getFirstHardwareId("Display");
      return next_hwid == null ? null : FindDisplayInContext(yctx, next_hwid);
   }

   public synchronized int flushLayers() throws YAPI_Exception {
      if (this._allDisplayLayers.size() > 0) {
         for (YDisplayLayer _allDisplayLayer : this._allDisplayLayers) {
            _allDisplayLayer.flush_now();
         }
      }

      return 0;
   }

   public synchronized void resetHiddenLayerFlags() {
      if (this._allDisplayLayers.size() > 0) {
         for (YDisplayLayer _allDisplayLayer : this._allDisplayLayers) {
            _allDisplayLayer.resetHiddenFlag();
         }
      }
   }

   public synchronized int sendCommand(String cmd) throws YAPI_Exception {
      if (!this._recording) {
         return this.set_command(cmd);
      } else {
         this._sequence = this._sequence + cmd + "\n";
         return 0;
      }
   }

   public interface TimedReportCallback {
      void timedReportCallback(YDisplay var1, YMeasure var2);
   }

   public interface UpdateCallback {
      void yNewValue(YDisplay var1, String var2);
   }
}
