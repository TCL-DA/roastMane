package com.cropster.roastingintelligence.ui.util;

import com.cropster.roastingintelligence.ui.JavaFxUtil;
import java.net.URL;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.util.Builder;
import javafx.util.BuilderFactory;
import org.girod.javafx.svgimage.SVGImage;

public class DelegatingBuilderFactory implements BuilderFactory {
   private final JavaFXBuilderFactory javaFXBuilderFactory = new JavaFXBuilderFactory();

   public Builder<?> getBuilder(Class<?> type) {
      return (Builder<?>)(type.equals(SVGImage.class) ? new DelegatingBuilderFactory.SVGImageBuilder() : this.javaFXBuilderFactory.getBuilder(type));
   }

   public class SVGImageBuilder implements Builder<SVGImage> {
      private String path;
      private Icon icon;
      private String styleClass;
      private double scale = -1.0;

      public String getPath() {
         return this.path;
      }

      public void setPath(String path) {
         this.path = path;
      }

      public Icon getIcon() {
         return this.icon;
      }

      public void setIcon(Icon icon) {
         this.icon = icon;
      }

      public String getStyleClass() {
         return this.styleClass;
      }

      public void setStyleClass(String styleClass) {
         this.styleClass = styleClass;
      }

      public double getScale() {
         return this.scale;
      }

      public void setScale(double scale) {
         this.scale = scale;
      }

      public SVGImage build() {
         URL url;
         if (this.icon != null) {
            if (this.path != null) {
               throw new IllegalStateException("Icon and path must not both be set!");
            }

            url = this.icon.toUrl();
         } else {
            url = DelegatingBuilderFactory.SVGImageBuilder.class.getResource(this.path);
         }

         return JavaFxUtil.loadSvg(url, this.styleClass, this.scale);
      }
   }
}
