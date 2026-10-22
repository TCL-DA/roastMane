package com.yoctopuce.YoctoAPI;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;

class YCallbackHub extends YGenericHub {
   private final YGenericHub.HTTPParams _http_params;
   private final OutputStream _out;
   private YJSONObject _callbackCache;

   YCallbackHub(YAPIContext yctx, int idx, YGenericHub.HTTPParams httpParams, InputStream request, OutputStream response) throws YAPI_Exception {
      super(yctx, httpParams, idx, true);
      this._http_params = httpParams;
      this._out = response;
      if (request != null && this._out != null) {
         try {
            this.loadCallbackCache(request);
         } catch (IOException var7) {
            throw new YAPI_Exception(-8, var7.getLocalizedMessage());
         }
      } else {
         throw new YAPI_Exception(-2, "Use RegisterHub(String url, BufferedReader request, PrintWriter response) to start api in callback");
      }
   }

   @Override
   void release() {
   }

   @Override
   String getRootUrl() {
      return this._http_params.getUrl();
   }

   @Override
   synchronized boolean isSameHub(String url, Object request, Object response, Object session) {
      YGenericHub.HTTPParams params = new YGenericHub.HTTPParams(url);
      OutputStream tmp = (OutputStream)response;
      return params.getUrl().equals(this._http_params.getUrl()) && tmp == this._out;
   }

   @Override
   void startNotifications() throws YAPI_Exception {
   }

   @Override
   void stopNotifications() {
   }

   private void _output(String msg) throws IOException {
      this._out.write(msg.getBytes(this._yctx._deviceCharset));
   }

   private void loadCallbackCache(InputStream in) throws YAPI_Exception, IOException {
      ByteArrayOutputStream buffer = new ByteArrayOutputStream();
      byte[] data = new byte[16384];

      int nRead;
      while ((nRead = in.read(data, 0, data.length)) != -1) {
         buffer.write(data, 0, nRead);
      }

      buffer.flush();
      String data_str = buffer.toString(this._yctx._defaultEncoding);
      if (data_str.length() == 0) {
         String errmsg = "RegisterHub(callback) used without posting YoctoAPI data";
         this._output("\n!YoctoAPI:" + errmsg + "\n");
         this._callbackCache = null;
         throw new YAPI_Exception(-8, errmsg);
      } else {
         try {
            this._callbackCache = new YJSONObject(data_str);
            this._callbackCache.parse();
         } catch (Exception var14) {
            String errmsg = "invalid data:[\n" + var14.toString() + data_str + "\n]";
            this._output("\n!YoctoAPI:" + errmsg + "\n");
            this._callbackCache = null;
            throw new YAPI_Exception(-8, errmsg);
         }

         if (!this._http_params.getPass().equals("")) {
            MessageDigest mdigest;
            try {
               mdigest = MessageDigest.getInstance("MD5");
            } catch (NoSuchAlgorithmException var13) {
               throw new YAPI_Exception(-3, "No MD5 provider");
            }

            if (!this._callbackCache.has("sign")) {
               String errmsg = "missing signature from incoming YoctoHub (callback password required)";
               this._output("\n!YoctoAPI:" + errmsg + "\n");
               this._callbackCache = null;
               throw new YAPI_Exception(-12, errmsg);
            }

            String sign = this._callbackCache.getString("sign");
            String pass = this._http_params.getPass();
            String salt;
            if (pass.length() == 32) {
               salt = pass.toLowerCase();
            } else {
               mdigest.reset();
               mdigest.update(pass.getBytes(this._yctx._deviceCharset));
               byte[] md5pass = mdigest.digest();
               salt = YAPIContext._bytesToHexStr(md5pass, 0, md5pass.length).toLowerCase();
            }

            data_str = data_str.replace(sign, salt);
            mdigest.reset();
            mdigest.update(data_str.getBytes(this._yctx._deviceCharset));
            byte[] md5 = mdigest.digest();
            String check = YAPIContext._bytesToHexStr(md5, 0, md5.length).toLowerCase();
            if (!check.equals(sign)) {
               String errmsg = "invalid signature from incoming YoctoHub (invalid callback password)";
               this._output("\n!YoctoAPI:" + errmsg + "\n");
               this._callbackCache = null;
               throw new YAPI_Exception(-12, errmsg);
            }
         }
      }
   }

   private byte[] cachedRequest(String query, byte[] header_and_body) throws YAPI_Exception, IOException {
      int endline = query.indexOf("\r");
      if (endline >= 0) {
         query = query.substring(0, endline);
      }

      if (!query.startsWith("POST ")) {
         if (!query.startsWith("GET ")) {
            return null;
         } else {
            int jzon = query.indexOf("?fw=");
            if (jzon >= 0 && query.indexOf(38, jzon) < 0) {
               query = query.substring(0, jzon);
            }

            if (query.contains("?")
               && !query.contains("/logs.txt")
               && !query.contains("/logger.json")
               && !query.contains("/ping.txt")
               && !query.contains("/files.json?a=dir")) {
               this._output("\n@YoctoAPI:" + query + "\n");
               return "".getBytes(this._yctx._deviceCharset);
            } else {
               try {
                  String[] parts = query.split(" ");
                  String url = parts[1];
                  boolean getmodule = url.contains("api/module.json");
                  if (getmodule) {
                     url = url.replace("api/module.json", "api.json");
                  }

                  if (!this._callbackCache.has(url)) {
                     this._output("\n!YoctoAPI:" + url + " is not preloaded, adding to list");
                     this._output("\n@YoctoAPI:+" + url + "\n");
                     return null;
                  } else {
                     YJSONObject jsonres = this._callbackCache.getYJSONObject(url);
                     if (getmodule) {
                        jsonres = jsonres.getYJSONObject("module");
                     }

                     return jsonres.toJSON().getBytes(this._yctx._deviceCharset);
                  }
               } catch (Exception var9) {
                  return "".getBytes();
               }
            }
         }
      } else {
         String boundary = "???";
         int body_start = YAPIContext._find_in_bytes(header_and_body, "\r\n\r\n".getBytes(this._yctx._deviceCharset)) + 4;
         int endb = body_start;

         while (endb < header_and_body.length && header_and_body[endb] != 13) {
            endb++;
         }

         String tmp = new String(header_and_body, body_start, 2);
         if (tmp.equals("--") && endb > body_start + 2 && endb < body_start + 20) {
            boundary = new String(header_and_body, body_start + 2, endb - body_start - 2);
         }

         int bodylen = header_and_body.length - body_start;
         this._output("\n@YoctoAPI:" + query + " " + Integer.toString(bodylen) + ":" + boundary + "\n");
         this._out.write(header_and_body, body_start, bodylen);
         return "".getBytes(this._yctx._deviceCharset);
      }
   }

   @Override
   synchronized void updateDeviceList(boolean forceupdate) throws YAPI_Exception {
      long now = YAPI.GetTickCount();
      if (forceupdate) {
         this._devListExpires = 0L;
      }

      if (this._devListExpires <= now) {
         String yreq;
         try {
            byte[] data = this.cachedRequest("GET /api.json\r\n", null);
            if (data == null) {
               throw new YAPI_Exception(-8, "no cached request for GET /api.json");
            }

            yreq = new String(data);
         } catch (IOException var18) {
            throw new YAPI_Exception(-8, var18.getLocalizedMessage());
         }

         HashMap<String, ArrayList<YPEntry>> yellowPages = new HashMap<>();
         ArrayList<WPEntry> whitePages = new ArrayList<>();

         try {
            YJSONObject loadval = new YJSONObject(yreq);
            loadval.parse();
            if (!loadval.has("services") || !loadval.getYJSONObject("services").has("whitePages")) {
               throw new YAPI_Exception(-2, "Device " + this._http_params.getHost() + " is not a hub");
            }

            YJSONArray whitePages_json = loadval.getYJSONObject("services").getYJSONArray("whitePages");
            YJSONObject yellowPages_json = loadval.getYJSONObject("services").getYJSONObject("yellowPages");

            for (String classname : yellowPages_json.getKeys()) {
               YJSONArray yprecs_json = yellowPages_json.getYJSONArray(classname);
               ArrayList<YPEntry> yprecs_arr = new ArrayList<>(yprecs_json.length());

               for (int i = 0; i < yprecs_json.length(); i++) {
                  YPEntry yprec = new YPEntry(yprecs_json.getYJSONObject(i));
                  yprecs_arr.add(yprec);
               }

               yellowPages.put(classname, yprecs_arr);
            }

            for (int i = 0; i < whitePages_json.length(); i++) {
               YJSONObject jsonObject = whitePages_json.getYJSONObject(i);
               WPEntry devinfo = new WPEntry(jsonObject);
               int index = jsonObject.getInt("index");
               this._serialByYdx.put(index, devinfo.getSerialNumber());
               whitePages.add(devinfo);
            }
         } catch (Exception var17) {
            throw new YAPI_Exception(-8, "Request failed, could not parse API result for " + this._http_params.getHost(), var17);
         }

         this.updateFromWpAndYp(whitePages, yellowPages);
         now = YAPI.GetTickCount();
         this._devListExpires = now + 500L;
      }
   }

   @Override
   public ArrayList<String> getBootloaders() throws YAPI_Exception {
      throw new YAPI_Exception(-3, "Firmware update is not supported in HTTP callback");
   }

   @Override
   public int ping(int mstimeout) throws YAPI_Exception {
      throw new YAPI_Exception(-3, "Not yet implemented");
   }

   @Override
   boolean isCallbackMode() {
      return true;
   }

   @Override
   boolean isReadOnly() {
      return false;
   }

   @Override
   ArrayList<String> firmwareUpdate(String serial, YFirmwareFile firmware, byte[] settings, YGenericHub.UpdateProgress progress) throws YAPI_Exception, InterruptedException {
      throw new YAPI_Exception(-3, "Firmware update is not supported in HTTP callback");
   }

   @Override
   void devRequestAsync(YDevice device, String req_first_line, byte[] req_head_and_body, YGenericHub.RequestAsyncResult asyncResult, Object asyncContext) throws YAPI_Exception {
      try {
         this.cachedRequest(req_first_line, req_head_and_body);
      } catch (IOException var7) {
         throw new YAPI_Exception(-8, var7.getLocalizedMessage());
      }
   }

   @Override
   byte[] devRequestSync(YDevice device, String req_first_line, byte[] req_head_and_body, YGenericHub.RequestProgress progress, Object context) throws YAPI_Exception {
      try {
         return this.cachedRequest(req_first_line, req_head_and_body);
      } catch (IOException var7) {
         throw new YAPI_Exception(-8, var7.getLocalizedMessage());
      }
   }
}
