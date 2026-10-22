package org.controlsfx.property.editor;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.Optional;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.util.Callback;
import org.controlsfx.control.PropertySheet;

public class DefaultPropertyEditorFactory implements Callback<PropertySheet.Item, PropertyEditor<?>> {
   private static Class<?>[] numericTypes = new Class[]{
      byte.class,
      Byte.class,
      short.class,
      Short.class,
      int.class,
      Integer.class,
      long.class,
      Long.class,
      float.class,
      Float.class,
      double.class,
      Double.class,
      BigInteger.class,
      BigDecimal.class
   };

   public PropertyEditor<?> call(PropertySheet.Item item) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.NullPointerException: Cannot invoke "org.jetbrains.java.decompiler.struct.gen.VarType.isGeneric()" because "newRet" is null
      //   at org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent.getInferredExprType(InvocationExprent.java:634)
      //   at org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent.getInferredExprType(InvocationExprent.java:503)
      //   at org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent.getInferredExprType(InvocationExprent.java:503)
      //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.getCastedExprent(ExprProcessor.java:966)
      //   at org.jetbrains.java.decompiler.modules.decompiler.exps.ExitExprent.toJava(ExitExprent.java:86)
      //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.listToJava(ExprProcessor.java:895)
      //   at org.jetbrains.java.decompiler.modules.decompiler.stats.BasicBlockStatement.toJava(BasicBlockStatement.java:90)
      //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.jmpWrapper(ExprProcessor.java:833)
      //   at org.jetbrains.java.decompiler.modules.decompiler.stats.IfStatement.toJava(IfStatement.java:241)
      //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.jmpWrapper(ExprProcessor.java:833)
      //   at org.jetbrains.java.decompiler.modules.decompiler.stats.IfStatement.toJava(IfStatement.java:254)
      //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.jmpWrapper(ExprProcessor.java:833)
      //   at org.jetbrains.java.decompiler.modules.decompiler.stats.IfStatement.toJava(IfStatement.java:254)
      //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.jmpWrapper(ExprProcessor.java:833)
      //   at org.jetbrains.java.decompiler.modules.decompiler.stats.IfStatement.toJava(IfStatement.java:254)
      //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.jmpWrapper(ExprProcessor.java:833)
      //   at org.jetbrains.java.decompiler.modules.decompiler.stats.IfStatement.toJava(IfStatement.java:254)
      //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.jmpWrapper(ExprProcessor.java:833)
      //   at org.jetbrains.java.decompiler.modules.decompiler.stats.IfStatement.toJava(IfStatement.java:254)
      //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.jmpWrapper(ExprProcessor.java:833)
      //   at org.jetbrains.java.decompiler.modules.decompiler.stats.SequenceStatement.toJava(SequenceStatement.java:107)
      //   at org.jetbrains.java.decompiler.modules.decompiler.stats.RootStatement.toJava(RootStatement.java:36)
      //   at org.jetbrains.java.decompiler.main.ClassWriter.writeMethod(ClassWriter.java:1283)
      //
      // Bytecode:
      // 00: aload 1
      // 01: invokeinterface org/controlsfx/control/PropertySheet$Item.getType ()Ljava/lang/Class; 1
      // 06: astore 2
      // 07: aload 1
      // 08: invokeinterface org/controlsfx/control/PropertySheet$Item.getPropertyEditorClass ()Ljava/util/Optional; 1
      // 0d: invokevirtual java/util/Optional.isPresent ()Z
      // 10: ifeq 27
      // 13: aload 1
      // 14: invokestatic org/controlsfx/property/editor/Editors.createCustomEditor (Lorg/controlsfx/control/PropertySheet$Item;)Ljava/util/Optional;
      // 17: astore 3
      // 18: aload 3
      // 19: invokevirtual java/util/Optional.isPresent ()Z
      // 1c: ifeq 27
      // 1f: aload 3
      // 20: invokevirtual java/util/Optional.get ()Ljava/lang/Object;
      // 23: checkcast org/controlsfx/property/editor/PropertyEditor
      // 26: areturn
      // 27: aload 2
      // 28: ldc java/lang/String
      // 2a: if_acmpne 32
      // 2d: aload 1
      // 2e: invokestatic org/controlsfx/property/editor/Editors.createTextEditor (Lorg/controlsfx/control/PropertySheet$Item;)Lorg/controlsfx/property/editor/PropertyEditor;
      // 31: areturn
      // 32: aload 2
      // 33: invokestatic org/controlsfx/property/editor/DefaultPropertyEditorFactory.isNumber (Ljava/lang/Class;)Z
      // 36: ifeq 3e
      // 39: aload 1
      // 3a: invokestatic org/controlsfx/property/editor/Editors.createNumericEditor (Lorg/controlsfx/control/PropertySheet$Item;)Lorg/controlsfx/property/editor/PropertyEditor;
      // 3d: areturn
      // 3e: aload 2
      // 3f: getstatic java/lang/Boolean.TYPE Ljava/lang/Class;
      // 42: if_acmpeq 4b
      // 45: aload 2
      // 46: ldc java/lang/Boolean
      // 48: if_acmpne 50
      // 4b: aload 1
      // 4c: invokestatic org/controlsfx/property/editor/Editors.createCheckEditor (Lorg/controlsfx/control/PropertySheet$Item;)Lorg/controlsfx/property/editor/PropertyEditor;
      // 4f: areturn
      // 50: aload 2
      // 51: ldc java/time/LocalDate
      // 53: if_acmpne 5b
      // 56: aload 1
      // 57: invokestatic org/controlsfx/property/editor/Editors.createDateEditor (Lorg/controlsfx/control/PropertySheet$Item;)Lorg/controlsfx/property/editor/PropertyEditor;
      // 5a: areturn
      // 5b: aload 2
      // 5c: ldc javafx/scene/paint/Color
      // 5e: if_acmpeq 67
      // 61: aload 2
      // 62: ldc javafx/scene/paint/Paint
      // 64: if_acmpne 6c
      // 67: aload 1
      // 68: invokestatic org/controlsfx/property/editor/Editors.createColorEditor (Lorg/controlsfx/control/PropertySheet$Item;)Lorg/controlsfx/property/editor/PropertyEditor;
      // 6b: areturn
      // 6c: aload 2
      // 6d: ifnull 83
      // 70: aload 2
      // 71: invokevirtual java/lang/Class.isEnum ()Z
      // 74: ifeq 83
      // 77: aload 1
      // 78: aload 2
      // 79: invokevirtual java/lang/Class.getEnumConstants ()[Ljava/lang/Object;
      // 7c: invokestatic java/util/Arrays.asList ([Ljava/lang/Object;)Ljava/util/List;
      // 7f: invokestatic org/controlsfx/property/editor/Editors.createChoiceEditor (Lorg/controlsfx/control/PropertySheet$Item;Ljava/util/Collection;)Lorg/controlsfx/property/editor/PropertyEditor;
      // 82: areturn
      // 83: aload 2
      // 84: ldc javafx/scene/text/Font
      // 86: if_acmpne 8e
      // 89: aload 1
      // 8a: invokestatic org/controlsfx/property/editor/Editors.createFontEditor (Lorg/controlsfx/control/PropertySheet$Item;)Lorg/controlsfx/property/editor/PropertyEditor;
      // 8d: areturn
      // 8e: aconst_null
      // 8f: areturn
   }

   private static boolean isNumber(Class<?> type) {
      if (type == null) {
         return false;
      } else {
         for (Class<?> cls : numericTypes) {
            if (type == cls) {
               return true;
            }
         }

         return false;
      }
   }
}
