package org.controlsfx.control.table;

import java.lang.reflect.Field;
import java.util.function.Consumer;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.skin.TableHeaderRow;
import javafx.scene.control.skin.TableViewSkin;
import javafx.scene.control.skin.TableViewSkinBase;

final class TableViewUtils {
   public static void modifyTableMenu(TableView<?> tableView, Consumer<ContextMenu> consumer) {
      modifyTableMenu(tableView, consumer);
   }

   public static void modifyTableMenu(TreeTableView<?> treeTableView, Consumer<ContextMenu> consumer) {
      modifyTableMenu(treeTableView, consumer);
   }

   private static void modifyTableMenu(final Control control, final Consumer<ContextMenu> consumer) {
      if (control.getScene() == null) {
         control.sceneProperty().addListener(new InvalidationListener() {
            public void invalidated(Observable o) {
               control.sceneProperty().removeListener(this);
               TableViewUtils.modifyTableMenu(control, consumer);
            }
         });
      } else {
         Skin<?> skin = control.getSkin();
         if (skin == null) {
            control.skinProperty().addListener(new InvalidationListener() {
               public void invalidated(Observable o) {
                  control.skinProperty().removeListener(this);
                  TableViewUtils.modifyTableMenu(control, consumer);
               }
            });
         } else {
            doModify(skin, consumer);
         }
      }
   }

   private static void doModify(Skin<?> skin, Consumer<ContextMenu> consumer) {
      if (skin instanceof TableViewSkinBase) {
         TableViewSkin<?> tableSkin = (TableViewSkin<?>)skin;
         TableHeaderRow headerRow = getHeaderRow(tableSkin);
         if (headerRow != null) {
            ContextMenu contextMenu = getContextMenu(headerRow);
            consumer.accept(contextMenu);
         }
      }
   }

   private static TableHeaderRow getHeaderRow(TableViewSkin<?> tableSkin) {
      ObservableList<Node> children = tableSkin.getChildren();
      int i = 0;

      for (int max = children.size(); i < max; i++) {
         Node child = (Node)children.get(i);
         if (child instanceof TableHeaderRow) {
            return (TableHeaderRow)child;
         }
      }

      return null;
   }

   private static ContextMenu getContextMenu(TableHeaderRow headerRow) {
      try {
         Field privateContextMenuField = TableHeaderRow.class.getDeclaredField("columnPopupMenu");
         privateContextMenuField.setAccessible(true);
         return (ContextMenu)privateContextMenuField.get(headerRow);
      } catch (IllegalArgumentException var3) {
         var3.printStackTrace();
      } catch (IllegalAccessException var4) {
         var4.printStackTrace();
      } catch (NoSuchFieldException var5) {
         var5.printStackTrace();
      } catch (SecurityException var6) {
         var6.printStackTrace();
      }

      return null;
   }
}
