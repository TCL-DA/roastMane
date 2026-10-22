package com.yoctopuce.YoctoAPI;

public class YAudioOut extends YFunction {
   public static final int VOLUME_INVALID = -1;
   public static final int MUTE_FALSE = 0;
   public static final int MUTE_TRUE = 1;
   public static final int MUTE_INVALID = -1;
   public static final String VOLUMERANGE_INVALID = "!INVALID!";
   public static final int SIGNAL_INVALID = Integer.MIN_VALUE;
   public static final int NOSIGNALFOR_INVALID = Integer.MIN_VALUE;
   protected int _volume = -1;
   protected int _mute = -1;
   protected String _volumeRange = "!INVALID!";
   protected int _signal = Integer.MIN_VALUE;
   protected int _noSignalFor = Integer.MIN_VALUE;
   protected YAudioOut.UpdateCallback _valueCallbackAudioOut = null;

   protected YAudioOut(YAPIContext ctx, String func) {
      super(ctx, func);
      this._className = "AudioOut";
   }

   protected YAudioOut(String func) {
      this(YAPI.GetYCtx(true), func);
   }

   @Override
   protected void _parseAttr(YJSONObject json_val) throws Exception {
      if (json_val.has("volume")) {
         this._volume = json_val.getInt("volume");
      }

      if (json_val.has("mute")) {
         this._mute = json_val.getInt("mute") > 0 ? 1 : 0;
      }

      if (json_val.has("volumeRange")) {
         this._volumeRange = json_val.getString("volumeRange");
      }

      if (json_val.has("signal")) {
         this._signal = json_val.getInt("signal");
      }

      if (json_val.has("noSignalFor")) {
         this._noSignalFor = json_val.getInt("noSignalFor");
      }

      super._parseAttr(json_val);
   }

   public int get_volume() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -1 : this._volume;
      }
   }

   public int getVolume() throws YAPI_Exception {
      return this.get_volume();
   }

   public int set_volume(int newval) throws YAPI_Exception {
      synchronized (this) {
         String rest_val = Integer.toString(newval);
         this._setAttr("volume", rest_val);
         return 0;
      }
   }

   public int setVolume(int newval) throws YAPI_Exception {
      return this.set_volume(newval);
   }

   public int get_mute() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -1 : this._mute;
      }
   }

   public int getMute() throws YAPI_Exception {
      return this.get_mute();
   }

   public int set_mute(int newval) throws YAPI_Exception {
      synchronized (this) {
         String rest_val = newval > 0 ? "1" : "0";
         this._setAttr("mute", rest_val);
         return 0;
      }
   }

   public int setMute(int newval) throws YAPI_Exception {
      return this.set_mute(newval);
   }

   public String get_volumeRange() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? "!INVALID!" : this._volumeRange;
      }
   }

   public String getVolumeRange() throws YAPI_Exception {
      return this.get_volumeRange();
   }

   public int get_signal() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? Integer.MIN_VALUE : this._signal;
      }
   }

   public int getSignal() throws YAPI_Exception {
      return this.get_signal();
   }

   public int get_noSignalFor() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? Integer.MIN_VALUE : this._noSignalFor;
      }
   }

   public int getNoSignalFor() throws YAPI_Exception {
      return this.get_noSignalFor();
   }

   public static YAudioOut FindAudioOut(String func) {
      YAPIContext ctx = YAPI.GetYCtx(true);
      synchronized (ctx._functionCacheLock) {
         YAudioOut obj = (YAudioOut)YFunction._FindFromCache("AudioOut", func);
         if (obj == null) {
            obj = new YAudioOut(func);
            YFunction._AddToCache("AudioOut", func, obj);
         }

         return obj;
      }
   }

   public static YAudioOut FindAudioOutInContext(YAPIContext yctx, String func) {
      synchronized (yctx._functionCacheLock) {
         YAudioOut obj = (YAudioOut)YFunction._FindFromCacheInContext(yctx, "AudioOut", func);
         if (obj == null) {
            obj = new YAudioOut(yctx, func);
            YFunction._AddToCache("AudioOut", func, obj);
         }

         return obj;
      }
   }

   public int registerValueCallback(YAudioOut.UpdateCallback callback) {
      if (callback != null) {
         YFunction._UpdateValueCallbackList(this, true);
      } else {
         YFunction._UpdateValueCallbackList(this, false);
      }

      this._valueCallbackAudioOut = callback;
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
      if (this._valueCallbackAudioOut != null) {
         this._valueCallbackAudioOut.yNewValue(this, value);
      } else {
         super._invokeValueCallback(value);
      }

      return 0;
   }

   public YAudioOut nextAudioOut() {
      String next_hwid;
      try {
         String hwid = this._yapi._yHash.resolveHwID(this._className, this._func);
         next_hwid = this._yapi._yHash.getNextHardwareId(this._className, hwid);
      } catch (YAPI_Exception var3) {
         next_hwid = null;
      }

      return next_hwid == null ? null : FindAudioOutInContext(this._yapi, next_hwid);
   }

   public static YAudioOut FirstAudioOut() {
      YAPIContext yctx = YAPI.GetYCtx(false);
      if (yctx == null) {
         return null;
      } else {
         String next_hwid = yctx._yHash.getFirstHardwareId("AudioOut");
         return next_hwid == null ? null : FindAudioOutInContext(yctx, next_hwid);
      }
   }

   public static YAudioOut FirstAudioOutInContext(YAPIContext yctx) {
      String next_hwid = yctx._yHash.getFirstHardwareId("AudioOut");
      return next_hwid == null ? null : FindAudioOutInContext(yctx, next_hwid);
   }

   public interface TimedReportCallback {
      void timedReportCallback(YAudioOut var1, YMeasure var2);
   }

   public interface UpdateCallback {
      void yNewValue(YAudioOut var1, String var2);
   }
}
