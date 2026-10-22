package jakarta.ws.rs.core;

import jakarta.ws.rs.ext.RuntimeDelegate;
import java.util.Objects;

public class EntityTag {
   @Deprecated
   private static final RuntimeDelegate.HeaderDelegate<EntityTag> HEADER_DELEGATE = RuntimeDelegate.getInstance().createHeaderDelegate(EntityTag.class);
   private String value;
   private boolean weak;

   public EntityTag(String value) {
      this(value, false);
   }

   public EntityTag(String value, boolean weak) {
      if (value == null) {
         throw new IllegalArgumentException("value==null");
      } else {
         this.value = value;
         this.weak = weak;
      }
   }

   @Deprecated
   public static EntityTag valueOf(String value) {
      return HEADER_DELEGATE.fromString(value);
   }

   public boolean isWeak() {
      return this.weak;
   }

   public String getValue() {
      return this.value;
   }

   @Override
   public boolean equals(Object obj) {
      if (!(obj instanceof EntityTag)) {
         return false;
      } else {
         EntityTag other = (EntityTag)obj;
         return Objects.equals(this.value, other.getValue()) && this.weak == other.isWeak();
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.value, this.weak);
   }

   @Deprecated
   @Override
   public String toString() {
      return HEADER_DELEGATE.toString(this);
   }
}
