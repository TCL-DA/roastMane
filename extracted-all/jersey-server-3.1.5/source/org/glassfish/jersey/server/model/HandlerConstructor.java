package org.glassfish.jersey.server.model;

import java.lang.reflect.Constructor;
import java.util.List;
import org.glassfish.jersey.model.Parameter.Source;

public final class HandlerConstructor implements Parameterized, ResourceModelComponent {
   private final Constructor<?> constructor;
   private final List<Parameter> parameters;

   HandlerConstructor(Constructor<?> constructor, List<Parameter> parameters) {
      this.constructor = constructor;
      this.parameters = parameters;
   }

   public Constructor<?> getConstructor() {
      return this.constructor;
   }

   @Override
   public List<Parameter> getParameters() {
      return this.parameters;
   }

   @Override
   public boolean requiresEntity() {
      for (Parameter p : this.getParameters()) {
         if (Source.ENTITY == p.getSource()) {
            return true;
         }
      }

      return false;
   }

   @Override
   public void accept(ResourceModelVisitor visitor) {
      visitor.visitResourceHandlerConstructor(this);
   }

   @Override
   public List<ResourceModelComponent> getComponents() {
      return null;
   }
}
