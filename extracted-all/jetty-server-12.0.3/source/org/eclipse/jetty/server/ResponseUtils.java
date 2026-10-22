package org.eclipse.jetty.server;

import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.eclipse.jetty.http.HttpField;
import org.eclipse.jetty.http.HttpFields;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.HttpHeaderValue;
import org.eclipse.jetty.util.StringUtil;

public class ResponseUtils {
   public static void ensureConsumeAvailableOrNotPersistent(Request request, Response response) {
      if (!request.consumeAvailable()) {
         ensureNotPersistent(request, response);
      }
   }

   public static void ensureNotPersistent(Request request, Response response) {
      switch (request.getConnectionMetaData().getHttpVersion()) {
         case HTTP_1_0:
            response.getHeaders()
               .computeField(
                  HttpHeader.CONNECTION,
                  (h, fields) -> {
                     if (fields != null && !fields.isEmpty()) {
                        String v = fields.stream()
                           .flatMap(field -> Stream.of(field.getValues()).filter(s -> !HttpHeaderValue.KEEP_ALIVE.is(s)))
                           .collect(Collectors.joining(", "));
                        return StringUtil.isEmpty(v) ? null : new HttpField(HttpHeader.CONNECTION, v);
                     } else {
                        return null;
                     }
                  }
               );
            break;
         case HTTP_1_1:
            response.getHeaders()
               .computeField(
                  HttpHeader.CONNECTION,
                  (h, fields) -> {
                     if (fields != null && !fields.isEmpty()) {
                        if (fields.stream().anyMatch(fx -> fx.contains(HttpHeaderValue.CLOSE.asString()))) {
                           if (fields.size() == 1) {
                              HttpField f = (HttpField)fields.get(0);
                              if (HttpFields.CONNECTION_CLOSE.equals(f)) {
                                 return f;
                              }
                           }

                           return new HttpField(
                              HttpHeader.CONNECTION,
                              fields.stream()
                                 .flatMap(field -> Stream.of(field.getValues()).filter(s -> !HttpHeaderValue.KEEP_ALIVE.is(s)))
                                 .collect(Collectors.joining(", "))
                           );
                        } else {
                           return new HttpField(
                              HttpHeader.CONNECTION,
                              Stream.concat(
                                    fields.stream().flatMap(field -> Stream.of(field.getValues()).filter(s -> !HttpHeaderValue.KEEP_ALIVE.is(s))),
                                    Stream.of(HttpHeaderValue.CLOSE.asString())
                                 )
                                 .collect(Collectors.joining(", "))
                           );
                        }
                     } else {
                        return HttpFields.CONNECTION_CLOSE;
                     }
                  }
               );
      }
   }

   private ResponseUtils() {
   }
}
