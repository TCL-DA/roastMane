package org.apache.commons.math3.geometry.euclidean.threed;

import java.io.Serializable;
import java.text.NumberFormat;
import org.apache.commons.math3.RealFieldElement;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MathArithmeticException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.MathArrays;

public class FieldVector3D<T extends RealFieldElement<T>> implements Serializable {
   private static final long serialVersionUID = 20130224L;
   private final T x;
   private final T y;
   private final T z;

   public FieldVector3D(T x, T y, T z) {
      this.x = x;
      this.y = y;
      this.z = z;
   }

   public FieldVector3D(T[] v) throws DimensionMismatchException {
      if (v.length != 3) {
         throw new DimensionMismatchException(v.length, 3);
      } else {
         this.x = v[0];
         this.y = v[1];
         this.z = v[2];
      }
   }

   public FieldVector3D(T alpha, T delta) {
      T cosDelta = delta.cos();
      this.x = alpha.cos().multiply(cosDelta);
      this.y = alpha.sin().multiply(cosDelta);
      this.z = delta.sin();
   }

   public FieldVector3D(T a, FieldVector3D<T> u) {
      this.x = a.multiply(u.x);
      this.y = a.multiply(u.y);
      this.z = a.multiply(u.z);
   }

   public FieldVector3D(T a, Vector3D u) {
      this.x = a.multiply(u.getX());
      this.y = a.multiply(u.getY());
      this.z = a.multiply(u.getZ());
   }

   public FieldVector3D(double a, FieldVector3D<T> u) {
      this.x = u.x.multiply(a);
      this.y = u.y.multiply(a);
      this.z = u.z.multiply(a);
   }

   public FieldVector3D(T a1, FieldVector3D<T> u1, T a2, FieldVector3D<T> u2) {
      this.x = a1.linearCombination(a1, u1.getX(), a2, u2.getX());
      this.y = a1.linearCombination(a1, u1.getY(), a2, u2.getY());
      this.z = a1.linearCombination(a1, u1.getZ(), a2, u2.getZ());
   }

   public FieldVector3D(T a1, Vector3D u1, T a2, Vector3D u2) {
      this.x = a1.linearCombination(u1.getX(), a1, u2.getX(), a2);
      this.y = a1.linearCombination(u1.getY(), a1, u2.getY(), a2);
      this.z = a1.linearCombination(u1.getZ(), a1, u2.getZ(), a2);
   }

   public FieldVector3D(double a1, FieldVector3D<T> u1, double a2, FieldVector3D<T> u2) {
      T prototype = u1.getX();
      this.x = prototype.linearCombination(a1, u1.getX(), a2, u2.getX());
      this.y = prototype.linearCombination(a1, u1.getY(), a2, u2.getY());
      this.z = prototype.linearCombination(a1, u1.getZ(), a2, u2.getZ());
   }

   public FieldVector3D(T a1, FieldVector3D<T> u1, T a2, FieldVector3D<T> u2, T a3, FieldVector3D<T> u3) {
      this.x = a1.linearCombination(a1, u1.getX(), a2, u2.getX(), a3, u3.getX());
      this.y = a1.linearCombination(a1, u1.getY(), a2, u2.getY(), a3, u3.getY());
      this.z = a1.linearCombination(a1, u1.getZ(), a2, u2.getZ(), a3, u3.getZ());
   }

   public FieldVector3D(T a1, Vector3D u1, T a2, Vector3D u2, T a3, Vector3D u3) {
      this.x = a1.linearCombination(u1.getX(), a1, u2.getX(), a2, u3.getX(), a3);
      this.y = a1.linearCombination(u1.getY(), a1, u2.getY(), a2, u3.getY(), a3);
      this.z = a1.linearCombination(u1.getZ(), a1, u2.getZ(), a2, u3.getZ(), a3);
   }

   public FieldVector3D(double a1, FieldVector3D<T> u1, double a2, FieldVector3D<T> u2, double a3, FieldVector3D<T> u3) {
      T prototype = u1.getX();
      this.x = prototype.linearCombination(a1, u1.getX(), a2, u2.getX(), a3, u3.getX());
      this.y = prototype.linearCombination(a1, u1.getY(), a2, u2.getY(), a3, u3.getY());
      this.z = prototype.linearCombination(a1, u1.getZ(), a2, u2.getZ(), a3, u3.getZ());
   }

   public FieldVector3D(T a1, FieldVector3D<T> u1, T a2, FieldVector3D<T> u2, T a3, FieldVector3D<T> u3, T a4, FieldVector3D<T> u4) {
      this.x = a1.linearCombination(a1, u1.getX(), a2, u2.getX(), a3, u3.getX(), a4, u4.getX());
      this.y = a1.linearCombination(a1, u1.getY(), a2, u2.getY(), a3, u3.getY(), a4, u4.getY());
      this.z = a1.linearCombination(a1, u1.getZ(), a2, u2.getZ(), a3, u3.getZ(), a4, u4.getZ());
   }

   public FieldVector3D(T a1, Vector3D u1, T a2, Vector3D u2, T a3, Vector3D u3, T a4, Vector3D u4) {
      this.x = a1.linearCombination(u1.getX(), a1, u2.getX(), a2, u3.getX(), a3, u4.getX(), a4);
      this.y = a1.linearCombination(u1.getY(), a1, u2.getY(), a2, u3.getY(), a3, u4.getY(), a4);
      this.z = a1.linearCombination(u1.getZ(), a1, u2.getZ(), a2, u3.getZ(), a3, u4.getZ(), a4);
   }

   public FieldVector3D(double a1, FieldVector3D<T> u1, double a2, FieldVector3D<T> u2, double a3, FieldVector3D<T> u3, double a4, FieldVector3D<T> u4) {
      T prototype = u1.getX();
      this.x = prototype.linearCombination(a1, u1.getX(), a2, u2.getX(), a3, u3.getX(), a4, u4.getX());
      this.y = prototype.linearCombination(a1, u1.getY(), a2, u2.getY(), a3, u3.getY(), a4, u4.getY());
      this.z = prototype.linearCombination(a1, u1.getZ(), a2, u2.getZ(), a3, u3.getZ(), a4, u4.getZ());
   }

   public T getX() {
      return this.x;
   }

   public T getY() {
      return this.y;
   }

   public T getZ() {
      return this.z;
   }

   public T[] toArray() {
      T[] array = MathArrays.buildArray(this.x.getField(), 3);
      array[0] = this.x;
      array[1] = this.y;
      array[2] = this.z;
      return array;
   }

   public Vector3D toVector3D() {
      return new Vector3D(this.x.getReal(), this.y.getReal(), this.z.getReal());
   }

   public T getNorm1() {
      return this.x.abs().add(this.y.abs()).add(this.z.abs());
   }

   public T getNorm() {
      return this.x.multiply(this.x).add(this.y.multiply(this.y)).add(this.z.multiply(this.z)).sqrt();
   }

   public T getNormSq() {
      return this.x.multiply(this.x).add(this.y.multiply(this.y)).add(this.z.multiply(this.z));
   }

   public T getNormInf() {
      T xAbs = this.x.abs();
      T yAbs = this.y.abs();
      T zAbs = this.z.abs();
      if (xAbs.getReal() <= yAbs.getReal()) {
         return yAbs.getReal() <= zAbs.getReal() ? zAbs : yAbs;
      } else {
         return xAbs.getReal() <= zAbs.getReal() ? zAbs : xAbs;
      }
   }

   public T getAlpha() {
      return this.y.atan2(this.x);
   }

   public T getDelta() {
      return this.z.divide(this.getNorm()).asin();
   }

   public FieldVector3D<T> add(FieldVector3D<T> v) {
      return new FieldVector3D<>(this.x.add(v.x), this.y.add(v.y), this.z.add(v.z));
   }

   public FieldVector3D<T> add(Vector3D v) {
      return new FieldVector3D<>(this.x.add(v.getX()), this.y.add(v.getY()), this.z.add(v.getZ()));
   }

   public FieldVector3D<T> add(T factor, FieldVector3D<T> v) {
      return new FieldVector3D<>(this.x.getField().getOne(), this, factor, v);
   }

   public FieldVector3D<T> add(T factor, Vector3D v) {
      return new FieldVector3D<>(this.x.add(factor.multiply(v.getX())), this.y.add(factor.multiply(v.getY())), this.z.add(factor.multiply(v.getZ())));
   }

   public FieldVector3D<T> add(double factor, FieldVector3D<T> v) {
      return new FieldVector3D<>(1.0, this, factor, v);
   }

   public FieldVector3D<T> add(double factor, Vector3D v) {
      return new FieldVector3D<>(this.x.add(factor * v.getX()), this.y.add(factor * v.getY()), this.z.add(factor * v.getZ()));
   }

   public FieldVector3D<T> subtract(FieldVector3D<T> v) {
      return new FieldVector3D<>(this.x.subtract(v.x), this.y.subtract(v.y), this.z.subtract(v.z));
   }

   public FieldVector3D<T> subtract(Vector3D v) {
      return new FieldVector3D<>(this.x.subtract(v.getX()), this.y.subtract(v.getY()), this.z.subtract(v.getZ()));
   }

   public FieldVector3D<T> subtract(T factor, FieldVector3D<T> v) {
      return new FieldVector3D<>(this.x.getField().getOne(), this, factor.negate(), v);
   }

   public FieldVector3D<T> subtract(T factor, Vector3D v) {
      return new FieldVector3D<>(
         this.x.subtract(factor.multiply(v.getX())), this.y.subtract(factor.multiply(v.getY())), this.z.subtract(factor.multiply(v.getZ()))
      );
   }

   public FieldVector3D<T> subtract(double factor, FieldVector3D<T> v) {
      return new FieldVector3D<>(1.0, this, -factor, v);
   }

   public FieldVector3D<T> subtract(double factor, Vector3D v) {
      return new FieldVector3D<>(this.x.subtract(factor * v.getX()), this.y.subtract(factor * v.getY()), this.z.subtract(factor * v.getZ()));
   }

   public FieldVector3D<T> normalize() throws MathArithmeticException {
      T s = this.getNorm();
      if (s.getReal() == 0.0) {
         throw new MathArithmeticException(LocalizedFormats.CANNOT_NORMALIZE_A_ZERO_NORM_VECTOR);
      } else {
         return this.scalarMultiply(s.reciprocal());
      }
   }

   public FieldVector3D<T> orthogonal() throws MathArithmeticException {
      double threshold = 0.6 * this.getNorm().getReal();
      if (threshold == 0.0) {
         throw new MathArithmeticException(LocalizedFormats.ZERO_NORM);
      } else if (FastMath.abs(this.x.getReal()) <= threshold) {
         T inverse = this.y.multiply(this.y).add(this.z.multiply(this.z)).sqrt().reciprocal();
         return new FieldVector3D<>(inverse.getField().getZero(), inverse.multiply(this.z), inverse.multiply(this.y).negate());
      } else if (FastMath.abs(this.y.getReal()) <= threshold) {
         T inverse = this.x.multiply(this.x).add(this.z.multiply(this.z)).sqrt().reciprocal();
         return new FieldVector3D<>(inverse.multiply(this.z).negate(), inverse.getField().getZero(), inverse.multiply(this.x));
      } else {
         T inverse = this.x.multiply(this.x).add(this.y.multiply(this.y)).sqrt().reciprocal();
         return new FieldVector3D<>(inverse.multiply(this.y), inverse.multiply(this.x).negate(), inverse.getField().getZero());
      }
   }

   public static <T extends RealFieldElement<T>> T angle(FieldVector3D<T> v1, FieldVector3D<T> v2) throws MathArithmeticException {
      T normProduct = (T)v1.getNorm().multiply(v2.getNorm());
      if (normProduct.getReal() == 0.0) {
         throw new MathArithmeticException(LocalizedFormats.ZERO_NORM);
      } else {
         T dot = dotProduct(v1, v2);
         double threshold = normProduct.getReal() * 0.9999;
         if (!(dot.getReal() < -threshold) && !(dot.getReal() > threshold)) {
            return dot.divide(normProduct).acos();
         } else {
            FieldVector3D<T> v3 = crossProduct(v1, v2);
            return dot.getReal() >= 0.0 ? v3.getNorm().divide(normProduct).asin() : v3.getNorm().divide(normProduct).asin().subtract(Math.PI).negate();
         }
      }
   }

   public static <T extends RealFieldElement<T>> T angle(FieldVector3D<T> v1, Vector3D v2) throws MathArithmeticException {
      T normProduct = (T)v1.getNorm().multiply(v2.getNorm());
      if (normProduct.getReal() == 0.0) {
         throw new MathArithmeticException(LocalizedFormats.ZERO_NORM);
      } else {
         T dot = dotProduct(v1, v2);
         double threshold = normProduct.getReal() * 0.9999;
         if (!(dot.getReal() < -threshold) && !(dot.getReal() > threshold)) {
            return dot.divide(normProduct).acos();
         } else {
            FieldVector3D<T> v3 = crossProduct(v1, v2);
            return dot.getReal() >= 0.0 ? v3.getNorm().divide(normProduct).asin() : v3.getNorm().divide(normProduct).asin().subtract(Math.PI).negate();
         }
      }
   }

   public static <T extends RealFieldElement<T>> T angle(Vector3D v1, FieldVector3D<T> v2) throws MathArithmeticException {
      return angle(v2, v1);
   }

   public FieldVector3D<T> negate() {
      return new FieldVector3D<>(this.x.negate(), this.y.negate(), this.z.negate());
   }

   public FieldVector3D<T> scalarMultiply(T a) {
      return new FieldVector3D<>(this.x.multiply(a), this.y.multiply(a), this.z.multiply(a));
   }

   public FieldVector3D<T> scalarMultiply(double a) {
      return new FieldVector3D<>(this.x.multiply(a), this.y.multiply(a), this.z.multiply(a));
   }

   public boolean isNaN() {
      return Double.isNaN(this.x.getReal()) || Double.isNaN(this.y.getReal()) || Double.isNaN(this.z.getReal());
   }

   public boolean isInfinite() {
      return !this.isNaN() && (Double.isInfinite(this.x.getReal()) || Double.isInfinite(this.y.getReal()) || Double.isInfinite(this.z.getReal()));
   }

   @Override
   public boolean equals(Object other) {
      if (this == other) {
         return true;
      } else if (other instanceof FieldVector3D) {
         FieldVector3D<T> rhs = (FieldVector3D<T>)other;
         return rhs.isNaN() ? this.isNaN() : this.x.equals(rhs.x) && this.y.equals(rhs.y) && this.z.equals(rhs.z);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return this.isNaN() ? 409 : 311 * (107 * this.x.hashCode() + 83 * this.y.hashCode() + this.z.hashCode());
   }

   public T dotProduct(FieldVector3D<T> v) {
      return this.x.linearCombination(this.x, v.x, this.y, v.y, this.z, v.z);
   }

   public T dotProduct(Vector3D v) {
      return this.x.linearCombination(v.getX(), this.x, v.getY(), this.y, v.getZ(), this.z);
   }

   public FieldVector3D<T> crossProduct(FieldVector3D<T> v) {
      return new FieldVector3D<>(
         this.x.linearCombination(this.y, v.z, this.z.negate(), v.y),
         this.y.linearCombination(this.z, v.x, this.x.negate(), v.z),
         this.z.linearCombination(this.x, v.y, this.y.negate(), v.x)
      );
   }

   public FieldVector3D<T> crossProduct(Vector3D v) {
      return new FieldVector3D<>(
         this.x.linearCombination(v.getZ(), this.y, -v.getY(), this.z),
         this.y.linearCombination(v.getX(), this.z, -v.getZ(), this.x),
         this.z.linearCombination(v.getY(), this.x, -v.getX(), this.y)
      );
   }

   public T distance1(FieldVector3D<T> v) {
      T dx = v.x.subtract(this.x).abs();
      T dy = v.y.subtract(this.y).abs();
      T dz = v.z.subtract(this.z).abs();
      return dx.add(dy).add(dz);
   }

   public T distance1(Vector3D v) {
      T dx = this.x.subtract(v.getX()).abs();
      T dy = this.y.subtract(v.getY()).abs();
      T dz = this.z.subtract(v.getZ()).abs();
      return dx.add(dy).add(dz);
   }

   public T distance(FieldVector3D<T> v) {
      T dx = v.x.subtract(this.x);
      T dy = v.y.subtract(this.y);
      T dz = v.z.subtract(this.z);
      return dx.multiply(dx).add(dy.multiply(dy)).add(dz.multiply(dz)).sqrt();
   }

   public T distance(Vector3D v) {
      T dx = this.x.subtract(v.getX());
      T dy = this.y.subtract(v.getY());
      T dz = this.z.subtract(v.getZ());
      return dx.multiply(dx).add(dy.multiply(dy)).add(dz.multiply(dz)).sqrt();
   }

   public T distanceInf(FieldVector3D<T> v) {
      T dx = v.x.subtract(this.x).abs();
      T dy = v.y.subtract(this.y).abs();
      T dz = v.z.subtract(this.z).abs();
      if (dx.getReal() <= dy.getReal()) {
         return dy.getReal() <= dz.getReal() ? dz : dy;
      } else {
         return dx.getReal() <= dz.getReal() ? dz : dx;
      }
   }

   public T distanceInf(Vector3D v) {
      T dx = this.x.subtract(v.getX()).abs();
      T dy = this.y.subtract(v.getY()).abs();
      T dz = this.z.subtract(v.getZ()).abs();
      if (dx.getReal() <= dy.getReal()) {
         return dy.getReal() <= dz.getReal() ? dz : dy;
      } else {
         return dx.getReal() <= dz.getReal() ? dz : dx;
      }
   }

   public T distanceSq(FieldVector3D<T> v) {
      T dx = v.x.subtract(this.x);
      T dy = v.y.subtract(this.y);
      T dz = v.z.subtract(this.z);
      return dx.multiply(dx).add(dy.multiply(dy)).add(dz.multiply(dz));
   }

   public T distanceSq(Vector3D v) {
      T dx = this.x.subtract(v.getX());
      T dy = this.y.subtract(v.getY());
      T dz = this.z.subtract(v.getZ());
      return dx.multiply(dx).add(dy.multiply(dy)).add(dz.multiply(dz));
   }

   public static <T extends RealFieldElement<T>> T dotProduct(FieldVector3D<T> v1, FieldVector3D<T> v2) {
      return v1.dotProduct(v2);
   }

   public static <T extends RealFieldElement<T>> T dotProduct(FieldVector3D<T> v1, Vector3D v2) {
      return v1.dotProduct(v2);
   }

   public static <T extends RealFieldElement<T>> T dotProduct(Vector3D v1, FieldVector3D<T> v2) {
      return v2.dotProduct(v1);
   }

   public static <T extends RealFieldElement<T>> FieldVector3D<T> crossProduct(FieldVector3D<T> v1, FieldVector3D<T> v2) {
      return v1.crossProduct(v2);
   }

   public static <T extends RealFieldElement<T>> FieldVector3D<T> crossProduct(FieldVector3D<T> v1, Vector3D v2) {
      return v1.crossProduct(v2);
   }

   public static <T extends RealFieldElement<T>> FieldVector3D<T> crossProduct(Vector3D v1, FieldVector3D<T> v2) {
      return new FieldVector3D<>(
         v2.x.linearCombination(v1.getY(), v2.z, -v1.getZ(), v2.y),
         v2.y.linearCombination(v1.getZ(), v2.x, -v1.getX(), v2.z),
         v2.z.linearCombination(v1.getX(), v2.y, -v1.getY(), v2.x)
      );
   }

   public static <T extends RealFieldElement<T>> T distance1(FieldVector3D<T> v1, FieldVector3D<T> v2) {
      return v1.distance1(v2);
   }

   public static <T extends RealFieldElement<T>> T distance1(FieldVector3D<T> v1, Vector3D v2) {
      return v1.distance1(v2);
   }

   public static <T extends RealFieldElement<T>> T distance1(Vector3D v1, FieldVector3D<T> v2) {
      return v2.distance1(v1);
   }

   public static <T extends RealFieldElement<T>> T distance(FieldVector3D<T> v1, FieldVector3D<T> v2) {
      return v1.distance(v2);
   }

   public static <T extends RealFieldElement<T>> T distance(FieldVector3D<T> v1, Vector3D v2) {
      return v1.distance(v2);
   }

   public static <T extends RealFieldElement<T>> T distance(Vector3D v1, FieldVector3D<T> v2) {
      return v2.distance(v1);
   }

   public static <T extends RealFieldElement<T>> T distanceInf(FieldVector3D<T> v1, FieldVector3D<T> v2) {
      return v1.distanceInf(v2);
   }

   public static <T extends RealFieldElement<T>> T distanceInf(FieldVector3D<T> v1, Vector3D v2) {
      return v1.distanceInf(v2);
   }

   public static <T extends RealFieldElement<T>> T distanceInf(Vector3D v1, FieldVector3D<T> v2) {
      return v2.distanceInf(v1);
   }

   public static <T extends RealFieldElement<T>> T distanceSq(FieldVector3D<T> v1, FieldVector3D<T> v2) {
      return v1.distanceSq(v2);
   }

   public static <T extends RealFieldElement<T>> T distanceSq(FieldVector3D<T> v1, Vector3D v2) {
      return v1.distanceSq(v2);
   }

   public static <T extends RealFieldElement<T>> T distanceSq(Vector3D v1, FieldVector3D<T> v2) {
      return v2.distanceSq(v1);
   }

   @Override
   public String toString() {
      return Vector3DFormat.getInstance().format(this.toVector3D());
   }

   public String toString(NumberFormat format) {
      return new Vector3DFormat(format).format(this.toVector3D());
   }
}
