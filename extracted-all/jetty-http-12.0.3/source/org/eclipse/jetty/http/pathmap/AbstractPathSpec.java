package org.eclipse.jetty.http.pathmap;

import java.util.Objects;

public abstract class AbstractPathSpec implements PathSpec {
   public int compareTo(PathSpec other) {
      int diff = this.getGroup().ordinal() - other.getGroup().ordinal();
      if (diff != 0) {
         return diff;
      } else {
         diff = other.getSpecLength() - this.getSpecLength();
         if (diff != 0) {
            return diff;
         } else {
            diff = this.getDeclaration().compareTo(other.getDeclaration());
            return diff != 0 ? diff : this.getClass().getName().compareTo(other.getClass().getName());
         }
      }
   }

   @Override
   public final boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else if (obj == null) {
         return false;
      } else {
         return this.getClass() != obj.getClass() ? false : this.compareTo((PathSpec)((AbstractPathSpec)obj)) == 0;
      }
   }

   @Override
   public final int hashCode() {
      return Objects.hash(this.getGroup().ordinal(), this.getSpecLength(), this.getDeclaration(), this.getClass().getName());
   }

   @Override
   public String toString() {
      return String.format("%s@%s{%s,%s}", this.getClass().getSimpleName(), Integer.toHexString(this.hashCode()), this.getGroup(), this.getDeclaration());
   }
}
