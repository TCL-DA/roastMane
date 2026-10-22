package io.github.classgraph;

import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;
import nonapi.io.github.classgraph.utils.Assert;

public class MethodParameterInfo {
   private final MethodInfo methodInfo;
   final AnnotationInfo[] annotationInfo;
   private final int modifiers;
   private final TypeSignature typeDescriptor;
   private final TypeSignature typeSignature;
   private final String name;
   private ScanResult scanResult;

   MethodParameterInfo(
      MethodInfo methodInfo, AnnotationInfo[] annotationInfo, int modifiers, TypeSignature typeDescriptor, TypeSignature typeSignature, String name
   ) {
      this.methodInfo = methodInfo;
      this.name = name;
      this.modifiers = modifiers;
      this.typeDescriptor = typeDescriptor;
      this.typeSignature = typeSignature;
      this.annotationInfo = annotationInfo;
   }

   public MethodInfo getMethodInfo() {
      return this.methodInfo;
   }

   public String getName() {
      return this.name;
   }

   public int getModifiers() {
      return this.modifiers;
   }

   public String getModifiersStr() {
      StringBuilder buf = new StringBuilder();
      modifiersToString(this.modifiers, buf);
      return buf.toString();
   }

   public TypeSignature getTypeSignature() {
      return this.typeSignature;
   }

   public TypeSignature getTypeDescriptor() {
      return this.typeDescriptor;
   }

   public TypeSignature getTypeSignatureOrTypeDescriptor() {
      return this.typeSignature != null ? this.typeSignature : this.typeDescriptor;
   }

   public AnnotationInfoList getAnnotationInfo() {
      if (!this.scanResult.scanSpec.enableAnnotationInfo) {
         throw new IllegalArgumentException("Please call ClassGraph#enableAnnotationInfo() before #scan()");
      } else if (this.annotationInfo != null && this.annotationInfo.length != 0) {
         AnnotationInfoList annotationInfoList = new AnnotationInfoList(this.annotationInfo.length);
         Collections.addAll(annotationInfoList, this.annotationInfo);
         return AnnotationInfoList.getIndirectAnnotations(annotationInfoList, null);
      } else {
         return AnnotationInfoList.EMPTY_LIST;
      }
   }

   public AnnotationInfo getAnnotationInfo(Class<? extends Annotation> annotation) {
      Assert.isAnnotation(annotation);
      return this.getAnnotationInfo(annotation.getName());
   }

   public AnnotationInfo getAnnotationInfo(String annotationName) {
      return this.getAnnotationInfo().get(annotationName);
   }

   public AnnotationInfoList getAnnotationInfoRepeatable(Class<? extends Annotation> annotation) {
      Assert.isAnnotation(annotation);
      return this.getAnnotationInfoRepeatable(annotation.getName());
   }

   public AnnotationInfoList getAnnotationInfoRepeatable(String annotationName) {
      return this.getAnnotationInfo().getRepeatable(annotationName);
   }

   public boolean hasAnnotation(Class<? extends Annotation> annotation) {
      Assert.isAnnotation(annotation);
      return this.hasAnnotation(annotation.getName());
   }

   public boolean hasAnnotation(String annotationName) {
      return this.getAnnotationInfo().containsName(annotationName);
   }

   protected void setScanResult(ScanResult scanResult) {
      this.scanResult = scanResult;
      if (this.annotationInfo != null) {
         for (AnnotationInfo ai : this.annotationInfo) {
            ai.setScanResult(scanResult);
         }
      }

      if (this.typeDescriptor != null) {
         this.typeDescriptor.setScanResult(scanResult);
      }

      if (this.typeSignature != null) {
         this.typeSignature.setScanResult(scanResult);
      }
   }

   public boolean isFinal() {
      return Modifier.isFinal(this.modifiers);
   }

   public boolean isSynthetic() {
      return (this.modifiers & 4096) != 0;
   }

   public boolean isMandated() {
      return (this.modifiers & 32768) != 0;
   }

   @Override
   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      } else if (!(obj instanceof MethodParameterInfo)) {
         return false;
      } else {
         MethodParameterInfo other = (MethodParameterInfo)obj;
         return Objects.equals(this.methodInfo, other.methodInfo)
            && Objects.deepEquals(this.annotationInfo, other.annotationInfo)
            && this.modifiers == other.modifiers
            && Objects.equals(this.typeDescriptor, other.typeDescriptor)
            && Objects.equals(this.typeSignature, other.typeSignature)
            && Objects.equals(this.name, other.name);
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.methodInfo, Arrays.hashCode((Object[])this.annotationInfo), this.typeDescriptor, this.typeSignature, this.name) + this.modifiers;
   }

   static void modifiersToString(int modifiers, StringBuilder buf) {
      if ((modifiers & 16) != 0) {
         buf.append("final ");
      }

      if ((modifiers & 4096) != 0) {
         buf.append("synthetic ");
      }

      if ((modifiers & 32768) != 0) {
         buf.append("mandated ");
      }
   }

   protected void toString(boolean useSimpleNames, StringBuilder buf) {
      if (this.annotationInfo != null) {
         for (AnnotationInfo anAnnotationInfo : this.annotationInfo) {
            anAnnotationInfo.toString(useSimpleNames, buf);
            buf.append(' ');
         }
      }

      modifiersToString(this.modifiers, buf);
      this.getTypeSignatureOrTypeDescriptor().toString(useSimpleNames, buf);
      buf.append(' ');
      buf.append(this.name == null ? "_unnamed_param" : this.name);
   }

   public String toStringWithSimpleNames() {
      StringBuilder buf = new StringBuilder();
      this.toString(true, buf);
      return buf.toString();
   }

   @Override
   public String toString() {
      StringBuilder buf = new StringBuilder();
      this.toString(false, buf);
      return buf.toString();
   }
}
