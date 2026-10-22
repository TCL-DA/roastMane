package org.controlsfx.control.decoration;

import impl.org.controlsfx.ImplUtils;
import impl.org.controlsfx.skin.DecorationPane;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;

public class Decorator {
   private static final String DECORATIONS_PROPERTY_KEY = "$org.controlsfx.decorations$";
   private static List<Scene> currentlyInstallingScenes = new ArrayList<>();
   private static Map<Scene, List<Consumer<DecorationPane>>> pendingTasksByScene = new HashMap<>();

   private Decorator() {
   }

   public static final void addDecoration(Node target, Decoration decoration) {
      getDecorations(target, true).add(decoration);
      updateDecorationsOnNode(target, FXCollections.observableArrayList(new Decoration[]{decoration}), null);
   }

   public static final void removeDecoration(Node target, Decoration decoration) {
      getDecorations(target, true).remove(decoration);
      updateDecorationsOnNode(target, null, FXCollections.observableArrayList(new Decoration[]{decoration}));
   }

   public static final void removeAllDecorations(Node target) {
      List<Decoration> decorations = getDecorations(target, true);
      List<Decoration> removed = FXCollections.observableArrayList(decorations);
      target.getProperties().remove("$org.controlsfx.decorations$");
      updateDecorationsOnNode(target, null, removed);
   }

   public static final ObservableList<Decoration> getDecorations(Node target) {
      return getDecorations(target, false);
   }

   private static final ObservableList<Decoration> getDecorations(Node target, boolean createIfAbsent) {
      ObservableList<Decoration> decorations = (ObservableList<Decoration>)target.getProperties().get("$org.controlsfx.decorations$");
      if (decorations == null && createIfAbsent) {
         decorations = FXCollections.observableArrayList();
         target.getProperties().put("$org.controlsfx.decorations$", decorations);
      }

      return decorations;
   }

   private static void updateDecorationsOnNode(Node target, List<Decoration> added, List<Decoration> removed) {
      getDecorationPane(target, pane -> pane.updateDecorationsOnNode(target, added, removed));
   }

   private static void getDecorationPane(final Node target, Consumer<DecorationPane> task) {
      DecorationPane pane = getDecorationPaneInParentHierarchy(target);
      if (pane != null) {
         task.accept(pane);
      } else {
         final Consumer<Scene> sceneConsumer = scenex -> {
            if (currentlyInstallingScenes.contains(scenex)) {
               List<Consumer<DecorationPane>> pendingTasks = pendingTasksByScene.get(scenex);
               if (pendingTasks == null) {
                  pendingTasks = new LinkedList<>();
                  pendingTasksByScene.put(scenex, pendingTasks);
               }

               pendingTasks.add(task);
            } else {
               DecorationPane _pane = getDecorationPaneInParentHierarchy(target);
               if (_pane == null) {
                  currentlyInstallingScenes.add(scenex);
                  _pane = new DecorationPane();
                  Parent oldRoot = scenex.getRoot();
                  _pane.getStylesheets().addAll(oldRoot.getStylesheets());
                  ImplUtils.injectAsRootPane(scenex, _pane, true);
                  _pane.setRoot(oldRoot);
                  currentlyInstallingScenes.remove(scenex);
               }

               task.accept(_pane);
               List<Consumer<DecorationPane>> pendingTasks = pendingTasksByScene.remove(scenex);
               if (pendingTasks != null) {
                  for (Consumer<DecorationPane> pendingTask : pendingTasks) {
                     pendingTask.accept(_pane);
                  }
               }
            }
         };
         Scene scene = target.getScene();
         if (scene != null) {
            sceneConsumer.accept(scene);
         } else {
            InvalidationListener sceneListener = new InvalidationListener() {
               public void invalidated(Observable o) {
                  if (target.getScene() != null) {
                     target.sceneProperty().removeListener(this);
                     sceneConsumer.accept(target.getScene());
                  }
               }
            };
            target.sceneProperty().addListener(sceneListener);
         }
      }
   }

   private static DecorationPane getDecorationPaneInParentHierarchy(Node target) {
      for (Parent p = target.getParent(); p != null; p = p.getParent()) {
         if (p instanceof DecorationPane) {
            return (DecorationPane)p;
         }
      }

      return null;
   }
}
