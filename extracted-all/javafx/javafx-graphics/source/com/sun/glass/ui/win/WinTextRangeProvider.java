package com.sun.glass.ui.win;

import java.text.BreakIterator;
import javafx.geometry.Bounds;
import javafx.scene.AccessibleAction;
import javafx.scene.AccessibleAttribute;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

class WinTextRangeProvider {
   private static final int TextPatternRangeEndpoint_Start = 0;
   private static final int TextPatternRangeEndpoint_End = 1;
   private static final int TextUnit_Character = 0;
   private static final int TextUnit_Format = 1;
   private static final int TextUnit_Word = 2;
   private static final int TextUnit_Line = 3;
   private static final int TextUnit_Paragraph = 4;
   private static final int TextUnit_Page = 5;
   private static final int TextUnit_Document = 6;
   private static final int UIA_FontNameAttributeId = 40005;
   private static final int UIA_FontSizeAttributeId = 40006;
   private static final int UIA_FontWeightAttributeId = 40007;
   private static final int UIA_IsHiddenAttributeId = 40013;
   private static final int UIA_IsItalicAttributeId = 40014;
   private static final int UIA_IsReadOnlyAttributeId = 40015;
   private static int idCount = 1;
   private int id;
   private int start;
   private int end;
   private WinAccessible accessible;
   private long peer;

   private static native void _initIDs();

   private native long _createTextRangeProvider(long var1);

   private native void _destroyTextRangeProvider(long var1);

   WinTextRangeProvider(WinAccessible var1) {
      this.accessible = var1;
      this.peer = this._createTextRangeProvider(var1.getNativeAccessible());
      this.id = idCount++;
   }

   long getNativeProvider() {
      return this.peer;
   }

   void dispose() {
      this._destroyTextRangeProvider(this.peer);
      this.peer = 0L;
   }

   void setRange(int var1, int var2) {
      this.start = var1;
      this.end = var2;
   }

   int getStart() {
      return this.start;
   }

   int getEnd() {
      return this.end;
   }

   @Override
   public String toString() {
      return "Range(start: " + this.start + ", end: " + this.end + ", id: " + this.id + ")";
   }

   private Object getAttribute(AccessibleAttribute var1, Object... var2) {
      return this.accessible.getAttribute(var1, var2);
   }

   private boolean isWordStart(BreakIterator var1, String var2, int var3) {
      if (var3 == 0) {
         return true;
      } else if (var3 == var2.length()) {
         return true;
      } else {
         return var3 == -1 ? true : var1.isBoundary(var3) && Character.isLetterOrDigit(var2.charAt(var3));
      }
   }

   private long Clone() {
      WinTextRangeProvider var1 = new WinTextRangeProvider(this.accessible);
      var1.setRange(this.start, this.end);
      return var1.getNativeProvider();
   }

   private boolean Compare(WinTextRangeProvider var1) {
      return var1 == null ? false : this.accessible == var1.accessible && this.start == var1.start && this.end == var1.end;
   }

   private int CompareEndpoints(int var1, WinTextRangeProvider var2, int var3) {
      int var4 = var1 == 0 ? this.start : this.end;
      int var5 = var3 == 0 ? var2.start : var2.end;
      return var4 - var5;
   }

   private void ExpandToEnclosingUnit(int var1) {
      String var2 = (String)this.getAttribute(AccessibleAttribute.TEXT);
      if (var2 != null) {
         int var3 = var2.length();
         if (var3 != 0) {
            switch (var1) {
               case 0:
                  if (this.start == var3) {
                     this.start--;
                  }

                  this.end = this.start + 1;
                  break;
               case 1:
               case 2:
                  BreakIterator var8 = BreakIterator.getWordInstance();
                  var8.setText(var2);
                  if (!this.isWordStart(var8, var2, this.start)) {
                     int var10 = var8.preceding(this.start);

                     while (!this.isWordStart(var8, var2, var10)) {
                        var10 = var8.previous();
                     }

                     this.start = var10 != -1 ? var10 : 0;
                  }

                  if (!this.isWordStart(var8, var2, this.end)) {
                     int var11 = var8.following(this.end);

                     while (!this.isWordStart(var8, var2, var11)) {
                        var11 = var8.next();
                     }

                     this.end = var11 != -1 ? var11 : var3;
                  }
                  break;
               case 3:
                  Integer var7 = (Integer)this.getAttribute(AccessibleAttribute.LINE_FOR_OFFSET, this.start);
                  Integer var9 = (Integer)this.getAttribute(AccessibleAttribute.LINE_START, var7);
                  Integer var13 = (Integer)this.getAttribute(AccessibleAttribute.LINE_END, var7);
                  if (var7 != null && var13 != null && var9 != null) {
                     this.start = var9;
                     this.end = var13;
                  } else {
                     this.start = 0;
                     this.end = var3;
                  }
                  break;
               case 4:
                  Integer var4 = (Integer)this.getAttribute(AccessibleAttribute.LINE_FOR_OFFSET, this.start);
                  if (var4 == null) {
                     this.start = 0;
                     this.end = var3;
                  } else {
                     BreakIterator var5 = BreakIterator.getSentenceInstance();
                     var5.setText(var2);
                     if (!var5.isBoundary(this.start)) {
                        int var6 = var5.preceding(this.start);
                        this.start = var6 != -1 ? var6 : 0;
                     }

                     int var12 = var5.following(this.start);
                     this.end = var12 != -1 ? var12 : var3;
                  }
                  break;
               case 5:
               case 6:
                  this.start = 0;
                  this.end = var3;
            }

            this.start = Math.max(0, Math.min(this.start, var3));
            this.end = Math.max(this.start, Math.min(this.end, var3));
         }
      }
   }

   private long FindAttribute(int var1, WinVariant var2, boolean var3) {
      System.err.println("FindAttribute NOT IMPLEMENTED");
      return 0L;
   }

   private long FindText(String var1, boolean var2, boolean var3) {
      if (var1 == null) {
         return 0L;
      } else {
         String var4 = (String)this.getAttribute(AccessibleAttribute.TEXT);
         if (var4 == null) {
            return 0L;
         } else {
            String var5 = var4.substring(this.start, this.end);
            if (var3) {
               var5 = var5.toLowerCase();
               var1 = var1.toLowerCase();
            }

            int var6 = -1;
            if (var2) {
               var6 = var5.lastIndexOf(var1);
            } else {
               var6 = var5.indexOf(var1);
            }

            if (var6 == -1) {
               return 0L;
            } else {
               WinTextRangeProvider var7 = new WinTextRangeProvider(this.accessible);
               var7.setRange(this.start + var6, this.start + var6 + var1.length());
               return var7.getNativeProvider();
            }
         }
      }
   }

   private WinVariant GetAttributeValue(int var1) {
      WinVariant var2 = null;
      switch (var1) {
         case 40005:
            Font var7 = (Font)this.getAttribute(AccessibleAttribute.FONT);
            if (var7 != null) {
               var2 = new WinVariant();
               var2.vt = 8;
               var2.bstrVal = var7.getName();
            }
            break;
         case 40006:
            Font var6 = (Font)this.getAttribute(AccessibleAttribute.FONT);
            if (var6 != null) {
               var2 = new WinVariant();
               var2.vt = 5;
               var2.dblVal = var6.getSize();
            }
            break;
         case 40007:
            Font var5 = (Font)this.getAttribute(AccessibleAttribute.FONT);
            if (var5 != null) {
               boolean var8 = var5.getStyle().toLowerCase().contains("bold");
               var2 = new WinVariant();
               var2.vt = 3;
               var2.lVal = var8 ? FontWeight.BOLD.getWeight() : FontWeight.NORMAL.getWeight();
            }
         case 40008:
         case 40009:
         case 40010:
         case 40011:
         case 40012:
         default:
            break;
         case 40013:
         case 40015:
            var2 = new WinVariant();
            var2.vt = 11;
            var2.boolVal = false;
            break;
         case 40014:
            Font var3 = (Font)this.getAttribute(AccessibleAttribute.FONT);
            if (var3 != null) {
               boolean var4 = var3.getStyle().toLowerCase().contains("italic");
               var2 = new WinVariant();
               var2.vt = 11;
               var2.boolVal = var4;
            }
      }

      return var2;
   }

   private double[] GetBoundingRectangles() {
      String var1 = (String)this.getAttribute(AccessibleAttribute.TEXT);
      if (var1 == null) {
         return null;
      } else {
         int var2 = var1.length();
         if (var2 == 0) {
            return new double[0];
         } else {
            int var3 = this.end;
            if (var3 > 0 && var3 > this.start && var1.charAt(var3 - 1) == '\n') {
               var3--;
            }

            if (var3 > 0 && var3 > this.start && var1.charAt(var3 - 1) == '\r') {
               var3--;
            }

            if (var3 > 0 && var3 > this.start && var3 == var2) {
               var3--;
            }

            Bounds[] var4 = (Bounds[])this.getAttribute(AccessibleAttribute.BOUNDS_FOR_RANGE, this.start, var3);
            if (var4 == null) {
               return null;
            } else {
               double[] var5 = new double[var4.length * 4];
               int var6 = 0;

               for (int var7 = 0; var7 < var4.length; var7++) {
                  Bounds var8 = var4[var7];
                  var5[var6++] = var8.getMinX();
                  var5[var6++] = var8.getMinY();
                  var5[var6++] = var8.getWidth();
                  var5[var6++] = var8.getHeight();
               }

               return var5;
            }
         }
      }
   }

   private long GetEnclosingElement() {
      return this.accessible.getNativeAccessible();
   }

   private String GetText(int var1) {
      String var2 = (String)this.getAttribute(AccessibleAttribute.TEXT);
      if (var2 == null) {
         return null;
      } else {
         int var3 = var1 != -1 ? Math.min(this.end, this.start + var1) : this.end;
         return var2.substring(this.start, var3);
      }
   }

   private int Move(int var1, int var2) {
      if (var2 == 0) {
         return 0;
      } else {
         String var3 = (String)this.getAttribute(AccessibleAttribute.TEXT);
         if (var3 == null) {
            return 0;
         } else {
            int var4 = var3.length();
            if (var4 == 0) {
               return 0;
            } else {
               int var5 = 0;
               switch (var1) {
                  case 0:
                     int var12 = this.start;
                     this.start = Math.max(0, Math.min(this.start + var2, var4 - 1));
                     this.end = this.start + 1;
                     var5 = this.start - var12;
                     break;
                  case 1:
                  case 2:
                     BreakIterator var11 = BreakIterator.getWordInstance();
                     var11.setText(var3);
                     int var15 = this.start;

                     while (!this.isWordStart(var11, var3, var15)) {
                        var15 = var11.preceding(this.start);
                     }

                     while (var15 != -1 && var5 != var2) {
                        if (var2 > 0) {
                           var15 = var11.following(var15);

                           while (!this.isWordStart(var11, var3, var15)) {
                              var15 = var11.next();
                           }

                           var5++;
                        } else {
                           var15 = var11.preceding(var15);

                           while (!this.isWordStart(var11, var3, var15)) {
                              var15 = var11.previous();
                           }

                           var5--;
                        }
                     }

                     if (var5 != 0) {
                        if (var15 != -1) {
                           this.start = var15;
                        } else {
                           this.start = var2 > 0 ? var4 : 0;
                        }

                        var15 = var11.following(this.start);

                        while (!this.isWordStart(var11, var3, var15)) {
                           var15 = var11.next();
                        }

                        this.end = var15 != -1 ? var15 : var4;
                     }
                     break;
                  case 3:
                     Integer var10 = (Integer)this.getAttribute(AccessibleAttribute.LINE_FOR_OFFSET, this.start);
                     if (var10 == null) {
                        return 0;
                     }

                     for (int var14 = var2 > 0 ? 1 : -1;
                        var2 != var5 && this.getAttribute(AccessibleAttribute.LINE_START, var10 + var14) != null;
                        var5 += var14
                     ) {
                        var10 = var10 + var14;
                     }

                     if (var5 != 0) {
                        Integer var8 = (Integer)this.getAttribute(AccessibleAttribute.LINE_START, var10);
                        Integer var9 = (Integer)this.getAttribute(AccessibleAttribute.LINE_END, var10);
                        if (var8 == null || var9 == null) {
                           return 0;
                        }

                        this.start = var8;
                        this.end = var9;
                     }
                     break;
                  case 4:
                     BreakIterator var6 = BreakIterator.getSentenceInstance();
                     var6.setText(var3);
                     int var7 = var6.isBoundary(this.start) ? this.start : var6.preceding(this.start);

                     while (var7 != -1 && var5 != var2) {
                        if (var2 > 0) {
                           var7 = var6.following(var7);
                           var5++;
                        } else {
                           var7 = var6.preceding(var7);
                           var5--;
                        }
                     }

                     if (var5 != 0) {
                        this.start = var7 != -1 ? var7 : 0;
                        var7 = var6.following(this.start);
                        this.end = var7 != -1 ? var7 : var4;
                     }
                     break;
                  case 5:
                  case 6:
                     return 0;
               }

               this.start = Math.max(0, Math.min(this.start, var4));
               this.end = Math.max(this.start, Math.min(this.end, var4));
               return var5;
            }
         }
      }
   }

   private int MoveEndpointByUnit(int var1, int var2, int var3) {
      if (var3 == 0) {
         return 0;
      } else {
         String var4 = (String)this.getAttribute(AccessibleAttribute.TEXT);
         if (var4 == null) {
            return 0;
         } else {
            int var5 = var4.length();
            int var6 = 0;
            int var7 = var1 == 0 ? this.start : this.end;
            switch (var2) {
               case 0:
                  int var15 = var7;
                  var7 = Math.max(0, Math.min(var7 + var3, var5));
                  var6 = var7 - var15;
                  break;
               case 1:
               case 2:
                  BreakIterator var14 = BreakIterator.getWordInstance();
                  var14.setText(var4);

                  while (var7 != -1 && var6 != var3) {
                     if (var3 > 0) {
                        var7 = var14.following(var7);

                        while (!this.isWordStart(var14, var4, var7)) {
                           var7 = var14.next();
                        }

                        var6++;
                     } else {
                        var7 = var14.preceding(var7);

                        while (!this.isWordStart(var14, var4, var7)) {
                           var7 = var14.previous();
                        }

                        var6--;
                     }
                  }

                  if (var7 == -1) {
                     var7 = var3 > 0 ? var5 : 0;
                  }
                  break;
               case 3:
                  Integer var13 = (Integer)this.getAttribute(AccessibleAttribute.LINE_FOR_OFFSET, var7);
                  Integer var9 = (Integer)this.getAttribute(AccessibleAttribute.LINE_START, var13);
                  Integer var10 = (Integer)this.getAttribute(AccessibleAttribute.LINE_END, var13);
                  if (var13 != null && var9 != null && var10 != null) {
                     int var11 = var3 > 0 ? 1 : -1;
                     int var12 = var3 > 0 ? var10 : var9;
                     if (var7 != var12) {
                        var6 += var11;
                     }

                     while (var3 != var6 && this.getAttribute(AccessibleAttribute.LINE_START, var13 + var11) != null) {
                        var13 = var13 + var11;
                        var6 += var11;
                     }

                     if (var6 != 0) {
                        var9 = (Integer)this.getAttribute(AccessibleAttribute.LINE_START, var13);
                        var10 = (Integer)this.getAttribute(AccessibleAttribute.LINE_END, var13);
                        if (var9 == null || var10 == null) {
                           return 0;
                        }

                        var7 = var3 > 0 ? var10 : var9;
                     }
                  } else {
                     var7 = var3 > 0 ? var5 : 0;
                  }
                  break;
               case 4:
                  BreakIterator var8 = BreakIterator.getSentenceInstance();
                  var8.setText(var4);

                  while (var7 != -1 && var6 != var3) {
                     if (var3 > 0) {
                        var7 = var8.following(var7);
                        var6++;
                     } else {
                        var7 = var8.preceding(var7);
                        var6--;
                     }
                  }

                  if (var7 == -1) {
                     var7 = var3 > 0 ? var5 : 0;
                  }
                  break;
               case 5:
               case 6:
                  return 0;
            }

            if (var1 == 0) {
               this.start = var7;
            } else {
               this.end = var7;
            }

            if (this.start > this.end) {
               this.start = this.end = var7;
            }

            this.start = Math.max(0, Math.min(this.start, var5));
            this.end = Math.max(this.start, Math.min(this.end, var5));
            return var6;
         }
      }
   }

   private void MoveEndpointByRange(int var1, WinTextRangeProvider var2, int var3) {
      String var4 = (String)this.getAttribute(AccessibleAttribute.TEXT);
      if (var4 != null) {
         int var5 = var4.length();
         int var6 = var3 == 0 ? var2.start : var2.end;
         if (var1 == 0) {
            this.start = var6;
         } else {
            this.end = var6;
         }

         if (this.start > this.end) {
            this.start = this.end = var6;
         }

         this.start = Math.max(0, Math.min(this.start, var5));
         this.end = Math.max(this.start, Math.min(this.end, var5));
      }
   }

   private void Select() {
      this.accessible.executeAction(AccessibleAction.SET_TEXT_SELECTION, new Object[]{this.start, this.end});
   }

   private void AddToSelection() {
   }

   private void RemoveFromSelection() {
   }

   private void ScrollIntoView(boolean var1) {
      this.accessible.executeAction(AccessibleAction.SHOW_TEXT_RANGE, new Object[]{this.start, this.end});
   }

   private long[] GetChildren() {
      return new long[0];
   }

   static {
      _initIDs();
   }
}
