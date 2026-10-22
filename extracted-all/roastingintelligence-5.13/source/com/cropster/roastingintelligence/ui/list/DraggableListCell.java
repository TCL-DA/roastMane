package com.cropster.roastingintelligence.ui.list;

import com.cropster.roastingintelligence.ui.util.PlatformScheduler;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import javafx.application.Platform;
import javafx.collections.ObservableMap;
import javafx.css.Styleable;
import javafx.geometry.Bounds;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.skin.VirtualFlow;
import javafx.scene.image.WritableImage;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import org.apache.commons.lang3.StringUtils;

public interface DraggableListCell<T, C extends ListCell<T>> extends Styleable {
   String FINALIZE_DRAG_DROPPED_RUNNABLE = "finalize-drag-dropped-runnable";
   String STYLE_DRAGGED_LIST_CELL = "dragged-list-cell";
   String STYLE_DRAG_OVER_LIST_CELL = "drag-over-list-cell";
   String STYLE_DRAG_ABOVE_LIST_CELL = "drag-above-list-cell";
   String STYLE_DRAG_BELOW_LIST_CELL = "drag-below-list-cell";

   void finalizeDragDropped(T var1, boolean var2);

   C getCell();

   Node getDragHandle();

   Node getDragNode();

   Node getDropTarget();

   int getIndex();

   T getItem();

   ListView<T> getListView();

   String idForItem(T var1);

   default WritableImage getDragImage() {
      SnapshotParameters params = new SnapshotParameters();
      Node dragNode = this.getDragNode();
      Bounds dragNodeBounds = dragNode.getBoundsInLocal();
      WritableImage dragImage = new WritableImage((int)dragNodeBounds.getWidth(), (int)dragNodeBounds.getHeight());
      return dragNode.snapshot(params, dragImage);
   }

   default int getIndex(T item) {
      if (item != null && this.getListView() != null) {
         return this.getItem() == item ? this.getIndex() : this.getListView().getItems().indexOf(item);
      } else {
         return -1;
      }
   }

   default void initDragAndDrop(DataFormat dataFormat, C cell) {
      Node dragHandle = this.getDragHandle();
      dragHandle.setOnDragDetected(e -> this.onDragDetected(e, dataFormat, cell, dragHandle));
      dragHandle.setOnMouseEntered(e -> dragHandle.setCursor(Cursor.OPEN_HAND));
      dragHandle.setOnMouseExited(e -> dragHandle.setCursor(Cursor.DEFAULT));
      dragHandle.setOnMousePressed(e -> dragHandle.setCursor(Cursor.CLOSED_HAND));
      dragHandle.setOnMouseReleased(e -> {
         Bounds bounds = dragHandle.localToScene(dragHandle.getBoundsInLocal());
         boolean insideDragHandle = bounds.contains(e.getSceneX(), e.getSceneY());
         dragHandle.setCursor(insideDragHandle ? Cursor.OPEN_HAND : Cursor.DEFAULT);
      });
      Node dropTarget = this.getDropTarget();
      dropTarget.setOnDragOver(e -> this.onDragOver(e, dataFormat));
      dropTarget.setOnDragEntered(e -> this.onDragEntered(e, dataFormat));
      dropTarget.setOnDragExited(e -> this.onDragExited(e, dataFormat));
      dropTarget.setOnDragDropped(e -> this.onDragDropped(e, dataFormat));
      dropTarget.setOnDragDone(this::onDragDone);
   }

   default T itemForId(String id) {
      return this.getListView().getItems().stream().filter(item -> StringUtils.equals(this.idForItem((T)item), id)).findFirst().orElse(null);
   }

   default void onDragDetected(MouseEvent e, DataFormat dataFormat, C cell, Node dragHandle) {
      T item = (T)cell.getItem();
      ClipboardContent content = new ClipboardContent();
      content.put(dataFormat, this.idForItem(item));
      SnapshotParameters snapshotParameters = new SnapshotParameters();
      snapshotParameters.setFill(Color.TRANSPARENT);
      Node dragNode = this.getDragNode();
      WritableImage snapshot = dragNode.snapshot(snapshotParameters, this.getDragImage());
      Bounds localBounds = cell.getBoundsInLocal();
      Bounds sceneBounds = cell.localToScene(localBounds);
      double offsetX = localBounds.getCenterX() - (e.getSceneX() - sceneBounds.getMinX());
      double offsetY = localBounds.getCenterY() - (e.getSceneY() - sceneBounds.getMinY());
      Dragboard dragBoard = dragNode.startDragAndDrop(new TransferMode[]{TransferMode.MOVE});
      dragBoard.setContent(content);
      dragBoard.setDragView(snapshot, offsetX, offsetY);
      dragNode.getStyleClass().add("dragged-list-cell");
      this.getListView().getProperties().put("dragged-list-cell", item);
      e.consume();
      dragHandle.setCursor(Cursor.CLOSED_HAND);
   }

   default void onDragDone(DragEvent dragEvent) {
      ObservableMap<Object, Object> properties = this.getListView().getProperties();
      ((DraggableListCell.AutoscrollHelper)properties.computeIfAbsent(DraggableListCell.AutoscrollHelper.class, k -> new DraggableListCell.AutoscrollHelper()))
         .stopAutoScrollIfNeeded();
      Node dragNode = this.getDragNode();
      dragNode.getStyleClass().remove("dragged-list-cell");
      properties.put("dragged-list-cell", null);
      if (dragEvent.getTransferMode() == TransferMode.MOVE) {
         dragEvent.getDragboard().clear();
      }

      dragEvent.consume();
      Runnable finalizeRunnable = (Runnable)properties.getOrDefault("finalize-drag-dropped-runnable", null);
      if (finalizeRunnable != null) {
         Platform.runLater(finalizeRunnable);
         properties.put("finalize-drag-dropped-runnable", null);
      }
   }

   default void onDragDropped(DragEvent dragEvent, DataFormat dataFormat) {
      ObservableMap<Object, Object> properties = this.getListView().getProperties();
      ((DraggableListCell.AutoscrollHelper)properties.computeIfAbsent(DraggableListCell.AutoscrollHelper.class, k -> new DraggableListCell.AutoscrollHelper()))
         .stopAutoScrollIfNeeded();
      this.getStyleClass().removeAll(new String[]{"drag-over-list-cell", "drag-above-list-cell", "drag-below-list-cell"});
      boolean success = false;
      Dragboard dragBoard = dragEvent.getDragboard();
      if (dragBoard.hasContent(dataFormat)) {
         T draggedItem = this.itemForId((String)dragBoard.getContent(dataFormat));
         boolean before = this.getIndex(draggedItem) > this.getIndex();
         Runnable finalizeRunnable = () -> this.finalizeDragDropped(draggedItem, before);
         properties.put("finalize-drag-dropped-runnable", finalizeRunnable);
         success = true;
      }

      dragEvent.setDropCompleted(success);
      dragEvent.consume();
   }

   default void onDragEntered(DragEvent dragEvent, DataFormat dataFormat) {
      Dragboard dragBoard = dragEvent.getDragboard();
      if (dragEvent.getTransferMode() == TransferMode.MOVE
         && dragBoard.hasContent(dataFormat)
         && !this.idForItem(this.getItem()).equals(dragBoard.getContent(dataFormat))) {
         this.getStyleClass().add("drag-over-list-cell");
         int thisIndex = this.getIndex();
         int thatIndex = this.getIndex(this.itemForId((String)dragBoard.getContent(dataFormat)));
         this.getStyleClass().add(thisIndex > thatIndex ? "drag-below-list-cell" : "drag-above-list-cell");
      }

      dragEvent.consume();
   }

   default void onDragExited(DragEvent dragEvent, DataFormat dataFormat) {
      Dragboard dragBoard = dragEvent.getDragboard();
      if (dragEvent.getTransferMode() == TransferMode.MOVE
         && dragBoard.hasContent(dataFormat)
         && !this.idForItem(this.getItem()).equals(dragBoard.getContent(dataFormat))) {
         this.getStyleClass().removeAll(new String[]{"drag-over-list-cell", "drag-above-list-cell", "drag-below-list-cell"});
      }

      dragEvent.consume();
   }

   default void onDragOver(DragEvent dragEvent, DataFormat dataFormat) {
      Dragboard dragBoard = dragEvent.getDragboard();
      if (dragEvent.getTransferMode() == TransferMode.MOVE
         && dragBoard.hasContent(dataFormat)
         && !this.idForItem(this.getItem()).equals(dragBoard.getContent(dataFormat))) {
         dragEvent.acceptTransferModes(new TransferMode[]{TransferMode.MOVE});
      }

      ListView<T> listView = this.getListView();
      ((DraggableListCell.AutoscrollHelper)listView.getProperties()
            .computeIfAbsent(DraggableListCell.AutoscrollHelper.class, k -> new DraggableListCell.AutoscrollHelper()))
         .autoscrollIfNeeded(listView, dragEvent);
      dragEvent.consume();
   }

   public static class AutoscrollHelper {
      private static final double MINIMUM_WIDTH = 1.0;
      private static final double PROXIMITY = 24.0;
      private static final double TRIGGER_SCROLL_MINIMUM = 0.001;
      private DraggableListCell.AutoscrollHelper.ScrollTask scrollTask;

      void autoscrollIfNeeded(ListView<?> listView, DragEvent dragEvent) {
         Region hotRegion = this.getClippedContainer(listView);
         if (hotRegion == null || hotRegion.getBoundsInLocal().getWidth() < 1.0) {
            hotRegion = listView;
            if (listView.getBoundsInLocal().getWidth() < 1.0) {
               this.stopAutoScrollIfNeeded();
               return;
            }
         }

         double yOffset = 0.0;
         double regionY = hotRegion.localToScene(0.0, 0.0).getY();
         double delta = dragEvent.getSceneY() - regionY;
         if (delta > 0.0 && delta < 24.0) {
            yOffset = delta - 24.0;
         } else {
            delta = regionY + hotRegion.getHeight() - dragEvent.getSceneY();
            if (delta > 0.0 && delta < 24.0) {
               yOffset = 24.0 - delta;
            }
         }

         if (Math.abs(yOffset) > 0.001) {
            this.autoscroll(listView, yOffset);
         } else {
            this.stopAutoScrollIfNeeded();
         }
      }

      void stopAutoScrollIfNeeded() {
         synchronized (this) {
            if (this.scrollTask != null) {
               this.scrollTask.stopRunning();
            }
         }
      }

      private void autoscroll(ListView<?> listView, double yOffset) {
         synchronized (this) {
            if (this.scrollTask == null || !this.scrollTask.isRunning()) {
               this.scrollTask = new DraggableListCell.AutoscrollHelper.ScrollTask(listView);
               PlatformScheduler.executeBackgroundTask(this.scrollTask);
            }
         }

         this.scrollTask.setDelta(yOffset);
      }

      private Region getClippedContainer(ListView<?> listView) {
         for (Node child : this.getVirtualFlow(listView).getChildrenUnmodifiable()) {
            if (child.getStyleClass().contains("clipped-container")) {
               return (Region)child;
            }
         }

         return null;
      }

      private VirtualFlow<?> getVirtualFlow(ListView<?> listView) {
         return (VirtualFlow<?>)listView.lookup("VirtualFlow");
      }

      private class ScrollTask implements Runnable {
         private final ListView<?> listView;
         private final AtomicBoolean running = new AtomicBoolean(true);
         private final AtomicReference<Double> yOffset = new AtomicReference<>(0.0);

         ScrollTask(ListView<?> listView) {
            this.listView = listView;
         }

         boolean isRunning() {
            return this.running.get();
         }

         @Override
         public void run() {
            this.trySleep(300L);

            while (this.running.get()) {
               Platform.runLater(this::scrollY);
               this.trySleep(15L);
            }
         }

         private void scrollY() {
            if (AutoscrollHelper.this.getVirtualFlow(this.listView).scrollPixels(this.yOffset.get() * 2.0 / 3.0) == 0.0) {
               this.stopRunning();
            }
         }

         public void stopRunning() {
            this.running.set(false);
         }

         public void setDelta(double yOffset) {
            this.yOffset.set(yOffset);
         }

         private void trySleep(long millis) {
            try {
               Thread.sleep(millis);
            } catch (InterruptedException var4) {
            }
         }
      }
   }
}
