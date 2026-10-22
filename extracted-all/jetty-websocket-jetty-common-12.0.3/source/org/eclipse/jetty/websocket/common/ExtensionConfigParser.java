package org.eclipse.jetty.websocket.common;

import org.eclipse.jetty.websocket.api.ExtensionConfig.Parser;
import org.eclipse.jetty.websocket.core.ExtensionConfig;

public class ExtensionConfigParser implements Parser {
   public JettyExtensionConfig parse(String parameterizedName) {
      return new JettyExtensionConfig(ExtensionConfig.parse(parameterizedName));
   }
}
