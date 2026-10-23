package org.cropster.roastingintelligence.model;

import java.io.Serializable;
import java.util.Date;

public class ProcessingAlert implements Serializable {
   private static final long serialVersionUID = -5442982878250075167L;
   private String _localizedMessage;
   private Date _created;
   private Measure _checkedValue;
   private Measure _deviation;
   private String _localizedReason;

   public String getLocalizedMessage() {
      return this._localizedMessage;
   }

   public void setLocalizedMessage(String localizedMessage) {
      this._localizedMessage = localizedMessage;
   }

   public Date getCreated() {
      return this._created;
   }

   public void setCreated(Date created) {
      this._created = created;
   }

   public Measure getCheckedValue() {
      return this._checkedValue;
   }

   public void setCheckedValue(Measure checkedValue) {
      this._checkedValue = checkedValue;
   }

   public Measure getDeviation() {
      return this._deviation;
   }

   public void setDeviation(Measure deviation) {
      this._deviation = deviation;
   }

   public String getLocalizedReason() {
      return this._localizedReason;
   }

   public void setLocalizedReason(String localizedReason) {
      this._localizedReason = localizedReason;
   }
}
