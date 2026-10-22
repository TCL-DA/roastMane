package io.github.classgraph;

import java.lang.annotation.Annotation;
import java.net.URI;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import nonapi.io.github.classgraph.utils.Assert;
import nonapi.io.github.classgraph.utils.CollectionUtils;

public class ModuleInfo implements Comparable<ModuleInfo>, HasName {
   private String name;
   private transient ClasspathElement classpathElement;
   private transient ModuleRef moduleRef;
   private transient URI locationURI;
   private Set<AnnotationInfo> annotationInfoSet;
   private AnnotationInfoList annotationInfo;
   private Set<PackageInfo> packageInfoSet;
   private Set<ClassInfo> classInfoSet;

   ModuleInfo() {
   }

   ModuleInfo(ModuleRef moduleRef, ClasspathElement classpathElement) {
      this.moduleRef = moduleRef;
      this.classpathElement = classpathElement;
      this.name = classpathElement.getModuleName();
   }

   @Override
   public String getName() {
      return this.name;
   }

   public URI getLocation() {
      if (this.locationURI == null) {
         this.locationURI = this.moduleRef != null ? this.moduleRef.getLocation() : null;
         if (this.locationURI == null) {
            this.locationURI = this.classpathElement.getURI();
         }
      }

      return this.locationURI;
   }

   public ModuleRef getModuleRef() {
      return this.moduleRef;
   }

   void addClassInfo(ClassInfo classInfo) {
      if (this.classInfoSet == null) {
         this.classInfoSet = new HashSet<>();
      }

      this.classInfoSet.add(classInfo);
   }

   public ClassInfo getClassInfo(String className) {
      for (ClassInfo ci : this.classInfoSet) {
         if (ci.getName().equals(className)) {
            return ci;
         }
      }

      return null;
   }

   public ClassInfoList getClassInfo() {
      return new ClassInfoList(this.classInfoSet, true);
   }

   void addPackageInfo(PackageInfo packageInfo) {
      if (this.packageInfoSet == null) {
         this.packageInfoSet = new HashSet<>();
      }

      this.packageInfoSet.add(packageInfo);
   }

   public PackageInfo getPackageInfo(String packageName) {
      if (this.packageInfoSet == null) {
         return null;
      } else {
         for (PackageInfo pi : this.packageInfoSet) {
            if (pi.getName().equals(packageName)) {
               return pi;
            }
         }

         return null;
      }
   }

   public PackageInfoList getPackageInfo() {
      if (this.packageInfoSet == null) {
         return new PackageInfoList(1);
      } else {
         PackageInfoList packageInfoList = new PackageInfoList(this.packageInfoSet);
         CollectionUtils.sortIfNotEmpty(packageInfoList);
         return packageInfoList;
      }
   }

   void addAnnotations(AnnotationInfoList moduleAnnotations) {
      if (moduleAnnotations != null && !moduleAnnotations.isEmpty()) {
         if (this.annotationInfoSet == null) {
            this.annotationInfoSet = new LinkedHashSet<>();
         }

         this.annotationInfoSet.addAll(moduleAnnotations);
      }
   }

   public AnnotationInfo getAnnotationInfo(Class<? extends Annotation> annotation) {
      Assert.isAnnotation(annotation);
      return this.getAnnotationInfo(annotation.getName());
   }

   public AnnotationInfo getAnnotationInfo(String annotationName) {
      return this.getAnnotationInfo().get(annotationName);
   }

   public AnnotationInfoList getAnnotationInfo() {
      if (this.annotationInfo == null) {
         if (this.annotationInfoSet == null) {
            this.annotationInfo = AnnotationInfoList.EMPTY_LIST;
         } else {
            this.annotationInfo = new AnnotationInfoList();
            this.annotationInfo.addAll(this.annotationInfoSet);
         }
      }

      return this.annotationInfo;
   }

   public boolean hasAnnotation(Class<? extends Annotation> annotation) {
      Assert.isAnnotation(annotation);
      return this.hasAnnotation(annotation.getName());
   }

   public boolean hasAnnotation(String annotationName) {
      return this.getAnnotationInfo().containsName(annotationName);
   }

   public int compareTo(ModuleInfo other) {
      int diff = this.name.compareTo(other.name);
      if (diff != 0) {
         return diff;
      } else {
         URI thisLoc = this.getLocation();
         URI otherLoc = other.getLocation();
         return thisLoc != null && otherLoc != null ? thisLoc.compareTo(otherLoc) : 0;
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
      } else {
         return !(obj instanceof ModuleInfo) ? false : this.compareTo((ModuleInfo)obj) == 0;
      }
   }

   @Override
   public String toString() {
      return this.name;
   }
}
