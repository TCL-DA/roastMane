package com.cropster.roastingintelligence.ui.util.validators;

import com.cropster.roastingintelligence.ui.components.MeasureEditor;
import com.cropster.roastingintelligence.ui.components.TimeEditor;
import javafx.beans.value.ChangeListener;
import javafx.scene.Node;
import javafx.scene.control.TextInputControl;

public final class RangeBindingEditorSupport {
   public static final String COUNTERPART_EDITOR = "counterpartEditor";
   public static final String LAST_FOCUS_GAINED_EDITOR = "lastFocusGainedEditor";
   public static final String LAST_FOCUS_LOST_EDITOR = "lastFocusLostEditor";
   public static final String OLD_EDITOR_VALUE = "oldEditorValue";
   public static final String RANGE_FOCUS_CHANGE_LISTENER = "rangeFocusChangeListener";
   public static final String RANGE_TEMPORARY_INVALID_VALUES = "rangeTemporaryInvalidValues";
   private static final Object MISSING_PROPERTY = new Object();
   private static final String SAVED_RESTORE_ON_ERROR = "savedRestoreOldValueOnError";
   private static final String SAVED_RESTORE_ON_FOCUS_LOST = "savedRestoreOldValueOnFocusLost";

   public static void initializeRangeBindingEditorProperties(Node loEditor, Node hiEditor, boolean allowTemporaryInvalidValues) {
      loEditor.getProperties().put("counterpartEditor", hiEditor);
      hiEditor.getProperties().put("counterpartEditor", loEditor);
      loEditor.getProperties().put("rangeTemporaryInvalidValues", allowTemporaryInvalidValues);
      hiEditor.getProperties().put("rangeTemporaryInvalidValues", allowTemporaryInvalidValues);
      loEditor.getProperties().put("oldEditorValue", getEditorValue(loEditor));
      hiEditor.getProperties().put("oldEditorValue", getEditorValue(hiEditor));
      if (allowTemporaryInvalidValues) {
         saveAndDisablePerEditorRestore(loEditor);
         saveAndDisablePerEditorRestore(hiEditor);
      }
   }

   public static void removeRangeBindingEditorProperties(Node loEditor, Node hiEditor) {
      removeRangeFocusListener(loEditor, hiEditor);
      restorePerEditorRestorePolicy(loEditor);
      restorePerEditorRestorePolicy(hiEditor);
      loEditor.getProperties().remove("counterpartEditor");
      hiEditor.getProperties().remove("counterpartEditor");
      loEditor.getProperties().remove("rangeTemporaryInvalidValues");
      hiEditor.getProperties().remove("rangeTemporaryInvalidValues");
      loEditor.getProperties().remove("oldEditorValue");
      hiEditor.getProperties().remove("oldEditorValue");
      loEditor.getProperties().remove("lastFocusGainedEditor");
      hiEditor.getProperties().remove("lastFocusGainedEditor");
      loEditor.getProperties().remove("lastFocusLostEditor");
      hiEditor.getProperties().remove("lastFocusLostEditor");
   }

   private static void saveAndDisablePerEditorRestore(Node editor) {
      Object restoreOnError = editor.getProperties().getOrDefault("restoreOldValueOnError", MISSING_PROPERTY);
      Object restoreOnFocusLost = editor.getProperties().getOrDefault("restoreOldValueOnFocusLost", MISSING_PROPERTY);
      editor.getProperties().put("savedRestoreOldValueOnError", restoreOnError);
      editor.getProperties().put("savedRestoreOldValueOnFocusLost", restoreOnFocusLost);
      editor.getProperties().put("restoreOldValueOnError", Boolean.FALSE);
      editor.getProperties().put("restoreOldValueOnFocusLost", Boolean.FALSE);
   }

   private static void restorePerEditorRestorePolicy(Node editor) {
      restoreOrRemoveProperty(editor, "restoreOldValueOnError", editor.getProperties().remove("savedRestoreOldValueOnError"));
      restoreOrRemoveProperty(editor, "restoreOldValueOnFocusLost", editor.getProperties().remove("savedRestoreOldValueOnFocusLost"));
   }

   private static void restoreOrRemoveProperty(Node editor, String key, Object savedValue) {
      if (savedValue == MISSING_PROPERTY) {
         editor.getProperties().remove(key);
      } else {
         editor.getProperties().put(key, savedValue);
      }
   }

   private static void removeRangeFocusListener(Node loEditor, Node hiEditor) {
      ChangeListener<Boolean> rangeFocusChangeListener = (ChangeListener<Boolean>)loEditor.getProperties().remove("rangeFocusChangeListener");
      hiEditor.getProperties().remove("rangeFocusChangeListener");
      if (rangeFocusChangeListener != null) {
         TextInputControl loInput = getEditorInput(loEditor);
         TextInputControl hiInput = getEditorInput(hiEditor);
         if (loInput != null) {
            loInput.focusedProperty().removeListener(rangeFocusChangeListener);
         }

         if (hiInput != null) {
            hiInput.focusedProperty().removeListener(rangeFocusChangeListener);
         }
      }
   }

   private static TextInputControl getEditorInput(Node editor) {
      if (editor instanceof MeasureEditor measureEditor) {
         return measureEditor.getInput();
      } else {
         return editor instanceof TimeEditor timeEditor ? timeEditor.getInput() : null;
      }
   }

   private static Number getEditorValue(Node editor) {
      if (editor instanceof MeasureEditor measureEditor) {
         return measureEditor.getValue();
      } else {
         return editor instanceof TimeEditor timeEditor ? timeEditor.getValue() : null;
      }
   }

   private RangeBindingEditorSupport() {
   }
}
