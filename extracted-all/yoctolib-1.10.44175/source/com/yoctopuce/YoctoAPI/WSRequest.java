package com.yoctopuce.YoctoAPI;

import java.nio.ByteBuffer;
import java.util.LinkedList;

class WSRequest {
   private final int _channel;
   private final ByteBuffer _requestData;
   private final LinkedList<WSStream> _responseStream;
   private final boolean _async;
   private final byte _asyncId;
   private final Object _progressCtx;
   private final YGenericHub.RequestProgress _progressCb;
   private volatile WSRequest.State _state;
   private int _errorCode = 0;
   private String _errorMsg = null;
   private Exception _errorEx = null;
   private final long _expiration;
   private final long _tmOpen;
   private long _tmProcess;
   private long _tmIn;
   private long _tmOut;
   private long _tmClose;
   private final String _dbgLabel;

   @Override
   public String toString() {
      return "WSRequest{_async=" + this._async + ", _asyncId=" + this._asyncId + ", _dbgLabel='" + this._dbgLabel + '\'' + '}';
   }

   void checkError() throws YAPI_Exception {
      if (this._errorCode != 0) {
         throw new YAPI_Exception(this._errorCode, this._errorMsg);
      }
   }

   synchronized void setError(int ioError, String reasonPhrase) {
      this._errorCode = ioError;
      this._errorMsg = reasonPhrase;
      this._state = WSRequest.State.FAKE_REQUEST;
      this.notifyAll();
   }

   void log(String msg) {
      long process = this._tmProcess - this._tmOpen;
      long write = this._tmOut - this._tmProcess;
      long read = this._tmIn - this._tmOut;
      long end = this._tmClose - this._tmOpen;
      System.out.println(String.format("%s:%s:%s in start:%d + write:%d + read+%d =%d ms", this, msg, this._state, process, write, read, end));
   }

   WSRequest.State waitProcessingEnd(long expiration_timeout) throws InterruptedException {
      synchronized (this) {
         while (
            !this._state.equals(WSRequest.State.FAKE_REQUEST) && !this._state.equals(WSRequest.State.CLOSED) && expiration_timeout > System.currentTimeMillis()
         ) {
            this.wait(expiration_timeout - System.currentTimeMillis());
         }

         return this._state;
      }
   }

   boolean isAsync() {
      return this._async;
   }

   byte getAsyncId() {
      return this._asyncId;
   }

   void reportProgress(int ackBytes) {
      if (this._progressCb != null && this._requestData.limit() > 0) {
         this._progressCb.requestProgressUpdate(this._progressCb, ackBytes, this._requestData.limit());
      }
   }

   long getExpiration() {
      return this._expiration;
   }

   WSRequest(int ioError, String reasonPhrase) {
      this._async = false;
      this._asyncId = 0;
      this._channel = 0;
      this._requestData = null;
      this._responseStream = new LinkedList<>();
      this._tmOpen = System.currentTimeMillis();
      this._progressCb = null;
      this._progressCtx = null;
      this._errorCode = ioError;
      this._errorMsg = reasonPhrase;
      this._state = WSRequest.State.FAKE_REQUEST;
      this._dbgLabel = "error:" + reasonPhrase;
      this._expiration = 0L;
   }

   WSRequest(int tcpchanel, byte asyncid, byte[] full_request, long expiration) {
      this._async = true;
      this._asyncId = asyncid;
      this._state = WSRequest.State.OPEN;
      this._channel = tcpchanel;
      this._requestData = ByteBuffer.wrap(full_request);
      this._responseStream = new LinkedList<>();
      this._tmOpen = System.currentTimeMillis();
      this._progressCb = null;
      this._progressCtx = null;
      this._dbgLabel = "";
      this._expiration = expiration;
   }

   WSRequest(int tcpchanel, byte[] full_request, long expiration, YGenericHub.RequestProgress progress, Object context) {
      this._async = false;
      this._asyncId = 0;
      this._state = WSRequest.State.OPEN;
      this._channel = tcpchanel;
      this._requestData = ByteBuffer.wrap(full_request);
      this._responseStream = new LinkedList<>();
      this._tmOpen = System.currentTimeMillis();
      this._progressCb = progress;
      this._progressCtx = context;
      this._dbgLabel = "";
      this._expiration = expiration;
   }

   private String getReqDbgString(byte[] full_request) {
      String dbg_req = new String(full_request);
      int pos = dbg_req.indexOf("\r");
      if (pos > 0) {
         dbg_req = dbg_req.substring(0, pos);
      }

      return dbg_req;
   }

   ByteBuffer getRequestBytes() {
      return this._requestData;
   }

   int getChannel() {
      return this._channel;
   }

   public synchronized void setState(WSRequest.State state) {
      this._state = state;
      if (state.equals(WSRequest.State.CLOSED)) {
         this._tmClose = System.currentTimeMillis();
      }

      this.notifyAll();
   }

   public synchronized WSRequest.State getState() {
      return this._state;
   }

   void addStream(WSStream stream) {
      this._responseStream.add(stream);
      this._tmIn = System.currentTimeMillis();
   }

   void reportDataSent() {
      this._tmOut = System.currentTimeMillis();
   }

   void reportStartOfProcess() {
      this._tmProcess = System.currentTimeMillis();
   }

   byte[] getResponseBytes() {
      int size = 0;

      for (WSStream s : this._responseStream) {
         size += s.getContentLen();
      }

      byte[] full_result = new byte[size];
      ByteBuffer bb = ByteBuffer.wrap(full_result);

      for (WSStream s : this._responseStream) {
         s.getContent(bb);
      }

      return full_result;
   }

   static enum State {
      OPEN,
      CLOSED_BY_HUB,
      CLOSED_BY_API,
      CLOSED,
      FAKE_REQUEST;
   }
}
