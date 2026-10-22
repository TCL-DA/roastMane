package org.glassfish.jersey.message.internal;

import java.text.ParseException;
import java.util.Collections;
import java.util.Map;

public class ParameterizedHeader {
   private String value;
   private Map<String, String> parameters;

   public ParameterizedHeader(String header) throws ParseException {
      this(HttpHeaderReader.newInstance(header));
   }

   public ParameterizedHeader(HttpHeaderReader reader) throws ParseException {
      reader.hasNext();

      for (this.value = ""; reader.hasNext() && !reader.hasNextSeparator(';', false); this.value = this.value + reader.getEventValue()) {
         reader.next();
      }

      if (reader.hasNext()) {
         this.parameters = HttpHeaderReader.readParameters(reader);
      }

      if (this.parameters == null) {
         this.parameters = Collections.emptyMap();
      } else {
         this.parameters = Collections.unmodifiableMap(this.parameters);
      }
   }

   public String getValue() {
      return this.value;
   }

   public Map<String, String> getParameters() {
      return this.parameters;
   }
}
