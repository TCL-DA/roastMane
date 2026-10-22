package org.girod.javafx.svgimage.tosvg.utils;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.geom.AffineTransform;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.ImageProducer;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;
import javax.swing.GrayFilter;

public class AwtImageUtilities {
   private static final double ASSUME_ZERO = 0.001;

   private AwtImageUtilities() {
   }

   public static Image createDisabledImage(Image image) {
      ImageFilter filter = new GrayFilter(true, 20);
      ImageProducer producer = new FilteredImageSource(image.getSource(), filter);
      return Toolkit.getDefaultToolkit().createImage(producer);
   }

   public static Image createOpacifiedImage(Image image, double opacity, boolean multiply) {
      OpacityFilter filter = new OpacityFilter(opacity, multiply);
      ImageProducer producer = new FilteredImageSource(image.getSource(), filter);
      return Toolkit.getDefaultToolkit().createImage(producer);
   }

   public static double getRotationAngle(AffineTransform tr) {
      double m00 = tr.getScaleX();
      double m01 = tr.getShearX();
      double m10 = tr.getShearY();
      double m11 = tr.getScaleY();
      double norm = Math.sqrt(m00 * m00 + m11 * m11);
      if (norm == 0.0) {
         return m10 >= 0.0 ? Math.PI / 2 : -Math.PI / 2;
      } else {
         double xx = m00 / norm;
         double xy = m01 / norm;
         double theta = -Math.atan2(xy, xx);
         return Math.abs(theta) < 0.001 ? 0.0 : theta;
      }
   }

   private static boolean toApply(Transform tr) {
      boolean toApply = true;
      if (tr instanceof Rotate) {
         Rotate rotate = (Rotate)tr;
         if (rotate.getAngle() == 0.0) {
            toApply = false;
         }
      } else if (tr instanceof Scale) {
         Scale scale = (Scale)tr;
         if (scale.getMxx() == 1.0 && scale.getMyy() == 1.0) {
            toApply = false;
         }
      } else if (tr instanceof Translate) {
         Translate translate = (Translate)tr;
         if (translate.getTx() == 0.0 && translate.getTy() == 0.0) {
            toApply = false;
         }
      }

      return toApply;
   }

   public static AffineTransform getTransform(Transform tr) {
      AffineTransform result = new AffineTransform();
      boolean toApply = toApply(tr);
      if (toApply) {
         AffineTransform transform = getAffineTransform(tr);
         result.concatenate(transform);
      }

      return result;
   }

   private static AffineTransform getAffineTransform(Transform tr) {
      AffineTransform affine;
      if (tr instanceof Rotate) {
         Rotate rotate = (Rotate)tr;
         double angle = Math.toRadians(rotate.getAngle());
         affine = AffineTransform.getRotateInstance(angle, rotate.getPivotX(), rotate.getPivotY());
      } else {
         affine = new AffineTransform(tr.getMxx(), tr.getMyx(), tr.getMxy(), tr.getMyy(), tr.getTx(), tr.getTy());
      }

      return affine;
   }
}
