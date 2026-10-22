package org.glassfish.jersey.internal.config;

import java.util.Arrays;
import java.util.List;

class JerseySystemPropertiesConfigurationModel extends SystemPropertiesConfigurationModel {
   static final List<String> PROPERTY_CLASSES = Arrays.asList(
      "org.glassfish.jersey.CommonProperties",
      "org.glassfish.jersey.ExternalProperties",
      "org.glassfish.jersey.server.ServerProperties",
      "org.glassfish.jersey.client.ClientProperties",
      "org.glassfish.jersey.servlet.ServletProperties",
      "org.glassfish.jersey.message.MessageProperties",
      "org.glassfish.jersey.apache.connector.ApacheClientProperties",
      "org.glassfish.jersey.helidon.connector.HelidonClientProperties",
      "org.glassfish.jersey.jdk.connector.JdkConnectorProperties",
      "org.glassfish.jersey.jetty.connector.JettyClientProperties",
      "org.glassfish.jersey.netty.connector.NettyClientProperties",
      "org.glassfish.jersey.media.multipart.MultiPartProperties",
      "org.glassfish.jersey.server.oauth1.OAuth1ServerProperties"
   );

   JerseySystemPropertiesConfigurationModel() {
      super(PROPERTY_CLASSES);
   }
}
