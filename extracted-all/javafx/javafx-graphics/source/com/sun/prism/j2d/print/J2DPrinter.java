package com.sun.prism.j2d.print;

import com.sun.javafx.print.PrintHelper;
import com.sun.javafx.print.PrinterImpl;
import com.sun.javafx.print.Units;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import javafx.geometry.Rectangle2D;
import javafx.print.Collation;
import javafx.print.JobSettings;
import javafx.print.PageLayout;
import javafx.print.PageOrientation;
import javafx.print.PageRange;
import javafx.print.Paper;
import javafx.print.PaperSource;
import javafx.print.PrintColor;
import javafx.print.PrintQuality;
import javafx.print.PrintResolution;
import javafx.print.PrintSides;
import javafx.print.Printer;
import javax.print.PrintService;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.standard.Chromaticity;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.CopiesSupported;
import javax.print.attribute.standard.Media;
import javax.print.attribute.standard.MediaPrintableArea;
import javax.print.attribute.standard.MediaSize;
import javax.print.attribute.standard.MediaSizeName;
import javax.print.attribute.standard.MediaTray;
import javax.print.attribute.standard.OrientationRequested;
import javax.print.attribute.standard.PageRanges;
import javax.print.attribute.standard.PrinterResolution;
import javax.print.attribute.standard.SheetCollate;
import javax.print.attribute.standard.Sides;

public class J2DPrinter implements PrinterImpl {
   private PrintService service;
   private Printer fxPrinter;
   private int defaultCopies = 0;
   private int maxCopies = 0;
   private Collation defaultCollation;
   private Set<Collation> collateSet;
   private PrintColor defColor;
   private Set<PrintColor> colorSet;
   private PrintSides defSides;
   private Set<PrintSides> sidesSet;
   private PageOrientation defOrient;
   private Set<PageOrientation> orientSet;
   private PrintResolution defRes;
   private Set<PrintResolution> resSet;
   private PrintQuality defQuality;
   private Set<PrintQuality> qualitySet;
   private Paper defPaper;
   private Set<Paper> paperSet;
   private static Map<MediaSizeName, Paper> predefinedPaperMap;
   private static Map<MediaTray, PaperSource> predefinedTrayMap;
   private PaperSource defPaperSource;
   private Set<PaperSource> paperSourceSet;
   private Map<PaperSource, MediaTray> sourceToTrayMap;
   private Map<MediaTray, PaperSource> trayToSourceMap;
   private final Map<MediaSizeName, Paper> mediaToPaperMap = new HashMap<>();
   private final Map<Paper, MediaSizeName> paperToMediaMap = new HashMap<>();
   private PageLayout defaultLayout;

   public J2DPrinter(PrintService var1) {
      this.service = var1;
   }

   public Printer getPrinter() {
      return this.fxPrinter;
   }

   @Override
   public void setPrinter(Printer var1) {
      this.fxPrinter = var1;
   }

   public PrintService getService() {
      return this.service;
   }

   @Override
   public String getName() {
      return this.service.getName();
   }

   @Override
   public JobSettings getDefaultJobSettings() {
      return PrintHelper.createJobSettings(this.fxPrinter);
   }

   @Override
   public int defaultCopies() {
      if (this.defaultCopies > 0) {
         return this.defaultCopies;
      } else {
         try {
            Copies var1 = (Copies)this.service.getDefaultAttributeValue(Copies.class);
            this.defaultCopies = var1.getValue();
         } catch (Exception var2) {
            this.defaultCopies = 1;
         }

         return this.defaultCopies;
      }
   }

   @Override
   public int maxCopies() {
      if (this.maxCopies > 0) {
         return this.maxCopies;
      } else {
         CopiesSupported var1 = null;

         try {
            var1 = (CopiesSupported)this.service.getSupportedAttributeValues(CopiesSupported.class, null, null);
         } catch (Exception var3) {
         }

         if (var1 != null) {
            int[][] var2 = var1.getMembers();
            if (var2 != null && var2.length > 0 && var2[0].length > 0) {
               this.maxCopies = var2[0][1];
            }
         }

         if (this.maxCopies == 0) {
            this.maxCopies = 999;
         }

         return this.maxCopies;
      }
   }

   @Override
   public PageRange defaultPageRange() {
      try {
         PageRanges var1 = (PageRanges)this.service.getDefaultAttributeValue(PageRanges.class);
         if (var1 == null) {
            return null;
         } else {
            int var2 = var1.getMembers()[0][0];
            int var3 = var1.getMembers()[0][1];
            return var2 == 1 && var3 == Integer.MAX_VALUE ? null : new PageRange(var2, var3);
         }
      } catch (Exception var4) {
         return null;
      }
   }

   @Override
   public boolean supportsPageRanges() {
      return true;
   }

   SheetCollate getDefaultSheetCollate() {
      SheetCollate var1 = null;

      try {
         var1 = (SheetCollate)this.service.getDefaultAttributeValue(SheetCollate.class);
      } catch (Exception var3) {
         var1 = SheetCollate.UNCOLLATED;
      }

      return var1;
   }

   @Override
   public Collation defaultCollation() {
      if (this.defaultCollation != null) {
         return this.defaultCollation;
      } else {
         SheetCollate var1 = this.getDefaultSheetCollate();
         this.defaultCollation = var1 == SheetCollate.COLLATED ? Collation.COLLATED : Collation.UNCOLLATED;
         return this.defaultCollation;
      }
   }

   @Override
   public Set<Collation> supportedCollations() {
      if (this.collateSet == null) {
         TreeSet var1 = new TreeSet();
         SheetCollate[] var2 = null;

         try {
            var2 = (SheetCollate[])this.service.getSupportedAttributeValues(SheetCollate.class, null, null);
         } catch (Exception var4) {
         }

         if (var2 != null) {
            for (int var3 = 0; var3 < var2.length; var3++) {
               if (var2[var3] == SheetCollate.UNCOLLATED) {
                  var1.add(Collation.UNCOLLATED);
               }

               if (var2[var3] == SheetCollate.COLLATED) {
                  var1.add(Collation.COLLATED);
               }
            }
         }

         this.collateSet = Collections.unmodifiableSet(var1);
      }

      return this.collateSet;
   }

   Chromaticity getDefaultChromaticity() {
      Chromaticity var1 = null;

      try {
         var1 = (Chromaticity)this.service.getDefaultAttributeValue(Chromaticity.class);
      } catch (Exception var3) {
         var1 = Chromaticity.COLOR;
      }

      return var1;
   }

   @Override
   public PrintColor defaultPrintColor() {
      if (this.defColor != null) {
         return this.defColor;
      } else {
         Chromaticity var1 = this.getDefaultChromaticity();
         this.defColor = var1 == Chromaticity.COLOR ? PrintColor.COLOR : PrintColor.MONOCHROME;
         return this.defColor;
      }
   }

   @Override
   public Set<PrintColor> supportedPrintColor() {
      if (this.colorSet == null) {
         TreeSet var1 = new TreeSet();
         Chromaticity[] var2 = null;

         try {
            var2 = (Chromaticity[])this.service.getSupportedAttributeValues(Chromaticity.class, null, null);
         } catch (Exception var4) {
         }

         if (var2 != null) {
            for (int var3 = 0; var3 < var2.length; var3++) {
               if (var2[var3] == Chromaticity.COLOR) {
                  var1.add(PrintColor.COLOR);
               }

               if (var2[var3] == Chromaticity.MONOCHROME) {
                  var1.add(PrintColor.MONOCHROME);
               }
            }
         }

         this.colorSet = Collections.unmodifiableSet(var1);
      }

      return this.colorSet;
   }

   @Override
   public PrintSides defaultSides() {
      if (this.defSides != null) {
         return this.defSides;
      } else {
         Sides var1 = (Sides)this.service.getDefaultAttributeValue(Sides.class);
         if (var1 == null || var1 == Sides.ONE_SIDED) {
            this.defSides = PrintSides.ONE_SIDED;
         } else if (var1 == Sides.DUPLEX) {
            this.defSides = PrintSides.DUPLEX;
         } else {
            this.defSides = PrintSides.TUMBLE;
         }

         return this.defSides;
      }
   }

   @Override
   public Set<PrintSides> supportedSides() {
      if (this.sidesSet == null) {
         TreeSet var1 = new TreeSet();
         Sides[] var2 = null;

         try {
            var2 = (Sides[])this.service.getSupportedAttributeValues(Sides.class, null, null);
         } catch (Exception var4) {
         }

         if (var2 != null) {
            for (int var3 = 0; var3 < var2.length; var3++) {
               if (var2[var3] == Sides.ONE_SIDED) {
                  var1.add(PrintSides.ONE_SIDED);
               }

               if (var2[var3] == Sides.DUPLEX) {
                  var1.add(PrintSides.DUPLEX);
               }

               if (var2[var3] == Sides.TUMBLE) {
                  var1.add(PrintSides.TUMBLE);
               }
            }
         }

         this.sidesSet = Collections.unmodifiableSet(var1);
      }

      return this.sidesSet;
   }

   static int getOrientID(PageOrientation var0) {
      if (var0 == PageOrientation.LANDSCAPE) {
         return 0;
      } else {
         return var0 == PageOrientation.REVERSE_LANDSCAPE ? 2 : 1;
      }
   }

   static OrientationRequested mapOrientation(PageOrientation var0) {
      if (var0 == PageOrientation.REVERSE_PORTRAIT) {
         return OrientationRequested.REVERSE_PORTRAIT;
      } else if (var0 == PageOrientation.LANDSCAPE) {
         return OrientationRequested.LANDSCAPE;
      } else {
         return var0 == PageOrientation.REVERSE_LANDSCAPE ? OrientationRequested.REVERSE_LANDSCAPE : OrientationRequested.PORTRAIT;
      }
   }

   static PageOrientation reverseMapOrientation(OrientationRequested var0) {
      if (var0 == OrientationRequested.REVERSE_PORTRAIT) {
         return PageOrientation.REVERSE_PORTRAIT;
      } else if (var0 == OrientationRequested.LANDSCAPE) {
         return PageOrientation.LANDSCAPE;
      } else {
         return var0 == OrientationRequested.REVERSE_LANDSCAPE ? PageOrientation.REVERSE_LANDSCAPE : PageOrientation.PORTRAIT;
      }
   }

   @Override
   public PageOrientation defaultOrientation() {
      if (this.defOrient == null) {
         OrientationRequested var1 = (OrientationRequested)this.service.getDefaultAttributeValue(OrientationRequested.class);
         this.defOrient = reverseMapOrientation(var1);
      }

      return this.defOrient;
   }

   @Override
   public Set<PageOrientation> supportedOrientation() {
      if (this.orientSet != null) {
         return this.orientSet;
      } else {
         TreeSet var1 = new TreeSet();
         OrientationRequested[] var2 = null;

         try {
            var2 = (OrientationRequested[])this.service.getSupportedAttributeValues(OrientationRequested.class, null, null);
         } catch (Exception var4) {
         }

         if (var2 != null && var2.length != 0) {
            for (int var3 = 0; var3 < var2.length; var3++) {
               if (var2[var3] == OrientationRequested.PORTRAIT) {
                  var1.add(PageOrientation.PORTRAIT);
               } else if (var2[var3] == OrientationRequested.REVERSE_PORTRAIT) {
                  var1.add(PageOrientation.REVERSE_PORTRAIT);
               } else if (var2[var3] == OrientationRequested.LANDSCAPE) {
                  var1.add(PageOrientation.LANDSCAPE);
               } else {
                  var1.add(PageOrientation.REVERSE_LANDSCAPE);
               }
            }
         } else {
            var1.add(this.defaultOrientation());
         }

         this.orientSet = Collections.unmodifiableSet(var1);
         return this.orientSet;
      }
   }

   PrinterResolution getDefaultPrinterResolution() {
      PrinterResolution var1 = (PrinterResolution)this.service.getDefaultAttributeValue(PrinterResolution.class);
      if (var1 == null) {
         var1 = new PrinterResolution(300, 300, 100);
      }

      return var1;
   }

   @Override
   public PrintResolution defaultPrintResolution() {
      if (this.defRes != null) {
         return this.defRes;
      } else {
         PrinterResolution var1 = this.getDefaultPrinterResolution();
         int var2 = var1.getCrossFeedResolution(100);
         int var3 = var1.getFeedResolution(100);
         this.defRes = PrintHelper.createPrintResolution(var2, var3);
         return this.defRes;
      }
   }

   @Override
   public Set<PrintResolution> supportedPrintResolution() {
      if (this.resSet != null) {
         return this.resSet;
      } else {
         TreeSet var1 = new TreeSet<>(J2DPrinter.PrintResolutionComparator.theComparator);
         PrinterResolution[] var2 = null;

         try {
            var2 = (PrinterResolution[])this.service.getSupportedAttributeValues(PrinterResolution.class, null, null);
         } catch (Exception var6) {
         }

         if (var2 != null && var2.length != 0) {
            for (int var3 = 0; var3 < var2.length; var3++) {
               int var4 = var2[var3].getCrossFeedResolution(100);
               int var5 = var2[var3].getFeedResolution(100);
               var1.add(PrintHelper.createPrintResolution(var4, var5));
            }
         } else {
            var1.add(this.defaultPrintResolution());
         }

         this.resSet = Collections.unmodifiableSet(var1);
         return this.resSet;
      }
   }

   javax.print.attribute.standard.PrintQuality getDefaultPrintQuality() {
      javax.print.attribute.standard.PrintQuality var1 = null;

      try {
         var1 = (javax.print.attribute.standard.PrintQuality)this.service.getDefaultAttributeValue(javax.print.attribute.standard.PrintQuality.class);
      } catch (Exception var3) {
         var1 = javax.print.attribute.standard.PrintQuality.NORMAL;
      }

      return var1;
   }

   @Override
   public PrintQuality defaultPrintQuality() {
      if (this.defQuality != null) {
         return this.defQuality;
      } else {
         javax.print.attribute.standard.PrintQuality var1 = this.getDefaultPrintQuality();
         if (var1 == javax.print.attribute.standard.PrintQuality.DRAFT) {
            this.defQuality = PrintQuality.DRAFT;
         } else if (var1 == javax.print.attribute.standard.PrintQuality.HIGH) {
            this.defQuality = PrintQuality.HIGH;
         } else {
            this.defQuality = PrintQuality.NORMAL;
         }

         return this.defQuality;
      }
   }

   @Override
   public Set<PrintQuality> supportedPrintQuality() {
      if (this.qualitySet == null) {
         TreeSet var1 = new TreeSet();
         javax.print.attribute.standard.PrintQuality[] var2 = null;

         try {
            var2 = (javax.print.attribute.standard.PrintQuality[])this.service
               .getSupportedAttributeValues(javax.print.attribute.standard.PrintQuality.class, null, null);
         } catch (Exception var4) {
         }

         if (var2 != null && var2.length != 0) {
            for (int var3 = 0; var3 < var2.length; var3++) {
               if (var2[var3] == javax.print.attribute.standard.PrintQuality.NORMAL) {
                  var1.add(PrintQuality.NORMAL);
               }

               if (var2[var3] == javax.print.attribute.standard.PrintQuality.DRAFT) {
                  var1.add(PrintQuality.DRAFT);
               }

               if (var2[var3] == javax.print.attribute.standard.PrintQuality.HIGH) {
                  var1.add(PrintQuality.HIGH);
               }
            }
         } else {
            var1.add(PrintQuality.NORMAL);
         }

         this.qualitySet = Collections.unmodifiableSet(var1);
      }

      return this.qualitySet;
   }

   Paper getPaperForMedia(Media var1) {
      this.populateMedia();
      return var1 != null && var1 instanceof MediaSizeName ? this.getPaper((MediaSizeName)var1) : this.defaultPaper();
   }

   @Override
   public Paper defaultPaper() {
      if (this.defPaper != null) {
         return this.defPaper;
      } else {
         Media var1 = (Media)this.service.getDefaultAttributeValue(Media.class);
         if (var1 != null && var1 instanceof MediaSizeName) {
            this.defPaper = this.getPaper((MediaSizeName)var1);
         } else {
            this.defPaper = Paper.NA_LETTER;
         }

         return this.defPaper;
      }
   }

   @Override
   public Set<Paper> supportedPapers() {
      if (this.paperSet == null) {
         this.populateMedia();
      }

      return this.paperSet;
   }

   private static void initPredefinedMediaMaps() {
      if (predefinedPaperMap == null) {
         predefinedPaperMap = Map.ofEntries(
            Map.entry(MediaSizeName.NA_LETTER, Paper.NA_LETTER),
            Map.entry(MediaSizeName.TABLOID, Paper.TABLOID),
            Map.entry(MediaSizeName.NA_LEGAL, Paper.LEGAL),
            Map.entry(MediaSizeName.EXECUTIVE, Paper.EXECUTIVE),
            Map.entry(MediaSizeName.NA_8X10, Paper.NA_8X10),
            Map.entry(MediaSizeName.MONARCH_ENVELOPE, Paper.MONARCH_ENVELOPE),
            Map.entry(MediaSizeName.NA_NUMBER_10_ENVELOPE, Paper.NA_NUMBER_10_ENVELOPE),
            Map.entry(MediaSizeName.ISO_A0, Paper.A0),
            Map.entry(MediaSizeName.ISO_A1, Paper.A1),
            Map.entry(MediaSizeName.ISO_A2, Paper.A2),
            Map.entry(MediaSizeName.ISO_A3, Paper.A3),
            Map.entry(MediaSizeName.ISO_A4, Paper.A4),
            Map.entry(MediaSizeName.ISO_A5, Paper.A5),
            Map.entry(MediaSizeName.ISO_A6, Paper.A6),
            Map.entry(MediaSizeName.C, Paper.C),
            Map.entry(MediaSizeName.ISO_DESIGNATED_LONG, Paper.DESIGNATED_LONG),
            Map.entry(MediaSizeName.JIS_B4, Paper.JIS_B4),
            Map.entry(MediaSizeName.JIS_B5, Paper.JIS_B5),
            Map.entry(MediaSizeName.JIS_B6, Paper.JIS_B6),
            Map.entry(MediaSizeName.JAPANESE_POSTCARD, Paper.JAPANESE_POSTCARD)
         );
      }

      if (predefinedTrayMap == null) {
         predefinedTrayMap = Map.of(
            MediaTray.MAIN,
            PaperSource.MAIN,
            MediaTray.MANUAL,
            PaperSource.MANUAL,
            MediaTray.BOTTOM,
            PaperSource.BOTTOM,
            MediaTray.MIDDLE,
            PaperSource.MIDDLE,
            MediaTray.TOP,
            PaperSource.TOP,
            MediaTray.SIDE,
            PaperSource.SIDE,
            MediaTray.ENVELOPE,
            PaperSource.ENVELOPE,
            MediaTray.LARGE_CAPACITY,
            PaperSource.LARGE_CAPACITY
         );
      }
   }

   private void populateMedia() {
      initPredefinedMediaMaps();
      if (this.paperSet == null) {
         Media[] var1 = (Media[])this.service.getSupportedAttributeValues(Media.class, null, null);
         TreeSet var2 = new TreeSet<>(J2DPrinter.PaperComparator.theComparator);
         TreeSet var3 = new TreeSet<>(J2DPrinter.PaperSourceComparator.theComparator);
         if (var1 != null) {
            for (int var4 = 0; var4 < var1.length; var4++) {
               Media var5 = var1[var4];
               if (var5 instanceof MediaSizeName) {
                  Paper var6 = this.addPaper((MediaSizeName)var5);
                  if (var6 != null) {
                     var2.add(var6);
                  }
               } else if (var5 instanceof MediaTray) {
                  var3.add(this.addPaperSource((MediaTray)var5));
               }
            }
         }

         this.paperSet = Collections.unmodifiableSet(var2);
         this.paperSourceSet = Collections.unmodifiableSet(var3);
      }
   }

   @Override
   public PaperSource defaultPaperSource() {
      if (this.defPaperSource != null) {
         return this.defPaperSource;
      } else {
         this.defPaperSource = PaperSource.AUTOMATIC;
         return this.defPaperSource;
      }
   }

   @Override
   public Set<PaperSource> supportedPaperSources() {
      if (this.paperSourceSet == null) {
         this.populateMedia();
      }

      return this.paperSourceSet;
   }

   final synchronized PaperSource getPaperSource(MediaTray var1) {
      if (this.paperSourceSet == null) {
         this.populateMedia();
      }

      PaperSource var2 = this.trayToSourceMap.get(var1);
      return var2 != null ? var2 : this.addPaperSource(var1);
   }

   MediaTray getTrayForPaperSource(PaperSource var1) {
      if (this.paperSourceSet == null) {
         this.populateMedia();
      }

      return this.sourceToTrayMap.get(var1);
   }

   private final synchronized PaperSource addPaperSource(MediaTray var1) {
      PaperSource var2 = predefinedTrayMap.get(var1);
      if (var2 == null) {
         var2 = PrintHelper.createPaperSource(var1.toString());
      }

      if (this.trayToSourceMap == null) {
         this.trayToSourceMap = new HashMap<>();
      }

      this.trayToSourceMap.put(var1, var2);
      if (this.sourceToTrayMap == null) {
         this.sourceToTrayMap = new HashMap<>();
      }

      this.sourceToTrayMap.put(var2, var1);
      return var2;
   }

   private Paper createPaper(MediaSizeName var1) {
      Paper var2 = null;
      MediaSize var3 = MediaSize.getMediaSizeForName(var1);
      if (var3 != null) {
         double var4 = (double)var3.getX(1) / 1000.0;
         double var6 = (double)var3.getY(1) / 1000.0;
         var2 = PrintHelper.createPaper(var1.toString(), var4, var6, Units.MM);
      }

      return var2;
   }

   private final synchronized Paper addPaper(MediaSizeName var1) {
      Paper var2 = predefinedPaperMap.get(var1);
      if (var2 == null) {
         var2 = this.createPaper(var1);
      }

      if (var2 == null) {
         Media var3 = (Media)this.service.getDefaultAttributeValue(Media.class);
         if (var3 instanceof MediaSizeName) {
            var2 = this.createPaper((MediaSizeName)var3);
         }
      }

      if (var2 != null) {
         this.paperToMediaMap.put(var2, var1);
         this.mediaToPaperMap.put(var1, var2);
      }

      return var2;
   }

   private Paper getPaper(MediaSizeName var1) {
      this.populateMedia();
      Paper var2 = this.mediaToPaperMap.get(var1);
      if (var2 == null) {
         var2 = Paper.NA_LETTER;
      }

      return var2;
   }

   private MediaSizeName getMediaSizeName(Paper var1) {
      this.populateMedia();
      MediaSizeName var2 = this.paperToMediaMap.get(var1);
      if (var2 == null) {
         var2 = MediaSize.findMedia((float)var1.getWidth(), (float)var1.getHeight(), 352);
      }

      return var2;
   }

   @Override
   public Rectangle2D printableArea(Paper var1) {
      Rectangle2D var2 = null;
      MediaSizeName var3 = this.getMediaSizeName(var1);
      if (var3 != null) {
         HashPrintRequestAttributeSet var4 = new HashPrintRequestAttributeSet();
         var4.add(var3);
         MediaPrintableArea[] var5 = (MediaPrintableArea[])this.service.getSupportedAttributeValues(MediaPrintableArea.class, null, var4);
         if (var5 != null && var5.length > 0 && var5[0] != null) {
            short var6 = 25400;
            var2 = new Rectangle2D((double)var5[0].getX(var6), (double)var5[0].getY(var6), (double)var5[0].getWidth(var6), (double)var5[0].getHeight(var6));
         }
      }

      if (var2 == null) {
         double var16 = var1.getWidth() / 72.0;
         double var17 = var1.getHeight() / 72.0;
         double var8;
         if (var16 < 3.0) {
            var8 = 0.75 * var16;
         } else {
            var8 = var16 - 1.5;
         }

         double var10;
         if (var17 < 3.0) {
            var10 = 0.75 * var17;
         } else {
            var10 = var17 - 1.5;
         }

         double var12 = (var16 - var8) / 2.0;
         double var14 = (var17 - var10) / 2.0;
         var2 = new Rectangle2D(var12, var14, var8, var10);
      }

      return var2;
   }

   PageLayout defaultPageLayout() {
      if (this.defaultLayout == null) {
         Paper var1 = this.defaultPaper();
         PageOrientation var2 = this.defaultOrientation();
         this.defaultLayout = this.fxPrinter.createPageLayout(var1, var2, Printer.MarginType.DEFAULT);
      }

      return this.defaultLayout;
   }

   private static class PaperComparator implements Comparator<Paper> {
      static final J2DPrinter.PaperComparator theComparator = new J2DPrinter.PaperComparator();

      public int compare(Paper var1, Paper var2) {
         return var1.getName().compareTo(var2.getName());
      }
   }

   private static class PaperSourceComparator implements Comparator<PaperSource> {
      static final J2DPrinter.PaperSourceComparator theComparator = new J2DPrinter.PaperSourceComparator();

      public int compare(PaperSource var1, PaperSource var2) {
         return var1.getName().compareTo(var2.getName());
      }
   }

   private static class PrintResolutionComparator implements Comparator<PrintResolution> {
      static final J2DPrinter.PrintResolutionComparator theComparator = new J2DPrinter.PrintResolutionComparator();

      public int compare(PrintResolution var1, PrintResolution var2) {
         long var3 = (long)(var1.getCrossFeedResolution() * var1.getFeedResolution());
         long var5 = (long)(var2.getCrossFeedResolution() * var2.getFeedResolution());
         if (var3 == var5) {
            return 0;
         } else {
            return var3 < var5 ? -1 : 1;
         }
      }
   }
}
