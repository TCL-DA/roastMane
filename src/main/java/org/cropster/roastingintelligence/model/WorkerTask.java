package org.cropster.roastingintelligence.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Date;
import org.cropster.roastingintelligence.util.json.ReferenceDeserializer;

public class WorkerTask {
   private final Long id;
   private Long machineId;
   private Long locationId;
   private User assignedTo;
   private final String name;
   private String detail;
   private String comment;
   private WorkerTask.TaskStatus status = WorkerTask.TaskStatus.TO_DO;
   private WorkerTask.TaskFrequency frequency;
   private final Date dueDate;
   private Integer overdueDays;
   private Measure productionThreshold;
   private Measure dueProductionValue;
   private final boolean missed;
   private Date taskDate;
   private Date completionDate;
   private volatile boolean _synced = true;

   @JsonCreator
   public WorkerTask(
      @JsonProperty("id") Long id, @JsonProperty("name") String name, @JsonProperty("dueDate") Date dueDate, @JsonProperty("missed") boolean missed
   ) {
      this.id = id;
      this.name = name;
      this.dueDate = dueDate;
      this.missed = missed;
   }

   public Long getId() {
      return this.id;
   }

   @JsonProperty("roastMachine")
   public Long getMachineId() {
      return this.machineId;
   }

   @JsonProperty("roastMachine")
   @JsonDeserialize(
      using = ReferenceDeserializer.class
   )
   public void setMachineId(Long machine) {
      this.machineId = machine;
   }

   @JsonProperty("location")
   public Long getLocationId() {
      return this.locationId;
   }

   @JsonProperty("location")
   @JsonDeserialize(
      using = ReferenceDeserializer.class
   )
   public void setLocationId(Long locationId) {
      this.locationId = locationId;
   }

   public User getAssignedTo() {
      return this.assignedTo;
   }

   public void setAssignedTo(User assignedTo) {
      this.assignedTo = assignedTo;
   }

   public String getName() {
      return this.name;
   }

   public String getDetail() {
      return this.detail;
   }

   public void setDetail(String detail) {
      this.detail = detail;
   }

   public String getComment() {
      return this.comment;
   }

   public void setComment(String comment) {
      this.comment = comment;
   }

   public WorkerTask.TaskStatus getStatus() {
      return this.status;
   }

   public void setStatus(WorkerTask.TaskStatus status) {
      this.status = status;
   }

   @JsonIgnore
   public boolean isOpen() {
      return WorkerTask.TaskStatus.TO_DO.equals(this.status) || WorkerTask.TaskStatus.IN_PROGRESS.equals(this.status);
   }

   @JsonIgnore
   public boolean isClosed() {
      return WorkerTask.TaskStatus.DONE.equals(this.status) || WorkerTask.TaskStatus.NOT_DONE.equals(this.status);
   }

   public WorkerTask.TaskFrequency getFrequency() {
      return this.frequency;
   }

   public void setFrequency(WorkerTask.TaskFrequency frequency) {
      this.frequency = frequency;
   }

   public Date getDueDate() {
      return this.dueDate;
   }

   public Integer getOverdueDays() {
      return this.overdueDays;
   }

   public Measure getProductionThreshold() {
      return this.productionThreshold;
   }

   public void setProductionThreshold(Measure productionThreshold) {
      this.productionThreshold = productionThreshold;
   }

   public Measure getDueProductionValue() {
      return this.dueProductionValue;
   }

   public void setDueProductionValue(Measure dueProductionValue) {
      this.dueProductionValue = dueProductionValue;
   }

   public boolean isMissed() {
      return this.missed;
   }

   public Date getTaskDate() {
      return this.taskDate;
   }

   public void setTaskDate(Date taskDate) {
      this.taskDate = taskDate;
   }

   public Date getCompletionDate() {
      return this.completionDate;
   }

   public void setCompletionDate(Date completionDate) {
      this.completionDate = completionDate;
   }

   @JsonIgnore
   public boolean isSynced() {
      return this._synced;
   }

   public void setSynced(boolean synced) {
      this._synced = synced;
   }

   @Override
   public String toString() {
      return String.format("[%s] %s", this.status, this.name);
   }

   public static enum TaskFrequency {
      DAILY,
      WEEKLY,
      WORKING_DAYS,
      MONTHLY,
      HALF_YEARLY,
      YEARLY,
      ONE_TIME,
      ROAST_VOLUME(true),
      ROAST_NUMBER(true),
      ROAST_DURATION(true);

      private boolean productionBased;

      private TaskFrequency() {
      }

      private TaskFrequency(boolean productionBased) {
         this.productionBased = productionBased;
      }

      public boolean isProductionBased() {
         return this.productionBased;
      }
   }

   public static enum TaskStatus {
      TO_DO,
      DONE,
      IN_PROGRESS,
      NOT_DONE;
   }
}
