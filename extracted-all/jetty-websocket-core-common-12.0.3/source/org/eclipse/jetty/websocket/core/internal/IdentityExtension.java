package org.eclipse.jetty.websocket.core.internal;

import org.eclipse.jetty.util.Callback;
import org.eclipse.jetty.util.QuotedStringTokenizer;
import org.eclipse.jetty.util.annotation.ManagedObject;
import org.eclipse.jetty.websocket.core.AbstractExtension;
import org.eclipse.jetty.websocket.core.ExtensionConfig;
import org.eclipse.jetty.websocket.core.Frame;
import org.eclipse.jetty.websocket.core.WebSocketComponents;

@ManagedObject("Identity Extension")
public class IdentityExtension extends AbstractExtension {
   private static final QuotedStringTokenizer PARAM_VALUE_QUOTING = QuotedStringTokenizer.builder().delimiters(";=").ignoreOptionalWhiteSpace().build();
   private String id;

   public String getParam(String key) {
      return this.getConfig().getParameter(key, "?");
   }

   @Override
   public String getName() {
      return "identity";
   }

   @Override
   public void onFrame(Frame frame, Callback callback) {
      this.nextIncomingFrame(frame, callback);
   }

   @Override
   public void sendFrame(Frame frame, Callback callback, boolean batch) {
      this.nextOutgoingFrame(frame, callback, batch);
   }

   @Override
   public void init(ExtensionConfig config, WebSocketComponents components) {
      super.init(config, components);
      StringBuilder s = new StringBuilder();
      s.append(config.getName());
      s.append("@").append(Integer.toHexString(this.hashCode()));
      s.append("[");
      boolean delim = false;

      for (String param : config.getParameterKeys()) {
         if (delim) {
            s.append(';');
         }

         s.append(param).append('=').append(PARAM_VALUE_QUOTING.quoteIfNeeded(config.getParameter(param, "")));
         delim = true;
      }

      s.append("]");
      this.id = s.toString();
   }

   @Override
   public String toString() {
      return this.id;
   }
}
