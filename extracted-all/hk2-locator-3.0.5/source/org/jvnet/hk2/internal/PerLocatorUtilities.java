package org.jvnet.hk2.internal;

import jakarta.inject.Inject;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.WeakHashMap;
import org.glassfish.hk2.api.Injectee;
import org.glassfish.hk2.api.InjectionPointIndicator;
import org.glassfish.hk2.api.InjectionResolver;
import org.glassfish.hk2.utilities.general.Hk2ThreadLocal;
import org.glassfish.hk2.utilities.reflection.Pretty;
import org.jvnet.hk2.annotations.Service;

public class PerLocatorUtilities {
   private final Hk2ThreadLocal<WeakHashMap<Class<?>, String>> threadLocalAutoAnalyzerNameCache = new Hk2ThreadLocal<WeakHashMap<Class<?>, String>>() {
      protected WeakHashMap<Class<?>, String> initialValue() {
         return new WeakHashMap<>();
      }
   };
   private final Hk2ThreadLocal<WeakHashMap<AnnotatedElement, SoftAnnotatedElementAnnotationInfo>> threadLocalAnnotationCache = new Hk2ThreadLocal<WeakHashMap<AnnotatedElement, SoftAnnotatedElementAnnotationInfo>>(
      
   ) {
      protected WeakHashMap<AnnotatedElement, SoftAnnotatedElementAnnotationInfo> initialValue() {
         return new WeakHashMap<>();
      }
   };
   private final Hk2ThreadLocal<WeakHashMap<AnnotatedElement, Boolean>> hasInjectCache = new Hk2ThreadLocal<WeakHashMap<AnnotatedElement, Boolean>>() {
      protected WeakHashMap<AnnotatedElement, Boolean> initialValue() {
         return new WeakHashMap<>();
      }
   };
   private volatile ProxyUtilities proxyUtilities;
   private final ServiceLocatorImpl parent;

   PerLocatorUtilities(ServiceLocatorImpl parent) {
      this.parent = parent;
   }

   boolean hasInjectAnnotation(AnnotatedElement annotated) {
      WeakHashMap<AnnotatedElement, Boolean> cache = (WeakHashMap<AnnotatedElement, Boolean>)this.hasInjectCache.get();
      Boolean rv = cache.get(annotated);
      if (rv != null) {
         return rv;
      } else {
         for (Annotation anno : annotated.getAnnotations()) {
            if (anno.annotationType().getAnnotation(InjectionPointIndicator.class) != null) {
               cache.put(annotated, true);
               return true;
            }

            if (this.parent.isInjectAnnotation(anno)) {
               cache.put(annotated, true);
               return true;
            }
         }

         boolean isConstructor;
         Annotation[][] allAnnotations;
         if (annotated instanceof Method) {
            Method m = (Method)annotated;
            isConstructor = false;
            allAnnotations = m.getParameterAnnotations();
         } else {
            if (!(annotated instanceof Constructor)) {
               cache.put(annotated, false);
               return false;
            }

            Constructor<?> c = (Constructor<?>)annotated;
            isConstructor = true;
            allAnnotations = c.getParameterAnnotations();
         }

         for (Annotation[] allParamAnnotations : allAnnotations) {
            for (Annotation paramAnno : allParamAnnotations) {
               if (paramAnno.annotationType().getAnnotation(InjectionPointIndicator.class) != null) {
                  cache.put(annotated, true);
                  return true;
               }

               if (this.parent.isInjectAnnotation(paramAnno, isConstructor)) {
                  cache.put(annotated, true);
                  return true;
               }
            }
         }

         cache.put(annotated, false);
         return false;
      }
   }

   public String getAutoAnalyzerName(Class<?> c) {
      String retVal = (String)((WeakHashMap)this.threadLocalAutoAnalyzerNameCache.get()).get(c);
      if (retVal != null) {
         return retVal;
      } else {
         Service s = c.getAnnotation(Service.class);
         if (s == null) {
            return null;
         } else {
            retVal = s.analyzer();
            ((WeakHashMap)this.threadLocalAutoAnalyzerNameCache.get()).put(c, retVal);
            return retVal;
         }
      }
   }

   public InjectionResolver<?> getInjectionResolver(ServiceLocatorImpl locator, Injectee injectee) throws IllegalStateException {
      return this.getInjectionResolver(locator, injectee.getParent(), injectee.getPosition());
   }

   InjectionResolver<?> getInjectionResolver(ServiceLocatorImpl locator, AnnotatedElement annotatedGuy) throws IllegalStateException {
      if (!(annotatedGuy instanceof Method) && !(annotatedGuy instanceof Constructor)) {
         return this.getInjectionResolver(locator, annotatedGuy, -1);
      } else {
         throw new IllegalArgumentException("Annotated element '" + annotatedGuy + "' can be neither a Method nor a Constructor.");
      }
   }

   private InjectionResolver<?> getInjectionResolver(ServiceLocatorImpl locator, AnnotatedElement annotatedGuy, int position) throws IllegalStateException {
      boolean methodOrConstructor = annotatedGuy instanceof Method || annotatedGuy instanceof Constructor;
      Annotation injectAnnotation = this.getInjectAnnotation(locator, annotatedGuy, methodOrConstructor, position);
      Class<? extends Annotation> injectType = injectAnnotation == null ? Inject.class : injectAnnotation.annotationType();
      InjectionResolver<?> retVal = locator.getInjectionResolver(injectType);
      if (retVal == null) {
         throw new IllegalStateException("There is no installed injection resolver for " + Pretty.clazz(injectType) + " for type " + annotatedGuy);
      } else {
         return retVal;
      }
   }

   private Annotation getInjectAnnotation(ServiceLocatorImpl locator, AnnotatedElement annotated, boolean checkParams, int position) {
      AnnotatedElementAnnotationInfo annotationInfo = this.computeElementAnnotationInfo(annotated);
      if (checkParams && annotationInfo.hasParams) {
         for (Annotation paramAnno : annotationInfo.paramAnnotations[position]) {
            if (locator.isInjectAnnotation(paramAnno, annotationInfo.isConstructor)) {
               return paramAnno;
            }
         }
      }

      for (Annotation annotation : annotationInfo.elementAnnotations) {
         if (locator.isInjectAnnotation(annotation)) {
            return annotation;
         }
      }

      return null;
   }

   private AnnotatedElementAnnotationInfo computeElementAnnotationInfo(AnnotatedElement ae) {
      SoftAnnotatedElementAnnotationInfo soft = (SoftAnnotatedElementAnnotationInfo)((WeakHashMap)this.threadLocalAnnotationCache.get()).get(ae);
      AnnotatedElementAnnotationInfo hard;
      if (soft != null) {
         hard = soft.harden(ae);
      } else {
         hard = Utilities.computeAEAI(ae);
         soft = hard.soften();
         ((WeakHashMap)this.threadLocalAnnotationCache.get()).put(ae, soft);
      }

      return hard;
   }

   public synchronized void releaseCaches() {
      this.hasInjectCache.removeAll();
      if (this.proxyUtilities != null) {
         this.proxyUtilities.releaseCache();
      }
   }

   public void shutdown() {
      this.releaseCaches();
      this.threadLocalAutoAnalyzerNameCache.removeAll();
      this.threadLocalAnnotationCache.removeAll();
   }

   public ProxyUtilities getProxyUtilities() {
      if (this.proxyUtilities != null) {
         return this.proxyUtilities;
      } else {
         synchronized (this) {
            if (this.proxyUtilities != null) {
               return this.proxyUtilities;
            } else {
               this.proxyUtilities = new ProxyUtilities();
               return this.proxyUtilities;
            }
         }
      }
   }
}
