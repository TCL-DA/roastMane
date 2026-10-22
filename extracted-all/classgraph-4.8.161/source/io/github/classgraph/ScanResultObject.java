package io.github.classgraph;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import nonapi.io.github.classgraph.utils.LogNode;

abstract class ScanResultObject {
   protected transient ScanResult scanResult;
   private transient ClassInfo classInfo;
   protected transient Class<?> classRef;

   void setScanResult(ScanResult scanResult) {
      this.scanResult = scanResult;
   }

   final Set<ClassInfo> findReferencedClassInfo(LogNode log) {
      Set<ClassInfo> refdClassInfo = new LinkedHashSet<>();
      if (this.scanResult != null) {
         this.findReferencedClassInfo(this.scanResult.classNameToClassInfo, refdClassInfo, log);
      }

      return refdClassInfo;
   }

   protected void findReferencedClassInfo(Map<String, ClassInfo> classNameToClassInfo, Set<ClassInfo> refdClassInfo, LogNode log) {
      ClassInfo ci = this.getClassInfo();
      if (ci != null) {
         refdClassInfo.add(ci);
      }
   }

   protected abstract String getClassName();

   ClassInfo getClassInfo() {
      if (this.classInfo == null) {
         if (this.scanResult == null) {
            return null;
         }

         String className = this.getClassName();
         if (className == null) {
            throw new IllegalArgumentException("Class name is not set");
         }

         this.classInfo = this.scanResult.getClassInfo(className);
      }

      return this.classInfo;
   }

   private String getClassInfoNameOrClassName() {
      ClassInfo ci = null;

      try {
         ci = this.getClassInfo();
      } catch (IllegalArgumentException var4) {
      }

      if (ci == null) {
         ci = this.classInfo;
      }

      String className;
      if (ci != null) {
         className = ci.getName();
      } else {
         className = this.getClassName();
      }

      if (className == null) {
         throw new IllegalArgumentException("Class name is not set");
      } else {
         return className;
      }
   }

   <T> Class<T> loadClass(Class<T> superclassOrInterfaceType, boolean ignoreExceptions) {
      if (this.classRef == null) {
         String className = this.getClassInfoNameOrClassName();
         if (this.scanResult != null) {
            this.classRef = this.scanResult.loadClass(className, superclassOrInterfaceType, ignoreExceptions);
         } else {
            try {
               this.classRef = Class.forName(className);
            } catch (Throwable var5) {
               if (!ignoreExceptions) {
                  throw new IllegalArgumentException("Could not load class " + className, var5);
               }
            }
         }
      }

      return (Class<T>)this.classRef;
   }

   <T> Class<T> loadClass(Class<T> superclassOrInterfaceType) {
      return this.loadClass(superclassOrInterfaceType, false);
   }

   Class<?> loadClass(boolean ignoreExceptions) {
      if (this.classRef == null) {
         String className = this.getClassInfoNameOrClassName();
         if (this.scanResult != null) {
            this.classRef = this.scanResult.loadClass(className, ignoreExceptions);
         } else {
            try {
               this.classRef = Class.forName(className);
            } catch (Throwable var4) {
               if (!ignoreExceptions) {
                  throw new IllegalArgumentException("Could not load class " + className, var4);
               }
            }
         }
      }

      return this.classRef;
   }

   Class<?> loadClass() {
      return this.loadClass(false);
   }

   protected abstract void toString(boolean var1, StringBuilder var2);

   String toString(boolean useSimpleNames) {
      StringBuilder buf = new StringBuilder();
      this.toString(useSimpleNames, buf);
      return buf.toString();
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
