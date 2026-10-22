package impl.org.controlsfx.skin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javafx.beans.value.ChangeListener;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.SkinBase;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeItem.TreeModificationEvent;
import javafx.util.Callback;
import org.controlsfx.control.BreadCrumbBar;

public class BreadCrumbBarSkin<T> extends SkinBase<BreadCrumbBar<T>> {
   private static final String STYLE_CLASS_FIRST = "first";
   private final ChangeListener<TreeItem<T>> selectedPathChangeListener = (obs, oldItem, newItem) -> this.updateSelectedPath(newItem, oldItem);
   private final EventHandler<TreeModificationEvent<Object>> treeChildrenModifiedHandler = args -> this.updateBreadCrumbs();

   public BreadCrumbBarSkin(BreadCrumbBar<T> control) {
      super(control);
      control.selectedCrumbProperty().addListener(this.selectedPathChangeListener);
      this.updateSelectedPath((TreeItem<T>)((BreadCrumbBar)this.getSkinnable()).selectedCrumbProperty().get(), null);
   }

   private void updateSelectedPath(TreeItem<T> newTarget, TreeItem<T> oldTarget) {
      if (oldTarget != null) {
         oldTarget.removeEventHandler(TreeItem.childrenModificationEvent(), this.treeChildrenModifiedHandler);
      }

      if (newTarget != null) {
         newTarget.addEventHandler(TreeItem.childrenModificationEvent(), this.treeChildrenModifiedHandler);
      }

      this.updateBreadCrumbs();
   }

   private void updateBreadCrumbs() {
      BreadCrumbBar<T> buttonBar = (BreadCrumbBar<T>)this.getSkinnable();
      TreeItem<T> pathTarget = buttonBar.getSelectedCrumb();
      Callback<TreeItem<T>, Button> factory = buttonBar.getCrumbFactory();
      this.getChildren().clear();
      if (pathTarget != null) {
         List<TreeItem<T>> crumbs = this.constructFlatPath(pathTarget);

         for (int i = 0; i < crumbs.size(); i++) {
            Button crumb = this.createCrumb(factory, crumbs.get(i));
            crumb.setMnemonicParsing(false);
            if (i == 0) {
               if (!crumb.getStyleClass().contains("first")) {
                  crumb.getStyleClass().add("first");
               }
            } else {
               crumb.getStyleClass().remove("first");
            }

            this.getChildren().add(crumb);
         }
      }
   }

   protected void layoutChildren(double x, double y, double w, double h) {
      for (int i = 0; i < this.getChildren().size(); i++) {
         Node n = (Node)this.getChildren().get(i);
         double nw = this.snapSizeX(n.prefWidth(h));
         double nh = this.snapSizeY(n.prefHeight(-1.0));
         if (i > 0) {
            double ins = n instanceof BreadCrumbBar.BreadCrumbButton ? ((BreadCrumbBar.BreadCrumbButton)n).getArrowWidth() : 0.0;
            x = this.snapPositionX(x - ins);
         }

         n.resize(nw, nh);
         n.relocate(x, y);
         x += nw;
      }
   }

   private List<TreeItem<T>> constructFlatPath(TreeItem<T> bottomMost) {
      List<TreeItem<T>> path = new ArrayList<>();
      TreeItem<T> current = bottomMost;

      do {
         path.add(current);
         current = current.getParent();
      } while (current != null);

      Collections.reverse(path);
      return path;
   }

   private Button createCrumb(Callback<TreeItem<T>, Button> factory, TreeItem<T> selectedCrumb) {
      Button crumb = (Button)factory.call(selectedCrumb);
      crumb.getStyleClass().add("crumb");
      crumb.setOnAction(ae -> this.onBreadCrumbAction(selectedCrumb));
      return crumb;
   }

   protected void onBreadCrumbAction(TreeItem<T> crumbModel) {
      BreadCrumbBar<T> breadCrumbBar = (BreadCrumbBar<T>)this.getSkinnable();
      Event.fireEvent(breadCrumbBar, new BreadCrumbBar.BreadCrumbActionEvent(crumbModel));
      if (breadCrumbBar.isAutoNavigationEnabled()) {
         breadCrumbBar.setSelectedCrumb(crumbModel);
      }
   }
}
