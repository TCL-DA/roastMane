package com.cropster.roastingintelligence.ui.util;

import com.cropster.roastingintelligence.ui.components.MeasureEditor;
import com.cropster.roastingintelligence.ui.components.TimeEditor;
import com.cropster.roastingintelligence.ui.util.validators.MeasureEditorRangeValidator;
import com.cropster.roastingintelligence.ui.util.validators.MeasureRangeBindingSupport;
import com.cropster.roastingintelligence.ui.util.validators.RangeBindingEditorSupport;
import com.cropster.roastingintelligence.ui.util.validators.StringEditorValidator;
import com.cropster.roastingintelligence.ui.util.validators.TimeEditorRangeValidator;
import com.cropster.roastingintelligence.ui.util.validators.TimeRangeBindingSupport;
import java.time.Duration;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.scene.Node;
import javafx.scene.control.TextInputControl;
import org.apache.commons.lang3.Validate;

public final class Validators {
   public static final String RESTORE_OLD_VALUE_ON_ERROR = "restoreOldValueOnError";
   public static final String RESTORE_OLD_VALUE_ON_FOCUS_LOST = "restoreOldValueOnFocusLost";
   public static final double ABSOLUTE_MIN_VALUE = -9999999.999;
   public static final double ABSOLUTE_MAX_VALUE = 9999999.999;
   public static final long ABSOLUTE_MIN_TIME = -Duration.ofHours(2L).toMillis();
   public static final long ABSOLUTE_MAX_TIME = Duration.ofHours(2L).toMillis();
   public static final double MAX_AIR = 10000.0;
   public static final double MAX_GAS = 10000.0;
   public static final double MAX_OFFSET = 10000.0;
   public static final double MAX_PAGE_SIZE = 10000.0;
   public static final double MAX_ROR = 1000.0;
   public static final double MAX_TEMPERATURE = 10000.0;
   public static final double MAX_TEMPERATURE_DROP = 1000.0;
   public static final int MAX_SOUND_REPETITION = 100;
   public static final int MAX_GAS_CURVE_AS_COMMENTS_THRESHOLD = 1000;
   private static final String NULL_EDITOR_MESSAGE = "Null 'editor' parameter";
   private static final String LO_VALUE_CHANGE_LISTENER = "loValueChangeListener";
   private static final String HI_VALUE_CHANGE_LISTENER = "hiValueChangeListener";
   private static final String LO_TEXT_CHANGE_LISTENER = "loTextChangeListener";
   private static final String HI_TEXT_CHANGE_LISTENER = "hiTextChangeListener";
   public static final String VALIDATOR_POPOVER_WINDOW_ID = "VALIDATOR_POPOVER_WINDOW_ID";

   public static Validators.Validator setValidator(MeasureEditor editor) {
      return setValidator(editor, -9999999.999, 9999999.999);
   }

   public static Validators.Validator setValidator(MeasureEditor editor, Number max) {
      return setValidator(editor, 0.0, max);
   }

   public static Validators.Validator setValidator(MeasureEditor editor, Number min, Number max) {
      if (min == null) {
         min = -9999999.999;
      }

      if (max == null) {
         max = 9999999.999;
      }

      Validate.isTrue(min.doubleValue() >= -9999999.999, "'min' (%f) not greater than or equal to %f", new Object[]{min, -9999999.999});
      Validate.isTrue(max.doubleValue() <= 9999999.999, "'max' (%f) not less than or equal to %f", new Object[]{max, 9999999.999});
      Validate.isTrue(max.doubleValue() >= min.doubleValue(), "'max' (%f) not greater than or equal to 'min' (%f)", new Object[]{max, min});
      MeasureEditorRangeValidator validator = (MeasureEditorRangeValidator)getValidator(editor);
      if (validator != null) {
         removeValidator(editor);
      }

      validator = new MeasureEditorRangeValidator(editor, min, max);
      validator.install();
      editor.getProperties().put(MeasureEditorRangeValidator.class, validator);
      return validator;
   }

   public static Validators.Validator removeValidator(MeasureEditor editor) {
      Validators.Validator validator = getValidator(editor);
      if (validator != null) {
         validator.uninstall();
         editor.getProperties().remove(MeasureEditorRangeValidator.class);
      }

      return validator;
   }

   public static Validators.Validator getValidator(MeasureEditor editor) {
      Objects.requireNonNull(editor, "Null 'editor' parameter");
      return (Validators.Validator)editor.getProperties().get(MeasureEditorRangeValidator.class);
   }

   public static boolean isValidValue(MeasureEditor editor) {
      MeasureEditorRangeValidator validator = (MeasureEditorRangeValidator)getValidator(editor);
      return validator != null ? validator.highlightProperty().get() == Validators.Status.NONE : true;
   }

   public static Number getSafeValue(MeasureEditor editor) {
      MeasureEditorRangeValidator validator = (MeasureEditorRangeValidator)getValidator(editor);
      double value = editor.getValue().doubleValue();
      return validator != null
         ? Math.max(validator.getEffectiveMin().doubleValue(), Math.min(validator.getEffectiveMax().doubleValue(), value))
         : Math.max(-9999999.999, Math.min(9999999.999, value));
   }

   public static void bindRanges(MeasureEditor loEditor, MeasureEditor hiEditor, Number offset) {
      bindRanges(loEditor, hiEditor, offset, false);
   }

   public static void bindRanges(MeasureEditor loEditor, MeasureEditor hiEditor, Number offset, boolean allowTemporaryInvalidValues) {
      Objects.requireNonNull(loEditor, "Null 'loEditor' parameter");
      Objects.requireNonNull(hiEditor, "Null 'hiEditor' parameter");
      Objects.requireNonNull(offset, "Null 'offset' parameter");
      double dOffset = offset.doubleValue();
      Validate.finite(dOffset, "'offset' (%f) not finite", new Object[]{dOffset});
      Validate.isTrue(dOffset >= 0.0, "'offset' (%f) not greater than or equal to 0.0", dOffset);
      MeasureEditorRangeValidator loValidator = (MeasureEditorRangeValidator)loEditor.getProperties().get(MeasureEditorRangeValidator.class);
      if (loValidator == null) {
         throw new IllegalArgumentException("No validator found for 'loEditor' parameter");
      } else {
         MeasureEditorRangeValidator hiValidator = (MeasureEditorRangeValidator)hiEditor.getProperties().get(MeasureEditorRangeValidator.class);
         if (hiValidator == null) {
            throw new IllegalArgumentException("No validator found for 'hiEditor' parameter");
         } else {
            unbindRanges(loEditor, hiEditor);
            RangeBindingEditorSupport.initializeRangeBindingEditorProperties(loEditor, hiEditor, allowTemporaryInvalidValues);
            MeasureRangeBindingSupport.initializeRangeBinding(loEditor, hiEditor, loValidator, hiValidator, dOffset);
            if (allowTemporaryInvalidValues) {
               MeasureRangeBindingSupport.synchronizeLoToHiTemporary(loEditor, hiValidator, dOffset);
               MeasureRangeBindingSupport.synchronizeHiToLoTemporary(hiEditor, loValidator, dOffset);
               ChangeListener<String> loTextChangeListener = (s, o, n) -> MeasureRangeBindingSupport.synchronizeTextToValue(loEditor, loValidator);
               loEditor.getInput().textProperty().addListener(loTextChangeListener);
               loEditor.getProperties().put("loTextChangeListener", loTextChangeListener);
               ChangeListener<String> hiTextChangeListener = (s, o, n) -> MeasureRangeBindingSupport.synchronizeTextToValue(hiEditor, hiValidator);
               hiEditor.getInput().textProperty().addListener(hiTextChangeListener);
               hiEditor.getProperties().put("hiTextChangeListener", hiTextChangeListener);
               ChangeListener<Boolean> rangeFocusChangeListener = (s, o, n) -> {
                  if (Boolean.FALSE.equals(n)) {
                     Platform.runLater(() -> rollbackMeasurePairIfInvalidOnFocusExit(loEditor, hiEditor, loValidator, hiValidator, dOffset));
                  }
               };
               loEditor.getInput().focusedProperty().addListener(rangeFocusChangeListener);
               hiEditor.getInput().focusedProperty().addListener(rangeFocusChangeListener);
               loEditor.getProperties().put("rangeFocusChangeListener", rangeFocusChangeListener);
               hiEditor.getProperties().put("rangeFocusChangeListener", rangeFocusChangeListener);
            }

            ChangeListener<Number> loValueChangeListener = (s, o, n) -> {
               if (n != null) {
                  if (allowTemporaryInvalidValues) {
                     MeasureRangeBindingSupport.synchronizeLoToHiTemporary(loEditor, hiValidator, dOffset);
                     if (!loEditor.getInput().isFocused() && !hiEditor.getInput().isFocused()) {
                        MeasureRangeBindingSupport.persistPairAsRollbackTargetIfValid(loEditor, hiEditor, loValidator, hiValidator, "oldEditorValue");
                     }
                  } else {
                     MeasureRangeBindingSupport.synchronizeLoToHi(loEditor, hiEditor, hiValidator, dOffset);
                  }
               }
            };
            loEditor.valueProperty().addListener(loValueChangeListener);
            loEditor.getProperties().put("loValueChangeListener", loValueChangeListener);
            ChangeListener<Number> hiValueChangeListener = (s, o, n) -> {
               if (n != null) {
                  if (allowTemporaryInvalidValues) {
                     MeasureRangeBindingSupport.synchronizeHiToLoTemporary(hiEditor, loValidator, dOffset);
                     if (!loEditor.getInput().isFocused() && !hiEditor.getInput().isFocused()) {
                        MeasureRangeBindingSupport.persistPairAsRollbackTargetIfValid(loEditor, hiEditor, loValidator, hiValidator, "oldEditorValue");
                     }
                  } else {
                     MeasureRangeBindingSupport.synchronizeHiToLo(loEditor, hiEditor, loValidator, dOffset);
                  }
               }
            };
            hiEditor.valueProperty().addListener(hiValueChangeListener);
            hiEditor.getProperties().put("hiValueChangeListener", hiValueChangeListener);
         }
      }
   }

   private static void rollbackMeasurePairIfInvalidOnFocusExit(
      MeasureEditor loEditor, MeasureEditor hiEditor, MeasureEditorRangeValidator loValidator, MeasureEditorRangeValidator hiValidator, double offset
   ) {
      MeasureRangeBindingSupport.rollbackPairIfInvalidOnFocusExit(loEditor, hiEditor, loValidator, hiValidator, offset, "oldEditorValue");
   }

   public static void unbindRanges(MeasureEditor loEditor, MeasureEditor hiEditor) {
      if (loEditor != null && hiEditor != null) {
         ChangeListener<Number> loValueChangeListener = (ChangeListener<Number>)loEditor.getProperties().remove("loValueChangeListener");
         if (loValueChangeListener != null) {
            loEditor.valueProperty().removeListener(loValueChangeListener);
         }

         ChangeListener<Number> hiValueChangeListener = (ChangeListener<Number>)hiEditor.getProperties().remove("hiValueChangeListener");
         if (hiValueChangeListener != null) {
            hiEditor.valueProperty().removeListener(hiValueChangeListener);
         }

         ChangeListener<String> loTextChangeListener = (ChangeListener<String>)loEditor.getProperties().remove("loTextChangeListener");
         if (loTextChangeListener != null) {
            loEditor.getInput().textProperty().removeListener(loTextChangeListener);
         }

         ChangeListener<String> hiTextChangeListener = (ChangeListener<String>)hiEditor.getProperties().remove("hiTextChangeListener");
         if (hiTextChangeListener != null) {
            hiEditor.getInput().textProperty().removeListener(hiTextChangeListener);
         }

         MeasureEditorRangeValidator loValidator = (MeasureEditorRangeValidator)loEditor.getProperties().get(MeasureEditorRangeValidator.class);
         if (loValidator != null) {
            loValidator.clearRangeBindingBounds();
         }

         MeasureEditorRangeValidator hiValidator = (MeasureEditorRangeValidator)hiEditor.getProperties().get(MeasureEditorRangeValidator.class);
         if (hiValidator != null) {
            hiValidator.clearRangeBindingBounds();
         }

         RangeBindingEditorSupport.removeRangeBindingEditorProperties(loEditor, hiEditor);
      }
   }

   public static Validators.Validator setValidator(TimeEditor editor) {
      return setValidator(editor, ABSOLUTE_MIN_TIME, ABSOLUTE_MAX_TIME);
   }

   public static Validators.Validator setValidator(TimeEditor editor, Long max) {
      return setValidator(editor, 0L, max);
   }

   public static Validators.Validator setValidator(TimeEditor editor, Long min, Long max) {
      if (min == null) {
         min = ABSOLUTE_MIN_TIME;
      }

      if (max == null) {
         max = ABSOLUTE_MAX_TIME;
      }

      Validate.isTrue(min >= ABSOLUTE_MIN_TIME, "'min' (%d) not greater than or equal to %d", new Object[]{min, ABSOLUTE_MIN_TIME});
      Validate.isTrue(max <= ABSOLUTE_MAX_TIME, "'max' (%d) not less than or equal to %d", new Object[]{max, ABSOLUTE_MAX_TIME});
      Validate.isTrue(max >= min, "'max' (%d) not greater than or equal to 'min' (%d)", new Object[]{max, min});
      TimeEditorRangeValidator validator = (TimeEditorRangeValidator)getValidator(editor);
      if (validator != null) {
         removeValidator(editor);
      }

      validator = new TimeEditorRangeValidator(editor, min, max);
      validator.install();
      editor.getProperties().put(TimeEditorRangeValidator.class, validator);
      return validator;
   }

   public static Validators.Validator removeValidator(TimeEditor editor) {
      Validators.Validator validator = getValidator(editor);
      if (validator != null) {
         validator.uninstall();
         editor.getProperties().remove(TimeEditorRangeValidator.class);
      }

      return validator;
   }

   public static Validators.Validator getValidator(TimeEditor editor) {
      Objects.requireNonNull(editor, "Null 'editor' parameter");
      return (Validators.Validator)editor.getProperties().get(TimeEditorRangeValidator.class);
   }

   public static boolean isValidValue(TimeEditor editor) {
      TimeEditorRangeValidator validator = (TimeEditorRangeValidator)getValidator(editor);
      return validator != null ? validator.highlightProperty().get() == Validators.Status.NONE : true;
   }

   public static Number getSafeValue(TimeEditor editor) {
      TimeEditorRangeValidator validator = (TimeEditorRangeValidator)getValidator(editor);
      long value = editor.getValue();
      return validator != null
         ? Math.max(validator.getEffectiveMin(), Math.min(validator.getEffectiveMax(), value))
         : Math.max(ABSOLUTE_MIN_TIME, Math.min(ABSOLUTE_MAX_TIME, value));
   }

   public static void bindRanges(TimeEditor loEditor, TimeEditor hiEditor, Long offset) {
      bindRanges(loEditor, hiEditor, offset, false);
   }

   public static void bindRanges(TimeEditor loEditor, TimeEditor hiEditor, Long offset, boolean allowTemporaryInvalidValues) {
      Objects.requireNonNull(loEditor, "Null 'loEditor' parameter");
      Objects.requireNonNull(hiEditor, "Null 'hiEditor' parameter");
      Objects.requireNonNull(offset, "Null 'offset' parameter");
      long lOffset = offset;
      Validate.isTrue(lOffset >= 0L, "'offset' (%d) not greater than or equal to 0", lOffset);
      TimeEditorRangeValidator loValidator = (TimeEditorRangeValidator)loEditor.getProperties().get(TimeEditorRangeValidator.class);
      if (loValidator == null) {
         throw new IllegalArgumentException("No validator found for 'loEditor' parameter");
      } else {
         TimeEditorRangeValidator hiValidator = (TimeEditorRangeValidator)hiEditor.getProperties().get(TimeEditorRangeValidator.class);
         if (hiValidator == null) {
            throw new IllegalArgumentException("No validator found for 'hiEditor' parameter");
         } else {
            unbindRanges(loEditor, hiEditor);
            RangeBindingEditorSupport.initializeRangeBindingEditorProperties(loEditor, hiEditor, allowTemporaryInvalidValues);
            TimeRangeBindingSupport.initializeRangeBinding(loEditor, hiEditor, loValidator, hiValidator, lOffset);
            if (allowTemporaryInvalidValues) {
               TimeRangeBindingSupport.synchronizeLoToHiTemporary(loEditor, hiValidator, lOffset);
               TimeRangeBindingSupport.synchronizeHiToLoTemporary(hiEditor, loValidator, lOffset);
               ChangeListener<String> loTextChangeListener = (s, o, n) -> TimeRangeBindingSupport.synchronizeTextToValue(loEditor, loValidator);
               loEditor.getInput().textProperty().addListener(loTextChangeListener);
               loEditor.getProperties().put("loTextChangeListener", loTextChangeListener);
               ChangeListener<String> hiTextChangeListener = (s, o, n) -> TimeRangeBindingSupport.synchronizeTextToValue(hiEditor, hiValidator);
               hiEditor.getInput().textProperty().addListener(hiTextChangeListener);
               hiEditor.getProperties().put("hiTextChangeListener", hiTextChangeListener);
               ChangeListener<Boolean> rangeFocusChangeListener = (s, o, n) -> {
                  if (Boolean.FALSE.equals(n)) {
                     Platform.runLater(() -> rollbackTimePairIfInvalidOnFocusExit(loEditor, hiEditor, loValidator, hiValidator, lOffset));
                  }
               };
               loEditor.getInput().focusedProperty().addListener(rangeFocusChangeListener);
               hiEditor.getInput().focusedProperty().addListener(rangeFocusChangeListener);
               loEditor.getProperties().put("rangeFocusChangeListener", rangeFocusChangeListener);
               hiEditor.getProperties().put("rangeFocusChangeListener", rangeFocusChangeListener);
            }

            ChangeListener<Number> loValueChangeListener = (s, o, n) -> {
               if (n != null) {
                  if (allowTemporaryInvalidValues) {
                     TimeRangeBindingSupport.synchronizeLoToHiTemporary(loEditor, hiValidator, lOffset);
                     if (!loEditor.getInput().isFocused() && !hiEditor.getInput().isFocused()) {
                        TimeRangeBindingSupport.persistPairAsRollbackTargetIfValid(loEditor, hiEditor, loValidator, hiValidator, "oldEditorValue");
                     }
                  } else {
                     TimeRangeBindingSupport.synchronizeLoToHi(loEditor, hiEditor, hiValidator, lOffset);
                  }
               }
            };
            loEditor.valueProperty().addListener(loValueChangeListener);
            loEditor.getProperties().put("loValueChangeListener", loValueChangeListener);
            ChangeListener<Number> hiValueChangeListener = (s, o, n) -> {
               if (n != null) {
                  if (allowTemporaryInvalidValues) {
                     TimeRangeBindingSupport.synchronizeHiToLoTemporary(hiEditor, loValidator, lOffset);
                     if (!loEditor.getInput().isFocused() && !hiEditor.getInput().isFocused()) {
                        TimeRangeBindingSupport.persistPairAsRollbackTargetIfValid(loEditor, hiEditor, loValidator, hiValidator, "oldEditorValue");
                     }
                  } else {
                     TimeRangeBindingSupport.synchronizeHiToLo(loEditor, hiEditor, loValidator, lOffset);
                  }
               }
            };
            hiEditor.valueProperty().addListener(hiValueChangeListener);
            hiEditor.getProperties().put("hiValueChangeListener", hiValueChangeListener);
         }
      }
   }

   private static void rollbackTimePairIfInvalidOnFocusExit(
      TimeEditor loEditor, TimeEditor hiEditor, TimeEditorRangeValidator loValidator, TimeEditorRangeValidator hiValidator, long offset
   ) {
      TimeRangeBindingSupport.rollbackPairIfInvalidOnFocusExit(loEditor, hiEditor, loValidator, hiValidator, offset, "oldEditorValue");
   }

   public static void unbindRanges(TimeEditor lowEditor, TimeEditor hiEditor) {
      if (lowEditor != null && hiEditor != null) {
         ChangeListener<Number> loValueChangeListener = (ChangeListener<Number>)lowEditor.getProperties().remove("loValueChangeListener");
         if (loValueChangeListener != null) {
            lowEditor.valueProperty().removeListener(loValueChangeListener);
         }

         ChangeListener<Number> hiValueChangeListener = (ChangeListener<Number>)hiEditor.getProperties().remove("hiValueChangeListener");
         if (hiValueChangeListener != null) {
            hiEditor.valueProperty().removeListener(hiValueChangeListener);
         }

         ChangeListener<String> loTextChangeListener = (ChangeListener<String>)lowEditor.getProperties().remove("loTextChangeListener");
         if (loTextChangeListener != null) {
            lowEditor.getInput().textProperty().removeListener(loTextChangeListener);
         }

         ChangeListener<String> hiTextChangeListener = (ChangeListener<String>)hiEditor.getProperties().remove("hiTextChangeListener");
         if (hiTextChangeListener != null) {
            hiEditor.getInput().textProperty().removeListener(hiTextChangeListener);
         }

         TimeEditorRangeValidator loValidator = (TimeEditorRangeValidator)lowEditor.getProperties().get(TimeEditorRangeValidator.class);
         if (loValidator != null) {
            loValidator.clearRangeBindingBounds();
         }

         TimeEditorRangeValidator hiValidator = (TimeEditorRangeValidator)hiEditor.getProperties().get(TimeEditorRangeValidator.class);
         if (hiValidator != null) {
            hiValidator.clearRangeBindingBounds();
         }

         RangeBindingEditorSupport.removeRangeBindingEditorProperties(lowEditor, hiEditor);
      }
   }

   public static boolean isTemporaryRangeBindingActive(Node editor) {
      return Boolean.TRUE.equals(editor.getProperties().get("rangeTemporaryInvalidValues"));
   }

   public static void recordRangeFocusGain(Node editor) {
      editor.getProperties().put("lastFocusGainedEditor", editor);
      if (editor.getProperties().get("counterpartEditor") instanceof Node counterpartNode) {
         counterpartNode.getProperties().put("lastFocusGainedEditor", editor);
      }
   }

   public static void recordRangeFocusLoss(Node editor) {
      editor.getProperties().put("lastFocusLostEditor", editor);
      if (editor.getProperties().get("counterpartEditor") instanceof Node counterpartNode) {
         counterpartNode.getProperties().put("lastFocusLostEditor", editor);
      }
   }

   public static Validators.Validator setValidator(TextInputControl editor, int minLength, int maxLength) {
      return setValidator(editor, true, true, minLength, maxLength);
   }

   public static Validators.Validator setValidator(TextInputControl editor, boolean nullAllowed, boolean blankAllowed, int minLength, int maxLength) {
      Validate.isTrue(minLength >= 0, "Negative 'minLength' parameter: %d", (long)minLength);
      Validate.isTrue(maxLength >= minLength, "'maxLength' (%d) not greater than or equal to 'minLength' (%d)", new Object[]{maxLength, minLength});
      StringEditorValidator validator = (StringEditorValidator)getValidator(editor);
      if (validator != null) {
         removeValidator(editor);
      }

      validator = new StringEditorValidator(editor, nullAllowed, blankAllowed, minLength, maxLength);
      validator.install();
      editor.getProperties().put(StringEditorValidator.class, validator);
      return validator;
   }

   public static Validators.Validator removeValidator(TextInputControl editor) {
      Validators.Validator validator = getValidator(editor);
      if (validator != null) {
         validator.uninstall();
         editor.getProperties().remove(StringEditorValidator.class);
      }

      return validator;
   }

   public static Validators.Validator getValidator(TextInputControl editor) {
      Objects.requireNonNull(editor, "Null 'editor' parameter");
      return (Validators.Validator)editor.getProperties().get(StringEditorValidator.class);
   }

   public static boolean isValidValue(TextInputControl editor) {
      StringEditorValidator validator = (StringEditorValidator)getValidator(editor);
      return validator != null ? validator.highlightProperty().get() == Validators.Status.NONE : true;
   }

   private Validators() {
   }

   public static enum Status {
      NONE,
      ERROR,
      WARNING;
   }

   public interface Validator {
      void setStatusProvider(Function<TextInputControl, Validators.Status> var1, Observable... var2);

      void setErrorMessageProvider(Supplier<String> var1);

      void setWarningMessageProvider(Supplier<String> var1);

      void install();

      void uninstall();

      ObjectProperty<Validators.Status> highlightProperty();

      default Validators.Status getHighlight() {
         return (Validators.Status)this.highlightProperty().get();
      }

      default void setHighlight(Validators.Status status) {
         this.highlightProperty().set(status);
      }
   }
}
