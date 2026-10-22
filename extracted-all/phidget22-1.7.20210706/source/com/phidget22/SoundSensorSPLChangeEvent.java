package com.phidget22;

import java.util.Arrays;

public class SoundSensorSPLChangeEvent {
   SoundSensor Source;
   double DB;
   double DBA;
   double DBC;
   double[] Octaves;

   public SoundSensorSPLChangeEvent(SoundSensor var1, double var2, double var4, double var6, double[] var8) {
      this.Source = var1;
      this.DB = var2;
      this.DBA = var4;
      this.DBC = var6;
      this.Octaves = var8;
   }

   public SoundSensor getSource() {
      return this.Source;
   }

   public double getDB() {
      return this.DB;
   }

   public double getDBA() {
      return this.DBA;
   }

   public double getDBC() {
      return this.DBC;
   }

   public double[] getOctaves() {
      return this.Octaves;
   }

   @Override
   public String toString() {
      return "[SoundSensorSPLChangeEvent](Source: "
         + this.Source
         + ", DB: "
         + this.DB
         + ", DBA: "
         + this.DBA
         + ", DBC: "
         + this.DBC
         + ", Octaves: "
         + Arrays.toString(this.Octaves)
         + ")";
   }
}
