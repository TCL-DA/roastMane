package org.eclipse.jetty.security.jaas.callback;

import java.util.List;
import javax.security.auth.callback.Callback;

public class RequestParameterCallback implements Callback {
   private String _paramName;
   private List<?> _paramValues;

   public void setParameterName(String name) {
      this._paramName = name;
   }

   public String getParameterName() {
      return this._paramName;
   }

   public void setParameterValues(List<?> values) {
      this._paramValues = values;
   }

   public List<?> getParameterValues() {
      return this._paramValues;
   }
}
