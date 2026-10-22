package com.sun.javafx.webkit.theme;

import com.sun.javafx.logging.PlatformLogger;
import com.sun.javafx.logging.PlatformLogger.Level;
import com.sun.javafx.webkit.Accessor;
import com.sun.webkit.LoadListenerClient;
import com.sun.webkit.graphics.Ref;
import com.sun.webkit.graphics.RenderTheme;
import com.sun.webkit.graphics.WCGraphicsContext;
import com.sun.webkit.graphics.WCSize;
import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import javafx.application.Application;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.geometry.Orientation;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Control;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.RadioButton;
import javafx.scene.control.SkinBase;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;

public final class RenderThemeImpl extends RenderTheme {
   private static final PlatformLogger log = PlatformLogger.getLogger(RenderThemeImpl.class.getName());
   private Accessor accessor;
   private boolean isDefault;
   private RenderThemeImpl.Pool<RenderThemeImpl.FormControl> pool;

   public RenderThemeImpl(Accessor var1) {
      this.accessor = var1;
      this.pool = new RenderThemeImpl.Pool<>(var1x -> var1.removeChild(var1x.asControl()), RenderThemeImpl.FormControl.class);
      var1.addViewListener(new RenderThemeImpl.ViewListener(this.pool, var1));
   }

   public RenderThemeImpl() {
      this.isDefault = true;
   }

   private void ensureNotDefault() {
      if (this.isDefault) {
         throw new IllegalStateException("the method should not be called in this context");
      }
   }

   @Override
   protected Ref createWidget(long var1, int var3, int var4, int var5, int var6, int var7, ByteBuffer var8) {
      this.ensureNotDefault();
      Object var9 = this.pool.get(var1);
      RenderThemeImpl.WidgetType var10 = RenderThemeImpl.WidgetType.convert(var3);
      if (var9 == null || ((RenderThemeImpl.FormControl)var9).getType() != var10) {
         if (var9 != null) {
            this.accessor.removeChild(((RenderThemeImpl.FormControl)var9).asControl());
         }

         switch (var10) {
            case TEXTFIELD:
               var9 = new RenderThemeImpl.FormTextField();
               break;
            case BUTTON:
               var9 = new RenderThemeImpl.FormButton();
               break;
            case CHECKBOX:
               var9 = new RenderThemeImpl.FormCheckBox();
               break;
            case RADIOBUTTON:
               var9 = new RenderThemeImpl.FormRadioButton();
               break;
            case MENULIST:
               var9 = new RenderThemeImpl.FormMenuList();
               break;
            case MENULISTBUTTON:
               var9 = new RenderThemeImpl.FormMenuListButton();
               break;
            case SLIDER:
               var9 = new RenderThemeImpl.FormSlider();
               break;
            case PROGRESSBAR:
               var9 = new RenderThemeImpl.FormProgressBar(RenderThemeImpl.WidgetType.PROGRESSBAR);
               break;
            case METER:
               var9 = new RenderThemeImpl.FormProgressBar(RenderThemeImpl.WidgetType.METER);
               break;
            default:
               log.severe("unknown widget index: {0}", new Object[]{var3});
               return null;
         }

         ((RenderThemeImpl.FormControl)var9).asControl().setFocusTraversable(false);
         this.pool.put(var1, (RenderThemeImpl.FormControl)var9, this.accessor.getPage().getUpdateContentCycleID());
         this.accessor.addChild(((RenderThemeImpl.FormControl)var9).asControl());
      }

      ((RenderThemeImpl.FormControl)var9).setState(var4);
      Control var11 = ((RenderThemeImpl.FormControl)var9).asControl();
      if (var11.getWidth() != (double)var5 || var11.getHeight() != (double)var6) {
         var11.resize((double)var5, (double)var6);
      }

      if (var11.isManaged()) {
         var11.setManaged(false);
      }

      if (var8 != null) {
         if (var10 == RenderThemeImpl.WidgetType.SLIDER) {
            Slider var12 = (Slider)var11;
            var8.order(ByteOrder.nativeOrder());
            var12.setOrientation(var8.getInt() == 0 ? Orientation.HORIZONTAL : Orientation.VERTICAL);
            var12.setMax((double)var8.getFloat());
            var12.setMin((double)var8.getFloat());
            var12.setValue((double)var8.getFloat());
         } else if (var10 == RenderThemeImpl.WidgetType.PROGRESSBAR) {
            ProgressBar var13 = (ProgressBar)var11;
            var8.order(ByteOrder.nativeOrder());
            var13.setProgress(var8.getInt() == 1 ? (double)var8.getFloat() : -1.0);
         } else if (var10 == RenderThemeImpl.WidgetType.METER) {
            ProgressBar var14 = (ProgressBar)var11;
            var8.order(ByteOrder.nativeOrder());
            var14.setProgress((double)var8.getFloat());
            var14.setStyle(this.getMeterStyle(var8.getInt()));
         }
      }

      return new RenderThemeImpl.FormControlRef((RenderThemeImpl.FormControl)var9);
   }

   private String getMeterStyle(int var1) {
      switch (var1) {
         case 1:
            return "-fx-accent: yellow";
         case 2:
            return "-fx-accent: red";
         default:
            return "-fx-accent: green";
      }
   }

   @Override
   public void drawWidget(WCGraphicsContext var1, Ref var2, int var3, int var4) {
      this.ensureNotDefault();
      RenderThemeImpl.FormControl var5 = ((RenderThemeImpl.FormControlRef)var2).asFormControl();
      if (var5 != null) {
         Control var6 = var5.asControl();
         if (var6 != null) {
            var1.saveState();
            var1.translate((float)var3, (float)var4);
            Renderer.getRenderer().render(var6, var1);
            var1.restoreState();
         }
      }
   }

   @Override
   public WCSize getWidgetSize(Ref var1) {
      this.ensureNotDefault();
      RenderThemeImpl.FormControl var2 = ((RenderThemeImpl.FormControlRef)var1).asFormControl();
      if (var2 != null) {
         Control var3 = var2.asControl();
         return new WCSize((float)var3.getWidth(), (float)var3.getHeight());
      } else {
         return new WCSize(0.0F, 0.0F);
      }
   }

   @Override
   protected int getRadioButtonSize() {
      String var1 = Application.getUserAgentStylesheet();
      if ("MODENA".equalsIgnoreCase(var1)) {
         return 20;
      } else {
         return "CASPIAN".equalsIgnoreCase(var1) ? 19 : 20;
      }
   }

   @Override
   protected int getSelectionColor(int var1) {
      switch (var1) {
         case 0:
            return -16739329;
         case 1:
            return -1;
         default:
            return 0;
      }
   }

   private static boolean hasState(int var0, int var1) {
      return (var0 & var1) != 0;
   }

   private static final class FormButton extends Button implements RenderThemeImpl.FormControl {
      @Override
      public Control asControl() {
         return this;
      }

      @Override
      public void setState(int var1) {
         this.setDisabled(!RenderThemeImpl.hasState(var1, 4));
         this.setFocused(RenderThemeImpl.hasState(var1, 8));
         this.setHover(RenderThemeImpl.hasState(var1, 32) && !this.isDisabled());
         this.setPressed(RenderThemeImpl.hasState(var1, 16));
         if (this.isPressed()) {
            this.arm();
         } else {
            this.disarm();
         }
      }

      @Override
      public RenderThemeImpl.WidgetType getType() {
         return RenderThemeImpl.WidgetType.BUTTON;
      }
   }

   private static final class FormCheckBox extends CheckBox implements RenderThemeImpl.FormControl {
      @Override
      public Control asControl() {
         return this;
      }

      @Override
      public void setState(int var1) {
         this.setDisabled(!RenderThemeImpl.hasState(var1, 4));
         this.setFocused(RenderThemeImpl.hasState(var1, 8));
         this.setHover(RenderThemeImpl.hasState(var1, 32) && !this.isDisabled());
         this.setSelected(RenderThemeImpl.hasState(var1, 1));
      }

      @Override
      public RenderThemeImpl.WidgetType getType() {
         return RenderThemeImpl.WidgetType.CHECKBOX;
      }
   }

   private interface FormControl extends RenderThemeImpl.Widget {
      Control asControl();

      void setState(int var1);
   }

   private static final class FormControlRef extends Ref {
      private final WeakReference<RenderThemeImpl.FormControl> fcRef;

      private FormControlRef(RenderThemeImpl.FormControl var1) {
         this.fcRef = new WeakReference<>(var1);
      }

      private RenderThemeImpl.FormControl asFormControl() {
         return this.fcRef.get();
      }
   }

   private static final class FormMenuList extends ChoiceBox implements RenderThemeImpl.FormControl {
      private FormMenuList() {
         ArrayList var1 = new ArrayList();
         var1.add("");
         this.setItems(FXCollections.observableList(var1));
      }

      @Override
      public Control asControl() {
         return this;
      }

      @Override
      public void setState(int var1) {
         this.setDisabled(!RenderThemeImpl.hasState(var1, 4));
         this.setFocused(RenderThemeImpl.hasState(var1, 8));
         this.setHover(RenderThemeImpl.hasState(var1, 32) && !this.isDisabled());
      }

      @Override
      public RenderThemeImpl.WidgetType getType() {
         return RenderThemeImpl.WidgetType.MENULIST;
      }
   }

   private static final class FormMenuListButton extends Button implements RenderThemeImpl.FormControl {
      private static final int MAX_WIDTH = 20;
      private static final int MIN_WIDTH = 16;

      @Override
      public Control asControl() {
         return this;
      }

      @Override
      public void setState(int var1) {
         this.setDisabled(!RenderThemeImpl.hasState(var1, 4));
         this.setHover(RenderThemeImpl.hasState(var1, 32));
         this.setPressed(RenderThemeImpl.hasState(var1, 16));
         if (this.isPressed()) {
            this.arm();
         } else {
            this.disarm();
         }
      }

      private FormMenuListButton() {
         this.setSkin(new RenderThemeImpl.FormMenuListButton.Skin());
         this.setFocusTraversable(false);
         this.getStyleClass().add("form-select-button");
      }

      public void resize(double var1, double var3) {
         var1 = var3 > 20.0 ? 20.0 : (var3 < 16.0 ? 16.0 : var3);
         super.resize(var1, var3);
         this.setTranslateX(-var1);
      }

      @Override
      public RenderThemeImpl.WidgetType getType() {
         return RenderThemeImpl.WidgetType.MENULISTBUTTON;
      }

      private final class Skin extends SkinBase {
         Skin() {
            super(FormMenuListButton.this);
            Region var2 = new Region();
            var2.getStyleClass().add("arrow");
            var2.setMaxSize(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY);
            BorderPane var3 = new BorderPane();
            var3.setCenter(var2);
            this.getChildren().add(var3);
         }
      }
   }

   private static final class FormProgressBar extends ProgressBar implements RenderThemeImpl.FormControl {
      private final RenderThemeImpl.WidgetType type;

      private FormProgressBar(RenderThemeImpl.WidgetType var1) {
         this.type = var1;
      }

      @Override
      public Control asControl() {
         return this;
      }

      @Override
      public void setState(int var1) {
         this.setDisabled(!RenderThemeImpl.hasState(var1, 4));
         this.setFocused(RenderThemeImpl.hasState(var1, 8));
         this.setHover(RenderThemeImpl.hasState(var1, 32) && !this.isDisabled());
      }

      @Override
      public RenderThemeImpl.WidgetType getType() {
         return this.type;
      }
   }

   private static final class FormRadioButton extends RadioButton implements RenderThemeImpl.FormControl {
      @Override
      public Control asControl() {
         return this;
      }

      @Override
      public void setState(int var1) {
         this.setDisabled(!RenderThemeImpl.hasState(var1, 4));
         this.setFocused(RenderThemeImpl.hasState(var1, 8));
         this.setHover(RenderThemeImpl.hasState(var1, 32) && !this.isDisabled());
         this.setSelected(RenderThemeImpl.hasState(var1, 1));
      }

      @Override
      public RenderThemeImpl.WidgetType getType() {
         return RenderThemeImpl.WidgetType.RADIOBUTTON;
      }
   }

   private static final class FormSlider extends Slider implements RenderThemeImpl.FormControl {
      @Override
      public Control asControl() {
         return this;
      }

      @Override
      public void setState(int var1) {
         this.setDisabled(!RenderThemeImpl.hasState(var1, 4));
         this.setFocused(RenderThemeImpl.hasState(var1, 8));
         this.setHover(RenderThemeImpl.hasState(var1, 32) && !this.isDisabled());
      }

      @Override
      public RenderThemeImpl.WidgetType getType() {
         return RenderThemeImpl.WidgetType.SLIDER;
      }
   }

   private static final class FormTextField extends TextField implements RenderThemeImpl.FormControl {
      private FormTextField() {
         this.setStyle("-fx-display-caret: false");
      }

      @Override
      public Control asControl() {
         return this;
      }

      @Override
      public void setState(int var1) {
         this.setDisabled(!RenderThemeImpl.hasState(var1, 4));
         this.setEditable(RenderThemeImpl.hasState(var1, 64));
         this.setFocused(RenderThemeImpl.hasState(var1, 8));
         this.setHover(RenderThemeImpl.hasState(var1, 32) && !this.isDisabled());
      }

      @Override
      public RenderThemeImpl.WidgetType getType() {
         return RenderThemeImpl.WidgetType.TEXTFIELD;
      }
   }

   static final class Pool<T extends RenderThemeImpl.Widget> {
      private static final int INITIAL_CAPACITY = 100;
      private int capacity = 100;
      private final LinkedHashMap<Long, Integer> ids = new LinkedHashMap<>();
      private final Map<Long, WeakReference<T>> pool = new HashMap<>();
      private final RenderThemeImpl.Pool.Notifier<T> notifier;
      private final String type;

      Pool(RenderThemeImpl.Pool.Notifier<T> var1, Class<T> var2) {
         this.notifier = var1;
         this.type = var2.getSimpleName();
      }

      T get(long var1) {
         if (RenderThemeImpl.log.isLoggable(Level.FINE)) {
            RenderThemeImpl.log.fine("type: {0}, size: {1}, id: 0x{2}", new Object[]{this.type, this.pool.size(), Long.toHexString(var1)});
         }

         assert this.ids.size() == this.pool.size();

         WeakReference var3 = this.pool.get(var1);
         if (var3 == null) {
            return null;
         } else {
            RenderThemeImpl.Widget var4 = (RenderThemeImpl.Widget)var3.get();
            if (var4 == null) {
               return null;
            } else {
               Integer var5 = this.ids.remove(Long.valueOf(var1));
               this.ids.put(Long.valueOf(var1), var5);
               return (T)var4;
            }
         }
      }

      void put(long var1, T var3, int var4) {
         if (RenderThemeImpl.log.isLoggable(Level.FINEST)) {
            RenderThemeImpl.log.finest("size: {0}, id: 0x{1}, control: {2}", new Object[]{this.pool.size(), Long.toHexString(var1), var3.getType()});
         }

         if (this.ids.size() >= this.capacity) {
            Long var5 = this.ids.keySet().iterator().next();
            Integer var6 = this.ids.get(var5);
            if (var6 != var4) {
               this.ids.remove(var5);
               RenderThemeImpl.Widget var7 = this.pool.remove(var5).get();
               if (var7 != null) {
                  this.notifier.notifyRemoved((T)var7);
               }
            } else {
               this.capacity = Math.min(this.capacity, (int)Math.ceil(1.073741823E9)) * 2;
            }
         }

         this.ids.put(Long.valueOf(var1), Integer.valueOf(var4));
         this.pool.put(var1, new WeakReference<>((T)var3));
      }

      void clear() {
         if (RenderThemeImpl.log.isLoggable(Level.FINE)) {
            RenderThemeImpl.log.fine("size: " + this.pool.size() + ", controls: " + this.pool.values());
         }

         if (this.pool.size() != 0) {
            this.ids.clear();

            for (WeakReference var2 : this.pool.values()) {
               RenderThemeImpl.Widget var3 = (RenderThemeImpl.Widget)var2.get();
               if (var3 != null) {
                  this.notifier.notifyRemoved((T)var3);
               }
            }

            this.pool.clear();
            this.capacity = 100;
         }
      }

      interface Notifier<T> {
         void notifyRemoved(T var1);
      }
   }

   static class ViewListener implements InvalidationListener {
      private final RenderThemeImpl.Pool pool;
      private final Accessor accessor;
      private LoadListenerClient loadListener;

      ViewListener(RenderThemeImpl.Pool var1, Accessor var2) {
         this.pool = var1;
         this.accessor = var2;
      }

      public void invalidated(Observable var1) {
         this.pool.clear();
         if (this.accessor.getPage() != null && this.loadListener == null) {
            this.loadListener = new LoadListenerClient() {
               @Override
               public void dispatchLoadEvent(long var1, int var3, String var4, String var5, double var6, int var8) {
                  if (var3 == 0) {
                     ViewListener.this.pool.clear();
                  }
               }

               @Override
               public void dispatchResourceLoadEvent(long var1, int var3, String var4, String var5, double var6, int var8) {
               }
            };
            this.accessor.getPage().addLoadListenerClient(this.loadListener);
         }
      }
   }

   interface Widget {
      RenderThemeImpl.WidgetType getType();
   }

   static enum WidgetType {
      TEXTFIELD(0),
      BUTTON(1),
      CHECKBOX(2),
      RADIOBUTTON(3),
      MENULIST(4),
      MENULISTBUTTON(5),
      SLIDER(6),
      PROGRESSBAR(7),
      METER(8),
      SCROLLBAR(9);

      private static final HashMap<Integer, RenderThemeImpl.WidgetType> map = new HashMap<>();
      private final int value;

      private WidgetType(int var3) {
         this.value = var3;
      }

      private static RenderThemeImpl.WidgetType convert(int var0) {
         return map.get(var0);
      }

      static {
         for (RenderThemeImpl.WidgetType var3 : values()) {
            map.put(var3.value, var3);
         }
      }
   }
}
