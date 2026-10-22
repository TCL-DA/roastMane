package com.cropster.roastingintelligence.ui.components;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import java.awt.image.BufferedImage;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;

public class QrCodeRenderer {
   private final int renderWidth;
   private final int renderHeight;
   private final double scale;

   public static WritableImage renderImage(int width, int height, String contents) {
      try {
         QRCodeWriter qrCodeWriter = new QRCodeWriter();
         BitMatrix bitMatrix = qrCodeWriter.encode(contents, BarcodeFormat.QR_CODE, width, height);
         BufferedImage bufferedImage = MatrixToImageWriter.toBufferedImage(bitMatrix);
         WritableImage fxImage = new WritableImage(width, height);
         SwingFXUtils.toFXImage(bufferedImage, fxImage);
         return fxImage;
      } catch (Exception var7) {
         throw new RuntimeException("Error generating QR code", var7);
      }
   }

   public QrCodeRenderer(int renderWidth, int renderHeight, double scale) {
      this.renderWidth = renderWidth;
      this.renderHeight = renderHeight;
      this.scale = scale;
   }

   public ImageView renderImageView(String code) {
      ImageView imageView = new ImageView(renderImage(this.renderWidth, this.renderHeight, code));
      imageView.setSmooth(true);
      imageView.setPreserveRatio(true);
      imageView.setFitWidth((double)((int)((double)this.renderWidth * this.scale)));
      imageView.setFitHeight((double)((int)((double)this.renderWidth * this.scale)));
      return imageView;
   }
}
