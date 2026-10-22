package org.eclipse.jetty.security.jaas.callback;

import java.io.IOException;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;

public abstract class AbstractCallbackHandler implements CallbackHandler {
   protected String _userName;
   protected Object _credential;

   public void setUserName(String userName) {
      this._userName = userName;
   }

   public String getUserName() {
      return this._userName;
   }

   public void setCredential(Object credential) {
      this._credential = credential;
   }

   public Object getCredential() {
      return this._credential;
   }

   @Override
   public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
   }
}
