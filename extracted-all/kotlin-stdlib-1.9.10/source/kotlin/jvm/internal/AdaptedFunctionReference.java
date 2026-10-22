package kotlin.jvm.internal;

import java.io.Serializable;
import kotlin.SinceKotlin;
import kotlin.reflect.KDeclarationContainer;

@SinceKotlin(
   version = "1.4"
)
public class AdaptedFunctionReference implements FunctionBase, Serializable {
   protected final Object receiver;
   private final Class owner;
   private final String name;
   private final String signature;
   private final boolean isTopLevel;
   private final int arity;
   private final int flags;

   public AdaptedFunctionReference(int arity, Class owner, String name, String signature, int flags) {
      this(arity, CallableReference.NO_RECEIVER, owner, name, signature, flags);
   }

   public AdaptedFunctionReference(int arity, Object receiver, Class owner, String name, String signature, int flags) {
      this.receiver = receiver;
      this.owner = owner;
      this.name = name;
      this.signature = signature;
      this.isTopLevel = (flags & 1) == 1;
      this.arity = arity;
      this.flags = flags >> 1;
   }

   @Override
   public int getArity() {
      return this.arity;
   }

   public KDeclarationContainer getOwner() {
      return (KDeclarationContainer)(this.owner == null
         ? null
         : (this.isTopLevel ? Reflection.getOrCreateKotlinPackage(this.owner) : Reflection.getOrCreateKotlinClass(this.owner)));
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (!(o instanceof AdaptedFunctionReference)) {
         return false;
      } else {
         AdaptedFunctionReference other = (AdaptedFunctionReference)o;
         return this.isTopLevel == other.isTopLevel
            && this.arity == other.arity
            && this.flags == other.flags
            && Intrinsics.areEqual(this.receiver, other.receiver)
            && Intrinsics.areEqual(this.owner, other.owner)
            && this.name.equals(other.name)
            && this.signature.equals(other.signature);
      }
   }

   @Override
   public int hashCode() {
      int result = this.receiver != null ? this.receiver.hashCode() : 0;
      result = result * 31 + (this.owner != null ? this.owner.hashCode() : 0);
      result = result * 31 + this.name.hashCode();
      result = result * 31 + this.signature.hashCode();
      result = result * 31 + (this.isTopLevel ? 1231 : 1237);
      result = result * 31 + this.arity;
      return result * 31 + this.flags;
   }

   @Override
   public String toString() {
      return Reflection.renderLambdaToString(this);
   }
}
