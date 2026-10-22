package org.glassfish.jersey.server.monitoring;

import jakarta.ws.rs.ConstrainedTo;
import jakarta.ws.rs.RuntimeType;
import org.glassfish.jersey.spi.Contract;

@Contract
@ConstrainedTo(RuntimeType.SERVER)
public interface MonitoringStatisticsListener {
   void onStatistics(MonitoringStatistics var1);
}
