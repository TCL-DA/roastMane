package org.girod.javafx.svgimage.xml.parsers;

public class SVGLibraryException extends RuntimeException {
   public SVGLibraryException(String message) {
      super(message);
   }

   public SVGLibraryException(String message, Throwable cause) {
      super(message, cause);
   }

   public SVGLibraryException(Throwable cause) {
      super(cause);
   }
}
