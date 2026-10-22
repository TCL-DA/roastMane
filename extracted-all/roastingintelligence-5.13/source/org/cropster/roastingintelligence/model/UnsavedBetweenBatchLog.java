package org.cropster.roastingintelligence.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;
import org.cropster.roastingintelligence.AppModel;
import org.cropster.roastingintelligence.BetweenBatchModel;
import org.cropster.roastingintelligence.RoastingModel;

@JsonFilter("unsavedBetweenBatchLog")
public class UnsavedBetweenBatchLog extends BetweenBatchLog {
   private final Date previousBatchDate;
   private Date nextBatchDate;
   private BetweenBatchModel betweenBatchModel;

   @JsonCreator
   public UnsavedBetweenBatchLog(@JsonProperty("previousBatchDate") Date previousBatchDate, @JsonProperty("nextBatchDate") Date nextBatchDate) {
      this.previousBatchDate = previousBatchDate;
      this.nextBatchDate = nextBatchDate;
   }

   public UnsavedBetweenBatchLog(Date previousBatchDate) {
      this.previousBatchDate = previousBatchDate;
   }

   public Date getPreviousBatchDate() {
      return this.previousBatchDate;
   }

   public Date getNextBatchDate() {
      return this.nextBatchDate;
   }

   public void setNextBatchDate(Date nextBatchDate) {
      this.nextBatchDate = nextBatchDate;
      this.setDuration(Long.valueOf(nextBatchDate.getTime() - this.getStartDate().getTime()));
   }

   @JsonIgnore
   public BetweenBatchModel getBetweenBatchModel() {
      return this.betweenBatchModel;
   }

   public void setBetweenBatchModel(BetweenBatchModel betweenBatchModel) {
      this.betweenBatchModel = betweenBatchModel;
   }

   public void normalizeCurves(AppModel appModel) {
      for (Channel channel : this.betweenBatchModel.getChannels()) {
         CurveModel rootModel = this.betweenBatchModel.getCurveModel(channel).getRootModel();
         long startTime = this.betweenBatchModel.getStartDate().getTime() - 1000L;
         long endTime = this.nextBatchDate.getTime();
         long firstKey = this.betweenBatchModel.getStartDate().getTime();
         long lastKey = this.nextBatchDate.getTime() - 1000L;
         Curve curve = RoastingModel.buildCurve(rootModel, channel, appModel.getPreferences(), startTime, endTime, firstKey, lastKey);
         Curve normalizedCurve = new Curve(curve.getUnit());
         curve.entrySet()
            .stream()
            .filter(e -> e.getKey() >= firstKey && e.getKey() < this.nextBatchDate.getTime())
            .forEach(e -> normalizedCurve.put(Long.valueOf(e.getKey() - firstKey), e.getValue()));
         this.getCurves().put(channel.getName(), normalizedCurve);
      }
   }
}
