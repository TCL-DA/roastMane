package io.github.classgraph;

import java.util.List;

public abstract class HierarchicalTypeSignature extends ScanResultObject {
   protected AnnotationInfoList typeAnnotationInfo;

   protected void addTypeAnnotation(AnnotationInfo annotationInfo) {
      if (this.typeAnnotationInfo == null) {
         this.typeAnnotationInfo = new AnnotationInfoList(1);
      }

      this.typeAnnotationInfo.add(annotationInfo);
   }

   @Override
   void setScanResult(ScanResult scanResult) {
      super.setScanResult(scanResult);
      if (this.typeAnnotationInfo != null) {
         for (AnnotationInfo annotationInfo : this.typeAnnotationInfo) {
            annotationInfo.setScanResult(scanResult);
         }
      }
   }

   public AnnotationInfoList getTypeAnnotationInfo() {
      return this.typeAnnotationInfo;
   }

   protected abstract void addTypeAnnotation(List<Classfile.TypePathNode> var1, AnnotationInfo var2);

   protected abstract void toStringInternal(boolean var1, AnnotationInfoList var2, StringBuilder var3);

   @Override
   protected void toString(boolean useSimpleNames, StringBuilder buf) {
      this.toStringInternal(useSimpleNames, null, buf);
   }
}
