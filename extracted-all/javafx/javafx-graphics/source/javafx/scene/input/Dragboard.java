package javafx.scene.input;

import com.sun.javafx.scene.input.DragboardHelper;
import com.sun.javafx.tk.PermissionHelper;
import com.sun.javafx.tk.TKClipboard;
import java.util.Set;
import javafx.scene.image.Image;

public final class Dragboard extends Clipboard {
   private boolean dataAccessRestricted = true;

   Dragboard(TKClipboard var1) {
      super(var1);
   }

   @Override
   Object getContentImpl(DataFormat var1) {
      if (this.dataAccessRestricted) {
         PermissionHelper.checkClipboardPermission();
      }

      return super.getContentImpl(var1);
   }

   public final Set<TransferMode> getTransferModes() {
      return this.peer.getTransferModes();
   }

   TKClipboard getPeer() {
      return this.peer;
   }

   static Dragboard createDragboard(TKClipboard var0) {
      return new Dragboard(var0);
   }

   public void setDragView(Image var1, double var2, double var4) {
      this.peer.setDragView(var1);
      this.peer.setDragViewOffsetX(var2);
      this.peer.setDragViewOffsetY(var4);
   }

   public void setDragView(Image var1) {
      this.peer.setDragView(var1);
   }

   public void setDragViewOffsetX(double var1) {
      this.peer.setDragViewOffsetX(var1);
   }

   public void setDragViewOffsetY(double var1) {
      this.peer.setDragViewOffsetY(var1);
   }

   public Image getDragView() {
      return this.peer.getDragView();
   }

   public double getDragViewOffsetX() {
      return this.peer.getDragViewOffsetX();
   }

   public double getDragViewOffsetY() {
      return this.peer.getDragViewOffsetY();
   }

   static {
      DragboardHelper.setDragboardAccessor(new DragboardHelper.DragboardAccessor() {
         @Override
         public void setDataAccessRestriction(Dragboard var1, boolean var2) {
            var1.dataAccessRestricted = var2;
         }

         @Override
         public TKClipboard getPeer(Dragboard var1) {
            return var1.getPeer();
         }

         @Override
         public Dragboard createDragboard(TKClipboard var1) {
            return Dragboard.createDragboard(var1);
         }
      });
   }
}
