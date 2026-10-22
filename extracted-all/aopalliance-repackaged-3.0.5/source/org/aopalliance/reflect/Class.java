package org.aopalliance.reflect;

public interface Class extends ProgramUnit {
   ClassLocator getClassLocator();

   String getName();

   Field[] getFields();

   Field[] getDeclaredFields();

   Method[] getMethods();

   Method[] getDeclaredMethods();

   Class getSuperclass();

   Class[] getInterfaces();
}
