package io.github.classgraph;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import nonapi.io.github.classgraph.utils.LogNode;

public class AnnotationParameterValueList extends MappableInfoList<AnnotationParameterValue> {
   private static final long serialVersionUID = 1L;
   static final AnnotationParameterValueList EMPTY_LIST = new AnnotationParameterValueList();

   public static AnnotationParameterValueList emptyList() {
      return EMPTY_LIST;
   }

   public AnnotationParameterValueList() {
   }

   public AnnotationParameterValueList(int sizeHint) {
      super(sizeHint);
   }

   public AnnotationParameterValueList(Collection<AnnotationParameterValue> annotationParameterValueCollection) {
      super(annotationParameterValueCollection);
   }

   protected void findReferencedClassInfo(Map<String, ClassInfo> classNameToClassInfo, Set<ClassInfo> refdClassInfo, LogNode log) {
      for (AnnotationParameterValue apv : this) {
         apv.findReferencedClassInfo(classNameToClassInfo, refdClassInfo, log);
      }
   }

   void convertWrapperArraysToPrimitiveArrays(ClassInfo annotationClassInfo) {
      for (AnnotationParameterValue apv : this) {
         apv.convertWrapperArraysToPrimitiveArrays(annotationClassInfo);
      }
   }

   public Object getValue(String parameterName) {
      AnnotationParameterValue apv = this.get(parameterName);
      return apv == null ? null : apv.getValue();
   }

   static {
      EMPTY_LIST.makeUnmodifiable();
   }
}
