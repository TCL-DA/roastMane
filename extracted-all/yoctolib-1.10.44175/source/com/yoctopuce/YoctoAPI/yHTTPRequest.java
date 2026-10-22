package com.yoctopuce.YoctoAPI;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Locale;

class yHTTPRequest implements Runnable {
   public static final int MAX_REQUEST_MS = 5000;
   private static final int YIO_IDLE_TCP_TIMEOUT = 5000;
   private Object _context;
   private YGenericHub.RequestAsyncResult _resultCallback;
   private final YHTTPHub _hub;
   private Socket _socket = null;
   private boolean _reuse_socket = false;
   private OutputStream _out = null;
   private InputStream _in = null;
   private yHTTPRequest.State _state = yHTTPRequest.State.AVAIL;
   private boolean _eof;
   private String _firstLine;
   private byte[] _rest_of_request;
   private final String _dbglabel;
   private final StringBuilder _header = new StringBuilder(1024);
   private Boolean _header_found;
   private final ByteArrayOutputStream _result = new ByteArrayOutputStream(1024);
   private long _startRequestTime;
   private long _lastReceiveTime;
   private long _requestTimeout;

   public void kill() {
      this._requestStop();
   }

   yHTTPRequest(YHTTPHub hub, String dbglabel) {
      this._hub = hub;
      this._dbglabel = dbglabel;
   }

   synchronized void _requestReserve() throws YAPI_Exception {
      long timeout = YAPI.GetTickCount() + 5000L + 1000L;

      while (timeout > YAPI.GetTickCount() && this._state != yHTTPRequest.State.AVAIL) {
         try {
            long toWait = timeout - YAPI.GetTickCount();
            this.wait(toWait);
         } catch (InterruptedException var5) {
            throw new YAPI_Exception(-7, "Last Http request did not finished");
         }
      }

      if (this._state != yHTTPRequest.State.AVAIL) {
         throw new YAPI_Exception(-7, "Last Http request did not finished");
      } else {
         this._state = yHTTPRequest.State.IN_REQUEST;
      }
   }

   synchronized void _requestRelease() {
      this._state = yHTTPRequest.State.AVAIL;
      this.notify();
   }

   void _requestStart(String firstLine, byte[] rest_of_request, long mstimeout, Object context, YGenericHub.RequestAsyncResult resultCallback) throws YAPI_Exception {
      this._firstLine = firstLine;
      this._rest_of_request = rest_of_request;
      this._context = context;
      this._startRequestTime = System.currentTimeMillis();
      this._requestTimeout = mstimeout;
      this._resultCallback = resultCallback;
      String persistent_tag = firstLine.substring(firstLine.length() - 2);
      if (persistent_tag.equals("&.")) {
         firstLine = firstLine + " \r\n";
      } else {
         firstLine = firstLine + " \r\nConnection: close\r\n";
      }

      byte[] full_request;
      if (rest_of_request == null) {
         String str_request = firstLine + this._hub.getAuthorization(firstLine) + "\r\n";
         full_request = str_request.getBytes();
      } else {
         String str_request = firstLine + this._hub.getAuthorization(firstLine);
         int len = str_request.length();
         full_request = new byte[len + rest_of_request.length];
         System.arraycopy(str_request.getBytes(), 0, full_request, 0, len);
         System.arraycopy(rest_of_request, 0, full_request, len, rest_of_request.length);
      }

      boolean retry;
      do {
         retry = false;

         try {
            if (!this._reuse_socket) {
               InetAddress addr = InetAddress.getByName(this._hub.getHost());
               SocketAddress sockaddr = new InetSocketAddress(addr, this._hub.getPort());
               this._socket = new Socket();
               this._socket.connect(sockaddr, (int)mstimeout);
               this._socket.setTcpNoDelay(true);
               this._out = this._socket.getOutputStream();
               this._in = this._socket.getInputStream();
            }

            this._result.reset();
            this._header.setLength(0);
            this._header_found = false;
            this._eof = false;
         } catch (UnknownHostException var12) {
            this._requestStop();
            throw new YAPI_Exception(-2, "Unknown host(" + this._hub.getHost() + ")");
         } catch (IOException var13) {
            this._requestStop();
            throw new YAPI_Exception(-8, var13.getLocalizedMessage());
         }

         try {
            this._out.write(full_request);
            this._out.flush();
            this._lastReceiveTime = -1L;
            if (this._reuse_socket) {
               this._socket.setSoTimeout(1);
               int b = this._in.read();
               if (b < 0) {
                  retry = true;
               } else {
                  this._header.append((char)b);
               }
            }
         } catch (SocketTimeoutException var14) {
         } catch (IOException var15) {
            if (!this._reuse_socket) {
               this._requestStop();
               throw new YAPI_Exception(-8, var15.getLocalizedMessage());
            }

            retry = true;
         }

         this._reuse_socket = false;
      } while (retry);
   }

   void _requestStop() {
      if (!this._reuse_socket) {
         if (this._out != null) {
            try {
               this._out.close();
            } catch (IOException var4) {
            }

            this._out = null;
         }

         if (this._in != null) {
            try {
               this._in.close();
            } catch (IOException var3) {
            }

            this._in = null;
         }

         if (this._socket != null) {
            try {
               this._socket.close();
            } catch (IOException var2) {
            }

            this._socket = null;
         }
      }
   }

   private void _requestReset() throws YAPI_Exception {
      this._requestStop();
      this._requestStart(this._firstLine, this._rest_of_request, this._requestTimeout, this._context, this._resultCallback);
   }

   int _requestProcesss() throws YAPI_Exception {
      int read = 0;
      if (this._eof) {
         return -1;
      } else {
         boolean retry;
         do {
            retry = false;
            byte[] buffer = new byte[1024];

            try {
               if (this._requestTimeout > 0L) {
                  long read_timeout = this._startRequestTime + this._requestTimeout - System.currentTimeMillis();
                  if (read_timeout < 0L) {
                     throw new YAPI_Exception(
                        -7, String.format(Locale.US, "Hub did not send data during %dms", System.currentTimeMillis() - this._lastReceiveTime)
                     );
                  }

                  if (read_timeout > 5000L) {
                     read_timeout = 5000L;
                  }

                  this._socket.setSoTimeout((int)read_timeout);
               } else {
                  this._socket.setSoTimeout(5000);
               }

               read = this._in.read(buffer, 0, buffer.length);
            } catch (SocketTimeoutException var12) {
               long nowTime = System.currentTimeMillis();
               if (this._lastReceiveTime >= 0L && nowTime - this._lastReceiveTime >= 5000L) {
                  long duration = nowTime - this._startRequestTime;
                  if (duration > this._requestTimeout) {
                     throw new YAPI_Exception(-7, String.format(Locale.US, "TCP request on %s took too long (%dms)", this._hub.getHost(), duration));
                  }

                  if (duration > this._requestTimeout - this._requestTimeout / 4L) {
                     this._hub._yctx._Log(String.format(Locale.US, "Slow TCP request on %s (%dms)\n", this._hub.getHost(), duration));
                  }

                  retry = true;
               } else {
                  retry = true;
               }
               continue;
            } catch (IOException var13) {
               throw new YAPI_Exception(-8, var13.getLocalizedMessage());
            }

            if (read < 0) {
               this._reuse_socket = false;
               this._eof = true;
            } else if (read > 0) {
               this._lastReceiveTime = System.currentTimeMillis();
               synchronized (this._result) {
                  if (!this._header_found) {
                     String partial_head = new String(buffer, 0, read, this._hub._yctx._deviceCharset);
                     this._header.append(partial_head);
                     int pos = this._header.indexOf("\r\n\r\n");
                     if (pos > 0) {
                        pos += 4;

                        try {
                           this._result.write(this._header.substring(pos).getBytes(this._hub._yctx._deviceCharset));
                        } catch (IOException var10) {
                           throw new YAPI_Exception(-8, var10.getLocalizedMessage());
                        }

                        this._header_found = true;
                        this._header.setLength(pos);
                        if (this._header.indexOf("0K\r\n") == 0) {
                           this._reuse_socket = true;
                        } else if (this._header.indexOf("OK\r\n") != 0) {
                           int lpos = this._header.indexOf("\r\n");
                           if (this._header.indexOf("HTTP/1.1 ") != 0) {
                              throw new YAPI_Exception(-8, "Invalid HTTP response header");
                           }

                           String[] parts = this._header.substring(9, lpos).split(" ");
                           if (parts[0].equals("401")) {
                              if (!this._hub.needRetryWithAuth()) {
                                 throw new YAPI_Exception(-12, "Authentication required");
                              }

                              this._hub.parseWWWAuthenticate(this._header.toString());
                              this._requestReset();
                              break;
                           }

                           if (!parts[0].equals("200") && !parts[0].equals("304")) {
                              throw new YAPI_Exception(-8, "Received HTTP status " + parts[0] + " (" + parts[1] + ")");
                           }
                        }

                        this._hub.authSucceded();
                     }
                  } else {
                     this._result.write(buffer, 0, read);
                  }

                  if (this._reuse_socket && this._result.toString().endsWith("\r\n")) {
                     this._eof = true;
                  }
               }
            }
         } while (retry);

         return read;
      }
   }

   byte[] getPartialResult() throws YAPI_Exception {
      synchronized (this._result) {
         if (!this._header_found) {
            return null;
         } else if (this._result.size() == 0) {
            if (this._eof) {
               throw new YAPI_Exception(-9, "end of file reached");
            } else {
               return null;
            }
         } else {
            byte[] res = this._result.toByteArray();
            this._result.reset();
            return res;
         }
      }
   }

   byte[] RequestSync(String req_first_line, byte[] req_head_and_body, int mstimeout) throws YAPI_Exception {
      this._requestReserve();

      byte[] res;
      try {
         this._requestStart(req_first_line, req_head_and_body, (long)mstimeout, null, null);

         int read;
         do {
            read = this._requestProcesss();
         } while (read >= 0);

         synchronized (this._result) {
            res = this._result.toByteArray();
            this._result.reset();
         }
      } catch (YAPI_Exception var9) {
         this._requestStop();
         this._requestRelease();
         throw var9;
      }

      this._requestStop();
      this._requestRelease();
      return res;
   }

   @Override
   public void run() {
      byte[] res = null;
      int errorType = 0;
      String errmsg = null;

      try {
         this._requestProcesss();

         int read;
         do {
            read = this._requestProcesss();
         } while (read >= 0);

         synchronized (this._result) {
            res = this._result.toByteArray();
            this._result.reset();
         }
      } catch (YAPI_Exception var8) {
         errorType = var8.errorType;
         errmsg = var8.getMessage();
         this._hub._yctx._Log("ASYNC request " + this._firstLine + "failed:" + errmsg + "\n");
      }

      this._requestStop();
      if (this._resultCallback != null) {
         this._resultCallback.RequestAsyncDone(this._context, res, errorType, errmsg);
      }

      this._requestRelease();
   }

   void RequestAsync(String req_first_line, byte[] req_head_and_body, YGenericHub.RequestAsyncResult callback, Object context) throws YAPI_Exception {
      this._requestReserve();

      try {
         this._requestStart(req_first_line, req_head_and_body, (long)this._hub._yctx._networkTimeoutMs, context, callback);
         Thread t = new Thread(this);
         t.setName(this._dbglabel);
         t.start();
      } catch (YAPI_Exception var6) {
         this._requestRelease();
         throw var6;
      }
   }

   synchronized void WaitRequestEnd(long mstimeout) throws InterruptedException {
      long timeout = YAPI.GetTickCount() + mstimeout;

      while (timeout > YAPI.GetTickCount() && this._state == yHTTPRequest.State.IN_REQUEST) {
         long toWait = timeout - YAPI.GetTickCount();
         this.wait(toWait);
      }

      if (this._state == yHTTPRequest.State.IN_REQUEST) {
         this._hub._yctx._Log("WARNING: Last Http request did not finished");
      }

      this._reuse_socket = false;
      this._requestStop();
      this._state = yHTTPRequest.State.STOPPED;
   }

   private static enum State {
      AVAIL,
      IN_REQUEST,
      STOPPED;
   }
}
