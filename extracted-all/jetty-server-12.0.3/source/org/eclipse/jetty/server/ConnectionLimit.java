package org.eclipse.jetty.server;

import java.nio.channels.SelectableChannel;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.jetty.io.Connection;
import org.eclipse.jetty.io.EndPoint;
import org.eclipse.jetty.io.Connection.Listener;
import org.eclipse.jetty.io.SelectorManager.AcceptListener;
import org.eclipse.jetty.util.annotation.ManagedAttribute;
import org.eclipse.jetty.util.annotation.ManagedObject;
import org.eclipse.jetty.util.annotation.Name;
import org.eclipse.jetty.util.component.AbstractLifeCycle;
import org.eclipse.jetty.util.thread.AutoLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ManagedObject
public class ConnectionLimit extends AbstractLifeCycle implements Listener, AcceptListener {
   private static final Logger LOG = LoggerFactory.getLogger(ConnectionLimit.class);
   private final AutoLock _lock = new AutoLock();
   private final Server _server;
   private final List<AbstractConnector> _connectors = new ArrayList<>();
   private final Set<SelectableChannel> _accepting = new HashSet<>();
   private int _connections;
   private int _maxConnections;
   private long _idleTimeout;
   private boolean _limiting = false;

   public ConnectionLimit(@Name("maxConnections") int maxConnections, @Name("server") Server server) {
      this._maxConnections = maxConnections;
      this._server = server;
   }

   public ConnectionLimit(@Name("maxConnections") int maxConnections, @Name("connectors") Connector... connectors) {
      this(maxConnections, (Server)null);

      for (Connector c : connectors) {
         if (c instanceof AbstractConnector) {
            this._connectors.add((AbstractConnector)c);
         } else {
            LOG.warn("Connector {} is not an AbstractConnection. Connections not limited", c);
         }
      }
   }

   @ManagedAttribute("The endpoint idle timeout in ms to apply when the connection limit is reached")
   public long getIdleTimeout() {
      return this._idleTimeout;
   }

   public void setIdleTimeout(long idleTimeout) {
      this._idleTimeout = idleTimeout;
   }

   @ManagedAttribute("The maximum number of connections allowed")
   public int getMaxConnections() {
      AutoLock l = this._lock.lock();

      int var2;
      try {
         var2 = this._maxConnections;
      } catch (Throwable var5) {
         if (l != null) {
            try {
               l.close();
            } catch (Throwable var4) {
               var5.addSuppressed(var4);
            }
         }

         throw var5;
      }

      if (l != null) {
         l.close();
      }

      return var2;
   }

   public void setMaxConnections(int max) {
      AutoLock l = this._lock.lock();

      try {
         this._maxConnections = max;
      } catch (Throwable var6) {
         if (l != null) {
            try {
               l.close();
            } catch (Throwable var5) {
               var6.addSuppressed(var5);
            }
         }

         throw var6;
      }

      if (l != null) {
         l.close();
      }
   }

   @ManagedAttribute("The current number of connections ")
   public int getConnections() {
      AutoLock l = this._lock.lock();

      int var2;
      try {
         var2 = this._connections;
      } catch (Throwable var5) {
         if (l != null) {
            try {
               l.close();
            } catch (Throwable var4) {
               var5.addSuppressed(var4);
            }
         }

         throw var5;
      }

      if (l != null) {
         l.close();
      }

      return var2;
   }

   protected void doStart() throws Exception {
      AutoLock l = this._lock.lock();

      try {
         if (this._server != null) {
            for (Connector c : this._server.getConnectors()) {
               if (c instanceof AbstractConnector) {
                  this._connectors.add((AbstractConnector)c);
               } else {
                  LOG.warn("Connector {} is not an AbstractConnector. Connections not limited", c);
               }
            }
         }

         if (LOG.isDebugEnabled()) {
            LOG.debug("ConnectionLimit {} for {}", this._maxConnections, this._connectors);
         }

         this._connections = 0;
         this._limiting = false;

         for (AbstractConnector cx : this._connectors) {
            cx.addBean(this);
         }
      } catch (Throwable var7) {
         if (l != null) {
            try {
               l.close();
            } catch (Throwable var6) {
               var7.addSuppressed(var6);
            }
         }

         throw var7;
      }

      if (l != null) {
         l.close();
      }
   }

   protected void doStop() throws Exception {
      AutoLock l = this._lock.lock();

      try {
         for (AbstractConnector c : this._connectors) {
            c.removeBean(this);
         }

         this._connections = 0;
         if (this._server != null) {
            this._connectors.clear();
         }
      } catch (Throwable var5) {
         if (l != null) {
            try {
               l.close();
            } catch (Throwable var4) {
               var5.addSuppressed(var4);
            }
         }

         throw var5;
      }

      if (l != null) {
         l.close();
      }
   }

   protected void check() {
      if (this._accepting.size() + this._connections >= this._maxConnections) {
         if (!this._limiting) {
            this._limiting = true;
            LOG.info("Connection Limit({}) reached for {}", this._maxConnections, this._connectors);
            this.limit();
         }
      } else if (this._limiting) {
         this._limiting = false;
         LOG.info("Connection Limit({}) cleared for {}", this._maxConnections, this._connectors);
         this.unlimit();
      }
   }

   protected void limit() {
      for (AbstractConnector c : this._connectors) {
         c.setAccepting(false);
         if (this._idleTimeout > 0L) {
            for (EndPoint endPoint : c.getConnectedEndPoints()) {
               endPoint.setIdleTimeout(this._idleTimeout);
            }
         }
      }
   }

   protected void unlimit() {
      for (AbstractConnector c : this._connectors) {
         c.setAccepting(true);
         if (this._idleTimeout > 0L) {
            for (EndPoint endPoint : c.getConnectedEndPoints()) {
               endPoint.setIdleTimeout(c.getIdleTimeout());
            }
         }
      }
   }

   public void onAccepting(SelectableChannel channel) {
      AutoLock l = this._lock.lock();

      try {
         this._accepting.add(channel);
         if (LOG.isDebugEnabled()) {
            LOG.debug("onAccepting ({}+{}) < {} {}", new Object[]{this._accepting.size(), this._connections, this._maxConnections, channel});
         }

         this.check();
      } catch (Throwable var6) {
         if (l != null) {
            try {
               l.close();
            } catch (Throwable var5) {
               var6.addSuppressed(var5);
            }
         }

         throw var6;
      }

      if (l != null) {
         l.close();
      }
   }

   public void onAcceptFailed(SelectableChannel channel, Throwable cause) {
      AutoLock l = this._lock.lock();

      try {
         this._accepting.remove(channel);
         if (LOG.isDebugEnabled()) {
            LOG.debug("onAcceptFailed ({}+{}) < {} {} {}", new Object[]{this._accepting.size(), this._connections, this._maxConnections, channel, cause});
         }

         this.check();
      } catch (Throwable var7) {
         if (l != null) {
            try {
               l.close();
            } catch (Throwable var6) {
               var7.addSuppressed(var6);
            }
         }

         throw var7;
      }

      if (l != null) {
         l.close();
      }
   }

   public void onAccepted(SelectableChannel channel) {
   }

   public void onOpened(Connection connection) {
      AutoLock l = this._lock.lock();

      try {
         this._accepting.remove(connection.getEndPoint().getTransport());
         this._connections++;
         if (LOG.isDebugEnabled()) {
            LOG.debug("onOpened ({}+{}) < {} {}", new Object[]{this._accepting.size(), this._connections, this._maxConnections, connection});
         }

         this.check();
      } catch (Throwable var6) {
         if (l != null) {
            try {
               l.close();
            } catch (Throwable var5) {
               var6.addSuppressed(var5);
            }
         }

         throw var6;
      }

      if (l != null) {
         l.close();
      }
   }

   public void onClosed(Connection connection) {
      AutoLock l = this._lock.lock();

      try {
         this._connections--;
         if (LOG.isDebugEnabled()) {
            LOG.debug("onClosed ({}+{}) < {} {}", new Object[]{this._accepting.size(), this._connections, this._maxConnections, connection});
         }

         this.check();
      } catch (Throwable var6) {
         if (l != null) {
            try {
               l.close();
            } catch (Throwable var5) {
               var6.addSuppressed(var5);
            }
         }

         throw var6;
      }

      if (l != null) {
         l.close();
      }
   }
}
