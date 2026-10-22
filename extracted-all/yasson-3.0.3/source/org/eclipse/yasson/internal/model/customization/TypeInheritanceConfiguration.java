package org.eclipse.yasson.internal.model.customization;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class TypeInheritanceConfiguration {
   private final String fieldName;
   private final boolean inherited;
   private final Map<Class<?>, String> aliases;
   private final Class<?> definedType;
   private final TypeInheritanceConfiguration parentConfig;

   private TypeInheritanceConfiguration(TypeInheritanceConfiguration.Builder builder) {
      this.fieldName = builder.fieldName;
      this.inherited = builder.inherited;
      this.aliases = Map.copyOf(builder.aliases);
      this.parentConfig = builder.parentConfig;
      this.definedType = builder.definedType;
   }

   public static TypeInheritanceConfiguration.Builder builder() {
      return new TypeInheritanceConfiguration.Builder();
   }

   public String getFieldName() {
      return this.fieldName;
   }

   public boolean isInherited() {
      return this.inherited;
   }

   public Map<Class<?>, String> getAliases() {
      return this.aliases;
   }

   public Class<?> getDefinedType() {
      return this.definedType;
   }

   public TypeInheritanceConfiguration getParentConfig() {
      return this.parentConfig;
   }

   public static final class Builder {
      private Map<Class<?>, String> aliases = new HashMap<>();
      private String fieldName = "@type";
      private boolean inherited = false;
      private Class<?> definedType;
      private TypeInheritanceConfiguration parentConfig;

      private Builder() {
      }

      public TypeInheritanceConfiguration.Builder inherited(boolean inherited) {
         this.inherited = inherited;
         return this;
      }

      public TypeInheritanceConfiguration.Builder fieldName(String fieldName) {
         this.fieldName = Objects.requireNonNull(fieldName);
         return this;
      }

      public TypeInheritanceConfiguration.Builder alias(Class<?> clazz, String alias) {
         this.aliases.put(clazz, alias);
         return this;
      }

      public TypeInheritanceConfiguration.Builder parentConfig(TypeInheritanceConfiguration parentConfig) {
         this.parentConfig = parentConfig;
         return this;
      }

      public TypeInheritanceConfiguration.Builder definedType(Class<?> definedType) {
         this.definedType = definedType;
         return this;
      }

      public TypeInheritanceConfiguration.Builder of(TypeInheritanceConfiguration typeInheritanceConfiguration) {
         this.fieldName = typeInheritanceConfiguration.fieldName;
         this.aliases = new HashMap<>(typeInheritanceConfiguration.aliases);
         this.inherited = typeInheritanceConfiguration.inherited;
         this.parentConfig = typeInheritanceConfiguration.parentConfig;
         this.definedType = typeInheritanceConfiguration.definedType;
         return this;
      }

      public TypeInheritanceConfiguration build() {
         return new TypeInheritanceConfiguration(this);
      }
   }
}
