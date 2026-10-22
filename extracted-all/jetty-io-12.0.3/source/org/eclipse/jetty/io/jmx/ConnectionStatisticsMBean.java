package org.eclipse.jetty.io.jmx;

import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.stream.Collectors;
import org.eclipse.jetty.io.ConnectionStatistics;
import org.eclipse.jetty.jmx.ObjectMBean;
import org.eclipse.jetty.util.annotation.ManagedAttribute;
import org.eclipse.jetty.util.annotation.ManagedObject;

@ManagedObject
public class ConnectionStatisticsMBean extends ObjectMBean {
   public ConnectionStatisticsMBean(Object object) {
      super(object);
   }

   @ManagedAttribute("ConnectionStatistics grouped by connection class")
   public Collection<String> getConnectionStatisticsGroups() {
      ConnectionStatistics delegate = (ConnectionStatistics)this.getManagedObject();
      Map<String, ConnectionStatistics.Stats> groups = delegate.getConnectionStatisticsGroups();
      return groups.values()
         .stream()
         .sorted(Comparator.comparing(ConnectionStatistics.Stats::getName))
         .map(stats -> stats.dump())
         .map(dump -> dump.replaceAll("[\r\n]", " "))
         .collect(Collectors.toList());
   }
}
