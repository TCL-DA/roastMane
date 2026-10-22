package com.yoctopuce.YoctoAPI;

import java.util.ArrayList;
import java.util.Arrays;

abstract class NotificationHandler implements Runnable {
   private static final char NOTIFY_NETPKT_NAME = '0';
   private static final char NOTIFY_NETPKT_CHILD = '2';
   private static final char NOTIFY_NETPKT_FUNCNAME = '4';
   private static final char NOTIFY_NETPKT_FUNCVAL = '5';
   private static final char NOTIFY_NETPKT_FUNCNAMEYDX = '8';
   private static final char NOTIFY_NETPKT_CONFCHGYDX = 's';
   private static final char NOTIFY_NETPKT_FLUSHV2YDX = 't';
   private static final char NOTIFY_NETPKT_FUNCV2YDX = 'u';
   private static final char NOTIFY_NETPKT_TIMEV2YDX = 'v';
   private static final char NOTIFY_NETPKT_DEVLOGYDX = 'w';
   private static final char NOTIFY_NETPKT_TIMEVALYDX = 'x';
   private static final char NOTIFY_NETPKT_FUNCVALYDX = 'y';
   private static final char NOTIFY_NETPKT_TIMEAVGYDX = 'z';
   private static final char NOTIFY_NETPKT_NOT_SYNC = '@';
   private static final char NOTIFY_NETPKT_LOG = '7';
   private static final int NOTIFY_NETPKT_STOP = 10;
   static final int NET_HUB_NOT_CONNECTION_TIMEOUT = 6000;
   long _notifyPos = -1L;
   int _notifRetryCount = 0;
   int _error_delay = 0;
   boolean _sendPingNotification = false;
   long _lastPing = 0L;
   final YHTTPHub _hub;

   NotificationHandler(YHTTPHub hub) {
      this._hub = hub;
   }

   abstract String getThreadLabel();

   private byte[] decodeNetFuncValV2(byte[] p) {
      int p_ofs = 0;
      int ch = p[p_ofs] & 255;
      int len = 0;
      byte[] funcVal = new byte[7];
      Arrays.fill(funcVal, (byte)0);
      if (ch >= 32 && ch <= 159) {
         ch -= 32;
         funcVal[0] = (byte)((ch & 64) != 0 ? 1 : 2);
         ch &= 63;

         while (len < 6) {
            if (++p_ofs < p.length) {
               int newCh = p[p_ofs] & 255;
               if (newCh != 10) {
                  if (newCh >= 32 && newCh <= 159) {
                     newCh -= 32;
                     ch = (ch << 7) + newCh;
                     funcVal[len + 1] = (byte)(ch >> 5 - len);
                     len++;
                     continue;
                  }

                  return null;
               }
            }
            break;
         }

         return funcVal;
      } else {
         return null;
      }
   }

   void handleNetNotification(String notification_line) {
      String ev = notification_line.trim();
      this._lastPing = System.currentTimeMillis();
      if ("".equals(ev)) {
         this._sendPingNotification = true;
      } else {
         if (ev.length() >= 3 && ev.charAt(0) >= 's' && ev.charAt(0) <= 'z') {
            this._hub._isNotifWorking = true;
            this._notifRetryCount = 0;
            if (this._notifyPos >= 0L) {
               this._notifyPos = this._notifyPos + (long)(ev.length() + 1);
            }

            int devydx = ev.charAt(1) - 'A';
            int funydx = ev.charAt(2) - '0';
            if ((funydx & 64) != 0) {
               funydx -= 64;
               devydx += 128;
            }

            String value = ev.substring(3);
            String serial = this._hub._serialByYdx.get(devydx);
            if (serial != null) {
               YDevice ydev = this._hub._yctx._yHash.getDevice(serial);
               if (ydev != null) {
                  switch (ev.charAt(0)) {
                     case 's':
                        this._hub.handleConfigChangeNotification(serial);
                     case 't':
                     default:
                        break;
                     case 'u':
                        String funcid = ydev.getYPEntry(funydx).getFuncId();
                        if (!funcid.equals("")) {
                           byte[] rawval = this.decodeNetFuncValV2(value.getBytes());
                           if (rawval != null) {
                              String decodedval = YGenericHub.decodePubVal(rawval[0], rawval, 1, 6);
                              this._hub.handleValueNotification(serial, funcid, decodedval);
                           }
                        }
                        break;
                     case 'v':
                     case 'x':
                     case 'z':
                        if (funydx == 15) {
                           int not_len = value.length() / 2;
                           Integer[] data = new Integer[not_len];

                           for (int i = 0; i < not_len; i++) {
                              String part = value.substring(i * 2, i * 2 + 2);
                              data[i] = Integer.parseInt(part, 16);
                           }

                           ydev.setLastTimeRef(data);
                        } else {
                           String funcidxx = ydev.getYPEntry(funydx).getFuncId();
                           if (!funcidxx.equals("")) {
                              ArrayList<Integer> report = new ArrayList<>(1 + value.length() / 2);
                              report.add(ev.charAt(0) == 'x' ? 0 : (ev.charAt(0) == 'z' ? 1 : 2));

                              for (int pos = 0; pos < value.length(); pos += 2) {
                                 int intval = Integer.parseInt(value.substring(pos, pos + 2), 16);
                                 report.add(intval);
                              }

                              this._hub.handleTimedNotification(serial, funcidxx, ydev.getLastTimeRef(), ydev.getLastDuration(), report);
                           }
                        }
                        break;
                     case 'w':
                        ydev.triggerLogPull();
                        break;
                     case 'y':
                        String funcidx = ydev.getYPEntry(funydx).getFuncId();
                        if (!funcidx.equals("")) {
                           this._hub.handleValueNotification(serial, funcidx, value);
                        }
                  }
               }
            }
         } else if (ev.length() >= 5 && ev.startsWith("YN01")) {
            this._hub._isNotifWorking = true;
            this._notifRetryCount = 0;
            if (this._notifyPos >= 0L) {
               this._notifyPos = this._notifyPos + (long)(ev.length() + 1);
            }

            char notype = ev.charAt(4);
            if (notype == '@') {
               this._notifyPos = (long)Integer.valueOf(ev.substring(5)).intValue();
            } else {
               switch (notype) {
                  case '0': {
                     String[] parts = ev.substring(5).split(",");

                     try {
                        int beacon = Integer.valueOf(parts[2]);
                        this._hub.handleBeaconNotification(parts[0], parts[1], beacon);
                     } catch (NumberFormatException var13) {
                     }
                  }
                  case '2':
                  case '4':
                  case '8':
                     this._hub._devListExpires = 0L;
                  case '1':
                  case '3':
                  case '6':
                  case '7':
                  default:
                     break;
                  case '5': {
                     String[] parts = ev.substring(5).split(",");
                     this._hub.handleValueNotification(parts[0], parts[1], parts[2]);
                  }
               }
            }
         } else {
            this._hub._isNotifWorking = false;
            this._notifyPos = -1L;
         }
      }
   }

   abstract byte[] hubRequestSync(String var1, byte[] var2, int var3) throws YAPI_Exception, InterruptedException;

   abstract byte[] devRequestSync(YDevice var1, String var2, byte[] var3, int var4, YGenericHub.RequestProgress var5, Object var6) throws YAPI_Exception, InterruptedException;

   abstract void devRequestAsync(YDevice var1, String var2, byte[] var3, YGenericHub.RequestAsyncResult var4, Object var5) throws YAPI_Exception, InterruptedException;

   abstract boolean waitAndFreeAsyncTasks(long var1) throws InterruptedException;

   abstract boolean isConnected();

   abstract boolean hasRwAccess();
}
