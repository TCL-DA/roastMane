package org.apache.logging.log4j.util;

import java.lang.StackWalker.Option;
import java.lang.StackWalker.StackFrame;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.function.Predicate;

public final class StackLocator {
   private static final StackWalker WALKER = StackWalker.getInstance(Option.RETAIN_CLASS_REFERENCE);
   private static final StackWalker STACK_WALKER = StackWalker.getInstance();
   private static final StackLocator INSTANCE = new StackLocator();

   public static StackLocator getInstance() {
      return INSTANCE;
   }

   private StackLocator() {
   }

   public Class<?> getCallerClass(final Class<?> sentinelClass, final Predicate<Class<?>> callerPredicate) {
      if (sentinelClass == null) {
         throw new IllegalArgumentException("sentinelClass cannot be null");
      } else if (callerPredicate == null) {
         throw new IllegalArgumentException("callerPredicate cannot be null");
      } else {
         return WALKER.walk(
            s -> s.map(StackFrame::getDeclaringClass)
                  .dropWhile(clazz -> !sentinelClass.equals(clazz))
                  .dropWhile(clazz -> sentinelClass.equals(clazz) || !callerPredicate.test((Class<?>)clazz))
                  .findFirst()
                  .orElse(null)
         );
      }
   }

   public Class<?> getCallerClass(final String fqcn, final String pkg) {
      return WALKER.walk(
            s -> s.dropWhile(f -> !f.getClassName().equals(fqcn))
                  .dropWhile(f -> f.getClassName().equals(fqcn))
                  .dropWhile(f -> !f.getClassName().startsWith(pkg))
                  .findFirst()
         )
         .map(StackFrame::getDeclaringClass)
         .orElse(null);
   }

   public Class<?> getCallerClass(final Class<?> anchor) {
      return WALKER.walk(s -> s.dropWhile(f -> !f.getDeclaringClass().equals(anchor)).dropWhile(f -> f.getDeclaringClass().equals(anchor)).findFirst())
         .map(StackFrame::getDeclaringClass)
         .orElse(null);
   }

   public Class<?> getCallerClass(final int depth) {
      return WALKER.walk(s -> s.skip((long)depth).findFirst()).map(StackFrame::getDeclaringClass).orElse(null);
   }

   public Deque<Class<?>> getCurrentStackTrace() {
      if (PrivateSecurityManagerStackTraceUtil.isEnabled()) {
         return PrivateSecurityManagerStackTraceUtil.getCurrentStackTrace();
      } else {
         Deque<Class<?>> stack = new ArrayDeque<>();
         return WALKER.walk(s -> {
            s.forEach(f -> stack.add(f.getDeclaringClass()));
            return stack;
         });
      }
   }

   public StackTraceElement calcLocation(final String fqcnOfLogger) {
      return STACK_WALKER.walk(s -> s.dropWhile(f -> !f.getClassName().equals(fqcnOfLogger)).dropWhile(f -> f.getClassName().equals(fqcnOfLogger)).findFirst())
         .map(StackFrame::toStackTraceElement)
         .orElse(null);
   }

   public StackTraceElement getStackTraceElement(final int depth) {
      return STACK_WALKER.walk(s -> s.skip((long)depth).findFirst()).map(StackFrame::toStackTraceElement).orElse(null);
   }
}
