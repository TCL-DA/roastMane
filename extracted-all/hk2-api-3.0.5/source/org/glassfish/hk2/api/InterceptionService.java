package org.glassfish.hk2.api;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;
import org.aopalliance.intercept.ConstructorInterceptor;
import org.aopalliance.intercept.MethodInterceptor;
import org.jvnet.hk2.annotations.Contract;

@Contract
public interface InterceptionService {
   Filter getDescriptorFilter();

   List<MethodInterceptor> getMethodInterceptors(Method var1);

   List<ConstructorInterceptor> getConstructorInterceptors(Constructor<?> var1);
}
