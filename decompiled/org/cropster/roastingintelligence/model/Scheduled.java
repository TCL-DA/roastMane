package org.cropster.roastingintelligence.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.cropster.roastingintelligence.util.json.ReferenceDeserializer;
import org.cropster.roastingintelligence.util.json.ScheduledDeserializer;

public class Scheduled implements Identifiable, Weighable, Serializable {
   private static final long serialVersionUID = 3999796900022612935L;
   private Long _id;
   private String _timeLabel;
   private Long _machineId;
   private Long _profileId;
   private String _roasterPeron;
   private List<ComponentLot> _componentLots = new ArrayList<>();
   private Map<String, Object> _schedule = Collections.emptyMap();
   private Measure _weight;
   private String _notes;
   private Long _roastedLot;
   private volatile boolean _done;

   @Override
   public Long getId() {
      return this._id;
   }

   public void setId(Long id) {
      this._id = id;
   }

   @JsonProperty("schedule")
   public String getTimeLabel() {
      return this._timeLabel;
   }

   @JsonProperty("schedule")
   @JsonDeserialize(
      using = ScheduledDeserializer.class
   )
   public void setTimeLabel(String timeLabel) {
      this._timeLabel = timeLabel;
   }

   @Override
   public String getName() {
      return this.getTimeLabel();
   }

   @JsonProperty("machine")
   public Long getMachineId() {
      return this._machineId;
   }

   @JsonProperty("machine")
   @JsonDeserialize(
      using = ReferenceDeserializer.class
   )
   public void setMachineId(Long machine) {
      this._machineId = machine;
   }

   @JsonProperty("worker")
   public String getRoastingPerson() {
      return this._roasterPeron;
   }

   @JsonProperty("worker")
   public void setRoasterPerson(String roasterPeron) {
      this._roasterPeron = roasterPeron;
   }

   @JsonProperty
   public List<ComponentLot> getComponentLots() {
      return this._componentLots;
   }

   public void setComponentLots(List<ComponentLot> componentLots) {
      this._componentLots = componentLots;
   }

   @JsonProperty("profile")
   public Long getProfileId() {
      return this._profileId;
   }

   @JsonProperty("profile")
   @JsonDeserialize(
      using = ReferenceDeserializer.class
   )
   public void setProfileId(Long profileId) {
      this._profileId = profileId;
   }

   public Map<String, Object> getSchedule() {
      return this._schedule;
   }

   public void setSchedule(Map<String, Object> schedule) {
      this._schedule = schedule;
   }

   @Override
   public Measure getWeight() {
      return this._weight;
   }

   public void setWeight(Measure weight) {
      this._weight = weight;
   }

   public String getNotes() {
      return this._notes;
   }

   public void setNotes(String notes) {
      this._notes = notes;
   }

   public Long getRoastedLot() {
      return this._roastedLot;
   }

   public void setRoastedLot(Long roastedLot) {
      this._roastedLot = roastedLot;
   }

   public boolean isDone() {
      return this._done;
   }

   public void setDone(boolean done) {
      this._done = done;
   }
}
