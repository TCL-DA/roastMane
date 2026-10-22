package com.yoctopuce.YoctoAPI;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;

abstract class YGenericHub {
   static final int NOTIFY_V2_LEGACY = 0;
   static final int NOTIFY_V2_6RAWBYTES = 1;
   static final int NOTIFY_V2_TYPEDDATA = 2;
   static final int NOTIFY_V2_FLUSHGROUP = 3;
   static final int YSTREAM_EMPTY = 0;
   static final int YSTREAM_TCP = 1;
   static final int YSTREAM_TCP_CLOSE = 2;
   static final int YSTREAM_NOTICE = 3;
   static final int YSTREAM_REPORT = 4;
   static final int YSTREAM_META = 5;
   static final int YSTREAM_REPORT_V2 = 6;
   static final int YSTREAM_NOTICE_V2 = 7;
   static final int YSTREAM_TCP_NOTIF = 8;
   static final int YSTREAM_TCP_ASYNCCLOSE = 9;
   static final int USB_META_UTCTIME = 1;
   static final int USB_META_DLFLUSH = 2;
   static final int USB_META_ACK_D2H_PACKET = 3;
   static final int USB_META_WS_ANNOUNCE = 4;
   static final int USB_META_WS_AUTHENTICATION = 5;
   static final int USB_META_WS_ERROR = 6;
   static final int USB_META_ACK_UPLOAD = 7;
   static final int USB_META_UTCTIME_SIZE = 6;
   static final int USB_META_DLFLUSH_SIZE = 1;
   static final int USB_META_ACK_D2H_PACKET_SIZE = 2;
   static final int USB_META_WS_ANNOUNCE_SIZE = 28;
   static final int USB_META_WS_AUTHENTICATION_SIZE = 28;
   static final int USB_META_WS_ERROR_SIZE = 6;
   static final int USB_META_ACK_UPLOAD_SIZE = 6;
   static final int USB_META_WS_PROTO_V1 = 1;
   static final int USB_META_WS_PROTO_V2 = 2;
   static final int VERSION_SUPPORT_ASYNC_CLOSE = 1;
   static final int USB_META_WS_VALID_SHA1 = 1;
   static final int USB_META_WS_AUTH_FLAGS_RW = 2;
   private static final int PUBVAL_LEGACY = 0;
   private static final int PUBVAL_1RAWBYTE = 1;
   private static final int PUBVAL_2RAWBYTES = 2;
   private static final int PUBVAL_3RAWBYTES = 3;
   private static final int PUBVAL_4RAWBYTES = 4;
   private static final int PUBVAL_5RAWBYTES = 5;
   private static final int PUBVAL_6RAWBYTES = 6;
   private static final int PUBVAL_C_LONG = 7;
   private static final int PUBVAL_C_FLOAT = 8;
   private static final int PUBVAL_YOCTO_FLOAT_E3 = 9;
   private static final int PUBVAL_YOCTO_FLOAT_E6 = 10;
   static final long YPROG_BOOTLOADER_TIMEOUT = 20000L;
   final YAPIContext _yctx;
   final YGenericHub.HTTPParams _http_params;
   int _hubidx;
   protected long _notifyTrigger = 0L;
   protected Object _notifyHandle = null;
   volatile boolean _isNotifWorking = false;
   long _devListExpires = 0L;
   final ConcurrentHashMap<Integer, String> _serialByYdx = new ConcurrentHashMap<>();
   private HashMap<String, YDevice> _devices = new HashMap<>();
   final boolean _reportConnnectionLost;
   private String _hubSerialNumber = null;
   private HashMap<String, Integer> _beaconss = new HashMap<>();
   private final BlockingQueue<YDevice> _logPullList = new LinkedBlockingDeque<>();

   YGenericHub(YAPIContext yctx, YGenericHub.HTTPParams httpParams, int idx, boolean reportConnnectionLost) {
      this._yctx = yctx;
      this._hubidx = idx;
      this._reportConnnectionLost = reportConnnectionLost;
      this._http_params = httpParams;
   }

   abstract void release();

   abstract String getRootUrl();

   abstract boolean isSameHub(String var1, Object var2, Object var3, Object var4);

   abstract void startNotifications() throws YAPI_Exception;

   abstract void stopNotifications();

   static String decodePubVal(int typeV2, byte[] funcval, int ofs, int funcvallen) {
      String buffer = "";
      if (typeV2 == 1 || typeV2 == 2) {
         int funcValType;
         if (typeV2 == 1) {
            funcValType = 6;
         } else {
            funcValType = funcval[ofs++] & 255;
         }

         switch (funcValType) {
            case 0:
               break;
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
               for (int i = 0; i < funcValType; i++) {
                  int c = funcval[ofs++] & 255;
                  int b = c >> 4;
                  buffer = buffer + (b > 9 ? b + 97 - 10 : b + 48);
                  b = c & 15;
                  buffer = buffer + (b > 9 ? b + 97 - 10 : b + 48);
               }

               return buffer;
            case 7:
            case 9:
               int numVal = funcval[ofs++] & 255;
               numVal += (funcval[ofs++] & 255) << 8;
               numVal += (funcval[ofs++] & 255) << 16;
               numVal += (funcval[ofs++] & 255) << 24;
               if (funcValType == 7) {
                  return String.format(Locale.US, "%d", numVal);
               }

               buffer = String.format(Locale.US, "%.3f", (double)numVal / 1000.0);
               int endp = buffer.length();

               while (endp > 0 && buffer.charAt(endp - 1) == '0') {
                  endp--;
               }

               if (endp > 0 && buffer.charAt(endp - 1) == '.') {
                  buffer = buffer.substring(0, --endp);
               }

               return buffer;
            case 8:
               float floatVal = ByteBuffer.wrap(funcval).order(ByteOrder.LITTLE_ENDIAN).getFloat();
               buffer = String.format(Locale.US, "%.6f", floatVal);
               int endp = buffer.length();

               while (endp > 0 && buffer.charAt(endp - 1) == '0') {
                  endp--;
               }

               if (endp > 0 && buffer.charAt(endp - 1) == '.') {
                  buffer = buffer.substring(0, --endp);
               }

               return buffer;
            default:
               return "?";
         }
      }

      int len = 0;

      while (len < 6 && len < funcvallen && funcval[len + ofs] != 0) {
         len++;
      }

      return new String(funcval, ofs, len);
   }

   void removeAllDevices() {
      HashMap<String, ArrayList<YPEntry>> yellowPages = new HashMap<>();
      ArrayList<WPEntry> whitePages = new ArrayList<>();

      try {
         this.updateFromWpAndYp(whitePages, yellowPages);
      } catch (YAPI_Exception var4) {
         var4.printStackTrace();
      }
   }

   void updateFromWpAndYp(ArrayList<WPEntry> whitePages, HashMap<String, ArrayList<YPEntry>> yellowPages) throws YAPI_Exception {
      ArrayList<YDevice> toRemove = new ArrayList<>(this._devices.values());

      for (WPEntry wp : whitePages) {
         String serial = wp.getSerialNumber();
         if (this._devices.containsKey(serial)) {
            YDevice currdev = this._devices.get(serial);
            if (!currdev.getLogicalName().equals(wp.getLogicalName())) {
               currdev.refresh();
               this._yctx._pushPlugEvent(YAPIContext.PlugEvent.Event.CHANGE, serial);
            } else if (currdev.getBeacon() > 0 != wp.getBeacon() > 0) {
               currdev.refresh();
            }

            toRemove.remove(currdev);
         } else {
            YDevice dev = new YDevice(this, wp, yellowPages);
            this._yctx._yHash.reindexDevice(dev);
            this._devices.put(serial, dev);
            this._yctx._pushPlugEvent(YAPIContext.PlugEvent.Event.PLUG, serial);
            this._yctx._Log("HUB: device " + serial + " has been plugged\n");
         }
      }

      for (YDevice dev : toRemove) {
         String serial = dev.getSerialNumber();
         this._yctx._pushPlugEvent(YAPIContext.PlugEvent.Event.UNPLUG, serial);
         this._yctx._Log("HUB: device " + serial + " has been unplugged\n");
         this._devices.remove(serial);
         this._yctx._yHash.forgetDevice(serial);
      }

      if (this._hubSerialNumber == null) {
         for (WPEntry wpx : whitePages) {
            if (wpx.getNetworkUrl().equals("")) {
               this._hubSerialNumber = wpx.getSerialNumber();
            }
         }
      }

      this._yctx._yHash.reindexYellowPages(yellowPages);
   }

   String getSerialNumber() {
      return this._hubSerialNumber;
   }

   public String get_urlOf(String serialNumber) {
      for (YDevice dev : this._devices.values()) {
         String devSerialNumber = dev.getSerialNumber();
         if (devSerialNumber.equals(serialNumber)) {
            return this._http_params.getUrl(true, false) + dev.getNetworkUrl();
         }
      }

      return this._http_params.getUrl(true, false);
   }

   public ArrayList<String> get_subDeviceOf(String serialNumber) {
      ArrayList<String> res = new ArrayList<>();

      for (YDevice dev : this._devices.values()) {
         String devSerialNumber = dev.getSerialNumber();
         if (devSerialNumber.equals(serialNumber) && !dev.getNetworkUrl().equals("")) {
            res.clear();
            return res;
         }

         res.add(devSerialNumber);
      }

      return res;
   }

   void handleValueNotification(String serial, String funcid, String value) {
      String hwid = serial + "." + funcid;
      this._yctx._yHash.setFunctionValue(hwid, value);
      YFunction conn_fn = this._yctx._GetValueCallback(hwid);
      if (conn_fn != null) {
         this._yctx._PushDataEvent(new YAPIContext.DataEvent(conn_fn, value));
      }
   }

   void handleConfigChangeNotification(String serial) {
      YModule module = this._yctx._GetModuleCallack(serial);
      if (module != null) {
         this._yctx._PushDataEvent(new YAPIContext.DataEvent(module));
      }
   }

   void handleBeaconNotification(String serial, String logicalName, int beacon) {
      if (!this._beaconss.containsKey(serial) || this._beaconss.get(serial) != beacon) {
         this._beaconss.put(serial, beacon);
         YModule module = this._yctx._GetModuleCallack(serial);
         if (module != null) {
            this._yctx._PushDataEvent(new YAPIContext.DataEvent(module, beacon));
         }
      }
   }

   protected void handleTimedNotification(String serial, String funcid, double time, double duration, byte[] report) {
      ArrayList<Integer> arrayList = new ArrayList<>(report.length);

      for (byte b : report) {
         int i = b & 255;
         arrayList.add(i);
      }

      this.handleTimedNotification(serial, funcid, time, duration, arrayList);
   }

   void handleTimedNotification(String serial, String funcid, double time, double duration, ArrayList<Integer> report) {
      String hwid = serial + "." + funcid;
      YFunction func = this._yctx._GetTimedReportCallback(hwid);
      if (func != null) {
         this._yctx._PushDataEvent(new YAPIContext.DataEvent(func, time, duration, report));
      }
   }

   abstract void updateDeviceList(boolean var1) throws YAPI_Exception, InterruptedException;

   public abstract ArrayList<String> getBootloaders() throws YAPI_Exception, InterruptedException;

   abstract int ping(int var1) throws YAPI_Exception;

   public static String getAPIVersion() {
      return "";
   }

   abstract boolean isCallbackMode();

   abstract boolean isReadOnly();

   abstract ArrayList<String> firmwareUpdate(String var1, YFirmwareFile var2, byte[] var3, YGenericHub.UpdateProgress var4) throws YAPI_Exception, InterruptedException;

   abstract void devRequestAsync(YDevice var1, String var2, byte[] var3, YGenericHub.RequestAsyncResult var4, Object var5) throws YAPI_Exception, InterruptedException;

   abstract byte[] devRequestSync(YDevice var1, String var2, byte[] var3, YGenericHub.RequestProgress var4, Object var5) throws YAPI_Exception, InterruptedException;

   void addDevForLogPull(YDevice yDevice) {
      this._logPullList.offer(yDevice);
   }

   void testLogPull() throws InterruptedException {
      YDevice device = this._logPullList.poll();
      if (device != null) {
         try {
            String logRequest = device.getLogRequest();
            if (logRequest == null) {
               return;
            }

            YGenericHub.RequestAsyncResult logCallbackHandler = device.getLogCallbackHandler();
            this.devRequestAsync(device, logRequest, null, logCallbackHandler, null);
         } catch (YAPI_Exception var4) {
            var4.printStackTrace();
         }
      }
   }

   static class HTTPParams {
      private final String _host;
      private final int _port;
      private final String _user;
      private final String _pass;
      private final String _proto;
      private final String _subDomain;

      public HTTPParams(String url) {
         int pos = 0;
         if (url.startsWith("http://")) {
            pos = 7;
            this._proto = "http";
         } else if (url.startsWith("wss://")) {
            pos = 6;
            this._proto = "wss";
         } else if (url.startsWith("usb://")) {
            pos = 6;
            this._proto = "usb";
         } else {
            if (url.startsWith("ws://")) {
               pos = 5;
            }

            this._proto = "ws";
         }

         int end_auth = url.indexOf(64, pos);
         int end_user = url.indexOf(58, pos);
         if (end_auth >= 0 && end_user >= 0 && end_user < end_auth) {
            this._user = url.substring(pos, end_user);
            this._pass = url.substring(end_user + 1, end_auth);
            pos = end_auth + 1;
         } else {
            this._user = "";
            this._pass = "";
         }

         if (url.length() > pos && url.charAt(pos) == '@') {
            pos++;
         }

         int end_url = url.indexOf(47, pos);
         if (end_url < 0) {
            end_url = url.length();
            this._subDomain = "";
         } else {
            int next_slash = url.indexOf("/", end_url + 1);
            if (next_slash < 0) {
               next_slash = url.length();
            }

            this._subDomain = url.substring(end_url, next_slash);
         }

         int portpos = url.indexOf(58, pos);
         if (portpos > 0) {
            if (portpos + 1 < end_url) {
               this._host = url.substring(pos, portpos);
               this._port = Integer.parseInt(url.substring(portpos + 1, end_url));
            } else {
               this._host = url.substring(pos, portpos);
               this._port = 4444;
            }
         } else {
            this._host = url.substring(pos, end_url);
            this._port = 4444;
         }
      }

      String getHost() {
         return this._host;
      }

      String getPass() {
         return this._pass;
      }

      int getPort() {
         return this._port;
      }

      String getUser() {
         return this._user;
      }

      String getUrl() {
         return this.getUrl(false, true);
      }

      String getUrl(boolean withProto, boolean withUserPass) {
         StringBuilder url = new StringBuilder();
         if (withProto) {
            url.append(this._proto).append("://");
         }

         if (withUserPass && !this._user.equals("")) {
            url.append(this._user);
            if (!this._pass.equals("")) {
               url.append(":");
               url.append(this._pass);
            }

            url.append("@");
         }

         url.append(this._host);
         url.append(":");
         url.append(this._port);
         url.append(this._subDomain);
         return url.toString();
      }

      boolean isWebSocket() {
         return this._proto.startsWith("ws");
      }

      String getSubDomain() {
         return this._subDomain;
      }

      boolean hasAuthParam() {
         return !this._user.equals("");
      }

      boolean useSecureSocket() {
         return "wss".equals(this._proto);
      }

      @Override
      public String toString() {
         return this._proto + "/" + this._host + ':' + this._port + this._subDomain;
      }
   }

   interface RequestAsyncResult {
      void RequestAsyncDone(Object var1, byte[] var2, int var3, String var4);
   }

   interface RequestProgress {
      void requestProgressUpdate(Object var1, int var2, int var3);
   }

   interface UpdateProgress {
      void firmware_progress(int var1, String var2);
   }
}
