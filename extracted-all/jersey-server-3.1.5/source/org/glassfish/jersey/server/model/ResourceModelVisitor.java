package org.glassfish.jersey.server.model;

public interface ResourceModelVisitor {
   void visitResource(Resource var1);

   void visitChildResource(Resource var1);

   void visitResourceMethod(ResourceMethod var1);

   void visitInvocable(Invocable var1);

   void visitMethodHandler(MethodHandler var1);

   void visitResourceHandlerConstructor(HandlerConstructor var1);

   void visitResourceModel(ResourceModel var1);

   void visitRuntimeResource(RuntimeResource var1);
}
