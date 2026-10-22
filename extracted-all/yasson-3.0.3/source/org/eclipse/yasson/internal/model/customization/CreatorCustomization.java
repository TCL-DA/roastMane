package org.eclipse.yasson.internal.model.customization;

import org.eclipse.yasson.internal.JsonbDateFormatter;
import org.eclipse.yasson.internal.JsonbNumberFormatter;
import org.eclipse.yasson.internal.model.PropertyModel;

public class CreatorCustomization extends CustomizationBase {
   private final JsonbNumberFormatter numberFormatter;
   private final JsonbDateFormatter dateFormatter;
   private final boolean required;
   private PropertyModel propertyModel;

   private CreatorCustomization(CreatorCustomization.Builder builder) {
      super(builder);
      this.numberFormatter = builder.numberFormatter;
      this.dateFormatter = builder.dateFormatter;
      this.required = builder.required;
   }

   public static CreatorCustomization.Builder builder() {
      return new CreatorCustomization.Builder();
   }

   @Override
   public JsonbNumberFormatter getSerializeNumberFormatter() {
      throw new UnsupportedOperationException("Serialization is not supported for creator parameters.");
   }

   @Override
   public JsonbNumberFormatter getDeserializeNumberFormatter() {
      if (this.numberFormatter != null) {
         return this.numberFormatter;
      } else {
         return this.propertyModel != null ? this.propertyModel.getCustomization().getDeserializeNumberFormatter() : null;
      }
   }

   @Override
   public JsonbDateFormatter getSerializeDateFormatter() {
      throw new UnsupportedOperationException("Serialization is not supported for creator parameters.");
   }

   @Override
   public JsonbDateFormatter getDeserializeDateFormatter() {
      if (this.dateFormatter != null) {
         return this.dateFormatter;
      } else {
         return this.propertyModel != null ? this.propertyModel.getCustomization().getDeserializeDateFormatter() : null;
      }
   }

   @Override
   public boolean isNillable() {
      throw new UnsupportedOperationException("Not supported for creator parameters.");
   }

   public void setPropertyModel(PropertyModel propertyModel) {
      this.propertyModel = propertyModel;
   }

   public boolean isRequired() {
      return this.required;
   }

   public static final class Builder extends CustomizationBase.Builder<CreatorCustomization.Builder, CreatorCustomization> {
      private JsonbNumberFormatter numberFormatter;
      private JsonbDateFormatter dateFormatter;
      private boolean required = false;

      private Builder() {
      }

      public CreatorCustomization.Builder of(CreatorCustomization customization) {
         super.of(customization);
         this.numberFormatter = customization.numberFormatter;
         this.dateFormatter = customization.dateFormatter;
         return this;
      }

      public CreatorCustomization.Builder numberFormatter(JsonbNumberFormatter numberFormatter) {
         this.numberFormatter = numberFormatter;
         return this;
      }

      public CreatorCustomization.Builder dateFormatter(JsonbDateFormatter dateFormatter) {
         this.dateFormatter = dateFormatter;
         return this;
      }

      public CreatorCustomization.Builder required(boolean required) {
         this.required = required;
         return this;
      }

      public CreatorCustomization build() {
         return new CreatorCustomization(this);
      }
   }
}
