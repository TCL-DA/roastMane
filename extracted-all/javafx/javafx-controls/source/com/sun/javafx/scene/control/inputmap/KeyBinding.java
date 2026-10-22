package com.sun.javafx.scene.control.inputmap;

import com.sun.javafx.tk.Toolkit;
import com.sun.javafx.util.Utils;
import java.util.Objects;
import javafx.event.EventType;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class KeyBinding {
   private final KeyCode code;
   private final EventType<KeyEvent> eventType;
   private KeyBinding.OptionalBoolean shift = KeyBinding.OptionalBoolean.FALSE;
   private KeyBinding.OptionalBoolean ctrl = KeyBinding.OptionalBoolean.FALSE;
   private KeyBinding.OptionalBoolean alt = KeyBinding.OptionalBoolean.FALSE;
   private KeyBinding.OptionalBoolean meta = KeyBinding.OptionalBoolean.FALSE;

   public KeyBinding(KeyCode var1) {
      this(var1, null);
   }

   public KeyBinding(EventType<KeyEvent> var1) {
      this(null, var1);
   }

   public KeyBinding(KeyCode var1, EventType<KeyEvent> var2) {
      this.code = var1;
      this.eventType = var2 != null ? var2 : KeyEvent.KEY_PRESSED;
   }

   public final KeyBinding shift() {
      return this.shift(KeyBinding.OptionalBoolean.TRUE);
   }

   public final KeyBinding shift(KeyBinding.OptionalBoolean var1) {
      this.shift = var1;
      return this;
   }

   public final KeyBinding ctrl() {
      return this.ctrl(KeyBinding.OptionalBoolean.TRUE);
   }

   public final KeyBinding ctrl(KeyBinding.OptionalBoolean var1) {
      this.ctrl = var1;
      return this;
   }

   public final KeyBinding alt() {
      return this.alt(KeyBinding.OptionalBoolean.TRUE);
   }

   public final KeyBinding alt(KeyBinding.OptionalBoolean var1) {
      this.alt = var1;
      return this;
   }

   public final KeyBinding meta() {
      return this.meta(KeyBinding.OptionalBoolean.TRUE);
   }

   public final KeyBinding meta(KeyBinding.OptionalBoolean var1) {
      this.meta = var1;
      return this;
   }

   public final KeyBinding shortcut() {
      if (Toolkit.getToolkit().getClass().getName().endsWith("StubToolkit")) {
         return Utils.isMac() ? this.meta() : this.ctrl();
      } else {
         switch (Toolkit.getToolkit().getPlatformShortcutKey()) {
            case SHIFT:
               return this.shift();
            case CONTROL:
               return this.ctrl();
            case ALT:
               return this.alt();
            case META:
               return this.meta();
            default:
               return this;
         }
      }
   }

   public final KeyCode getCode() {
      return this.code;
   }

   public final EventType<KeyEvent> getType() {
      return this.eventType;
   }

   public final KeyBinding.OptionalBoolean getShift() {
      return this.shift;
   }

   public final KeyBinding.OptionalBoolean getCtrl() {
      return this.ctrl;
   }

   public final KeyBinding.OptionalBoolean getAlt() {
      return this.alt;
   }

   public final KeyBinding.OptionalBoolean getMeta() {
      return this.meta;
   }

   public int getSpecificity(KeyEvent var1) {
      int var2 = 0;
      if (this.code != null && this.code != var1.getCode()) {
         return 0;
      } else {
         var2 = 1;
         if (!this.shift.equals(var1.isShiftDown())) {
            return 0;
         } else {
            if (this.shift != KeyBinding.OptionalBoolean.ANY) {
               var2++;
            }

            if (!this.ctrl.equals(var1.isControlDown())) {
               return 0;
            } else {
               if (this.ctrl != KeyBinding.OptionalBoolean.ANY) {
                  var2++;
               }

               if (!this.alt.equals(var1.isAltDown())) {
                  return 0;
               } else {
                  if (this.alt != KeyBinding.OptionalBoolean.ANY) {
                     var2++;
                  }

                  if (!this.meta.equals(var1.isMetaDown())) {
                     return 0;
                  } else {
                     if (this.meta != KeyBinding.OptionalBoolean.ANY) {
                        var2++;
                     }

                     return this.eventType != null && this.eventType != var1.getEventType() ? 0 : var2 + 1;
                  }
               }
            }
         }
      }
   }

   @Override
   public String toString() {
      return "KeyBinding [code="
         + this.code
         + ", shift="
         + this.shift
         + ", ctrl="
         + this.ctrl
         + ", alt="
         + this.alt
         + ", meta="
         + this.meta
         + ", type="
         + this.eventType
         + "]";
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof KeyBinding)) {
         return false;
      } else {
         KeyBinding var2 = (KeyBinding)var1;
         return Objects.equals(this.getCode(), var2.getCode())
            && Objects.equals(this.eventType, var2.eventType)
            && Objects.equals(this.getShift(), var2.getShift())
            && Objects.equals(this.getCtrl(), var2.getCtrl())
            && Objects.equals(this.getAlt(), var2.getAlt())
            && Objects.equals(this.getMeta(), var2.getMeta());
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.getCode(), this.eventType, this.getShift(), this.getCtrl(), this.getAlt(), this.getMeta());
   }

   public static KeyBinding toKeyBinding(KeyEvent var0) {
      KeyBinding var1 = new KeyBinding(var0.getCode(), var0.getEventType());
      if (var0.isShiftDown()) {
         var1.shift();
      }

      if (var0.isControlDown()) {
         var1.ctrl();
      }

      if (var0.isAltDown()) {
         var1.alt();
      }

      if (var0.isShortcutDown()) {
         var1.shortcut();
      }

      return var1;
   }

   public static enum OptionalBoolean {
      TRUE,
      FALSE,
      ANY;

      public boolean equals(boolean var1) {
         if (this == ANY) {
            return true;
         } else {
            return var1 && this == TRUE ? true : !var1 && this == FALSE;
         }
      }
   }
}
