package org.glassfish.hk2.api;

import org.jvnet.hk2.annotations.Contract;

@Contract
public interface InjectionResolver<T> {
   String SYSTEM_RESOLVER_NAME = "SystemInjectResolver";

   Object resolve(Injectee var1, ServiceHandle<?> var2);

   boolean isConstructorParameterIndicator();

   boolean isMethodParameterIndicator();
}
