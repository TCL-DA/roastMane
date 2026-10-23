package com.cropster.roastingintelligence.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import org.cropster.roastingintelligence.model.ControlState;

@JsonAutoDetect(
   fieldVisibility = Visibility.NONE,
   getterVisibility = Visibility.NONE,
   isGetterVisibility = Visibility.NONE,
   setterVisibility = Visibility.NONE
)
@JsonPropertyOrder({"state", "customName", "separator", "offTimerInterval"})
public class ControlStateConfiguration {
   @JsonUnwrapped
   private ControlState state;
   private boolean enabled = false;
   private String customName = null;
   private SeparatorType separator = SeparatorType.NONE;
   private long offTimerInterval = 0L;

   public ControlStateConfiguration() {
   }

   public ControlStateConfiguration(ControlState state) {
      this.state = state;
   }

   @JsonProperty
   public ControlState getState() {
      return this.state;
   }

   public boolean isEnabled() {
      return this.enabled;
   }

   public void setEnabled(boolean enabled) {
      this.enabled = enabled;
   }

   @JsonProperty
   public String getCustomName() {
      return this.customName;
   }

   public void setCustomName(String customName) {
      this.customName = customName;
   }

   @JsonProperty
   public SeparatorType getSeparator() {
      return this.separator;
   }

   public void setSeparator(SeparatorType separator) {
      this.separator = separator;
   }

   @JsonProperty
   public long getOffTimerInterval() {
      return this.offTimerInterval;
   }

   public void setOffTimerInterval(long offTimerInterval) {
      this.offTimerInterval = Math.max(0L, offTimerInterval);
   }
}
