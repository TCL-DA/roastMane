package org.aopalliance.reflect;

public interface Field extends Member {
   CodeLocator getReadLocator();

   CodeLocator getReadLocator(int var1);

   CodeLocator getWriteLocator();

   CodeLocator getWriteLocator(int var1);
}
