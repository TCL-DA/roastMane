package org.apache.logging.log4j.message;

import java.util.Objects;
import java.util.ResourceBundle;
import org.jspecify.annotations.Nullable;

public class LocalizedMessageFactory extends AbstractMessageFactory {
   private static final long serialVersionUID = -1996295808703146741L;
   @Nullable
   private final transient ResourceBundle resourceBundle;
   @Nullable
   private final String baseName;

   public LocalizedMessageFactory(final ResourceBundle resourceBundle) {
      this.resourceBundle = resourceBundle;
      this.baseName = null;
   }

   public LocalizedMessageFactory(final String baseName) {
      this.resourceBundle = null;
      this.baseName = baseName;
   }

   public String getBaseName() {
      return this.baseName;
   }

   public ResourceBundle getResourceBundle() {
      return this.resourceBundle;
   }

   @Override
   public Message newMessage(final String key) {
      return this.resourceBundle == null ? new LocalizedMessage(this.baseName, key) : new LocalizedMessage(this.resourceBundle, key);
   }

   @Override
   public Message newMessage(final String key, final Object... params) {
      return this.resourceBundle == null ? new LocalizedMessage(this.baseName, key, params) : new LocalizedMessage(this.resourceBundle, key, params);
   }

   @Override
   public boolean equals(final Object object) {
      if (this == object) {
         return true;
      } else if (object != null && this.getClass() == object.getClass()) {
         LocalizedMessageFactory that = (LocalizedMessageFactory)object;
         return Objects.equals(this.resourceBundle, that.resourceBundle) && Objects.equals(this.baseName, that.baseName);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.resourceBundle, this.baseName);
   }
}
