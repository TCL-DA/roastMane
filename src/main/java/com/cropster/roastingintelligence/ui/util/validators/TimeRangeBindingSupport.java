package com.cropster.roastingintelligence.ui.util.validators;

import com.cropster.roastingintelligence.ui.components.TimeEditor;
import com.cropster.roastingintelligence.ui.util.Validators;
import javafx.scene.control.TextFormatter;

public final class TimeRangeBindingSupport {
   private TimeRangeBindingSupport() {
   }

   public static void initializeRangeBinding(
      TimeEditor loEditor, TimeEditor hiEditor, TimeEditorRangeValidator loValidator, TimeEditorRangeValidator hiValidator, long offset
   ) {
      Number hiValue = hiEditor.getValue();
      if (hiValue != null) {
         clampLoAgainstHi(loEditor, hiEditor, loValidator, offset);
      } else if (loEditor.getValue() != null) {
         initializeHiFromLo(loEditor, hiEditor, hiValidator, offset);
      }
   }

   public static void synchronizeLoToHi(TimeEditor loEditor, TimeEditor hiEditor, TimeEditorRangeValidator hiValidator, long offset) {
      if (loEditor.getValue() != null) {
         long low = getSafeValue(loEditor);
         hiValidator.setRangeBindingMin(low + offset);
         Number hiValue = hiEditor.getValue();
         if (hiValue != null) {
            hiValidator.updateValue(Math.max(hiValue.longValue(), hiValidator.getEffectiveMin()));
         }
      }
   }

   public static void synchronizeLoToHiTemporary(TimeEditor loEditor, TimeEditorRangeValidator hiValidator, long offset) {
      Number loValue = loEditor.getValue();
      if (loValue != null) {
         hiValidator.setRangeBindingMin(loValue.longValue() + offset);
      }
   }

   public static void synchronizeHiToLo(TimeEditor loEditor, TimeEditor hiEditor, TimeEditorRangeValidator loValidator, long offset) {
      long high = getSafeValue(hiEditor);
      long maxLow = high - offset;
      loValidator.setRangeBindingMax(maxLow);
      clampLoToMaximum(loEditor, loValidator, maxLow);
   }

   public static void synchronizeHiToLoTemporary(TimeEditor hiEditor, TimeEditorRangeValidator loValidator, long offset) {
      Number hiValue = hiEditor.getValue();
      if (hiValue != null) {
         loValidator.setRangeBindingMax(hiValue.longValue() - offset);
      }
   }

   public static void synchronizeTextToValue(TimeEditor editor, TimeEditorRangeValidator validator) {
      String text = editor.getInput().getText();
      if (text != null) {
         try {
            TextFormatter<Long> formatter = (TextFormatter<Long>)editor.getInput().getTextFormatter();
            Long parsedValue = (Long)formatter.getValueConverter().fromString(text);
            if (parsedValue == null) {
               return;
            }

            Number currentValue = editor.getValue();
            if (currentValue == null || currentValue.longValue() != parsedValue) {
               validator.updateValue(parsedValue);
            }
         } catch (Exception var6) {
         }
      }
   }

   public static void rollbackPairIfInvalidOnFocusExit(
      TimeEditor loEditor,
      TimeEditor hiEditor,
      TimeEditorRangeValidator loValidator,
      TimeEditorRangeValidator hiValidator,
      long offset,
      String rollbackSnapshotKey
   ) {
      if (!loEditor.getInput().isFocused() && !hiEditor.getInput().isFocused()) {
         boolean loValid = loValidator.highlightProperty().get() == Validators.Status.NONE;
         boolean hiValid = hiValidator.highlightProperty().get() == Validators.Status.NONE;
         if (loValid && hiValid) {
            loEditor.getProperties().put(rollbackSnapshotKey, loEditor.getValue());
            hiEditor.getProperties().put(rollbackSnapshotKey, hiEditor.getValue());
         } else {
            Long loOldValue = getPersistedRollbackValue(loEditor, rollbackSnapshotKey);
            Long hiOldValue = getPersistedRollbackValue(hiEditor, rollbackSnapshotKey);
            if (loOldValue != null) {
               loValidator.updateValue(loOldValue);
            }

            if (hiOldValue != null) {
               hiValidator.updateValue(hiOldValue);
            }

            synchronizeLoToHiTemporary(loEditor, hiValidator, offset);
            synchronizeHiToLoTemporary(hiEditor, loValidator, offset);
         }
      }
   }

   public static void persistPairAsRollbackTargetIfValid(
      TimeEditor loEditor, TimeEditor hiEditor, TimeEditorRangeValidator loValidator, TimeEditorRangeValidator hiValidator, String rollbackSnapshotKey
   ) {
      boolean loValid = loValidator.highlightProperty().get() == Validators.Status.NONE;
      boolean hiValid = hiValidator.highlightProperty().get() == Validators.Status.NONE;
      if (loValid && hiValid) {
         loEditor.getProperties().put(rollbackSnapshotKey, loEditor.getValue());
         hiEditor.getProperties().put(rollbackSnapshotKey, hiEditor.getValue());
      }
   }

   private static void initializeHiFromLo(TimeEditor loEditor, TimeEditor hiEditor, TimeEditorRangeValidator hiValidator, long offset) {
      if (loEditor.getValue() != null) {
         long low = getSafeValue(loEditor);
         hiValidator.setRangeBindingMin(low + offset);
         Number hiValue = hiEditor.getValue();
         if (hiValue != null) {
            hiValidator.updateValue(Math.max(hiValue.longValue(), hiValidator.getEffectiveMin()));
         }
      }
   }

   private static Long getPersistedRollbackValue(TimeEditor editor, String rollbackSnapshotKey) {
      return editor.getProperties().get(rollbackSnapshotKey) instanceof Number number ? number.longValue() : null;
   }

   private static void clampLoAgainstHi(TimeEditor loEditor, TimeEditor hiEditor, TimeEditorRangeValidator loValidator, long offset) {
      if (hiEditor.getValue() != null) {
         long high = getSafeValue(hiEditor);
         long maxLow = high - offset;
         clampLoToMaximum(loEditor, loValidator, maxLow);
      }
   }

   private static void clampLoToMaximum(TimeEditor loEditor, TimeEditorRangeValidator loValidator, long maxLow) {
      Number lowValue = loEditor.getValue();
      if (lowValue != null) {
         loValidator.updateValue(Math.min(lowValue.longValue(), maxLow));
      }
   }

   private static long getSafeValue(TimeEditor editor) {
      return Validators.getSafeValue(editor).longValue();
   }
}
