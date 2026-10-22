package io.github.classgraph;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import nonapi.io.github.classgraph.concurrency.WorkQueue;
import nonapi.io.github.classgraph.fileslice.reader.ClassfileReader;
import nonapi.io.github.classgraph.scanspec.ScanSpec;
import nonapi.io.github.classgraph.types.ParseException;
import nonapi.io.github.classgraph.utils.CollectionUtils;
import nonapi.io.github.classgraph.utils.JarUtils;
import nonapi.io.github.classgraph.utils.LogNode;
import nonapi.io.github.classgraph.utils.StringUtils;

class Classfile {
   private ClassfileReader reader;
   private final ClasspathElement classpathElement;
   private final List<ClasspathElement> classpathOrder;
   private final String relativePath;
   private final Resource classfileResource;
   private final ConcurrentHashMap<String, String> stringInternMap;
   private String className;
   private int minorVersion;
   private int majorVersion;
   private final boolean isExternalClass;
   private int classModifiers;
   private boolean isInterface;
   private boolean isRecord;
   private boolean isAnnotation;
   private String superclassName;
   private List<String> implementedInterfaces;
   private AnnotationInfoList classAnnotations;
   private String fullyQualifiedDefiningMethodName;
   private List<Classfile.ClassContainment> classContainmentEntries;
   private AnnotationParameterValueList annotationParamDefaultValues;
   private Set<String> refdClassNames;
   private FieldInfoList fieldInfoList;
   private MethodInfoList methodInfoList;
   private String typeSignatureStr;
   private String sourceFile;
   private List<Classfile.ClassTypeAnnotationDecorator> classTypeAnnotationDecorators;
   private final Set<String> acceptedClassNamesFound;
   private final Set<String> classNamesScheduledForExtendedScanning;
   private List<Scanner.ClassfileScanWorkUnit> additionalWorkUnits;
   private final ScanSpec scanSpec;
   private int cpCount;
   private int[] entryOffset;
   private int[] entryTag;
   private int[] indirectStringRefs;
   private static final AnnotationInfo[] NO_ANNOTATIONS = new AnnotationInfo[0];

   private void scheduleScanningIfExternalClass(String className, String relationship, LogNode log) {
      if (className != null
         && !className.equals("java.lang.Object")
         && !this.acceptedClassNamesFound.contains(className)
         && this.classNamesScheduledForExtendedScanning.add(className)) {
         if (this.scanSpec.classAcceptReject.isRejected(className)) {
            if (log != null) {
               log.log("Cannot extend scanning upwards to external " + relationship + " " + className + ", since it is rejected");
            }
         } else {
            String classfilePath = JarUtils.classNameToClassfilePath(className);
            Resource classResource = this.classpathElement.getResource(classfilePath);
            ClasspathElement foundInClasspathElt = null;
            if (classResource != null) {
               foundInClasspathElt = this.classpathElement;
            } else {
               for (ClasspathElement classpathOrderElt : this.classpathOrder) {
                  if (classpathOrderElt != this.classpathElement) {
                     classResource = classpathOrderElt.getResource(classfilePath);
                     if (classResource != null) {
                        foundInClasspathElt = classpathOrderElt;
                        break;
                     }
                  }
               }
            }

            if (classResource != null) {
               if (log != null) {
                  classResource.scanLog = log.log(
                     "Extending scanning to external "
                        + relationship
                        + (foundInClasspathElt == this.classpathElement ? " in same classpath element" : " in classpath element " + foundInClasspathElt)
                        + ": "
                        + className
                  );
               }

               if (this.additionalWorkUnits == null) {
                  this.additionalWorkUnits = new ArrayList<>();
               }

               this.additionalWorkUnits.add(new Scanner.ClassfileScanWorkUnit(foundInClasspathElt, classResource, true));
            } else if (log != null) {
               log.log("External " + relationship + " " + className + " was not found in non-rejected packages -- cannot extend scanning to this class");
            }
         }
      }
   }

   private void extendScanningUpwardsFromAnnotationParameterValues(Object annotationParamVal, LogNode log) {
      if (annotationParamVal != null) {
         if (annotationParamVal instanceof AnnotationInfo) {
            AnnotationInfo annotationInfo = (AnnotationInfo)annotationParamVal;
            this.scheduleScanningIfExternalClass(annotationInfo.getClassName(), "annotation class", log);

            for (AnnotationParameterValue apv : annotationInfo.getParameterValues()) {
               this.extendScanningUpwardsFromAnnotationParameterValues(apv.getValue(), log);
            }
         } else if (annotationParamVal instanceof AnnotationEnumValue) {
            this.scheduleScanningIfExternalClass(((AnnotationEnumValue)annotationParamVal).getClassName(), "enum class", log);
         } else if (annotationParamVal instanceof AnnotationClassRef) {
            this.scheduleScanningIfExternalClass(((AnnotationClassRef)annotationParamVal).getClassName(), "class ref", log);
         } else if (annotationParamVal.getClass().isArray()) {
            int i = 0;

            for (int n = Array.getLength(annotationParamVal); i < n; i++) {
               this.extendScanningUpwardsFromAnnotationParameterValues(Array.get(annotationParamVal, i), log);
            }
         }
      }
   }

   private void extendScanningUpwards(LogNode log) {
      if (this.superclassName != null) {
         this.scheduleScanningIfExternalClass(this.superclassName, "superclass", log);
      }

      if (this.implementedInterfaces != null) {
         for (String interfaceName : this.implementedInterfaces) {
            this.scheduleScanningIfExternalClass(interfaceName, "interface", log);
         }
      }

      if (this.classAnnotations != null) {
         for (AnnotationInfo annotationInfo : this.classAnnotations) {
            this.scheduleScanningIfExternalClass(annotationInfo.getName(), "class annotation", log);
            this.extendScanningUpwardsFromAnnotationParameterValues(annotationInfo, log);
         }
      }

      if (this.annotationParamDefaultValues != null) {
         for (AnnotationParameterValue apv : this.annotationParamDefaultValues) {
            this.extendScanningUpwardsFromAnnotationParameterValues(apv.getValue(), log);
         }
      }

      if (this.methodInfoList != null) {
         for (MethodInfo methodInfo : this.methodInfoList) {
            if (methodInfo.annotationInfo != null) {
               for (AnnotationInfo methodAnnotationInfo : methodInfo.annotationInfo) {
                  this.scheduleScanningIfExternalClass(methodAnnotationInfo.getName(), "method annotation", log);
                  this.extendScanningUpwardsFromAnnotationParameterValues(methodAnnotationInfo, log);
               }

               if (methodInfo.parameterAnnotationInfo != null && methodInfo.parameterAnnotationInfo.length > 0) {
                  for (AnnotationInfo[] paramAnnInfoArr : methodInfo.parameterAnnotationInfo) {
                     if (paramAnnInfoArr != null && paramAnnInfoArr.length > 0) {
                        for (AnnotationInfo paramAnnInfo : paramAnnInfoArr) {
                           this.scheduleScanningIfExternalClass(paramAnnInfo.getName(), "method parameter annotation", log);
                           this.extendScanningUpwardsFromAnnotationParameterValues(paramAnnInfo, log);
                        }
                     }
                  }
               }
            }

            if (methodInfo.getThrownExceptionNames() != null) {
               for (String thrownExceptionName : methodInfo.getThrownExceptionNames()) {
                  this.scheduleScanningIfExternalClass(thrownExceptionName, "method throws", log);
               }
            }
         }
      }

      if (this.fieldInfoList != null) {
         for (FieldInfo fieldInfo : this.fieldInfoList) {
            if (fieldInfo.annotationInfo != null) {
               for (AnnotationInfo fieldAnnotationInfo : fieldInfo.annotationInfo) {
                  this.scheduleScanningIfExternalClass(fieldAnnotationInfo.getName(), "field annotation", log);
                  this.extendScanningUpwardsFromAnnotationParameterValues(fieldAnnotationInfo, log);
               }
            }
         }
      }

      if (this.classContainmentEntries != null) {
         for (Classfile.ClassContainment classContainmentEntry : this.classContainmentEntries) {
            if (classContainmentEntry.innerClassName.equals(this.className)) {
               this.scheduleScanningIfExternalClass(classContainmentEntry.outerClassName, "outer class", log);
            }
         }
      }
   }

   void link(Map<String, ClassInfo> classNameToClassInfo, Map<String, PackageInfo> packageNameToPackageInfo, Map<String, ModuleInfo> moduleNameToModuleInfo) {
      boolean isModuleDescriptor = false;
      boolean isPackageDescriptor = false;
      ClassInfo classInfo = null;
      if (this.className.equals("module-info")) {
         isModuleDescriptor = true;
      } else if (!this.className.equals("package-info") && !this.className.endsWith(".package-info")) {
         classInfo = ClassInfo.addScannedClass(
            this.className, this.classModifiers, this.isExternalClass, classNameToClassInfo, this.classpathElement, this.classfileResource
         );
         classInfo.setClassfileVersion(this.minorVersion, this.majorVersion);
         classInfo.setModifiers(this.classModifiers);
         classInfo.setIsInterface(this.isInterface);
         classInfo.setIsAnnotation(this.isAnnotation);
         classInfo.setIsRecord(this.isRecord);
         classInfo.setSourceFile(this.sourceFile);
         if (this.superclassName != null) {
            classInfo.addSuperclass(this.superclassName, classNameToClassInfo);
         }

         if (this.implementedInterfaces != null) {
            for (String interfaceName : this.implementedInterfaces) {
               classInfo.addImplementedInterface(interfaceName, classNameToClassInfo);
            }
         }

         if (this.classAnnotations != null) {
            for (AnnotationInfo classAnnotation : this.classAnnotations) {
               classInfo.addClassAnnotation(classAnnotation, classNameToClassInfo);
            }
         }

         if (this.classContainmentEntries != null) {
            ClassInfo.addClassContainment(this.classContainmentEntries, classNameToClassInfo);
         }

         if (this.annotationParamDefaultValues != null) {
            classInfo.addAnnotationParamDefaultValues(this.annotationParamDefaultValues);
         }

         if (this.fullyQualifiedDefiningMethodName != null) {
            classInfo.addFullyQualifiedDefiningMethodName(this.fullyQualifiedDefiningMethodName);
         }

         if (this.fieldInfoList != null) {
            classInfo.addFieldInfo(this.fieldInfoList, classNameToClassInfo);
         }

         if (this.methodInfoList != null) {
            classInfo.addMethodInfo(this.methodInfoList, classNameToClassInfo);
         }

         if (this.typeSignatureStr != null) {
            classInfo.setTypeSignature(this.typeSignatureStr);
         }

         if (this.refdClassNames != null) {
            classInfo.addReferencedClassNames(this.refdClassNames);
         }

         if (this.classTypeAnnotationDecorators != null) {
            classInfo.addTypeDecorators(this.classTypeAnnotationDecorators);
         }
      } else {
         isPackageDescriptor = true;
      }

      PackageInfo packageInfo = null;
      if (!isModuleDescriptor) {
         String packageName = PackageInfo.getParentPackageName(this.className);
         packageInfo = PackageInfo.getOrCreatePackage(packageName, packageNameToPackageInfo, this.scanSpec);
         if (isPackageDescriptor) {
            packageInfo.addAnnotations(this.classAnnotations);
         } else if (classInfo != null) {
            packageInfo.addClassInfo(classInfo);
            classInfo.packageInfo = packageInfo;
         }
      }

      String moduleName = this.classpathElement.getModuleName();
      if (moduleName != null) {
         ModuleInfo moduleInfo = moduleNameToModuleInfo.get(moduleName);
         if (moduleInfo == null) {
            moduleNameToModuleInfo.put(moduleName, moduleInfo = new ModuleInfo(this.classfileResource.getModuleRef(), this.classpathElement));
         }

         if (isModuleDescriptor) {
            moduleInfo.addAnnotations(this.classAnnotations);
         }

         if (classInfo != null) {
            moduleInfo.addClassInfo(classInfo);
            classInfo.moduleInfo = moduleInfo;
         }

         if (packageInfo != null) {
            moduleInfo.addPackageInfo(packageInfo);
         }
      }
   }

   private String intern(String str) {
      if (str == null) {
         return null;
      } else {
         String interned = this.stringInternMap.putIfAbsent(str, str);
         return interned != null ? interned : str;
      }
   }

   private int getConstantPoolStringOffset(int cpIdx, int subFieldIdx) throws Classfile.ClassfileFormatException {
      if (cpIdx >= 1 && cpIdx < this.cpCount) {
         int t = this.entryTag[cpIdx];
         if ((t == 12 || subFieldIdx == 0) && (t != 12 || subFieldIdx == 0 || subFieldIdx == 1)) {
            if (t == 0) {
               return 0;
            } else {
               int cpIdxToUse;
               if (t == 1) {
                  cpIdxToUse = cpIdx;
               } else if (t != 7 && t != 8 && t != 19) {
                  if (t != 12) {
                     throw new Classfile.ClassfileFormatException(
                        "Wrong tag number "
                           + t
                           + " at constant pool index "
                           + cpIdx
                           + ", cannot continue reading class. Please report this at https://github.com/classgraph/classgraph/issues"
                     );
                  }

                  int compoundIndirIdx = this.indirectStringRefs[cpIdx];
                  if (compoundIndirIdx == -1) {
                     throw new Classfile.ClassfileFormatException(
                        "Bad string indirection index, cannot continue reading class. Please report this at https://github.com/classgraph/classgraph/issues"
                     );
                  }

                  int indirIdx = (subFieldIdx == 0 ? compoundIndirIdx >> 16 : compoundIndirIdx) & 65535;
                  if (indirIdx == 0) {
                     throw new Classfile.ClassfileFormatException(
                        "Bad string indirection index, cannot continue reading class. Please report this at https://github.com/classgraph/classgraph/issues"
                     );
                  }

                  cpIdxToUse = indirIdx;
               } else {
                  int indirIdx = this.indirectStringRefs[cpIdx];
                  if (indirIdx == -1) {
                     throw new Classfile.ClassfileFormatException(
                        "Bad string indirection index, cannot continue reading class. Please report this at https://github.com/classgraph/classgraph/issues"
                     );
                  }

                  if (indirIdx == 0) {
                     return 0;
                  }

                  cpIdxToUse = indirIdx;
               }

               if (cpIdxToUse >= 1 && cpIdxToUse < this.cpCount) {
                  return this.entryOffset[cpIdxToUse];
               } else {
                  throw new Classfile.ClassfileFormatException(
                     "Constant pool index "
                        + cpIdx
                        + ", should be in range [1, "
                        + (this.cpCount - 1)
                        + "] -- cannot continue reading class. Please report this at https://github.com/classgraph/classgraph/issues"
                  );
               }
            }
         } else {
            throw new Classfile.ClassfileFormatException(
               "Bad subfield index "
                  + subFieldIdx
                  + " for tag "
                  + t
                  + ", cannot continue reading class. Please report this at https://github.com/classgraph/classgraph/issues"
            );
         }
      } else {
         throw new Classfile.ClassfileFormatException(
            "Constant pool index "
               + cpIdx
               + ", should be in range [1, "
               + (this.cpCount - 1)
               + "] -- cannot continue reading class. Please report this at https://github.com/classgraph/classgraph/issues"
         );
      }
   }

   private String getConstantPoolString(int cpIdx, boolean replaceSlashWithDot, boolean stripLSemicolon) throws Classfile.ClassfileFormatException, IOException {
      int constantPoolStringOffset = this.getConstantPoolStringOffset(cpIdx, 0);
      if (constantPoolStringOffset == 0) {
         return null;
      } else {
         int utfLen = this.reader.readUnsignedShort((long)constantPoolStringOffset);
         return utfLen == 0 ? "" : this.intern(this.reader.readString((long)constantPoolStringOffset + 2L, utfLen, replaceSlashWithDot, stripLSemicolon));
      }
   }

   private String getConstantPoolString(int cpIdx, int subFieldIdx) throws Classfile.ClassfileFormatException, IOException {
      int constantPoolStringOffset = this.getConstantPoolStringOffset(cpIdx, subFieldIdx);
      if (constantPoolStringOffset == 0) {
         return null;
      } else {
         int utfLen = this.reader.readUnsignedShort((long)constantPoolStringOffset);
         return utfLen == 0 ? "" : this.intern(this.reader.readString((long)constantPoolStringOffset + 2L, utfLen, false, false));
      }
   }

   private String getConstantPoolString(int cpIdx) throws Classfile.ClassfileFormatException, IOException {
      return this.getConstantPoolString(cpIdx, 0);
   }

   private byte getConstantPoolStringFirstByte(int cpIdx) throws Classfile.ClassfileFormatException, IOException {
      int constantPoolStringOffset = this.getConstantPoolStringOffset(cpIdx, 0);
      if (constantPoolStringOffset == 0) {
         return 0;
      } else {
         int utfLen = this.reader.readUnsignedShort((long)constantPoolStringOffset);
         return utfLen == 0 ? 0 : this.reader.readByte((long)constantPoolStringOffset + 2L);
      }
   }

   private String getConstantPoolClassName(int cpIdx) throws Classfile.ClassfileFormatException, IOException {
      return this.getConstantPoolString(cpIdx, true, false);
   }

   private String getConstantPoolClassDescriptor(int cpIdx) throws Classfile.ClassfileFormatException, IOException {
      return this.getConstantPoolString(cpIdx, true, true);
   }

   private boolean constantPoolStringEquals(int cpIdx, String asciiStr) throws Classfile.ClassfileFormatException, IOException {
      int cpStrOffset = this.getConstantPoolStringOffset(cpIdx, 0);
      if (cpStrOffset == 0) {
         return asciiStr == null;
      } else if (asciiStr == null) {
         return false;
      } else {
         int cpStrLen = this.reader.readUnsignedShort((long)cpStrOffset);
         int asciiStrLen = asciiStr.length();
         if (cpStrLen != asciiStrLen) {
            return false;
         } else {
            int cpStrStart = cpStrOffset + 2;
            this.reader.bufferTo(cpStrStart + cpStrLen);
            byte[] buf = this.reader.buf();

            for (int i = 0; i < cpStrLen; i++) {
               if ((char)(buf[cpStrStart + i] & 255) != asciiStr.charAt(i)) {
                  return false;
               }
            }

            return true;
         }
      }
   }

   private int cpReadInt(int cpIdx) throws IOException {
      if (cpIdx >= 1 && cpIdx < this.cpCount) {
         return this.reader.readInt((long)this.entryOffset[cpIdx]);
      } else {
         throw new Classfile.ClassfileFormatException(
            "Constant pool index "
               + cpIdx
               + ", should be in range [1, "
               + (this.cpCount - 1)
               + "] -- cannot continue reading class. Please report this at https://github.com/classgraph/classgraph/issues"
         );
      }
   }

   private long cpReadLong(int cpIdx) throws IOException {
      if (cpIdx >= 1 && cpIdx < this.cpCount) {
         return this.reader.readLong((long)this.entryOffset[cpIdx]);
      } else {
         throw new Classfile.ClassfileFormatException(
            "Constant pool index "
               + cpIdx
               + ", should be in range [1, "
               + (this.cpCount - 1)
               + "] -- cannot continue reading class. Please report this at https://github.com/classgraph/classgraph/issues"
         );
      }
   }

   private Object getFieldConstantPoolValue(int tag, char fieldTypeDescriptorFirstChar, int cpIdx) throws Classfile.ClassfileFormatException, IOException {
      switch (tag) {
         case 1:
         case 7:
         case 8:
            return this.getConstantPoolString(cpIdx);
         case 2:
         default:
            throw new Classfile.ClassfileFormatException(
               "Unknown field constant pool tag "
                  + tag
                  + ", cannot continue reading class. Please report this at https://github.com/classgraph/classgraph/issues"
            );
         case 3:
            int intVal = this.cpReadInt(cpIdx);
            switch (fieldTypeDescriptorFirstChar) {
               case 'B':
                  return (byte)intVal;
               case 'C':
                  return (char)intVal;
               case 'I':
                  return intVal;
               case 'S':
                  return (short)intVal;
               case 'Z':
                  return intVal != 0;
               default:
                  throw new Classfile.ClassfileFormatException(
                     "Unknown Constant_INTEGER type "
                        + fieldTypeDescriptorFirstChar
                        + ", cannot continue reading class. Please report this at https://github.com/classgraph/classgraph/issues"
                  );
            }
         case 4:
            return Float.intBitsToFloat(this.cpReadInt(cpIdx));
         case 5:
            return this.cpReadLong(cpIdx);
         case 6:
            return Double.longBitsToDouble(this.cpReadLong(cpIdx));
      }
   }

   private AnnotationInfo readAnnotation() throws IOException {
      String annotationClassName = this.getConstantPoolClassDescriptor(this.reader.readUnsignedShort());
      int numElementValuePairs = this.reader.readUnsignedShort();
      AnnotationParameterValueList paramVals = null;
      if (numElementValuePairs > 0) {
         paramVals = new AnnotationParameterValueList(numElementValuePairs);

         for (int i = 0; i < numElementValuePairs; i++) {
            String paramName = this.getConstantPoolString(this.reader.readUnsignedShort());
            Object paramValue = this.readAnnotationElementValue();
            paramVals.add(new AnnotationParameterValue(paramName, paramValue));
         }
      }

      return new AnnotationInfo(annotationClassName, paramVals);
   }

   private Object readAnnotationElementValue() throws IOException {
      int tag = (char)this.reader.readUnsignedByte();
      switch (tag) {
         case 64:
            return this.readAnnotation();
         case 65:
         case 69:
         case 71:
         case 72:
         case 75:
         case 76:
         case 77:
         case 78:
         case 79:
         case 80:
         case 81:
         case 82:
         case 84:
         case 85:
         case 86:
         case 87:
         case 88:
         case 89:
         case 92:
         case 93:
         case 94:
         case 95:
         case 96:
         case 97:
         case 98:
         case 100:
         case 102:
         case 103:
         case 104:
         case 105:
         case 106:
         case 107:
         case 108:
         case 109:
         case 110:
         case 111:
         case 112:
         case 113:
         case 114:
         default:
            throw new Classfile.ClassfileFormatException(
               "Class "
                  + this.className
                  + " has unknown annotation element type tag '"
                  + (char)tag
                  + "': element size unknown, cannot continue reading class. Please report this at https://github.com/classgraph/classgraph/issues"
            );
         case 66:
            return (byte)this.cpReadInt(this.reader.readUnsignedShort());
         case 67:
            return (char)this.cpReadInt(this.reader.readUnsignedShort());
         case 68:
            return Double.longBitsToDouble(this.cpReadLong(this.reader.readUnsignedShort()));
         case 70:
            return Float.intBitsToFloat(this.cpReadInt(this.reader.readUnsignedShort()));
         case 73:
            return this.cpReadInt(this.reader.readUnsignedShort());
         case 74:
            return this.cpReadLong(this.reader.readUnsignedShort());
         case 83:
            return (short)this.cpReadInt(this.reader.readUnsignedShort());
         case 90:
            return this.cpReadInt(this.reader.readUnsignedShort()) != 0;
         case 91:
            int count = this.reader.readUnsignedShort();
            Object[] arr = new Object[count];

            for (int i = 0; i < count; i++) {
               arr[i] = this.readAnnotationElementValue();
            }

            return arr;
         case 99:
            String classRefTypeDescriptor = this.getConstantPoolString(this.reader.readUnsignedShort());
            return new AnnotationClassRef(classRefTypeDescriptor);
         case 101:
            String annotationClassName = this.getConstantPoolClassDescriptor(this.reader.readUnsignedShort());
            String annotationConstName = this.getConstantPoolString(this.reader.readUnsignedShort());
            return new AnnotationEnumValue(annotationClassName, annotationConstName);
         case 115:
            return this.getConstantPoolString(this.reader.readUnsignedShort());
      }
   }

   private List<Classfile.TypePathNode> readTypePath() throws IOException {
      int typePathLength = this.reader.readUnsignedByte();
      if (typePathLength == 0) {
         return Collections.emptyList();
      } else {
         List<Classfile.TypePathNode> list = new ArrayList<>(typePathLength);

         for (int i = 0; i < typePathLength; i++) {
            int typePathKind = this.reader.readUnsignedByte();
            int typeArgumentIdx = this.reader.readUnsignedByte();
            list.add(new Classfile.TypePathNode(typePathKind, typeArgumentIdx));
         }

         return list;
      }
   }

   private void readConstantPoolEntries(LogNode log) throws IOException {
      List<Integer> classNameCpIdxs = null;
      List<Integer> typeSignatureIdxs = null;
      if (this.scanSpec.enableInterClassDependencies) {
         classNameCpIdxs = new ArrayList<>();
         typeSignatureIdxs = new ArrayList<>();
      }

      this.cpCount = this.reader.readUnsignedShort();
      this.entryOffset = new int[this.cpCount];
      this.entryTag = new int[this.cpCount];
      this.indirectStringRefs = new int[this.cpCount];
      Arrays.fill(this.indirectStringRefs, 0, this.cpCount, -1);
      int i = 1;

      for (int skipSlot = 0; i < this.cpCount; i++) {
         if (skipSlot == 1) {
            skipSlot = 0;
         } else {
            this.entryTag[i] = this.reader.readUnsignedByte();
            this.entryOffset[i] = this.reader.currPos();
            switch (this.entryTag[i]) {
               case 0:
                  throw new Classfile.ClassfileFormatException(
                     "Invalid constant pool tag 0 in classfile "
                        + this.relativePath
                        + " (possible buffer underflow issue). Please report this at https://github.com/classgraph/classgraph/issues"
                  );
               case 1:
                  int strLen = this.reader.readUnsignedShort();
                  this.reader.skip(strLen);
                  break;
               case 2:
               case 13:
               case 14:
               default:
                  throw new Classfile.ClassfileFormatException(
                     "Unknown constant pool tag "
                        + this.entryTag[i]
                        + " (element size unknown, cannot continue reading class). Please report this at https://github.com/classgraph/classgraph/issues"
                  );
               case 3:
               case 4:
                  this.reader.skip(4);
                  break;
               case 5:
               case 6:
                  this.reader.skip(8);
                  skipSlot = 1;
                  break;
               case 7:
                  this.indirectStringRefs[i] = this.reader.readUnsignedShort();
                  if (classNameCpIdxs != null) {
                     classNameCpIdxs.add(this.indirectStringRefs[i]);
                  }
                  break;
               case 8:
                  this.indirectStringRefs[i] = this.reader.readUnsignedShort();
                  break;
               case 9:
                  this.reader.skip(4);
                  break;
               case 10:
                  this.reader.skip(4);
                  break;
               case 11:
                  this.reader.skip(4);
                  break;
               case 12:
                  int nameRef = this.reader.readUnsignedShort();
                  int typeRef = this.reader.readUnsignedShort();
                  if (typeSignatureIdxs != null) {
                     typeSignatureIdxs.add(typeRef);
                  }

                  this.indirectStringRefs[i] = nameRef << 16 | typeRef;
                  break;
               case 15:
                  this.reader.skip(3);
                  break;
               case 16:
                  this.reader.skip(2);
                  break;
               case 17:
                  this.reader.skip(4);
                  break;
               case 18:
                  this.reader.skip(4);
                  break;
               case 19:
                  this.indirectStringRefs[i] = this.reader.readUnsignedShort();
                  break;
               case 20:
                  this.reader.skip(2);
            }
         }
      }

      if (classNameCpIdxs != null) {
         this.refdClassNames = new HashSet<>();

         for (int cpIdx : classNameCpIdxs) {
            String refdClassName = this.getConstantPoolString(cpIdx, true, false);
            if (refdClassName != null) {
               if (refdClassName.startsWith("[")) {
                  try {
                     TypeSignature typeSig = TypeSignature.parse(refdClassName.replace('.', '/'), null);
                     typeSig.findReferencedClassNames(this.refdClassNames);
                  } catch (ParseException var9) {
                     throw new Classfile.ClassfileFormatException("Could not parse class name: " + refdClassName, var9);
                  }
               } else {
                  this.refdClassNames.add(refdClassName);
               }
            }
         }
      }

      if (typeSignatureIdxs != null) {
         for (int cpIdxx : typeSignatureIdxs) {
            String typeSigStr = this.getConstantPoolString(cpIdxx);
            if (typeSigStr != null) {
               try {
                  if (typeSigStr.startsWith("L") && typeSigStr.endsWith(";")) {
                     TypeSignature typeSig = TypeSignature.parse(typeSigStr, null);
                     typeSig.findReferencedClassNames(this.refdClassNames);
                  } else if (typeSigStr.indexOf(40) >= 0 || "<init>".equals(typeSigStr)) {
                     MethodTypeSignature typeSig = MethodTypeSignature.parse(typeSigStr, null);
                     typeSig.findReferencedClassNames(this.refdClassNames);
                  } else if (log != null) {
                     log.log("Could not extract referenced class names from constant pool string: " + typeSigStr);
                  }
               } catch (ParseException var10) {
                  if (log != null) {
                     log.log("Could not extract referenced class names from constant pool string: " + typeSigStr + " : " + var10);
                  }
               }
            }
         }
      }
   }

   private void readBasicClassInfo() throws IOException, Classfile.ClassfileFormatException, Classfile.SkipClassException {
      this.classModifiers = this.reader.readUnsignedShort();
      this.isInterface = (this.classModifiers & 512) != 0;
      this.isAnnotation = (this.classModifiers & 8192) != 0;
      String classNamePath = this.getConstantPoolString(this.reader.readUnsignedShort());
      if (classNamePath == null) {
         throw new Classfile.ClassfileFormatException("Class name is null");
      } else {
         this.className = classNamePath.replace('/', '.');
         if ("java.lang.Object".equals(this.className)) {
            throw new Classfile.SkipClassException("No need to scan java.lang.Object");
         } else {
            boolean isModule = (this.classModifiers & 32768) != 0;
            boolean isPackage = this.relativePath.regionMatches(this.relativePath.lastIndexOf(47) + 1, "package-info.class", 0, 18);
            if (!this.scanSpec.ignoreClassVisibility && !Modifier.isPublic(this.classModifiers) && !isModule && !isPackage) {
               throw new Classfile.SkipClassException("Class is not public, and ignoreClassVisibility() was not called");
            } else if (!this.relativePath.endsWith(".class")) {
               throw new Classfile.SkipClassException("Classfile filename " + this.relativePath + " does not end in \".class\"");
            } else {
               int len = classNamePath.length();
               if (this.relativePath.length() == len + 6 && classNamePath.regionMatches(0, this.relativePath, 0, len)) {
                  int superclassNameCpIdx = this.reader.readUnsignedShort();
                  if (superclassNameCpIdx > 0) {
                     this.superclassName = this.getConstantPoolClassName(superclassNameCpIdx);
                  }
               } else {
                  throw new Classfile.SkipClassException("Relative path " + this.relativePath + " does not match class name " + this.className);
               }
            }
         }
      }
   }

   private void readInterfaces() throws IOException {
      int interfaceCount = this.reader.readUnsignedShort();

      for (int i = 0; i < interfaceCount; i++) {
         String interfaceName = this.getConstantPoolClassName(this.reader.readUnsignedShort());
         if (this.implementedInterfaces == null) {
            this.implementedInterfaces = new ArrayList<>();
         }

         this.implementedInterfaces.add(interfaceName);
      }
   }

   private void readFields() throws IOException, Classfile.ClassfileFormatException {
      int fieldCount = this.reader.readUnsignedShort();

      for (int i = 0; i < fieldCount; i++) {
         int fieldModifierFlags = this.reader.readUnsignedShort();
         boolean isPublicField = (fieldModifierFlags & 1) == 1;
         boolean fieldIsVisible = isPublicField || this.scanSpec.ignoreFieldVisibility;
         boolean getStaticFinalFieldConstValue = this.scanSpec.enableStaticFinalFieldConstantInitializerValues && fieldIsVisible;
         List<Classfile.TypeAnnotationDecorator> fieldTypeAnnotationDecorators = null;
         if (!fieldIsVisible || !this.scanSpec.enableFieldInfo && !getStaticFinalFieldConstValue) {
            this.reader.readUnsignedShort();
            this.reader.readUnsignedShort();
            int attributesCount = this.reader.readUnsignedShort();

            for (int j = 0; j < attributesCount; j++) {
               this.reader.readUnsignedShort();
               int attributeLength = this.reader.readInt();
               this.reader.skip(attributeLength);
            }
         } else {
            int fieldNameCpIdx = this.reader.readUnsignedShort();
            String fieldName = this.getConstantPoolString(fieldNameCpIdx);
            int fieldTypeDescriptorCpIdx = this.reader.readUnsignedShort();
            char fieldTypeDescriptorFirstChar = (char)this.getConstantPoolStringFirstByte(fieldTypeDescriptorCpIdx);
            String fieldTypeSignatureStr = null;
            String fieldTypeDescriptor = this.getConstantPoolString(fieldTypeDescriptorCpIdx);
            Object fieldConstValue = null;
            AnnotationInfoList fieldAnnotationInfo = null;
            int attributesCount = this.reader.readUnsignedShort();

            for (int j = 0; j < attributesCount; j++) {
               int attributeNameCpIdx = this.reader.readUnsignedShort();
               int attributeLength = this.reader.readInt();
               if (getStaticFinalFieldConstValue && this.constantPoolStringEquals(attributeNameCpIdx, "ConstantValue")) {
                  int cpIdx = this.reader.readUnsignedShort();
                  if (cpIdx < 1 || cpIdx >= this.cpCount) {
                     throw new Classfile.ClassfileFormatException(
                        "Constant pool index "
                           + cpIdx
                           + ", should be in range [1, "
                           + (this.cpCount - 1)
                           + "] -- cannot continue reading class. Please report this at https://github.com/classgraph/classgraph/issues"
                     );
                  }

                  fieldConstValue = this.getFieldConstantPoolValue(this.entryTag[cpIdx], fieldTypeDescriptorFirstChar, cpIdx);
               } else if (fieldIsVisible && this.constantPoolStringEquals(attributeNameCpIdx, "Signature")) {
                  fieldTypeSignatureStr = this.getConstantPoolString(this.reader.readUnsignedShort());
               } else if (!this.scanSpec.enableAnnotationInfo
                  || !this.constantPoolStringEquals(attributeNameCpIdx, "RuntimeVisibleAnnotations")
                     && (this.scanSpec.disableRuntimeInvisibleAnnotations || !this.constantPoolStringEquals(attributeNameCpIdx, "RuntimeInvisibleAnnotations"))
                  )
                {
                  if (this.scanSpec.enableAnnotationInfo
                     && (
                        this.constantPoolStringEquals(attributeNameCpIdx, "RuntimeVisibleTypeAnnotations")
                           || !this.scanSpec.disableRuntimeInvisibleAnnotations
                              && this.constantPoolStringEquals(attributeNameCpIdx, "RuntimeInvisibleTypeAnnotations")
                     )) {
                     int annotationCount = this.reader.readUnsignedShort();
                     if (annotationCount > 0) {
                        fieldTypeAnnotationDecorators = new ArrayList<>();

                        for (int m = 0; m < annotationCount; m++) {
                           int targetType = this.reader.readUnsignedByte();
                           if (targetType != 19) {
                              throw new Classfile.ClassfileFormatException(
                                 "Class "
                                    + this.className
                                    + " has unknown field type annotation target 0x"
                                    + Integer.toHexString(targetType)
                                    + ": element size unknown, cannot continue reading class. Please report this at https://github.com/classgraph/classgraph/issues"
                              );
                           }

                           final List<Classfile.TypePathNode> typePath = this.readTypePath();
                           final AnnotationInfo annotationInfo = this.readAnnotation();
                           fieldTypeAnnotationDecorators.add(new Classfile.TypeAnnotationDecorator() {
                              @Override
                              public void decorate(TypeSignature typeSignature) {
                                 typeSignature.addTypeAnnotation(typePath, annotationInfo);
                              }
                           });
                        }
                     }
                  } else {
                     this.reader.skip(attributeLength);
                  }
               } else {
                  int fieldAnnotationCount = this.reader.readUnsignedShort();
                  if (fieldAnnotationCount > 0) {
                     if (fieldAnnotationInfo == null) {
                        fieldAnnotationInfo = new AnnotationInfoList(1);
                     }

                     for (int k = 0; k < fieldAnnotationCount; k++) {
                        AnnotationInfo fieldAnnotation = this.readAnnotation();
                        fieldAnnotationInfo.add(fieldAnnotation);
                     }
                  }
               }
            }

            if (this.scanSpec.enableFieldInfo && fieldIsVisible) {
               if (this.fieldInfoList == null) {
                  this.fieldInfoList = new FieldInfoList();
               }

               this.fieldInfoList
                  .add(
                     new FieldInfo(
                        this.className,
                        fieldName,
                        fieldModifierFlags,
                        fieldTypeDescriptor,
                        fieldTypeSignatureStr,
                        fieldConstValue,
                        fieldAnnotationInfo,
                        fieldTypeAnnotationDecorators
                     )
                  );
            }
         }
      }
   }

   private void readMethods() throws IOException, Classfile.ClassfileFormatException {
      int methodCount = this.reader.readUnsignedShort();

      for (int i = 0; i < methodCount; i++) {
         int methodModifierFlags = this.reader.readUnsignedShort();
         boolean isPublicMethod = (methodModifierFlags & 1) == 1;
         boolean methodIsVisible = isPublicMethod || this.scanSpec.ignoreMethodVisibility;
         List<Classfile.MethodTypeAnnotationDecorator> methodTypeAnnotationDecorators = null;
         String methodName = null;
         String methodTypeDescriptor = null;
         String methodTypeSignatureStr = null;
         boolean enableMethodInfo = this.scanSpec.enableMethodInfo || this.isAnnotation;
         if (!enableMethodInfo && !this.isAnnotation) {
            this.reader.skip(4);
         } else {
            int methodNameCpIdx = this.reader.readUnsignedShort();
            methodName = this.getConstantPoolString(methodNameCpIdx);
            int methodTypeDescriptorCpIdx = this.reader.readUnsignedShort();
            methodTypeDescriptor = this.getConstantPoolString(methodTypeDescriptorCpIdx);
         }

         int attributesCount = this.reader.readUnsignedShort();
         String[] methodParameterNames = null;
         String[] thrownExceptionNames = null;
         int[] methodParameterModifiers = null;
         AnnotationInfo[][] methodParameterAnnotations = (AnnotationInfo[][])null;
         AnnotationInfoList methodAnnotationInfo = null;
         boolean methodHasBody = false;
         int minLineNum = 0;
         int maxLineNum = 0;
         if (methodIsVisible && (enableMethodInfo || this.isAnnotation)) {
            for (int j = 0; j < attributesCount; j++) {
               int attributeNameCpIdx = this.reader.readUnsignedShort();
               int attributeLength = this.reader.readInt();
               if (!this.scanSpec.enableAnnotationInfo
                  || !this.constantPoolStringEquals(attributeNameCpIdx, "RuntimeVisibleAnnotations")
                     && (this.scanSpec.disableRuntimeInvisibleAnnotations || !this.constantPoolStringEquals(attributeNameCpIdx, "RuntimeInvisibleAnnotations"))
                  )
                {
                  if (!this.scanSpec.enableAnnotationInfo
                     || !this.constantPoolStringEquals(attributeNameCpIdx, "RuntimeVisibleParameterAnnotations")
                        && (
                           this.scanSpec.disableRuntimeInvisibleAnnotations
                              || !this.constantPoolStringEquals(attributeNameCpIdx, "RuntimeInvisibleParameterAnnotations")
                        )) {
                     if (!this.scanSpec.enableAnnotationInfo
                        || !this.constantPoolStringEquals(attributeNameCpIdx, "RuntimeVisibleTypeAnnotations")
                           && (
                              this.scanSpec.disableRuntimeInvisibleAnnotations
                                 || !this.constantPoolStringEquals(attributeNameCpIdx, "RuntimeInvisibleTypeAnnotations")
                           )) {
                        if (this.constantPoolStringEquals(attributeNameCpIdx, "MethodParameters")) {
                           int paramCount = this.reader.readUnsignedByte();
                           methodParameterNames = new String[paramCount];
                           methodParameterModifiers = new int[paramCount];

                           for (int k = 0; k < paramCount; k++) {
                              int cpIdx = this.reader.readUnsignedShort();
                              methodParameterNames[k] = cpIdx == 0 ? null : this.getConstantPoolString(cpIdx);
                              methodParameterModifiers[k] = this.reader.readUnsignedShort();
                           }
                        } else if (this.constantPoolStringEquals(attributeNameCpIdx, "Signature")) {
                           methodTypeSignatureStr = this.getConstantPoolString(this.reader.readUnsignedShort());
                        } else if (this.constantPoolStringEquals(attributeNameCpIdx, "AnnotationDefault")) {
                           if (this.annotationParamDefaultValues == null) {
                              this.annotationParamDefaultValues = new AnnotationParameterValueList();
                           }

                           this.annotationParamDefaultValues.add(new AnnotationParameterValue(methodName, this.readAnnotationElementValue()));
                        } else if (this.constantPoolStringEquals(attributeNameCpIdx, "Exceptions")) {
                           int exceptionCount = this.reader.readUnsignedShort();
                           thrownExceptionNames = new String[exceptionCount];

                           for (int k = 0; k < exceptionCount; k++) {
                              int cpIdx = this.reader.readUnsignedShort();
                              thrownExceptionNames[k] = this.getConstantPoolClassName(cpIdx);
                           }
                        } else if (!this.constantPoolStringEquals(attributeNameCpIdx, "Code")) {
                           this.reader.skip(attributeLength);
                        } else {
                           methodHasBody = true;
                           this.reader.skip(4);
                           int codeLength = this.reader.readInt();
                           this.reader.skip(codeLength);
                           int exceptionTableLength = this.reader.readUnsignedShort();
                           this.reader.skip(8 * exceptionTableLength);
                           int codeAttrCount = this.reader.readUnsignedShort();

                           for (int k = 0; k < codeAttrCount; k++) {
                              int codeAttrCpIdx = this.reader.readUnsignedShort();
                              int codeAttrLen = this.reader.readInt();
                              if (this.constantPoolStringEquals(codeAttrCpIdx, "LineNumberTable")) {
                                 int lineNumTableLen = this.reader.readUnsignedShort();

                                 for (int l = 0; l < lineNumTableLen; l++) {
                                    this.reader.skip(2);
                                    int lineNum = this.reader.readUnsignedShort();
                                    minLineNum = minLineNum == 0 ? lineNum : Math.min(minLineNum, lineNum);
                                    maxLineNum = maxLineNum == 0 ? lineNum : Math.max(maxLineNum, lineNum);
                                 }
                              } else {
                                 this.reader.skip(codeAttrLen);
                              }
                           }
                        }
                     } else {
                        int annotationCount = this.reader.readUnsignedShort();
                        if (annotationCount > 0) {
                           methodTypeAnnotationDecorators = new ArrayList<>(annotationCount);

                           for (int m = 0; m < annotationCount; m++) {
                              final int targetType = this.reader.readUnsignedByte();
                              final int formalParameterIndex;
                              final int throwsTypeIndex;
                              final int typeParameterIndex;
                              final int boundIndex;
                              if (targetType == 1) {
                                 typeParameterIndex = this.reader.readUnsignedByte();
                                 boundIndex = -1;
                                 formalParameterIndex = -1;
                                 throwsTypeIndex = -1;
                              } else if (targetType == 18) {
                                 typeParameterIndex = this.reader.readUnsignedByte();
                                 boundIndex = this.reader.readUnsignedByte();
                                 formalParameterIndex = -1;
                                 throwsTypeIndex = -1;
                              } else if (targetType == 20) {
                                 typeParameterIndex = -1;
                                 boundIndex = -1;
                                 formalParameterIndex = -1;
                                 throwsTypeIndex = -1;
                              } else if (targetType == 21) {
                                 typeParameterIndex = -1;
                                 boundIndex = -1;
                                 formalParameterIndex = -1;
                                 throwsTypeIndex = -1;
                              } else if (targetType == 22) {
                                 typeParameterIndex = -1;
                                 boundIndex = -1;
                                 formalParameterIndex = this.reader.readUnsignedByte();
                                 throwsTypeIndex = -1;
                              } else {
                                 if (targetType != 23) {
                                    throw new Classfile.ClassfileFormatException(
                                       "Class "
                                          + this.className
                                          + " has unknown method type annotation target 0x"
                                          + Integer.toHexString(targetType)
                                          + ": element size unknown, cannot continue reading class. Please report this at https://github.com/classgraph/classgraph/issues"
                                    );
                                 }

                                 typeParameterIndex = -1;
                                 boundIndex = -1;
                                 formalParameterIndex = -1;
                                 throwsTypeIndex = this.reader.readUnsignedShort();
                              }

                              final List<Classfile.TypePathNode> typePath = this.readTypePath();
                              final AnnotationInfo annotationInfo = this.readAnnotation();
                              methodTypeAnnotationDecorators.add(new Classfile.MethodTypeAnnotationDecorator() {
                                 @Override
                                 public void decorate(MethodTypeSignature methodTypeSignature) {
                                    if (targetType == 1) {
                                       List<TypeParameter> typeParameters = methodTypeSignature.getTypeParameters();
                                       if (typeParameters != null && typeParameterIndex < typeParameters.size()) {
                                          typeParameters.get(typeParameterIndex).addTypeAnnotation(typePath, annotationInfo);
                                       }
                                    } else if (targetType == 18) {
                                       List<TypeParameter> typeParameters = methodTypeSignature.getTypeParameters();
                                       if (typeParameters != null && typeParameterIndex < typeParameters.size()) {
                                          TypeParameter typeParameter = typeParameters.get(typeParameterIndex);
                                          if (boundIndex == 0) {
                                             ReferenceTypeSignature classBound = typeParameter.getClassBound();
                                             if (classBound != null) {
                                                classBound.addTypeAnnotation(typePath, annotationInfo);
                                             }
                                          } else {
                                             List<ReferenceTypeSignature> interfaceBounds = typeParameter.getInterfaceBounds();
                                             if (interfaceBounds != null && boundIndex - 1 < interfaceBounds.size()) {
                                                interfaceBounds.get(boundIndex - 1).addTypeAnnotation(typePath, annotationInfo);
                                             }
                                          }
                                       }
                                    } else if (targetType == 20) {
                                       methodTypeSignature.getResultType().addTypeAnnotation(typePath, annotationInfo);
                                    } else if (targetType == 21) {
                                       methodTypeSignature.addRecieverTypeAnnotation(annotationInfo);
                                    } else if (targetType == 22) {
                                       List<TypeSignature> parameterTypeSignatures = methodTypeSignature.getParameterTypeSignatures();
                                       if (formalParameterIndex < parameterTypeSignatures.size()) {
                                          parameterTypeSignatures.get(formalParameterIndex).addTypeAnnotation(typePath, annotationInfo);
                                       }
                                    } else if (targetType == 23) {
                                       List<ClassRefOrTypeVariableSignature> throwsSignatures = methodTypeSignature.getThrowsSignatures();
                                       if (throwsSignatures != null && throwsTypeIndex < throwsSignatures.size()) {
                                          throwsSignatures.get(throwsTypeIndex).addTypeAnnotation(typePath, annotationInfo);
                                       }
                                    }
                                 }
                              });
                           }
                        }
                     }
                  } else {
                     int numParams = this.reader.readUnsignedByte();
                     if (methodParameterAnnotations == null) {
                        methodParameterAnnotations = new AnnotationInfo[numParams][];
                     } else if (methodParameterAnnotations.length != numParams) {
                        throw new Classfile.ClassfileFormatException(
                           "Mismatch in number of parameters between RuntimeVisibleParameterAnnotations and RuntimeInvisibleParameterAnnotations"
                        );
                     }

                     for (int paramIdx = 0; paramIdx < numParams; paramIdx++) {
                        int numAnnotations = this.reader.readUnsignedShort();
                        if (numAnnotations > 0) {
                           int annStartIdx = 0;
                           if (methodParameterAnnotations[paramIdx] != null) {
                              annStartIdx = methodParameterAnnotations[paramIdx].length;
                              methodParameterAnnotations[paramIdx] = Arrays.copyOf(methodParameterAnnotations[paramIdx], annStartIdx + numAnnotations);
                           } else {
                              methodParameterAnnotations[paramIdx] = new AnnotationInfo[numAnnotations];
                           }

                           for (int annIdx = 0; annIdx < numAnnotations; annIdx++) {
                              methodParameterAnnotations[paramIdx][annStartIdx + annIdx] = this.readAnnotation();
                           }
                        } else if (methodParameterAnnotations[paramIdx] == null) {
                           methodParameterAnnotations[paramIdx] = NO_ANNOTATIONS;
                        }
                     }
                  }
               } else {
                  int methodAnnotationCount = this.reader.readUnsignedShort();
                  if (methodAnnotationCount > 0) {
                     if (methodAnnotationInfo == null) {
                        methodAnnotationInfo = new AnnotationInfoList(1);
                     }

                     for (int kx = 0; kx < methodAnnotationCount; kx++) {
                        AnnotationInfo annotationInfo = this.readAnnotation();
                        methodAnnotationInfo.add(annotationInfo);
                     }
                  }
               }
            }

            if (enableMethodInfo) {
               if (this.methodInfoList == null) {
                  this.methodInfoList = new MethodInfoList();
               }

               this.methodInfoList
                  .add(
                     new MethodInfo(
                        this.className,
                        methodName,
                        methodAnnotationInfo,
                        methodModifierFlags,
                        methodTypeDescriptor,
                        methodTypeSignatureStr,
                        methodParameterNames,
                        methodParameterModifiers,
                        methodParameterAnnotations,
                        methodHasBody,
                        minLineNum,
                        maxLineNum,
                        methodTypeAnnotationDecorators,
                        thrownExceptionNames
                     )
                  );
            }
         } else {
            for (int jx = 0; jx < attributesCount; jx++) {
               this.reader.skip(2);
               int attributeLength = this.reader.readInt();
               this.reader.skip(attributeLength);
            }
         }
      }
   }

   private void readClassAttributes() throws IOException, Classfile.ClassfileFormatException {
      int attributesCount = this.reader.readUnsignedShort();

      for (int i = 0; i < attributesCount; i++) {
         int attributeNameCpIdx = this.reader.readUnsignedShort();
         int attributeLength = this.reader.readInt();
         if (this.scanSpec.enableAnnotationInfo
            && (
               this.constantPoolStringEquals(attributeNameCpIdx, "RuntimeVisibleAnnotations")
                  || !this.scanSpec.disableRuntimeInvisibleAnnotations && this.constantPoolStringEquals(attributeNameCpIdx, "RuntimeInvisibleAnnotations")
            )) {
            int annotationCount = this.reader.readUnsignedShort();
            if (annotationCount > 0) {
               if (this.classAnnotations == null) {
                  this.classAnnotations = new AnnotationInfoList();
               }

               for (int m = 0; m < annotationCount; m++) {
                  this.classAnnotations.add(this.readAnnotation());
               }
            }
         } else if (this.scanSpec.enableAnnotationInfo
            && (
               this.constantPoolStringEquals(attributeNameCpIdx, "RuntimeVisibleTypeAnnotations")
                  || !this.scanSpec.disableRuntimeInvisibleAnnotations && this.constantPoolStringEquals(attributeNameCpIdx, "RuntimeInvisibleTypeAnnotations")
            )) {
            int annotationCount = this.reader.readUnsignedShort();
            if (annotationCount > 0) {
               this.classTypeAnnotationDecorators = new ArrayList<>(annotationCount);

               for (int m = 0; m < annotationCount; m++) {
                  final int targetType = this.reader.readUnsignedByte();
                  final int typeParameterIndex;
                  final int supertypeIndex;
                  final int boundIndex;
                  if (targetType == 0) {
                     typeParameterIndex = this.reader.readUnsignedByte();
                     supertypeIndex = -1;
                     boundIndex = -1;
                  } else if (targetType == 16) {
                     supertypeIndex = this.reader.readUnsignedShort();
                     typeParameterIndex = -1;
                     boundIndex = -1;
                  } else {
                     if (targetType != 17) {
                        throw new Classfile.ClassfileFormatException(
                           "Class "
                              + this.className
                              + " has unknown class type annotation target 0x"
                              + Integer.toHexString(targetType)
                              + ": element size unknown, cannot continue reading class. Please report this at https://github.com/classgraph/classgraph/issues"
                        );
                     }

                     typeParameterIndex = this.reader.readUnsignedByte();
                     boundIndex = this.reader.readUnsignedByte();
                     supertypeIndex = -1;
                  }

                  final List<Classfile.TypePathNode> typePath = this.readTypePath();
                  final AnnotationInfo annotationInfo = this.readAnnotation();
                  this.classTypeAnnotationDecorators.add(new Classfile.ClassTypeAnnotationDecorator() {
                     @Override
                     public void decorate(ClassTypeSignature classTypeSignature) {
                        if (targetType == 0) {
                           List<TypeParameter> typeParameters = classTypeSignature.getTypeParameters();
                           if (typeParameters != null && typeParameterIndex < typeParameters.size()) {
                              typeParameters.get(typeParameterIndex).addTypeAnnotation(typePath, annotationInfo);
                           }
                        } else if (targetType == 16) {
                           if (supertypeIndex == 65535) {
                              classTypeSignature.getSuperclassSignature().addTypeAnnotation(typePath, annotationInfo);
                           } else {
                              classTypeSignature.getSuperinterfaceSignatures().get(supertypeIndex).addTypeAnnotation(typePath, annotationInfo);
                           }
                        } else if (targetType == 17) {
                           List<TypeParameter> typeParameters = classTypeSignature.getTypeParameters();
                           if (typeParameters != null && typeParameterIndex < typeParameters.size()) {
                              TypeParameter typeParameter = typeParameters.get(typeParameterIndex);
                              if (boundIndex == 0) {
                                 ReferenceTypeSignature classBound = typeParameter.getClassBound();
                                 if (classBound != null) {
                                    classBound.addTypeAnnotation(typePath, annotationInfo);
                                 }
                              } else {
                                 List<ReferenceTypeSignature> interfaceBounds = typeParameter.getInterfaceBounds();
                                 if (interfaceBounds != null && boundIndex - 1 < interfaceBounds.size()) {
                                    typeParameter.getInterfaceBounds().get(boundIndex - 1).addTypeAnnotation(typePath, annotationInfo);
                                 }
                              }
                           }
                        }
                     }
                  });
               }
            }
         } else if (this.constantPoolStringEquals(attributeNameCpIdx, "Record")) {
            this.isRecord = true;
            this.reader.skip(attributeLength);
         } else if (this.constantPoolStringEquals(attributeNameCpIdx, "InnerClasses")) {
            int numInnerClasses = this.reader.readUnsignedShort();

            for (int j = 0; j < numInnerClasses; j++) {
               int innerClassInfoCpIdx = this.reader.readUnsignedShort();
               int outerClassInfoCpIdx = this.reader.readUnsignedShort();
               this.reader.skip(2);
               int innerClassAccessFlags = this.reader.readUnsignedShort();
               if (innerClassInfoCpIdx != 0 && outerClassInfoCpIdx != 0) {
                  String innerClassName = this.getConstantPoolClassName(innerClassInfoCpIdx);
                  String outerClassName = this.getConstantPoolClassName(outerClassInfoCpIdx);
                  if (innerClassName == null || outerClassName == null) {
                     throw new Classfile.ClassfileFormatException("Inner and/or outer class name is null");
                  }

                  if (innerClassName.equals(outerClassName)) {
                     throw new Classfile.ClassfileFormatException("Inner and outer class name cannot be the same");
                  }

                  if (!"java.lang.invoke.MethodHandles$Lookup".equals(innerClassName) || !"java.lang.invoke.MethodHandles".equals(outerClassName)) {
                     if (this.classContainmentEntries == null) {
                        this.classContainmentEntries = new ArrayList<>();
                     }

                     this.classContainmentEntries.add(new Classfile.ClassContainment(innerClassName, innerClassAccessFlags, outerClassName));
                  }
               }
            }
         } else if (this.constantPoolStringEquals(attributeNameCpIdx, "Signature")) {
            this.typeSignatureStr = this.getConstantPoolString(this.reader.readUnsignedShort());
         } else if (this.constantPoolStringEquals(attributeNameCpIdx, "SourceFile")) {
            this.sourceFile = this.getConstantPoolString(this.reader.readUnsignedShort());
         } else if (this.constantPoolStringEquals(attributeNameCpIdx, "EnclosingMethod")) {
            String innermostEnclosingClassName = this.getConstantPoolClassName(this.reader.readUnsignedShort());
            int enclosingMethodCpIdx = this.reader.readUnsignedShort();
            String definingMethodName;
            if (enclosingMethodCpIdx == 0) {
               definingMethodName = "<clinit>";
            } else {
               definingMethodName = this.getConstantPoolString(enclosingMethodCpIdx, 0);
            }

            if (this.classContainmentEntries == null) {
               this.classContainmentEntries = new ArrayList<>();
            }

            this.classContainmentEntries.add(new Classfile.ClassContainment(this.className, this.classModifiers, innermostEnclosingClassName));
            this.fullyQualifiedDefiningMethodName = innermostEnclosingClassName + "." + definingMethodName;
         } else if (this.constantPoolStringEquals(attributeNameCpIdx, "Module")) {
            int moduleNameCpIdx = this.reader.readUnsignedShort();
            this.classpathElement.moduleNameFromModuleDescriptor = this.getConstantPoolString(moduleNameCpIdx);
            this.reader.skip(attributeLength - 2);
         } else {
            this.reader.skip(attributeLength);
         }
      }
   }

   Classfile(
      ClasspathElement classpathElement,
      List<ClasspathElement> classpathOrder,
      Set<String> acceptedClassNamesFound,
      Set<String> classNamesScheduledForExtendedScanning,
      String relativePath,
      Resource classfileResource,
      boolean isExternalClass,
      ConcurrentHashMap<String, String> stringInternMap,
      WorkQueue<Scanner.ClassfileScanWorkUnit> workQueue,
      ScanSpec scanSpec,
      LogNode log
   ) throws IOException, Classfile.ClassfileFormatException, Classfile.SkipClassException {
      this.classpathElement = classpathElement;
      this.classpathOrder = classpathOrder;
      this.relativePath = relativePath;
      this.acceptedClassNamesFound = acceptedClassNamesFound;
      this.classNamesScheduledForExtendedScanning = classNamesScheduledForExtendedScanning;
      this.classfileResource = classfileResource;
      this.isExternalClass = isExternalClass;
      this.stringInternMap = stringInternMap;
      this.scanSpec = scanSpec;

      try (ClassfileReader classfileReader = classfileResource.openClassfile()) {
         this.reader = classfileReader;
         if (this.reader.readInt() != -889275714) {
            throw new Classfile.ClassfileFormatException("Classfile does not have correct magic number");
         }

         this.minorVersion = this.reader.readUnsignedShort();
         this.majorVersion = this.reader.readUnsignedShort();
         this.readConstantPoolEntries(log);
         this.readBasicClassInfo();
         this.readInterfaces();
         this.readFields();
         this.readMethods();
         this.readClassAttributes();
         this.reader = null;
      }

      LogNode subLog = log == null
         ? null
         : log.log("Found " + (this.isAnnotation ? "annotation class" : (this.isInterface ? "interface class" : "class")) + " " + this.className);
      if (subLog != null) {
         if (this.superclassName != null) {
            subLog.log("Super" + (this.isInterface && !this.isAnnotation ? "interface" : "class") + ": " + this.superclassName);
         }

         if (this.implementedInterfaces != null) {
            subLog.log("Interfaces: " + StringUtils.join(", ", this.implementedInterfaces));
         }

         if (this.classAnnotations != null) {
            subLog.log("Class annotations: " + StringUtils.join(", ", this.classAnnotations));
         }

         if (this.annotationParamDefaultValues != null) {
            for (AnnotationParameterValue apv : this.annotationParamDefaultValues) {
               subLog.log("Annotation default param value: " + apv);
            }
         }

         if (this.fieldInfoList != null) {
            for (FieldInfo fieldInfo : this.fieldInfoList) {
               String modifierStr = fieldInfo.getModifiersStr();
               subLog.log("Field: " + modifierStr + (modifierStr.isEmpty() ? "" : " ") + fieldInfo.getName());
            }
         }

         if (this.methodInfoList != null) {
            for (MethodInfo methodInfo : this.methodInfoList) {
               String modifierStr = methodInfo.getModifiersStr();
               subLog.log("Method: " + modifierStr + (modifierStr.isEmpty() ? "" : " ") + methodInfo.getName());
            }
         }

         if (this.typeSignatureStr != null) {
            subLog.log("Class type signature: " + this.typeSignatureStr);
         }

         if (this.refdClassNames != null) {
            List<String> refdClassNamesSorted = new ArrayList<>(this.refdClassNames);
            CollectionUtils.sortIfNotEmpty(refdClassNamesSorted);
            subLog.log("Additional referenced class names: " + StringUtils.join(", ", refdClassNamesSorted));
         }
      }

      if (scanSpec.extendScanningUpwardsToExternalClasses) {
         this.extendScanningUpwards(subLog);
         if (this.additionalWorkUnits != null) {
            workQueue.addWorkUnits(this.additionalWorkUnits);
         }
      }
   }

   static class ClassContainment {
      public final String innerClassName;
      public final int innerClassModifierBits;
      public final String outerClassName;

      public ClassContainment(String innerClassName, int innerClassModifierBits, String outerClassName) {
         this.innerClassName = innerClassName;
         this.innerClassModifierBits = innerClassModifierBits;
         this.outerClassName = outerClassName;
      }
   }

   interface ClassTypeAnnotationDecorator {
      void decorate(ClassTypeSignature var1);
   }

   static class ClassfileFormatException extends IOException {
      static final long serialVersionUID = 1L;

      public ClassfileFormatException(String message) {
         super(message);
      }

      public ClassfileFormatException(String message, Throwable cause) {
         super(message, cause);
      }

      @Override
      public synchronized Throwable fillInStackTrace() {
         return this;
      }
   }

   interface MethodTypeAnnotationDecorator {
      void decorate(MethodTypeSignature var1);
   }

   static class SkipClassException extends IOException {
      static final long serialVersionUID = 1L;

      public SkipClassException(String message) {
         super(message);
      }

      @Override
      public synchronized Throwable fillInStackTrace() {
         return this;
      }
   }

   interface TypeAnnotationDecorator {
      void decorate(TypeSignature var1);
   }

   static class TypePathNode {
      short typePathKind;
      short typeArgumentIdx;

      public TypePathNode(int typePathKind, int typeArgumentIdx) {
         this.typePathKind = (short)typePathKind;
         this.typeArgumentIdx = (short)typeArgumentIdx;
      }

      @Override
      public String toString() {
         return "(" + this.typePathKind + "," + this.typeArgumentIdx + ")";
      }
   }
}
