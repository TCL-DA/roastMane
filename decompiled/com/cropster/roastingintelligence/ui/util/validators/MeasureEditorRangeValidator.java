package com.cropster.roastingintelligence.ui.util.validators;

import com.cropster.roastingintelligence.ui.WindowManager;
import com.cropster.roastingintelligence.ui.components.MeasureEditor;
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
import org.apache.commons.lang3.StringUtils;

public class MeasureEditorRangeValidator extends AbstractValueValidator<Number> {
   private final MeasureEditor editor;
   private final AtomicBoolean updatingValue = new AtomicBoolean(false);
   private final Number baseMin;
   private final Number baseMax;
   private Number effectiveMin;
   private Number effectiveMax;
   private Number rangeBindingMin;
   private Number rangeBindingMax;
   private Number originalValue;
   private final ObjectProperty<Long> boundsRevision = new SimpleObjectProperty(0L);

   public MeasureEditorRangeValidator(MeasureEditor editor, Number initialMin, Number initialMax) {
      super(editor.getInput(), editor);
      this.editor = editor;
      this.baseMin = initialMin;
      this.baseMax = initialMax;
      this.effectiveMin = initialMin;
      this.effectiveMax = initialMax;
      this.originalValue = editor.getValue();
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
                  } else if (this.editor.isNullable()) {
                     this.updateValue(null);
                  }
               } else if (n) {
                  this.statusChangeListener.changed(this.highlightProperty(), null, this.getHighlight());
               }
            }
         }
      };
   }

   @Override
   protected ChangeListener<Number> createValueChangeListener() {
      return (s, o, n) -> {
         if (!this.updatingValue.get()) {
            if (!Validators.isTemporaryRangeBindingActive(this.editor)) {
               if (this.editor.getProperties().getOrDefault("restoreOldValueOnError", Boolean.TRUE) instanceof Boolean restore && !restore) {
                  return;
               }

               this.originalValue = o;
               if (!Validators.isValidValue(this.editor)) {
                  if (o != null) {
                     this.updateValue(o);
                  } else if (this.editor.isNullable()) {
                     this.updateValue(null);
                  }
               }
            }
         }
      };
   }

   @Override
   protected Supplier<String> getDefaultMessageProvider() {
      return () -> org.cropster.roastingintelligence.util.Messages.m_("Value must be between {0} and {1}", this.effectiveMin, this.effectiveMax);
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

   public void updateValue(Number newValue) {
      if (this.updatingValue.compareAndSet(false, true)) {
         try {
            this.editor.setValue(newValue);
         } finally {
            this.updatingValue.set(false);
         }
      }
   }

   public Number getEffectiveMin() {
      return this.effectiveMin;
   }

   public Number getEffectiveMax() {
      return this.effectiveMax;
   }

   public void setRangeBindingMin(Number bindingMin) {
      this.rangeBindingMin = bindingMin;
      this.updateEffectiveBounds();
   }

   public void setRangeBindingMax(Number bindingMax) {
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
         if (StringUtils.isBlank(text)) {
            return this.editor.isNullable() ? Validators.Status.NONE : Validators.Status.ERROR;
         } else {
            try {
               Number number = (Number)textInputControl.getTextFormatter().getValueConverter().fromString(text);
               if (number == null) {
                  return Validators.Status.ERROR;
               } else {
                  double n = number.doubleValue();
                  return n >= this.effectiveMin.doubleValue() && n <= this.effectiveMax.doubleValue() ? Validators.Status.NONE : Validators.Status.ERROR;
               }
            } catch (Exception var6) {
               return Validators.Status.ERROR;
            }
         }
      };
   }

   private void updateEffectiveBounds() {
      double nextEffectiveMin = this.baseMin.doubleValue();
      if (this.rangeBindingMin != null) {
         nextEffectiveMin = Math.max(nextEffectiveMin, this.rangeBindingMin.doubleValue());
      }

      double nextEffectiveMax = this.baseMax.doubleValue();
      if (this.rangeBindingMax != null) {
         nextEffectiveMax = Math.min(nextEffectiveMax, this.rangeBindingMax.doubleValue());
      }

      this.effectiveMin = nextEffectiveMin;
      this.effectiveMax = nextEffectiveMax;
      this.boundsRevision.set((Long)this.boundsRevision.get() + 1L);
   }
}
