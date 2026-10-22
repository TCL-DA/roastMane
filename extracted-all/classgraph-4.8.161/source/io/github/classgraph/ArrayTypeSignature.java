package io.github.classgraph;

import java.lang.reflect.Array;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import nonapi.io.github.classgraph.types.ParseException;
import nonapi.io.github.classgraph.types.Parser;

public class ArrayTypeSignature extends ReferenceTypeSignature {
   private final String typeSignatureStr;
   private String className;
   private ArrayClassInfo arrayClassInfo;
   private Class<?> elementClassRef;
   private final TypeSignature nestedType;

   ArrayTypeSignature(TypeSignature elementTypeSignature, int numDims, String typeSignatureStr) {
      boolean typeSigHasTwoOrMoreDims = typeSignatureStr.startsWith("[[");
      if (numDims < 1) {
         throw new IllegalArgumentException("numDims < 1");
      } else if (numDims >= 2 != typeSigHasTwoOrMoreDims) {
         throw new IllegalArgumentException("numDims does not match type signature");
      } else {
         this.typeSignatureStr = typeSignatureStr;
         this.nestedType = (TypeSignature)(typeSigHasTwoOrMoreDims
            ? new ArrayTypeSignature(elementTypeSignature, numDims - 1, typeSignatureStr.substring(1))
            : elementTypeSignature);
      }
   }

   public String getTypeSignatureStr() {
      return this.typeSignatureStr;
   }

   public TypeSignature getElementTypeSignature() {
      ArrayTypeSignature curr = this;

      while (curr.nestedType instanceof ArrayTypeSignature) {
         curr = (ArrayTypeSignature)curr.nestedType;
      }

      return curr.getNestedType();
   }

   public int getNumDimensions() {
      int numDims = 1;

      for (ArrayTypeSignature curr = this; curr.nestedType instanceof ArrayTypeSignature; numDims++) {
         curr = (ArrayTypeSignature)curr.nestedType;
      }

      return numDims;
   }

   public TypeSignature getNestedType() {
      return this.nestedType;
   }

   @Override
   protected void addTypeAnnotation(List<Classfile.TypePathNode> typePath, AnnotationInfo annotationInfo) {
      if (typePath.isEmpty()) {
         this.addTypeAnnotation(annotationInfo);
      } else {
         Classfile.TypePathNode head = typePath.get(0);
         if (head.typePathKind != 0 || head.typeArgumentIdx != 0) {
            throw new IllegalArgumentException("typePath element contains bad values: " + head);
         }

         this.nestedType.addTypeAnnotation(typePath.subList(1, typePath.size()), annotationInfo);
      }
   }

   @Override
   public AnnotationInfoList getTypeAnnotationInfo() {
      return this.typeAnnotationInfo;
   }

   @Override
   protected String getClassName() {
      if (this.className == null) {
         this.className = this.toString();
      }

      return this.className;
   }

   @Override
   protected ClassInfo getClassInfo() {
      return this.getArrayClassInfo();
   }

   public ArrayClassInfo getArrayClassInfo() {
      if (this.arrayClassInfo == null) {
         if (this.scanResult != null) {
            String clsName = this.getClassName();
            this.arrayClassInfo = (ArrayClassInfo)this.scanResult.classNameToClassInfo.get(clsName);
            if (this.arrayClassInfo == null) {
               this.scanResult.classNameToClassInfo.put(clsName, this.arrayClassInfo = new ArrayClassInfo(this));
               this.arrayClassInfo.setScanResult(this.scanResult);
            }
         } else {
            this.arrayClassInfo = new ArrayClassInfo(this);
         }
      }

      return this.arrayClassInfo;
   }

   @Override
   void setScanResult(ScanResult scanResult) {
      super.setScanResult(scanResult);
      this.nestedType.setScanResult(scanResult);
      if (this.arrayClassInfo != null) {
         this.arrayClassInfo.setScanResult(scanResult);
      }
   }

   @Override
   protected void findReferencedClassNames(Set<String> refdClassNames) {
      this.nestedType.findReferencedClassNames(refdClassNames);
   }

   public Class<?> loadElementClass(boolean ignoreExceptions) {
      if (this.elementClassRef == null) {
         TypeSignature elementTypeSignature = this.getElementTypeSignature();
         if (elementTypeSignature instanceof BaseTypeSignature) {
            this.elementClassRef = ((BaseTypeSignature)elementTypeSignature).getType();
         } else if (this.scanResult != null) {
            this.elementClassRef = elementTypeSignature.loadClass(ignoreExceptions);
         } else {
            String elementTypeName = elementTypeSignature.getClassName();

            try {
               this.elementClassRef = Class.forName(elementTypeName);
            } catch (Throwable var5) {
               if (!ignoreExceptions) {
                  throw new IllegalArgumentException("Could not load array element class " + elementTypeName, var5);
               }
            }
         }
      }

      return this.elementClassRef;
   }

   public Class<?> loadElementClass() {
      return this.loadElementClass(false);
   }

   @Override
   public Class<?> loadClass(boolean ignoreExceptions) {
      if (this.classRef == null) {
         Class<?> eltClassRef = null;
         if (ignoreExceptions) {
            try {
               eltClassRef = this.loadElementClass();
            } catch (IllegalArgumentException var4) {
               return null;
            }
         } else {
            eltClassRef = this.loadElementClass();
         }

         if (eltClassRef == null) {
            throw new IllegalArgumentException("Could not load array element class " + this.getElementTypeSignature());
         }

         Object eltArrayInstance = Array.newInstance(eltClassRef);
         this.classRef = eltArrayInstance.getClass();
      }

      return this.classRef;
   }

   @Override
   public Class<?> loadClass() {
      return this.loadClass(false);
   }

   @Override
   public int hashCode() {
      return 1 + this.nestedType.hashCode();
   }

   @Override
   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      } else if (!(obj instanceof ArrayTypeSignature)) {
         return false;
      } else {
         ArrayTypeSignature other = (ArrayTypeSignature)obj;
         return Objects.equals(this.typeAnnotationInfo, other.typeAnnotationInfo) && this.nestedType.equals(other.nestedType);
      }
   }

   @Override
   public boolean equalsIgnoringTypeParams(TypeSignature other) {
      if (this == other) {
         return true;
      } else if (!(other instanceof ArrayTypeSignature)) {
         return false;
      } else {
         ArrayTypeSignature o = (ArrayTypeSignature)other;
         return this.nestedType.equalsIgnoringTypeParams(o.nestedType);
      }
   }

   @Override
   protected void toStringInternal(boolean useSimpleNames, AnnotationInfoList annotationsToExclude, StringBuilder buf) {
      this.getElementTypeSignature().toStringInternal(useSimpleNames, annotationsToExclude, buf);
      ArrayTypeSignature curr = this;

      while (true) {
         if (curr.typeAnnotationInfo != null && !curr.typeAnnotationInfo.isEmpty()) {
            for (AnnotationInfo annotationInfo : curr.typeAnnotationInfo) {
               if (buf.length() == 0 || buf.charAt(buf.length() - 1) != ' ') {
                  buf.append(' ');
               }

               annotationInfo.toString(useSimpleNames, buf);
            }

            buf.append(' ');
         }

         buf.append("[]");
         if (!(curr.nestedType instanceof ArrayTypeSignature)) {
            return;
         }

         curr = (ArrayTypeSignature)curr.nestedType;
      }
   }

   static ArrayTypeSignature parse(Parser parser, String definingClassName) throws ParseException {
      int numArrayDims = 0;
      int begin = parser.getPosition();

      while (parser.peek() == '[') {
         numArrayDims++;
         parser.next();
      }

      if (numArrayDims > 0) {
         TypeSignature elementTypeSignature = TypeSignature.parse(parser, definingClassName);
         if (elementTypeSignature == null) {
            throw new ParseException(parser, "elementTypeSignature == null");
         } else {
            String typeSignatureStr = parser.getSubsequence(begin, parser.getPosition()).toString();
            return new ArrayTypeSignature(elementTypeSignature, numArrayDims, typeSignatureStr);
         }
      } else {
         return null;
      }
   }
}
