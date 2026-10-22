package org.eclipse.yasson.internal.serializer;

import java.lang.reflect.Type;
import java.util.Objects;
import org.eclipse.yasson.internal.model.customization.ClassCustomization;
import org.eclipse.yasson.internal.model.customization.Customization;

class SerializerBuilderParams {
   private final Type type;
   private final Customization customization;
   private final boolean root;
   private final boolean key;
   private final boolean resolveRootAdapter;
   private final ModelSerializer objectBaseSerializer;

   private SerializerBuilderParams(SerializerBuilderParams.Builder builder) {
      this.type = builder.type;
      this.customization = builder.customization;
      this.root = builder.root;
      this.key = builder.key;
      this.resolveRootAdapter = builder.resolveRootAdapter;
      this.objectBaseSerializer = builder.objectBaseSerializer;
   }

   public static SerializerBuilderParams.Builder builder(Type type) {
      return new SerializerBuilderParams.Builder(type);
   }

   public Type getType() {
      return this.type;
   }

   public Customization getCustomization() {
      return this.customization;
   }

   public boolean isRoot() {
      return this.root;
   }

   public boolean isKey() {
      return this.key;
   }

   public boolean isResolveRootAdapter() {
      return this.resolveRootAdapter;
   }

   public ModelSerializer getObjectBaseSerializer() {
      return this.objectBaseSerializer;
   }

   static final class Builder {
      private Type type;
      private Customization customization;
      private boolean root;
      private boolean key;
      private boolean resolveRootAdapter;
      private ModelSerializer objectBaseSerializer;

      private Builder(Type type) {
         this.type = Objects.requireNonNull(type);
         this.customization = ClassCustomization.empty();
         this.root = true;
         this.key = false;
      }

      public SerializerBuilderParams.Builder type(Type type) {
         this.type = Objects.requireNonNull(type);
         return this;
      }

      public SerializerBuilderParams.Builder customization(Customization customization) {
         this.customization = Objects.requireNonNull(customization);
         return this;
      }

      public SerializerBuilderParams.Builder root(boolean root) {
         this.root = root;
         return this;
      }

      public SerializerBuilderParams.Builder key(boolean key) {
         this.key = key;
         return this;
      }

      public SerializerBuilderParams.Builder resolveRootAdapter(boolean resolveRootAdapter) {
         this.resolveRootAdapter = resolveRootAdapter;
         return this;
      }

      public SerializerBuilderParams.Builder objectBaseSerializer(ModelSerializer objectBaseSerializer) {
         this.objectBaseSerializer = objectBaseSerializer;
         return this;
      }

      public SerializerBuilderParams build() {
         return new SerializerBuilderParams(this);
      }
   }
}
