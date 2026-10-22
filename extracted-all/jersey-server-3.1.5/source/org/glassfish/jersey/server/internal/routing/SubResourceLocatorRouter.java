package org.glassfish.jersey.server.internal.routing;

import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.SecurityContext;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.UndeclaredThrowableException;
import java.security.PrivilegedAction;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import org.glassfish.jersey.server.SubjectSecurityContext;
import org.glassfish.jersey.server.internal.JerseyResourceContext;
import org.glassfish.jersey.server.internal.LocalizationMessages;
import org.glassfish.jersey.server.internal.process.MappableException;
import org.glassfish.jersey.server.internal.process.RequestProcessingContext;
import org.glassfish.jersey.server.model.Resource;
import org.glassfish.jersey.server.model.ResourceMethod;
import org.glassfish.jersey.server.monitoring.RequestEvent;
import org.glassfish.jersey.server.spi.internal.ParamValueFactoryWithSource;
import org.glassfish.jersey.server.spi.internal.ParameterValueHelper;
import org.glassfish.jersey.server.spi.internal.ValueParamProvider;

final class SubResourceLocatorRouter implements Router {
   private final ResourceMethod locatorModel;
   private final List<ParamValueFactoryWithSource<?>> valueProviders;
   private final RuntimeLocatorModelBuilder runtimeLocatorBuilder;
   private final JerseyResourceContext resourceContext;
   private final Function<Class<?>, ?> createFunction;

   SubResourceLocatorRouter(
      Function<Class<?>, ?> createServiceFunction,
      Collection<ValueParamProvider> valueSuppliers,
      ResourceMethod locatorModel,
      JerseyResourceContext resourceContext,
      RuntimeLocatorModelBuilder runtimeLocatorBuilder
   ) {
      this.runtimeLocatorBuilder = runtimeLocatorBuilder;
      this.locatorModel = locatorModel;
      this.resourceContext = resourceContext;
      this.createFunction = createServiceFunction;
      this.valueProviders = ParameterValueHelper.createValueProviders(valueSuppliers, locatorModel.getInvocable());
   }

   @Override
   public Router.Continuation apply(RequestProcessingContext processingContext) {
      Object subResourceInstance = this.getResource(processingContext);
      if (subResourceInstance == null) {
         throw new NotFoundException();
      } else {
         RoutingContext routingContext = processingContext.routingContext();
         LocatorRouting routing;
         if (subResourceInstance instanceof Resource) {
            routing = this.runtimeLocatorBuilder.getRouting((Resource)subResourceInstance);
         } else {
            Class<?> locatorClass = subResourceInstance.getClass();
            if (locatorClass.isAssignableFrom(Class.class)) {
               locatorClass = (Class<?>)subResourceInstance;
               if (!this.runtimeLocatorBuilder.isCached(locatorClass)) {
                  subResourceInstance = this.createFunction.apply(locatorClass);
               }
            }

            routingContext.pushMatchedResource(subResourceInstance);
            this.resourceContext.bindResourceIfSingleton(subResourceInstance);
            routing = this.runtimeLocatorBuilder.getRouting(locatorClass);
         }

         routingContext.pushLocatorSubResource(routing.locator.getResources().get(0));
         processingContext.triggerEvent(RequestEvent.Type.SUBRESOURCE_LOCATED);
         return Router.Continuation.of(processingContext, routing.router);
      }
   }

   private Object getResource(RequestProcessingContext context) {
      Object resource = context.routingContext().peekMatchedResource();
      Method handlingMethod = this.locatorModel.getInvocable().getHandlingMethod();
      Object[] parameterValues = ParameterValueHelper.getParameterValues(this.valueProviders, context.request());
      context.triggerEvent(RequestEvent.Type.LOCATOR_MATCHED);
      PrivilegedAction invokeMethodAction = () -> {
         try {
            return handlingMethod.invoke(resource, parameterValues);
         } catch (IllegalArgumentException | UndeclaredThrowableException | IllegalAccessException var5x) {
            throw new ProcessingException(LocalizationMessages.ERROR_RESOURCE_JAVA_METHOD_INVOCATION(), var5x);
         } catch (InvocationTargetException var6x) {
            Throwable cause = var6x.getCause();
            if (cause instanceof WebApplicationException) {
               throw (WebApplicationException)cause;
            } else {
               throw new MappableException(cause);
            }
         } catch (Throwable var7) {
            throw new ProcessingException(var7);
         }
      };
      SecurityContext securityContext = context.request().getSecurityContext();
      return securityContext instanceof SubjectSecurityContext
         ? ((SubjectSecurityContext)securityContext).doAsSubject(invokeMethodAction)
         : invokeMethodAction.run();
   }
}
