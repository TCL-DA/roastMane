package org.eclipse.jetty.websocket.common;

import java.util.Map;
import java.util.Set;
import org.eclipse.jetty.websocket.api.ExtensionConfig;

public class JettyExtensionConfig implements ExtensionConfig {
   private final org.eclipse.jetty.websocket.core.ExtensionConfig config;

   public JettyExtensionConfig(JettyExtensionConfig copy) {
      this(copy.config);
   }

   public JettyExtensionConfig(org.eclipse.jetty.websocket.core.ExtensionConfig config) {
      this.config = config;
   }

   public JettyExtensionConfig(String parameterizedName) {
      this.config = new org.eclipse.jetty.websocket.core.ExtensionConfig(parameterizedName);
   }

   public JettyExtensionConfig(String name, Map<String, String> parameters) {
      this.config = new org.eclipse.jetty.websocket.core.ExtensionConfig(name, parameters);
   }

   public org.eclipse.jetty.websocket.core.ExtensionConfig getCoreConfig() {
      return this.config;
   }

   public String getName() {
      return this.config.getName();
   }

   public final int getParameter(String key, int defValue) {
      return this.config.getParameter(key, defValue);
   }

   public final String getParameter(String key, String defValue) {
      return this.config.getParameter(key, defValue);
   }

   public final String getParameterizedName() {
      return this.config.getParameterizedName();
   }

   public final Set<String> getParameterKeys() {
      return this.config.getParameterKeys();
   }

   public final Map<String, String> getParameters() {
      return this.config.getParameters();
   }

   public final void setParameter(String key) {
      this.config.setParameter(key);
   }

   public final void setParameter(String key, int value) {
      this.config.setParameter(key, value);
   }

   public final void setParameter(String key, String value) {
      this.config.setParameter(key, value);
   }

   @Override
   public String toString() {
      return this.config.toString();
   }
}
