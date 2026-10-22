package io.github.classgraph;

import java.util.BitSet;
import java.util.HashSet;
import java.util.Set;
import nonapi.io.github.classgraph.scanspec.ScanSpec;
import nonapi.io.github.classgraph.utils.CollectionUtils;

final class GraphvizDotfileGenerator {
   private static final String STANDARD_CLASS_COLOR = "fff2b6";
   private static final String INTERFACE_COLOR = "b6e7ff";
   private static final String ANNOTATION_COLOR = "f3c9ff";
   private static final int PARAM_WRAP_WIDTH = 40;
   private static final BitSet IS_UNICODE_WHITESPACE = new BitSet(65536);

   private GraphvizDotfileGenerator() {
   }

   private static boolean isUnicodeWhitespace(char c) {
      return IS_UNICODE_WHITESPACE.get(c);
   }

   private static void htmlEncode(CharSequence unsafeStr, boolean turnNewlineIntoBreak, StringBuilder buf) {
      int i = 0;

      for (int n = unsafeStr.length(); i < n; i++) {
         char c = unsafeStr.charAt(i);
         switch (c) {
            case '\n':
               if (turnNewlineIntoBreak) {
                  buf.append("<br>");
               } else {
                  buf.append(' ');
               }
               break;
            case '"':
               buf.append("&quot;");
               break;
            case '&':
               buf.append("&amp;");
               break;
            case '\'':
               buf.append("&#x27;");
               break;
            case '/':
               buf.append("&#x2F;");
               break;
            case '<':
               buf.append("&lt;");
               break;
            case '>':
               buf.append("&gt;");
               break;
            case '\\':
               buf.append("&lsol;");
               break;
            case ' ':
               buf.append("&nbsp;");
               break;
            case '£':
               buf.append("&pound;");
               break;
            case '©':
               buf.append("&copy;");
               break;
            case '«':
               buf.append("&laquo;");
               break;
            case '®':
               buf.append("&reg;");
               break;
            case '»':
               buf.append("&raquo;");
               break;
            case '–':
               buf.append("&ndash;");
               break;
            case '—':
               buf.append("&mdash;");
               break;
            case '‘':
               buf.append("&lsquo;");
               break;
            case '’':
               buf.append("&rsquo;");
               break;
            case '“':
               buf.append("&ldquo;");
               break;
            case '”':
               buf.append("&rdquo;");
               break;
            default:
               if (c > ' ' && !isUnicodeWhitespace(c)) {
                  buf.append(c);
               } else {
                  buf.append(' ');
               }
         }
      }
   }

   private static void htmlEncode(CharSequence unsafeStr, StringBuilder buf) {
      htmlEncode(unsafeStr, false, buf);
   }

   private static void labelClassNodeHTML(
      ClassInfo ci, String shape, String boxBgColor, boolean showFields, boolean showMethods, boolean useSimpleNames, ScanSpec scanSpec, StringBuilder buf
   ) {
      buf.append("[shape=").append(shape).append(",style=filled,fillcolor=\"#").append(boxBgColor).append("\",label=");
      buf.append('<');
      buf.append("<table border='0' cellborder='0' cellspacing='1'>");
      buf.append("<tr><td><font point-size='12'>")
         .append(ci.getModifiersStr())
         .append(' ')
         .append(ci.isEnum() ? "enum" : (ci.isAnnotation() ? "@interface" : (ci.isInterface() ? "interface" : "class")))
         .append("</font></td></tr>");
      if (ci.getName().contains(".")) {
         buf.append("<tr><td><font point-size='14'><b>");
         htmlEncode(ci.getPackageName() + ".", buf);
         buf.append("</b></font></td></tr>");
      }

      buf.append("<tr><td><font point-size='20'><b>");
      htmlEncode(ci.getSimpleName(), buf);
      buf.append("</b></font></td></tr>");
      float darkness = 0.8F;
      int r = (int)((float)Integer.parseInt(boxBgColor.substring(0, 2), 16) * 0.8F);
      int g = (int)((float)Integer.parseInt(boxBgColor.substring(2, 4), 16) * 0.8F);
      int b = (int)((float)Integer.parseInt(boxBgColor.substring(4, 6), 16) * 0.8F);
      String darkerColor = String.format(
         "#%s%s%s%s%s%s",
         Integer.toString(r >> 4, 16),
         Integer.toString(r & 15, 16),
         Integer.toString(g >> 4, 16),
         Integer.toString(g & 15, 16),
         Integer.toString(b >> 4, 16),
         Integer.toString(b & 15, 16)
      );
      AnnotationInfoList annotationInfo = ci.annotationInfo;
      if (annotationInfo != null && !annotationInfo.isEmpty()) {
         buf.append("<tr><td colspan='3' bgcolor='").append(darkerColor).append("'><font point-size='12'><b>ANNOTATIONS</b></font></td></tr>");
         AnnotationInfoList annotationInfoSorted = new AnnotationInfoList(annotationInfo);
         CollectionUtils.sortIfNotEmpty(annotationInfoSorted);

         for (AnnotationInfo ai : annotationInfoSorted) {
            String annotationName = ai.getName();
            if (!annotationName.startsWith("java.lang.annotation.")) {
               buf.append("<tr>");
               buf.append("<td align='center' valign='top'>");
               htmlEncode(ai.toString(), buf);
               buf.append("</td></tr>");
            }
         }
      }

      FieldInfoList fieldInfo = ci.fieldInfo;
      if (showFields && fieldInfo != null && !fieldInfo.isEmpty()) {
         FieldInfoList fieldInfoSorted = new FieldInfoList(fieldInfo);
         CollectionUtils.sortIfNotEmpty(fieldInfoSorted);

         for (int i = fieldInfoSorted.size() - 1; i >= 0; i--) {
            if (fieldInfoSorted.get(i).getName().equals("serialVersionUID")) {
               fieldInfoSorted.remove(i);
            }
         }

         if (!fieldInfoSorted.isEmpty()) {
            buf.append("<tr><td colspan='3' bgcolor='")
               .append(darkerColor)
               .append("'><font point-size='12'><b>")
               .append(scanSpec.ignoreFieldVisibility ? "" : "PUBLIC ")
               .append("FIELDS</b></font></td></tr>");
            buf.append("<tr><td cellpadding='0'>");
            buf.append("<table border='0' cellborder='0'>");

            for (FieldInfo fi : fieldInfoSorted) {
               buf.append("<tr>");
               buf.append("<td align='right' valign='top'>");
               AnnotationInfoList fieldAnnotationInfo = fi.annotationInfo;
               if (fieldAnnotationInfo != null) {
                  for (AnnotationInfo aix : fieldAnnotationInfo) {
                     if (buf.charAt(buf.length() - 1) != ' ') {
                        buf.append(' ');
                     }

                     htmlEncode(aix.toString(), buf);
                  }
               }

               if (scanSpec.ignoreFieldVisibility) {
                  if (buf.charAt(buf.length() - 1) != ' ') {
                     buf.append(' ');
                  }

                  buf.append(fi.getModifiersStr());
               }

               if (buf.charAt(buf.length() - 1) != ' ') {
                  buf.append(' ');
               }

               TypeSignature typeSig = fi.getTypeSignatureOrTypeDescriptor();
               htmlEncode(useSimpleNames ? typeSig.toStringWithSimpleNames() : typeSig.toString(), buf);
               buf.append("</td>");
               buf.append("<td align='left' valign='top'><b>");
               String fieldName = fi.getName();
               htmlEncode(fieldName, buf);
               buf.append("</b></td></tr>");
            }

            buf.append("</table>");
            buf.append("</td></tr>");
         }
      }

      MethodInfoList methodInfo = ci.methodInfo;
      if (showMethods && methodInfo != null) {
         MethodInfoList methodInfoSorted = new MethodInfoList(methodInfo);
         CollectionUtils.sortIfNotEmpty(methodInfoSorted);

         for (int ix = methodInfoSorted.size() - 1; ix >= 0; ix--) {
            MethodInfo mi = methodInfoSorted.get(ix);
            String name = mi.getName();
            int numParam = mi.getParameterInfo().length;
            if (name.equals("<clinit>")
               || name.equals("hashCode") && numParam == 0
               || name.equals("toString") && numParam == 0
               || name.equals("equals") && numParam == 1 && mi.getTypeDescriptor().toString().equals("boolean (java.lang.Object)")) {
               methodInfoSorted.remove(ix);
            }
         }

         if (!methodInfoSorted.isEmpty()) {
            buf.append("<tr><td cellpadding='0'>");
            buf.append("<table border='0' cellborder='0'>");
            buf.append("<tr><td colspan='3' bgcolor='")
               .append(darkerColor)
               .append("'><font point-size='12'><b>")
               .append(scanSpec.ignoreMethodVisibility ? "" : "PUBLIC ")
               .append("METHODS</b></font></td></tr>");

            for (MethodInfo mi : methodInfoSorted) {
               buf.append("<tr>");
               buf.append("<td align='right' valign='top'>");
               AnnotationInfoList methodAnnotationInfo = mi.annotationInfo;
               if (methodAnnotationInfo != null) {
                  for (AnnotationInfo aix : methodAnnotationInfo) {
                     if (buf.charAt(buf.length() - 1) != ' ') {
                        buf.append(' ');
                     }

                     htmlEncode(aix.toString(), buf);
                  }
               }

               if (scanSpec.ignoreMethodVisibility) {
                  if (buf.charAt(buf.length() - 1) != ' ') {
                     buf.append(' ');
                  }

                  buf.append(mi.getModifiersStr());
               }

               if (buf.charAt(buf.length() - 1) != ' ') {
                  buf.append(' ');
               }

               if (!mi.getName().equals("<init>")) {
                  TypeSignature resultTypeSig = mi.getTypeSignatureOrTypeDescriptor().getResultType();
                  htmlEncode(useSimpleNames ? resultTypeSig.toStringWithSimpleNames() : resultTypeSig.toString(), buf);
               } else {
                  buf.append("<b>&lt;constructor&gt;</b>");
               }

               buf.append("</td>");
               buf.append("<td align='left' valign='top'>");
               buf.append("<b>");
               if (mi.getName().equals("<init>")) {
                  htmlEncode(ci.getSimpleName(), buf);
               } else {
                  htmlEncode(mi.getName(), buf);
               }

               buf.append("</b>&nbsp;");
               buf.append("</td>");
               buf.append("<td align='left' valign='top'>");
               buf.append('(');
               MethodParameterInfo[] paramInfo = mi.getParameterInfo();
               if (paramInfo.length != 0) {
                  int ixx = 0;

                  for (int wrapPos = 0; ixx < paramInfo.length; ixx++) {
                     if (ixx > 0) {
                        buf.append(", ");
                        wrapPos += 2;
                     }

                     if (wrapPos > 40) {
                        buf.append("</td></tr><tr><td></td><td></td><td align='left' valign='top'>");
                        wrapPos = 0;
                     }

                     AnnotationInfo[] paramAnnotationInfo = paramInfo[ixx].annotationInfo;
                     if (paramAnnotationInfo != null) {
                        for (AnnotationInfo aix : paramAnnotationInfo) {
                           String ais = aix.toString();
                           if (!ais.isEmpty()) {
                              if (buf.charAt(buf.length() - 1) != ' ') {
                                 buf.append(' ');
                              }

                              htmlEncode(ais, buf);
                              wrapPos += 1 + ais.length();
                              if (wrapPos > 40) {
                                 buf.append("</td></tr><tr><td></td><td></td><td align='left' valign='top'>");
                                 wrapPos = 0;
                              }
                           }
                        }
                     }

                     TypeSignature paramTypeSig = paramInfo[ixx].getTypeSignatureOrTypeDescriptor();
                     String paramTypeStr = useSimpleNames ? paramTypeSig.toStringWithSimpleNames() : paramTypeSig.toString();
                     htmlEncode(paramTypeStr, buf);
                     wrapPos += paramTypeStr.length();
                     String paramName = paramInfo[ixx].getName();
                     if (paramName != null) {
                        buf.append(" <B>");
                        htmlEncode(paramName, buf);
                        wrapPos += 1 + paramName.length();
                        buf.append("</B>");
                     }
                  }
               }

               buf.append(')');
               buf.append("</td></tr>");
            }

            buf.append("</table>");
            buf.append("</td></tr>");
         }
      }

      buf.append("</table>");
      buf.append(">]");
   }

   static String generateGraphVizDotFile(
      ClassInfoList classInfoList,
      float sizeX,
      float sizeY,
      boolean showFields,
      boolean showFieldTypeDependencyEdges,
      boolean showMethods,
      boolean showMethodTypeDependencyEdges,
      boolean showAnnotations,
      boolean useSimpleNames,
      ScanSpec scanSpec
   ) {
      StringBuilder buf = new StringBuilder(1048576);
      buf.append("digraph {\n");
      buf.append("size=\"").append(sizeX).append(',').append(sizeY).append("\";\n");
      buf.append("layout=dot;\n");
      buf.append("rankdir=\"BT\";\n");
      buf.append("overlap=false;\n");
      buf.append("splines=true;\n");
      buf.append("pack=true;\n");
      buf.append("graph [fontname = \"Courier, Regular\"]\n");
      buf.append("node [fontname = \"Courier, Regular\"]\n");
      buf.append("edge [fontname = \"Courier, Regular\"]\n");
      ClassInfoList standardClassNodes = classInfoList.getStandardClasses();
      ClassInfoList interfaceNodes = classInfoList.getInterfaces();
      ClassInfoList annotationNodes = classInfoList.getAnnotations();

      for (ClassInfo node : standardClassNodes) {
         buf.append('"').append(node.getName()).append('"');
         labelClassNodeHTML(node, "box", "fff2b6", showFields, showMethods, useSimpleNames, scanSpec, buf);
         buf.append(";\n");
      }

      for (ClassInfo node : interfaceNodes) {
         buf.append('"').append(node.getName()).append('"');
         labelClassNodeHTML(node, "diamond", "b6e7ff", showFields, showMethods, useSimpleNames, scanSpec, buf);
         buf.append(";\n");
      }

      for (ClassInfo node : annotationNodes) {
         buf.append('"').append(node.getName()).append('"');
         labelClassNodeHTML(node, "oval", "f3c9ff", showFields, showMethods, useSimpleNames, scanSpec, buf);
         buf.append(";\n");
      }

      Set<String> allVisibleNodes = new HashSet<>();
      allVisibleNodes.addAll(standardClassNodes.getNames());
      allVisibleNodes.addAll(interfaceNodes.getNames());
      allVisibleNodes.addAll(annotationNodes.getNames());
      buf.append('\n');

      for (ClassInfo classNode : standardClassNodes) {
         for (ClassInfo directSuperclassNode : classNode.getSuperclasses().directOnly()) {
            if (directSuperclassNode != null
               && allVisibleNodes.contains(directSuperclassNode.getName())
               && !directSuperclassNode.getName().equals("java.lang.Object")) {
               buf.append("  \"").append(classNode.getName()).append("\" -> \"").append(directSuperclassNode.getName()).append("\" [arrowsize=2.5]\n");
            }
         }

         for (ClassInfo implementedInterfaceNode : classNode.getInterfaces().directOnly()) {
            if (allVisibleNodes.contains(implementedInterfaceNode.getName())) {
               buf.append("  \"")
                  .append(classNode.getName())
                  .append("\" -> \"")
                  .append(implementedInterfaceNode.getName())
                  .append("\" [arrowhead=diamond, arrowsize=2.5]\n");
            }
         }

         if (showFieldTypeDependencyEdges && classNode.fieldInfo != null) {
            for (FieldInfo fi : classNode.fieldInfo) {
               for (ClassInfo referencedFieldType : fi.findReferencedClassInfo(null)) {
                  if (allVisibleNodes.contains(referencedFieldType.getName())) {
                     buf.append("  \"")
                        .append(referencedFieldType.getName())
                        .append("\" -> \"")
                        .append(classNode.getName())
                        .append("\" [arrowtail=obox, arrowsize=2.5, dir=back]\n");
                  }
               }
            }
         }

         if (showMethodTypeDependencyEdges && classNode.methodInfo != null) {
            for (MethodInfo mi : classNode.methodInfo) {
               for (ClassInfo referencedMethodType : mi.findReferencedClassInfo(null)) {
                  if (allVisibleNodes.contains(referencedMethodType.getName())) {
                     buf.append("  \"")
                        .append(referencedMethodType.getName())
                        .append("\" -> \"")
                        .append(classNode.getName())
                        .append("\" [arrowtail=box, arrowsize=2.5, dir=back]\n");
                  }
               }
            }
         }
      }

      for (ClassInfo interfaceNode : interfaceNodes) {
         for (ClassInfo superinterfaceNode : interfaceNode.getInterfaces().directOnly()) {
            if (allVisibleNodes.contains(superinterfaceNode.getName())) {
               buf.append("  \"")
                  .append(interfaceNode.getName())
                  .append("\" -> \"")
                  .append(superinterfaceNode.getName())
                  .append("\" [arrowhead=diamond, arrowsize=2.5]\n");
            }
         }
      }

      if (showAnnotations) {
         for (ClassInfo annotationNode : annotationNodes) {
            for (ClassInfo annotatedClassNode : annotationNode.getClassesWithAnnotationDirectOnly()) {
               if (allVisibleNodes.contains(annotatedClassNode.getName())) {
                  buf.append("  \"")
                     .append(annotatedClassNode.getName())
                     .append("\" -> \"")
                     .append(annotationNode.getName())
                     .append("\" [arrowhead=dot, arrowsize=2.5]\n");
               }
            }

            for (ClassInfo classWithMethodAnnotationNode : annotationNode.getClassesWithMethodAnnotationDirectOnly()) {
               if (allVisibleNodes.contains(classWithMethodAnnotationNode.getName())) {
                  buf.append("  \"")
                     .append(classWithMethodAnnotationNode.getName())
                     .append("\" -> \"")
                     .append(annotationNode.getName())
                     .append("\" [arrowhead=odot, arrowsize=2.5]\n");
               }
            }

            for (ClassInfo classWithMethodAnnotationNodex : annotationNode.getClassesWithFieldAnnotationDirectOnly()) {
               if (allVisibleNodes.contains(classWithMethodAnnotationNodex.getName())) {
                  buf.append("  \"")
                     .append(classWithMethodAnnotationNodex.getName())
                     .append("\" -> \"")
                     .append(annotationNode.getName())
                     .append("\" [arrowhead=odot, arrowsize=2.5]\n");
               }
            }
         }
      }

      buf.append('}');
      return buf.toString();
   }

   static String generateGraphVizDotFileFromInterClassDependencies(ClassInfoList classInfoList, float sizeX, float sizeY, boolean includeExternalClasses) {
      StringBuilder buf = new StringBuilder(1048576);
      buf.append("digraph {\n");
      buf.append("size=\"").append(sizeX).append(',').append(sizeY).append("\";\n");
      buf.append("layout=dot;\n");
      buf.append("rankdir=\"BT\";\n");
      buf.append("overlap=false;\n");
      buf.append("splines=true;\n");
      buf.append("pack=true;\n");
      buf.append("graph [fontname = \"Courier, Regular\"]\n");
      buf.append("node [fontname = \"Courier, Regular\"]\n");
      buf.append("edge [fontname = \"Courier, Regular\"]\n");
      Set<ClassInfo> allVisibleNodes = new HashSet<>(classInfoList);
      if (includeExternalClasses) {
         for (ClassInfo ci : classInfoList) {
            allVisibleNodes.addAll(ci.getClassDependencies());
         }
      }

      for (ClassInfo ci : allVisibleNodes) {
         buf.append('"').append(ci.getName()).append('"');
         buf.append("[shape=")
            .append(ci.isAnnotation() ? "oval" : (ci.isInterface() ? "diamond" : "box"))
            .append(",style=filled,fillcolor=\"#")
            .append(ci.isAnnotation() ? "f3c9ff" : (ci.isInterface() ? "b6e7ff" : "fff2b6"))
            .append("\",label=");
         buf.append('<');
         buf.append("<table border='0' cellborder='0' cellspacing='1'>");
         buf.append("<tr><td><font point-size='12'>")
            .append(ci.getModifiersStr())
            .append(' ')
            .append(ci.isEnum() ? "enum" : (ci.isAnnotation() ? "@interface" : (ci.isInterface() ? "interface" : "class")))
            .append("</font></td></tr>");
         if (ci.getName().contains(".")) {
            buf.append("<tr><td><font point-size='14'><b>");
            htmlEncode(ci.getPackageName(), buf);
            buf.append("</b></font></td></tr>");
         }

         buf.append("<tr><td><font point-size='20'><b>");
         htmlEncode(ci.getSimpleName(), buf);
         buf.append("</b></font></td></tr>");
         buf.append("</table>");
         buf.append(">];\n");
      }

      buf.append('\n');

      for (ClassInfo ci : classInfoList) {
         for (ClassInfo dep : ci.getClassDependencies()) {
            if (includeExternalClasses || allVisibleNodes.contains(dep)) {
               buf.append("  \"").append(ci.getName()).append("\" -> \"").append(dep.getName()).append("\" [arrowsize=2.5]\n");
            }
         }
      }

      buf.append('}');
      return buf.toString();
   }

   static {
      String wsChars = " \t\n\u000b\f\r\u0085  \u180e           \u2028\u2029  　";

      for (int i = 0; i < " \t\n\u000b\f\r\u0085  \u180e           \u2028\u2029  　".length(); i++) {
         IS_UNICODE_WHITESPACE.set(" \t\n\u000b\f\r\u0085  \u180e           \u2028\u2029  　".charAt(i));
      }
   }
}
