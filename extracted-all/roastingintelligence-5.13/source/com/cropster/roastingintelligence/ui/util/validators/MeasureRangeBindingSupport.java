package com.cropster.roastingintelligence.ui.util.validators;

import com.cropster.roastingintelligence.ui.components.MeasureEditor;
import com.cropster.roastingintelligence.ui.util.Validators;
import javafx.scene.control.TextFormatter;
import org.apache.commons.lang3.StringUtils;

public final class MeasureRangeBindingSupport {
   private MeasureRangeBindingSupport() {
   }

   public static void initializeRangeBinding(
      MeasureEditor loEditor, MeasureEditor hiEditor, MeasureEditorRangeValidator loValidator, MeasureEditorRangeValidator hiValidator, double offset
   ) {
      Number hiValue = hiEditor.getValue();
      if (hiValue != null) {
         clampLoAgainstHi(loEditor, hiEditor, loValidator, offset);
      } else if (loEditor.getValue() != null) {
         initializeHiFromLo(loEditor, hiEditor, hiValidator, offset);
      }
   }

   public static void synchronizeLoToHi(MeasureEditor loEditor, MeasureEditor hiEditor, MeasureEditorRangeValidator hiValidator, double offset) {
      if (loEditor.getValue() != null) {
         double low = getSafeValue(loEditor);
         hiValidator.setRangeBindingMin(low + offset);
         Number hiValue = hiEditor.getValue();
         if (hiValue != null) {
            hiValidator.updateValue(Math.max(hiValue.doubleValue(), hiValidator.getEffectiveMin().doubleValue()));
         }
      }
   }

   public static void synchronizeLoToHiTemporary(MeasureEditor loEditor, MeasureEditorRangeValidator hiValidator, double offset) {
      Number loValue = loEditor.getValue();
      if (loValue != null) {
         hiValidator.setRangeBindingMin(loValue.doubleValue() + offset);
      }
   }

   public static void synchronizeHiToLo(MeasureEditor loEditor, MeasureEditor hiEditor, MeasureEditorRangeValidator loValidator, double offset) {
      double high = getSafeValue(hiEditor);
      double maxLow = high - offset;
      loValidator.setRangeBindingMax(maxLow);
      clampLoToMaximum(loEditor, loValidator, maxLow);
   }

   public static void synchronizeHiToLoTemporary(MeasureEditor hiEditor, MeasureEditorRangeValidator loValidator, double offset) {
      Number hiValue = hiEditor.getValue();
      if (hiValue != null) {
         loValidator.setRangeBindingMax(hiValue.doubleValue() - offset);
      }
   }

   public static void synchronizeTextToValue(MeasureEditor editor, MeasureEditorRangeValidator validator) {
      String text = editor.getInput().getText();
      if (StringUtils.isBlank(text)) {
         if (editor.isNullable() && editor.getValue() != null) {
            validator.updateValue(null);
         }
      } else {
         try {
            TextFormatter<Number> formatter = editor.getInput().getTextFormatter();
            Number parsedValue = (Number)formatter.getValueConverter().fromString(text);
            if (parsedValue == null) {
               return;
            }

            Number currentValue = editor.getValue();
            if (currentValue == null || Double.compare(currentValue.doubleValue(), parsedValue.doubleValue()) != 0) {
               validator.updateValue(parsedValue);
            }
         } catch (Exception var6) {
         }
      }
   }

   public static void rollbackPairIfInvalidOnFocusExit(
      MeasureEditor loEditor,
      MeasureEditor hiEditor,
      MeasureEditorRangeValidator loValidator,
      MeasureEditorRangeValidator hiValidator,
      double offset,
      String rollbackSnapshotKey
   ) {
      if (!loEditor.getInput().isFocused() && !hiEditor.getInput().isFocused()) {
         boolean loValid = loValidator.highlightProperty().get() == Validators.Status.NONE;
         boolean hiValid = hiValidator.highlightProperty().get() == Validators.Status.NONE;
         if (loValid && hiValid) {
            loEditor.getProperties().put(rollbackSnapshotKey, loEditor.getValue());
            hiEditor.getProperties().put(rollbackSnapshotKey, hiEditor.getValue());
         } else {
            Number loOldValue = getPersistedRollbackValue(loEditor, rollbackSnapshotKey);
            Number hiOldValue = getPersistedRollbackValue(hiEditor, rollbackSnapshotKey);
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
      MeasureEditor loEditor,
      MeasureEditor hiEditor,
      MeasureEditorRangeValidator loValidator,
      MeasureEditorRangeValidator hiValidator,
      String rollbackSnapshotKey
   ) {
      boolean loValid = loValidator.highlightProperty().get() == Validators.Status.NONE;
      boolean hiValid = hiValidator.highlightProperty().get() == Validators.Status.NONE;
      if (loValid && hiValid) {
         loEditor.getProperties().put(rollbackSnapshotKey, loEditor.getValue());
         hiEditor.getProperties().put(rollbackSnapshotKey, hiEditor.getValue());
      }
   }

   private static void initializeHiFromLo(MeasureEditor loEditor, MeasureEditor hiEditor, MeasureEditorRangeValidator hiValidator, double offset) {
      if (loEditor.getValue() != null) {
         double low = getSafeValue(loEditor);
         hiValidator.setRangeBindingMin(low + offset);
         Number hiValue = hiEditor.getValue();
         if (hiValue != null) {
            hiValidator.updateValue(Math.max(hiValue.doubleValue(), hiValidator.getEffectiveMin().doubleValue()));
         }
      }
   }

   private static Number getPersistedRollbackValue(MeasureEditor editor, String rollbackSnapshotKey) {
      return editor.getProperties().get(rollbackSnapshotKey) instanceof Number number ? number : null;
   }

   private static void clampLoAgainstHi(MeasureEditor loEditor, MeasureEditor hiEditor, MeasureEditorRangeValidator loValidator, double offset) {
      if (hiEditor.getValue() != null) {
         double high = getSafeValue(hiEditor);
         double maxLow = high - offset;
         clampLoToMaximum(loEditor, loValidator, maxLow);
      }
   }

   private static void clampLoToMaximum(MeasureEditor loEditor, MeasureEditorRangeValidator loValidator, double maxLow) {
      Number lowValue = loEditor.getValue();
      if (lowValue != null) {
         loValidator.updateValue(Math.min(lowValue.doubleValue(), maxLow));
      }
   }

   private static double getSafeValue(MeasureEditor editor) {
      return Validators.getSafeValue(editor).doubleValue();
   }
}
