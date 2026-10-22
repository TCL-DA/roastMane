package org.apache.commons.math3;

import org.apache.commons.math3.exception.MathArithmeticException;
import org.apache.commons.math3.exception.NullArgumentException;

public interface FieldElement<T> {
   T add(T var1) throws NullArgumentException;

   T subtract(T var1) throws NullArgumentException;

   T negate();

   T multiply(int var1);

   T multiply(T var1) throws NullArgumentException;

   T divide(T var1) throws NullArgumentException, MathArithmeticException;

   T reciprocal() throws MathArithmeticException;

   Field<T> getField();
}
