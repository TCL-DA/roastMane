package org.cropster.roastingintelligence.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.cropster.roastingintelligence.util.json.ReferenceDeserializer;

public class LotConsistencyCheck implements Serializable {
   private static final long serialVersionUID = -4667781463965107950L;
   private Date _created;
   private int _checkCount;
   private List<ProcessingAlert> _alerts = new ArrayList<>();
   private Long _lot;

   public Date getCreated() {
      return this._created;
   }

   public void setCreated(Date created) {
      this._created = created;
   }

   public int getCheckCount() {
      return this._checkCount;
   }

   public void setCheckCount(int checkCount) {
      this._checkCount = checkCount;
   }

   public List<ProcessingAlert> getAlerts() {
      return this._alerts;
   }

   public void setAlerts(List<ProcessingAlert> alerts) {
      this._alerts = alerts;
   }

   @JsonDeserialize(
      using = ReferenceDeserializer.class
   )
   public Long getLot() {
      return this._lot;
   }

   public void setLot(Long lot) {
      this._lot = lot;
   }
}
