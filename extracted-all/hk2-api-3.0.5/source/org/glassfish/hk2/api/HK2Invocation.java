package org.glassfish.hk2.api;

public interface HK2Invocation {
   void setUserData(String var1, Object var2);

   Object getUserData(String var1);
}
