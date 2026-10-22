package org.aopalliance.reflect;

public interface Member extends ProgramUnit {
   int USER_SIDE = 0;
   int PROVIDER_SIDE = 1;

   Class getDeclaringClass();

   String getName();

   int getModifiers();
}
