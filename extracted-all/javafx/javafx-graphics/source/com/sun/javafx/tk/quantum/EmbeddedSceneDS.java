package com.sun.javafx.tk.quantum;

import com.sun.glass.ui.ClipboardAssistance;
import com.sun.javafx.embed.EmbeddedSceneDSInterface;
import java.util.Arrays;
import java.util.Set;
import javafx.scene.input.TransferMode;

final class EmbeddedSceneDS implements EmbeddedSceneDSInterface {
   private final EmbeddedSceneDnD dnd;
   private final ClipboardAssistance assistant;
   private final GlassSceneDnDEventHandler dndHandler;

   public EmbeddedSceneDS(EmbeddedSceneDnD var1, ClipboardAssistance var2, GlassSceneDnDEventHandler var3) {
      this.dnd = var1;
      this.assistant = var2;
      this.dndHandler = var3;
   }

   @Override
   public Set<TransferMode> getSupportedActions() {
      assert this.dnd.isHostThread();

      return this.dnd.executeOnFXThread(() -> QuantumClipboard.clipboardActionsToTransferModes(this.assistant.getSupportedSourceActions()));
   }

   @Override
   public Object getData(String var1) {
      assert this.dnd.isHostThread();

      return this.dnd.executeOnFXThread(() -> this.assistant.getData(var1));
   }

   @Override
   public String[] getMimeTypes() {
      assert this.dnd.isHostThread();

      return this.dnd.executeOnFXThread(() -> this.assistant.getMimeTypes());
   }

   @Override
   public boolean isMimeTypeAvailable(String var1) {
      assert this.dnd.isHostThread();

      return this.dnd.executeOnFXThread(() -> Arrays.asList(this.assistant.getMimeTypes()).contains(var1));
   }

   @Override
   public void dragDropEnd(TransferMode var1) {
      assert this.dnd.isHostThread();

      this.dnd.executeOnFXThread(() -> {
         try {
            this.dndHandler.handleDragEnd(var1, this.assistant);
         } finally {
            this.dnd.onDragSourceReleased(this);
         }

         return null;
      });
   }
}
