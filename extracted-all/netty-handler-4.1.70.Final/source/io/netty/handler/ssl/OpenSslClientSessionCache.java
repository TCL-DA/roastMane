package io.netty.handler.ssl;

import io.netty.internal.tcnative.SSL;
import io.netty.util.AsciiString;
import java.util.HashMap;
import java.util.Map;

final class OpenSslClientSessionCache extends OpenSslSessionCache {
   private final Map<OpenSslClientSessionCache.HostPort, OpenSslSessionCache.NativeSslSession> sessions = new HashMap<>();

   OpenSslClientSessionCache(OpenSslEngineMap engineMap) {
      super(engineMap);
   }

   @Override
   protected boolean sessionCreated(OpenSslSessionCache.NativeSslSession session) {
      assert Thread.holdsLock(this);

      OpenSslClientSessionCache.HostPort hostPort = keyFor(session.getPeerHost(), session.getPeerPort());
      if (hostPort != null && !this.sessions.containsKey(hostPort)) {
         this.sessions.put(hostPort, session);
         return true;
      } else {
         return false;
      }
   }

   @Override
   protected void sessionRemoved(OpenSslSessionCache.NativeSslSession session) {
      assert Thread.holdsLock(this);

      OpenSslClientSessionCache.HostPort hostPort = keyFor(session.getPeerHost(), session.getPeerPort());
      if (hostPort != null) {
         this.sessions.remove(hostPort);
      }
   }

   @Override
   void setSession(long ssl, String host, int port) {
      OpenSslClientSessionCache.HostPort hostPort = keyFor(host, port);
      if (hostPort != null) {
         OpenSslSessionCache.NativeSslSession session;
         boolean reused;
         synchronized (this) {
            session = this.sessions.get(hostPort);
            if (session == null) {
               return;
            }

            if (!session.isValid()) {
               this.removeSessionWithId(session.sessionId());
               return;
            }

            reused = SSL.setSession(ssl, session.session());
         }

         if (reused) {
            if (session.shouldBeSingleUse()) {
               session.invalidate();
            }

            session.updateLastAccessedTime();
         }
      }
   }

   private static OpenSslClientSessionCache.HostPort keyFor(String host, int port) {
      return host == null && port < 1 ? null : new OpenSslClientSessionCache.HostPort(host, port);
   }

   @Override
   synchronized void clear() {
      super.clear();
      this.sessions.clear();
   }

   private static final class HostPort {
      private final int hash;
      private final String host;
      private final int port;

      HostPort(String host, int port) {
         this.host = host;
         this.port = port;
         this.hash = 31 * AsciiString.hashCode(host) + port;
      }

      @Override
      public int hashCode() {
         return this.hash;
      }

      @Override
      public boolean equals(Object obj) {
         if (!(obj instanceof OpenSslClientSessionCache.HostPort)) {
            return false;
         } else {
            OpenSslClientSessionCache.HostPort other = (OpenSslClientSessionCache.HostPort)obj;
            return this.port == other.port && this.host.equalsIgnoreCase(other.host);
         }
      }

      @Override
      public String toString() {
         return "HostPort{host='" + this.host + '\'' + ", port=" + this.port + '}';
      }
   }
}
