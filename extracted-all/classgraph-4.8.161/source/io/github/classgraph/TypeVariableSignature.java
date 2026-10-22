package io.github.classgraph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import nonapi.io.github.classgraph.types.ParseException;
import nonapi.io.github.classgraph.types.Parser;
import nonapi.io.github.classgraph.types.TypeUtils;

public final class TypeVariableSignature extends ClassRefOrTypeVariableSignature {
   private final String name;
   private final String definingClassName;
   MethodTypeSignature containingMethodSignature;
   private TypeParameter typeParameterCached;

   private TypeVariableSignature(String typeVariableName, String definingClassName) {
      this.name = typeVariableName;
      this.definingClassName = definingClassName;
   }

   public String getName() {
      return this.name;
   }

   public TypeParameter resolve() {
      if (this.typeParameterCached != null) {
         return this.typeParameterCached;
      } else {
         if (this.containingMethodSignature != null
            && this.containingMethodSignature.typeParameters != null
            && !this.containingMethodSignature.typeParameters.isEmpty()) {
            for (TypeParameter typeParameter : this.containingMethodSignature.typeParameters) {
               if (typeParameter.name.equals(this.name)) {
                  this.typeParameterCached = typeParameter;
                  return typeParameter;
               }
            }
         }

         if (this.getClassName() != null) {
            ClassInfo containingClassInfo = this.getClassInfo();
            if (containingClassInfo == null) {
               throw new IllegalArgumentException("Could not find ClassInfo object for " + this.definingClassName);
            }

            ClassTypeSignature containingClassSignature = null;

            try {
               containingClassSignature = containingClassInfo.getTypeSignature();
            } catch (Exception var5) {
            }

            if (containingClassSignature != null && containingClassSignature.typeParameters != null && !containingClassSignature.typeParameters.isEmpty()) {
               for (TypeParameter typeParameterx : containingClassSignature.typeParameters) {
                  if (typeParameterx.name.equals(this.name)) {
                     this.typeParameterCached = typeParameterx;
                     return typeParameterx;
                  }
               }
            }
         }

         TypeParameter typeParameterxx = new TypeParameter(this.name, null, Collections.emptyList());
         typeParameterxx.setScanResult(this.scanResult);
         this.typeParameterCached = typeParameterxx;
         return typeParameterxx;
      }
   }

   @Override
   protected void addTypeAnnotation(List<Classfile.TypePathNode> typePath, AnnotationInfo annotationInfo) {
      if (typePath.isEmpty()) {
         this.addTypeAnnotation(annotationInfo);
      } else {
         throw new IllegalArgumentException("Type variable should have empty typePath");
      }
   }

   static TypeVariableSignature parse(Parser parser, String definingClassName) throws ParseException {
      char peek = parser.peek();
      if (peek == 'T') {
         parser.next();
         if (!TypeUtils.getIdentifierToken(parser, false)) {
            throw new ParseException(parser, "Could not parse type variable signature");
         } else {
            parser.expect(';');
            TypeVariableSignature typeVariableSignature = new TypeVariableSignature(parser.currToken(), definingClassName);
            List<TypeVariableSignature> typeVariableSignatures = (List<TypeVariableSignature>)parser.getState();
            if (typeVariableSignatures == null) {
               parser.setState(typeVariableSignatures = new ArrayList<>());
            }

            typeVariableSignatures.add(typeVariableSignature);
            return typeVariableSignature;
         }
      } else {
         return null;
      }
   }

   @Override
   protected String getClassName() {
      return this.definingClassName;
   }

   @Override
   protected void findReferencedClassNames(Set<String> refdClassNames) {
   }

   @Override
   void setScanResult(ScanResult scanResult) {
      super.setScanResult(scanResult);
      if (this.typeParameterCached != null) {
         this.typeParameterCached.setScanResult(scanResult);
      }
   }

   @Override
   public int hashCode() {
      return this.name.hashCode();
   }

   @Override
   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      } else if (!(obj instanceof TypeVariableSignature)) {
         return false;
      } else {
         TypeVariableSignature other = (TypeVariableSignature)obj;
         return other.name.equals(this.name) && Objects.equals(other.typeAnnotationInfo, this.typeAnnotationInfo);
      }
   }

   @Override
   public boolean equalsIgnoringTypeParams(TypeSignature other) {
      if (!(other instanceof ClassRefTypeSignature)) {
         return this.equals(other);
      } else if (((ClassRefTypeSignature)other).className.equals("java.lang.Object")) {
         return true;
      } else {
         TypeParameter typeParameter;
         try {
            typeParameter = this.resolve();
         } catch (IllegalArgumentException var5) {
            return true;
         }

         if (typeParameter.classBound != null || typeParameter.interfaceBounds != null && !typeParameter.interfaceBounds.isEmpty()) {
            if (typeParameter.classBound != null) {
               if (!(typeParameter.classBound instanceof ClassRefTypeSignature)) {
                  if (typeParameter.classBound instanceof TypeVariableSignature) {
                     return this.equalsIgnoringTypeParams(typeParameter.classBound);
                  }

                  return false;
               }

               if (typeParameter.classBound.equals(other)) {
                  return true;
               }
            }

            if (typeParameter.interfaceBounds != null) {
               for (ReferenceTypeSignature interfaceBound : typeParameter.interfaceBounds) {
                  if (!(interfaceBound instanceof ClassRefTypeSignature)) {
                     if (interfaceBound instanceof TypeVariableSignature) {
                        return this.equalsIgnoringTypeParams(interfaceBound);
                     }

                     return false;
                  }

                  if (interfaceBound.equals(other)) {
                     return true;
                  }
               }
            }

            return false;
         } else {
            return true;
         }
      }
   }

   public String toStringWithTypeBound() {
      try {
         return this.resolve().toString();
      } catch (IllegalArgumentException var2) {
         return this.name;
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

      buf.append(this.name);
   }
}
