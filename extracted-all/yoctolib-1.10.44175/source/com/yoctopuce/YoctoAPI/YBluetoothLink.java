package com.yoctopuce.YoctoAPI;

public class YBluetoothLink extends YFunction {
   public static final String OWNADDRESS_INVALID = "!INVALID!";
   public static final String PAIRINGPIN_INVALID = "!INVALID!";
   public static final String REMOTEADDRESS_INVALID = "!INVALID!";
   public static final String REMOTENAME_INVALID = "!INVALID!";
   public static final int MUTE_FALSE = 0;
   public static final int MUTE_TRUE = 1;
   public static final int MUTE_INVALID = -1;
   public static final int PREAMPLIFIER_INVALID = -1;
   public static final int VOLUME_INVALID = -1;
   public static final int LINKSTATE_DOWN = 0;
   public static final int LINKSTATE_FREE = 1;
   public static final int LINKSTATE_SEARCH = 2;
   public static final int LINKSTATE_EXISTS = 3;
   public static final int LINKSTATE_LINKED = 4;
   public static final int LINKSTATE_PLAY = 5;
   public static final int LINKSTATE_INVALID = -1;
   public static final int LINKQUALITY_INVALID = -1;
   public static final String COMMAND_INVALID = "!INVALID!";
   protected String _ownAddress = "!INVALID!";
   protected String _pairingPin = "!INVALID!";
   protected String _remoteAddress = "!INVALID!";
   protected String _remoteName = "!INVALID!";
   protected int _mute = -1;
   protected int _preAmplifier = -1;
   protected int _volume = -1;
   protected int _linkState = -1;
   protected int _linkQuality = -1;
   protected String _command = "!INVALID!";
   protected YBluetoothLink.UpdateCallback _valueCallbackBluetoothLink = null;

   protected YBluetoothLink(YAPIContext ctx, String func) {
      super(ctx, func);
      this._className = "BluetoothLink";
   }

   protected YBluetoothLink(String func) {
      this(YAPI.GetYCtx(true), func);
   }

   @Override
   protected void _parseAttr(YJSONObject json_val) throws Exception {
      if (json_val.has("ownAddress")) {
         this._ownAddress = json_val.getString("ownAddress");
      }

      if (json_val.has("pairingPin")) {
         this._pairingPin = json_val.getString("pairingPin");
      }

      if (json_val.has("remoteAddress")) {
         this._remoteAddress = json_val.getString("remoteAddress");
      }

      if (json_val.has("remoteName")) {
         this._remoteName = json_val.getString("remoteName");
      }

      if (json_val.has("mute")) {
         this._mute = json_val.getInt("mute") > 0 ? 1 : 0;
      }

      if (json_val.has("preAmplifier")) {
         this._preAmplifier = json_val.getInt("preAmplifier");
      }

      if (json_val.has("volume")) {
         this._volume = json_val.getInt("volume");
      }

      if (json_val.has("linkState")) {
         this._linkState = json_val.getInt("linkState");
      }

      if (json_val.has("linkQuality")) {
         this._linkQuality = json_val.getInt("linkQuality");
      }

      if (json_val.has("command")) {
         this._command = json_val.getString("command");
      }

      super._parseAttr(json_val);
   }

   public String get_ownAddress() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? "!INVALID!" : this._ownAddress;
      }
   }

   public String getOwnAddress() throws YAPI_Exception {
      return this.get_ownAddress();
   }

   public String get_pairingPin() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? "!INVALID!" : this._pairingPin;
      }
   }

   public String getPairingPin() throws YAPI_Exception {
      return this.get_pairingPin();
   }

   public int set_pairingPin(String newval) throws YAPI_Exception {
      synchronized (this) {
         this._setAttr("pairingPin", newval);
         return 0;
      }
   }

   public int setPairingPin(String newval) throws YAPI_Exception {
      return this.set_pairingPin(newval);
   }

   public String get_remoteAddress() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? "!INVALID!" : this._remoteAddress;
      }
   }

   public String getRemoteAddress() throws YAPI_Exception {
      return this.get_remoteAddress();
   }

   public int set_remoteAddress(String newval) throws YAPI_Exception {
      synchronized (this) {
         this._setAttr("remoteAddress", newval);
         return 0;
      }
   }

   public int setRemoteAddress(String newval) throws YAPI_Exception {
      return this.set_remoteAddress(newval);
   }

   public String get_remoteName() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? "!INVALID!" : this._remoteName;
      }
   }

   public String getRemoteName() throws YAPI_Exception {
      return this.get_remoteName();
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

   public int get_preAmplifier() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -1 : this._preAmplifier;
      }
   }

   public int getPreAmplifier() throws YAPI_Exception {
      return this.get_preAmplifier();
   }

   public int set_preAmplifier(int newval) throws YAPI_Exception {
      synchronized (this) {
         String rest_val = Integer.toString(newval);
         this._setAttr("preAmplifier", rest_val);
         return 0;
      }
   }

   public int setPreAmplifier(int newval) throws YAPI_Exception {
      return this.set_preAmplifier(newval);
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

   public int get_linkState() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -1 : this._linkState;
      }
   }

   public int getLinkState() throws YAPI_Exception {
      return this.get_linkState();
   }

   public int get_linkQuality() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -1 : this._linkQuality;
      }
   }

   public int getLinkQuality() throws YAPI_Exception {
      return this.get_linkQuality();
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

   public static YBluetoothLink FindBluetoothLink(String func) {
      YAPIContext ctx = YAPI.GetYCtx(true);
      synchronized (ctx._functionCacheLock) {
         YBluetoothLink obj = (YBluetoothLink)YFunction._FindFromCache("BluetoothLink", func);
         if (obj == null) {
            obj = new YBluetoothLink(func);
            YFunction._AddToCache("BluetoothLink", func, obj);
         }

         return obj;
      }
   }

   public static YBluetoothLink FindBluetoothLinkInContext(YAPIContext yctx, String func) {
      synchronized (yctx._functionCacheLock) {
         YBluetoothLink obj = (YBluetoothLink)YFunction._FindFromCacheInContext(yctx, "BluetoothLink", func);
         if (obj == null) {
            obj = new YBluetoothLink(yctx, func);
            YFunction._AddToCache("BluetoothLink", func, obj);
         }

         return obj;
      }
   }

   public int registerValueCallback(YBluetoothLink.UpdateCallback callback) {
      if (callback != null) {
         YFunction._UpdateValueCallbackList(this, true);
      } else {
         YFunction._UpdateValueCallbackList(this, false);
      }

      this._valueCallbackBluetoothLink = callback;
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
      if (this._valueCallbackBluetoothLink != null) {
         this._valueCallbackBluetoothLink.yNewValue(this, value);
      } else {
         super._invokeValueCallback(value);
      }

      return 0;
   }

   public int connect() throws YAPI_Exception {
      return this.set_command("C");
   }

   public int disconnect() throws YAPI_Exception {
      return this.set_command("D");
   }

   public YBluetoothLink nextBluetoothLink() {
      String next_hwid;
      try {
         String hwid = this._yapi._yHash.resolveHwID(this._className, this._func);
         next_hwid = this._yapi._yHash.getNextHardwareId(this._className, hwid);
      } catch (YAPI_Exception var3) {
         next_hwid = null;
      }

      return next_hwid == null ? null : FindBluetoothLinkInContext(this._yapi, next_hwid);
   }

   public static YBluetoothLink FirstBluetoothLink() {
      YAPIContext yctx = YAPI.GetYCtx(false);
      if (yctx == null) {
         return null;
      } else {
         String next_hwid = yctx._yHash.getFirstHardwareId("BluetoothLink");
         return next_hwid == null ? null : FindBluetoothLinkInContext(yctx, next_hwid);
      }
   }

   public static YBluetoothLink FirstBluetoothLinkInContext(YAPIContext yctx) {
      String next_hwid = yctx._yHash.getFirstHardwareId("BluetoothLink");
      return next_hwid == null ? null : FindBluetoothLinkInContext(yctx, next_hwid);
   }

   public interface TimedReportCallback {
      void timedReportCallback(YBluetoothLink var1, YMeasure var2);
   }

   public interface UpdateCallback {
      void yNewValue(YBluetoothLink var1, String var2);
   }
}
