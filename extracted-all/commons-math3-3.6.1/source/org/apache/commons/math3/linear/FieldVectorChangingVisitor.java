package org.apache.commons.math3.linear;

import org.apache.commons.math3.FieldElement;

public interface FieldVectorChangingVisitor<T extends FieldElement<?>> {
   void start(int var1, int var2, int var3);

   T visit(int var1, T var2);

   T end();
}
