package com.cropster.roastingintelligence.model.alarms;

import com.cropster.roastingintelligence.model.RoastingPhase;
import com.cropster.roastingintelligence.ui.audio.Sound;
import com.cropster.util.model.Unit;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import java.awt.Color;
import java.text.MessageFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.cropster.roastingintelligence.model.Measure;

@JsonDeserialize(
   builder = Alarm.AlarmBuilder.class
)
@JsonPropertyOrder({"audibleFeedback", "enabled", "id", "targetDirection", "targetMeasure", "timeOffset", "timeReference", "trigger", "triggeringPhase", "type", "visualFeedback", "visualFeedbackColor"})
public final class Alarm {
   public static final String ALARMS_FILE_BACKUP_EXTENSION = ".bak";
   public static final String ALARMS_FILE_NAME = "alarms";
   public static final String ALARMS_FILE = "alarms.json";
   public static final String TIME_TRIGGER = "time";
   public static final List<String> EVENTS = List.of("turningPoint", "colorChange", "firstCrack", "secondCrack", "end", "gas");
   public static final List<String> MEASURES = List.of("beanTemperature");
   private static final String NULL_TRIGGER_MESSAGE = "Null, empty or blank 'trigger' parameter.";
   private static final String NULL_PHASE_MESSAGE = "Null 'triggeringPhase' parameter.";
   private final Sound audibleFeedback;
   private final boolean enabled;
   private final String id;
   private final AlarmTargetDirection targetDirection;
   private final Measure targetMeasure;
   private final long timeOffset;
   private final long timeReference;
   private final String trigger;
   private final RoastingPhase triggeringPhase;
   private final AlarmType type;
   private final boolean visualFeedback;
   private final Color visualFeedbackColor;

   public static Alarm.AlarmBuilder builder(Alarm alarm, boolean duplicateId) {
      Objects.requireNonNull(alarm, "Null 'alarm' parameter.");
      Alarm.AlarmBuilder builder = new Alarm.AlarmBuilder(alarm.getType(), alarm.getTriggeringPhase())
         .withAudibleFeedback(alarm.getAudibleFeedback())
         .withVisualFeedbackColor(alarm.getVisualFeedbackColor())
         .withEnabled(alarm.isEnabled())
         .withTargetDirection(alarm.getTargetDirection())
         .withTargetMeasure(alarm.getTargetMeasure())
         .withTimeOffset(alarm.getTimeOffset())
         .withTimeReference(alarm.getTimeReference())
         .withTrigger(alarm.getTrigger())
         .withVisualFeedback(alarm.isVisualFeedback());
      if (duplicateId) {
         builder.withId(alarm.getId());
      }

      return builder;
   }

   public static Alarm.AlarmBuilder builder(Alarm alarm, RoastingPhase triggeringPhase, boolean duplicateId) {
      Objects.requireNonNull(alarm, "Null 'alarm' parameter.");
      Alarm.AlarmBuilder builder = new Alarm.AlarmBuilder(alarm.getType(), triggeringPhase)
         .withAudibleFeedback(alarm.getAudibleFeedback())
         .withVisualFeedbackColor(alarm.getVisualFeedbackColor())
         .withEnabled(alarm.isEnabled())
         .withTargetDirection(alarm.getTargetDirection())
         .withTargetMeasure(alarm.getTargetMeasure())
         .withTimeOffset(alarm.getTimeOffset())
         .withTimeReference(alarm.getTimeReference())
         .withTrigger(alarm.getTrigger())
         .withVisualFeedback(alarm.isVisualFeedback());
      if (duplicateId) {
         builder.withId(alarm.getId());
      }

      return builder;
   }

   public static Alarm.AlarmBuilder eventBasedBuilder(RoastingPhase triggeringPhase, String trigger) {
      Objects.requireNonNull(triggeringPhase, "Null 'triggeringPhase' parameter.");
      Validate.notBlank(trigger, "Null, empty or blank 'trigger' parameter.", new Object[0]);
      return new Alarm.AlarmBuilder(AlarmType.EVENT_BASED, triggeringPhase).withTrigger(trigger);
   }

   public static Alarm.AlarmBuilder measureBasedBuilder(RoastingPhase triggeringPhase, String trigger, Measure targetMeasure) {
      Objects.requireNonNull(triggeringPhase, "Null 'triggeringPhase' parameter.");
      Validate.notBlank(trigger, "Null, empty or blank 'trigger' parameter.", new Object[0]);
      Objects.requireNonNull(targetMeasure, "Null 'targetMeasure' parameter.");
      return new Alarm.AlarmBuilder(AlarmType.MEASURE_BASED, triggeringPhase).withTrigger(trigger).withTargetMeasure(targetMeasure);
   }

   public static Alarm.AlarmBuilder measureBasedBuilder(RoastingPhase triggeringPhase, String trigger, double amount, Unit unit) {
      Objects.requireNonNull(triggeringPhase, "Null 'triggeringPhase' parameter.");
      Validate.notBlank(trigger, "Null, empty or blank 'trigger' parameter.", new Object[0]);
      Objects.requireNonNull(unit, "Null 'unit' parameter.");
      return new Alarm.AlarmBuilder(AlarmType.MEASURE_BASED, triggeringPhase).withTrigger(trigger).withTargetMeasure(amount, unit);
   }

   public static AlarmType typeFromTrigger(String trigger) {
      return !EVENTS.contains(trigger) && !"time".equals(trigger) ? AlarmType.MEASURE_BASED : AlarmType.EVENT_BASED;
   }

   public static boolean isSupported(Alarm alarm) {
      String t = alarm.getTrigger();
      return MEASURES.contains(t) || EVENTS.contains(t) || "time".equals(t);
   }

   public static String getTranslatedTriggerVisualName(String trigger) {
      String var10000;
      if (trigger == null) {
         var10000 = null;
      } else {
         switch (trigger) {
            case "end":
               var10000 = org.cropster.roastingintelligence.util.Messages.m_("End of profile");
               break;
            case "gas":
               var10000 = org.cropster.roastingintelligence.util.Messages.m_("Gas comments");
               break;
            default:
               var10000 = org.cropster.roastingintelligence.util.Messages.m_(trigger);
         }
      }

      return var10000;
   }

   private Alarm(Alarm.AlarmBuilder builder) {
      this.audibleFeedback = builder.audibleFeedback;
      this.visualFeedbackColor = builder.visualFeedbackColor;
      this.enabled = builder.enabled;
      this.id = builder.id;
      this.targetMeasure = builder.targetMeasure;
      this.targetDirection = builder.targetDirection;
      this.timeOffset = builder.timeOffset;
      this.timeReference = builder.timeReference;
      this.trigger = builder.trigger;
      this.triggeringPhase = builder.triggeringPhase;
      this.type = builder.type;
      this.visualFeedback = builder.visualFeedback;
   }

   public Sound getAudibleFeedback() {
      return this.audibleFeedback;
   }

   public boolean isEnabled() {
      return this.enabled;
   }

   public String getId() {
      return this.id;
   }

   public AlarmTargetDirection getTargetDirection() {
      return this.targetDirection;
   }

   public Measure getTargetMeasure() {
      return this.targetMeasure;
   }

   public long getTimeOffset() {
      return this.timeOffset;
   }

   public long getTimeReference() {
      return this.timeReference;
   }

   public String getTrigger() {
      return this.trigger;
   }

   public RoastingPhase getTriggeringPhase() {
      return this.triggeringPhase;
   }

   public AlarmType getType() {
      return this.type;
   }

   public Color getVisualFeedbackColor() {
      return this.visualFeedbackColor;
   }

   public boolean isVisualFeedback() {
      return this.visualFeedback;
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
         Alarm rhs = (Alarm)obj;
         return new EqualsBuilder()
            .append(this.audibleFeedback, rhs.audibleFeedback)
            .append(this.enabled, rhs.enabled)
            .append(this.id, rhs.id)
            .append(this.targetDirection, rhs.targetDirection)
            .append(this.targetMeasure, rhs.targetMeasure)
            .append(this.timeOffset, rhs.timeOffset)
            .append(this.timeReference, rhs.timeReference)
            .append(this.trigger, rhs.trigger)
            .append(this.triggeringPhase, rhs.triggeringPhase)
            .append(this.type, rhs.type)
            .append(this.visualFeedback, rhs.visualFeedback)
            .append(this.visualFeedbackColor, rhs.visualFeedbackColor)
            .isEquals();
      }
   }

   @Override
   public int hashCode() {
      return new HashCodeBuilder(17, 13)
         .append(this.audibleFeedback)
         .append(this.enabled)
         .append(this.id)
         .append(this.targetDirection)
         .append(this.targetMeasure)
         .append(this.timeOffset)
         .append(this.timeReference)
         .append(this.trigger)
         .append(this.triggeringPhase)
         .append(this.type)
         .append(this.visualFeedback)
         .append(this.visualFeedbackColor)
         .toHashCode();
   }

   @Override
   public String toString() {
      return switch (this.type) {
         case EVENT_BASED -> {
            if ("time".equals(this.trigger)) {
               yield org.cropster.roastingintelligence.util.Messages.m_("{0,time,mm:ss} Time", new Date(this.timeReference));
            } else {
               String directionSign = switch (this.targetDirection) {
                  case HIGHER -> "+";
                  case LOWER -> "-";
                  default -> "±";
               };
               yield MessageFormat.format("{0} {1,time,mm:ss} {2}", directionSign, new Date(this.timeOffset), getTranslatedTriggerVisualName(this.trigger));
            }
         }
         case MEASURE_BASED -> MessageFormat.format(
         "{0,number} {1} {2}",
         this.targetMeasure.getAmount(),
         org.cropster.roastingintelligence.util.Messages.m_(this.targetMeasure.getUnit()),
         getTranslatedTriggerVisualName(this.trigger)
      );
         default -> super.toString();
      };
   }

   @JsonPOJOBuilder(
      withPrefix = "with"
   )
   public static final class AlarmBuilder {
      private Sound audibleFeedback = Sound.LEGACY;
      private boolean enabled = true;
      private String id = UUID.randomUUID().toString();
      private AlarmTargetDirection targetDirection = AlarmTargetDirection.ANY;
      private Measure targetMeasure = null;
      private long timeOffset = 0L;
      private long timeReference = 0L;
      private String trigger;
      private RoastingPhase triggeringPhase = RoastingPhase.ROASTING;
      private AlarmType type = AlarmType.EVENT_BASED;
      private boolean visualFeedback = true;
      private Color visualFeedbackColor = Color.ORANGE;

      @JsonCreator
      private AlarmBuilder() {
      }

      private AlarmBuilder(AlarmType type, RoastingPhase triggeringPhase) {
         this.type = type;
         this.triggeringPhase = triggeringPhase;
      }

      public Alarm build() {
         return new Alarm(this);
      }

      public Alarm.AlarmBuilder withAudibleFeedback(Sound audibleFeedback) {
         this.audibleFeedback = audibleFeedback;
         return this;
      }

      public Alarm.AlarmBuilder withoutAudibleFeedback() {
         this.audibleFeedback = null;
         return this;
      }

      public Alarm.AlarmBuilder withEnabled(boolean enabled) {
         this.enabled = enabled;
         return this;
      }

      public Alarm.AlarmBuilder withId(String id) {
         this.id = id;
         return this;
      }

      public Alarm.AlarmBuilder withTargetDirection(AlarmTargetDirection targetDirection) {
         Objects.requireNonNull(targetDirection, "Null 'targetDirection' parameter.");
         this.targetDirection = targetDirection;
         return this;
      }

      public Alarm.AlarmBuilder withTargetMeasure(Measure targetMeasure) {
         this.targetMeasure = targetMeasure;
         return this;
      }

      public Alarm.AlarmBuilder withTargetMeasure(double amount, Unit unit) {
         this.targetMeasure = new Measure(amount, unit);
         return this;
      }

      public Alarm.AlarmBuilder withTimeOffset(long timeOffset) {
         if (timeOffset < 0L) {
            throw new IllegalArgumentException("Non-positive 'timeReference' parameter.");
         } else {
            this.timeOffset = timeOffset;
            return this;
         }
      }

      public Alarm.AlarmBuilder withTimeOffset(long timeOffset, TimeUnit timeUnit) {
         Objects.requireNonNull(timeUnit, "Null 'timeUnit' parameter.");
         return this.withTimeOffset(timeUnit.convert(timeOffset, TimeUnit.MILLISECONDS));
      }

      public Alarm.AlarmBuilder withTimeReference(long timeReference) {
         if (timeReference < 0L) {
            throw new IllegalArgumentException("Negative 'timeReference' parameter: " + timeReference);
         } else {
            this.timeReference = timeReference;
            return this;
         }
      }

      public Alarm.AlarmBuilder withTrigger(String trigger) {
         if (StringUtils.isBlank(trigger)) {
            throw new IllegalArgumentException("'trigger' parameter cannot be null, empty or blank.");
         } else {
            this.trigger = trigger;
            return this;
         }
      }

      public Alarm.AlarmBuilder withVisualFeedback() {
         return this.withVisualFeedback(true);
      }

      public Alarm.AlarmBuilder withVisualFeedback(boolean visualFeedback) {
         this.visualFeedback = visualFeedback;
         return this;
      }

      public Alarm.AlarmBuilder withoutVisualFeedback() {
         return this.withVisualFeedback(false);
      }

      public Alarm.AlarmBuilder withVisualFeedbackColor(Color color) {
         Objects.requireNonNull(color, "Null 'color' parameter.");
         this.visualFeedbackColor = color;
         return this;
      }

      Alarm.AlarmBuilder withTriggeringPhase(RoastingPhase triggeringPhase) {
         this.triggeringPhase = triggeringPhase;
         return this;
      }

      Alarm.AlarmBuilder withType(AlarmType type) {
         this.type = type;
         return this;
      }
   }
}
