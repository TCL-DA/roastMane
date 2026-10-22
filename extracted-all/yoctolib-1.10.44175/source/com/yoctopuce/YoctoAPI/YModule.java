package com.yoctopuce.YoctoAPI;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;

public class YModule extends YFunction {
   public static final String PRODUCTNAME_INVALID = "!INVALID!";
   public static final String SERIALNUMBER_INVALID = "!INVALID!";
   public static final int PRODUCTID_INVALID = -1;
   public static final int PRODUCTRELEASE_INVALID = -1;
   public static final String FIRMWARERELEASE_INVALID = "!INVALID!";
   public static final int PERSISTENTSETTINGS_LOADED = 0;
   public static final int PERSISTENTSETTINGS_SAVED = 1;
   public static final int PERSISTENTSETTINGS_MODIFIED = 2;
   public static final int PERSISTENTSETTINGS_INVALID = -1;
   public static final int LUMINOSITY_INVALID = -1;
   public static final int BEACON_OFF = 0;
   public static final int BEACON_ON = 1;
   public static final int BEACON_INVALID = -1;
   public static final long UPTIME_INVALID = -9223372036854775807L;
   public static final int USBCURRENT_INVALID = -1;
   public static final int REBOOTCOUNTDOWN_INVALID = Integer.MIN_VALUE;
   public static final int USERVAR_INVALID = Integer.MIN_VALUE;
   protected String _productName = "!INVALID!";
   protected String _serialNumber = "!INVALID!";
   protected int _productId = -1;
   protected int _productRelease = -1;
   protected String _firmwareRelease = "!INVALID!";
   protected int _persistentSettings = -1;
   protected int _luminosity = -1;
   protected int _beacon = -1;
   protected long _upTime = -9223372036854775807L;
   protected int _usbCurrent = -1;
   protected int _rebootCountdown = Integer.MIN_VALUE;
   protected int _userVar = Integer.MIN_VALUE;
   protected YModule.UpdateCallback _valueCallbackModule = null;
   protected YModule.LogCallback _logCallback = null;
   protected YModule.ConfigChangeCallback _confChangeCallback = null;
   protected YModule.BeaconCallback _beaconCallback = null;

   private static void _updateModuleCallbackList(YModule module, boolean add) {
      module._yapi._UpdateModuleCallbackList(module, add);
   }

   YDevice _getDev() throws YAPI_Exception {
      String devid = this._func;
      int dotidx = devid.indexOf(46);
      if (dotidx >= 0) {
         devid = devid.substring(0, dotidx);
      }

      YDevice dev = this._yapi._yHash.getDevice(devid);
      if (dev == null) {
         throw new YAPI_Exception(-4, "Device [" + devid + "] is not online");
      } else {
         return dev;
      }
   }

   protected YModule(YAPIContext yctx, String func) {
      super(yctx, func);
      this._className = "Module";
   }

   protected YModule(String func) {
      this(YAPI.GetYCtx(false), func);
   }

   public int functionCount() throws YAPI_Exception {
      YDevice dev = this._getDev();
      return dev.getFunctions().size();
   }

   private YPEntry getYPFromIndex(int index) throws YAPI_Exception {
      YDevice dev = this._getDev();
      Collection<YPEntry> functions = dev.getFunctions();
      int i = 0;

      for (YPEntry yp : functions) {
         if (i++ == index) {
            return yp;
         }
      }

      throw new YAPI_Exception(-2, String.format(Locale.US, "Invalid function index (%d/%d)", index, functions.size()));
   }

   public String functionId(int functionIndex) throws YAPI_Exception {
      YPEntry ypEntry = this.getYPFromIndex(functionIndex);
      return ypEntry.getFuncId();
   }

   public String functionType(int functionIndex) throws YAPI_Exception {
      YPEntry ypEntry = this.getYPFromIndex(functionIndex);
      return ypEntry.getClassname();
   }

   public String functionBaseType(int functionIndex) throws YAPI_Exception {
      YPEntry ypEntry = this.getYPFromIndex(functionIndex);
      return ypEntry.getBaseType();
   }

   public String functionName(int functionIndex) throws YAPI_Exception {
      YPEntry ypEntry = this.getYPFromIndex(functionIndex);
      return ypEntry.getLogicalName();
   }

   public String functionValue(int functionIndex) throws YAPI_Exception {
      YPEntry ypEntry = this.getYPFromIndex(functionIndex);
      return ypEntry.getAdvertisedValue();
   }

   private byte[] _flattenJsonStruct_internal(byte[] actualSettings) throws YAPI_Exception {
      YJSONObject json = null;
      YJSONArray out = new YJSONArray();

      try {
         json = new YJSONObject(new String(actualSettings, this._yapi._deviceCharset));
         json.parse();
      } catch (Exception var13) {
         throw new YAPI_Exception(-8, var13.getLocalizedMessage());
      }

      for (String fun_key : json.getKeys()) {
         if (!fun_key.equals("services")) {
            YJSONObject functionJson = json.getYJSONObject(fun_key);
            if (functionJson != null) {
               for (String attr_key : functionJson.getKeys()) {
                  YJSONContent value = functionJson.get(attr_key);
                  if (value != null && value.getJSONType() != YJSONContent.YJSONType.OBJECT) {
                     String flat_attr = fun_key + "/" + attr_key + "=" + value.toString();
                     out.put(flat_attr);
                  }
               }
            }
         }
      }

      return out.toJSON().getBytes();
   }

   private ArrayList<String> get_subDevices_internal() throws YAPI_Exception {
      YDevice dev = this._getDev();
      YGenericHub hub = dev.getHub();
      return hub.get_subDeviceOf(this._serialNumber);
   }

   private String get_parentHub_internal() throws YAPI_Exception {
      YDevice dev = this._getDev();
      YGenericHub hub = dev.getHub();
      String hubSerial = hub.getSerialNumber();
      return hubSerial.equals(this._serialNumber) ? "" : hubSerial;
   }

   private String get_url_internal() throws YAPI_Exception {
      YDevice dev = this._getDev();
      YGenericHub hub = dev.getHub();
      return hub.get_urlOf(this._serialNumber);
   }

   public void _startStopDevLog_internal(String serial, boolean start) throws YAPI_Exception {
      YDevice ydev = this._yapi._yHash.getDevice(serial);
      if (ydev != null) {
         ydev.registerLogCallback(this._logCallback);
      }
   }

   @Override
   protected void _parseAttr(YJSONObject json_val) throws Exception {
      if (json_val.has("productName")) {
         this._productName = json_val.getString("productName");
      }

      if (json_val.has("serialNumber")) {
         this._serialNumber = json_val.getString("serialNumber");
      }

      if (json_val.has("productId")) {
         this._productId = json_val.getInt("productId");
      }

      if (json_val.has("productRelease")) {
         this._productRelease = json_val.getInt("productRelease");
      }

      if (json_val.has("firmwareRelease")) {
         this._firmwareRelease = json_val.getString("firmwareRelease");
      }

      if (json_val.has("persistentSettings")) {
         this._persistentSettings = json_val.getInt("persistentSettings");
      }

      if (json_val.has("luminosity")) {
         this._luminosity = json_val.getInt("luminosity");
      }

      if (json_val.has("beacon")) {
         this._beacon = json_val.getInt("beacon") > 0 ? 1 : 0;
      }

      if (json_val.has("upTime")) {
         this._upTime = json_val.getLong("upTime");
      }

      if (json_val.has("usbCurrent")) {
         this._usbCurrent = json_val.getInt("usbCurrent");
      }

      if (json_val.has("rebootCountdown")) {
         this._rebootCountdown = json_val.getInt("rebootCountdown");
      }

      if (json_val.has("userVar")) {
         this._userVar = json_val.getInt("userVar");
      }

      super._parseAttr(json_val);
   }

   public String get_productName() throws YAPI_Exception {
      synchronized (this) {
         if (this._cacheExpiration == 0L) {
            YDevice dev = this._getDev();
            if (dev != null) {
               return dev.getProductName();
            }

            if (this.load(this._yapi._defaultCacheValidity) != 0) {
               return "!INVALID!";
            }
         }

         return this._productName;
      }
   }

   public String getProductName() throws YAPI_Exception {
      return this.get_productName();
   }

   @Override
   public String get_serialNumber() throws YAPI_Exception {
      synchronized (this) {
         if (this._cacheExpiration == 0L) {
            YDevice dev = this._getDev();
            if (dev != null) {
               return dev.getSerialNumber();
            }

            if (this.load(this._yapi._defaultCacheValidity) != 0) {
               return "!INVALID!";
            }
         }

         return this._serialNumber;
      }
   }

   public String getSerialNumber() throws YAPI_Exception {
      return this.get_serialNumber();
   }

   public int get_productId() throws YAPI_Exception {
      synchronized (this) {
         if (this._cacheExpiration == 0L) {
            YDevice dev = this._getDev();
            if (dev != null) {
               return dev.getProductId();
            }

            if (this.load(this._yapi._defaultCacheValidity) != 0) {
               return -1;
            }
         }

         return this._productId;
      }
   }

   public int getProductId() throws YAPI_Exception {
      return this.get_productId();
   }

   public int get_productRelease() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration == 0L && this.load(this._yapi._defaultCacheValidity) != 0 ? -1 : this._productRelease;
      }
   }

   public int getProductRelease() throws YAPI_Exception {
      return this.get_productRelease();
   }

   public String get_firmwareRelease() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? "!INVALID!" : this._firmwareRelease;
      }
   }

   public String getFirmwareRelease() throws YAPI_Exception {
      return this.get_firmwareRelease();
   }

   public int get_persistentSettings() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -1 : this._persistentSettings;
      }
   }

   public int getPersistentSettings() throws YAPI_Exception {
      return this.get_persistentSettings();
   }

   public int set_persistentSettings(int newval) throws YAPI_Exception {
      synchronized (this) {
         String rest_val = Integer.toString(newval);
         this._setAttr("persistentSettings", rest_val);
         return 0;
      }
   }

   public int get_luminosity() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -1 : this._luminosity;
      }
   }

   public int getLuminosity() throws YAPI_Exception {
      return this.get_luminosity();
   }

   public int set_luminosity(int newval) throws YAPI_Exception {
      synchronized (this) {
         String rest_val = Integer.toString(newval);
         this._setAttr("luminosity", rest_val);
         return 0;
      }
   }

   public int setLuminosity(int newval) throws YAPI_Exception {
      return this.set_luminosity(newval);
   }

   public int get_beacon() throws YAPI_Exception {
      synchronized (this) {
         if (this._cacheExpiration <= YAPIContext.GetTickCount()) {
            YDevice dev = this._getDev();
            if (dev != null) {
               return dev.getBeacon();
            }

            if (this.load(this._yapi._defaultCacheValidity) != 0) {
               return -1;
            }
         }

         return this._beacon;
      }
   }

   public int getBeacon() throws YAPI_Exception {
      return this.get_beacon();
   }

   public int set_beacon(int newval) throws YAPI_Exception {
      synchronized (this) {
         String rest_val = newval > 0 ? "1" : "0";
         this._setAttr("beacon", rest_val);
         return 0;
      }
   }

   public int setBeacon(int newval) throws YAPI_Exception {
      return this.set_beacon(newval);
   }

   public long get_upTime() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -9223372036854775807L : this._upTime;
      }
   }

   public long getUpTime() throws YAPI_Exception {
      return this.get_upTime();
   }

   public int get_usbCurrent() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? -1 : this._usbCurrent;
      }
   }

   public int getUsbCurrent() throws YAPI_Exception {
      return this.get_usbCurrent();
   }

   public int get_rebootCountdown() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0
            ? Integer.MIN_VALUE
            : this._rebootCountdown;
      }
   }

   public int getRebootCountdown() throws YAPI_Exception {
      return this.get_rebootCountdown();
   }

   public int set_rebootCountdown(int newval) throws YAPI_Exception {
      synchronized (this) {
         String rest_val = Integer.toString(newval);
         this._setAttr("rebootCountdown", rest_val);
         return 0;
      }
   }

   public int get_userVar() throws YAPI_Exception {
      synchronized (this) {
         return this._cacheExpiration <= YAPIContext.GetTickCount() && this.load(this._yapi._defaultCacheValidity) != 0 ? Integer.MIN_VALUE : this._userVar;
      }
   }

   public int getUserVar() throws YAPI_Exception {
      return this.get_userVar();
   }

   public int set_userVar(int newval) throws YAPI_Exception {
      synchronized (this) {
         String rest_val = Integer.toString(newval);
         this._setAttr("userVar", rest_val);
         return 0;
      }
   }

   public int setUserVar(int newval) throws YAPI_Exception {
      return this.set_userVar(newval);
   }

   public static YModule FindModule(String func) {
      String cleanHwId = func;
      int modpos = func.indexOf(".module");
      if (modpos != func.length() - 7) {
         cleanHwId = func + ".module";
      }

      YAPIContext ctx = YAPI.GetYCtx(true);
      synchronized (ctx._functionCacheLock) {
         YModule obj = (YModule)YFunction._FindFromCache("Module", cleanHwId);
         if (obj == null) {
            obj = new YModule(cleanHwId);
            YFunction._AddToCache("Module", cleanHwId, obj);
         }

         return obj;
      }
   }

   public static YModule FindModuleInContext(YAPIContext yctx, String func) {
      synchronized (yctx._functionCacheLock) {
         String cleanHwId = func;
         int modpos = func.indexOf(".module");
         if (modpos != func.length() - 7) {
            cleanHwId = func + ".module";
         }

         YModule obj = (YModule)YFunction._FindFromCacheInContext(yctx, "Module", cleanHwId);
         if (obj == null) {
            obj = new YModule(yctx, cleanHwId);
            YFunction._AddToCache("Module", cleanHwId, obj);
         }

         return obj;
      }
   }

   public int registerValueCallback(YModule.UpdateCallback callback) {
      if (callback != null) {
         YFunction._UpdateValueCallbackList(this, true);
      } else {
         YFunction._UpdateValueCallbackList(this, false);
      }

      this._valueCallbackModule = callback;
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
      if (this._valueCallbackModule != null) {
         this._valueCallbackModule.yNewValue(this, value);
      } else {
         super._invokeValueCallback(value);
      }

      return 0;
   }

   public String get_productNameAndRevision() throws YAPI_Exception {
      String prodname = this.get_productName();
      int prodrel = this.get_productRelease();
      String fullname;
      if (prodrel > 1) {
         fullname = String.format(Locale.US, "%s rev. %c", prodname, 64 + prodrel);
      } else {
         fullname = prodname;
      }

      return fullname;
   }

   public int saveToFlash() throws YAPI_Exception {
      return this.set_persistentSettings(1);
   }

   public int revertFromFlash() throws YAPI_Exception {
      return this.set_persistentSettings(0);
   }

   public int reboot(int secBeforeReboot) throws YAPI_Exception {
      return this.set_rebootCountdown(secBeforeReboot);
   }

   public int triggerFirmwareUpdate(int secBeforeReboot) throws YAPI_Exception {
      return this.set_rebootCountdown(-secBeforeReboot);
   }

   public void _startStopDevLog(String serial, boolean start) throws YAPI_Exception {
      this._startStopDevLog_internal(serial, start);
   }

   public int registerLogCallback(YModule.LogCallback callback) throws YAPI_Exception {
      String serial = this.get_serialNumber();
      if (serial.equals("!INVALID!")) {
         return -4;
      } else {
         this._logCallback = callback;
         this._startStopDevLog(serial, callback != null);
         return 0;
      }
   }

   public YModule.LogCallback get_logCallback() {
      return this._logCallback;
   }

   public int registerConfigChangeCallback(YModule.ConfigChangeCallback callback) {
      if (callback != null) {
         _updateModuleCallbackList(this, true);
      } else {
         _updateModuleCallbackList(this, false);
      }

      this._confChangeCallback = callback;
      return 0;
   }

   public int _invokeConfigChangeCallback() {
      if (this._confChangeCallback != null) {
         this._confChangeCallback.configChangeCallback(this);
      }

      return 0;
   }

   public int registerBeaconCallback(YModule.BeaconCallback callback) {
      if (callback != null) {
         _updateModuleCallbackList(this, true);
      } else {
         _updateModuleCallbackList(this, false);
      }

      this._beaconCallback = callback;
      return 0;
   }

   public int _invokeBeaconCallback(int beaconState) {
      if (this._beaconCallback != null) {
         this._beaconCallback.beaconCallback(this, beaconState);
      }

      return 0;
   }

   public int triggerConfigChangeCallback() throws YAPI_Exception {
      this._setAttr("persistentSettings", "2");
      return 0;
   }

   public String checkFirmware(String path, boolean onlynew) throws YAPI_Exception {
      int release;
      if (onlynew) {
         release = YAPIContext._atoi(this.get_firmwareRelease());
      } else {
         release = 0;
      }

      String serial = this.get_serialNumber();
      String tmp_res = YFirmwareUpdate.CheckFirmware(serial, path, release);
      if (tmp_res.indexOf("error:") == 0) {
         this._throw(-2, tmp_res);
      }

      return tmp_res;
   }

   public YFirmwareUpdate updateFirmwareEx(String path, boolean force) throws YAPI_Exception {
      String serial = this.get_serialNumber();
      byte[] settings = this.get_allSettings();
      if (settings.length == 0) {
         this._throw(-8, "Unable to get device settings");
         settings = "error:Unable to get device settings".getBytes();
      }

      return new YFirmwareUpdate(this._yapi, serial, path, settings, force);
   }

   public YFirmwareUpdate updateFirmware(String path) throws YAPI_Exception {
      return this.updateFirmwareEx(path, false);
   }

   public byte[] get_allSettings() throws YAPI_Exception {
      new ArrayList();
      new ArrayList();
      byte[] settings = this._download("api.json");
      if (settings.length == 0) {
         return settings;
      } else {
         String ext_settings = ", \"extras\":[";
         ArrayList<String> templist = this.get_functionIds("Temperature");
         String sep = "";

         for (String ii : templist) {
            if (YAPIContext._atoi(this.get_firmwareRelease()) > 9000) {
               String url = String.format(Locale.US, "api/%s/sensorType", ii);
               String t_type = new String(this._download(url));
               if (t_type.equals("RES_NTC") || t_type.equals("RES_LINEAR")) {
                  String id = ii.substring(11, 11 + ii.length() - 11);
                  if (id.equals("")) {
                     id = "1";
                  }

                  byte[] temp_data_bin = this._download(String.format(Locale.US, "extra.json?page=%s", id));
                  if (temp_data_bin.length > 0) {
                     String item = String.format(Locale.US, "%s{\"fid\":\"%s\", \"json\":%s}\n", sep, ii, new String(temp_data_bin));
                     ext_settings = ext_settings + item;
                     sep = ",";
                  }
               }
            }
         }

         ext_settings = ext_settings + "],\n\"files\":[";
         if (this.hasFunction("files")) {
            byte[] json = this._download("files.json?a=dir&f=");
            if (json.length == 0) {
               return json;
            }

            ArrayList<String> filelist = this._json_get_array(json);
            sep = "";

            for (String iix : filelist) {
               String name = this._json_get_key(iix.getBytes(), "name");
               if (name.length() > 0 && !name.equals("startupConf.json")) {
                  byte[] file_data_bin = this._download(this._escapeAttr(name));
                  String file_data = YAPIContext._bytesToHexStr(file_data_bin, 0, file_data_bin.length);
                  String item = String.format(Locale.US, "%s{\"name\":\"%s\", \"data\":\"%s\"}\n", sep, name, file_data);
                  ext_settings = ext_settings + item;
                  sep = ",";
               }
            }
         }

         return ("{ \"api\":" + new String(settings) + ext_settings + "]}").getBytes();
      }
   }

   public int loadThermistorExtra(String funcId, String jsonExtra) throws YAPI_Exception {
      new ArrayList();
      String url = "api/" + funcId + ".json?command=Z";
      this._download(url);
      ArrayList<String> values = this._json_get_array(jsonExtra.getBytes());
      int ofs = 0;

      for (int size = values.size(); ofs + 1 < size; ofs += 2) {
         String curr = (String)values.get(ofs);
         String currTemp = (String)values.get(ofs + 1);
         url = String.format(Locale.US, "api/%s.json?command=m%s:%s", funcId, curr, currTemp);
         this._download(url);
      }

      return 0;
   }

   public int set_extraSettings(String jsonExtra) throws YAPI_Exception {
      new ArrayList();

      for (String ii : this._json_get_array(jsonExtra.getBytes())) {
         String functionId = this._get_json_path(ii, "fid");
         functionId = this._decode_json_string(functionId);
         String data = this._get_json_path(ii, "json");
         if (this.hasFunction(functionId)) {
            this.loadThermistorExtra(functionId, data);
         }
      }

      return 0;
   }

   public int set_allSettingsAndFiles(byte[] settings) throws YAPI_Exception {
      int fuperror = 0;
      String json = new String(settings);
      String json_api = this._get_json_path(json, "api");
      if (json_api.equals("")) {
         return this.set_allSettings(settings);
      } else {
         String json_extra = this._get_json_path(json, "extras");
         if (!json_extra.equals("")) {
            this.set_extraSettings(json_extra);
         }

         this.set_allSettings(json_api.getBytes());
         if (this.hasFunction("files")) {
            new ArrayList();
            byte[] down = this._download("files.json?a=format");
            String res = this._get_json_path(new String(down), "res");
            res = this._decode_json_string(res);
            if (!res.equals("ok")) {
               throw new YAPI_Exception(-8, "format failed");
            }

            String json_files = this._get_json_path(json, "files");

            for (String ii : this._json_get_array(json_files.getBytes())) {
               String name = this._get_json_path(ii, "name");
               name = this._decode_json_string(name);
               String data = this._get_json_path(ii, "data");
               data = this._decode_json_string(data);
               if (name.equals("")) {
                  fuperror++;
               } else {
                  this._upload(name, YAPIContext._hexStrToBin(data));
               }
            }
         }

         int globalres = this.set_allSettings(json_api.getBytes());
         if (fuperror != 0) {
            throw new YAPI_Exception(-8, "Error during file upload");
         } else {
            return globalres;
         }
      }
   }

   public boolean hasFunction(String funcId) throws YAPI_Exception {
      int count = this.functionCount();

      for (int i = 0; i < count; i++) {
         String fid = this.functionId(i);
         if (fid.equals(funcId)) {
            return true;
         }
      }

      return false;
   }

   public ArrayList<String> get_functionIds(String funType) throws YAPI_Exception {
      ArrayList<String> res = new ArrayList<>();
      int count = this.functionCount();

      for (int i = 0; i < count; i++) {
         String ftype = this.functionType(i);
         if (ftype.equals(funType)) {
            res.add(this.functionId(i));
         } else {
            ftype = this.functionBaseType(i);
            if (ftype.equals(funType)) {
               res.add(this.functionId(i));
            }
         }
      }

      return res;
   }

   public byte[] _flattenJsonStruct(byte[] jsoncomplex) throws YAPI_Exception {
      return this._flattenJsonStruct_internal(jsoncomplex);
   }

   public int calibVersion(String cparams) {
      if (cparams.equals("0,")) {
         return 3;
      } else if (cparams.indexOf(",") >= 0) {
         return cparams.indexOf(" ") > 0 ? 3 : 1;
      } else if (!cparams.equals("") && !cparams.equals("0")) {
         return cparams.length() >= 2 && cparams.indexOf(".") < 0 ? 2 : 0;
      } else {
         return 1;
      }
   }

   public int calibScale(String unit_name, String sensorType) {
      if (unit_name.equals("g") || unit_name.equals("gauss") || unit_name.equals("W")) {
         return 1000;
      } else if (unit_name.equals("C")) {
         if (sensorType.equals("")) {
            return 16;
         } else {
            return YAPIContext._atoi(sensorType) < 8 ? 16 : 100;
         }
      } else {
         return !unit_name.equals("m") && !unit_name.equals("deg") ? 1 : 10;
      }
   }

   public int calibOffset(String unit_name) {
      return !unit_name.equals("% RH") && !unit_name.equals("mbar") && !unit_name.equals("lx") ? 32767 : 0;
   }

   public String calibConvert(String param, String currentFuncValue, String unit_name, String sensorType) {
      ArrayList<Integer> words = new ArrayList<>();
      new ArrayList();
      ArrayList<Double> calibData = new ArrayList<>();
      new ArrayList();
      int paramVer = this.calibVersion(param);
      int funVer = this.calibVersion(currentFuncValue);
      int funScale = this.calibScale(unit_name, sensorType);
      int funOffset = this.calibOffset(unit_name);
      int paramScale = funScale;
      int paramOffset = funOffset;
      if (funVer < 3) {
         if (funVer == 2) {
            words = YAPIContext._decodeWords(currentFuncValue);
            if (words.get(0) == 1366 && words.get(1) == 12500) {
               funScale = 1;
               funOffset = 0;
            } else {
               funScale = words.get(1);
               funOffset = words.get(0);
            }
         } else if (funVer == 1 && (currentFuncValue.equals("") || YAPIContext._atoi(currentFuncValue) > 10)) {
            funScale = 0;
         }
      }

      calibData.clear();
      int calibType = 0;
      if (paramVer < 3) {
         if (paramVer == 2) {
            words = YAPIContext._decodeWords(param);
            if (words.get(0) == 1366 && words.get(1) == 12500) {
               paramScale = 1;
               paramOffset = 0;
            } else {
               paramScale = words.get(1);
               paramOffset = words.get(0);
            }

            if (words.size() >= 3 && words.get(2) > 0) {
               int maxSize = 3 + 2 * (words.get(2) % 10);
               if (maxSize > words.size()) {
                  maxSize = words.size();
               }

               for (int i = 3; i < maxSize; i++) {
                  calibData.add((double)words.get(i).intValue());
               }
            }
         } else if (paramVer != 1) {
            if (paramVer == 0) {
               double ratio = Double.valueOf(param);
               if (ratio > 0.0) {
                  calibData.add(0.0);
                  calibData.add(0.0);
                  calibData.add((double)Math.round(65535.0 / ratio));
                  calibData.add(65535.0);
               }
            }
         } else {
            for (String ii : new ArrayList<>(Arrays.asList(param.split(",")))) {
               words.add(YAPIContext._atoi(ii));
            }

            if (param.equals("") || words.get(0) > 10) {
               paramScale = 0;
            }

            if (words.size() > 0 && words.get(0) > 0) {
               int maxSize = 1 + 2 * (words.get(0) % 10);
               if (maxSize > words.size()) {
                  maxSize = words.size();
               }

               for (int i = 1; i < maxSize; i++) {
                  calibData.add((double)words.get(i).intValue());
               }
            }
         }

         for (int i = 0; i < calibData.size(); i++) {
            if (paramScale > 0) {
               calibData.set(i, (calibData.get(i) - (double)paramOffset) / (double)paramScale);
            } else {
               calibData.set(i, YAPIContext._decimalToDouble((int)((double)Math.round(calibData.get(i)))));
            }
         }
      } else {
         ArrayList<Integer> iCalib = YAPIContext._decodeFloats(param);
         calibType = (int)((double)Math.round(((Integer)iCalib.get(0)).doubleValue() / 1000.0));
         if (calibType >= 30) {
            calibType -= 30;
         }

         for (int ix = 1; ix < iCalib.size(); ix++) {
            calibData.add(((Integer)iCalib.get(ix)).doubleValue() / 1000.0);
         }
      }

      if (funVer >= 3) {
         if (calibData.size() == 0) {
            param = "0,";
         } else {
            param = Integer.toString(30 + calibType);

            for (int var31 = 0; var31 < calibData.size(); var31++) {
               if ((var31 & 1) > 0) {
                  param = param + ":";
               } else {
                  param = param + " ";
               }

               param = param + Integer.toString((int)((double)Math.round(calibData.get(var31) * 1000.0 / 1000.0)));
            }

            param = param + ",";
         }
      } else if (funVer >= 1) {
         int nPoints = calibData.size() / 2;
         param = Integer.toString(nPoints);

         for (int var32 = 0; var32 < 2 * nPoints; var32++) {
            double wordVal;
            if (funScale == 0) {
               wordVal = (double)YAPIContext._doubleToDecimal((double)((int)((double)Math.round(calibData.get(var32)))));
            } else {
               wordVal = calibData.get(var32) * (double)funScale + (double)funOffset;
            }

            param = param + "," + Double.toString((double)Math.round(wordVal));
         }
      } else if (calibData.size() == 4) {
         param = Double.toString((double)Math.round(1000.0 * (calibData.get(3) - calibData.get(1)) / calibData.get(2) - calibData.get(0)));
      }

      return param;
   }

   public int _tryExec(String url) {
      int res = 0;
      int done = 1;

      try {
         this._download(url);
      } catch (Exception var6) {
         done = 0;
      }

      if (done == 0) {
         try {
            YAPI.Sleep(500L);
            this._download(url);
         } catch (Exception var5) {
            res = this.get_errorType();
         }
      }

      return res;
   }

   public int set_allSettings(byte[] settings) throws YAPI_Exception {
      ArrayList<String> restoreLast = new ArrayList<>();
      new ArrayList();
      ArrayList<String> old_jpath = new ArrayList<>();
      ArrayList<Integer> old_jpath_len = new ArrayList<>();
      ArrayList<String> old_val_arr = new ArrayList<>();
      new ArrayList();
      ArrayList<String> new_jpath = new ArrayList<>();
      ArrayList<Integer> new_jpath_len = new ArrayList<>();
      ArrayList<String> new_val_arr = new ArrayList<>();
      int res = 0;
      String tmp = new String(settings);
      tmp = this._get_json_path(tmp, "api");
      if (!tmp.equals("")) {
         settings = tmp.getBytes();
      }

      String oldval = "";
      String newval = "";
      byte[] old_json_flat = this._flattenJsonStruct(settings);

      for (String ii : this._json_get_array(old_json_flat)) {
         String each_str = this._json_get_string(ii.getBytes());
         int leng = each_str.length();
         int eqpos = each_str.indexOf("=");
         if (eqpos < 0 || leng == 0) {
            this._throw(-2, "Invalid settings");
            return -2;
         }

         String jpath = each_str.substring(0, eqpos);
         String value = each_str.substring(++eqpos, eqpos + leng - eqpos);
         old_jpath.add(jpath);
         old_jpath_len.add(jpath.length());
         old_val_arr.add(value);
      }

      byte[] actualSettings;
      try {
         actualSettings = this._download("api.json");
      } catch (Exception var38) {
         YAPI.Sleep(500L);
         actualSettings = this._download("api.json");
      }

      actualSettings = this._flattenJsonStruct(actualSettings);

      for (String ii : this._json_get_array(actualSettings)) {
         String each_str = this._json_get_string(ii.getBytes());
         int leng = each_str.length();
         int eqpos = each_str.indexOf("=");
         if (eqpos < 0 || leng == 0) {
            this._throw(-2, "Invalid settings");
            return -2;
         }

         String jpath = each_str.substring(0, eqpos);
         String value = each_str.substring(++eqpos, eqpos + leng - eqpos);
         new_jpath.add(jpath);
         new_jpath_len.add(jpath.length());
         new_val_arr.add(value);
      }

      int i = 0;

      while (i < new_jpath.size()) {
         String njpath = new_jpath.get(i);
         int leng = njpath.length();
         int cpos = njpath.indexOf("/");
         if (cpos >= 0 && leng != 0) {
            String fun = njpath.substring(0, cpos);
            String attr = njpath.substring(++cpos, cpos + leng - cpos);
            boolean do_update = true;
            if (fun.equals("services")) {
               do_update = false;
            }

            if (do_update && attr.equals("firmwareRelease")) {
               do_update = false;
            }

            if (do_update && attr.equals("usbCurrent")) {
               do_update = false;
            }

            if (do_update && attr.equals("upTime")) {
               do_update = false;
            }

            if (do_update && attr.equals("persistentSettings")) {
               do_update = false;
            }

            if (do_update && attr.equals("adminPassword")) {
               do_update = false;
            }

            if (do_update && attr.equals("userPassword")) {
               do_update = false;
            }

            if (do_update && attr.equals("rebootCountdown")) {
               do_update = false;
            }

            if (do_update && attr.equals("advertisedValue")) {
               do_update = false;
            }

            if (do_update && attr.equals("poeCurrent")) {
               do_update = false;
            }

            if (do_update && attr.equals("readiness")) {
               do_update = false;
            }

            if (do_update && attr.equals("ipAddress")) {
               do_update = false;
            }

            if (do_update && attr.equals("subnetMask")) {
               do_update = false;
            }

            if (do_update && attr.equals("router")) {
               do_update = false;
            }

            if (do_update && attr.equals("linkQuality")) {
               do_update = false;
            }

            if (do_update && attr.equals("ssid")) {
               do_update = false;
            }

            if (do_update && attr.equals("channel")) {
               do_update = false;
            }

            if (do_update && attr.equals("security")) {
               do_update = false;
            }

            if (do_update && attr.equals("message")) {
               do_update = false;
            }

            if (do_update && attr.equals("signalValue")) {
               do_update = false;
            }

            if (do_update && attr.equals("currentValue")) {
               do_update = false;
            }

            if (do_update && attr.equals("currentRawValue")) {
               do_update = false;
            }

            if (do_update && attr.equals("currentRunIndex")) {
               do_update = false;
            }

            if (do_update && attr.equals("pulseTimer")) {
               do_update = false;
            }

            if (do_update && attr.equals("lastTimePressed")) {
               do_update = false;
            }

            if (do_update && attr.equals("lastTimeReleased")) {
               do_update = false;
            }

            if (do_update && attr.equals("filesCount")) {
               do_update = false;
            }

            if (do_update && attr.equals("freeSpace")) {
               do_update = false;
            }

            if (do_update && attr.equals("timeUTC")) {
               do_update = false;
            }

            if (do_update && attr.equals("rtcTime")) {
               do_update = false;
            }

            if (do_update && attr.equals("unixTime")) {
               do_update = false;
            }

            if (do_update && attr.equals("dateTime")) {
               do_update = false;
            }

            if (do_update && attr.equals("rawValue")) {
               do_update = false;
            }

            if (do_update && attr.equals("lastMsg")) {
               do_update = false;
            }

            if (do_update && attr.equals("delayedPulseTimer")) {
               do_update = false;
            }

            if (do_update && attr.equals("rxCount")) {
               do_update = false;
            }

            if (do_update && attr.equals("txCount")) {
               do_update = false;
            }

            if (do_update && attr.equals("msgCount")) {
               do_update = false;
            }

            if (do_update && attr.equals("rxMsgCount")) {
               do_update = false;
            }

            if (do_update && attr.equals("txMsgCount")) {
               do_update = false;
            }

            if (do_update) {
               do_update = false;
               newval = new_val_arr.get(i);
               int j = 0;

               for (boolean found = false; j < old_jpath.size() && !found; j++) {
                  if (new_jpath_len.get(i) == old_jpath_len.get(j) && new_jpath.get(i).equals(old_jpath.get(j))) {
                     found = true;
                     oldval = old_val_arr.get(j);
                     if (!newval.equals(oldval)) {
                        do_update = true;
                     }
                  }
               }
            }

            if (do_update) {
               if (!attr.equals("calibrationParam")) {
                  String url = "api/" + fun + ".json?" + attr + "=" + this._escapeAttr(oldval);
                  if (attr.equals("resolution")) {
                     restoreLast.add(url);
                  } else {
                     int subres = this._tryExec(url);
                     if (res == 0 && subres != 0) {
                        res = subres;
                     }
                  }
               } else {
                  String old_calib = "";
                  String unit_name = "";
                  String sensorType = "";
                  int j = 0;

                  for (boolean foundx = false; j < old_jpath.size() && !foundx; j++) {
                     if (new_jpath_len.get(i) == old_jpath_len.get(j) && new_jpath.get(i).equals(old_jpath.get(j))) {
                        foundx = true;
                        old_calib = old_val_arr.get(j);
                     }
                  }

                  tmp = fun + "/unit";
                  j = 0;

                  for (boolean var61 = false; j < new_jpath.size() && !var61; j++) {
                     if (tmp.equals(new_jpath.get(j))) {
                        var61 = true;
                        unit_name = new_val_arr.get(j);
                     }
                  }

                  tmp = fun + "/sensorType";
                  j = 0;

                  for (boolean var62 = false; j < new_jpath.size() && !var62; j++) {
                     if (tmp.equals(new_jpath.get(j))) {
                        var62 = true;
                        sensorType = new_val_arr.get(j);
                     }
                  }

                  newval = this.calibConvert(old_calib, new_val_arr.get(i), unit_name, sensorType);
                  String url = "api/" + fun + ".json?" + attr + "=" + this._escapeAttr(newval);
                  int subres = this._tryExec(url);
                  if (res == 0 && subres != 0) {
                     res = subres;
                  }
               }
            }

            i++;
         }
      }

      for (String ii : restoreLast) {
         int subres = this._tryExec(ii);
         if (res == 0 && subres != 0) {
            res = subres;
         }
      }

      this.clearCache();
      return res;
   }

   @Override
   public String get_hardwareId() throws YAPI_Exception {
      String serial = this.get_serialNumber();
      return serial + ".module";
   }

   public byte[] download(String pathname) throws YAPI_Exception {
      return this._download(pathname);
   }

   public byte[] get_icon2d() throws YAPI_Exception {
      return this._download("icon2d.png");
   }

   public String get_lastLogs() throws YAPI_Exception {
      byte[] content = this._download("logs.txt");
      return new String(content);
   }

   public int log(String text) throws YAPI_Exception {
      return this._upload("logs.txt", text.getBytes());
   }

   public ArrayList<String> get_subDevices() throws YAPI_Exception {
      return this.get_subDevices_internal();
   }

   public String get_parentHub() throws YAPI_Exception {
      return this.get_parentHub_internal();
   }

   public String get_url() throws YAPI_Exception {
      return this.get_url_internal();
   }

   public YModule nextModule() {
      String next_hwid;
      try {
         String hwid = this._yapi._yHash.resolveHwID(this._className, this._func);
         next_hwid = this._yapi._yHash.getNextHardwareId(this._className, hwid);
      } catch (YAPI_Exception var3) {
         next_hwid = null;
      }

      return next_hwid == null ? null : FindModuleInContext(this._yapi, next_hwid);
   }

   public static YModule FirstModule() {
      YAPIContext yctx = YAPI.GetYCtx(false);
      if (yctx == null) {
         return null;
      } else {
         String next_hwid = yctx._yHash.getFirstHardwareId("Module");
         return next_hwid == null ? null : FindModuleInContext(yctx, next_hwid);
      }
   }

   public static YModule FirstModuleInContext(YAPIContext yctx) {
      String next_hwid = yctx._yHash.getFirstHardwareId("Module");
      return next_hwid == null ? null : FindModuleInContext(yctx, next_hwid);
   }

   public interface BeaconCallback {
      void beaconCallback(YModule var1, int var2);
   }

   public interface ConfigChangeCallback {
      void configChangeCallback(YModule var1);
   }

   public interface LogCallback {
      void logCallback(YModule var1, String var2);
   }

   public interface TimedReportCallback {
      void timedReportCallback(YModule var1, YMeasure var2);
   }

   public interface UpdateCallback {
      void yNewValue(YModule var1, String var2);
   }
}
