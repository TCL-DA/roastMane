package com.sun.javafx.embed.swing;

import com.sun.javafx.embed.EmbeddedSceneDSInterface;
import com.sun.javafx.embed.EmbeddedSceneDTInterface;
import com.sun.javafx.embed.EmbeddedSceneInterface;
import com.sun.javafx.embed.HostDragStartListener;
import com.sun.javafx.tk.Toolkit;
import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureRecognizer;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceAdapter;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.InvalidDnDOperationException;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import javafx.scene.input.TransferMode;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;

public final class SwingDnD {
   private final Transferable dndTransferable = new SwingDnD.DnDTransferable();
   private final DragSource dragSource;
   private final DragSourceListener dragSourceListener;
   private SwingDragSource swingDragSource;
   private EmbeddedSceneDTInterface fxDropTarget;
   private EmbeddedSceneDSInterface fxDragSource;
   private MouseEvent me;

   public SwingDnD(final JComponent var1, final EmbeddedSceneInterface var2) {
      var1.addMouseListener(new MouseAdapter() {
         @Override
         public void mouseClicked(MouseEvent var1) {
            SwingDnD.this.storeMouseEvent(var1);
         }

         @Override
         public void mouseDragged(MouseEvent var1) {
            SwingDnD.this.storeMouseEvent(var1);
         }

         @Override
         public void mousePressed(MouseEvent var1) {
            SwingDnD.this.storeMouseEvent(var1);
         }

         @Override
         public void mouseReleased(MouseEvent var1) {
            SwingDnD.this.storeMouseEvent(var1);
         }
      });
      this.dragSource = new DragSource();
      this.dragSourceListener = new DragSourceAdapter() {
         @Override
         public void dragDropEnd(DragSourceDropEvent var1) {
            assert SwingDnD.this.fxDragSource != null;

            try {
               SwingDnD.this.fxDragSource.dragDropEnd(SwingDnD.dropActionToTransferMode(var1.getDropAction()));
            } finally {
               SwingDnD.this.fxDragSource = null;
            }
         }
      };
      DropTargetAdapter var3 = new DropTargetAdapter() {
         private TransferMode lastTransferMode;

         @Override
         public void dragEnter(DropTargetDragEvent var1x) {
            if (SwingDnD.this.swingDragSource == null && SwingDnD.this.fxDropTarget == null) {
               assert SwingDnD.this.swingDragSource == null;

               SwingDnD.this.swingDragSource = new SwingDragSource();
               SwingDnD.this.swingDragSource.updateContents(var1x, false);

               assert SwingDnD.this.fxDropTarget == null;

               SwingDnD.this.fxDropTarget = var2.createDropTarget();
               Point var2x = var1x.getLocation();
               Point var3 = new Point(var2x);
               SwingUtilities.convertPointToScreen(var3, var1);
               this.lastTransferMode = SwingDnD.this.fxDropTarget
                  .handleDragEnter(var2x.x, var2x.y, var3.x, var3.y, SwingDnD.dropActionToTransferMode(var1x.getDropAction()), SwingDnD.this.swingDragSource);
               SwingDnD.this.applyDragResult(this.lastTransferMode, var1x);
            }
         }

         @Override
         public void dragExit(DropTargetEvent var1x) {
            assert SwingDnD.this.swingDragSource != null;

            assert SwingDnD.this.fxDropTarget != null;

            try {
               SwingDnD.this.fxDropTarget.handleDragLeave();
            } finally {
               SwingDnD.this.endDnD();
               this.lastTransferMode = null;
            }
         }

         @Override
         public void dragOver(DropTargetDragEvent var1x) {
            assert SwingDnD.this.swingDragSource != null;

            SwingDnD.this.swingDragSource.updateContents(var1x, false);

            assert SwingDnD.this.fxDropTarget != null;

            Point var2x = var1x.getLocation();
            Point var3 = new Point(var2x);
            SwingUtilities.convertPointToScreen(var3, var1);
            this.lastTransferMode = SwingDnD.this.fxDropTarget
               .handleDragOver(var2x.x, var2x.y, var3.x, var3.y, SwingDnD.dropActionToTransferMode(var1x.getDropAction()));
            SwingDnD.this.applyDragResult(this.lastTransferMode, var1x);
         }

         @Override
         public void drop(DropTargetDropEvent var1x) {
            assert SwingDnD.this.swingDragSource != null;

            SwingDnD.this.applyDropResult(this.lastTransferMode, var1x);
            SwingDnD.this.swingDragSource.updateContents(var1x, true);
            Point var2x = var1x.getLocation();
            Point var3 = new Point(var2x);
            SwingUtilities.convertPointToScreen(var3, var1);

            assert SwingDnD.this.fxDropTarget != null;

            try {
               this.lastTransferMode = SwingDnD.this.fxDropTarget
                  .handleDragDrop(var2x.x, var2x.y, var3.x, var3.y, SwingDnD.dropActionToTransferMode(var1x.getDropAction()));

               try {
                  SwingDnD.this.applyDropResult(this.lastTransferMode, var1x);
               } catch (InvalidDnDOperationException var8) {
               }
            } finally {
               var1x.dropComplete(this.lastTransferMode != null);
               SwingDnD.this.endDnD();
               this.lastTransferMode = null;
            }
         }
      };
      var1.setDropTarget(new DropTarget(var1, 1073741827, var3));
   }

   public void addNotify() {
      this.dragSource.addDragSourceListener(this.dragSourceListener);
   }

   public void removeNotify() {
      this.dragSource.removeDragSourceListener(this.dragSourceListener);
   }

   public HostDragStartListener getDragStartListener() {
      return (var1, var2) -> {
         assert Toolkit.getToolkit().isFxUserThread();

         assert var1 != null;

         SwingUtilities.invokeLater(() -> {
            assert this.fxDragSource == null;

            assert this.swingDragSource == null;

            assert this.fxDropTarget == null;

            this.fxDragSource = var1;
            this.startDrag(this.me, this.dndTransferable, var1.getSupportedActions(), var2);
         });
      };
   }

   private void startDrag(final MouseEvent var1, Transferable var2, final Set<TransferMode> var3, TransferMode var4) {
      assert var3.contains(var4);

      Point var5 = new Point(var1.getX(), var1.getY());
      int var6 = transferModeToDropAction(var4);

      final class StubDragGestureRecognizer extends DragGestureRecognizer {
         StubDragGestureRecognizer(DragSource var2) {
            super(var2, var1.getComponent());
            this.setSourceActions(SwingDnD.transferModesToDropActions(var3));
            this.appendEvent(var1);
         }

         @Override
         protected void registerListeners() {
         }

         @Override
         protected void unregisterListeners() {
         }
      }

      StubDragGestureRecognizer var7 = new StubDragGestureRecognizer(this.dragSource);
      List var8 = Arrays.asList(var7.getTriggerEvent());
      DragGestureEvent var9 = new DragGestureEvent(var7, var6, var5, var8);
      var9.startDrag(null, var2);
   }

   private void endDnD() {
      assert this.swingDragSource != null;

      assert this.fxDropTarget != null;

      this.fxDropTarget = null;
      this.swingDragSource = null;
   }

   private void storeMouseEvent(MouseEvent var1) {
      this.me = var1;
   }

   private void applyDragResult(TransferMode var1, DropTargetDragEvent var2) {
      if (var1 == null) {
         var2.rejectDrag();
      } else {
         var2.acceptDrag(transferModeToDropAction(var1));
      }
   }

   private void applyDropResult(TransferMode var1, DropTargetDropEvent var2) {
      if (var1 == null) {
         var2.rejectDrop();
      } else {
         var2.acceptDrop(transferModeToDropAction(var1));
      }
   }

   public static TransferMode dropActionToTransferMode(int var0) {
      switch (var0) {
         case 0:
            return null;
         case 1:
            return TransferMode.COPY;
         case 2:
            return TransferMode.MOVE;
         case 1073741824:
            return TransferMode.LINK;
         default:
            throw new IllegalArgumentException();
      }
   }

   public static int transferModeToDropAction(TransferMode var0) {
      switch (var0) {
         case COPY:
            return 1;
         case MOVE:
            return 2;
         case LINK:
            return 1073741824;
         default:
            throw new IllegalArgumentException();
      }
   }

   public static Set<TransferMode> dropActionsToTransferModes(int var0) {
      EnumSet var1 = EnumSet.noneOf(TransferMode.class);
      if ((var0 & 1) != 0) {
         var1.add(TransferMode.COPY);
      }

      if ((var0 & 2) != 0) {
         var1.add(TransferMode.MOVE);
      }

      if ((var0 & 1073741824) != 0) {
         var1.add(TransferMode.LINK);
      }

      return Collections.unmodifiableSet(var1);
   }

   public static int transferModesToDropActions(Set<TransferMode> var0) {
      int var1 = 0;

      for (TransferMode var3 : var0) {
         var1 |= transferModeToDropAction(var3);
      }

      return var1;
   }

   private final class DnDTransferable implements Transferable {
      @Override
      public Object getTransferData(DataFlavor var1) throws UnsupportedEncodingException {
         assert SwingDnD.this.fxDragSource != null;

         assert SwingUtilities.isEventDispatchThread();

         String var2 = DataFlavorUtils.getFxMimeType(var1);
         return DataFlavorUtils.adjustFxData(var1, SwingDnD.this.fxDragSource.getData(var2));
      }

      @Override
      public DataFlavor[] getTransferDataFlavors() {
         assert SwingDnD.this.fxDragSource != null;

         assert SwingUtilities.isEventDispatchThread();

         String[] var1 = SwingDnD.this.fxDragSource.getMimeTypes();
         ArrayList var2 = new ArrayList(var1.length);

         for (String var6 : var1) {
            Object var7 = null;

            try {
               var7 = new DataFlavor(var6);
            } catch (ClassNotFoundException var9) {
               continue;
            }

            var2.add(var7);
         }

         return var2.toArray(new DataFlavor[0]);
      }

      @Override
      public boolean isDataFlavorSupported(DataFlavor var1) {
         assert SwingDnD.this.fxDragSource != null;

         assert SwingUtilities.isEventDispatchThread();

         return SwingDnD.this.fxDragSource.isMimeTypeAvailable(DataFlavorUtils.getFxMimeType(var1));
      }
   }
}
