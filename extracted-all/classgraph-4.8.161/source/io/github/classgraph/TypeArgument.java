package io.github.classgraph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import nonapi.io.github.classgraph.types.ParseException;
import nonapi.io.github.classgraph.types.Parser;

public final class TypeArgument extends HierarchicalTypeSignature {
   private final TypeArgument.Wildcard wildcard;
   private final ReferenceTypeSignature typeSignature;

   private TypeArgument(TypeArgument.Wildcard wildcard, ReferenceTypeSignature typeSignature) {
      this.wildcard = wildcard;
      this.typeSignature = typeSignature;
   }

   public TypeArgument.Wildcard getWildcard() {
      return this.wildcard;
   }

   public ReferenceTypeSignature getTypeSignature() {
      return this.typeSignature;
   }

   @Override
   protected void addTypeAnnotation(List<Classfile.TypePathNode> typePath, AnnotationInfo annotationInfo) {
      if (typePath.size() == 0 && this.wildcard != TypeArgument.Wildcard.NONE) {
         this.addTypeAnnotation(annotationInfo);
      } else if (typePath.size() > 0 && typePath.get(0).typePathKind == 2) {
         if (this.typeSignature != null) {
            this.typeSignature.addTypeAnnotation(typePath.subList(1, typePath.size()), annotationInfo);
         }
      } else if (this.typeSignature != null) {
         this.typeSignature.addTypeAnnotation(typePath, annotationInfo);
      }
   }

   private static TypeArgument parse(Parser parser, String definingClassName) throws ParseException {
      char peek = parser.peek();
      if (peek == '*') {
         parser.expect('*');
         return new TypeArgument(TypeArgument.Wildcard.ANY, null);
      } else if (peek == '+') {
         parser.expect('+');
         ReferenceTypeSignature typeSignature = ReferenceTypeSignature.parseReferenceTypeSignature(parser, definingClassName);
         if (typeSignature == null) {
            throw new ParseException(parser, "Missing '+' type bound");
         } else {
            return new TypeArgument(TypeArgument.Wildcard.EXTENDS, typeSignature);
         }
      } else if (peek == '-') {
         parser.expect('-');
         ReferenceTypeSignature typeSignature = ReferenceTypeSignature.parseReferenceTypeSignature(parser, definingClassName);
         if (typeSignature == null) {
            throw new ParseException(parser, "Missing '-' type bound");
         } else {
            return new TypeArgument(TypeArgument.Wildcard.SUPER, typeSignature);
         }
      } else {
         ReferenceTypeSignature typeSignature = ReferenceTypeSignature.parseReferenceTypeSignature(parser, definingClassName);
         if (typeSignature == null) {
            throw new ParseException(parser, "Missing type bound");
         } else {
            return new TypeArgument(TypeArgument.Wildcard.NONE, typeSignature);
         }
      }
   }

   static List<TypeArgument> parseList(Parser parser, String definingClassName) throws ParseException {
      if (parser.peek() == '<') {
         parser.expect('<');
         List<TypeArgument> typeArguments = new ArrayList<>(2);

         while (parser.peek() != '>') {
            if (!parser.hasMore()) {
               throw new ParseException(parser, "Missing '>'");
            }

            typeArguments.add(parse(parser, definingClassName));
         }

         parser.expect('>');
         return typeArguments;
      } else {
         return Collections.emptyList();
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
      if (this.typeSignature != null) {
         this.typeSignature.setScanResult(scanResult);
      }
   }

   public void findReferencedClassNames(Set<String> refdClassNames) {
      if (this.typeSignature != null) {
         this.typeSignature.findReferencedClassNames(refdClassNames);
      }
   }

   @Override
   public int hashCode() {
      return (this.typeSignature != null ? this.typeSignature.hashCode() : 0) + 7 * this.wildcard.hashCode();
   }

   @Override
   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      } else if (!(obj instanceof TypeArgument)) {
         return false;
      } else {
         TypeArgument other = (TypeArgument)obj;
         return Objects.equals(this.typeAnnotationInfo, other.typeAnnotationInfo)
            && Objects.equals(this.typeSignature, other.typeSignature)
            && other.wildcard.equals(this.wildcard);
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

      switch (this.wildcard) {
         case ANY:
            buf.append('?');
            break;
         case EXTENDS:
            String typeSigStr = this.typeSignature.toString(useSimpleNames);
            buf.append(typeSigStr.equals("java.lang.Object") ? "?" : "? extends " + typeSigStr);
            break;
         case SUPER:
            buf.append("? super ");
            this.typeSignature.toString(useSimpleNames, buf);
            break;
         case NONE:
         default:
            this.typeSignature.toString(useSimpleNames, buf);
      }
   }

   public static enum Wildcard {
      NONE,
      ANY,
      EXTENDS,
      SUPER;
   }
}
