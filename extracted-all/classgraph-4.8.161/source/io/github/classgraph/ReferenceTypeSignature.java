package io.github.classgraph;

import nonapi.io.github.classgraph.types.ParseException;
import nonapi.io.github.classgraph.types.Parser;

public abstract class ReferenceTypeSignature extends TypeSignature {
   protected ReferenceTypeSignature() {
   }

   static ReferenceTypeSignature parseReferenceTypeSignature(Parser parser, String definingClassName) throws ParseException {
      ClassRefTypeSignature classTypeSignature = ClassRefTypeSignature.parse(parser, definingClassName);
      if (classTypeSignature != null) {
         return classTypeSignature;
      } else {
         TypeVariableSignature typeVariableSignature = TypeVariableSignature.parse(parser, definingClassName);
         if (typeVariableSignature != null) {
            return typeVariableSignature;
         } else {
            ArrayTypeSignature arrayTypeSignature = ArrayTypeSignature.parse(parser, definingClassName);
            return arrayTypeSignature != null ? arrayTypeSignature : null;
         }
      }
   }

   static ReferenceTypeSignature parseClassBound(Parser parser, String definingClassName) throws ParseException {
      parser.expect(':');
      return parseReferenceTypeSignature(parser, definingClassName);
   }
}
