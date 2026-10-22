package org.cropster.roastingintelligence.model;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.cropster.roastingintelligence.util.json.ReferenceDeserializer;

@JsonPropertyOrder({"lot", "weight"})
public class LotTransaction {
   private Long _lot;
   private Measure _weight;

   public LotTransaction() {
   }

   public LotTransaction(Long lot, Measure weight) {
      this._lot = lot;
      this._weight = weight;
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

   public Measure getWeight() {
      return this._weight;
   }

   public void setWeight(Measure weight) {
      this._weight = weight;
   }

   @Override
   public int hashCode() {
      int prime = 31;
      int result = 1;
      result = 31 * result + (this._lot == null ? 0 : this._lot.hashCode());
      return 31 * result + (this._weight == null ? 0 : this._weight.hashCode());
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else if (obj == null) {
         return false;
      } else if (this.getClass() != obj.getClass()) {
         return false;
      } else {
         LotTransaction other = (LotTransaction)obj;
         if (this._lot == null) {
            if (other._lot != null) {
               return false;
            }
         } else if (!this._lot.equals(other._lot)) {
            return false;
         }

         if (this._weight == null) {
            if (other._weight != null) {
               return false;
            }
         } else if (!this._weight.equals(other._weight)) {
            return false;
         }

         return true;
      }
   }
}
