package org.cropster.roastingintelligence.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Collections;
import java.util.Set;

public class CuppingItem {
   private CuppingSheetItem _cuppingSheetItem;
   private Set<Descriptor> _descriptors = Collections.emptySet();

   @JsonProperty("cuppingSkinItem")
   public CuppingSheetItem getCuppingSheetItem() {
      return this._cuppingSheetItem;
   }

   public void setCuppingSheetItem(CuppingSheetItem cuppingSheetItem) {
      this._cuppingSheetItem = cuppingSheetItem;
   }

   @JsonProperty("assignedSensorialDescriptors")
   public Set<Descriptor> getDescriptors() {
      return this._descriptors;
   }

   public void setDescriptors(Set<Descriptor> descriptors) {
      this._descriptors = descriptors;
   }
}
