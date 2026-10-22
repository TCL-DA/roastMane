package com.yoctopuce.YoctoAPI;

public class YSegmentedDisplay extends YFunction {
   public static final String DISPLAYEDTEXT_INVALID = "!INVALID!";
   public static final int DISPLAYMODE_DISCONNECTED = 0;
   public static final int DISPLAYMODE_MANUAL = 1;
   public static final int DISPLAYMODE_AUTO1 = 2;
   public static final int DISPLAYMODE_AUTO60 = 3;
   public static final int DISPLAYMODE_INVALID = -1;
   protected String _displayedText = "!INVALID!";
   protected int _displayMode = -1;
   protected YSegmentedDisplay.UpdateCallback _valueCallbackSegmentedDisplay = null;

   protected YSegmentedDisplay(YAPIContext ctx, String func) {
      super(ctx, func);
      this._className = "SegmentedDisplay";
   }

   protected YSegmentedDisplay(String func) {
      this(YAPI.GetYCtx(true), func);
   }

   @Override
   protected void _parseAttr(YJSONObject json_val) throws Exception {
      if (json_val.has("displayedText")) {
         this._displayedText = json_val.getString("displayedText");
      }

      if (json_val.has("displayMode")) {
         this._displayMode = json_val.getInt("displayMode");
      }

      super._parseAttr(json_val);
   }

   public String get_displayedText() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? "!INVALID!" : this._displayedText;
      }
   }

   public String getDisplayedText() throws YAPI_Exception {
      return this.get_displayedText();
   }

   public int set_displayedText(String newval) throws YAPI_Exception {
      synchronized (this) {
         this._setAttr("displayedText", newval);
         return 0;
      }
   }

   public int setDisplayedText(String newval) throws YAPI_Exception {
      return this.set_displayedText(newval);
   }

   public int get_displayMode() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -1 : this._displayMode;
      }
   }

   public int set_displayMode(int newval) throws YAPI_Exception {
      synchronized (this) {
         String rest_val = Integer.toString(newval);
         this._setAttr("displayMode", rest_val);
         return 0;
      }
   }

   public static YSegmentedDisplay FindSegmentedDisplay(String func) {
      YAPIContext ctx = YAPI.GetYCtx(true);
      synchronized (ctx._functionCacheLock) {
         YSegmentedDisplay obj = (YSegmentedDisplay)YFunction._FindFromCache("SegmentedDisplay", func);
         if (obj == null) {
            obj = new YSegmentedDisplay(func);
            YFunction._AddToCache("SegmentedDisplay", func, obj);
         }

         return obj;
      }
   }

   public static YSegmentedDisplay FindSegmentedDisplayInContext(YAPIContext yctx, String func) {
      synchronized (yctx._functionCacheLock) {
         YSegmentedDisplay obj = (YSegmentedDisplay)YFunction._FindFromCacheInContext(yctx, "SegmentedDisplay", func);
         if (obj == null) {
            obj = new YSegmentedDisplay(yctx, func);
            YFunction._AddToCache("SegmentedDisplay", func, obj);
         }

         return obj;
      }
   }

   public int registerValueCallback(YSegmentedDisplay.UpdateCallback callback) {
      if (callback != null) {
         YFunction._UpdateValueCallbackList(this, true);
      } else {
         YFunction._UpdateValueCallbackList(this, false);
      }

      this._valueCallbackSegmentedDisplay = callback;
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
      if (this._valueCallbackSegmentedDisplay != null) {
         this._valueCallbackSegmentedDisplay.yNewValue(this, value);
      } else {
         super._invokeValueCallback(value);
      }

      return 0;
   }

   public YSegmentedDisplay nextSegmentedDisplay() {
      String next_hwid;
      try {
         String hwid = this._yapi._yHash.resolveHwID(this._className, this._func);
         next_hwid = this._yapi._yHash.getNextHardwareId(this._className, hwid);
      } catch (YAPI_Exception var3) {
         next_hwid = null;
      }

      return next_hwid == null ? null : FindSegmentedDisplayInContext(this._yapi, next_hwid);
   }

   public static YSegmentedDisplay FirstSegmentedDisplay() {
      YAPIContext yctx = YAPI.GetYCtx(false);
      if (yctx == null) {
         return null;
      } else {
         String next_hwid = yctx._yHash.getFirstHardwareId("SegmentedDisplay");
         return next_hwid == null ? null : FindSegmentedDisplayInContext(yctx, next_hwid);
      }
   }

   public static YSegmentedDisplay FirstSegmentedDisplayInContext(YAPIContext yctx) {
      String next_hwid = yctx._yHash.getFirstHardwareId("SegmentedDisplay");
      return next_hwid == null ? null : FindSegmentedDisplayInContext(yctx, next_hwid);
   }

   public interface TimedReportCallback {
      void timedReportCallback(YSegmentedDisplay var1, YMeasure var2);
   }

   public interface UpdateCallback {
      void yNewValue(YSegmentedDisplay var1, String var2);
   }
}
