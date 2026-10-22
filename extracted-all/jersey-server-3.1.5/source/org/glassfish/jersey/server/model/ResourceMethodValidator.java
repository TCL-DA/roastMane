package org.glassfish.jersey.server.model;

import jakarta.ws.rs.BeanParam;
import jakarta.ws.rs.CookieParam;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.HttpMethod;
import jakarta.ws.rs.MatrixParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import org.glassfish.jersey.internal.Errors;
import org.glassfish.jersey.model.Parameter.Source;
import org.glassfish.jersey.server.ContainerRequest;
import org.glassfish.jersey.server.internal.LocalizationMessages;
import org.glassfish.jersey.server.model.internal.SseTypeResolver;
import org.glassfish.jersey.server.spi.internal.ParameterValueHelper;
import org.glassfish.jersey.server.spi.internal.ValueParamProvider;

class ResourceMethodValidator extends AbstractResourceModelVisitor {
   private final Collection<ValueParamProvider> valueParamProviders;
   private static final Set<Class> PARAM_ANNOTATION_SET = createParamAnnotationSet();

   ResourceMethodValidator(Collection<ValueParamProvider> valueParamProviders) {
      this.valueParamProviders = valueParamProviders;
   }

   @Override
   public void visitResourceMethod(ResourceMethod method) {
      switch (method.getType()) {
         case RESOURCE_METHOD:
            this.visitJaxrsResourceMethod(method);
            break;
         case SUB_RESOURCE_LOCATOR:
            this.visitSubResourceLocator(method);
      }
   }

   private void visitJaxrsResourceMethod(ResourceMethod method) {
      this.checkMethod(method);
   }

   private void checkMethod(ResourceMethod method) {
      this.checkValueProviders(method);
      Invocable invocable = method.getInvocable();
      this.checkParameters(method);
      if ("GET".equals(method.getHttpMethod())) {
         long eventSinkCount = invocable.getParameters().stream().filter(parameter -> SseTypeResolver.isSseSinkParam(parameter.getRawType())).count();
         boolean isSse = eventSinkCount > 0L;
         if (eventSinkCount > 1L) {
            Errors.warning(method, LocalizationMessages.MULTIPLE_EVENT_SINK_INJECTION(invocable.getHandlingMethod()));
         }

         if (void.class == invocable.getHandlingMethod().getReturnType() && !method.isSuspendDeclared() && !isSse) {
            Errors.hint(method, LocalizationMessages.GET_RETURNS_VOID(invocable.getHandlingMethod()));
         }

         if (invocable.requiresEntity() && !invocable.isInflector()) {
            Errors.warning(method, LocalizationMessages.GET_CONSUMES_ENTITY(invocable.getHandlingMethod()));
         }

         for (Parameter p : invocable.getParameters()) {
            if (p.isAnnotationPresent(FormParam.class)) {
               Errors.fatal(method, LocalizationMessages.GET_CONSUMES_FORM_PARAM(invocable.getHandlingMethod()));
               break;
            }
         }

         if (isSse && void.class != invocable.getHandlingMethod().getReturnType()) {
            Errors.fatal(method, LocalizationMessages.EVENT_SINK_RETURNS_TYPE(invocable.getHandlingMethod()));
         }
      }

      List<String> httpMethodAnnotations = new LinkedList<>();

      for (Annotation a : invocable.getHandlingMethod().getDeclaredAnnotations()) {
         if (null != a.annotationType().getAnnotation(HttpMethod.class)) {
            httpMethodAnnotations.add(a.toString());
         }
      }

      if (httpMethodAnnotations.size() > 1) {
         Errors.fatal(method, LocalizationMessages.MULTIPLE_HTTP_METHOD_DESIGNATORS(invocable.getHandlingMethod(), httpMethodAnnotations.toString()));
      }

      Type responseType = invocable.getResponseType();
      if (!isConcreteType(responseType)) {
         Errors.warning(
            invocable.getHandlingMethod(),
            LocalizationMessages.TYPE_OF_METHOD_NOT_RESOLVABLE_TO_CONCRETE_TYPE(responseType, invocable.getHandlingMethod().toGenericString())
         );
      }

      Path pathAnnotation = invocable.getHandlingMethod().getAnnotation(Path.class);
      if (pathAnnotation != null) {
         String path = pathAnnotation.value();
         if (path == null || path.isEmpty() || "/".equals(path)) {
            Errors.warning(
               invocable.getHandlingMethod(),
               LocalizationMessages.METHOD_EMPTY_PATH_ANNOTATION(invocable.getHandlingMethod().getName(), invocable.getHandler().getHandlerClass().getName())
            );
         }
      }

      if (httpMethodAnnotations.size() != 0) {
         this.checkUnexpectedAnnotations(method);
      }
   }

   private void checkUnexpectedAnnotations(ResourceMethod resourceMethod) {
      Invocable invocable = resourceMethod.getInvocable();

      for (Annotation annotation : invocable.getHandlingMethod().getDeclaredAnnotations()) {
         if (PARAM_ANNOTATION_SET.contains(annotation.annotationType())) {
            Errors.fatal(
               resourceMethod,
               LocalizationMessages.METHOD_UNEXPECTED_ANNOTATION(
                  invocable.getHandlingMethod().getName(), invocable.getHandler().getHandlerClass().getName(), annotation.annotationType().getName()
               )
            );
         }
      }
   }

   private void checkValueProviders(ResourceMethod method) {
      List<? extends Function<ContainerRequest, ?>> valueProviders = ParameterValueHelper.createValueProviders(this.valueParamProviders, method.getInvocable());
      if (valueProviders.contains(null)) {
         int index = valueProviders.indexOf(null);
         Errors.fatal(method, LocalizationMessages.ERROR_PARAMETER_MISSING_VALUE_PROVIDER(index, method.getInvocable().getHandlingMethod()));
      }
   }

   private void visitSubResourceLocator(ResourceMethod locator) {
      this.checkParameters(locator);
      this.checkValueProviders(locator);
      Invocable invocable = locator.getInvocable();
      if (void.class == invocable.getRawResponseType()) {
         Errors.fatal(locator, LocalizationMessages.SUBRES_LOC_RETURNS_VOID(invocable.getHandlingMethod()));
      }

      if (invocable.getHandlingMethod().getAnnotation(Path.class) != null) {
         this.checkUnexpectedAnnotations(locator);
      }
   }

   private void checkParameters(ResourceMethod method) {
      Invocable invocable = method.getInvocable();
      Method handlingMethod = invocable.getHandlingMethod();
      int paramCount = 0;
      int nonAnnotatedParameters = 0;

      for (Parameter p : invocable.getParameters()) {
         validateParameter(p, handlingMethod, handlingMethod.toGenericString(), Integer.toString(++paramCount), false);
         if (method.getType() == ResourceMethod.JaxrsType.SUB_RESOURCE_LOCATOR && Source.ENTITY == p.getSource()) {
            Errors.fatal(method, LocalizationMessages.SUBRES_LOC_HAS_ENTITY_PARAM(invocable.getHandlingMethod()));
         } else if (p.getAnnotations().length == 0) {
            if (++nonAnnotatedParameters > 1) {
               Errors.fatal(
                  method,
                  LocalizationMessages.AMBIGUOUS_NON_ANNOTATED_PARAMETER(invocable.getHandlingMethod(), invocable.getHandlingMethod().getDeclaringClass())
               );
            }
         }
      }
   }

   private boolean isSseInjected(Invocable invocable) {
      return invocable.getParameters().stream().anyMatch(parameter -> SseTypeResolver.isSseSinkParam(parameter.getRawType()));
   }

   private static Set<Class> createParamAnnotationSet() {
      Set<Class> set = new HashSet<>(6);
      set.add(HeaderParam.class);
      set.add(CookieParam.class);
      set.add(MatrixParam.class);
      set.add(QueryParam.class);
      set.add(PathParam.class);
      set.add(BeanParam.class);
      return Collections.unmodifiableSet(set);
   }

   static void validateParameter(
      final Parameter parameter, final Object source, final String reportedSourceName, final String reportedParameterName, final boolean injectionsForbidden
   ) {
      Errors.processWithException(new Runnable() {
         @Override
         public void run() {
            int counter = 0;
            Annotation[] annotations = parameter.getAnnotations();

            for (Annotation a : annotations) {
               if (ResourceMethodValidator.PARAM_ANNOTATION_SET.contains(a.annotationType())) {
                  if (injectionsForbidden) {
                     Errors.fatal(source, LocalizationMessages.SINGLETON_INJECTS_PARAMETER(reportedSourceName, reportedParameterName));
                     break;
                  }

                  if (++counter > 1) {
                     Errors.warning(source, LocalizationMessages.AMBIGUOUS_PARAMETER(reportedSourceName, reportedParameterName));
                     break;
                  }
               }
            }

            Type paramType = parameter.getType();
            if (!ResourceMethodValidator.isConcreteType(paramType)) {
               Errors.warning(source, LocalizationMessages.PARAMETER_UNRESOLVABLE(reportedParameterName, paramType, reportedSourceName));
            }
         }
      });
   }

   private static boolean isConcreteType(Type t) {
      return t instanceof ParameterizedType ? isConcreteParameterizedType((ParameterizedType)t) : t instanceof Class;
   }

   private static boolean isConcreteParameterizedType(ParameterizedType pt) {
      boolean isConcrete = true;

      for (Type t : pt.getActualTypeArguments()) {
         isConcrete &= isConcreteType(t);
      }

      return isConcrete;
   }
}
