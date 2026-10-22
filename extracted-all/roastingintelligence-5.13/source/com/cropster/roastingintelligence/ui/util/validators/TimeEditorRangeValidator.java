package com.cropster.roastingintelligence.ui.util.validators;

import com.cropster.roastingintelligence.ui.WindowManager;
import com.cropster.roastingintelligence.ui.components.TimeEditor;
import com.cropster.roastingintelligence.ui.util.Validators;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.function.Supplier;
import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.scene.control.TextInputControl;
import javafx.stage.Window;
import javafx.util.StringConverter;

public class TimeEditorRangeValidator extends AbstractValueValidator<Long> {
   private final TimeEditor editor;
   private final AtomicBoolean updatingValue = new AtomicBoolean(false);
   private final Long baseMin;
   private final Long baseMax;
   private Long effectiveMin;
   private Long effectiveMax;
   private Long rangeBindingMin;
   private Long rangeBindingMax;
   private Long originalValue;
   private final ObjectProperty<Long> boundsRevision = new SimpleObjectProperty(0L);

   public TimeEditorRangeValidator(TimeEditor editor, Long initialMin, Long initialMax) {
      super(editor.getInput(), editor);
      this.editor = editor;
      this.baseMin = initialMin;
      this.baseMax = initialMax;
      this.effectiveMin = initialMin;
      this.effectiveMax = initialMax;
      this.originalValue = editor.getValue();
      editor.setNegativeAllowed(initialMin < 0L);
      this.initListeners(this.createFocusChangeListener(), this.createStatusChangeListener(), this.createValueChangeListener());
      this.initStatusProvider(this.createStatusProvider());
      this.initMessages();
      this.dependencies = new Observable[]{this.boundsRevision};
   }

   @Override
   protected ChangeListener<Boolean> createFocusChangeListener() {
      return (s, o, n) -> {
         if (n != null) {
            if (Validators.isTemporaryRangeBindingActive(this.editor)) {
               if (n) {
                  Validators.recordRangeFocusGain(this.editor);
               } else {
                  Validators.recordRangeFocusLoss(this.editor);
               }
            } else {
               if (!n && !Validators.isValidValue(this.editor)) {
                  WindowManager.get().getWindow("VALIDATOR_POPOVER_WINDOW_ID").ifPresent(Window::hide);
                  if (this.editor.getProperties().getOrDefault("restoreOldValueOnFocusLost", Boolean.TRUE) instanceof Boolean restore && !restore) {
                     return;
                  }

                  if (this.originalValue != null) {
                     this.updateValue(this.originalValue);
                     this.restoreInputText(this.originalValue);
                  }
               } else if (n) {
                  this.statusChangeListener.changed(this.highlightProperty(), null, this.getHighlight());
               }
            }
         }
      };
   }

   @Override
   protected ChangeListener<Long> createValueChangeListener() {
      return (s, o, n) -> {
         if (!this.updatingValue.get()) {
            if (!Validators.isTemporaryRangeBindingActive(this.editor)) {
               if (this.editor.getProperties().getOrDefault("restoreOldValueOnError", Boolean.TRUE) instanceof Boolean restore && !restore) {
                  return;
               }

               this.originalValue = o;
               if (!Validators.isValidValue(this.editor) && o != null) {
                  this.updateValue(o);
               }
            }
         }
      };
   }

   @Override
   protected Supplier<String> getDefaultMessageProvider() {
      if (this.editor == null) {
         return super.getDefaultMessageProvider();
      } else {
         StringConverter<Long> valueConverter = this.editor.getInput().getTextFormatter().getValueConverter();
         return () -> org.cropster.roastingintelligence.util.Messages.m_(
               "Value must be between {0} and {1}", valueConverter.toString(this.effectiveMin), valueConverter.toString(this.effectiveMax)
            );
      }
   }

   @Override
   public void install() {
      super.install();
      this.editor.valueProperty().addListener(this.valueChangeListener);
   }

   @Override
   public void uninstall() {
      super.uninstall();
      this.editor.valueProperty().removeListener(this.valueChangeListener);
   }

   public void updateValue(Long newValue) {
      if (this.updatingValue.compareAndSet(false, true)) {
         try {
            this.editor.setValue(newValue);
         } finally {
            this.updatingValue.set(false);
         }
      }
   }

   public Long getEffectiveMin() {
      return this.effectiveMin;
   }

   public Long getEffectiveMax() {
      return this.effectiveMax;
   }

   public void setRangeBindingMin(Long bindingMin) {
      this.rangeBindingMin = bindingMin;
      this.updateEffectiveBounds();
   }

   public void setRangeBindingMax(Long bindingMax) {
      this.rangeBindingMax = bindingMax;
      this.updateEffectiveBounds();
   }

   public void clearRangeBindingBounds() {
      this.rangeBindingMin = null;
      this.rangeBindingMax = null;
      this.updateEffectiveBounds();
   }

   private Function<TextInputControl, Validators.Status> createStatusProvider() {
      return textInputControl -> {
         String text = textInputControl.getText();
         if (text == null) {
            return Validators.Status.ERROR;
         } else {
            try {
               Long number = (Long)textInputControl.getTextFormatter().getValueConverter().fromString(text);
               if (number == null) {
                  return Validators.Status.ERROR;
               } else {
                  long n = number;
                  return n >= this.effectiveMin && n <= this.effectiveMax ? Validators.Status.NONE : Validators.Status.ERROR;
               }
            } catch (Exception var6) {
               return Validators.Status.ERROR;
            }
         }
      };
   }

   private void restoreInputText(Long value) {
      StringConverter<Long> valueConverter = this.editor.getInput().getTextFormatter().getValueConverter();
      this.editor.getInput().setText(valueConverter.toString(value));
   }

   private void updateEffectiveBounds() {
      long nextEffectiveMin = this.baseMin;
      if (this.rangeBindingMin != null) {
         nextEffectiveMin = Math.max(nextEffectiveMin, this.rangeBindingMin);
      }

      long nextEffectiveMax = this.baseMax;
      if (this.rangeBindingMax != null) {
         nextEffectiveMax = Math.min(nextEffectiveMax, this.rangeBindingMax);
      }

      this.effectiveMin = nextEffectiveMin;
      this.effectiveMax = nextEffectiveMax;
      this.boundsRevision.set((Long)this.boundsRevision.get() + 1L);
   }
}
