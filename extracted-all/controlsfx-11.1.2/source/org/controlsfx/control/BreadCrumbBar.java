package org.controlsfx.control;

import impl.org.controlsfx.skin.BreadCrumbBarSkin;
import java.util.UUID;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Skin;
import javafx.scene.control.TreeItem;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcTo;
import javafx.scene.shape.ClosePath;
import javafx.scene.shape.HLineTo;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.util.Callback;

public class BreadCrumbBar<T> extends ControlsFXControl {
   private static final String STYLE_CLASS_FIRST = "first";
   private final Callback<TreeItem<T>, Button> defaultCrumbNodeFactory = new Callback<TreeItem<T>, Button>() {
      public Button call(TreeItem<T> crumb) {
         return new BreadCrumbBar.BreadCrumbButton(crumb.getValue() != null ? crumb.getValue().toString() : "");
      }
   };
   private final ObjectProperty<TreeItem<T>> selectedCrumb = new SimpleObjectProperty(this, "selectedCrumb");
   private final BooleanProperty autoNavigation = new SimpleBooleanProperty(this, "autoNavigationEnabled", true);
   private final ObjectProperty<Callback<TreeItem<T>, Button>> crumbFactory = new SimpleObjectProperty(this, "crumbFactory");
   private ObjectProperty<EventHandler<BreadCrumbBar.BreadCrumbActionEvent<T>>> onCrumbAction = new ObjectPropertyBase<EventHandler<BreadCrumbBar.BreadCrumbActionEvent<T>>>(
      
   ) {
      protected void invalidated() {
         BreadCrumbBar.this.setEventHandler(BreadCrumbBar.BreadCrumbActionEvent.CRUMB_ACTION, (EventHandler)this.get());
      }

      public Object getBean() {
         return BreadCrumbBar.this;
      }

      public String getName() {
         return "onCrumbAction";
      }
   };
   private static final String DEFAULT_STYLE_CLASS = "bread-crumb-bar";

   public static <T> TreeItem<T> buildTreeModel(T... crumbs) {
      TreeItem<T> subRoot = null;

      for (T crumb : crumbs) {
         TreeItem<T> currentNode = new TreeItem(crumb);
         if (subRoot == null) {
            subRoot = currentNode;
         } else {
            subRoot.getChildren().add(currentNode);
            subRoot = currentNode;
         }
      }

      return subRoot;
   }

   public BreadCrumbBar() {
      this(null);
   }

   public BreadCrumbBar(TreeItem<T> selectedCrumb) {
      this.getStyleClass().add("bread-crumb-bar");
      this.setSelectedCrumb(selectedCrumb);
      this.setCrumbFactory(this.defaultCrumbNodeFactory);
   }

   public final ObjectProperty<TreeItem<T>> selectedCrumbProperty() {
      return this.selectedCrumb;
   }

   public final TreeItem<T> getSelectedCrumb() {
      return (TreeItem<T>)this.selectedCrumb.get();
   }

   public final void setSelectedCrumb(TreeItem<T> selectedCrumb) {
      this.selectedCrumb.set(selectedCrumb);
   }

   public final BooleanProperty autoNavigationEnabledProperty() {
      return this.autoNavigation;
   }

   public final boolean isAutoNavigationEnabled() {
      return this.autoNavigation.get();
   }

   public final void setAutoNavigationEnabled(boolean enabled) {
      this.autoNavigation.set(enabled);
   }

   public final ObjectProperty<Callback<TreeItem<T>, Button>> crumbFactoryProperty() {
      return this.crumbFactory;
   }

   public final void setCrumbFactory(Callback<TreeItem<T>, Button> value) {
      if (value == null) {
         value = this.defaultCrumbNodeFactory;
      }

      this.crumbFactoryProperty().set(value);
   }

   public final Callback<TreeItem<T>, Button> getCrumbFactory() {
      return (Callback<TreeItem<T>, Button>)this.crumbFactory.get();
   }

   public final ObjectProperty<EventHandler<BreadCrumbBar.BreadCrumbActionEvent<T>>> onCrumbActionProperty() {
      return this.onCrumbAction;
   }

   public final void setOnCrumbAction(EventHandler<BreadCrumbBar.BreadCrumbActionEvent<T>> value) {
      this.onCrumbActionProperty().set(value);
   }

   public final EventHandler<BreadCrumbBar.BreadCrumbActionEvent<T>> getOnCrumbAction() {
      return (EventHandler<BreadCrumbBar.BreadCrumbActionEvent<T>>)this.onCrumbActionProperty().get();
   }

   protected Skin<?> createDefaultSkin() {
      return new BreadCrumbBarSkin<T>(this);
   }

   public String getUserAgentStylesheet() {
      return this.getUserAgentStylesheet(BreadCrumbBar.class, "breadcrumbbar.css");
   }

   public static class BreadCrumbActionEvent<TE> extends Event {
      public static final EventType<BreadCrumbBar.BreadCrumbActionEvent<?>> CRUMB_ACTION = new EventType("CRUMB_ACTION" + UUID.randomUUID().toString());
      private final TreeItem<TE> selectedCrumb;

      public BreadCrumbActionEvent(TreeItem<TE> selectedCrumb) {
         super(CRUMB_ACTION);
         this.selectedCrumb = selectedCrumb;
      }

      public TreeItem<TE> getSelectedCrumb() {
         return this.selectedCrumb;
      }
   }

   public static class BreadCrumbButton extends Button {
      private final ObjectProperty<Boolean> first = new SimpleObjectProperty(this, "first");
      private final double arrowWidth = 5.0;
      private final double arrowHeight = 20.0;

      public BreadCrumbButton(String text) {
         this(text, null);
      }

      public BreadCrumbButton(String text, Node gfx) {
         super(text, gfx);
         this.first.set(false);
         this.getStyleClass().addListener(new InvalidationListener() {
            public void invalidated(Observable arg0) {
               BreadCrumbButton.this.updateShape();
            }
         });
         this.updateShape();
      }

      private void updateShape() {
         this.setShape(this.createButtonShape());
      }

      public double getArrowWidth() {
         return 5.0;
      }

      private Path createButtonShape() {
         Path path = new Path();
         MoveTo e1 = new MoveTo(0.0, 0.0);
         path.getElements().add(e1);
         HLineTo e2 = new HLineTo();
         e2.xProperty().bind(this.widthProperty().subtract(5.0));
         path.getElements().add(e2);
         LineTo e3 = new LineTo();
         e3.xProperty().bind(e2.xProperty().add(5.0));
         e3.setY(10.0);
         path.getElements().add(e3);
         LineTo e4 = new LineTo();
         e4.xProperty().bind(e2.xProperty());
         e4.setY(20.0);
         path.getElements().add(e4);
         HLineTo e5 = new HLineTo(0.0);
         path.getElements().add(e5);
         if (!this.getStyleClass().contains("first")) {
            LineTo e6 = new LineTo(5.0, 10.0);
            path.getElements().add(e6);
         } else {
            ArcTo arcTo = new ArcTo();
            arcTo.setSweepFlag(true);
            arcTo.setX(0.0);
            arcTo.setY(0.0);
            arcTo.setRadiusX(15.0);
            arcTo.setRadiusY(15.0);
            path.getElements().add(arcTo);
         }

         ClosePath e7 = new ClosePath();
         path.getElements().add(e7);
         path.setFill(Color.BLACK);
         return path;
      }
   }
}
