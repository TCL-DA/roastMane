package org.girod.javafx.svgimage;

import java.net.URL;

public class SVGContent {
   public final URL url;
   public final String content;
   public final LoaderParameters params;

   public SVGContent(URL url, LoaderParameters params) {
      this.url = url;
      this.params = params;
      this.content = null;
   }

   public SVGContent(String content, LoaderParameters params) {
      this.url = null;
      this.params = params;
      this.content = content;
   }

   public boolean isFromURL() {
      return this.url != null;
   }

   public boolean isFromString() {
      return this.content != null;
   }
}
