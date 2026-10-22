package org.cropster.roastingintelligence.model;

import java.util.Set;

public class Goal {
   private String _name;
   private String _localizedMessage;
   private boolean _global;
   private boolean _absolute;
   private Long _startTime;
   private Long _endTime;
   private Measure _lowerBound;
   private Measure _upperBound;
   private Set<Long> _profiles;
   private Long _lastModified;

   public String getName() {
      return this._name;
   }

   public void setName(String name) {
      this._name = name;
   }

   public String getLocalizedMessage() {
      return this._localizedMessage;
   }

   public void setLocalizedMessage(String localizedMessage) {
      this._localizedMessage = localizedMessage;
   }

   public boolean isGlobal() {
      return this._global;
   }

   public void setGlobal(boolean global) {
      this._global = global;
   }

   public boolean isAbsolute() {
      return this._absolute;
   }

   public void setAbsolute(boolean absolute) {
      this._absolute = absolute;
   }

   public Long getStartTime() {
      return this._startTime;
   }

   public void setStartTime(Long startTime) {
      this._startTime = startTime;
   }

   public Long getEndTime() {
      return this._endTime;
   }

   public void setEndTime(Long endTime) {
      this._endTime = endTime;
   }

   public Measure getLowerBound() {
      return this._lowerBound;
   }

   public void setLowerBound(Measure lowerBound) {
      this._lowerBound = lowerBound;
   }

   public Measure getUpperBound() {
      return this._upperBound;
   }

   public void setUpperBound(Measure upperBound) {
      this._upperBound = upperBound;
   }

   public Set<Long> getProfiles() {
      return this._profiles;
   }

   public void setProfiles(Set<Long> profiles) {
      this._profiles = profiles;
   }

   public Long getLastModified() {
      return this._lastModified;
   }

   public void setLastModified(Long lastModified) {
      this._lastModified = lastModified;
   }

   public boolean hasBoundsDefined() {
      return this._lowerBound != null || this._upperBound != null;
   }

   public boolean isSatisfiable() {
      return this._lowerBound != null && this._upperBound != null ? this._lowerBound.getAmount() < this._upperBound.getAmount() : true;
   }
}
