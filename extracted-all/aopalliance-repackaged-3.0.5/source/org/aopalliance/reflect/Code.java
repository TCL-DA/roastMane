package org.aopalliance.reflect;

public interface Code {
   CodeLocator getLocator();

   CodeLocator getCallLocator(Method var1);

   CodeLocator getReadLocator(Field var1);

   CodeLocator getWriteLocator(Field var1);

   CodeLocator getThrowLocator(Class var1);

   CodeLocator getCatchLocator(Class var1);
}
