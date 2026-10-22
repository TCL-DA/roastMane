package org.glassfish.jersey.server;

import org.glassfish.jersey.internal.inject.AnnotationLiteral;

public final class ManagedAsyncExecutorLiteral extends AnnotationLiteral<ManagedAsyncExecutor> implements ManagedAsyncExecutor {
   public static final ManagedAsyncExecutor INSTANCE = new ManagedAsyncExecutorLiteral();

   private ManagedAsyncExecutorLiteral() {
   }
}
