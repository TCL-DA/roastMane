package org.glassfish.jersey.model;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.CookieParam;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.Encoded;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.HttpMethod;
import jakarta.ws.rs.MatrixParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.security.AccessController;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.glassfish.jersey.internal.util.ReflectionHelper;

public class AnnotatedMethod implements AnnotatedElement {
   private static final Set<Class<? extends Annotation>> METHOD_META_ANNOTATIONS = getSet(HttpMethod.class);
   private static final Set<Class<? extends Annotation>> METHOD_ANNOTATIONS = getSet(Path.class, Produces.class, Consumes.class);
   private static final Set<Class<? extends Annotation>> PARAMETER_ANNOTATIONS = getSet(
      Context.class,
      Encoded.class,
      DefaultValue.class,
      MatrixParam.class,
      QueryParam.class,
      CookieParam.class,
      HeaderParam.class,
      PathParam.class,
      FormParam.class
   );
   private final Method m;
   private final Method am;
   private final Annotation[] methodAnnotations;
   private final Annotation[][] parameterAnnotations;

   @SafeVarargs
   private static Set<Class<? extends Annotation>> getSet(Class<? extends Annotation>... cs) {
      Set<Class<? extends Annotation>> s = new HashSet<>();
      s.addAll(Arrays.asList(cs));
      return s;
   }

   public AnnotatedMethod(Method method) {
      this.m = method;
      this.am = findAnnotatedMethod(method);
      if (method.equals(this.am)) {
         this.methodAnnotations = method.getAnnotations();
         this.parameterAnnotations = method.getParameterAnnotations();
      } else {
         this.methodAnnotations = mergeMethodAnnotations(method, this.am);
         this.parameterAnnotations = mergeParameterAnnotations(method, this.am);
      }
   }

   public Method getMethod() {
      return this.am;
   }

   public Method getDeclaredMethod() {
      return this.m;
   }

   public Annotation[][] getParameterAnnotations() {
      return (Annotation[][])this.parameterAnnotations.clone();
   }

   public Class<?>[] getParameterTypes() {
      return this.am.getParameterTypes();
   }

   public TypeVariable<Method>[] getTypeParameters() {
      return this.am.getTypeParameters();
   }

   public Type[] getGenericParameterTypes() {
      return this.am.getGenericParameterTypes();
   }

   public <T extends Annotation> List<T> getMetaMethodAnnotations(Class<T> annotation) {
      List<T> ma = new ArrayList<>();

      for (Annotation a : this.methodAnnotations) {
         T metaAnnotation = a.annotationType().getAnnotation(annotation);
         if (metaAnnotation != null) {
            ma.add(metaAnnotation);
         }
      }

      return ma;
   }

   @Override
   public String toString() {
      return this.m.toString();
   }

   @Override
   public boolean isAnnotationPresent(Class<? extends Annotation> annotationType) {
      for (Annotation ma : this.methodAnnotations) {
         if (ma.annotationType() == annotationType) {
            return true;
         }
      }

      return false;
   }

   @Override
   public <T extends Annotation> T getAnnotation(Class<T> annotationType) {
      for (Annotation ma : this.methodAnnotations) {
         if (ma.annotationType() == annotationType) {
            return annotationType.cast(ma);
         }
      }

      return this.am.getAnnotation(annotationType);
   }

   @Override
   public Annotation[] getAnnotations() {
      return (Annotation[])this.methodAnnotations.clone();
   }

   @Override
   public Annotation[] getDeclaredAnnotations() {
      return this.getAnnotations();
   }

   private static Annotation[] mergeMethodAnnotations(Method m, Method am) {
      List<Annotation> al = asList(m.getAnnotations());

      for (Annotation a : am.getAnnotations()) {
         if (!m.isAnnotationPresent((Class<? extends Annotation>)a.getClass())) {
            al.add(a);
         }
      }

      return al.toArray(new Annotation[al.size()]);
   }

   private static Annotation[][] mergeParameterAnnotations(Method m, Method am) {
      Annotation[][] methodParamAnnotations = m.getParameterAnnotations();
      Annotation[][] annotatedMethodParamAnnotations = am.getParameterAnnotations();
      List<List<Annotation>> methodParamAnnotationsList = new ArrayList<>();

      for (int i = 0; i < methodParamAnnotations.length; i++) {
         List<Annotation> al = asList(methodParamAnnotations[i]);

         for (Annotation a : annotatedMethodParamAnnotations[i]) {
            if (annotationNotInList((Class<? extends Annotation>)a.getClass(), al)) {
               al.add(a);
            }
         }

         methodParamAnnotationsList.add(al);
      }

      Annotation[][] mergedAnnotations = new Annotation[methodParamAnnotations.length][];

      for (int i = 0; i < methodParamAnnotations.length; i++) {
         List<Annotation> paramAnnotations = methodParamAnnotationsList.get(i);
         mergedAnnotations[i] = paramAnnotations.toArray(new Annotation[paramAnnotations.size()]);
      }

      return mergedAnnotations;
   }

   private static boolean annotationNotInList(Class<? extends Annotation> ca, List<Annotation> la) {
      for (Annotation a : la) {
         if (ca == a.getClass()) {
            return false;
         }
      }

      return true;
   }

   private static Method findAnnotatedMethod(Method m) {
      Method am = findAnnotatedMethod(m.getDeclaringClass(), m);
      return am != null ? am : m;
   }

   private static Method findAnnotatedMethod(Class<?> c, Method m) {
      if (c == Object.class) {
         return null;
      } else {
         m = AccessController.doPrivileged(ReflectionHelper.findMethodOnClassPA(c, m));
         if (m == null) {
            return null;
         } else if (hasAnnotations(m)) {
            return m;
         } else {
            Class<?> sc = c.getSuperclass();
            if (sc != null && sc != Object.class) {
               Method sm = findAnnotatedMethod(sc, m);
               if (sm != null) {
                  return sm;
               }
            }

            for (Class<?> ic : c.getInterfaces()) {
               Method im = findAnnotatedMethod(ic, m);
               if (im != null) {
                  return im;
               }
            }

            return null;
         }
      }
   }

   private static boolean hasAnnotations(Method m) {
      return hasMetaMethodAnnotations(m) || hasMethodAnnotations(m) || hasParameterAnnotations(m);
   }

   private static boolean hasMetaMethodAnnotations(Method m) {
      for (Class<? extends Annotation> ac : METHOD_META_ANNOTATIONS) {
         for (Annotation a : m.getAnnotations()) {
            if (a.annotationType().getAnnotation(ac) != null) {
               return true;
            }
         }
      }

      return false;
   }

   private static boolean hasMethodAnnotations(Method m) {
      for (Class<? extends Annotation> ac : METHOD_ANNOTATIONS) {
         if (m.isAnnotationPresent(ac)) {
            return true;
         }
      }

      return false;
   }

   private static boolean hasParameterAnnotations(Method m) {
      for (Annotation[] as : m.getParameterAnnotations()) {
         for (Annotation a : as) {
            if (PARAMETER_ANNOTATIONS.contains(a.annotationType())) {
               return true;
            }
         }
      }

      return false;
   }

   @SafeVarargs
   private static <T> List<T> asList(T... ts) {
      List<T> l = new ArrayList<>();
      l.addAll(Arrays.asList(ts));
      return l;
   }
}
