package org.eclipse.jetty.websocket.api;

import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;

public interface ExtensionConfig {
   private static ExtensionConfig.Parser getParser() {
      return ServiceLoader.load(ExtensionConfig.Parser.class).iterator().next();
   }

   static ExtensionConfig parse(String parameterizedName) {
      return getParser().parse(parameterizedName);
   }

   String getName();

   int getParameter(String var1, int var2);

   String getParameter(String var1, String var2);

   String getParameterizedName();

   Set<String> getParameterKeys();

   Map<String, String> getParameters();

   void setParameter(String var1);

   void setParameter(String var1, int var2);

   void setParameter(String var1, String var2);

   public interface Parser {
      ExtensionConfig parse(String var1);
   }
}
