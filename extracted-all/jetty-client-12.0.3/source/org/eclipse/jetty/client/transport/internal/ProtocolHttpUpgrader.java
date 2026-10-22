package org.eclipse.jetty.client.transport.internal;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.jetty.client.Destination;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.HttpResponseException;
import org.eclipse.jetty.client.HttpUpgrader;
import org.eclipse.jetty.client.Origin;
import org.eclipse.jetty.client.Request;
import org.eclipse.jetty.client.Response;
import org.eclipse.jetty.client.transport.HttpClientTransportDynamic;
import org.eclipse.jetty.client.transport.HttpDestination;
import org.eclipse.jetty.client.transport.HttpResponse;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.io.EndPoint;
import org.eclipse.jetty.util.Callback;
import org.eclipse.jetty.util.Promise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProtocolHttpUpgrader implements HttpUpgrader {
   private static final Logger LOG = LoggerFactory.getLogger(ProtocolHttpUpgrader.class);
   private final HttpDestination destination;
   private final String protocol;

   public ProtocolHttpUpgrader(HttpDestination destination, String protocol) {
      this.destination = destination;
      this.protocol = protocol;
   }

   @Override
   public void prepare(Request request) {
   }

   @Override
   public void upgrade(Response response, EndPoint endPoint, Callback callback) {
      if (response.getHeaders().contains(HttpHeader.UPGRADE, this.protocol)) {
         HttpClient httpClient = this.destination.getHttpClient();
         if (httpClient.getTransport() instanceof HttpClientTransportDynamic dynamicTransport) {
            Origin origin = this.destination.getOrigin();
            Origin newOrigin = new Origin(origin.getScheme(), origin.getAddress(), origin.getTag(), new Origin.Protocol(List.of(this.protocol), false));
            Destination newDestination = httpClient.resolveDestination(newOrigin);
            Map<String, Object> context = new HashMap<>();
            context.put("org.eclipse.jetty.client.destination", newDestination);
            context.put(HttpResponse.class.getName(), response);
            context.put("org.eclipse.jetty.client.connection.promise", Promise.from(y -> callback.succeeded(), callback::failed));
            if (LOG.isDebugEnabled()) {
               LOG.debug("Upgrading {} on {}", response.getRequest(), endPoint);
            }

            dynamicTransport.upgrade(endPoint, context);
         } else {
            callback.failed(new HttpResponseException(HttpClientTransportDynamic.class.getName() + " required to upgrade to: " + this.protocol, response));
         }
      } else {
         callback.failed(new HttpResponseException("Not an upgrade to: " + this.protocol, response));
      }
   }
}
