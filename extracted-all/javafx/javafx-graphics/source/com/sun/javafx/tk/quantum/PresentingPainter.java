package com.sun.javafx.tk.quantum;

import com.sun.javafx.logging.PulseLogger;
import com.sun.prism.Graphics;
import com.sun.prism.GraphicsPipeline;
import com.sun.prism.impl.Disposer;

final class PresentingPainter extends ViewPainter {
   PresentingPainter(ViewScene var1) {
      super(var1);
   }

   @Override
   public void run() {
      renderLock.lock();
      boolean var1 = false;
      boolean var2 = false;
      boolean var3 = false;

      try {
         var2 = this.validateStageGraphics();
         if (!var2) {
            if (QuantumToolkit.verbose) {
               System.err.println("PresentingPainter: validateStageGraphics failed");
            }

            this.paintImpl(null);
            return;
         }

         this.sceneState.lock();
         var1 = true;
         if (this.factory == null) {
            this.factory = GraphicsPipeline.getDefaultResourceFactory();
         }

         if (this.factory == null || !this.factory.isDeviceReady()) {
            this.sceneState.getScene().entireSceneNeedsRepaint();
            this.factory = null;
            return;
         }

         if (this.presentable != null && this.presentable.lockResources(this.sceneState)) {
            this.disposePresentable();
         }

         if (this.presentable == null) {
            this.presentable = this.factory.createPresentable(this.sceneState);
            this.penWidth = this.viewWidth;
            this.penHeight = this.viewHeight;
            this.freshBackBuffer = true;
         }

         if (this.presentable == null) {
            return;
         }

         Graphics var4 = this.presentable.createGraphics();
         ViewScene var5 = (ViewScene)this.sceneState.getScene();
         if (var4 != null) {
            this.paintImpl(var4);
            this.freshBackBuffer = false;
         }

         if (PulseLogger.PULSE_LOGGING_ENABLED) {
            PulseLogger.newPhase("Presenting");
         }

         if (this.presentable.prepare(null)) {
            if (var5.getDoPresent() && !this.presentable.present()) {
               this.disposePresentable();
               this.sceneState.getScene().entireSceneNeedsRepaint();
            }

            return;
         }

         this.disposePresentable();
         this.sceneState.getScene().entireSceneNeedsRepaint();
      } catch (Throwable var11) {
         var3 = true;
         var11.printStackTrace(System.err);
         return;
      } finally {
         Disposer.cleanUp();
         if (var1) {
            this.sceneState.unlock();
         }

         ViewScene var8 = (ViewScene)this.sceneState.getScene();
         var8.setPainting(false);
         if (this.factory != null) {
            this.factory.getTextureResourcePool().freeDisposalRequestedAndCheckResources(var3);
         }

         renderLock.unlock();
      }
   }
}
