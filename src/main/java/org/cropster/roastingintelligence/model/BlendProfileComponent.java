package org.cropster.roastingintelligence.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;

public class BlendProfileComponent {
   private final BigDecimal percentage;
   private final ProfileGroup profileGroup;

   public BlendProfileComponent(@JsonProperty("percentage") BigDecimal percentage, @JsonProperty("profileGroup") ProfileGroup profileGroup) {
      this.percentage = percentage;
      this.profileGroup = profileGroup;
   }

   public BigDecimal getPercentage() {
      return this.percentage;
   }

   public ProfileGroup getProfileGroup() {
      return this.profileGroup;
   }
}
