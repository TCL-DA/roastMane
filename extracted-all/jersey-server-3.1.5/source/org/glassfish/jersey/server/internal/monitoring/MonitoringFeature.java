package org.glassfish.jersey.server.internal.monitoring;

import jakarta.inject.Inject;
import jakarta.inject.Provider;
import jakarta.inject.Singleton;
import jakarta.ws.rs.core.Feature;
import jakarta.ws.rs.core.FeatureContext;
import jakarta.ws.rs.core.GenericType;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.glassfish.jersey.internal.inject.AbstractBinder;
import org.glassfish.jersey.internal.inject.ClassBinding;
import org.glassfish.jersey.internal.inject.ReferencingFactory;
import org.glassfish.jersey.internal.inject.SupplierInstanceBinding;
import org.glassfish.jersey.internal.util.collection.Ref;
import org.glassfish.jersey.server.ServerProperties;
import org.glassfish.jersey.server.internal.LocalizationMessages;
import org.glassfish.jersey.server.internal.monitoring.jmx.MBeanExposer;
import org.glassfish.jersey.server.monitoring.ApplicationInfo;
import org.glassfish.jersey.server.monitoring.MonitoringStatistics;
import org.glassfish.jersey.server.monitoring.MonitoringStatisticsListener;

public final class MonitoringFeature implements Feature {
   private static final Logger LOGGER = Logger.getLogger(MonitoringFeature.class.getName());
   private boolean monitoringEnabled = true;
   private boolean statisticsEnabled = true;
   private boolean mBeansEnabled;

   public boolean configure(FeatureContext context) {
      Boolean monitoringEnabledProperty = ServerProperties.getValue(
         context.getConfiguration().getProperties(), "jersey.config.server.monitoring.enabled", null, Boolean.class
      );
      Boolean statisticsEnabledProperty = ServerProperties.getValue(
         context.getConfiguration().getProperties(), "jersey.config.server.monitoring.statistics.enabled", null, Boolean.class
      );
      Boolean mbeansEnabledProperty = ServerProperties.getValue(
         context.getConfiguration().getProperties(), "jersey.config.server.monitoring.statistics.mbeans.enabled", null, Boolean.class
      );
      if (monitoringEnabledProperty != null) {
         this.monitoringEnabled = monitoringEnabledProperty;
         this.statisticsEnabled = this.monitoringEnabled;
      }

      if (statisticsEnabledProperty != null) {
         this.monitoringEnabled = this.monitoringEnabled || statisticsEnabledProperty;
         this.statisticsEnabled = statisticsEnabledProperty;
      }

      if (mbeansEnabledProperty != null) {
         this.monitoringEnabled = this.monitoringEnabled || mbeansEnabledProperty;
         this.statisticsEnabled = this.statisticsEnabled || mbeansEnabledProperty;
         this.mBeansEnabled = mbeansEnabledProperty;
      }

      if (statisticsEnabledProperty != null && !statisticsEnabledProperty) {
         if (mbeansEnabledProperty != null && this.mBeansEnabled) {
            LOGGER.log(Level.WARNING, LocalizationMessages.WARNING_MONITORING_FEATURE_ENABLED("jersey.config.server.monitoring.statistics.enabled"));
         } else {
            LOGGER.log(Level.WARNING, LocalizationMessages.WARNING_MONITORING_FEATURE_DISABLED("jersey.config.server.monitoring.statistics.enabled"));
         }
      }

      if (this.monitoringEnabled) {
         context.register(ApplicationInfoListener.class);
         context.register(new AbstractBinder() {
            protected void configure() {
               ((SupplierInstanceBinding)this.bindFactory(ReferencingFactory.referenceFactory()).to(new GenericType<Ref<ApplicationInfo>>() {
               })).in(Singleton.class);
               this.bindFactory(MonitoringFeature.ApplicationInfoInjectionFactory.class).to(ApplicationInfo.class);
            }
         });
      }

      if (this.statisticsEnabled) {
         context.register(MonitoringEventListener.class);
         context.register(new AbstractBinder() {
            protected void configure() {
               ((SupplierInstanceBinding)this.bindFactory(ReferencingFactory.referenceFactory()).to(new GenericType<Ref<MonitoringStatistics>>() {
               })).in(Singleton.class);
               this.bindFactory(MonitoringFeature.StatisticsInjectionFactory.class).to(MonitoringStatistics.class);
               ((ClassBinding)this.bind(MonitoringFeature.StatisticsListener.class).to(MonitoringStatisticsListener.class)).in(Singleton.class);
            }
         });
      }

      if (this.mBeansEnabled) {
         context.register(new MBeanExposer());
      }

      return this.monitoringEnabled;
   }

   public void setmBeansEnabled(boolean mBeansEnabled) {
      this.mBeansEnabled = mBeansEnabled;
   }

   private static class ApplicationInfoInjectionFactory extends ReferencingFactory<ApplicationInfo> {
      @Inject
      public ApplicationInfoInjectionFactory(Provider<Ref<ApplicationInfo>> referenceFactory) {
         super(referenceFactory);
      }
   }

   private static class StatisticsInjectionFactory extends ReferencingFactory<MonitoringStatistics> {
      @Inject
      public StatisticsInjectionFactory(Provider<Ref<MonitoringStatistics>> referenceFactory) {
         super(referenceFactory);
      }

      public MonitoringStatistics get() {
         return (MonitoringStatistics)super.get();
      }
   }

   private static class StatisticsListener implements MonitoringStatisticsListener {
      @Inject
      Provider<Ref<MonitoringStatistics>> statisticsFactory;

      @Override
      public void onStatistics(MonitoringStatistics statistics) {
         ((Ref)this.statisticsFactory.get()).set(statistics);
      }
   }
}
