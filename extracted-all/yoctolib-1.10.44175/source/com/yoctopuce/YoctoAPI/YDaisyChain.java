package com.yoctopuce.YoctoAPI;

public class YDaisyChain extends YFunction {
   public static final int DAISYSTATE_READY = 0;
   public static final int DAISYSTATE_IS_CHILD = 1;
   public static final int DAISYSTATE_FIRMWARE_MISMATCH = 2;
   public static final int DAISYSTATE_CHILD_MISSING = 3;
   public static final int DAISYSTATE_CHILD_LOST = 4;
   public static final int DAISYSTATE_INVALID = -1;
   public static final int CHILDCOUNT_INVALID = -1;
   public static final int REQUIREDCHILDCOUNT_INVALID = -1;
   protected int _daisyState = -1;
   protected int _childCount = -1;
   protected int _requiredChildCount = -1;
   protected YDaisyChain.UpdateCallback _valueCallbackDaisyChain = null;

   protected YDaisyChain(YAPIContext ctx, String func) {
      super(ctx, func);
      this._className = "DaisyChain";
   }

   protected YDaisyChain(String func) {
      this(YAPI.GetYCtx(true), func);
   }

   @Override
   protected void _parseAttr(YJSONObject json_val) throws Exception {
      if (json_val.has("daisyState")) {
         this._daisyState = json_val.getInt("daisyState");
      }

      if (json_val.has("childCount")) {
         this._childCount = json_val.getInt("childCount");
      }

      if (json_val.has("requiredChildCount")) {
         this._requiredChildCount = json_val.getInt("requiredChildCount");
      }

      super._parseAttr(json_val);
   }

   public int get_daisyState() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -1 : this._daisyState;
      }
   }

   public int getDaisyState() throws YAPI_Exception {
      return this.get_daisyState();
   }

   public int get_childCount() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -1 : this._childCount;
      }
   }

   public int getChildCount() throws YAPI_Exception {
      return this.get_childCount();
   }

   public int get_requiredChildCount() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -1 : this._requiredChildCount;
      }
   }

   public int getRequiredChildCount() throws YAPI_Exception {
      return this.get_requiredChildCount();
   }

   public int set_requiredChildCount(int newval) throws YAPI_Exception {
      synchronized (this) {
         String rest_val = Integer.toString(newval);
         this._setAttr("requiredChildCount", rest_val);
         return 0;
      }
   }

   public int setRequiredChildCount(int newval) throws YAPI_Exception {
      return this.set_requiredChildCount(newval);
   }

   public static YDaisyChain FindDaisyChain(String func) {
      YAPIContext ctx = YAPI.GetYCtx(true);
      synchronized (ctx._functionCacheLock) {
         YDaisyChain obj = (YDaisyChain)YFunction._FindFromCache("DaisyChain", func);
         if (obj == null) {
            obj = new YDaisyChain(func);
            YFunction._AddToCache("DaisyChain", func, obj);
         }

         return obj;
      }
   }

   public static YDaisyChain FindDaisyChainInContext(YAPIContext yctx, String func) {
      synchronized (yctx._functionCacheLock) {
         YDaisyChain obj = (YDaisyChain)YFunction._FindFromCacheInContext(yctx, "DaisyChain", func);
         if (obj == null) {
            obj = new YDaisyChain(yctx, func);
            YFunction._AddToCache("DaisyChain", func, obj);
         }

         return obj;
      }
   }

   public int registerValueCallback(YDaisyChain.UpdateCallback callback) {
      if (callback != null) {
         YFunction._UpdateValueCallbackList(this, true);
      } else {
         YFunction._UpdateValueCallbackList(this, false);
      }

      this._valueCallbackDaisyChain = callback;
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
      if (this._valueCallbackDaisyChain != null) {
         this._valueCallbackDaisyChain.yNewValue(this, value);
      } else {
         super._invokeValueCallback(value);
      }

      return 0;
   }

   public YDaisyChain nextDaisyChain() {
      String next_hwid;
      try {
         String hwid = this._yapi._yHash.resolveHwID(this._className, this._func);
         next_hwid = this._yapi._yHash.getNextHardwareId(this._className, hwid);
      } catch (YAPI_Exception var3) {
         next_hwid = null;
      }

      return next_hwid == null ? null : FindDaisyChainInContext(this._yapi, next_hwid);
   }

   public static YDaisyChain FirstDaisyChain() {
      YAPIContext yctx = YAPI.GetYCtx(false);
      if (yctx == null) {
         return null;
      } else {
         String next_hwid = yctx._yHash.getFirstHardwareId("DaisyChain");
         return next_hwid == null ? null : FindDaisyChainInContext(yctx, next_hwid);
      }
   }

   public static YDaisyChain FirstDaisyChainInContext(YAPIContext yctx) {
      String next_hwid = yctx._yHash.getFirstHardwareId("DaisyChain");
      return next_hwid == null ? null : FindDaisyChainInContext(yctx, next_hwid);
   }

   public interface TimedReportCallback {
      void timedReportCallback(YDaisyChain var1, YMeasure var2);
   }

   public interface UpdateCallback {
      void yNewValue(YDaisyChain var1, String var2);
   }
}
