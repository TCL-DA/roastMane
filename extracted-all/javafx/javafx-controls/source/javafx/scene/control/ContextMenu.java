package javafx.scene.control;

import com.sun.javafx.beans.IDProperty;
import com.sun.javafx.collections.TrackableObservableList;
import com.sun.javafx.util.Utils;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.collections.ObservableList;
import javafx.collections.ListChangeListener.Change;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Point2D;
import javafx.geometry.Side;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.skin.ContextMenuSkin;
import javafx.stage.Window;

@IDProperty("id")
public class ContextMenu extends PopupControl {
   private boolean showRelativeToWindow = false;
   private ObjectProperty<EventHandler<ActionEvent>> onAction = new ObjectPropertyBase<EventHandler<ActionEvent>>() {
      protected void invalidated() {
         ContextMenu.this.setEventHandler(ActionEvent.ACTION, (EventHandler)this.get());
      }

      public Object getBean() {
         return ContextMenu.this;
      }

      public String getName() {
         return "onAction";
      }
   };
   private final ObservableList<MenuItem> items = new TrackableObservableList<MenuItem>() {
      protected void onChanged(Change<MenuItem> var1) {
         while (var1.next()) {
            for (MenuItem var3 : var1.getRemoved()) {
               var3.setParentPopup(null);
            }

            for (MenuItem var5 : var1.getAddedSubList()) {
               if (var5.getParentPopup() != null) {
                  var5.getParentPopup().getItems().remove(var5);
               }

               var5.setParentPopup(ContextMenu.this);
            }
         }
      }
   };
   private static final String DEFAULT_STYLE_CLASS = "context-menu";

   public ContextMenu() {
      this.getStyleClass().setAll(new String[]{"context-menu"});
      this.setAutoHide(true);
      this.setConsumeAutoHidingEvents(false);
   }

   public ContextMenu(MenuItem... var1) {
      this();
      this.items.addAll(var1);
   }

   public final void setOnAction(EventHandler<ActionEvent> var1) {
      this.onActionProperty().set(var1);
   }

   public final EventHandler<ActionEvent> getOnAction() {
      return (EventHandler<ActionEvent>)this.onActionProperty().get();
   }

   public final ObjectProperty<EventHandler<ActionEvent>> onActionProperty() {
      return this.onAction;
   }

   public final ObservableList<MenuItem> getItems() {
      return this.items;
   }

   public void show(Node var1, Side var2, double var3, double var5) {
      if (var1 != null) {
         if (this.getItems().size() != 0) {
            this.getScene().setNodeOrientation(var1.getEffectiveNodeOrientation());
            if (this.getScene().getStylesheets().isEmpty()) {
               this.getScene().getStylesheets().setAll(var1.getScene().getStylesheets());
            }

            HPos var7 = var2 == Side.LEFT ? HPos.LEFT : (var2 == Side.RIGHT ? HPos.RIGHT : HPos.CENTER);
            VPos var8 = var2 == Side.TOP ? VPos.TOP : (var2 == Side.BOTTOM ? VPos.BOTTOM : VPos.CENTER);
            Point2D var9 = Utils.pointRelativeTo(var1, this.prefWidth(-1.0), this.prefHeight(-1.0), var7, var8, var3, var5, true);
            this.doShow(var1, var9.getX(), var9.getY());
         }
      }
   }

   public void show(Node var1, double var2, double var4) {
      if (var1 != null) {
         if (this.getItems().size() != 0) {
            this.getScene().setNodeOrientation(var1.getEffectiveNodeOrientation());
            this.doShow(var1, var2, var4);
         }
      }
   }

   public void hide() {
      if (this.isShowing()) {
         Event.fireEvent(this, new Event(Menu.ON_HIDING));
         super.hide();
         Event.fireEvent(this, new Event(Menu.ON_HIDDEN));
      }
   }

   @Override
   protected Skin<?> createDefaultSkin() {
      return new ContextMenuSkin(this);
   }

   final boolean isShowRelativeToWindow() {
      return this.showRelativeToWindow;
   }

   final void setShowRelativeToWindow(boolean var1) {
      this.showRelativeToWindow = var1;
   }

   private void doShow(Node var1, double var2, double var4) {
      Event.fireEvent(this, new Event(Menu.ON_SHOWING));
      if (this.isShowRelativeToWindow()) {
         Scene var6 = var1 == null ? null : var1.getScene();
         Window var7 = var6 == null ? null : var6.getWindow();
         if (var7 == null) {
            return;
         }

         super.show(var7, var2, var4);
      } else {
         super.show(var1, var2, var4);
      }

      Event.fireEvent(this, new Event(Menu.ON_SHOWN));
   }
}
