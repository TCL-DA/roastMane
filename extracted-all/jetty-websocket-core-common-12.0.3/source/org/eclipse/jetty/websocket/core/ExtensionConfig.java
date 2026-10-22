package org.eclipse.jetty.websocket.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import org.eclipse.jetty.http.QuotedCSV;
import org.eclipse.jetty.util.Index;
import org.eclipse.jetty.util.StringUtil;
import org.eclipse.jetty.util.Index.Builder;

public class ExtensionConfig {
   private static final Index<ExtensionConfig> CACHE = new Builder()
      .caseSensitive(false)
      .with("identity", new ExtensionConfig("identity"))
      .with("permessage-deflate", new ExtensionConfig("permessage-deflate"))
      .with("permessage-deflate; client_max_window_bits", new ExtensionConfig("permessage-deflate; client_max_window_bits"))
      .build();
   private final String name;
   private final Map<String, String> parameters;

   public static ExtensionConfig parse(String parameterizedName) {
      ExtensionConfig config = (ExtensionConfig)CACHE.get(parameterizedName);
      return config != null ? config : new ExtensionConfig(parameterizedName);
   }

   public static List<ExtensionConfig> parseEnum(Enumeration<String> valuesEnum) {
      List<ExtensionConfig> configs = new ArrayList<>();
      if (valuesEnum != null) {
         while (valuesEnum.hasMoreElements()) {
            for (String extToken : new QuotedCSV(new String[]{valuesEnum.nextElement()})) {
               configs.add(parse(extToken));
            }
         }
      }

      return configs;
   }

   public static List<ExtensionConfig> parseList(String... rawSecWebSocketExtensions) {
      List<ExtensionConfig> configs = new ArrayList<>();

      for (String rawValue : rawSecWebSocketExtensions) {
         for (String extToken : new QuotedCSV(new String[]{rawValue})) {
            configs.add(parse(extToken));
         }
      }

      return configs;
   }

   public static String toHeaderValue(List<ExtensionConfig> configs) {
      if (configs != null && !configs.isEmpty()) {
         StringBuilder parameters = new StringBuilder();
         boolean needsDelim = false;

         for (ExtensionConfig ext : configs) {
            if (needsDelim) {
               parameters.append(", ");
            }

            parameters.append(ext.getParameterizedName());
            needsDelim = true;
         }

         return parameters.toString();
      } else {
         return null;
      }
   }

   public ExtensionConfig(ExtensionConfig copy) {
      this.name = copy.name;
      this.parameters = new HashMap<>();
      this.parameters.putAll(copy.parameters);
   }

   public ExtensionConfig(String name, Map<String, String> parameters) {
      this.name = name;
      this.parameters = new HashMap<>();
      this.parameters.putAll(parameters);
   }

   public ExtensionConfig(String parameterizedName) {
      ExtensionConfig.ParamParser paramParser = new ExtensionConfig.ParamParser(parameterizedName);
      paramParser.parse();
      this.name = paramParser.getName();
      this.parameters = paramParser.getParams();
   }

   public boolean isInternalExtension() {
      return this.name.startsWith("@");
   }

   public List<Entry<String, String>> getInternalParameters() {
      return this.parameters.entrySet().stream().filter(entry -> entry.getKey().startsWith("@")).collect(Collectors.toList());
   }

   public void removeInternalParameters() {
      this.parameters.entrySet().removeIf(entry -> entry.getKey().startsWith("@"));
   }

   public String getName() {
      return this.name;
   }

   public final int getParameter(String key, int defValue) {
      String val = this.parameters.get(key);
      return val == null ? defValue : Integer.parseInt(val);
   }

   public final String getParameter(String key, String defValue) {
      String val = this.parameters.get(key);
      return val == null ? defValue : val;
   }

   public final String getParameterizedName() {
      StringBuilder str = new StringBuilder();
      str.append(this.name);

      for (String param : this.parameters.keySet()) {
         str.append(';');
         str.append(param);
         String value = this.parameters.get(param);
         if (value != null) {
            str.append('=');
            quoteIfNeeded(str, value);
         }
      }

      return str.toString();
   }

   public final String getParameterizedNameWithoutInternalParams() {
      StringBuilder str = new StringBuilder();
      str.append(this.name);

      for (String param : this.parameters.keySet()) {
         if (!param.startsWith("@")) {
            str.append(';');
            str.append(param);
            String value = this.parameters.get(param);
            if (value != null) {
               str.append('=');
               quoteIfNeeded(str, value);
            }
         }
      }

      return str.toString();
   }

   public static void quoteIfNeeded(StringBuilder buf, String str) {
      if (str != null) {
         int len = str.length();
         if (len != 0) {
            for (int i = 0; i < len; i++) {
               int ch = str.codePointAt(i);
               if (ch == 59 || ch == 61) {
                  buf.append('"').append(str).append('"');
                  return;
               }
            }

            buf.append(str);
         }
      }
   }

   public final Set<String> getParameterKeys() {
      return Collections.unmodifiableSet(this.parameters.keySet());
   }

   public final Map<String, String> getParameters() {
      return Collections.unmodifiableMap(this.parameters);
   }

   public final void setParameter(String key) {
      this.parameters.put(key, null);
   }

   public final void setParameter(String key, int value) {
      this.parameters.put(key, Integer.toString(value));
   }

   public final void setParameter(String key, String value) {
      this.parameters.put(key, value);
   }

   @Override
   public String toString() {
      return this.getParameterizedName();
   }

   private static class ParamParser extends QuotedCSV {
      private final String parameterizedName;
      private String name;
      private final Map<String, String> params = new HashMap<>();

      public ParamParser(String parameterizedName) {
         super(false, new String[0]);
         this.parameterizedName = parameterizedName;
      }

      public String getName() {
         return this.name;
      }

      public Map<String, String> getParams() {
         return this.params;
      }

      protected void parsedParam(StringBuffer buffer, int valueLength, int paramNameIdx, int paramValueIdx) {
         String paramName = "";
         String paramValue = null;
         if (paramValueIdx > 0) {
            paramName = buffer.substring(paramNameIdx, paramValueIdx - 1);
            paramValue = buffer.substring(paramValueIdx);
         } else if (paramNameIdx > 0) {
            paramName = buffer.substring(paramNameIdx);
         }

         this.params.put(paramName, paramValue);
         super.parsedParam(buffer, valueLength, paramNameIdx, paramValueIdx);
      }

      protected void parsedValue(StringBuffer buffer) {
         String extName = buffer.toString();
         if (this.name != null) {
            throw new IllegalArgumentException("parameterizedName contains multiple ExtensionConfigs: " + this.parameterizedName);
         } else {
            this.name = extName;
            super.parsedValue(buffer);
         }
      }

      public void parse() {
         this.addValue(this.parameterizedName);
         if (StringUtil.isEmpty(this.name)) {
            throw new IllegalArgumentException("parameterizedName contains no ExtensionConfigs: " + this.parameterizedName);
         }
      }
   }
}
