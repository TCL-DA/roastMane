package org.apache.commons.math3.geometry.euclidean.threed;

import java.io.Serializable;
import org.apache.commons.math3.Field;
import org.apache.commons.math3.RealFieldElement;
import org.apache.commons.math3.exception.MathArithmeticException;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.MathArrays;

public class FieldRotation<T extends RealFieldElement<T>> implements Serializable {
   private static final long serialVersionUID = 20130224L;
   private final T q0;
   private final T q1;
   private final T q2;
   private final T q3;

   public FieldRotation(T q0, T q1, T q2, T q3, boolean needsNormalization) {
      if (needsNormalization) {
         T inv = q0.multiply(q0).add(q1.multiply(q1)).add(q2.multiply(q2)).add(q3.multiply(q3)).sqrt().reciprocal();
         this.q0 = inv.multiply(q0);
         this.q1 = inv.multiply(q1);
         this.q2 = inv.multiply(q2);
         this.q3 = inv.multiply(q3);
      } else {
         this.q0 = q0;
         this.q1 = q1;
         this.q2 = q2;
         this.q3 = q3;
      }
   }

   @Deprecated
   public FieldRotation(FieldVector3D<T> axis, T angle) throws MathIllegalArgumentException {
      this(axis, angle, RotationConvention.VECTOR_OPERATOR);
   }

   public FieldRotation(FieldVector3D<T> axis, T angle, RotationConvention convention) throws MathIllegalArgumentException {
      T norm = axis.getNorm();
      if (norm.getReal() == 0.0) {
         throw new MathIllegalArgumentException(LocalizedFormats.ZERO_NORM_FOR_ROTATION_AXIS);
      } else {
         T halfAngle = angle.multiply(convention == RotationConvention.VECTOR_OPERATOR ? -0.5 : 0.5);
         T coeff = halfAngle.sin().divide(norm);
         this.q0 = halfAngle.cos();
         this.q1 = coeff.multiply(axis.getX());
         this.q2 = coeff.multiply(axis.getY());
         this.q3 = coeff.multiply(axis.getZ());
      }
   }

   public FieldRotation(T[][] m, double threshold) throws NotARotationMatrixException {
      if (m.length == 3 && m[0].length == 3 && m[1].length == 3 && m[2].length == 3) {
         T[][] ort = this.orthogonalizeMatrix(m, threshold);
         T d0 = ort[1][1].multiply(ort[2][2]).subtract(ort[2][1].multiply(ort[1][2]));
         T d1 = ort[0][1].multiply(ort[2][2]).subtract(ort[2][1].multiply(ort[0][2]));
         T d2 = ort[0][1].multiply(ort[1][2]).subtract(ort[1][1].multiply(ort[0][2]));
         T det = ort[0][0].multiply(d0).subtract(ort[1][0].multiply(d1)).add(ort[2][0].multiply(d2));
         if (det.getReal() < 0.0) {
            throw new NotARotationMatrixException(LocalizedFormats.CLOSEST_ORTHOGONAL_MATRIX_HAS_NEGATIVE_DETERMINANT, det);
         } else {
            T[] quat = this.mat2quat(ort);
            this.q0 = quat[0];
            this.q1 = quat[1];
            this.q2 = quat[2];
            this.q3 = quat[3];
         }
      } else {
         throw new NotARotationMatrixException(LocalizedFormats.ROTATION_MATRIX_DIMENSIONS, m.length, m[0].length);
      }
   }

   public FieldRotation(FieldVector3D<T> u1, FieldVector3D<T> u2, FieldVector3D<T> v1, FieldVector3D<T> v2) throws MathArithmeticException {
      FieldVector3D<T> u3 = FieldVector3D.crossProduct(u1, u2).normalize();
      u2 = FieldVector3D.crossProduct(u3, u1).normalize();
      u1 = u1.normalize();
      FieldVector3D<T> v3 = FieldVector3D.crossProduct(v1, v2).normalize();
      v2 = FieldVector3D.crossProduct(v3, v1).normalize();
      v1 = v1.normalize();
      T[][] array = MathArrays.buildArray(u1.getX().getField(), 3, 3);
      array[0][0] = u1.getX().multiply(v1.getX()).add(u2.getX().multiply(v2.getX())).add(u3.getX().multiply(v3.getX()));
      array[0][1] = u1.getY().multiply(v1.getX()).add(u2.getY().multiply(v2.getX())).add(u3.getY().multiply(v3.getX()));
      array[0][2] = u1.getZ().multiply(v1.getX()).add(u2.getZ().multiply(v2.getX())).add(u3.getZ().multiply(v3.getX()));
      array[1][0] = u1.getX().multiply(v1.getY()).add(u2.getX().multiply(v2.getY())).add(u3.getX().multiply(v3.getY()));
      array[1][1] = u1.getY().multiply(v1.getY()).add(u2.getY().multiply(v2.getY())).add(u3.getY().multiply(v3.getY()));
      array[1][2] = u1.getZ().multiply(v1.getY()).add(u2.getZ().multiply(v2.getY())).add(u3.getZ().multiply(v3.getY()));
      array[2][0] = u1.getX().multiply(v1.getZ()).add(u2.getX().multiply(v2.getZ())).add(u3.getX().multiply(v3.getZ()));
      array[2][1] = u1.getY().multiply(v1.getZ()).add(u2.getY().multiply(v2.getZ())).add(u3.getY().multiply(v3.getZ()));
      array[2][2] = u1.getZ().multiply(v1.getZ()).add(u2.getZ().multiply(v2.getZ())).add(u3.getZ().multiply(v3.getZ()));
      T[] quat = this.mat2quat(array);
      this.q0 = quat[0];
      this.q1 = quat[1];
      this.q2 = quat[2];
      this.q3 = quat[3];
   }

   public FieldRotation(FieldVector3D<T> u, FieldVector3D<T> v) throws MathArithmeticException {
      T normProduct = u.getNorm().multiply(v.getNorm());
      if (normProduct.getReal() == 0.0) {
         throw new MathArithmeticException(LocalizedFormats.ZERO_NORM_FOR_ROTATION_DEFINING_VECTOR);
      } else {
         T dot = FieldVector3D.dotProduct(u, v);
         if (dot.getReal() < -0.999999999999998 * normProduct.getReal()) {
            FieldVector3D<T> w = u.orthogonal();
            this.q0 = normProduct.getField().getZero();
            this.q1 = w.getX().negate();
            this.q2 = w.getY().negate();
            this.q3 = w.getZ().negate();
         } else {
            this.q0 = dot.divide(normProduct).add(1.0).multiply(0.5).sqrt();
            T coeff = this.q0.multiply(normProduct).multiply(2.0).reciprocal();
            FieldVector3D<T> q = FieldVector3D.crossProduct(v, u);
            this.q1 = coeff.multiply(q.getX());
            this.q2 = coeff.multiply(q.getY());
            this.q3 = coeff.multiply(q.getZ());
         }
      }
   }

   @Deprecated
   public FieldRotation(RotationOrder order, T alpha1, T alpha2, T alpha3) {
      this(order, RotationConvention.VECTOR_OPERATOR, alpha1, alpha2, alpha3);
   }

   public FieldRotation(RotationOrder order, RotationConvention convention, T alpha1, T alpha2, T alpha3) {
      T one = alpha1.getField().getOne();
      FieldRotation<T> r1 = new FieldRotation<>(new FieldVector3D<>(one, order.getA1()), alpha1, convention);
      FieldRotation<T> r2 = new FieldRotation<>(new FieldVector3D<>(one, order.getA2()), alpha2, convention);
      FieldRotation<T> r3 = new FieldRotation<>(new FieldVector3D<>(one, order.getA3()), alpha3, convention);
      FieldRotation<T> composed = r1.compose(r2.compose(r3, convention), convention);
      this.q0 = composed.q0;
      this.q1 = composed.q1;
      this.q2 = composed.q2;
      this.q3 = composed.q3;
   }

   private T[] mat2quat(T[][] ort) {
      T[] quat = MathArrays.buildArray(ort[0][0].getField(), 4);
      T s = ort[0][0].add(ort[1][1]).add(ort[2][2]);
      if (s.getReal() > -0.19) {
         quat[0] = s.add(1.0).sqrt().multiply(0.5);
         T inv = quat[0].reciprocal().multiply(0.25);
         quat[1] = inv.multiply(ort[1][2].subtract(ort[2][1]));
         quat[2] = inv.multiply(ort[2][0].subtract(ort[0][2]));
         quat[3] = inv.multiply(ort[0][1].subtract(ort[1][0]));
      } else {
         s = ort[0][0].subtract(ort[1][1]).subtract(ort[2][2]);
         if (s.getReal() > -0.19) {
            quat[1] = s.add(1.0).sqrt().multiply(0.5);
            T inv = quat[1].reciprocal().multiply(0.25);
            quat[0] = inv.multiply(ort[1][2].subtract(ort[2][1]));
            quat[2] = inv.multiply(ort[0][1].add(ort[1][0]));
            quat[3] = inv.multiply(ort[0][2].add(ort[2][0]));
         } else {
            s = ort[1][1].subtract(ort[0][0]).subtract(ort[2][2]);
            if (s.getReal() > -0.19) {
               quat[2] = s.add(1.0).sqrt().multiply(0.5);
               T inv = quat[2].reciprocal().multiply(0.25);
               quat[0] = inv.multiply(ort[2][0].subtract(ort[0][2]));
               quat[1] = inv.multiply(ort[0][1].add(ort[1][0]));
               quat[3] = inv.multiply(ort[2][1].add(ort[1][2]));
            } else {
               s = ort[2][2].subtract(ort[0][0]).subtract(ort[1][1]);
               quat[3] = s.add(1.0).sqrt().multiply(0.5);
               T inv = quat[3].reciprocal().multiply(0.25);
               quat[0] = inv.multiply(ort[0][1].subtract(ort[1][0]));
               quat[1] = inv.multiply(ort[0][2].add(ort[2][0]));
               quat[2] = inv.multiply(ort[2][1].add(ort[1][2]));
            }
         }
      }

      return quat;
   }

   public FieldRotation<T> revert() {
      return new FieldRotation<>(this.q0.negate(), this.q1, this.q2, this.q3, false);
   }

   public T getQ0() {
      return this.q0;
   }

   public T getQ1() {
      return this.q1;
   }

   public T getQ2() {
      return this.q2;
   }

   public T getQ3() {
      return this.q3;
   }

   @Deprecated
   public FieldVector3D<T> getAxis() {
      return this.getAxis(RotationConvention.VECTOR_OPERATOR);
   }

   public FieldVector3D<T> getAxis(RotationConvention convention) {
      T squaredSine = this.q1.multiply(this.q1).add(this.q2.multiply(this.q2)).add(this.q3.multiply(this.q3));
      if (squaredSine.getReal() == 0.0) {
         Field<T> field = squaredSine.getField();
         return new FieldVector3D<>(
            convention == RotationConvention.VECTOR_OPERATOR ? field.getOne() : field.getOne().negate(), field.getZero(), field.getZero()
         );
      } else {
         double sgn = convention == RotationConvention.VECTOR_OPERATOR ? 1.0 : -1.0;
         if (this.q0.getReal() < 0.0) {
            T inverse = squaredSine.sqrt().reciprocal().multiply(sgn);
            return new FieldVector3D<>(this.q1.multiply(inverse), this.q2.multiply(inverse), this.q3.multiply(inverse));
         } else {
            T inverse = squaredSine.sqrt().reciprocal().negate().multiply(sgn);
            return new FieldVector3D<>(this.q1.multiply(inverse), this.q2.multiply(inverse), this.q3.multiply(inverse));
         }
      }
   }

   public T getAngle() {
      if (this.q0.getReal() < -0.1 || this.q0.getReal() > 0.1) {
         return this.q1.multiply(this.q1).add(this.q2.multiply(this.q2)).add(this.q3.multiply(this.q3)).sqrt().asin().multiply(2);
      } else {
         return this.q0.getReal() < 0.0 ? this.q0.negate().acos().multiply(2) : this.q0.acos().multiply(2);
      }
   }

   @Deprecated
   public T[] getAngles(RotationOrder order) throws CardanEulerSingularityException {
      return this.getAngles(order, RotationConvention.VECTOR_OPERATOR);
   }

   public T[] getAngles(RotationOrder order, RotationConvention convention) throws CardanEulerSingularityException {
      if (convention == RotationConvention.VECTOR_OPERATOR) {
         if (order == RotationOrder.XYZ) {
            FieldVector3D<T> v1 = this.applyTo(this.vector(0.0, 0.0, 1.0));
            FieldVector3D<T> v2 = this.applyInverseTo(this.vector(1.0, 0.0, 0.0));
            if (!(v2.getZ().getReal() < -0.9999999999) && !(v2.getZ().getReal() > 0.9999999999)) {
               return this.buildArray(v1.getY().negate().atan2(v1.getZ()), v2.getZ().asin(), v2.getY().negate().atan2(v2.getX()));
            } else {
               throw new CardanEulerSingularityException(true);
            }
         } else if (order == RotationOrder.XZY) {
            FieldVector3D<T> v1 = this.applyTo(this.vector(0.0, 1.0, 0.0));
            FieldVector3D<T> v2 = this.applyInverseTo(this.vector(1.0, 0.0, 0.0));
            if (!(v2.getY().getReal() < -0.9999999999) && !(v2.getY().getReal() > 0.9999999999)) {
               return this.buildArray(v1.getZ().atan2(v1.getY()), v2.getY().asin().negate(), v2.getZ().atan2(v2.getX()));
            } else {
               throw new CardanEulerSingularityException(true);
            }
         } else if (order == RotationOrder.YXZ) {
            FieldVector3D<T> v1 = this.applyTo(this.vector(0.0, 0.0, 1.0));
            FieldVector3D<T> v2 = this.applyInverseTo(this.vector(0.0, 1.0, 0.0));
            if (!(v2.getZ().getReal() < -0.9999999999) && !(v2.getZ().getReal() > 0.9999999999)) {
               return this.buildArray(v1.getX().atan2(v1.getZ()), v2.getZ().asin().negate(), v2.getX().atan2(v2.getY()));
            } else {
               throw new CardanEulerSingularityException(true);
            }
         } else if (order == RotationOrder.YZX) {
            FieldVector3D<T> v1 = this.applyTo(this.vector(1.0, 0.0, 0.0));
            FieldVector3D<T> v2 = this.applyInverseTo(this.vector(0.0, 1.0, 0.0));
            if (!(v2.getX().getReal() < -0.9999999999) && !(v2.getX().getReal() > 0.9999999999)) {
               return this.buildArray(v1.getZ().negate().atan2(v1.getX()), v2.getX().asin(), v2.getZ().negate().atan2(v2.getY()));
            } else {
               throw new CardanEulerSingularityException(true);
            }
         } else if (order == RotationOrder.ZXY) {
            FieldVector3D<T> v1 = this.applyTo(this.vector(0.0, 1.0, 0.0));
            FieldVector3D<T> v2 = this.applyInverseTo(this.vector(0.0, 0.0, 1.0));
            if (!(v2.getY().getReal() < -0.9999999999) && !(v2.getY().getReal() > 0.9999999999)) {
               return this.buildArray(v1.getX().negate().atan2(v1.getY()), v2.getY().asin(), v2.getX().negate().atan2(v2.getZ()));
            } else {
               throw new CardanEulerSingularityException(true);
            }
         } else if (order == RotationOrder.ZYX) {
            FieldVector3D<T> v1 = this.applyTo(this.vector(1.0, 0.0, 0.0));
            FieldVector3D<T> v2 = this.applyInverseTo(this.vector(0.0, 0.0, 1.0));
            if (!(v2.getX().getReal() < -0.9999999999) && !(v2.getX().getReal() > 0.9999999999)) {
               return this.buildArray(v1.getY().atan2(v1.getX()), v2.getX().asin().negate(), v2.getY().atan2(v2.getZ()));
            } else {
               throw new CardanEulerSingularityException(true);
            }
         } else if (order == RotationOrder.XYX) {
            FieldVector3D<T> v1 = this.applyTo(this.vector(1.0, 0.0, 0.0));
            FieldVector3D<T> v2 = this.applyInverseTo(this.vector(1.0, 0.0, 0.0));
            if (!(v2.getX().getReal() < -0.9999999999) && !(v2.getX().getReal() > 0.9999999999)) {
               return this.buildArray(v1.getY().atan2(v1.getZ().negate()), v2.getX().acos(), v2.getY().atan2(v2.getZ()));
            } else {
               throw new CardanEulerSingularityException(false);
            }
         } else if (order == RotationOrder.XZX) {
            FieldVector3D<T> v1 = this.applyTo(this.vector(1.0, 0.0, 0.0));
            FieldVector3D<T> v2 = this.applyInverseTo(this.vector(1.0, 0.0, 0.0));
            if (!(v2.getX().getReal() < -0.9999999999) && !(v2.getX().getReal() > 0.9999999999)) {
               return this.buildArray(v1.getZ().atan2(v1.getY()), v2.getX().acos(), v2.getZ().atan2(v2.getY().negate()));
            } else {
               throw new CardanEulerSingularityException(false);
            }
         } else if (order == RotationOrder.YXY) {
            FieldVector3D<T> v1 = this.applyTo(this.vector(0.0, 1.0, 0.0));
            FieldVector3D<T> v2 = this.applyInverseTo(this.vector(0.0, 1.0, 0.0));
            if (!(v2.getY().getReal() < -0.9999999999) && !(v2.getY().getReal() > 0.9999999999)) {
               return this.buildArray(v1.getX().atan2(v1.getZ()), v2.getY().acos(), v2.getX().atan2(v2.getZ().negate()));
            } else {
               throw new CardanEulerSingularityException(false);
            }
         } else if (order == RotationOrder.YZY) {
            FieldVector3D<T> v1 = this.applyTo(this.vector(0.0, 1.0, 0.0));
            FieldVector3D<T> v2 = this.applyInverseTo(this.vector(0.0, 1.0, 0.0));
            if (!(v2.getY().getReal() < -0.9999999999) && !(v2.getY().getReal() > 0.9999999999)) {
               return this.buildArray(v1.getZ().atan2(v1.getX().negate()), v2.getY().acos(), v2.getZ().atan2(v2.getX()));
            } else {
               throw new CardanEulerSingularityException(false);
            }
         } else if (order == RotationOrder.ZXZ) {
            FieldVector3D<T> v1 = this.applyTo(this.vector(0.0, 0.0, 1.0));
            FieldVector3D<T> v2 = this.applyInverseTo(this.vector(0.0, 0.0, 1.0));
            if (!(v2.getZ().getReal() < -0.9999999999) && !(v2.getZ().getReal() > 0.9999999999)) {
               return this.buildArray(v1.getX().atan2(v1.getY().negate()), v2.getZ().acos(), v2.getX().atan2(v2.getY()));
            } else {
               throw new CardanEulerSingularityException(false);
            }
         } else {
            FieldVector3D<T> v1 = this.applyTo(this.vector(0.0, 0.0, 1.0));
            FieldVector3D<T> v2 = this.applyInverseTo(this.vector(0.0, 0.0, 1.0));
            if (!(v2.getZ().getReal() < -0.9999999999) && !(v2.getZ().getReal() > 0.9999999999)) {
               return this.buildArray(v1.getY().atan2(v1.getX()), v2.getZ().acos(), v2.getY().atan2(v2.getX().negate()));
            } else {
               throw new CardanEulerSingularityException(false);
            }
         }
      } else if (order == RotationOrder.XYZ) {
         FieldVector3D<T> v1 = this.applyTo(Vector3D.PLUS_I);
         FieldVector3D<T> v2 = this.applyInverseTo(Vector3D.PLUS_K);
         if (!(v2.getX().getReal() < -0.9999999999) && !(v2.getX().getReal() > 0.9999999999)) {
            return this.buildArray(v2.getY().negate().atan2(v2.getZ()), v2.getX().asin(), v1.getY().negate().atan2(v1.getX()));
         } else {
            throw new CardanEulerSingularityException(true);
         }
      } else if (order == RotationOrder.XZY) {
         FieldVector3D<T> v1 = this.applyTo(Vector3D.PLUS_I);
         FieldVector3D<T> v2 = this.applyInverseTo(Vector3D.PLUS_J);
         if (!(v2.getX().getReal() < -0.9999999999) && !(v2.getX().getReal() > 0.9999999999)) {
            return this.buildArray(v2.getZ().atan2(v2.getY()), v2.getX().asin().negate(), v1.getZ().atan2(v1.getX()));
         } else {
            throw new CardanEulerSingularityException(true);
         }
      } else if (order == RotationOrder.YXZ) {
         FieldVector3D<T> v1 = this.applyTo(Vector3D.PLUS_J);
         FieldVector3D<T> v2 = this.applyInverseTo(Vector3D.PLUS_K);
         if (!(v2.getY().getReal() < -0.9999999999) && !(v2.getY().getReal() > 0.9999999999)) {
            return this.buildArray(v2.getX().atan2(v2.getZ()), v2.getY().asin().negate(), v1.getX().atan2(v1.getY()));
         } else {
            throw new CardanEulerSingularityException(true);
         }
      } else if (order == RotationOrder.YZX) {
         FieldVector3D<T> v1 = this.applyTo(Vector3D.PLUS_J);
         FieldVector3D<T> v2 = this.applyInverseTo(Vector3D.PLUS_I);
         if (!(v2.getY().getReal() < -0.9999999999) && !(v2.getY().getReal() > 0.9999999999)) {
            return this.buildArray(v2.getZ().negate().atan2(v2.getX()), v2.getY().asin(), v1.getZ().negate().atan2(v1.getY()));
         } else {
            throw new CardanEulerSingularityException(true);
         }
      } else if (order == RotationOrder.ZXY) {
         FieldVector3D<T> v1 = this.applyTo(Vector3D.PLUS_K);
         FieldVector3D<T> v2 = this.applyInverseTo(Vector3D.PLUS_J);
         if (!(v2.getZ().getReal() < -0.9999999999) && !(v2.getZ().getReal() > 0.9999999999)) {
            return this.buildArray(v2.getX().negate().atan2(v2.getY()), v2.getZ().asin(), v1.getX().negate().atan2(v1.getZ()));
         } else {
            throw new CardanEulerSingularityException(true);
         }
      } else if (order == RotationOrder.ZYX) {
         FieldVector3D<T> v1 = this.applyTo(Vector3D.PLUS_K);
         FieldVector3D<T> v2 = this.applyInverseTo(Vector3D.PLUS_I);
         if (!(v2.getZ().getReal() < -0.9999999999) && !(v2.getZ().getReal() > 0.9999999999)) {
            return this.buildArray(v2.getY().atan2(v2.getX()), v2.getZ().asin().negate(), v1.getY().atan2(v1.getZ()));
         } else {
            throw new CardanEulerSingularityException(true);
         }
      } else if (order == RotationOrder.XYX) {
         FieldVector3D<T> v1 = this.applyTo(Vector3D.PLUS_I);
         FieldVector3D<T> v2 = this.applyInverseTo(Vector3D.PLUS_I);
         if (!(v2.getX().getReal() < -0.9999999999) && !(v2.getX().getReal() > 0.9999999999)) {
            return this.buildArray(v2.getY().atan2(v2.getZ().negate()), v2.getX().acos(), v1.getY().atan2(v1.getZ()));
         } else {
            throw new CardanEulerSingularityException(false);
         }
      } else if (order == RotationOrder.XZX) {
         FieldVector3D<T> v1 = this.applyTo(Vector3D.PLUS_I);
         FieldVector3D<T> v2 = this.applyInverseTo(Vector3D.PLUS_I);
         if (!(v2.getX().getReal() < -0.9999999999) && !(v2.getX().getReal() > 0.9999999999)) {
            return this.buildArray(v2.getZ().atan2(v2.getY()), v2.getX().acos(), v1.getZ().atan2(v1.getY().negate()));
         } else {
            throw new CardanEulerSingularityException(false);
         }
      } else if (order == RotationOrder.YXY) {
         FieldVector3D<T> v1 = this.applyTo(Vector3D.PLUS_J);
         FieldVector3D<T> v2 = this.applyInverseTo(Vector3D.PLUS_J);
         if (!(v2.getY().getReal() < -0.9999999999) && !(v2.getY().getReal() > 0.9999999999)) {
            return this.buildArray(v2.getX().atan2(v2.getZ()), v2.getY().acos(), v1.getX().atan2(v1.getZ().negate()));
         } else {
            throw new CardanEulerSingularityException(false);
         }
      } else if (order == RotationOrder.YZY) {
         FieldVector3D<T> v1 = this.applyTo(Vector3D.PLUS_J);
         FieldVector3D<T> v2 = this.applyInverseTo(Vector3D.PLUS_J);
         if (!(v2.getY().getReal() < -0.9999999999) && !(v2.getY().getReal() > 0.9999999999)) {
            return this.buildArray(v2.getZ().atan2(v2.getX().negate()), v2.getY().acos(), v1.getZ().atan2(v1.getX()));
         } else {
            throw new CardanEulerSingularityException(false);
         }
      } else if (order == RotationOrder.ZXZ) {
         FieldVector3D<T> v1 = this.applyTo(Vector3D.PLUS_K);
         FieldVector3D<T> v2 = this.applyInverseTo(Vector3D.PLUS_K);
         if (!(v2.getZ().getReal() < -0.9999999999) && !(v2.getZ().getReal() > 0.9999999999)) {
            return this.buildArray(v2.getX().atan2(v2.getY().negate()), v2.getZ().acos(), v1.getX().atan2(v1.getY()));
         } else {
            throw new CardanEulerSingularityException(false);
         }
      } else {
         FieldVector3D<T> v1 = this.applyTo(Vector3D.PLUS_K);
         FieldVector3D<T> v2 = this.applyInverseTo(Vector3D.PLUS_K);
         if (!(v2.getZ().getReal() < -0.9999999999) && !(v2.getZ().getReal() > 0.9999999999)) {
            return this.buildArray(v2.getY().atan2(v2.getX()), v2.getZ().acos(), v1.getY().atan2(v1.getX().negate()));
         } else {
            throw new CardanEulerSingularityException(false);
         }
      }
   }

   private T[] buildArray(T a0, T a1, T a2) {
      T[] array = MathArrays.buildArray(a0.getField(), 3);
      array[0] = a0;
      array[1] = a1;
      array[2] = a2;
      return array;
   }

   private FieldVector3D<T> vector(double x, double y, double z) {
      T zero = this.q0.getField().getZero();
      return new FieldVector3D<>(zero.add(x), zero.add(y), zero.add(z));
   }

   public T[][] getMatrix() {
      T q0q0 = this.q0.multiply(this.q0);
      T q0q1 = this.q0.multiply(this.q1);
      T q0q2 = this.q0.multiply(this.q2);
      T q0q3 = this.q0.multiply(this.q3);
      T q1q1 = this.q1.multiply(this.q1);
      T q1q2 = this.q1.multiply(this.q2);
      T q1q3 = this.q1.multiply(this.q3);
      T q2q2 = this.q2.multiply(this.q2);
      T q2q3 = this.q2.multiply(this.q3);
      T q3q3 = this.q3.multiply(this.q3);
      T[][] m = MathArrays.buildArray(this.q0.getField(), 3, 3);
      m[0][0] = q0q0.add(q1q1).multiply(2).subtract(1.0);
      m[1][0] = q1q2.subtract(q0q3).multiply(2);
      m[2][0] = q1q3.add(q0q2).multiply(2);
      m[0][1] = q1q2.add(q0q3).multiply(2);
      m[1][1] = q0q0.add(q2q2).multiply(2).subtract(1.0);
      m[2][1] = q2q3.subtract(q0q1).multiply(2);
      m[0][2] = q1q3.subtract(q0q2).multiply(2);
      m[1][2] = q2q3.add(q0q1).multiply(2);
      m[2][2] = q0q0.add(q3q3).multiply(2).subtract(1.0);
      return m;
   }

   public Rotation toRotation() {
      return new Rotation(this.q0.getReal(), this.q1.getReal(), this.q2.getReal(), this.q3.getReal(), false);
   }

   public FieldVector3D<T> applyTo(FieldVector3D<T> u) {
      T x = u.getX();
      T y = u.getY();
      T z = u.getZ();
      T s = this.q1.multiply(x).add(this.q2.multiply(y)).add(this.q3.multiply(z));
      return new FieldVector3D<>(
         this.q0.multiply(x.multiply(this.q0).subtract(this.q2.multiply(z).subtract(this.q3.multiply(y)))).add(s.multiply(this.q1)).multiply(2).subtract(x),
         this.q0.multiply(y.multiply(this.q0).subtract(this.q3.multiply(x).subtract(this.q1.multiply(z)))).add(s.multiply(this.q2)).multiply(2).subtract(y),
         this.q0.multiply(z.multiply(this.q0).subtract(this.q1.multiply(y).subtract(this.q2.multiply(x)))).add(s.multiply(this.q3)).multiply(2).subtract(z)
      );
   }

   public FieldVector3D<T> applyTo(Vector3D u) {
      double x = u.getX();
      double y = u.getY();
      double z = u.getZ();
      T s = this.q1.multiply(x).add(this.q2.multiply(y)).add(this.q3.multiply(z));
      return new FieldVector3D<>(
         this.q0.multiply(this.q0.multiply(x).subtract(this.q2.multiply(z).subtract(this.q3.multiply(y)))).add(s.multiply(this.q1)).multiply(2).subtract(x),
         this.q0.multiply(this.q0.multiply(y).subtract(this.q3.multiply(x).subtract(this.q1.multiply(z)))).add(s.multiply(this.q2)).multiply(2).subtract(y),
         this.q0.multiply(this.q0.multiply(z).subtract(this.q1.multiply(y).subtract(this.q2.multiply(x)))).add(s.multiply(this.q3)).multiply(2).subtract(z)
      );
   }

   public void applyTo(T[] in, T[] out) {
      T x = in[0];
      T y = in[1];
      T z = in[2];
      T s = this.q1.multiply(x).add(this.q2.multiply(y)).add(this.q3.multiply(z));
      out[0] = this.q0
         .multiply(x.multiply(this.q0).subtract(this.q2.multiply(z).subtract(this.q3.multiply(y))))
         .add(s.multiply(this.q1))
         .multiply(2)
         .subtract(x);
      out[1] = this.q0
         .multiply(y.multiply(this.q0).subtract(this.q3.multiply(x).subtract(this.q1.multiply(z))))
         .add(s.multiply(this.q2))
         .multiply(2)
         .subtract(y);
      out[2] = this.q0
         .multiply(z.multiply(this.q0).subtract(this.q1.multiply(y).subtract(this.q2.multiply(x))))
         .add(s.multiply(this.q3))
         .multiply(2)
         .subtract(z);
   }

   public void applyTo(double[] in, T[] out) {
      double x = in[0];
      double y = in[1];
      double z = in[2];
      T s = this.q1.multiply(x).add(this.q2.multiply(y)).add(this.q3.multiply(z));
      out[0] = this.q0
         .multiply(this.q0.multiply(x).subtract(this.q2.multiply(z).subtract(this.q3.multiply(y))))
         .add(s.multiply(this.q1))
         .multiply(2)
         .subtract(x);
      out[1] = this.q0
         .multiply(this.q0.multiply(y).subtract(this.q3.multiply(x).subtract(this.q1.multiply(z))))
         .add(s.multiply(this.q2))
         .multiply(2)
         .subtract(y);
      out[2] = this.q0
         .multiply(this.q0.multiply(z).subtract(this.q1.multiply(y).subtract(this.q2.multiply(x))))
         .add(s.multiply(this.q3))
         .multiply(2)
         .subtract(z);
   }

   public static <T extends RealFieldElement<T>> FieldVector3D<T> applyTo(Rotation r, FieldVector3D<T> u) {
      T x = u.getX();
      T y = u.getY();
      T z = u.getZ();
      T s = (T)x.multiply(r.getQ1()).add(y.multiply(r.getQ2())).add(z.multiply(r.getQ3()));
      return new FieldVector3D<>(
         x.multiply(r.getQ0())
            .subtract(z.multiply(r.getQ2()).subtract(y.multiply(r.getQ3())))
            .multiply(r.getQ0())
            .add(s.multiply(r.getQ1()))
            .multiply(2)
            .subtract(x),
         y.multiply(r.getQ0())
            .subtract(x.multiply(r.getQ3()).subtract(z.multiply(r.getQ1())))
            .multiply(r.getQ0())
            .add(s.multiply(r.getQ2()))
            .multiply(2)
            .subtract(y),
         z.multiply(r.getQ0())
            .subtract(y.multiply(r.getQ1()).subtract(x.multiply(r.getQ2())))
            .multiply(r.getQ0())
            .add(s.multiply(r.getQ3()))
            .multiply(2)
            .subtract(z)
      );
   }

   public FieldVector3D<T> applyInverseTo(FieldVector3D<T> u) {
      T x = u.getX();
      T y = u.getY();
      T z = u.getZ();
      T s = this.q1.multiply(x).add(this.q2.multiply(y)).add(this.q3.multiply(z));
      T m0 = this.q0.negate();
      return new FieldVector3D<>(
         m0.multiply(x.multiply(m0).subtract(this.q2.multiply(z).subtract(this.q3.multiply(y)))).add(s.multiply(this.q1)).multiply(2).subtract(x),
         m0.multiply(y.multiply(m0).subtract(this.q3.multiply(x).subtract(this.q1.multiply(z)))).add(s.multiply(this.q2)).multiply(2).subtract(y),
         m0.multiply(z.multiply(m0).subtract(this.q1.multiply(y).subtract(this.q2.multiply(x)))).add(s.multiply(this.q3)).multiply(2).subtract(z)
      );
   }

   public FieldVector3D<T> applyInverseTo(Vector3D u) {
      double x = u.getX();
      double y = u.getY();
      double z = u.getZ();
      T s = this.q1.multiply(x).add(this.q2.multiply(y)).add(this.q3.multiply(z));
      T m0 = this.q0.negate();
      return new FieldVector3D<>(
         m0.multiply(m0.multiply(x).subtract(this.q2.multiply(z).subtract(this.q3.multiply(y)))).add(s.multiply(this.q1)).multiply(2).subtract(x),
         m0.multiply(m0.multiply(y).subtract(this.q3.multiply(x).subtract(this.q1.multiply(z)))).add(s.multiply(this.q2)).multiply(2).subtract(y),
         m0.multiply(m0.multiply(z).subtract(this.q1.multiply(y).subtract(this.q2.multiply(x)))).add(s.multiply(this.q3)).multiply(2).subtract(z)
      );
   }

   public void applyInverseTo(T[] in, T[] out) {
      T x = in[0];
      T y = in[1];
      T z = in[2];
      T s = this.q1.multiply(x).add(this.q2.multiply(y)).add(this.q3.multiply(z));
      T m0 = this.q0.negate();
      out[0] = m0.multiply(x.multiply(m0).subtract(this.q2.multiply(z).subtract(this.q3.multiply(y)))).add(s.multiply(this.q1)).multiply(2).subtract(x);
      out[1] = m0.multiply(y.multiply(m0).subtract(this.q3.multiply(x).subtract(this.q1.multiply(z)))).add(s.multiply(this.q2)).multiply(2).subtract(y);
      out[2] = m0.multiply(z.multiply(m0).subtract(this.q1.multiply(y).subtract(this.q2.multiply(x)))).add(s.multiply(this.q3)).multiply(2).subtract(z);
   }

   public void applyInverseTo(double[] in, T[] out) {
      double x = in[0];
      double y = in[1];
      double z = in[2];
      T s = this.q1.multiply(x).add(this.q2.multiply(y)).add(this.q3.multiply(z));
      T m0 = this.q0.negate();
      out[0] = m0.multiply(m0.multiply(x).subtract(this.q2.multiply(z).subtract(this.q3.multiply(y)))).add(s.multiply(this.q1)).multiply(2).subtract(x);
      out[1] = m0.multiply(m0.multiply(y).subtract(this.q3.multiply(x).subtract(this.q1.multiply(z)))).add(s.multiply(this.q2)).multiply(2).subtract(y);
      out[2] = m0.multiply(m0.multiply(z).subtract(this.q1.multiply(y).subtract(this.q2.multiply(x)))).add(s.multiply(this.q3)).multiply(2).subtract(z);
   }

   public static <T extends RealFieldElement<T>> FieldVector3D<T> applyInverseTo(Rotation r, FieldVector3D<T> u) {
      T x = u.getX();
      T y = u.getY();
      T z = u.getZ();
      T s = (T)x.multiply(r.getQ1()).add(y.multiply(r.getQ2())).add(z.multiply(r.getQ3()));
      double m0 = -r.getQ0();
      return new FieldVector3D<>(
         x.multiply(m0).subtract(z.multiply(r.getQ2()).subtract(y.multiply(r.getQ3()))).multiply(m0).add(s.multiply(r.getQ1())).multiply(2).subtract(x),
         y.multiply(m0).subtract(x.multiply(r.getQ3()).subtract(z.multiply(r.getQ1()))).multiply(m0).add(s.multiply(r.getQ2())).multiply(2).subtract(y),
         z.multiply(m0).subtract(y.multiply(r.getQ1()).subtract(x.multiply(r.getQ2()))).multiply(m0).add(s.multiply(r.getQ3())).multiply(2).subtract(z)
      );
   }

   public FieldRotation<T> applyTo(FieldRotation<T> r) {
      return this.compose(r, RotationConvention.VECTOR_OPERATOR);
   }

   public FieldRotation<T> compose(FieldRotation<T> r, RotationConvention convention) {
      return convention == RotationConvention.VECTOR_OPERATOR ? this.composeInternal(r) : r.composeInternal(this);
   }

   private FieldRotation<T> composeInternal(FieldRotation<T> r) {
      return new FieldRotation<>(
         r.q0.multiply(this.q0).subtract(r.q1.multiply(this.q1).add(r.q2.multiply(this.q2)).add(r.q3.multiply(this.q3))),
         r.q1.multiply(this.q0).add(r.q0.multiply(this.q1)).add(r.q2.multiply(this.q3).subtract(r.q3.multiply(this.q2))),
         r.q2.multiply(this.q0).add(r.q0.multiply(this.q2)).add(r.q3.multiply(this.q1).subtract(r.q1.multiply(this.q3))),
         r.q3.multiply(this.q0).add(r.q0.multiply(this.q3)).add(r.q1.multiply(this.q2).subtract(r.q2.multiply(this.q1))),
         false
      );
   }

   public FieldRotation<T> applyTo(Rotation r) {
      return this.compose(r, RotationConvention.VECTOR_OPERATOR);
   }

   public FieldRotation<T> compose(Rotation r, RotationConvention convention) {
      return convention == RotationConvention.VECTOR_OPERATOR ? this.composeInternal(r) : applyTo(r, this);
   }

   private FieldRotation<T> composeInternal(Rotation r) {
      return new FieldRotation<>(
         this.q0.multiply(r.getQ0()).subtract(this.q1.multiply(r.getQ1()).add(this.q2.multiply(r.getQ2())).add(this.q3.multiply(r.getQ3()))),
         this.q0.multiply(r.getQ1()).add(this.q1.multiply(r.getQ0())).add(this.q3.multiply(r.getQ2()).subtract(this.q2.multiply(r.getQ3()))),
         this.q0.multiply(r.getQ2()).add(this.q2.multiply(r.getQ0())).add(this.q1.multiply(r.getQ3()).subtract(this.q3.multiply(r.getQ1()))),
         this.q0.multiply(r.getQ3()).add(this.q3.multiply(r.getQ0())).add(this.q2.multiply(r.getQ1()).subtract(this.q1.multiply(r.getQ2()))),
         false
      );
   }

   public static <T extends RealFieldElement<T>> FieldRotation<T> applyTo(Rotation r1, FieldRotation<T> rInner) {
      return new FieldRotation<>(
         rInner.q0.multiply(r1.getQ0()).subtract(rInner.q1.multiply(r1.getQ1()).add(rInner.q2.multiply(r1.getQ2())).add(rInner.q3.multiply(r1.getQ3()))),
         rInner.q1.multiply(r1.getQ0()).add(rInner.q0.multiply(r1.getQ1())).add(rInner.q2.multiply(r1.getQ3()).subtract(rInner.q3.multiply(r1.getQ2()))),
         rInner.q2.multiply(r1.getQ0()).add(rInner.q0.multiply(r1.getQ2())).add(rInner.q3.multiply(r1.getQ1()).subtract(rInner.q1.multiply(r1.getQ3()))),
         rInner.q3.multiply(r1.getQ0()).add(rInner.q0.multiply(r1.getQ3())).add(rInner.q1.multiply(r1.getQ2()).subtract(rInner.q2.multiply(r1.getQ1()))),
         false
      );
   }

   public FieldRotation<T> applyInverseTo(FieldRotation<T> r) {
      return this.composeInverse(r, RotationConvention.VECTOR_OPERATOR);
   }

   public FieldRotation<T> composeInverse(FieldRotation<T> r, RotationConvention convention) {
      return convention == RotationConvention.VECTOR_OPERATOR ? this.composeInverseInternal(r) : r.composeInternal(this.revert());
   }

   private FieldRotation<T> composeInverseInternal(FieldRotation<T> r) {
      return new FieldRotation<>(
         r.q0.multiply(this.q0).add(r.q1.multiply(this.q1).add(r.q2.multiply(this.q2)).add(r.q3.multiply(this.q3))).negate(),
         r.q0.multiply(this.q1).add(r.q2.multiply(this.q3).subtract(r.q3.multiply(this.q2))).subtract(r.q1.multiply(this.q0)),
         r.q0.multiply(this.q2).add(r.q3.multiply(this.q1).subtract(r.q1.multiply(this.q3))).subtract(r.q2.multiply(this.q0)),
         r.q0.multiply(this.q3).add(r.q1.multiply(this.q2).subtract(r.q2.multiply(this.q1))).subtract(r.q3.multiply(this.q0)),
         false
      );
   }

   public FieldRotation<T> applyInverseTo(Rotation r) {
      return this.composeInverse(r, RotationConvention.VECTOR_OPERATOR);
   }

   public FieldRotation<T> composeInverse(Rotation r, RotationConvention convention) {
      return convention == RotationConvention.VECTOR_OPERATOR ? this.composeInverseInternal(r) : applyTo(r, this.revert());
   }

   private FieldRotation<T> composeInverseInternal(Rotation r) {
      return new FieldRotation<>(
         this.q0.multiply(r.getQ0()).add(this.q1.multiply(r.getQ1()).add(this.q2.multiply(r.getQ2())).add(this.q3.multiply(r.getQ3()))).negate(),
         this.q1.multiply(r.getQ0()).add(this.q3.multiply(r.getQ2()).subtract(this.q2.multiply(r.getQ3()))).subtract(this.q0.multiply(r.getQ1())),
         this.q2.multiply(r.getQ0()).add(this.q1.multiply(r.getQ3()).subtract(this.q3.multiply(r.getQ1()))).subtract(this.q0.multiply(r.getQ2())),
         this.q3.multiply(r.getQ0()).add(this.q2.multiply(r.getQ1()).subtract(this.q1.multiply(r.getQ2()))).subtract(this.q0.multiply(r.getQ3())),
         false
      );
   }

   public static <T extends RealFieldElement<T>> FieldRotation<T> applyInverseTo(Rotation rOuter, FieldRotation<T> rInner) {
      return new FieldRotation<>(
         rInner.q0
            .multiply(rOuter.getQ0())
            .add(rInner.q1.multiply(rOuter.getQ1()).add(rInner.q2.multiply(rOuter.getQ2())).add(rInner.q3.multiply(rOuter.getQ3())))
            .negate(),
         rInner.q0
            .multiply(rOuter.getQ1())
            .add(rInner.q2.multiply(rOuter.getQ3()).subtract(rInner.q3.multiply(rOuter.getQ2())))
            .subtract(rInner.q1.multiply(rOuter.getQ0())),
         rInner.q0
            .multiply(rOuter.getQ2())
            .add(rInner.q3.multiply(rOuter.getQ1()).subtract(rInner.q1.multiply(rOuter.getQ3())))
            .subtract(rInner.q2.multiply(rOuter.getQ0())),
         rInner.q0
            .multiply(rOuter.getQ3())
            .add(rInner.q1.multiply(rOuter.getQ2()).subtract(rInner.q2.multiply(rOuter.getQ1())))
            .subtract(rInner.q3.multiply(rOuter.getQ0())),
         false
      );
   }

   private T[][] orthogonalizeMatrix(T[][] m, double threshold) throws NotARotationMatrixException {
      T x00 = m[0][0];
      T x01 = m[0][1];
      T x02 = m[0][2];
      T x10 = m[1][0];
      T x11 = m[1][1];
      T x12 = m[1][2];
      T x20 = m[2][0];
      T x21 = m[2][1];
      T x22 = m[2][2];
      double fn = 0.0;
      T[][] o = MathArrays.buildArray(m[0][0].getField(), 3, 3);
      int i = 0;

      while (++i < 11) {
         T mx00 = m[0][0].multiply(x00).add(m[1][0].multiply(x10)).add(m[2][0].multiply(x20));
         T mx10 = m[0][1].multiply(x00).add(m[1][1].multiply(x10)).add(m[2][1].multiply(x20));
         T mx20 = m[0][2].multiply(x00).add(m[1][2].multiply(x10)).add(m[2][2].multiply(x20));
         T mx01 = m[0][0].multiply(x01).add(m[1][0].multiply(x11)).add(m[2][0].multiply(x21));
         T mx11 = m[0][1].multiply(x01).add(m[1][1].multiply(x11)).add(m[2][1].multiply(x21));
         T mx21 = m[0][2].multiply(x01).add(m[1][2].multiply(x11)).add(m[2][2].multiply(x21));
         T mx02 = m[0][0].multiply(x02).add(m[1][0].multiply(x12)).add(m[2][0].multiply(x22));
         T mx12 = m[0][1].multiply(x02).add(m[1][1].multiply(x12)).add(m[2][1].multiply(x22));
         T mx22 = m[0][2].multiply(x02).add(m[1][2].multiply(x12)).add(m[2][2].multiply(x22));
         o[0][0] = x00.subtract(x00.multiply(mx00).add(x01.multiply(mx10)).add(x02.multiply(mx20)).subtract(m[0][0]).multiply(0.5));
         o[0][1] = x01.subtract(x00.multiply(mx01).add(x01.multiply(mx11)).add(x02.multiply(mx21)).subtract(m[0][1]).multiply(0.5));
         o[0][2] = x02.subtract(x00.multiply(mx02).add(x01.multiply(mx12)).add(x02.multiply(mx22)).subtract(m[0][2]).multiply(0.5));
         o[1][0] = x10.subtract(x10.multiply(mx00).add(x11.multiply(mx10)).add(x12.multiply(mx20)).subtract(m[1][0]).multiply(0.5));
         o[1][1] = x11.subtract(x10.multiply(mx01).add(x11.multiply(mx11)).add(x12.multiply(mx21)).subtract(m[1][1]).multiply(0.5));
         o[1][2] = x12.subtract(x10.multiply(mx02).add(x11.multiply(mx12)).add(x12.multiply(mx22)).subtract(m[1][2]).multiply(0.5));
         o[2][0] = x20.subtract(x20.multiply(mx00).add(x21.multiply(mx10)).add(x22.multiply(mx20)).subtract(m[2][0]).multiply(0.5));
         o[2][1] = x21.subtract(x20.multiply(mx01).add(x21.multiply(mx11)).add(x22.multiply(mx21)).subtract(m[2][1]).multiply(0.5));
         o[2][2] = x22.subtract(x20.multiply(mx02).add(x21.multiply(mx12)).add(x22.multiply(mx22)).subtract(m[2][2]).multiply(0.5));
         double corr00 = o[0][0].getReal() - m[0][0].getReal();
         double corr01 = o[0][1].getReal() - m[0][1].getReal();
         double corr02 = o[0][2].getReal() - m[0][2].getReal();
         double corr10 = o[1][0].getReal() - m[1][0].getReal();
         double corr11 = o[1][1].getReal() - m[1][1].getReal();
         double corr12 = o[1][2].getReal() - m[1][2].getReal();
         double corr20 = o[2][0].getReal() - m[2][0].getReal();
         double corr21 = o[2][1].getReal() - m[2][1].getReal();
         double corr22 = o[2][2].getReal() - m[2][2].getReal();
         double fn1 = corr00 * corr00
            + corr01 * corr01
            + corr02 * corr02
            + corr10 * corr10
            + corr11 * corr11
            + corr12 * corr12
            + corr20 * corr20
            + corr21 * corr21
            + corr22 * corr22;
         if (FastMath.abs(fn1 - fn) <= threshold) {
            return o;
         }

         x00 = o[0][0];
         x01 = o[0][1];
         x02 = o[0][2];
         x10 = o[1][0];
         x11 = o[1][1];
         x12 = o[1][2];
         x20 = o[2][0];
         x21 = o[2][1];
         x22 = o[2][2];
         fn = fn1;
      }

      throw new NotARotationMatrixException(LocalizedFormats.UNABLE_TO_ORTHOGONOLIZE_MATRIX, i - 1);
   }

   public static <T extends RealFieldElement<T>> T distance(FieldRotation<T> r1, FieldRotation<T> r2) {
      return r1.composeInverseInternal(r2).getAngle();
   }
}
