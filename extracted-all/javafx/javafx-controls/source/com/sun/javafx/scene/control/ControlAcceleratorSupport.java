package com.sun.javafx.scene.control;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.event.Event;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Control;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumnBase;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.input.KeyCombination;

public class ControlAcceleratorSupport {
   private static Map<Object, WeakReference<ChangeListener<Scene>>> sceneChangeListenerMap = new WeakHashMap<>();
   private static Map<MenuItem, WeakReference<ChangeListener<KeyCombination>>> changeListenerMap = new WeakHashMap<>();

   public static void addAcceleratorsIntoScene(ObservableList<MenuItem> var0, Tab var1) {
      addAcceleratorsIntoScene(var0, (Object)var1);
   }

   public static void addAcceleratorsIntoScene(ObservableList<MenuItem> var0, TableColumnBase<?, ?> var1) {
      addAcceleratorsIntoScene(var0, var1);
   }

   public static void addAcceleratorsIntoScene(ObservableList<MenuItem> var0, Node var1) {
      if (var0 != null) {
         if (var1 == null) {
            throw new IllegalArgumentException("Anchor cannot be null");
         } else {
            Scene var2 = var1.getScene();
            if (var2 != null) {
               doAcceleratorInstall(var0, var2);
            }

            WeakReference var3 = sceneChangeListenerMap.get(var1);
            if (var3 != null) {
               ChangeListener var4 = (ChangeListener)var3.get();
               if (var4 != null) {
                  var1.sceneProperty().removeListener(var4);
               }

               sceneChangeListenerMap.remove(var1);
            }

            var1.sceneProperty().addListener(getSceneChangeListener(var1, var0));
         }
      }
   }

   private static void addAcceleratorsIntoScene(final ObservableList<MenuItem> var0, Object var1) {
      if (var1 == null) {
         throw new IllegalArgumentException("Anchor cannot be null");
      } else {
         final ReadOnlyObjectProperty var2 = getControlProperty(var1);
         Control var3 = (Control)var2.get();
         if (var3 == null) {
            var2.addListener(new InvalidationListener() {
               public void invalidated(Observable var1) {
                  Control var2x = (Control)var2.get();
                  if (var2x != null) {
                     var2.removeListener(this);
                     ControlAcceleratorSupport.addAcceleratorsIntoScene(var0, var2x);
                  }
               }
            });
         } else {
            addAcceleratorsIntoScene(var0, (Node)var3);
         }
      }
   }

   private static ChangeListener<Scene> getSceneChangeListener(Object var0, ObservableList<MenuItem> var1) {
      WeakReference var2 = sceneChangeListenerMap.get(var0);
      ChangeListener var3 = var2 == null ? null : (ChangeListener)var2.get();
      if (var3 == null) {
         var3 = (var1x, var2x, var3x) -> {
            if (var2x != null) {
               removeAcceleratorsFromScene(var1, var2x);
            }

            if (var3x != null) {
               doAcceleratorInstall(var1, var3x);
            }
         };
         sceneChangeListenerMap.put(var0, new WeakReference<>(var3));
      }

      return var3;
   }

   private static void doAcceleratorInstall(ObservableList<MenuItem> var0, Scene var1) {
      var0.addListener(var1x -> {
         while (var1x.next()) {
            if (var1x.wasRemoved()) {
               removeAcceleratorsFromScene(var1x.getRemoved(), var1);
            }

            if (var1x.wasAdded()) {
               doAcceleratorInstall(var1x.getAddedSubList(), var1);
            }
         }
      });
      doAcceleratorInstall(var0, var1);
   }

   private static void doAcceleratorInstall(List<? extends MenuItem> var0, Scene var1) {
      for (MenuItem var3 : var0) {
         if (var3 instanceof Menu) {
            doAcceleratorInstall(((Menu)var3).getItems(), var1);
         } else {
            if (var3.getAccelerator() != null) {
               ObservableMap var4 = var1.getAccelerators();
               Runnable var5 = () -> {
                  if (var3.getOnMenuValidation() != null) {
                     Event.fireEvent(var3, new Event(MenuItem.MENU_VALIDATION_EVENT));
                  }

                  Menu var1x = var3.getParentMenu();
                  if (var1x != null && var1x.getOnMenuValidation() != null) {
                     Event.fireEvent(var1x, new Event(MenuItem.MENU_VALIDATION_EVENT));
                  }

                  if (!var3.isDisable()) {
                     if (var3 instanceof RadioMenuItem) {
                        ((RadioMenuItem)var3).setSelected(!((RadioMenuItem)var3).isSelected());
                     } else if (var3 instanceof CheckMenuItem) {
                        ((CheckMenuItem)var3).setSelected(!((CheckMenuItem)var3).isSelected());
                     }

                     var3.fire();
                  }
               };
               var4.put(var3.getAccelerator(), var5);
            }

            var3.acceleratorProperty().addListener(getListener(var1, var3));
         }
      }
   }

   private static ChangeListener<KeyCombination> getListener(Scene var0, MenuItem var1) {
      WeakReference var2 = changeListenerMap.get(var1);
      ChangeListener var3 = var2 == null ? null : (ChangeListener)var2.get();
      if (var3 == null) {
         var3 = (var1x, var2x, var3x) -> {
            ObservableMap var4 = var0.getAccelerators();
            Runnable var5 = (Runnable)var4.remove(var2x);
            if (var3x != null) {
               var4.put(var3x, var5);
            }
         };
         changeListenerMap.put(var1, new WeakReference<>(var3));
      }

      return var3;
   }

   public static void removeAcceleratorsFromScene(List<? extends MenuItem> var0, Tab var1) {
      TabPane var2 = var1.getTabPane();
      if (var2 != null) {
         Scene var3 = var2.getScene();
         removeAcceleratorsFromScene(var0, var3);
      }
   }

   public static void removeAcceleratorsFromScene(List<? extends MenuItem> var0, TableColumnBase<?, ?> var1) {
      ReadOnlyObjectProperty var2 = getControlProperty(var1);
      if (var2 != null) {
         Control var3 = (Control)var2.get();
         if (var3 != null) {
            Scene var4 = var3.getScene();
            removeAcceleratorsFromScene(var0, var4);
         }
      }
   }

   public static void removeAcceleratorsFromScene(List<? extends MenuItem> var0, Node var1) {
      Scene var2 = var1.getScene();
      if (var2 == null) {
         WeakReference var3 = sceneChangeListenerMap.get(var1);
         if (var3 != null) {
            ChangeListener var4 = (ChangeListener)var3.get();
            if (var4 != null) {
               var1.sceneProperty().removeListener(var4);
            }

            sceneChangeListenerMap.remove(var1);
         }
      }

      removeAcceleratorsFromScene(var0, var2);
   }

   public static void removeAcceleratorsFromScene(List<? extends MenuItem> var0, Scene var1) {
      if (var1 != null) {
         for (MenuItem var3 : var0) {
            if (var3 instanceof Menu) {
               removeAcceleratorsFromScene(((Menu)var3).getItems(), var1);
            } else {
               ObservableMap var4 = var1.getAccelerators();
               var4.remove(var3.getAccelerator());
               WeakReference var5 = changeListenerMap.get(var3);
               if (var5 != null) {
                  ChangeListener var6 = (ChangeListener)var5.get();
                  if (var6 != null) {
                     var3.acceleratorProperty().removeListener(var6);
                  }

                  changeListenerMap.remove(var3);
               }
            }
         }
      }
   }

   private static ReadOnlyObjectProperty<? extends Control> getControlProperty(Object var0) {
      if (var0 instanceof TableColumn) {
         return ((TableColumn)var0).tableViewProperty();
      } else if (var0 instanceof TreeTableColumn) {
         return ((TreeTableColumn)var0).treeTableViewProperty();
      } else {
         return var0 instanceof Tab ? ((Tab)var0).tabPaneProperty() : null;
      }
   }
}
