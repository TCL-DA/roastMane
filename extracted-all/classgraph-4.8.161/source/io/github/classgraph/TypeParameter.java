package io.github.classgraph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import nonapi.io.github.classgraph.types.ParseException;
import nonapi.io.github.classgraph.types.Parser;
import nonapi.io.github.classgraph.types.TypeUtils;

public final class TypeParameter extends HierarchicalTypeSignature {
   final String name;
   final ReferenceTypeSignature classBound;
   final List<ReferenceTypeSignature> interfaceBounds;

   protected TypeParameter(String identifier, ReferenceTypeSignature classBound, List<ReferenceTypeSignature> interfaceBounds) {
      this.name = identifier;
      this.classBound = classBound;
      this.interfaceBounds = interfaceBounds;
   }

   public String getName() {
      return this.name;
   }

   public ReferenceTypeSignature getClassBound() {
      return this.classBound;
   }

   public List<ReferenceTypeSignature> getInterfaceBounds() {
      return this.interfaceBounds;
   }

   @Override
   protected void addTypeAnnotation(List<Classfile.TypePathNode> typePath, AnnotationInfo annotationInfo) {
      if (typePath.isEmpty()) {
         this.addTypeAnnotation(annotationInfo);
      } else {
         throw new IllegalArgumentException("Type parameter should have empty typePath");
      }
   }

   static List<TypeParameter> parseList(Parser parser, String definingClassName) throws ParseException {
      if (parser.peek() != '<') {
         return Collections.emptyList();
      } else {
         parser.expect('<');
         List<TypeParameter> typeParams = new ArrayList<>(1);

         while (parser.peek() != '>') {
            if (!parser.hasMore()) {
               throw new ParseException(parser, "Missing '>'");
            }

            if (!TypeUtils.getIdentifierToken(parser, false)) {
               throw new ParseException(parser, "Could not parse identifier token");
            }

            String identifier = parser.currToken();
            ReferenceTypeSignature classBound = ReferenceTypeSignature.parseClassBound(parser, definingClassName);
            List<ReferenceTypeSignature> interfaceBounds;
            if (parser.peek() == ':') {
               interfaceBounds = new ArrayList<>();

               while (parser.peek() == ':') {
                  parser.expect(':');
                  ReferenceTypeSignature interfaceTypeSignature = ReferenceTypeSignature.parseReferenceTypeSignature(parser, definingClassName);
                  if (interfaceTypeSignature == null) {
                     throw new ParseException(parser, "Missing interface type signature");
                  }

                  interfaceBounds.add(interfaceTypeSignature);
               }
            } else {
               interfaceBounds = Collections.emptyList();
            }

            typeParams.add(new TypeParameter(identifier, classBound, interfaceBounds));
         }

         parser.expect('>');
         return typeParams;
      }
   }

   @Override
   protected String getClassName() {
      throw new IllegalArgumentException("getClassName() cannot be called here");
   }

   @Override
   protected ClassInfo getClassInfo() {
      throw new IllegalArgumentException("getClassInfo() cannot be called here");
   }

   @Override
   void setScanResult(ScanResult scanResult) {
      super.setScanResult(scanResult);
      if (this.classBound != null) {
         this.classBound.setScanResult(scanResult);
      }

      if (this.interfaceBounds != null) {
         for (ReferenceTypeSignature referenceTypeSignature : this.interfaceBounds) {
            referenceTypeSignature.setScanResult(scanResult);
         }
      }
   }

   protected void findReferencedClassNames(Set<String> refdClassNames) {
      if (this.classBound != null) {
         this.classBound.findReferencedClassNames(refdClassNames);
      }

      for (ReferenceTypeSignature typeSignature : this.interfaceBounds) {
         typeSignature.findReferencedClassNames(refdClassNames);
      }
   }

   @Override
   public int hashCode() {
      return this.name.hashCode() + (this.classBound == null ? 0 : this.classBound.hashCode() * 7) + this.interfaceBounds.hashCode() * 15;
   }

   @Override
   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      } else if (!(obj instanceof TypeParameter)) {
         return false;
      } else {
         TypeParameter other = (TypeParameter)obj;
         return other.name.equals(this.name)
            && Objects.equals(other.typeAnnotationInfo, this.typeAnnotationInfo)
            && (other.classBound == null && this.classBound == null || other.classBound != null && other.classBound.equals(this.classBound))
            && other.interfaceBounds.equals(this.interfaceBounds);
      }
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

      buf.append(useSimpleNames ? ClassInfo.getSimpleName(this.name) : this.name);
      String classBoundStr;
      if (this.classBound == null) {
         classBoundStr = null;
      } else {
         classBoundStr = this.classBound.toString(useSimpleNames);
         if (classBoundStr.equals("java.lang.Object")
            || classBoundStr.equals("Object") && ((ClassRefTypeSignature)this.classBound).className.equals("java.lang.Object")) {
            classBoundStr = null;
         }
      }

      if (classBoundStr != null || !this.interfaceBounds.isEmpty()) {
         buf.append(" extends");
      }

      if (classBoundStr != null) {
         buf.append(' ');
         buf.append(classBoundStr);
      }

      for (int i = 0; i < this.interfaceBounds.size(); i++) {
         if (i > 0 || classBoundStr != null) {
            buf.append(" &");
         }

         buf.append(' ');
         this.interfaceBounds.get(i).toString(useSimpleNames, buf);
      }
   }
}
