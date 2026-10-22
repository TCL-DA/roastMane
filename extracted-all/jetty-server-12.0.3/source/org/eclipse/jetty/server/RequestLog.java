package org.eclipse.jetty.server;

import java.io.IOException;

public interface RequestLog {
   void log(Request var1, Response var2);

   public static class Collection implements RequestLog {
      private final RequestLog[] _logs;

      public Collection(RequestLog... logs) {
         this._logs = logs;
      }

      @Override
      public void log(Request request, Response response) {
         for (RequestLog log : this._logs) {
            log.log(request, response);
         }
      }
   }

   public interface Writer {
      void write(String var1) throws IOException;
   }
}
