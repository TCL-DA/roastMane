package com.yoctopuce.YoctoAPI;

import java.util.Locale;

public class YGyro extends YSensor {
   private static YQt.UpdateCallback yInternalGyroCallback = new YQt.UpdateCallback() {
      @Override
      public void yNewValue(YQt obj, String value) {
         YGyro gyro = (YGyro)obj.get_userData();
         if (gyro != null) {
            try {
               int idx = Integer.parseInt(obj.get_functionId().substring(2));
               gyro._invokeGyroCallbacks(idx, Double.parseDouble(value));
            } catch (YAPI_Exception var5) {
            }
         }
      }
   };
   public static final int BANDWIDTH_INVALID = -1;
   public static final double XVALUE_INVALID = -1.79769313486231E308;
   public static final double YVALUE_INVALID = -1.79769313486231E308;
   public static final double ZVALUE_INVALID = -1.79769313486231E308;
   protected int _bandwidth = -1;
   protected double _xValue = -1.79769313486231E308;
   protected double _yValue = -1.79769313486231E308;
   protected double _zValue = -1.79769313486231E308;
   protected YGyro.UpdateCallback _valueCallbackGyro = null;
   protected YGyro.TimedReportCallback _timedReportCallbackGyro = null;
   protected int _qt_stamp = 0;
   protected YQt _qt_w;
   protected YQt _qt_x;
   protected YQt _qt_y;
   protected YQt _qt_z;
   protected double _w = 0.0;
   protected double _x = 0.0;
   protected double _y = 0.0;
   protected double _z = 0.0;
   protected int _angles_stamp = 0;
   protected double _head = 0.0;
   protected double _pitch = 0.0;
   protected double _roll = 0.0;
   protected YGyro.YQuatCallback _quatCallback;
   protected YGyro.YAnglesCallback _anglesCallback;

   protected YGyro(YAPIContext yctx, String func) {
      super(yctx, func);
      this._className = "Gyro";
   }

   protected YGyro(String func) {
      this(YAPI.GetYCtx(true), func);
   }

   @Override
   protected void _parseAttr(YJSONObject json_val) throws Exception {
      if (json_val.has("bandwidth")) {
         this._bandwidth = json_val.getInt("bandwidth");
      }

      if (json_val.has("xValue")) {
         this._xValue = (double)Math.round(json_val.getDouble("xValue") * 1000.0 / 65536.0) / 1000.0;
      }

      if (json_val.has("yValue")) {
         this._yValue = (double)Math.round(json_val.getDouble("yValue") * 1000.0 / 65536.0) / 1000.0;
      }

      if (json_val.has("zValue")) {
         this._zValue = (double)Math.round(json_val.getDouble("zValue") * 1000.0 / 65536.0) / 1000.0;
      }

      super._parseAttr(json_val);
   }

   public int get_bandwidth() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -1 : this._bandwidth;
      }
   }

   public int getBandwidth() throws YAPI_Exception {
      return this.get_bandwidth();
   }

   public int set_bandwidth(int newval) throws YAPI_Exception {
      synchronized (this) {
         String rest_val = Integer.toString(newval);
         this._setAttr("bandwidth", rest_val);
         return 0;
      }
   }

   public int setBandwidth(int newval) throws YAPI_Exception {
      return this.set_bandwidth(newval);
   }

   public double get_xValue() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -1.79769313486231E308 : this._xValue;
      }
   }

   public double getXValue() throws YAPI_Exception {
      return this.get_xValue();
   }

   public double get_yValue() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -1.79769313486231E308 : this._yValue;
      }
   }

   public double getYValue() throws YAPI_Exception {
      return this.get_yValue();
   }

   public double get_zValue() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -1.79769313486231E308 : this._zValue;
      }
   }

   public double getZValue() throws YAPI_Exception {
      return this.get_zValue();
   }

   public static YGyro FindGyro(String func) {
      YAPIContext ctx = YAPI.GetYCtx(true);
      synchronized (ctx._functionCacheLock) {
         YGyro obj = (YGyro)YFunction._FindFromCache("Gyro", func);
         if (obj == null) {
            obj = new YGyro(func);
            YFunction._AddToCache("Gyro", func, obj);
         }

         return obj;
      }
   }

   public static YGyro FindGyroInContext(YAPIContext yctx, String func) {
      synchronized (yctx._functionCacheLock) {
         YGyro obj = (YGyro)YFunction._FindFromCacheInContext(yctx, "Gyro", func);
         if (obj == null) {
            obj = new YGyro(yctx, func);
            YFunction._AddToCache("Gyro", func, obj);
         }

         return obj;
      }
   }

   public int registerValueCallback(YGyro.UpdateCallback callback) {
      if (callback != null) {
         YFunction._UpdateValueCallbackList(this, true);
      } else {
         YFunction._UpdateValueCallbackList(this, false);
      }

      this._valueCallbackGyro = callback;
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
      if (this._valueCallbackGyro != null) {
         this._valueCallbackGyro.yNewValue(this, value);
      } else {
         super._invokeValueCallback(value);
      }

      return 0;
   }

   public int registerTimedReportCallback(YGyro.TimedReportCallback callback) {
      if (callback != null) {
         YFunction._UpdateTimedReportCallbackList(this, true);
      } else {
         YFunction._UpdateTimedReportCallbackList(this, false);
      }

      this._timedReportCallbackGyro = callback;
      return 0;
   }

   @Override
   public int _invokeTimedReportCallback(YMeasure value) {
      if (this._timedReportCallbackGyro != null) {
         this._timedReportCallbackGyro.timedReportCallback(this, value);
      } else {
         super._invokeTimedReportCallback(value);
      }

      return 0;
   }

   public int _loadQuaternion() throws YAPI_Exception {
      int now_stamp = (int)(YAPIContext.GetTickCount() & 2147483647L);
      int age_ms = now_stamp - this._qt_stamp & 2147483647;
      if (age_ms >= 10 || this._qt_stamp == 0) {
         if (this.load(10L) != 0) {
            return -4;
         }

         if (this._qt_stamp == 0) {
            this._qt_w = YQt.FindQtInContext(this._yapi, String.format(Locale.US, "%s.qt1", this._serial));
            this._qt_x = YQt.FindQtInContext(this._yapi, String.format(Locale.US, "%s.qt2", this._serial));
            this._qt_y = YQt.FindQtInContext(this._yapi, String.format(Locale.US, "%s.qt3", this._serial));
            this._qt_z = YQt.FindQtInContext(this._yapi, String.format(Locale.US, "%s.qt4", this._serial));
         }

         if (this._qt_w.load(9L) != 0) {
            return -4;
         }

         if (this._qt_x.load(9L) != 0) {
            return -4;
         }

         if (this._qt_y.load(9L) != 0) {
            return -4;
         }

         if (this._qt_z.load(9L) != 0) {
            return -4;
         }

         this._w = this._qt_w.get_currentValue();
         this._x = this._qt_x.get_currentValue();
         this._y = this._qt_y.get_currentValue();
         this._z = this._qt_z.get_currentValue();
         this._qt_stamp = now_stamp;
      }

      return 0;
   }

   public int _loadAngles() throws YAPI_Exception {
      if (this._loadQuaternion() != 0) {
         return -4;
      } else {
         if (this._angles_stamp != this._qt_stamp) {
            double sqw = this._w * this._w;
            double sqx = this._x * this._x;
            double sqy = this._y * this._y;
            double sqz = this._z * this._z;
            double norm = sqx + sqy + sqz + sqw;
            double delta = this._y * this._w - this._x * this._z;
            if (delta > 0.499 * norm) {
               this._pitch = 90.0;
               this._head = (double)Math.round(1145.9155902616465 * Math.atan2(this._x, -this._w)) / 10.0;
            } else if (delta < -0.499 * norm) {
               this._pitch = -90.0;
               this._head = (double)Math.round(-1145.9155902616465 * Math.atan2(this._x, -this._w)) / 10.0;
            } else {
               this._roll = (double)Math.round(572.9577951308232 * Math.atan2(2.0 * (this._w * this._x + this._y * this._z), sqw - sqx - sqy + sqz)) / 10.0;
               this._pitch = (double)Math.round(572.9577951308232 * Math.asin(2.0 * delta / norm)) / 10.0;
               this._head = (double)Math.round(572.9577951308232 * Math.atan2(2.0 * (this._x * this._y + this._z * this._w), sqw + sqx - sqy - sqz)) / 10.0;
            }

            this._angles_stamp = this._qt_stamp;
         }

         return 0;
      }
   }

   public double get_roll() throws YAPI_Exception {
      this._loadAngles();
      return this._roll;
   }

   public double get_pitch() throws YAPI_Exception {
      this._loadAngles();
      return this._pitch;
   }

   public double get_heading() throws YAPI_Exception {
      this._loadAngles();
      return this._head;
   }

   public double get_quaternionW() throws YAPI_Exception {
      this._loadQuaternion();
      return this._w;
   }

   public double get_quaternionX() throws YAPI_Exception {
      this._loadQuaternion();
      return this._x;
   }

   public double get_quaternionY() throws YAPI_Exception {
      this._loadQuaternion();
      return this._y;
   }

   public double get_quaternionZ() throws YAPI_Exception {
      this._loadQuaternion();
      return this._z;
   }

   public int registerQuaternionCallback(YGyro.YQuatCallback callback) throws YAPI_Exception {
      this._quatCallback = callback;
      if (callback != null) {
         if (this._loadQuaternion() != 0) {
            return -4;
         }

         this._qt_w.set_userData(this);
         this._qt_x.set_userData(this);
         this._qt_y.set_userData(this);
         this._qt_z.set_userData(this);
         this._qt_w.registerValueCallback(yInternalGyroCallback);
         this._qt_x.registerValueCallback(yInternalGyroCallback);
         this._qt_y.registerValueCallback(yInternalGyroCallback);
         this._qt_z.registerValueCallback(yInternalGyroCallback);
      } else if (this._anglesCallback == null) {
         this._qt_w.registerValueCallback((YQt.UpdateCallback)null);
         this._qt_x.registerValueCallback((YQt.UpdateCallback)null);
         this._qt_y.registerValueCallback((YQt.UpdateCallback)null);
         this._qt_z.registerValueCallback((YQt.UpdateCallback)null);
      }

      return 0;
   }

   public int registerAnglesCallback(YGyro.YAnglesCallback callback) throws YAPI_Exception {
      this._anglesCallback = callback;
      if (callback != null) {
         if (this._loadQuaternion() != 0) {
            return -4;
         }

         this._qt_w.set_userData(this);
         this._qt_x.set_userData(this);
         this._qt_y.set_userData(this);
         this._qt_z.set_userData(this);
         this._qt_w.registerValueCallback(yInternalGyroCallback);
         this._qt_x.registerValueCallback(yInternalGyroCallback);
         this._qt_y.registerValueCallback(yInternalGyroCallback);
         this._qt_z.registerValueCallback(yInternalGyroCallback);
      } else if (this._quatCallback == null) {
         this._qt_w.registerValueCallback((YQt.UpdateCallback)null);
         this._qt_x.registerValueCallback((YQt.UpdateCallback)null);
         this._qt_y.registerValueCallback((YQt.UpdateCallback)null);
         this._qt_z.registerValueCallback((YQt.UpdateCallback)null);
      }

      return 0;
   }

   public int _invokeGyroCallbacks(int qtIndex, double qtValue) throws YAPI_Exception {
      switch (qtIndex - 1) {
         case 0:
            this._w = qtValue;
            break;
         case 1:
            this._x = qtValue;
            break;
         case 2:
            this._y = qtValue;
            break;
         case 3:
            this._z = qtValue;
      }

      if (qtIndex < 4) {
         return 0;
      } else {
         this._qt_stamp = (int)(YAPIContext.GetTickCount() & 2147483647L);
         if (this._quatCallback != null) {
            this._quatCallback.yQuaternionCallback(this, this._w, this._x, this._y, this._z);
         }

         if (this._anglesCallback != null) {
            this._loadAngles();
            this._anglesCallback.yAnglesCallback(this, this._roll, this._pitch, this._head);
         }

         return 0;
      }
   }

   public YGyro nextGyro() {
      String next_hwid;
      try {
         String hwid = this._yapi._yHash.resolveHwID(this._className, this._func);
         next_hwid = this._yapi._yHash.getNextHardwareId(this._className, hwid);
      } catch (YAPI_Exception var3) {
         next_hwid = null;
      }

      return next_hwid == null ? null : FindGyroInContext(this._yapi, next_hwid);
   }

   public static YGyro FirstGyro() {
      YAPIContext yctx = YAPI.GetYCtx(false);
      if (yctx == null) {
         return null;
      } else {
         String next_hwid = yctx._yHash.getFirstHardwareId("Gyro");
         return next_hwid == null ? null : FindGyroInContext(yctx, next_hwid);
      }
   }

   public static YGyro FirstGyroInContext(YAPIContext yctx) {
      String next_hwid = yctx._yHash.getFirstHardwareId("Gyro");
      return next_hwid == null ? null : FindGyroInContext(yctx, next_hwid);
   }

   public interface TimedReportCallback {
      void timedReportCallback(YGyro var1, YMeasure var2);
   }

   public interface UpdateCallback {
      void yNewValue(YGyro var1, String var2);
   }

   public interface YAnglesCallback {
      void yAnglesCallback(YGyro var1, double var2, double var4, double var6);
   }

   public interface YQuatCallback {
      void yQuaternionCallback(YGyro var1, double var2, double var4, double var6, double var8);
   }
}
