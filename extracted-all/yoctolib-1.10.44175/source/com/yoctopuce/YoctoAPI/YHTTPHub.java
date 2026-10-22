package com.yoctopuce.YoctoAPI;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

class YHTTPHub extends YGenericHub {
   public static final int YIO_DEFAULT_TCP_TIMEOUT = 20000;
   private static final int YIO_1_MINUTE_TCP_TIMEOUT = 60000;
   private static final int YIO_10_MINUTES_TCP_TIMEOUT = 600000;
   private final Object _callbackSession;
   private NotificationHandler _notificationHandler;
   private Thread _thread;
   private String _http_realm = "";
   private String _nounce = "";
   private String _serial = "";
   private int _nounce_count = 0;
   private String _ha1 = "";
   private String _opaque = "";
   private Random _randGen = new Random();
   private MessageDigest mdigest;
   private int _authRetryCount = 0;
   private boolean _writeProtected = false;
   private final Object _authLock = new Object();

   boolean needRetryWithAuth() {
      synchronized (this._authLock) {
         return this._http_params.getUser().length() != 0 && this._http_params.getPass().length() != 0 && this._authRetryCount++ <= 3;
      }
   }

   void authSucceded() {
      synchronized (this._authLock) {
         this._authRetryCount = 0;
      }
   }

   void parseWWWAuthenticate(String header) {
      synchronized (this._authLock) {
         int pos = header.indexOf("\r\nWWW-Authenticate:");
         if (pos != -1) {
            header = header.substring(pos + 19);
            int eol = header.indexOf(13);
            if (eol >= 0) {
               header = header.substring(0, eol);
            }

            this._http_realm = "";
            this._nounce = "";
            this._opaque = "";
            this._nounce_count = 0;
            String[] tags = header.split(" ");

            for (String tag : tags) {
               String[] parts = tag.split("[=\",]");
               String name;
               String value;
               if (parts.length == 2) {
                  name = parts[0];
                  value = parts[1];
               } else {
                  if (parts.length != 3) {
                     continue;
                  }

                  name = parts[0];
                  value = parts[2];
               }

               switch (name) {
                  case "realm":
                     this._http_realm = value;
                     break;
                  case "nonce":
                     this._nounce = value;
                     break;
                  case "opaque":
                     this._opaque = value;
               }
            }

            String plaintext = this._http_params.getUser() + ":" + this._http_realm + ":" + this._http_params.getPass();
            this.mdigest.reset();
            this.mdigest.update(plaintext.getBytes());
            byte[] digest = this.mdigest.digest();
            this._ha1 = YAPIContext._bytesToHexStr(digest, 0, digest.length).toLowerCase();
         }
      }
   }

   String getAuthorization(String request) {
      synchronized (this._authLock) {
         if (this._http_params.getUser().length() != 0 && this._http_realm.length() != 0) {
            this._nounce_count++;
            int pos = request.indexOf(32);
            String method = request.substring(0, pos);
            int enduri = request.indexOf(32, pos + 1);
            if (enduri < 0) {
               enduri = request.length();
            }

            String uri = request.substring(pos + 1, enduri);
            String nc = String.format("%08x", this._nounce_count);
            String cnonce = String.format("%08x", this._randGen.nextInt());
            String plaintext = method + ":" + uri;
            this.mdigest.reset();
            this.mdigest.update(plaintext.getBytes());
            byte[] digest = this.mdigest.digest();
            String ha2 = YAPIContext._bytesToHexStr(digest, 0, digest.length).toLowerCase();
            plaintext = this._ha1 + ":" + this._nounce + ":" + nc + ":" + cnonce + ":auth:" + ha2;
            this.mdigest.reset();
            this.mdigest.update(plaintext.getBytes());
            digest = this.mdigest.digest();
            String response = YAPIContext._bytesToHexStr(digest, 0, digest.length).toLowerCase();
            return String.format(
               "Authorization: Digest username=\"%s\", realm=\"%s\", nonce=\"%s\", uri=\"%s\", qop=auth, nc=%s, cnonce=\"%s\", response=\"%s\", opaque=\"%s\"\r\n",
               this._http_params.getUser(),
               this._http_realm,
               this._nounce,
               uri,
               nc,
               cnonce,
               response,
               this._opaque
            );
         } else {
            return "";
         }
      }
   }

   YHTTPHub(YAPIContext yctx, int idx, YGenericHub.HTTPParams httpParams, boolean reportConnnectionLost, Object session) throws YAPI_Exception {
      super(yctx, httpParams, idx, reportConnnectionLost);
      this._callbackSession = session;

      try {
         this.mdigest = MessageDigest.getInstance("MD5");
      } catch (NoSuchAlgorithmException var7) {
         throw new YAPI_Exception(-3, "No MD5 provider");
      }
   }

   @Override
   synchronized void startNotifications() throws YAPI_Exception {
      if (this._notificationHandler != null) {
         throw new YAPI_Exception(-2, "notification already started");
      } else {
         if (this._http_params.isWebSocket()) {
            this._notificationHandler = new WSNotificationHandler(this, this._callbackSession);
         } else {
            this._notificationHandler = new TCPNotificationHandler(this);
         }

         this._thread = new Thread(this._notificationHandler, this._notificationHandler.getThreadLabel());
         this._thread.start();
      }
   }

   @Override
   synchronized void stopNotifications() {
      if (this._notificationHandler != null) {
         try {
            boolean requestsUnfinished = this._notificationHandler.waitAndFreeAsyncTasks(5000L);
            if (requestsUnfinished) {
               this._yctx._Log(String.format("Stop hub %s before all async request has ended", this.getHost()));
            }

            this._thread.interrupt();
            this._thread.join(10000L);
         } catch (InterruptedException var2) {
            this._thread = null;
         }

         this._notificationHandler = null;
      }

      this.removeAllDevices();
   }

   @Override
   synchronized void release() {
   }

   @Override
   String getRootUrl() {
      return this._http_params.getUrl();
   }

   @Override
   boolean isSameHub(String url, Object request, Object response, Object session) {
      YGenericHub.HTTPParams params = new YGenericHub.HTTPParams(url);
      boolean url_equals = params.getUrl(false, false).equals(this._http_params.getUrl(false, false));
      return url_equals && (this._callbackSession == null || this._callbackSession.equals(session));
   }

   @Override
   synchronized void updateDeviceList(boolean forceupdate) throws YAPI_Exception, InterruptedException {
      long now = YAPI.GetTickCount();
      if (forceupdate) {
         this._devListExpires = 0L;
      }

      if (this._devListExpires <= now) {
         if (this._notificationHandler != null && this._notificationHandler.isConnected()) {
            String json_data;
            try {
               json_data = new String(
                  this._notificationHandler.hubRequestSync("GET /api.json", null, this._yctx._networkTimeoutMs), Charset.forName("ISO_8859_1")
               );
            } catch (YAPI_Exception var17) {
               if (this._reportConnnectionLost) {
                  throw var17;
               }

               this.removeAllDevices();
               return;
            }

            HashMap<String, ArrayList<YPEntry>> yellowPages = new HashMap<>();
            ArrayList<WPEntry> whitePages = new ArrayList<>();

            try {
               YJSONObject loadval = new YJSONObject(json_data);
               loadval.parse();
               if (!loadval.has("services") || !loadval.getYJSONObject("services").has("whitePages")) {
                  throw new YAPI_Exception(-2, "Device " + this._http_params.getHost() + " is not a hub");
               }

               this._serial = loadval.getYJSONObject("module").getString("serialNumber");
               YJSONArray whitePages_json = loadval.getYJSONObject("services").getYJSONArray("whitePages");
               YJSONObject yellowPages_json = loadval.getYJSONObject("services").getYJSONObject("yellowPages");
               if (loadval.has("network")) {
                  String adminpass = loadval.getYJSONObject("network").getString("adminPassword");
                  this._writeProtected = adminpass.length() > 0;
               }

               for (String classname : yellowPages_json.getKeys()) {
                  YJSONArray yprecs_json = yellowPages_json.getYJSONArray(classname);
                  ArrayList<YPEntry> yprecs_arr = new ArrayList<>(yprecs_json.length());

                  for (int i = 0; i < yprecs_json.length(); i++) {
                     YPEntry yprec = new YPEntry(yprecs_json.getYJSONObject(i));
                     yprecs_arr.add(yprec);
                  }

                  yellowPages.put(classname, yprecs_arr);
               }

               this._serialByYdx.clear();

               for (int i = 0; i < whitePages_json.length(); i++) {
                  YJSONObject jsonObject = whitePages_json.getYJSONObject(i);
                  WPEntry devinfo = new WPEntry(jsonObject);
                  int index = jsonObject.getInt("index");
                  this._serialByYdx.put(index, devinfo.getSerialNumber());
                  whitePages.add(devinfo);
               }
            } catch (Exception var18) {
               throw new YAPI_Exception(-8, "Request failed, could not parse API result for " + this._http_params.getHost(), var18);
            }

            this.updateFromWpAndYp(whitePages, yellowPages);
            now = YAPI.GetTickCount();
            if (this._isNotifWorking) {
               this._devListExpires = now + this._yctx._deviceListValidityMs;
            } else {
               this._devListExpires = now + 500L;
            }
         } else if (this._reportConnnectionLost) {
            throw new YAPI_Exception(-7, "hub " + this._http_params.getUrl() + " is not reachable");
         } else {
            this.removeAllDevices();
         }
      }
   }

   @Override
   ArrayList<String> firmwareUpdate(String serial, YFirmwareFile firmware, byte[] settings, YGenericHub.UpdateProgress progress) throws YAPI_Exception, InterruptedException {
      boolean use_self_flash = false;
      String baseurl = "";
      boolean need_reboot = true;
      if (this._serial.startsWith("VIRTHUB")) {
         use_self_flash = false;
      } else if (serial.equals(this._serial)) {
         use_self_flash = true;
      } else {
         try {
            this._notificationHandler.hubRequestSync("GET /bySerial/" + serial + "/flash.json?a=state", null, this._yctx._networkTimeoutMs);
            baseurl = "/bySerial/" + serial;
            use_self_flash = true;
         } catch (YAPI_Exception var24) {
         }
      }

      progress.firmware_progress(5, "Enter in bootloader");
      ArrayList<String> bootloaders = this.getBootloaders();
      boolean is_shield = serial.startsWith("YHUBSHL1");

      for (String bl : bootloaders) {
         if (bl.equals(serial)) {
            need_reboot = false;
         } else if (is_shield && bl.startsWith("YHUBSHL1")) {
            throw new YAPI_Exception(-8, "Only one YoctoHub-Shield is allowed in update mode");
         }
      }

      if (!use_self_flash && need_reboot && bootloaders.size() >= 4) {
         throw new YAPI_Exception(-8, "Too many devices in update mode");
      } else {
         byte[] bytes = this._notificationHandler.hubRequestSync("GET " + baseurl + "/flash.json?a=state", null, this._yctx._networkTimeoutMs);
         String uploadstate = new String(bytes);

         try {
            YJSONObject uploadres = new YJSONObject(uploadstate);
            uploadres.parse();
            String state = uploadres.getString("state");
            if (state.equals("uploading") || state.equals("flashing")) {
               throw new YAPI_Exception(-8, "Cannot start firmware update: busy (" + state + ")");
            }
         } catch (Exception var27) {
            throw new YAPI_Exception(-8, "invalid json response :" + var27.getLocalizedMessage());
         }

         progress.firmware_progress(10, "Send firmware file");
         byte[] head_body = YDevice.formatHTTPUpload("firmware", firmware.getData());
         this._notificationHandler.hubRequestSync("POST " + baseurl + "/upload.html", head_body, 0);
         bytes = this._notificationHandler.hubRequestSync("GET " + baseurl + "/flash.json?a=state", null, 600000);
         String uploadresstr = new String(bytes);

         try {
            YJSONObject uploadres = new YJSONObject(uploadresstr);
            uploadres.parse();
            if (!uploadres.getString("state").equals("valid")) {
               throw new YAPI_Exception(-8, "Upload of firmware failed: invalid firmware(" + uploadres.getString("state") + ")");
            }

            if (uploadres.getInt("progress") != 100) {
               throw new YAPI_Exception(-8, "Upload of firmware failed: incomplete upload");
            }
         } catch (Exception var26) {
            throw new YAPI_Exception(-8, "invalid json response :" + var26.getLocalizedMessage());
         }

         if (use_self_flash) {
            byte[] startupConf;
            try {
               String json = new String(settings);
               YJSONObject jsonObject = new YJSONObject(json);
               jsonObject.parse();
               YJSONObject settingsOnly = jsonObject.getYJSONObject("api");
               settingsOnly.remove("services");
               String startupConfStr = settingsOnly.toJSON();
               startupConf = startupConfStr.getBytes();
            } catch (Exception var23) {
               startupConf = new byte[0];
            }

            progress.firmware_progress(20, "Upload startupConf.json");
            head_body = YDevice.formatHTTPUpload("startupConf.json", startupConf);
            this._notificationHandler.hubRequestSync("POST " + baseurl + "/upload.html", head_body, 600000);
            progress.firmware_progress(20, "Upload firmwareConf");
            head_body = YDevice.formatHTTPUpload("firmwareConf", startupConf);
            this._notificationHandler.hubRequestSync("POST " + baseurl + "/upload.html", head_body, 600000);
         }

         if (use_self_flash) {
            progress.firmware_progress(40, "Flash firmware");
            this._notificationHandler
               .hubRequestSync("GET " + baseurl + "/api/module/rebootCountdown?rebootCountdown=-1003", null, this._yctx._networkTimeoutMs);
            Thread.sleep(7000L);
            return null;
         } else {
            if (need_reboot) {
               this._notificationHandler
                  .hubRequestSync("GET /bySerial/" + serial + "/api/module/rebootCountdown?rebootCountdown=-2", null, this._yctx._networkTimeoutMs);
            }

            long timeout = YAPI.GetTickCount() + 20000L;
            boolean found = false;
            progress.firmware_progress(40, "Wait for device to be in bootloader");

            do {
               for (String blx : this.getBootloaders()) {
                  if (blx.equals(serial)) {
                     found = true;
                     break;
                  }
               }

               if (!found) {
                  Thread.sleep(100L);
               }
            } while (!found && YAPI.GetTickCount() < timeout);

            progress.firmware_progress(45, "Flash firmware");
            byte[] res = this._notificationHandler.hubRequestSync("GET /flash.json?a=flash&s=" + serial, null, 600000);

            try {
               String jsonstr = new String(res);
               YJSONObject flashres = new YJSONObject(jsonstr);
               flashres.parse();
               YJSONArray list = flashres.getYJSONArray("logs");
               ArrayList<String> logs = new ArrayList<>(list.length());

               for (int i = 0; i < list.length(); i++) {
                  logs.add(list.getString(i));
               }

               return logs;
            } catch (Exception var25) {
               throw new YAPI_Exception(-8, "invalid response");
            }
         }
      }
   }

   @Override
   synchronized void devRequestAsync(
      YDevice device, String req_first_line, byte[] req_head_and_body, YGenericHub.RequestAsyncResult asyncResult, Object asyncContext
   ) throws YAPI_Exception, InterruptedException {
      if (this._notificationHandler == null || !this._notificationHandler.isConnected()) {
         throw new YAPI_Exception(-7, "hub " + this._http_params.getUrl() + " is not reachable");
      } else if (this._writeProtected && !this._notificationHandler.hasRwAccess()) {
         throw new YAPI_Exception(-12, "Access denied: admin credentials required");
      } else {
         this._notificationHandler.devRequestAsync(device, req_first_line, req_head_and_body, asyncResult, asyncContext);
      }
   }

   @Override
   synchronized byte[] devRequestSync(YDevice device, String req_first_line, byte[] req_head_and_body, YGenericHub.RequestProgress progress, Object context) throws YAPI_Exception, InterruptedException {
      if (this._notificationHandler != null && this._notificationHandler.isConnected()) {
         int tcpTimeout = this._yctx._networkTimeoutMs;
         if (req_first_line.contains("/testcb.txt")
            || req_first_line.contains("/logger.json")
            || req_first_line.contains("/rxmsg.json")
            || req_first_line.contains("/rxdata.bin")
            || req_first_line.contains("/at.txt")
            || req_first_line.contains("/files.json")) {
            tcpTimeout = 60000;
         } else if (req_first_line.contains("/flash.json") || req_first_line.contains("/upload.html")) {
            tcpTimeout = 600000;
         }

         return this._notificationHandler.devRequestSync(device, req_first_line, req_head_and_body, tcpTimeout, progress, context);
      } else {
         throw new YAPI_Exception(-7, "hub " + this._http_params.getUrl() + " is not reachable");
      }
   }

   String getHost() {
      return this._http_params.getHost();
   }

   int getPort() {
      return this._http_params.getPort();
   }

   @Override
   public synchronized ArrayList<String> getBootloaders() throws YAPI_Exception, InterruptedException {
      ArrayList<String> res = new ArrayList<>();
      byte[] raw_data = this._notificationHandler.hubRequestSync("GET /flash.json?a=list", null, this._yctx._networkTimeoutMs);
      String jsonstr = new String(raw_data);

      try {
         YJSONObject flashres = new YJSONObject(jsonstr);
         flashres.parse();
         YJSONArray list = flashres.getYJSONArray("list");

         for (int i = 0; i < list.length(); i++) {
            res.add(list.getString(i));
         }

         return res;
      } catch (Exception var7) {
         throw new YAPI_Exception(-8, "Unable to retrieve bootloader list");
      }
   }

   @Override
   public int ping(int mstimeout) throws YAPI_Exception {
      this.startNotifications();

      try {
         this._notificationHandler.hubRequestSync("GET /api/module/firmwareRelease.json", null, mstimeout);
      } catch (InterruptedException var6) {
         var6.printStackTrace();
      } finally {
         this.stopNotifications();
      }

      return 0;
   }

   @Override
   boolean isCallbackMode() {
      return this._callbackSession != null;
   }

   @Override
   boolean isReadOnly() {
      return this._writeProtected && !this._notificationHandler.hasRwAccess();
   }
}
