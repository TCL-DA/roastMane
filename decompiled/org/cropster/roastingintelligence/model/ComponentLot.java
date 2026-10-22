package org.cropster.roastingintelligence.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.io.Serializable;
import org.cropster.roastingintelligence.util.json.ReferenceDeserializer;

public class ComponentLot implements Serializable {
   private static final long serialVersionUID = -1667782486892223469L;
   private Long _lotId;
   private Long _componentId;

   @JsonProperty("lot")
   public Long getLotId() {
      return this._lotId;
   }

   @JsonProperty("lot")
   @JsonDeserialize(
      using = ReferenceDeserializer.class
   )
   public void setLotId(Long lotId) {
      this._lotId = lotId;
   }

   @JsonProperty("component")
   public Long getComponentId() {
      return this._componentId;
   }

   @JsonProperty("component")
   @JsonDeserialize(
      using = ReferenceDeserializer.class
   )
   public void setComponentId(Long componentId) {
      this._componentId = componentId;
   }
}
