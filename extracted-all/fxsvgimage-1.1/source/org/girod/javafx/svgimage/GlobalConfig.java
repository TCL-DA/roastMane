package org.girod.javafx.svgimage;

import org.girod.javafx.svgimage.xml.parsers.SVGLibraryException;
import org.girod.javafx.svgimage.xml.parsers.SVGParsingException;

public class GlobalConfig implements ExceptionsHandling {
   private static GlobalConfig config = null;
   private Boolean swingAvailable = null;
   private short exceptionsHandling = 1;

   private GlobalConfig() {
   }

   public static GlobalConfig getInstance() {
      if (config == null) {
         config = new GlobalConfig();
      }

      return config;
   }

   public void setExceptionsHandling(short exceptionsHandling) {
      this.exceptionsHandling = exceptionsHandling;
   }

   public short getExceptionsHandling() {
      return this.exceptionsHandling;
   }

   public boolean isSwingAvailable() {
      if (this.swingAvailable == null) {
         try {
            Class.forName("org.girod.javafx.svgimage.AwtImageConverter", true, this.getClass().getClassLoader());
            this.swingAvailable = Boolean.TRUE;
         } catch (ClassNotFoundException var2) {
            this.swingAvailable = Boolean.FALSE;
         }
      }

      return this.swingAvailable;
   }

   public void handleLibraryError(String message) throws SVGLibraryException {
      switch (this.exceptionsHandling) {
         case 0:
            return;
         case 1:
            System.err.println(message);
            break;
         case 2:
            System.err.println(message);
            break;
         case 3:
            System.err.println(message);
            break;
         case 4:
            throw new SVGLibraryException(message);
      }
   }

   public void handleParsingError(String message) throws SVGParsingException {
      switch (this.exceptionsHandling) {
         case 0:
            return;
         case 1:
            System.err.println(message);
            break;
         case 2:
            System.err.println(message);
            break;
         case 3:
            System.err.println(message);
            break;
         case 4:
            throw new SVGParsingException(message);
      }
   }

   public void handleLibraryException(Throwable th) throws SVGLibraryException {
      switch (this.exceptionsHandling) {
         case 0:
            return;
         case 1:
            System.err.println(th.getMessage());
            break;
         case 2:
            th.printStackTrace();
            break;
         case 3:
            if (th instanceof SVGLibraryException) {
               throw (SVGLibraryException)th;
            }

            throw new SVGLibraryException(th);
      }
   }

   public void handleParsingException(Throwable th) throws SVGParsingException {
      switch (this.exceptionsHandling) {
         case 0:
            return;
         case 1:
            System.err.println(th.getMessage());
            break;
         case 2:
            th.printStackTrace();
            break;
         case 3:
            if (th instanceof SVGLibraryException) {
               throw (SVGParsingException)th;
            }

            throw new SVGParsingException(th);
      }
   }
}
