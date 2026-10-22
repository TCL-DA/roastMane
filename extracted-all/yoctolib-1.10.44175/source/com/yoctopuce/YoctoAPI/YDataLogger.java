package com.yoctopuce.YoctoAPI;

import java.util.ArrayList;
import java.util.Locale;

public class YDataLogger extends YFunction {
   public static final int CURRENTRUNINDEX_INVALID = -1;
   public static final long TIMEUTC_INVALID = -9223372036854775807L;
   public static final int RECORDING_OFF = 0;
   public static final int RECORDING_ON = 1;
   public static final int RECORDING_PENDING = 2;
   public static final int RECORDING_INVALID = -1;
   public static final int AUTOSTART_OFF = 0;
   public static final int AUTOSTART_ON = 1;
   public static final int AUTOSTART_INVALID = -1;
   public static final int BEACONDRIVEN_OFF = 0;
   public static final int BEACONDRIVEN_ON = 1;
   public static final int BEACONDRIVEN_INVALID = -1;
   public static final int USAGE_INVALID = -1;
   public static final int CLEARHISTORY_FALSE = 0;
   public static final int CLEARHISTORY_TRUE = 1;
   public static final int CLEARHISTORY_INVALID = -1;
   protected int _currentRunIndex = -1;
   protected long _timeUTC = -9223372036854775807L;
   protected int _recording = -1;
   protected int _autoStart = -1;
   protected int _beaconDriven = -1;
   protected int _usage = -1;
   protected int _clearHistory = -1;
   protected YDataLogger.UpdateCallback _valueCallbackDataLogger = null;
   protected String _dataLoggerURL;

   public String getData(Integer runIdx, Integer timeIdx) throws YAPI_Exception {
      if (this._dataLoggerURL == null) {
         this._dataLoggerURL = "/logger.json";
      }

      String devid = this.module().get_serialNumber();
      String httpreq = "GET " + this._dataLoggerURL;
      if (timeIdx != null) {
         httpreq = httpreq + String.format(Locale.US, "?run=%d&time=%d", runIdx, timeIdx);
      }

      YDevice dev = this._yapi._yHash.getDevice(devid);
      return dev.requestHTTPSyncAsString(httpreq, null);
   }

   protected YDataLogger(YAPIContext yctx, String func) {
      super(yctx, func);
      this._className = "DataLogger";
   }

   protected YDataLogger(String func) {
      this(YAPI.GetYCtx(true), func);
   }

   @Override
   protected void _parseAttr(YJSONObject json_val) throws Exception {
      if (json_val.has("currentRunIndex")) {
         this._currentRunIndex = json_val.getInt("currentRunIndex");
      }

      if (json_val.has("timeUTC")) {
         this._timeUTC = json_val.getLong("timeUTC");
      }

      if (json_val.has("recording")) {
         this._recording = json_val.getInt("recording");
      }

      if (json_val.has("autoStart")) {
         this._autoStart = json_val.getInt("autoStart") > 0 ? 1 : 0;
      }

      if (json_val.has("beaconDriven")) {
         this._beaconDriven = json_val.getInt("beaconDriven") > 0 ? 1 : 0;
      }

      if (json_val.has("usage")) {
         this._usage = json_val.getInt("usage");
      }

      if (json_val.has("clearHistory")) {
         this._clearHistory = json_val.getInt("clearHistory") > 0 ? 1 : 0;
      }

      super._parseAttr(json_val);
   }

   public int get_currentRunIndex() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -1 : this._currentRunIndex;
      }
   }

   public int getCurrentRunIndex() throws YAPI_Exception {
      return this.get_currentRunIndex();
   }

   public long get_timeUTC() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -9223372036854775807L : this._timeUTC;
      }
   }

   public long getTimeUTC() throws YAPI_Exception {
      return this.get_timeUTC();
   }

   public int set_timeUTC(long newval) throws YAPI_Exception {
      synchronized (this) {
         String rest_val = Long.toString(newval);
         this._setAttr("timeUTC", rest_val);
         return 0;
      }
   }

   public int setTimeUTC(long newval) throws YAPI_Exception {
      return this.set_timeUTC(newval);
   }

   public int get_recording() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -1 : this._recording;
      }
   }

   public int getRecording() throws YAPI_Exception {
      return this.get_recording();
   }

   public int set_recording(int newval) throws YAPI_Exception {
      synchronized (this) {
         String rest_val = Integer.toString(newval);
         this._setAttr("recording", rest_val);
         return 0;
      }
   }

   public int setRecording(int newval) throws YAPI_Exception {
      return this.set_recording(newval);
   }

   public int get_autoStart() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -1 : this._autoStart;
      }
   }

   public int getAutoStart() throws YAPI_Exception {
      return this.get_autoStart();
   }

   public int set_autoStart(int newval) throws YAPI_Exception {
      synchronized (this) {
         String rest_val = newval > 0 ? "1" : "0";
         this._setAttr("autoStart", rest_val);
         return 0;
      }
   }

   public int setAutoStart(int newval) throws YAPI_Exception {
      return this.set_autoStart(newval);
   }

   public int get_beaconDriven() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -1 : this._beaconDriven;
      }
   }

   public int getBeaconDriven() throws YAPI_Exception {
      return this.get_beaconDriven();
   }

   public int set_beaconDriven(int newval) throws YAPI_Exception {
      synchronized (this) {
         String rest_val = newval > 0 ? "1" : "0";
         this._setAttr("beaconDriven", rest_val);
         return 0;
      }
   }

   public int setBeaconDriven(int newval) throws YAPI_Exception {
      return this.set_beaconDriven(newval);
   }

   public int get_usage() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -1 : this._usage;
      }
   }

   public int getUsage() throws YAPI_Exception {
      return this.get_usage();
   }

   public int get_clearHistory() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -1 : this._clearHistory;
      }
   }

   public int set_clearHistory(int newval) throws YAPI_Exception {
      synchronized (this) {
         String rest_val = newval > 0 ? "1" : "0";
         this._setAttr("clearHistory", rest_val);
         return 0;
      }
   }

   public static YDataLogger FindDataLogger(String func) {
      YAPIContext ctx = YAPI.GetYCtx(true);
      synchronized (ctx._functionCacheLock) {
         YDataLogger obj = (YDataLogger)YFunction._FindFromCache("DataLogger", func);
         if (obj == null) {
            obj = new YDataLogger(func);
            YFunction._AddToCache("DataLogger", func, obj);
         }

         return obj;
      }
   }

   public static YDataLogger FindDataLoggerInContext(YAPIContext yctx, String func) {
      synchronized (yctx._functionCacheLock) {
         YDataLogger obj = (YDataLogger)YFunction._FindFromCacheInContext(yctx, "DataLogger", func);
         if (obj == null) {
            obj = new YDataLogger(yctx, func);
            YFunction._AddToCache("DataLogger", func, obj);
         }

         return obj;
      }
   }

   public int registerValueCallback(YDataLogger.UpdateCallback callback) {
      if (callback != null) {
         YFunction._UpdateValueCallbackList(this, true);
      } else {
         YFunction._UpdateValueCallbackList(this, false);
      }

      this._valueCallbackDataLogger = callback;
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
      if (this._valueCallbackDataLogger != null) {
         this._valueCallbackDataLogger.yNewValue(this, value);
      } else {
         super._invokeValueCallback(value);
      }

      return 0;
   }

   public int forgetAllDataStreams() throws YAPI_Exception {
      return this.set_clearHistory(1);
   }

   public ArrayList<YDataSet> get_dataSets() throws YAPI_Exception {
      return this.parse_dataSets(this._download("logger.json"));
   }

   public ArrayList<YDataSet> parse_dataSets(byte[] json) throws YAPI_Exception {
      new ArrayList();
      ArrayList<YDataSet> res = new ArrayList<>();
      ArrayList<String> dslist = this._json_get_array(json);
      res.clear();

      for (String ii : dslist) {
         YDataSet dataset = new YDataSet(this);
         dataset._parse(ii);
         res.add(dataset);
      }

      return res;
   }

   public YDataLogger nextDataLogger() {
      String next_hwid;
      try {
         String hwid = this._yapi._yHash.resolveHwID(this._className, this._func);
         next_hwid = this._yapi._yHash.getNextHardwareId(this._className, hwid);
      } catch (YAPI_Exception var3) {
         next_hwid = null;
      }

      return next_hwid == null ? null : FindDataLoggerInContext(this._yapi, next_hwid);
   }

   public static YDataLogger FirstDataLogger() {
      YAPIContext yctx = YAPI.GetYCtx(false);
      if (yctx == null) {
         return null;
      } else {
         String next_hwid = yctx._yHash.getFirstHardwareId("DataLogger");
         return next_hwid == null ? null : FindDataLoggerInContext(yctx, next_hwid);
      }
   }

   public static YDataLogger FirstDataLoggerInContext(YAPIContext yctx) {
      String next_hwid = yctx._yHash.getFirstHardwareId("DataLogger");
      return next_hwid == null ? null : FindDataLoggerInContext(yctx, next_hwid);
   }

   public interface TimedReportCallback {
      void timedReportCallback(YDataLogger var1, YMeasure var2);
   }

   public interface UpdateCallback {
      void yNewValue(YDataLogger var1, String var2);
   }
}
