package com.yoctopuce.YoctoAPI;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Locale;
import java.util.Random;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSocketFactory;

class WSHandlerYocto implements WSHandlerInterface, Runnable {
   private final WSHandlerInterface.WSHandlerResponseInterface _nhandler;
   private MessageDigest _sha1 = null;
   private String _websocket_key;
   private static final String WS_HEADER_START = " HTTP/1.1\r\nSec-WebSocket-Version: 13\r\nUser-Agent: Yoctopuce\r\nSec-WebSocket-Key: ";
   private static final String WS_HEADER_END = "\r\nConnection: keep-alive, Upgrade\r\nUpgrade: websocket\r\n\r\n";
   private static final String WEBSOCKET_MAGIC = "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
   private Socket _socket;
   private BufferedOutputStream _out;
   private BufferedInputStream _in;
   private final Random _randGen = new Random();
   private ByteArrayOutputStream _fragments = null;
   private boolean _closing;
   private boolean _closed;
   private Thread _thread;

   WSHandlerYocto(WSHandlerInterface.WSHandlerResponseInterface notificationHandler) {
      this._nhandler = notificationHandler;

      try {
         this._sha1 = MessageDigest.getInstance("SHA-1");
      } catch (NoSuchAlgorithmException var3) {
         var3.printStackTrace();
      }
   }

   @Override
   public void close() {
      if (!this._closed) {
         this._closing = true;
         if (this._socket != null) {
            try {
               if (this._out != null) {
                  this._out.flush();
                  this._out.close();
               }

               if (this._in != null) {
                  this._in.close();
               }

               this._socket.close();
            } catch (IOException var3) {
               var3.printStackTrace();
            }

            if (this._thread != null) {
               this._thread.interrupt();

               try {
                  this._thread.join(1000L);
               } catch (InterruptedException var2) {
                  var2.printStackTrace();
                  Thread.currentThread().interrupt();
               }
            }

            this._out = null;
            this._in = null;
            this._socket = null;
         }
      }
   }

   @Override
   public boolean isOpen() {
      return !this.isClosed() & !this.isClosing();
   }

   @Override
   public void sendBinary(ByteBuffer partialByte, boolean isLast) throws YAPI_Exception {
      byte[] header = new byte[2];
      byte[] mask = new byte[4];
      this._randGen.nextBytes(mask);
      int pktlen = partialByte.remaining();

      try {
         header[0] = -126;
         header[1] = (byte)(pktlen & 0xFF | 128);
         this._out.write(header);
         this._out.write(mask);
         byte[] buf = new byte[pktlen];
         partialByte.get(buf);

         for (int i = 0; i < pktlen; i++) {
            buf[i] ^= mask[i & 3];
         }

         this._out.write(buf);
         if (isLast) {
            this._out.flush();
         }
      } catch (IOException var8) {
         var8.printStackTrace();
      }
   }

   @Override
   public String getThreadLabel() {
      return "WSYocto";
   }

   @Override
   public boolean isCallback() {
      return false;
   }

   private boolean VerifyWebsocketKey(String toValidate) {
      String magic = this._websocket_key + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
      this._sha1.reset();
      this._sha1.update(magic.getBytes());
      byte[] digest = this._sha1.digest();
      String valid = YAPI.Base64Encode(digest, 0, digest.length);
      return toValidate.trim().equals(valid);
   }

   @Override
   public void connect(YHTTPHub hub, boolean first_notification_connection, int mstimeout, int notifAbsPos) throws YAPI_Exception {
      long start = System.currentTimeMillis();
      String request = "GET ";
      String subDomain = hub._http_params.getSubDomain();
      request = request + subDomain;
      this._closed = true;
      this._closing = false;
      this._fragments = null;
      String host = hub.getHost();
      this._nhandler.WSLOG(String.format(Locale.US, "hub(%s) try to open WS connection at %d", host, notifAbsPos));
      if (first_notification_connection) {
         request = request + "/not.byn";
      } else {
         request = request + String.format(Locale.US, "/not.byn?abs=%d", notifAbsPos);
      }

      try {
         InetAddress addr = InetAddress.getByName(host);
         int port = hub.getPort();
         SocketAddress sockaddr = new InetSocketAddress(addr, port);
         if (hub._http_params.useSecureSocket()) {
            SSLSocketFactory sslsocketFactory = (SSLSocketFactory)SSLSocketFactory.getDefault();
            this._socket = sslsocketFactory.createSocket(addr, port);
         } else {
            this._socket = new Socket();
            this._socket.connect(sockaddr, mstimeout);
         }

         this._socket.setTcpNoDelay(true);
         this._socket.setSoTimeout(1000);
         this._out = new BufferedOutputStream(this._socket.getOutputStream());
         this._in = new BufferedInputStream(this._socket.getInputStream(), 2048);
      } catch (UnknownHostException var26) {
         throw new YAPI_Exception(-2, "Unknown host(" + host + ")");
      } catch (IOException var27) {
         throw new YAPI_Exception(-8, var27.getLocalizedMessage());
      }

      try {
         this._out.write(request.getBytes());
         this._out.write(" HTTP/1.1\r\nSec-WebSocket-Version: 13\r\nUser-Agent: Yoctopuce\r\nSec-WebSocket-Key: ".getBytes());
         byte[] SecWebSocketKey = new byte[16];
         this._randGen.nextBytes(SecWebSocketKey);
         String encodedBytes = YAPI.Base64Encode(SecWebSocketKey, 0, SecWebSocketKey.length);
         this._websocket_key = encodedBytes;
         this._out.write(encodedBytes.getBytes());
         this._out.write("\r\nConnection: keep-alive, Upgrade\r\nUpgrade: websocket\r\n\r\n".getBytes());
         this._out.flush();
         this._closed = false;
         StringBuilder header = new StringBuilder(2048);
         byte[] buffer = new byte[2048];
         boolean websock_ok = false;

         label70:
         while (System.currentTimeMillis() - start < (long)mstimeout) {
            int read = this._in.read(buffer, 0, buffer.length);
            if (read < 0) {
               break;
            }

            String part = new String(buffer, 0, read, hub._yctx._deviceCharset);
            header.append(part);
            int end_of_head = header.indexOf("\r\n\r\n");
            if (end_of_head > 0) {
               end_of_head += 2;
               header.setLength(end_of_head);
               String fullHeader = header.toString();
               int ofs = fullHeader.indexOf("HTTP/1.1 101");
               if (ofs != 0) {
                  throw new YAPI_Exception(-8, "hub does not support WebSocket");
               }

               ofs = fullHeader.indexOf("\r\n");

               while (ofs > 0 && ofs < fullHeader.length() - 2) {
                  ofs += 2;
                  int nextofs = header.indexOf("\r\n", ofs);
                  int sep = header.indexOf(":", ofs);
                  if (sep > 0 && nextofs > sep) {
                     String field = header.substring(ofs, sep);
                     String lowerCase = field.toLowerCase().trim();
                     if (lowerCase.startsWith("sec-websocket-accept")) {
                        String value = header.substring(sep + 1, nextofs);
                        websock_ok = this.VerifyWebsocketKey(value);
                        if (websock_ok) {
                           int start_bin = end_of_head + 2;
                           this.setupNewWSConnection(buffer, start_bin, read - start_bin);
                        }
                        break label70;
                     }
                  }

                  ofs = nextofs;
               }
               break;
            }
         }

         if (!websock_ok) {
            throw new YAPI_Exception(-8, "hub does not support WebSocket");
         }
      } catch (SSLException var28) {
         throw new YAPI_Exception(-2, var28.getLocalizedMessage());
      } catch (IOException var29) {
         throw new YAPI_Exception(-8, var29.getLocalizedMessage());
      }
   }

   private void setupNewWSConnection(byte[] buffer, int ofs, int len) throws YAPI_Exception {
      this._closing = false;
      if (len > 0) {
         this.decodeFrame(buffer, ofs, len);
      }

      this._thread = new Thread(this);
      this._thread.setName("WSYocto_helpler");
      this._thread.start();
   }

   @Override
   public void run() {
      byte[] rawbuffer = new byte[2048];
      int ofs = 0;
      int max = rawbuffer.length;

      try {
         while (!this.isClosing() && !this.isClosed()) {
            int readBytes;
            try {
               readBytes = this._in.read(rawbuffer, ofs, max);
            } catch (SocketTimeoutException var9) {
               continue;
            }

            if (readBytes < 0) {
               break;
            }

            int end_ofs = ofs + readBytes;
            int ptr = 0;
            int avail = end_ofs;

            int consumed;
            do {
               consumed = this.decodeFrame(rawbuffer, ptr, avail);
               ptr += consumed;
               avail -= consumed;
            } while (consumed > 0 && avail > 2);

            if (avail > 0 && ptr > 0) {
               System.arraycopy(rawbuffer, ptr, rawbuffer, 0, avail);
            }

            ofs = avail;
            max = rawbuffer.length - avail;
         }
      } catch (YAPI_Exception | IOException var10) {
         this._closed = true;
         this._nhandler.errorOnSession(-8, var10.getLocalizedMessage());
      }

      this._closed = true;
   }

   private int decodeFrame(byte[] data, int ofs, int len) throws YAPI_Exception {
      if (len < 2) {
         return 0;
      } else {
         int pktlen = data[ofs + 1] & 127;
         if (pktlen > 125) {
            throw new YAPI_Exception(-8, "Unsupported long WebSocket frame");
         } else {
            byte[] mask = null;
            int hdrlen;
            if ((data[ofs + 1] & 128) != 0) {
               hdrlen = 6;
               if (len < hdrlen + pktlen) {
                  return 0;
               }

               mask = Arrays.copyOfRange(data, ofs + 2, ofs + 2);
            } else {
               hdrlen = 2;
               if (len < hdrlen + pktlen) {
                  return 0;
               }
            }

            if ((data[ofs] & 127) != 2) {
               if ((data[ofs] & 255) == 136) {
                  byte[] header = new byte[]{-120, -126};

                  try {
                     this._out.write(header);
                     byte[] outmask = new byte[4];
                     this._randGen.nextBytes(outmask);
                     this._out.write(outmask);
                     header[0] = (byte)(3 ^ outmask[0]);
                     header[1] = (byte)(232 ^ outmask[1]);
                     this._out.write(header);
                     this._out.flush();
                  } catch (IOException var10) {
                     var10.printStackTrace();
                     throw new YAPI_Exception(-8, "io error on base socket:" + var10.getLocalizedMessage());
                  }

                  this._closed = true;
               } else {
                  this._nhandler.WSLOG(String.format(Locale.US, "unhandled packet:%x%x\n", data[ofs], data[ofs + 1]));
               }
            }

            boolean fragmented = (data[ofs] & 13) != 0;
            ofs += hdrlen;
            if (mask != null) {
               for (int i = 0; i < pktlen; i++) {
                  data[ofs + i] = (byte)(data[ofs + i] ^ mask[i & 3]);
               }
            }

            if (fragmented) {
               int ystream = (data[ofs] & 255) >> 3;
               if (ystream == 5) {
                  this._nhandler.WSLOG("Warning:fragmented META\n");
                  return hdrlen + pktlen;
               }

               if (this._fragments == null) {
                  this._fragments = new ByteArrayOutputStream(1024);
               }

               this._fragments.write(data, ofs, pktlen);
            } else if (this._fragments != null && this._fragments.size() > 0) {
               this._fragments.write(data, ofs, pktlen);
               byte[] merged = this._fragments.toByteArray();
               ByteBuffer wrap = ByteBuffer.wrap(merged);
               this._fragments.reset();
               this._nhandler.parseBinaryMessage(wrap);
            } else {
               ByteBuffer wrap = ByteBuffer.wrap(data, ofs, pktlen);
               this._nhandler.parseBinaryMessage(wrap);
            }

            return hdrlen + pktlen;
         }
      }
   }

   private boolean isClosed() {
      return this._closed;
   }

   private boolean isClosing() {
      return this._closing;
   }
}
