package org.eclipse.jetty.server;

import org.eclipse.jetty.util.Attributes;

public interface Session extends Attributes {
   static Session getSession(Object session) {
      return session instanceof Session.API wrapper ? wrapper.getSession() : null;
   }

   <T extends Session.API> T getApi();

   boolean isValid();

   String getId();

   String getExtendedId();

   long getLastAccessedTime();

   void setMaxInactiveInterval(int var1);

   int getMaxInactiveInterval();

   void renewId(Request var1, Response var2);

   void invalidate();

   boolean isNew() throws IllegalStateException;

   String encodeURI(Request var1, String var2, boolean var3);

   public interface API {
      Session getSession();
   }

   public interface LifeCycleListener {
      default void onSessionIdChanged(Session session, String oldId) {
      }

      default void onSessionCreated(Session session) {
      }

      default void onSessionDestroyed(Session session) {
      }
   }

   public interface ValueListener {
      default void onSessionAttributeUpdate(Session session, String name, Object oldValue, Object newValue) {
      }

      default void onSessionActivation(Session session) {
      }

      default void onSessionPassivation(Session session) {
      }
   }
}
