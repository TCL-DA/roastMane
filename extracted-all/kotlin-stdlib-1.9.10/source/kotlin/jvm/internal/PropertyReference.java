package kotlin.jvm.internal;

import kotlin.SinceKotlin;
import kotlin.reflect.KCallable;
import kotlin.reflect.KProperty;

public abstract class PropertyReference extends CallableReference implements KProperty {
   private final boolean syntheticJavaProperty;

   public PropertyReference() {
      this.syntheticJavaProperty = false;
   }

   @SinceKotlin(
      version = "1.1"
   )
   public PropertyReference(Object receiver) {
      super(receiver);
      this.syntheticJavaProperty = false;
   }

   @SinceKotlin(
      version = "1.4"
   )
   public PropertyReference(Object receiver, Class owner, String name, String signature, int flags) {
      super(receiver, owner, name, signature, (flags & 1) == 1);
      this.syntheticJavaProperty = (flags & 2) == 2;
   }

   @SinceKotlin(
      version = "1.1"
   )
   protected KProperty getReflected() {
      if (this.syntheticJavaProperty) {
         throw new UnsupportedOperationException("Kotlin reflection is not yet supported for synthetic Java properties");
      } else {
         return (KProperty)super.getReflected();
      }
   }

   @Override
   public KCallable compute() {
      return (KCallable)(this.syntheticJavaProperty ? this : super.compute());
   }

   @SinceKotlin(
      version = "1.1"
   )
   @Override
   public boolean isLateinit() {
      return this.getReflected().isLateinit();
   }

   @SinceKotlin(
      version = "1.1"
   )
   @Override
   public boolean isConst() {
      return this.getReflected().isConst();
   }

   @Override
   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      } else if (!(obj instanceof PropertyReference)) {
         return obj instanceof KProperty ? obj.equals(this.compute()) : false;
      } else {
         PropertyReference other = (PropertyReference)obj;
         return this.getOwner().equals(other.getOwner())
            && this.getName().equals(other.getName())
            && this.getSignature().equals(other.getSignature())
            && Intrinsics.areEqual(this.getBoundReceiver(), other.getBoundReceiver());
      }
   }

   @Override
   public int hashCode() {
      return (this.getOwner().hashCode() * 31 + this.getName().hashCode()) * 31 + this.getSignature().hashCode();
   }

   @Override
   public String toString() {
      KCallable reflected = this.compute();
      return reflected != this ? reflected.toString() : "property " + this.getName() + " (Kotlin reflection is not available)";
   }
}
