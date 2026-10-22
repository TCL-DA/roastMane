package org.eclipse.jetty.util;

public interface ClassVisibilityChecker {
   boolean isSystemClass(Class<?> var1);

   boolean isServerClass(Class<?> var1);
}
