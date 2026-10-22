package org.girod.javafx.svgimage.xml.parsers;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import org.girod.javafx.svgimage.xml.specs.Viewport;

public class MarkerContextPathParser extends AbstractPathParser {
   public void parsePathContent(String content, Viewport viewport) {
      short type = -1;
      int index = 0;
      boolean isFirst = true;
      List<String> list = new ArrayList<>();
      StringTokenizer tok = new StringTokenizer(content, " ,");

      while (tok.hasMoreTokens()) {
         String tk = tok.nextToken();
         Matcher m = LETTER.matcher(tk);
         int offset = 0;
         String part = tk;

         while (true) {
            boolean found = m.find(offset);
            if (!found) {
               this.decomposePart(list, part);
               break;
            }

            int start = m.start();
            int end = m.end();
            if (start > 0) {
               String previousPart = tk.substring(offset, start);
               this.decomposePart(list, previousPart);
            }

            String letter = tk.substring(start, end);
            list.add(letter);
            offset = end;
            if (end >= tk.length()) {
               break;
            }

            part = tk.substring(end);
         }
      }

      for (int i = 0; i < list.size(); i++) {
         String tk = list.get(i);
         switch (tk) {
            case "m":
            case "M":
               type = 0;
               index = -1;
               break;
            case "h":
            case "H":
               type = 3;
               index = -1;
               break;
            case "v":
            case "V":
               type = 4;
               index = -1;
               break;
            case "l":
            case "L":
               type = 2;
               index = -1;
               break;
            case "c":
            case "C":
               type = 5;
               index = -1;
               break;
            case "q":
            case "Q":
               type = 7;
               index = -1;
               break;
            case "s":
            case "S":
               type = 6;
               index = -1;
               break;
            case "t":
            case "T":
               type = 8;
               index = -1;
               break;
            case "a":
            case "A":
               type = 9;
               index = -1;
               break;
            case "z":
            case "Z":
               index = -1;
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
                        double var33 = LengthParser.parseLength(tk, true, viewport);
                     } else if (index == 1) {
                        double var34 = LengthParser.parseLength(tk, false, viewport);
                     }
                  case 1:
                  default:
                     break;
                  case 3: {
                     index++;
                     double d = LengthParser.parseLength(tk, true, viewport);
                     break;
                  }
                  case 4: {
                     index++;
                     double d = LengthParser.parseLength(tk, false, viewport);
                     break;
                  }
                  case 5:
                     if (++index == 6) {
                        index = 0;
                     }

                     if (index == 0 || index == 2 || index == 4) {
                        double var30 = LengthParser.parseLength(tk, true, viewport);
                     } else if (index == 1 || index == 3 || index == 5) {
                        double var29 = LengthParser.parseLength(tk, false, viewport);
                     }
                     break;
                  case 6:
                  case 7:
                     if (++index == 4) {
                        index = 0;
                     }

                     if (index == 0 || index == 2) {
                        double var28 = LengthParser.parseLength(tk, true, viewport);
                     } else if (index == 1 || index == 3) {
                        double var27 = LengthParser.parseLength(tk, false, viewport);
                     }
                     break;
                  case 8:
                     if (++index == 3) {
                        index = 0;
                     }

                     if (index == 0) {
                        double var25 = LengthParser.parseLength(tk, true, viewport);
                     } else if (index == 1) {
                        double var26 = LengthParser.parseLength(tk, false, viewport);
                     }
                     break;
                  case 9:
                     if (++index == 0) {
                        double var20 = LengthParser.parseLength(tk, true, viewport);
                     } else if (index == 1) {
                        double var21 = LengthParser.parseLength(tk, false, viewport);
                     } else if (index == 2) {
                        double var22 = ParserUtils.parseDoubleProtected(tk);
                     } else if (index != 3 && index != 4) {
                        if (index == 5) {
                           double var23 = LengthParser.parseLength(tk, true, viewport);
                        } else if (index == 6) {
                           double var24 = LengthParser.parseLength(tk, false, viewport);
                        }
                     }
               }
         }

         isFirst = false;
      }
   }
}
