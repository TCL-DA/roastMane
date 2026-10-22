package org.eclipse.jetty.client.jmx;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.jmx.ObjectMBean;

public class HttpClientMBean extends ObjectMBean {
   public HttpClientMBean(Object managedObject) {
      super(managedObject);
   }

   public String getObjectContextBasis() {
      HttpClient httpClient = (HttpClient)this.getManagedObject();
      return httpClient.getName();
   }
}
