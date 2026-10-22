package io.github.classgraph;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import nonapi.io.github.classgraph.scanspec.ScanSpec;
import nonapi.io.github.classgraph.utils.CollectionUtils;

public class ClassInfoList extends MappableInfoList<ClassInfo> {
   private final transient Set<ClassInfo> directlyRelatedClasses;
   private final boolean sortByName;
   private static final long serialVersionUID = 1L;
   static final ClassInfoList EMPTY_LIST = new ClassInfoList();

   public static ClassInfoList emptyList() {
      return EMPTY_LIST;
   }

   ClassInfoList(Set<ClassInfo> reachableClasses, Set<ClassInfo> directlyRelatedClasses, boolean sortByName) {
      super(reachableClasses);
      this.sortByName = sortByName;
      if (sortByName) {
         CollectionUtils.sortIfNotEmpty(this);
      }

      this.directlyRelatedClasses = directlyRelatedClasses == null ? reachableClasses : directlyRelatedClasses;
   }

   ClassInfoList(ClassInfo.ReachableAndDirectlyRelatedClasses reachableAndDirectlyRelatedClasses, boolean sortByName) {
      this(reachableAndDirectlyRelatedClasses.reachableClasses, reachableAndDirectlyRelatedClasses.directlyRelatedClasses, sortByName);
   }

   ClassInfoList(Set<ClassInfo> reachableClasses, boolean sortByName) {
      this(reachableClasses, null, sortByName);
   }

   public ClassInfoList() {
      super(1);
      this.sortByName = false;
      this.directlyRelatedClasses = new HashSet<>(2);
   }

   public ClassInfoList(int sizeHint) {
      super(sizeHint);
      this.sortByName = false;
      this.directlyRelatedClasses = new HashSet<>(2);
   }

   public ClassInfoList(Collection<ClassInfo> classInfoCollection) {
      this((Set<ClassInfo>)(classInfoCollection instanceof Set ? (Set)classInfoCollection : new HashSet<>(classInfoCollection)), null, true);
   }

   public <T> List<Class<T>> loadClasses(Class<T> superclassOrInterfaceType, boolean ignoreExceptions) {
      if (this.isEmpty()) {
         return Collections.emptyList();
      } else {
         List<Class<T>> classRefs = new ArrayList<>();

         for (ClassInfo classInfo : this) {
            Class<T> classRef = classInfo.loadClass(superclassOrInterfaceType, ignoreExceptions);
            if (classRef != null) {
               classRefs.add(classRef);
            }
         }

         return classRefs.isEmpty() ? Collections.emptyList() : classRefs;
      }
   }

   public <T> List<Class<T>> loadClasses(Class<T> superclassOrInterfaceType) {
      return this.loadClasses(superclassOrInterfaceType, false);
   }

   public List<Class<?>> loadClasses(boolean ignoreExceptions) {
      if (this.isEmpty()) {
         return Collections.emptyList();
      } else {
         List<Class<?>> classRefs = new ArrayList<>();

         for (ClassInfo classInfo : this) {
            Class<?> classRef = classInfo.loadClass(ignoreExceptions);
            if (classRef != null) {
               classRefs.add(classRef);
            }
         }

         return classRefs.isEmpty() ? Collections.emptyList() : classRefs;
      }
   }

   public List<Class<?>> loadClasses() {
      return this.loadClasses(false);
   }

   public ClassInfoList directOnly() {
      return new ClassInfoList(this.directlyRelatedClasses, this.directlyRelatedClasses, this.sortByName);
   }

   public ClassInfoList union(ClassInfoList... others) {
      Set<ClassInfo> reachableClassesUnion = new LinkedHashSet<>(this);
      Set<ClassInfo> directlyRelatedClassesUnion = new LinkedHashSet<>(this.directlyRelatedClasses);

      for (ClassInfoList other : others) {
         reachableClassesUnion.addAll(other);
         directlyRelatedClassesUnion.addAll(other.directlyRelatedClasses);
      }

      return new ClassInfoList(reachableClassesUnion, directlyRelatedClassesUnion, this.sortByName);
   }

   public ClassInfoList intersect(ClassInfoList... others) {
      ArrayDeque<ClassInfoList> intersectionOrder = new ArrayDeque<>();
      intersectionOrder.add(this);
      boolean foundFirst = false;

      for (ClassInfoList other : others) {
         if (other.sortByName) {
            intersectionOrder.add(other);
         } else if (!foundFirst) {
            foundFirst = true;
            intersectionOrder.push(other);
         } else {
            intersectionOrder.add(other);
         }
      }

      ClassInfoList first = intersectionOrder.remove();
      Set<ClassInfo> reachableClassesIntersection = new LinkedHashSet<>(first);

      while (!intersectionOrder.isEmpty()) {
         reachableClassesIntersection.retainAll(intersectionOrder.remove());
      }

      Set<ClassInfo> directlyRelatedClassesIntersection = new LinkedHashSet<>(this.directlyRelatedClasses);

      for (ClassInfoList otherx : others) {
         directlyRelatedClassesIntersection.retainAll(otherx.directlyRelatedClasses);
      }

      return new ClassInfoList(reachableClassesIntersection, directlyRelatedClassesIntersection, first.sortByName);
   }

   public ClassInfoList exclude(ClassInfoList other) {
      Set<ClassInfo> reachableClassesDifference = new LinkedHashSet<>(this);
      Set<ClassInfo> directlyRelatedClassesDifference = new LinkedHashSet<>(this.directlyRelatedClasses);
      reachableClassesDifference.removeAll(other);
      directlyRelatedClassesDifference.removeAll(other.directlyRelatedClasses);
      return new ClassInfoList(reachableClassesDifference, directlyRelatedClassesDifference, this.sortByName);
   }

   public ClassInfoList filter(ClassInfoList.ClassInfoFilter filter) {
      Set<ClassInfo> reachableClassesFiltered = new LinkedHashSet<>(this.size());
      Set<ClassInfo> directlyRelatedClassesFiltered = new LinkedHashSet<>(this.directlyRelatedClasses.size());

      for (ClassInfo ci : this) {
         if (filter.accept(ci)) {
            reachableClassesFiltered.add(ci);
            if (this.directlyRelatedClasses.contains(ci)) {
               directlyRelatedClassesFiltered.add(ci);
            }
         }
      }

      return new ClassInfoList(reachableClassesFiltered, directlyRelatedClassesFiltered, this.sortByName);
   }

   public ClassInfoList getStandardClasses() {
      return this.filter(new ClassInfoList.ClassInfoFilter() {
         @Override
         public boolean accept(ClassInfo ci) {
            return ci.isStandardClass();
         }
      });
   }

   public ClassInfoList getInterfaces() {
      return this.filter(new ClassInfoList.ClassInfoFilter() {
         @Override
         public boolean accept(ClassInfo ci) {
            return ci.isInterface();
         }
      });
   }

   public ClassInfoList getInterfacesAndAnnotations() {
      return this.filter(new ClassInfoList.ClassInfoFilter() {
         @Override
         public boolean accept(ClassInfo ci) {
            return ci.isInterfaceOrAnnotation();
         }
      });
   }

   public ClassInfoList getImplementedInterfaces() {
      return this.filter(new ClassInfoList.ClassInfoFilter() {
         @Override
         public boolean accept(ClassInfo ci) {
            return ci.isImplementedInterface();
         }
      });
   }

   public ClassInfoList getAnnotations() {
      return this.filter(new ClassInfoList.ClassInfoFilter() {
         @Override
         public boolean accept(ClassInfo ci) {
            return ci.isAnnotation();
         }
      });
   }

   public ClassInfoList getEnums() {
      return this.filter(new ClassInfoList.ClassInfoFilter() {
         @Override
         public boolean accept(ClassInfo ci) {
            return ci.isEnum();
         }
      });
   }

   public ClassInfoList getRecords() {
      return this.filter(new ClassInfoList.ClassInfoFilter() {
         @Override
         public boolean accept(ClassInfo ci) {
            return ci.isRecord();
         }
      });
   }

   public ClassInfoList getAssignableTo(ClassInfo superclassOrInterface) {
      if (superclassOrInterface == null) {
         throw new IllegalArgumentException("assignableToClass parameter cannot be null");
      } else {
         final Set<ClassInfo> allAssignableFromClasses = new HashSet<>();
         if (superclassOrInterface.isStandardClass()) {
            allAssignableFromClasses.addAll(superclassOrInterface.getSubclasses());
         } else if (superclassOrInterface.isInterfaceOrAnnotation()) {
            allAssignableFromClasses.addAll(superclassOrInterface.getClassesImplementing());
         }

         allAssignableFromClasses.add(superclassOrInterface);
         return this.filter(new ClassInfoList.ClassInfoFilter() {
            @Override
            public boolean accept(ClassInfo ci) {
               return allAssignableFromClasses.contains(ci);
            }
         });
      }
   }

   public String generateGraphVizDotFileFromInterClassDependencies(float sizeX, float sizeY, boolean includeExternalClasses) {
      if (this.isEmpty()) {
         throw new IllegalArgumentException("List is empty");
      } else {
         ScanSpec scanSpec = this.get(0).scanResult.scanSpec;
         if (!scanSpec.enableInterClassDependencies) {
            throw new IllegalArgumentException("Please call ClassGraph#enableInterClassDependencies() before #scan()");
         } else {
            return GraphvizDotfileGenerator.generateGraphVizDotFileFromInterClassDependencies(this, sizeX, sizeY, includeExternalClasses);
         }
      }
   }

   public String generateGraphVizDotFileFromInterClassDependencies(float sizeX, float sizeY) {
      if (this.isEmpty()) {
         throw new IllegalArgumentException("List is empty");
      } else {
         ScanSpec scanSpec = this.get(0).scanResult.scanSpec;
         if (!scanSpec.enableInterClassDependencies) {
            throw new IllegalArgumentException("Please call ClassGraph#enableInterClassDependencies() before #scan()");
         } else {
            return GraphvizDotfileGenerator.generateGraphVizDotFileFromInterClassDependencies(this, sizeX, sizeY, scanSpec.enableExternalClasses);
         }
      }
   }

   public String generateGraphVizDotFileFromInterClassDependencies() {
      if (this.isEmpty()) {
         throw new IllegalArgumentException("List is empty");
      } else {
         ScanSpec scanSpec = this.get(0).scanResult.scanSpec;
         if (!scanSpec.enableInterClassDependencies) {
            throw new IllegalArgumentException("Please call ClassGraph#enableInterClassDependencies() before #scan()");
         } else {
            return GraphvizDotfileGenerator.generateGraphVizDotFileFromInterClassDependencies(this, 10.5F, 8.0F, scanSpec.enableExternalClasses);
         }
      }
   }

   @Deprecated
   public String generateGraphVizDotFileFromClassDependencies() {
      return this.generateGraphVizDotFileFromInterClassDependencies();
   }

   public String generateGraphVizDotFile(
      float sizeX,
      float sizeY,
      boolean showFields,
      boolean showFieldTypeDependencyEdges,
      boolean showMethods,
      boolean showMethodTypeDependencyEdges,
      boolean showAnnotations,
      boolean useSimpleNames
   ) {
      if (this.isEmpty()) {
         throw new IllegalArgumentException("List is empty");
      } else {
         ScanSpec scanSpec = this.get(0).scanResult.scanSpec;
         if (!scanSpec.enableClassInfo) {
            throw new IllegalArgumentException("Please call ClassGraph#enableClassInfo() before #scan()");
         } else {
            return GraphvizDotfileGenerator.generateGraphVizDotFile(
               this,
               sizeX,
               sizeY,
               showFields,
               showFieldTypeDependencyEdges,
               showMethods,
               showMethodTypeDependencyEdges,
               showAnnotations,
               useSimpleNames,
               scanSpec
            );
         }
      }
   }

   public String generateGraphVizDotFile(
      float sizeX,
      float sizeY,
      boolean showFields,
      boolean showFieldTypeDependencyEdges,
      boolean showMethods,
      boolean showMethodTypeDependencyEdges,
      boolean showAnnotations
   ) {
      return this.generateGraphVizDotFile(
         sizeX, sizeY, showFields, showFieldTypeDependencyEdges, showMethods, showMethodTypeDependencyEdges, showAnnotations, true
      );
   }

   public String generateGraphVizDotFile(float sizeX, float sizeY) {
      return this.generateGraphVizDotFile(sizeX, sizeY, true, true, true, true, true);
   }

   public String generateGraphVizDotFile() {
      return this.generateGraphVizDotFile(10.5F, 8.0F, true, true, true, true, true);
   }

   public void generateGraphVizDotFile(File file) throws IOException {
      try (PrintWriter writer = new PrintWriter(file)) {
         writer.print(this.generateGraphVizDotFile());
      }
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else if (!(obj instanceof ClassInfoList)) {
         return false;
      } else {
         ClassInfoList other = (ClassInfoList)obj;
         if (this.directlyRelatedClasses == null != (other.directlyRelatedClasses == null)) {
            return false;
         } else {
            return this.directlyRelatedClasses == null
               ? super.equals(other)
               : super.equals(other) && this.directlyRelatedClasses.equals(other.directlyRelatedClasses);
         }
      }
   }

   @Override
   public int hashCode() {
      return super.hashCode() ^ (this.directlyRelatedClasses == null ? 0 : this.directlyRelatedClasses.hashCode());
   }

   static {
      EMPTY_LIST.makeUnmodifiable();
   }

   @FunctionalInterface
   public interface ClassInfoFilter {
      boolean accept(ClassInfo var1);
   }
}
