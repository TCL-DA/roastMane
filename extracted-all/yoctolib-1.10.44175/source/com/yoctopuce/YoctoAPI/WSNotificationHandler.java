package com.yoctopuce.YoctoAPI;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

class WSNotificationHandler extends NotificationHandler implements WSHandlerInterface.WSHandlerResponseInterface {
   private static final int NB_TCP_CHANNEL = 4;
   private static final int HUB_TCP_CHANNEL = 0;
   private static final int DEVICE_TCP_CHANNEL = 0;
   private static final int WS_REQUEST_MAX_DURATION = 50000;
   private static final int DEFAULT_TCP_ROUND_TRIP_TIME = 30;
   private static final int DEFAULT_TCP_MAX_WINDOW_SIZE = 262144;
   private final ExecutorService _executorService;
   private MessageDigest _sha1 = null;
   private MessageDigest _md5 = null;
   private WSHandlerInterface _wsHandler;
   private final BlockingQueue<WSRequest> _pendingRequests = new LinkedBlockingQueue<>();
   private final ArrayList<ArrayList<WSRequest>> _workingRequests;
   private final Object _stateLock = new Object();
   private volatile boolean _waitingForConnectionState;
   private volatile boolean _muststop;
   private long _connectionTime = 0L;
   private WSNotificationHandler.ConnectionState _connectionState = WSNotificationHandler.ConnectionState.CONNECTING;
   private int _remoteVersion = 0;
   private String _remoteSerial;
   private long _remoteNouce;
   private int _nounce;
   private volatile int _session_errno;
   private volatile String _session_error;
   private boolean _rwAccess = false;
   private long _tcpRoundTripTime = 30L;
   private int _tcpMaxWindowSize = 262144;
   private final int[] _lastUploadAckBytes = new int[4];
   private final long[] _lastUploadAckTime = new long[4];
   private final int[] _lastUploadRateBytes = new int[4];
   private final long[] _lastUploadRateTime = new long[4];
   private int _uploadRate = 0;
   private byte _nextAsyncId = 48;
   private long _next_transmit_tm = 0L;
   private final Object _sendLock = new Object();
   private int _notifAbsPos;
   private final StringBuilder _notificationsFifo = new StringBuilder();

   WSNotificationHandler(YHTTPHub hub, Object session) {
      super(hub);
      if (session != null) {
         this._wsHandler = new WSHandlerJEE(this, session);
      } else {
         this._wsHandler = new WSHandlerYocto(this);
      }

      this._workingRequests = new ArrayList<>(4);

      for (int i = 0; i < 4; i++) {
         this._workingRequests.add(i, new ArrayList<>());
      }

      this._executorService = Executors.newFixedThreadPool(1);
      this._muststop = false;

      try {
         this._sha1 = MessageDigest.getInstance("SHA-1");
         this._md5 = MessageDigest.getInstance("MD5");
      } catch (NoSuchAlgorithmException var4) {
         var4.printStackTrace();
      }
   }

   @Override
   public void run() {
      this._waitingForConnectionState = true;

      do {
         if (this._error_delay > 0) {
            try {
               Thread.sleep((long)this._error_delay);
            } catch (InterruptedException var6) {
               break;
            }
         }

         synchronized (this._stateLock) {
            this._connectionState = WSNotificationHandler.ConnectionState.CONNECTING;
         }

         try {
            this._wsHandler.connect(this._hub, this._waitingForConnectionState, 10000, this._notifAbsPos);
            this.runOnSession();
         } catch (YAPI_Exception var7) {
            if (var7.errorType == -2) {
               this._muststop = true;
            }

            this._session_errno = -8;
            this._session_error = var7.getLocalizedMessage();
         }

         this._waitingForConnectionState = true;
         this._notifRetryCount++;
         this._hub._isNotifWorking = false;
         this._error_delay = 100 << (this._notifRetryCount > 4 ? 4 : this._notifRetryCount);
      } while (!Thread.currentThread().isInterrupted() && !this._muststop && !this._wsHandler.isCallback());

      this._wsHandler.close();
      synchronized (this._stateLock) {
         this._connectionState = WSNotificationHandler.ConnectionState.DEAD;
         if (this._session_errno == 0) {
            this._session_errno = -8;
            this._session_error = "WS Session is closed";
         }
      }
   }

   private void runOnSession() {
      if (!this._wsHandler.isOpen()) {
         this.WSLOG("WebSocket is not open");
      } else {
         String errmsg = "WebSocket session is closed";

         try {
            long timeout = System.currentTimeMillis() + 10000L;
            synchronized (this._stateLock) {
               while (this._connectionState == WSNotificationHandler.ConnectionState.CONNECTING && !this._muststop) {
                  this._stateLock.wait(1000L);
                  if (timeout < System.currentTimeMillis()) {
                     this.WSLOG("YoctoHub did not send any data for 10 secs");
                     this._connectionState = WSNotificationHandler.ConnectionState.DISCONNECTED;
                     this._stateLock.notifyAll();
                     return;
                  }
               }
            }

            while (!Thread.currentThread().isInterrupted() && !this._muststop && this._wsHandler.isOpen()) {
               long now = YAPI.GetTickCount();
               long wait;
               if (this._next_transmit_tm >= now) {
                  wait = this._next_transmit_tm - now;
               } else {
                  wait = 1000L;
               }

               WSRequest request = this._pendingRequests.poll(wait, TimeUnit.MILLISECONDS);
               if (request != null) {
                  if (request.getState().equals(WSRequest.State.FAKE_REQUEST)) {
                     break;
                  }

                  synchronized (this._workingRequests) {
                     request.reportStartOfProcess();
                     this._workingRequests.get(request.getChannel()).add(request);
                  }
               }

               this.processRequests();
               this._hub.testLogPull();
            }
         } catch (Exception var18) {
            errmsg = var18.getLocalizedMessage();
         }

         try {
            for (WSRequest wsRequest = this._pendingRequests.poll(10L, TimeUnit.MILLISECONDS);
               wsRequest != null;
               wsRequest = this._pendingRequests.poll(10L, TimeUnit.MILLISECONDS)
            ) {
               wsRequest.setError(-8, errmsg);
            }
         } catch (InterruptedException var16) {
         }

         synchronized (this._workingRequests) {
            for (int i = 0; i < 4; i++) {
               for (WSRequest request : this._workingRequests.get(i)) {
                  request.setError(-8, errmsg);
               }

               this._workingRequests.get(i).clear();
            }
         }

         synchronized (this._stateLock) {
            this._connectionState = WSNotificationHandler.ConnectionState.DISCONNECTED;
            this._stateLock.notifyAll();
         }
      }
   }

   private WSRequest sendRequest(
      String req_first_line, byte[] req_head_and_body, int tcpchanel, boolean async, YGenericHub.RequestProgress progress, Object context, long expiration
   ) throws YAPI_Exception, InterruptedException {
      byte[] full_request;
      if (req_head_and_body == null) {
         req_first_line = req_first_line + "\r\n\r\n";
         byte[] req_first_lineBytes = req_first_line.getBytes();
         full_request = req_first_lineBytes;
      } else {
         req_first_line = req_first_line + "\r\n";
         byte[] req_first_lineBytes = req_first_line.getBytes();
         full_request = new byte[req_first_lineBytes.length + req_head_and_body.length];
         System.arraycopy(req_first_lineBytes, 0, full_request, 0, req_first_lineBytes.length);
         System.arraycopy(req_head_and_body, 0, full_request, req_first_lineBytes.length, req_head_and_body.length);
      }

      long timeout = System.currentTimeMillis() + 50000L;
      if (timeout < expiration) {
         expiration = timeout;
      }

      WSRequest request;
      synchronized (this._stateLock) {
         while (this._connectionState != WSNotificationHandler.ConnectionState.CONNECTED && this._connectionState != WSNotificationHandler.ConnectionState.DEAD) {
            long delay = expiration - System.currentTimeMillis();
            if (delay <= 0L) {
               if (this._connectionState != WSNotificationHandler.ConnectionState.CONNECTED
                  && this._connectionState != WSNotificationHandler.ConnectionState.CONNECTING) {
                  throw new YAPI_Exception(-8, "IO error with hub");
               }

               if (this._session_errno != 0) {
                  throw new YAPI_Exception(this._session_errno, "Unable to start the request in time (" + this._session_error + ")");
               }

               throw new YAPI_Exception(-7, "Unable to start the request in time");
            }

            this._stateLock.wait(delay);
         }

         if (this._connectionState == WSNotificationHandler.ConnectionState.DEAD) {
            throw new YAPI_Exception(this._session_errno, this._session_error);
         }

         if (async) {
            request = new WSRequest(tcpchanel, this._nextAsyncId++, full_request, expiration);
            if (this._nextAsyncId >= 127) {
               this._nextAsyncId = 48;
            }
         } else {
            request = new WSRequest(tcpchanel, full_request, expiration, progress, context);
         }
      }

      this._pendingRequests.put(request);
      return request;
   }

   private byte[] getRequestResponse(WSRequest wsRequest, long expiration) throws YAPI_Exception, InterruptedException {
      WSRequest.State state = wsRequest.waitProcessingEnd(expiration);
      if (!state.equals(WSRequest.State.CLOSED)) {
         wsRequest.checkError();
         throw new YAPI_Exception(-7, "request did not finished correctly");
      } else {
         byte[] full_result = wsRequest.getResponseBytes();
         int okpos = YAPIContext._find_in_bytes(full_result, "OK".getBytes());
         if (okpos != 0) {
            okpos = YAPIContext._find_in_bytes(full_result, "HTTP/1.1 ".getBytes());
            int endl = YAPIContext._find_in_bytes(full_result, "\r\n".getBytes());
            if (okpos == 0 && endl > 8) {
               String line = new String(full_result, 9, endl - 9);
               String[] parts = line.trim().split(" ");
               if (parts[0].equals("401")) {
                  throw new YAPI_Exception(-12, "Authentication required");
               }

               throw new YAPI_Exception(line.trim());
            }
         }

         int hpos = YAPIContext._find_in_bytes(full_result, "\r\n\r\n".getBytes());
         return hpos >= 0 ? Arrays.copyOfRange(full_result, hpos + 4, full_result.length) : full_result;
      }
   }

   @Override
   String getThreadLabel() {
      return this._wsHandler.getThreadLabel() + "_" + this._hub._http_params.toString();
   }

   @Override
   public byte[] hubRequestSync(String req_first_line, byte[] req_head_and_body, int mstimeout) throws YAPI_Exception, InterruptedException {
      if (mstimeout == 0) {
         mstimeout = 86400000;
      }

      long expiration = System.currentTimeMillis() + (long)mstimeout;
      WSRequest wsRequest = this.sendRequest(req_first_line, req_head_and_body, 0, false, null, null, expiration);
      return this.getRequestResponse(wsRequest, expiration);
   }

   @Override
   byte[] devRequestSync(YDevice device, String req_first_line, byte[] req_head_and_body, int mstimeout, YGenericHub.RequestProgress progress, Object context) throws YAPI_Exception, InterruptedException {
      if (mstimeout == 0) {
         mstimeout = 86400000;
      }

      long expiration = System.currentTimeMillis() + (long)mstimeout;
      WSRequest wsRequest = this.sendRequest(req_first_line, req_head_and_body, 0, false, progress, context, expiration);
      return this.getRequestResponse(wsRequest, expiration);
   }

   @Override
   void devRequestAsync(
      YDevice device, String req_first_line, byte[] req_head_and_body, final YGenericHub.RequestAsyncResult asyncResult, final Object asyncContext
   ) throws YAPI_Exception, InterruptedException {
      final long expiration = System.currentTimeMillis() + 50000L;
      final WSRequest wsRequest = this.sendRequest(req_first_line, req_head_and_body, 0, true, null, null, expiration);
      this._executorService.execute(new Runnable() {
         @Override
         public void run() {
            byte[] response = null;
            int error_code = 0;
            String errmsg = null;

            try {
               response = WSNotificationHandler.this.getRequestResponse(wsRequest, expiration);
            } catch (YAPI_Exception var5) {
               error_code = var5.errorType;
               errmsg = var5.getLocalizedMessage();
            } catch (InterruptedException var6) {
               error_code = -8;
               errmsg = var6.getLocalizedMessage();
            }

            if (asyncResult != null) {
               asyncResult.RequestAsyncDone(asyncContext, response, error_code, errmsg);
            }
         }
      });
   }

   @Override
   boolean waitAndFreeAsyncTasks(long timeout) throws InterruptedException {
      this._executorService.shutdown();
      boolean allTerminated = this._executorService.awaitTermination(timeout, TimeUnit.MILLISECONDS);
      this._muststop = true;
      this._wsHandler.close();
      return !allTerminated;
   }

   @Override
   public boolean isConnected() {
      if (this._sendPingNotification) {
         return this._lastPing + 6000L > System.currentTimeMillis();
      } else {
         synchronized (this._stateLock) {
            return this._connectionState == WSNotificationHandler.ConnectionState.CONNECTED
               || this._connectionState == WSNotificationHandler.ConnectionState.AUTHENTICATING
               || this._connectionState == WSNotificationHandler.ConnectionState.CONNECTING;
         }
      }
   }

   @Override
   public boolean hasRwAccess() {
      return this._rwAccess;
   }

   private void dumpRequestQueue(String message) {
      synchronized (this._workingRequests) {
         System.out.println(message + ": dump requests");

         for (WSRequest req : this._workingRequests.get(0)) {
            req.log("");
         }
      }
   }

   @Override
   public void parseBinaryMessage(ByteBuffer raw_data) {
      raw_data.order(ByteOrder.LITTLE_ENDIAN);
      byte first_byte = raw_data.get();
      int tcpchan = first_byte & 7;
      int ystream = (first_byte & 255) >> 3;
      ArrayList<WSRequest> requestOfTCPChan = this._workingRequests.get(tcpchan);
      switch (ystream) {
         case 0:
            return;
         case 1:
         case 2:
            if (tcpchan > 3) {
               this._hub._yctx._Log("WS: Unexpected frame for tcpChan " + tcpchan + " (" + ystream + ")");
               return;
            }

            int ofs = 0;
            WSRequest workingRequest;
            synchronized (this._workingRequests) {
               do {
                  workingRequest = requestOfTCPChan.size() > ofs ? requestOfTCPChan.get(ofs) : null;
                  ofs++;
               } while (workingRequest != null && workingRequest.getState() == WSRequest.State.CLOSED);
            }

            if (workingRequest == null) {
               this._hub._yctx._Log(String.format(Locale.US, "WS: Drop frame for closed tcpChan %d (%s)\n", tcpchan, ystream == 2 ? "TCP_CLOSE" : "TCP"));
               return;
            }

            WSRequest.State workingRequestState = workingRequest.getState();
            WSStream stream = new WSStream(ystream, tcpchan, raw_data.remaining(), raw_data);
            workingRequest.addStream(stream);
            if (ystream == 2) {
               if (workingRequest.isAsync()) {
                  this._hub
                     ._yctx
                     ._Log(
                        String.format(
                           Locale.US, "WS: Synchronous close received instead of async-%d close for tcpchan %d\n", workingRequest.getAsyncId(), tcpchan
                        )
                     );
                  workingRequest.setState(WSRequest.State.CLOSED);
               } else {
                  if (workingRequestState == WSRequest.State.OPEN) {
                     WSStream outstream = new WSStream(2, tcpchan, 0, null);

                     try {
                        synchronized (this._sendLock) {
                           this._wsHandler.sendBinary(outstream.getContent(), true);
                        }
                     } catch (YAPI_Exception var42) {
                        var42.printStackTrace();
                     }
                  }

                  workingRequest.setState(WSRequest.State.CLOSED);
               }

               synchronized (this._workingRequests) {
                  requestOfTCPChan.remove(workingRequest);
               }
            }
            break;
         case 3:
         case 4:
         case 6:
         case 7:
         default:
            this._hub._yctx._Log(String.format(Locale.US, "Invalid WS stream type (%d)\n", ystream));
            break;
         case 5:
            int metatype = raw_data.get() & 255;
            switch (metatype) {
               case 4:
                  int version = raw_data.get() & 255;
                  if (version < 1 || raw_data.limit() < 28) {
                     return;
                  }

                  this._remoteVersion = version;
                  int maxtcpws = raw_data.getShort();
                  if (maxtcpws > 0) {
                     this._tcpMaxWindowSize = maxtcpws;
                  }

                  long nounce = (long)raw_data.getInt();
                  nounce &= -1L;
                  byte[] serial_char = new byte[raw_data.remaining()];
                  raw_data.get(serial_char);
                  int len = 8;

                  while (len < serial_char.length && serial_char[len] != 0) {
                     len++;
                  }

                  this._remoteSerial = new String(serial_char, 0, len, Charset.forName("ISO-8859-1"));
                  this._remoteNouce = nounce;
                  this._connectionTime = YAPI.GetTickCount();
                  Random randomGenerator = new Random();
                  this._nounce = randomGenerator.nextInt();
                  synchronized (this._stateLock) {
                     this._connectionState = WSNotificationHandler.ConnectionState.AUTHENTICATING;
                     this._stateLock.notifyAll();
                  }

                  this.sendAuthenticationMeta();
                  return;
               case 5:
                  synchronized (this._stateLock) {
                     if (this._connectionState != WSNotificationHandler.ConnectionState.AUTHENTICATING) {
                        return;
                     }
                  }

                  int versionx = raw_data.get() & 255;
                  if (versionx < 1 || raw_data.limit() < 28) {
                     return;
                  }

                  this._tcpRoundTripTime = YAPI.GetTickCount() - this._connectionTime + 1L;
                  long uploadRate = (long)(this._tcpMaxWindowSize * 1000) / this._tcpRoundTripTime;
                  this._hub
                     ._yctx
                     ._Log(
                        String.format(
                           Locale.US, "WS:RTT=%dms, WS=%d, uploadRate=%f KB/s\n", this._tcpRoundTripTime, this._tcpMaxWindowSize, (double)uploadRate / 1000.0
                        )
                     );
                  int flags = raw_data.getShort() & '\uffff';
                  raw_data.getInt();
                  if ((flags & 2) != 0) {
                     this._rwAccess = true;
                  }

                  if ((flags & 1) != 0) {
                     byte[] remote_sha1 = new byte[20];
                     raw_data.get(remote_sha1);
                     byte[] sha1 = this.computeAUTH(this._hub._http_params.getUser(), this._hub._http_params.getPass(), this._remoteSerial, (long)this._nounce);
                     if (Arrays.equals(sha1, remote_sha1)) {
                        synchronized (this._stateLock) {
                           this._connectionState = WSNotificationHandler.ConnectionState.CONNECTED;
                           this._stateLock.notifyAll();
                        }

                        this._waitingForConnectionState = false;
                     } else {
                        this.errorOnSession(-12, String.format("Authentication as %s failed", this._hub._http_params.getUser()));
                     }

                     return;
                  } else if (!this._hub._http_params.hasAuthParam()) {
                     synchronized (this._stateLock) {
                        this._connectionState = WSNotificationHandler.ConnectionState.CONNECTED;
                        this._stateLock.notifyAll();
                     }

                     this._waitingForConnectionState = false;
                     return;
                  } else {
                     if (this._hub._http_params.getUser().equals("admin") && !this._rwAccess) {
                        this.errorOnSession(-12, String.format("Authentication as %s failed", this._hub._http_params.getUser()));
                     } else {
                        this.errorOnSession(-12, String.format("Authentication error : hub has no password for %s", this._hub._http_params.getUser()));
                     }

                     return;
                  }
               case 6:
                  raw_data.get();
                  int html_error = raw_data.getShort() & '\uffff';
                  if (html_error == 401) {
                     this.errorOnSession(-12, "Authentication failed");
                  } else {
                     this.errorOnSession(-8, String.format(Locale.US, "Remote hub closed connection with error %d", html_error));
                  }

                  return;
               case 7:
                  int var51 = raw_data.get();
                  WSRequest workingRequest;
                  synchronized (this._workingRequests) {
                     ArrayList<WSRequest> uploadChandRequests = this._workingRequests.get(var51);
                     workingRequest = uploadChandRequests.size() > 0 ? uploadChandRequests.get(0) : null;
                  }

                  if (workingRequest != null) {
                     int b0 = raw_data.get() & 255;
                     int b1 = raw_data.get() & 255;
                     int b2 = raw_data.get() & 255;
                     int b3 = raw_data.get() & 255;
                     int ackBytes = b0 + (b1 << 8) + (b2 << 16) + (b3 << 24);
                     long ackTime = YAPI.GetTickCount();
                     if (this._lastUploadAckTime[var51] != 0L && ackBytes > this._lastUploadAckBytes[var51]) {
                        this._lastUploadAckBytes[var51] = ackBytes;
                        this._lastUploadAckTime[var51] = ackTime;
                        int deltaBytes = ackBytes - this._lastUploadRateBytes[var51];
                        long deltaTime = ackTime - this._lastUploadRateTime[var51];
                        if (deltaTime >= 500L && (deltaTime >= 1000L || deltaBytes >= 65536)) {
                           this._lastUploadRateBytes[var51] = ackBytes;
                           this._lastUploadRateTime[var51] = ackTime;
                           workingRequest.reportProgress(ackBytes);
                           double newRate = (double)deltaBytes * 1000.0 / (double)deltaTime;
                           this._uploadRate = (int)(0.8 * (double)this._uploadRate + 0.3 * newRate);
                           this._hub
                              ._yctx
                              ._Log(
                                 String.format(
                                    Locale.US,
                                    "Upload rate: %.2f KB/s (based on %.2f KB in %fs)\n",
                                    newRate / 1000.0,
                                    (double)deltaBytes / 1000.0,
                                    (double)deltaTime / 1000.0
                                 )
                              );
                           return;
                        }
                     } else {
                        this._hub._yctx._Log("First Ack received\n");
                        this._lastUploadAckBytes[var51] = ackBytes;
                        this._lastUploadAckTime[var51] = ackTime;
                        this._lastUploadRateBytes[var51] = ackBytes;
                        this._lastUploadRateTime[var51] = ackTime;
                        workingRequest.reportProgress(ackBytes);
                     }

                     return;
                  }

                  return;
               default:
                  this.WSLOG(String.format(Locale.US, "unhandled Meta pkt %d", ystream));
                  return;
            }
         case 8:
            if (this._waitingForConnectionState) {
               if (this._hub._http_params.hasAuthParam()) {
                  return;
               }

               synchronized (this._stateLock) {
                  this._connectionState = WSNotificationHandler.ConnectionState.CONNECTED;
                  this._stateLock.notifyAll();
               }

               this._waitingForConnectionState = false;
            }

            byte[] chars = new byte[raw_data.remaining()];
            raw_data.get(chars);
            String tcpNotif = new String(chars, Charset.forName("ISO-8859-1"));
            this.decodeTCPNotif(tcpNotif);
            break;
         case 9:
            if (tcpchan > 3) {
               this._hub._yctx._Log("WS: Unexpected frame for tcpChan " + tcpchan + " (" + ystream + ")");
               return;
            }

            WSRequest workingRequest;
            synchronized (this._workingRequests) {
               workingRequest = requestOfTCPChan.size() > 0 ? requestOfTCPChan.get(0) : null;
            }

            if (workingRequest == null) {
               this._hub._yctx._Log("WS: Drop frame for closed tcpChan " + tcpchan + " (" + ystream + ")");
               return;
            }

            if (raw_data.remaining() >= 1) {
               WSStream stream = new WSStream(ystream, tcpchan, raw_data.remaining() - 1, raw_data);
               int asyncId = raw_data.get();
               if (workingRequest.getAsyncId() == 0) {
                  this._hub._yctx._Log("Asynchronous close received, sync reply request");
               } else if (workingRequest.getAsyncId() != asyncId) {
                  this._hub._yctx._Log("WS: Incorrect async-close signature on tcpChan " + tcpchan);
               } else {
                  workingRequest.addStream(stream);
                  workingRequest.setState(WSRequest.State.CLOSED);
               }

               synchronized (this._workingRequests) {
                  requestOfTCPChan.remove(workingRequest);
               }
            } else {
               this._hub._yctx._Log("WS: Incorrect async-close packet (too short message) on tcpChan " + tcpchan);
            }
      }
   }

   private void processRequests() throws YAPI_Exception {
      if (this._next_transmit_tm == 0L || this._next_transmit_tm <= YAPI.GetTickCount()) {
         for (int tcpchan = 0; tcpchan < 4; tcpchan++) {
            ArrayList<WSRequest> requestOfTCPChan = this._workingRequests.get(tcpchan);
            int reqIndex = 0;
            WSRequest req;
            synchronized (this._workingRequests) {
               req = requestOfTCPChan.size() > reqIndex ? requestOfTCPChan.get(reqIndex) : null;
            }

            while (req != null) {
               WSRequest.State reqState = req.getState();
               if (reqState.equals(WSRequest.State.CLOSED_BY_API)) {
                  if (req.getExpiration() + 5000L < System.currentTimeMillis()) {
                     req.setState(WSRequest.State.FAKE_REQUEST);
                     synchronized (this._workingRequests) {
                        requestOfTCPChan.remove(req);
                     }
                  }
               } else if (reqState.equals(WSRequest.State.OPEN)) {
                  if (req.getExpiration() < System.currentTimeMillis()) {
                     if (req.isAsync()) {
                        req.setState(WSRequest.State.FAKE_REQUEST);
                        synchronized (this._workingRequests) {
                           requestOfTCPChan.remove(req);
                        }
                     } else if (req.getRequestBytes().position() > 0) {
                        WSStream wsstream = new WSStream(1, tcpchan, 0, null);
                        synchronized (this._sendLock) {
                           this._wsHandler.sendBinary(wsstream.getContent(), true);
                        }

                        req.setState(WSRequest.State.CLOSED_BY_API);
                     } else {
                        req.setState(WSRequest.State.CLOSED);
                        synchronized (this._workingRequests) {
                           requestOfTCPChan.remove(req);
                        }
                     }
                  } else {
                     ByteBuffer requestBytes = req.getRequestBytes();
                     int throttle_start = requestBytes.position();
                     int throttle_end = requestBytes.limit();
                     if (throttle_end > 2108 && this._remoteVersion >= 2 && tcpchan == 0) {
                        if (requestBytes.position() == 0) {
                           throttle_end = 2108;
                           this._lastUploadAckBytes[tcpchan] = 0;
                           this._lastUploadAckTime[tcpchan] = 0L;
                           this._uploadRate = (int)((long)(this._tcpMaxWindowSize * 1000) / this._tcpRoundTripTime);
                        } else if (this._lastUploadAckTime[tcpchan] == 0L) {
                           throttle_end = 0;
                        } else {
                           int bytesOnTheAir = requestBytes.position() - this._lastUploadAckBytes[tcpchan];
                           long timeOnTheAir = YAPI.GetTickCount() - this._lastUploadAckTime[tcpchan];
                           int uploadRate = this._uploadRate;
                           int toBeSent = (int)((long)(2 * uploadRate + 1024 - bytesOnTheAir) + (long)uploadRate * timeOnTheAir / 1000L);
                           if (toBeSent + bytesOnTheAir > 262144) {
                              toBeSent = 262144 - bytesOnTheAir;
                           }

                           this.WSLOG(
                              String.format(
                                 Locale.US, "throttling: %d bytes/s (%d + %d = %d)", this._uploadRate, toBeSent, bytesOnTheAir, bytesOnTheAir + toBeSent
                              )
                           );
                           if (toBeSent < 64) {
                              long waitTime = (long)(1000 * (128 - toBeSent) / this._uploadRate);
                              if (waitTime < 2L) {
                                 waitTime = 2L;
                              }

                              this._next_transmit_tm = YAPI.GetTickCount() + waitTime;
                              this.WSLOG(String.format(Locale.US, "WS: %d sent %dms ago, waiting %dms...", bytesOnTheAir, timeOnTheAir, waitTime));
                              throttle_end = 0;
                           }

                           if (throttle_end > requestBytes.position() + toBeSent) {
                              if (toBeSent > 124) {
                                 toBeSent = toBeSent / 124 * 124;
                              }

                              throttle_end = requestBytes.position() + toBeSent;
                           }
                        }
                     }

                     while (requestBytes.position() < throttle_end) {
                        int datalen = throttle_end - requestBytes.position();
                        if (datalen > WSStream.MAX_DATA_LEN) {
                           datalen = WSStream.MAX_DATA_LEN;
                        }

                        if (req.isAsync() && requestBytes.position() + datalen == requestBytes.limit()) {
                           if (datalen == WSStream.MAX_DATA_LEN) {
                              WSStream wsstream = new WSStream(1, tcpchan, datalen, requestBytes);
                              synchronized (this._sendLock) {
                                 this._wsHandler.sendBinary(wsstream.getContent(), true);
                              }

                              req.reportDataSent();
                              datalen = 0;
                           }

                           WSStream wsstream = new WSStream(9, tcpchan, datalen, requestBytes, req.getAsyncId());
                           synchronized (this._sendLock) {
                              this._wsHandler.sendBinary(wsstream.getContent(), true);
                           }

                           req.reportDataSent();
                        } else {
                           WSStream wsstream = new WSStream(1, tcpchan, datalen, requestBytes);
                           synchronized (this._sendLock) {
                              this._wsHandler.sendBinary(wsstream.getContent(), true);
                           }

                           req.reportDataSent();
                        }
                     }

                     if (requestBytes.position() < requestBytes.limit()) {
                        int sent = requestBytes.position() - throttle_start;
                        if (sent > 0 && this._uploadRate > 0) {
                           long waitTime = (long)(1000 * sent / this._uploadRate);
                           if (waitTime < 2L) {
                              waitTime = 2L;
                           }

                           this._next_transmit_tm = YAPI.GetTickCount() + waitTime;
                           this.WSLOG(String.format(Locale.US, "Sent %dbytes, waiting %dms...", sent, waitTime));
                        } else {
                           this._next_transmit_tm = YAPI.GetTickCount() + 100L;
                        }
                     }
                  }
               }

               synchronized (this._workingRequests) {
                  reqIndex++;
                  req = requestOfTCPChan.size() > reqIndex ? requestOfTCPChan.get(reqIndex) : null;
               }
            }
         }
      }
   }

   @Override
   public void WSLOG(String s) {
      this._hub._yctx._Log(s + "\n");
   }

   private void sendAuthenticationMeta() {
      ByteBuffer auth = ByteBuffer.allocate(28);
      auth.order(ByteOrder.LITTLE_ENDIAN);
      auth.put((byte)5);
      if (this._hub._http_params.hasAuthParam()) {
         auth.put((byte)2);
         auth.putShort((short)1);
         auth.putInt(this._nounce);
         byte[] sha1 = this.computeAUTH(this._hub._http_params.getUser(), this._hub._http_params.getPass(), this._remoteSerial, this._remoteNouce);
         auth.put(sha1);
      } else {
         auth.put((byte)2);
         auth.putInt(0);

         for (int i = 0; i < 5; i++) {
            auth.putInt(0);
         }
      }

      ((Buffer)auth).rewind();
      WSStream stream = new WSStream(5, 0, 28, auth);

      try {
         synchronized (this._sendLock) {
            this._wsHandler.sendBinary(stream.getContent(), true);
         }
      } catch (YAPI_Exception var6) {
         var6.printStackTrace();
      }
   }

   private byte[] computeAUTH(String user, String pass, String serial, long noune) {
      String ha1_str = user + ":" + serial + ":" + pass;
      this._md5.reset();
      this._md5.update(ha1_str.getBytes());
      byte[] digest = this._md5.digest();
      String ha1 = YAPIContext._bytesToHexStr(digest, 0, digest.length).toLowerCase();
      String sha1_raw = ha1 + String.format("%02x%02x%02x%02x", noune & 255L, noune >> 8 & 255L, noune >> 16 & 255L, noune >> 24 & 255L);
      this._sha1.reset();
      this._sha1.update(sha1_raw.getBytes());
      return this._sha1.digest();
   }

   private void decodeTCPNotif(String tcpNofif) {
      this._notificationsFifo.append(tcpNofif);

      while (true) {
         int pos = this._notificationsFifo.indexOf("\n");
         if (pos < 0) {
            return;
         }

         String line = this._notificationsFifo.substring(0, pos + 1);
         if (line.indexOf(27) == -1) {
            this.handleNetNotification(line);
         }

         this._notificationsFifo.delete(0, pos + 1);
      }
   }

   @Override
   public void errorOnSession(int errno, String closeReason) {
      synchronized (this._stateLock) {
         if (this._connectionState == WSNotificationHandler.ConnectionState.DEAD) {
            return;
         }

         this._connectionState = WSNotificationHandler.ConnectionState.DEAD;
         if (errno != 0) {
            this._session_errno = errno;
            if (!closeReason.equals("")) {
               this._session_error = closeReason;
            } else {
               this._session_error = "WebSocket connection has been closed";
            }
         }

         this._stateLock.notifyAll();
      }

      this._wsHandler.close();
      WSRequest fakeRequest = new WSRequest(-8, closeReason);

      try {
         this._pendingRequests.put(fakeRequest);
      } catch (InterruptedException var5) {
         Thread.currentThread().interrupt();
      }
   }

   private static enum ConnectionState {
      DEAD,
      DISCONNECTED,
      CONNECTING,
      AUTHENTICATING,
      CONNECTED;
   }
}
