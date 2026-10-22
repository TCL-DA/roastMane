package org.glassfish.jersey.server.wadl.internal.generators.resourcedoc;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.glassfish.jersey.server.internal.LocalizationMessages;
import org.glassfish.jersey.server.model.Parameter;
import org.glassfish.jersey.server.wadl.internal.generators.resourcedoc.model.AnnotationDocType;
import org.glassfish.jersey.server.wadl.internal.generators.resourcedoc.model.ClassDocType;
import org.glassfish.jersey.server.wadl.internal.generators.resourcedoc.model.MethodDocType;
import org.glassfish.jersey.server.wadl.internal.generators.resourcedoc.model.NamedValueType;
import org.glassfish.jersey.server.wadl.internal.generators.resourcedoc.model.ParamDocType;
import org.glassfish.jersey.server.wadl.internal.generators.resourcedoc.model.RepresentationDocType;
import org.glassfish.jersey.server.wadl.internal.generators.resourcedoc.model.ResourceDocType;
import org.glassfish.jersey.server.wadl.internal.generators.resourcedoc.model.ResponseDocType;

public class ResourceDocAccessor {
   private static final Logger LOGGER = Logger.getLogger(ResourceDocAccessor.class.getName());
   private ResourceDocType _resourceDoc;

   public ResourceDocAccessor(ResourceDocType resourceDoc) {
      this._resourceDoc = resourceDoc;
   }

   public ClassDocType getClassDoc(Class<?> resourceClass) {
      if (resourceClass == null) {
         return null;
      } else {
         for (ClassDocType classDocType : this._resourceDoc.getDocs()) {
            if (resourceClass.getName().equals(classDocType.getClassName())) {
               return classDocType;
            }
         }

         return null;
      }
   }

   public MethodDocType getMethodDoc(Class<?> resourceClass, Method method) {
      if (resourceClass != null && method != null) {
         ClassDocType classDoc = this.getClassDoc(resourceClass);
         if (classDoc == null) {
            return null;
         } else {
            MethodDocType candidate = null;
            int candidateCount = 0;
            String methodName = method.getName();
            String methodSignature = this.computeSignature(method);

            for (MethodDocType methodDocType : classDoc.getMethodDocs()) {
               if (methodName.equals(methodDocType.getMethodName())) {
                  candidateCount++;
                  if (candidate == null) {
                     candidate = methodDocType;
                  }

                  String docMethodSignature = methodDocType.getMethodSignature();
                  if (docMethodSignature != null && docMethodSignature.equals(methodSignature)) {
                     return methodDocType;
                  }
               }
            }

            if (candidate != null && candidateCount > 1 && LOGGER.isLoggable(Level.CONFIG)) {
               LOGGER.config(
                  LocalizationMessages.WADL_RESOURCEDOC_AMBIGUOUS_METHOD_ENTRIES(resourceClass.getName(), methodName, methodSignature, candidateCount)
               );
            }

            return candidate;
         }
      } else {
         return null;
      }
   }

   private String computeSignature(Method method) {
      String methodAsString = method.toGenericString();
      return methodAsString.substring(methodAsString.indexOf(40), methodAsString.lastIndexOf(41) + 1);
   }

   public ParamDocType getParamDoc(Class<?> resourceClass, Method method, Parameter p) {
      MethodDocType methodDoc = this.getMethodDoc(resourceClass, method);
      if (methodDoc != null) {
         for (ParamDocType paramDocType : methodDoc.getParamDocs()) {
            for (AnnotationDocType annotationDocType : paramDocType.getAnnotationDocs()) {
               Class<? extends Annotation> annotationType = p.getSourceAnnotation().annotationType();
               if (annotationType != null) {
                  String sourceName = this.getSourceName(annotationDocType);
                  if (sourceName != null && sourceName.equals(p.getSourceName())) {
                     return paramDocType;
                  }
               }
            }
         }
      }

      return null;
   }

   public RepresentationDocType getRequestRepresentation(Class<?> resourceClass, Method method, String mediaType) {
      if (mediaType == null) {
         return null;
      } else {
         MethodDocType methodDoc = this.getMethodDoc(resourceClass, method);
         return methodDoc != null && methodDoc.getRequestDoc() != null && methodDoc.getRequestDoc().getRepresentationDoc() != null
            ? methodDoc.getRequestDoc().getRepresentationDoc()
            : null;
      }
   }

   public ResponseDocType getResponse(Class<?> resourceClass, Method method) {
      MethodDocType methodDoc = this.getMethodDoc(resourceClass, method);
      return methodDoc != null && methodDoc.getResponseDoc() != null ? methodDoc.getResponseDoc() : null;
   }

   private String getSourceName(AnnotationDocType annotationDocType) {
      if (annotationDocType.hasAttributeDocs()) {
         for (NamedValueType namedValueType : annotationDocType.getAttributeDocs()) {
            if ("value".equals(namedValueType.getName())) {
               return namedValueType.getValue();
            }
         }
      }

      return null;
   }
}
