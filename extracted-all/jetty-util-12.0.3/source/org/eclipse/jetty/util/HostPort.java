package org.eclipse.jetty.util;

import java.net.InetAddress;
import org.eclipse.jetty.util.annotation.ManagedAttribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HostPort {
   private static final Logger LOG = LoggerFactory.getLogger(HostPort.class);
   private static final int BAD_PORT = -1;
   private final String _host;
   private final int _port;

   public static HostPort unsafe(String authority) {
      return new HostPort(authority, true);
   }

   public HostPort(String host, int port) {
      this._host = normalizeHost(host);
      this._port = port;
   }

   public HostPort(String authority) throws IllegalArgumentException {
      this(authority, false);
   }

   private HostPort(String authority, boolean unsafe) {
      int port = 0;
      if (authority == null) {
         LOG.warn("Bad Authority [<null>]");
         if (!unsafe) {
            throw new IllegalArgumentException("No Authority");
         } else {
            this._host = "";
            this._port = 0;
         }
      } else if (authority.isEmpty()) {
         this._host = authority;
         this._port = 0;
      } else {
         String host;
         try {
            if (authority.charAt(0) == '[') {
               int close = authority.lastIndexOf(93);
               if (close < 0) {
                  LOG.warn("Bad IPv6 host: [{}]", authority);
                  if (!unsafe) {
                     throw new IllegalArgumentException("Bad IPv6 host");
                  }

                  host = authority;
               } else {
                  host = authority.substring(0, close + 1);
               }

               if (!this.isValidIpAddress(host)) {
                  LOG.warn("Bad IPv6 host: [{}]", host);
                  if (!unsafe) {
                     throw new IllegalArgumentException("Bad IPv6 host");
                  }
               }

               if (authority.length() > close + 1) {
                  if (authority.charAt(close + 1) != ':') {
                     LOG.warn("Bad IPv6 port: [{}]", authority);
                     if (!unsafe) {
                        throw new IllegalArgumentException("Bad IPv6 port");
                     }

                     host = authority;
                     port = 0;
                  } else {
                     port = this.parsePort(authority.substring(close + 2), unsafe);
                     if (unsafe && port == -1) {
                        host = authority;
                        port = 0;
                     }
                  }
               } else {
                  port = 0;
               }
            } else {
               int c = authority.lastIndexOf(58);
               if (c >= 0) {
                  if (c != authority.indexOf(58)) {
                     port = 0;
                     host = "[" + authority + "]";
                     if (!this.isValidIpAddress(host)) {
                        LOG.warn("Bad IPv6Address: [{}]", host);
                        if (!unsafe) {
                           throw new IllegalArgumentException("Bad IPv6 host");
                        }

                        host = authority;
                     }
                  } else {
                     host = authority.substring(0, c);
                     if (StringUtil.isBlank(host)) {
                        LOG.warn("Bad Authority: [{}]", host);
                        if (!unsafe) {
                           throw new IllegalArgumentException("Bad Authority");
                        }

                        host = "";
                     } else if (!this.isValidHostName(host)) {
                        LOG.warn("Bad Authority: [{}]", host);
                        if (!unsafe) {
                           throw new IllegalArgumentException("Bad Authority");
                        }

                        host = authority;
                     }

                     port = this.parsePort(authority.substring(c + 1), unsafe);
                     if (unsafe && port == -1) {
                        host = authority;
                        port = 0;
                     }
                  }
               } else {
                  host = authority;
                  if (StringUtil.isBlank(authority) || !this.isValidHostName(authority)) {
                     LOG.warn("Bad Authority: [{}]", authority);
                     if (!unsafe) {
                        throw new IllegalArgumentException("Bad Authority");
                     }
                  }

                  port = 0;
               }
            }
         } catch (IllegalArgumentException var6) {
            if (!unsafe) {
               throw var6;
            }

            host = authority;
            port = 0;
         } catch (Exception var7) {
            if (!unsafe) {
               throw new IllegalArgumentException("Bad HostPort", var7);
            }

            host = authority;
            port = 0;
         }

         this._host = host;
         this._port = port;
      }
   }

   protected boolean isValidIpAddress(String ip) {
      try {
         InetAddress.getByName(ip);
         return true;
      } catch (Throwable var3) {
         return false;
      }
   }

   protected boolean isValidHostName(String name) {
      return URIUtil.isValidHostRegisteredName(name);
   }

   @ManagedAttribute("host")
   public String getHost() {
      return this._host;
   }

   @ManagedAttribute("port")
   public int getPort() {
      return this._port;
   }

   public int getPort(int defaultPort) {
      return this._port > 0 ? this._port : defaultPort;
   }

   public boolean hasHost() {
      return StringUtil.isNotBlank(this._host);
   }

   public boolean hasPort() {
      return this._port > 0;
   }

   @Override
   public String toString() {
      return this._port > 0 ? this._host + ":" + this._port : this._host;
   }

   public static String normalizeHost(String host) {
      return host != null && !host.isEmpty() && host.charAt(0) != '[' && host.indexOf(58) >= 0 ? "[" + host + "]" : host;
   }

   public static int parsePort(String rawPort) throws IllegalArgumentException {
      if (StringUtil.isEmpty(rawPort)) {
         throw new IllegalArgumentException("Bad port");
      } else {
         int port = Integer.parseInt(rawPort);
         if (port > 0 && port <= 65535) {
            return port;
         } else {
            throw new IllegalArgumentException("Bad port");
         }
      }
   }

   private int parsePort(String rawPort, boolean unsafe) {
      if (StringUtil.isEmpty(rawPort)) {
         if (!unsafe) {
            throw new IllegalArgumentException("Bad port [" + rawPort + "]");
         } else {
            return 0;
         }
      } else {
         try {
            int port = Integer.parseInt(rawPort);
            if (port > 0 && port <= 65535) {
               return port;
            } else {
               LOG.warn("Bad port [{}]", port);
               if (!unsafe) {
                  throw new IllegalArgumentException("Bad port");
               } else {
                  return -1;
               }
            }
         } catch (NumberFormatException var4) {
            LOG.warn("Bad port [{}]", rawPort);
            if (!unsafe) {
               throw new IllegalArgumentException("Bad Port");
            } else {
               return -1;
            }
         }
      }
   }
}
