package org.cropster.roastingintelligence.model;

public class Physical {
   private Long _id;
   private Float _moisture;
   private Float _waterActivity;
   private Float _density;

   public Long getId() {
      return this._id;
   }

   public void setId(Long id) {
      this._id = id;
   }

   public Float getMoisture() {
      return this._moisture;
   }

   public void setMoisture(Float moisture) {
      this._moisture = moisture;
   }

   public Float getWaterActivity() {
      return this._waterActivity;
   }

   public void setWaterActivity(Float waterActivity) {
      this._waterActivity = waterActivity;
   }

   public Float getDensity() {
      return this._density;
   }

   public void setDensity(Float density) {
      this._density = density;
   }
}
