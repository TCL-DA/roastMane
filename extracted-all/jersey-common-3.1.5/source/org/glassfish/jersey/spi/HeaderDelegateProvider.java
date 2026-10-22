package org.glassfish.jersey.spi;

import jakarta.ws.rs.ext.RuntimeDelegate.HeaderDelegate;

@Contract
public interface HeaderDelegateProvider<T> extends HeaderDelegate<T> {
   boolean supports(Class<?> var1);
}
