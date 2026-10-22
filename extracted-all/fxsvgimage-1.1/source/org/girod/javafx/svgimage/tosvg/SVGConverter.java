package org.girod.javafx.svgimage.tosvg;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import javafx.scene.Node;
import org.girod.javafx.svgimage.tosvg.converters.ConverterDelegate;
import org.girod.javafx.svgimage.tosvg.xml.XMLNodeUtilities;
import org.girod.javafx.svgimage.tosvg.xml.XMLRoot;

public class SVGConverter {
   private ConverterDelegate delegate = null;

   public SVGConverter() {
      this.delegate = new ConverterDelegate();
   }

   public ConverterDelegate getConverterDelegate() {
      return this.delegate;
   }

   public void convert(Node root, File file) throws IOException {
      this.convert(root, file, new ConverterParameters());
   }

   public void convert(Node root, File file, ConverterParameters params) throws IOException {
      this.delegate.setSVGFile(file);
      XMLRoot xmlRoot = new XMLRoot("svg");
      if (params.width > 0.0) {
         xmlRoot.addAttribute("width", params.width);
      } else {
         double width = root.getBoundsInLocal().getWidth();
         xmlRoot.addAttribute("width", width);
      }

      if (params.height > 0.0) {
         xmlRoot.addAttribute("height", params.height);
      } else {
         double height = root.getBoundsInLocal().getHeight();
         xmlRoot.addAttribute("height", height);
      }

      this.delegate.convertRoot(root, xmlRoot);
      XMLNodeUtilities.print(xmlRoot, 2, file);
   }

   public void convert(Node root, URL url) throws IOException {
      this.convert(root, url, new ConverterParameters());
   }

   public void convert(Node root, URL url, ConverterParameters params) throws IOException {
      this.delegate.setSVGFile(new File(url.getFile()));
      XMLRoot xmlRoot = new XMLRoot("svg");
      if (params.width > 0.0) {
         xmlRoot.addAttribute("width", params.width);
      }

      if (params.height > 0.0) {
         xmlRoot.addAttribute("height", params.height);
      }

      this.delegate.convertRoot(root, xmlRoot);
      XMLNodeUtilities.print(xmlRoot, 2, url);
   }
}
