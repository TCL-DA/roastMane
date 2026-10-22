package org.aopalliance.intercept;

public interface FieldInterceptor extends Interceptor {
   Object get(FieldAccess var1) throws Throwable;

   Object set(FieldAccess var1) throws Throwable;
}
