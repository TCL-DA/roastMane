package org.cropster.roastingintelligence.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Collections;
import java.util.Date;
import java.util.Set;

public class Cupping {
   private boolean _takenIntoAccount;
   private Date cuppingDate;
   private Float _score;
   private String _notes;
   private Set<CuppingItem> _items = Collections.emptySet();

   public boolean isTakenIntoAccount() {
      return this._takenIntoAccount;
   }

   public void setTakenIntoAccount(boolean takenIntoAccount) {
      this._takenIntoAccount = takenIntoAccount;
   }

   public Date getCuppingDate() {
      return this.cuppingDate;
   }

   public void setCuppingDate(Date cuppingDate) {
      this.cuppingDate = cuppingDate;
   }

   public Float getScore() {
      return this._score;
   }

   public void setScore(Float score) {
      this._score = score;
   }

   public String getNotes() {
      return this._notes;
   }

   public void setNotes(String notes) {
      this._notes = notes;
   }

   @JsonProperty("resultItems")
   public Set<CuppingItem> getItems() {
      return this._items;
   }

   public void setItems(Set<CuppingItem> items) {
      this._items = items;
   }
}
