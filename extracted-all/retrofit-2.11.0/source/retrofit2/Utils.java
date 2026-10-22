package retrofit2;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Objects;
import javax.annotation.Nullable;
import kotlin.Unit;
import okhttp3.ResponseBody;
import okio.Buffer;

final class Utils {
   static final Type[] EMPTY_TYPE_ARRAY = new Type[0];
   private static boolean checkForKotlinUnit = true;

   private Utils() {
   }

   static RuntimeException methodError(Method method, String message, Object... args) {
      return methodError(method, null, message, args);
   }

   static RuntimeException methodError(Method method, @Nullable Throwable cause, String message, Object... args) {
      message = String.format(message, args);
      return new IllegalArgumentException(message + "\n    for method " + method.getDeclaringClass().getSimpleName() + "." + method.getName(), cause);
   }

   static RuntimeException parameterError(Method method, Throwable cause, int p, String message, Object... args) {
      String paramDesc = Platform.reflection.describeMethodParameter(method, p);
      return methodError(method, cause, message + " (" + paramDesc + ")", args);
   }

   static RuntimeException parameterError(Method method, int p, String message, Object... args) {
      String paramDesc = Platform.reflection.describeMethodParameter(method, p);
      return methodError(method, message + " (" + paramDesc + ")", args);
   }

   static Class<?> getRawType(Type type) {
      Objects.requireNonNull(type, "type == null");
      if (type instanceof Class) {
         return (Class<?>)type;
      } else if (type instanceof ParameterizedType) {
         ParameterizedType parameterizedType = (ParameterizedType)type;
         Type rawType = parameterizedType.getRawType();
         if (!(rawType instanceof Class)) {
            throw new IllegalArgumentException();
         } else {
            return (Class<?>)rawType;
         }
      } else if (type instanceof GenericArrayType) {
         Type componentType = ((GenericArrayType)type).getGenericComponentType();
         return Array.newInstance(getRawType(componentType), 0).getClass();
      } else if (type instanceof TypeVariable) {
         return Object.class;
      } else if (type instanceof WildcardType) {
         return getRawType(((WildcardType)type).getUpperBounds()[0]);
      } else {
         throw new IllegalArgumentException(
            "Expected a Class, ParameterizedType, or GenericArrayType, but <" + type + "> is of type " + type.getClass().getName()
         );
      }
   }

   static boolean equals(Type a, Type b) {
      if (a == b) {
         return true;
      } else if (a instanceof Class) {
         return a.equals(b);
      } else if (a instanceof ParameterizedType) {
         if (!(b instanceof ParameterizedType)) {
            return false;
         } else {
            ParameterizedType pa = (ParameterizedType)a;
            ParameterizedType pb = (ParameterizedType)b;
            Object ownerA = pa.getOwnerType();
            Object ownerB = pb.getOwnerType();
            boolean ownersAreEqual = ownerA == ownerB || ownerA != null && ownerA.equals(ownerB);
            boolean rawTypesAreEqual = pa.getRawType().equals(pb.getRawType());
            boolean typeArgumentsAreEqual = Arrays.equals((Object[])pa.getActualTypeArguments(), (Object[])pb.getActualTypeArguments());
            return ownersAreEqual && rawTypesAreEqual && typeArgumentsAreEqual;
         }
      } else if (a instanceof GenericArrayType) {
         if (!(b instanceof GenericArrayType)) {
            return false;
         } else {
            GenericArrayType ga = (GenericArrayType)a;
            GenericArrayType gb = (GenericArrayType)b;
            return equals(ga.getGenericComponentType(), gb.getGenericComponentType());
         }
      } else if (a instanceof WildcardType) {
         if (!(b instanceof WildcardType)) {
            return false;
         } else {
            WildcardType wa = (WildcardType)a;
            WildcardType wb = (WildcardType)b;
            return Arrays.equals((Object[])wa.getUpperBounds(), (Object[])wb.getUpperBounds())
               && Arrays.equals((Object[])wa.getLowerBounds(), (Object[])wb.getLowerBounds());
         }
      } else if (a instanceof TypeVariable) {
         if (!(b instanceof TypeVariable)) {
            return false;
         } else {
            TypeVariable<?> va = (TypeVariable<?>)a;
            TypeVariable<?> vb = (TypeVariable<?>)b;
            return va.getGenericDeclaration() == vb.getGenericDeclaration() && va.getName().equals(vb.getName());
         }
      } else {
         return false;
      }
   }

   static Type getGenericSupertype(Type context, Class<?> rawType, Class<?> toResolve) {
      if (toResolve == rawType) {
         return context;
      } else {
         if (toResolve.isInterface()) {
            Class<?>[] interfaces = rawType.getInterfaces();
            int i = 0;

            for (int length = interfaces.length; i < length; i++) {
               if (interfaces[i] == toResolve) {
                  return rawType.getGenericInterfaces()[i];
               }

               if (toResolve.isAssignableFrom(interfaces[i])) {
                  return getGenericSupertype(rawType.getGenericInterfaces()[i], interfaces[i], toResolve);
               }
            }
         }

         if (!rawType.isInterface()) {
            while (rawType != Object.class) {
               Class<?> rawSupertype = rawType.getSuperclass();
               if (rawSupertype == toResolve) {
                  return rawType.getGenericSuperclass();
               }

               if (toResolve.isAssignableFrom(rawSupertype)) {
                  return getGenericSupertype(rawType.getGenericSuperclass(), rawSupertype, toResolve);
               }

               rawType = rawSupertype;
            }
         }

         return toResolve;
      }
   }

   private static int indexOf(Object[] array, Object toFind) {
      for (int i = 0; i < array.length; i++) {
         if (toFind.equals(array[i])) {
            return i;
         }
      }

      throw new NoSuchElementException();
   }

   static String typeToString(Type type) {
      return type instanceof Class ? ((Class)type).getName() : type.toString();
   }

   static Type getSupertype(Type context, Class<?> contextRawType, Class<?> supertype) {
      if (!supertype.isAssignableFrom(contextRawType)) {
         throw new IllegalArgumentException();
      } else {
         return resolve(context, contextRawType, getGenericSupertype(context, contextRawType, supertype));
      }
   }

   static Type resolve(Type context, Class<?> contextRawType, Type toResolve) {
      while (toResolve instanceof TypeVariable) {
         TypeVariable<?> typeVariable = (TypeVariable<?>)toResolve;
         toResolve = resolveTypeVariable(context, contextRawType, typeVariable);
         if (toResolve == typeVariable) {
            return toResolve;
         }
      }

      if (toResolve instanceof Class && ((Class)toResolve).isArray()) {
         Class<?> original = (Class<?>)toResolve;
         Type componentType = original.getComponentType();
         Type newComponentType = resolve(context, contextRawType, componentType);
         return (Type)(componentType == newComponentType ? original : new Utils.GenericArrayTypeImpl(newComponentType));
      } else if (toResolve instanceof GenericArrayType) {
         GenericArrayType original = (GenericArrayType)toResolve;
         Type componentType = original.getGenericComponentType();
         Type newComponentType = resolve(context, contextRawType, componentType);
         return (Type)(componentType == newComponentType ? original : new Utils.GenericArrayTypeImpl(newComponentType));
      } else if (toResolve instanceof ParameterizedType) {
         ParameterizedType original = (ParameterizedType)toResolve;
         Type ownerType = original.getOwnerType();
         Type newOwnerType = resolve(context, contextRawType, ownerType);
         boolean changed = newOwnerType != ownerType;
         Type[] args = original.getActualTypeArguments();
         int t = 0;

         for (int length = args.length; t < length; t++) {
            Type resolvedTypeArgument = resolve(context, contextRawType, args[t]);
            if (resolvedTypeArgument != args[t]) {
               if (!changed) {
                  args = (Type[])args.clone();
                  changed = true;
               }

               args[t] = resolvedTypeArgument;
            }
         }

         return (Type)(changed ? new Utils.ParameterizedTypeImpl(newOwnerType, original.getRawType(), args) : original);
      } else if (toResolve instanceof WildcardType) {
         WildcardType original = (WildcardType)toResolve;
         Type[] originalLowerBound = original.getLowerBounds();
         Type[] originalUpperBound = original.getUpperBounds();
         if (originalLowerBound.length == 1) {
            Type lowerBound = resolve(context, contextRawType, originalLowerBound[0]);
            if (lowerBound != originalLowerBound[0]) {
               return new Utils.WildcardTypeImpl(new Type[]{Object.class}, new Type[]{lowerBound});
            }
         } else if (originalUpperBound.length == 1) {
            Type upperBound = resolve(context, contextRawType, originalUpperBound[0]);
            if (upperBound != originalUpperBound[0]) {
               return new Utils.WildcardTypeImpl(new Type[]{upperBound}, EMPTY_TYPE_ARRAY);
            }
         }

         return original;
      } else {
         return toResolve;
      }
   }

   private static Type resolveTypeVariable(Type context, Class<?> contextRawType, TypeVariable<?> unknown) {
      Class<?> declaredByRaw = declaringClassOf(unknown);
      if (declaredByRaw == null) {
         return unknown;
      } else {
         Type declaredBy = getGenericSupertype(context, contextRawType, declaredByRaw);
         if (declaredBy instanceof ParameterizedType) {
            int index = indexOf(declaredByRaw.getTypeParameters(), unknown);
            return ((ParameterizedType)declaredBy).getActualTypeArguments()[index];
         } else {
            return unknown;
         }
      }
   }

   @Nullable
   private static Class<?> declaringClassOf(TypeVariable<?> typeVariable) {
      GenericDeclaration genericDeclaration = typeVariable.getGenericDeclaration();
      return genericDeclaration instanceof Class ? (Class)genericDeclaration : null;
   }

   static void checkNotPrimitive(Type type) {
      if (type instanceof Class && ((Class)type).isPrimitive()) {
         throw new IllegalArgumentException();
      }
   }

   static boolean isAnnotationPresent(Annotation[] annotations, Class<? extends Annotation> cls) {
      for (Annotation annotation : annotations) {
         if (cls.isInstance(annotation)) {
            return true;
         }
      }

      return false;
   }

   static ResponseBody buffer(ResponseBody body) throws IOException {
      Buffer buffer = new Buffer();
      body.source().readAll(buffer);
      return ResponseBody.create(body.contentType(), body.contentLength(), buffer);
   }

   static Type getParameterUpperBound(int index, ParameterizedType type) {
      Type[] types = type.getActualTypeArguments();
      if (index >= 0 && index < types.length) {
         Type paramType = types[index];
         return paramType instanceof WildcardType ? ((WildcardType)paramType).getUpperBounds()[0] : paramType;
      } else {
         throw new IllegalArgumentException("Index " + index + " not in range [0," + types.length + ") for " + type);
      }
   }

   static Type getParameterLowerBound(int index, ParameterizedType type) {
      Type paramType = type.getActualTypeArguments()[index];
      return paramType instanceof WildcardType ? ((WildcardType)paramType).getLowerBounds()[0] : paramType;
   }

   static boolean hasUnresolvableType(@Nullable Type type) {
      if (type instanceof Class) {
         return false;
      } else if (type instanceof ParameterizedType) {
         ParameterizedType parameterizedType = (ParameterizedType)type;

         for (Type typeArgument : parameterizedType.getActualTypeArguments()) {
            if (hasUnresolvableType(typeArgument)) {
               return true;
            }
         }

         return false;
      } else if (type instanceof GenericArrayType) {
         return hasUnresolvableType(((GenericArrayType)type).getGenericComponentType());
      } else if (type instanceof TypeVariable) {
         return true;
      } else if (type instanceof WildcardType) {
         return true;
      } else {
         String className = type == null ? "null" : type.getClass().getName();
         throw new IllegalArgumentException("Expected a Class, ParameterizedType, or GenericArrayType, but <" + type + "> is of type " + className);
      }
   }

   static void throwIfFatal(Throwable t) {
      if (t instanceof VirtualMachineError) {
         throw (VirtualMachineError)t;
      } else if (t instanceof ThreadDeath) {
         throw (ThreadDeath)t;
      } else if (t instanceof LinkageError) {
         throw (LinkageError)t;
      }
   }

   static boolean isUnit(Type type) {
      if (checkForKotlinUnit) {
         try {
            return type == Unit.class;
         } catch (NoClassDefFoundError var2) {
            checkForKotlinUnit = false;
         }
      }

      return false;
   }

   private static final class GenericArrayTypeImpl implements GenericArrayType {
      private final Type componentType;

      GenericArrayTypeImpl(Type componentType) {
         this.componentType = componentType;
      }

      @Override
      public Type getGenericComponentType() {
         return this.componentType;
      }

      @Override
      public boolean equals(Object o) {
         return o instanceof GenericArrayType && Utils.equals(this, (GenericArrayType)o);
      }

      @Override
      public int hashCode() {
         return this.componentType.hashCode();
      }

      @Override
      public String toString() {
         return Utils.typeToString(this.componentType) + "[]";
      }
   }

   static final class ParameterizedTypeImpl implements ParameterizedType {
      @Nullable
      private final Type ownerType;
      private final Type rawType;
      private final Type[] typeArguments;

      ParameterizedTypeImpl(@Nullable Type ownerType, Type rawType, Type... typeArguments) {
         if (rawType instanceof Class && ownerType == null != (((Class)rawType).getEnclosingClass() == null)) {
            throw new IllegalArgumentException();
         } else {
            for (Type typeArgument : typeArguments) {
               Objects.requireNonNull(typeArgument, "typeArgument == null");
               Utils.checkNotPrimitive(typeArgument);
            }

            this.ownerType = ownerType;
            this.rawType = rawType;
            this.typeArguments = (Type[])typeArguments.clone();
         }
      }

      @Override
      public Type[] getActualTypeArguments() {
         return (Type[])this.typeArguments.clone();
      }

      @Override
      public Type getRawType() {
         return this.rawType;
      }

      @Nullable
      @Override
      public Type getOwnerType() {
         return this.ownerType;
      }

      @Override
      public boolean equals(Object other) {
         return other instanceof ParameterizedType && Utils.equals(this, (ParameterizedType)other);
      }

      @Override
      public int hashCode() {
         return Arrays.hashCode((Object[])this.typeArguments) ^ this.rawType.hashCode() ^ (this.ownerType != null ? this.ownerType.hashCode() : 0);
      }

      @Override
      public String toString() {
         if (this.typeArguments.length == 0) {
            return Utils.typeToString(this.rawType);
         } else {
            StringBuilder result = new StringBuilder(30 * (this.typeArguments.length + 1));
            result.append(Utils.typeToString(this.rawType));
            result.append("<").append(Utils.typeToString(this.typeArguments[0]));

            for (int i = 1; i < this.typeArguments.length; i++) {
               result.append(", ").append(Utils.typeToString(this.typeArguments[i]));
            }

            return result.append(">").toString();
         }
      }
   }

   private static final class WildcardTypeImpl implements WildcardType {
      private final Type upperBound;
      @Nullable
      private final Type lowerBound;

      WildcardTypeImpl(Type[] upperBounds, Type[] lowerBounds) {
         if (lowerBounds.length > 1) {
            throw new IllegalArgumentException();
         } else if (upperBounds.length != 1) {
            throw new IllegalArgumentException();
         } else {
            if (lowerBounds.length == 1) {
               if (lowerBounds[0] == null) {
                  throw new NullPointerException();
               }

               Utils.checkNotPrimitive(lowerBounds[0]);
               if (upperBounds[0] != Object.class) {
                  throw new IllegalArgumentException();
               }

               this.lowerBound = lowerBounds[0];
               this.upperBound = Object.class;
            } else {
               if (upperBounds[0] == null) {
                  throw new NullPointerException();
               }

               Utils.checkNotPrimitive(upperBounds[0]);
               this.lowerBound = null;
               this.upperBound = upperBounds[0];
            }
         }
      }

      @Override
      public Type[] getUpperBounds() {
         return new Type[]{this.upperBound};
      }

      @Override
      public Type[] getLowerBounds() {
         return this.lowerBound != null ? new Type[]{this.lowerBound} : Utils.EMPTY_TYPE_ARRAY;
      }

      @Override
      public boolean equals(Object other) {
         return other instanceof WildcardType && Utils.equals(this, (WildcardType)other);
      }

      @Override
      public int hashCode() {
         return (this.lowerBound != null ? 31 + this.lowerBound.hashCode() : 1) ^ 31 + this.upperBound.hashCode();
      }

      @Override
      public String toString() {
         if (this.lowerBound != null) {
            return "? super " + Utils.typeToString(this.lowerBound);
         } else {
            return this.upperBound == Object.class ? "?" : "? extends " + Utils.typeToString(this.upperBound);
         }
      }
   }
}
