package org.girod.javafx.svgimage.xml.parsers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.geometry.Bounds;
import org.girod.javafx.svgimage.xml.specs.Viewport;

public class LengthParser {
   private static final Pattern NUMBER = Pattern.compile("\\-?\\d*(\\.\\d+)?");
   private static final Pattern NUMBER_UNIT = Pattern.compile("(\\-?\\d+)(\\.\\d*)?([a-z%A-Z]+)");
   private static final double INCH = 0.010416666666666666;
   private static final Viewport DEFAULT_VIEWPORT = new Viewport();

   private LengthParser() {
   }

   public static double parseLength(XMLNode node, Viewport viewport, String attrName) {
      return parseLength(node, true, null, viewport, attrName);
   }

   public static double parseLength(XMLNode node, boolean isWidth, Viewport viewport, String attrName) {
      String valueAsString = node.getAttributeValue(attrName);
      return valueAsString != null ? parseLength(valueAsString, isWidth, null, viewport) : 0.0;
   }

   public static double parseLength(XMLNode node, boolean isWidth, Bounds bounds, Viewport viewport, String attrName) {
      String valueAsString = node.getAttributeValue(attrName);
      return valueAsString != null ? parseLength(valueAsString, isWidth, bounds, viewport) : 0.0;
   }

   public static double parseLength(String lengthValue, Viewport viewport) {
      return parseLength(lengthValue, true, null, viewport);
   }

   public static double parseLength(String lengthValue, boolean isWidth, Viewport viewport) {
      return parseLength(lengthValue, isWidth, null, viewport);
   }

   public static double parsePosition(String lengthValue, boolean isWidth, Bounds bounds, Viewport viewport) {
      if (viewport == null) {
         viewport = DEFAULT_VIEWPORT;
      }

      lengthValue = lengthValue.trim();
      lengthValue = lengthValue.replace('−', '-');
      Matcher m = NUMBER.matcher(lengthValue);
      if (m.matches()) {
         if (bounds == null) {
            return viewport.scalePosition(Double.parseDouble(lengthValue), isWidth);
         } else {
            return isWidth
               ? viewport.scalePosition(bounds.getMinX() + Double.parseDouble(lengthValue) * bounds.getWidth(), isWidth)
               : viewport.scalePosition(bounds.getMinY() + Double.parseDouble(lengthValue) * bounds.getHeight(), isWidth);
         }
      } else {
         m = NUMBER_UNIT.matcher(lengthValue);
         if (m.matches()) {
            String unitS = m.group(m.groupCount());
            String startDigits = m.group(1);
            String endDigit = null;
            if (m.groupCount() > 1) {
               endDigit = m.group(2);
            }

            double parsedValue;
            if (endDigit == null) {
               parsedValue = Double.parseDouble(startDigits);
            } else {
               parsedValue = Double.parseDouble(startDigits + "." + endDigit);
            }

            switch (unitS) {
               case "px":
                  return viewport.scalePosition(parsedValue, isWidth);
               case "pt":
                  return viewport.scalePosition(parsedValue / 0.010416666666666666 * 72.0 / 96.0, isWidth);
               case "in":
                  return viewport.scalePosition(parsedValue / 0.010416666666666666, isWidth);
               case "cm":
                  return viewport.scalePosition(parsedValue / 0.010416666666666666 * 72.0 / 243.84, isWidth);
               case "mm":
                  return viewport.scalePosition(parsedValue / 0.010416666666666666 * 72.0 / 2438.4, isWidth);
               case "%":
                  if (viewport == null) {
                     return 0.0;
                  } else {
                     if (isWidth) {
                        return viewport.scalePosition(parsedValue * viewport.getBestWidth() / 100.0, isWidth);
                     }

                     return viewport.scalePosition(parsedValue * viewport.getBestHeight() / 100.0, isWidth);
                  }
               default:
                  return parsedValue;
            }
         } else {
            return 0.0;
         }
      }
   }

   public static double parseLineWidth(String lengthValue, Viewport viewport) {
      if (viewport == null) {
         viewport = DEFAULT_VIEWPORT;
      }

      lengthValue = lengthValue.trim();
      lengthValue = lengthValue.replace('−', '-');
      Matcher m = NUMBER.matcher(lengthValue);
      if (m.matches()) {
         return viewport == null ? Double.parseDouble(lengthValue) : viewport.scaleLineWidth(Double.parseDouble(lengthValue));
      } else {
         m = NUMBER_UNIT.matcher(lengthValue);
         if (m.matches()) {
            String unitS = m.group(m.groupCount());
            String startDigits = m.group(1);
            String endDigit = null;
            if (m.groupCount() > 1) {
               endDigit = m.group(2);
            }

            double parsedValue;
            if (endDigit == null) {
               parsedValue = Double.parseDouble(startDigits);
            } else {
               parsedValue = Double.parseDouble(startDigits + endDigit);
            }

            switch (unitS) {
               case "px":
                  return viewport.scaleLineWidth(parsedValue);
               case "pt":
                  return viewport.scaleLineWidth(parsedValue * 96.0 / 72.0);
               case "in":
                  return viewport.scaleLineWidth(parsedValue / 0.010416666666666666);
               case "cm":
                  return viewport.scaleLineWidth(parsedValue / 0.010416666666666666 * 72.0 / 243.84);
               case "mm":
                  return viewport.scaleLineWidth(parsedValue / 0.010416666666666666 * 72.0 / 2438.4);
               case "%":
                  return viewport.scaleLineWidth(parsedValue * viewport.getBestWidth() / 100.0);
               default:
                  return viewport.scaleLineWidth(parsedValue);
            }
         } else {
            return 0.0;
         }
      }
   }

   public static double parseLength(String lengthValue, boolean isWidth, Bounds bounds, Viewport viewport) {
      if (viewport == null) {
         viewport = DEFAULT_VIEWPORT;
      }

      lengthValue = lengthValue.trim();
      lengthValue = lengthValue.replace('−', '-');
      Matcher m = NUMBER.matcher(lengthValue);
      if (m.matches()) {
         if (viewport == null) {
            return Double.parseDouble(lengthValue);
         } else if (bounds == null) {
            return viewport.scaleLength(Double.parseDouble(lengthValue));
         } else {
            return isWidth
               ? viewport.scaleLength(Double.parseDouble(lengthValue) * bounds.getWidth())
               : viewport.scaleLength(Double.parseDouble(lengthValue) * bounds.getHeight());
         }
      } else {
         m = NUMBER_UNIT.matcher(lengthValue);
         if (m.matches()) {
            String unitS = m.group(m.groupCount());
            String startDigits = m.group(1);
            String endDigit = null;
            if (m.groupCount() > 1) {
               endDigit = m.group(2);
            }

            double parsedValue;
            if (endDigit == null) {
               parsedValue = Double.parseDouble(startDigits);
            } else {
               parsedValue = Double.parseDouble(startDigits + endDigit);
            }

            switch (unitS) {
               case "px":
                  return viewport.scaleLength(parsedValue);
               case "pt":
                  return viewport.scaleLength(parsedValue * 96.0 / 72.0);
               case "em":
                  return viewport.scaleLength(parsedValue * 16.0);
               case "ex":
                  return viewport.scaleLength(parsedValue * 16.0 * 0.5);
               case "in":
                  return viewport.scaleLength(parsedValue / 0.010416666666666666);
               case "cm":
                  return viewport.scaleLength(parsedValue * 37.79527559055118);
               case "mm":
                  return viewport.scaleLength(parsedValue * 3.7795275590551185);
               case "%":
                  if (isWidth) {
                     return viewport.scaleLength(parsedValue * viewport.getBestWidth() / 100.0);
                  }

                  return viewport.scaleLength(parsedValue * viewport.getBestHeight() / 100.0);
               default:
                  return viewport.scaleLength(parsedValue);
            }
         } else {
            return 0.0;
         }
      }
   }
}
