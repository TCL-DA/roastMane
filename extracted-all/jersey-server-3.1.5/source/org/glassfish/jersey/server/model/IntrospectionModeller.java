package org.glassfish.jersey.server.model;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Encoded;
import jakarta.ws.rs.HttpMethod;
import jakarta.ws.rs.NameBinding;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.container.Suspended;
import jakarta.ws.rs.core.MediaType;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.glassfish.jersey.internal.Errors;
import org.glassfish.jersey.internal.util.Producer;
import org.glassfish.jersey.internal.util.ReflectionHelper;
import org.glassfish.jersey.internal.util.Tokenizer;
import org.glassfish.jersey.model.Parameter.Source;
import org.glassfish.jersey.server.ManagedAsync;
import org.glassfish.jersey.server.internal.LocalizationMessages;
import org.glassfish.jersey.server.model.internal.ModelHelper;
import org.glassfish.jersey.server.model.internal.SseTypeResolver;

final class IntrospectionModeller {
   private static final Logger LOGGER = Logger.getLogger(IntrospectionModeller.class.getName());
   private final Class<?> handlerClass;
   private final boolean disableValidation;

   public IntrospectionModeller(Class<?> handlerClass, boolean disableValidation) {
      this.handlerClass = handlerClass;
      this.disableValidation = disableValidation;
   }

   public Resource.Builder createResourceBuilder() {
      return (Resource.Builder)Errors.processWithException(new Producer<Resource.Builder>() {
         public Resource.Builder call() {
            return IntrospectionModeller.this.doCreateResourceBuilder();
         }
      });
   }

   private Resource.Builder doCreateResourceBuilder() {
      if (!this.disableValidation) {
         this.checkForNonPublicMethodIssues();
      }

      Class<?> annotatedResourceClass = ModelHelper.getAnnotatedResourceClass(this.handlerClass);
      Path rPathAnnotation = annotatedResourceClass.getAnnotation(Path.class);
      boolean keepEncodedParams = null != annotatedResourceClass.getAnnotation(Encoded.class);
      List<MediaType> defaultConsumedTypes = extractMediaTypes(annotatedResourceClass.getAnnotation(Consumes.class));
      List<MediaType> defaultProducedTypes = extractMediaTypes(annotatedResourceClass.getAnnotation(Produces.class));
      Collection<Class<? extends Annotation>> defaultNameBindings = ReflectionHelper.getAnnotationTypes(annotatedResourceClass, NameBinding.class);
      MethodList methodList = new MethodList(this.handlerClass);
      List<Parameter> resourceClassParameters = new LinkedList<>();
      this.checkResourceClassSetters(methodList, keepEncodedParams, resourceClassParameters);
      this.checkResourceClassFields(keepEncodedParams, InvocableValidator.isSingleton(this.handlerClass), resourceClassParameters);
      Resource.Builder resourceBuilder;
      if (null != rPathAnnotation) {
         resourceBuilder = Resource.builder(rPathAnnotation.value());
      } else {
         resourceBuilder = Resource.builder();
      }

      boolean extended = false;
      if (this.handlerClass.isAnnotationPresent(ExtendedResource.class)) {
         resourceBuilder.extended(true);
         extended = true;
      }

      resourceBuilder.name(this.handlerClass.getName());
      this.addResourceMethods(
         resourceBuilder, methodList, resourceClassParameters, keepEncodedParams, defaultConsumedTypes, defaultProducedTypes, defaultNameBindings, extended
      );
      this.addSubResourceMethods(
         resourceBuilder, methodList, resourceClassParameters, keepEncodedParams, defaultConsumedTypes, defaultProducedTypes, defaultNameBindings, extended
      );
      this.addSubResourceLocators(resourceBuilder, methodList, resourceClassParameters, keepEncodedParams, extended);
      if (LOGGER.isLoggable(Level.FINEST)) {
         LOGGER.finest(LocalizationMessages.NEW_AR_CREATED_BY_INTROSPECTION_MODELER(resourceBuilder.toString()));
      }

      return resourceBuilder;
   }

   private void checkForNonPublicMethodIssues() {
      MethodList allDeclaredMethods = new MethodList(this.getAllDeclaredMethods(this.handlerClass));

      for (AnnotatedMethod m : allDeclaredMethods.withMetaAnnotation(HttpMethod.class).withoutAnnotation(Path.class).isNotPublic()) {
         Errors.warning(this.handlerClass, LocalizationMessages.NON_PUB_RES_METHOD(m.getMethod().toGenericString()));
      }

      for (AnnotatedMethod m : allDeclaredMethods.withMetaAnnotation(HttpMethod.class).withAnnotation(Path.class).isNotPublic()) {
         Errors.warning(this.handlerClass, LocalizationMessages.NON_PUB_SUB_RES_METHOD(m.getMethod().toGenericString()));
      }

      for (AnnotatedMethod m : allDeclaredMethods.withoutMetaAnnotation(HttpMethod.class).withAnnotation(Path.class).isNotPublic()) {
         Errors.warning(this.handlerClass, LocalizationMessages.NON_PUB_SUB_RES_LOC(m.getMethod().toGenericString()));
      }
   }

   private void checkResourceClassSetters(MethodList methodList, boolean encodedFlag, Collection<Parameter> injectableParameters) {
      for (AnnotatedMethod method : methodList.withoutMetaAnnotation(HttpMethod.class)
         .withoutAnnotation(Path.class)
         .hasNumParams(1)
         .hasReturnType(void.class)
         .nameStartsWith("set")) {
         Parameter p = Parameter.create(
            this.handlerClass,
            method.getMethod().getDeclaringClass(),
            encodedFlag || method.isAnnotationPresent(Encoded.class),
            method.getParameterTypes()[0],
            method.getGenericParameterTypes()[0],
            method.getAnnotations()
         );
         if (null != p) {
            if (!this.disableValidation) {
               ResourceMethodValidator.validateParameter(
                  p, method.getMethod(), method.getMethod().toGenericString(), "1", InvocableValidator.isSingleton(this.handlerClass)
               );
            }

            if (p.getSource() != Source.ENTITY) {
               injectableParameters.add(p);
            }
         }
      }
   }

   private void checkResourceClassFields(boolean encodedFlag, boolean isInSingleton, Collection<Parameter> injectableParameters) {
      for (Field field : (Field[])AccessController.doPrivileged(ReflectionHelper.getDeclaredFieldsPA(this.handlerClass))) {
         if (field.getDeclaredAnnotations().length > 0) {
            Parameter p = Parameter.create(
               this.handlerClass,
               field.getDeclaringClass(),
               encodedFlag || field.isAnnotationPresent(Encoded.class),
               field.getType(),
               field.getGenericType(),
               field.getAnnotations()
            );
            if (null != p) {
               if (!this.disableValidation) {
                  ResourceMethodValidator.validateParameter(p, field, field.toGenericString(), field.getName(), isInSingleton);
               }

               if (p.getSource() != Source.ENTITY) {
                  injectableParameters.add(p);
               }
            }
         }
      }
   }

   private List<Method> getAllDeclaredMethods(final Class<?> clazz) {
      final List<Method> result = new LinkedList<>();
      AccessController.doPrivileged(new PrivilegedAction<Object>() {
         @Override
         public Object run() {
            for (Class current = clazz; current != Object.class && current != null; current = current.getSuperclass()) {
               result.addAll(Arrays.asList(current.getDeclaredMethods()));
            }

            return null;
         }
      });
      return result;
   }

   private static List<MediaType> resolveConsumedTypes(AnnotatedMethod am, List<MediaType> defaultConsumedTypes) {
      return am.isAnnotationPresent(Consumes.class) ? extractMediaTypes((Consumes)am.getAnnotation(Consumes.class)) : defaultConsumedTypes;
   }

   private static List<MediaType> resolveProducedTypes(AnnotatedMethod am, List<MediaType> defaultProducedTypes) {
      return am.isAnnotationPresent(Produces.class) ? extractMediaTypes((Produces)am.getAnnotation(Produces.class)) : defaultProducedTypes;
   }

   private static List<MediaType> extractMediaTypes(Consumes annotation) {
      return annotation != null ? extractMediaTypes(annotation.value()) : Collections.emptyList();
   }

   private static List<MediaType> extractMediaTypes(Produces annotation) {
      return annotation != null ? extractMediaTypes(annotation.value()) : Collections.emptyList();
   }

   private static List<MediaType> extractMediaTypes(String[] values) {
      if (values.length == 0) {
         return Collections.emptyList();
      } else {
         List<MediaType> types = new ArrayList<>(values.length);

         for (String mtEntry : values) {
            for (String mt : Tokenizer.tokenize(mtEntry, ",")) {
               types.add(MediaType.valueOf(mt));
            }
         }

         return types;
      }
   }

   private static void introspectAsyncFeatures(AnnotatedMethod am, ResourceMethod.Builder resourceMethodBuilder) {
      if (am.isAnnotationPresent(ManagedAsync.class)) {
         resourceMethodBuilder.managedAsync();
      }

      for (Annotation[] annotations : am.getParameterAnnotations()) {
         for (Annotation annotation : annotations) {
            if (annotation.annotationType() == Suspended.class) {
               resourceMethodBuilder.suspended(0L, TimeUnit.MILLISECONDS);
            }
         }
      }

      for (Class<?> paramType : am.getParameterTypes()) {
         if (SseTypeResolver.isSseSinkParam(paramType)) {
            resourceMethodBuilder.sse();
         }
      }
   }

   private void addResourceMethods(
      Resource.Builder resourceBuilder,
      MethodList methodList,
      List<Parameter> resourceClassParameters,
      boolean encodedParameters,
      List<MediaType> defaultConsumedTypes,
      List<MediaType> defaultProducedTypes,
      Collection<Class<? extends Annotation>> defaultNameBindings,
      boolean extended
   ) {
      for (AnnotatedMethod am : methodList.withMetaAnnotation(HttpMethod.class).withoutAnnotation(Path.class)) {
         ResourceMethod.Builder methodBuilder = resourceBuilder.addMethod(((HttpMethod)am.getMetaMethodAnnotations(HttpMethod.class).get(0)).value())
            .consumes(resolveConsumedTypes(am, defaultConsumedTypes))
            .produces(resolveProducedTypes(am, defaultProducedTypes))
            .encodedParameters(encodedParameters || am.isAnnotationPresent(Encoded.class))
            .nameBindings(defaultNameBindings)
            .nameBindings(am.getAnnotations())
            .handledBy(this.handlerClass, am.getMethod())
            .handlingMethod(am.getDeclaredMethod())
            .handlerParameters(resourceClassParameters)
            .extended(extended || am.isAnnotationPresent(ExtendedResource.class));
         introspectAsyncFeatures(am, methodBuilder);
      }
   }

   private void addSubResourceMethods(
      Resource.Builder resourceBuilder,
      MethodList methodList,
      List<Parameter> resourceClassParameters,
      boolean encodedParameters,
      List<MediaType> defaultConsumedTypes,
      List<MediaType> defaultProducedTypes,
      Collection<Class<? extends Annotation>> defaultNameBindings,
      boolean extended
   ) {
      for (AnnotatedMethod am : methodList.withMetaAnnotation(HttpMethod.class).withAnnotation(Path.class)) {
         Resource.Builder childResourceBuilder = resourceBuilder.addChildResource(((Path)am.getAnnotation(Path.class)).value());
         ResourceMethod.Builder methodBuilder = childResourceBuilder.addMethod(((HttpMethod)am.getMetaMethodAnnotations(HttpMethod.class).get(0)).value())
            .consumes(resolveConsumedTypes(am, defaultConsumedTypes))
            .produces(resolveProducedTypes(am, defaultProducedTypes))
            .encodedParameters(encodedParameters || am.isAnnotationPresent(Encoded.class))
            .nameBindings(defaultNameBindings)
            .nameBindings(am.getAnnotations())
            .handledBy(this.handlerClass, am.getMethod())
            .handlingMethod(am.getDeclaredMethod())
            .handlerParameters(resourceClassParameters)
            .extended(extended || am.isAnnotationPresent(ExtendedResource.class));
         introspectAsyncFeatures(am, methodBuilder);
      }
   }

   private void addSubResourceLocators(
      Resource.Builder resourceBuilder, MethodList methodList, List<Parameter> resourceClassParameters, boolean encodedParameters, boolean extended
   ) {
      for (AnnotatedMethod am : methodList.withoutMetaAnnotation(HttpMethod.class).withAnnotation(Path.class)) {
         String path = ((Path)am.getAnnotation(Path.class)).value();
         Resource.Builder builder = resourceBuilder;
         if (path != null && !path.isEmpty() && !"/".equals(path)) {
            builder = resourceBuilder.addChildResource(path);
         }

         builder.addMethod()
            .encodedParameters(encodedParameters || am.isAnnotationPresent(Encoded.class))
            .handledBy(this.handlerClass, am.getMethod())
            .handlingMethod(am.getDeclaredMethod())
            .handlerParameters(resourceClassParameters)
            .extended(extended || am.isAnnotationPresent(ExtendedResource.class));
      }
   }
}
