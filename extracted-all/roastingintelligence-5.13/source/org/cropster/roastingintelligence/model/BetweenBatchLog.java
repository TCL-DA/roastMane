package org.cropster.roastingintelligence.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import org.cropster.roastingintelligence.util.json.CurveDeserializer;
import org.cropster.roastingintelligence.util.json.CurveSerializer;
import org.cropster.roastingintelligence.util.json.ReferenceDeserializer;
import org.cropster.roastingintelligence.util.json.ReferenceSerializer;

public class BetweenBatchLog {
   private Date startDate;
   private Long duration;
   private Long previousBatch;
   private Long nextBatch;
   private Long lowestTemperatureTime;
   private Long highestTemperatureTime;
   private Map<String, Curve> curves = new HashMap<>();
   private Set<RoastingComment> comments = new LinkedHashSet<>();

   public Date getStartDate() {
      return this.startDate;
   }

   public void setStartDate(Date startDate) {
      this.startDate = startDate;
   }

   public Long getDuration() {
      return this.duration;
   }

   public void setDuration(Long duration) {
      this.duration = duration;
   }

   @JsonSerialize(
      using = ReferenceSerializer.class
   )
   public Long getPreviousBatch() {
      return this.previousBatch;
   }

   @JsonDeserialize(
      using = ReferenceDeserializer.class
   )
   public void setPreviousBatch(Long previousBatch) {
      this.previousBatch = previousBatch;
   }

   @JsonSerialize(
      using = ReferenceSerializer.class
   )
   public Long getNextBatch() {
      return this.nextBatch;
   }

   @JsonDeserialize(
      using = ReferenceDeserializer.class
   )
   public void setNextBatch(Long nextBatch) {
      this.nextBatch = nextBatch;
   }

   @JsonInclude(Include.NON_NULL)
   public Long getLowestTemperatureTime() {
      return this.lowestTemperatureTime;
   }

   public void setLowestTemperatureTime(Long lowestTemperatureTime) {
      this.lowestTemperatureTime = lowestTemperatureTime;
   }

   @JsonInclude(Include.NON_NULL)
   public Long getHighestTemperatureTime() {
      return this.highestTemperatureTime;
   }

   public void setHighestTemperatureTime(Long highestTemperatureTime) {
      this.highestTemperatureTime = highestTemperatureTime;
   }

   @JsonSerialize(
      using = CurveSerializer.class
   )
   public Map<String, Curve> getCurves() {
      return this.curves;
   }

   @JsonDeserialize(
      using = CurveDeserializer.class
   )
   public void setCurves(Map<String, Curve> curves) {
      this.curves = curves;
   }

   public Map<String, Curve> getCurves(Collection<Channel> channels) {
      Map<String, Curve> curves = new LinkedHashMap<>();

      for (Channel channel : channels) {
         if (channel.isActive() && this.curves.containsKey(channel.getName())) {
            Curve curve = this.curves.get(channel.getName());
            curves.put(channel.getName(), curve);
         }
      }

      return curves;
   }

   public boolean isEmpty() {
      return this.duration == null || this.duration <= 0L;
   }

   public Set<RoastingComment> getComments() {
      return this.comments;
   }
}
