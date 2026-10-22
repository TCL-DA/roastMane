package com.yoctopuce.YoctoAPI;

public class YWakeUpSchedule extends YFunction {
   public static final int MINUTESA_INVALID = -1;
   public static final int MINUTESB_INVALID = -1;
   public static final int HOURS_INVALID = -1;
   public static final int WEEKDAYS_INVALID = -1;
   public static final int MONTHDAYS_INVALID = -1;
   public static final int MONTHS_INVALID = -1;
   public static final long NEXTOCCURENCE_INVALID = -9223372036854775807L;
   protected int _minutesA = -1;
   protected int _minutesB = -1;
   protected int _hours = -1;
   protected int _weekDays = -1;
   protected int _monthDays = -1;
   protected int _months = -1;
   protected long _nextOccurence = -9223372036854775807L;
   protected YWakeUpSchedule.UpdateCallback _valueCallbackWakeUpSchedule = null;

   protected YWakeUpSchedule(YAPIContext ctx, String func) {
      super(ctx, func);
      this._className = "WakeUpSchedule";
   }

   protected YWakeUpSchedule(String func) {
      this(YAPI.GetYCtx(true), func);
   }

   @Override
   protected void _parseAttr(YJSONObject json_val) throws Exception {
      if (json_val.has("minutesA")) {
         this._minutesA = json_val.getInt("minutesA");
      }

      if (json_val.has("minutesB")) {
         this._minutesB = json_val.getInt("minutesB");
      }

      if (json_val.has("hours")) {
         this._hours = json_val.getInt("hours");
      }

      if (json_val.has("weekDays")) {
         this._weekDays = json_val.getInt("weekDays");
      }

      if (json_val.has("monthDays")) {
         this._monthDays = json_val.getInt("monthDays");
      }

      if (json_val.has("months")) {
         this._months = json_val.getInt("months");
      }

      if (json_val.has("nextOccurence")) {
         this._nextOccurence = json_val.getLong("nextOccurence");
      }

      super._parseAttr(json_val);
   }

   public int get_minutesA() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -1 : this._minutesA;
      }
   }

   public int getMinutesA() throws YAPI_Exception {
      return this.get_minutesA();
   }

   public int set_minutesA(int newval) throws YAPI_Exception {
      synchronized (this) {
         String rest_val = Integer.toString(newval);
         this._setAttr("minutesA", rest_val);
         return 0;
      }
   }

   public int setMinutesA(int newval) throws YAPI_Exception {
      return this.set_minutesA(newval);
   }

   public int get_minutesB() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -1 : this._minutesB;
      }
   }

   public int getMinutesB() throws YAPI_Exception {
      return this.get_minutesB();
   }

   public int set_minutesB(int newval) throws YAPI_Exception {
      synchronized (this) {
         String rest_val = Integer.toString(newval);
         this._setAttr("minutesB", rest_val);
         return 0;
      }
   }

   public int setMinutesB(int newval) throws YAPI_Exception {
      return this.set_minutesB(newval);
   }

   public int get_hours() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -1 : this._hours;
      }
   }

   public int getHours() throws YAPI_Exception {
      return this.get_hours();
   }

   public int set_hours(int newval) throws YAPI_Exception {
      synchronized (this) {
         String rest_val = Integer.toString(newval);
         this._setAttr("hours", rest_val);
         return 0;
      }
   }

   public int setHours(int newval) throws YAPI_Exception {
      return this.set_hours(newval);
   }

   public int get_weekDays() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -1 : this._weekDays;
      }
   }

   public int getWeekDays() throws YAPI_Exception {
      return this.get_weekDays();
   }

   public int set_weekDays(int newval) throws YAPI_Exception {
      synchronized (this) {
         String rest_val = Integer.toString(newval);
         this._setAttr("weekDays", rest_val);
         return 0;
      }
   }

   public int setWeekDays(int newval) throws YAPI_Exception {
      return this.set_weekDays(newval);
   }

   public int get_monthDays() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -1 : this._monthDays;
      }
   }

   public int getMonthDays() throws YAPI_Exception {
      return this.get_monthDays();
   }

   public int set_monthDays(int newval) throws YAPI_Exception {
      synchronized (this) {
         String rest_val = Integer.toString(newval);
         this._setAttr("monthDays", rest_val);
         return 0;
      }
   }

   public int setMonthDays(int newval) throws YAPI_Exception {
      return this.set_monthDays(newval);
   }

   public int get_months() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -1 : this._months;
      }
   }

   public int getMonths() throws YAPI_Exception {
      return this.get_months();
   }

   public int set_months(int newval) throws YAPI_Exception {
      synchronized (this) {
         String rest_val = Integer.toString(newval);
         this._setAttr("months", rest_val);
         return 0;
      }
   }

   public int setMonths(int newval) throws YAPI_Exception {
      return this.set_months(newval);
   }

   public long get_nextOccurence() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0
            ? -9223372036854775807L
            : this._nextOccurence;
      }
   }

   public long getNextOccurence() throws YAPI_Exception {
      return this.get_nextOccurence();
   }

   public static YWakeUpSchedule FindWakeUpSchedule(String func) {
      YAPIContext ctx = YAPI.GetYCtx(true);
      synchronized (ctx._functionCacheLock) {
         YWakeUpSchedule obj = (YWakeUpSchedule)YFunction._FindFromCache("WakeUpSchedule", func);
         if (obj == null) {
            obj = new YWakeUpSchedule(func);
            YFunction._AddToCache("WakeUpSchedule", func, obj);
         }

         return obj;
      }
   }

   public static YWakeUpSchedule FindWakeUpScheduleInContext(YAPIContext yctx, String func) {
      synchronized (yctx._functionCacheLock) {
         YWakeUpSchedule obj = (YWakeUpSchedule)YFunction._FindFromCacheInContext(yctx, "WakeUpSchedule", func);
         if (obj == null) {
            obj = new YWakeUpSchedule(yctx, func);
            YFunction._AddToCache("WakeUpSchedule", func, obj);
         }

         return obj;
      }
   }

   public int registerValueCallback(YWakeUpSchedule.UpdateCallback callback) {
      if (callback != null) {
         YFunction._UpdateValueCallbackList(this, true);
      } else {
         YFunction._UpdateValueCallbackList(this, false);
      }

      this._valueCallbackWakeUpSchedule = callback;
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
      if (this._valueCallbackWakeUpSchedule != null) {
         this._valueCallbackWakeUpSchedule.yNewValue(this, value);
      } else {
         super._invokeValueCallback(value);
      }

      return 0;
   }

   public long get_minutes() throws YAPI_Exception {
      long res = (long)this.get_minutesB();
      res <<= 30;
      return res + (long)this.get_minutesA();
   }

   public int set_minutes(long bitmap) throws YAPI_Exception {
      this.set_minutesA((int)(bitmap & 1073741823L));
      bitmap >>= 30;
      return this.set_minutesB((int)(bitmap & 1073741823L));
   }

   public YWakeUpSchedule nextWakeUpSchedule() {
      String next_hwid;
      try {
         String hwid = this._yapi._yHash.resolveHwID(this._className, this._func);
         next_hwid = this._yapi._yHash.getNextHardwareId(this._className, hwid);
      } catch (YAPI_Exception var3) {
         next_hwid = null;
      }

      return next_hwid == null ? null : FindWakeUpScheduleInContext(this._yapi, next_hwid);
   }

   public static YWakeUpSchedule FirstWakeUpSchedule() {
      YAPIContext yctx = YAPI.GetYCtx(false);
      if (yctx == null) {
         return null;
      } else {
         String next_hwid = yctx._yHash.getFirstHardwareId("WakeUpSchedule");
         return next_hwid == null ? null : FindWakeUpScheduleInContext(yctx, next_hwid);
      }
   }

   public static YWakeUpSchedule FirstWakeUpScheduleInContext(YAPIContext yctx) {
      String next_hwid = yctx._yHash.getFirstHardwareId("WakeUpSchedule");
      return next_hwid == null ? null : FindWakeUpScheduleInContext(yctx, next_hwid);
   }

   public interface TimedReportCallback {
      void timedReportCallback(YWakeUpSchedule var1, YMeasure var2);
   }

   public interface UpdateCallback {
      void yNewValue(YWakeUpSchedule var1, String var2);
   }
}
