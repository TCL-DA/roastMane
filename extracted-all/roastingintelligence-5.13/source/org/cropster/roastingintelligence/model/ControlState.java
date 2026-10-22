package org.cropster.roastingintelligence.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Objects;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.cropster.roastingintelligence.measurement.MeasurementSourceFactory;
import org.cropster.roastingintelligence.util.json.LenientClassDeserialzer;

@JsonAutoDetect(
   fieldVisibility = Visibility.NONE,
   getterVisibility = Visibility.NONE,
   isGetterVisibility = Visibility.NONE,
   setterVisibility = Visibility.NONE
)
@JsonPropertyOrder({"name", "sourceFactory"})
public class ControlState {
   private final String name;
   private final Class<? extends MeasurementSourceFactory> sourceFactory;

   public ControlState(
      @JsonProperty("name") String name,
      @JsonProperty("sourceFactory") @JsonDeserialize(using = LenientClassDeserialzer.class) Class<? extends MeasurementSourceFactory> sourceFactory
   ) {
      Validate.notBlank(name, "Null, empty or blank `name` parameter", new Object[0]);
      Objects.requireNonNull(sourceFactory, "Null `sourceFactory` parameter");
      this.name = name;
      this.sourceFactory = sourceFactory;
   }

   @JsonProperty
   public String getName() {
      return this.name;
   }

   @JsonProperty
   public Class<? extends MeasurementSourceFactory> getSourceFactory() {
      return this.sourceFactory;
   }

   @Override
   public boolean equals(Object obj) {
      if (obj == null) {
         return false;
      } else if (obj == this) {
         return true;
      } else if (obj.getClass() != this.getClass()) {
         return false;
      } else {
         ControlState cs = (ControlState)obj;
         return new EqualsBuilder().append(this.name, cs.name).append(this.sourceFactory, cs.sourceFactory).isEquals();
      }
   }

   @Override
   public int hashCode() {
      return new HashCodeBuilder(17, 37).append(this.name).append(this.sourceFactory).toHashCode();
   }

   @Override
   public String toString() {
      return new ToStringBuilder(this).append("name", this.name).append("sourceFactory", this.sourceFactory.getSimpleName()).toString();
   }
}
