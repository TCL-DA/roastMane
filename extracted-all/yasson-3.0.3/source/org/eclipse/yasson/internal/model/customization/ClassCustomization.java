package org.eclipse.yasson.internal.model.customization;

import jakarta.json.bind.config.PropertyVisibilityStrategy;
import org.eclipse.yasson.internal.JsonbDateFormatter;
import org.eclipse.yasson.internal.JsonbNumberFormatter;
import org.eclipse.yasson.internal.model.JsonbCreator;

public class ClassCustomization extends CustomizationBase {
   private static final ClassCustomization EMPTY = new ClassCustomization(new ClassCustomization.Builder());
   private final JsonbCreator creator;
   private final String[] propertyOrder;
   private final JsonbNumberFormatter numberFormatter;
   private final JsonbDateFormatter dateTimeFormatter;
   private final PropertyVisibilityStrategy propertyVisibilityStrategy;
   private final TypeInheritanceConfiguration typeInheritanceConfiguration;

   private ClassCustomization(ClassCustomization.Builder builder) {
      super(builder);
      this.creator = builder.creator;
      this.propertyOrder = builder.propertyOrder;
      this.numberFormatter = builder.numberFormatter;
      this.dateTimeFormatter = builder.dateTimeFormatter;
      this.propertyVisibilityStrategy = builder.propertyVisibilityStrategy;
      this.typeInheritanceConfiguration = builder.typeInheritanceConfiguration;
   }

   public static ClassCustomization empty() {
      return EMPTY;
   }

   public static ClassCustomization.Builder builder() {
      return new ClassCustomization.Builder();
   }

   public JsonbCreator getCreator() {
      return this.creator;
   }

   public String[] getPropertyOrder() {
      return this.propertyOrder;
   }

   public PropertyVisibilityStrategy getPropertyVisibilityStrategy() {
      return this.propertyVisibilityStrategy;
   }

   @Override
   public JsonbNumberFormatter getSerializeNumberFormatter() {
      return this.numberFormatter;
   }

   @Override
   public JsonbNumberFormatter getDeserializeNumberFormatter() {
      return this.numberFormatter;
   }

   @Override
   public JsonbDateFormatter getSerializeDateFormatter() {
      return this.dateTimeFormatter;
   }

   @Override
   public JsonbDateFormatter getDeserializeDateFormatter() {
      return this.dateTimeFormatter;
   }

   public TypeInheritanceConfiguration getPolymorphismConfig() {
      return this.typeInheritanceConfiguration;
   }

   public static class Builder extends CustomizationBase.Builder<ClassCustomization.Builder, ClassCustomization> {
      private JsonbCreator creator;
      private String[] propertyOrder;
      private JsonbNumberFormatter numberFormatter;
      private JsonbDateFormatter dateTimeFormatter;
      private PropertyVisibilityStrategy propertyVisibilityStrategy;
      private TypeInheritanceConfiguration typeInheritanceConfiguration;

      private Builder() {
      }

      public ClassCustomization.Builder of(ClassCustomization customization) {
         super.of(customization);
         this.creator(customization.creator);
         this.propertyOrder(customization.propertyOrder);
         this.numberFormatter(customization.numberFormatter);
         this.dateTimeFormatter(customization.dateTimeFormatter);
         this.propertyVisibilityStrategy(customization.propertyVisibilityStrategy);
         return this;
      }

      public ClassCustomization.Builder creator(JsonbCreator creator) {
         this.creator = creator;
         return this;
      }

      public ClassCustomization.Builder propertyOrder(String[] propertyOrder) {
         this.propertyOrder = propertyOrder;
         return this;
      }

      public ClassCustomization.Builder numberFormatter(JsonbNumberFormatter numberFormatter) {
         this.numberFormatter = numberFormatter;
         return this;
      }

      public ClassCustomization.Builder dateTimeFormatter(JsonbDateFormatter dateTimeFormatter) {
         this.dateTimeFormatter = dateTimeFormatter;
         return this;
      }

      public ClassCustomization.Builder propertyVisibilityStrategy(PropertyVisibilityStrategy propertyVisibilityStrategy) {
         this.propertyVisibilityStrategy = propertyVisibilityStrategy;
         return this;
      }

      public ClassCustomization.Builder polymorphismConfig(TypeInheritanceConfiguration typeInheritanceConfiguration) {
         this.typeInheritanceConfiguration = typeInheritanceConfiguration;
         return this;
      }

      public ClassCustomization build() {
         return new ClassCustomization(this);
      }
   }
}
