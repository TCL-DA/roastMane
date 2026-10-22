package org.glassfish.jersey.server.wadl.internal.generators.resourcedoc.model;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAnyElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
   name = "paramDoc",
   propOrder = {}
)
public class ParamDocType {
   private String paramName;
   private String commentText;
   @XmlElementWrapper(
      name = "annotationDocs"
   )
   protected List<AnnotationDocType> annotationDoc;
   @XmlAnyElement(
      lax = true
   )
   private List<Object> any;

   public ParamDocType() {
   }

   public ParamDocType(String paramName, String commentText) {
      this.paramName = paramName;
      this.commentText = commentText;
   }

   public List<AnnotationDocType> getAnnotationDocs() {
      if (this.annotationDoc == null) {
         this.annotationDoc = new ArrayList<>();
      }

      return this.annotationDoc;
   }

   public List<Object> getAny() {
      if (this.any == null) {
         this.any = new ArrayList<>();
      }

      return this.any;
   }

   public String getCommentText() {
      return this.commentText;
   }

   public void setCommentText(String commentText) {
      this.commentText = commentText;
   }

   public String getParamName() {
      return this.paramName;
   }

   public void setParamName(String paramName) {
      this.paramName = paramName;
   }
}
