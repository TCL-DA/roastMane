package io.github.classgraph;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import nonapi.io.github.classgraph.types.Parser;

public class BaseTypeSignature extends TypeSignature {
   private final char typeSignatureChar;

   BaseTypeSignature(char typeSignatureChar) {
      switch (typeSignatureChar) {
         case 'B':
         case 'C':
         case 'D':
         case 'F':
         case 'I':
         case 'J':
         case 'S':
         case 'V':
         case 'Z':
            this.typeSignatureChar = typeSignatureChar;
            return;
         case 'E':
         case 'G':
         case 'H':
         case 'K':
         case 'L':
         case 'M':
         case 'N':
         case 'O':
         case 'P':
         case 'Q':
         case 'R':
         case 'T':
         case 'U':
         case 'W':
         case 'X':
         case 'Y':
         default:
            throw new IllegalArgumentException("Illegal " + BaseTypeSignature.class.getSimpleName() + " type: '" + typeSignatureChar + "'");
      }
   }

   static String getTypeStr(char typeChar) {
      switch (typeChar) {
         case 'B':
            return "byte";
         case 'C':
            return "char";
         case 'D':
            return "double";
         case 'E':
         case 'G':
         case 'H':
         case 'K':
         case 'L':
         case 'M':
         case 'N':
         case 'O':
         case 'P':
         case 'Q':
         case 'R':
         case 'T':
         case 'U':
         case 'W':
         case 'X':
         case 'Y':
         default:
            return null;
         case 'F':
            return "float";
         case 'I':
            return "int";
         case 'J':
            return "long";
         case 'S':
            return "short";
         case 'V':
            return "void";
         case 'Z':
            return "boolean";
      }
   }

   static char getTypeChar(String typeStr) {
      switch (typeStr) {
         case "byte":
            return 'B';
         case "char":
            return 'C';
         case "double":
            return 'D';
         case "float":
            return 'F';
         case "int":
            return 'I';
         case "long":
            return 'J';
         case "short":
            return 'S';
         case "boolean":
            return 'Z';
         case "void":
            return 'V';
         default:
            return '\u0000';
      }
   }

   static Class<?> getType(char typeChar) {
      switch (typeChar) {
         case 'B':
            return byte.class;
         case 'C':
            return char.class;
         case 'D':
            return double.class;
         case 'E':
         case 'G':
         case 'H':
         case 'K':
         case 'L':
         case 'M':
         case 'N':
         case 'O':
         case 'P':
         case 'Q':
         case 'R':
         case 'T':
         case 'U':
         case 'W':
         case 'X':
         case 'Y':
         default:
            return null;
         case 'F':
            return float.class;
         case 'I':
            return int.class;
         case 'J':
            return long.class;
         case 'S':
            return short.class;
         case 'V':
            return void.class;
         case 'Z':
            return boolean.class;
      }
   }

   public char getTypeSignatureChar() {
      return this.typeSignatureChar;
   }

   public String getTypeStr() {
      return getTypeStr(this.typeSignatureChar);
   }

   public Class<?> getType() {
      return getType(this.typeSignatureChar);
   }

   @Override
   protected void addTypeAnnotation(List<Classfile.TypePathNode> typePath, AnnotationInfo annotationInfo) {
      this.addTypeAnnotation(annotationInfo);
   }

   @Override
   Class<?> loadClass() {
      return this.getType();
   }

   @Override
   <T> Class<T> loadClass(Class<T> superclassOrInterfaceType) {
      Class<?> type = this.getType();
      if (!superclassOrInterfaceType.isAssignableFrom(type)) {
         throw new IllegalArgumentException("Primitive class " + this.getTypeStr() + " cannot be cast to " + superclassOrInterfaceType.getName());
      } else {
         return (Class<T>)type;
      }
   }

   static BaseTypeSignature parse(Parser parser) {
      switch (parser.peek()) {
         case 'B':
            parser.next();
            return new BaseTypeSignature('B');
         case 'C':
            parser.next();
            return new BaseTypeSignature('C');
         case 'D':
            parser.next();
            return new BaseTypeSignature('D');
         case 'E':
         case 'G':
         case 'H':
         case 'K':
         case 'L':
         case 'M':
         case 'N':
         case 'O':
         case 'P':
         case 'Q':
         case 'R':
         case 'T':
         case 'U':
         case 'W':
         case 'X':
         case 'Y':
         default:
            return null;
         case 'F':
            parser.next();
            return new BaseTypeSignature('F');
         case 'I':
            parser.next();
            return new BaseTypeSignature('I');
         case 'J':
            parser.next();
            return new BaseTypeSignature('J');
         case 'S':
            parser.next();
            return new BaseTypeSignature('S');
         case 'V':
            parser.next();
            return new BaseTypeSignature('V');
         case 'Z':
            parser.next();
            return new BaseTypeSignature('Z');
      }
   }

   @Override
   protected String getClassName() {
      return this.getTypeStr();
   }

   @Override
   protected ClassInfo getClassInfo() {
      return null;
   }

   @Override
   protected void findReferencedClassNames(Set<String> refdClassNames) {
   }

   @Override
   void setScanResult(ScanResult scanResult) {
   }

   @Override
   public int hashCode() {
      return this.typeSignatureChar;
   }

   @Override
   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      } else if (!(obj instanceof BaseTypeSignature)) {
         return false;
      } else {
         BaseTypeSignature other = (BaseTypeSignature)obj;
         return Objects.equals(this.typeAnnotationInfo, other.typeAnnotationInfo) && other.typeSignatureChar == this.typeSignatureChar;
      }
   }

   @Override
   public boolean equalsIgnoringTypeParams(TypeSignature other) {
      return !(other instanceof BaseTypeSignature) ? false : this.typeSignatureChar == ((BaseTypeSignature)other).typeSignatureChar;
   }

   @Override
   protected void toStringInternal(boolean useSimpleNames, AnnotationInfoList annotationsToExclude, StringBuilder buf) {
      if (this.typeAnnotationInfo != null) {
         for (AnnotationInfo annotationInfo : this.typeAnnotationInfo) {
            if (annotationsToExclude == null || !annotationsToExclude.contains(annotationInfo)) {
               annotationInfo.toString(useSimpleNames, buf);
               buf.append(' ');
            }
         }
      }

      buf.append(this.getTypeStr());
   }
}
