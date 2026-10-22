package org.aopalliance.reflect;

public interface Method extends Member {
   CodeLocator getCallLocator();

   CodeLocator getCallLocator(int var1);

   Code getBody();
}
