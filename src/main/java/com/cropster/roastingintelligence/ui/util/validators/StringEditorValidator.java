package com.cropster.roastingintelligence.ui.util.validators;

import com.cropster.roastingintelligence.ui.WindowManager;
import com.cropster.roastingintelligence.ui.util.Validators;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.function.Supplier;
import javafx.beans.value.ChangeListener;
import javafx.scene.control.TextInputControl;
import javafx.stage.Window;
import org.apache.commons.lang3.StringUtils;

public class StringEditorValidator extends AbstractValidator {
   private final boolean blankAllowed;
   private final int maxLength;
   private final int minLength;
   private final boolean nullAllowed;
   private final AtomicBoolean updatingValue = new AtomicBoolean(false);
   private final ChangeListener<String> valueChangeListener;
   private String originalValue;

   public StringEditorValidator(TextInputControl textInputControl, boolean nullAllowed, boolean blankAllowed, int minLength, int maxLength) {
      super(textInputControl, textInputControl.getParent());
      this.nullAllowed = nullAllowed;
      this.blankAllowed = blankAllowed;
      this.minLength = minLength;
      this.maxLength = maxLength;
      this.valueChangeListener = this.createValueChangeListener();
      this.originalValue = textInputControl.getText();
      this.initListeners(this.createFocusChangeListener(), this.createStatusChangeListener());
      this.initStatusProvider(this.createStatusProvider());
      this.initMessages();
   }

   @Override
   protected ChangeListener<Boolean> createFocusChangeListener() {
      return (s, o, n) -> {
         if (n != null) {
            boolean invalidOnFocusLost = !n && !Validators.isValidValue(this.textInputControl);
            if (invalidOnFocusLost) {
               WindowManager.get().getWindow("VALIDATOR_POPOVER_WINDOW_ID").ifPresent(Window::hide);
               if (this.textInputControl.getProperties().getOrDefault("restoreOldValueOnFocusLost", Boolean.TRUE) instanceof Boolean restore && !restore) {
                  return;
               }

               if ((this.originalValue != null || this.nullAllowed) && (StringUtils.isNotBlank(this.originalValue) || this.blankAllowed)) {
                  this.updateValue(this.originalValue);
               }
            } else if (n) {
               this.statusChangeListener.changed(this.highlightProperty(), null, this.getHighlight());
            }
         }
      };
   }

   @Override
   protected Supplier<String> getDefaultMessageProvider() {
      if (this.textInputControl == null) {
         return super.getDefaultMessageProvider();
      } else if (this.nullAllowed && this.blankAllowed) {
         return () -> org.cropster.roastingintelligence.util.Messages.m_("Value must be between {0} and {1} characters long", this.minLength, this.maxLength);
      } else if (this.nullAllowed) {
         return () -> org.cropster.roastingintelligence.util.Messages.m_(
               "Value must not be blank and must be between {0} and {1} characters long", this.minLength, this.maxLength
            );
      } else {
         return this.blankAllowed
            ? () -> org.cropster.roastingintelligence.util.Messages.m_("Value must be between {0} and {1} characters long", this.minLength, this.maxLength)
            : () -> org.cropster.roastingintelligence.util.Messages.m_(
                  "Value must not be null or blank, and must be between {0} and {1} characters long", this.minLength, this.maxLength
               );
      }
   }

   @Override
   public void install() {
      super.install();
      this.textInputControl.textProperty().addListener(this.valueChangeListener);
   }

   @Override
   public void uninstall() {
      super.uninstall();
      this.textInputControl.textProperty().removeListener(this.valueChangeListener);
   }

   public void updateValue(String newValue) {
      if (this.updatingValue.compareAndSet(false, true)) {
         try {
            this.textInputControl.setText(newValue);
         } finally {
            this.updatingValue.set(false);
         }
      }
   }

   private ChangeListener<String> createValueChangeListener() {
      return (s, o, n) -> {
         if (!this.updatingValue.get()) {
            if (this.textInputControl.getProperties().getOrDefault("restoreOldValueOnError", Boolean.TRUE) instanceof Boolean restore && !restore) {
               return;
            }

            this.originalValue = o;
            if (!Validators.isValidValue(this.textInputControl) && (o != null || this.nullAllowed) && (StringUtils.isNotBlank(o) || this.blankAllowed)) {
               this.updateValue(o);
            }
         }
      };
   }

   private Function<TextInputControl, Validators.Status> createStatusProvider() {
      return textInputControl -> {
         String text = textInputControl.getText();
         if (text == null) {
            return this.nullAllowed ? Validators.Status.NONE : Validators.Status.ERROR;
         } else if (StringUtils.isBlank(text) && !this.blankAllowed) {
            return Validators.Status.ERROR;
         } else {
            int length = text.length();
            return length >= this.minLength && length <= this.maxLength ? Validators.Status.NONE : Validators.Status.ERROR;
         }
      };
   }
}
