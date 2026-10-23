package org.cropster.roastingintelligence.model;

import com.cropster.util.model.Unit;
import com.cropster.util.text.DurationFormat;
import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;
import org.cropster.roastingintelligence.util.CollectionUtil;
import org.cropster.roastingintelligence.util.MeasureFormat;
import org.cropster.roastingintelligence.util.NanoTime;
import org.cropster.roastingintelligence.util.json.CurveDeserializer;
import org.cropster.roastingintelligence.util.json.CurveSerializer;
import org.cropster.roastingintelligence.util.json.MapAsNameValuesDeserializer;
import org.cropster.roastingintelligence.util.json.MapAsNameValuesSerializer;
import org.cropster.roastingintelligence.util.json.ReferenceDeserializer;

@JsonFilter("roast")
@JsonPropertyOrder({"lot", "startDate", "endDate", "startWeight", "endWeight", "profile", "machine", "worker", "schedule", "notes", "transactions", "measures", "comments", "curves"})
public class Roast {
   private static final String[] MEASURES = new String[]{"developmentTime", "developmentTimeRatio", "roastValue", "endTemperature"};
   private static final MeasureFormat MEASURE_FORMAT = MeasureFormat.getInstance();
   private static final DurationFormat DURATION_FORMAT = DurationFormat.getDurationInstance(true);
   private Lot _lot = new Lot();
   private Measure _startWeight;
   private Measure _endWeight;
   private String _worker;
   private Long _machine;
   private Long _profile;
   private Date _startDate;
   private Date _endDate;
   private Long _duration;
   private String _notes;
   private final Map<String, Measure> _measures;
   private Map<String, Curve> _curves = new LinkedHashMap<>();
   private final TreeMap<Long, RoastingComment> _comments = new TreeMap<>();
   private Long _scheduledId;
   private Date _markedAsReferenceDate;
   private volatile boolean _synced;

   public Roast() {
      this._measures = new LinkedHashMap<>();
   }

   public Lot getLot() {
      return this._lot;
   }

   public void setLot(Lot lot) {
      this._lot = lot;
   }

   public Measure getStartWeight() {
      return this._startWeight;
   }

   public void setStartWeight(Measure startWeight) {
      this._startWeight = startWeight;
   }

   @JsonIgnore
   public double getWeightChange() {
      return this._startWeight != null && this._endWeight != null
         ? (this._endWeight.getAmount() - this._startWeight.getAmount()) / this._startWeight.getAmount()
         : 0.0;
   }

   public String getWorker() {
      return this._worker;
   }

   public void setWorker(String worker) {
      this._worker = worker;
   }

   public Long getProfile() {
      return this._profile;
   }

   @JsonDeserialize(
      using = ReferenceDeserializer.class
   )
   public void setProfile(Long profile) {
      this._profile = profile;
   }

   public Long getMachine() {
      return this._machine;
   }

   @JsonDeserialize(
      using = ReferenceDeserializer.class
   )
   public void setMachine(Long machine) {
      this._machine = machine;
   }

   public Date getStartDate() {
      return this._startDate;
   }

   public void setStartDate(Date startDate) {
      this._startDate = startDate;
   }

   @JsonIgnore
   public long getElapsedTime() {
      return NanoTime.currentTimeMillis() - this._startDate.getTime();
   }

   public long getElapsedTimeAt(Date date) {
      return date.getTime() - this._startDate.getTime();
   }

   public Date getEndDate() {
      return this._endDate;
   }

   public void setEndDate(Date endDate) {
      this._endDate = endDate;
   }

   @JsonIgnore
   public Long getDuration() {
      if (this._duration != null) {
         return this._duration;
      } else {
         return this._endDate == null ? NanoTime.currentTimeMillis() - this._startDate.getTime() : this._endDate.getTime() - this._startDate.getTime();
      }
   }

   public void setDuration(Long duration) {
      this._duration = duration;
   }

   public Measure getEndWeight() {
      return this._endWeight;
   }

   public void setEndWeight(Measure endWeight) {
      this._endWeight = endWeight;
   }

   public String getNotes() {
      return this._notes;
   }

   public void setNotes(String notes) {
      this._notes = notes;
   }

   @JsonInclude(Include.NON_EMPTY)
   @JsonSerialize(
      using = MapAsNameValuesSerializer.class
   )
   @JsonDeserialize(
      using = MapAsNameValuesDeserializer.class
   )
   public Map<String, Measure> getMeasures() {
      return this._measures;
   }

   @JsonSerialize(
      using = CurveSerializer.class
   )
   public Map<String, Curve> getCurves() {
      return this._curves;
   }

   public Map<String, Curve> getCurves(Collection<Channel> channels) {
      Map<String, Curve> curves = new LinkedHashMap<>();

      for (Channel channel : channels) {
         if (channel.isActive() && this._curves.containsKey(channel.getName())) {
            Curve curve = this._curves.get(channel.getName());
            curves.put(channel.getName(), curve);
         }
      }

      return curves;
   }

   @JsonDeserialize(
      using = CurveDeserializer.class
   )
   public void setCurves(Map<String, Curve> curves) {
      this._curves = curves;
   }

   public void addCurveValue(String curveName, Long time, Double value, Unit unit) {
      if (curveName == null) {
         throw new IllegalArgumentException("curveName must not be null");
      } else if (time == null) {
         throw new IllegalArgumentException("time must not be null");
      } else {
         Curve curve = this._curves.get(curveName);
         if (curve == null) {
            curve = new Curve(unit);
            this._curves.put(curveName, curve);
         }

         if (!unit.equals(curve.getUnit())) {
            throw new IllegalArgumentException("Units do not match!");
         } else {
            curve.put(time, value);
         }
      }
   }

   public Measure getTempAtTime(long time) {
      Curve beanCurve = this._curves.get("beanTemperature");
      if (beanCurve == null) {
         return null;
      } else {
         Long key = CollectionUtil.getProximateKey(beanCurve, time, 500L);
         return key != null ? new Measure(beanCurve.get(key), beanCurve.getUnit()) : null;
      }
   }

   public Collection<RoastingComment> getComments() {
      return this._comments.values();
   }

   public void setComments(Collection<RoastingComment> comments) {
      this._comments.clear();

      for (RoastingComment comment : comments) {
         this.addComment(comment);
      }
   }

   public RoastingComment getComment(Long time) {
      return this._comments.get(time);
   }

   public RoastingComment addComment(RoastingComment comment) {
      return this._comments.put(comment.getTime(), comment);
   }

   @JsonIgnore
   public Collection<RoastingComment> getCommentsWithControl() {
      if (!this._curves.containsKey("gasControl")) {
         return this._comments.values();
      } else {
         MeasureFormat measureFormat = MeasureFormat.getInstance();
         measureFormat.getNumberFormat().setMinimumFractionDigits(0);
         measureFormat.getNumberFormat().setMaximumFractionDigits(1);
         Double lastValue = null;
         List<RoastingComment> comments = new ArrayList<>(this._comments.values());
         Curve gasControlCurve = this._curves.get("gasControl");

         for (Entry<Long, Double> entry : gasControlCurve.entrySet()) {
            if (lastValue == null || Math.abs(entry.getValue() - lastValue) > 0.0) {
               String gas = measureFormat.format(new Measure(entry.getValue(), gasControlCurve.getUnit()));
               comments.add(new RoastingComment(entry.getKey(), "gasControl", gas));
               lastValue = entry.getValue();
            }
         }

         Collections.sort(comments, (c1, c2) -> (int)(c1.getTime() - c2.getTime()));
         return comments;
      }
   }

   @JsonIgnore
   public Map<String, Long> getEventTimes() {
      HashMap<String, Long> eventTimes = new HashMap<>();

      for (RoastingComment comment : this._comments.values()) {
         if ("firstCrack".equals(comment.getEvent()) || "colorChange".equals(comment.getEvent()) || "turningPoint".equals(comment.getEvent())) {
            eventTimes.put(comment.getEvent(), comment.getTime());
         }
      }

      return eventTimes;
   }

   @JsonIgnore
   public Map<String, Long> getModulationPhases() {
      Map<String, Long> eventTimes = this.getEventTimes();
      Long colorChangeTime = eventTimes.get("colorChange");
      Long firstCrackTime = eventTimes.get("firstCrack");
      Map<String, Long> modulationPhases = new HashMap<>();
      if (firstCrackTime != null) {
         modulationPhases.put("development", this.getDuration() - firstCrackTime);
      }

      if (colorChangeTime != null) {
         modulationPhases.put("drying", colorChangeTime);
         if (firstCrackTime != null) {
            modulationPhases.put("maillard", firstCrackTime - colorChangeTime);
         }
      }

      return modulationPhases;
   }

   public Long getScheduledId() {
      return this._scheduledId;
   }

   public void setScheduledId(Long scheduledId) {
      this._scheduledId = scheduledId;
   }

   public Date getMarkedAsReferenceDate() {
      return this._markedAsReferenceDate;
   }

   public void setMarkedAsReferenceDate(Date markedAsReferenceDate) {
      this._markedAsReferenceDate = markedAsReferenceDate;
   }

   @JsonIgnore
   public boolean isSynced() {
      return this._synced;
   }

   public void setSynced(boolean synced) {
      this._synced = synced;
   }

   public String[] toSummary() {
      List<String> summary = new ArrayList<>();
      summary.add(String.format("%s: %s", org.cropster.roastingintelligence.util.Messages.m_("Duration"), DURATION_FORMAT.format(this.getDuration())));

      for (String element : MEASURES) {
         if (this.getMeasures().containsKey(element)) {
            Measure measure = this.getMeasures().get(element);
            if (measure != null) {
               String formatted = null;
               if (Unit.MILLISECOND.isCompatible(measure.getUnit())) {
                  formatted = DURATION_FORMAT.format(measure.toUnit(Unit.MILLISECOND).getAmount());
               } else {
                  formatted = MEASURE_FORMAT.format(measure);
               }

               summary.add(String.format("%s: %s", org.cropster.roastingintelligence.util.Messages.m_(element), formatted));
            }
         }
      }

      return summary.toArray(new String[summary.size()]);
   }

   public static class DateComparator implements Comparator<Roast> {
      public int compare(Roast o1, Roast o2) {
         return o1.getStartDate().compareTo(o2.getStartDate());
      }
   }
}
