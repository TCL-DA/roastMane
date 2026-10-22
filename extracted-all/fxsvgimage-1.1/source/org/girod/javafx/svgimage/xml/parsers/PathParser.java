package org.girod.javafx.svgimage.xml.parsers;

import java.util.ArrayList;
import java.util.List;
import javafx.scene.shape.SVGPath;
import org.girod.javafx.svgimage.xml.specs.Viewport;

public class PathParser extends AbstractPathParser {
   public List<SVGPath> parsePathContent(String content, Viewport viewport, boolean hasFill) {
      List<SVGPath> listPath = new ArrayList<>();
      short type = -1;
      int index = 0;
      boolean isFirst = true;
      StringBuilder buf = new StringBuilder();
      List<String> list = new ArrayList<>();
      list.add(content);

      for (int i = 0; i < list.size(); i++) {
         String tk = list.get(i);
         switch (tk) {
            case "m":
            case "M":
               if (!isFirst) {
                  SVGPath path = new SVGPath();
                  path.setContent(buf.toString());
                  listPath.add(path);
                  buf = new StringBuilder();
               }

               type = 0;
               index = -1;
               addPathCommand(buf, tk, isFirst);
               break;
            case "h":
            case "H":
               type = 3;
               index = -1;
               addPathCommand(buf, tk, isFirst);
               break;
            case "v":
            case "V":
               type = 4;
               index = -1;
               addPathCommand(buf, tk, isFirst);
               break;
            case "l":
            case "L":
               type = 2;
               index = -1;
               addPathCommand(buf, tk, isFirst);
               break;
            case "c":
            case "C":
               type = 5;
               index = -1;
               addPathCommand(buf, tk, isFirst);
               break;
            case "q":
            case "Q":
               type = 7;
               index = -1;
               addPathCommand(buf, tk, isFirst);
               break;
            case "s":
            case "S":
               type = 6;
               index = -1;
               addPathCommand(buf, tk, isFirst);
               break;
            case "t":
            case "T":
               type = 8;
               index = -1;
               addPathCommand(buf, tk, isFirst);
               break;
            case "a":
            case "A":
               type = 9;
               index = -1;
               addPathCommand(buf, tk, isFirst);
               break;
            case "z":
            case "Z":
               index = -1;
               addPathCommand(buf, tk, isFirst);
               type = 1;
               break;
            default:
               switch (type) {
                  case 0:
                  case 2:
                     if (++index == 2) {
                        index = 0;
                     }

                     if (index == 0) {
                        double dx = LengthParser.parseLength(tk, true, viewport);
                        addPathCommand(buf, dx);
                     } else if (index == 1) {
                        double dx = LengthParser.parseLength(tk, false, viewport);
                        addPathCommand(buf, dx);
                     } else {
                        addPathCommand(buf, tk);
                     }
                  case 1:
                  default:
                     break;
                  case 3: {
                     index++;
                     double d = LengthParser.parseLength(tk, true, viewport);
                     addPathCommand(buf, d);
                     break;
                  }
                  case 4: {
                     index++;
                     double d = LengthParser.parseLength(tk, false, viewport);
                     addPathCommand(buf, d);
                     break;
                  }
                  case 5:
                     if (++index == 6) {
                        index = 0;
                     }

                     if (index == 0 || index == 2 || index == 4) {
                        double dx = LengthParser.parseLength(tk, true, viewport);
                        addPathCommand(buf, dx);
                     } else if (index != 1 && index != 3 && index != 5) {
                        addPathCommand(buf, tk);
                     } else {
                        double dx = LengthParser.parseLength(tk, false, viewport);
                        addPathCommand(buf, dx);
                     }
                     break;
                  case 6:
                  case 7:
                     if (++index == 4) {
                        index = 0;
                     }

                     if (index == 0 || index == 2) {
                        double dx = LengthParser.parseLength(tk, true, viewport);
                        addPathCommand(buf, dx);
                     } else if (index != 1 && index != 3) {
                        addPathCommand(buf, tk);
                     } else {
                        double dx = LengthParser.parseLength(tk, false, viewport);
                        addPathCommand(buf, dx);
                     }
                     break;
                  case 8:
                     if (++index == 3) {
                        index = 0;
                     }

                     if (index == 0) {
                        double dx = LengthParser.parseLength(tk, true, viewport);
                        addPathCommand(buf, dx);
                     } else if (index == 1) {
                        double dx = LengthParser.parseLength(tk, false, viewport);
                        addPathCommand(buf, dx);
                     } else {
                        addPathCommand(buf, tk);
                     }
                     break;
                  case 9:
                     if (++index == 0) {
                        double dx = LengthParser.parseLength(tk, true, viewport);
                        addPathCommand(buf, dx);
                     } else if (index == 1) {
                        double dx = LengthParser.parseLength(tk, false, viewport);
                        addPathCommand(buf, dx);
                     } else if (index == 2) {
                        double dx = ParserUtils.parseDoubleProtected(tk);
                        addPathCommand(buf, dx);
                     } else if (index == 3 || index == 4) {
                        addPathCommand(buf, tk);
                     } else if (index == 5) {
                        double dx = LengthParser.parseLength(tk, true, viewport);
                        addPathCommand(buf, dx);
                     } else if (index == 6) {
                        double dx = LengthParser.parseLength(tk, false, viewport);
                        addPathCommand(buf, dx);
                     } else {
                        addPathCommand(buf, tk);
                     }
               }
         }

         isFirst = false;
      }

      if (!content.isEmpty()) {
         SVGPath path = new SVGPath();
         path.setContent(content);
         listPath.add(path);
      }

      if (listPath.isEmpty()) {
         listPath = null;
      }

      return listPath;
   }

   private static void addPathCommand(StringBuilder buf, double value) {
      buf.append(" ");
      buf.append(Double.toString(value));
   }

   private static void addPathCommand(StringBuilder buf, String command) {
      addPathCommand(buf, command, false);
   }

   private static void addPathCommand(StringBuilder buf, String command, boolean isFirst) {
      if (!isFirst) {
         buf.append(" ");
      }

      buf.append(command);
   }
}
