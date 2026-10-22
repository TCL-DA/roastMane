package org.eclipse.jetty.server.handler;

import java.util.HashSet;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ManagedAttributeListener {
   private static final Logger LOG = LoggerFactory.getLogger(ManagedAttributeListener.class);
   final Set<String> _managedAttributes = new HashSet<>();
   final ContextHandler _context;

   public ManagedAttributeListener(ContextHandler context, String... managedAttributes) {
      this._context = context;

      for (String attr : managedAttributes) {
         this._managedAttributes.add(attr);
      }

      if (LOG.isDebugEnabled()) {
         LOG.debug("managedAttributes {}", this._managedAttributes);
      }
   }
}
