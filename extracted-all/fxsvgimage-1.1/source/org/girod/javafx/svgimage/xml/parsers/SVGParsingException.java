package org.girod.javafx.svgimage.xml.parsers;

public class SVGParsingException extends SVGLibraryException {
   public SVGParsingException(String message) {
      super(message);
   }

   public SVGParsingException(String message, Throwable cause) {
      super(message, cause);
   }

   public SVGParsingException(Throwable cause) {
      super(cause);
   }
}
