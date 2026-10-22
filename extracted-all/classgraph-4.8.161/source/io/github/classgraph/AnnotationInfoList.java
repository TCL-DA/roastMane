package io.github.classgraph;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import nonapi.io.github.classgraph.utils.Assert;
import nonapi.io.github.classgraph.utils.CollectionUtils;
import nonapi.io.github.classgraph.utils.LogNode;

public class AnnotationInfoList extends MappableInfoList<AnnotationInfo> {
   private AnnotationInfoList directlyRelatedAnnotations;
   private static final long serialVersionUID = 1L;
   static final AnnotationInfoList EMPTY_LIST = new AnnotationInfoList();

   public static AnnotationInfoList emptyList() {
      return EMPTY_LIST;
   }

   public AnnotationInfoList() {
   }

   public AnnotationInfoList(int sizeHint) {
      super(sizeHint);
   }

   public AnnotationInfoList(AnnotationInfoList reachableAnnotations) {
      this(reachableAnnotations, reachableAnnotations);
   }

   AnnotationInfoList(AnnotationInfoList reachableAnnotations, AnnotationInfoList directlyRelatedAnnotations) {
      super(reachableAnnotations);
      this.directlyRelatedAnnotations = directlyRelatedAnnotations;
   }

   public AnnotationInfoList filter(AnnotationInfoList.AnnotationInfoFilter filter) {
      AnnotationInfoList annotationInfoFiltered = new AnnotationInfoList();

      for (AnnotationInfo resource : this) {
         if (filter.accept(resource)) {
            annotationInfoFiltered.add(resource);
         }
      }

      return annotationInfoFiltered;
   }

   protected void findReferencedClassInfo(Map<String, ClassInfo> classNameToClassInfo, Set<ClassInfo> refdClassInfo, LogNode log) {
      for (AnnotationInfo ai : this) {
         ai.findReferencedClassInfo(classNameToClassInfo, refdClassInfo, log);
      }
   }

   void handleRepeatableAnnotations(
      Set<String> allRepeatableAnnotationNames,
      ClassInfo containingClassInfo,
      ClassInfo.RelType forwardRelType,
      ClassInfo.RelType reverseRelType0,
      ClassInfo.RelType reverseRelType1
   ) {
      List<AnnotationInfo> repeatableAnnotations = null;

      for (int i = this.size() - 1; i >= 0; i--) {
         AnnotationInfo ai = this.get(i);
         if (allRepeatableAnnotationNames.contains(ai.getName())) {
            if (repeatableAnnotations == null) {
               repeatableAnnotations = new ArrayList<>();
            }

            repeatableAnnotations.add(ai);
            this.remove(i);
         }
      }

      if (repeatableAnnotations != null) {
         for (AnnotationInfo repeatableAnnotation : repeatableAnnotations) {
            AnnotationParameterValueList values = repeatableAnnotation.getParameterValues();
            if (!values.isEmpty()) {
               AnnotationParameterValue apv = values.get("value");
               if (apv != null) {
                  Object arr = apv.getValue();
                  if (arr instanceof Object[]) {
                     for (Object value : (Object[])arr) {
                        if (value instanceof AnnotationInfo) {
                           AnnotationInfo ai = (AnnotationInfo)value;
                           this.add(ai);
                           if (forwardRelType != null && (reverseRelType0 != null || reverseRelType1 != null)) {
                              ClassInfo annotationClass = ai.getClassInfo();
                              if (annotationClass != null) {
                                 containingClassInfo.addRelatedClass(forwardRelType, annotationClass);
                                 if (reverseRelType0 != null) {
                                    annotationClass.addRelatedClass(reverseRelType0, containingClassInfo);
                                 }

                                 if (reverseRelType1 != null) {
                                    annotationClass.addRelatedClass(reverseRelType1, containingClassInfo);
                                 }
                              }
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private static void findMetaAnnotations(AnnotationInfo ai, AnnotationInfoList allAnnotationsOut, Set<ClassInfo> visited) {
      ClassInfo annotationClassInfo = ai.getClassInfo();
      if (annotationClassInfo != null && annotationClassInfo.annotationInfo != null && visited.add(annotationClassInfo)) {
         for (AnnotationInfo metaAnnotationInfo : annotationClassInfo.annotationInfo) {
            ClassInfo metaAnnotationClassInfo = metaAnnotationInfo.getClassInfo();
            String metaAnnotationClassName = metaAnnotationClassInfo.getName();
            if (!metaAnnotationClassName.startsWith("java.lang.annotation.")) {
               allAnnotationsOut.add(metaAnnotationInfo);
               findMetaAnnotations(metaAnnotationInfo, allAnnotationsOut, visited);
            }
         }
      }
   }

   static AnnotationInfoList getIndirectAnnotations(AnnotationInfoList directAnnotationInfo, ClassInfo annotatedClass) {
      Set<ClassInfo> directOrInheritedAnnotationClasses = new HashSet<>();
      Set<ClassInfo> reachedAnnotationClasses = new HashSet<>();
      AnnotationInfoList reachableAnnotationInfo = new AnnotationInfoList(directAnnotationInfo == null ? 2 : directAnnotationInfo.size());
      if (directAnnotationInfo != null) {
         for (AnnotationInfo dai : directAnnotationInfo) {
            directOrInheritedAnnotationClasses.add(dai.getClassInfo());
            reachableAnnotationInfo.add(dai);
            findMetaAnnotations(dai, reachableAnnotationInfo, reachedAnnotationClasses);
         }
      }

      if (annotatedClass != null) {
         for (ClassInfo superclass : annotatedClass.getSuperclasses()) {
            if (superclass.annotationInfo != null) {
               for (AnnotationInfo sai : superclass.annotationInfo) {
                  if (sai.isInherited() && directOrInheritedAnnotationClasses.add(sai.getClassInfo())) {
                     reachableAnnotationInfo.add(sai);
                     AnnotationInfoList reachableMetaAnnotationInfo = new AnnotationInfoList(2);
                     findMetaAnnotations(sai, reachableMetaAnnotationInfo, reachedAnnotationClasses);

                     for (AnnotationInfo rmai : reachableMetaAnnotationInfo) {
                        if (rmai.isInherited()) {
                           reachableAnnotationInfo.add(rmai);
                        }
                     }
                  }
               }
            }
         }
      }

      AnnotationInfoList directAnnotationInfoSorted = directAnnotationInfo == null ? EMPTY_LIST : new AnnotationInfoList(directAnnotationInfo);
      CollectionUtils.sortIfNotEmpty(directAnnotationInfoSorted);
      AnnotationInfoList annotationInfoList = new AnnotationInfoList(reachableAnnotationInfo, directAnnotationInfoSorted);
      CollectionUtils.sortIfNotEmpty(annotationInfoList);
      return annotationInfoList;
   }

   public AnnotationInfoList directOnly() {
      return this.directlyRelatedAnnotations == null ? this : new AnnotationInfoList(this.directlyRelatedAnnotations, null);
   }

   public AnnotationInfoList getRepeatable(Class<? extends Annotation> annotationClass) {
      Assert.isAnnotation(annotationClass);
      return this.getRepeatable(annotationClass.getName());
   }

   public AnnotationInfoList getRepeatable(String name) {
      boolean hasNamedAnnotation = false;

      for (AnnotationInfo ai : this) {
         if (ai.getName().equals(name)) {
            hasNamedAnnotation = true;
            break;
         }
      }

      if (!hasNamedAnnotation) {
         return EMPTY_LIST;
      } else {
         AnnotationInfoList matchingAnnotations = new AnnotationInfoList(this.size());

         for (AnnotationInfo aix : this) {
            if (aix.getName().equals(name)) {
               matchingAnnotations.add(aix);
            }
         }

         return matchingAnnotations;
      }
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else if (!(obj instanceof AnnotationInfoList)) {
         return false;
      } else {
         AnnotationInfoList other = (AnnotationInfoList)obj;
         if (this.directlyRelatedAnnotations == null != (other.directlyRelatedAnnotations == null)) {
            return false;
         } else {
            return this.directlyRelatedAnnotations == null
               ? super.equals(other)
               : super.equals(other) && this.directlyRelatedAnnotations.equals(other.directlyRelatedAnnotations);
         }
      }
   }

   @Override
   public int hashCode() {
      return super.hashCode() ^ (this.directlyRelatedAnnotations == null ? 0 : this.directlyRelatedAnnotations.hashCode());
   }

   static {
      EMPTY_LIST.makeUnmodifiable();
   }

   @FunctionalInterface
   public interface AnnotationInfoFilter {
      boolean accept(AnnotationInfo var1);
   }
}
