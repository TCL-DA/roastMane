package javafx.print;

import com.sun.javafx.print.PrinterImpl;
import java.util.Set;

public final class PrinterAttributes {
   private PrinterImpl impl;

   PrinterAttributes(PrinterImpl var1) {
      this.impl = var1;
   }

   public int getDefaultCopies() {
      return this.impl.defaultCopies();
   }

   public int getMaxCopies() {
      return this.impl.maxCopies();
   }

   public boolean supportsPageRanges() {
      return this.impl.supportsPageRanges();
   }

   public Collation getDefaultCollation() {
      return this.impl.defaultCollation();
   }

   public Set<Collation> getSupportedCollations() {
      return this.impl.supportedCollations();
   }

   public PrintSides getDefaultPrintSides() {
      return this.impl.defaultSides();
   }

   public Set<PrintSides> getSupportedPrintSides() {
      return this.impl.supportedSides();
   }

   public PrintColor getDefaultPrintColor() {
      return this.impl.defaultPrintColor();
   }

   public Set<PrintColor> getSupportedPrintColors() {
      return this.impl.supportedPrintColor();
   }

   public PrintQuality getDefaultPrintQuality() {
      return this.impl.defaultPrintQuality();
   }

   public Set<PrintQuality> getSupportedPrintQuality() {
      return this.impl.supportedPrintQuality();
   }

   public PrintResolution getDefaultPrintResolution() {
      return this.impl.defaultPrintResolution();
   }

   public Set<PrintResolution> getSupportedPrintResolutions() {
      return this.impl.supportedPrintResolution();
   }

   public PageOrientation getDefaultPageOrientation() {
      return this.impl.defaultOrientation();
   }

   public Set<PageOrientation> getSupportedPageOrientations() {
      return this.impl.supportedOrientation();
   }

   public Paper getDefaultPaper() {
      return this.impl.defaultPaper();
   }

   public Set<Paper> getSupportedPapers() {
      return this.impl.supportedPapers();
   }

   public PaperSource getDefaultPaperSource() {
      return this.impl.defaultPaperSource();
   }

   public Set<PaperSource> getSupportedPaperSources() {
      return this.impl.supportedPaperSources();
   }
}
