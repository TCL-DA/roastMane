package org.girod.javafx.svgimage;

import java.io.IOException;
import java.net.URL;
import java.util.PropertyResourceBundle;

public class Main {
   public static void main(String[] args) {
      URL url = Thread.currentThread().getContextClassLoader().getResource("org/girod/javafx/svgimage/fxsvgimage.properties");

      try {
         PropertyResourceBundle prb = new PropertyResourceBundle(url.openStream());
         String version = prb.getString("version");
         String date = prb.getString("date");
         System.out.println("fxsvgimage version " + version + " build on " + date);
         System.out.println("Distributed under the BSD-3-Clause License");
      } catch (IOException var5) {
         var5.printStackTrace();
      }
   }
}
