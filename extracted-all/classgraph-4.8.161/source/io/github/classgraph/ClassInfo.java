package io.github.classgraph;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.annotation.Inherited;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;
import nonapi.io.github.classgraph.json.Id;
import nonapi.io.github.classgraph.reflection.ReflectionUtils;
import nonapi.io.github.classgraph.scanspec.ScanSpec;
import nonapi.io.github.classgraph.types.ParseException;
import nonapi.io.github.classgraph.types.Parser;
import nonapi.io.github.classgraph.types.TypeUtils;
import nonapi.io.github.classgraph.utils.Assert;
import nonapi.io.github.classgraph.utils.LogNode;

public class ClassInfo extends ScanResultObject implements Comparable<ClassInfo>, HasName {
   @Id
   protected String name;
   private int modifiers;
   private boolean isRecord;
   boolean isInherited;
   private int classfileMinorVersion;
   private int classfileMajorVersion;
   protected String typeSignatureStr;
   private transient ClassTypeSignature typeSignature;
   private transient ClassTypeSignature typeDescriptor;
   private String sourceFile;
   private String fullyQualifiedDefiningMethodName;
   protected boolean isExternalClass = true;
   protected boolean isScannedClass;
   transient ClasspathElement classpathElement;
   protected transient Resource classfileResource;
   transient ClassLoader classLoader;
   ModuleInfo moduleInfo;
   PackageInfo packageInfo;
   AnnotationInfoList annotationInfo;
   FieldInfoList fieldInfo;
   MethodInfoList methodInfo;
   AnnotationParameterValueList annotationDefaultParamValues;
   transient List<Classfile.ClassTypeAnnotationDecorator> typeAnnotationDecorators;
   private Set<String> referencedClassNames;
   private ClassInfoList referencedClasses;
   transient boolean annotationDefaultParamValuesHasBeenConvertedToPrimitive;
   private Map<ClassInfo.RelType, Set<ClassInfo>> relatedClasses;
   private transient List<ClassInfo> overrideOrder;
   private transient List<ClassInfo> methodOverrideOrder;
   private static final int ANNOTATION_CLASS_MODIFIER = 8192;
   private static final ClassInfo.ReachableAndDirectlyRelatedClasses NO_REACHABLE_CLASSES = new ClassInfo.ReachableAndDirectlyRelatedClasses(
      Collections.emptySet(), Collections.emptySet()
   );

   ClassInfo() {
   }

   protected ClassInfo(String name, int classModifiers, Resource classfileResource) {
      this.name = name;
      if (name.endsWith(";")) {
         throw new IllegalArgumentException("Bad class name");
      } else {
         this.setModifiers(classModifiers);
         this.classfileResource = classfileResource;
         this.relatedClasses = new EnumMap<>(ClassInfo.RelType.class);
      }
   }

   boolean addRelatedClass(ClassInfo.RelType relType, ClassInfo classInfo) {
      Set<ClassInfo> classInfoSet = this.relatedClasses.get(relType);
      if (classInfoSet == null) {
         this.relatedClasses.put(relType, classInfoSet = new LinkedHashSet<>(4));
      }

      return classInfoSet.add(classInfo);
   }

   static ClassInfo getOrCreateClassInfo(String className, Map<String, ClassInfo> classNameToClassInfo) {
      int numArrayDims = 0;

      String baseClassName;
      for (baseClassName = className; baseClassName.endsWith("[]"); baseClassName = baseClassName.substring(0, baseClassName.length() - 2)) {
         numArrayDims++;
      }

      while (baseClassName.startsWith("[")) {
         numArrayDims++;
         baseClassName = baseClassName.substring(1);
      }

      if (baseClassName.endsWith(";")) {
         baseClassName = baseClassName.substring(baseClassName.length() - 1);
      }

      baseClassName = baseClassName.replace('/', '.');
      ClassInfo classInfo = classNameToClassInfo.get(className);
      if (classInfo == null) {
         if (numArrayDims == 0) {
            classInfo = new ClassInfo(baseClassName, 0, null);
         } else {
            StringBuilder arrayTypeSigStrBuf = new StringBuilder();

            for (int i = 0; i < numArrayDims; i++) {
               arrayTypeSigStrBuf.append('[');
            }

            char baseTypeChar = BaseTypeSignature.getTypeChar(baseClassName);
            TypeSignature elementTypeSignature;
            if (baseTypeChar != 0) {
               arrayTypeSigStrBuf.append(baseTypeChar);
               elementTypeSignature = new BaseTypeSignature(baseTypeChar);
            } else {
               String eltTypeSigStr = "L" + baseClassName.replace('.', '/') + ";";
               arrayTypeSigStrBuf.append(eltTypeSigStr);

               try {
                  elementTypeSignature = ClassRefTypeSignature.parse(new Parser(eltTypeSigStr), null);
                  if (elementTypeSignature == null) {
                     throw new IllegalArgumentException("Could not form array base type signature for class " + baseClassName);
                  }
               } catch (ParseException var10) {
                  throw new IllegalArgumentException("Could not form array base type signature for class " + baseClassName);
               }
            }

            classInfo = new ArrayClassInfo(new ArrayTypeSignature(elementTypeSignature, numArrayDims, arrayTypeSigStrBuf.toString()));
         }

         classNameToClassInfo.put(className, classInfo);
      }

      return classInfo;
   }

   void setClassfileVersion(int minorVersion, int majorVersion) {
      this.classfileMinorVersion = minorVersion;
      this.classfileMajorVersion = majorVersion;
   }

   void setModifiers(int modifiers) {
      this.modifiers |= modifiers;
   }

   void setIsInterface(boolean isInterface) {
      if (isInterface) {
         this.modifiers |= 512;
      }
   }

   void setIsAnnotation(boolean isAnnotation) {
      if (isAnnotation) {
         this.modifiers |= 8192;
      }
   }

   void setIsRecord(boolean isRecord) {
      if (isRecord) {
         this.isRecord = isRecord;
      }
   }

   void setSourceFile(String sourceFile) {
      this.sourceFile = sourceFile;
   }

   void addTypeDecorators(List<Classfile.ClassTypeAnnotationDecorator> classTypeAnnotationDecorators) {
      if (this.typeAnnotationDecorators == null) {
         this.typeAnnotationDecorators = new ArrayList<>();
      }

      this.typeAnnotationDecorators.addAll(classTypeAnnotationDecorators);
   }

   void addSuperclass(String superclassName, Map<String, ClassInfo> classNameToClassInfo) {
      if (superclassName != null && !superclassName.equals("java.lang.Object")) {
         ClassInfo superclassClassInfo = getOrCreateClassInfo(superclassName, classNameToClassInfo);
         this.addRelatedClass(ClassInfo.RelType.SUPERCLASSES, superclassClassInfo);
         superclassClassInfo.addRelatedClass(ClassInfo.RelType.SUBCLASSES, this);
      }
   }

   void addImplementedInterface(String interfaceName, Map<String, ClassInfo> classNameToClassInfo) {
      ClassInfo interfaceClassInfo = getOrCreateClassInfo(interfaceName, classNameToClassInfo);
      interfaceClassInfo.setIsInterface(true);
      this.addRelatedClass(ClassInfo.RelType.IMPLEMENTED_INTERFACES, interfaceClassInfo);
      interfaceClassInfo.addRelatedClass(ClassInfo.RelType.CLASSES_IMPLEMENTING, this);
   }

   static void addClassContainment(List<Classfile.ClassContainment> classContainmentEntries, Map<String, ClassInfo> classNameToClassInfo) {
      for (Classfile.ClassContainment classContainment : classContainmentEntries) {
         ClassInfo innerClassInfo = getOrCreateClassInfo(classContainment.innerClassName, classNameToClassInfo);
         innerClassInfo.setModifiers(classContainment.innerClassModifierBits);
         ClassInfo outerClassInfo = getOrCreateClassInfo(classContainment.outerClassName, classNameToClassInfo);
         innerClassInfo.addRelatedClass(ClassInfo.RelType.CONTAINED_WITHIN_OUTER_CLASS, outerClassInfo);
         outerClassInfo.addRelatedClass(ClassInfo.RelType.CONTAINS_INNER_CLASS, innerClassInfo);
      }
   }

   void addFullyQualifiedDefiningMethodName(String fullyQualifiedDefiningMethodName) {
      this.fullyQualifiedDefiningMethodName = fullyQualifiedDefiningMethodName;
   }

   void addClassAnnotation(AnnotationInfo classAnnotationInfo, Map<String, ClassInfo> classNameToClassInfo) {
      ClassInfo annotationClassInfo = getOrCreateClassInfo(classAnnotationInfo.getName(), classNameToClassInfo);
      annotationClassInfo.setModifiers(8192);
      if (this.annotationInfo == null) {
         this.annotationInfo = new AnnotationInfoList(2);
      }

      this.annotationInfo.add(classAnnotationInfo);
      this.addRelatedClass(ClassInfo.RelType.CLASS_ANNOTATIONS, annotationClassInfo);
      annotationClassInfo.addRelatedClass(ClassInfo.RelType.CLASSES_WITH_ANNOTATION, this);
      if (classAnnotationInfo.getName().equals(Inherited.class.getName())) {
         this.isInherited = true;
      }
   }

   private void addFieldOrMethodAnnotationInfo(
      AnnotationInfoList annotationInfoList, boolean isField, int modifiers, Map<String, ClassInfo> classNameToClassInfo
   ) {
      if (annotationInfoList != null) {
         for (AnnotationInfo fieldAnnotationInfo : annotationInfoList) {
            ClassInfo annotationClassInfo = getOrCreateClassInfo(fieldAnnotationInfo.getName(), classNameToClassInfo);
            annotationClassInfo.setModifiers(8192);
            this.addRelatedClass(isField ? ClassInfo.RelType.FIELD_ANNOTATIONS : ClassInfo.RelType.METHOD_ANNOTATIONS, annotationClassInfo);
            annotationClassInfo.addRelatedClass(
               isField ? ClassInfo.RelType.CLASSES_WITH_FIELD_ANNOTATION : ClassInfo.RelType.CLASSES_WITH_METHOD_ANNOTATION, this
            );
            if (!Modifier.isPrivate(modifiers)) {
               annotationClassInfo.addRelatedClass(
                  isField ? ClassInfo.RelType.CLASSES_WITH_NONPRIVATE_FIELD_ANNOTATION : ClassInfo.RelType.CLASSES_WITH_NONPRIVATE_METHOD_ANNOTATION, this
               );
            }
         }
      }
   }

   void addFieldInfo(FieldInfoList fieldInfoList, Map<String, ClassInfo> classNameToClassInfo) {
      for (FieldInfo fi : fieldInfoList) {
         this.addFieldOrMethodAnnotationInfo(fi.annotationInfo, true, fi.getModifiers(), classNameToClassInfo);
      }

      if (this.fieldInfo == null) {
         this.fieldInfo = fieldInfoList;
      } else {
         this.fieldInfo.addAll(fieldInfoList);
      }
   }

   void addMethodInfo(MethodInfoList methodInfoList, Map<String, ClassInfo> classNameToClassInfo) {
      for (MethodInfo mi : methodInfoList) {
         this.addFieldOrMethodAnnotationInfo(mi.annotationInfo, false, mi.getModifiers(), classNameToClassInfo);
         if (mi.parameterAnnotationInfo != null) {
            for (int i = 0; i < mi.parameterAnnotationInfo.length; i++) {
               AnnotationInfo[] paramAnnotationInfoArr = mi.parameterAnnotationInfo[i];
               if (paramAnnotationInfoArr != null) {
                  for (AnnotationInfo methodParamAnnotationInfo : paramAnnotationInfoArr) {
                     ClassInfo annotationClassInfo = getOrCreateClassInfo(methodParamAnnotationInfo.getName(), classNameToClassInfo);
                     annotationClassInfo.setModifiers(8192);
                     this.addRelatedClass(ClassInfo.RelType.METHOD_PARAMETER_ANNOTATIONS, annotationClassInfo);
                     annotationClassInfo.addRelatedClass(ClassInfo.RelType.CLASSES_WITH_METHOD_PARAMETER_ANNOTATION, this);
                     if (!Modifier.isPrivate(mi.getModifiers())) {
                        annotationClassInfo.addRelatedClass(ClassInfo.RelType.CLASSES_WITH_NONPRIVATE_METHOD_PARAMETER_ANNOTATION, this);
                     }
                  }
               }
            }
         }
      }

      if (this.methodInfo == null) {
         this.methodInfo = methodInfoList;
      } else {
         this.methodInfo.addAll(methodInfoList);
      }
   }

   void setTypeSignature(String typeSignatureStr) {
      this.typeSignatureStr = typeSignatureStr;
   }

   void addAnnotationParamDefaultValues(AnnotationParameterValueList paramNamesAndValues) {
      this.setIsAnnotation(true);
      if (this.annotationDefaultParamValues == null) {
         this.annotationDefaultParamValues = paramNamesAndValues;
      } else {
         this.annotationDefaultParamValues.addAll(paramNamesAndValues);
      }
   }

   static ClassInfo addScannedClass(
      String className,
      int classModifiers,
      boolean isExternalClass,
      Map<String, ClassInfo> classNameToClassInfo,
      ClasspathElement classpathElement,
      Resource classfileResource
   ) {
      ClassInfo classInfo = classNameToClassInfo.get(className);
      if (classInfo == null) {
         classNameToClassInfo.put(className, classInfo = new ClassInfo(className, classModifiers, classfileResource));
      } else {
         if (classInfo.isScannedClass) {
            throw new IllegalArgumentException(
               "Class "
                  + className
                  + " should not have been encountered more than once due to classpath masking -- please report this bug at: https://github.com/classgraph/classgraph/issues"
            );
         }

         classInfo.classfileResource = classfileResource;
         classInfo.modifiers |= classModifiers;
      }

      classInfo.isScannedClass = true;
      classInfo.isExternalClass = isExternalClass;
      classInfo.classpathElement = classpathElement;
      classInfo.classLoader = classpathElement.getClassLoader();
      return classInfo;
   }

   private static Set<ClassInfo> filterClassInfo(Collection<ClassInfo> classes, ScanSpec scanSpec, boolean strictAccept, ClassInfo.ClassType... classTypes) {
      if (classes == null) {
         return Collections.emptySet();
      } else {
         boolean includeAllTypes = classTypes.length == 0;
         boolean includeStandardClasses = false;
         boolean includeImplementedInterfaces = false;
         boolean includeAnnotations = false;
         boolean includeEnums = false;
         boolean includeRecords = false;

         for (ClassInfo.ClassType classType : classTypes) {
            switch (classType) {
               case ALL:
                  includeAllTypes = true;
                  break;
               case STANDARD_CLASS:
                  includeStandardClasses = true;
                  break;
               case IMPLEMENTED_INTERFACE:
                  includeImplementedInterfaces = true;
                  break;
               case ANNOTATION:
                  includeAnnotations = true;
                  break;
               case INTERFACE_OR_ANNOTATION:
                  includeAnnotations = true;
                  includeImplementedInterfaces = true;
                  break;
               case ENUM:
                  includeEnums = true;
                  break;
               case RECORD:
                  includeRecords = true;
                  break;
               default:
                  throw new IllegalArgumentException("Unknown ClassType: " + classType);
            }
         }

         if (includeStandardClasses && includeImplementedInterfaces && includeAnnotations) {
            includeAllTypes = true;
         }

         Set<ClassInfo> classInfoSetFiltered = new LinkedHashSet<>(classes.size());

         for (ClassInfo classInfo : classes) {
            boolean includeType = includeAllTypes
               || includeStandardClasses && classInfo.isStandardClass()
               || includeImplementedInterfaces && classInfo.isImplementedInterface()
               || includeAnnotations && classInfo.isAnnotation()
               || includeEnums && classInfo.isEnum()
               || includeRecords && classInfo.isRecord();
            boolean acceptClass = !classInfo.isExternalClass || scanSpec.enableExternalClasses || !strictAccept;
            if (includeType && acceptClass && !scanSpec.classOrPackageIsRejected(classInfo.name)) {
               classInfoSetFiltered.add(classInfo);
            }
         }

         return classInfoSetFiltered;
      }
   }

   private ClassInfo.ReachableAndDirectlyRelatedClasses filterClassInfo(ClassInfo.RelType relType, boolean strictAccept, ClassInfo.ClassType... classTypes) {
      Set<ClassInfo> directlyRelatedClasses = this.relatedClasses.get(relType);
      if (directlyRelatedClasses == null) {
         return NO_REACHABLE_CLASSES;
      } else {
         Set<ClassInfo> var11 = new LinkedHashSet<>(directlyRelatedClasses);
         Set<ClassInfo> reachableClasses = new LinkedHashSet<>(var11);
         if (relType != ClassInfo.RelType.METHOD_ANNOTATIONS
            && relType != ClassInfo.RelType.METHOD_PARAMETER_ANNOTATIONS
            && relType != ClassInfo.RelType.FIELD_ANNOTATIONS) {
            if (relType == ClassInfo.RelType.CLASSES_WITH_METHOD_ANNOTATION
               || relType == ClassInfo.RelType.CLASSES_WITH_NONPRIVATE_METHOD_ANNOTATION
               || relType == ClassInfo.RelType.CLASSES_WITH_METHOD_PARAMETER_ANNOTATION
               || relType == ClassInfo.RelType.CLASSES_WITH_NONPRIVATE_METHOD_PARAMETER_ANNOTATION
               || relType == ClassInfo.RelType.CLASSES_WITH_FIELD_ANNOTATION
               || relType == ClassInfo.RelType.CLASSES_WITH_NONPRIVATE_FIELD_ANNOTATION) {
               for (ClassInfo subAnnotation : this.filterClassInfo(ClassInfo.RelType.CLASSES_WITH_ANNOTATION, strictAccept, ClassInfo.ClassType.ANNOTATION).reachableClasses) {
                  Set<ClassInfo> annotatedClasses = subAnnotation.relatedClasses.get(relType);
                  if (annotatedClasses != null) {
                     reachableClasses.addAll(annotatedClasses);
                  }
               }
            } else {
               LinkedList<ClassInfo> queue = new LinkedList<>(var11);

               while (!queue.isEmpty()) {
                  ClassInfo head = queue.removeFirst();
                  Set<ClassInfo> headRelatedClasses = head.relatedClasses.get(relType);
                  if (headRelatedClasses != null) {
                     for (ClassInfo directlyReachableFromHead : headRelatedClasses) {
                        if (reachableClasses.add(directlyReachableFromHead)) {
                           queue.add(directlyReachableFromHead);
                        }
                     }
                  }
               }
            }
         } else {
            for (ClassInfo annotation : var11) {
               reachableClasses.addAll(annotation.filterClassInfo(ClassInfo.RelType.CLASS_ANNOTATIONS, strictAccept).reachableClasses);
            }
         }

         if (reachableClasses.isEmpty()) {
            return NO_REACHABLE_CLASSES;
         } else {
            if (relType == ClassInfo.RelType.CLASS_ANNOTATIONS
               || relType == ClassInfo.RelType.METHOD_ANNOTATIONS
               || relType == ClassInfo.RelType.METHOD_PARAMETER_ANNOTATIONS
               || relType == ClassInfo.RelType.FIELD_ANNOTATIONS) {
               Set<ClassInfo> reachableClassesToRemove = null;

               for (ClassInfo reachableClassInfo : reachableClasses) {
                  if (reachableClassInfo.getName().startsWith("java.lang.annotation.") && !var11.contains(reachableClassInfo)) {
                     if (reachableClassesToRemove == null) {
                        reachableClassesToRemove = new LinkedHashSet<>();
                     }

                     reachableClassesToRemove.add(reachableClassInfo);
                  }
               }

               if (reachableClassesToRemove != null) {
                  reachableClasses.removeAll(reachableClassesToRemove);
               }
            }

            return new ClassInfo.ReachableAndDirectlyRelatedClasses(
               filterClassInfo(reachableClasses, this.scanResult.scanSpec, strictAccept, classTypes),
               filterClassInfo(var11, this.scanResult.scanSpec, strictAccept, classTypes)
            );
         }
      }
   }

   static ClassInfoList getAllClasses(Collection<ClassInfo> classes, ScanSpec scanSpec) {
      return new ClassInfoList(filterClassInfo(classes, scanSpec, true, ClassInfo.ClassType.ALL), true);
   }

   static ClassInfoList getAllEnums(Collection<ClassInfo> classes, ScanSpec scanSpec) {
      return new ClassInfoList(filterClassInfo(classes, scanSpec, true, ClassInfo.ClassType.ENUM), true);
   }

   static ClassInfoList getAllRecords(Collection<ClassInfo> classes, ScanSpec scanSpec) {
      return new ClassInfoList(filterClassInfo(classes, scanSpec, true, ClassInfo.ClassType.RECORD), true);
   }

   static ClassInfoList getAllStandardClasses(Collection<ClassInfo> classes, ScanSpec scanSpec) {
      return new ClassInfoList(filterClassInfo(classes, scanSpec, true, ClassInfo.ClassType.STANDARD_CLASS), true);
   }

   static ClassInfoList getAllImplementedInterfaceClasses(Collection<ClassInfo> classes, ScanSpec scanSpec) {
      return new ClassInfoList(filterClassInfo(classes, scanSpec, true, ClassInfo.ClassType.IMPLEMENTED_INTERFACE), true);
   }

   static ClassInfoList getAllAnnotationClasses(Collection<ClassInfo> classes, ScanSpec scanSpec) {
      return new ClassInfoList(filterClassInfo(classes, scanSpec, true, ClassInfo.ClassType.ANNOTATION), true);
   }

   static ClassInfoList getAllInterfacesOrAnnotationClasses(Collection<ClassInfo> classes, ScanSpec scanSpec) {
      return new ClassInfoList(filterClassInfo(classes, scanSpec, true, ClassInfo.ClassType.INTERFACE_OR_ANNOTATION), true);
   }

   @Override
   public String getName() {
      return this.name;
   }

   static String getSimpleName(String className) {
      return className.substring(Math.max(className.lastIndexOf(46), className.lastIndexOf(36)) + 1);
   }

   public String getSimpleName() {
      return getSimpleName(this.name);
   }

   public ModuleInfo getModuleInfo() {
      return this.moduleInfo;
   }

   public PackageInfo getPackageInfo() {
      return this.packageInfo;
   }

   public String getPackageName() {
      return PackageInfo.getParentPackageName(this.name);
   }

   public boolean isExternalClass() {
      return this.isExternalClass;
   }

   public int getClassfileMinorVersion() {
      return this.classfileMinorVersion;
   }

   public int getClassfileMajorVersion() {
      return this.classfileMajorVersion;
   }

   public int getModifiers() {
      return this.modifiers;
   }

   public String getModifiersStr() {
      StringBuilder buf = new StringBuilder();
      TypeUtils.modifiersToString(this.modifiers, TypeUtils.ModifierType.CLASS, false, buf);
      return buf.toString();
   }

   public boolean isPublic() {
      return Modifier.isPublic(this.modifiers);
   }

   public boolean isPrivate() {
      return Modifier.isPrivate(this.modifiers);
   }

   public boolean isProtected() {
      return Modifier.isProtected(this.modifiers);
   }

   public boolean isPackageVisible() {
      return !this.isPublic() && !this.isPrivate() && !this.isProtected();
   }

   public boolean isAbstract() {
      return Modifier.isAbstract(this.modifiers);
   }

   public boolean isSynthetic() {
      return (this.modifiers & 4096) != 0;
   }

   public boolean isFinal() {
      return Modifier.isFinal(this.modifiers);
   }

   public boolean isStatic() {
      return Modifier.isStatic(this.modifiers);
   }

   public boolean isAnnotation() {
      return (this.modifiers & 8192) != 0;
   }

   public boolean isInterface() {
      return this.isInterfaceOrAnnotation() && !this.isAnnotation();
   }

   public boolean isInterfaceOrAnnotation() {
      return (this.modifiers & 512) != 0;
   }

   public boolean isEnum() {
      return (this.modifiers & 16384) != 0;
   }

   public boolean isRecord() {
      return this.isRecord;
   }

   public boolean isStandardClass() {
      return !this.isAnnotation() && !this.isInterface();
   }

   public boolean isArrayClass() {
      return this instanceof ArrayClassInfo;
   }

   public boolean extendsSuperclass(Class<?> superclass) {
      return this.extendsSuperclass(superclass.getName());
   }

   public boolean extendsSuperclass(String superclassName) {
      return superclassName.equals("java.lang.Object") && this.isStandardClass() || this.getSuperclasses().containsName(superclassName);
   }

   public boolean isInnerClass() {
      return !this.getOuterClasses().isEmpty();
   }

   public boolean isOuterClass() {
      return !this.getInnerClasses().isEmpty();
   }

   public boolean isAnonymousInnerClass() {
      return this.fullyQualifiedDefiningMethodName != null;
   }

   public boolean isImplementedInterface() {
      return this.relatedClasses.get(ClassInfo.RelType.CLASSES_IMPLEMENTING) != null || this.isInterface();
   }

   public boolean implementsInterface(Class<?> interfaceClazz) {
      Assert.isInterface(interfaceClazz);
      return this.implementsInterface(interfaceClazz.getName());
   }

   public boolean implementsInterface(String interfaceName) {
      return this.getInterfaces().containsName(interfaceName);
   }

   public boolean hasAnnotation(Class<? extends Annotation> annotation) {
      Assert.isAnnotation(annotation);
      return this.hasAnnotation(annotation.getName());
   }

   public boolean hasAnnotation(String annotationName) {
      return this.getAnnotations().containsName(annotationName);
   }

   public boolean hasDeclaredField(String fieldName) {
      return this.getDeclaredFieldInfo().containsName(fieldName);
   }

   public boolean hasField(String fieldName) {
      for (ClassInfo ci : this.getFieldOverrideOrder()) {
         if (ci.hasDeclaredField(fieldName)) {
            return true;
         }
      }

      return false;
   }

   public boolean hasDeclaredFieldAnnotation(Class<? extends Annotation> annotation) {
      Assert.isAnnotation(annotation);
      return this.hasDeclaredFieldAnnotation(annotation.getName());
   }

   public boolean hasDeclaredFieldAnnotation(String fieldAnnotationName) {
      for (FieldInfo fi : this.getDeclaredFieldInfo()) {
         if (fi.hasAnnotation(fieldAnnotationName)) {
            return true;
         }
      }

      return false;
   }

   public boolean hasFieldAnnotation(Class<? extends Annotation> fieldAnnotation) {
      Assert.isAnnotation(fieldAnnotation);
      return this.hasFieldAnnotation(fieldAnnotation.getName());
   }

   public boolean hasFieldAnnotation(String fieldAnnotationName) {
      for (ClassInfo ci : this.getFieldOverrideOrder()) {
         if (ci.hasDeclaredFieldAnnotation(fieldAnnotationName)) {
            return true;
         }
      }

      return false;
   }

   public boolean hasDeclaredMethod(String methodName) {
      return this.getDeclaredMethodInfo().containsName(methodName);
   }

   public boolean hasMethod(String methodName) {
      for (ClassInfo ci : this.getMethodOverrideOrder()) {
         if (ci.hasDeclaredMethod(methodName)) {
            return true;
         }
      }

      return false;
   }

   public boolean hasDeclaredMethodAnnotation(Class<? extends Annotation> methodAnnotation) {
      Assert.isAnnotation(methodAnnotation);
      return this.hasDeclaredMethodAnnotation(methodAnnotation.getName());
   }

   public boolean hasDeclaredMethodAnnotation(String methodAnnotationName) {
      for (MethodInfo mi : this.getDeclaredMethodInfo()) {
         if (mi.hasAnnotation(methodAnnotationName)) {
            return true;
         }
      }

      return false;
   }

   public boolean hasMethodAnnotation(Class<? extends Annotation> methodAnnotation) {
      Assert.isAnnotation(methodAnnotation);
      return this.hasMethodAnnotation(methodAnnotation.getName());
   }

   public boolean hasMethodAnnotation(String methodAnnotationName) {
      for (ClassInfo ci : this.getMethodOverrideOrder()) {
         if (ci.hasDeclaredMethodAnnotation(methodAnnotationName)) {
            return true;
         }
      }

      return false;
   }

   public boolean hasDeclaredMethodParameterAnnotation(Class<? extends Annotation> methodParameterAnnotation) {
      Assert.isAnnotation(methodParameterAnnotation);
      return this.hasDeclaredMethodParameterAnnotation(methodParameterAnnotation.getName());
   }

   public boolean hasDeclaredMethodParameterAnnotation(String methodParameterAnnotationName) {
      for (MethodInfo mi : this.getDeclaredMethodInfo()) {
         if (mi.hasParameterAnnotation(methodParameterAnnotationName)) {
            return true;
         }
      }

      return false;
   }

   public boolean hasMethodParameterAnnotation(Class<? extends Annotation> methodParameterAnnotation) {
      Assert.isAnnotation(methodParameterAnnotation);
      return this.hasMethodParameterAnnotation(methodParameterAnnotation.getName());
   }

   public boolean hasMethodParameterAnnotation(String methodParameterAnnotationName) {
      for (ClassInfo ci : this.getMethodOverrideOrder()) {
         if (ci.hasDeclaredMethodParameterAnnotation(methodParameterAnnotationName)) {
            return true;
         }
      }

      return false;
   }

   private List<ClassInfo> getFieldOverrideOrder(Set<ClassInfo> visited, List<ClassInfo> overrideOrderOut) {
      if (visited.add(this)) {
         overrideOrderOut.add(this);

         for (ClassInfo iface : this.getInterfaces()) {
            iface.getFieldOverrideOrder(visited, overrideOrderOut);
         }

         ClassInfo superclass = this.getSuperclass();
         if (superclass != null) {
            superclass.getFieldOverrideOrder(visited, overrideOrderOut);
         }
      }

      return overrideOrderOut;
   }

   private List<ClassInfo> getFieldOverrideOrder() {
      if (this.overrideOrder == null) {
         this.overrideOrder = this.getFieldOverrideOrder(new HashSet<>(), new ArrayList<>());
      }

      return this.overrideOrder;
   }

   private List<ClassInfo> getMethodOverrideOrder(Set<ClassInfo> visited, List<ClassInfo> overrideOrderOut) {
      if (!visited.add(this)) {
         return overrideOrderOut;
      } else if (!this.isInterfaceOrAnnotation()) {
         overrideOrderOut.add(this);
         ClassInfo superclass = this.getSuperclass();
         if (superclass != null) {
            superclass.getMethodOverrideOrder(visited, overrideOrderOut);
         }

         for (ClassInfo iface : this.getInterfaces()) {
            iface.getMethodOverrideOrder(visited, overrideOrderOut);
         }

         return overrideOrderOut;
      } else {
         ClassInfoList interfaces = this.getInterfaces();
         int minIndex = Integer.MAX_VALUE;

         for (ClassInfo iface : interfaces) {
            if (visited.contains(iface)) {
               int currIdx = overrideOrderOut.indexOf(iface);
               minIndex = currIdx >= 0 && currIdx < minIndex ? currIdx : minIndex;
            }
         }

         if (minIndex == Integer.MAX_VALUE) {
            overrideOrderOut.add(this);
         } else {
            overrideOrderOut.add(minIndex, this);
         }

         for (ClassInfo ifacex : interfaces) {
            ifacex.getMethodOverrideOrder(visited, overrideOrderOut);
         }

         return overrideOrderOut;
      }
   }

   private List<ClassInfo> getMethodOverrideOrder() {
      if (this.methodOverrideOrder == null) {
         this.methodOverrideOrder = this.getMethodOverrideOrder(new HashSet<>(), new ArrayList<>());
      }

      return this.methodOverrideOrder;
   }

   public ClassInfoList getSubclasses() {
      return this.getName().equals("java.lang.Object")
         ? this.scanResult.getAllStandardClasses()
         : new ClassInfoList(this.filterClassInfo(ClassInfo.RelType.SUBCLASSES, !this.isExternalClass), true);
   }

   public ClassInfoList getSuperclasses() {
      return new ClassInfoList(this.filterClassInfo(ClassInfo.RelType.SUPERCLASSES, false), false);
   }

   public ClassInfo getSuperclass() {
      Set<ClassInfo> superClasses = this.relatedClasses.get(ClassInfo.RelType.SUPERCLASSES);
      if (superClasses != null && !superClasses.isEmpty()) {
         if (superClasses.size() > 2) {
            throw new IllegalArgumentException("More than one superclass: " + superClasses);
         } else {
            ClassInfo superclass = superClasses.iterator().next();
            return superclass.getName().equals("java.lang.Object") ? null : superclass;
         }
      } else {
         return null;
      }
   }

   public ClassInfoList getOuterClasses() {
      return new ClassInfoList(this.filterClassInfo(ClassInfo.RelType.CONTAINED_WITHIN_OUTER_CLASS, false), false);
   }

   public ClassInfoList getInnerClasses() {
      return new ClassInfoList(this.filterClassInfo(ClassInfo.RelType.CONTAINS_INNER_CLASS, false), true);
   }

   public String getFullyQualifiedDefiningMethodName() {
      return this.fullyQualifiedDefiningMethodName;
   }

   public ClassInfoList getInterfaces() {
      ClassInfo.ReachableAndDirectlyRelatedClasses implementedInterfaces = this.filterClassInfo(ClassInfo.RelType.IMPLEMENTED_INTERFACES, false);
      Set<ClassInfo> allInterfaces = new LinkedHashSet<>(implementedInterfaces.reachableClasses);

      for (ClassInfo superclass : this.filterClassInfo(ClassInfo.RelType.SUPERCLASSES, false).reachableClasses) {
         Set<ClassInfo> superclassImplementedInterfaces = superclass.filterClassInfo(ClassInfo.RelType.IMPLEMENTED_INTERFACES, false).reachableClasses;
         allInterfaces.addAll(superclassImplementedInterfaces);
      }

      return new ClassInfoList(allInterfaces, implementedInterfaces.directlyRelatedClasses, false);
   }

   public ClassInfoList getClassesImplementing() {
      ClassInfo.ReachableAndDirectlyRelatedClasses implementingClasses = this.filterClassInfo(ClassInfo.RelType.CLASSES_IMPLEMENTING, !this.isExternalClass);
      Set<ClassInfo> allImplementingClasses = new LinkedHashSet<>(implementingClasses.reachableClasses);

      for (ClassInfo implementingClass : implementingClasses.reachableClasses) {
         Set<ClassInfo> implementingSubclasses = implementingClass.filterClassInfo(ClassInfo.RelType.SUBCLASSES, !implementingClass.isExternalClass).reachableClasses;
         allImplementingClasses.addAll(implementingSubclasses);
      }

      return new ClassInfoList(allImplementingClasses, implementingClasses.directlyRelatedClasses, true);
   }

   public ClassInfoList getAnnotations() {
      if (!this.scanResult.scanSpec.enableAnnotationInfo) {
         throw new IllegalArgumentException("Please call ClassGraph#enableAnnotationInfo() before #scan()");
      } else {
         ClassInfo.ReachableAndDirectlyRelatedClasses annotationClasses = this.filterClassInfo(ClassInfo.RelType.CLASS_ANNOTATIONS, false);
         Set<ClassInfo> inheritedSuperclassAnnotations = null;

         for (ClassInfo superclass : this.getSuperclasses()) {
            for (ClassInfo superclassAnnotation : superclass.filterClassInfo(ClassInfo.RelType.CLASS_ANNOTATIONS, false).reachableClasses) {
               if (superclassAnnotation != null && superclassAnnotation.isInherited) {
                  if (inheritedSuperclassAnnotations == null) {
                     inheritedSuperclassAnnotations = new LinkedHashSet<>();
                  }

                  inheritedSuperclassAnnotations.add(superclassAnnotation);
               }
            }
         }

         if (inheritedSuperclassAnnotations == null) {
            return new ClassInfoList(annotationClasses, true);
         } else {
            inheritedSuperclassAnnotations.addAll(annotationClasses.reachableClasses);
            return new ClassInfoList(inheritedSuperclassAnnotations, annotationClasses.directlyRelatedClasses, true);
         }
      }
   }

   private ClassInfoList getFieldOrMethodAnnotations(ClassInfo.RelType relType) {
      boolean isField = relType == ClassInfo.RelType.FIELD_ANNOTATIONS;
      if ((isField ? this.scanResult.scanSpec.enableFieldInfo : this.scanResult.scanSpec.enableMethodInfo) && this.scanResult.scanSpec.enableAnnotationInfo) {
         ClassInfo.ReachableAndDirectlyRelatedClasses fieldOrMethodAnnotations = this.filterClassInfo(relType, false, ClassInfo.ClassType.ANNOTATION);
         Set<ClassInfo> fieldOrMethodAnnotationsAndMetaAnnotations = new LinkedHashSet<>(fieldOrMethodAnnotations.reachableClasses);
         return new ClassInfoList(fieldOrMethodAnnotationsAndMetaAnnotations, fieldOrMethodAnnotations.directlyRelatedClasses, true);
      } else {
         throw new IllegalArgumentException(
            "Please call ClassGraph#enable" + (isField ? "Field" : "Method") + "Info() and #enableAnnotationInfo() before #scan()"
         );
      }
   }

   private ClassInfoList getClassesWithFieldOrMethodAnnotation(ClassInfo.RelType relType) {
      boolean isField = relType == ClassInfo.RelType.CLASSES_WITH_FIELD_ANNOTATION || relType == ClassInfo.RelType.CLASSES_WITH_NONPRIVATE_FIELD_ANNOTATION;
      if ((isField ? this.scanResult.scanSpec.enableFieldInfo : this.scanResult.scanSpec.enableMethodInfo) && this.scanResult.scanSpec.enableAnnotationInfo) {
         ClassInfo.ReachableAndDirectlyRelatedClasses classesWithDirectlyAnnotatedFieldsOrMethods = this.filterClassInfo(relType, !this.isExternalClass);
         ClassInfo.ReachableAndDirectlyRelatedClasses annotationsWithThisMetaAnnotation = this.filterClassInfo(
            ClassInfo.RelType.CLASSES_WITH_ANNOTATION, !this.isExternalClass, ClassInfo.ClassType.ANNOTATION
         );
         if (annotationsWithThisMetaAnnotation.reachableClasses.isEmpty()) {
            return new ClassInfoList(classesWithDirectlyAnnotatedFieldsOrMethods, true);
         } else {
            Set<ClassInfo> allClassesWithAnnotatedOrMetaAnnotatedFieldsOrMethods = new LinkedHashSet<>(
               classesWithDirectlyAnnotatedFieldsOrMethods.reachableClasses
            );

            for (ClassInfo metaAnnotatedAnnotation : annotationsWithThisMetaAnnotation.reachableClasses) {
               allClassesWithAnnotatedOrMetaAnnotatedFieldsOrMethods.addAll(
                  metaAnnotatedAnnotation.filterClassInfo(relType, !metaAnnotatedAnnotation.isExternalClass).reachableClasses
               );
            }

            return new ClassInfoList(
               allClassesWithAnnotatedOrMetaAnnotatedFieldsOrMethods, classesWithDirectlyAnnotatedFieldsOrMethods.directlyRelatedClasses, true
            );
         }
      } else {
         throw new IllegalArgumentException(
            "Please call ClassGraph#enable" + (isField ? "Field" : "Method") + "Info() and #enableAnnotationInfo() before #scan()"
         );
      }
   }

   public AnnotationInfoList getAnnotationInfo() {
      if (!this.scanResult.scanSpec.enableAnnotationInfo) {
         throw new IllegalArgumentException("Please call ClassGraph#enableAnnotationInfo() before #scan()");
      } else {
         return AnnotationInfoList.getIndirectAnnotations(this.annotationInfo, this);
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

   public AnnotationParameterValueList getAnnotationDefaultParameterValues() {
      if (!this.scanResult.scanSpec.enableAnnotationInfo) {
         throw new IllegalArgumentException("Please call ClassGraph#enableAnnotationInfo() before #scan()");
      } else if (!this.isAnnotation()) {
         throw new IllegalArgumentException("Class is not an annotation: " + this.getName());
      } else if (this.annotationDefaultParamValues == null) {
         return AnnotationParameterValueList.EMPTY_LIST;
      } else {
         if (!this.annotationDefaultParamValuesHasBeenConvertedToPrimitive) {
            this.annotationDefaultParamValues.convertWrapperArraysToPrimitiveArrays(this);
            this.annotationDefaultParamValuesHasBeenConvertedToPrimitive = true;
         }

         return this.annotationDefaultParamValues;
      }
   }

   public ClassInfoList getClassesWithAnnotation() {
      if (!this.scanResult.scanSpec.enableAnnotationInfo) {
         throw new IllegalArgumentException("Please call ClassGraph#enableAnnotationInfo() before #scan()");
      } else {
         ClassInfo.ReachableAndDirectlyRelatedClasses classesWithAnnotation = this.filterClassInfo(
            ClassInfo.RelType.CLASSES_WITH_ANNOTATION, !this.isExternalClass
         );
         if (!this.isInherited) {
            return new ClassInfoList(classesWithAnnotation, true);
         } else {
            Set<ClassInfo> classesWithAnnotationAndTheirSubclasses = new LinkedHashSet<>(classesWithAnnotation.reachableClasses);

            for (ClassInfo classWithAnnotation : classesWithAnnotation.reachableClasses) {
               classesWithAnnotationAndTheirSubclasses.addAll(classWithAnnotation.getSubclasses());
            }

            return new ClassInfoList(classesWithAnnotationAndTheirSubclasses, classesWithAnnotation.directlyRelatedClasses, true);
         }
      }
   }

   ClassInfoList getClassesWithAnnotationDirectOnly() {
      return new ClassInfoList(this.filterClassInfo(ClassInfo.RelType.CLASSES_WITH_ANNOTATION, !this.isExternalClass), true);
   }

   private MethodInfoList getDeclaredMethodInfo(String methodName, boolean getNormalMethods, boolean getConstructorMethods, boolean getStaticInitializerMethods) {
      if (!this.scanResult.scanSpec.enableMethodInfo) {
         throw new IllegalArgumentException("Please call ClassGraph#enableMethodInfo() before #scan()");
      } else if (this.methodInfo == null) {
         return MethodInfoList.EMPTY_LIST;
      } else if (methodName == null) {
         MethodInfoList methodInfoList = new MethodInfoList();

         for (MethodInfo mi : this.methodInfo) {
            String miName = mi.getName();
            boolean isConstructor = "<init>".equals(miName);
            boolean isStaticInitializer = "<clinit>".equals(miName);
            if (isConstructor && getConstructorMethods
               || isStaticInitializer && getStaticInitializerMethods
               || !isConstructor && !isStaticInitializer && getNormalMethods) {
               methodInfoList.add(mi);
            }
         }

         return methodInfoList;
      } else {
         boolean hasMethodWithName = false;

         for (MethodInfo f : this.methodInfo) {
            if (f.getName().equals(methodName)) {
               hasMethodWithName = true;
               break;
            }
         }

         if (!hasMethodWithName) {
            return MethodInfoList.EMPTY_LIST;
         } else {
            MethodInfoList methodInfoList = new MethodInfoList();

            for (MethodInfo mix : this.methodInfo) {
               if (mix.getName().equals(methodName)) {
                  methodInfoList.add(mix);
               }
            }

            return methodInfoList;
         }
      }
   }

   private MethodInfoList getMethodInfo(String methodName, boolean getNormalMethods, boolean getConstructorMethods, boolean getStaticInitializerMethods) {
      if (!this.scanResult.scanSpec.enableMethodInfo) {
         throw new IllegalArgumentException("Please call ClassGraph#enableMethodInfo() before #scan()");
      } else {
         MethodInfoList methodInfoList = new MethodInfoList();
         Set<Entry<String, String>> nameAndTypeDescriptorSet = new HashSet<>();

         for (ClassInfo ci : this.getMethodOverrideOrder()) {
            for (MethodInfo mi : ci.getDeclaredMethodInfo(methodName, getNormalMethods, getConstructorMethods, getStaticInitializerMethods)) {
               if (nameAndTypeDescriptorSet.add(new SimpleEntry<>(mi.getName(), mi.getTypeDescriptorStr()))) {
                  methodInfoList.add(mi);
               }
            }
         }

         return methodInfoList;
      }
   }

   public MethodInfoList getDeclaredMethodInfo() {
      return this.getDeclaredMethodInfo(null, true, false, false);
   }

   public MethodInfoList getMethodInfo() {
      return this.getMethodInfo(null, true, false, false);
   }

   public MethodInfoList getDeclaredConstructorInfo() {
      return this.getDeclaredMethodInfo(null, false, true, false);
   }

   public MethodInfoList getConstructorInfo() {
      return this.getMethodInfo(null, false, true, false);
   }

   public MethodInfoList getDeclaredMethodAndConstructorInfo() {
      return this.getDeclaredMethodInfo(null, true, true, false);
   }

   public MethodInfoList getMethodAndConstructorInfo() {
      return this.getMethodInfo(null, true, true, false);
   }

   public MethodInfoList getDeclaredMethodInfo(String methodName) {
      return this.getDeclaredMethodInfo(methodName, false, false, false);
   }

   public MethodInfoList getMethodInfo(String methodName) {
      return this.getMethodInfo(methodName, false, false, false);
   }

   public ClassInfoList getMethodAnnotations() {
      return this.getFieldOrMethodAnnotations(ClassInfo.RelType.METHOD_ANNOTATIONS);
   }

   public ClassInfoList getMethodParameterAnnotations() {
      return this.getFieldOrMethodAnnotations(ClassInfo.RelType.METHOD_PARAMETER_ANNOTATIONS);
   }

   public ClassInfoList getClassesWithMethodAnnotation() {
      Set<ClassInfo> classesWithMethodAnnotation = new HashSet<>(this.getClassesWithFieldOrMethodAnnotation(ClassInfo.RelType.CLASSES_WITH_METHOD_ANNOTATION));

      for (ClassInfo classWithNonprivateMethodAnnotationOrMetaAnnotation : this.getClassesWithFieldOrMethodAnnotation(
         ClassInfo.RelType.CLASSES_WITH_NONPRIVATE_METHOD_ANNOTATION
      )) {
         classesWithMethodAnnotation.addAll(classWithNonprivateMethodAnnotationOrMetaAnnotation.getSubclasses());
      }

      return new ClassInfoList(classesWithMethodAnnotation, new HashSet<>(this.getClassesWithMethodAnnotationDirectOnly()), true);
   }

   public ClassInfoList getClassesWithMethodParameterAnnotation() {
      Set<ClassInfo> classesWithMethodParameterAnnotation = new HashSet<>(
         this.getClassesWithFieldOrMethodAnnotation(ClassInfo.RelType.CLASSES_WITH_METHOD_PARAMETER_ANNOTATION)
      );

      for (ClassInfo classWithNonprivateMethodParameterAnnotationOrMetaAnnotation : this.getClassesWithFieldOrMethodAnnotation(
         ClassInfo.RelType.CLASSES_WITH_NONPRIVATE_METHOD_PARAMETER_ANNOTATION
      )) {
         classesWithMethodParameterAnnotation.addAll(classWithNonprivateMethodParameterAnnotationOrMetaAnnotation.getSubclasses());
      }

      return new ClassInfoList(classesWithMethodParameterAnnotation, new HashSet<>(this.getClassesWithMethodParameterAnnotationDirectOnly()), true);
   }

   ClassInfoList getClassesWithMethodAnnotationDirectOnly() {
      return new ClassInfoList(this.filterClassInfo(ClassInfo.RelType.CLASSES_WITH_METHOD_ANNOTATION, !this.isExternalClass), true);
   }

   ClassInfoList getClassesWithMethodParameterAnnotationDirectOnly() {
      return new ClassInfoList(this.filterClassInfo(ClassInfo.RelType.CLASSES_WITH_METHOD_PARAMETER_ANNOTATION, !this.isExternalClass), true);
   }

   public FieldInfoList getDeclaredFieldInfo() {
      if (!this.scanResult.scanSpec.enableFieldInfo) {
         throw new IllegalArgumentException("Please call ClassGraph#enableFieldInfo() before #scan()");
      } else {
         return this.fieldInfo == null ? FieldInfoList.EMPTY_LIST : this.fieldInfo;
      }
   }

   public FieldInfoList getFieldInfo() {
      if (!this.scanResult.scanSpec.enableFieldInfo) {
         throw new IllegalArgumentException("Please call ClassGraph#enableFieldInfo() before #scan()");
      } else {
         FieldInfoList fieldInfoList = new FieldInfoList();
         Set<String> fieldNameSet = new HashSet<>();

         for (ClassInfo ci : this.getFieldOverrideOrder()) {
            for (FieldInfo fi : ci.getDeclaredFieldInfo()) {
               if (fieldNameSet.add(fi.getName())) {
                  fieldInfoList.add(fi);
               }
            }
         }

         return fieldInfoList;
      }
   }

   public FieldInfoList getEnumConstants() {
      if (!this.isEnum()) {
         throw new IllegalArgumentException("Class " + this.getName() + " is not an enum");
      } else {
         return this.getFieldInfo().filter(new FieldInfoList.FieldInfoFilter() {
            @Override
            public boolean accept(FieldInfo fieldInfo) {
               return fieldInfo.isEnum();
            }
         });
      }
   }

   public List<Object> getEnumConstantObjects() {
      if (!this.isEnum()) {
         throw new IllegalArgumentException("Class " + this.getName() + " is not an enum");
      } else {
         Class<?> enumClass = this.loadClass();
         FieldInfoList consts = this.getEnumConstants();
         List<Object> constObjs = new ArrayList<>(consts.size());
         ReflectionUtils reflectionUtils = this.scanResult == null ? new ReflectionUtils() : this.scanResult.reflectionUtils;

         for (FieldInfo constFieldInfo : consts) {
            Object constObj = reflectionUtils.getStaticFieldVal(true, enumClass, constFieldInfo.getName());
            if (constObj == null) {
               throw new IllegalArgumentException("Could not read enum constant objects");
            }

            constObjs.add(constObj);
         }

         return constObjs;
      }
   }

   public FieldInfo getDeclaredFieldInfo(String fieldName) {
      if (!this.scanResult.scanSpec.enableFieldInfo) {
         throw new IllegalArgumentException("Please call ClassGraph#enableFieldInfo() before #scan()");
      } else if (this.fieldInfo == null) {
         return null;
      } else {
         for (FieldInfo fi : this.fieldInfo) {
            if (fi.getName().equals(fieldName)) {
               return fi;
            }
         }

         return null;
      }
   }

   public FieldInfo getFieldInfo(String fieldName) {
      if (!this.scanResult.scanSpec.enableFieldInfo) {
         throw new IllegalArgumentException("Please call ClassGraph#enableFieldInfo() before #scan()");
      } else {
         for (ClassInfo ci : this.getFieldOverrideOrder()) {
            FieldInfo fi = ci.getDeclaredFieldInfo(fieldName);
            if (fi != null) {
               return fi;
            }
         }

         return null;
      }
   }

   public ClassInfoList getFieldAnnotations() {
      return this.getFieldOrMethodAnnotations(ClassInfo.RelType.FIELD_ANNOTATIONS);
   }

   public ClassInfoList getClassesWithFieldAnnotation() {
      Set<ClassInfo> classesWithMethodAnnotation = new HashSet<>(this.getClassesWithFieldOrMethodAnnotation(ClassInfo.RelType.CLASSES_WITH_FIELD_ANNOTATION));

      for (ClassInfo classWithNonprivateMethodAnnotationOrMetaAnnotation : this.getClassesWithFieldOrMethodAnnotation(
         ClassInfo.RelType.CLASSES_WITH_NONPRIVATE_FIELD_ANNOTATION
      )) {
         classesWithMethodAnnotation.addAll(classWithNonprivateMethodAnnotationOrMetaAnnotation.getSubclasses());
      }

      return new ClassInfoList(classesWithMethodAnnotation, new HashSet<>(this.getClassesWithMethodAnnotationDirectOnly()), true);
   }

   ClassInfoList getClassesWithFieldAnnotationDirectOnly() {
      return new ClassInfoList(this.filterClassInfo(ClassInfo.RelType.CLASSES_WITH_FIELD_ANNOTATION, !this.isExternalClass), true);
   }

   public ClassTypeSignature getTypeSignature() {
      if (this.typeSignatureStr == null) {
         return null;
      } else {
         if (this.typeSignature == null) {
            try {
               this.typeSignature = ClassTypeSignature.parse(this.typeSignatureStr, this);
               this.typeSignature.setScanResult(this.scanResult);
               if (this.typeAnnotationDecorators != null) {
                  for (Classfile.ClassTypeAnnotationDecorator decorator : this.typeAnnotationDecorators) {
                     decorator.decorate(this.typeSignature);
                  }
               }
            } catch (ParseException var3) {
               throw new IllegalArgumentException(
                  "Invalid type signature for class "
                     + this.getName()
                     + " in classpath element "
                     + this.getClasspathElementURI()
                     + " : "
                     + this.typeSignatureStr,
                  var3
               );
            }
         }

         return this.typeSignature;
      }
   }

   public String getTypeSignatureStr() {
      return this.typeSignatureStr;
   }

   public ClassTypeSignature getTypeSignatureOrTypeDescriptor() {
      ClassTypeSignature typeSig = null;

      try {
         typeSig = this.getTypeSignature();
         if (typeSig != null) {
            return typeSig;
         }
      } catch (Exception var3) {
      }

      return this.getTypeDescriptor();
   }

   public ClassTypeSignature getTypeDescriptor() {
      if (this.typeDescriptor == null) {
         this.typeDescriptor = new ClassTypeSignature(this, this.getSuperclass(), this.getInterfaces());
         this.typeDescriptor.setScanResult(this.scanResult);
         if (this.typeAnnotationDecorators != null) {
            for (Classfile.ClassTypeAnnotationDecorator decorator : this.typeAnnotationDecorators) {
               decorator.decorate(this.typeDescriptor);
            }
         }
      }

      return this.typeDescriptor;
   }

   public String getSourceFile() {
      return this.sourceFile;
   }

   public URI getClasspathElementURI() {
      return this.classfileResource.getClasspathElementURI();
   }

   public URL getClasspathElementURL() {
      try {
         return this.getClasspathElementURI().toURL();
      } catch (MalformedURLException | IllegalArgumentException var2) {
         throw new IllegalArgumentException("Could not get classpath element URL", var2);
      }
   }

   public File getClasspathElementFile() {
      if (this.classpathElement == null) {
         throw new IllegalArgumentException("Classpath element is not known for this classpath element");
      } else {
         return this.classpathElement.getFile();
      }
   }

   public ModuleRef getModuleRef() {
      if (this.classpathElement == null) {
         throw new IllegalArgumentException("Classpath element is not known for this classpath element");
      } else {
         return this.classpathElement instanceof ClasspathElementModule ? ((ClasspathElementModule)this.classpathElement).getModuleRef() : null;
      }
   }

   public Resource getResource() {
      return this.classfileResource;
   }

   @Override
   public <T> Class<T> loadClass(Class<T> superclassOrInterfaceType, boolean ignoreExceptions) {
      return super.loadClass(superclassOrInterfaceType, ignoreExceptions);
   }

   @Override
   public <T> Class<T> loadClass(Class<T> superclassOrInterfaceType) {
      return super.loadClass(superclassOrInterfaceType, false);
   }

   @Override
   public Class<?> loadClass(boolean ignoreExceptions) {
      return super.loadClass(ignoreExceptions);
   }

   @Override
   public Class<?> loadClass() {
      return super.loadClass(false);
   }

   @Override
   protected String getClassName() {
      return this.name;
   }

   @Override
   protected ClassInfo getClassInfo() {
      return this;
   }

   @Override
   void setScanResult(ScanResult scanResult) {
      super.setScanResult(scanResult);
      if (this.typeSignature != null) {
         this.typeSignature.setScanResult(scanResult);
      }

      if (this.annotationInfo != null) {
         for (AnnotationInfo ai : this.annotationInfo) {
            ai.setScanResult(scanResult);
         }
      }

      if (this.fieldInfo != null) {
         for (FieldInfo fi : this.fieldInfo) {
            fi.setScanResult(scanResult);
         }
      }

      if (this.methodInfo != null) {
         for (MethodInfo mi : this.methodInfo) {
            mi.setScanResult(scanResult);
         }
      }

      if (this.annotationDefaultParamValues != null) {
         for (AnnotationParameterValue apv : this.annotationDefaultParamValues) {
            apv.setScanResult(scanResult);
         }
      }
   }

   void handleRepeatableAnnotations(Set<String> allRepeatableAnnotationNames) {
      if (this.annotationInfo != null) {
         this.annotationInfo
            .handleRepeatableAnnotations(
               allRepeatableAnnotationNames, this, ClassInfo.RelType.CLASS_ANNOTATIONS, ClassInfo.RelType.CLASSES_WITH_ANNOTATION, null
            );
      }

      if (this.fieldInfo != null) {
         for (FieldInfo fi : this.fieldInfo) {
            fi.handleRepeatableAnnotations(allRepeatableAnnotationNames);
         }
      }

      if (this.methodInfo != null) {
         for (MethodInfo mi : this.methodInfo) {
            mi.handleRepeatableAnnotations(allRepeatableAnnotationNames);
         }
      }
   }

   void addReferencedClassNames(Set<String> refdClassNames) {
      if (this.referencedClassNames == null) {
         this.referencedClassNames = refdClassNames;
      } else {
         this.referencedClassNames.addAll(refdClassNames);
      }
   }

   @Override
   protected void findReferencedClassInfo(Map<String, ClassInfo> classNameToClassInfo, Set<ClassInfo> refdClassInfo, LogNode log) {
      super.findReferencedClassInfo(classNameToClassInfo, refdClassInfo, log);
      if (this.referencedClassNames != null) {
         for (String refdClassName : this.referencedClassNames) {
            ClassInfo classInfo = getOrCreateClassInfo(refdClassName, classNameToClassInfo);
            classInfo.setScanResult(this.scanResult);
            refdClassInfo.add(classInfo);
         }
      }

      this.getMethodInfo().findReferencedClassInfo(classNameToClassInfo, refdClassInfo, log);
      this.getFieldInfo().findReferencedClassInfo(classNameToClassInfo, refdClassInfo, log);
      this.getAnnotationInfo().findReferencedClassInfo(classNameToClassInfo, refdClassInfo, log);
      if (this.annotationDefaultParamValues != null) {
         this.annotationDefaultParamValues.findReferencedClassInfo(classNameToClassInfo, refdClassInfo, log);
      }

      try {
         ClassTypeSignature classSig = this.getTypeSignature();
         if (classSig != null) {
            classSig.findReferencedClassInfo(classNameToClassInfo, refdClassInfo, log);
         }
      } catch (IllegalArgumentException var7) {
         if (log != null) {
            log.log("Illegal type signature for class " + this.getClassName() + ": " + this.getTypeSignatureStr());
         }
      }
   }

   void setReferencedClasses(ClassInfoList refdClasses) {
      this.referencedClasses = refdClasses;
   }

   public ClassInfoList getClassDependencies() {
      if (!this.scanResult.scanSpec.enableInterClassDependencies) {
         throw new IllegalArgumentException("Please call ClassGraph#enableInterClassDependencies() before #scan()");
      } else {
         return this.referencedClasses == null ? ClassInfoList.EMPTY_LIST : this.referencedClasses;
      }
   }

   public int compareTo(ClassInfo o) {
      return this.name.compareTo(o.name);
   }

   @Override
   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      } else if (!(obj instanceof ClassInfo)) {
         return false;
      } else {
         ClassInfo other = (ClassInfo)obj;
         return this.name.equals(other.name);
      }
   }

   @Override
   public int hashCode() {
      return this.name == null ? 0 : this.name.hashCode();
   }

   @Override
   protected void toString(boolean useSimpleNames, StringBuilder buf) {
      if (this.annotationInfo != null) {
         for (AnnotationInfo annotation : this.annotationInfo) {
            if (buf.length() > 0) {
               buf.append(' ');
            }

            annotation.toString(useSimpleNames, buf);
         }
      }

      ClassTypeSignature typeSig = null;

      try {
         typeSig = this.getTypeSignature();
      } catch (Exception var9) {
      }

      if (typeSig != null) {
         typeSig.toStringInternal(
            useSimpleNames ? getSimpleName(this.name) : this.name, false, this.modifiers, this.isAnnotation(), this.isInterface(), this.annotationInfo, buf
         );
      } else {
         TypeUtils.modifiersToString(this.modifiers, TypeUtils.ModifierType.CLASS, false, buf);
         if (buf.length() > 0) {
            buf.append(' ');
         }

         buf.append(
            this.isRecord() ? "record " : (this.isEnum() ? "enum " : (this.isAnnotation() ? "@interface " : (this.isInterface() ? "interface " : "class ")))
         );
         buf.append(useSimpleNames ? getSimpleName(this.name) : this.name);
         ClassInfo superclass = this.getSuperclass();
         if (superclass != null && !superclass.getName().equals("java.lang.Object")) {
            buf.append(" extends ");
            superclass.toString(useSimpleNames, buf);
         }

         Set<ClassInfo> interfaces = this.filterClassInfo(ClassInfo.RelType.IMPLEMENTED_INTERFACES, false).directlyRelatedClasses;
         if (!interfaces.isEmpty()) {
            buf.append(this.isInterface() ? " extends " : " implements ");
            boolean first = true;

            for (ClassInfo iface : interfaces) {
               if (first) {
                  first = false;
               } else {
                  buf.append(", ");
               }

               iface.toString(useSimpleNames, buf);
            }
         }
      }
   }

   private static enum ClassType {
      ALL,
      STANDARD_CLASS,
      IMPLEMENTED_INTERFACE,
      ANNOTATION,
      INTERFACE_OR_ANNOTATION,
      ENUM,
      RECORD;
   }

   static class ReachableAndDirectlyRelatedClasses {
      final Set<ClassInfo> reachableClasses;
      final Set<ClassInfo> directlyRelatedClasses;

      private ReachableAndDirectlyRelatedClasses(Set<ClassInfo> reachableClasses, Set<ClassInfo> directlyRelatedClasses) {
         this.reachableClasses = reachableClasses;
         this.directlyRelatedClasses = directlyRelatedClasses;
      }
   }

   static enum RelType {
      SUPERCLASSES,
      SUBCLASSES,
      CONTAINS_INNER_CLASS,
      CONTAINED_WITHIN_OUTER_CLASS,
      IMPLEMENTED_INTERFACES,
      CLASSES_IMPLEMENTING,
      CLASS_ANNOTATIONS,
      CLASSES_WITH_ANNOTATION,
      METHOD_ANNOTATIONS,
      CLASSES_WITH_METHOD_ANNOTATION,
      CLASSES_WITH_NONPRIVATE_METHOD_ANNOTATION,
      METHOD_PARAMETER_ANNOTATIONS,
      CLASSES_WITH_METHOD_PARAMETER_ANNOTATION,
      CLASSES_WITH_NONPRIVATE_METHOD_PARAMETER_ANNOTATION,
      FIELD_ANNOTATIONS,
      CLASSES_WITH_FIELD_ANNOTATION,
      CLASSES_WITH_NONPRIVATE_FIELD_ANNOTATION;
   }
}
