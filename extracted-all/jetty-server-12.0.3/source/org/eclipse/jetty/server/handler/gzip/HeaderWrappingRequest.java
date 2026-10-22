package org.eclipse.jetty.server.handler.gzip;

import org.eclipse.jetty.http.HttpFields;
import org.eclipse.jetty.server.Request;

public class HeaderWrappingRequest extends Request.Wrapper {
   private final HttpFields _fields;

   public HeaderWrappingRequest(Request request, HttpFields fields) {
      super(request);
      this._fields = fields;
   }

   @Override
   public HttpFields getHeaders() {
      return this._fields == null ? super.getHeaders() : this._fields;
   }
}
