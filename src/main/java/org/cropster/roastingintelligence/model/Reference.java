package org.cropster.roastingintelligence.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;

public class Reference {
   private final Date _created;
   private final Long _lotId;

   @JsonCreator
   public Reference(@JsonProperty("created") Date created, @JsonProperty("lot") Long lotId) {
      this._created = created;
      this._lotId = lotId;
   }

   public Date getCreated() {
      return this._created;
   }

   @JsonProperty("lot")
   public Long getLotId() {
      return this._lotId;
   }
}
