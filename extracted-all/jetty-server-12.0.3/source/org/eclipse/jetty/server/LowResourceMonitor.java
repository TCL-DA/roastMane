package org.eclipse.jetty.server;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.eclipse.jetty.io.EndPoint;
import org.eclipse.jetty.util.annotation.ManagedAttribute;
import org.eclipse.jetty.util.annotation.ManagedObject;
import org.eclipse.jetty.util.annotation.Name;
import org.eclipse.jetty.util.component.ContainerLifeCycle;
import org.eclipse.jetty.util.thread.ScheduledExecutorScheduler;
import org.eclipse.jetty.util.thread.Scheduler;
import org.eclipse.jetty.util.thread.ThreadPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ManagedObject("Monitor for low resource conditions and activate a low resource mode if detected")
public class LowResourceMonitor extends ContainerLifeCycle {
   private static final Logger LOG = LoggerFactory.getLogger(LowResourceMonitor.class);
   protected final Server _server;
   private Scheduler _scheduler;
   private Connector[] _monitoredConnectors;
   private Set<AbstractConnector> _acceptingConnectors = new HashSet<>();
   private int _period = 1000;
   private int _lowResourcesIdleTimeout = 1000;
   private int _maxLowResourcesTime = 0;
   private final AtomicBoolean _low = new AtomicBoolean();
   private String _reasons;
   private long _lowStarted;
   private boolean _acceptingInLowResources = true;
   private Set<LowResourceMonitor.LowResourceCheck> _lowResourceChecks = new HashSet<>();
   private final Runnable _monitor = new Runnable() {
      @Override
      public void run() {
         if (LowResourceMonitor.this.isRunning()) {
            LowResourceMonitor.this.monitor();
            LowResourceMonitor.this._scheduler.schedule(LowResourceMonitor.this._monitor, (long)LowResourceMonitor.this._period, TimeUnit.MILLISECONDS);
         }
      }
   };

   public LowResourceMonitor(@Name("server") Server server) {
      this._server = server;
   }

   @ManagedAttribute("True if low available threads status is monitored")
   public boolean getMonitorThreads() {
      return !this.getBeans(LowResourceMonitor.ConnectorsThreadPoolLowResourceCheck.class).isEmpty();
   }

   public void setMonitorThreads(boolean monitorThreads) {
      if (monitorThreads) {
         if (!this.getMonitorThreads()) {
            this.addLowResourceCheck(new LowResourceMonitor.ConnectorsThreadPoolLowResourceCheck());
         } else {
            this.getBeans(LowResourceMonitor.ConnectorsThreadPoolLowResourceCheck.class).forEach(this::removeBean);
         }
      }
   }

   @ManagedAttribute("The reasons the monitored connectors are low on resources")
   public String getReasons() {
      return this._reasons;
   }

   protected void setReasons(String reasons) {
      this._reasons = reasons;
   }

   @ManagedAttribute("Are the monitored connectors low on resources?")
   public boolean isLowOnResources() {
      return this._low.get();
   }

   protected boolean enableLowOnResources(boolean expectedValue, boolean newValue) {
      return this._low.compareAndSet(expectedValue, newValue);
   }

   @ManagedAttribute("The reason(s) the monitored connectors are low on resources")
   public String getLowResourcesReasons() {
      return this._reasons;
   }

   protected void setLowResourcesReasons(String reasons) {
      this._reasons = reasons;
   }

   @ManagedAttribute("Get the timestamp in ms since epoch that low resources state started")
   public long getLowResourcesStarted() {
      return this._lowStarted;
   }

   public void setLowResourcesStarted(long lowStarted) {
      this._lowStarted = lowStarted;
   }

   @ManagedAttribute("The monitored connectors. If null then all server connectors are monitored")
   public Collection<Connector> getMonitoredConnectors() {
      return this._monitoredConnectors == null ? Collections.emptyList() : Arrays.asList(this._monitoredConnectors);
   }

   public void setMonitoredConnectors(Collection<Connector> monitoredConnectors) {
      if (monitoredConnectors != null && monitoredConnectors.size() != 0) {
         this._monitoredConnectors = monitoredConnectors.toArray(new Connector[monitoredConnectors.size()]);
      } else {
         this._monitoredConnectors = null;
      }
   }

   protected Connector[] getMonitoredOrServerConnectors() {
      return this._monitoredConnectors != null && this._monitoredConnectors.length > 0 ? this._monitoredConnectors : this._server.getConnectors();
   }

   @ManagedAttribute("If false, new connections are not accepted while in low resources")
   public boolean isAcceptingInLowResources() {
      return this._acceptingInLowResources;
   }

   public void setAcceptingInLowResources(boolean acceptingInLowResources) {
      this._acceptingInLowResources = acceptingInLowResources;
   }

   @ManagedAttribute("The monitor period in ms")
   public int getPeriod() {
      return this._period;
   }

   public void setPeriod(int periodMS) {
      this._period = periodMS;
   }

   @ManagedAttribute("The idletimeout in ms to apply to all existing connections when low resources is detected")
   public int getLowResourcesIdleTimeout() {
      return this._lowResourcesIdleTimeout;
   }

   public void setLowResourcesIdleTimeout(int lowResourcesIdleTimeoutMS) {
      this._lowResourcesIdleTimeout = lowResourcesIdleTimeoutMS;
   }

   @ManagedAttribute("The maximum time in ms that low resources condition can persist before lowResourcesIdleTimeout is applied to new connections as well as existing connections")
   public int getMaxLowResourcesTime() {
      return this._maxLowResourcesTime;
   }

   public void setMaxLowResourcesTime(int maxLowResourcesTimeMS) {
      this._maxLowResourcesTime = maxLowResourcesTimeMS;
   }

   @ManagedAttribute("The maximum memory (in bytes) that can be used before low resources is triggered.  Memory used is calculated as (totalMemory-freeMemory).")
   public long getMaxMemory() {
      Collection<LowResourceMonitor.MemoryLowResourceCheck> beans = this.getBeans(LowResourceMonitor.MemoryLowResourceCheck.class);
      return beans.isEmpty() ? 0L : beans.stream().findFirst().get().getMaxMemory();
   }

   public void setMaxMemory(long maxMemoryBytes) {
      if (maxMemoryBytes > 0L) {
         Collection<LowResourceMonitor.MemoryLowResourceCheck> beans = this.getBeans(LowResourceMonitor.MemoryLowResourceCheck.class);
         if (beans.isEmpty()) {
            this.addLowResourceCheck(new LowResourceMonitor.MemoryLowResourceCheck(maxMemoryBytes));
         } else {
            beans.forEach(lowResourceCheck -> lowResourceCheck.setMaxMemory(maxMemoryBytes));
         }
      }
   }

   public Set<LowResourceMonitor.LowResourceCheck> getLowResourceChecks() {
      return this._lowResourceChecks;
   }

   public void setLowResourceChecks(Set<LowResourceMonitor.LowResourceCheck> lowResourceChecks) {
      this.updateBeans(this._lowResourceChecks.toArray(), lowResourceChecks.toArray());
      this._lowResourceChecks = lowResourceChecks;
   }

   public void addLowResourceCheck(LowResourceMonitor.LowResourceCheck lowResourceCheck) {
      this.addBean(lowResourceCheck);
      this._lowResourceChecks.add(lowResourceCheck);
   }

   protected void monitor() {
      String reasons = null;

      for (LowResourceMonitor.LowResourceCheck lowResourceCheck : this._lowResourceChecks) {
         if (lowResourceCheck.isLowOnResources()) {
            reasons = lowResourceCheck.getReason();
            break;
         }
      }

      if (reasons != null) {
         if (!reasons.equals(this.getReasons())) {
            LOG.warn("Low Resources: {}", reasons);
            this.setReasons(reasons);
         }

         if (this.enableLowOnResources(false, true)) {
            this.setLowResourcesReasons(reasons);
            this.setLowResourcesStarted(System.currentTimeMillis());
            this.setLowResources();
         }

         if (this.getMaxLowResourcesTime() > 0 && System.currentTimeMillis() - this.getLowResourcesStarted() > (long)this.getMaxLowResourcesTime()) {
            this.setLowResources();
         }
      } else if (this.enableLowOnResources(true, false)) {
         LOG.info("Low Resources cleared");
         this.setLowResourcesReasons(null);
         this.setLowResourcesStarted(0L);
         this.setReasons(null);
         this.clearLowResources();
      }
   }

   protected void doStart() throws Exception {
      this._scheduler = this._server.getScheduler();
      if (this._scheduler == null) {
         this._scheduler = new LowResourceMonitor.LRMScheduler();
         this._scheduler.start();
      }

      super.doStart();
      this._scheduler.schedule(this._monitor, (long)this._period, TimeUnit.MILLISECONDS);
   }

   protected void doStop() throws Exception {
      if (this._scheduler instanceof LowResourceMonitor.LRMScheduler) {
         this._scheduler.stop();
      }

      super.doStop();
   }

   protected void setLowResources() {
      for (Connector connector : this.getMonitoredOrServerConnectors()) {
         if (connector instanceof AbstractConnector c && !this.isAcceptingInLowResources() && c.isAccepting()) {
            this._acceptingConnectors.add(c);
            c.setAccepting(false);
         }

         for (EndPoint endPoint : connector.getConnectedEndPoints()) {
            endPoint.setIdleTimeout((long)this._lowResourcesIdleTimeout);
         }
      }
   }

   protected void clearLowResources() {
      for (Connector connector : this.getMonitoredOrServerConnectors()) {
         for (EndPoint endPoint : connector.getConnectedEndPoints()) {
            endPoint.setIdleTimeout(connector.getIdleTimeout());
         }
      }

      for (AbstractConnector connector : this._acceptingConnectors) {
         connector.setAccepting(true);
      }

      this._acceptingConnectors.clear();
   }

   protected String low(String reasons, String newReason) {
      return reasons == null ? newReason : reasons + ", " + newReason;
   }

   public class ConnectorsThreadPoolLowResourceCheck implements LowResourceMonitor.LowResourceCheck {
      private String reason;

      @Override
      public boolean isLowOnResources() {
         ThreadPool serverThreads = LowResourceMonitor.this._server.getThreadPool();
         if (serverThreads.isLowOnThreads()) {
            this.reason = "Server low on threads: " + serverThreads.getThreads() + ", idleThreads:" + serverThreads.getIdleThreads();
            return true;
         } else {
            for (Connector connector : LowResourceMonitor.this.getMonitoredConnectors()) {
               Executor executor = connector.getExecutor();
               if (executor instanceof ThreadPool && executor != serverThreads) {
                  ThreadPool connectorThreads = (ThreadPool)executor;
                  if (connectorThreads.isLowOnThreads()) {
                     this.reason = "Connector low on threads: " + connectorThreads;
                     return true;
                  }
               }
            }

            return false;
         }
      }

      @Override
      public String getReason() {
         return this.reason;
      }

      @Override
      public String toString() {
         return "Check if the ThreadPool from monitored connectors are lowOnThreads and if all connections number is higher than the allowed maxConnection";
      }
   }

   private static class LRMScheduler extends ScheduledExecutorScheduler {
   }

   public interface LowResourceCheck {
      boolean isLowOnResources();

      String getReason();
   }

   public class MainThreadPoolLowResourceCheck implements LowResourceMonitor.LowResourceCheck {
      private String reason;

      @Override
      public boolean isLowOnResources() {
         ThreadPool serverThreads = LowResourceMonitor.this._server.getThreadPool();
         if (serverThreads.isLowOnThreads()) {
            this.reason = "Server low on threads: " + serverThreads;
            return true;
         } else {
            return false;
         }
      }

      @Override
      public String getReason() {
         return this.reason;
      }

      @Override
      public String toString() {
         return "Check if the server ThreadPool is lowOnThreads";
      }
   }

   @ManagedObject("Check max allowed connections on connectors")
   public class MaxConnectionsLowResourceCheck implements LowResourceMonitor.LowResourceCheck {
      private String reason;
      private int maxConnections;

      public MaxConnectionsLowResourceCheck(int maxConnections) {
         this.maxConnections = maxConnections;
      }

      @Override
      public boolean isLowOnResources() {
         int connections = 0;

         for (Connector connector : LowResourceMonitor.this.getMonitoredConnectors()) {
            connections += connector.getConnectedEndPoints().size();
         }

         if (this.maxConnections > 0 && connections > this.maxConnections) {
            this.reason = "Max Connections exceeded: " + connections + ">" + this.maxConnections;
            return true;
         } else {
            return false;
         }
      }

      @Override
      public String getReason() {
         return this.reason;
      }

      @Override
      public String toString() {
         return "All connections number is higher than the allowed maxConnection";
      }
   }

   public class MemoryLowResourceCheck implements LowResourceMonitor.LowResourceCheck {
      private String reason;
      private long maxMemory;

      public MemoryLowResourceCheck(long maxMemory) {
         this.maxMemory = maxMemory;
      }

      @Override
      public boolean isLowOnResources() {
         long memory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
         if (this.maxMemory > 0L && memory > this.maxMemory) {
            this.reason = "Max memory exceeded: " + memory + ">" + this.maxMemory;
            return true;
         } else {
            return false;
         }
      }

      public long getMaxMemory() {
         return this.maxMemory;
      }

      public void setMaxMemory(long maxMemoryBytes) {
         this.maxMemory = maxMemoryBytes;
      }

      @Override
      public String getReason() {
         return this.reason;
      }

      @Override
      public String toString() {
         return "Check if used memory is higher than the allowed max memory";
      }
   }
}
