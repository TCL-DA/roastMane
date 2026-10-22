package org.eclipse.jetty.websocket.core.util;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import org.eclipse.jetty.websocket.core.exception.InvalidSignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InvokerUtils {
   public static final InvokerUtils.ParamIdentifier PARAM_IDENTITY = new InvokerUtils.ParamIdentity();
   private static final Logger LOG = LoggerFactory.getLogger(InvokerUtils.class);

   public static MethodHandle bindTo(MethodHandle methodHandle, Object... objs) {
      if (methodHandle == null) {
         return null;
      } else {
         MethodHandle ret = methodHandle;

         for (Object obj : objs) {
            if (ret.type().parameterType(0).isAssignableFrom(obj.getClass())) {
               ret = ret.bindTo(obj);
            }
         }

         return ret;
      }
   }

   public static MethodHandle mutatedInvoker(Lookup lookup, Class<?> targetClass, Method method, InvokerUtils.Arg... callingArgs) {
      return mutatedInvoker(lookup, targetClass, true, method, PARAM_IDENTITY, null, callingArgs);
   }

   public static MethodHandle mutatedInvoker(
      Lookup lookup,
      Class<?> targetClass,
      Method method,
      InvokerUtils.ParamIdentifier paramIdentifier,
      String[] namedVariables,
      InvokerUtils.Arg... callingArgs
   ) {
      return mutatedInvoker(lookup, targetClass, true, method, paramIdentifier, namedVariables, callingArgs);
   }

   private static MethodHandle mutatedInvoker(
      Lookup lookup,
      Class<?> targetClass,
      boolean throwOnFailure,
      Method method,
      InvokerUtils.ParamIdentifier paramIdentifier,
      String[] namedVariables,
      InvokerUtils.Arg... rawCallingArgs
   ) {
      Class<?>[] parameterTypes = method.getParameterTypes();
      InvokerUtils.Arg[] callingArgs = new InvokerUtils.Arg[rawCallingArgs.length + (namedVariables == null ? 0 : namedVariables.length)];
      int callingArgIdx = 0;
      if (namedVariables != null) {
         for (String namedVariable : namedVariables) {
            callingArgs[callingArgIdx++] = new InvokerUtils.Arg(String.class, namedVariable).convertible();
         }
      }

      for (InvokerUtils.Arg rawCallingArg : rawCallingArgs) {
         callingArgs[callingArgIdx++] = rawCallingArg;
      }

      boolean hasNamedParamArgs = false;
      InvokerUtils.Arg[] parameterArgs = new InvokerUtils.Arg[parameterTypes.length + 1];
      parameterArgs[0] = new InvokerUtils.Arg(targetClass);

      for (int i = 0; i < parameterTypes.length; i++) {
         InvokerUtils.Arg arg = paramIdentifier.getParamArg(method, parameterTypes[i], i);
         if (arg.name != null) {
            hasNamedParamArgs = true;
         }

         parameterArgs[i + 1] = arg;
      }

      if (callingArgs.length < parameterTypes.length) {
         if (!throwOnFailure) {
            return null;
         } else {
            StringBuilder err = new StringBuilder();
            err.append("Target method ");
            ReflectUtils.append(err, targetClass, method);
            err.append(" contains too many parameters and cannot be mapped to expected callable args ");
            appendTypeList(err, callingArgs);
            throw new InvalidSignatureException(err.toString());
         }
      } else {
         boolean hasNamedCallingArgs = false;
         boolean hasConvertibleTypes = false;
         List<Class<?>> cTypes = new ArrayList<>();
         cTypes.add(targetClass);

         for (InvokerUtils.Arg arg : callingArgs) {
            if (arg.name != null) {
               hasNamedCallingArgs = true;
            }

            if (arg.convertible) {
               hasConvertibleTypes = true;
            }

            cTypes.add(arg.getType());
         }

         try {
            MethodType callingType = MethodType.methodType(method.getReturnType(), cTypes);
            MethodType rawType = MethodType.methodType(method.getReturnType(), method.getParameterTypes());
            MethodHandle methodHandle = lookup.findVirtual(targetClass, method.getName(), rawType);
            if (!hasNamedCallingArgs && !hasNamedParamArgs && rawType.equals(callingType)) {
               return methodHandle;
            } else {
               int[] reorderMap = new int[callingType.parameterCount()];
               Arrays.fill(reorderMap, -1);
               reorderMap[0] = 0;
               boolean[] usedCallingArgs = new boolean[callingArgs.length];
               Arrays.fill(usedCallingArgs, false);

               for (int pi = 1; pi < parameterArgs.length; pi++) {
                  int ref = -1;

                  for (int ci = 0; ci < callingArgs.length; ci++) {
                     if (!usedCallingArgs[ci] && callingArgs[ci].matches(parameterArgs[pi])) {
                        ref = ci + 1;
                        usedCallingArgs[ci] = true;
                        break;
                     }
                  }

                  if (ref < 0) {
                     if (!throwOnFailure) {
                        return null;
                     }

                     StringBuilder err = new StringBuilder();
                     err.append("Invalid mapping of type [");
                     err.append(parameterArgs[pi].getType());
                     err.append("] in method ");
                     ReflectUtils.append(err, method);
                     err.append(" to calling args ");
                     appendTypeList(err, callingArgs);
                     throw new InvalidSignatureException(err.toString());
                  }

                  reorderMap[pi] = ref;
               }

               for (int ri = parameterArgs.length; ri <= reorderMap.length; ri++) {
                  for (int uci = 0; uci < usedCallingArgs.length; uci++) {
                     if (!usedCallingArgs[uci]) {
                        if (callingArgs[uci].required) {
                           if (!throwOnFailure) {
                              return null;
                           }

                           StringBuilder err = new StringBuilder();
                           err.append("Missing required argument [");
                           err.append(callingArgs[uci].getType().getName());
                           err.append("] in method ");
                           ReflectUtils.append(err, method);
                           throw new InvalidSignatureException(err.toString());
                        }

                        reorderMap[ri] = uci + 1;
                        ri++;
                     }
                  }
               }

               int idxDrop = parameterArgs.length;
               int dropLength = reorderMap.length - idxDrop;
               if (dropLength > 0) {
                  List<Class<?>> dropTypes = new ArrayList<>();

                  for (int i = 0; i < dropLength; i++) {
                     int callingTypeIdx = reorderMap[idxDrop + i];
                     dropTypes.add(callingType.parameterType(callingTypeIdx));
                  }

                  methodHandle = MethodHandles.dropArguments(methodHandle, idxDrop, dropTypes);
               }

               if (hasConvertibleTypes) {
                  cTypes = new ArrayList<>();
                  cTypes.add(targetClass);

                  for (InvokerUtils.Arg arg : callingArgs) {
                     cTypes.add(arg.getConvertedType());
                  }

                  callingType = MethodType.methodType(method.getReturnType(), cTypes);
               }

               return MethodHandles.permuteArguments(methodHandle, callingType, reorderMap);
            }
         } catch (NoSuchMethodException | IllegalAccessException var25) {
            if (!throwOnFailure) {
               if (LOG.isDebugEnabled()) {
                  LOG.debug("Unable to obtain MethodHandle for " + method, var25);
               }

               return null;
            } else {
               throw new InvalidSignatureException("Unable to obtain MethodHandle for " + method, var25);
            }
         }
      }
   }

   public static MethodHandle optionalMutatedInvoker(
      Lookup lookup,
      Class<?> targetClass,
      Method method,
      InvokerUtils.ParamIdentifier paramIdentifier,
      String[] namedVariables,
      InvokerUtils.Arg... callingArgs
   ) {
      return mutatedInvoker(lookup, targetClass, false, method, paramIdentifier, namedVariables, callingArgs);
   }

   public static MethodHandle optionalMutatedInvoker(Lookup lookup, Class<?> targetClass, Method method, InvokerUtils.Arg... callingArgs) {
      return mutatedInvoker(lookup, targetClass, false, method, PARAM_IDENTITY, null, callingArgs);
   }

   private static void appendTypeList(StringBuilder str, InvokerUtils.Arg[] args) {
      str.append("(");
      boolean comma = false;

      for (InvokerUtils.Arg arg : args) {
         if (comma) {
            str.append(", ");
         }

         str.append(arg.getType().getName());
         comma = true;
      }

      str.append(")");
   }

   public static class Arg {
      private final Class<?> type;
      private String name;
      private boolean required = false;
      private boolean convertible = false;
      private Class<?> convertedType;

      public Arg(Class<?> type) {
         this.type = type;
      }

      public Arg(Class<?> type, String name) {
         this.type = type;
         this.name = name;
      }

      public String getName() {
         return this.name;
      }

      public boolean matches(InvokerUtils.Arg other) {
         if (this.name == null && other.name == null) {
            return other.type.isAssignableFrom(this.type);
         } else if (Objects.equals(this.name, other.name)) {
            if (this.convertible) {
               this.convertedType = other.type;
               return true;
            } else {
               return this.type.isAssignableFrom(other.type) ? true : true;
            }
         } else {
            return false;
         }
      }

      public InvokerUtils.Arg required() {
         this.required = true;
         return this;
      }

      public InvokerUtils.Arg convertible() {
         this.convertible = true;
         return this;
      }

      public Class<?> getType() {
         return this.type;
      }

      public Class<?> getConvertedType() {
         return this.convertible && this.convertedType != null ? this.convertedType : this.type;
      }

      public boolean isRequired() {
         return this.required;
      }
   }

   public interface ParamIdentifier {
      InvokerUtils.Arg getParamArg(Method var1, Class<?> var2, int var3);
   }

   private static class ParamIdentity implements InvokerUtils.ParamIdentifier {
      @Override
      public InvokerUtils.Arg getParamArg(Method method, Class<?> paramType, int idx) {
         return new InvokerUtils.Arg(paramType);
      }
   }
}
