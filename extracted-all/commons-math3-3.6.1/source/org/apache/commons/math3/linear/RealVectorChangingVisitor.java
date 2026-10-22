package org.apache.commons.math3.linear;

public interface RealVectorChangingVisitor {
   void start(int var1, int var2, int var3);

   double visit(int var1, double var2);

   double end();
}
