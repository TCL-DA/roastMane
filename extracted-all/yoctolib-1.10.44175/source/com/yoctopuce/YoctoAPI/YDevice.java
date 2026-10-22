package com.yoctopuce.YoctoAPI;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Random;

class YDevice {
   private final YGenericHub _hub;
   private final WPEntry _wpRec;
   private final HashMap<Integer, YPEntry> _ypRecs;
   private long _cache_expiration;
   private YJSONObject _cache_json;
   private double _lastTimeRef;
   private double _lastDuration;
   private YPEntry _moduleYPEntry;
   private YModule.LogCallback _logCallback = null;
   private int _logpos = 0;
   private boolean _logIsPulling = false;
   private boolean _logNeedPulling = false;
   private YGenericHub.RequestAsyncResult _logCallbackHandler = new YGenericHub.RequestAsyncResult() {
      @Override
      public void RequestAsyncDone(Object context, byte[] result, int error, String errmsg) {
         if (result == null) {
            YDevice.this._logIsPulling = false;
         } else if (YDevice.this._logCallback == null) {
            YDevice.this._logIsPulling = false;
         } else {
            String resultStr = new String(result);
            int pos = resultStr.lastIndexOf("@");
            if (pos < 0) {
               YDevice.this._logIsPulling = false;
            } else {
               String logs = resultStr.substring(0, pos);
               String posStr = resultStr.substring(pos + 1);
               YDevice.this._logpos = Integer.valueOf(posStr);
               YModule module = YModule.FindModuleInContext(YDevice.this._hub._yctx, YDevice.this.getSerialNumber());
               String[] lines = logs.split("\n");

               for (String line : lines) {
                  YDevice.this._logCallback.logCallback(module, line);
               }

               YDevice.this._logIsPulling = false;
            }
         }
      }
   };

   YDevice(YGenericHub hub, WPEntry wpRec, HashMap<String, ArrayList<YPEntry>> ypRecs) {
      this._hub = hub;
      this._wpRec = wpRec;
      this._cache_expiration = 0L;
      this._cache_json = null;
      this._moduleYPEntry = new YPEntry(wpRec.getSerialNumber(), "module", YPEntry.BaseClass.Function);
      this._moduleYPEntry.setLogicalName(wpRec.getLogicalName());
      this._ypRecs = new HashMap<>();

      for (String categ : ypRecs.keySet()) {
         for (YPEntry rec : ypRecs.get(categ)) {
            if (rec.getSerial().equals(wpRec.getSerialNumber())) {
               int funydx = rec.getIndex();
               this._ypRecs.put(funydx, rec);
            }
         }
      }
   }

   YGenericHub getHub() {
      return this._hub;
   }

   String getNetworkUrl() {
      return this._wpRec.getNetworkUrl();
   }

   String getSerialNumber() {
      return this._wpRec.getSerialNumber();
   }

   String getLogicalName() {
      return this._wpRec.getLogicalName();
   }

   int getBeacon() {
      return this._wpRec.getBeacon();
   }

   String getProductName() {
      return this._wpRec.getProductName();
   }

   int getProductId() {
      return this._wpRec.getProductId();
   }

   synchronized YJSONObject requestAPI() throws YAPI_Exception {
      long tickCount = YAPI.GetTickCount();
      if (this._cache_expiration > tickCount) {
         return this._cache_json;
      } else {
         String request = "GET /api.json";
         if (this._cache_json != null) {
            String fwrelease = this._cache_json.getYJSONObject("module").getString("firmwareRelease");

            try {
               fwrelease = URLEncoder.encode(fwrelease, this._hub._yctx._defaultEncoding);
            } catch (UnsupportedEncodingException var10) {
            }

            request = request + "?fw=" + fwrelease;
         }

         String yreq;
         try {
            yreq = this.requestHTTPSyncAsString(request, null);
         } catch (YAPI_Exception var9) {
            try {
               this._hub.updateDeviceList(true);
            } catch (InterruptedException var8) {
               var8.printStackTrace();
               throw new YAPI_Exception(-8, "Thread interrupted", var8);
            }

            yreq = this.requestHTTPSyncAsString(request, null);
         }

         YJSONObject cache_json;
         try {
            cache_json = new YJSONObject(yreq);
            cache_json.parseWithRef(this._cache_json);
         } catch (Exception var7) {
            this._cache_json = null;
            throw new YAPI_Exception(-8, "Request failed, could not parse API result for " + this);
         }

         this._cache_expiration = YAPI.GetTickCount() + this._hub._yctx._defaultCacheValidity;
         this._cache_json = cache_json;
         return cache_json;
      }
   }

   synchronized void refresh() throws YAPI_Exception {
      YJSONObject loadval = this.requestAPI();
      Boolean reindex = false;

      try {
         for (String key : loadval.getKeys()) {
            if (key.equals("module")) {
               YJSONObject module = loadval.getYJSONObject("module");
               if (!this._wpRec.getLogicalName().equals(module.getString("logicalName"))) {
                  this._wpRec.setLogicalName(module.getString("logicalName"));
                  this._moduleYPEntry.setLogicalName(this._wpRec.getLogicalName());
                  reindex = true;
               }

               this._wpRec.setBeacon(module.getInt("beacon"));
            } else if (!key.equals("services")) {
               YJSONObject func = loadval.getYJSONObject(key);
               String name;
               if (func.has("logicalName")) {
                  name = func.getString("logicalName");
               } else {
                  name = this._wpRec.getLogicalName();
               }

               if (func.has("advertisedValue")) {
                  String pubval = func.getString("advertisedValue");
                  this._hub._yctx._yHash.setFunctionValue(this._wpRec.getSerialNumber() + "." + key, pubval);
               }

               for (int f = 0; f < this._ypRecs.size(); f++) {
                  if (this._ypRecs.get(f).getFuncId().equals(key)) {
                     if (!this._ypRecs.get(f).getLogicalName().equals(name)) {
                        this._ypRecs.get(f).setLogicalName(name);
                        reindex = true;
                     }
                     break;
                  }
               }
            }
         }
      } catch (Exception var9) {
         throw new YAPI_Exception(-8, "Request failed, could not parse API result");
      }

      if (reindex) {
         this._hub._yctx._yHash.reindexDevice(this);
      }
   }

   synchronized void clearCache() {
      this._cache_expiration = 0L;
   }

   Collection<YPEntry> getFunctions() {
      return this._ypRecs.values();
   }

   YPEntry getYPEntry(int idx) {
      return this._ypRecs.get(idx);
   }

   synchronized byte[] requestHTTPSync(String request, byte[] rest_of_request) throws YAPI_Exception {
      String shortRequest = this.formatRequest(request);

      byte[] res;
      try {
         res = this._hub.devRequestSync(this, shortRequest, rest_of_request, null, null);
      } catch (InterruptedException var6) {
         throw new YAPI_Exception(-8, "Thread has been interrupted");
      }

      if (this._logNeedPulling) {
         this.triggerLogPull();
      }

      return res;
   }

   String requestHTTPSyncAsString(String request, byte[] rest_of_request) throws YAPI_Exception {
      byte[] bytes = this.requestHTTPSync(request, rest_of_request);
      if (this._logNeedPulling) {
         this.triggerLogPull();
      }

      return new String(bytes, this._hub._yctx._deviceCharset);
   }

   void requestHTTPAsync(String request, byte[] rest_of_request, YGenericHub.RequestAsyncResult asyncResult, Object context) throws YAPI_Exception {
      String shortRequest = this.formatRequest(request);

      try {
         this._hub.devRequestAsync(this, shortRequest, rest_of_request, asyncResult, context);
      } catch (InterruptedException var7) {
         throw new YAPI_Exception(-8, "Thread has been interrupted");
      }

      if (this._logNeedPulling) {
         this.triggerLogPull();
      }
   }

   private String formatRequest(String request) throws YAPI_Exception {
      String[] words = request.split(" ");
      if (words.length < 2) {
         throw new YAPI_Exception(-2, "Invalid request, not enough words; expected a method name and a URL");
      } else {
         String relativeUrl = words[1];
         if (relativeUrl.charAt(0) != '/') {
            relativeUrl = "/" + relativeUrl;
         }

         return String.format("%s %s%s", words[0], this._wpRec.getNetworkUrl(), relativeUrl);
      }
   }

   double getLastTimeRef() {
      return this._lastTimeRef;
   }

   double getLastDuration() {
      return this._lastDuration;
   }

   void setLastTimeRef(Integer[] data) {
      double time = (double)((data[0] & 0xFF) + 256 * (data[1] & 0xFF) + 65536 * (data[2] & 0xFF) + 16777216 * (data[3] & 0xFF));
      long ms = (long)((data[4] & 0xFF) * 4);
      if (data.length >= 6) {
         ms += (long)((data[5] & 0xFF) >> 6);
         long freq = (long)data[6].intValue();
         freq += (long)((data[5] & 15) * 256);
         if ((data[5] & 16) != 0) {
            this._lastDuration = (double)freq;
         } else {
            this._lastDuration = (double)freq / 1000.0;
         }
      } else {
         this._lastDuration = 0.0;
      }

      this._lastTimeRef = time + (double)ms / 1000.0;
   }

   YPEntry getModuleYPEntry() {
      return this._moduleYPEntry;
   }

   String getLogRequest() throws YAPI_Exception {
      return !this._logIsPulling && this._logCallback != null ? this.formatRequest("GET logs.txt?pos=" + this._logpos) : null;
   }

   YGenericHub.RequestAsyncResult getLogCallbackHandler() {
      return this._logCallbackHandler;
   }

   void triggerLogPull() {
      this._hub.addDevForLogPull(this);
   }

   void registerLogCallback(YModule.LogCallback callback) {
      this._logCallback = callback;
      if (callback != null) {
         this.triggerLogPull();
      }
   }

   static byte[] formatHTTPUpload(String path, byte[] content) {
      Random randomGenerator = new Random();
      String mp_header = "Content-Disposition: form-data; name=\""
         + path
         + "\"; filename=\"api\"\r\nContent-Type: application/octet-stream\r\nContent-Transfer-Encoding: binary\r\n\r\n";

      String boundary;
      do {
         boundary = String.format("Zz%06xzZ", randomGenerator.nextInt(16777216));
      } while (mp_header.contains(boundary) && YAPIContext._find_in_bytes(content, boundary.getBytes()) >= 0);

      String header_start = "Content-Type: multipart/form-data; boundary=" + boundary + "\r\n\r\n--" + boundary + "\r\n" + mp_header;
      String header_stop = "\r\n--" + boundary + "--\r\n";
      byte[] head_body = new byte[header_start.length() + content.length + header_stop.length()];
      int pos = 0;
      int len = header_start.length();
      System.arraycopy(header_start.getBytes(), 0, head_body, pos, len);
      pos += len;
      len = content.length;
      System.arraycopy(content, 0, head_body, pos, len);
      pos += len;
      len = header_stop.length();
      System.arraycopy(header_stop.getBytes(), 0, head_body, pos, len);
      System.arraycopy(header_stop.getBytes(), 0, head_body, pos, len);
      return head_body;
   }

   int requestHTTPUpload(String path, byte[] content) throws YAPI_Exception {
      String request = "POST /upload.html";
      byte[] head_body = formatHTTPUpload(path, content);
      this.requestHTTPSync(request, head_body);
      return 0;
   }

   byte[] requestHTTPUploadEx(String path, byte[] content) throws YAPI_Exception {
      String request = "POST /upload.html";
      byte[] head_body = formatHTTPUpload(path, content);
      return this.requestHTTPSync(request, head_body);
   }
}
