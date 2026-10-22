package org.jvnet.hk2.internal;

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import javassist.util.proxy.MethodHandler;
import org.glassfish.hk2.api.ActiveDescriptor;
import org.glassfish.hk2.api.Context;
import org.glassfish.hk2.api.Injectee;
import org.glassfish.hk2.api.MultiException;
import org.glassfish.hk2.api.ProxyCtl;
import org.glassfish.hk2.utilities.reflection.ReflectionHelper;

public class MethodInterceptorImpl implements MethodHandler {
   private static final String PROXY_MORE_METHOD_NAME = "__make";
   private final ServiceLocatorImpl locator;
   private final ActiveDescriptor<?> descriptor;
   private final ServiceHandleImpl<?> root;
   private final WeakReference<Injectee> myInjectee;
   private static final String EQUALS_NAME = "equals";

   MethodInterceptorImpl(ServiceLocatorImpl sli, ActiveDescriptor<?> descriptor, ServiceHandleImpl<?> root, Injectee injectee) {
      this.locator = sli;
      this.descriptor = descriptor;
      this.root = root;
      if (injectee != null) {
         this.myInjectee = new WeakReference<>(injectee);
      } else {
         this.myInjectee = null;
      }
   }

   private Object internalInvoke(Object target, Method method, Method proceed, Object[] params) throws Throwable {
      Context<?> context = this.locator.resolveContext(this.descriptor.getScopeAnnotation());
      Object service = context.findOrCreate(this.descriptor, this.root);
      if (service == null) {
         throw new MultiException(
            new IllegalStateException(
               "Proxiable context " + context + " findOrCreate returned a null for descriptor " + this.descriptor + " and handle " + this.root
            )
         );
      } else if (method.getName().equals("__make")) {
         return service;
      } else {
         if (isEquals(method) && params.length == 1 && params[0] != null && params[0] instanceof ProxyCtl) {
            ProxyCtl equalsProxy = (ProxyCtl)params[0];
            params = new Object[]{equalsProxy.__make()};
         }

         return ReflectionHelper.invoke(service, method, params, this.locator.getNeutralContextClassLoader());
      }
   }

   public Object invoke(Object target, Method method, Method proceed, Object[] params) throws Throwable {
      boolean pushed = false;
      if (this.root != null && this.myInjectee != null) {
         Injectee ref = this.myInjectee.get();
         if (ref != null) {
            this.root.pushInjectee(ref);
            pushed = true;
         }
      }

      Object var10;
      try {
         var10 = this.internalInvoke(target, method, proceed, params);
      } finally {
         if (pushed) {
            this.root.popInjectee();
         }
      }

      return var10;
   }

   private static boolean isEquals(Method m) {
      if (!m.getName().equals("equals")) {
         return false;
      } else {
         Class<?>[] params = m.getParameterTypes();
         return params != null && params.length == 1 ? Object.class.equals(params[0]) : false;
      }
   }
}
