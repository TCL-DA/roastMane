package org.glassfish.jersey.server.internal.inject;

import jakarta.inject.Provider;
import jakarta.ws.rs.Encoded;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.function.Function;
import org.glassfish.jersey.internal.inject.Injectee;
import org.glassfish.jersey.internal.inject.InjectionResolver;
import org.glassfish.jersey.internal.util.ReflectionHelper;
import org.glassfish.jersey.server.ContainerRequest;
import org.glassfish.jersey.server.model.Parameter;
import org.glassfish.jersey.server.spi.internal.ValueParamProvider;

public class ParamInjectionResolver<A extends Annotation> implements InjectionResolver<A> {
   private final ValueParamProvider valueParamProvider;
   private final Class<A> annotation;
   private final Provider<ContainerRequest> request;

   public ParamInjectionResolver(ValueParamProvider valueParamProvider, Class<A> annotation, Provider<ContainerRequest> request) {
      this.valueParamProvider = valueParamProvider;
      this.annotation = annotation;
      this.request = request;
   }

   public Object resolve(Injectee injectee) {
      AnnotatedElement annotated = injectee.getParent();
      Annotation[] annotations;
      if (annotated.getClass().equals(Constructor.class)) {
         annotations = ((Constructor)annotated).getParameterAnnotations()[injectee.getPosition()];
      } else {
         annotations = annotated.getDeclaredAnnotations();
      }

      Class componentClass = injectee.getInjecteeClass();
      Type genericType = injectee.getRequiredType();
      Type targetGenericType;
      if (injectee.isFactory()) {
         targetGenericType = ReflectionHelper.getTypeArgument(genericType, 0);
      } else {
         targetGenericType = genericType;
      }

      Class<?> targetType = ReflectionHelper.erasure(targetGenericType);
      Parameter parameter = Parameter.create(componentClass, componentClass, this.hasEncodedAnnotation(injectee), targetType, targetGenericType, annotations);
      Function<ContainerRequest, ?> valueProvider = this.valueParamProvider.getValueProvider(parameter);
      if (valueProvider != null) {
         return injectee.isFactory()
            ? () -> valueProvider.apply((ContainerRequest)this.request.get())
            : valueProvider.apply((ContainerRequest)this.request.get());
      } else {
         return null;
      }
   }

   private boolean hasEncodedAnnotation(Injectee injectee) {
      AnnotatedElement element = injectee.getParent();
      boolean isConstructor = element instanceof Constructor;
      boolean isMethod = element instanceof Method;
      if (isConstructor || isMethod) {
         Annotation[] annotations;
         if (isMethod) {
            annotations = ((Method)element).getParameterAnnotations()[injectee.getPosition()];
         } else {
            annotations = ((Constructor)element).getParameterAnnotations()[injectee.getPosition()];
         }

         for (Annotation annotation : annotations) {
            if (annotation.annotationType().equals(Encoded.class)) {
               return true;
            }
         }
      }

      if (element.isAnnotationPresent(Encoded.class)) {
         return true;
      } else {
         Class<?> clazz = injectee.getInjecteeClass();
         return clazz.isAnnotationPresent(Encoded.class);
      }
   }

   public boolean isConstructorParameterIndicator() {
      return true;
   }

   public boolean isMethodParameterIndicator() {
      return false;
   }

   public Class<A> getAnnotation() {
      return this.annotation;
   }
}
