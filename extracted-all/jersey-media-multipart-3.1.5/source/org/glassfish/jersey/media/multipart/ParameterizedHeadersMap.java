package org.glassfish.jersey.media.multipart;

import jakarta.ws.rs.core.MultivaluedMap;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import org.glassfish.jersey.internal.util.collection.StringKeyIgnoreCaseMultivaluedMap;
import org.glassfish.jersey.message.internal.ParameterizedHeader;

class ParameterizedHeadersMap extends StringKeyIgnoreCaseMultivaluedMap<ParameterizedHeader> {
   public ParameterizedHeadersMap() {
   }

   public ParameterizedHeadersMap(MultivaluedMap<String, String> headers) throws ParseException {
      for (Entry<String, List<String>> entry : headers.entrySet()) {
         List<ParameterizedHeader> list = new ArrayList<>(entry.getValue().size());

         for (String value : entry.getValue()) {
            list.add(new ParameterizedHeader(value));
         }

         this.put(entry.getKey(), list);
      }
   }
}
