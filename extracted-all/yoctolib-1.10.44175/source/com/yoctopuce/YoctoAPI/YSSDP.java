package com.yoctopuce.YoctoAPI;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Random;

class YSSDP {
   private static final int SSDP_PORT = 1900;
   private static final String SSDP_MCAST_ADDR = "239.255.255.250";
   private static final String SSDP_URN_YOCTOPUCE = "urn:yoctopuce-com:device:hub:1";
   private static final String SSDP_DISCOVERY_MESSAGE = "M-SEARCH * HTTP/1.1\r\nHOST: 239.255.255.250:"
      + Integer.toString(1900)
      + "\r\nMAN: \"ssdp:discover\"\r\nMX: 5\r\nST: "
      + "urn:yoctopuce-com:device:hub:1"
      + "\r\n\r\n";
   private static final String SSDP_NOTIFY = "NOTIFY * HTTP/1.1";
   private static final String SSDP_HTTP = "HTTP/1.1 200 OK";
   private final YAPIContext _yctx;
   private final ArrayList<NetworkInterface> _netInterfaces = new ArrayList<>(1);
   private final HashMap<String, YSSDPCacheEntry> _cache = new HashMap<>();
   private InetSocketAddress mMcastAddr;
   private boolean _Listening;
   private YSSDP.YSSDPReportInterface _callbacks;
   private Thread[] _listenMSearchThread;
   private Thread[] _listenBcastThread;

   YSSDP(YAPIContext yctx) {
      this._yctx = yctx;
   }

   void reset() {
      this._netInterfaces.clear();
      this._cache.clear();
      this.mMcastAddr = null;
      this._Listening = false;
      this._callbacks = null;
      this._listenMSearchThread = null;
      this._listenBcastThread = null;
   }

   synchronized void addCallback(YSSDP.YSSDPReportInterface callback) throws YAPI_Exception {
      if (this._callbacks != callback) {
         this._callbacks = callback;
         this.stopThreads();
         if (!this._Listening) {
            try {
               this.startListening();
            } catch (IOException var3) {
               throw new YAPI_Exception(-8, "Unable to start SSDP thread : " + var3.toString());
            }
         }
      }
   }

   private synchronized void updateCache(String uuid, String url, int cacheValidity) {
      if (cacheValidity <= 0) {
         cacheValidity = 1800;
      }

      cacheValidity *= 1000;
      if (this._cache.containsKey(uuid)) {
         YSSDPCacheEntry entry = this._cache.get(uuid);
         if (!entry.getURL().equals(url)) {
            this._callbacks.HubDiscoveryCallback(entry.getSerial(), url, entry.getURL());
            entry.setURL(url);
         } else if (entry.hasExpired()) {
            this._callbacks.HubDiscoveryCallback(entry.getSerial(), url, null);
         }

         entry.resetExpiration(cacheValidity);
      } else {
         YSSDPCacheEntry entry = new YSSDPCacheEntry(uuid, url, (long)cacheValidity);
         this._cache.put(uuid, entry);
         this._callbacks.HubDiscoveryCallback(entry.getSerial(), entry.getURL(), null);
      }
   }

   private synchronized void checkCacheExpiration() {
      ArrayList<String> to_remove = new ArrayList<>();

      for (YSSDPCacheEntry entry : this._cache.values()) {
         if (entry.hasExpired()) {
            this._callbacks.HubDiscoveryCallback(entry.getSerial(), null, entry.getURL());
            to_remove.add(entry.getUUID());
         }
      }

      for (String uuid : to_remove) {
         this._cache.remove(uuid);
      }
   }

   private void startListening() throws IOException {
      Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();

      for (NetworkInterface netint : Collections.list(nets)) {
         if (netint.isUp() && !netint.isLoopback() && netint.supportsMulticast()) {
            this._netInterfaces.add(netint);
         }
      }

      this.mMcastAddr = new InetSocketAddress(InetAddress.getByName("239.255.255.250"), 1900);
      int size = this._netInterfaces.size();
      this._listenBcastThread = new Thread[size];
      this._listenMSearchThread = new Thread[size];
      this._Listening = false;

      for (int i = 0; i < size; i++) {
         final NetworkInterface netIf = this._netInterfaces.get(i);
         String if_name = netIf.getDisplayName();
         this._listenBcastThread[i] = new Thread(new Runnable() {
            @Override
            public void run() {
               MulticastSocket socketReception = null;
               YSSDP.this._yctx._Log("Start listening for SSDP on " + netIf.getDisplayName());

               try {
                  socketReception = new MulticastSocket(1900);
                  socketReception.joinGroup(YSSDP.this.mMcastAddr, netIf);
                  socketReception.setSoTimeout(10000);
               } catch (IOException var8) {
                  YSSDP.this._yctx._Log(String.format(Locale.US, "Unable to join MCAST group for %s: %s", netIf.getName(), var8.getLocalizedMessage()));
                  var8.printStackTrace();
                  return;
               }

               for (; YSSDP.this._Listening; YSSDP.this.checkCacheExpiration()) {
                  byte[] pktContent = new byte[1536];
                  DatagramPacket pkt = new DatagramPacket(pktContent, pktContent.length);

                  try {
                     socketReception.receive(pkt);
                     String ssdpMessage = new String(pktContent, pkt.getOffset(), pkt.getLength());
                     YSSDP.this.parseIncomingMessage(ssdpMessage);
                  } catch (SocketTimeoutException var6) {
                  } catch (IOException var7) {
                     YSSDP.this._yctx._Log("SSDP:" + var7.getLocalizedMessage());
                  }
               }

               YSSDP.this._yctx._Log("Stop listening for SSDP on " + netIf.getDisplayName());
            }
         }, "ssdp_mcast_" + if_name);
         this._listenBcastThread[i].start();
         this._listenMSearchThread[i] = new Thread(new Runnable() {
            @Override
            public void run() {
               try {
                  YSSDP.this.doMSearch(netIf);
               } catch (InterruptedException | IOException var2) {
                  YSSDP.this._yctx._Log(String.format(Locale.US, "MSearch request failed (%s)", var2.getLocalizedMessage()));
                  var2.printStackTrace();
               }
            }
         }, "ssdp_msearch_" + if_name);
         this._listenMSearchThread[i].start();
      }

      this._Listening = true;
   }

   private void doMSearch(NetworkInterface netIf) throws IOException, InterruptedException {
      Random rand = new Random();
      this._yctx._Log("Start SSDP MSearch on " + netIf.getDisplayName());
      MulticastSocket msearchSocket = new MulticastSocket();
      msearchSocket.setTimeToLive(2);
      msearchSocket.setNetworkInterface(netIf);
      byte[] outPktContent = SSDP_DISCOVERY_MESSAGE.getBytes();
      DatagramPacket outPkt = new DatagramPacket(outPktContent, outPktContent.length, this.mMcastAddr);
      int response_delay = 20000;
      msearchSocket.setSoTimeout(response_delay);

      for (int i = 0; i < 5; i++) {
         msearchSocket.send(outPkt);
         int sleep_delay = rand.nextInt(100);
         Thread.sleep((long)sleep_delay);
      }

      long wait_until = System.currentTimeMillis() + (long)response_delay;

      while (this._Listening && System.currentTimeMillis() < wait_until) {
         byte[] pktContent = new byte[1536];
         DatagramPacket pkt = new DatagramPacket(pktContent, pktContent.length);

         try {
            msearchSocket.receive(pkt);
            String ssdpMessage = new String(pktContent, pkt.getOffset(), pkt.getLength());
            this.parseIncomingMessage(ssdpMessage);
         } catch (SocketTimeoutException var13) {
         }
      }

      this._yctx._Log("End of SSDP MSearch on " + netIf.getDisplayName());
   }

   void Stop() {
      this.stopThreads();
      this._cache.clear();
   }

   private void stopThreads() {
      this._Listening = false;

      for (int i = 0; i < this._netInterfaces.size(); i++) {
         if (this._listenMSearchThread[i].isAlive()) {
            this._listenMSearchThread[i].interrupt();
         }

         this._listenMSearchThread[i] = null;
         if (this._listenBcastThread[i].isAlive()) {
            this._listenBcastThread[i].interrupt();
         }

         this._listenBcastThread[i] = null;
      }
   }

   private void parseIncomingMessage(String message) {
      int i = 0;
      String location = null;
      String usn = null;
      String cache = null;
      String[] lines = message.split("\r\n");
      if (lines[i].equals("HTTP/1.1 200 OK") || lines[i].equals("NOTIFY * HTTP/1.1")) {
         for (int var14 = 0; var14 < lines.length; var14++) {
            int pos = lines[var14].indexOf(":");
            if (pos > 0) {
               if (lines[var14].startsWith("LOCATION")) {
                  location = lines[var14].substring(pos + 1).trim();
               } else if (lines[var14].startsWith("USN")) {
                  usn = lines[var14].substring(pos + 1).trim();
               } else if (lines[var14].startsWith("CACHE-CONTROL")) {
                  cache = lines[var14].substring(pos + 1).trim();
               }
            }
         }

         if (location != null && usn != null && cache != null) {
            int posuuid = usn.indexOf(58);
            if (posuuid < 0) {
               return;
            }

            int posurn = usn.indexOf("::", ++posuuid);
            if (posurn < 0) {
               return;
            }

            String uuid = usn.substring(posuuid, posurn).trim();
            String urn = usn.substring(posurn + 2).trim();
            if (!urn.equals("urn:yoctopuce-com:device:hub:1")) {
               return;
            }

            if (location.startsWith("http://")) {
               location = location.substring(7);
            }

            int posslash = location.indexOf(47);
            if (posslash > 0) {
               location = location.substring(0, posslash);
            }

            int poscache = cache.indexOf(61);
            if (poscache < 0) {
               return;
            }

            cache = cache.substring(poscache + 1).trim();
            int cacheVal = Integer.decode(cache);
            this.updateCache(uuid, location, cacheVal);
         }
      }
   }

   interface YSSDPReportInterface {
      void HubDiscoveryCallback(String var1, String var2, String var3);
   }
}
