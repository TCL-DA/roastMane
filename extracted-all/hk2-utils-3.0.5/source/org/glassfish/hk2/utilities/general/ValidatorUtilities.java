package org.glassfish.hk2.utilities.general;

import jakarta.validation.Path;
import jakarta.validation.TraversableResolver;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorContext;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.Path.Node;
import java.lang.annotation.ElementType;
import java.security.AccessController;
import java.security.PrivilegedAction;
import org.glassfish.hk2.utilities.general.internal.MessageInterpolatorImpl;
import org.hibernate.validator.HibernateValidator;

public class ValidatorUtilities {
   private static final TraversableResolver TRAVERSABLE_RESOLVER = new TraversableResolver() {
      public boolean isReachable(
         Object traversableObject, Node traversableProperty, Class<?> rootBeanType, Path pathToTraversableObject, ElementType elementType
      ) {
         return true;
      }

      public boolean isCascadable(
         Object traversableObject, Node traversableProperty, Class<?> rootBeanType, Path pathToTraversableObject, ElementType elementType
      ) {
         return true;
      }
   };
   private static Validator validator;

   private static Validator initializeValidator() {
      ClassLoader cl = Thread.currentThread().getContextClassLoader();

      Validator var3;
      try {
         Thread.currentThread().setContextClassLoader(HibernateValidator.class.getClassLoader());
         ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
         ValidatorContext validatorContext = validatorFactory.usingContext();
         validatorContext.messageInterpolator(new MessageInterpolatorImpl());
         var3 = validatorContext.traversableResolver(TRAVERSABLE_RESOLVER).getValidator();
      } finally {
         Thread.currentThread().setContextClassLoader(cl);
      }

      return var3;
   }

   public static synchronized Validator getValidator() {
      if (validator == null) {
         validator = AccessController.doPrivileged(new PrivilegedAction<Validator>() {
            public Validator run() {
               return ValidatorUtilities.initializeValidator();
            }
         });
      }

      if (validator == null) {
         throw new IllegalStateException("Could not find a jakarta.validator");
      } else {
         return validator;
      }
   }
}
