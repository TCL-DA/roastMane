package org.glassfish.hk2.api;

public interface AOPProxyCtl {
   String UNDERLYING_METHOD_NAME = "__getUnderlyingDescriptor";

   ActiveDescriptor<?> __getUnderlyingDescriptor();
}
