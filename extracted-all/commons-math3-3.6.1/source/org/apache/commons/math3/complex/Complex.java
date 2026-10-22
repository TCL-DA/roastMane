package org.apache.commons.math3.complex;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.math3.FieldElement;
import org.apache.commons.math3.exception.NotPositiveException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.MathUtils;
import org.apache.commons.math3.util.Precision;

public class Complex implements FieldElement<Complex>, Serializable {
   public static final Complex I = new Complex(0.0, 1.0);
   public static final Complex NaN = new Complex(Double.NaN, Double.NaN);
   public static final Complex INF = new Complex(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
   public static final Complex ONE = new Complex(1.0, 0.0);
   public static final Complex ZERO = new Complex(0.0, 0.0);
   private static final long serialVersionUID = -6195664516687396620L;
   private final double imaginary;
   private final double real;
   private final transient boolean isNaN;
   private final transient boolean isInfinite;

   public Complex(double real) {
      this(real, 0.0);
   }

   public Complex(double real, double imaginary) {
      this.real = real;
      this.imaginary = imaginary;
      this.isNaN = Double.isNaN(real) || Double.isNaN(imaginary);
      this.isInfinite = !this.isNaN && (Double.isInfinite(real) || Double.isInfinite(imaginary));
   }

   public double abs() {
      if (this.isNaN) {
         return Double.NaN;
      } else if (this.isInfinite()) {
         return Double.POSITIVE_INFINITY;
      } else if (FastMath.abs(this.real) < FastMath.abs(this.imaginary)) {
         if (this.imaginary == 0.0) {
            return FastMath.abs(this.real);
         } else {
            double q = this.real / this.imaginary;
            return FastMath.abs(this.imaginary) * FastMath.sqrt(1.0 + q * q);
         }
      } else if (this.real == 0.0) {
         return FastMath.abs(this.imaginary);
      } else {
         double q = this.imaginary / this.real;
         return FastMath.abs(this.real) * FastMath.sqrt(1.0 + q * q);
      }
   }

   public Complex add(Complex addend) throws NullArgumentException {
      MathUtils.checkNotNull(addend);
      return !this.isNaN && !addend.isNaN ? this.createComplex(this.real + addend.getReal(), this.imaginary + addend.getImaginary()) : NaN;
   }

   public Complex add(double addend) {
      return !this.isNaN && !Double.isNaN(addend) ? this.createComplex(this.real + addend, this.imaginary) : NaN;
   }

   public Complex conjugate() {
      return this.isNaN ? NaN : this.createComplex(this.real, -this.imaginary);
   }

   public Complex divide(Complex divisor) throws NullArgumentException {
      MathUtils.checkNotNull(divisor);
      if (!this.isNaN && !divisor.isNaN) {
         double c = divisor.getReal();
         double d = divisor.getImaginary();
         if (c == 0.0 && d == 0.0) {
            return NaN;
         } else if (divisor.isInfinite() && !this.isInfinite()) {
            return ZERO;
         } else if (FastMath.abs(c) < FastMath.abs(d)) {
            double q = c / d;
            double denominator = c * q + d;
            return this.createComplex((this.real * q + this.imaginary) / denominator, (this.imaginary * q - this.real) / denominator);
         } else {
            double q = d / c;
            double denominator = d * q + c;
            return this.createComplex((this.imaginary * q + this.real) / denominator, (this.imaginary - this.real * q) / denominator);
         }
      } else {
         return NaN;
      }
   }

   public Complex divide(double divisor) {
      if (this.isNaN || Double.isNaN(divisor)) {
         return NaN;
      } else if (divisor == 0.0) {
         return NaN;
      } else if (Double.isInfinite(divisor)) {
         return !this.isInfinite() ? ZERO : NaN;
      } else {
         return this.createComplex(this.real / divisor, this.imaginary / divisor);
      }
   }

   public Complex reciprocal() {
      if (this.isNaN) {
         return NaN;
      } else if (this.real == 0.0 && this.imaginary == 0.0) {
         return INF;
      } else if (this.isInfinite) {
         return ZERO;
      } else if (FastMath.abs(this.real) < FastMath.abs(this.imaginary)) {
         double q = this.real / this.imaginary;
         double scale = 1.0 / (this.real * q + this.imaginary);
         return this.createComplex(scale * q, -scale);
      } else {
         double q = this.imaginary / this.real;
         double scale = 1.0 / (this.imaginary * q + this.real);
         return this.createComplex(scale, -scale * q);
      }
   }

   @Override
   public boolean equals(Object other) {
      if (this == other) {
         return true;
      } else if (other instanceof Complex) {
         Complex c = (Complex)other;
         return c.isNaN ? this.isNaN : MathUtils.equals(this.real, c.real) && MathUtils.equals(this.imaginary, c.imaginary);
      } else {
         return false;
      }
   }

   public static boolean equals(Complex x, Complex y, int maxUlps) {
      return Precision.equals(x.real, y.real, maxUlps) && Precision.equals(x.imaginary, y.imaginary, maxUlps);
   }

   public static boolean equals(Complex x, Complex y) {
      return equals(x, y, 1);
   }

   public static boolean equals(Complex x, Complex y, double eps) {
      return Precision.equals(x.real, y.real, eps) && Precision.equals(x.imaginary, y.imaginary, eps);
   }

   public static boolean equalsWithRelativeTolerance(Complex x, Complex y, double eps) {
      return Precision.equalsWithRelativeTolerance(x.real, y.real, eps) && Precision.equalsWithRelativeTolerance(x.imaginary, y.imaginary, eps);
   }

   @Override
   public int hashCode() {
      return this.isNaN ? 7 : 37 * (17 * MathUtils.hash(this.imaginary) + MathUtils.hash(this.real));
   }

   public double getImaginary() {
      return this.imaginary;
   }

   public double getReal() {
      return this.real;
   }

   public boolean isNaN() {
      return this.isNaN;
   }

   public boolean isInfinite() {
      return this.isInfinite;
   }

   public Complex multiply(Complex factor) throws NullArgumentException {
      MathUtils.checkNotNull(factor);
      if (this.isNaN || factor.isNaN) {
         return NaN;
      } else {
         return !Double.isInfinite(this.real) && !Double.isInfinite(this.imaginary) && !Double.isInfinite(factor.real) && !Double.isInfinite(factor.imaginary)
            ? this.createComplex(this.real * factor.real - this.imaginary * factor.imaginary, this.real * factor.imaginary + this.imaginary * factor.real)
            : INF;
      }
   }

   public Complex multiply(int factor) {
      if (this.isNaN) {
         return NaN;
      } else {
         return !Double.isInfinite(this.real) && !Double.isInfinite(this.imaginary)
            ? this.createComplex(this.real * (double)factor, this.imaginary * (double)factor)
            : INF;
      }
   }

   public Complex multiply(double factor) {
      if (this.isNaN || Double.isNaN(factor)) {
         return NaN;
      } else {
         return !Double.isInfinite(this.real) && !Double.isInfinite(this.imaginary) && !Double.isInfinite(factor)
            ? this.createComplex(this.real * factor, this.imaginary * factor)
            : INF;
      }
   }

   public Complex negate() {
      return this.isNaN ? NaN : this.createComplex(-this.real, -this.imaginary);
   }

   public Complex subtract(Complex subtrahend) throws NullArgumentException {
      MathUtils.checkNotNull(subtrahend);
      return !this.isNaN && !subtrahend.isNaN ? this.createComplex(this.real - subtrahend.getReal(), this.imaginary - subtrahend.getImaginary()) : NaN;
   }

   public Complex subtract(double subtrahend) {
      return !this.isNaN && !Double.isNaN(subtrahend) ? this.createComplex(this.real - subtrahend, this.imaginary) : NaN;
   }

   public Complex acos() {
      return this.isNaN ? NaN : this.add(this.sqrt1z().multiply(I)).log().multiply(I.negate());
   }

   public Complex asin() {
      return this.isNaN ? NaN : this.sqrt1z().add(this.multiply(I)).log().multiply(I.negate());
   }

   public Complex atan() {
      return this.isNaN ? NaN : this.add(I).divide(I.subtract(this)).log().multiply(I.divide(this.createComplex(2.0, 0.0)));
   }

   public Complex cos() {
      return this.isNaN
         ? NaN
         : this.createComplex(FastMath.cos(this.real) * FastMath.cosh(this.imaginary), -FastMath.sin(this.real) * FastMath.sinh(this.imaginary));
   }

   public Complex cosh() {
      return this.isNaN
         ? NaN
         : this.createComplex(FastMath.cosh(this.real) * FastMath.cos(this.imaginary), FastMath.sinh(this.real) * FastMath.sin(this.imaginary));
   }

   public Complex exp() {
      if (this.isNaN) {
         return NaN;
      } else {
         double expReal = FastMath.exp(this.real);
         return this.createComplex(expReal * FastMath.cos(this.imaginary), expReal * FastMath.sin(this.imaginary));
      }
   }

   public Complex log() {
      return this.isNaN ? NaN : this.createComplex(FastMath.log(this.abs()), FastMath.atan2(this.imaginary, this.real));
   }

   public Complex pow(Complex x) throws NullArgumentException {
      MathUtils.checkNotNull(x);
      return this.log().multiply(x).exp();
   }

   public Complex pow(double x) {
      return this.log().multiply(x).exp();
   }

   public Complex sin() {
      return this.isNaN
         ? NaN
         : this.createComplex(FastMath.sin(this.real) * FastMath.cosh(this.imaginary), FastMath.cos(this.real) * FastMath.sinh(this.imaginary));
   }

   public Complex sinh() {
      return this.isNaN
         ? NaN
         : this.createComplex(FastMath.sinh(this.real) * FastMath.cos(this.imaginary), FastMath.cosh(this.real) * FastMath.sin(this.imaginary));
   }

   public Complex sqrt() {
      if (this.isNaN) {
         return NaN;
      } else if (this.real == 0.0 && this.imaginary == 0.0) {
         return this.createComplex(0.0, 0.0);
      } else {
         double t = FastMath.sqrt((FastMath.abs(this.real) + this.abs()) / 2.0);
         return this.real >= 0.0
            ? this.createComplex(t, this.imaginary / (2.0 * t))
            : this.createComplex(FastMath.abs(this.imaginary) / (2.0 * t), FastMath.copySign(1.0, this.imaginary) * t);
      }
   }

   public Complex sqrt1z() {
      return this.createComplex(1.0, 0.0).subtract(this.multiply(this)).sqrt();
   }

   public Complex tan() {
      if (this.isNaN || Double.isInfinite(this.real)) {
         return NaN;
      } else if (this.imaginary > 20.0) {
         return this.createComplex(0.0, 1.0);
      } else if (this.imaginary < -20.0) {
         return this.createComplex(0.0, -1.0);
      } else {
         double real2 = 2.0 * this.real;
         double imaginary2 = 2.0 * this.imaginary;
         double d = FastMath.cos(real2) + FastMath.cosh(imaginary2);
         return this.createComplex(FastMath.sin(real2) / d, FastMath.sinh(imaginary2) / d);
      }
   }

   public Complex tanh() {
      if (this.isNaN || Double.isInfinite(this.imaginary)) {
         return NaN;
      } else if (this.real > 20.0) {
         return this.createComplex(1.0, 0.0);
      } else if (this.real < -20.0) {
         return this.createComplex(-1.0, 0.0);
      } else {
         double real2 = 2.0 * this.real;
         double imaginary2 = 2.0 * this.imaginary;
         double d = FastMath.cosh(real2) + FastMath.cos(imaginary2);
         return this.createComplex(FastMath.sinh(real2) / d, FastMath.sin(imaginary2) / d);
      }
   }

   public double getArgument() {
      return FastMath.atan2(this.getImaginary(), this.getReal());
   }

   public List<Complex> nthRoot(int n) throws NotPositiveException {
      if (n <= 0) {
         throw new NotPositiveException(LocalizedFormats.CANNOT_COMPUTE_NTH_ROOT_FOR_NEGATIVE_N, n);
      } else {
         List<Complex> result = new ArrayList<>();
         if (this.isNaN) {
            result.add(NaN);
            return result;
         } else if (this.isInfinite()) {
            result.add(INF);
            return result;
         } else {
            double nthRootOfAbs = FastMath.pow(this.abs(), 1.0 / (double)n);
            double nthPhi = this.getArgument() / (double)n;
            double slice = (Math.PI * 2) / (double)n;
            double innerPart = nthPhi;

            for (int k = 0; k < n; k++) {
               double realPart = nthRootOfAbs * FastMath.cos(innerPart);
               double imaginaryPart = nthRootOfAbs * FastMath.sin(innerPart);
               result.add(this.createComplex(realPart, imaginaryPart));
               innerPart += slice;
            }

            return result;
         }
      }
   }

   protected Complex createComplex(double realPart, double imaginaryPart) {
      return new Complex(realPart, imaginaryPart);
   }

   public static Complex valueOf(double realPart, double imaginaryPart) {
      return !Double.isNaN(realPart) && !Double.isNaN(imaginaryPart) ? new Complex(realPart, imaginaryPart) : NaN;
   }

   public static Complex valueOf(double realPart) {
      return Double.isNaN(realPart) ? NaN : new Complex(realPart);
   }

   protected final Object readResolve() {
      return this.createComplex(this.real, this.imaginary);
   }

   public ComplexField getField() {
      return ComplexField.getInstance();
   }

   @Override
   public String toString() {
      return "(" + this.real + ", " + this.imaginary + ")";
   }
}
