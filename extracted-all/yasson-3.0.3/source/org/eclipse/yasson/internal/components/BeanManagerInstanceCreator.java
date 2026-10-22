package org.eclipse.yasson.internal.components;

import jakarta.enterprise.context.spi.CreationalContext;
import jakarta.enterprise.inject.spi.AnnotatedType;
import jakarta.enterprise.inject.spi.BeanManager;
import jakarta.enterprise.inject.spi.InjectionTarget;
import jakarta.json.bind.JsonbException;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.eclipse.yasson.internal.properties.MessageKeys;
import org.eclipse.yasson.internal.properties.Messages;
import org.eclipse.yasson.spi.JsonbComponentInstanceCreator;

public class BeanManagerInstanceCreator implements JsonbComponentInstanceCreator {
   private final BeanManager beanManager;
   private final ConcurrentMap<Class<?>, BeanManagerInstanceCreator.CDIManagedBean<?>> injectionTargets = new ConcurrentHashMap<>();

   public BeanManagerInstanceCreator(Object beanManager) {
      if (!(beanManager instanceof BeanManager)) {
         throw new JsonbException(Messages.getMessage(MessageKeys.INTERNAL_ERROR, "beanManager instance should be of type '" + BeanManager.class + "'"));
      } else {
         this.beanManager = (BeanManager)beanManager;
      }
   }

   @Override
   public <T> T getOrCreateComponent(Class<T> componentClass) {
      return (T)this.injectionTargets.computeIfAbsent(componentClass, clazz -> {
         AnnotatedType<T> aType = this.beanManager.createAnnotatedType(componentClass);
         InjectionTarget<T> injectionTarget = this.beanManager.getInjectionTargetFactory(aType).createInjectionTarget(null);
         CreationalContext<T> creationalContext = this.beanManager.createCreationalContext(null);
         T beanInstance = (T)injectionTarget.produce(creationalContext);
         injectionTarget.inject(beanInstance, creationalContext);
         injectionTarget.postConstruct(beanInstance);
         return new BeanManagerInstanceCreator.CDIManagedBean(beanInstance, injectionTarget, creationalContext);
      }).getInstance();
   }

   @Override
   public void close() throws IOException {
      this.injectionTargets.forEach((clazz, target) -> this.cleanupBean((BeanManagerInstanceCreator.CDIManagedBean<?>)target));
      this.injectionTargets.clear();
   }

   private <T> void cleanupBean(BeanManagerInstanceCreator.CDIManagedBean<T> bean) {
      bean.getInjectionTarget().preDestroy(bean.getInstance());
      bean.getInjectionTarget().dispose(bean.getInstance());
      bean.getCreationalContext().release();
   }

   private static final class CDIManagedBean<T> {
      private final T instance;
      private final InjectionTarget<T> injectionTarget;
      private final CreationalContext<T> creationalContext;

      CDIManagedBean(T instance, InjectionTarget<T> injectionTarget, CreationalContext<T> creationalContext) {
         this.instance = instance;
         this.injectionTarget = injectionTarget;
         this.creationalContext = creationalContext;
      }

      private InjectionTarget<T> getInjectionTarget() {
         return this.injectionTarget;
      }

      private T getInstance() {
         return this.instance;
      }

      private CreationalContext<T> getCreationalContext() {
         return this.creationalContext;
      }
   }
}
