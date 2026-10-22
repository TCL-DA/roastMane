package org.eclipse.jetty.server.jmx;

import java.util.List;
import org.eclipse.jetty.jmx.ObjectMBean;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.util.annotation.ManagedObject;

public class Handler {
   @ManagedObject
   public static class AbstractMBean extends ObjectMBean {
      public AbstractMBean(Object managedObject) {
         super(managedObject);
      }

      public org.eclipse.jetty.server.Handler.Abstract getManagedObject() {
         return (org.eclipse.jetty.server.Handler.Abstract)super.getManagedObject();
      }

      public String getObjectContextBasis() {
         if (this._managed instanceof ContextHandler contextHandler) {
            String contextName = this.getContextName(contextHandler);
            if (contextName == null) {
               contextName = contextHandler.getDisplayName();
            }

            return contextName;
         } else {
            String basis = null;
            org.eclipse.jetty.server.Handler.Abstract handler = this.getManagedObject();
            Server server = handler.getServer();
            if (server != null) {
               ContextHandler context = server.getContainer(handler, ContextHandler.class);
               if (context != null) {
                  basis = this.getContextName(context);
               }
            }

            return basis;
         }
      }

      protected String getContextName(ContextHandler context) {
         String name = null;
         String contextPath = context.getContextPath();
         if (contextPath != null && !contextPath.isEmpty()) {
            int idx = contextPath.lastIndexOf(47);
            name = idx < 0 ? contextPath : contextPath.substring(++idx);
            if (name.isEmpty()) {
               name = "ROOT";
            }
         }

         if (name == null && context.getBaseResource() != null) {
            name = context.getBaseResource().getPath().getFileName().toString();
         }

         List<String> vhosts = context.getVirtualHosts();
         if (!vhosts.isEmpty()) {
            name = "\"%s@%s\"".formatted(name, vhosts.get(0));
         }

         return name;
      }
   }
}
