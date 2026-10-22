package org.eclipse.jetty.security.jaas.callback;

import javax.security.auth.callback.Callback;

public class ObjectCallback implements Callback {
   protected Object _object;

   public void setObject(Object o) {
      this._object = o;
   }

   public Object getObject() {
      return this._object;
   }

   public void clearObject() {
      this._object = null;
   }
}
