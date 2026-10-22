package org.jvnet.hk2.internal;

import java.lang.reflect.Proxy;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashMap;
import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.ProxyFactory;
import javassist.util.proxy.ProxyObject;
import javassist.util.proxy.ProxyFactory.ClassLoaderProvider;
import org.glassfish.hk2.api.ActiveDescriptor;
import org.glassfish.hk2.api.Injectee;
import org.glassfish.hk2.api.MultiException;
import org.glassfish.hk2.api.ProxyCtl;
import org.glassfish.hk2.api.ServiceLocator;

public class ProxyUtilities {
   private static final Object proxyCreationLock = new Object();
   private final HashMap<ClassLoader, DelegatingClassLoader> superClassToDelegator = new HashMap<>();

   private <T> T secureCreate(final Class<?> superclass, final Class<?>[] interfaces, final MethodHandler callback, boolean useJDKProxy, ServiceLocator anchor) {
      final ClassLoader loader = AccessController.doPrivileged(
         new PrivilegedAction<ClassLoader>() {
            public ClassLoader run() {
               ClassLoader retVal = superclass.getClassLoader();
               if (retVal == null) {
                  try {
                     retVal = ClassLoader.getSystemClassLoader();
                  } catch (SecurityException var3) {
                     throw new IllegalStateException(
                        "Insufficient privilege to get system classloader while looking for classloader of " + superclass.getName(), var3
                     );
                  }
               }

               if (retVal == null) {
                  throw new IllegalStateException("Could not find system classloader or classloader of " + superclass.getName());
               } else {
                  return retVal;
               }
            }
         }
      );
      final DelegatingClassLoader initDelegatingLoader;
      synchronized (this.superClassToDelegator) {
         initDelegatingLoader = this.superClassToDelegator.get(loader);
         if (initDelegatingLoader == null) {
            initDelegatingLoader = AccessController.doPrivileged(new PrivilegedAction<DelegatingClassLoader>() {
               public DelegatingClassLoader run() {
                  return new DelegatingClassLoader(loader, ProxyFactory.class.getClassLoader(), ProxyCtl.class.getClassLoader());
               }
            });
            this.superClassToDelegator.put(loader, initDelegatingLoader);
         }
      }

      return useJDKProxy ? AccessController.doPrivileged(new PrivilegedAction<T>() {
         @Override
         public T run() {
            return (T)Proxy.newProxyInstance(initDelegatingLoader, interfaces, new MethodInterceptorInvocationHandler(callback));
         }
      }) : AccessController.doPrivileged(new PrivilegedAction<T>() {
         @Override
         public T run() {
            synchronized (ProxyUtilities.proxyCreationLock) {
               ClassLoaderProvider originalProvider = ProxyFactory.classLoaderProvider;
               ProxyFactory.classLoaderProvider = new ClassLoaderProvider() {
                  public ClassLoader get(ProxyFactory arg0) {
                     return initDelegatingLoader;
                  }
               };

               Object var6;
               try {
                  ProxyFactory proxyFactory = new ProxyFactory();
                  proxyFactory.setInterfaces(interfaces);
                  proxyFactory.setSuperclass(superclass);
                  Class<?> proxyClass = proxyFactory.createClass();

                  try {
                     T proxy = (T)proxyClass.newInstance();
                     ((ProxyObject)proxy).setHandler(callback);
                     var6 = proxy;
                  } catch (Exception var12) {
                     throw new RuntimeException(var12);
                  }
               } finally {
                  ProxyFactory.classLoaderProvider = originalProvider;
               }

               return (T)var6;
            }
         }
      });
   }

   public <T> T generateProxy(Class<?> requestedClass, ServiceLocatorImpl locator, ActiveDescriptor<T> root, ServiceHandleImpl<T> handle, Injectee injectee) {
      boolean isInterface = requestedClass == null ? false : requestedClass.isInterface();
      Class<?> proxyClass;
      Class<?>[] iFaces;
      if (isInterface) {
         proxyClass = requestedClass;
         iFaces = new Class[]{requestedClass, ProxyCtl.class};
      } else {
         proxyClass = Utilities.getFactoryAwareImplementationClass(root);
         iFaces = Utilities.getInterfacesForProxy(root.getContractTypes());
      }

      try {
         return this.secureCreate(proxyClass, iFaces, new MethodInterceptorImpl(locator, root, handle, injectee), isInterface, locator);
      } catch (Throwable var13) {
         Exception addMe = new IllegalArgumentException(
            "While attempting to create a Proxy for " + proxyClass.getName() + " in scope " + root.getScope() + " an error occured while creating the proxy"
         );
         if (var13 instanceof MultiException) {
            MultiException me = (MultiException)var13;
            me.addError(addMe);
            throw me;
         } else {
            MultiException me = new MultiException(var13);
            me.addError(addMe);
            throw me;
         }
      }
   }

   public void releaseCache() {
      synchronized (this.superClassToDelegator) {
         this.superClassToDelegator.clear();
      }
   }
}
