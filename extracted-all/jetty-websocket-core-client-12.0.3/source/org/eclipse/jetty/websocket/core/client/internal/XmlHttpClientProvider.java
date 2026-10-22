package org.eclipse.jetty.websocket.core.client.internal;

import java.net.URL;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.util.resource.ResourceFactory;
import org.eclipse.jetty.util.resource.ResourceFactory.Closeable;
import org.eclipse.jetty.xml.XmlConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class XmlHttpClientProvider implements HttpClientProvider {
   private static final Logger LOG = LoggerFactory.getLogger(XmlHttpClientProvider.class);

   @Override
   public HttpClient newHttpClient() {
      ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
      if (contextClassLoader == null) {
         return null;
      } else {
         URL resource = contextClassLoader.getResource("jetty-websocket-httpclient.xml");
         if (resource == null) {
            return null;
         } else {
            HttpClient var3;
            try {
               Thread.currentThread().setContextClassLoader(HttpClient.class.getClassLoader());
               var3 = newHttpClient(resource);
            } finally {
               Thread.currentThread().setContextClassLoader(contextClassLoader);
            }

            return var3;
         }
      }
   }

   private static HttpClient newHttpClient(URL resource) {
      try {
         Closeable resourceFactory = ResourceFactory.closeable();

         HttpClient var3;
         try {
            XmlConfiguration configuration = new XmlConfiguration(resourceFactory.newResource(resource));
            var3 = (HttpClient)configuration.configure();
         } catch (Throwable var5) {
            if (resourceFactory != null) {
               try {
                  resourceFactory.close();
               } catch (Throwable var4) {
                  var5.addSuppressed(var4);
               }
            }

            throw var5;
         }

         if (resourceFactory != null) {
            resourceFactory.close();
         }

         return var3;
      } catch (Throwable var6) {
         LOG.warn("Failure to load HttpClient from XML {}", resource, var6);
         return null;
      }
   }
}
