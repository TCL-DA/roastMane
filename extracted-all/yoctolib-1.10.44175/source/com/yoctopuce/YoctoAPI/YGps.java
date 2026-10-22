package com.yoctopuce.YoctoAPI;

public class YGps extends YFunction {
   public static final int ISFIXED_FALSE = 0;
   public static final int ISFIXED_TRUE = 1;
   public static final int ISFIXED_INVALID = -1;
   public static final long SATCOUNT_INVALID = -9223372036854775807L;
   public static final long SATPERCONST_INVALID = -9223372036854775807L;
   public static final double GPSREFRESHRATE_INVALID = -1.79769313486231E308;
   public static final int COORDSYSTEM_GPS_DMS = 0;
   public static final int COORDSYSTEM_GPS_DM = 1;
   public static final int COORDSYSTEM_GPS_D = 2;
   public static final int COORDSYSTEM_INVALID = -1;
   public static final int CONSTELLATION_GNSS = 0;
   public static final int CONSTELLATION_GPS = 1;
   public static final int CONSTELLATION_GLONASS = 2;
   public static final int CONSTELLATION_GALILEO = 3;
   public static final int CONSTELLATION_GPS_GLONASS = 4;
   public static final int CONSTELLATION_GPS_GALILEO = 5;
   public static final int CONSTELLATION_GLONASS_GALILEO = 6;
   public static final int CONSTELLATION_INVALID = -1;
   public static final String LATITUDE_INVALID = "!INVALID!";
   public static final String LONGITUDE_INVALID = "!INVALID!";
   public static final double DILUTION_INVALID = -1.79769313486231E308;
   public static final double ALTITUDE_INVALID = -1.79769313486231E308;
   public static final double GROUNDSPEED_INVALID = -1.79769313486231E308;
   public static final double DIRECTION_INVALID = -1.79769313486231E308;
   public static final long UNIXTIME_INVALID = -9223372036854775807L;
   public static final String DATETIME_INVALID = "!INVALID!";
   public static final int UTCOFFSET_INVALID = Integer.MIN_VALUE;
   public static final String COMMAND_INVALID = "!INVALID!";
   protected int _isFixed = -1;
   protected long _satCount = -9223372036854775807L;
   protected long _satPerConst = -9223372036854775807L;
   protected double _gpsRefreshRate = -1.79769313486231E308;
   protected int _coordSystem = -1;
   protected int _constellation = -1;
   protected String _latitude = "!INVALID!";
   protected String _longitude = "!INVALID!";
   protected double _dilution = -1.79769313486231E308;
   protected double _altitude = -1.79769313486231E308;
   protected double _groundSpeed = -1.79769313486231E308;
   protected double _direction = -1.79769313486231E308;
   protected long _unixTime = -9223372036854775807L;
   protected String _dateTime = "!INVALID!";
   protected int _utcOffset = Integer.MIN_VALUE;
   protected String _command = "!INVALID!";
   protected YGps.UpdateCallback _valueCallbackGps = null;

   protected YGps(YAPIContext ctx, String func) {
      super(ctx, func);
      this._className = "Gps";
   }

   protected YGps(String func) {
      this(YAPI.GetYCtx(true), func);
   }

   @Override
   protected void _parseAttr(YJSONObject json_val) throws Exception {
      if (json_val.has("isFixed")) {
         this._isFixed = json_val.getInt("isFixed") > 0 ? 1 : 0;
      }

      if (json_val.has("satCount")) {
         this._satCount = json_val.getLong("satCount");
      }

      if (json_val.has("satPerConst")) {
         this._satPerConst = json_val.getLong("satPerConst");
      }

      if (json_val.has("gpsRefreshRate")) {
         this._gpsRefreshRate = (double)Math.round(json_val.getDouble("gpsRefreshRate") * 1000.0 / 65536.0) / 1000.0;
      }

      if (json_val.has("coordSystem")) {
         this._coordSystem = json_val.getInt("coordSystem");
      }

      if (json_val.has("constellation")) {
         this._constellation = json_val.getInt("constellation");
      }

      if (json_val.has("latitude")) {
         this._latitude = json_val.getString("latitude");
      }

      if (json_val.has("longitude")) {
         this._longitude = json_val.getString("longitude");
      }

      if (json_val.has("dilution")) {
         this._dilution = (double)Math.round(json_val.getDouble("dilution") * 1000.0 / 65536.0) / 1000.0;
      }

      if (json_val.has("altitude")) {
         this._altitude = (double)Math.round(json_val.getDouble("altitude") * 1000.0 / 65536.0) / 1000.0;
      }

      if (json_val.has("groundSpeed")) {
         this._groundSpeed = (double)Math.round(json_val.getDouble("groundSpeed") * 1000.0 / 65536.0) / 1000.0;
      }

      if (json_val.has("direction")) {
         this._direction = (double)Math.round(json_val.getDouble("direction") * 1000.0 / 65536.0) / 1000.0;
      }

      if (json_val.has("unixTime")) {
         this._unixTime = json_val.getLong("unixTime");
      }

      if (json_val.has("dateTime")) {
         this._dateTime = json_val.getString("dateTime");
      }

      if (json_val.has("utcOffset")) {
         this._utcOffset = json_val.getInt("utcOffset");
      }

      if (json_val.has("command")) {
         this._command = json_val.getString("command");
      }

      super._parseAttr(json_val);
   }

   public int get_isFixed() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -1 : this._isFixed;
      }
   }

   public int getIsFixed() throws YAPI_Exception {
      return this.get_isFixed();
   }

   public long get_satCount() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0
            ? -9223372036854775807L
            : this._satCount;
      }
   }

   public long getSatCount() throws YAPI_Exception {
      return this.get_satCount();
   }

   public long get_satPerConst() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0
            ? -9223372036854775807L
            : this._satPerConst;
      }
   }

   public long getSatPerConst() throws YAPI_Exception {
      return this.get_satPerConst();
   }

   public double get_gpsRefreshRate() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0
            ? -1.79769313486231E308
            : this._gpsRefreshRate;
      }
   }

   public double getGpsRefreshRate() throws YAPI_Exception {
      return this.get_gpsRefreshRate();
   }

   public int get_coordSystem() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -1 : this._coordSystem;
      }
   }

   public int getCoordSystem() throws YAPI_Exception {
      return this.get_coordSystem();
   }

   public int set_coordSystem(int newval) throws YAPI_Exception {
      synchronized (this) {
         String rest_val = Integer.toString(newval);
         this._setAttr("coordSystem", rest_val);
         return 0;
      }
   }

   public int setCoordSystem(int newval) throws YAPI_Exception {
      return this.set_coordSystem(newval);
   }

   public int get_constellation() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -1 : this._constellation;
      }
   }

   public int getConstellation() throws YAPI_Exception {
      return this.get_constellation();
   }

   public int set_constellation(int newval) throws YAPI_Exception {
      synchronized (this) {
         String rest_val = Integer.toString(newval);
         this._setAttr("constellation", rest_val);
         return 0;
      }
   }

   public int setConstellation(int newval) throws YAPI_Exception {
      return this.set_constellation(newval);
   }

   public String get_latitude() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? "!INVALID!" : this._latitude;
      }
   }

   public String getLatitude() throws YAPI_Exception {
      return this.get_latitude();
   }

   public String get_longitude() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? "!INVALID!" : this._longitude;
      }
   }

   public String getLongitude() throws YAPI_Exception {
      return this.get_longitude();
   }

   public double get_dilution() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0
            ? -1.79769313486231E308
            : this._dilution;
      }
   }

   public double getDilution() throws YAPI_Exception {
      return this.get_dilution();
   }

   public double get_altitude() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0
            ? -1.79769313486231E308
            : this._altitude;
      }
   }

   public double getAltitude() throws YAPI_Exception {
      return this.get_altitude();
   }

   public double get_groundSpeed() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0
            ? -1.79769313486231E308
            : this._groundSpeed;
      }
   }

   public double getGroundSpeed() throws YAPI_Exception {
      return this.get_groundSpeed();
   }

   public double get_direction() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0
            ? -1.79769313486231E308
            : this._direction;
      }
   }

   public double getDirection() throws YAPI_Exception {
      return this.get_direction();
   }

   public long get_unixTime() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0
            ? -9223372036854775807L
            : this._unixTime;
      }
   }

   public long getUnixTime() throws YAPI_Exception {
      return this.get_unixTime();
   }

   public String get_dateTime() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? "!INVALID!" : this._dateTime;
      }
   }

   public String getDateTime() throws YAPI_Exception {
      return this.get_dateTime();
   }

   public int get_utcOffset() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? Integer.MIN_VALUE : this._utcOffset;
      }
   }

   public int getUtcOffset() throws YAPI_Exception {
      return this.get_utcOffset();
   }

   public int set_utcOffset(int newval) throws YAPI_Exception {
      synchronized (this) {
         String rest_val = Integer.toString(newval);
         this._setAttr("utcOffset", rest_val);
         return 0;
      }
   }

   public int setUtcOffset(int newval) throws YAPI_Exception {
      return this.set_utcOffset(newval);
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

   public static YGps FindGps(String func) {
      YAPIContext ctx = YAPI.GetYCtx(true);
      synchronized (ctx._functionCacheLock) {
         YGps obj = (YGps)YFunction._FindFromCache("Gps", func);
         if (obj == null) {
            obj = new YGps(func);
            YFunction._AddToCache("Gps", func, obj);
         }

         return obj;
      }
   }

   public static YGps FindGpsInContext(YAPIContext yctx, String func) {
      synchronized (yctx._functionCacheLock) {
         YGps obj = (YGps)YFunction._FindFromCacheInContext(yctx, "Gps", func);
         if (obj == null) {
            obj = new YGps(yctx, func);
            YFunction._AddToCache("Gps", func, obj);
         }

         return obj;
      }
   }

   public int registerValueCallback(YGps.UpdateCallback callback) {
      if (callback != null) {
         YFunction._UpdateValueCallbackList(this, true);
      } else {
         YFunction._UpdateValueCallbackList(this, false);
      }

      this._valueCallbackGps = callback;
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
      if (this._valueCallbackGps != null) {
         this._valueCallbackGps.yNewValue(this, value);
      } else {
         super._invokeValueCallback(value);
      }

      return 0;
   }

   public YGps nextGps() {
      String next_hwid;
      try {
         String hwid = this._yapi._yHash.resolveHwID(this._className, this._func);
         next_hwid = this._yapi._yHash.getNextHardwareId(this._className, hwid);
      } catch (YAPI_Exception var3) {
         next_hwid = null;
      }

      return next_hwid == null ? null : FindGpsInContext(this._yapi, next_hwid);
   }

   public static YGps FirstGps() {
      YAPIContext yctx = YAPI.GetYCtx(false);
      if (yctx == null) {
         return null;
      } else {
         String next_hwid = yctx._yHash.getFirstHardwareId("Gps");
         return next_hwid == null ? null : FindGpsInContext(yctx, next_hwid);
      }
   }

   public static YGps FirstGpsInContext(YAPIContext yctx) {
      String next_hwid = yctx._yHash.getFirstHardwareId("Gps");
      return next_hwid == null ? null : FindGpsInContext(yctx, next_hwid);
   }

   public interface TimedReportCallback {
      void timedReportCallback(YGps var1, YMeasure var2);
   }

   public interface UpdateCallback {
      void yNewValue(YGps var1, String var2);
   }
}
