package org.glassfish.jersey.server.model;

import jakarta.ws.rs.Encoded;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.security.AccessController;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import org.glassfish.jersey.internal.util.ReflectionHelper;
import org.glassfish.jersey.model.Parameter.ParamAnnotationHelper;
import org.glassfish.jersey.model.Parameter.ParamCreationFactory;
import org.glassfish.jersey.model.Parameter.Source;
import org.glassfish.jersey.model.internal.spi.ParameterServiceProvider;
import org.glassfish.jersey.server.Uri;

public class Parameter extends org.glassfish.jersey.model.Parameter implements AnnotatedElement {
   public static <PARAMETER extends org.glassfish.jersey.model.Parameter> PARAMETER create(
      Class concreteClass, Class declaringClass, boolean encodeByDefault, Class<?> rawType, Type type, Annotation[] annotations
   ) {
      return (PARAMETER)create(concreteClass, declaringClass, encodeByDefault, rawType, type, annotations, Parameter.class);
   }

   protected static <PARAMETER extends org.glassfish.jersey.model.Parameter> List<PARAMETER> createList(
      Class concreteClass,
      Class declaringClass,
      boolean keepEncoded,
      Class[] parameterTypes,
      Type[] genericParameterTypes,
      Annotation[][] parameterAnnotations,
      Class<?> parameterClass
   ) {
      List<PARAMETER> parameters = new ArrayList<>(parameterTypes.length);

      for (int i = 0; i < parameterTypes.length; i++) {
         PARAMETER parameter = create(concreteClass, declaringClass, keepEncoded, parameterTypes[i], genericParameterTypes[i], parameterAnnotations[i]);
         if (null == parameter) {
            return Collections.emptyList();
         }

         parameters.add(parameter);
      }

      return parameters;
   }

   public static <PARAMETER extends org.glassfish.jersey.model.Parameter> List<PARAMETER> create(
      Class concreteClass, Class declaringClass, Constructor<?> ctor, boolean keepEncoded
   ) {
      return createList(concreteClass, declaringClass, ctor, keepEncoded, Parameter.class);
   }

   public static <PARAMETER extends org.glassfish.jersey.model.Parameter> List<PARAMETER> create(
      Class concreteClass, Class declaringClass, Method javaMethod, boolean keepEncoded
   ) {
      return createList(concreteClass, declaringClass, javaMethod, keepEncoded, Parameter.class);
   }

   public static Parameter overrideSource(Parameter original, Source source) {
      return new Parameter(
         original.getAnnotations(),
         original.getSourceAnnotation(),
         source,
         source.name(),
         original.getRawType(),
         original.getType(),
         original.isEncoded(),
         original.getDefaultValue()
      );
   }

   protected Parameter(
      Annotation[] markers, Annotation marker, Source source, String sourceName, Class<?> rawType, Type type, boolean encoded, String defaultValue
   ) {
      super(markers, marker, source, sourceName, rawType, type, encoded, defaultValue);
   }

   public boolean isQualified() {
      for (Annotation a : this.getAnnotations()) {
         if (a.annotationType().isAnnotationPresent(ParamQualifier.class)) {
            return true;
         }
      }

      return false;
   }

   public static class BeanParameter extends Parameter {
      private final Collection<Parameter> parameters;

      private BeanParameter(Annotation[] markers, Annotation marker, String sourceName, Class<?> rawType, Type type, boolean encoded, String defaultValue) {
         super(markers, marker, Source.BEAN_PARAM, sourceName, rawType, type, encoded, defaultValue);
         Collection<Parameter> parameters = new LinkedList<>();

         for (Field field : (Field[])AccessController.doPrivileged(ReflectionHelper.getDeclaredFieldsPA(rawType))) {
            if (field.getDeclaredAnnotations().length > 0) {
               Parameter beanParamParameter = Parameter.create(
                  rawType, field.getDeclaringClass(), field.isAnnotationPresent(Encoded.class), field.getType(), field.getGenericType(), field.getAnnotations()
               );
               parameters.add(beanParamParameter);
            }
         }

         for (Constructor constructor : (Constructor[])AccessController.doPrivileged(ReflectionHelper.getDeclaredConstructorsPA(rawType))) {
            for (org.glassfish.jersey.model.Parameter parameter : Parameter.create(rawType, rawType, constructor, false)) {
               parameters.add((Parameter)parameter);
            }
         }

         this.parameters = Collections.unmodifiableCollection(parameters);
      }

      public Collection<Parameter> getParameters() {
         return this.parameters;
      }
   }

   public static class ServerParameterService implements ParameterServiceProvider {
      public Map<Class, ParamAnnotationHelper> getParameterAnnotationHelperMap() {
         Map<Class, ParamAnnotationHelper> m = new WeakHashMap<>();
         m.put(Uri.class, new ParamAnnotationHelper<Uri>() {
            public String getValueOf(Uri a) {
               return a.value();
            }

            public Source getSource() {
               return Source.URI;
            }
         });
         return m;
      }

      public ParamCreationFactory<Parameter> getParameterCreationFactory() {
         return new ParamCreationFactory<Parameter>() {
            public boolean isFor(Class<?> clazz) {
               return clazz == Parameter.class;
            }

            public Parameter createParameter(
               Annotation[] markers, Annotation marker, Source source, String sourceName, Class<?> rawType, Type type, boolean encoded, String defaultValue
            ) {
               return new Parameter(markers, marker, source, sourceName, rawType, type, encoded, defaultValue);
            }

            public Parameter createBeanParameter(
               Annotation[] markers, Annotation marker, Source source, String sourceName, Class<?> rawType, Type type, boolean encoded, String defaultValue
            ) {
               return new Parameter.BeanParameter(markers, marker, sourceName, rawType, type, encoded, defaultValue);
            }
         };
      }
   }
}
