package org.glassfish.jersey.server.model;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.security.AccessController;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.glassfish.jersey.internal.util.ReflectionHelper;

public final class MethodList implements Iterable<AnnotatedMethod> {
   private AnnotatedMethod[] methods;

   public MethodList(Class<?> c) {
      this(c, false);
   }

   public MethodList(Class<?> c, boolean declaredMethods) {
      this(declaredMethods ? getAllDeclaredMethods(c) : getMethods(c));
   }

   private static List<Method> getAllDeclaredMethods(Class<?> c) {
      List<Method> l = new ArrayList<>();

      while (c != null && c != Object.class) {
         l.addAll(AccessController.doPrivileged(ReflectionHelper.getDeclaredMethodsPA(c)));
         c = c.getSuperclass();
      }

      return l;
   }

   private static List<Method> getMethods(Class<?> c) {
      return Arrays.asList(c.getMethods());
   }

   public MethodList(Collection<Method> methods) {
      List<AnnotatedMethod> l = new ArrayList<>(methods.size());

      for (Method m : methods) {
         if (!m.isSynthetic() && m.getDeclaringClass() != Object.class) {
            l.add(new AnnotatedMethod(m));
         }
      }

      this.methods = new AnnotatedMethod[l.size()];
      this.methods = l.toArray(this.methods);
   }

   public MethodList(Method... methods) {
      this(Arrays.asList(methods));
   }

   public MethodList(AnnotatedMethod... methods) {
      this.methods = methods;
   }

   @Override
   public Iterator<AnnotatedMethod> iterator() {
      return Arrays.asList(this.methods).iterator();
   }

   public MethodList isNotPublic() {
      return this.filter(new MethodList.Filter() {
         @Override
         public boolean keep(AnnotatedMethod m) {
            return !Modifier.isPublic(m.getMethod().getModifiers());
         }
      });
   }

   public MethodList hasNumParams(final int paramCount) {
      return this.filter(new MethodList.Filter() {
         @Override
         public boolean keep(AnnotatedMethod m) {
            return m.getParameterTypes().length == paramCount;
         }
      });
   }

   public MethodList hasReturnType(final Class<?> returnType) {
      return this.filter(new MethodList.Filter() {
         @Override
         public boolean keep(AnnotatedMethod m) {
            return m.getMethod().getReturnType() == returnType;
         }
      });
   }

   public MethodList nameStartsWith(final String prefix) {
      return this.filter(new MethodList.Filter() {
         @Override
         public boolean keep(AnnotatedMethod m) {
            return m.getMethod().getName().startsWith(prefix);
         }
      });
   }

   public <T extends Annotation> MethodList withAnnotation(final Class<T> annotation) {
      return this.filter(new MethodList.Filter() {
         @Override
         public boolean keep(AnnotatedMethod m) {
            return m.getAnnotation(annotation) != null;
         }
      });
   }

   public <T extends Annotation> MethodList withMetaAnnotation(final Class<T> annotation) {
      return this.filter(new MethodList.Filter() {
         @Override
         public boolean keep(AnnotatedMethod m) {
            for (Annotation a : m.getAnnotations()) {
               if (a.annotationType().<T>getAnnotation(annotation) != null) {
                  return true;
               }
            }

            return false;
         }
      });
   }

   public <T extends Annotation> MethodList withoutAnnotation(final Class<T> annotation) {
      return this.filter(new MethodList.Filter() {
         @Override
         public boolean keep(AnnotatedMethod m) {
            return m.getAnnotation(annotation) == null;
         }
      });
   }

   public <T extends Annotation> MethodList withoutMetaAnnotation(final Class<T> annotation) {
      return this.filter(new MethodList.Filter() {
         @Override
         public boolean keep(AnnotatedMethod m) {
            for (Annotation a : m.getAnnotations()) {
               if (a.annotationType().<T>getAnnotation(annotation) != null) {
                  return false;
               }
            }

            return true;
         }
      });
   }

   public MethodList filter(MethodList.Filter filter) {
      List<AnnotatedMethod> result = new ArrayList<>();

      for (AnnotatedMethod m : this.methods) {
         if (filter.keep(m)) {
            result.add(m);
         }
      }

      return new MethodList(result.toArray(new AnnotatedMethod[result.size()]));
   }

   public interface Filter {
      boolean keep(AnnotatedMethod var1);
   }
}
