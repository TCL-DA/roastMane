package org.glassfish.jersey.internal.inject;

public final class CustomAnnotationLiteral extends AnnotationLiteral<Custom> implements Custom {
   public static final Custom INSTANCE = new CustomAnnotationLiteral();

   private CustomAnnotationLiteral() {
   }
}
