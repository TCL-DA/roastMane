package org.glassfish.jersey.server.model;

public abstract class AbstractResourceModelVisitor implements ResourceModelVisitor {
   @Override
   public void visitResource(Resource resource) {
   }

   @Override
   public void visitChildResource(Resource resource) {
   }

   @Override
   public void visitResourceMethod(ResourceMethod method) {
   }

   @Override
   public void visitInvocable(Invocable invocable) {
   }

   @Override
   public void visitMethodHandler(MethodHandler methodHandler) {
   }

   @Override
   public void visitResourceHandlerConstructor(HandlerConstructor constructor) {
   }

   @Override
   public void visitResourceModel(ResourceModel resourceModel) {
   }

   @Override
   public void visitRuntimeResource(RuntimeResource runtimeResource) {
   }
}
