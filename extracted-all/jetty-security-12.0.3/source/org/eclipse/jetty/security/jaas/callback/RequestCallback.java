package org.eclipse.jetty.security.jaas.callback;

import javax.security.auth.callback.Callback;
import org.eclipse.jetty.server.Request;

public class RequestCallback implements Callback {
   protected Request _request;

   public void setRequest(Request request) {
      this._request = request;
   }

   public Request getRequest() {
      return this._request;
   }
}
