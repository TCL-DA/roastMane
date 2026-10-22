package org.eclipse.jetty.security.jaas.callback;

import java.io.IOException;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import org.eclipse.jetty.server.FormFields;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.util.ExceptionUtil;
import org.eclipse.jetty.util.Fields;

public class DefaultCallbackHandler extends AbstractCallbackHandler {
   private Request _request;

   public void setRequest(Request request) {
      this._request = request;
   }

   @Override
   public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
      for (Callback callback : callbacks) {
         if (callback instanceof NameCallback) {
            ((NameCallback)callback).setName(this.getUserName());
         } else if (callback instanceof ObjectCallback) {
            ((ObjectCallback)callback).setObject(this.getCredential());
         } else if (callback instanceof PasswordCallback) {
            ((PasswordCallback)callback).setPassword(this.getCredential().toString().toCharArray());
         } else if (callback instanceof RequestParameterCallback) {
            if (this._request != null) {
               RequestParameterCallback rpc = (RequestParameterCallback)callback;
               Fields queryFields = Request.extractQueryParameters(this._request);
               Fields formFields = (Fields)ExceptionUtil.get(FormFields.from(this._request));
               Fields fields = Fields.combine(queryFields, formFields);
               rpc.setParameterValues(fields.getValues(rpc.getParameterName()));
            }
         } else {
            if (!(callback instanceof RequestCallback)) {
               throw new UnsupportedCallbackException(callback);
            }

            ((RequestCallback)callback).setRequest(this._request);
         }
      }
   }
}
