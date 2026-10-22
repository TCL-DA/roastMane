package org.jvnet.hk2.internal;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.RandomAccess;
import org.aopalliance.intercept.ConstructorInterceptor;
import org.aopalliance.intercept.ConstructorInvocation;
import org.glassfish.hk2.api.HK2Invocation;
import org.glassfish.hk2.utilities.reflection.ReflectionHelper;

public class ConstructorInterceptorHandler {
   private static final ConstructorAction DEFAULT_ACTION = new ConstructorAction() {
      @Override
      public Object makeMe(Constructor<?> c, Object[] args, boolean neutralCCL) throws Throwable {
         return ReflectionHelper.makeMe(c, args, neutralCCL);
      }
   };

   public static Object construct(Constructor<?> c, Object[] args, boolean neutralCCL, List<ConstructorInterceptor> interceptors, ConstructorAction action) throws Throwable {
      if (interceptors != null && !interceptors.isEmpty()) {
         if (!(interceptors instanceof RandomAccess)) {
            interceptors = new ArrayList<>(interceptors);
         }

         ConstructorInterceptor firstInterceptor = interceptors.get(0);
         Object retVal = firstInterceptor.construct(
            new ConstructorInterceptorHandler.ConstructorInvocationImpl(c, args, neutralCCL, action, 0, interceptors, null)
         );
         if (retVal == null) {
            throw new AssertionError("ConstructorInterceptor construct method returned null for " + c);
         } else {
            return retVal;
         }
      } else {
         return action.makeMe(c, args, neutralCCL);
      }
   }

   public static Object construct(Constructor<?> c, Object[] args, boolean neutralCCL, List<ConstructorInterceptor> interceptors) throws Throwable {
      return construct(c, args, neutralCCL, interceptors, DEFAULT_ACTION);
   }

   private static class ConstructorInvocationImpl implements ConstructorInvocation, HK2Invocation {
      private final Constructor<?> c;
      private final Object[] args;
      private final boolean neutralCCL;
      private Object myThis = null;
      private final int index;
      private final ConstructorAction finalAction;
      private final List<ConstructorInterceptor> interceptors;
      private HashMap<String, Object> userData;

      private ConstructorInvocationImpl(
         Constructor<?> c,
         Object[] args,
         boolean neutralCCL,
         ConstructorAction finalAction,
         int index,
         List<ConstructorInterceptor> interceptors,
         HashMap<String, Object> userData
      ) {
         this.c = c;
         this.args = args;
         this.neutralCCL = neutralCCL;
         this.finalAction = finalAction;
         this.index = index;
         this.interceptors = interceptors;
         this.userData = userData;
      }

      public Object[] getArguments() {
         return this.args;
      }

      public AccessibleObject getStaticPart() {
         return this.c;
      }

      public Object getThis() {
         return this.myThis;
      }

      public Object proceed() throws Throwable {
         int newIndex = this.index + 1;
         if (newIndex >= this.interceptors.size()) {
            this.myThis = this.finalAction.makeMe(this.c, this.args, this.neutralCCL);
            return this.myThis;
         } else {
            ConstructorInterceptor nextInterceptor = this.interceptors.get(newIndex);
            this.myThis = nextInterceptor.construct(
               new ConstructorInterceptorHandler.ConstructorInvocationImpl(
                  this.c, this.args, this.neutralCCL, this.finalAction, newIndex, this.interceptors, this.userData
               )
            );
            return this.myThis;
         }
      }

      public Constructor getConstructor() {
         return this.c;
      }

      public void setUserData(String key, Object data) {
         if (key == null) {
            throw new IllegalArgumentException();
         } else {
            if (this.userData == null) {
               this.userData = new HashMap<>();
            }

            if (data == null) {
               this.userData.remove(key);
            } else {
               this.userData.put(key, data);
            }
         }
      }

      public Object getUserData(String key) {
         if (key == null) {
            throw new IllegalArgumentException();
         } else {
            return this.userData == null ? null : this.userData.get(key);
         }
      }
   }
}
