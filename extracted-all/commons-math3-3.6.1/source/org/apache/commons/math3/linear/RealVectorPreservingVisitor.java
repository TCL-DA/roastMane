package org.apache.commons.math3.linear;

public interface RealVectorPreservingVisitor {
   void start(int var1, int var2, int var3);

   void visit(int var1, double var2);

   double end();
}
