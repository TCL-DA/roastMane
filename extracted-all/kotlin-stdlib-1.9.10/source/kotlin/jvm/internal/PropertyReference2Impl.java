package kotlin.jvm.internal;

import kotlin.SinceKotlin;
import kotlin.reflect.KClass;
import kotlin.reflect.KDeclarationContainer;

public class PropertyReference2Impl extends PropertyReference2 {
   public PropertyReference2Impl(KDeclarationContainer owner, String name, String signature) {
      super(((ClassBasedDeclarationContainer)owner).getJClass(), name, signature, owner instanceof KClass ? 0 : 1);
   }

   @SinceKotlin(
      version = "1.4"
   )
   public PropertyReference2Impl(Class owner, String name, String signature, int flags) {
      super(owner, name, signature, flags);
   }

   @Override
   public Object get(Object receiver1, Object receiver2) {
      return this.getGetter().call(new Object[]{receiver1, receiver2});
   }
}
