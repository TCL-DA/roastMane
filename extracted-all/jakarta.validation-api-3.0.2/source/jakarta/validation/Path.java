package jakarta.validation;

import java.util.List;

public interface Path extends Iterable<Path.Node> {
   @Override
   String toString();

   public interface BeanNode extends Path.Node {
      Class<?> getContainerClass();

      Integer getTypeArgumentIndex();
   }

   public interface ConstructorNode extends Path.Node {
      List<Class<?>> getParameterTypes();
   }

   public interface ContainerElementNode extends Path.Node {
      Class<?> getContainerClass();

      Integer getTypeArgumentIndex();
   }

   public interface CrossParameterNode extends Path.Node {
   }

   public interface MethodNode extends Path.Node {
      List<Class<?>> getParameterTypes();
   }

   public interface Node {
      String getName();

      boolean isInIterable();

      Integer getIndex();

      Object getKey();

      ElementKind getKind();

      <T extends Path.Node> T as(Class<T> var1);

      @Override
      String toString();
   }

   public interface ParameterNode extends Path.Node {
      int getParameterIndex();
   }

   public interface PropertyNode extends Path.Node {
      Class<?> getContainerClass();

      Integer getTypeArgumentIndex();
   }

   public interface ReturnValueNode extends Path.Node {
   }
}
