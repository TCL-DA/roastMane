package com.sun.glass.ui;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class TouchInputSupport {
   private int touchCount = 0;
   private boolean filterTouchCoordinates;
   private Map<Long, TouchInputSupport.TouchCoord> touch;
   private TouchInputSupport.TouchCountListener listener;
   private int curTouchCount;
   private View curView;
   private int curModifiers;
   private boolean curIsDirect;

   public TouchInputSupport(TouchInputSupport.TouchCountListener var1, boolean var2) {
      Application.checkEventThread();
      this.listener = var1;
      this.filterTouchCoordinates = var2;
      if (var2) {
         this.touch = new HashMap<>();
      }
   }

   public int getTouchCount() {
      Application.checkEventThread();
      return this.touchCount;
   }

   public void notifyBeginTouchEvent(View var1, int var2, boolean var3, int var4) {
      if (this.curView != null && var1 != this.curView && this.touchCount != 0 && this.touch != null) {
         if (!this.curView.isClosed()) {
            this.curView.notifyBeginTouchEvent(0, true, this.touchCount);

            for (Entry var6 : this.touch.entrySet()) {
               TouchInputSupport.TouchCoord var7 = (TouchInputSupport.TouchCoord)var6.getValue();
               this.curView.notifyNextTouchEvent(813, (Long)var6.getKey(), var7.x, var7.y, var7.xAbs, var7.yAbs);
            }

            this.curView.notifyEndTouchEvent();
         }

         this.touch.clear();
         this.touchCount = 0;
         if (this.listener != null) {
            this.listener.touchCountChanged(this, this.curView, 0, true);
         }
      }

      this.curTouchCount = this.touchCount;
      this.curView = var1;
      this.curModifiers = var2;
      this.curIsDirect = var3;
      if (var1 != null) {
         var1.notifyBeginTouchEvent(var2, var3, var4);
      }
   }

   public void notifyEndTouchEvent(View var1) {
      if (var1 != null) {
         var1.notifyEndTouchEvent();
         if (this.curTouchCount != 0 && this.touchCount != 0 && this.curTouchCount != this.touchCount && this.listener != null) {
            this.listener.touchCountChanged(this, this.curView, this.curModifiers, this.curIsDirect);
         }
      }
   }

   public void notifyNextTouchEvent(View var1, int var2, long var3, int var5, int var6, int var7, int var8) {
      switch (var2) {
         case 811:
            this.touchCount++;
         case 812:
         case 814:
            break;
         case 813:
            this.touchCount--;
            break;
         default:
            System.err.println("Unknown touch state: " + var2);
            return;
      }

      if (this.filterTouchCoordinates) {
         var2 = this.filterTouchInputState(var2, var3, var5, var6, var7, var8);
      }

      if (var1 != null) {
         var1.notifyNextTouchEvent(var2, var3, var5, var6, var7, var8);
      }
   }

   private int filterTouchInputState(int var1, long var2, int var4, int var5, int var6, int var7) {
      switch (var1) {
         case 812:
            TouchInputSupport.TouchCoord var8 = this.touch.get(var2);
            if (var4 == var8.x && var5 == var8.y) {
               var1 = 814;
               break;
            }
         case 811:
            this.touch.put(var2, new TouchInputSupport.TouchCoord(var4, var5, var6, var7));
            break;
         case 813:
            this.touch.remove(var2);
         case 814:
            break;
         default:
            System.err.println("Unknown touch state: " + var1);
      }

      return var1;
   }

   private static class TouchCoord {
      private final int x;
      private final int y;
      private final int xAbs;
      private final int yAbs;

      private TouchCoord(int var1, int var2, int var3, int var4) {
         this.x = var1;
         this.y = var2;
         this.xAbs = var3;
         this.yAbs = var4;
      }
   }

   public interface TouchCountListener {
      void touchCountChanged(TouchInputSupport var1, View var2, int var3, boolean var4);
   }
}
