package org.apache.logging.log4j.core.jmx;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import javax.management.MBeanNotificationInfo;
import javax.management.MBeanRegistration;
import javax.management.MBeanServer;
import javax.management.Notification;
import javax.management.NotificationBroadcasterSupport;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;
import javax.management.ObjectName;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.status.StatusData;
import org.apache.logging.log4j.status.StatusListener;
import org.apache.logging.log4j.status.StatusLogger;

public class StatusLoggerAdmin extends NotificationBroadcasterSupport implements StatusListener, StatusLoggerAdminMBean, MBeanRegistration {
   private final AtomicLong sequenceNo = new AtomicLong();
   private final ObjectName objectName;
   private final String contextName;
   private Level level = Level.WARN;
   private boolean statusListenerRegistered = false;
   private final Lock statusListenerRegistrationGuard = new ReentrantLock();

   public StatusLoggerAdmin(final String contextName, final Executor executor) {
      super(executor, createNotificationInfo());
      this.contextName = contextName;

      try {
         String mbeanName = String.format("org.apache.logging.log4j2:type=%s,component=StatusLogger", Server.escape(contextName));
         this.objectName = new ObjectName(mbeanName);
      } catch (Exception var4) {
         throw new IllegalStateException(var4);
      }
   }

   private static MBeanNotificationInfo createNotificationInfo() {
      String[] notifTypes = new String[]{"com.apache.logging.log4j.core.jmx.statuslogger.data", "com.apache.logging.log4j.core.jmx.statuslogger.message"};
      String name = Notification.class.getName();
      String description = "StatusLogger has logged an event";
      return new MBeanNotificationInfo(notifTypes, name, "StatusLogger has logged an event");
   }

   @Override
   public void addNotificationListener(final NotificationListener listener, final NotificationFilter filter, final Object handback) {
      super.addNotificationListener(listener, filter, handback);
      this.statusListenerRegistrationGuard.lock();

      try {
         if (!this.statusListenerRegistered) {
            StatusLogger.getLogger().registerListener(this);
            this.statusListenerRegistered = true;
         }
      } finally {
         this.statusListenerRegistrationGuard.unlock();
      }
   }

   @Override
   public ObjectName preRegister(final MBeanServer server, final ObjectName name) {
      return name;
   }

   @Override
   public void postRegister(final Boolean registrationDone) {
   }

   @Override
   public void preDeregister() {
   }

   @Override
   public void postDeregister() {
      this.statusListenerRegistrationGuard.lock();

      try {
         if (this.statusListenerRegistered) {
            StatusLogger.getLogger().removeListener(this);
            this.statusListenerRegistered = false;
         }
      } finally {
         this.statusListenerRegistrationGuard.unlock();
      }
   }

   @Override
   public String[] getStatusDataHistory() {
      List<StatusData> data = this.getStatusData();
      String[] result = new String[data.size()];

      for (int i = 0; i < result.length; i++) {
         result[i] = data.get(i).getFormattedStatus();
      }

      return result;
   }

   @Override
   public List<StatusData> getStatusData() {
      return StatusLogger.getLogger().getStatusData();
   }

   @Override
   public String getLevel() {
      return this.level.name();
   }

   public Level getStatusLevel() {
      return this.level;
   }

   @Override
   public void setLevel(final String level) {
      this.level = Level.toLevel(level, Level.ERROR);
   }

   @Override
   public String getContextName() {
      return this.contextName;
   }

   public void log(final StatusData data) {
      Notification notifMsg = new Notification(
         "com.apache.logging.log4j.core.jmx.statuslogger.message", this.getObjectName(), this.nextSeqNo(), this.nowMillis(), data.getFormattedStatus()
      );
      this.sendNotification(notifMsg);
      Notification notifData = new Notification("com.apache.logging.log4j.core.jmx.statuslogger.data", this.getObjectName(), this.nextSeqNo(), this.nowMillis());
      notifData.setUserData(data);
      this.sendNotification(notifData);
   }

   @Override
   public ObjectName getObjectName() {
      return this.objectName;
   }

   private long nextSeqNo() {
      return this.sequenceNo.getAndIncrement();
   }

   private long nowMillis() {
      return System.currentTimeMillis();
   }

   public void close() throws IOException {
   }
}
