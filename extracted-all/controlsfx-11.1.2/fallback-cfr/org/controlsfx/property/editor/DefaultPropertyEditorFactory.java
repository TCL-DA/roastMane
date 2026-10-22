/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javafx.scene.paint.Color
 *  javafx.scene.paint.Paint
 *  javafx.scene.text.Font
 *  javafx.util.Callback
 */
package org.controlsfx.property.editor;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.util.Callback;
import org.controlsfx.control.PropertySheet;
import org.controlsfx.property.editor.Editors;
import org.controlsfx.property.editor.PropertyEditor;

public class DefaultPropertyEditorFactory
implements Callback<PropertySheet.Item, PropertyEditor<?>> {
    private static Class<?>[] numericTypes = new Class[]{Byte.TYPE, Byte.class, Short.TYPE, Short.class, Integer.TYPE, Integer.class, Long.TYPE, Long.class, Float.TYPE, Float.class, Double.TYPE, Double.class, BigInteger.class, BigDecimal.class};

    public PropertyEditor<?> call(PropertySheet.Item item) {
        Optional<PropertyEditor<?>> ed;
        Class<?> type = item.getType();
        if (item.getPropertyEditorClass().isPresent() && (ed = Editors.createCustomEditor(item)).isPresent()) {
            return ed.get();
        }
        if (type == String.class) {
            return Editors.createTextEditor(item);
        }
        if (DefaultPropertyEditorFactory.isNumber(type)) {
            return Editors.createNumericEditor(item);
        }
        if (type == Boolean.TYPE || type == Boolean.class) {
            return Editors.createCheckEditor(item);
        }
        if (type == LocalDate.class) {
            return Editors.createDateEditor(item);
        }
        if (type == Color.class || type == Paint.class) {
            return Editors.createColorEditor(item);
        }
        if (type != null && type.isEnum()) {
            return Editors.createChoiceEditor(item, Arrays.asList(type.getEnumConstants()));
        }
        if (type == Font.class) {
            return Editors.createFontEditor(item);
        }
        return null;
    }

    private static boolean isNumber(Class<?> type) {
        if (type == null) {
            return false;
        }
        for (Class<?> cls : numericTypes) {
            if (type != cls) continue;
            return true;
        }
        return false;
    }
}

