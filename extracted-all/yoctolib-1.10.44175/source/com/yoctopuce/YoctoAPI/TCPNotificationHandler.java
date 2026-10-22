package com.yoctopuce.YoctoAPI;

import java.util.HashMap;
import java.util.Locale;

class TCPNotificationHandler extends NotificationHandler {
   private volatile boolean _connected = false;
   private volatile boolean _connecting = true;
   private HashMap<YDevice, yHTTPRequest> _httpReqByDev = new HashMap<>();

   TCPNotificationHandler(YHTTPHub hub) {
      super(hub);
   }

   @Override
   String getThreadLabel() {
      return "TCPNotHandler_" + this._hub._http_params.toString();
   }

   @Override
   public void run() {
      yHTTPRequest yreq = new yHTTPRequest(this._hub, "Notification of " + this._hub.getRootUrl());

      while (!Thread.currentThread().isInterrupted()) {
         if (this._error_delay > 0) {
            try {
               Thread.sleep((long)this._error_delay);
            } catch (InterruptedException var7) {
               this._connected = false;
               Thread.currentThread().interrupt();
               return;
            }
         }

         try {
            yreq._requestReserve();
            String notUrl;
            if (this._notifyPos < 0L) {
               notUrl = "GET /not.byn";
            } else {
               notUrl = String.format(Locale.US, "GET /not.byn?abs=%d", this._notifyPos);
            }

            yreq._requestStart(notUrl, null, 0L, null, null);
            this._connected = true;
            this._connecting = false;
            String fifo = "";

            do {
               try {
                  this._hub.testLogPull();
               } catch (InterruptedException var8) {
                  var8.printStackTrace();
                  Thread.currentThread().interrupt();
                  throw new YAPI_Exception(var8.getLocalizedMessage());
               }

               yreq._requestProcesss();
               byte[] partial = yreq.getPartialResult();
               if (partial != null) {
                  fifo = fifo + new String(partial);
               }

               int pos;
               do {
                  pos = fifo.indexOf("\n");
                  if (pos < 0) {
                     break;
                  }

                  String line = fifo.substring(0, pos + 1);
                  if (line.indexOf(27) == -1) {
                     this.handleNetNotification(line);
                  }

                  fifo = fifo.substring(pos + 1);
               } while (pos >= 0);

               this._error_delay = 0;
            } while (!Thread.currentThread().isInterrupted());

            yreq._requestStop();
            yreq._requestRelease();
         } catch (YAPI_Exception var9) {
            this._connected = false;
            yreq._requestStop();
            yreq._requestRelease();
            this._notifRetryCount++;
            this._hub._isNotifWorking = false;
            this._error_delay = 100 << (this._notifRetryCount > 4 ? 4 : this._notifRetryCount);
         }
      }

      yreq._requestStop();
      yreq._requestRelease();
   }

   @Override
   byte[] hubRequestSync(String req_first_line, byte[] req_head_and_body, int mstimeout) throws YAPI_Exception {
      yHTTPRequest req = new yHTTPRequest(this._hub, "request to " + this._hub.getHost());
      return req.RequestSync(req_first_line, req_head_and_body, mstimeout);
   }

   @Override
   byte[] devRequestSync(YDevice device, String req_first_line, byte[] req_head_and_body, int mstimeout, YGenericHub.RequestProgress progress, Object context) throws YAPI_Exception {
      if (!this._httpReqByDev.containsKey(device)) {
         this._httpReqByDev.put(device, new yHTTPRequest(this._hub, "Device " + device.getSerialNumber()));
      }

      yHTTPRequest req = this._httpReqByDev.get(device);
      return req.RequestSync(req_first_line, req_head_and_body, mstimeout);
   }

   @Override
   void devRequestAsync(YDevice device, String req_first_line, byte[] req_head_and_body, YGenericHub.RequestAsyncResult asyncResult, Object asyncContext) throws YAPI_Exception {
      if (!this._httpReqByDev.containsKey(device)) {
         this._httpReqByDev.put(device, new yHTTPRequest(this._hub, "Device " + device.getSerialNumber()));
      }

      yHTTPRequest req = this._httpReqByDev.get(device);
      req.RequestAsync(req_first_line, req_head_and_body, asyncResult, asyncContext);
   }

   @Override
   boolean waitAndFreeAsyncTasks(long timeout) throws InterruptedException {
      for (yHTTPRequest req : this._httpReqByDev.values()) {
         req.WaitRequestEnd(timeout);
      }

      return false;
   }

   @Override
   public boolean isConnected() {
      return this._sendPingNotification ? this._lastPing + 6000L > System.currentTimeMillis() : this._connecting || this._connected;
   }

   @Override
   public boolean hasRwAccess() {
      return this._hub._http_params.getUser().equals("admin");
   }
}
