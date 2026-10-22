package com.yoctopuce.YoctoAPI;

import java.util.Locale;

public class YDigitalIO extends YFunction {
   public static final int PORTSTATE_INVALID = -1;
   public static final int PORTDIRECTION_INVALID = -1;
   public static final int PORTOPENDRAIN_INVALID = -1;
   public static final int PORTPOLARITY_INVALID = -1;
   public static final int PORTDIAGS_INVALID = -1;
   public static final int PORTSIZE_INVALID = -1;
   public static final int OUTPUTVOLTAGE_USB_5V = 0;
   public static final int OUTPUTVOLTAGE_USB_3V = 1;
   public static final int OUTPUTVOLTAGE_EXT_V = 2;
   public static final int OUTPUTVOLTAGE_INVALID = -1;
   public static final String COMMAND_INVALID = "!INVALID!";
   protected int _portState = -1;
   protected int _portDirection = -1;
   protected int _portOpenDrain = -1;
   protected int _portPolarity = -1;
   protected int _portDiags = -1;
   protected int _portSize = -1;
   protected int _outputVoltage = -1;
   protected String _command = "!INVALID!";
   protected YDigitalIO.UpdateCallback _valueCallbackDigitalIO = null;

   protected YDigitalIO(YAPIContext ctx, String func) {
      super(ctx, func);
      this._className = "DigitalIO";
   }

   protected YDigitalIO(String func) {
      this(YAPI.GetYCtx(true), func);
   }

   @Override
   protected void _parseAttr(YJSONObject json_val) throws Exception {
      if (json_val.has("portState")) {
         this._portState = json_val.getInt("portState");
      }

      if (json_val.has("portDirection")) {
         this._portDirection = json_val.getInt("portDirection");
      }

      if (json_val.has("portOpenDrain")) {
         this._portOpenDrain = json_val.getInt("portOpenDrain");
      }

      if (json_val.has("portPolarity")) {
         this._portPolarity = json_val.getInt("portPolarity");
      }

      if (json_val.has("portDiags")) {
         this._portDiags = json_val.getInt("portDiags");
      }

      if (json_val.has("portSize")) {
         this._portSize = json_val.getInt("portSize");
      }

      if (json_val.has("outputVoltage")) {
         this._outputVoltage = json_val.getInt("outputVoltage");
      }

      if (json_val.has("command")) {
         this._command = json_val.getString("command");
      }

      super._parseAttr(json_val);
   }

   public int get_portState() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -1 : this._portState;
      }
   }

   public int getPortState() throws YAPI_Exception {
      return this.get_portState();
   }

   public int set_portState(int newval) throws YAPI_Exception {
      synchronized (this) {
         String rest_val = Integer.toString(newval);
         this._setAttr("portState", rest_val);
         return 0;
      }
   }

   public int setPortState(int newval) throws YAPI_Exception {
      return this.set_portState(newval);
   }

   public int get_portDirection() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -1 : this._portDirection;
      }
   }

   public int getPortDirection() throws YAPI_Exception {
      return this.get_portDirection();
   }

   public int set_portDirection(int newval) throws YAPI_Exception {
      synchronized (this) {
         String rest_val = Integer.toString(newval);
         this._setAttr("portDirection", rest_val);
         return 0;
      }
   }

   public int setPortDirection(int newval) throws YAPI_Exception {
      return this.set_portDirection(newval);
   }

   public int get_portOpenDrain() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -1 : this._portOpenDrain;
      }
   }

   public int getPortOpenDrain() throws YAPI_Exception {
      return this.get_portOpenDrain();
   }

   public int set_portOpenDrain(int newval) throws YAPI_Exception {
      synchronized (this) {
         String rest_val = Integer.toString(newval);
         this._setAttr("portOpenDrain", rest_val);
         return 0;
      }
   }

   public int setPortOpenDrain(int newval) throws YAPI_Exception {
      return this.set_portOpenDrain(newval);
   }

   public int get_portPolarity() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -1 : this._portPolarity;
      }
   }

   public int getPortPolarity() throws YAPI_Exception {
      return this.get_portPolarity();
   }

   public int set_portPolarity(int newval) throws YAPI_Exception {
      synchronized (this) {
         String rest_val = Integer.toString(newval);
         this._setAttr("portPolarity", rest_val);
         return 0;
      }
   }

   public int setPortPolarity(int newval) throws YAPI_Exception {
      return this.set_portPolarity(newval);
   }

   public int get_portDiags() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -1 : this._portDiags;
      }
   }

   public int getPortDiags() throws YAPI_Exception {
      return this.get_portDiags();
   }

   public int get_portSize() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration == 0L && this.load(this._yapi._defaultCacheValidity) != 0 ? -1 : this._portSize;
      }
   }

   public int getPortSize() throws YAPI_Exception {
      return this.get_portSize();
   }

   public int get_outputVoltage() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -1 : this._outputVoltage;
      }
   }

   public int getOutputVoltage() throws YAPI_Exception {
      return this.get_outputVoltage();
   }

   public int set_outputVoltage(int newval) throws YAPI_Exception {
      synchronized (this) {
         String rest_val = Integer.toString(newval);
         this._setAttr("outputVoltage", rest_val);
         return 0;
      }
   }

   public int setOutputVoltage(int newval) throws YAPI_Exception {
      return this.set_outputVoltage(newval);
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

   public static YDigitalIO FindDigitalIO(String func) {
      YAPIContext ctx = YAPI.GetYCtx(true);
      synchronized (ctx._functionCacheLock) {
         YDigitalIO obj = (YDigitalIO)YFunction._FindFromCache("DigitalIO", func);
         if (obj == null) {
            obj = new YDigitalIO(func);
            YFunction._AddToCache("DigitalIO", func, obj);
         }

         return obj;
      }
   }

   public static YDigitalIO FindDigitalIOInContext(YAPIContext yctx, String func) {
      synchronized (yctx._functionCacheLock) {
         YDigitalIO obj = (YDigitalIO)YFunction._FindFromCacheInContext(yctx, "DigitalIO", func);
         if (obj == null) {
            obj = new YDigitalIO(yctx, func);
            YFunction._AddToCache("DigitalIO", func, obj);
         }

         return obj;
      }
   }

   public int registerValueCallback(YDigitalIO.UpdateCallback callback) {
      if (callback != null) {
         YFunction._UpdateValueCallbackList(this, true);
      } else {
         YFunction._UpdateValueCallbackList(this, false);
      }

      this._valueCallbackDigitalIO = callback;
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
      if (this._valueCallbackDigitalIO != null) {
         this._valueCallbackDigitalIO.yNewValue(this, value);
      } else {
         super._invokeValueCallback(value);
      }

      return 0;
   }

   public int set_bitState(int bitno, int bitstate) throws YAPI_Exception {
      if (bitstate < 0) {
         throw new YAPI_Exception(-2, "invalid bit state");
      } else if (bitstate > 1) {
         throw new YAPI_Exception(-2, "invalid bit state");
      } else {
         return this.set_command(String.format(Locale.US, "%c%d", 82 + bitstate, bitno));
      }
   }

   public int get_bitState(int bitno) throws YAPI_Exception {
      int portVal = this.get_portState();
      return portVal >> bitno & 1;
   }

   public int toggle_bitState(int bitno) throws YAPI_Exception {
      return this.set_command(String.format(Locale.US, "T%d", bitno));
   }

   public int set_bitDirection(int bitno, int bitdirection) throws YAPI_Exception {
      if (bitdirection < 0) {
         throw new YAPI_Exception(-2, "invalid direction");
      } else if (bitdirection > 1) {
         throw new YAPI_Exception(-2, "invalid direction");
      } else {
         return this.set_command(String.format(Locale.US, "%c%d", 73 + 6 * bitdirection, bitno));
      }
   }

   public int get_bitDirection(int bitno) throws YAPI_Exception {
      int portDir = this.get_portDirection();
      return portDir >> bitno & 1;
   }

   public int set_bitPolarity(int bitno, int bitpolarity) throws YAPI_Exception {
      if (bitpolarity < 0) {
         throw new YAPI_Exception(-2, "invalid bit polarity");
      } else if (bitpolarity > 1) {
         throw new YAPI_Exception(-2, "invalid bit polarity");
      } else {
         return this.set_command(String.format(Locale.US, "%c%d", 110 + 4 * bitpolarity, bitno));
      }
   }

   public int get_bitPolarity(int bitno) throws YAPI_Exception {
      int portPol = this.get_portPolarity();
      return portPol >> bitno & 1;
   }

   public int set_bitOpenDrain(int bitno, int opendrain) throws YAPI_Exception {
      if (opendrain < 0) {
         throw new YAPI_Exception(-2, "invalid state");
      } else if (opendrain > 1) {
         throw new YAPI_Exception(-2, "invalid state");
      } else {
         return this.set_command(String.format(Locale.US, "%c%d", 100 - 32 * opendrain, bitno));
      }
   }

   public int get_bitOpenDrain(int bitno) throws YAPI_Exception {
      int portOpenDrain = this.get_portOpenDrain();
      return portOpenDrain >> bitno & 1;
   }

   public int pulse(int bitno, int ms_duration) throws YAPI_Exception {
      return this.set_command(String.format(Locale.US, "Z%d,0,%d", bitno, ms_duration));
   }

   public int delayedPulse(int bitno, int ms_delay, int ms_duration) throws YAPI_Exception {
      return this.set_command(String.format(Locale.US, "Z%d,%d,%d", bitno, ms_delay, ms_duration));
   }

   public YDigitalIO nextDigitalIO() {
      String next_hwid;
      try {
         String hwid = this._yapi._yHash.resolveHwID(this._className, this._func);
         next_hwid = this._yapi._yHash.getNextHardwareId(this._className, hwid);
      } catch (YAPI_Exception var3) {
         next_hwid = null;
      }

      return next_hwid == null ? null : FindDigitalIOInContext(this._yapi, next_hwid);
   }

   public static YDigitalIO FirstDigitalIO() {
      YAPIContext yctx = YAPI.GetYCtx(false);
      if (yctx == null) {
         return null;
      } else {
         String next_hwid = yctx._yHash.getFirstHardwareId("DigitalIO");
         return next_hwid == null ? null : FindDigitalIOInContext(yctx, next_hwid);
      }
   }

   public static YDigitalIO FirstDigitalIOInContext(YAPIContext yctx) {
      String next_hwid = yctx._yHash.getFirstHardwareId("DigitalIO");
      return next_hwid == null ? null : FindDigitalIOInContext(yctx, next_hwid);
   }

   public interface TimedReportCallback {
      void timedReportCallback(YDigitalIO var1, YMeasure var2);
   }

   public interface UpdateCallback {
      void yNewValue(YDigitalIO var1, String var2);
   }
}
