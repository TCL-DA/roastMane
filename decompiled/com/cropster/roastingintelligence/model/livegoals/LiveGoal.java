package com.cropster.roastingintelligence.model.livegoals;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import java.util.Objects;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@JsonDeserialize(
   builder = LiveGoal.LiveGoalBuilder.class
)
@JsonPropertyOrder({"id", "displayMode", "mode", "type"})
public final class LiveGoal {
   public static final String LIVE_GOALS_FILE_BACKUP_EXTENSION = ".bak";
   public static final String LIVE_GOALS_FILE_NAME = "livegoals";
   public static final String LIVE_GOALS_FILE = "livegoals.json";
   private final LiveGoalDisplayMode displayMode;
   private final String id;
   private final LiveGoalMode mode;
   private final LiveGoalType type;

   public static LiveGoal.LiveGoalBuilder builder(LiveGoal goal) {
      Objects.requireNonNull(goal, "Null 'goal' parameter.");
      return new LiveGoal.LiveGoalBuilder(goal.getId(), goal.getType(), goal.getMode()).withDisplayMode(goal.getDisplayMode());
   }

   public static LiveGoal.LiveGoalBuilder curveBasedBuilder(String id) {
      return curveBasedBuilder(id, LiveGoalMode.ABSOLUTE_OR_RELATIVE);
   }

   public static LiveGoal.LiveGoalBuilder curveBasedBuilder(String id, LiveGoalMode mode) {
      return new LiveGoal.LiveGoalBuilder(id, LiveGoalType.CURVE_BASED, mode);
   }

   public static LiveGoal.LiveGoalBuilder eventBasedBuilder(String id) {
      return eventBasedBuilder(id, LiveGoalMode.ABSOLUTE_OR_RELATIVE);
   }

   public static LiveGoal.LiveGoalBuilder eventBasedBuilder(String id, LiveGoalMode mode) {
      return new LiveGoal.LiveGoalBuilder(id, LiveGoalType.EVENT_BASED, mode);
   }

   public static LiveGoal.LiveGoalBuilder valueBasedBuilder(String id) {
      return valueBasedBuilder(id, LiveGoalMode.ABSOLUTE_OR_RELATIVE);
   }

   public static LiveGoal.LiveGoalBuilder valueBasedBuilder(String id, LiveGoalMode mode) {
      return new LiveGoal.LiveGoalBuilder(id, LiveGoalType.VALUE_BASED, mode);
   }

   private LiveGoal(LiveGoal.LiveGoalBuilder builder) {
      this.id = builder.id;
      this.displayMode = builder.displayMode;
      this.mode = builder.mode;
      this.type = builder.type;
   }

   public LiveGoalDisplayMode getDisplayMode() {
      return this.displayMode;
   }

   public String getId() {
      return this.id;
   }

   public LiveGoalMode getMode() {
      return this.mode;
   }

   public LiveGoalType getType() {
      return this.type;
   }

   @JsonIgnore
   public boolean isMeetTheReference() {
      return this.displayMode == LiveGoalDisplayMode.MEET_THE_REFERENCE;
   }

   @JsonIgnore
   public boolean isTarget() {
      return this.displayMode == LiveGoalDisplayMode.TARGET;
   }

   @JsonIgnore
   public boolean isRange() {
      return this.displayMode == LiveGoalDisplayMode.RANGE;
   }

   @JsonIgnore
   public boolean isVisible() {
      return this.displayMode != LiveGoalDisplayMode.HIDDEN;
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
         LiveGoal rhs = (LiveGoal)obj;
         return new EqualsBuilder()
            .append(this.displayMode, rhs.displayMode)
            .append(this.id, rhs.id)
            .append(this.mode, rhs.mode)
            .append(this.type, rhs.type)
            .isEquals();
      }
   }

   @Override
   public int hashCode() {
      return new HashCodeBuilder(17, 23).append(this.displayMode).append(this.id).append(this.mode).append(this.type).toHashCode();
   }

   @JsonPOJOBuilder(
      withPrefix = "with"
   )
   public static final class LiveGoalBuilder {
      private LiveGoalDisplayMode displayMode = LiveGoalDisplayMode.HIDDEN;
      private String id;
      private LiveGoalMode mode;
      private LiveGoalType type;

      @JsonCreator
      private LiveGoalBuilder() {
      }

      private LiveGoalBuilder(String id, LiveGoalType type, LiveGoalMode mode) {
         this.id = id;
         this.mode = mode;
         this.type = type;
      }

      public LiveGoal build() {
         return new LiveGoal(this);
      }

      public LiveGoal.LiveGoalBuilder withDisplayMode(LiveGoalDisplayMode displayMode) {
         this.displayMode = displayMode;
         return this;
      }

      public LiveGoal.LiveGoalBuilder withMeetTheReferenceDisplayMode() {
         this.displayMode = LiveGoalDisplayMode.MEET_THE_REFERENCE;
         return this;
      }

      public LiveGoal.LiveGoalBuilder withRangeDisplayMode() {
         this.displayMode = LiveGoalDisplayMode.RANGE;
         return this;
      }

      public LiveGoal.LiveGoalBuilder withTargetDisplayMode() {
         this.displayMode = LiveGoalDisplayMode.TARGET;
         return this;
      }

      LiveGoal.LiveGoalBuilder withId(String id) {
         this.id = id;
         return this;
      }

      LiveGoal.LiveGoalBuilder withMode(LiveGoalMode mode) {
         this.mode = mode;
         return this;
      }

      LiveGoal.LiveGoalBuilder withType(LiveGoalType type) {
         this.type = type;
         return this;
      }
   }
}
