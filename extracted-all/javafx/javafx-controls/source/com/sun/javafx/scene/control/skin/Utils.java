package com.sun.javafx.scene.control.skin;

import com.sun.javafx.PlatformUtil;
import com.sun.javafx.scene.NodeHelper;
import com.sun.javafx.scene.control.ContextMenuContent;
import com.sun.javafx.scene.control.behavior.TextBinding;
import com.sun.javafx.scene.text.FontHelper;
import com.sun.javafx.scene.text.TextLayout;
import com.sun.javafx.tk.Toolkit;
import java.net.URL;
import java.text.Bidi;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;
import javafx.application.ConditionalFeature;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.geometry.HPos;
import javafx.geometry.Point2D;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.OverrunStyle;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.Mnemonic;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextBoundsType;

public class Utils {
   static final Text helper = new Text();
   static final double DEFAULT_WRAPPING_WIDTH = helper.getWrappingWidth();
   static final double DEFAULT_LINE_SPACING = helper.getLineSpacing();
   static final String DEFAULT_TEXT = helper.getText();
   static final TextBoundsType DEFAULT_BOUNDS_TYPE = helper.getBoundsType();
   static final TextLayout layout = Toolkit.getToolkit().getTextLayoutFactory().createLayout();

   public static double getAscent(Font var0, TextBoundsType var1) {
      layout.setContent("", FontHelper.getNativeFont(var0));
      layout.setWrapWidth(0.0F);
      layout.setLineSpacing(0.0F);
      if (var1 == TextBoundsType.LOGICAL_VERTICAL_CENTER) {
         layout.setBoundsType(16384);
      } else {
         layout.setBoundsType(0);
      }

      return (double)(-layout.getBounds().getMinY());
   }

   public static double getLineHeight(Font var0, TextBoundsType var1) {
      layout.setContent("", FontHelper.getNativeFont(var0));
      layout.setWrapWidth(0.0F);
      layout.setLineSpacing(0.0F);
      if (var1 == TextBoundsType.LOGICAL_VERTICAL_CENTER) {
         layout.setBoundsType(16384);
      } else {
         layout.setBoundsType(0);
      }

      return (double)layout.getLines()[0].getBounds().getHeight();
   }

   public static double computeTextWidth(Font var0, String var1, double var2) {
      layout.setContent(var1 != null ? var1 : "", FontHelper.getNativeFont(var0));
      layout.setWrapWidth((float)var2);
      return (double)layout.getBounds().getWidth();
   }

   public static double computeTextHeight(Font var0, String var1, double var2, TextBoundsType var4) {
      return computeTextHeight(var0, var1, var2, 0.0, var4);
   }

   public static double computeTextHeight(Font var0, String var1, double var2, double var4, TextBoundsType var6) {
      layout.setContent(var1 != null ? var1 : "", FontHelper.getNativeFont(var0));
      layout.setWrapWidth((float)var2);
      layout.setLineSpacing((float)var4);
      if (var6 == TextBoundsType.LOGICAL_VERTICAL_CENTER) {
         layout.setBoundsType(16384);
      } else {
         layout.setBoundsType(0);
      }

      return (double)layout.getBounds().getHeight();
   }

   public static Point2D computeMnemonicPosition(Font var0, String var1, int var2, double var3, double var5, boolean var7) {
      if (var0 != null && var1 != null && var2 >= 0 && var2 <= var1.length()) {
         layout.setContent(var1, FontHelper.getNativeFont(var0));
         layout.setWrapWidth((float)var3);
         layout.setLineSpacing((float)var5);
         int var8 = 0;
         int var9 = 0;
         int var10 = layout.getLines().length;

         int var11;
         for (var11 = 0; var9 < var10; var9++) {
            var11 = layout.getLines()[var9].getLength();
            if (var2 >= var8 && var2 < var8 + var11) {
               break;
            }

            var8 += var11;
         }

         double var12 = (double)(layout.getBounds().getHeight() / (float)var10);
         double var14 = computeTextWidth(var0, var1.substring(var8, var2), 0.0);
         if (var7) {
            double var16 = computeTextWidth(var0, var1.substring(var8, var8 + var11 - 1), 0.0);
            var14 = var16 - var14;
         }

         double var18 = var12 * (double)(var9 + 1);
         if (var9 + 1 != var10) {
            var18 -= var5 / 2.0;
         }

         return new Point2D(var14, var18);
      } else {
         return null;
      }
   }

   public static int computeTruncationIndex(Font var0, String var1, double var2) {
      helper.setText(var1);
      helper.setFont(var0);
      helper.setWrappingWidth(0.0);
      helper.setLineSpacing(0.0);
      Bounds var4 = helper.getLayoutBounds();
      Point2D var5 = new Point2D(var2 - 2.0, var4.getMinY() + var4.getHeight() / 2.0);
      int var6 = helper.hitTest(var5).getCharIndex();
      helper.setWrappingWidth(DEFAULT_WRAPPING_WIDTH);
      helper.setLineSpacing(DEFAULT_LINE_SPACING);
      helper.setText(DEFAULT_TEXT);
      return var6;
   }

   public static String computeClippedText(Font var0, String var1, double var2, OverrunStyle var4, String var5) {
      if (var0 == null) {
         throw new IllegalArgumentException("Must specify a font");
      } else {
         OverrunStyle var6 = var4 != null && var4 != OverrunStyle.CLIP ? var4 : OverrunStyle.ELLIPSIS;
         String var7 = var4 == OverrunStyle.CLIP ? "" : var5;
         if (var1 != null && !"".equals(var1)) {
            double var8 = computeTextWidth(var0, var1, 0.0);
            if (var8 - var2 < 0.001F) {
               return var1;
            } else {
               double var10 = computeTextWidth(var0, var7, 0.0);
               double var12 = var2 - var10;
               if (var2 < var10) {
                  return "";
               } else if (var6 != OverrunStyle.ELLIPSIS
                  && var6 != OverrunStyle.WORD_ELLIPSIS
                  && var6 != OverrunStyle.LEADING_ELLIPSIS
                  && var6 != OverrunStyle.LEADING_WORD_ELLIPSIS) {
                  int var26 = 0;
                  int var28 = 0;
                  int var32 = -1;
                  int var17 = -1;
                  var26 = -1;
                  var28 = -1;
                  double var33 = 0.0;

                  for (int var38 = 0; var38 <= var1.length() - 1; var38++) {
                     char var40 = var1.charAt(var38);
                     var33 += computeTextWidth(var0, var40 + "", 0.0);
                     if (var33 > var12) {
                        break;
                     }

                     var26 = var38;
                     if (Character.isWhitespace(var40)) {
                        var32 = var38;
                     }

                     int var43 = var1.length() - 1 - var38;
                     var40 = var1.charAt(var43);
                     var33 += computeTextWidth(var0, var40 + "", 0.0);
                     if (var33 > var12) {
                        break;
                     }

                     var28 = var43;
                     if (Character.isWhitespace(var40)) {
                        var17 = var43;
                     }
                  }

                  if (var26 < 0) {
                     return var7;
                  } else if (var6 != OverrunStyle.CENTER_ELLIPSIS) {
                     boolean var35 = Character.isWhitespace(var1.charAt(var26 + 1));
                     int var36 = var32 != -1 && !var35 ? var32 : var26 + 1;
                     String var39 = var1.substring(0, var36);
                     if (var28 < 0) {
                        return var39 + var7;
                     } else {
                        boolean var42 = Character.isWhitespace(var1.charAt(var28 - 1));
                        var36 = var17 != -1 && !var42 ? var17 + 1 : var28;
                        String var44 = var1.substring(var36);
                        return var39 + var7 + var44;
                     }
                  } else {
                     return var28 < 0 ? var1.substring(0, var26 + 1) + var7 : var1.substring(0, var26 + 1) + var7 + var1.substring(var28);
                  }
               } else {
                  boolean var14 = var6 == OverrunStyle.WORD_ELLIPSIS || var6 == OverrunStyle.LEADING_WORD_ELLIPSIS;
                  if (var6 == OverrunStyle.ELLIPSIS && !new Bidi(var1, 0).isMixed()) {
                     int var31 = computeTruncationIndex(var0, var1, var2 - var10);
                     return var31 >= 0 && var31 < var1.length() ? var1.substring(0, var31) + var7 : var1;
                  } else {
                     double var16 = 0.0;
                     int var18 = -1;
                     int var19 = 0;
                     int var20 = var6 != OverrunStyle.LEADING_ELLIPSIS && var6 != OverrunStyle.LEADING_WORD_ELLIPSIS ? 0 : var1.length() - 1;
                     int var21 = var20 == 0 ? var1.length() - 1 : 0;
                     int var22 = var20 == 0 ? 1 : -1;
                     boolean var23 = var20 == 0 ? var20 > var21 : var20 < var21;

                     for (int var24 = var20; !var23; var24 += var22) {
                        var19 = var24;
                        char var25 = var1.charAt(var24);
                        var16 = computeTextWidth(var0, var20 == 0 ? var1.substring(0, var24 + 1) : var1.substring(var24, var20 + 1), 0.0);
                        if (Character.isWhitespace(var25)) {
                           var18 = var24;
                        }

                        if (var16 > var12) {
                           break;
                        }

                        var23 = var20 == 0 ? var24 >= var21 : var24 <= var21;
                     }

                     boolean var45 = !var14 || var18 == -1;
                     String var15 = var20 == 0 ? var1.substring(0, var45 ? var19 : var18) : var1.substring((var45 ? var19 : var18) + 1);

                     assert !var1.equals(var15);

                     return var6 != OverrunStyle.ELLIPSIS && var6 != OverrunStyle.WORD_ELLIPSIS ? var7 + var15 : var15 + var7;
                  }
               }
            }
         } else {
            return var1;
         }
      }
   }

   public static String computeClippedWrappedText(
      Font var0, String var1, double var2, double var4, double var6, OverrunStyle var8, String var9, TextBoundsType var10
   ) {
      if (var0 == null) {
         throw new IllegalArgumentException("Must specify a font");
      } else {
         var4 += var6;
         String var11 = var8 == OverrunStyle.CLIP ? "" : var9;
         int var12 = var11.length();
         double var13 = computeTextWidth(var0, var11, 0.0);
         double var15 = computeTextHeight(var0, var11, 0.0, var6, var10);
         if (!(var2 < var13) && !(var4 < var15)) {
            helper.setText(var1);
            helper.setFont(var0);
            helper.setWrappingWidth((double)((int)Math.ceil(var2)));
            helper.setBoundsType(var10);
            helper.setLineSpacing(var6);
            boolean var17 = var8 == OverrunStyle.LEADING_ELLIPSIS || var8 == OverrunStyle.LEADING_WORD_ELLIPSIS;
            boolean var18 = var8 == OverrunStyle.CENTER_ELLIPSIS || var8 == OverrunStyle.CENTER_WORD_ELLIPSIS;
            boolean var19 = !var17 && !var18;
            boolean var20 = var8 == OverrunStyle.WORD_ELLIPSIS || var8 == OverrunStyle.LEADING_WORD_ELLIPSIS || var8 == OverrunStyle.CENTER_WORD_ELLIPSIS;
            String var21 = var1;
            int var22 = var1 != null ? var1.length() : 0;
            int var23 = -1;
            Point2D var24 = null;
            if (var18) {
               var24 = new Point2D((var2 - var13) / 2.0, var4 / 2.0 - helper.getBaselineOffset());
            }

            Point2D var25 = new Point2D(0.0, var4 - helper.getBaselineOffset());
            int var26 = helper.hitTest(var25).getCharIndex();
            if (var26 >= var22) {
               helper.setBoundsType(TextBoundsType.LOGICAL);
               return var1;
            } else {
               if (var18) {
                  var26 = helper.hitTest(var24).getCharIndex();
               }

               if (var26 > 0 && var26 < var22) {
                  if (var18 || var19) {
                     int var27 = var26;
                     if (var18) {
                        if (var20) {
                           int var28 = lastBreakCharIndex(var1, var26 + 1);
                           if (var28 >= 0) {
                              var27 = var28 + 1;
                           } else {
                              var28 = firstBreakCharIndex(var1, var26);
                              if (var28 >= 0) {
                                 var27 = var28 + 1;
                              }
                           }
                        }

                        var23 = var27 + var12;
                     }

                     var21 = var1.substring(0, var27) + var11;
                  }

                  if (var17 || var18) {
                     int var31 = Math.max(0, var22 - var26 - 10);
                     if (var31 > 0 && var20) {
                        int var34 = lastBreakCharIndex(var1, var31 + 1);
                        if (var34 >= 0) {
                           var31 = var34 + 1;
                        } else {
                           var34 = firstBreakCharIndex(var1, var31);
                           if (var34 >= 0) {
                              var31 = var34 + 1;
                           }
                        }
                     }

                     if (var18) {
                        var21 = var21 + var1.substring(var31);
                     } else {
                        var21 = var11 + var1.substring(var31);
                     }
                  }

                  while (true) {
                     helper.setText(var21);
                     int var32 = helper.hitTest(var25).getCharIndex();
                     if (var18 && var32 < var23) {
                        if (var32 > 0 && var21.charAt(var32 - 1) == '\n') {
                           var32--;
                        }

                        var21 = var1.substring(0, var32) + var11;
                        break;
                     }

                     if (var32 <= 0 || var32 >= var21.length()) {
                        break;
                     }

                     if (var17) {
                        int var36 = var12 + 1;
                        if (var20) {
                           int var29 = firstBreakCharIndex(var21, var36);
                           if (var29 >= 0) {
                              var36 = var29 + 1;
                           }
                        }

                        var21 = var11 + var21.substring(var36);
                     } else if (var18) {
                        int var37 = var23 + 1;
                        if (var20) {
                           int var39 = firstBreakCharIndex(var21, var37);
                           if (var39 >= 0) {
                              var37 = var39 + 1;
                           }
                        }

                        var21 = var21.substring(0, var23) + var21.substring(var37);
                     } else {
                        int var38 = var21.length() - var12 - 1;
                        if (var20) {
                           int var40 = lastBreakCharIndex(var21, var38);
                           if (var40 >= 0) {
                              var38 = var40;
                           }
                        }

                        var21 = var21.substring(0, var38) + var11;
                     }
                  }
               }

               helper.setWrappingWidth(DEFAULT_WRAPPING_WIDTH);
               helper.setLineSpacing(DEFAULT_LINE_SPACING);
               helper.setText(DEFAULT_TEXT);
               helper.setBoundsType(DEFAULT_BOUNDS_TYPE);
               return var21;
            }
         } else {
            return var1;
         }
      }
   }

   private static int firstBreakCharIndex(String var0, int var1) {
      char[] var2 = var0.toCharArray();

      for (int var3 = var1; var3 < var2.length; var3++) {
         if (isPreferredBreakCharacter(var2[var3])) {
            return var3;
         }
      }

      return -1;
   }

   private static int lastBreakCharIndex(String var0, int var1) {
      char[] var2 = var0.toCharArray();

      for (int var3 = var1; var3 >= 0; var3--) {
         if (isPreferredBreakCharacter(var2[var3])) {
            return var3;
         }
      }

      return -1;
   }

   private static boolean isPreferredBreakCharacter(char var0) {
      if (Character.isWhitespace(var0)) {
         return true;
      } else {
         switch (var0) {
            case '.':
            case ':':
            case ';':
               return true;
            default:
               return false;
         }
      }
   }

   private static boolean requiresComplexLayout(Font var0, String var1) {
      return false;
   }

   static int computeStartOfWord(Font var0, String var1, int var2) {
      if (!"".equals(var1) && var2 >= 0) {
         if (var1.length() <= var2) {
            return var1.length();
         } else if (Character.isWhitespace(var1.charAt(var2))) {
            return var2;
         } else {
            boolean var3 = requiresComplexLayout(var0, var1);
            if (var3) {
               return 0;
            } else {
               int var4 = var2;

               while (--var4 >= 0) {
                  if (Character.isWhitespace(var1.charAt(var4))) {
                     return var4 + 1;
                  }
               }

               return 0;
            }
         }
      } else {
         return 0;
      }
   }

   static int computeEndOfWord(Font var0, String var1, int var2) {
      if (!var1.equals("") && var2 >= 0) {
         if (var1.length() <= var2) {
            return var1.length();
         } else if (Character.isWhitespace(var1.charAt(var2))) {
            return var2;
         } else {
            boolean var3 = requiresComplexLayout(var0, var1);
            if (var3) {
               return var1.length();
            } else {
               int var4 = var2;

               while (++var4 < var1.length()) {
                  if (Character.isWhitespace(var1.charAt(var4))) {
                     return var4;
                  }
               }

               return var1.length();
            }
         }
      } else {
         return 0;
      }
   }

   public static double boundedSize(double var0, double var2, double var4) {
      return Math.min(Math.max(var0, var2), Math.max(var2, var4));
   }

   public static void addMnemonics(ContextMenu var0, Scene var1) {
      addMnemonics(var0, var1, false);
   }

   public static void addMnemonics(ContextMenu var0, Scene var1, boolean var2) {
      addMnemonics(var0, var1, var2, null);
   }

   public static void addMnemonics(ContextMenu var0, Scene var1, boolean var2, List<Mnemonic> var3) {
      if (!PlatformUtil.isMac()) {
         ContextMenuContent var4 = (ContextMenuContent)var0.getSkin().getNode();

         for (int var6 = 0; var6 < var0.getItems().size(); var6++) {
            MenuItem var5 = (MenuItem)var0.getItems().get(var6);
            if (var5.isMnemonicParsing()) {
               TextBinding var7 = new TextBinding(var5.getText());
               int var8 = var7.getMnemonicIndex();
               if (var8 >= 0) {
                  KeyCombination var9 = var7.getMnemonicKeyCombination();
                  Mnemonic var10 = new Mnemonic(var4.getLabelAt(var6), var9);
                  var1.addMnemonic(var10);
                  NodeHelper.setShowMnemonics(var4.getLabelAt(var6), var2);
                  if (var3 != null) {
                     var3.add(var10);
                  }
               }
            }
         }
      }
   }

   public static void removeMnemonics(ContextMenu var0, Scene var1) {
      if (!PlatformUtil.isMac()) {
         ContextMenuContent var2 = (ContextMenuContent)var0.getSkin().getNode();

         for (int var4 = 0; var4 < var0.getItems().size(); var4++) {
            MenuItem var3 = (MenuItem)var0.getItems().get(var4);
            if (var3.isMnemonicParsing()) {
               TextBinding var5 = new TextBinding(var3.getText());
               int var6 = var5.getMnemonicIndex();
               if (var6 >= 0) {
                  KeyCombination var7 = var5.getMnemonicKeyCombination();
                  ObservableList var8 = (ObservableList)var1.getMnemonics().get(var7);
                  if (var8 != null) {
                     for (int var9 = 0; var9 < var8.size(); var9++) {
                        if (((Mnemonic)var8.get(var9)).getNode() == var2.getLabelAt(var4)) {
                           var8.remove(var9);
                        }
                     }
                  }
               }
            }
         }
      }
   }

   public static double computeXOffset(double var0, double var2, HPos var4) {
      if (var4 == null) {
         return 0.0;
      } else {
         switch (var4) {
            case LEFT:
               return 0.0;
            case CENTER:
               return (var0 - var2) / 2.0;
            case RIGHT:
               return var0 - var2;
            default:
               return 0.0;
         }
      }
   }

   public static double computeYOffset(double var0, double var2, VPos var4) {
      if (var4 == null) {
         return 0.0;
      } else {
         switch (var4) {
            case TOP:
               return 0.0;
            case CENTER:
               return (var0 - var2) / 2.0;
            case BOTTOM:
               return var0 - var2;
            default:
               return 0.0;
         }
      }
   }

   public static boolean isTwoLevelFocus() {
      return Platform.isSupported(ConditionalFeature.TWO_LEVEL_FOCUS);
   }

   public static <T> void executeOnceWhenPropertyIsNonNull(final ObservableValue<T> var0, final Consumer<T> var1) {
      if (var0 != null) {
         Object var2 = var0.getValue();
         if (var2 != null) {
            var1.accept(var2);
         } else {
            InvalidationListener var3 = new InvalidationListener() {
               public void invalidated(Observable var1x) {
                  Object var2 = var0.getValue();
                  if (var2 != null) {
                     var0.removeListener(this);
                     var1.accept(var2);
                  }
               }
            };
            var0.addListener(var3);
         }
      }
   }

   public static String formatHexString(Color var0) {
      return var0 != null
         ? String.format(
            (Locale)null, "#%02x%02x%02x", Math.round(var0.getRed() * 255.0), Math.round(var0.getGreen() * 255.0), Math.round(var0.getBlue() * 255.0)
         )
         : null;
   }

   public static URL getResource(String var0) {
      return Utils.class.getResource(var0);
   }
}
