package com.cropster.roastingintelligence;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import org.cropster.roastingintelligence.model.Analysis;
import org.cropster.roastingintelligence.model.BetweenBatchLog;
import org.cropster.roastingintelligence.model.Goal;
import org.cropster.roastingintelligence.model.GroupMember;
import org.cropster.roastingintelligence.model.Lot;
import org.cropster.roastingintelligence.model.LotConsistencyCheck;
import org.cropster.roastingintelligence.model.Machine;
import org.cropster.roastingintelligence.model.Profile;
import org.cropster.roastingintelligence.model.Roast;

@JsonPropertyOrder({"machines", "workers", "profiles", "lots", "references", "betweenBatchLogs", "analyses", "goals", "checks", "groupMembers"})
public class Data implements Cloneable {
   private Map<Long, Machine> _machines = Collections.emptyMap();
   private Set<String> _workers = Collections.emptySet();
   private Map<Long, Profile> _profiles;
   private Map<Long, Lot> _lots = Collections.emptyMap();
   private Map<Long, Roast> _references;
   private Map<Long, BetweenBatchLog> _betweenBatchLogs;
   private Map<Long, Analysis> _analyses;
   private Map<Long, Collection<Goal>> _goals;
   private Map<Long, LotConsistencyCheck> _checks;
   private Map<String, GroupMember> _groupMembers;

   public Data() {
      this._profiles = Collections.emptyMap();
      this._references = Collections.emptyMap();
      this._betweenBatchLogs = Collections.emptyMap();
      this._analyses = Collections.emptyMap();
      this._goals = Collections.emptyMap();
      this._checks = Collections.emptyMap();
      this._groupMembers = Collections.emptyMap();
   }

   public Data(Data data) {
      this();
      this._machines = data._machines;
      this._groupMembers = data._groupMembers;
   }

   @JsonProperty
   public Map<Long, Machine> getMachines() {
      return this._machines;
   }

   public synchronized void setMachines(Map<Long, Machine> machines) {
      this._machines = Collections.unmodifiableMap(new LinkedHashMap<>(machines));
   }

   @JsonProperty
   public Collection<String> getWorkers() {
      return this._workers;
   }

   public synchronized void setWorkers(Collection<String> workers) {
      this._workers = Collections.unmodifiableSet(new LinkedHashSet<>(workers));
   }

   @JsonProperty
   public Map<Long, Profile> getProfiles() {
      return this._profiles;
   }

   public synchronized void setProfiles(Map<Long, Profile> profiles) {
      this._profiles = Collections.unmodifiableMap(new LinkedHashMap<>(profiles));
   }

   @JsonProperty
   public Map<Long, Lot> getLots() {
      return this._lots;
   }

   public synchronized void setLots(Map<Long, Lot> lots) {
      this._lots = Collections.unmodifiableMap(new LinkedHashMap<>(lots));
   }

   @JsonProperty
   public Map<Long, Roast> getReferences() {
      return this._references;
   }

   public synchronized void setReferences(Map<Long, Roast> references) {
      this._references = Collections.unmodifiableMap(new LinkedHashMap<>(references));
   }

   @JsonProperty
   public Map<Long, BetweenBatchLog> getBetweenBatchLogs() {
      return this._betweenBatchLogs;
   }

   public synchronized void setBetweenBatchLogs(Map<Long, BetweenBatchLog> betweenBatchLogs) {
      this._betweenBatchLogs = Collections.unmodifiableMap(new LinkedHashMap<>(betweenBatchLogs));
   }

   @JsonProperty
   public Map<Long, Analysis> getAnalyses() {
      return this._analyses;
   }

   public synchronized void setAnalyses(Map<Long, Analysis> analyses) {
      this._analyses = Collections.unmodifiableMap(new LinkedHashMap<>(analyses));
   }

   @JsonProperty
   public Map<Long, Collection<Goal>> getGoals() {
      return this._goals;
   }

   public synchronized void setGoals(Map<Long, Collection<Goal>> goals) {
      this._goals = Collections.unmodifiableMap(new LinkedHashMap<>(goals));
   }

   @JsonProperty
   public Map<Long, LotConsistencyCheck> getChecks() {
      return this._checks;
   }

   public synchronized void setChecks(Map<Long, LotConsistencyCheck> checks) {
      this._checks = Collections.unmodifiableMap(new LinkedHashMap<>(checks));
   }

   public Data clone() {
      try {
         return (Data)super.clone();
      } catch (CloneNotSupportedException var2) {
         throw new Error(var2);
      }
   }

   public Map<String, GroupMember> getGroupMembers() {
      return this._groupMembers;
   }

   public void setGroupMembers(Map<String, GroupMember> groupMembers) {
      this._groupMembers = Collections.unmodifiableMap(new HashMap<>(groupMembers));
   }
}
