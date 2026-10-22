package javafx.stage;

import com.sun.javafx.FXPermissions;
import com.sun.javafx.collections.TrackableObservableList;
import com.sun.javafx.collections.VetoableListDecorator;
import com.sun.javafx.scene.SceneHelper;
import com.sun.javafx.stage.StageHelper;
import com.sun.javafx.stage.StagePeerListener;
import com.sun.javafx.tk.TKStage;
import com.sun.javafx.tk.Toolkit;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.NamedArg;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.DoublePropertyBase;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.StringPropertyBase;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.collections.ListChangeListener.Change;
import javafx.geometry.NodeOrientation;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCombination;

public class Stage extends Window {
   private boolean inNestedEventLoop = false;
   private static final StagePeerListener.StageAccessor STAGE_ACCESSOR = new StagePeerListener.StageAccessor() {
      @Override
      public void setIconified(Stage var1, boolean var2) {
         var1.iconifiedPropertyImpl().set(var2);
      }

      @Override
      public void setMaximized(Stage var1, boolean var2) {
         var1.maximizedPropertyImpl().set(var2);
      }

      @Override
      public void setResizable(Stage var1, boolean var2) {
         ((Stage.ResizableProperty)var1.resizableProperty()).setNoInvalidate(var2);
      }

      @Override
      public void setFullScreen(Stage var1, boolean var2) {
         var1.fullScreenPropertyImpl().set(var2);
      }

      @Override
      public void setAlwaysOnTop(Stage var1, boolean var2) {
         var1.alwaysOnTopPropertyImpl().set(var2);
      }
   };
   private boolean primary = false;
   private boolean securityDialog = false;
   private boolean important = true;
   private StageStyle style;
   private Modality modality = Modality.NONE;
   private Window owner = null;
   private ReadOnlyBooleanWrapper fullScreen;
   private ObservableList<Image> icons = new VetoableListDecorator<Image>(new TrackableObservableList<Image>() {
      protected void onChanged(Change<Image> var1) {
         ArrayList var2 = new ArrayList();

         for (Image var4 : Stage.this.icons) {
            var2.add(Toolkit.getImageAccessor().getPlatformImage(var4));
         }

         if (Stage.this.getPeer() != null) {
            Stage.this.getPeer().setIcons(var2);
         }
      }
   }) {
      protected void onProposedChange(List<Image> var1, int[] var2) {
         for (Image var4 : var1) {
            if (var4 == null) {
               throw new NullPointerException("icon can not be null.");
            }
         }
      }
   };
   private StringProperty title;
   private ReadOnlyBooleanWrapper iconified;
   private ReadOnlyBooleanWrapper maximized;
   private ReadOnlyBooleanWrapper alwaysOnTop;
   private BooleanProperty resizable;
   private DoubleProperty minWidth;
   private DoubleProperty minHeight;
   private DoubleProperty maxWidth;
   private DoubleProperty maxHeight;
   private final ObjectProperty<KeyCombination> fullScreenExitCombination = new SimpleObjectProperty(this, "fullScreenExitCombination", null);
   private final ObjectProperty<String> fullScreenExitHint = new SimpleObjectProperty(this, "fullScreenExitHint", null);

   public Stage() {
      this(StageStyle.DECORATED);
   }

   public Stage(@NamedArg(value = "style",defaultValue = "DECORATED") StageStyle var1) {
      Toolkit.getToolkit().checkFxUserThread();
      this.initStyle(var1);
      StageHelper.initHelper(this);
   }

   @Override
   public final void setScene(Scene var1) {
      Toolkit.getToolkit().checkFxUserThread();
      super.setScene(var1);
   }

   @Override
   public final void show() {
      super.show();
   }

   final void initSecurityDialog(boolean var1) {
      if (this.hasBeenVisible) {
         throw new IllegalStateException("Cannot set securityDialog once stage has been set visible");
      } else {
         this.securityDialog = var1;
      }
   }

   final boolean isSecurityDialog() {
      return this.securityDialog;
   }

   void setPrimary(boolean var1) {
      this.primary = var1;
   }

   boolean isPrimary() {
      return this.primary;
   }

   void setImportant(boolean var1) {
      this.important = var1;
   }

   private boolean isImportant() {
      return this.important;
   }

   public void showAndWait() {
      Toolkit.getToolkit().checkFxUserThread();
      if (this.isPrimary()) {
         throw new IllegalStateException("Cannot call this method on primary stage");
      } else if (this.isShowing()) {
         throw new IllegalStateException("Stage already visible");
      } else if (!Toolkit.getToolkit().canStartNestedEventLoop()) {
         throw new IllegalStateException("showAndWait is not allowed during animation or layout processing");
      } else {
         assert !this.inNestedEventLoop;

         this.show();
         this.inNestedEventLoop = true;
         Toolkit.getToolkit().enterNestedEventLoop(this);
      }
   }

   public final void initStyle(StageStyle var1) {
      if (this.hasBeenVisible) {
         throw new IllegalStateException("Cannot set style once stage has been set visible");
      } else {
         this.style = var1;
      }
   }

   public final StageStyle getStyle() {
      return this.style;
   }

   public final void initModality(Modality var1) {
      if (this.hasBeenVisible) {
         throw new IllegalStateException("Cannot set modality once stage has been set visible");
      } else if (this.isPrimary()) {
         throw new IllegalStateException("Cannot set modality for the primary stage");
      } else {
         this.modality = var1;
      }
   }

   public final Modality getModality() {
      return this.modality;
   }

   public final void initOwner(Window var1) {
      if (this.hasBeenVisible) {
         throw new IllegalStateException("Cannot set owner once stage has been set visible");
      } else if (this.isPrimary()) {
         throw new IllegalStateException("Cannot set owner for the primary stage");
      } else {
         this.owner = var1;
         Scene var2 = this.getScene();
         if (var2 != null) {
            SceneHelper.parentEffectiveOrientationInvalidated(var2);
         }
      }
   }

   public final Window getOwner() {
      return this.owner;
   }

   public final void setFullScreen(boolean var1) {
      Toolkit.getToolkit().checkFxUserThread();
      this.fullScreenPropertyImpl().set(var1);
      if (this.getPeer() != null) {
         this.getPeer().setFullScreen(var1);
      }
   }

   public final boolean isFullScreen() {
      return this.fullScreen == null ? false : this.fullScreen.get();
   }

   public final ReadOnlyBooleanProperty fullScreenProperty() {
      return this.fullScreenPropertyImpl().getReadOnlyProperty();
   }

   private ReadOnlyBooleanWrapper fullScreenPropertyImpl() {
      if (this.fullScreen == null) {
         this.fullScreen = new ReadOnlyBooleanWrapper(this, "fullScreen");
      }

      return this.fullScreen;
   }

   public final ObservableList<Image> getIcons() {
      return this.icons;
   }

   public final void setTitle(String var1) {
      this.titleProperty().set(var1);
   }

   public final String getTitle() {
      return this.title == null ? null : (String)this.title.get();
   }

   public final StringProperty titleProperty() {
      if (this.title == null) {
         this.title = new StringPropertyBase() {
            protected void invalidated() {
               if (Stage.this.getPeer() != null) {
                  Stage.this.getPeer().setTitle(this.get());
               }
            }

            public Object getBean() {
               return Stage.this;
            }

            public String getName() {
               return "title";
            }
         };
      }

      return this.title;
   }

   public final void setIconified(boolean var1) {
      this.iconifiedPropertyImpl().set(var1);
      if (this.getPeer() != null) {
         this.getPeer().setIconified(var1);
      }
   }

   public final boolean isIconified() {
      return this.iconified == null ? false : this.iconified.get();
   }

   public final ReadOnlyBooleanProperty iconifiedProperty() {
      return this.iconifiedPropertyImpl().getReadOnlyProperty();
   }

   private final ReadOnlyBooleanWrapper iconifiedPropertyImpl() {
      if (this.iconified == null) {
         this.iconified = new ReadOnlyBooleanWrapper(this, "iconified");
      }

      return this.iconified;
   }

   public final void setMaximized(boolean var1) {
      this.maximizedPropertyImpl().set(var1);
      if (this.getPeer() != null) {
         this.getPeer().setMaximized(var1);
      }
   }

   public final boolean isMaximized() {
      return this.maximized == null ? false : this.maximized.get();
   }

   public final ReadOnlyBooleanProperty maximizedProperty() {
      return this.maximizedPropertyImpl().getReadOnlyProperty();
   }

   private final ReadOnlyBooleanWrapper maximizedPropertyImpl() {
      if (this.maximized == null) {
         this.maximized = new ReadOnlyBooleanWrapper(this, "maximized");
      }

      return this.maximized;
   }

   public final void setAlwaysOnTop(boolean var1) {
      this.alwaysOnTopPropertyImpl().set(var1);
      if (this.getPeer() != null) {
         this.getPeer().setAlwaysOnTop(var1);
      }
   }

   public final boolean isAlwaysOnTop() {
      return this.alwaysOnTop == null ? false : this.alwaysOnTop.get();
   }

   public final ReadOnlyBooleanProperty alwaysOnTopProperty() {
      return this.alwaysOnTopPropertyImpl().getReadOnlyProperty();
   }

   private ReadOnlyBooleanWrapper alwaysOnTopPropertyImpl() {
      if (this.alwaysOnTop == null) {
         this.alwaysOnTop = new ReadOnlyBooleanWrapper(this, "alwaysOnTop");
      }

      return this.alwaysOnTop;
   }

   public final void setResizable(boolean var1) {
      this.resizableProperty().set(var1);
   }

   public final boolean isResizable() {
      return this.resizable == null ? true : this.resizable.get();
   }

   public final BooleanProperty resizableProperty() {
      if (this.resizable == null) {
         this.resizable = new Stage.ResizableProperty();
      }

      return this.resizable;
   }

   public final void setMinWidth(double var1) {
      this.minWidthProperty().set(var1);
   }

   public final double getMinWidth() {
      return this.minWidth == null ? 0.0 : this.minWidth.get();
   }

   public final DoubleProperty minWidthProperty() {
      if (this.minWidth == null) {
         this.minWidth = new DoublePropertyBase(0.0) {
            protected void invalidated() {
               if (Stage.this.getPeer() != null) {
                  Stage.this.getPeer().setMinimumSize((int)Math.ceil(this.get()), (int)Math.ceil(Stage.this.getMinHeight()));
               }

               if (Stage.this.getWidth() < Stage.this.getMinWidth()) {
                  Stage.this.setWidth(Stage.this.getMinWidth());
               }
            }

            public Object getBean() {
               return Stage.this;
            }

            public String getName() {
               return "minWidth";
            }
         };
      }

      return this.minWidth;
   }

   public final void setMinHeight(double var1) {
      this.minHeightProperty().set(var1);
   }

   public final double getMinHeight() {
      return this.minHeight == null ? 0.0 : this.minHeight.get();
   }

   public final DoubleProperty minHeightProperty() {
      if (this.minHeight == null) {
         this.minHeight = new DoublePropertyBase(0.0) {
            protected void invalidated() {
               if (Stage.this.getPeer() != null) {
                  Stage.this.getPeer().setMinimumSize((int)Math.ceil(Stage.this.getMinWidth()), (int)Math.ceil(this.get()));
               }

               if (Stage.this.getHeight() < Stage.this.getMinHeight()) {
                  Stage.this.setHeight(Stage.this.getMinHeight());
               }
            }

            public Object getBean() {
               return Stage.this;
            }

            public String getName() {
               return "minHeight";
            }
         };
      }

      return this.minHeight;
   }

   public final void setMaxWidth(double var1) {
      this.maxWidthProperty().set(var1);
   }

   public final double getMaxWidth() {
      return this.maxWidth == null ? Double.MAX_VALUE : this.maxWidth.get();
   }

   public final DoubleProperty maxWidthProperty() {
      if (this.maxWidth == null) {
         this.maxWidth = new DoublePropertyBase(Double.MAX_VALUE) {
            protected void invalidated() {
               if (Stage.this.getPeer() != null) {
                  Stage.this.getPeer().setMaximumSize((int)Math.floor(this.get()), (int)Math.floor(Stage.this.getMaxHeight()));
               }

               if (Stage.this.getWidth() > Stage.this.getMaxWidth()) {
                  Stage.this.setWidth(Stage.this.getMaxWidth());
               }
            }

            public Object getBean() {
               return Stage.this;
            }

            public String getName() {
               return "maxWidth";
            }
         };
      }

      return this.maxWidth;
   }

   public final void setMaxHeight(double var1) {
      this.maxHeightProperty().set(var1);
   }

   public final double getMaxHeight() {
      return this.maxHeight == null ? Double.MAX_VALUE : this.maxHeight.get();
   }

   public final DoubleProperty maxHeightProperty() {
      if (this.maxHeight == null) {
         this.maxHeight = new DoublePropertyBase(Double.MAX_VALUE) {
            protected void invalidated() {
               if (Stage.this.getPeer() != null) {
                  Stage.this.getPeer().setMaximumSize((int)Math.floor(Stage.this.getMaxWidth()), (int)Math.floor(this.get()));
               }

               if (Stage.this.getHeight() > Stage.this.getMaxHeight()) {
                  Stage.this.setHeight(Stage.this.getMaxHeight());
               }
            }

            public Object getBean() {
               return Stage.this;
            }

            public String getName() {
               return "maxHeight";
            }
         };
      }

      return this.maxHeight;
   }

   private void doVisibleChanging(boolean var1) {
      Toolkit var2 = Toolkit.getToolkit();
      if (var1 && this.getPeer() == null) {
         Window var3 = this.getOwner();
         TKStage var4 = var3 == null ? null : var3.getPeer();
         Scene var5 = this.getScene();
         boolean var6 = var5 != null && var5.getEffectiveNodeOrientation() == NodeOrientation.RIGHT_TO_LEFT;
         StageStyle var7 = this.getStyle();
         if (var7 == StageStyle.TRANSPARENT) {
            SecurityManager var8 = System.getSecurityManager();
            if (var8 != null) {
               try {
                  var8.checkPermission(FXPermissions.CREATE_TRANSPARENT_WINDOW_PERMISSION);
               } catch (SecurityException var10) {
                  var7 = StageStyle.UNDECORATED;
               }
            }
         }

         this.setPeer(var2.createTKStage(this, this.isSecurityDialog(), var7, this.isPrimary(), this.getModality(), var4, var6, this.acc));
         this.getPeer().setMinimumSize((int)Math.ceil(this.getMinWidth()), (int)Math.ceil(this.getMinHeight()));
         this.getPeer().setMaximumSize((int)Math.floor(this.getMaxWidth()), (int)Math.floor(this.getMaxHeight()));
         this.setPeerListener(new StagePeerListener(this, STAGE_ACCESSOR));
      }
   }

   private void doVisibleChanged(boolean var1) {
      if (var1) {
         TKStage var2 = this.getPeer();
         var2.setImportant(this.isImportant());
         var2.setResizable(this.isResizable());
         var2.setFullScreen(this.isFullScreen());
         var2.setAlwaysOnTop(this.isAlwaysOnTop());
         var2.setIconified(this.isIconified());
         var2.setMaximized(this.isMaximized());
         var2.setTitle(this.getTitle());
         ArrayList var3 = new ArrayList();

         for (Image var5 : this.icons) {
            var3.add(Toolkit.getImageAccessor().getPlatformImage(var5));
         }

         if (var2 != null) {
            var2.setIcons(var3);
         }
      }

      if (!var1 && this.inNestedEventLoop) {
         this.inNestedEventLoop = false;
         Toolkit.getToolkit().exitNestedEventLoop(this, null);
      }
   }

   public void toFront() {
      if (this.getPeer() != null) {
         this.getPeer().toFront();
      }
   }

   public void toBack() {
      if (this.getPeer() != null) {
         this.getPeer().toBack();
      }
   }

   public void close() {
      this.hide();
   }

   @Override
   Window getWindowOwner() {
      return this.getOwner();
   }

   public final void setFullScreenExitKeyCombination(KeyCombination var1) {
      this.fullScreenExitCombination.set(var1);
   }

   public final KeyCombination getFullScreenExitKeyCombination() {
      return (KeyCombination)this.fullScreenExitCombination.get();
   }

   public final ObjectProperty<KeyCombination> fullScreenExitKeyProperty() {
      return this.fullScreenExitCombination;
   }

   public final void setFullScreenExitHint(String var1) {
      this.fullScreenExitHint.set(var1);
   }

   public final String getFullScreenExitHint() {
      return (String)this.fullScreenExitHint.get();
   }

   public final ObjectProperty<String> fullScreenExitHintProperty() {
      return this.fullScreenExitHint;
   }

   static {
      StageHelper.setStageAccessor(new StageHelper.StageAccessor() {
         @Override
         public void doVisibleChanging(Window var1, boolean var2) {
            ((Stage)var1).doVisibleChanging(var2);
         }

         @Override
         public void doVisibleChanged(Window var1, boolean var2) {
            ((Stage)var1).doVisibleChanged(var2);
         }

         @Override
         public void initSecurityDialog(Stage var1, boolean var2) {
            var1.initSecurityDialog(var2);
         }

         @Override
         public void setPrimary(Stage var1, boolean var2) {
            var1.setPrimary(var2);
         }

         @Override
         public void setImportant(Stage var1, boolean var2) {
            var1.setImportant(var2);
         }
      });
   }

   private class ResizableProperty extends SimpleBooleanProperty {
      private boolean noInvalidate;

      public ResizableProperty() {
         super(Stage.this, "resizable", true);
      }

      void setNoInvalidate(boolean var1) {
         this.noInvalidate = true;
         this.set(var1);
         this.noInvalidate = false;
      }

      protected void invalidated() {
         if (!this.noInvalidate) {
            if (Stage.this.getPeer() != null) {
               Stage.this.applyBounds();
               Stage.this.getPeer().setResizable(this.get());
            }
         }
      }

      public void bind(ObservableValue<? extends Boolean> var1) {
         throw new RuntimeException("Resizable property cannot be bound");
      }
   }
}
