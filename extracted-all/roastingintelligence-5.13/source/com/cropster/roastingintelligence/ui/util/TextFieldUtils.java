package com.cropster.roastingintelligence.ui.util;

import com.sun.javafx.scene.control.behavior.TextFieldBehavior;
import com.sun.javafx.scene.control.inputmap.InputMap;
import com.sun.javafx.scene.control.inputmap.KeyBinding;
import com.sun.javafx.scene.control.inputmap.InputMap.KeyMapping;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.FieldInfo;
import io.github.classgraph.ScanResult;
import java.lang.reflect.Field;
import java.util.Objects;
import javafx.scene.control.Skin;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.util.StringConverter;

public final class TextFieldUtils {
   private static Field behaviorField = null;

   public static <T> boolean isDirty(TextField field) {
      TextFormatter<T> textFormatter = field.getTextFormatter();
      if (textFormatter != null && textFormatter.getValueConverter() != null) {
         String fieldText = field.getText();
         StringConverter<T> valueConverter = textFormatter.getValueConverter();
         String formatterText = valueConverter.toString(textFormatter.getValue());
         return !Objects.equals(fieldText, formatterText);
      } else {
         return false;
      }
   }

   public static void setTextFormatter(TextField field, TextFormatter<?> formatter) {
      Objects.requireNonNull(field, "Null 'field' parameter.");
      Objects.requireNonNull(formatter, "Null 'formatter' parameter.");
      field.setTextFormatter(formatter);
      field.skinProperty().addListener((s, o, n) -> installCancel(field));
   }

   private static void cancelEdit(TextField field, KeyEvent ev) {
      if (isDirty(field)) {
         ev.consume();
      }

      field.cancelEdit();
   }

   private static TextFieldBehavior getBehaviorFieldValue(TextField textField) {
      try {
         Skin<?> skin = textField.getSkin();
         if (behaviorField == null) {
            String skinClassName = skin.getClass().getName();
            ScanResult scanResult = new ClassGraph()
               .enableClassInfo()
               .enableFieldInfo()
               .enableSystemJarsAndModules()
               .ignoreClassVisibility()
               .ignoreFieldVisibility()
               .acceptClasses(new String[]{skinClassName})
               .scan();

            try {
               ClassInfo classInfo = scanResult.getClassInfo(skinClassName);
               FieldInfo fieldInfo = classInfo.getFieldInfo("behavior");
               behaviorField = fieldInfo.loadClassAndGetField();
               behaviorField.setAccessible(true);
            } catch (Throwable var7) {
               if (scanResult != null) {
                  try {
                     scanResult.close();
                  } catch (Throwable var6) {
                     var7.addSuppressed(var6);
                  }
               }

               throw var7;
            }

            if (scanResult != null) {
               scanResult.close();
            }
         }

         return (TextFieldBehavior)behaviorField.get(skin);
      } catch (Exception var8) {
         throw new RuntimeException("Unable to find or access the 'behavior' field of the given text field.", var8);
      }
   }

   private static void installCancel(TextField textField) {
      TextFieldBehavior behavior = getBehaviorFieldValue(textField);
      InputMap inputMap = behavior.getInputMap();
      KeyBinding binding = new KeyBinding(KeyCode.ESCAPE);
      KeyMapping keyMapping = new KeyMapping(binding, e -> cancelEdit(textField, e));
      keyMapping.setAutoConsume(false);
      inputMap.getMappings().remove(keyMapping);
      inputMap.getMappings().add(keyMapping);
   }

   private TextFieldUtils() {
   }
}
