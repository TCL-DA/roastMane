package javafx.scene.control.skin;

import com.sun.javafx.scene.control.ContextMenuContent;
import com.sun.javafx.scene.control.EmbeddedTextContextMenuContent;
import com.sun.javafx.scene.control.Properties;
import com.sun.javafx.scene.control.behavior.TwoLevelFocusPopupBehavior;
import com.sun.javafx.scene.control.skin.Utils;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.scene.AccessibleAttribute;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.Skin;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.WindowEvent;

public class ContextMenuSkin implements Skin<ContextMenu> {
   private ContextMenu popupMenu;
   private final Region root;
   private TwoLevelFocusPopupBehavior tlFocus;
   private double prefHeight;
   private double shiftY;
   private double prefWidth;
   private double shiftX;
   private final EventHandler<KeyEvent> keyListener = new EventHandler<KeyEvent>() {
      public void handle(KeyEvent var1) {
         if (var1.getEventType() == KeyEvent.KEY_PRESSED) {
            if (ContextMenuSkin.this.root.isFocused()) {
               KeyCode var2 = var1.getCode();
               switch (var2) {
                  case ENTER:
                  case SPACE:
                     ContextMenuSkin.this.popupMenu.hide();
                     return;
               }
            }
         }
      }
   };

   public ContextMenuSkin(ContextMenu var1) {
      this.popupMenu = var1;
      this.popupMenu.addEventHandler(Menu.ON_SHOWING, new EventHandler<Event>() {
         public void handle(Event var1) {
            ContextMenuSkin.this.prefHeight = ContextMenuSkin.this.root.prefHeight(-1.0);
            ContextMenuSkin.this.prefWidth = ContextMenuSkin.this.root.prefWidth(-1.0);
         }
      });
      this.popupMenu.addEventHandler(Menu.ON_SHOWN, new EventHandler<Event>() {
         public void handle(Event var1) {
            Node var2 = ContextMenuSkin.this.popupMenu.getSkin().getNode();
            if (var2 != null && var2 instanceof ContextMenuContent) {
               VBox var3 = ((ContextMenuContent)var2).getItemsContainer();
               var3.notifyAccessibleAttributeChanged(AccessibleAttribute.VISIBLE);
            }

            ContextMenuSkin.this.root.addEventHandler(KeyEvent.KEY_PRESSED, ContextMenuSkin.this.keyListener);
            ContextMenuSkin.this.performPopupShifts();
         }
      });
      this.popupMenu.addEventHandler(Menu.ON_HIDDEN, new EventHandler<Event>() {
         public void handle(Event var1) {
            Node var2 = ContextMenuSkin.this.popupMenu.getSkin().getNode();
            if (var2 != null) {
               var2.requestFocus();
            }

            ContextMenuSkin.this.root.removeEventHandler(KeyEvent.KEY_PRESSED, ContextMenuSkin.this.keyListener);
         }
      });
      this.popupMenu.addEventFilter(WindowEvent.WINDOW_HIDING, new EventHandler<Event>() {
         public void handle(Event var1) {
            Node var2 = ContextMenuSkin.this.popupMenu.getSkin().getNode();
            if (var2 instanceof ContextMenuContent) {
               VBox var3 = ((ContextMenuContent)var2).getItemsContainer();
               var3.notifyAccessibleAttributeChanged(AccessibleAttribute.VISIBLE);
            }
         }
      });
      if (Properties.IS_TOUCH_SUPPORTED && this.popupMenu.getStyleClass().contains("text-input-context-menu")) {
         this.root = new EmbeddedTextContextMenuContent(this.popupMenu);
      } else {
         this.root = new ContextMenuContent(this.popupMenu);
      }

      this.root.idProperty().bind(this.popupMenu.idProperty());
      this.root.styleProperty().bind(this.popupMenu.styleProperty());
      this.root.getStyleClass().addAll(this.popupMenu.getStyleClass());
      if (Utils.isTwoLevelFocus()) {
         this.tlFocus = new TwoLevelFocusPopupBehavior(this.popupMenu);
      }
   }

   public ContextMenu getSkinnable() {
      return this.popupMenu;
   }

   @Override
   public Node getNode() {
      return this.root;
   }

   @Override
   public void dispose() {
      this.root.idProperty().unbind();
      this.root.styleProperty().unbind();
      if (this.tlFocus != null) {
         this.tlFocus.dispose();
      }
   }

   private void performPopupShifts() {
      ContextMenu var1 = this.getSkinnable();
      Node var2 = var1.getOwnerNode();
      if (var2 != null) {
         Bounds var3 = var2.localToScreen(var2.getLayoutBounds());
         if (var3 != null) {
            double var4 = this.root.prefHeight(-1.0);
            this.shiftY = this.prefHeight - var4;
            if (this.shiftY > 0.0 && var1.getY() + var4 < var3.getMinY()) {
               var1.setY(var1.getY() + this.shiftY);
            }

            double var6 = this.root.prefWidth(-1.0);
            this.shiftX = this.prefWidth - var6;
            if (this.shiftX > 0.0 && var1.getX() + var6 < var3.getMinX()) {
               var1.setX(var1.getX() + this.shiftX);
            }
         }
      }
   }
}
