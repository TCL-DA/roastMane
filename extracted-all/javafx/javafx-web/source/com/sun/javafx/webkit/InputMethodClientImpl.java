package com.sun.javafx.webkit;

import com.sun.javafx.logging.PlatformLogger;
import com.sun.javafx.scene.SceneHelper;
import com.sun.javafx.scene.input.ExtendedInputMethodRequests;
import com.sun.webkit.InputMethodClient;
import com.sun.webkit.Invoker;
import com.sun.webkit.WebPage;
import com.sun.webkit.event.WCInputMethodEvent;
import com.sun.webkit.graphics.WCPoint;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import javafx.geometry.Point2D;
import javafx.scene.input.InputMethodEvent;
import javafx.scene.input.InputMethodHighlight;
import javafx.scene.input.InputMethodTextRun;
import javafx.scene.web.WebView;

public final class InputMethodClientImpl implements InputMethodClient, ExtendedInputMethodRequests {
   private static final PlatformLogger log = PlatformLogger.getLogger(InputMethodClientImpl.class.getName());
   private final WeakReference<WebView> wvRef;
   private final WebPage webPage;
   private boolean state;

   public InputMethodClientImpl(WebView var1, WebPage var2) {
      this.wvRef = new WeakReference<>(var1);
      this.webPage = var2;
      if (var2 != null) {
         var2.setInputMethodClient(this);
      }
   }

   @Override
   public void activateInputMethods(boolean var1) {
      WebView var2 = this.wvRef.get();
      if (var2 != null && var2.getScene() != null) {
         SceneHelper.enableInputMethodEvents(var2.getScene(), var1);
      }

      this.state = var1;
   }

   public boolean getInputMethodState() {
      return this.state;
   }

   public static WCInputMethodEvent convertToWCInputMethodEvent(InputMethodEvent var0) {
      ArrayList var1 = new ArrayList();
      StringBuilder var2 = new StringBuilder();
      int var3 = 0;

      for (InputMethodTextRun var5 : var0.getComposed()) {
         String var6 = var5.getText();
         InputMethodHighlight var7 = var5.getHighlight();
         var1.add(var3);
         var1.add(var3 + var6.length());
         var1.add(var7 != InputMethodHighlight.SELECTED_CONVERTED && var7 != InputMethodHighlight.SELECTED_RAW ? 0 : 1);
         var3 += var6.length();
         var2.append(var6);
      }

      int var8 = var1.size();
      if (var8 == 0) {
         var1.add(0);
         var1.add(var3);
         var1.add(0);
         var8 = var1.size();
      }

      int[] var9 = new int[var8];

      for (int var10 = 0; var10 < var8; var10++) {
         var9[var10] = (Integer)var1.get(var10);
      }

      return new WCInputMethodEvent(var0.getCommitted(), var2.toString(), var9, var0.getCaretPosition());
   }

   public Point2D getTextLocation(int var1) {
      FutureTask var2 = new FutureTask<>(() -> {
         int[] var2x = this.webPage.getClientTextLocation(var1);
         WCPoint var3x = this.webPage.getPageClient().windowToScreen(new WCPoint((float)var2x[0], (float)(var2x[1] + var2x[3])));
         return new Point2D((double)var3x.getIntX(), (double)var3x.getIntY());
      });
      Invoker.getInvoker().invokeOnEventThread(var2);
      Point2D var3 = null;

      try {
         var3 = (Point2D)var2.get();
      } catch (ExecutionException var5) {
         log.severe("InputMethodClientImpl.getTextLocation " + var5);
      } catch (InterruptedException var6) {
         log.severe("InputMethodClientImpl.getTextLocation InterruptedException" + var6);
      }

      return var3;
   }

   public int getLocationOffset(int var1, int var2) {
      FutureTask var3 = new FutureTask<>(() -> {
         WCPoint var3x = this.webPage.getPageClient().windowToScreen(new WCPoint(0.0F, 0.0F));
         return this.webPage.getClientLocationOffset(var1 - var3x.getIntX(), var2 - var3x.getIntY());
      });
      Invoker.getInvoker().invokeOnEventThread(var3);
      int var4 = 0;

      try {
         var4 = (Integer)var3.get();
      } catch (ExecutionException var6) {
         log.severe("InputMethodClientImpl.getLocationOffset " + var6);
      } catch (InterruptedException var7) {
         log.severe("InputMethodClientImpl.getTextLocation InterruptedException" + var7);
      }

      return var4;
   }

   public void cancelLatestCommittedText() {
   }

   public String getSelectedText() {
      return this.webPage.getClientSelectedText();
   }

   public int getInsertPositionOffset() {
      return this.webPage.getClientInsertPositionOffset();
   }

   public String getCommittedText(int var1, int var2) {
      try {
         return this.webPage.getClientCommittedText().substring(var1, var2);
      } catch (StringIndexOutOfBoundsException var4) {
         throw new IllegalArgumentException(var4);
      }
   }

   public int getCommittedTextLength() {
      return this.webPage.getClientCommittedTextLength();
   }
}
