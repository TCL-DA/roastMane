package org.cropster.roastingintelligence.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashSet;

public class BlendComponent implements Serializable {
   private static final long serialVersionUID = -1388265633801768726L;
   private Long _id;
   private String _name;
   private BigDecimal _percentage = BigDecimal.ZERO;
   private Collection<Long> _lotIds = new LinkedHashSet<>();

   public Long getId() {
      return this._id;
   }

   public void setId(Long id) {
      this._id = id;
   }

   public String getName() {
      return this._name;
   }

   public void setName(String name) {
      this._name = name;
   }

   public BigDecimal getPercentage() {
      return this._percentage;
   }

   public void setPercentage(BigDecimal percentage) {
      this._percentage = percentage;
   }

   @JsonProperty("lots")
   public Collection<Long> getLotIds() {
      return this._lotIds;
   }

   public void setLotIds(Collection<Long> lotIds) {
      this._lotIds = lotIds;
   }

   public static class PercentageComparator implements Comparator<BlendComponent> {
      public int compare(BlendComponent o1, BlendComponent o2) {
         return o1._percentage.compareTo(o2._percentage);
      }
   }
}
