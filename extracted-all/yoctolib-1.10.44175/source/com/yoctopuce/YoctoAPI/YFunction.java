package com.yoctopuce.YoctoAPI;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class YFunction {
   public static final String FUNCTIONDESCRIPTOR_INVALID = "!INVALID!";
   protected final YAPIContext _yapi;
   protected String _className;
   protected final String _func;
   protected int _lastErrorType;
   protected String _lastErrorMsg;
   protected Object _userData;
   protected HashMap<String, YDataStream> _dataStreams;
   public static final String LOGICALNAME_INVALID = "!INVALID!";
   public static final String ADVERTISEDVALUE_INVALID = "!INVALID!";
   protected String _logicalName = "!INVALID!";
   protected String _advertisedValue = "!INVALID!";
   protected YFunction.UpdateCallback _valueCallbackFunction = null;
   protected long _cacheExpiration = 0L;
   protected String _serial;
   protected String _funId;
   protected String _hwId;

   protected YFunction(YAPIContext yctx, String func) {
      this._yapi = yctx;
      this._className = "Function";
      this._func = func;
      this._lastErrorType = 0;
      this._lastErrorMsg = "";
      this._userData = null;
      this._dataStreams = new HashMap<>();
   }

   public YFunction(String func) {
      this._yapi = YAPI.GetYCtx(false);
      this._className = "Function";
      this._func = func;
      this._lastErrorType = 0;
      this._lastErrorMsg = "";
      this._userData = null;
      this._dataStreams = new HashMap<>();
   }

   protected void _throw(int error, String message) throws YAPI_Exception {
      throw new YAPI_Exception(error, message);
   }

   protected static YFunction _FindFromCacheInContext(YAPIContext yctx, String className, String func) {
      return yctx._yHash.getFunction(className, func);
   }

   protected static YFunction _FindFromCache(String className, String func) {
      YAPIContext ctx = YAPI.GetYCtx(true);
      return ctx._yHash.getFunction(className, func);
   }

   protected static void _AddToCache(String className, String func, YFunction obj) {
      obj._yapi._yHash.setFunction(className, func, obj);
   }

   protected static void _UpdateValueCallbackList(YFunction func, boolean add) {
      func._yapi._UpdateValueCallbackList(func, add);
   }

   protected static void _UpdateTimedReportCallbackList(YFunction func, boolean add) {
      func._yapi._UpdateTimedReportCallbackList(func, add);
   }

   private boolean isReadOnly_internal() {
      try {
         YDevice dev = this.getYDevice();
         YGenericHub hub = dev.getHub();
         return hub.isReadOnly();
      } catch (YAPI_Exception var3) {
         var3.printStackTrace();
         return false;
      }
   }

   protected void _parseAttr(YJSONObject json_val) throws Exception {
      if (json_val.has("logicalName")) {
         this._logicalName = json_val.getString("logicalName");
      }

      if (json_val.has("advertisedValue")) {
         this._advertisedValue = json_val.getString("advertisedValue");
      }
   }

   public String get_logicalName() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? "!INVALID!" : this._logicalName;
      }
   }

   public String getLogicalName() throws YAPI_Exception {
      return this.get_logicalName();
   }

   public int set_logicalName(String newval) throws YAPI_Exception {
      if (!YAPI.CheckLogicalName(newval)) {
         this._throw(-2, "Invalid name :" + newval);
      }

      synchronized (this) {
         this._setAttr("logicalName", newval);
         return 0;
      }
   }

   public int setLogicalName(String newval) throws YAPI_Exception {
      return this.set_logicalName(newval);
   }

   public String get_advertisedValue() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? "!INVALID!" : this._advertisedValue;
      }
   }

   public String getAdvertisedValue() throws YAPI_Exception {
      return this.get_advertisedValue();
   }

   public int set_advertisedValue(String newval) throws YAPI_Exception {
      synchronized (this) {
         this._setAttr("advertisedValue", newval);
         return 0;
      }
   }

   public static YFunction FindFunction(String func) {
      YAPIContext ctx = YAPI.GetYCtx(true);
      synchronized (ctx._functionCacheLock) {
         YFunction obj = _FindFromCache("Function", func);
         if (obj == null) {
            obj = new YFunction(func);
            _AddToCache("Function", func, obj);
         }

         return obj;
      }
   }

   public static YFunction FindFunctionInContext(YAPIContext yctx, String func) {
      synchronized (yctx._functionCacheLock) {
         YFunction obj = _FindFromCacheInContext(yctx, "Function", func);
         if (obj == null) {
            obj = new YFunction(yctx, func);
            _AddToCache("Function", func, obj);
         }

         return obj;
      }
   }

   public int registerValueCallback(YFunction.UpdateCallback callback) {
      if (callback != null) {
         _UpdateValueCallbackList(this, true);
      } else {
         _UpdateValueCallbackList(this, false);
      }

      this._valueCallbackFunction = callback;
      if (callback != null && this.isOnline()) {
         String val = this._advertisedValue;
         if (!val.equals("")) {
            this._invokeValueCallback(val);
         }
      }

      return 0;
   }

   public int _invokeValueCallback(String value) {
      if (this._valueCallbackFunction != null) {
         this._valueCallbackFunction.yNewValue(this, value);
      }

      return 0;
   }

   public int muteValueCallbacks() throws YAPI_Exception {
      return this.set_advertisedValue("SILENT");
   }

   public int unmuteValueCallbacks() throws YAPI_Exception {
      return this.set_advertisedValue("");
   }

   public String loadAttribute(String attrName) throws YAPI_Exception {
      String url = String.format(Locale.US, "api/%s/%s", this.get_functionId(), attrName);
      byte[] attrVal = this._download(url);
      return new String(attrVal);
   }

   public boolean isReadOnly() {
      return this.isReadOnly_internal();
   }

   public String get_serialNumber() throws YAPI_Exception {
      YModule m = this.get_module();
      return m.get_serialNumber();
   }

   public int _parserHelper() {
      return 0;
   }

   public YFunction nextFunction() {
      String next_hwid;
      try {
         String hwid = this._yapi._yHash.resolveHwID(this._className, this._func);
         next_hwid = this._yapi._yHash.getNextHardwareId(this._className, hwid);
      } catch (YAPI_Exception var3) {
         next_hwid = null;
      }

      return next_hwid == null ? null : FindFunctionInContext(this._yapi, next_hwid);
   }

   public static YFunction FirstFunction() {
      YAPIContext yctx = YAPI.GetYCtx(false);
      if (yctx == null) {
         return null;
      } else {
         String next_hwid = yctx._yHash.getFirstHardwareId("Function");
         return next_hwid == null ? null : FindFunctionInContext(yctx, next_hwid);
      }
   }

   public static YFunction FirstFunctionInContext(YAPIContext yctx) {
      String next_hwid = yctx._yHash.getFirstHardwareId("Function");
      return next_hwid == null ? null : FindFunctionInContext(yctx, next_hwid);
   }

   public synchronized String describe() {
      try {
         String hwid = this._yapi._yHash.resolveHwID(this._className, this._func);
         return this._className + "(" + this._func + ")=" + hwid;
      } catch (YAPI_Exception var2) {
         return this._className + "(" + this._func + ")=unresolved";
      }
   }

   public String get_hardwareId() throws YAPI_Exception {
      return this._yapi._yHash.resolveHwID(this._className, this._func);
   }

   public String getHardwareId() throws YAPI_Exception {
      return this._yapi._yHash.resolveHwID(this._className, this._func);
   }

   public synchronized String get_functionId() throws YAPI_Exception {
      return this._yapi._yHash.resolveFuncId(this._className, this._func);
   }

   public synchronized String getFunctionId() throws YAPI_Exception {
      return this._yapi._yHash.resolveFuncId(this._className, this._func);
   }

   public synchronized String get_friendlyName() throws YAPI_Exception {
      YPEntry yp = this._yapi._yHash.resolveFunction(this._className, this._func);
      return yp.getFriendlyName(this._yapi);
   }

   public String getFriendlyName() throws YAPI_Exception {
      return this.get_friendlyName();
   }

   @Override
   public String toString() {
      return this.describe();
   }

   protected void _parse(YJSONObject json, long msValidity) throws YAPI_Exception {
      try {
         this._parseAttr(json);
      } catch (Exception var5) {
         this._throw(-8, var5.getMessage());
      }

      this._parserHelper();
   }

   protected String _escapeAttr(String newval) throws YAPI_Exception {
      try {
         String escaped = URLEncoder.encode(newval, this._yapi._defaultEncoding);
         return escaped.replace("%21", "!")
            .replace("%23", "#")
            .replace("%24", "$")
            .replace("%27", "'")
            .replace("%28", "(")
            .replace("%29", ")")
            .replace("%2C", ",")
            .replace("%2F", "/")
            .replace("%3A", ":")
            .replace("%3B", ";")
            .replace("%3F", "?")
            .replace("%40", "@")
            .replace("%5B", "[")
            .replace("%5D", "]");
      } catch (UnsupportedEncodingException var3) {
         throw new YAPI_Exception(-2, "Unsupported Encoding");
      }
   }

   protected int _setAttr(String attr, String newval) throws YAPI_Exception {
      if (newval == null) {
         throw new YAPI_Exception(-2, "Undefined value to set for attribute " + attr);
      } else {
         String attrname;
         try {
            attrname = URLEncoder.encode(attr, this._yapi._defaultEncoding);
         } catch (UnsupportedEncodingException var5) {
            throw new YAPI_Exception(-2, "Unsupported Encoding");
         }

         String extra = "/" + attrname + "?" + attrname + "=" + this._escapeAttr(newval) + "&.";
         this._devRequest(extra);
         if (this._cacheExpiration != 0L) {
            this._cacheExpiration = YAPI.GetTickCount();
         }

         return 0;
      }
   }

   private byte[] _request(String req_first_line, byte[] req_head_and_body) throws YAPI_Exception {
      YDevice dev = this.getYDevice();
      return dev.requestHTTPSync(req_first_line, req_head_and_body);
   }

   protected byte[] _uploadEx(String path, byte[] content) throws YAPI_Exception {
      YDevice dev = this.getYDevice();
      return dev.requestHTTPUploadEx(path, content);
   }

   protected int _upload(String path, byte[] content) throws YAPI_Exception {
      YDevice dev = this.getYDevice();
      return dev.requestHTTPUpload(path, content);
   }

   protected int _upload(String pathname, String content) throws YAPI_Exception {
      return this._upload(pathname, content.getBytes(this._yapi._deviceCharset));
   }

   protected byte[] _download(String url) throws YAPI_Exception {
      String request = "GET /" + url + " HTTP/1.1\r\n\r\n";
      return this._request(request, null);
   }

   protected String _json_get_key(byte[] json, String key) throws YAPI_Exception {
      YJSONObject obj = null;

      try {
         obj = new YJSONObject(new String(json, this._yapi._deviceCharset));
         obj.parse();
      } catch (Exception var5) {
         throw new YAPI_Exception(-8, var5.getLocalizedMessage());
      }

      if (obj.has(key)) {
         String val = obj.getString(key);
         if (val == null) {
            val = obj.toString();
         }

         return val;
      } else {
         throw new YAPI_Exception(-2, "No key " + key + "in JSON struct");
      }
   }

   protected String _json_get_string(byte[] json) throws YAPI_Exception {
      YJSONArray array = null;

      try {
         String s = new String(json, this._yapi._deviceCharset);
         YJSONString yjsonString = new YJSONString(s, 0, s.length());
         yjsonString.parse();
         return yjsonString.getString();
      } catch (Exception var5) {
         throw new YAPI_Exception(-8, var5.getLocalizedMessage());
      }
   }

   protected ArrayList<String> _json_get_array(byte[] json) throws YAPI_Exception {
      YJSONArray array = null;

      try {
         array = new YJSONArray(new String(json, this._yapi._deviceCharset));
         array.parse();
      } catch (Exception var8) {
         throw new YAPI_Exception(-8, var8.getLocalizedMessage());
      }

      ArrayList<String> list = new ArrayList<>();
      int len = array.length();

      for (int i = 0; i < len; i++) {
         try {
            YJSONContent o = array.get(i);
            list.add(o.toJSON());
         } catch (Exception var7) {
            throw new YAPI_Exception(-8, var7.getLocalizedMessage());
         }
      }

      return list;
   }

   protected String _get_json_path_struct(YJSONObject jsonObject, String[] paths, int ofs) {
      String key = paths[ofs];
      if (!jsonObject.has(key)) {
         return "";
      } else {
         YJSONContent obj = jsonObject.get(key);
         if (obj != null) {
            if (paths.length == ofs + 1) {
               return obj.toJSON();
            }

            if (obj.getJSONType() == YJSONContent.YJSONType.ARRAY) {
               return this._get_json_path_array(jsonObject.getYJSONArray(key), paths, ofs + 1);
            }

            if (obj.getJSONType() == YJSONContent.YJSONType.OBJECT) {
               return this._get_json_path_struct(jsonObject.getYJSONObject(key), paths, ofs + 1);
            }
         }

         return "";
      }
   }

   private String _get_json_path_array(YJSONArray jsonArray, String[] paths, int ofs) {
      int key = Integer.valueOf(paths[ofs]);
      if (jsonArray.length() <= key) {
         return "";
      } else {
         YJSONContent obj = jsonArray.get(key);
         if (obj != null) {
            if (paths.length == ofs + 1) {
               return obj.toJSON();
            }

            if (obj.getJSONType() == YJSONContent.YJSONType.ARRAY) {
               return this._get_json_path_array(jsonArray.getYJSONArray(key), paths, ofs + 1);
            }

            if (obj.getJSONType() == YJSONContent.YJSONType.OBJECT) {
               return this._get_json_path_struct(jsonArray.getYJSONObject(key), paths, ofs + 1);
            }
         }

         return "";
      }
   }

   protected String _get_json_path(String json, String path) {
      YJSONObject jsonObject = null;

      try {
         jsonObject = new YJSONObject(json);
         jsonObject.parse();
      } catch (Exception var6) {
         return "";
      }

      String[] split = path.split("\\|");
      int ofs = 0;
      return this._get_json_path_struct(jsonObject, split, 0);
   }

   String _decode_json_string(String json) {
      try {
         YJSONString yjsonString = new YJSONString(json);
         yjsonString.parse();
         return yjsonString.getString();
      } catch (Exception var3) {
         return "";
      }
   }

   YJSONObject _devRequest(String extra) throws YAPI_Exception {
      YDevice dev = this.getYDevice();
      this._hwId = this._yapi._yHash.resolveHwID(this._className, this._func);
      String[] split = this._hwId.split("\\.");
      this._funId = split[1];
      this._serial = split[0];
      YJSONObject loadval = null;
      if (extra.equals("")) {
         YJSONObject jsonval = dev.requestAPI();

         try {
            loadval = jsonval.getYJSONObject(this._funId);
         } catch (Exception var9) {
            throw new YAPI_Exception(-8, "Request failed, could not parse API result for " + dev);
         }
      } else {
         dev.clearCache();
      }

      if (loadval != null) {
         return loadval;
      } else if (!extra.equals("")) {
         String httpreq = "GET /api/" + this._funId + extra;
         dev.requestHTTPAsync(httpreq, null, null, null);
         return null;
      } else {
         String httpreq = "GET /api/" + this._funId + ".json";
         String yreq = dev.requestHTTPSyncAsString(httpreq, null);

         try {
            loadval = new YJSONObject(yreq);
            loadval.parse();
            return loadval;
         } catch (Exception var8) {
            throw new YAPI_Exception(-8, "Request failed, could not parse API value for " + httpreq);
         }
      }
   }

   YDevice getYDevice() throws YAPI_Exception {
      return this._yapi.funcGetDevice(this._className, this._func);
   }

   YDataStream _findDataStream(YDataSet dataset, String def) throws YAPI_Exception {
      String key = dataset.get_functionId() + ":" + def;
      if (this._dataStreams.containsKey(key)) {
         return this._dataStreams.get(key);
      } else {
         ArrayList<Integer> encoded = YAPIContext._decodeWords(def);
         if (encoded.size() < 14) {
            this._throw(-5, "device firmware is too old");
         }

         YDataStream newDataStream = new YDataStream(this, dataset, encoded);
         this._dataStreams.put(key, newDataStream);
         return newDataStream;
      }
   }

   public void _clearDataStreamCache() {
      this._dataStreams.clear();
   }

   public synchronized boolean isOnline() {
      if (this._cacheExpiration > YAPI.GetTickCount()) {
         return true;
      } else {
         try {
            this.load(this._yapi._defaultCacheValidity);
            return true;
         } catch (YAPI_Exception var2) {
            return false;
         }
      }
   }

   public int get_errorType() {
      return this._lastErrorType;
   }

   public int getErrorType() {
      return this._lastErrorType;
   }

   public int errorType() {
      return this._lastErrorType;
   }

   public int errType() {
      return this._lastErrorType;
   }

   public String get_errorMessage() {
      return this._lastErrorMsg;
   }

   public String getErrorMessage() {
      return this._lastErrorMsg;
   }

   public String errorMessage() {
      return this._lastErrorMsg;
   }

   public String errMessage() {
      return this._lastErrorMsg;
   }

   public synchronized void clearCache() {
      try {
         YDevice dev = this.getYDevice();
         dev.clearCache();
      } catch (YAPI_Exception var2) {
      }

      if (this._cacheExpiration != 0L) {
         this._cacheExpiration = YAPI.GetTickCount();
      }
   }

   public synchronized int load(long msValidity) throws YAPI_Exception {
      YJSONObject json_obj = this._devRequest("");
      this._parse(json_obj, msValidity);
      this._cacheExpiration = YAPI.GetTickCount() + msValidity;
      return 0;
   }

   public synchronized YModule get_module() {
      if (this._serial != null && !this._serial.equals("")) {
         return YModule.FindModuleInContext(this._yapi, this._serial + ".module");
      } else {
         int ofs = this._func.indexOf(46);
         if (ofs == -1) {
            try {
               String serial = this._yapi._yHash.resolveSerial(this._className, this._func);
               return YModule.FindModuleInContext(this._yapi, serial + ".module");
            } catch (YAPI_Exception var4) {
            }
         }

         if (ofs >= 0) {
            String serial = this._func.substring(0, ofs);
            return YModule.FindModuleInContext(this._yapi, serial + ".module");
         } else {
            try {
               if (this.load(this._yapi._defaultCacheValidity) == 0) {
                  String serial = this._yapi._yHash.resolveSerial(this._className, this._func);
                  return YModule.FindModuleInContext(this._yapi, serial + ".module");
               }
            } catch (YAPI_Exception var3) {
            }

            return YModule.FindModuleInContext(this._yapi, "module_of_" + this._className + "_" + this._func);
         }
      }
   }

   public YModule getModule() {
      return this.get_module();
   }

   public YModule module() {
      return this.get_module();
   }

   public synchronized String get_functionDescriptor() {
      try {
         return this._yapi._yHash.resolveHwID(this._className, this._func);
      } catch (YAPI_Exception var2) {
         return "!INVALID!";
      }
   }

   public String getFunctionDescriptor() {
      return this.get_functionDescriptor();
   }

   public String functionDescriptor() {
      return this.get_functionDescriptor();
   }

   public synchronized Object get_userData() {
      return this._userData;
   }

   public Object getUserData() {
      return this.get_userData();
   }

   public Object userData() {
      return this.get_userData();
   }

   public synchronized void set_userData(Object data) {
      this._userData = data;
   }

   public void setUserData(Object data) {
      this.set_userData(data);
   }

   public interface TimedReportCallback {
      void timedReportCallback(YFunction var1, YMeasure var2);
   }

   public interface UpdateCallback {
      void yNewValue(YFunction var1, String var2);
   }
}
