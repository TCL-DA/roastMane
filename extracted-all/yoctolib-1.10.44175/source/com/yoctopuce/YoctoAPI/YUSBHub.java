package com.yoctopuce.YoctoAPI;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

class YUSBHub extends YGenericHub {
   private Thread _thread;
   private boolean _processNotifications = false;

   static void SetContextType(Object ctx) throws YAPI_Exception {
      YJniWrapper.Check();
   }

   static void CheckUSBAcces() throws YAPI_Exception {
      YJniWrapper.Check();
   }

   public static String getAPIVersion() {
      try {
         YJniWrapper.Check();
         return " (" + YJniWrapper.getAPIVersion() + ")";
      } catch (YAPI_Exception var1) {
         return "";
      }
   }

   public static String addUdevRule(boolean force) {
      try {
         YJniWrapper.Check();
         String res = YJniWrapper.addUdevRule(force ? 1 : 0);
         return res.length() > 0 ? "error: " + res : res;
      } catch (YAPI_Exception var2) {
         return "error" + var2.getLocalizedMessage();
      }
   }

   @Override
   boolean isCallbackMode() {
      return false;
   }

   @Override
   boolean isReadOnly() {
      return false;
   }

   @Override
   String getSerialNumber() {
      return "";
   }

   @Override
   public String get_urlOf(String serialNumber) {
      return "usb";
   }

   @Override
   public ArrayList<String> get_subDeviceOf(String serialNumber) {
      return new ArrayList<>();
   }

   YUSBHub(YAPIContext yctx, int idx, boolean requestPermission, int pktAckDelay) throws YAPI_Exception {
      super(yctx, new YGenericHub.HTTPParams("usb://"), idx, true);
      YJniWrapper.reserveUSBAccess();
   }

   @Override
   public void startNotifications() {
      YJniWrapper.startNotifications(this);
      this._processNotifications = true;
      this._thread = new Thread(new Runnable() {
         @Override
         public void run() {
            while (YUSBHub.this._processNotifications) {
               YJniWrapper.usbProcess(YUSBHub.this);

               try {
                  Thread.sleep(10L);
               } catch (InterruptedException var2) {
                  break;
               }
            }
         }
      });
      this._thread.start();
   }

   @Override
   public void stopNotifications() {
      this._processNotifications = false;
      this._thread.interrupt();
      YJniWrapper.stopNotifications();
      this.removeAllDevices();
   }

   @Override
   void release() {
      YJniWrapper.releaseUSBAccess();
   }

   @Override
   void updateDeviceList(boolean forceupdate) throws YAPI_Exception {
      long now = YAPI.GetTickCount();
      if (forceupdate) {
         this._devListExpires = 0L;
      }

      if (this._devListExpires <= now) {
         ArrayList<YPEntry> functions = new ArrayList<>();
         ArrayList<WPEntry> whitePages = new ArrayList<>();
         HashMap<String, ArrayList<YPEntry>> yellowPages = new HashMap<>();
         YJniWrapper.updateDeviceList(whitePages, functions);

         for (YPEntry yp : functions) {
            String classname = yp.getClassname();
            if (!yellowPages.containsKey(classname)) {
               yellowPages.put(classname, new ArrayList<>());
            }

            yellowPages.get(classname).add(yp);
         }

         for (int i = 0; i < whitePages.size(); i++) {
            this._serialByYdx.put(i, whitePages.get(i).getSerialNumber());
         }

         this.updateFromWpAndYp(whitePages, yellowPages);
         now = YAPI.GetTickCount();
         this._devListExpires = now + 500L;
      }
   }

   @Override
   public ArrayList<String> getBootloaders() throws YAPI_Exception {
      return YJniWrapper.getBootloaders();
   }

   @Override
   public int ping(int mstimeout) throws YAPI_Exception {
      return 0;
   }

   @Override
   ArrayList<String> firmwareUpdate(String serial, YFirmwareFile firmware, byte[] settings, YGenericHub.UpdateProgress progress) throws YAPI_Exception {
      throw new YAPI_Exception(-3, "Firmware update on USB with JAVA is not yet available");
   }

   @Override
   void devRequestAsync(YDevice device, String req_first_line, byte[] req_head_and_body, YGenericHub.RequestAsyncResult asyncResult, Object asyncContext) throws YAPI_Exception {
      byte[] currentRequest = this.prepareRequest(req_first_line, req_head_and_body);
      YJniWrapper.devRequestAsync(device.getSerialNumber(), currentRequest, asyncResult, asyncContext);
   }

   @Override
   byte[] devRequestSync(YDevice device, String req_first_line, byte[] req_head_and_body, YGenericHub.RequestProgress progress, Object context) throws YAPI_Exception {
      byte[] currentRequest = this.prepareRequest(req_first_line, req_head_and_body);
      byte[] result = YJniWrapper.devRequestSync(device.getSerialNumber(), currentRequest);
      int hpos = YAPIContext._find_in_bytes(result, "\r\n\r\n".getBytes());
      return hpos >= 0 ? Arrays.copyOfRange(result, hpos + 4, result.length) : result;
   }

   private byte[] prepareRequest(String firstLine, byte[] rest_of_request) {
      byte[] currentRequest;
      if (rest_of_request == null) {
         currentRequest = (firstLine + "\r\n\r\n").getBytes();
      } else {
         firstLine = firstLine + "\r\n";
         int len = firstLine.length() + rest_of_request.length;
         currentRequest = new byte[len];
         System.arraycopy(firstLine.getBytes(), 0, currentRequest, 0, len);
         System.arraycopy(rest_of_request, 0, currentRequest, firstLine.length(), rest_of_request.length);
      }

      return currentRequest;
   }

   @Override
   public String getRootUrl() {
      return "usb";
   }

   @Override
   boolean isSameHub(String url, Object request, Object response, Object session) {
      return url.equals("usb");
   }

   public static boolean RegisterLocalhost() {
      return true;
   }
}
