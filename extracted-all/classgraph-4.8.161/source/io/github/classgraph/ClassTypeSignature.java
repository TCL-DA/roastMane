package io.github.classgraph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import nonapi.io.github.classgraph.types.ParseException;
import nonapi.io.github.classgraph.types.Parser;
import nonapi.io.github.classgraph.types.TypeUtils;
import nonapi.io.github.classgraph.utils.LogNode;

public final class ClassTypeSignature extends HierarchicalTypeSignature {
   private final ClassInfo classInfo;
   final List<TypeParameter> typeParameters;
   private final ClassRefTypeSignature superclassSignature;
   private final List<ClassRefTypeSignature> superinterfaceSignatures;
   private final List<ClassRefOrTypeVariableSignature> throwsSignatures;

   private ClassTypeSignature(
      ClassInfo classInfo,
      List<TypeParameter> typeParameters,
      ClassRefTypeSignature superclassSignature,
      List<ClassRefTypeSignature> superinterfaceSignatures,
      List<ClassRefOrTypeVariableSignature> throwsSignatures
   ) {
      this.classInfo = classInfo;
      this.typeParameters = typeParameters;
      this.superclassSignature = superclassSignature;
      this.superinterfaceSignatures = superinterfaceSignatures;
      this.throwsSignatures = throwsSignatures;
   }

   ClassTypeSignature(ClassInfo classInfo, ClassInfo superclass, ClassInfoList interfaces) {
      this.classInfo = classInfo;
      this.typeParameters = Collections.emptyList();
      ClassRefTypeSignature superclassSignature = null;

      try {
         superclassSignature = superclass == null
            ? null
            : (ClassRefTypeSignature)TypeSignature.parse("L" + superclass.getName().replace('.', '/') + ";", classInfo.getName());
      } catch (ParseException var9) {
      }

      this.superclassSignature = superclassSignature;
      this.superinterfaceSignatures = (List<ClassRefTypeSignature>)(interfaces != null && !interfaces.isEmpty()
         ? new ArrayList<>(interfaces.size())
         : Collections.emptyList());
      if (interfaces != null) {
         for (ClassInfo iface : interfaces) {
            try {
               ClassRefTypeSignature ifaceSignature = (ClassRefTypeSignature)TypeSignature.parse(
                  "L" + iface.getName().replace('.', '/') + ";", classInfo.getName()
               );
               this.superinterfaceSignatures.add(ifaceSignature);
            } catch (ParseException var8) {
            }
         }
      }

      this.throwsSignatures = null;
   }

   public List<TypeParameter> getTypeParameters() {
      return this.typeParameters;
   }

   public ClassRefTypeSignature getSuperclassSignature() {
      return this.superclassSignature;
   }

   public List<ClassRefTypeSignature> getSuperinterfaceSignatures() {
      return this.superinterfaceSignatures;
   }

   List<ClassRefOrTypeVariableSignature> getThrowsSignatures() {
      return this.throwsSignatures;
   }

   @Override
   protected void addTypeAnnotation(List<Classfile.TypePathNode> typePath, AnnotationInfo annotationInfo) {
      throw new IllegalArgumentException("Cannot call this method on " + ClassTypeSignature.class.getSimpleName());
   }

   @Override
   protected String getClassName() {
      return this.classInfo != null ? this.classInfo.getName() : null;
   }

   @Override
   protected ClassInfo getClassInfo() {
      return this.classInfo;
   }

   @Override
   void setScanResult(ScanResult scanResult) {
      super.setScanResult(scanResult);
      if (this.typeParameters != null) {
         for (TypeParameter typeParameter : this.typeParameters) {
            typeParameter.setScanResult(scanResult);
         }
      }

      if (this.superclassSignature != null) {
         this.superclassSignature.setScanResult(scanResult);
      }

      if (this.superinterfaceSignatures != null) {
         for (ClassRefTypeSignature classRefTypeSignature : this.superinterfaceSignatures) {
            classRefTypeSignature.setScanResult(scanResult);
         }
      }
   }

   protected void findReferencedClassNames(Set<String> refdClassNames) {
      for (TypeParameter typeParameter : this.typeParameters) {
         typeParameter.findReferencedClassNames(refdClassNames);
      }

      if (this.superclassSignature != null) {
         this.superclassSignature.findReferencedClassNames(refdClassNames);
      }

      if (this.superinterfaceSignatures != null) {
         for (ClassRefTypeSignature typeSignature : this.superinterfaceSignatures) {
            typeSignature.findReferencedClassNames(refdClassNames);
         }
      }

      if (this.throwsSignatures != null) {
         for (ClassRefOrTypeVariableSignature typeSignature : this.throwsSignatures) {
            typeSignature.findReferencedClassNames(refdClassNames);
         }
      }
   }

   @Override
   protected void findReferencedClassInfo(Map<String, ClassInfo> classNameToClassInfo, Set<ClassInfo> refdClassInfo, LogNode log) {
      Set<String> refdClassNames = new HashSet<>();
      this.findReferencedClassNames(refdClassNames);

      for (String refdClassName : refdClassNames) {
         ClassInfo clsInfo = ClassInfo.getOrCreateClassInfo(refdClassName, classNameToClassInfo);
         clsInfo.scanResult = this.scanResult;
         refdClassInfo.add(clsInfo);
      }
   }

   @Override
   public int hashCode() {
      return this.typeParameters.hashCode()
         + (this.superclassSignature == null ? 1 : this.superclassSignature.hashCode()) * 7
         + (this.superinterfaceSignatures == null ? 1 : this.superinterfaceSignatures.hashCode()) * 15;
   }

   @Override
   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      } else if (!(obj instanceof ClassTypeSignature)) {
         return false;
      } else {
         ClassTypeSignature o = (ClassTypeSignature)obj;
         return Objects.equals(o.typeParameters, this.typeParameters)
            && Objects.equals(o.superclassSignature, this.superclassSignature)
            && Objects.equals(o.superinterfaceSignatures, this.superinterfaceSignatures);
      }
   }

   void toStringInternal(
      String className,
      boolean useSimpleNames,
      int modifiers,
      boolean isAnnotation,
      boolean isInterface,
      AnnotationInfoList annotationsToExclude,
      StringBuilder buf
   ) {
      if (this.throwsSignatures != null) {
         for (ClassRefOrTypeVariableSignature throwsSignature : this.throwsSignatures) {
            if (buf.length() > 0) {
               buf.append(' ');
            }

            buf.append("@throws(").append(throwsSignature).append(")");
         }
      }

      if (modifiers != 0) {
         if (buf.length() > 0) {
            buf.append(' ');
         }

         TypeUtils.modifiersToString(modifiers, TypeUtils.ModifierType.CLASS, false, buf);
      }

      if (buf.length() > 0) {
         buf.append(' ');
      }

      buf.append(isAnnotation ? "@interface" : (isInterface ? "interface" : ((modifiers & 16384) != 0 ? "enum" : "class")));
      buf.append(' ');
      if (className != null) {
         buf.append(useSimpleNames ? ClassInfo.getSimpleName(className) : className);
      }

      if (!this.typeParameters.isEmpty()) {
         buf.append('<');

         for (int i = 0; i < this.typeParameters.size(); i++) {
            if (i > 0) {
               buf.append(", ");
            }

            this.typeParameters.get(i).toStringInternal(useSimpleNames, null, buf);
         }

         buf.append('>');
      }

      if (this.superclassSignature != null) {
         String superSig = this.superclassSignature.toString(useSimpleNames);
         if (!superSig.equals("java.lang.Object") && (!superSig.equals("Object") || !this.superclassSignature.className.equals("java.lang.Object"))) {
            buf.append(" extends ");
            buf.append(superSig);
         }
      }

      if (this.superinterfaceSignatures != null && !this.superinterfaceSignatures.isEmpty()) {
         buf.append(isInterface ? " extends " : " implements ");

         for (int i = 0; i < this.superinterfaceSignatures.size(); i++) {
            if (i > 0) {
               buf.append(", ");
            }

            this.superinterfaceSignatures.get(i).toStringInternal(useSimpleNames, null, buf);
         }
      }
   }

   @Override
   protected void toStringInternal(boolean useSimpleNames, AnnotationInfoList annotationsToExclude, StringBuilder buf) {
      this.toStringInternal(
         this.classInfo.getName(),
         useSimpleNames,
         this.classInfo.getModifiers(),
         this.classInfo.isAnnotation(),
         this.classInfo.isInterface(),
         annotationsToExclude,
         buf
      );
   }

   static ClassTypeSignature parse(String typeDescriptor, ClassInfo classInfo) throws ParseException {
      Parser parser = new Parser(typeDescriptor);
      String definingClassNameNull = null;
      List<TypeParameter> typeParameters = TypeParameter.parseList(parser, definingClassNameNull);
      ClassRefTypeSignature superclassSignature = ClassRefTypeSignature.parse(parser, definingClassNameNull);
      List<ClassRefTypeSignature> superinterfaceSignatures;
      if (parser.hasMore()) {
         superinterfaceSignatures = new ArrayList<>();

         while (parser.hasMore() && parser.peek() != '^') {
            ClassRefTypeSignature superinterfaceSignature = ClassRefTypeSignature.parse(parser, definingClassNameNull);
            if (superinterfaceSignature == null) {
               throw new ParseException(parser, "Could not parse superinterface signature");
            }

            superinterfaceSignatures.add(superinterfaceSignature);
         }
      } else {
         superinterfaceSignatures = Collections.emptyList();
      }

      List<ClassRefOrTypeVariableSignature> throwsSignatures;
      if (parser.peek() == '^') {
         throwsSignatures = new ArrayList<>();

         while (parser.peek() == '^') {
            parser.expect('^');
            ClassRefTypeSignature classTypeSignature = ClassRefTypeSignature.parse(parser, classInfo.getName());
            if (classTypeSignature != null) {
               throwsSignatures.add(classTypeSignature);
            } else {
               TypeVariableSignature typeVariableSignature = TypeVariableSignature.parse(parser, classInfo.getName());
               if (typeVariableSignature == null) {
                  throw new ParseException(parser, "Missing type variable signature");
               }

               throwsSignatures.add(typeVariableSignature);
            }
         }
      } else {
         throwsSignatures = null;
      }

      if (parser.hasMore()) {
         throw new ParseException(parser, "Extra characters at end of type descriptor");
      } else {
         return new ClassTypeSignature(classInfo, typeParameters, superclassSignature, superinterfaceSignatures, throwsSignatures);
      }
   }
}
