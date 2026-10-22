package com.sun.javafx.geom;

final class Order0 extends Curve {
   private double x;
   private double y;

   public Order0(double var1, double var3) {
      super(1);
      this.x = var1;
      this.y = var3;
   }

   @Override
   public int getOrder() {
      return 0;
   }

   @Override
   public double getXTop() {
      return this.x;
   }

   @Override
   public double getYTop() {
      return this.y;
   }

   @Override
   public double getXBot() {
      return this.x;
   }

   @Override
   public double getYBot() {
      return this.y;
   }

   @Override
   public double getXMin() {
      return this.x;
   }

   @Override
   public double getXMax() {
      return this.x;
   }

   @Override
   public double getX0() {
      return this.x;
   }

   @Override
   public double getY0() {
      return this.y;
   }

   @Override
   public double getX1() {
      return this.x;
   }

   @Override
   public double getY1() {
      return this.y;
   }

   @Override
   public double XforY(double var1) {
      return var1;
   }

   @Override
   public double TforY(double var1) {
      return 0.0;
   }

   @Override
   public double XforT(double var1) {
      return this.x;
   }

   @Override
   public double YforT(double var1) {
      return this.y;
   }

   @Override
   public double dXforT(double var1, int var3) {
      return 0.0;
   }

   @Override
   public double dYforT(double var1, int var3) {
      return 0.0;
   }

   @Override
   public double nextVertical(double var1, double var3) {
      return var3;
   }

   @Override
   public int crossingsFor(double var1, double var3) {
      return 0;
   }

   @Override
   public boolean accumulateCrossings(Crossings var1) {
      return this.x > var1.getXLo() && this.x < var1.getXHi() && this.y > var1.getYLo() && this.y < var1.getYHi();
   }

   @Override
   public void enlarge(RectBounds var1) {
      var1.add((float)this.x, (float)this.y);
   }

   @Override
   public Curve getSubCurve(double var1, double var3, int var5) {
      return this;
   }

   @Override
   public Curve getReversedCurve() {
      return this;
   }

   @Override
   public int getSegment(float[] var1) {
      var1[0] = (float)this.x;
      var1[1] = (float)this.y;
      return 0;
   }
}
